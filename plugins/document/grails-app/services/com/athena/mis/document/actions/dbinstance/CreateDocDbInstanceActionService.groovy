package com.athena.mis.document.actions.dbinstance

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
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

class CreateDocDbInstanceActionService extends BaseService implements ActionIntf {


    private Logger log = Logger.getLogger(getClass())

    private static final String DB_INSTANCE = 'dbInstance'
    private static final String CREATE_SUCCESS_MESSAGE = 'DB Instance has been successfully saved'
    private static final String CREATE_FAILURE_MESSAGE = 'Failed to saved DB Instance'
    private static final String NAME_MUST_BE_UNIQUE = 'DB Instance name must be unique'


    DocDbInstanceService docDbInstanceService

    @Autowired
    DocDbInstanceCacheUtility docDbInstanceCacheUtility
    @Autowired
    DocSessionUtil docSessionUtil
    @Autowired
    DocDBVendorCacheUtility docDBVendorCacheUtility

    /**
     * @params parameters - serialize parameters form UI
     * @params obj - N/A
     * Check duplicate DB Instance name
     * @return - A map of containing error message
     * */

    public Object executePreCondition(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters

            int duplicateCount = docDbInstanceCacheUtility.countByInstanceNameIlike(params.instanceName.toString())
            if (duplicateCount > 0) {
                result.put(Tools.MESSAGE, NAME_MUST_BE_UNIQUE)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CREATE_FAILURE_MESSAGE)
            return result
        }
    }


    public Object executePostCondition(Object parameters, Object obj) {
        //Do nothing for post - operation
        return null
    }

    /**
     * Create DB Instance object to DB also add to cache utility
     * @params parameters - serialize parameters form UI
     * @params obj - N/A
     * @return - A map of saved DB Instance object or error messages
     * Ths method is in Transactional boundary so will rollback in case of any exception
     * */

    @Transactional
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            DocDbInstance docDbInstance = buildDbInstance(parameterMap)     //build DbInstance object

            docDbInstanceService.create(docDbInstance)
            docDbInstanceCacheUtility.add(docDbInstance, docDbInstanceCacheUtility.DEFAULT_SORT_NAME, docDbInstanceCacheUtility.SORT_ORDER_ASCENDING)
            result.put(DB_INSTANCE, docDbInstance)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(CREATE_FAILURE_MESSAGE)
            result.put(Tools.MESSAGE, CREATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    /**
     * @params obj - map from execute method
     * Show newly created DB Instance to grid
     * Show success message
     * @return - A map containing all necessary object for show
     * */

    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map preResult = (Map) obj
            DocDbInstance docDbInstance = (DocDbInstance) preResult.get(DB_INSTANCE)
            SystemEntity systemEntity = (SystemEntity) docDBVendorCacheUtility.read(docDbInstance.vendorId)
            GridEntity object = new GridEntity()
            object.id = docDbInstance.id
            object.cell = [
                    Tools.LABEL_NEW,
                    docDbInstance.id,
                    systemEntity.key,
                    docDbInstance.instanceName,
                    docDbInstance.driver,
                    docDbInstance.connectionString
            ]
            result.put(Tools.ENTITY, object)
            result.put(Tools.MESSAGE, CREATE_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.MESSAGE, CREATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    /**
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
                    result.put(Tools.MESSAGE, CREATE_FAILURE_MESSAGE)
                }
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.MESSAGE, CREATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    /*
   * Build DB Instance object for create
   * */

    private DocDbInstance buildDbInstance(GrailsParameterMap parameterMap) {
        AppUser appUser = docSessionUtil.appSessionUtil.appUser
        DocDbInstance docDbInstance = new DocDbInstance(parameterMap)
        docDbInstance.companyId = appUser.getCompanyId()
        docDbInstance.createdBy = appUser.id
        docDbInstance.createdOn = new Date()
        docDbInstance.updatedBy = 0L
        docDbInstance.updatedOn = null
        return docDbInstance
    }

}
