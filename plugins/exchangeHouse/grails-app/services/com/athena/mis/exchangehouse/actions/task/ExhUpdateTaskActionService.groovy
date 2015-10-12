package com.athena.mis.exchangehouse.actions.task

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.*
import com.athena.mis.application.service.AppMailService
import com.athena.mis.application.service.EntityContentService
import com.athena.mis.application.utility.*
import com.athena.mis.exchangehouse.config.ExhSysConfigurationCacheUtility
import com.athena.mis.exchangehouse.entity.*
import com.athena.mis.exchangehouse.service.ExhBeneficiaryService
import com.athena.mis.exchangehouse.service.ExhCustomerService
import com.athena.mis.exchangehouse.service.ExhTaskService
import com.athena.mis.exchangehouse.service.ExhTaskTraceService
import com.athena.mis.exchangehouse.utility.*
import com.athena.mis.integration.sarb.SarbPluginConnector
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.plugin.mail.MailService
import grails.util.Environment
import groovy.sql.GroovyRowResult
import groovy.text.SimpleTemplateEngine
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

import javax.activation.DataHandler
import javax.activation.DataSource
import javax.mail.Message
import javax.mail.Multipart
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart
import java.util.concurrent.ExecutorService

/**
 *  Update task object and show in grid
 *  For details go through Use-Case doc named 'ExhUpdateTaskActionService'
 */
class ExhUpdateTaskActionService extends BaseService implements ActionIntf {
    private final Logger log = Logger.getLogger(getClass())
    // constants
    private static final String TASK_UPDATE_FAILURE_MESSAGE = "Task could not be updated"
    private static final String TASK_UPDATE_SUCCESS_MESSAGE = "Task has been updated successfully"
    private static final String LST_ERRORS = "lstErrors"
    private static final String IS_CONFIRMATION_ISSUES = "isConfirmationIssues"
    private static final String TOTAL_AMOUNT = "totalAmount"
    private static final String TASK_OBJ = "task"
    private static final String CUSTOMER_OBJ = "customer"
    private static final String BENEFICIARY_OBJ = "beneficiary"
    private static final String CUSTOMER_PHOTO_ID_EXPIRED = 'Customer photo ID expired'
    private static final String CUSTOMER_PROOF_OF_ADDRESS_REQUIRED = 'Customer proof of address required'
    private static final String CUSTOMER_PHOTO_ID_REQUIRED = 'Customer photo ID required'
    private static final String CUSTOMER_SOURCE_OF_FUND_IS_REQUIRED = 'Customer source of fund is required'
    private static final String ERROR_AGENT_TASK_CAN_NOT_BE_EDITED = "You cant not edit Agent's Task"
    private static final String ERROR_TASK_NOT_FOUND = "Task not found"
    private static final String TASK_STATUS_MISMATCH = "Task could not be updated due to status mismatch"
    private static final String FAIL_TO_UPDATE_TASK = 'Failed to update Task'
    private static final String FAIL_UPDATE_BENEFICIARY = "Failed to update Beneficiary"
    private static
    final String TOTAL_AMOUNT_EXCEEDS_DECLARATION_AMOUNT = "Total amount exceeds the customer's declaration amount"
    private static final String CUSTOMER_VISA_EXPIRED = 'Customer VISA expired'
    private static final String CUSTOMER_DECLARATION_END_DATE_EXPIRED = 'Customer declaration end date expired'
    private static final String CUSTOMER_DECLARATION_DATE_NOT_EXIST = 'Customer declaration date does not exist'
    private static final String AMOUNT = 'amount'
    private static final String VALUE_1 = "1"
    private static final String LST_POST_MESSAGE = 'lstPostMessage'
    private static final String ERROR_IN_MAIL_SMS = "Error sending mail/sms"
    private static final String BUT_FOLLOWING_ERROR_OCCURRED = " But following error occurred:"
    private static final String TRANSACTION_CODE = "ExhCreateTaskActionService"
    private static final String MAIL_TEMPLATE_NOT_FOUND = "Mail to customer is not activated"
    private static final String EMAIL_PATTERN = """^[-0-9a-zA-Z.+_]+@[-0-9a-zA-Z.+_]+\\.[a-zA-Z]{2,4}\$"""
    private static final String EMAIL_INVALID = "Customer email not found or invalid"
    private static final String AMOUNT_MODIFIED = "Customer payable amount can't be modified"
    private static final String PAID_BY_CHANGED = "Paid By can't be changed of paid PayPoint task"
    private static final String CUSTOMER_IS_BLOCKED = "Customer is blocked"

// constants for download report
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_FOLDER = 'task'
    private static final String SUB_REPORT_FOLDER = 'subreports'
    private static final String JASPER_FILE_CASHIER = 'TaskInvoice.jasper'
    private static final String JASPER_FILE_FOR_AGENT = 'TaskInvoiceForAgent.jasper'
    private static final String REPORT_TITLE = 'Task Invoice'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String SUB_REPORT_DIR = 'SUBREPORT_DIR'
    private static final String TASK_ID = "taskId"
    private static final String SESSION_USER_NAME = "sessionUserName"
    private static final String REMITTANCE_PURPOSE = "remittancePurpose"
    private static final String PAYMENT_METHOD_NAME = "paymentMethodName"
    private static final String PAYMENT_METHOD_ID = "paymentMethodId"
    private static final String PAID_BY = "paidBy"
    private static final String ANY_BRANCH = 'Any Branch'
    private static final String COLLECTION_POINT = 'collectionPoint'
    private static final String EXCHANGE_HOUSE_NAME = 'exchangeHouseName'
    private static final String COMPANY_LOGO = "companyLogo"
    private static final String PAY_METHOD_BANK_DEPOSIT_ID = 'payMethodBankDepositId'
    private static final String PAY_METHOD_CASH_COLLECTION_ID = 'payMethodCashCollectionId'
    private static final String AGENT_NAME = 'agentName'
    private static final String AGENT_PHONE = 'agentPhone'
    private static final String AGENT_ADDRESS = 'agentAddress'
    private static final String LOCAL_CURRENCY_NAME = 'localCurrencyName'
    private static final String LABEL_NO_RED = "<span style='color:red;'>NO</span>"
    private static final String TASK_AMOUNT_EXCEEDS_LIMIT = "Task amount exceeds limit. Max limit: "
    private static final String TASK_AMOUNT_LIMIT_CONFIG_NOT_FOUND = "Config for 'Max amount limit' not found"
    private static final String MONTHLY_AMOUNT_EXCEEDS = "Monthly amount limit exceeds for this customer"
    private static final String TASK_MONTHLY_AMOUNT_LIMIT_CONFIG_NOT_FOUND = "Config for 'Monthly amount limit per customer' not found"


