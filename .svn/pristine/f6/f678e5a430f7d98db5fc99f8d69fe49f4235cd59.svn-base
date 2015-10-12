package com.athena.mis.exchangehouse.actions.photoidtype

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.exchangehouse.entity.ExhPhotoIdType
import com.athena.mis.exchangehouse.service.ExhPhotoIdTypeService
import com.athena.mis.exchangehouse.utility.ExhPhotoIdTypeCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class ExhDeletePhotoIdTypeActionService extends BaseService implements ActionIntf {
    //call photoIdType service
    ExhPhotoIdTypeService exhPhotoIdTypeService
    @Autowired
    ExhPhotoIdTypeCacheUtility exhPhotoIdTypeCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    //assign photoIdType information removing status information
    private static String PHOTO_ID_TYPE_DELETE_SUCCESS_MSG = "Photo id type has been successfully deleted!"
    private static String PHOTO_ID_TYPE_DELETE_FAILURE_MSG = "Sorry! Photo id type has not been deleted."

    //define logger to set all log message
    private final Logger log = Logger.getLogger(getClass())

    //implement the pre-condition method of action class
    /*
    Get all pre-condition to save photoIdType info
     */

    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        Map outputMap = new HashMap()
        try {
            GrailsParameterMap parameters=(GrailsParameterMap) params
            ExhPhotoIdType photoIdType = (ExhPhotoIdType) exhPhotoIdTypeCacheUtility.read(parameters.id.toLong())
            String associationMessage = null
            if (exhSessionUtil.appSessionUtil.getAppUser().isPowerUser) {
                outputMap.put("hasAccess", new Boolean(true))
                associationMessage = isAssociated(photoIdType)
            } else {
                outputMap.put("hasAccess", new Boolean(false))
            }
            if (associationMessage != null) {
                outputMap.put("hasAssociation", associationMessage)
            }
            return outputMap
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return null
        }
    }

    //implement the execute method of action class
    /*
    if precondition is ok. then save photoIdType info using
    execute method
     */

    @Transactional
    public Object execute(Object parameters, Object obj) {
        try {
            GrailsParameterMap parameterMap=(GrailsParameterMap) parameters
            Long photoIdTypeId = parameterMap.id.toLong()
            Boolean result = (Boolean) exhPhotoIdTypeService.delete(photoIdTypeId)
            if (result.booleanValue()) {
                ExhPhotoIdType photoIdType = (ExhPhotoIdType) exhPhotoIdTypeCacheUtility.read(photoIdTypeId)
                exhPhotoIdTypeCacheUtility.delete(photoIdType.id)
            }
            return result
        } catch (Exception ex) {
            log.error(e.getMessage())
            //@todo:rollback
            throw new RuntimeException('Failed to delete Photo Id Type')
            return Boolean.FALSE
        }
    }

    //implement the executePostCondition method of action class
    /*
    if execute is ok. then executePostCondition method will be checked
     */

    public Object executePostCondition(Object parameters, Object obj) {
        //there are not post condition
        return null

    }

    //implement the buildSuccessResultForUI method of action class
    /*
    if photoIdType build successfully then initiate success message
     */

    public Object buildSuccessResultForUI(Object obj) {
        return [deleted: Boolean.TRUE.booleanValue(), message: PHOTO_ID_TYPE_DELETE_SUCCESS_MSG]
    }

    //implement the buildFailureResultForUI method of action class
    /*
    if photoIdType build failed then initiate failure message
     */

    public Object buildFailureResultForUI(Object obj) {
        // add try catch and add log entry
        LinkedHashMap result
        try {
            if (!obj) {
                return [deleted: Boolean.FALSE.booleanValue(), message: PHOTO_ID_TYPE_DELETE_FAILURE_MSG]
            }
            return [deleted: Boolean.FALSE.booleanValue(), message: (String) obj]
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return [deleted: Boolean.FALSE.booleanValue(), message: PHOTO_ID_TYPE_DELETE_FAILURE_MSG]
        }
    }

    // checking all association
    private String isAssociated(ExhPhotoIdType photoIdType) {
        Long photoIdTypeId = photoIdType.id
        String photoIdTypeName = photoIdType.name
        Integer count = 0

        // has Customer
        count = countCustomer(photoIdTypeId)
        if (count.intValue() > 0) return Tools.getMessageOfAssociation(photoIdTypeName, count, Tools.DOMAIN_CUSTOMER)

        return null
    }

    //count number of row in customer table by photoIdType id
    private int countCustomer(Long photoIdTypeId) {
        String query = """
            SELECT COUNT(id) as count
            FROM exh_customer
            WHERE photo_id_type_id = ${photoIdTypeId}
        """
        List photoIdTypeCount = executeSelectSql(query)
        int count = photoIdTypeCount[0].count
        return count
    }
}

