package com.athena.mis.arms.actions.rmstask

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.BankBranch
import com.athena.mis.application.entity.Sms
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.BankBranchCacheUtility
import com.athena.mis.application.utility.BankCacheUtility
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.application.utility.SmsCacheUtility
import com.athena.mis.arms.entity.RmsTask
import com.athena.mis.arms.service.RmsTaskListService
import com.athena.mis.arms.service.RmsTaskService
import com.athena.mis.arms.service.RmsTaskTraceService
import com.athena.mis.arms.utility.RmsPaymentMethodCacheUtility
import com.athena.mis.arms.utility.RmsProcessTypeCacheUtility
import com.athena.mis.arms.utility.RmsSessionUtil
import com.athena.mis.arms.utility.RmsTaskStatusCacheUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class DisburseRmsTaskActionService extends BaseService implements ActionIntf {

    RmsTaskService rmsTaskService
    RmsTaskListService rmsTaskListService
    RmsTaskTraceService rmsTaskTraceService
    @Autowired
    RmsTaskStatusCacheUtility rmsTaskStatusCacheUtility
    @Autowired
    RmsSessionUtil rmsSessionUtil
    @Autowired
    RmsProcessTypeCacheUtility rmsProcessTypeCacheUtility
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility
    @Autowired
    BankCacheUtility bankCacheUtility
    @Autowired
    BankBranchCacheUtility bankBranchCacheUtility
    @Autowired
    RmsPaymentMethodCacheUtility rmsPaymentMethodCacheUtility
    @Autowired
    SmsCacheUtility smsCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String CAN_NOT_DISBURSED = "Only approved task can be disbursed"
    private static final String APP_USER_CAN_NOT_DISBURSE_THIS_TASK = "cannot disburse this task"
    private static final String REFRESH_PAGE = "Task status mismatched, refresh the grid and try again"
    private static final String LST_TASK_IDS = "lstTaskIds"
    private static final String LST_RMS_TASK = "lstRmsTask"
    private static final String CURRENT_STATUS = "currentStatus"
    private static final String DISBURSE_TASK_FAILURE_MESSAGE = "Task is not disbursed"
    private static final String TASK_HAS_BEEN_DISBURSED = "Task has been disbursed successfully."
    private static final String TASK_IDS = "taskIds"
    private static final String SMS_NOT_SENT = "Error: Failed to send sms"
    private static final String TRANSACTION_CODE = "DisburseRmsTaskActionService"
    private static final String AMOUNT_IN_LOCAL_CURRENCY = "amountInLocalCurrency"
    private static final String STR_REF_OR_PIN_NO = "strRefOrPinNo"
    private static final String UTF_8 = "UTF-8"
    private static final String RECIPIENT = 'recipient'
    private static final String CONTENT = 'content'

    /**
     * 1. Get parameters from UI
     * 2. Get list of task id
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */

    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            List<Long> lstTaskIds = Tools.getIdsFromParams(params, TASK_IDS)
            long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
            long currentStatus = Long.parseLong(params.currentStatus)
            List<RmsTask> lstRmsTask = rmsTaskService.findAllByCurrentStatusAndIdInList(currentStatus, lstTaskIds)
            if (lstTaskIds.size() != lstRmsTask.size()) {
                result.put(Tools.MESSAGE, REFRESH_PAGE)
                return result
            }
            if (!params.processTypeId) {
                result.put(Tools.MESSAGE, DISBURSE_TASK_FAILURE_MESSAGE)
                return result
            }
            long processTypeId = Long.parseLong(params.processTypeId)
            boolean hasAccessForDisburse = checkAccessForDisburse(processTypeId, companyId, lstRmsTask)
            AppUser appUser = rmsSessionUtil.appSessionUtil.getAppUser()
            if (!hasAccessForDisburse) {
                result.put(Tools.MESSAGE, appUser.username + Tools.SINGLE_SPACE + APP_USER_CAN_NOT_DISBURSE_THIS_TASK)
                return result
            }
            SystemEntity decisionApproved = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(rmsTaskStatusCacheUtility.DECISION_APPROVED, companyId)
            if (currentStatus != decisionApproved.id) {
                result.put(Tools.MESSAGE, CAN_NOT_DISBURSED)
                return result
            }
            SystemEntity disbursed = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(rmsTaskStatusCacheUtility.DISBURSED, companyId)
            long newCurrentStatus = disbursed.id
            result.put(CURRENT_STATUS, newCurrentStatus)
            result.put(LST_TASK_IDS, lstTaskIds)
            result.put(LST_RMS_TASK, lstRmsTask)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DISBURSE_TASK_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Update  task status for disbursed
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            Map preResult = (Map) parameters
            List<Long> lstTaskIds = (List<Long>) preResult.get(LST_TASK_IDS)
            long newCurrentStatus = (long) preResult.get(CURRENT_STATUS)
            List<RmsTask> lstRmsTask = (List<RmsTask>) preResult.get(LST_RMS_TASK)
            rmsTaskService.updateRmsTaskStatus(lstTaskIds, newCurrentStatus, Boolean.FALSE, null)
            createRmsTaskTrace(lstRmsTask, newCurrentStatus)
            result.put(LST_RMS_TASK, lstRmsTask)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(DISBURSE_TASK_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DISBURSE_TASK_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Do nothing for buildSuccessResultForUI
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    /**
     * send sms & build success result
     */
    public Object executePostCondition(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        String msg = TASK_HAS_BEEN_DISBURSED
        result.put(Tools.IS_ERROR, Boolean.FALSE)
        try {
            Map executeResult = (Map) obj
            List<RmsTask> lstRmsTask = (List<RmsTask>) executeResult.get(LST_RMS_TASK)
            for (int i = 0; i < lstRmsTask.size(); i++) {
                sendSms(lstRmsTask[i])
            }
            result.put(Tools.MESSAGE, msg)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            msg += (Tools.SINGLE_SPACE + SMS_NOT_SENT)
            result.put(Tools.MESSAGE, msg)
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
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DISBURSE_TASK_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DISBURSE_TASK_FAILURE_MESSAGE)
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
    /**
     * Admin can disburse task of SEBL
     * Branch user can disburse only task of his branch
     * @param processTypeId -issue/forward/purchase
     * @param companyId -companyId
     * @param lstRmsTask -selected task list(s)
     * @return-true / f a l s e based on appUser has access to disburse task
     */
    private boolean checkAccessForDisburse(long processTypeId, long companyId, List<RmsTask> lstRmsTask) {
        boolean hasAccessForDisburse = Boolean.TRUE
        SystemEntity issueProcess = (SystemEntity) rmsProcessTypeCacheUtility.readByReservedAndCompany(rmsProcessTypeCacheUtility.ISSUE, companyId)
        SystemEntity forwardProcess = (SystemEntity) rmsProcessTypeCacheUtility.readByReservedAndCompany(rmsProcessTypeCacheUtility.FORWARD, companyId)
        SystemEntity purchaseProcess = (SystemEntity) rmsProcessTypeCacheUtility.readByReservedAndCompany(rmsProcessTypeCacheUtility.PURCHASE, companyId)
        boolean isRemittanceUser = rmsSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_ARMS_REMITTANCE_USER)
        if ((issueProcess.id == processTypeId) || (processTypeId == purchaseProcess.id)) {
            if (!isRemittanceUser) {
                hasAccessForDisburse = Boolean.FALSE
            }
        } else if (processTypeId == forwardProcess.id) {
            boolean hasBranchRole = rmsSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_ARMS_BRANCH_USER)
            long userBranchId = rmsSessionUtil.appSessionUtil.getUserBankBranchId()
            BankBranch bankBranch = (BankBranch) bankBranchCacheUtility.read(userBranchId)
            if (!hasBranchRole) {
                hasAccessForDisburse = Boolean.FALSE
            }
            for (int i = 0; i < lstRmsTask.size(); i++) {
                if (lstRmsTask[i].mappingBankId != bankBranch.bankId) {
                    hasAccessForDisburse = Boolean.FALSE
                }
                BankBranch branch = (BankBranch) bankBranchCacheUtility.read(lstRmsTask[i].mappingBranchId)
                if (lstRmsTask[i].mappingBranchId != userBranchId || !branch.isGlobal.booleanValue()) {
                    hasAccessForDisburse = Boolean.FALSE
                }
            }
        }
        return hasAccessForDisburse
    }

    private void sendSms(RmsTask task) {
        long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity bankDeposit = (SystemEntity) rmsPaymentMethodCacheUtility.readByReservedAndCompany(RmsPaymentMethodCacheUtility.BANK_DEPOSIT_ID, companyId)
        SystemEntity cashCollection = (SystemEntity) rmsPaymentMethodCacheUtility.readByReservedAndCompany(RmsPaymentMethodCacheUtility.CASH_COLLECTION_ID, companyId)
        List<Sms> lstSms = smsCacheUtility.listByTransactionCodeAndCompanyIdAndIsActive(TRANSACTION_CODE, companyId, Boolean.TRUE)
        if (lstSms.size() == 0) {
            return
        }
        String phone = task.senderMobile
        if (!phone || phone.equals(Tools.EMPTY_SPACE)) {
            return
        }
        Sms sms = lstSms[0]
        double amountInLocalCurrency = Math.floor(task.amount)
        String refOrPinNo = Tools.EMPTY_SPACE
        if (task.paymentMethod == bankDeposit.id) {
            refOrPinNo = "Ref No: " + task.refNo
        } else if (task.paymentMethod == cashCollection.id) {
            if (task.pinNo && !task.pinNo.equals(Tools.EMPTY_SPACE)) {
                refOrPinNo = "PIN No: " + task.pinNo
            } else {
                refOrPinNo = "Ref No: " + task.refNo
            }
        }
        Binding binding = new Binding()
        binding.setVariable(STR_REF_OR_PIN_NO, refOrPinNo)
        binding.setVariable(AMOUNT_IN_LOCAL_CURRENCY, amountInLocalCurrency)
        GroovyShell shell = new GroovyShell(binding)
        String content = shell.evaluate(sms.body)
        String encodedContent = URLEncoder.encode(content, UTF_8);

        binding = new Binding()
        binding.setVariable(RECIPIENT, phone)
        binding.setVariable(CONTENT, encodedContent)
        shell = new GroovyShell(binding)
        String strSms = shell.evaluate(sms.url)
        strSms.toURL().text
    }
}
