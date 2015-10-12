package com.athena.mis.application.actions.customer

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Customer
import com.athena.mis.application.utility.CustomerCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Show UI for customer CRUD and list of customer for grid
 *  For details go through Use-Case doc named 'ShowCustomerActionService'
 */
class ShowCustomerActionService extends BaseService implements ActionIntf {

    @Autowired
    CustomerCacheUtility customerCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to load customer page"
    private static final String CUSTOMER_LIST = "customerList"
    private static final String COUNT = "count"

    /**
     * do nothing for pre condition
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * get list of customerObjects
     * @param parameters -N/A
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            initPager(params)
            int count = customerCacheUtility.count()
            List customerList = customerCacheUtility.list(this)
            result.put(CUSTOMER_LIST, customerList)
            result.put(COUNT, count)
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
     * do nothing for post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * wrap customerObjectList to show on grid
     * @param obj -a map contains customerObjectList and count
     * @return -wrapped customerObjectList to show on grid
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map executeResult = (Map) obj
            List customerList = (List) executeResult.get(CUSTOMER_LIST)
            int count = (int) executeResult.get(COUNT)
            List resultList = wrapListInGridEntityList(customerList, start)
            Map output = [page: pageNumber, total: count, rows: resultList]

            result.put(CUSTOMER_LIST, output)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
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
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.message) {
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
     * wrappedCustomerObjectList for grid
     * @param customerList - list of customer objects
     * @param start -start index
     * @return -wrappedCustomerObjectList
     */
    private List wrapListInGridEntityList(List<Customer> customerList, int start) {
        List customers = [] as List
        int counter = start + 1
        for (int i = 0; i < customerList.size(); i++) {
            GridEntity obj = new GridEntity()
            obj.id = customerList[i].id
            obj.cell = [counter,
                    customerList[i].id,
                    customerList[i].fullName,
                    customerList[i].nickName,
                    customerList[i].email ? customerList[i].email : Tools.EMPTY_SPACE,
                    DateUtility.getLongDateForUI(customerList[i].dateOfBirth),
                    customerList[i].address ? customerList[i].address : Tools.EMPTY_SPACE
            ]
            customers << obj
            counter++
        }
        return customers
    }
}
