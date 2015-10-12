package com.athena.mis.document.actions.dbinstance

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.document.entity.DocDbInstance
import com.athena.mis.document.service.DocDbInstanceService
import com.athena.mis.document.utility.DocDBVendorCacheUtility
import com.athena.mis.document.utility.DocDbInstanceCacheUtility
import com.athena.mis.document.utility.DocSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class UpdateDocDbInstanceActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String DB_INSTANCE = 'dbInstance'
    private static final String UPDATE_SUCCESS_MESSAGE = 'DB Instance has been successfully updated'
    private static final String UPDATE_FAILURE_MESSAGE = 'Failed to saved DB Instance'
    private static final String NAME_MUST_BE_UNIQUE = 'DB Instance name must be unique'
    private static final String OBJ_NOT_FOUND = "DB Instance not found"

    DocDbInstanceService docDbInstanceService

    @Autowired
    DocSessionUtil docSessionUtil
    @Autowired
    DocDbInstanceCacheUtility docDbInstanceCacheUtility
    @Autowired
    DocDBVendorCacheUtility docDBVendorCacheUtility

    /*
    * @params parameters - Serialize parameters from UI
    * @params obj - N/A
    * Check for invalid input, object
    * Build new DB Instance
    * check for duplicate name
    * @return - A map on containing new DB Instance object or error messages
    * */

    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap grailsParameterMap = (GrailsParameterMap) parameters
            if (!grailsParameterMap.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            long id = Long.parseLong(grailsParameterMap.id.toString())
            DocDbInstance oldDbInstance = (DocDbInstance) docDbInstanceService.read(id)
            if (!oldDbInstance) {
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND)
                return result
            }

            DocDbInstance newDbInstance = buildDbInstance(grailsParameterMap, oldDbInstance)
            int duplicateCount = docDbInstanceCacheUtility.countByInstanceNameIlikeAndIdNotEqual(newDbInstance.instanceName, newDbInstance.id)
            if (duplicateCount > 0) {
                result.put(Tools.MESSAGE, NAME_MUST_BE_UNIQUE)
                return result
            }

            result.put(DB_INSTANCE, newDbInstance)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }


    public Object executePostCondition(Object parameters, Object obj) {
        //Do Nothing ofr post - operation
        return null
    }

    /**
     * Update DB Instance object in DB & update cache utility accordingly
     * This function is in transactional block and will roll back in case of any exception
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for build result
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map preResult = (Map) obj
            DocDbInstance docDbInstance = (DocDbInstance) preResult.get(DB_INSTANCE)
            docDbInstanceService.update(docDbInstance)
            docDbInstanceCacheUtility.update(docDbInstance, docDbInstanceCacheUtility.DEFAULT_SORT_NAME, docDbInstanceCacheUtility.SORT_ORDER_ASCENDING)
            result.put(DB_INSTANCE, docDbInstance)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(UPDATE_FAILURE_MESSAGE)

            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    /*
    * @params obj - map from execute method
    * Show newly updated DB Instance to grid
    * Show success message
    * @return - A map containing all necessary object for show
    * */

    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map preResult = (Map) obj
            DocDbInstance dbInstance = (DocDbInstance) preResult.get(DB_INSTANCE)
            SystemEntity systemEntity = (SystemEntity) docDBVendorCacheUtility.read(dbInstance.vendorId)
            GridEntity object = new GridEntity()
            object.id = dbInstance.id
            object.cell = [
                    Tools.LABEL_NEW,
                    dbInstance.id,
                    systemEntity.key,
                    dbInstance.instanceName,
                    dbInstance.driver,
                    dbInstance.connectionString
            ]
            result.put(Tools.ENTITY, object)
            result.put(Tools.VERSION, dbInstance.version)
            result.put(Tools.MESSAGE, UPDATE_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    /*
   * Build Failure result in case of any error
   * @params obj - A map from execute method
   * @return - A map containing all necessary message for show
   * */

    public Object buildFailureResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map preResult = (Map) obj
            if (obj) {
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                } else {
                    result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
                }
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    /*
    * Build DB Instance object for update
    * */

    private DocDbInstance buildDbInstance(GrailsParameterMap parameterMap, DocDbInstance oldDbInstance) {
        DocDbInstance newDbInstance = new DocDbInstance(parameterMap)
        oldDbInstance.instanceName = newDbInstance.instanceName
        oldDbInstance.vendorId= newDbInstance.vendorId
        oldDbInstance.driver = newDbInstance.driver
        oldDbInstance.connectionString = newDbInstance.connectionString
        oldDbInstance.sqlQuery= newDbInstance.sqlQuery
        oldDbInstance.updatedOn = new Date()
        oldDbInstance.updatedBy = docSessionUtil.appSessionUtil.getAppUser().id
        return oldDbInstance
    }
}
