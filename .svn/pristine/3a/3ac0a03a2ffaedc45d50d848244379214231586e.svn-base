package com.athena.mis.procurement.actions.purchaserequest

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppMail
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Project
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppMailService
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.application.utility.UnitCacheUtility
import com.athena.mis.integration.budget.BudgetPluginConnector
import com.athena.mis.procurement.entity.ProcPurchaseRequest
import com.athena.mis.procurement.service.PurchaseRequestService
import com.athena.mis.procurement.utility.ProcSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.plugin.mail.MailService
import groovy.sql.GroovyRowResult
import groovy.text.SimpleTemplateEngine
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

import java.util.concurrent.ExecutorService

class UnApprovePurchaseRequestActionService extends BaseService implements ActionIntf {

    private static final String PURCHASE_REQUEST_SEND_MAIL_SUCCESS_MESSAGE = "Notification of purchase request has been sent successfully"
    private static final String PURCHASE_REQUEST_SEND_MAIL_FAILURE_MESSAGE = "Notification of purchase request has not sent"
    private static final String PURCHASE_REQUEST_NOT_FOUND = "Purchase request not found."
    private static final String NOT_SENT_FOR_APPROVE_MSG = "Selected purchase request has not been sent for approval"
    private static final String PURCHASE_REQUEST_OBJ = "purchaseRequest"
    private static final String IS_APPROVED = "isApproved"
    private static final String SERVER_ERROR_MESSAGE = "Fail to update purchase request"
    private static final String TRANSACTION_CODE = "UnApprovePurchaseRequestActionService"
    private static final String ERROR_SENDING_MAIL = "Purchase request mail notification not send"
    private static final String MAIL_TEMPLATE_NOT_FOUND = "Purchase request mail notification not send due to absence of mail template"
    private static final String MAIL_SUBJECT_POSTFIX = "PR No: "
    private static final String ROLE_NOT_FOUND = "Role not found to send the mail"
    private static final String USER_NOT_FOUND = "User not found to send the mail"
    private static final String MAIL_ADDRESS = "mailAddress"

    private final Logger log = Logger.getLogger(getClass())

    PurchaseRequestService purchaseRequestService
    AppMailService appMailService
    MailService mailService
    ExecutorService executorService
    @Autowired
    ProcSessionUtil procSessionUtil
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility
    @Autowired
    UnitCacheUtility unitCacheUtility
    @Autowired(required = false)
    BudgetPluginConnector budgetImplService
    /**
     * Get parameters from UI and build purchase request object for update
     * @param parameters - serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(IS_APPROVED, Boolean.FALSE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params

            long id = Long.parseLong(parameterMap.id.toString())
            ProcPurchaseRequest purchaseRequest = purchaseRequestService.read(id)

            // check if purchase request object exists or not
            if (!purchaseRequest) {
                result.put(Tools.MESSAGE, PURCHASE_REQUEST_NOT_FOUND)
                return result
            }
            // check if selected purchase request has been sent for approval or not
            if (!purchaseRequest.sentForApproval) {
                result.put(Tools.MESSAGE, NOT_SENT_FOR_APPROVE_MSG)
                return result
            }
            updatePropertyToUnApprovePR(purchaseRequest)

            result.put(PURCHASE_REQUEST_OBJ, purchaseRequest)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            return result
        }
    }
    /**
     * Update Purchase request object in DB
     * This function is in transactional boundary and will roll back in case of any exception
     * @param parameters -N/A
     * @param obj -object receive from pre execute method
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            ProcPurchaseRequest purchaseRequest = (ProcPurchaseRequest) preResult.get(PURCHASE_REQUEST_OBJ)
            unApprovePurchaseRequest(purchaseRequest)

            AppMail appMail = appMailService.findByTransactionCodeAndCompanyIdAndIsActive(TRANSACTION_CODE, purchaseRequest.companyId, true)
            if (!appMail) {
                result.put(Tools.MESSAGE, MAIL_TEMPLATE_NOT_FOUND)
                return result
            }
            LinkedHashMap chkMailMap = checkMail(appMail, purchaseRequest.projectId)
            Boolean isError = (Boolean) chkMailMap.get(Tools.IS_ERROR)
            if (isError.booleanValue()) {
                result.put(Tools.MESSAGE, chkMailMap.get(Tools.MESSAGE))
                return Boolean.FALSE
            }
            String strOpenedOn = DateUtility.getDateTimeFormatAsString(purchaseRequest.updatedOn)
            AppUser user = (AppUser) appUserCacheUtility.read(purchaseRequest.updatedBy)
            String openedBy = user.username
            Map paramsBody = [prId: purchaseRequest.id, prOpenedOn: strOpenedOn, prOpenedBy: openedBy]
            SimpleTemplateEngine engine = new SimpleTemplateEngine()
            Writable templateBody = engine.createTemplate(appMail.body).make(paramsBody)
            appMail.body = templateBody.toString()
            appMail.subject = appMail.subject + Tools.PARENTHESIS_START + MAIL_SUBJECT_POSTFIX + purchaseRequest.id + Tools.PARENTHESIS_END
            List<String> userMailAddress = (List<String>) chkMailMap.get(MAIL_ADDRESS)

            executeMail(appMail, userMailAddress)

            result.put(PURCHASE_REQUEST_OBJ, purchaseRequest)
            result.put(IS_APPROVED, preResult.get(IS_APPROVED))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(ERROR_SENDING_MAIL)
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
     * 1. Show newly created pr in grid
     * 2. Wrap grid object
     * 3. Show success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary for grid view
     * -map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            String approvedByDirector
            String approvedByProjectDirector
            String isSentForApproval
            ProcPurchaseRequest purchaseRequestInstance = (ProcPurchaseRequest) receiveResult.get(PURCHASE_REQUEST_OBJ)
            GridEntity object = new GridEntity()
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
            result.put(Tools.MESSAGE, PURCHASE_REQUEST_SEND_MAIL_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
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
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, PURCHASE_REQUEST_SEND_MAIL_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            return result
        }
    }
    /**
     * Get purchase request object
     * @param purchaseRequest - purchase request object
     * @return - newly created purchase request object
     */
    private ProcPurchaseRequest updatePropertyToUnApprovePR(ProcPurchaseRequest purchaseRequest) {
            purchaseRequest.sentForApproval = false
            purchaseRequest.approvedByDirectorId = 0
            purchaseRequest.approvedByProjectDirectorId = 0
            purchaseRequest.updatedOn = new Date()
            purchaseRequest.updatedBy = procSessionUtil.appSessionUtil.getAppUser().id
        return purchaseRequest
    }

