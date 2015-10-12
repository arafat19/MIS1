package com.athena.mis.document.actions.article

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.document.entity.DocArticle
import com.athena.mis.document.service.DocArticleService
import com.athena.mis.document.utility.DocSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class RestoreFromTrashDocArticleActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass());

    private static final String RESTORE_SUCCESS_MSG = "Article has been successfully restored!"
    private static final String RESTORE_ERROR_MESSAGE = "Failed to restore Article"
    private static final String OBJ_NOT_FOUND = "Article not found"
    private static final String DOC_ARTICLE = 'docArticle'
    private static final String RESTORE = 'restore'


    DocArticleService docArticleService

    @Autowired
    DocSessionUtil docSessionUtil

    /*
    * @params parameters - Serialize parameters from UI
    * @params obj - N/A
    * Check for invalid input, object
    * @return - A map of containing article object or error messages
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
            DocArticle oldArticle = (DocArticle) docArticleService.read(id)
            if (!oldArticle) {
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND)
                return result
            }
            DocArticle newArticle = buildArticle(oldArticle)

            result.put(DOC_ARTICLE, newArticle)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, RESTORE_ERROR_MESSAGE)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        //Do nothing for post - operation
        return null
    }

    /**
     * Restore Article object from grid & update isMovedToTrash flag
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
            DocArticle article = (DocArticle) preResult.get(DOC_ARTICLE)
            docArticleService.update(article)
            result.put(DOC_ARTICLE, article)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(RESTORE_ERROR_MESSAGE)

            result.put(Tools.MESSAGE, RESTORE_ERROR_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    /*
    * @params obj - map from execute method
    * Show success message
    * @return - A map containing all necessary object for show
    * */

    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        result.put(RESTORE, Boolean.TRUE)
        result.put(Tools.MESSAGE, RESTORE_SUCCESS_MSG)
        result.put(Tools.IS_ERROR, Boolean.FALSE)
        return result

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
                    result.put(Tools.MESSAGE, RESTORE_ERROR_MESSAGE)
                }
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.MESSAGE, RESTORE_ERROR_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    /*
  * Build Article for update
  * */

    private DocArticle buildArticle(DocArticle oldArticle) {
        oldArticle.isMovedToTrash = false
        oldArticle.updatedOn = new Date()
        oldArticle.updatedBy = docSessionUtil.appSessionUtil.getAppUser().id
        return oldArticle
    }
}
