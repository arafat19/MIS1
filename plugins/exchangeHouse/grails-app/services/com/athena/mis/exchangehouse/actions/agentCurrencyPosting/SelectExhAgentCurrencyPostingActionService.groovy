package com.athena.mis.exchangehouse.actions.agentCurrencyPosting

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.exchangehouse.entity.ExhAgentCurrencyPosting
import com.athena.mis.exchangehouse.service.ExhAgentCurrencyPostingService
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

class SelectExhAgentCurrencyPostingActionService extends BaseService implements ActionIntf {
    private final Logger log = Logger.getLogger(getClass())


    private static final String NOT_FOUND_MASSAGE = "Selected  agent currency posting information is not found"
    private static final String DEFAULT_ERROR_MASSAGE = "Failed to select agent currency posting information"

    ExhAgentCurrencyPostingService exhAgentCurrencyPostingService

    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            long currencyPostingId = Long.parseLong(parameterMap.id.toString())
            ExhAgentCurrencyPosting exhAgentCurrencyPosting = exhAgentCurrencyPostingService.read(currencyPostingId)
            if (exhAgentCurrencyPosting) {
                result.put(Tools.ENTITY, exhAgentCurrencyPosting)
            } else {
                result.put(Tools.MESSAGE, NOT_FOUND_MASSAGE)
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, NOT_FOUND_MASSAGE)
            return result
        }
    }



    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
            ExhAgentCurrencyPosting exhAgentCurrencyPosting = (ExhAgentCurrencyPosting) executeResult.get(Tools.ENTITY)

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.ENTITY, exhAgentCurrencyPosting)
            result.put(Tools.VERSION, exhAgentCurrencyPosting.version)
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
                LinkedHashMap preResult = (LinkedHashMap) obj
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        }
    }
}
