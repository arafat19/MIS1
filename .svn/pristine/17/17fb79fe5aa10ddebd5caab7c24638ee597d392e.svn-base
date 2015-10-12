package com.athena.mis.procurement.actions.purchaserequest

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.*
import com.athena.mis.application.service.AppMailService
import com.athena.mis.application.service.EntityContentService
import com.athena.mis.application.utility.*
import com.athena.mis.integration.budget.BudgetPluginConnector
import com.athena.mis.procurement.entity.ProcPurchaseRequest
import com.athena.mis.procurement.service.PurchaseRequestService
import com.athena.mis.procurement.utility.ProcSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.plugin.mail.MailService
import groovy.text.SimpleTemplateEngine
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

import java.util.concurrent.ExecutorService

class SentMailPurchaseRequestActionService extends BaseService implements ActionIntf {

    private static final String INPUT_VALIDATION_FAIL_ERROR = "Fail to approve Purchase Request due to Invalid Input"
    private static final String NOT_FOUND_MESSAGE = "Selected Purchase Request not found"
    private static final String ALREADY_SENT_ERROR = "Purchase Request already sent for approval"
    private static final String MAIL_TEMPLATE_NOT_FOUND = "Purchase request saved but notification mail not send due to absence of mail template"
    private static final String MAIL_RECIPIENT_NOT_FOUND = "Purchase request saved but notification mail not send due to absence of recipient"
    private static final String PR_HAS_NO_ITEMS = "PR has no item(s)"
    private static final String PURCHASE_REQUEST_OBJ = "purchaseRequestInstance"
    private static final String TRANSACTION_CODE = "SentMailPurchaseRequestActionService"
    private static final String ERROR_SENDING_MAIL = "Purchase request mail notification not send"
    private static final String MAIL_SUBJECT_POSTFIX = "PR No:"
    private static final String ROLE_NOT_FOUND = "Role not found to send the mail"
    private static final String USER_NOT_FOUND = "User not found to send the mail"
    private static final String MAIL_ADDRESS = "mailAddress"
    private static final String MIME_PDF = "application/pdf"
    private static final String PURCHASE_REQUEST_APPROVE_SUCCESS_MESSAGE = "Notification of Purchase Request has been sent successfully"

    private final String PURCHASE_REQUEST_MAP = "purchaseRequestMap"
    private final String PURCHASE_REQUEST_ID = "purchaseRequestId"

    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_FOLDER = 'purchaseRequest'
    private static final String OUTPUT_FILE_NAME = 'purchaseRequest'
    private static final String REPORT_TITLE = 'Purchase Request'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String SUBREPORT_DIR = 'SUBREPORT_DIR'
    private static final String JASPER_FILE = 'purchaseRequest.jasper'
    private static final String PDF_EXTENSION = ".pdf"
    private static final String DB_QUANTITY_FORMAT = "dbQuantityFormat"
    private static final String DB_CURRENCY_FORMAT = "dbCurrencyFormat"
    private static final String DIRECTOR_IMAGE_STREAM = "directorImageStream"
    private static final String PD_IMAGE_STREAM = "pdImageStream"

    private Logger log = Logger.getLogger(getClass())

    PurchaseRequestService purchaseRequestService
    AppMailService appMailService
    ExecutorService executorService
    MailService mailService
    JasperService jasperService
    BudgetPluginConnector budgetImplService
    EntityContentService entityContentService
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    UnitCacheUtility unitCacheUtility
    @Autowired
    ContentEntityTypeCacheUtility contentEntityTypeCacheUtility
    @Autowired
    ContentCategoryCacheUtility contentCategoryCacheUtility
    @Autowired
    ProcSessionUtil procSessionUtil

