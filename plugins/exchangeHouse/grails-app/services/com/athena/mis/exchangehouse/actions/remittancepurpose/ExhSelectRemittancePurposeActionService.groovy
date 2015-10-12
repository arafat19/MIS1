package com.athena.mis.exchangehouse.actions.remittancepurpose

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.exchangehouse.entity.ExhRemittancePurpose
import com.athena.mis.exchangehouse.utility.ExhRemittancePurposeCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class ExhSelectRemittancePurposeActionService extends BaseService implements ActionIntf {

    @Autowired
    ExhRemittancePurposeCacheUtility exhRemittancePurposeCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    public Object executePreCondition(Object parameters, Object obj) {
        Map outputMap = new HashMap()
        try {
            if (exhSessionUtil.appSessionUtil.getAppUser().isPowerUser) {
                outputMap.put("hasAccess", new Boolean(true))
            } else {
                outputMap.put("hasAccess", new Boolean(false))
            }
            return outputMap
        } catch (Exception ex) {
            log.error(ex.getMessage())
            outputMap.put("hasAccess", new Boolean(false))
            return null
        }
    }

    /**
     * Select the remittancePurpose by id (primary key) and returns
     *
     * @param params request parameters
     * @param obj additional parameters, not required for this action
     * @return return RemittancePurpose instance and version info in a map
     */
    public Object execute(Object params, Object obj) {
        try {
            GrailsParameterMap parameters=(GrailsParameterMap) params
            ExhRemittancePurpose remittancePurposeInstance = (ExhRemittancePurpose) exhRemittancePurposeCacheUtility.read(Long.parseLong(parameters.id.toString()))
            return remittancePurposeInstance
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return null
        }
    }

    /**
     * Select remittancePurpose has not post-condition
     *
     * @param paramters
     * @param obj
     * @return nothing
     */
    public Object executePostCondition(Object parameters, Object obj) {
        // do nothing for post operation
        return null
    }


    public Object buildSuccessResultForUI(Object remittancePurposeInstance) {
        Map result = new LinkedHashMap()
        try {
            ExhRemittancePurpose remittancePurpose=(ExhRemittancePurpose) remittancePurposeInstance
            result = [entity: remittancePurpose, version: remittancePurpose.version]
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return result
        }
    }

    /**
     * Builds UI specific object on failure;
     *
     * @param obj Object to be used to determine building of UI result
     * @return Object to be used for rendering at UI level
     */
    public Object buildFailureResultForUI(Object obj) {
        Map result = [] as LinkedHashMap
        try {
            result = [isError: true, entity: null, errors: null, message: ENTITY_NOT_FOUND_ERROR_MESSAGE]
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return result
        }
    }

}