package com.athena.mis.arms.actions.rmstask

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.arms.entity.RmsTask
import com.athena.mis.arms.service.RmsTaskListService
import com.athena.mis.arms.service.RmsTaskService
import com.athena.mis.arms.service.RmsTaskTraceService
import com.athena.mis.arms.utility.RmsSessionUtil
import com.athena.mis.arms.utility.RmsTaskStatusCacheUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class ForwardRmsTaskActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    RmsTaskService rmsTaskService
    RmsTaskListService rmsTaskListService
    RmsTaskTraceService rmsTaskTraceService
    @Autowired
    RmsSessionUtil rmsSessionUtil
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility
    @Autowired
    RmsTaskStatusCacheUtility rmsTaskStatusCacheUtility

    public static final String TASK_NOT_FOUND_MESSAGE = "Task could not found"
    public static final String DISTRICT_NOT_FOUND = "District not found"
    public static final String BANK_NOT_FOUND = "Bank not found"
    public static final String BRANCH_NOT_FOUND = "Branch not found"
    public static final String DEFAULT_ERROR_MESSAGE = "Task could not be forwarded"
    public static final String TASK_FORWARDED_SUCCESSFULLY = "Task forwarded successfully"
    public static final String DISTRICT_ID = "districtId"
    public static final String BRANCH_ID = "branchId"
    public static final String FORWARD_MSG_FOR_UI = "forwardMsg"
    public static final String ADMIN_CAN_FORWARD_TASK = "Only admin can forward this task"
    public static final String CAN_FORWARD_TASK = "canForwardTask"
    public static final String NOT_SAME_BRANCH = "Task can not be forwarded to existing mapped branch"
    public static final String FAILURE_MSG = "Task can not be forwarded"
    private static final String DISBURSED_FAILED = "Only approved task can be forwarded"
    private static final String BANK_ID = "bankId"

    /**
     * Get serialized parameters From UI
     * @param parameters - parameters from UI
     * @param obj -N/A
     * @return true/false and necessary obj for execute
     */

    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(CAN_FORWARD_TASK, Boolean.FALSE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.taskId) {                       // check required parameter
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            if (!parameterMap.bankId) {
                result.put(Tools.MESSAGE, BANK_NOT_FOUND)
                return result
            }
            if (!parameterMap.districtId) {
                result.put(Tools.MESSAGE, DISTRICT_NOT_FOUND)
                return result
            }
            if (!parameterMap.branchId) {
                result.put(Tools.MESSAGE, BRANCH_NOT_FOUND)
                return result
            }
            long taskId = Long.parseLong(parameterMap.taskId.toString())
            long bankId = Long.parseLong(parameterMap.bankId.toString())
            long districtId = Long.parseLong(parameterMap.districtId.toString())
            long branchId = Long.parseLong(parameterMap.branchId.toString())
            RmsTask rmsTask = rmsTaskService.read(taskId)
            if (!rmsTask) {
                result.put(Tools.MESSAGE, TASK_NOT_FOUND_MESSAGE)
                return result
            }
            if (branchId == rmsTask.mappingBranchId) {
                result.put(Tools.MESSAGE, NOT_SAME_BRANCH)
                return result
            }
            Boolean isBranchUser = rmsSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_ARMS_BRANCH_USER)
            Boolean isOtherBankUser = rmsSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_ARMS_OTHER_BANK_USER)
            long bankBranchId = rmsSessionUtil.appSessionUtil.getUserBankBranchId()
            if ((isBranchUser.booleanValue() || isOtherBankUser.booleanValue())) {

                if (rmsTask.mappingBranchId != bankBranchId) {
                    result.put(Tools.MESSAGE, ADMIN_CAN_FORWARD_TASK)
                    return result
                }
                result.put(CAN_FORWARD_TASK, Boolean.TRUE)
            }
            SystemEntity statusApproved = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(rmsTaskStatusCacheUtility.DECISION_APPROVED, rmsSessionUtil.appSessionUtil.getCompanyId())
            long currentStatusId = statusApproved.id
            if (currentStatusId != rmsTask.currentStatus) {
                result.put(Tools.MESSAGE, DISBURSED_FAILED)
                return result
            }
            result.put(BANK_ID, bankId)
            result.put(DISTRICT_ID, districtId)
            result.put(BRANCH_ID, branchId)
            result.put(Tools.ENTITY, rmsTask)
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
     * Do nothing for executePostCondition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * Update mappingDistrictId and mapping branchId of RmsTask
     * @param parameters -parameters from executePreCondition
     * @param obj -N/A
     * @return result
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map executeResult = (Map) parameters
            boolean canForwardTask = (boolean) executeResult.get(CAN_FORWARD_TASK)
            if (!canForwardTask.booleanValue()) {
                result.put(Tools.MESSAGE, ADMIN_CAN_FORWARD_TASK)
                return result
            }
            RmsTask rmsTask = (RmsTask) executeResult.get(Tools.ENTITY)
            long bankId = (long) executeResult.get(BANK_ID)
            long districtId = (long) executeResult.get(DISTRICT_ID)
            long branchId = (long) executeResult.get(BRANCH_ID)
            rmsTaskService.updateTaskForForward(rmsTask, bankId, districtId, branchId)
            rmsTaskTraceService.create(rmsTask)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(TASK_NOT_FOUND_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, TASK_NOT_FOUND_MESSAGE)
            return result
        }
    }

    /**
     * Show success message
     * @param obj -N/A
     * @return success msg and forwardMsg for UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        result.put(Tools.IS_ERROR, Boolean.FALSE)
        result.put(Tools.MESSAGE, TASK_FORWARDED_SUCCESSFULLY)
        result.put(FORWARD_MSG_FOR_UI, Boolean.TRUE)
        return result
    }

    /**
     * Show failure message in case of any failure
     * @param obj -N/A
     * @return failure msg and forwardMsg for UI
     */

    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            Map preResult = (Map) obj
            String msg = preResult.get(Tools.MESSAGE)
            if (msg) {
                result.put(Tools.MESSAGE, msg)
            } else {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            }
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(FORWARD_MSG_FOR_UI, Boolean.TRUE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }
}
