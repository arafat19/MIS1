package com.athena.mis.sarb.actions.taskmodel

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.sarb.entity.SarbTaskDetails
import com.athena.mis.sarb.model.SarbTaskModel
import com.athena.mis.sarb.service.SarbTaskDetailsService
import com.athena.mis.sarb.service.SarbTaskModelService
import com.athena.mis.sarb.utility.SarbSessionUtil
import com.athena.mis.sarb.utility.SarbTaskReviseStatusCacheUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 * Move sarb task to send for refund
 * for details go through use-case named "MoveSarbTaskForRefundActionService"
 */
class MoveSarbTaskForRefundActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    SarbTaskDetailsService sarbTaskDetailsService
    SarbTaskModelService sarbTaskModelService
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    SarbSessionUtil sarbSessionUtil
    @Autowired
    SarbTaskReviseStatusCacheUtility sarbTaskReviseStatusCacheUtility

    private static final String DEFAULT_ERROR_MESSAGE = "Task could not be moved"
    private static final String TASK_NOT_FOUND = "Task not found"
    private static final String ONLY_ACCEPTED_TASK_CAN_BE_REFUND = "Unaccepted task can not be refunded"
    private static final String ALREADY_CANCELLED = "Cancelled and Accepted task can not be refunded"
    private static final String TASK_MODEL = "sarbTaskModel"
    private static final String TASK_RESEND_SUCCESS = "Task has been moved to refund"
    private static final String CONFIG_MANAGER_CAN_PERFORM_THIS = "Only config manager can perform this operation"

    /**
     * 1. Check for config manager to perform the operation
     * 2. Only accepted task can be moved
     * 3. Not cancelled task can be moved
     * @param parameters - parameters from UI
     * @param obj -N/A
     * @return- map containing sarbTaskDetails
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {

        Map result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            /*long appUserId = sarbSessionUtil.appSessionUtil.getAppUser().id
            AppUser appUser = (AppUser) appUserCacheUtility.read(appUserId)
            if (!appUser.isConfigManager) {
                result.put(Tools.MESSAGE, CONFIG_MANAGER_CAN_PERFORM_THIS)
                return result
            }*/

            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            long taskId = Long.parseLong(parameterMap.id)
            SarbTaskModel taskModel = sarbTaskModelService.read(taskId)
            if (!taskModel) {
                result.put(Tools.MESSAGE, TASK_NOT_FOUND)
                return result
            }
            if (!taskModel.isAcceptedBySarb) {
                result.put(Tools.MESSAGE, ONLY_ACCEPTED_TASK_CAN_BE_REFUND)
                return result
            }
            if (taskModel.isCancelled) {
                result.put(Tools.MESSAGE, ALREADY_CANCELLED)
                return result
            }
            result.put(TASK_MODEL, taskModel)
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
     * do nothing for post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * update sarbTask for move for refund
     * @param parameters - parameters return from executePrecondition
     * @param obj -N/A
     * @return isError-false
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj
            SarbTaskModel sarbTaskModel = (SarbTaskModel) executeResult.get(TASK_MODEL)
            SystemEntity movedForRefund = (SystemEntity) sarbTaskReviseStatusCacheUtility.readByReservedAndCompany(SarbTaskReviseStatusCacheUtility.MOVED_FOR_REFUND, sarbSessionUtil.appSessionUtil.getCompanyId())
            SarbTaskDetails sarbTaskDetails = sarbTaskDetailsService.findByTaskIdAndEnabled(sarbTaskModel.id)
            sarbTaskDetailsService.updateForMove(sarbTaskDetails, movedForRefund.id)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(DEFAULT_ERROR_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * build success result for ui
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        result.put(Tools.MESSAGE, TASK_RESEND_SUCCESS)
        result.put(Tools.IS_ERROR, Boolean.FALSE)
        return result
    }

    /**
     * build failure result for ui
     */
    public Object buildFailureResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map preResult = (Map) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (preResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            }
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }
}
