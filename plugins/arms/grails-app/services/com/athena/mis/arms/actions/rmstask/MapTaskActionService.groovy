package com.athena.mis.arms.actions.rmstask

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.arms.entity.RmsCommissionDetails
import com.athena.mis.arms.entity.RmsPurchaseInstrumentMapping
import com.athena.mis.arms.entity.RmsTask
import com.athena.mis.arms.service.*
import com.athena.mis.arms.utility.RmsProcessTypeCacheUtility
import com.athena.mis.arms.utility.RmsSessionUtil
import com.athena.mis.arms.utility.RmsTaskStatusCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Update task object
 *  For details go through Use-Case doc named 'MapTaskActionService'
 */
class MapTaskActionService extends BaseService implements ActionIntf {

    RmsTaskService rmsTaskService
    RmsTaskListService rmsTaskListService
    RmsTaskTraceService rmsTaskTraceService
    RmsPurchaseInstrumentMappingService rmsPurchaseInstrumentMappingService
    RmsCommissionDetailsService rmsCommissionDetailsService
    @Autowired
    RmsSessionUtil rmsSessionUtil
    @Autowired
    RmsTaskStatusCacheUtility rmsTaskStatusCacheUtility
    @Autowired
    RmsProcessTypeCacheUtility rmsProcessTypeCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String TASK_MAP_FAILURE_MSG = "Task has not been mapped"
    private static final String TASK_MAP_SUCCESS_MSG = "Task has been successfully mapped"
    private static final String LST_TASK_IDS = "lstTaskIds"
    private static final String PROCESS_TYPE = "processType"
    private static final String INSTRUMENT_TYPE = "instrumentType"
    private static final String BANK = "bank"
    private static final String DISTRICT = "district"
    private static final String BRANCH = "branch"
    private static final String REFRESH_PAGE = "Please refresh the page"
    private static final String NOT_OPENED = "Transaction day is not opened"
    private static final String LST_RMS_TASK = "lstRmsTask"
    private static final String TASK_IDS = "taskIds"
    //variable for calculating commission details
    private static final String P_AMOUNT = "pAmount"
    private static final String COMMISSION_DETAILS = "commissionDetails"

