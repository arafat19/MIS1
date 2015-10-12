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

class DownloadCsvResultForDbInstanceActionService extends BaseService implements ActionIntf {

    @Autowired
    DocDbInstanceCacheUtility docDbInstanceCacheUtility
    def exportService

    private final Logger log = Logger.getLogger(getClass())
    private static final String FILE_NAME = "fileName"
    private static final String OUTPUT_BYTES = "outputBytes"

    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try{
            GrailsParameterMap params = (GrailsParameterMap) parameters
            long dbInstanceId = Tools.parseLongInput(params.dbInstanceId)
            DocDbInstance dbInstance = (DocDbInstance) docDbInstanceCacheUtility.read(dbInstanceId)
            if(!dbInstance) {
                result.put(Tools.IS_ERROR, Boolean.TRUE)
                return result
            }
            DocJdbcConnection jdbcConnection = new DocJdbcConnection(dbInstance)
            List lstResult = jdbcConnection.runQuery()

            Map singleResult = (Map) lstResult[0]
            List csvFields = []
            for (String key : singleResult.keySet()) {
                csvFields << key
            }
            String fileName = dbInstance.instanceName + Tools.CSV_EXTENSION
            ByteArrayOutputStream outputBytes = new ByteArrayOutputStream()
            exportService.export(Tools.FORMAT_TYPE_NAME_CSV, outputBytes,
                    lstResult, csvFields, null, null, null)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(FILE_NAME, fileName)
            result.put(OUTPUT_BYTES, outputBytes)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
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