    ExhTaskService exhTaskService
    ExhBeneficiaryService exhBeneficiaryService
    ExhCustomerService exhCustomerService
    ExhTaskTraceService exhTaskTraceService
    JasperService jasperService
    GrailsApplication grailsApplication
    MailService mailService
    AppMailService appMailService
    ExecutorService executorService
    EntityContentService entityContentService
    @Autowired
    ExhPaymentMethodCacheUtility exhPaymentMethodCacheUtility
    @Autowired
    ExhPaidByCacheUtility exhPaidByCacheUtility
    @Autowired
    BankCacheUtility bankCacheUtility
    @Autowired
    ExhTaskStatusCacheUtility exhTaskStatusCacheUtility
    @Autowired
    ExhTaskTypeCacheUtility exhTaskTypeCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    ExhAgentCacheUtility exhAgentCacheUtility
    @Autowired
    ExhRegularFeeCacheUtility exhRegularFeeCacheUtility
    @Autowired
    ExhSysConfigurationCacheUtility exhSysConfigurationCacheUtility
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    ExhRemittancePurposeCacheUtility exhRemittancePurposeCacheUtility
    @Autowired
    CompanyCacheUtility companyCacheUtility
    @Autowired
    BankBranchCacheUtility bankBranchCacheUtility
    @Autowired
    DistrictCacheUtility districtCacheUtility
    @Autowired
    CurrencyCacheUtility currencyCacheUtility
    @Autowired
    ContentEntityTypeCacheUtility contentEntityTypeCacheUtility
    @Autowired
    ContentCategoryCacheUtility contentCategoryCacheUtility
    @Autowired
    CountryCacheUtility countryCacheUtility
    @Autowired
    SmsCacheUtility smsCacheUtility
    @Autowired
    ExhCurrencyConversionCacheUtility exhCurrencyConversionCacheUtility
    @Autowired
    ExhPhotoIdTypeCacheUtility exhPhotoIdTypeCacheUtility
    @Autowired(required = false)
    SarbPluginConnector sarbImplService

    /**
     * Get parameters from UI and check pre condition
     * 1. check task status for edit the task
     * 1. check customer's visa, photo or declaration end date expiration
     * 2. check customer's photo ID or declaration amount against total amount of sending task
     * 3. check customer's proof of address or source of fund against certain amount of sending one task which will take confirmation from User
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)         // set default value

            GrailsParameterMap params = (GrailsParameterMap) parameters

            long id = Long.parseLong(params.id)
            ExhTask existingTask = exhTaskService.read(id)          // get task object form DB

            if (!existingTask) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            // preserve existing object in 'oldTask' before change/modification
            // required to check old vs new values
            ExhTask oldTask = new ExhTask()
            oldTask.properties = existingTask.properties

            String errorMsg = checkTaskValidity(params, existingTask)    // check task status for edit the task
            if (errorMsg) {
                result.put(Tools.MESSAGE, errorMsg)
                return result
            }

            errorMsg=checkBlockedCustomer(existingTask)                 //check for block customer
            if(errorMsg){
                result.put(Tools.MESSAGE,errorMsg)
                return result
            }
            double existingAmountInLocal = existingTask.amountInLocalCurrency
            // storing the value before mutating existing task
            ExhTask task = (ExhTask) buildTaskObject(params, existingTask)         // build task object

            boolean isConfirmed = Boolean.parseBoolean(params.isConfirmed)
            if (isConfirmed) {
                result.put(Tools.IS_ERROR, Boolean.FALSE)
                result.put(TASK_OBJ, task)
                return result
            }

            ExhCustomer customer = exhCustomerService.read(task.customerId)       // get customer object from DB
            errorMsg = checkExpirationDates(customer)
            if (errorMsg) {
                result.put(Tools.MESSAGE, errorMsg)
                return result
            }

            double totalAmount = getTotalAmountForCustomer(customer.id)// get amount total of ALL previous
            totalAmount = (totalAmount + task.amountInLocalCurrency - existingAmountInLocal).round(2)
            // get amount total of ALL task excluding this task previous amount

            errorMsg = checkAmountLimitForPhotoId(customer, totalAmount)
            if (errorMsg) {
                result.put(Tools.MESSAGE, errorMsg)
                return result
            }
            errorMsg = checkDeclarationAmountAndDate(customer, task, existingAmountInLocal)
            if (errorMsg) {
                result.put(Tools.MESSAGE, errorMsg)
                return result
            }

            errorMsg = checkPerTransactionAmountLimit(task)
            if (errorMsg) {
                result.put(Tools.MESSAGE, errorMsg)
                return result
            }

            errorMsg = checkCustomerMonthlyTranLimit(task.customerId, (task.amountInLocalCurrency - existingAmountInLocal))
            if (errorMsg) {
                result.put(Tools.MESSAGE, errorMsg)
                return result
            }

            errorMsg = checkAmountAndPaidByOfPayPointTask(task, oldTask)
            if (errorMsg) {
                result.put(Tools.MESSAGE, errorMsg)
                return result
            }


            if (sarbImplService) {
                ExhRemittancePurpose remittancePurpose = (ExhRemittancePurpose) exhRemittancePurposeCacheUtility.read(task.remittancePurpose)
                ExhPhotoIdType photoIdType = (ExhPhotoIdType) exhPhotoIdTypeCacheUtility.read(customer.photoIdTypeId)
                errorMsg = sarbImplService.validateTaskDetails(remittancePurpose.code, photoIdType.code)
                if (errorMsg) {
                    result.put(Tools.MESSAGE, errorMsg)
                    return result
                }
            }

            List<String> lstErrors = checkCustomer(customer, totalAmount)
            // check low-priority validation which will take confirmation from User
            if (lstErrors.size() > 0) {
                result.put(LST_ERRORS, lstErrors)
                result.put(TOTAL_AMOUNT, totalAmount)
                return result
            }

            result.put(TASK_OBJ, task)
            result.put(CUSTOMER_OBJ, customer)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result

        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, TASK_UPDATE_FAILURE_MESSAGE)
            return result
        }

    }
    /**
     * Update task and task trace object in DB accordingly*
     * Update beneficiary -if changes payment method & beneficiary information null
     * This method is in transactional boundary and will roll back in case of any exception
     * @param params -serialized parameters from UI
     * @param obj -map returned from executePreCondition method
     * @return - the same map from preCondition with newly pulled beneficiary
     */

