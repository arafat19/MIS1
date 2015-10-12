package com.athena.mis.projecttrack.actions.ptbug

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.ContentCategory
import com.athena.mis.application.entity.EntityContent
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.EntityContentService
import com.athena.mis.application.utility.ContentCategoryCacheUtility
import com.athena.mis.application.utility.ContentEntityTypeCacheUtility
import com.athena.mis.projecttrack.utility.PtSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class DownloadEntityContentForBugActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Fail to download attachment"
    private static final String NOT_FOUND_MESSAGE = "Selected attachment not found"
    private static final String ENTITY_CONTENT_OBJECT = "entityContent"
    private static final String FILE_NAME = "reportFileName"
    private static final String BUG_ATTACHMENT = "BugAttachment"

    EntityContentService entityContentService
    @Autowired
    ContentEntityTypeCacheUtility contentEntityTypeCacheUtility
    @Autowired
    ContentCategoryCacheUtility contentCategoryCacheUtility
    @Autowired
    PtSessionUtil ptSessionUtil

    /**
     * Check different criteria to download entityContent(project attachment) object
     *      1) Check existence of required parameters
     *      2) Check existence of entityContent(attachment file)
     * @param parameters -parameters from UI
     * @param obj - N/A
     * @return -a map containing entityContent object & isError(True/False) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters

            if (!params.entityId) {// check required parameter
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            // check existence of entity content
            long entityContentId = Long.parseLong(params.entityId.toString())
            long companyId = ptSessionUtil.appSessionUtil.getCompanyId()
            // pull system entity type(Bug) object
            SystemEntity contentEntityTypeBug = (SystemEntity) contentEntityTypeCacheUtility.readByReservedAndCompany(contentEntityTypeCacheUtility.CONTENT_ENTITY_TYPE_PT_BUG, companyId)
            ContentCategory imageTypePhoto = (ContentCategory) contentCategoryCacheUtility.readBySystemContentCategory(contentCategoryCacheUtility.IMAGE_TYPE_SCREEN_SHOT)
            ContentCategory contentCategory = (ContentCategory) contentCategoryCacheUtility.read(imageTypePhoto.id)
            long entityTypeId = contentEntityTypeBug.id
            EntityContent entityContent = entityContentService.findByEntityTypeIdAndEntityIdAndContentTypeId(entityTypeId, entityContentId, contentCategory.contentTypeId)

            if (!entityContent) {
                result.put(Tools.MESSAGE, NOT_FOUND_MESSAGE)
                return result
            }

            result.put(ENTITY_CONTENT_OBJECT, entityContent)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * get entityContent(attachment file) object
     * @param parameters -N/A
     * @param obj -entityContent(attachment file) object received from executePreCondition
     * @return -entityContent(attachment file) object
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            EntityContent entityContent = (EntityContent) receiveResult.get(ENTITY_CONTENT_OBJECT)
            result.put(FILE_NAME, BUG_ATTACHMENT+Tools.DASH+entityContent.entityId)
            result.put(ENTITY_CONTENT_OBJECT, entityContent)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * do nothing at post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * do nothing at buildSuccessResultForUI
     */
    public Object buildSuccessResultForUI(Object result) {
        return null
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            if (preResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }
}