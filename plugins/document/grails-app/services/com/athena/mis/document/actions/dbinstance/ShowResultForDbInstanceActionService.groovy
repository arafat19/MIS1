package com.athena.mis.document.actions.dbinstance

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.document.entity.DocDbInstance
import com.athena.mis.document.utility.DocDbInstanceCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class ShowResultForDbInstanceActionService extends BaseService implements ActionIntf {

    @Autowired
    DocDbInstanceCacheUtility docDbInstanceCacheUtility

    private final Logger log = Logger.getLogger(getClass())
    private static final String ERROR_MSG = "Failed to show query result"
    private static final String NOT_FOUND = "Database instance not found"
    private static final String DB_INSTANCE_ID = "dbInstanceId"

    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            long dbInstanceId = Tools.parseLongInput(params.dbInstanceId)
            DocDbInstance dbInstance = (DocDbInstance) docDbInstanceCacheUtility.read(dbInstanceId)
            if (!dbInstance) {
                result.put(Tools.IS_ERROR, Boolean.TRUE)
                result.put(Tools.MESSAGE, NOT_FOUND)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(DB_INSTANCE_ID, dbInstanceId)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_MSG)
            return result
        }
    }

    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    public Object buildFailureResultForUI(Object obj) {
        return null
    }
}
