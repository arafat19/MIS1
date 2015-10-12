package com.athena.mis.accounting.actions.acccustomgroup

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccCustomGroup
import com.athena.mis.accounting.utility.AccCustomGroupCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
// select object of selected row from custom group grid
class SelectAccCustomGroupActionService extends BaseService implements ActionIntf {

    private static final String ACC_CUSTOM_GROUP_NOT_FOUND_MASSAGE = "Selected custom group is not found"
    private static final String DEFAULT_ERROR_MASSAGE = "Failed to select custom group"

    @Autowired
    AccCustomGroupCacheUtility accCustomGroupCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        }
    }

    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            Long accCustomGroupId = Long.parseLong(parameterMap.id.toString())
            AccCustomGroup accCustomGroup = (AccCustomGroup) accCustomGroupCacheUtility.read(accCustomGroupId)
            if (accCustomGroup) {
                result.put(Tools.ENTITY, accCustomGroup)
            } else {
                result.put(Tools.MESSAGE, ACC_CUSTOM_GROUP_NOT_FOUND_MASSAGE)
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ACC_CUSTOM_GROUP_NOT_FOUND_MASSAGE)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)

            LinkedHashMap executeResult = (LinkedHashMap) obj
            AccCustomGroup accCustomGroup = (AccCustomGroup) executeResult.get(Tools.ENTITY)

            result.put(Tools.ENTITY, accCustomGroup)
            result.put(Tools.VERSION, accCustomGroup.version)
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
                if (preResult.message) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        }
    }
}
