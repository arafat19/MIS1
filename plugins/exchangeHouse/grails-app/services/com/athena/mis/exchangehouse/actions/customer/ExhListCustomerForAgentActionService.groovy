package com.athena.mis.exchangehouse.actions.customer

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.exchangehouse.entity.ExhCustomer
import com.athena.mis.exchangehouse.utility.ExhPhotoIdTypeCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Show list of customer for grid
 *  For details go through Use-Case doc named 'ExhListCustomerActionService'
 */
class ExhListCustomerForAgentActionService extends BaseService implements ActionIntf {
    private final Logger log = Logger.getLogger(getClass());

    private static final String SORT_COLUMN_NAME = "name"
    private static final String LST_CUSTOMER = "lstCustomer"
    private static final String SHOW_CUSTOMER_FAILURE_MSG = "Failed to load customer page"

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
     * Get customer list for grid
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            if (!parameterMap.rp) {
                parameterMap.rp = DEFAULT_RESULT_PER_PAGE
            }

            if (!parameterMap.sortname) {
                // if no sort name then sort by name/asc
                parameterMap.sortname = SORT_COLUMN_NAME
                parameterMap.sortorder = ASCENDING_SORT_ORDER
            }

            initSearch(parameterMap) // initSearch will call initPager()
            int count = 0

            LinkedHashMap resultList = listCustomer()

            List<ExhCustomer> lstCustomers = (List<ExhCustomer>) resultList.customerList
            count = (int) resultList.count
            result.put(LST_CUSTOMER, lstCustomers)
            result.put(Tools.COUNT, Integer.valueOf(count))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage());
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_CUSTOMER_FAILURE_MSG)
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
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj    // cast map returned from execute method
            List<ExhCustomer> lstCustomers = (List<ExhCustomer>) executeResult.get(LST_CUSTOMER)
            int count = ((Integer) executeResult.get(Tools.COUNT)).intValue()
            List lstWrappedCustomers = wrapCustomers(lstCustomers, start)
            Map output = [page: pageNumber, total: 0, rows: []]
            return output
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_CUSTOMER_FAILURE_MSG)
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
            result.put(Tools.MESSAGE, SHOW_CUSTOMER_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_CUSTOMER_FAILURE_MSG)
            return result
        }
    }

    /**
     * Wrap list of customers in grid entity
     * @param lstCustomers -list of customer object(s)
     * @param start -starting index of the page
     * @return -list of wrapped customers
     */
    private List wrapCustomers(List<ExhCustomer> lstCustomers, int start) {
        List customers = []
        int counter = start + 1
        for (int i = 0; i < lstCustomers.size(); i++) {
            ExhCustomer customer = lstCustomers[i]
            GridEntity obj = new GridEntity()          // build grid object
            obj.id = customer.id
            String dateOfBirth = DateUtility.getDateFormatAsString(customer.dateOfBirth)       // date format "dd-MMMM-yyyy"
            obj.cell = [counter,
                    customer.code,
                    customer.fullName,
                    dateOfBirth,
                    customer.photoIdTypeId ? exhPhotoIdTypeCacheUtility.read(customer.photoIdTypeId).name : Tools.NONE,
                    customer.photoIdNo ? customer.photoIdNo : Tools.NONE,
                    customer.phone ? customer.phone : Tools.NONE]
            customers << obj
            counter++
        };
        return customers;
    }

    /**
     * Get list of customer and count by agent
     * */
    public LinkedHashMap listCustomer() {
        long agentId = exhSessionUtil.getUserAgentId()
        List<ExhCustomer> lstCustomer = ExhCustomer.findAllByAgentId(agentId, [max: resultPerPage, offset: start, sort: sortColumn, order: sortOrder, readOnly: true])
        int count = ExhCustomer.countByAgentId(agentId)
        return [customerList: lstCustomer, count: count]
    }
}
