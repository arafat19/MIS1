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
 *  Class to get search result list of customer to show on grid
 *  For details go through Use-Case doc named 'SearchCustomerActionService'
 */
class SearchCustomerActionService extends BaseService implements ActionIntf {

    @Autowired
    CustomerCacheUtility customerCacheUtility

    protected final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to search customer list"
    private static final String CUSTOMER_LIST = "customerList"
    private static final String COUNT = "count"

    /**
     * do nothing for pre condition
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * get search result list of customerObjects
     * @param parameters -N/A
     * @param obj -N/A
     * @return -a map contains customerList and count
     */
    public Object execute(Object params, Object obj) {
        try {
            if ((!params.sortname) || (params.sortname.toString().equals(ID))) {
                params.sortname = customerCacheUtility.SORT_ON_NAME
                params.sortorder = customerCacheUtility.SORT_ORDER_ASCENDING
            }
            initSearch(params)
            Map searchResult = customerCacheUtility.search(queryType, query, this)
            List<Customer> customerList = searchResult.list
            int count = searchResult.count
            return [customerList: customerList, count: count]
        } catch (Exception ex) {
            log.error(ex.getMessage())
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
        try {
            Map executeResult = (Map) obj
            List<Customer> customerList = (List<Customer>) executeResult.get(CUSTOMER_LIST)
            int count = (int) executeResult.get(COUNT)
            List customerListWrap = wrapListInGridEntityList(customerList, start)
            return [page: pageNumber, total: count, rows: customerListWrap]
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return [page: pageNumber, total: 0, rows: null, isError: Boolean.TRUE, message: DEFAULT_ERROR_MESSAGE]
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
     * @param customerList -list of customer objects
     * @param start -start index
     * @return -wrappedCustomerObjectList
     */
    private List wrapListInGridEntityList(List<Customer> customerList, int start) {
        List customers = [] as List
        int counter = start + 1
        for (int i = 0; i < customerList.size(); i++) {
            Customer customer = customerList[i]
            GridEntity obj = new GridEntity()
            obj.id = customer.id
            obj.cell = [counter,
                    customer.id,
                    customer.fullName,
                    customer.nickName,
                    customer.email ? customer.email : Tools.EMPTY_SPACE,
                    DateUtility.getLongDateForUI(customerList[i].dateOfBirth),
                    customer.address ? customer.address : Tools.EMPTY_SPACE
            ]
            customers << obj
            counter++
        }
        return customers
    }
}
