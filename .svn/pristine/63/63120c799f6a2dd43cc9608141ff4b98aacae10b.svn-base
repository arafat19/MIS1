package com.athena.mis.procurement.actions.purchaseorder

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppMail
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppMailService
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.application.utility.SupplierCacheUtility
import com.athena.mis.procurement.entity.ProcPurchaseOrder
import com.athena.mis.procurement.service.PurchaseOrderService
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

/**
 * Re-open PO for editing and send mail to responsible persons for notification
 * For details go through Use-Case doc named 'UnApprovePurchaseOrderActionService'
 */
class UnApprovePurchaseOrderActionService extends BaseService implements ActionIntf {

    private static final String PURCHASE_ORDER_SEND_MAIL_SUCCESS_MESSAGE = "Notification of purchase order has been sent successfully"
    private static final String NOT_FOUND_MESSAGE = "Selected purchase order not found"
    private static final String PURCHASE_ORDER_OBJ = "purchaseOrder"
    private static final String NOT_SENT_FOR_APPROVE_MSG = "Selected purchase order has not been sent for approval"
    private static final String TRANSACTION_CODE = "UnApprovePurchaseOrderActionService"
    private static final String ERROR_SENDING_MAIL = "Purchase order mail notification not send"
    private static final String MAIL_TEMPLATE_NOT_FOUND = "Purchase order mail notification not send due to absence of mail template"
    private static final String MAIL_SUBJECT_POSTFIX = "PO No: "
    private static final String ROLE_NOT_FOUND = "Role not found to send the mail"
    private static final String USER_NOT_FOUND = "User not found to send the mail"
    private static final String MAIL_ADDRESS = "mailAddress"

    private Logger log = Logger.getLogger(getClass())

    AppMailService appMailService
    MailService mailService
    ExecutorService executorService
    PurchaseOrderService purchaseOrderService
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    ProcSessionUtil procSessionUtil
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility
    @Autowired
    SupplierCacheUtility supplierCacheUtility

    /**
     * 1. get parameters from UI
     * 2. pull purchase order object
     * 3. check existence of purchase order object
     * 4. check if selected purchase order has been sent for approval or not
     * 5. update required properties of PO to re-open for editing
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            long purchaseOrderId = Long.parseLong(parameterMap.id.toString())
            // pull purchase order object
            ProcPurchaseOrder purchaseOrder = purchaseOrderService.read(purchaseOrderId)
            // check if purchase order object exists or not
            if (!purchaseOrder) {
                result.put(Tools.MESSAGE, NOT_FOUND_MESSAGE)
                return result
            }
            // check if selected purchase order has been sent for approval or not
            if (!purchaseOrder.sentForApproval) {
                result.put(Tools.MESSAGE, NOT_SENT_FOR_APPROVE_MSG)
                return result
            }
            updatePropertyToUnApprovePO(purchaseOrder)  // update required properties of PO to re-open for editing
            result.put(PURCHASE_ORDER_OBJ, purchaseOrder)
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
     * 1. update purchase order object in DB
     * 2. send mail to responsible persons
     * The method is in transactional block and will roll back in any case of exception
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            LinkedHashMap receivedResult = (LinkedHashMap) obj  // cast map returned from executePreCondition method
            ProcPurchaseOrder purchaseOrder = (ProcPurchaseOrder) receivedResult.get(PURCHASE_ORDER_OBJ)
            unApprovePurchaseOrder(purchaseOrder)
            String msg = sendMail(purchaseOrder)    // send mail
            if (msg) {
                result.put(Tools.MESSAGE, msg)
                return result
            }
            result.put(PURCHASE_ORDER_OBJ, purchaseOrder)
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
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Show newly updated purchase order object in grid
     * Show success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            ProcPurchaseOrder purchaseOrder = (ProcPurchaseOrder) receiveResult.get(PURCHASE_ORDER_OBJ)

            String createdOn = DateUtility.getDateForUI(purchaseOrder.createdOn)
            Object supplier = supplierCacheUtility.read(purchaseOrder.supplierId)

            GridEntity object = new GridEntity()
            object.id = purchaseOrder.id
            object.cell = [
                    Tools.LABEL_NEW,
                    purchaseOrder.id,
                    createdOn,
                    purchaseOrder.purchaseRequestId,
                    supplier ? supplier.name : Tools.EMPTY_SPACE,
                    purchaseOrder.itemCount,
                    Tools.makeAmountWithThousandSeparator(purchaseOrder.discount),
                    Tools.makeAmountWithThousandSeparator(purchaseOrder.totalPrice),
                    Tools.makeAmountWithThousandSeparator(purchaseOrder.trCostTotal),
                    Tools.makeAmountWithThousandSeparator(purchaseOrder.totalVatTax),
                    purchaseOrder.sentForApproval ? Tools.YES : Tools.NO,
                    purchaseOrder.approvedByDirectorId ? Tools.YES : Tools.NO,
                    purchaseOrder.approvedByProjectDirectorId ? Tools.YES : Tools.NO
            ]
            result.put(Tools.ENTITY, object)
            result.put(Tools.VERSION, purchaseOrder.version)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, PURCHASE_ORDER_SEND_MAIL_SUCCESS_MESSAGE)
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
     * 1. get mail template by transaction code
     * 2. get user email addresses
     * 3. build mail body template and subject
     * 4. send mail
     * @param purchaseOrder -object of ProcPurchaseOrder
     * @return -a string containing message or null value depending on method success
     */
    private String sendMail(ProcPurchaseOrder purchaseOrder) {
        AppMail appMail = appMailService.findByTransactionCodeAndCompanyIdAndIsActive(TRANSACTION_CODE, purchaseOrder.companyId, true)
        if (!appMail) {
            return MAIL_TEMPLATE_NOT_FOUND
        }
        LinkedHashMap chkMailMap = checkMail(appMail, purchaseOrder.projectId)
        Boolean isError = (Boolean) chkMailMap.get(Tools.IS_ERROR)
        if (isError.booleanValue()) {
            return chkMailMap.get(Tools.MESSAGE)
        }
        String strOpenedOn = DateUtility.getDateTimeFormatAsString(purchaseOrder.updatedOn)
        AppUser user = (AppUser) appUserCacheUtility.read(purchaseOrder.updatedBy)
        String openedBy = user.username
        Map paramsBody = [poId: purchaseOrder.id, poOpenedOn: strOpenedOn, poOpenedBy: openedBy]
        SimpleTemplateEngine engine = new SimpleTemplateEngine()
        Writable templateBody = engine.createTemplate(appMail.body).make(paramsBody)
        appMail.body = templateBody.toString()
        appMail.subject = appMail.subject + Tools.PARENTHESIS_START + MAIL_SUBJECT_POSTFIX + purchaseOrder.id + Tools.PARENTHESIS_END
        List<String> userMailAddress = (List<String>) chkMailMap.get(MAIL_ADDRESS)

        executeMail(appMail, userMailAddress)
        return null
    }

