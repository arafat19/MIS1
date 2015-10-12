package com.athena.mis.sarb.actions.taskmodel

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import com.athena.mis.sarb.model.SarbTaskModel
import com.athena.mis.sarb.service.SarbTaskModelService
import com.athena.mis.sarb.utility.SarbSessionUtil
import com.athena.mis.sarb.utility.SarbTaskReviseStatusCacheUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 * update exhTask, exhBeneficiary for replace task
 * for details go through use-case doc named "UpdateTaskForReplaceTaskActionService"
 */
class UpdateTaskForReplaceTaskActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String SUCCESS_MSG = "Task updated successfully"
    private static final String FAILURE_MSG = "Failed to update task"
    private static final String NOT_FOUND = "Task not found"
    private static final String STATUS_MISMATCH = "Task status mismatched. Refresh the grid and try again"

    SarbTaskModelService sarbTaskModelService
    @Autowired
    ExchangeHousePluginConnector exchangeHouseImplService
    @Autowired
    SarbTaskReviseStatusCacheUtility sarbTaskReviseStatusCacheUtility
    @Autowired
    SarbSessionUtil sarbSessionUtil

    /**
     * do nothing
     */
    Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * do nothing
     */
    Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /** update exhTask, exhBeneficiary through exchange house interface
     * 1. check if task exists
     * 2. check if task status sent to bank, sent to other bank, resolved by other bank
     * 3. check if sarb task enabled is false and reviseStatus is movedForReplace
     * @param parameters - params from ui
     * @param obj
     * @return
     */
    @Transactional
    Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            if (!params.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long taskId = Tools.parseLongInput(params.id)
            SarbTaskModel taskModel = sarbTaskModelService.read(taskId)
            if (!taskModel) {
                result.put(Tools.MESSAGE, NOT_FOUND)
                return result
            }
            List<Long> lstTaskStatus = exchangeHouseImplService.listTaskStatusForSarb()
            SystemEntity movedForReplace = (SystemEntity) sarbTaskReviseStatusCacheUtility.readByReservedAndCompany(SarbTaskReviseStatusCacheUtility.MOVED_FOR_REPLACE, sarbSessionUtil.appSessionUtil.getCompanyId())
            if (!lstTaskStatus.contains(taskModel.currentStatus.longValue()) || taskModel.enabled || (taskModel.reviseStatus != movedForReplace.id)) {
                result.put(Tools.MESSAGE, STATUS_MISMATCH)
                return result
            }
            Map updateResult = exchangeHouseImplService.updateExhTaskForReplaceTask(params) //update exchange house task in DB
            Boolean isError = updateResult.isError
            if(isError.booleanValue()) {
                String msg = updateResult.message
                if(msg) {
                    result.put(Tools.MESSAGE, msg)
                    return result
                }
                throw new RuntimeException(FAILURE_MSG)
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, SUCCESS_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(FAILURE_MSG)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    /**
     * do nothing
     */
    Object buildSuccessResultForUI(Object obj) {
        return null
    }

    /**
     * build failure result for ui
     */
    Object buildFailureResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                Map preResult = (Map) obj
                String msg = preResult.get(Tools.MESSAGE)
                if (msg) {
                    result.put(Tools.MESSAGE, msg)
                } else {
                    result.put(Tools.MESSAGE, FAILURE_MSG)
                }
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

}
