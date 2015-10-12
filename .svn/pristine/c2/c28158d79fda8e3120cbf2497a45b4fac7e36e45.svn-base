package com.athena.mis.exchangehouse.actions.customer

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.exchangehouse.utility.ExhPhotoIdTypeCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Search customer and show specific list of customer for grid
 *  For details go through Use-Case doc named 'ExhSearchCustomerActionService'
 */
class ExhSearchCustomerActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    // constant
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to load customer list"
    private static final String LST_CUSTOMER = "lstCustomer"
    private static final String COMPANY_ID = 'companyId'
    private static final String SORT_COLUMN_NAME = "name"
    private static final String FULL_NAME = "fullName"

    @Autowired
    ExhPhotoIdTypeCacheUtility exhPhotoIdTypeCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil
    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get customer list for grid through specific search
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     */
    @Transactional(readOnly = true)
    Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap=(GrailsParameterMap) params
            if (!parameterMap.sortname) {
                // if no sort name then sort by name/asc
                parameterMap.sortname = SORT_COLUMN_NAME
                parameterMap.sortorder = ASCENDING_SORT_ORDER
            }

            if (parameterMap.qtype.toString().equals(Tools.ID)) {
                Long.parseLong(parameterMap.query) // check if customer ID can be parsed
            }
            initSearch(parameterMap)        // initialize parameters for flexGrid
            int count = 0
            LinkedHashMap resultList = searchCustomer()          // get customer
            List<GroovyRowResult> lstCustomer = (List<GroovyRowResult>) resultList.customerList
            count = (int) resultList.count

            result.put(Tools.COUNT, Integer.valueOf(count))
            result.put(LST_CUSTOMER, lstCustomer)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage());
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap customer list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj   // cast map returned from execute method
            List<GroovyRowResult> lstCustomer = (List<GroovyRowResult>) executeResult.get(LST_CUSTOMER)
            int count = ((Integer) executeResult.get(Tools.COUNT)).intValue()
            List lstWrapCustomers = wrapCustomers(lstCustomer, start)                 // wrap customers
            Map output = [page: pageNumber, total: count, rows: lstWrapCustomers]
            return output
        } catch (Exception e) {
            log.error(e.getMessage());
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }

    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from previous method
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Wrap list of customer in grid entity
     * @param lstCustomers -list of customer object(s)
     * @param start -starting index of the page
     * @return -list of wrapped customers
     */
    private List wrapCustomers(List<GroovyRowResult> lstCustomers, int start) {
        List customers = []
        if (lstCustomers == null) return customers

        int counter = start + 1
        for (int i = 0; i < lstCustomers.size(); i++) {
            GroovyRowResult customer = lstCustomers[i]
            GridEntity obj = new GridEntity()            // build grid object
            obj.id = customer.id
            String dateOfBirth = DateUtility.getDateFormatAsString(customer.date_of_birth)    // date format 'dd-MMMM-yyyy'
            obj.cell = [
                    counter,
                    customer.code,
                    customer.name + (customer.surname? (Tools.SINGLE_SPACE + customer.surname): Tools.EMPTY_SPACE) ,
                    dateOfBirth,
                    customer.photo_id_type_id ? exhPhotoIdTypeCacheUtility.read(customer.photo_id_type_id).name : Tools.NONE,
                    customer.photo_id_no ? customer.photo_id_no : Tools.NONE,
                    customer.phone ? customer.phone : Tools.NONE]
            customers << obj
            counter++
        }
        return customers
    }

    /**
     * Search customer by query type
     **/
    private LinkedHashMap searchCustomer() {
        if(queryType.equals(FULL_NAME)) {
            queryType = "(name || ' ' || COALESCE(surname,''))"
        }
        String queryStr = Tools.PERCENTAGE + query + Tools.PERCENTAGE
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()

        String SQL_SEARCH = """
		SELECT * from exh_customer WHERE
		company_id = ${companyId} AND
		${queryType} ilike :query
		ORDER BY ${sortColumn} ${sortOrder}
		OFFSET ${start} LIMIT ${resultPerPage}
		"""
        String SQL_SEARCH_COUNT = """
		SELECT count(*) from exh_customer WHERE
		company_id = ${companyId} AND
		${queryType} ilike :query
		"""

        Map queryParam = [query: queryStr]

        List<GroovyRowResult> customerList = executeSelectSql(SQL_SEARCH, queryParam)
        int counts = (int) executeSelectSql(SQL_SEARCH_COUNT, queryParam).first().count

        return [customerList: customerList, count: counts]
    }
}
