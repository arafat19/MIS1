package com.athena.mis.application.actions.userrole

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.UserRole
import com.athena.mis.application.service.UserRoleService
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to create User-role mapping object and to show on grid list
 *  For details go through Use-Case doc named 'CreateUserRoleActionService'
 */
class CreateUserRoleActionService extends BaseService implements ActionIntf {

    UserRoleService userRoleService
    @Autowired
    AppSessionUtil appSessionUtil

    private static final String FAILURE_MESSAGE = 'Failed to create User-Role'
    private static final String SUCCESS_MESSAGE = 'User-Role mapping saved successfully'
    private static final String INVALID_INPUT = 'Error occurred for invalid input'

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Check different criteria for creating new User-Role mapping object
     *      1) Check access permission to create User-Role mapping object
     *      2) Check existence of same userRole mapping object
     *      3) Validate userRole object
     * @param params -N/A
     * @param obj -UserRole object send from controller
     * @return -a map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)

            //Only admin can create User-Role mapping object
            if (!appSessionUtil.getAppUser().isPowerUser) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }

            UserRole userRole = (UserRole) obj
            userRole.validate()
            if (userRole.hasErrors()) { //validate object
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Save User-Role mapping object in DB
     * @param parameters -N/A
     * @param obj -contains userRole mapping object send from controller
     * @return -contains newly created userRole mapping object for buildSuccessResultForUI
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        try {
            UserRole userRoleInstance = (UserRole) obj
            UserRole savedUserRoleInstance = userRoleService.create(userRoleInstance)
            return savedUserRoleInstance
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException('Failed to create user-role mapping')
            return null
        }
    }

    /**
     * do nothing at post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap newly created userRole mapping object to show on grid
     * @param obj -a map contains userRole mapping object send from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            UserRole userRole = (UserRole) obj
            GridEntity objGrid = new GridEntity()
            objGrid.id = userRole.user.id
            objGrid.cell = [
                    Tools.LABEL_NEW,
                    userRole.user.id,
                    userRole.role.id,
                    userRole.user.username
            ]
            result.put(Tools.ENTITY, objGrid)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, SUCCESS_MESSAGE)
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
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap receivedResult = (LinkedHashMap) obj
                String receivedMessage = receivedResult.get(Tools.MESSAGE)
                if (receivedMessage) {
                    result.put(Tools.MESSAGE, receivedMessage)
                    return result
                }
            }
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }
}
