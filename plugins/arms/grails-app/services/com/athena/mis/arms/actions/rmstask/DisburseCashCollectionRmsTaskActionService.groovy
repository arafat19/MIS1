package com.athena.mis.arms.actions.rmstask

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.BankBranch
import com.athena.mis.application.entity.Sms
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.BankBranchCacheUtility
import com.athena.mis.application.utility.SmsCacheUtility
import com.athena.mis.arms.entity.RmsTask
import com.athena.mis.arms.service.RmsTaskListService
import com.athena.mis.arms.service.RmsTaskService
import com.athena.mis.arms.service.RmsTaskTraceService
import com.athena.mis.arms.utility.RmsInstrumentTypeCacheUtility
import com.athena.mis.arms.utility.RmsPaymentMethodCacheUtility
import com.athena.mis.arms.utility.RmsProcessTypeCacheUtility
import com.athena.mis.arms.utility.RmsSessionUtil
import com.athena.mis.arms.utility.RmsTaskStatusCacheUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 * disburse cash collection rms task
 * for details go through use-case named "DisburseCashCollectionRmsTaskActionService"
 */
class DisburseCashCollectionRmsTaskActionService extends BaseService implements ActionIntf{

    RmsTaskService rmsTaskService
    RmsTaskListService rmsTaskListService
    RmsTaskTraceService rmsTaskTraceService
    @Autowired
    BankBranchCacheUtility bankBranchCacheUtility
    @Autowired
    RmsSessionUtil rmsSessionUtil
    @Autowired
    RmsTaskStatusCacheUtility rmsTaskStatusCacheUtility
    @Autowired
    RmsProcessTypeCacheUtility rmsProcessTypeCacheUtility
    @Autowired
    RmsPaymentMethodCacheUtility rmsPaymentMethodCacheUtility
    @Autowired
    RmsInstrumentTypeCacheUtility rmsInstrumentTypeCacheUtility
    @Autowired
    SmsCacheUtility smsCacheUtility

    private Logger log = Logger.getLogger(getClass())

    private static final String NOT_FOUND = "Task Not Found"
    private static final String DISBURSED_SUCCESS = "Task successfully disbursed."
    private static final String SAVE_FAILED = "Failed to disburse task"
    private static final String NOT_AUTHORIZED = "Your branch is not authorized to disburse this task"
    private static final String DISBURSED_FAILED = "Only approved task can be disbursed"
    private static final String PROCESS_INS_ERROR = "Only forward cash collection task can be disbursed"
    private static final String SMS_NOT_SENT = "Error: Sms to sender is not sent"
    private static final String SMS_TEMPLATE_NOT_FOUND = "Error: SMS to sender is not activated"
    private static final String PHONE_INVALID = "Error: Sender phone not found or invalid"
    private static final String TRANSACTION_CODE = "DisburseRmsTaskActionService"
    private static final String AMOUNT_IN_LOCAL_CURRENCY = "amountInLocalCurrency"
    private static final String STR_REF_OR_PIN_NO = "strRefOrPinNo"
    private static final String UTF_8 = "UTF-8"
    private static final String RECIPIENT = 'recipient'
    private static final String CONTENT = 'content'

