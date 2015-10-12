package com.athena.mis.application.actions.sms

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Sms
import com.athena.mis.application.service.SmsService
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.SmsCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Update sms object and grid data
 *  For details go through Use-Case doc named 'UpdateSmsActionService'
 */
class UpdateSmsActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    SmsService smsService
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    SmsCacheUtility smsCacheUtility
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    private static final String UPDATE_FAILURE_MSG = "SMS could not be updated"
    private static final String UPDATE_SUCCESS_MSG = "SMS has been updated successfully"
    private static final String SMS_NOT_FOUND = "SMS not found to be updated, refresh the page"
    private static final String SMS_OBJECT = "sms"
    private static final String SORT_BY_ID = "id"

    /**
     * Get parameters from UI and build sms object for update
     * 1. Check the access of Development user
     * 2. Check the existence of old sms object
     * 3. Build new sms object
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)

            if (!appSessionUtil.getAppUser().isConfigManager) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }
            GrailsParameterMap params = (GrailsParameterMap) parameters
            long smsId = Integer.parseInt(params.id.toString())

            // check required parameters
            if (!params.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            Sms oldSms = smsService.read(smsId)
            if (!oldSms) {
                result.put(Tools.MESSAGE, SMS_NOT_FOUND)
                return result
            }

            Sms smsObject = buildSmsObjectForUpdate(params, oldSms) // build sms object

            result.put(SMS_OBJECT, smsObject)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
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
     * Update sms object in DB & update cache utility
     * 1. This function is in transactional block and will roll back in case of any exception
     * @param parameters - N/A
     * @param obj - serialized parameters from UI
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            Sms smsObject = (Sms) preResult.get(SMS_OBJECT)
            smsService.update(smsObject)
            smsObject.version = smsObject.version + 1
            smsCacheUtility.update(smsObject, SORT_BY_ID, ASCENDING_SORT_ORDER)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(SMS_OBJECT, smsObject)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Show updated sms object in grid
     * Show success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            Sms smsObject = (Sms) executeResult.get(SMS_OBJECT)
            GridEntity object = new GridEntity()
            object.id = smsObject.id
            object.cell = [
                    Tools.LABEL_NEW,
                    smsObject.id,
                    smsObject.transactionCode,
                    Tools.makeDetailsShort(smsObject.body, Tools.DEFAULT_LENGTH_DETAILS_OF_SMS_BODY), // make shorter the sms body
                    Tools.makeDetailsShort(smsObject.description, Tools.DEFAULT_LENGTH_DETAILS_OF_SMS_DES), // make shorter the sms description body
                    smsObject.isActive ? Tools.YES : Tools.NO,
                    smsObject.isManualSend ? Tools.YES : Tools.NO,
                    smsObject.controllerName,
                    smsObject.actionName
            ]
            result.put(Tools.MESSAGE, UPDATE_SUCCESS_MSG)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message to display on page load
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
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Build sms object for update
     * 1. Update oldSms object by newSms object
     * @param params - serialized parameters from UI
     * @param oldSms - old sms object
     * @return - updated sms object
     */
    private Sms buildSmsObjectForUpdate(GrailsParameterMap params, Sms oldSms) {
        Sms newSms = new Sms(params)
        oldSms.isActive = newSms.isActive
        oldSms.body = newSms.body
        oldSms.url = newSms.url
        oldSms.recipients = newSms.recipients
        return oldSms
    }
}
