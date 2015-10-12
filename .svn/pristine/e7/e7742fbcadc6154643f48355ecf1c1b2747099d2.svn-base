package com.athena.mis.application.actions.customer

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Customer
import com.athena.mis.application.service.CustomerService
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.CustomerCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to update customer CRUD and to show on grid list
 *  For details go through Use-Case doc named 'UpdateCustomerActionService'
 */
class UpdateCustomerActionService extends BaseService implements ActionIntf {

    CustomerService customerService
    @Autowired
    CustomerCacheUtility customerCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil

    private static final String CUSTOMER_UPDATE_FAILURE_MESSAGE = "Fail to update Customer"
    private static final String CUSTOMER_UPDATE_SUCCESS_MESSAGE = "Customer has been updated successfully"
    private static final String CUSTOMER = "customer"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * @param parameters -N/A
     * @param obj -Customer object send from controller
     * @return -a map containing customer object for execute method
     * map -contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Customer customer = (Customer) obj
            result.put(CUSTOMER, customer)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CUSTOMER_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Update customer object in DB as well as in cache
     * @param parameters -N/A
     * @param obj -customerObject from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            Customer customer = (Customer) preResult.get(CUSTOMER)
            customerService.update(customer)
            customer.version = customer.version + 1
            customerCacheUtility.update(customer, customerCacheUtility.SORT_ON_NAME, customerCacheUtility.SORT_ORDER_ASCENDING)
            result.put(CUSTOMER, customer)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(CUSTOMER_UPDATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CUSTOMER_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * do nothing at post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap updated customer to show on grid
     * @param obj -updated customer object from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
            Customer customer = (Customer) executeResult.get(CUSTOMER)
            GridEntity object = new GridEntity()
            object.id = customer.id
            object.cell = [
                    Tools.LABEL_NEW,
                    customer.id,
                    customer.fullName,
                    customer.nickName,
                    customer.email ? customer.email : Tools.EMPTY_SPACE,
                    DateUtility.getLongDateForUI(customer.dateOfBirth),
                    customer.address ? customer.address : Tools.EMPTY_SPACE]
            Map resultMap = [entity: object, version: customer.version]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, CUSTOMER_UPDATE_SUCCESS_MESSAGE)
            result.put(CUSTOMER, resultMap)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CUSTOMER_UPDATE_FAILURE_MESSAGE)
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
            result.put(Tools.MESSAGE, CUSTOMER_UPDATE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CUSTOMER_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }
}