    /**
     * 1. Get parameters from UI
     * 2. check if transaction day is opened
     * 3. check if task list is locked
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            List<Long> lstTaskIds = Tools.getIdsFromParams(params, TASK_IDS)
            Long currentStatus = Long.parseLong(params.currentStatus)
            List<RmsTask> lstRmsTask = rmsTaskService.findAllByCurrentStatusAndIdInList(currentStatus, lstTaskIds)
            if (lstRmsTask.size() != lstTaskIds.size()) {
                result.put(Tools.MESSAGE, REFRESH_PAGE)
                return result
            }
            boolean isOpened = isTransactionDayOpened()
            if (!isOpened) {
                result.put(Tools.MESSAGE, NOT_OPENED)
                return result
            }

            Long processType = Long.parseLong(params.processType)
            Long instrumentType = Long.parseLong(params.instrumentType)
            Long bank = Long.parseLong(params.bank)
            Long district = Long.parseLong(params.district)
            Long branch = Long.parseLong(params.branch)
            result.put(LST_RMS_TASK, lstRmsTask)
            result.put(LST_TASK_IDS, lstTaskIds)
            result.put(PROCESS_TYPE, processType)
            result.put(INSTRUMENT_TYPE, instrumentType)
            result.put(BANK, bank)
            result.put(DISTRICT, district)
            result.put(BRANCH, branch)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, TASK_MAP_FAILURE_MSG)
            return result
        }
    }

    /**
     * 1. Update task for map and create commission details
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            SystemEntity decisionTaken = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(rmsTaskStatusCacheUtility.DECISION_TAKEN, rmsSessionUtil.appSessionUtil.getCompanyId())
            LinkedHashMap preResult = (LinkedHashMap) obj
            List<Long> lstTaskIds = (List<Long>) preResult.get(LST_TASK_IDS)
            List<RmsTask> lstRmsTask = (List<RmsTask>) preResult.get(LST_RMS_TASK)
            Long processType = (Long) preResult.get(PROCESS_TYPE)
            Long instrumentType = (Long) preResult.get(INSTRUMENT_TYPE)
            Long bank = (Long) preResult.get(BANK)
            Long district = (Long) preResult.get(DISTRICT)
            Long branch = (Long) preResult.get(BRANCH)
            rmsTaskService.updateForDecisionTaken(lstTaskIds, decisionTaken.id, processType, instrumentType, bank, district, branch)
            createCommissionDetails(lstRmsTask, processType.longValue(), branch.longValue(), instrumentType.longValue())
            createRmsTaskTrace(lstRmsTask, decisionTaken.id)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(TASK_MAP_FAILURE_MSG)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, TASK_MAP_FAILURE_MSG)
            return result
        }
    }

    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Build success message
     * @param obj -N/A
     * @return -a map containing all objects necessary to indicate success event
     * map contains isError(false)
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        result.put(Tools.MESSAGE, TASK_MAP_SUCCESS_MSG)
        result.put(Tools.IS_ERROR, Boolean.FALSE)
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
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, TASK_MAP_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, TASK_MAP_FAILURE_MSG)
            return result
        }
    }

    private void createRmsTaskTrace(List<RmsTask> lstRmsTask, long currentStatus) {
        for (int i = 0; i < lstRmsTask.size(); i++) {
            RmsTask rmsTask = lstRmsTask[i]
            rmsTask.previousStatus = rmsTask.currentStatus
            rmsTask.currentStatus = currentStatus
            rmsTask.isRevised = Boolean.FALSE
            rmsTaskTraceService.create(rmsTask)
        }
    }

    private boolean isTransactionDayOpened() {
        long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
        String query = """
        SELECT count(id) from rms_transaction_day where company_id = :companyId and closed_on is null
        """
        Map queryParam = [companyId: companyId]

        List result = executeSelectSql(query, queryParam)
        int count = result[0].count
        (count > 0) ? Boolean.TRUE : Boolean.FALSE
    }

    /**
     * create RmsCommissionDetails for each task and update RmsTask.commissionDetailsId
     * @param lstRmsTask - list of rmsTask
     */
    private void createCommissionDetails(List<RmsTask> lstRmsTask, long processType, long branchId, long instrumentType) {
        long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity purchaseObj = (SystemEntity) rmsProcessTypeCacheUtility.readByReservedAndCompany(RmsProcessTypeCacheUtility.PURCHASE, companyId)
        if (processType != purchaseObj.id) {
            return
        }
        RmsPurchaseInstrumentMapping purchaseInstrumentMapping = rmsPurchaseInstrumentMappingService.readByCompanyIdAndBranchIdAndInstrumentTypeId(companyId, branchId, instrumentType)
        if (!purchaseInstrumentMapping) {
            return
        }
        for (int i = 0; i < lstRmsTask.size(); i++) {
            RmsTask rmsTask = lstRmsTask[i]
            String commissionScript = purchaseInstrumentMapping.commissionScript
            RmsCommissionDetails commissionDetails = new RmsCommissionDetails()
            Binding binding = new Binding()
            binding.setVariable(P_AMOUNT, rmsTask.amount)
            binding.setVariable(COMMISSION_DETAILS, commissionDetails)
            GroovyShell shell = new GroovyShell(binding)
            Object result = shell.evaluate(commissionScript)
            if (result instanceof RmsCommissionDetails) {
                RmsCommissionDetails commissionDetailsObj = (RmsCommissionDetails) result
                commissionDetailsObj.totalCharge = commissionDetailsObj.totalCharge.round()
                if (!checkCommissionDetailsValidity(commissionDetailsObj)) {
                    throw new RuntimeException()
                }
                RmsCommissionDetails savedCommissionDetails = rmsCommissionDetailsService.create(commissionDetailsObj)
                rmsTask.commissionDetailsId = savedCommissionDetails.id
                rmsTaskService.updateTaskCommissionDetailsId(rmsTask.id, rmsTask.commissionDetailsId)
            } else {
                throw new RuntimeException()
            }
        }
    }

    // following method will check if the objects holds consistent values
    private boolean checkCommissionDetailsValidity(RmsCommissionDetails commissionDetails) {
        float totalCharge = (
                commissionDetails.comm +
                        commissionDetails.pNt +
                        commissionDetails.postage +
                        commissionDetails.serviceCharge +
                        commissionDetails.vat +
                        commissionDetails.vatOnPnt
        )
        if (totalCharge < 0) return false
        return (totalCharge.round() == commissionDetails.totalCharge)   // commissionDetails.totalCharge already rounded
    }
}
