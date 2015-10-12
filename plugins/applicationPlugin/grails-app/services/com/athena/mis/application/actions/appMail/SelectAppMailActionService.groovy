package com.athena.mis.application.actions.appMail

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppMail
import com.athena.mis.application.service.AppMailService
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

class SelectAppMailActionService extends BaseService implements ActionIntf {

    AppMailService appMailService

    private static final String APP_MAIL_NOT_FOUND_MASSAGE = "Selected mail is not found"
    private static final String DEFAULT_ERROR_MASSAGE = "Failed to select mail"

    private final Logger log = Logger.getLogger(getClass())

    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
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
            AppMail appMail = (AppMail) appMailService.read(appMailId)

            if (appMail) {
                result.put(Tools.ENTITY, appMail)
            } else {
                result.put(Tools.MESSAGE, APP_MAIL_NOT_FOUND_MASSAGE)
            }

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, APP_MAIL_NOT_FOUND_MASSAGE)
            return result
        }
    }


    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }


    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            AppMail appMail = (AppMail) executeResult.get(Tools.ENTITY)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.ENTITY, appMail)
            result.put(Tools.VERSION, appMail.version)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        }
    }


    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                Map previousResult = (Map) obj  // cast map returned from previous method
                result.put(Tools.MESSAGE, previousResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            }
            return result
        }
        catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        }
    }


}
