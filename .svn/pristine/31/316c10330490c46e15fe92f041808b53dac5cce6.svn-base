package com.athena.mis.exchangehouse.actions.customer

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.exchangehouse.entity.ExhCustomer
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Show specific customer details
 *  For details go through Use-Case doc named 'ExhSearchForCustomerByNameCodeActionService'
 */
class ExhSearchForCustomerByNameCodeActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to Search Customer"
    private static final String FAILURE_MESSAGE = "Failed to Load Customer Page"
    private static final String NOT_FOUND_MESSAGE = "Customer not found"
    private static final String LST_CUSTOMER = "lstCustomer"
    private static final String SORT_BY_NAME = "name"
    private static final String GRID_OBJ = "gridObjCustomer"
    private static final int RESULT_PER_PAGE = 20

    @Autowired
    ExhSessionUtil exhSessionUtil

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get customer(s) for UI through specific search
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     */
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)             // set default
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            if (!parameterMap.rp) {
                parameterMap.rp = RESULT_PER_PAGE
                parameterMap.page = 1
            }

            initPager(parameterMap)              // initialize grid params
            List<ExhCustomer> lstCustomer = []
            int count = 0
            long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
            int qType = Integer.parseInt(parameterMap.queryType)
            switch (qType) {
                case 1:                     //Name
                    String queryOnName = Tools.PERCENTAGE + parameterMap.queryString + Tools.PERCENTAGE
                    lstCustomer = searchCustomerListByFullName(queryOnName, companyId)
                    count = countCustomerListByFullName(queryOnName, companyId)
                    break
                case 2:                     // Customer A/C No or Code
                    String queryOnCode = parameterMap.queryString
                    lstCustomer = ExhCustomer.findAllByCodeIlikeAndCompanyId(queryOnCode, companyId, [readOnly: true, max: resultPerPage, offset: start, sort: SORT_BY_NAME, order: ASCENDING_SORT_ORDER])
                    count = ExhCustomer.countByCodeIlikeAndCompanyId(queryOnCode, companyId)
                    break
                case 3:    // Phone Number
                    String queryOnPhoneNumber = Tools.PERCENTAGE + parameterMap.queryString + Tools.PERCENTAGE
                    lstCustomer = ExhCustomer.findAllByPhoneIlikeAndCompanyId(queryOnPhoneNumber, companyId, [readOnly: true, max: resultPerPage, offset: start, sort: SORT_BY_NAME, order: ASCENDING_SORT_ORDER])
                    count = ExhCustomer.countByPhoneIlikeAndCompanyId(queryOnPhoneNumber, companyId)
                    break
                case 4:    // Photo ID No
                    String queryOnPhotoIdNo = Tools.PERCENTAGE + parameterMap.queryString + Tools.PERCENTAGE
                    lstCustomer = ExhCustomer.findAllByPhotoIdNoIlikeAndCompanyId(queryOnPhotoIdNo, companyId, [readOnly: true, max: resultPerPage, offset: start, sort: SORT_BY_NAME, order: ASCENDING_SORT_ORDER])
                    count = ExhCustomer.countByPhotoIdNoIlikeAndCompanyId(queryOnPhotoIdNo, companyId)
                    break
                default:
                    break
            }

            result.put(LST_CUSTOMER, lstCustomer)
            result.put(Tools.COUNT, Integer.valueOf(count))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return null
        }
    }

    /**
     * Wrap list of customer(s) in grid entity
     * @param lstCustomer -list of customer object(s)
     * @param start -starting index of the page
     * @return -list of wrapped customer(s)
     */
    private List wrapCustomers(List<ExhCustomer> lstCustomer, int start) {
        List customerList = []
        int counter = start + 1
        for (int i = 0; i < lstCustomer.size(); i++) {
            GridEntity obj = new GridEntity()              // build grid
            ExhCustomer customer = lstCustomer[i]
            obj.id = customer.id
            obj.cell = [
                    counter,
                    customer.id,
                    customer.fullName,
                    customer.code,
                    customer.address,
                    customer.phone,
                    customer.photoIdNo
            ]
            customerList << obj
            counter++
        }
        return customerList
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap customer(s) for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            List<ExhCustomer> lstCustomer = (List<ExhCustomer>) executeResult.get(LST_CUSTOMER)
            int count = ((Integer) executeResult.get(Tools.COUNT)).intValue()
            if (count <= 0) {
                result.put(Tools.MESSAGE, NOT_FOUND_MESSAGE)
                return result
            }
            List resultList = wrapCustomers(lstCustomer, this.start)
            Map gridObject = [page: pageNumber, total: count, rows: resultList]
            result.put(GRID_OBJ, gridObject)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
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
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    private List<ExhCustomer> searchCustomerListByFullName(String queryOnName, long companyId) {
        List<ExhCustomer> lstCustomer = []
        queryType = "(name || ' ' || COALESCE(surname,''))"
        String SQL_SEARCH = """
		SELECT * from exh_customer WHERE
		company_id = ${companyId} AND
		${queryType} ilike :query
		ORDER BY ${SORT_BY_NAME} ${ASCENDING_SORT_ORDER}
		OFFSET ${start} LIMIT ${resultPerPage}
		"""
        Map queryParam = [query: queryOnName]
        List<GroovyRowResult> customerList = executeSelectSql(SQL_SEARCH, queryParam)
        for(int i=0; i< customerList.size();i++) {
            ExhCustomer customer = new ExhCustomer()
            customer.id = customerList[i].id
            customer.name = customerList[i].name
            customer.surname = customerList[i].surname
            customer.code = customerList[i].code
            customer.address = customerList[i].address
            customer.phone = customerList[i].phone
            customer.photoIdNo = customerList[i].photo_id_no
            lstCustomer << customer
        }
        return lstCustomer
    }

    private int countCustomerListByFullName(String queryOnName, long companyId) {
        queryType = "(name || ' ' || COALESCE(surname,''))"

        String SQL_SEARCH_COUNT = """
		SELECT count(*) from exh_customer WHERE
		company_id = ${companyId} AND
		${queryType} ilike :query
		"""
        Map queryParam = [query: queryOnName]
        int count = (int) executeSelectSql(SQL_SEARCH_COUNT, queryParam).first().count
        return count
    }

}