    /**
     * Check all pre conditions before sending notification
     * @param params - params from UI
     * @param obj - N/A
     * @return - a map containing Purchase request object
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params

            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }

            long purchaseRequestId = Long.parseLong(parameterMap.id.toString())
            ProcPurchaseRequest procPurchaseRequest = purchaseRequestService.read(purchaseRequestId)
            // check whether the PR object exists or not
            if (!procPurchaseRequest) {
                result.put(Tools.MESSAGE, NOT_FOUND_MESSAGE)
                return result
            }

            // check the selected PR is already approved or not
            if (procPurchaseRequest.sentForApproval) {
                result.put(Tools.MESSAGE, ALREADY_SENT_ERROR)
                return result
            }

            // check the PR has items or not
            if (procPurchaseRequest.itemCount <= 0) {
                result.put(Tools.MESSAGE, PR_HAS_NO_ITEMS)
                return result
            }

            procPurchaseRequest.sentForApproval = true

            result.put(PURCHASE_REQUEST_OBJ, procPurchaseRequest)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_SENDING_MAIL)
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
     * Get Purchase request object
     * 1. Check the existence of app mail object
     * 2. Check is the mail sent or not
     * 3. DB update of table named proc_purchase_request (column named sent_for_approval)
     * 4. This method is in transactional block. If there will be any
     * @param parameters - N/A
     * @param obj - object from pre executed method
     * @return - a map containing Purchase request object
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)  // make intentional FALSE
            Map executeResult = (Map) obj
            ProcPurchaseRequest purchaseRequest = (ProcPurchaseRequest) executeResult.get(PURCHASE_REQUEST_OBJ)
            updatePurchaseRequest(purchaseRequest)  // DB update of table named proc_purchase_request
            result.put(PURCHASE_REQUEST_OBJ, purchaseRequest)

            AppMail appMail = appMailService.findByTransactionCodeAndCompanyIdAndIsActive(TRANSACTION_CODE, purchaseRequest.companyId, true)
            if (!appMail) {
                result.put(Tools.MESSAGE, MAIL_TEMPLATE_NOT_FOUND)
                return result
            }

            Boolean mailSend = sendMail(appMail, purchaseRequest)
            if (!mailSend) {
                result.put(Tools.MESSAGE, MAIL_RECIPIENT_NOT_FOUND)
                return result
            }
            return result
        }
        catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            throw new RuntimeException("Failed to update PR")
            result.put(Tools.MESSAGE, ERROR_SENDING_MAIL)
            return result
        }
    }

    /**
     * Show newly sent for approval object
     * 1. Wrap grid object
     * 2. Show success message
     * @param obj - map from execute method
     * @return - a map containing all objects necessary for grid view
     * -map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            ProcPurchaseRequest purchaseRequestInstance = (ProcPurchaseRequest) receiveResult.get(PURCHASE_REQUEST_OBJ)
            String msg = receiveResult.get(Tools.MESSAGE)    // mail template or mail recipient not found message
            GridEntity object = new GridEntity()
            String approvedByDirector
            String approvedByProjectDirector
            String isSentForApproval
            object.id = purchaseRequestInstance.id
            Project project = (Project) projectCacheUtility.read(purchaseRequestInstance.projectId)
            AppUser systemUser = (AppUser) appUserCacheUtility.read(purchaseRequestInstance.createdBy)
            approvedByDirector = purchaseRequestInstance.approvedByDirectorId ? Tools.YES : Tools.NO
            approvedByProjectDirector = purchaseRequestInstance.approvedByProjectDirectorId ? Tools.YES : Tools.NO
            isSentForApproval = purchaseRequestInstance.sentForApproval ? Tools.YES : Tools.NO
            object.cell = [
                    Tools.LABEL_NEW,
                    purchaseRequestInstance.id,
                    project.name,
                    purchaseRequestInstance.itemCount,
                    approvedByDirector,
                    approvedByProjectDirector,
                    systemUser.username,
                    isSentForApproval
            ]
            if(msg){
                result.put(Tools.MESSAGE, msg)
            }else{
                result.put(Tools.MESSAGE, PURCHASE_REQUEST_APPROVE_SUCCESS_MESSAGE)
            }
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_SENDING_MAIL)
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
            result.put(Tools.MESSAGE, ERROR_SENDING_MAIL)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_SENDING_MAIL)
            return result
        }
    }

    /**
     * 1. Get app user object
     * 2. Get mail address list by role
     * 3. Build mail body
     * 4. Build purchase request map
     * 5. Get purchase request pdf report for mail attachment
     * @param appMail - object of AppMail
     * @param purchaseRequest - object of ProcPurchaseRequest
     * @return - a boolean value
     */
    private Boolean sendMail(AppMail appMail, ProcPurchaseRequest purchaseRequest) {
        String prCreatedOn = DateUtility.getDateTimeFormatAsString(purchaseRequest.createdOn)
        AppUser user = (AppUser) appUserCacheUtility.read(purchaseRequest.createdBy)
        String prCreatedBy = user.username

        LinkedHashMap chkMailMap = checkMail(appMail, purchaseRequest.projectId)
        Boolean isError = (Boolean) chkMailMap.get(Tools.IS_ERROR)
        if (isError.booleanValue()) {
            return Boolean.FALSE
        }

        Map parameters = [prId: purchaseRequest.id, prCreatedOn: prCreatedOn, prCreatedBy: prCreatedBy]
        buildMailBody(appMail, parameters)
        List<String> userMailAddress = (List<String>) chkMailMap.get(MAIL_ADDRESS)

        // build mail subject(e.g- 'MIS Notification-PR approval(PR No: 520)' )
        appMail.subject = appMail.subject + Tools.PARENTHESIS_START + MAIL_SUBJECT_POSTFIX + purchaseRequest.id + Tools.PARENTHESIS_END
        LinkedHashMap purchaseRequestMap = buildPurchaseRequestMap(purchaseRequest)
        byte[] attachment = getPurchaseRequestReport(purchaseRequestMap)

        executeMail(appMail, userMailAddress, purchaseRequest.id, attachment)          // For Development


        return Boolean.TRUE
    }

