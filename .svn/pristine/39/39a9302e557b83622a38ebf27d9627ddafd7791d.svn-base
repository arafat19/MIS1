package com.athena.mis.document.actions.dbinstance

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.document.entity.DocDbInstance
import com.athena.mis.document.utility.DocDbInstanceCacheUtility
import com.athena.mis.document.utility.DocJdbcConnection
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class ListResultForDbInstanceActionService extends BaseService implements ActionIntf {

    @Autowired
    DocDbInstanceCacheUtility docDbInstanceCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    public Object execute(Object parameters, Object obj) {
        List lstResult = []
        try{
            GrailsParameterMap params = (GrailsParameterMap) parameters
            long dbInstanceId = Tools.parseLongInput(params.dbInstanceId)
            DocDbInstance dbInstance = (DocDbInstance) docDbInstanceCacheUtility.read(dbInstanceId)
            if(!dbInstance) {
                return lstResult
            }
            DocJdbcConnection jdbcConnection = new DocJdbcConnection(dbInstance)
            lstResult = jdbcConnection.runQuery()
            return lstResult
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            return []
        }
    }

    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    public Object buildFailureResultForUI(Object obj) {
        return null
    }
}
