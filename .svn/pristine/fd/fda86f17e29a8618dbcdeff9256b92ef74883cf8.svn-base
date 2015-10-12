package com.athena.mis.application.actions.appMail

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppMail
import com.athena.mis.application.entity.RoleType
import com.athena.mis.application.service.AppMailService
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class UpdateAppMailActionService extends BaseService implements ActionIntf {

    AppMailService appMailService
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    private static final String APP_MAIL_UPDATE_FAILURE_MESSAGE = "Mail could not be updated"
    private static final String APP_MAIL_UPDATE_SUCCESS_MESSAGE = "Mail has been updated successfully"
    private static final String OBJ_NOT_FOUND = "Selected mail not found"
    private static final String APP_MAIL_OBJ = "appMail"
    private static final String ROLE_NOT_FOUND = " Role not found"
    private static final String RECIPIENTS_NOT_FOUND = " Recipients not found"

    private final Logger log = Logger.getLogger(getClass())

    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            // check required parameters
            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            long appMailId = Long.parseLong(parameterMap.id.toString())
            AppMail oldAppMail = appMailService.read(appMailId)
            // check whether selected  object exists or not
            if (!oldAppMail) {
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND)
                return result
            }
            AppMail appMail = buildAppMailObject(parameterMap, oldAppMail)

            if (appMail.isRequiredRoleIds) {
                List<String> roleIds = parameterMap.roleIds.split(Tools.COMA)
                String msgReturn = checkRoleIds(roleIds)
                if (msgReturn) {
                    result.put(Tools.MESSAGE, msgReturn)
                    return result
                }
            }

            if(appMail.isRequiredRecipients){
                String recipients = parameterMap.recipients
                List<String> lstRecipients = recipients.split(Tools.COMA)
                if(lstRecipients.size() <= 0){
                    result.put(Tools.MESSAGE, RECIPIENTS_NOT_FOUND)
                    return result
                }
            }
            result.put(APP_MAIL_OBJ, appMail)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, APP_MAIL_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from executePreCondition method
            AppMail appMail = (AppMail) preResult.get(APP_MAIL_OBJ)
            appMailService.update(appMail)
            result.put(APP_MAIL_OBJ, appMail)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(APP_MAIL_UPDATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, APP_MAIL_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }


    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            AppMail appMail = (AppMail) executeResult.get(APP_MAIL_OBJ)
            GridEntity object = new GridEntity()    // build grid entity object
            object.id = appMail.id
            object.cell = [
                    Tools.LABEL_NEW,
                    appMail.id,
                    appMail.subject,
                    appMail.transactionCode,
                    appMail.roleIds,
                    appMail.isActive ? Tools.YES : Tools.NO,
                    appMail.isManualSend ? Tools.YES : Tools.NO,
                    appMail.controllerName,
                    appMail.actionName
            ]

            result.put(Tools.MESSAGE, APP_MAIL_UPDATE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(Tools.VERSION, appMail.version)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, APP_MAIL_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }


    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from previous method
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, APP_MAIL_UPDATE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, APP_MAIL_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }


    private AppMail buildAppMailObject(GrailsParameterMap parameterMap, AppMail oldAppMail) {
        AppMail appMail = new AppMail(parameterMap)
        oldAppMail.subject = appMail.subject
        oldAppMail.body = appMail.body
        if (!parameterMap.transactionCode) {
            oldAppMail.transactionCode = oldAppMail.transactionCode
        }
        if (oldAppMail.isRequiredRoleIds) {
            oldAppMail.roleIds = appMail.roleIds
        }
        if (oldAppMail.isRequiredRecipients) {
            /*roleIds is optional for isRequiredRecipients = true.
              So it's editable though isRequiredRoleIds = false*/
            oldAppMail.roleIds = appMail.roleIds
            oldAppMail.recipients = appMail.recipients
        }
        oldAppMail.isActive = appMail.isActive

        return oldAppMail
    }

    /**
     * Check the existence of role ids from role type CacheUtility
     * @param roleIds - list of role ids
     * @return - a string of message
     */
    private String checkRoleIds(List<String> roleIds) {
        for (int i = 0; i < roleIds.size(); i++) {
            long roleId = 0L
            try {
                roleId = Long.parseLong(roleIds[i].trim())
            } catch (Exception e) {
                return roleIds[i] + ROLE_NOT_FOUND
            }
            RoleType roleType = (RoleType) roleTypeCacheUtility.read(roleId)
            if (!roleType) {
                return roleIds[i] + ROLE_NOT_FOUND
            }
        }

        return null
    }
}