    /**
     * Generate report by given data
     * 1. set report directory
     * 2. assign all required params
     * @param purchaseRequestMap - purchase request object
     * @return - generated report with required params
     */
    private byte[] getPurchaseRequestReport(Map purchaseRequestMap) {

        String reportDir = Tools.getProcurementReportDirectory() + File.separator + REPORT_FOLDER
        String outputFileName = OUTPUT_FILE_NAME + PDF_EXTENSION

        String subReportDir = Tools.getProcurementReportDirectory()
        Map reportParams = new LinkedHashMap()
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(SUBREPORT_DIR, subReportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(PURCHASE_REQUEST_MAP, purchaseRequestMap)
        reportParams.put(PURCHASE_REQUEST_ID, purchaseRequestMap.purchaseRequestId)
        reportParams.put(DB_QUANTITY_FORMAT, Tools.DB_QUANTITY_FORMAT)
        reportParams.put(DB_CURRENCY_FORMAT, Tools.DB_CURRENCY_FORMAT)
        reportParams.put(DIRECTOR_IMAGE_STREAM, purchaseRequestMap.directorImageStream)
        reportParams.put(PD_IMAGE_STREAM, purchaseRequestMap.pdImageStream)
        ByteArrayOutputStream report

        JasperReportDef reportDef = new JasperReportDef(name: JASPER_FILE, fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: reportParams, folder: reportDir)
        /*  Generate a report based on jasper file. */
        report = jasperService.generateReport(reportDef)

        report.toByteArray()
    }

    /**
     * 1. pull budget & budget type object
     * 2. pull unit from system entity
     * 3. pull createdBy, approvedByDirector, approvedByProjectDirector from appUser
     * 4. pull supplier object
     * 5. pull entity content for Director & Project Director
     * The method is in Transactional block as it can be roll back in case of any exception.
     * @param purchaseRequest - purchase request object
     * @return - newly built purchase request map
     */
    @Transactional(readOnly = true)
    private LinkedHashMap buildPurchaseRequestMap(ProcPurchaseRequest purchaseRequest) {

        Project project = (Project) projectCacheUtility.read(purchaseRequest.projectId)
        AppUser createdBy = (AppUser) appUserCacheUtility.read(purchaseRequest.createdBy)
        AppUser approvedByDirector = (AppUser) appUserCacheUtility.read(purchaseRequest.approvedByDirectorId)
        AppUser approvedByProjectDirector = (AppUser) appUserCacheUtility.read(purchaseRequest.approvedByProjectDirectorId)
        long companyId = procSessionUtil.appSessionUtil.getCompanyId()
        // pull system entity type(AppUser) object
        SystemEntity contentEntityTypeAppUser = (SystemEntity) contentEntityTypeCacheUtility.readByReservedAndCompany(contentEntityTypeCacheUtility.CONTENT_ENTITY_TYPE_APPUSER, companyId)

        InputStream directorImageStream = null
        InputStream pdImageStream = null
        ContentCategory imageTypeSignature = (ContentCategory) contentCategoryCacheUtility.readBySystemContentCategory(contentCategoryCacheUtility.IMAGE_TYPE_SIGNATURE)
        if (approvedByDirector && approvedByDirector.hasSignature) {
            EntityContent dirImage = entityContentService.findByEntityIdAndEntityTypeIdAndContentCategoryId(approvedByDirector.id, contentEntityTypeAppUser.id, imageTypeSignature.id)
            directorImageStream = new ByteArrayInputStream(dirImage.content)
        }
        if (approvedByProjectDirector && approvedByProjectDirector.hasSignature) {
            EntityContent pdImage = entityContentService.findByEntityIdAndEntityTypeIdAndContentCategoryId(approvedByProjectDirector.id, contentEntityTypeAppUser.id, imageTypeSignature.id)
            pdImageStream = new ByteArrayInputStream(pdImage.content)
        }

        LinkedHashMap purchaseRequestMap = [
                purchaseRequestId: purchaseRequest.id,
                numberOfItems: purchaseRequest.itemCount,
                createdOn: DateUtility.getDateFormatAsString(purchaseRequest.createdOn), // po date
                printDate: DateUtility.getDateFormatAsString(new Date()),
                createdBy: createdBy.username,
                projectName: project.name,
                projectDescription: project.description,
                comments: purchaseRequest.comments,
                directorName: approvedByDirector ? approvedByDirector.username : Tools.EMPTY_SPACE,
                directorId: approvedByDirector ? approvedByDirector.id : 0,
                pdName: approvedByProjectDirector ? approvedByProjectDirector.username : Tools.EMPTY_SPACE,
                pdId: approvedByProjectDirector ? approvedByProjectDirector.id : 0,
                directorImageStream: directorImageStream,
                pdImageStream: pdImageStream
        ]
        return purchaseRequestMap
    }

    /**
     * Build mail body
     * @param appMail - object of AppMail
     * @param parameters - params from the caller method
     */
    private void buildMailBody(AppMail appMail, Map parameters) {
        SimpleTemplateEngine engine = new SimpleTemplateEngine()
        Writable template = engine.createTemplate(appMail.body).make(parameters)
        appMail.body = template.toString()
    }

    /**
     * Give mail address list by role ids
     * @param appMail - object of AppMail
     * @param projectId - Project.id
     * @return - a map containing mail address list
     */
    private LinkedHashMap checkMail(AppMail appMail, long projectId) {
        LinkedHashMap result = new LinkedHashMap()
        result.put(Tools.IS_ERROR, Boolean.TRUE)

        String roleIds = appMail.roleIds
        List<String> lstRoles = roleIds.split(Tools.COMA)

        if (lstRoles.size() <= 0) {
            result.put(Tools.MESSAGE, ROLE_NOT_FOUND)
            return result
        }

        List<String> mailAddress = appMailService.getUserMailAddress(roleIds, projectId)
        if (mailAddress.size() <= 0) {
            result.put(Tools.MESSAGE, USER_NOT_FOUND)
            return result
        }

        result.put(MAIL_ADDRESS, mailAddress)
        result.put(Tools.IS_ERROR, Boolean.FALSE)
        return result
    }

    /**
     * process mail for sending
     */
    private void executeMail(AppMail appMail, List<String> userMailAddress, long prNo, byte[] attachment) {
        executorService.submit({
            println "Sending mail for transaction ${appMail.transactionCode} ..."
            String attachmentName = MAIL_SUBJECT_POSTFIX + prNo
            mailService.sendMail {
                multipart true
                to userMailAddress
                subject appMail.subject
                html appMail.body
                attachBytes attachmentName, MIME_PDF, attachment
            }
            println "Mail sent successfully for ${appMail.transactionCode}"
        })
    }

    private static final String QUERY_UPDATE = """
                    UPDATE proc_purchase_request SET
                          version=:newVersion,
                          sent_for_approval=:sentForApproval
                      WHERE
                          id=:id AND
                          version=:version
                          """

    /**
     * Update proc_purchase_request by setting sent_for_approval true
     * @param accIouSlip - accIouSlip object from execute
     * @return -new updated accIouSlip object
     */
    private ProcPurchaseRequest updatePurchaseRequest(ProcPurchaseRequest purchaseRequest) {
        Map queryParams = [
                id: purchaseRequest.id,
                newVersion: purchaseRequest.version + 1,
                sentForApproval: purchaseRequest.sentForApproval,
                version: purchaseRequest.version
        ]
        int updateCount = executeUpdateSql(QUERY_UPDATE, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException(ERROR_SENDING_MAIL)
        }
        purchaseRequest.version = purchaseRequest.version + 1
        return purchaseRequest
    }

}
