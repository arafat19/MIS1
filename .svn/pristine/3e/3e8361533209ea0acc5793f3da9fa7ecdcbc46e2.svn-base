package com.athena.mis.document.actions.dbinstance

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.document.entity.DocDbInstance
import com.athena.mis.document.utility.DocDbInstanceCacheUtility
import com.athena.mis.document.utility.DocSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class SelectDocDbInstanceActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String NOT_FOUND_ERROR_MESSAGE = 'Selected DB Instance is not found'
    private static final String DEFAULT_ERROR_MESSAGE = 'Failed to select DB Instance'

    @Autowired
    DocSessionUtil docSessionUtil
    @Autowired
    DocDbInstanceCacheUtility docDbInstanceCacheUtility


    public Object executePreCondition(Object parameters, Object obj) {
        //Do nothing for pre - operation
        return null
    }


    public Object executePostCondition(Object parameters, Object obj) {
        //Do nothing for post - operation
        return null
    }

    /*
    * @params parameters - Serialize parameters from UI
    * @params obj - N/A
    * get DB Instance object by id
    * @return - A map of Entity or error message
    * */

    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()

        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long id = Long.parseLong(parameterMap.id.toString())
            DocDbInstance docDbInstance = (DocDbInstance) docDbInstanceCacheUtility.read(id)
            if (!docDbInstance) {
                result.put(Tools.MESSAGE, NOT_FOUND_ERROR_MESSAGE)
                return result
            }

            result.put(Tools.ENTITY, docDbInstance)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
        }
    }

    /*
    * Build Success Results
    * @params obj - Map return from execute method
    * @return a map of containing all object necessary for edit/delete page
    * */

    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map preResult = (Map) obj
            DocDbInstance docDbInstance = (DocDbInstance) preResult.get(Tools.ENTITY)
            result.put(Tools.ENTITY, docDbInstance)
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
}