    @Transactional
    public Object execute(Object params, Object obj) {
        try {
            Map preResult = (Map) obj          // cast map returned from executePreCondition method
            ExhTask task = (ExhTask) preResult.get(TASK_OBJ)
            GrailsParameterMap parameters = (GrailsParameterMap) params
            ExhBeneficiary beneficiary = exhBeneficiaryService.read(task.beneficiaryId)
            checkAndUpdateBeneficiary(parameters, task, beneficiary)
            Integer updateCount = exhTaskService.update(task)
            boolean saveTrace = exhTaskTraceService.create(task, new Date(), Tools.ACTION_UPDATE)
            preResult.put(BENEFICIARY_OBJ, beneficiary)   // put beneficiary for mail/sms
            return preResult        // return the same map (of task , customer & beneficiary)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            //@todo:rollback
            throw new RuntimeException(FAIL_TO_UPDATE_TASK)
            Map result = new LinkedHashMap()
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAIL_TO_UPDATE_TASK)
            return result
        }
    }

    /**
     * Send mail & sms if login user as Cashier otherwise show message
     * @param params -N/A
     * @param obj -map returned from execute method containing task, customer & beneficiary object
     * @return - send task for success result & necessary for buildSuccessResultForUI
     */
    @Transactional(readOnly = true)
    public Object executePostCondition(Object params, Object obj) {
        Map result = new LinkedHashMap()
        List<String> lstPostMessage = []
        try {
            Map preResult = (Map) obj    // cast map returned from executePreCondition method
            ExhTask task = (ExhTask) preResult.get(TASK_OBJ)
            //@todo: remove customer/agent login check when seperate usecase implemented
            if (exhSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_TYPE_EXH_CUSTOMER) ||
                    exhSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_TYPE_EXH_AGENT)) {
                result.put(LST_POST_MESSAGE, lstPostMessage)
                result.put(TASK_OBJ, task)
                return result
            }
            ExhCustomer customer = (ExhCustomer) preResult.get(CUSTOMER_OBJ)
            ExhBeneficiary beneficiary = (ExhBeneficiary) preResult.get(BENEFICIARY_OBJ)
            if (!customer) {
                customer = exhCustomerService.read(task.customerId)
            }

            // send mail
            String msgMail = sendMail(task, customer)
            if (msgMail) lstPostMessage << msgMail
            // send sms
            String msgSms = sendSms(task, customer, beneficiary)
            if (msgSms) lstPostMessage << msgSms
            result.put(LST_POST_MESSAGE, lstPostMessage)
            result.put(TASK_OBJ, task)     // send task for success result
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            lstPostMessage << ERROR_IN_MAIL_SMS
            result.put(LST_POST_MESSAGE, lstPostMessage)
            return result
        }

    }


    public Object buildSuccessResultForUI(Object obj) {
        Map result
        try {
            Map preResult = (Map) obj
            ExhTask task = (ExhTask) preResult.get(TASK_OBJ)
            List<String> lstPostMessage = (List<String>) preResult.get(LST_POST_MESSAGE)
            GridEntity object = new GridEntity()
            object.id = task.id
            String payMethod = exhPaymentMethodCacheUtility.read(task.paymentMethod).key
            Double amount_gbp = task.amountInLocalCurrency
            Double temp_total_due = amount_gbp + task.regularFee - task.discount
            double total_due = temp_total_due.round(2)
            String gatewayPayment = task.isGatewayPaymentDone ? Tools.YES : LABEL_NO_RED
            object.cell = [Tools.LABEL_NEW, task.id, task.refNo,
                    task.amountInForeignCurrency, amount_gbp, total_due,
                    task.customerName, task.beneficiaryName, payMethod, task.regularFee, task.discount, gatewayPayment]

            // task-grid(of customer) in cashier login in different
            long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
            SystemEntity customerTaskObj = (SystemEntity) exhTaskTypeCacheUtility.readByReservedAndCompany(exhTaskTypeCacheUtility.TYPE_CUSTOMER_TASK, companyId)
            SystemEntity exhPendingTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_PENDING_TASK, companyId)

            if (task.taskTypeId == customerTaskObj.id
                    && task.currentStatus != exhPendingTaskSysEntityObject.id) {
                String status = exhTaskStatusCacheUtility.read(task.currentStatus).key
                object.cell = [Tools.LABEL_NEW, task.id, task.refNo, task.currentStatus,
                        task.amountInForeignCurrency, amount_gbp, total_due,
                        task.customerName, task.beneficiaryName, payMethod, status]
            }

            String message = TASK_UPDATE_SUCCESS_MESSAGE
            if (lstPostMessage.size() > 0) {
                message = message + BUT_FOLLOWING_ERROR_OCCURRED + Tools.NEW_LINE
                lstPostMessage.each {
                    message = message + it + Tools.NEW_LINE
                }
            }

            result = [isError: false, entity: object, version: task.version, message: message]
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result = [isError: true, entity: null, version: 0, message: TASK_UPDATE_FAILURE_MESSAGE]
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
            result.put(Tools.MESSAGE, TASK_UPDATE_FAILURE_MESSAGE)
            result.put(IS_CONFIRMATION_ISSUES, Boolean.FALSE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                result.put(IS_CONFIRMATION_ISSUES, Boolean.TRUE)

                result.put(LST_ERRORS, preResult.get(LST_ERRORS))
                result.put(TOTAL_AMOUNT, preResult.get(TOTAL_AMOUNT))

                return result
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, TASK_UPDATE_FAILURE_MESSAGE)
            result.put(IS_CONFIRMATION_ISSUES, Boolean.FALSE)
            return result
        }
    }

    // Return the task amount(local) total of a particular customer within Declaration duration
    private double getTotalAmountWithinDeclaration(ExhCustomer customer) {
        Date dtStart = DateUtility.setFirstHour(customer.declarationStart)
        Date dtEnd = DateUtility.setLastHour(customer.declarationEnd)

        String query = """
            SELECT
                sum(task.amount_in_local_currency) as total_amount_local
            FROM
                 exh_task task
            WHERE
                task.customer_id = ${customer.id}
            AND task.created_on BETWEEN '${DateUtility.getDBDateFormatWithSecond(dtStart)}' AND '${
            DateUtility.getDBDateFormatWithSecond(dtEnd)
        }'
        """
        List<GroovyRowResult> results = executeSelectSql(query)

        Double total = results[0].total_amount_local ? results[0].total_amount_local : 0
        return total.doubleValue()
    }

    private String checkTaskValidity(GrailsParameterMap params, ExhTask existingTask) {

        long version = Long.parseLong(params.version)
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity exhPendingTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_PENDING_TASK, companyId)
        SystemEntity exhNewTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_NEW_TASK, companyId)
        SystemEntity exhStatusUnApprovedSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_UN_APPROVED, companyId)

        if ((!existingTask) || (existingTask.version != version)) {
            return ERROR_TASK_NOT_FOUND
        }

        if ((existingTask.currentStatus != exhPendingTaskSysEntityObject.id)
                && (existingTask.currentStatus != exhStatusUnApprovedSysEntityObject.id)
                && (existingTask.currentStatus != exhNewTaskSysEntityObject.id)) {
            return TASK_STATUS_MISMATCH
        }

        boolean hasAgentRole = exhSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_TYPE_EXH_AGENT)
        if (!hasAgentRole && (existingTask.agentId > 0)) {
            return ERROR_AGENT_TASK_CAN_NOT_BE_EDITED
        }
        return null
    }

    /**
     * check customer's proof of address or source of fund against certain amount of sending one task which will take confirmation from User
     * @param customer -ExhCustomer object
     * @param totalAmount -total task amount(Local) of a particular customer
     * @return errorList -a message
     */
    private List<String> checkCustomer(ExhCustomer customer, double totalAmount) {

        List<String> errorList = []
        if (totalAmount >= 800 &&
                customer.addressVerifiedStatus == Tools.CUSTOMER_ADDRESS_NOT_VERIFIED) {
            errorList << CUSTOMER_PROOF_OF_ADDRESS_REQUIRED
        }

        if (totalAmount > 8000 &&
                ((!customer.sourceOfFund) || (customer.sourceOfFund.trim().length() == 0))) {
            errorList << CUSTOMER_SOURCE_OF_FUND_IS_REQUIRED
        }

        return errorList
    }

    /**
     * check customer's visa, photoId date expiration
     * @param customer - a ExhCustomer object
     * @return - message or null
     */
    private String checkExpirationDates(ExhCustomer customer) {

        Date today = new Date()
        Date visaExpireDate = DateUtility.setLastHour(customer.visaExpireDate)
        if (visaExpireDate && today > visaExpireDate) {  // If VISA is expired, full restriction
            return CUSTOMER_VISA_EXPIRED
        }

        Date photoIdExpiryDate = DateUtility.setLastHour(customer.photoIdExpiryDate)
        if (photoIdExpiryDate && today > photoIdExpiryDate) {
            // If customer has provided photo id expiration date and photo id expired, full restriction
            return CUSTOMER_PHOTO_ID_EXPIRED
        }
        return null
    }

    /**
     * Check customer's photo ID against total amount of sending task
     * @param customer - a ExhCustomer object
     * @param totalAmount - total task amount(Local) of a particular customer
     * @return -message or null
     */
    private String checkAmountLimitForPhotoId(ExhCustomer customer, double totalAmount) {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        double limitForPhotoId = 0.0d
        SysConfiguration sysConfiguration = exhSysConfigurationCacheUtility.readByKeyAndCompanyId(exhSysConfigurationCacheUtility.EXH_AMOUNT_LIMIT_FOR_MANDATORY_PHOTOID, companyId)

        if (!sysConfiguration) {
            return null
        }
        if (sysConfiguration) {
            try {
                limitForPhotoId = Double.parseDouble(sysConfiguration.value)
            } catch (Exception e) {
                limitForPhotoId = 0.0d
            }
        }
        boolean hasPhotoId = (customer.photoIdTypeId > 0)
        if (totalAmount > limitForPhotoId && !hasPhotoId) {
            // full restriction, if amount > 750 but customer's does not have photo id
            return CUSTOMER_PHOTO_ID_REQUIRED
        }
        return null
    }

    /**
     * Check customer's declaration amount declaration end against total amount of sending task
     * @param customer - a ExhCustomer object
     * @param task -ExhTask object
     * @param existingAmountInLocal - previous total task amount(Local) of a particular customer
     * @return -message or null
     */
    private String checkDeclarationAmountAndDate(ExhCustomer customer, ExhTask task, double existingAmountInLocal) {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        SysConfiguration sysConfigCheckDeclaration = exhSysConfigurationCacheUtility.readByKeyAndCompanyId(exhSysConfigurationCacheUtility.EXH_CUSTOMER_DECLARATION_AMOUNT, companyId)
        if (!sysConfigCheckDeclaration) {
            return null
        }
        boolean isDeclarationEnabled = false
        try {
            int val = Integer.parseInt(sysConfigCheckDeclaration.value)
            isDeclarationEnabled = (val > 0) ? true : false
        } catch (Exception e) {
            isDeclarationEnabled = false
        }
        if (!isDeclarationEnabled) {
            return null
        }
        // check if both declaration date exists
        if(!customer.declarationStart || !customer.declarationEnd) {
            return CUSTOMER_DECLARATION_DATE_NOT_EXIST
        }
        Date declarationEnd = DateUtility.setLastHour(customer.declarationEnd)
        if (new Date() > declarationEnd) {    // If Declaration End date is expired, full restriction
            return CUSTOMER_DECLARATION_END_DATE_EXPIRED
        }

        double amountWithinDeclaration = getTotalAmountWithinDeclaration(customer)
        // get declaration task amount within declaration date
        amountWithinDeclaration = (amountWithinDeclaration - existingAmountInLocal + task.amountInLocalCurrency).round(2)
        if (amountWithinDeclaration > customer.declarationAmount) {
            // full restriction, if totalAmount > declarationAmount whatever the amount
            return TOTAL_AMOUNT_EXCEEDS_DECLARATION_AMOUNT
        }
        return null
    }

    /**
     * Check amount limit per transaction
     * @param task -ExhTask object
     * @return -message or null
     */
    private String checkPerTransactionAmountLimit(ExhTask task) {
        SysConfiguration sysConfigAmountLimit = exhSysConfigurationCacheUtility.readByKeyAndCompanyId(exhSysConfigurationCacheUtility.EXH_TASK_PER_TRANSACTION_AMOUNT, exhSessionUtil.appSessionUtil.getCompanyId())
        if (!sysConfigAmountLimit) {
            return TASK_AMOUNT_LIMIT_CONFIG_NOT_FOUND
        }
        double maxAmountLimit = 0.0d
        try {
            maxAmountLimit = Double.parseDouble(sysConfigAmountLimit.value)
            maxAmountLimit = maxAmountLimit.round(2)
        } catch (Exception e) {
            maxAmountLimit = 0.0d
        }
        if (task.amountInLocalCurrency > maxAmountLimit) {
            return TASK_AMOUNT_EXCEEDS_LIMIT + maxAmountLimit.round(2)
        }
        return null
    }

    /**
     * check customer transaction limit in a calender month based on sys_config
     */
    private String checkCustomerMonthlyTranLimit(long customerId, double amount) {
        Date date1 = new Date() - DateUtility.DATE_RANGE_THIRTY
        Date date2 = new Date()
        String startDate = DateUtility.getFromDateWithSecond(date1)
        String endDate = DateUtility.getToDateWithSecond(date2)
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()

        SysConfiguration sysConfigAmountLimit = exhSysConfigurationCacheUtility.readByKeyAndCompanyId(exhSysConfigurationCacheUtility.MONTHLY_TRAN_LIMIT_PER_CUSTOMER, companyId)
        if (!sysConfigAmountLimit) {
            return TASK_MONTHLY_AMOUNT_LIMIT_CONFIG_NOT_FOUND
        }
        double maxAmountLimit
        try {
            maxAmountLimit = Double.parseDouble(sysConfigAmountLimit.value)
            maxAmountLimit = maxAmountLimit.round(2)
        } catch (Exception e) {
            maxAmountLimit = 0.0d
        }
        if(maxAmountLimit < 0) {    //any negative will disable this check
            return null
        }
        SystemEntity exhNewTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_NEW_TASK, companyId)
        SystemEntity exhSentToBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_SENT_TO_BANK, companyId)
        SystemEntity exhStatusUnApprovedSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_UN_APPROVED, companyId)
        SystemEntity exhSentToOtherBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_SENT_TO_OTHER_BANK, companyId)
        SystemEntity exhResolvedByOtherBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_RESOLVED_BY_OTHER_BANK, companyId)

        String queryStr = """
            SELECT COALESCE(sum(amount_in_local_currency),0) as sum
            FROM exh_task
            WHERE
               exh_task.company_id=${companyId} AND
               exh_task.customer_id =${customerId} AND
               exh_task.created_on BETWEEN '${startDate}' AND '${endDate}' AND
               exh_task.current_status IN(${exhNewTaskSysEntityObject.id},
               ${exhSentToBankSysEntityObject.id},
               ${exhSentToOtherBankSysEntityObject.id},
               ${exhResolvedByOtherBankSysEntityObject.id},
               ${exhStatusUnApprovedSysEntityObject.id})
        """
        double totalAmount = (double) executeSelectSql(queryStr).first().sum
        totalAmount += amount

        if (totalAmount > maxAmountLimit) {
            return MONTHLY_AMOUNT_EXCEEDS
        }
        return null
    }

    private static final String TOTAL_TASK_AMOUNT_OF_CUSTOMER =
            """
            SELECT
                sum(task.amount_in_local_currency) as total_amount_gbp
            FROM
                 exh_task task
            WHERE
                task.customer_id =:customerId
        """
    /**
     * Get total task amount(Local) of a particular customer
     * @param customerId
     * @return total
     */
    private double getTotalAmountForCustomer(Long customerId) {
        Map queryParams = [customerId: customerId]
        List<GroovyRowResult> results = executeSelectSql(TOTAL_TASK_AMOUNT_OF_CUSTOMER, queryParams)
        Double total = results[0].total_amount_gbp ? results[0].total_amount_gbp : 0
        return total.doubleValue()
    }

    /**
     * Update beneficiary information if changes payment method and information is null
     * @param params -serialized parameters from UI
     * @param task -ExhTask object
     */
    private void checkAndUpdateBeneficiary(GrailsParameterMap params, ExhTask task, ExhBeneficiary beneficiary) {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity exhPaymentMethodObj = (SystemEntity) exhPaymentMethodCacheUtility.readByReservedAndCompany(exhPaymentMethodCacheUtility.PAYMENT_METHOD_BANK_DEPOSIT, companyId)
        SystemEntity exhPaymentMethodCashObj = (SystemEntity) exhPaymentMethodCacheUtility.readByReservedAndCompany(exhPaymentMethodCacheUtility.PAYMENT_METHOD_CASH_COLLECTION, companyId)

        boolean isChanged = false
        switch (task.paymentMethod) {
            case exhPaymentMethodObj.id:
                if ((!beneficiary.accountNo.equals(params.accountNumber)) ||
                        (!beneficiary.bank.equals(params.bankName)) ||
                        (!beneficiary.bankBranch.equals(params.bankBranchName)) ||
                        (!beneficiary.district.equals(params.districtName))) {
                    beneficiary.accountNo = params.accountNumber
                    beneficiary.bank = params.bankName
                    beneficiary.bankBranch = params.bankBranchName
                    beneficiary.district = params.districtName
                    isChanged = true
                }
                break
            case exhPaymentMethodCashObj.id:
                if ((!beneficiary.photoIdType.equals(params.identityType)) ||
                        (!beneficiary.photoIdNo.equals(params.identityNo))) {

                    beneficiary.photoIdType = params.identityType
                    beneficiary.photoIdNo = params.identityNo.toString().length() > 0 ? params.identityNo : null
                    isChanged = true
                }
                break
        }
        if (isChanged) {
            updateForCreateTask(beneficiary)
        }
    }

    /**
     * Update beneficiary information in DB
     * @param beneficiary -ExhBeneficiary object
     */
    private Integer updateForCreateTask(ExhBeneficiary beneficiary) {
        String query = """UPDATE exh_beneficiary SET
                          version=${beneficiary.version + 1},
                          photo_id_type=:photoIdType,
                          photo_id_no=:photoIdNo,
                          phone=:phone,
                          account_no=:accountNo,
                          bank=:bank,
                          bank_branch=:bankBranch,
                          district=:district,
                          thana=:thana
                      WHERE
                          id=${beneficiary.id} AND
                          version=${beneficiary.version}"""

        Map queryParams = [photoIdType: beneficiary.photoIdType,
                photoIdNo: beneficiary.photoIdNo,
                phone: beneficiary.phone,
                accountNo: beneficiary.accountNo,
                bank: beneficiary.bank,
                bankBranch: beneficiary.bankBranch,
                district: beneficiary.district,
                thana: beneficiary.thana]

        int updateCount = executeUpdateSql(query, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException(FAIL_UPDATE_BENEFICIARY)
        }
        beneficiary.version = beneficiary.version + 1
        return (new Integer(updateCount))
    }

    /**
     * Build task object
     * @param params -serialized parameters from UI
     * @param existingTask -ExhTask object
     * @return existingTask -updated task object
     */
    public ExhTask buildTaskObject(GrailsParameterMap params, ExhTask existingTask) {
        ExhTask newTask = new ExhTask(params)
        newTask.amountInForeignCurrency = Double.parseDouble(params.hidAmountInForeignCurrency)
        double amountInLocalCurrency = Double.parseDouble(params.hidAmountInLocalCurrency)
        newTask.amountInLocalCurrency = amountInLocalCurrency
        newTask.conversionRate = Double.parseDouble(params.hidConversionRate)
        newTask.discount = params.hidDiscount ? Double.parseDouble(params.hidDiscount) : 0.0d

        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity paidByOnline = (SystemEntity) exhPaidByCacheUtility.readByReservedAndCompany(exhPaidByCacheUtility.PAID_BY_ID_ONLINE, companyId)
        SystemEntity paidByPaypoint = (SystemEntity) exhPaidByCacheUtility.readByReservedAndCompany(exhPaidByCacheUtility.PAID_BY_ID_PAY_POINT, companyId)
        SystemEntity agentTaskObj = (SystemEntity) exhTaskTypeCacheUtility.readByReservedAndCompany(exhTaskTypeCacheUtility.TYPE_AGENT_TASK, companyId)

        // Now read the config to evaluate Reg fee & commission
        boolean evaluateAsLocalCurrency = true // Default value , if config not found
        SysConfiguration sysConfig = (SysConfiguration) exhSysConfigurationCacheUtility.readByKeyAndCompanyId(exhSysConfigurationCacheUtility.EXH_REGULAR_FEE_EVALUATE_ON_LOCAL_CURRENCY, companyId)
        if (sysConfig) {
            evaluateAsLocalCurrency = sysConfig.value.equals(VALUE_1)      // 1 = local(GBP) , 0 = foreign(BDT)
        }
        double amountToEvaluate = (evaluateAsLocalCurrency) ? newTask.amountInLocalCurrency : newTask.amountInForeignCurrency

        newTask.regularFee = getRegularFee(amountToEvaluate)

        if (existingTask.taskTypeId == agentTaskObj.id) {
            newTask.commission = getCommission(amountToEvaluate)
        }

        newTask.toCurrencyId = Integer.parseInt(params.toCurrencyId)
        newTask.fromCurrencyId = Integer.parseInt(params.fromCurrencyId)
        if (!newTask.outletBankId) newTask.outletBankId = bankCacheUtility.getSystemBank().id
        if (!newTask.outletDistrictId) newTask.outletDistrictId = null
        if (!newTask.outletBranchId) newTask.outletBranchId = null

        newTask.companyId = companyId
        newTask.paidByNo = null
        newTask.isGatewayPaymentDone = true // default value

        if (newTask.paidBy == paidByOnline.id) {
            newTask.paidByNo = params.paidByNo
        }

        if (newTask.paidBy == paidByPaypoint.id && existingTask.paidBy != newTask.paidBy) {
            // update by  paypoint & method changed
            newTask.isGatewayPaymentDone = false
        } else if (newTask.paidBy == paidByPaypoint.id && !existingTask.isGatewayPaymentDone) {
            // keep isGatewayPaymentDone= true for paypoint
            newTask.isGatewayPaymentDone = false
        }

        // now populate existing object with new properties
        existingTask.remittancePurpose = newTask.remittancePurpose
        existingTask.paymentMethod = newTask.paymentMethod
        existingTask.outletBankId = newTask.outletBankId
        existingTask.outletDistrictId = newTask.outletDistrictId
        existingTask.outletBranchId = newTask.outletBranchId
        existingTask.amountInForeignCurrency = newTask.amountInForeignCurrency
        existingTask.amountInLocalCurrency = newTask.amountInLocalCurrency
        existingTask.fromCurrencyId = newTask.fromCurrencyId
        existingTask.toCurrencyId = newTask.toCurrencyId
        existingTask.conversionRate = newTask.conversionRate
        existingTask.regularFee = newTask.regularFee
        existingTask.commission = newTask.commission
        existingTask.discount = newTask.discount  // customer discount check
        existingTask.paidBy = newTask.paidBy
        existingTask.paidByNo = newTask.paidByNo
        existingTask.isGatewayPaymentDone = newTask.isGatewayPaymentDone
        evaluateExhGain(existingTask)
        return existingTask
    }

    /**
     * Evaluate commission logic
     * @param amount -a task local currency amount
     * @param logic - a logic come from agent
     * @return totalFee
     */
    private double evaluateLogic(double amount, String logic) {
        double totalFee = 0.0d
        try {
            if (!logic || logic.isEmpty()) return totalFee
            Binding binding = new Binding()
            binding.setVariable(AMOUNT, amount)
            GroovyShell shell = new GroovyShell(binding)
            totalFee = (double) shell.evaluate(logic)
        } catch (Exception e) {
            log.error(e.getMessage())
        }
        return totalFee
    }

    /**
     * Get amount of commission for agent
     * @param amount -a task local currency amount
     * @return commission
     */
    private double getCommission(double amount) {
        ExhAgent agent = (ExhAgent) exhAgentCacheUtility.read(exhSessionUtil.getUserAgentId())
        double commission = evaluateLogic(amount, agent.commissionLogic)
        return commission.round(2)
    }

    /**
     * Get amount of regular fee for task
     * @param amount -a task local currency amount
     * @return regularFee
     */
    private double getRegularFee(double amount) {
        List<ExhRegularFee> lstRegularFee = exhRegularFeeCacheUtility.list()
        ExhRegularFee exhRegularFee = lstRegularFee[0]  // assuming that ExhRegularFee has one object for every company
        double regularFee = evaluateLogic(amount, exhRegularFee.logic)
        return regularFee.round(2)
    }

    /**
     * Send mail to customer if customer has valid email address
     * @param task - an object of ExhTask
     * @param customer -an object of ExhCustomer
     */
    private String sendMail(ExhTask task, ExhCustomer customer) {
        // check email existence & pattern
        if ((!customer.email) || !(customer.email.matches(EMAIL_PATTERN))) {
            return EMAIL_INVALID
        }

        AppMail appMail = appMailService.findByTransactionCodeAndCompanyIdAndIsActive(TRANSACTION_CODE, task.companyId, true)
        if (!appMail) {
            return MAIL_TEMPLATE_NOT_FOUND
        }

        Map paramsBody = [customerName: customer.fullName, customerCode: customer.code]
        Map paramsSubject = [refNo: task.refNo]
        SimpleTemplateEngine engine = new SimpleTemplateEngine()
        Writable templateBody = engine.createTemplate(appMail.body).make(paramsBody)
        Writable templateSubject = engine.createTemplate(appMail.subject).make(paramsSubject)
        appMail.body = templateBody.toString()
        appMail.subject = templateSubject.toString()
        byte[] attachment = getInvoiceReport(task)

        Environment.executeForCurrentEnvironment {
            development {
                executeMail(appMail, customer.email, task, attachment)          // For Development
            }
            production {
                executeMailForSFSA(appMail, customer.email, task, attachment)   // For Production
            }
        }
        return null
    }

    /**
     * process mail for sending in development
     */
    private void executeMail(AppMail appMail, String userMailAddress, ExhTask task, byte[] attachment) {
        executorService.submit({
            println "Sending mail for transaction ${appMail.transactionCode} ..."

            mailService.sendMail {
                multipart true
                to userMailAddress
                subject appMail.subject
                html appMail.body
                attachBytes task.refNo, MIME_PDF, attachment
            }
            println "Mail sent successfully for ${appMail.transactionCode}"
        })
    }


    private static final String MAIL_HOST = "mail.smtp.host"
    private static final String MAIL_PORT = "mail.smtp.port"
    private static final String MAIL_MIME_CONTENT = "text/html; charset=utf-8"
    private static final String CONTENT_TYPE = "Content-Type"
    private static final String MIME_PDF = "application/pdf"

    /**
     * process mail for sending in production
     */
    private void executeMailForSFSA(AppMail appMail, String userMailAddress, ExhTask task, byte[] attachment) {
        try {
            String host = grailsApplication.config.mis.exh.sfsa.mail.smtp.host
            String port = grailsApplication.config.mis.exh.sfsa.mail.smtp.port
            String email = grailsApplication.config.mis.exh.sfsa.mail.smtp.email
            String pwd = grailsApplication.config.mis.exh.sfsa.mail.smtp.pwd
            Company company = (Company) companyCacheUtility.read(exhSessionUtil.appSessionUtil.getCompanyId())
            String emailFrom = company.email
            if (!emailFrom) {
                emailFrom = grailsApplication.config.mis.exh.sfsa.mail.from
            }
            Properties props = new Properties()
            props.put(MAIL_HOST, host)
            props.put(MAIL_PORT, port)


            Session session = Session.getDefaultInstance(props,
                    new javax.mail.Authenticator() {
                        protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                            return new javax.mail.PasswordAuthentication(email, pwd)
                        }
                    })
            Message message = new MimeMessage(session)
            message.setFrom(new InternetAddress(emailFrom))
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userMailAddress))
            message.setSubject(appMail.subject)

            // create multipart which contains body & attachment
            Multipart multiPart = new MimeMultipart()
            MimeBodyPart bodyPartText = new MimeBodyPart()
            MimeBodyPart bodyPartInvoice = new MimeBodyPart()

            // create body part for Mail-Text
            bodyPartText.setHeader(CONTENT_TYPE, MAIL_MIME_CONTENT)
            bodyPartText.setContent(appMail.body, MAIL_MIME_CONTENT)
            // create body part for Mail-Attachment
            bodyPartInvoice.setDataHandler(
                    new DataHandler(
                            new DataSource() {
                                public String getContentType() {
                                    return MIME_PDF
                                }

                                public InputStream getInputStream() throws IOException {
                                    return new ByteArrayInputStream(attachment)
                                }

                                public String getName() {
                                    return null
                                }

                                public OutputStream getOutputStream() throws IOException {
                                    return null
                                }
                            }))

            bodyPartInvoice.setFileName(task.refNo)
            // Add all body parts to multipart
            multiPart.addBodyPart(bodyPartText)
            multiPart.addBodyPart(bodyPartInvoice)

            message.setContent(multiPart)

            executorService.submit({
                println "Sending mail for transaction ${appMail.transactionCode} ..."
                try {
                    Transport.send(message)
                    println "Mail sent successfully for ${appMail.transactionCode}"
                } catch (Exception e) {
                    println('Mail Exception: ' + e.getMessage())
                }

            })
        } catch (Exception e) {
            log.error(e.getMessage())
            println('Mail Exception: ' + e.getMessage())
        }
    }

    /**
     * Get invoice report as PDF
     * @param task -a object of ExhTask
     */
    private byte[] getInvoiceReport(ExhTask task) {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity exhPaymentMethodObj = (SystemEntity) exhPaymentMethodCacheUtility.readByReservedAndCompany(exhPaymentMethodCacheUtility.PAYMENT_METHOD_BANK_DEPOSIT, companyId)
        SystemEntity exhPaymentMethodCashObj = (SystemEntity) exhPaymentMethodCacheUtility.readByReservedAndCompany(exhPaymentMethodCacheUtility.PAYMENT_METHOD_CASH_COLLECTION, companyId)

        AppUser user = (AppUser) appUserCacheUtility.read(task.userId)

        String reportDir = Tools.getExchangeHouseReportDirectory() + File.separator + REPORT_FOLDER
        String subReportDir = reportDir + File.separator + SUB_REPORT_FOLDER + File.separator

        String remittancePurposeName = exhRemittancePurposeCacheUtility.read(task.remittancePurpose).name
        SystemEntity paymentMethod = (SystemEntity) exhPaymentMethodCacheUtility.read(task.paymentMethod)

        // now evaluate collection point
        String collectionPoint = Tools.NOT_APPLICABLE
        Bank systemBank = bankCacheUtility.getSystemBank()

        if (task.paymentMethod == exhPaymentMethodCashObj.id) {
            if (task.outletBankId == systemBank.id) {
                collectionPoint = systemBank.name + Tools.COMA + ANY_BRANCH
            } else {
                Bank bank = (Bank) bankCacheUtility.read(task.outletBankId)
                BankBranch bankBranch = (BankBranch) bankBranchCacheUtility.read(task.outletBranchId)
                District district = (District) districtCacheUtility.read(task.outletDistrictId)
                collectionPoint = bank.name + Tools.COMA + bankBranch.name + Tools.COMA + district.name
            }
        } else if (task.paymentMethod == exhPaymentMethodObj.id) {
            if (task.outletBankId != systemBank.id) {
                Bank bank = (Bank) bankCacheUtility.read(task.outletBankId)
                BankBranch bankBranch = (BankBranch) bankBranchCacheUtility.read(task.outletBranchId)
                District district = (District) districtCacheUtility.read(task.outletDistrictId)
                collectionPoint = bank.name + Tools.COMA + bankBranch.name + Tools.COMA + district.name
            }
        }

        String paidByName = exhPaidByCacheUtility.read(task.paidBy).key
        Company company = (Company) companyCacheUtility.read(task.companyId)

        Map reportParams = new LinkedHashMap()
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(SUB_REPORT_DIR, subReportDir)
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(TASK_ID, task.id)
        reportParams.put(SESSION_USER_NAME, user.username)
        reportParams.put(REMITTANCE_PURPOSE, remittancePurposeName)
        reportParams.put(PAYMENT_METHOD_NAME, paymentMethod.key)
        reportParams.put(PAYMENT_METHOD_ID, paymentMethod.id)
        reportParams.put(PAID_BY, paidByName)
        reportParams.put(COLLECTION_POINT, collectionPoint)
        reportParams.put(PAY_METHOD_BANK_DEPOSIT_ID, exhPaymentMethodObj.id)
        reportParams.put(PAY_METHOD_CASH_COLLECTION_ID, exhPaymentMethodCashObj.id)
        reportParams.put(LOCAL_CURRENCY_NAME, currencyCacheUtility.localCurrency.symbol)
        reportParams.put(COMPANY_LOGO, getLogo(company))

        String reportFile
        if (task.agentId > 0) {
            ExhAgent exhAgent = (ExhAgent) exhAgentCacheUtility.read(task.agentId)
            String exhAgentAddress = exhAgent.address.replace(Tools.NEW_LINE, Tools.SINGLE_SPACE)
            reportParams.put(AGENT_NAME, exhAgent.name)
            reportParams.put(AGENT_PHONE, exhAgent.phone)
            reportParams.put(AGENT_ADDRESS, exhAgentAddress)
            reportFile = JASPER_FILE_FOR_AGENT
        } else {
            reportParams.put(EXCHANGE_HOUSE_NAME, company.name)
            reportFile = JASPER_FILE_CASHIER
        }
        JasperReportDef reportDef = new JasperReportDef(name: reportFile, fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: reportParams, folder: reportDir)
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        report.toByteArray()
    }

    /**
     * Get company logo
     * @param company -a object of Company
     * @return logo
     */
    private InputStream getLogo(Company company) {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        // pull system entity type(Company) object
        SystemEntity contentEntityTypeCompany = (SystemEntity) contentEntityTypeCacheUtility.readByReservedAndCompany(contentEntityTypeCacheUtility.CONTENT_ENTITY_TYPE_COMPANY, companyId)

        long entityTypeId = contentEntityTypeCompany.id
        ContentCategory contentCategory = (ContentCategory) contentCategoryCacheUtility.readBySystemContentCategory(contentCategoryCacheUtility.IMAGE_TYPE_LOGO)
        EntityContent entityContent = entityContentService.findByEntityTypeIdAndEntityIdAndContentTypeId(entityTypeId, company.id, contentCategory.contentTypeId)
        InputStream logo = new ByteArrayInputStream(entityContent.content)
        return logo
    }

    private static final String UTF_8 = "UTF-8"
    private static final String RECIPIENT = 'recipient'
    private static final String CONTENT = 'content'
    private static final String ACTION_NAME_BANK = "ExhCreateTaskActionService_BankDeposit"
    private static final String ACTION_NAME_CASH = "ExhCreateTaskActionService_CashCollection"
    private static final String SMS_TEMPLATE_NOT_FOUND = "SMS to customer is not activated"
    private static final String PHONE_INVALID = "Customer phone not found or invalid"
    private static final String BENEFICIARY_FULL_NAME = "beneficiaryFullName"

    /**
     * Build & send SMS to Customer & Beneficiary if both has valid phone number
     * @param task -an object of ExhTask
     * @param customer -an object of ExhCustomer
     * @param beneficiary -an object of ExhBeneficiary
     */
    private String sendSms(ExhTask task, ExhCustomer customer, ExhBeneficiary beneficiary) {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity exhPaymentMethodObj = (SystemEntity) exhPaymentMethodCacheUtility.readByReservedAndCompany(exhPaymentMethodCacheUtility.PAYMENT_METHOD_BANK_DEPOSIT, companyId)
        SystemEntity exhPaymentMethodCashObj = (SystemEntity) exhPaymentMethodCacheUtility.readByReservedAndCompany(exhPaymentMethodCacheUtility.PAYMENT_METHOD_CASH_COLLECTION, companyId)

        String actionName = Tools.EMPTY_SPACE
        if (task.paymentMethod == exhPaymentMethodObj.id) {
            actionName = ACTION_NAME_BANK
        } else if (task.paymentMethod == exhPaymentMethodCashObj.id) {
            actionName = ACTION_NAME_CASH
        }
        List<Sms> lstSms = smsCacheUtility.listByTransactionCodeAndCompanyIdAndIsActive(actionName, task.companyId, true)
        // pull sms from cache by action
        if (lstSms.size() == 0) return SMS_TEMPLATE_NOT_FOUND
        Company company = (Company) companyCacheUtility.read(customer.companyId)
        Country country = (Country) countryCacheUtility.read(company.countryId)


        // if sms for customer but his phone isn't available then don't send sms
        if ((!customer.phone) || (customer.phone.length() == 0)) {
            return PHONE_INVALID
        }
        customer.phone = country.isdCode + customer.phone
        task.amountInForeignCurrency = Math.floor(task.amountInForeignCurrency)

        // fit long text for sms
        String beneficiaryFullName = beneficiary.fullName
        if (beneficiaryFullName.length() > 27) {
            beneficiaryFullName = beneficiaryFullName.substring(0, 27)
        }
        if (beneficiary.accountNo) {
            if (beneficiary.accountNo.length() > 35) {
                beneficiary.accountNo = beneficiary.accountNo.substring(0, 35)
            }
        }
        for (int i = 0; i < lstSms.size(); i++) {
            Sms sms = lstSms[i]
            Binding binding = new Binding()
            binding.setVariable(TASK_OBJ, task)
            // although customer/beneficiary object has no use in sms(currently) But we set variable in case of further use w/o changing codebase
            binding.setVariable(CUSTOMER_OBJ, customer)
            binding.setVariable(BENEFICIARY_OBJ, beneficiary)
            binding.setVariable(BENEFICIARY_FULL_NAME, beneficiaryFullName)
            GroovyShell shell = new GroovyShell(binding)
            String content = shell.evaluate(sms.body)
            // format content for sms
            String encodedContent= URLEncoder.encode(content,UTF_8);
            // now evaluate full sms url
            binding = new Binding()
            binding.setVariable(RECIPIENT, customer.phone)
            binding.setVariable(CONTENT, encodedContent)
            shell = new GroovyShell(binding)
            String strSms = shell.evaluate(sms.url)
            strSms.toURL().text
        }
        return null
    }

    /**
     * Evaluate task.exhGain based on buyRate and sellRate
     */
    private void evaluateExhGain(ExhTask task) {
        double total = task.amountInLocalCurrency + task.regularFee - task.discount - task.commission
        Currency fromCurrency = currencyCacheUtility.getLocalCurrency()
        Currency toCurrency = currencyCacheUtility.getForeignCurrency()
        ExhCurrencyConversion gbpToBdtConversion = exhCurrencyConversionCacheUtility.readByCurrencies(fromCurrency.id, toCurrency.id)
        // get local currency
        ExhCurrencyConversion bdtToGbpConversion = exhCurrencyConversionCacheUtility.readByCurrencies(toCurrency.id, fromCurrency.id)
        // get foreign currency
        double diff = gbpToBdtConversion.buyRate - gbpToBdtConversion.sellRate

        double gain = total * diff
        gain = gain * bdtToGbpConversion.buyRate
        task.exhGain = gain.round(2)
    }

    /**
     * 1. check if paidBy is changed if paidBy is payPoint and isGatewayPaymentDone is true
     * 2. check if customer payable amount is changed if paidBy is payPoint and isGatewayPaymentDone is true
     */
    private String checkAmountAndPaidByOfPayPointTask(ExhTask task, ExhTask existingTask) {
        if (!existingTask.isGatewayPaymentDone) return null
        long existingTaskPaidBy = (long) existingTask.paidBy
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity paidByPaypoint = (SystemEntity) exhPaidByCacheUtility.readByReservedAndCompany(ExhPaidByCacheUtility.PAID_BY_ID_PAY_POINT, companyId)
        if (existingTaskPaidBy != paidByPaypoint.id) return null

        // now all isGatewayPaymentDone = true && existing was payPoint
        if (task.paidBy != existingTask.paidBy) {
            return PAID_BY_CHANGED
        }

        double newPayable = task.amountInLocalCurrency + task.regularFee - task.discount
        double existingPayable = existingTask.amountInLocalCurrency + existingTask.regularFee - existingTask.discount
        if (newPayable != existingPayable) {
            return AMOUNT_MODIFIED
        }
        return null
    }
    private String checkBlockedCustomer(ExhTask exhTask){
        long customerId= exhTask.customerId
        ExhCustomer exhCustomer=exhCustomerService.read(customerId)
        boolean isBlocked= exhCustomer.isBlocked
        if(isBlocked){
            return CUSTOMER_IS_BLOCKED
        }
        return null

    }
}
