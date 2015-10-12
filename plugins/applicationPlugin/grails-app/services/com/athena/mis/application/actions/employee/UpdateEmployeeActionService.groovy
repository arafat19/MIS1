package com.athena.mis.application.actions.employee

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Designation
import com.athena.mis.application.entity.Employee
import com.athena.mis.application.service.EmployeeService
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.DesignationCacheUtility
import com.athena.mis.application.utility.EmployeeCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Update employee object and grid data
 *  For details go through Use-Case doc named 'UpdateEmployeeActionService'
 */
class UpdateEmployeeActionService extends BaseService implements ActionIntf {

    EmployeeService employeeService
    @Autowired
    EmployeeCacheUtility employeeCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    DesignationCacheUtility designationCacheUtility

    private static final String EMPLOYEE_UPDATE_FAILURE_MESSAGE = "Employee could not be updated"
    private static final String EMPLOYEE_UPDATE_SUCCESS_MESSAGE = "Employee has been updated successfully"
    private static final String OBJ_NOT_FOUND = "Selected employee not found"
    private static final String EMPLOYEE_OBJ = "employee"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Get parameters from UI and build employee object for update
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            // check required parameters
            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            long employeeId = Long.parseLong(parameterMap.id.toString())
            Employee oldEmployee = (Employee) employeeCacheUtility.read(employeeId) // get employee object
            // check whether selected employee object exists or not
            if (!oldEmployee) {
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND)
                return result
            }

            Employee employee = buildEmployeeObject(parameterMap, oldEmployee)  // build employee object for update

            result.put(EMPLOYEE_OBJ, employee)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, EMPLOYEE_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Update employee object in DB & update cache utility accordingly
     * This function is in transactional block and will roll back in case of any exception
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from executePreCondition method
            Employee employee = (Employee) preResult.get(EMPLOYEE_OBJ)
            employeeService.update(employee)  // update employee object in DB
            // update cache utility accordingly and keep the data sorted
            employeeCacheUtility.update(employee, employeeCacheUtility.SORT_ON_NAME, employeeCacheUtility.SORT_ORDER_ASCENDING)
            result.put(EMPLOYEE_OBJ, employee)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(EMPLOYEE_UPDATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, EMPLOYEE_UPDATE_FAILURE_MESSAGE)
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
     * Show updated employee object in grid
     * Show success message
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            Employee employee = (Employee) executeResult.get(EMPLOYEE_OBJ)
            Designation designation = (Designation) designationCacheUtility.read(employee.designationId)
            GridEntity object = new GridEntity()    // build grid entity object
            object.id = employee.id
            object.cell = [
                    Tools.LABEL_NEW,
                    employee.id,
                    employee.fullName,
                    employee.nickName,
                    designation.name,
                    employee.mobileNo ? employee.mobileNo : Tools.EMPTY_SPACE,
                    employee.email ? employee.email : Tools.EMPTY_SPACE,
                    DateUtility.getLongDateForUI(employee.dateOfJoin),
                    employee.address
            ]

            result.put(Tools.MESSAGE, EMPLOYEE_UPDATE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(Tools.VERSION, employee.version)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, EMPLOYEE_UPDATE_FAILURE_MESSAGE)
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
            result.put(Tools.MESSAGE, EMPLOYEE_UPDATE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, EMPLOYEE_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build employee object for update
     * @param parameterMap -serialized parameters from UI
     * @param oldEmployee -old employee object
     * @return -updated employee object
     */
    private Employee buildEmployeeObject(GrailsParameterMap parameterMap, Employee oldEmployee) {
        Employee employee = new Employee(parameterMap)
        employee.id = oldEmployee.id
        employee.version = oldEmployee.version
        employee.dateOfBirth = DateUtility.parseMaskedDate(parameterMap.dateOfBirth.toString())
        employee.dateOfJoin = DateUtility.parseMaskedDate(parameterMap.dateOfJoin.toString())
        employee.createdOn = oldEmployee.createdOn
        employee.createdBy = oldEmployee.createdBy
        employee.updatedOn = new Date()
        employee.updatedBy = appSessionUtil.getAppUser().id
        employee.companyId = oldEmployee.companyId
        return employee
    }
}