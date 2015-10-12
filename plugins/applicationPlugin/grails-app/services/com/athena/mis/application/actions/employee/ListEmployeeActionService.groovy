package com.athena.mis.application.actions.employee

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Designation
import com.athena.mis.application.entity.Employee
import com.athena.mis.application.utility.DesignationCacheUtility
import com.athena.mis.application.utility.EmployeeCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Show list of employee for grid
 *  For details go through Use-Case doc named 'ListEmployeeActionService'
 */
class ListEmployeeActionService extends BaseService implements ActionIntf {

    @Autowired
    EmployeeCacheUtility employeeCacheUtility
    @Autowired
    DesignationCacheUtility designationCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String SHOW_EMPLOYEE_FAILURE_MESSAGE = "Failed to load employee page"
    private static final String LST_EMPLOYEES = "lstEmployee"

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null;
    }

    /**
     * Get employee list for grid
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            initPager(parameterMap) // initialize parameters for flexGrid
            int count = employeeCacheUtility.count()    // get count total employee
            List lstEmployees = employeeCacheUtility.list(this)    // get sub list of employee
            result.put(LST_EMPLOYEES, lstEmployees)
            result.put(Tools.COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_EMPLOYEE_FAILURE_MESSAGE)
            return null
        }
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap employee list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj    // cast map returned from execute method
            List lstEmployees = (List) executeResult.get(LST_EMPLOYEES)
            int count = (int) executeResult.get(Tools.COUNT)
            List lstWrappedEmployees = wrapEmployees(lstEmployees, start)
            Map output = [page: pageNumber, total: count, rows: lstWrappedEmployees]
            return output
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_EMPLOYEE_FAILURE_MESSAGE)
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
            result.put(Tools.MESSAGE, SHOW_EMPLOYEE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_EMPLOYEE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Wrap list of employees in grid entity
     * @param lstEmployees -list of employee object(s)
     * @param start -starting index of the page
     * @return -list of wrapped employees
     */
    private List wrapEmployees(List<Employee> lstEmployees, int start) {
        List lstWrappedEmployees = []
        int counter = start + 1
        for (int i = 0; i < lstEmployees.size(); i++) {
            Employee employee = lstEmployees[i]
            Designation designation = (Designation) designationCacheUtility.read(employee.designationId)
            GridEntity obj = new GridEntity()
            obj.id = employee.id
            obj.cell = [
                    counter,
                    employee.id,
                    employee.fullName,
                    employee.nickName,
                    designation.name,
                    employee.mobileNo ? employee.mobileNo : Tools.EMPTY_SPACE,
                    employee.email ? employee.email : Tools.EMPTY_SPACE,
                    DateUtility.getLongDateForUI(employee.dateOfJoin),
                    employee.address
            ]
            lstWrappedEmployees << obj
            counter++
        }
        return lstWrappedEmployees
    }
}