    private static final String QUERY_UPDATE = """
        UPDATE proc_purchase_request SET
            version=version+1,
            sent_for_approval=:sentForApproval,
            approved_by_director_id=:approvedByDirectorId,
            approved_by_project_director_id=:approvedByProjectDirectorId,
            updated_by=:updatedBy,
            updated_on=:updatedOn
        WHERE
            id=:id AND
            version=:version
    """

    private ProcPurchaseRequest unApprovePurchaseRequest(ProcPurchaseRequest purchaseRequest) {
        Map queryParams = [
                id: purchaseRequest.id,
                sentForApproval: purchaseRequest.sentForApproval,
                approvedByDirectorId: purchaseRequest.approvedByDirectorId,
                approvedByProjectDirectorId: purchaseRequest.approvedByProjectDirectorId,
                updatedBy: purchaseRequest.updatedBy,
                updatedOn: DateUtility.getSqlDate(purchaseRequest.updatedOn),
                version: purchaseRequest.version
        ]
        int updateCount = executeUpdateSql(QUERY_UPDATE, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException(ERROR_SENDING_MAIL)
        }
        purchaseRequest.version = purchaseRequest.version + 1
        return purchaseRequest
    }

    private LinkedHashMap checkMail(AppMail appMail, long projectId) {
        LinkedHashMap result = new LinkedHashMap()
        result.put(Tools.IS_ERROR, Boolean.TRUE)
        String roleIds = appMail.roleIds
        List<String> lstRoles = roleIds.split(Tools.COMA)
        if (lstRoles.size() <= 0) {
            result.put(Tools.MESSAGE, ROLE_NOT_FOUND)
            return result
        }
        List<String> mailAddress = getUserMailAddress(roleIds, projectId)
        if (mailAddress.size() <= 0) {
            result.put(Tools.MESSAGE, USER_NOT_FOUND)
            return result
        }
        result.put(MAIL_ADDRESS, mailAddress)
        result.put(Tools.IS_ERROR, Boolean.FALSE)
        return result
    }

    public List<String> getUserMailAddress(String roleIds, long projectId) {
        long companyId = procSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity appUserSysEntityObject = (SystemEntity) appUserEntityTypeCacheUtility.readByReservedAndCompany(appUserEntityTypeCacheUtility.PROJECT, companyId)

        String query = """
            SELECT au.login_id FROM app_user_entity app
            LEFT JOIN app_user au ON au.id= app.app_user_id
                WHERE app.entity_id = :projectId AND
                      app.entity_type_id = :project AND
                      app.app_user_id IN
                          (SELECT ur.user_id FROM user_role ur
                                WHERE ur.role_id IN
                                    (SELECT id FROM role WHERE role_type_id IN (${roleIds})
                                        AND company_id = :companyId))
        """

        Map queryParams = [
                projectId: projectId,
                project: appUserSysEntityObject.id,
                companyId: companyId
        ]
        List<GroovyRowResult> result = executeSelectSql(query, queryParams)

        List<String> userMailAddress = []
        for (int i = 0; i < result.size(); i++) {
            userMailAddress << result[i][0]
        }
        return userMailAddress
    }

    private void executeMail(AppMail appMail, List<String> userMailAddress) {
        executorService.submit({
            println "Sending mail for transaction ${appMail.transactionCode} ..."
            mailService.sendMail {
                to userMailAddress
                subject appMail.subject
                html appMail.body
            }
            println "Mail sent successfully for ${appMail.transactionCode}"
        })
    }
}
