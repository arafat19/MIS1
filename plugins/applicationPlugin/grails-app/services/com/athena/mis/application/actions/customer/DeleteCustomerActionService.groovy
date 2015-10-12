package com.athena.mis.application.actions.customer

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Customer
import com.athena.mis.application.service.CustomerService
import com.athena.mis.application.utility.CustomerCacheUtility
import com.athena.mis.integration.accounting.AccountingPluginConnector
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to delete customer object from DB as well as from cache
 *  For details go through Use-Case doc named 'DeleteCustomerActionService'
 */
class DeleteCustomerActionService extends BaseService implements ActionIntf {

    CustomerService customerService
    @Autowired
    CustomerCacheUtility customerCacheUtility
    @Autowired(required = false)
    AccountingPluginConnector accountingImplService

    private static final String HAS_ASSOCIATION_MESSAGE_VOUCHER_DETAILS = " voucher information is associated with this customer"
    private static final String DELETE_CUSTOMER_SUCCESS_MESSAGE = "Customer has been deleted successfully"
    private static final String DELETE_CUSTOMER_FAILURE_MESSAGE = "Customer could not be deleted, Please refresh the Customer List"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Check different criteria to delete customer object
     *      1) Check existence of customer object
     *      2) Check association with AccVoucherDetails
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap params = (GrailsParameterMap) parameters
            long customerId = Long.parseLong(params.id.toString())
            Customer customer = (Customer) customerCacheUtility.read(customerId)
            if (!customer) {//check existence on object
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }

            //Check association
            Map associationResult = (Map) hasAssociation(customer)
            Boolean hasAssociation = (Boolean) associationResult.get(Tools.HAS_ASSOCIATION)
            if (hasAssociation.booleanValue()) {
                result.put(Tools.MESSAGE, associationResult.get(Tools.MESSAGE))
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_CUSTOMER_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Delete company object from DB & cache
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            long customerId = Long.parseLong(parameterMap.id.toString())
            //delete from DB
            Boolean result = (Boolean) customerService.delete(customerId)
            //Delete from cache
            customerCacheUtility.delete(customerId)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException('Failed to delete customer')
            return Boolean.FALSE
        }
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * contains delete success message to show on UI
     * @param obj -N/A
     * @return -a map contains boolean value(true) and delete success message to show on UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        return [deleted: Boolean.TRUE.booleanValue(), message: DELETE_CUSTOMER_SUCCESS_MESSAGE]
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
            result.put(Tools.MESSAGE, DELETE_CUSTOMER_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_CUSTOMER_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * check different association with customer while deleting customer
     * @param customer -Customer object
     * @return -a map contains boolean value(true/false) & association message
     */
    private LinkedHashMap hasAssociation(Customer customer) {
        LinkedHashMap result = new LinkedHashMap()
        int customerId = customer.id
        int count = 0
        result.put(Tools.HAS_ASSOCIATION, Boolean.TRUE)

        count = countVoucherDetails(customerId)
        if (count.intValue() > 0) {
            result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_VOUCHER_DETAILS)
            return result
        }

        result.put(Tools.HAS_ASSOCIATION, Boolean.FALSE)
        return result
    }


    private int countVoucherDetails(int customerId) {
        String queryStr = """
            SELECT COUNT(id) AS count
            FROM acc_voucher_details
            WHERE source_type_id = ${accountingImplService.getAccSourceTypeCustomer()}
            AND source_id = ${customerId}
            """
        log.debug(query)
        List results = executeSelectSql(queryStr)
        int count = results[0].count
        return count
    }
}
