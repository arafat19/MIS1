package com.athena.mis.application.actions.employee

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.Employee
import com.athena.mis.application.service.EmployeeService
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.application.utility.EmployeeCacheUtility
import com.athena.mis.application.utility.SystemEntityTypeCacheUtility
import com.athena.mis.integration.accounting.AccountingPluginConnector
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Delete employee object from DB and cache utility and remove it from grid
 *  For details go through Use-Case doc named 'DeleteEmployeeActionService'
 */
class DeleteEmployeeActionService extends BaseService implements ActionIntf {

    EmployeeService employeeService
    @Autowired
    EmployeeCacheUtility employeeCacheUtility
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility
    @Autowired
    SystemEntityTypeCacheUtility systemEntityTypeCacheUtility
    @Autowired(required = false)
    AccountingPluginConnector accountingImplService

    private static final String HAS_ASSOCIATION_MESSAGE_VOUCHER_DETAILS = " voucher information(s) associated with this employee"
    private static final String HAS_ASSOCIATION_MESSAGE_IOU_SLIP = " Iou Slip(s) associated with this employee"
    private static final String HAS_ASSOCIATION_MESSAGE_APP_USER = " user(s) associated with this employee"
    private static final String EMPLOYEE_DELETE_SUCCESS_MSG = "Employee has been successfully deleted"
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to delete employee"
    private static final String DELETED = "deleted"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Checking pre condition and association before deleting the employee object
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            GrailsParameterMap params = (GrailsParameterMap) parameters
            // check required parameters
            if (!params.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long employeeId = Long.parseLong(params.id.toString())
            Employee employee = (Employee) employeeCacheUtility.read(employeeId)    // get employee object
            // check whether selected employee object exists or not
            if (!employee) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }

            // check association of employee with relevant domains
            Map associationResult = (Map) hasAssociation(employee)
            Boolean hasAssociation = (Boolean) associationResult.get(Tools.HAS_ASSOCIATION)
            if (hasAssociation.booleanValue()) {
                result.put(Tools.MESSAGE, associationResult.get(Tools.MESSAGE))
                return result
            }

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Delete employee object from DB and cache utility
     * This function is in transactional boundary and will roll back in case of any exception
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            long employeeId = Long.parseLong(parameterMap.id.toString())
            employeeService.delete(employeeId)    // delete employee object from DB
            Employee employee = (Employee) employeeCacheUtility.read(employeeId)
            employeeCacheUtility.delete(employee.id)   // delete employee object from cache utility
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(DEFAULT_ERROR_MESSAGE)
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
     * Remove object from grid
     * Show success message
     * @param obj -N/A
     * @return -a map containing success message for UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        result.put(DELETED, Boolean.TRUE)
        result.put(Tools.MESSAGE, EMPLOYEE_DELETE_SUCCESS_MSG)
        return result
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
     * Check association of employee with relevant domains
     * @param employee -employee object
     * @return --a map containing hasAssociation(true/false) and relevant message
     */
    private LinkedHashMap hasAssociation(Employee employee) {
        LinkedHashMap result = new LinkedHashMap()
        int employeeId = employee.id
        int count = 0
        result.put(Tools.HAS_ASSOCIATION, Boolean.TRUE)

        count = countAppUser(employeeId)    // count appUser associated with the employee
        if (count.intValue() > 0) {
            result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_APP_USER)
            return result
        }

        if (PluginConnector.isPluginInstalled(PluginConnector.ACCOUNTING)) {

            count = countVoucherDetails(employeeId) // count accVoucherDetails associated with the employee
            if (count.intValue() > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_VOUCHER_DETAILS)
                return result
            }

            count = countIouSlip(employeeId)    // count accIouSlip associated with the employee
            if (count.intValue() > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_IOU_SLIP)
                return result
            }
        }

        result.put(Tools.HAS_ASSOCIATION, Boolean.FALSE)
        return result
    }

    private static final String QUERY_APP_USER = """
            SELECT COUNT(id) AS count
            FROM app_user
            WHERE employee_id = :employeeId
    """

    /**
     * Count appUser associated with the employee
     * @param employeeId -id of employee object
     * @return -an integer containing the value of count
     */
    private int countAppUser(int employeeId) {
        Map queryParams = [employeeId: employeeId]
        List results = executeSelectSql(QUERY_APP_USER, queryParams)
        int count = results[0].count
        return count
    }

    private static final String QUERY_VOUCHER_DETAILS = """
            SELECT COUNT(id) AS count
            FROM acc_voucher_details
            WHERE source_type_id = :sourceTypeId
            AND source_id = :employeeId
    """

    /**
     * Count accVoucherDetails associated with the employee
     * @param employeeId -id of employee object
     * @return -an integer containing the value of count
     */
    private int countVoucherDetails(int employeeId) {
        Map queryParams = [
                employeeId: employeeId,
                sourceTypeId: accountingImplService.getAccSourceTypeEmployee()
        ]
        List results = executeSelectSql(QUERY_VOUCHER_DETAILS, queryParams)
        int count = results[0].count
        return count
    }

    private static final String QUERY_IOU_SLIP = """
            SELECT COUNT(id) AS count
            FROM acc_iou_slip
            WHERE employee_id = :employeeId
    """

    /**
     * Count accIouSlip associated with the employee
     * @param employeeId -id of employee object
     * @return -an integer containing the value of count
     */
    private int countIouSlip(int employeeId) {
        Map queryParams = [employeeId: employeeId]
        List results = executeSelectSql(QUERY_IOU_SLIP, queryParams)
        int count = results[0].count
        return count
    }
}
