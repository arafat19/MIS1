package com.athena.mis.arms.actions.rmstask

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.arms.entity.RmsTask
import com.athena.mis.arms.service.RmsTaskService
import com.athena.mis.arms.utility.RmsSessionUtil
import com.athena.mis.arms.utility.RmsTaskStatusCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Select Task object
 *  For details go through Use-Case doc named 'SelectRmsTaskActionService'
 */
class SelectRmsTaskActionService extends BaseService implements ActionIntf {

    RmsTaskService rmsTaskService
    @Autowired
    RmsSessionUtil rmsSessionUtil
    @Autowired
    RmsTaskStatusCacheUtility rmsTaskStatusCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String TASK_NOT_FOUND_MESSAGE = "Selected task is not found"
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to select task"
    private static final String VALUE_DATE = "valueDate"
    private static final String DISABLE_EXH = "disableExh"
    private static final String DISABLE_PAY = "disablePay"

    /**
     * Do nothing for pre operation
     */
    Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Do nothing for post operation
     */
    Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get parameters from UI and build Task object for select
     * 1. Check validity for input
     * 2. Check existence of Task object
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long taskId = Long.parseLong(parameterMap.id)
            // Get Task object by id
            RmsTask task = rmsTaskService.read(taskId)
            if (!task) {
                result.put(Tools.MESSAGE, TASK_NOT_FOUND_MESSAGE)
                return result
            }
            if (parameterMap.isExhUser) {
                long exhId = rmsSessionUtil.getUserExchangeHouseId()
                if (task.exchangeHouseId != exhId) {
                    result.put(Tools.MESSAGE, TASK_NOT_FOUND_MESSAGE)
                    return result
                }
            }
            result.put(Tools.ENTITY, task)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, TASK_NOT_FOUND_MESSAGE)
            return result
        }
    }

    /**
     * Build a map with Task object & other related properties to show on UI
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
            RmsTask task = (RmsTask) executeResult.get(Tools.ENTITY)
            long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
            SystemEntity includedInList = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(RmsTaskStatusCacheUtility.INCLUDED_IN_LIST, companyId)
            SystemEntity decisionTaken = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(RmsTaskStatusCacheUtility.DECISION_TAKEN, companyId)
            SystemEntity decisionApp = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(RmsTaskStatusCacheUtility.DECISION_APPROVED, companyId)
            SystemEntity disbursed = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(RmsTaskStatusCacheUtility.DISBURSED, companyId)
            SystemEntity canceled = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(RmsTaskStatusCacheUtility.CANCELED, companyId)
            long currentStatus = task.currentStatus
            if (currentStatus == includedInList.id) {
                result.put(DISABLE_EXH, Boolean.TRUE)
            }
            if ((currentStatus == decisionTaken.id) || (currentStatus == decisionApp.id) || (currentStatus == disbursed.id) || (currentStatus == canceled.id)) {
                result.put(DISABLE_EXH, Boolean.TRUE)
                result.put(DISABLE_PAY, Boolean.TRUE)
            }

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.ENTITY, task)
            result.put(Tools.VERSION, task.version)
            result.put(VALUE_DATE, DateUtility.getDateForUI(task.valueDate))
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (!obj) {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
                return result
            }
            Map previousResult = (Map) obj
            result.put(Tools.MESSAGE, previousResult.get(Tools.MESSAGE))
            return result
        }
        catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }
}
