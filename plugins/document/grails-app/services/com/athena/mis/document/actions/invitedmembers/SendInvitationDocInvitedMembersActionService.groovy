package com.athena.mis.document.actions.invitedmembers

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppMail
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.service.AppMailService
import com.athena.mis.document.config.DocSysConfigurationCacheUtility
import com.athena.mis.document.entity.DocCategory
import com.athena.mis.document.entity.DocInvitedMembers
import com.athena.mis.document.entity.DocInvitedMembersCategory
import com.athena.mis.document.entity.DocSubCategory
import com.athena.mis.document.service.DocAllCategoryUserMappingService
import com.athena.mis.document.service.DocInvitedMembersCategoryService
import com.athena.mis.document.service.DocInvitedMembersService
import com.athena.mis.document.utility.DocCategoryCacheUtility
import com.athena.mis.document.utility.DocSessionUtil
import com.athena.mis.document.utility.DocSubCategoryCacheUtility
import com.athena.mis.utility.Tools
import grails.plugin.mail.MailService
import grails.plugins.springsecurity.SpringSecurityService
import groovy.sql.GroovyRowResult
import groovy.text.SimpleTemplateEngine
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

import java.util.concurrent.ExecutorService

class SendInvitationDocInvitedMembersActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = 'Failed to load send invitation page'
    private static final String NOT_FOUND_MESSAGE = ' not found'
    private static
    final String TRANSACTION_CODE_FOR_CATEGORY = 'SendInvitationDocInvitedMembersActionServiceForCategory'
    private static
    final String TRANSACTION_CODE_FOR_SUB_CATEGORY = 'SendInvitationDocInvitedMembersActionServiceForSubCategory'
    private static final String MAIL_TEMPLATE_NOT_FOUND = 'Recipients template not found'
    private static final String RECIPIENTS_NOT_FOUND = 'Recipients not found'
    private static final String RECIPIENTS_LIMIT_EXCEED = 'Maximum recipients limit exceed'
    private static final String INVALID_EMAIL_PATTERN = 'One or more email address is invalid'
    private static final String LST_REJECTED_FOR_CATEGORY = 'lstRejectedForCategory'
    private static final String LST_REJECTED_FOR_SUB_CATEGORY = 'lstRejectedForSubCategory'
    private static final String APP_MAIL_OBJ = 'appMailObj'
    private static final String CATEGORY_LABEL = 'categoryLabel'
    private static final String CATEGORY_NAME = 'Category'
    private static final String CATEGORY = 'category'
    private static final String SUB_CATEGORY = 'subCategory'
    private static final String LST_SUB_CATEGORY_ID = 'lstSubCategoryId'
    private static final String LST_VALID_INVITATION = 'lstValidInvitation'
    private static final String LST_REJECTED_INVITATION = 'lstRejectedInvitation'
    private static final String MAIL_SEND_SUCCESS_MESSAGE = ' invitation successfully send to recipients'
    private static
    final String MAIL_SEND_SUCCESS_MESSAGE_EXCEPT_REJECTED = ' invitation successfully send to recipients except following list'
    private static
    final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})\$";
    private static final String PARA_START = '<p>'
    private static final String PARA_END = '</p>'
    private static final String DEFAULT_EXPIRE_TIME = '7'

    AppMailService appMailService
    DocInvitedMembersService docInvitedMembersService
    DocAllCategoryUserMappingService docAllCategoryUserMappingService
    ExecutorService executorService
    MailService mailService
    SpringSecurityService springSecurityService
    LinkGenerator grailsLinkGenerator
    DocInvitedMembersCategoryService docInvitedMembersCategoryService

    @Autowired
    DocCategoryCacheUtility docCategoryCacheUtility
    @Autowired
    DocSubCategoryCacheUtility docSubCategoryCacheUtility
    @Autowired
    DocSysConfigurationCacheUtility docSysConfigurationCacheUtility
    @Autowired
    DocSessionUtil docSessionUtil

    /**
     * 1. Get parameters from UI
     * 2. category label from system configuration
     * 3. check category existence
     * 4. check AppMail template existence
     * 5. check recipients, pattern, limit & existence of this recipients
     * 6. get list of all combination of invitation with category/sub-category
     * 7. get list of invited recipients,list of mapped recipients,list of rejected recipients
     * 8. get list of valid invitation for send invitation
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all object necessary for execute
     * Ths method is in Transactional boundary so will rollback in case of any exception
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        String categoryLabel = CATEGORY_NAME
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters

            long companyId = docSessionUtil.appSessionUtil.companyId
            SysConfiguration sysConfiguration = docSysConfigurationCacheUtility.readByKeyAndCompanyId(docSysConfigurationCacheUtility.DOC_CATEGORY_LABEL, companyId)
            if (sysConfiguration) {
                categoryLabel = sysConfiguration.value
            }
            long categoryId = Long.parseLong(params.categoryId.toString())
            DocCategory category = (DocCategory) docCategoryCacheUtility.read(categoryId)
            if (!category) {
                result.put(Tools.MESSAGE, categoryLabel + NOT_FOUND_MESSAGE)
                return result
            }

            AppMail appMail = appMailService.findByTransactionCodeAndCompanyIdAndIsActive(TRANSACTION_CODE_FOR_CATEGORY, companyId, true)
            if (!appMail) {
                result.put(Tools.MESSAGE, MAIL_TEMPLATE_NOT_FOUND)
                return result
            }

            String emailsString = params.recipients.toString()
            List<String> emails = emailsString.split(Tools.COMA)
            List<String> recipients = []
            for (int i = 0; i < emails.size(); i++) {
                String email = emails[i].toString().trim()
                if (!email.matches(EMAIL_PATTERN)) {
                    result.put(Tools.MESSAGE, INVALID_EMAIL_PATTERN)
                    return result
                }
                recipients.add(email)
            }
            if (recipients.size() <= 0) {
                result.put(Tools.MESSAGE, RECIPIENTS_NOT_FOUND)
                return result
            }
            if (recipients.size() > 10) {
                result.put(Tools.MESSAGE, RECIPIENTS_LIMIT_EXCEED)
                return result
            }
            List subCategoryIds = []
            def subCat = params.subCategoryId
            if (!subCat) {
                subCategoryIds = []
            } else if (subCat instanceof String[]) {
                subCategoryIds = subCat
            } else {
                subCategoryIds << subCat
            }
            List<Long> lstSubCategoryId = []
            for (String value : subCategoryIds) {
                lstSubCategoryId.add(Long.parseLong(value));
            }
            List lstInvited = []
            List lstMapped = []
            List lstAll = []
            List lstRejected = []
            List lstValidInvitation = []
            if (lstSubCategoryId.size() > 0) {
                lstAll = listAllForSubCategoryAndRecipients(lstSubCategoryId, recipients)
                lstInvited = listInvitedSubCategoryAndRecipients(categoryId, lstSubCategoryId, recipients)
                lstMapped = listMappedSubCategoryAndRecipients(categoryId, lstSubCategoryId, recipients)
                lstRejected = lstInvited + lstMapped
                lstRejected = lstRejected.unique()
                lstValidInvitation = lstAll - lstRejected
            } else {
                lstAll = listAllForCategoryAndRecipients(categoryId, recipients)
                lstInvited = listInvitedCategoryAndRecipients(categoryId, recipients)
                lstMapped = listMappedCategoryAndRecipients(categoryId, recipients)
                lstRejected = lstInvited + lstMapped
                lstRejected = lstRejected.unique()
                lstValidInvitation = lstAll - lstRejected
            }
            result.put(CATEGORY, category)
            result.put(LST_SUB_CATEGORY_ID, lstSubCategoryId)
            result.put(LST_VALID_INVITATION, lstValidInvitation)
            result.put(LST_REJECTED_INVITATION, lstRejected)
            result.put(APP_MAIL_OBJ, appMail)
            result.put(CATEGORY_LABEL, categoryLabel)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            throw new RuntimeException(DEFAULT_ERROR_MESSAGE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }


    public Object executePostCondition(Object parameters, Object obj) {
        //Do nothing for post-operation
        return null

    }

    /*
    * @param parameters - N/A
    * @param obj - a map from precondition
    * Send mail to recipients
    * Insert invited member info to DB
    * @return - A map containing error/success for buildSuccessResultForUI
    * Ths method is in Transactional boundary so will rollback in case of any exception
    * */

    @Transactional
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map preResult = (Map) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            String categoryLabel = preResult.get(CATEGORY_LABEL)
            List<Long> lstSubCategoryId = (List<Long>) preResult.get(LST_SUB_CATEGORY_ID)
            List lstValidInvitation = (List) preResult.get(LST_VALID_INVITATION)
            List lstRejected = (List) preResult.get(LST_REJECTED_INVITATION)
            List<Map> lstRejectedForSubCategory = []
            List<Map> lstRejectedForCategory = []
            if (lstSubCategoryId.size() > 0) {
                lstRejectedForSubCategory = listRejectedRecipientForSubCategory(lstRejected)
                result.put(LST_REJECTED_FOR_SUB_CATEGORY, lstRejectedForSubCategory)
            } else {
                lstRejectedForCategory = listRejectedRecipientForCategory(lstRejected)
                result.put(LST_REJECTED_FOR_CATEGORY, lstRejectedForCategory)
            }

            AppMail appMailForCategory = (AppMail) preResult.get(APP_MAIL_OBJ)
            DocCategory category = (DocCategory) preResult.get(CATEGORY)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            String message = params.message.toString()

            AppMail appMailForSubCategory = appMailService.findByTransactionCodeAndCompanyIdAndIsActive(TRANSACTION_CODE_FOR_SUB_CATEGORY, category.companyId, true)
            if (!appMailForSubCategory) {
                result.put(Tools.MESSAGE, MAIL_TEMPLATE_NOT_FOUND)
                return result
            }

            if (lstSubCategoryId.size() > 0) {
                List<DocInvitedMembers> lstDocInvitedMemberForSubCategory = buildDocInvitedMembersForSubCategory(lstValidInvitation, message)
                for (int i = 0; i < lstDocInvitedMemberForSubCategory.size(); i++) {
                    DocInvitedMembers docInvitedMembers = lstDocInvitedMemberForSubCategory[i]
                    DocInvitedMembers newDocInvitedMembers = docInvitedMembersService.create(docInvitedMembers)
                    List<DocInvitedMembersCategory> lstDocInvitedMembersCategory = buildDocInvitedMembersCategoryForSubCategory(newDocInvitedMembers, category, lstValidInvitation)
                    for (int j = 0; j < lstDocInvitedMembersCategory.size(); j++) {
                        DocInvitedMembersCategory docInvitedMembersCategory = lstDocInvitedMembersCategory[j]
                        docInvitedMembersCategoryService.create(docInvitedMembersCategory)
                    }
                }
                executeMailForSubCategory(categoryLabel, appMailForSubCategory, params, category, lstDocInvitedMemberForSubCategory, lstValidInvitation)
            } else {
                List<DocInvitedMembers> lstDocInvitedMemberForCategory = buildDocInvitedMembersForCategory(lstValidInvitation, message)
                for (int i = 0; i < lstDocInvitedMemberForCategory.size(); i++) {
                    DocInvitedMembers docInvitedMembers = lstDocInvitedMemberForCategory[i]
                    DocInvitedMembers newDocInvitedMembers = docInvitedMembersService.create(docInvitedMembers)

                    DocInvitedMembersCategory docInvitedMembersCategory = buildDocInvitedMembersCategoryForCategory(newDocInvitedMembers, category)
                    docInvitedMembersCategoryService.create(docInvitedMembersCategory)
                }
                executeMailForCategory(categoryLabel, appMailForCategory, params, category, lstDocInvitedMemberForCategory)
            }
            result.put(LST_VALID_INVITATION, lstValidInvitation)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(DEFAULT_ERROR_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }

    }

    /*
    * Build Success Results for UI
    * @params obj - Map return from execute method
    * @return a map of containing all object necessary for show page
    * */

    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map preResult = (Map) obj
            List<Map> lstRejectedForCategory = (List<Map>) preResult.get(LST_REJECTED_FOR_CATEGORY)
            List<Map> lstRejectedForSubCategory = (List<Map>) preResult.get(LST_REJECTED_FOR_SUB_CATEGORY)
            List<Map> lstValidInvitation = (List<Map>) preResult.get(LST_VALID_INVITATION)
            int validInvitationsSize = lstValidInvitation.size()

            if (lstRejectedForCategory != null && lstRejectedForCategory.size() > 0) {
                result.put(LST_REJECTED_FOR_CATEGORY, lstRejectedForCategory)
                result.put(Tools.MESSAGE, validInvitationsSize + MAIL_SEND_SUCCESS_MESSAGE_EXCEPT_REJECTED)
                result.put(Tools.IS_ERROR, Boolean.FALSE)
                return result
            } else if (lstRejectedForCategory != null && lstRejectedForCategory.size() == 0) {
                result.put(Tools.MESSAGE, validInvitationsSize + MAIL_SEND_SUCCESS_MESSAGE)
                result.put(Tools.IS_ERROR, Boolean.FALSE)
                return result
            }

            if (lstRejectedForSubCategory != null && lstRejectedForSubCategory.size() > 0) {
                result.put(LST_REJECTED_FOR_SUB_CATEGORY, lstRejectedForSubCategory)
                result.put(Tools.MESSAGE, validInvitationsSize + MAIL_SEND_SUCCESS_MESSAGE_EXCEPT_REJECTED)
                result.put(Tools.IS_ERROR, Boolean.FALSE)
                return result
            } else if (lstRejectedForSubCategory != null && lstRejectedForSubCategory.size() == 0) {
                result.put(Tools.MESSAGE, validInvitationsSize + MAIL_SEND_SUCCESS_MESSAGE)
            }

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result

        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    /*
    * Build Failure result for UI
    * @params obj - A map from execute method
    * @return a Map containing IsError and default error message/relevant error message to display
    * */

    public Object buildFailureResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map preResult = (Map) obj
            if (obj) {
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result

        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /*
    * Build list of invited member
    * @param lstValidInvitation - List of valid Invitation
    * @param message - Custom Message
    * @return lstDocInvitedMember
    * */

    private List<DocInvitedMembers> buildDocInvitedMembersForCategory(List lstValidInvitation, String message) {
        String expiredOn
        long companyId = docSessionUtil.appSessionUtil.getCompanyId()
        SysConfiguration sysConfiguration = docSysConfigurationCacheUtility.readByKeyAndCompanyId(docSysConfigurationCacheUtility.DOC_EXPIRATION_INVITED_MEMBER, companyId)
        if (sysConfiguration) {
            expiredOn = sysConfiguration.value
        }

        List lstUniqueValidEmail = []
        lstValidInvitation.each {
            lstUniqueValidEmail << it[1]
        }
        lstUniqueValidEmail = lstUniqueValidEmail.unique()

        List<DocInvitedMembers> lstDocInvitedMember = []
        long timeInMillSec = new Date().getTime()
        for (int i = 0; i < lstUniqueValidEmail.size(); i++) {
            DocInvitedMembers docInvitedMembers = new DocInvitedMembers()
            docInvitedMembers.email = lstUniqueValidEmail[i]
            docInvitedMembers.invitationSentOn = new Date()
            docInvitedMembers.invitationAcceptedOn = null
            docInvitedMembers.invitationCode = springSecurityService.encodePassword((timeInMillSec + i).toString())
            docInvitedMembers.invitationLink = grailsLinkGenerator.link(controller: 'docCategory', action: 'showAcceptInvitation', absolute: true, params: [code: docInvitedMembers.invitationCode])
            docInvitedMembers.expiredOn = new Date() + Integer.parseInt(expiredOn.toString())
            docInvitedMembers.message = message
            docInvitedMembers.companyId = companyId
            docInvitedMembers.createdBy = docSessionUtil.appSessionUtil.appUser.id
            docInvitedMembers.resendBy = 0L
            lstDocInvitedMember << docInvitedMembers
        }
        return lstDocInvitedMember
    }

    /*
   * Build DocInvitedMembersCategory for category
   * @param docInvitedMembers - DocInvitedMembers Object
   * @param category - DocCategory object
   * @return docInvitedMembersCategory - DocInvitedMembersCategory object
   * */

    private DocInvitedMembersCategory buildDocInvitedMembersCategoryForCategory(DocInvitedMembers docInvitedMembers, DocCategory category) {

        DocInvitedMembersCategory docInvitedMembersCategory = new DocInvitedMembersCategory()
        docInvitedMembersCategory.invitedMemberId = docInvitedMembers.id
        docInvitedMembersCategory.categoryId = category.id
        docInvitedMembersCategory.subCategoryId = 0

        return docInvitedMembersCategory
    }

    /*
    * Build list of invited member for Sub-category
    * @param lstValidInvitation - list of valid invitation
    * @param message - custom message for invitation
    * @return lstDocInvitedMember
    * */

    private List<DocInvitedMembers> buildDocInvitedMembersForSubCategory(List lstValidInvitation, String message) {
        String expiredOn
        long companyId = docSessionUtil.appSessionUtil.getCompanyId()
        SysConfiguration sysConfiguration = docSysConfigurationCacheUtility.readByKeyAndCompanyId(docSysConfigurationCacheUtility.DOC_EXPIRATION_INVITED_MEMBER, companyId)
        if (sysConfiguration) {
            expiredOn = sysConfiguration.value
        }
        List lstUniqueValidEmail = []
        lstValidInvitation.each {
            lstUniqueValidEmail << it[1]
        }
        lstUniqueValidEmail = lstUniqueValidEmail.unique()

        List<DocInvitedMembers> lstDocInvitedMember = []
        long timeInMillSec = new Date().getTime()
        for (int i = 0; i < lstUniqueValidEmail.size(); i++) {
            DocInvitedMembers docInvitedMembers = new DocInvitedMembers()
            docInvitedMembers.email = lstUniqueValidEmail[i]
            docInvitedMembers.invitationSentOn = new Date()
            docInvitedMembers.invitationAcceptedOn = null
            docInvitedMembers.invitationCode = springSecurityService.encodePassword((timeInMillSec + i).toString())
            docInvitedMembers.invitationLink = grailsLinkGenerator.link(controller: 'docCategory', action: 'showAcceptInvitation', absolute: true, params: [code: docInvitedMembers.invitationCode])
            docInvitedMembers.message = message
            docInvitedMembers.expiredOn = new Date() + Integer.parseInt(expiredOn.toString())
            docInvitedMembers.companyId = companyId
            docInvitedMembers.createdBy = docSessionUtil.appSessionUtil.appUser.id
            docInvitedMembers.resendBy = 0L
            lstDocInvitedMember << docInvitedMembers
        }
        return lstDocInvitedMember
    }

    /*
    * Build list of DocInvitedMembersCategory for Sub-category
    * @param docInvitedMembers - DocInvitedMembers object
    * @param category - DocCategory object
    * @param lstValidInvitation - List of valid Invitation
    * @return lstDocInvitedMember
    * */

    private List<DocInvitedMembersCategory> buildDocInvitedMembersCategoryForSubCategory(DocInvitedMembers docInvitedMembers, DocCategory category, List lstValidInvitation) {
        List lstUniqueValidSubCategoryId = []
        lstValidInvitation.each {
            lstUniqueValidSubCategoryId << it[0]
        }
        lstUniqueValidSubCategoryId = lstUniqueValidSubCategoryId.unique()

        List<DocInvitedMembersCategory> lstInvitedMembersCategory = []
        for (int i = 0; i < lstUniqueValidSubCategoryId.size(); i++) {
            DocInvitedMembersCategory docInvitedMembersCategory = new DocInvitedMembersCategory()
            docInvitedMembersCategory.invitedMemberId = docInvitedMembers.id
            docInvitedMembersCategory.categoryId = category.id
            docInvitedMembersCategory.subCategoryId = Long.parseLong(lstUniqueValidSubCategoryId[i].toString())
            lstInvitedMembersCategory << docInvitedMembersCategory
        }
        return lstInvitedMembersCategory
    }

    /**
     * process mail sending for sub-category
     * @param categoryLabel
     * @param appMail - AppMail Object
     * @param parameterMap
     * @param category object
     * @param lstDocInvitedMember - List of DocInvitedMembers
     * @param lstValidInvitation - List of Valid Invitation
     */
    private void executeMailForSubCategory(String categoryLabel, AppMail appMail, GrailsParameterMap parameterMap, DocCategory category, List<DocInvitedMembers> lstDocInvitedMember, List lstValidInvitation) {
        String message = parameterMap.message.toString()

        String subCategoryLabel = SUB_CATEGORY
        long companyId = docSessionUtil.appSessionUtil.companyId
        SysConfiguration sysConfiguration = docSysConfigurationCacheUtility.readByKeyAndCompanyId(docSysConfigurationCacheUtility.DOC_SUB_CATEGORY_LABEL, companyId)
        if (sysConfiguration) {
            subCategoryLabel = sysConfiguration.value
        }
        String expireTime = DEFAULT_EXPIRE_TIME
        SysConfiguration sysConfiguration1 = docSysConfigurationCacheUtility.readByKeyAndCompanyId(docSysConfigurationCacheUtility.DOC_EXPIRATION_INVITED_MEMBER, companyId)
        if (sysConfiguration1) {
            expireTime = sysConfiguration1.value
        }

        for (int i = 0; i < lstDocInvitedMember.size(); i++) {
            DocInvitedMembers docInvitedMember = lstDocInvitedMember[i]
            String subCategoryDetails = buildSubCategoryDetails(docInvitedMember, lstValidInvitation)
            Map mailBodyParams = [message: message, categoryLabel: categoryLabel, categoryName: category.name,
                    categoryDescription: category.description ? category.description : Tools.EMPTY_SPACE, categoryUrl: category.url, invitationLink: docInvitedMember.invitationLink,
                    subCategoryLabel: subCategoryLabel, subCategoryDetails: subCategoryDetails, expireTime: expireTime]
            AppMail mail = new AppMail()
            mail.properties = appMail.properties
            buildMailBody(mail, mailBodyParams)

            String recipient = docInvitedMember.email
            executorService.submit({
                println "Sending mail for transaction ${appMail.transactionCode} ..."
                mailService.sendMail {
                    to recipient
                    subject mail.subject
                    html mail.body
                }
                println "Mail sent successfully for ${appMail.transactionCode}"
            })
        }
    }

    /**
     * process mail sending for category
     * @param categoryLabel
     * @param appMail - AppMail Object
     * @param parameterMap - serialize parameter from UI
     * @param category object
     * @param lstDocInvitedMember - List of DocInvitedMembers
     */
    private void executeMailForCategory(String categoryLabel, AppMail appMail, GrailsParameterMap parameterMap, DocCategory category, List<DocInvitedMembers> lstDocInvitedMember) {
        String message = parameterMap.message.toString()

        long companyId = docSessionUtil.appSessionUtil.companyId
        String expireTime = DEFAULT_EXPIRE_TIME
        SysConfiguration sysConfiguration1 = docSysConfigurationCacheUtility.readByKeyAndCompanyId(docSysConfigurationCacheUtility.DOC_EXPIRATION_INVITED_MEMBER, companyId)
        if (sysConfiguration1) {
            expireTime = sysConfiguration1.value
        }

        for (int i = 0; i < lstDocInvitedMember.size(); i++) {
            DocInvitedMembers docInvitedMember = lstDocInvitedMember[i]
            Map mailBodyParams = [message: message, categoryLabel: categoryLabel, categoryName: category.name,
                    categoryDescription: category.description ? category.description : Tools.EMPTY_SPACE, invitationLink: docInvitedMember.invitationLink, expireTime: expireTime]
            AppMail mail = new AppMail()
            mail.properties = appMail.properties
            buildMailBody(mail, mailBodyParams)

            String recipient = docInvitedMember.email
            executorService.submit({
                println "Sending mail for transaction ${appMail.transactionCode} ..."
                mailService.sendMail {
                    to recipient
                    subject mail.subject
                    html mail.body
                }
                println "Mail sent successfully for ${appMail.transactionCode}"
            })
        }
    }

    /*
    * Build Sub-Category Details for mail
    * @param docInvitedMembers object
    * @param lstValidInvitation - List of Valid invitation
    * @return subCategoryDetails
    * */

    private String buildSubCategoryDetails(DocInvitedMembers docInvitedMember, List lstValidInvitation) {
        String subCategoryDetails = PARA_START
        for (int j = 0; j < lstValidInvitation.size(); j++) {
            Long subCategoryId = (Long) lstValidInvitation[j][0]
            String invitedEmail = (String) lstValidInvitation[j][1]
            if (invitedEmail.equals(docInvitedMember.email)) {
                DocSubCategory subCategory = (DocSubCategory) docSubCategoryCacheUtility.read(subCategoryId)
                subCategoryDetails += """
                                         Name : ${subCategory.name} <br>
                                         Description : ${
                    subCategory.description ? subCategory.description : Tools.EMPTY_SPACE
                }<br><br>
                                      """
            }
        }
        subCategoryDetails += PARA_END
        return subCategoryDetails
    }

    /**
     * Build mail body
     * @param appMail - object of AppMail
     * @param parameters - params from the caller method
     */
    private void buildMailBody(AppMail appMail, Map parameters) {
        SimpleTemplateEngine engine = new SimpleTemplateEngine()
        Writable templateSubject = engine.createTemplate(appMail.subject).make(parameters)
        Writable templateBody = engine.createTemplate(appMail.body).make(parameters)
        appMail.subject = templateSubject.toString()
        appMail.body = templateBody.toString()
    }


    private List<Map> listRejectedRecipientForCategory(List listRejectedRecipient) {
        List<Map> listRejectedRecipients = []
        for (int i = 0; i < listRejectedRecipient.size(); i++) {
            Map failureRecipient = [
                    rejectedMailForCategory: listRejectedRecipient[i][1]
            ]
            listRejectedRecipients << failureRecipient
        }
        return listRejectedRecipients
    }

    private List<Map> listRejectedRecipientForSubCategory(List listRejectedRecipient) {
        List<Map> listRejectedRecipients = []
        for (int i = 0; i < listRejectedRecipient.size(); i++) {
            long id = Long.parseLong(listRejectedRecipient[i][0].toString())
            DocSubCategory subCategory = (DocSubCategory) docSubCategoryCacheUtility.read(id)
            Map failureRecipient = [
                    rejectedMailForSubCategory: listRejectedRecipient[i][1],
                    subCategoryName: subCategory.name
            ]
            listRejectedRecipients << failureRecipient
        }
        return listRejectedRecipients
    }

    private List listAllForSubCategoryAndRecipients(List<Long> lstSubCategoryId, List<String> recipients) {
        List mainList = [lstSubCategoryId, recipients].combinations()
        return mainList
    }

    private List listInvitedSubCategoryAndRecipients(long categoryId, List<Long> lstSubCategoryId, List<String> recipients) {
        List invitedList = []
        String lstSubCategoryIds = Tools.buildCommaSeparatedStringOfIds(lstSubCategoryId)
        String lstRecipient = buildCommaSeparatedStringOfRecipients(recipients)
        String query = """
                        SELECT dimc.sub_category_id,dim.email FROM doc_invited_members_category dimc
                        LEFT JOIN doc_invited_members dim ON dim.id=dimc.invited_member_id
                        WHERE dimc.category_id=${categoryId}
                        AND dimc.sub_category_id IN (${lstSubCategoryIds})
                        AND dim.email IN (${lstRecipient})
                        AND dim.expired_on >= '${new Date()}'
                        """

        List<GroovyRowResult> lstDocInvitedMember = executeSelectSql(query)
        for (int i = 0; i < lstDocInvitedMember.size(); i++) {
            invitedList << [lstDocInvitedMember[i].sub_category_id, lstDocInvitedMember[i].email]
        }
        return invitedList
    }

    private List listMappedSubCategoryAndRecipients(long categoryId, List<Long> lstSubCategoryId, List<String> recipients) {
        List mappedList = []
        String lstSubCategoryIds = Tools.buildCommaSeparatedStringOfIds(lstSubCategoryId)
        String lstRecipient = buildCommaSeparatedStringOfRecipients(recipients)
        String query = """
                        SELECT dacm.sub_category_id,au.login_id FROM doc_all_category_user_mapping dacm
                        LEFT JOIN app_user au ON au.id=dacm.user_id
                        WHERE dacm.category_id=${categoryId}
                        AND dacm.sub_category_id IN (${lstSubCategoryIds})
                        AND dacm.user_id IN
                        (SELECT id FROM app_user WHERE login_id IN (${lstRecipient}))
                        """

        List<GroovyRowResult> lstMappedMember = executeSelectSql(query)
        for (int i = 0; i < lstMappedMember.size(); i++) {
            mappedList << [lstMappedMember[i].sub_category_id, lstMappedMember[i].login_id]
        }
        return mappedList
    }

    private List listAllForCategoryAndRecipients(long categoryId, List<String> recipients) {
        List mainList = [categoryId, recipients].combinations()
        return mainList
    }

    private List listInvitedCategoryAndRecipients(long categoryId, List<String> recipients) {
        List invitedList = []
        String lstRecipient = buildCommaSeparatedStringOfRecipients(recipients)
        String query = """
                        SELECT dimc.category_id,dim.email FROM doc_invited_members_category dimc
                        LEFT JOIN doc_invited_members dim ON dim.id=dimc.invited_member_id
                        WHERE dimc.category_id=${categoryId}
                        AND dimc.sub_category_id = 0
                        AND dim.email IN (${lstRecipient})
                        AND dim.expired_on >= '${new Date()}'
                        """

        List<GroovyRowResult> lstDocInvitedMember = executeSelectSql(query)
        for (int i = 0; i < lstDocInvitedMember.size(); i++) {
            invitedList << [lstDocInvitedMember[i].category_id, lstDocInvitedMember[i].email]
        }
        return invitedList
    }

    private List listMappedCategoryAndRecipients(long categoryId, List<String> recipients) {
        List mappedList = []
        String lstRecipient = buildCommaSeparatedStringOfRecipients(recipients)
        String query = """
                        SELECT dacm.category_id,au.login_id FROM doc_all_category_user_mapping dacm
                        LEFT JOIN app_user au ON au.id=dacm.user_id
                        WHERE dacm.category_id=${categoryId}
                        AND dacm.sub_category_id = 0
                        AND dacm.user_id IN
                        (SELECT id FROM app_user WHERE login_id IN (${lstRecipient}))
                        """

        List<GroovyRowResult> lstMappedMember = executeSelectSql(query)
        for (int i = 0; i < lstMappedMember.size(); i++) {
            mappedList << [lstMappedMember[i].category_id, lstMappedMember[i].login_id]
        }
        return mappedList
    }


    public static String buildCommaSeparatedStringOfRecipients(List<String> recipients) {
        String strIds = ""
        for (int i = 0; i < recipients.size(); i++) {
            if ((i + 1) < recipients.size()) strIds += Tools.SINGLE_QUOTE + recipients[i] + Tools.SINGLE_QUOTE + Tools.COMA
            else strIds += Tools.SINGLE_QUOTE + recipients[i] + Tools.SINGLE_QUOTE
        }
        return strIds
    }


}
