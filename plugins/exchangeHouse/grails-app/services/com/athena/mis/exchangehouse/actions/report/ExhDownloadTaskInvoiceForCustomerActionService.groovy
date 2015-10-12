package com.athena.mis.exchangehouse.actions.report

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.*
import com.athena.mis.application.service.EntityContentService
import com.athena.mis.application.utility.*
import com.athena.mis.exchangehouse.entity.ExhAgent
import com.athena.mis.exchangehouse.entity.ExhTask
import com.athena.mis.exchangehouse.service.ExhTaskService
import com.athena.mis.exchangehouse.utility.*
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Download Invoice in pdf format for Customer
 * For details go through Use-Case doc named 'ExhDownloadTaskInvoiceForCustomerActionService'
 */
class ExhDownloadTaskInvoiceForCustomerActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    JasperService jasperService
    ExhTaskService exhTaskService
    EntityContentService entityContentService
    @Autowired
    BankCacheUtility bankCacheUtility
    @Autowired
    BankBranchCacheUtility bankBranchCacheUtility
    @Autowired
    DistrictCacheUtility districtCacheUtility
    @Autowired
    CompanyCacheUtility companyCacheUtility
    @Autowired
    ExhPaidByCacheUtility exhPaidByCacheUtility
    @Autowired
    ExhPaymentMethodCacheUtility exhPaymentMethodCacheUtility
    @Autowired
    ExhRemittancePurposeCacheUtility exhRemittancePurposeCacheUtility
    @Autowired
    ExhAgentCacheUtility exhAgentCacheUtility
    @Autowired
    ExhTaskTypeCacheUtility exhTaskTypeCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    CurrencyCacheUtility currencyCacheUtility
    @Autowired
    ContentEntityTypeCacheUtility contentEntityTypeCacheUtility
    @Autowired
    ContentCategoryCacheUtility contentCategoryCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil

    private static final String REPORT_FILE_FORMAT = 'pdf'
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_FOLDER = 'task'
    private static final String SUB_REPORT_FOLDER = 'subreports'
    private static final String JASPER_FILE_CASHIER = 'TaskInvoice.jasper'
    private static final String JASPER_FILE_FOR_AGENT = 'TaskInvoiceForAgent.jasper'
    private static final String REPORT_TITLE = 'Task Invoice'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String SUB_REPORT_DIR = 'SUBREPORT_DIR'
    private static final String ERROR_INVALID_INPUT = "Error occurred for invalid inputs"
    private static final String ERROR_TASK_NOT_FOUND = "Task not found"
    private static final String ERROR_EXCEPTION = "Failed to generate invoice report"
    private static final String PDF_EXTENSION = ".pdf"
    private static final String REPORT = "report"

    private static final String TASK_OBJECT = "taskObject"
    private static final String TASK_ID = "taskId"
    private static final String SESSION_USER_NAME = "sessionUserName"
    private static final String REMITTANCE_PURPOSE = "remittancePurpose"
    private static final String PAYMENT_METHOD_NAME = "paymentMethodName"
    private static final String PAYMENT_METHOD_ID = "paymentMethodId"
    private static final String PAID_BY = "paidBy"
    private static final String ANY_BRANCH = 'Any Branch'
    private static final String COLLECTION_POINT = 'collectionPoint'
    private static final String EXCHANGE_HOUSE_NAME = 'exchangeHouseName'
    private static final String PAY_METHOD_BANK_DEPOSIT_ID = 'payMethodBankDepositId'
    private static final String PAY_METHOD_CASH_COLLECTION_ID = 'payMethodCashCollectionId'

    private static final String AGENT_NAME = 'agentName'
    private static final String AGENT_PHONE = 'agentPhone'
    private static final String AGENT_ADDRESS = 'agentAddress'
    private static final String LOCAL_CURRENCY_NAME = 'localCurrencyName'
    private static final String COMPANY_LOGO = "companyLogo"

    /**
     * 1. check required parameters
     * 2. pull task object & check existence
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return - a map containing budget object & isError(True/False)
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)             // set default value
            GrailsParameterMap params = (GrailsParameterMap) parameters

            if ((!params.hidTaskId) || (!params.hidRefNo)) {
                result.put(Tools.MESSAGE, ERROR_INVALID_INPUT)
                return result
            }
            long taskId = Long.parseLong(params.hidTaskId.toString())

            ExhTask task = exhTaskService.read(taskId)          // get task object form DB

            if (!task) {
                result.put(Tools.MESSAGE, ERROR_TASK_NOT_FOUND)
                return result
            }
            long customerId = exhSessionUtil.getUserCustomerId()
            if(customerId != task.customerId) {
                result.put(Tools.MESSAGE, ERROR_TASK_NOT_FOUND)
                return result
            }
            result.put(TASK_OBJECT, task)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_EXCEPTION)
            return result
        }
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get desired report providing all required parameters
     * 1. Build invoice map by given parameters
     * @param params - N/A
     * @param obj - receive map from executePreCondition
     * @return -a map containing report
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            Map preResult = (Map) obj
            ExhTask task = (ExhTask) preResult.get(TASK_OBJECT)
            long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
            SystemEntity exhTaskObj = (SystemEntity) exhTaskTypeCacheUtility.readByReservedAndCompany(exhTaskTypeCacheUtility.TYPE_EXH_TASK, companyId)
            SystemEntity agentTaskObj = (SystemEntity) exhTaskTypeCacheUtility.readByReservedAndCompany(exhTaskTypeCacheUtility.TYPE_AGENT_TASK, companyId)

            AppUser taskCreator
            if ((task.taskTypeId == agentTaskObj.id) || (task.taskTypeId == exhTaskObj.id)) {
                taskCreator = (AppUser) appUserCacheUtility.read(task.userId)
            } else {
                taskCreator = (AppUser) appUserCacheUtility.read(task.approvedBy)
            }
            Map report = getInvoiceReport(task, taskCreator)            // build invoice map

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(REPORT, report)
            return result
        } catch (Exception ex) {
            log.error ex.getMessage()
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_EXCEPTION)
            return result
        }
    }

    /**
     * do nothing for build success operation
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    /**
     * do nothing for failure operation
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    /**
     * Generate Invoice report with assigned parameters
     * 1. check payment method of task, if cash collection & outletBank equal system bank then collection point will be any branch of SouthEast Bank
     * 2. other wise collection point will be another bank
     * 3. check payment method of task, if Bank Deposit then collection point will be  desired bank
     * @param task - object of ExhTask
     * @param user - an object of AppUser
     * @return - generated report with required params
     */
    private Map getInvoiceReport(ExhTask task, AppUser user) {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity agentTaskObj = (SystemEntity) exhTaskTypeCacheUtility.readByReservedAndCompany(exhTaskTypeCacheUtility.TYPE_AGENT_TASK, companyId)
        SystemEntity exhPaymentMethodObj = (SystemEntity) exhPaymentMethodCacheUtility.readByReservedAndCompany(exhPaymentMethodCacheUtility.PAYMENT_METHOD_BANK_DEPOSIT, companyId)
        SystemEntity exhPaymentMethodCashObj = (SystemEntity) exhPaymentMethodCacheUtility.readByReservedAndCompany(exhPaymentMethodCacheUtility.PAYMENT_METHOD_CASH_COLLECTION, companyId)

        String reportDir = Tools.getExchangeHouseReportDirectory() + File.separator + REPORT_FOLDER
        String subReportDir = reportDir + File.separator + SUB_REPORT_FOLDER + File.separator

        String outputFileName = task.refNo + PDF_EXTENSION
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

        Company company = (Company) companyCacheUtility.read(task.companyId)
        String paidByName = exhPaidByCacheUtility.read(task.paidBy).key

        Map reportParams = new LinkedHashMap()
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(SUB_REPORT_DIR, subReportDir)
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(TASK_ID, task.id)
        reportParams.put(SESSION_USER_NAME, user?.username)
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

        if (task.taskTypeId == agentTaskObj.id) {
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
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)         // generate pdf report
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }

    /**
     * Get company logo by company
     * @param company
     * @return logo -company logo
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
}
