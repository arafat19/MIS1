package com.athena.mis.application.actions.entitycontent

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.EntityContent
import com.athena.mis.application.service.EntityContentService
import com.athena.mis.application.utility.ContentCategoryCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to select specific entityContent(attachment) object and show in UI for editing
 *  For details go through Use-Case doc named 'SelectEntityContentActionService'
 */
class SelectEntityContentActionService extends BaseService implements ActionIntf {

    EntityContentService entityContentService
    @Autowired
    ContentCategoryCacheUtility contentCategoryCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String ENTITY_CONTENT_NOT_FOUND_MASSAGE = "Selected entity content is not found"
    private static final String DEFAULT_ERROR_MASSAGE = "Failed to select entity content"
    private static final String ENTITY_CONTENT_OBJECT = "entityContent"
    private static final String CONTENT_CATEGORY_LIST = "contentCategoryList"
    private static final String EXPIRATION_DATE = "expirationDate"

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get specific entityContent(attachment) object by id
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            long entityContentId = Long.parseLong(parameterMap.id.toString())

            EntityContent entityContent = entityContentService.read(entityContentId)
            if (!entityContent) {//check existence of selected object
                result.put(Tools.MESSAGE, ENTITY_CONTENT_NOT_FOUND_MASSAGE)
                return result
            }
            //get list of contentCategory to show on dropDown
            List lstContentCategory = contentCategoryCacheUtility.listByContentTypeId(entityContent.contentTypeId)

            result.put(ENTITY_CONTENT_OBJECT, entityContent)
            result.put(CONTENT_CATEGORY_LIST, lstContentCategory)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ENTITY_CONTENT_NOT_FOUND_MASSAGE)
            return result
        }
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Build a map with necessary objects to show on UI
     * @param obj -map contains entityContent(attachment) object
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            EntityContent entityContentObject = (EntityContent) receiveResult.get(ENTITY_CONTENT_OBJECT)
            List lstContentCategory = (List) receiveResult.get(CONTENT_CATEGORY_LIST)
            result.put(CONTENT_CATEGORY_LIST, Tools.listForKendoDropdown(lstContentCategory,null,null))
            result.put(Tools.ENTITY, entityContentObject)
            result.put(EXPIRATION_DATE, DateUtility.getDateForUI(entityContentObject.expirationDate))
            result.put(Tools.VERSION, entityContentObject.version)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, ENTITY_CONTENT_NOT_FOUND_MASSAGE)
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