    /**
     * Set properties of ProcPurchaseOrder object to re-open for editing
     * @param purchaseOrder -object of ProcPurchaseOrder
     * @return -updated object of ProcPurchaseOrder
     */
    private ProcPurchaseOrder updatePropertyToUnApprovePO(ProcPurchaseOrder purchaseOrder) {
        purchaseOrder.sentForApproval = false
        purchaseOrder.approvedByDirectorId = 0
        purchaseOrder.approvedByProjectDirectorId = 0
        purchaseOrder.updatedOn = new Date()
        purchaseOrder.updatedBy = procSessionUtil.appSessionUtil.getAppUser().id
        return purchaseOrder
    }

    private static final String QUERY_UPDATE = """
        UPDATE proc_purchase_order SET
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

    /**
     * Update ProcPurchaseOrder object in DB
     * @param purchaseOrder -object of ProcPurchaseOrder
     * @return -updated object of ProcPurchaseOrder
     */
    private ProcPurchaseOrder unApprovePurchaseOrder(ProcPurchaseOrder purchaseOrder) {
        Map queryParams = [
                id: purchaseOrder.id,
                sentForApproval: purchaseOrder.sentForApproval,
                approvedByDirectorId: purchaseOrder.approvedByDirectorId,
                approvedByProjectDirectorId: purchaseOrder.approvedByProjectDirectorId,
                updatedBy: purchaseOrder.updatedBy,
                updatedOn: DateUtility.getSqlDateWithSeconds(purchaseOrder.updatedOn),
                version: purchaseOrder.version
        ]
        int updateCount = executeUpdateSql(QUERY_UPDATE, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException(ERROR_SENDING_MAIL)
        }
        purchaseOrder.version = purchaseOrder.version + 1
        return purchaseOrder
    }

    /**
     * list of user mail addresses
     * @param roleIds -a string containing the role ids of user
     * @param projectId -id of Project
     * @return -a list containing the list of user mail addresses
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
        List<String> mailAddress = getUserMailAddress(roleIds, projectId)
        if (mailAddress.size() <= 0) {
            result.put(Tools.MESSAGE, USER_NOT_FOUND)
            return result
        }
        result.put(MAIL_ADDRESS, mailAddress)
        result.put(Tools.IS_ERROR, Boolean.FALSE)
        return result
    }

    /**
     * list of user mail addresses
     * @param roleIds -a string containing the role ids of user
     * @param projectId -id of Project
     * @return -a list containing the list of user mail addresses
     */
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

    /**
     * Send mail
     * @param appMail -object of AppMail
     * @param userMailAddress -list of user mail addresses
     */
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
