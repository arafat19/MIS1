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
 *  Create new employee object and show in grid
 *  For details go through Use-Case doc named 'CreateEmployeeActionService'
 */
class CreateEmployeeActionService extends BaseService implements ActionIntf {

    EmployeeService employeeService
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    EmployeeCacheUtility employeeCacheUtility
    @Autowired
    DesignationCacheUtility designationCacheUtility

    private static final String EMPLOYEE_CREATE_SUCCESS_MSG = "Employee has been successfully saved"
    private static final String EMPLOYEE_CREATE_FAILURE_MSG = "Employee has not been saved"
    private static final String EMPLOYEE = "employee"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Get parameters from UI and build employee object
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            Employee employee = buildEmployeeObject(parameterMap)   // build employee object
            result.put(EMPLOYEE, employee)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, EMPLOYEE_CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Save employee object in DB and update cache utility accordingly
     * This method is in transactional block and will roll back in case of any exception
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
            Employee employee = (Employee) preResult.get(EMPLOYEE)
            Employee savedEmployeeObj = employeeService.create(employee)  // save new employee object in DB
            // add new employee object in cache utility and keep the data sorted
            employeeCacheUtility.add(savedEmployeeObj, employeeCacheUtility.SORT_ON_NAME, employeeCacheUtility.SORT_ORDER_ASCENDING)
            result.put(EMPLOYEE, savedEmployeeObj)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(EMPLOYEE_CREATE_FAILURE_MSG)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, EMPLOYEE_CREATE_FAILURE_MSG)
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
     * Show newly created employee object in grid
     * Show success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            Employee employee = (Employee) executeResult.get(EMPLOYEE)
            Designation designation = (Designation) designationCacheUtility.read(employee.designationId)
            GridEntity object = new GridEntity()    //build grid object
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

            result.put(Tools.MESSAGE, EMPLOYEE_CREATE_SUCCESS_MSG)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, EMPLOYEE_CREATE_FAILURE_MSG)
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
            result.put(Tools.MESSAGE, EMPLOYEE_CREATE_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, EMPLOYEE_CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Build employee object
     * @param parameterMap -serialized parameters from UI
     * @return -new employee object
     */
    private Employee buildEmployeeObject(GrailsParameterMap parameterMap) {
        Employee employee = new Employee(parameterMap)
        employee.dateOfBirth = DateUtility.parseMaskedDate(parameterMap.dateOfBirth.toString())
        employee.dateOfJoin = DateUtility.parseMaskedDate(parameterMap.dateOfJoin.toString())
        employee.companyId = appSessionUtil.getCompanyId()
        employee.createdOn = new Date()
        employee.createdBy = appSessionUtil.getAppUser().id
        employee.updatedOn = null
        employee.updatedBy = 0
        return employee
    }
}