package com.athena.mis.document.actions.articlequery

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.SystemEntityService
import com.athena.mis.document.entity.DocArticleQuery
import com.athena.mis.document.service.DocArticleQueryService
import com.athena.mis.document.utility.DocSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class CreateDocArticleQueryActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String DOC_ARTICLE_QUERY = 'docArticleQuery'
    private static final String CREATE_SUCCESS_MESSAGE = 'Article query has been successfully saved'
    private static final String CREATE_FAILURE_MESSAGE = 'Failed to saved article query'
    private static final String CONTENT_TYPE_NOT_FOUND = 'Content type not found'

    DocArticleQueryService docArticleQueryService
    SystemEntityService systemEntityService

    @Autowired
    DocSessionUtil docSessionUtil

    /**
     * @params parameters - serialize parameters form UI
     * @params obj - N/A
     * Check Article length
     * @return - A map of containing isError message
     * */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            long contentTypeId = Long.parseLong(params.contentTypeId.toString())
            SystemEntity systemEntity = systemEntityService.read(contentTypeId)
            if (!systemEntity) {
                result.put(Tools.MESSAGE, CONTENT_TYPE_NOT_FOUND)
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
     * Create Article object to DB
     * @params parameters - serialize parameters form UI
     * @params obj - N/A
     * @return - A map of saved article object or error messages
     * Ths method is in Transactional boundary so will rollback in case of any exception
     * */

    @Transactional
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            DocArticleQuery articleQuery = buildArticleQuery(parameterMap)     //build Article object

            docArticleQueryService.create(articleQuery)
            result.put(DOC_ARTICLE_QUERY, articleQuery)
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
     * Show newly created article to grid
     * Show success message
     * @return - A map containing all necessary object for show
     * */

    @Transactional(readOnly = true)
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()

        try {
            Map preResult = (Map) obj
            DocArticleQuery articleQuery = (DocArticleQuery) preResult.get(DOC_ARTICLE_QUERY)
            GridEntity object = new GridEntity()
            SystemEntity systemEntity = systemEntityService.read(articleQuery.contentTypeId)
            object.id = articleQuery.id
            object.cell = [
                    Tools.LABEL_NEW,
                    articleQuery.id,
                    articleQuery.name,
                    systemEntity.value,
                    Tools.makeDetailsShort(articleQuery.criteria, 60)
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
   * Build Article object for create
   * */

    private DocArticleQuery buildArticleQuery(GrailsParameterMap parameterMap) {
        AppUser appUser = docSessionUtil.appSessionUtil.appUser
        DocArticleQuery articleQuery = new DocArticleQuery(parameterMap)
        articleQuery.companyId = appUser.getCompanyId()
        articleQuery.createdBy = appUser.id
        articleQuery.createdOn = new Date()
        articleQuery.updatedBy = 0L
        articleQuery.updatedOn = null
        return articleQuery
    }
}