    /*
     * check pre condition for disburse task (if status approved, forward-cash-collection)
     * @param parameters
     * @param obj
     * @return
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            if(!params.taskId) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long taskId = Tools.parseLongInput(params.taskId)
            RmsTask rmsTask = rmsTaskService.read(taskId)
            if(!rmsTask) {
                result.put(Tools.MESSAGE, NOT_FOUND)
                return result
            }
            long userBranchId = rmsSessionUtil.appSessionUtil.getUserBankBranchId()
            BankBranch bankBranch=(BankBranch)bankBranchCacheUtility.read(rmsTask.mappingBranchId)
            if(bankBranch.bankId != rmsTask.mappingBankId) { //mapping bank should be user bank
                result.put(Tools.MESSAGE, NOT_AUTHORIZED)
                return result
            }
            if(rmsTask.mappingBranchId != userBranchId && !bankBranch.isGlobal.booleanValue()) { //mapping bankBranch should be user bankBranch or Any branch
                result.put(Tools.MESSAGE, NOT_AUTHORIZED)
                return result
            }
            long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
            SystemEntity forwardObj = (SystemEntity) rmsProcessTypeCacheUtility.readByReservedAndCompany(RmsProcessTypeCacheUtility.FORWARD, companyId)
            SystemEntity cashCollectionObj = (SystemEntity) rmsInstrumentTypeCacheUtility.readByReservedAndCompany(RmsInstrumentTypeCacheUtility.CASH_COLLECTION, companyId)
            SystemEntity statusApproved = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(RmsTaskStatusCacheUtility.DECISION_APPROVED, companyId)
            SystemEntity disbursedObj = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(RmsTaskStatusCacheUtility.DISBURSED, companyId)

            if(rmsTask.currentStatus != statusApproved.id) {
                result.put(Tools.MESSAGE, DISBURSED_FAILED)
                return result
            }
            if ((rmsTask.processTypeId != forwardObj.id) || rmsTask.instrumentTypeId != cashCollectionObj.id) {
                result.put(Tools.MESSAGE, PROCESS_INS_ERROR)
                return result
            }
            rmsTask = buildRmsTaskForDisburse(rmsTask, params, disbursedObj.id, userBranchId)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.ENTITY, rmsTask)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SAVE_FAILED)
            return result
        }
    }

    /**
     * update task status id DB and save task trace
     * @param parameters
     * @param obj
     * @return
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map preResult = (Map) obj
            RmsTask rmsTask = (RmsTask) preResult.get(Tools.ENTITY)
            rmsTaskService.updateRmsTaskForDisburse(rmsTask)
            rmsTaskTraceService.create(rmsTask)
            result.put(Tools.ENTITY, rmsTask)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(SAVE_FAILED)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SAVE_FAILED)
            return result
        }
    }

    /**
     * do nothing
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    /**
     * build failure result
     */
    public Object buildFailureResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map preResult = (Map) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            String msg = preResult.get(Tools.MESSAGE)
            if (msg) {
                result.put(Tools.MESSAGE, msg)
            } else {
                result.put(Tools.MESSAGE, SAVE_FAILED)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SAVE_FAILED)
            return result
        }
    }

    /**
     * Send sms and build success result
     */
    public Object executePostCondition(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        String msg = DISBURSED_SUCCESS
        result.put(Tools.IS_ERROR, Boolean.FALSE)
        try{
            Map executeResult = (Map) obj
            RmsTask task = (RmsTask) executeResult.get(Tools.ENTITY)
            msg += sendSms(task)
            result.put(Tools.MESSAGE, msg)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            msg += (Tools.SINGLE_SPACE + SMS_NOT_SENT)
            result.put(Tools.MESSAGE, msg)
            return result
        }
    }

    private RmsTask buildRmsTaskForDisburse(RmsTask oldRmsTask, GrailsParameterMap params, long disbursedStatus,long userBranchId) {
        BankBranch userBranch = (BankBranch) bankBranchCacheUtility.read(userBranchId)
        oldRmsTask.previousStatus = oldRmsTask.currentStatus
        oldRmsTask.currentStatus = disbursedStatus
        oldRmsTask.identityType = params.idType
        oldRmsTask.identityNo = params.idNo
        // set mapping branch to own(in case Any Branch)
        oldRmsTask.mappingBranchId = userBranch.id
        oldRmsTask.mappingDistrictId= userBranch.districtId
        return oldRmsTask
    }

    /**
     * build and send sms to remittance sender
     */
    private String sendSms(RmsTask task) {
        long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity bankDeposit = (SystemEntity) rmsPaymentMethodCacheUtility.readByReservedAndCompany(RmsPaymentMethodCacheUtility.BANK_DEPOSIT_ID, companyId)
        SystemEntity cashCollection = (SystemEntity) rmsPaymentMethodCacheUtility.readByReservedAndCompany(RmsPaymentMethodCacheUtility.CASH_COLLECTION_ID, companyId)
        List<Sms> lstSms = smsCacheUtility.listByTransactionCodeAndCompanyIdAndIsActive(TRANSACTION_CODE, companyId, Boolean.TRUE)
        if (lstSms.size() == 0) {
            return (Tools.SINGLE_SPACE + SMS_TEMPLATE_NOT_FOUND)
        }
        String phone = task.senderMobile
        if(!phone || phone.equals(Tools.EMPTY_SPACE)) {
            return (Tools.SINGLE_SPACE + PHONE_INVALID)
        }
        Sms sms = lstSms[0]
        double amountInLocalCurrency = Math.floor(task.amount)
        String refOrPinNo = Tools.EMPTY_SPACE
        if (task.paymentMethod == bankDeposit.id) {
            refOrPinNo = "Ref No: " + task.refNo
        } else if(task.paymentMethod == cashCollection.id) {
            if(task.pinNo && !task.pinNo.equals(Tools.EMPTY_SPACE)) {
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
        String encodedContent= URLEncoder.encode(content,UTF_8);

        binding = new Binding()
        binding.setVariable(RECIPIENT, phone)
        binding.setVariable(CONTENT, encodedContent)
        shell = new GroovyShell(binding)
        String strSms = shell.evaluate(sms.url)
        strSms.toURL().text

        return Tools.EMPTY_SPACE
    }
}
