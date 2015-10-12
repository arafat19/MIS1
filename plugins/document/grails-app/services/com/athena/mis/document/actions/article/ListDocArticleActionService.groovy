package com.athena.mis.document.actions.article

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.document.service.DocArticleService
import com.athena.mis.document.utility.DocSessionUtil
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class ListDocArticleActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = 'Failed to load my articles'
    private static final String LIST_ARTICLE = 'lstArticle'
    private static final String GRID_OBJ = 'gridObj'

    DocArticleService docArticleService
    @Autowired
    DocSessionUtil docSessionUtil

    public Object executePreCondition(Object parameters, Object obj) {
        //Do nothing for pre-operation
        return null
    }


    public Object executePostCondition(Object parameters, Object obj) {
        //Do nothing for post-operation
        return null
    }

    /*
    * @param parameters - serialize parameters from UI
    * @param obj - N/A
    * @return - A map containing all objects of article list,count of article for buildSuccessResultForUI
    * Ths method is in Transactional boundary so will rollback in case of any exception
    * */

    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            initPager(params)
            long companyId = docSessionUtil.appSessionUtil.getCompanyId()
            long userId = docSessionUtil.appSessionUtil.getAppUser().id
            Map lstResult = listDocArticle(companyId, userId)
            List<GroovyRowResult> lstDocArticle = lstResult.lstDocArticle
            //get list of article from DB
            int count = lstResult.count
            result.put(LIST_ARTICLE, lstDocArticle)
            result.put(Tools.COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            throw new RuntimeException(DEFAULT_ERROR_MESSAGE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }

    }

    /*
    * Build Success Results for grid in UI
    * @params obj - Map return from execute method
    * Wrap article list for grid
    * @return a map of containing all object necessary for grid
    * */

    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map preResult = (Map) obj
            List<GroovyRowResult> lstDocArticle = (List<GroovyRowResult>) preResult.get(LIST_ARTICLE)
            int count = (int) preResult.get(Tools.COUNT)
            List lstArticle = wrapListDocArticle(lstDocArticle, start)
            Map gridObject = [page: pageNumber, total: count, rows: lstArticle]
            result.put(GRID_OBJ, gridObject)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result

        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    /*
    * Build Failure result for UI
    * @params obj - A map from execute method
    * @return a Map containing IsError and default error message/relevant error message to display
    * */

    public Object buildFailureResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map preResult = (Map) obj
            if (obj) {
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result

        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /*
    * Wrap article List for Grid
    * @params lstDocArticle - List of Article
    * @params start - starting index of the page
    * @return List of wrapped article
    * */

    private List wrapListDocArticle(List<GroovyRowResult> lstDocArticle, int start) {
        List lstArticle = []
        int counter = start + 1
        for (int i = 0; i < lstDocArticle.size(); i++) {
            GridEntity object = new GridEntity()
            GroovyRowResult article = lstDocArticle[i]
            object.id = article.id
            object.cell = [
                    counter,
                    article.id,
                    Tools.makeDetailsShort(article.title, 60),
                    article.category_name,
                    article.sub_category_name
            ]
            lstArticle << object
            counter++
        }
        return lstArticle
    }

    private static final String LST_QUERY = """
            SELECT da.id,da.title,dc.name category_name,dsc.name sub_category_name
            FROM doc_article da
            LEFT JOIN doc_category dc ON da.category_id=dc.id
            LEFT JOIN doc_sub_category dsc ON da.sub_category_id=dsc.id
            WHERE da.company_id=:companyId
            AND da.created_by=:userId
            AND da.is_moved_to_trash = false
            ORDER BY da.created_on desc
            LIMIT :resultPerPage OFFSET :start
        """

    private static final String COUNT_QUERY = """
            SELECT COUNT(da.id) count
            FROM doc_article da
            LEFT JOIN doc_category dc ON da.category_id=dc.id
            LEFT JOIN doc_sub_category dsc ON da.sub_category_id=dsc.id
            WHERE da.company_id=:companyId
            AND da.created_by=:userId
            AND da.is_moved_to_trash = false
        """

    /**
     * Get the list of DocArticle
     * @param companyId - companyId Id
     * @param userId - userId Id
     * @return - a map containing list of docArticle and count
     */
    private Map listDocArticle(long companyId, long userId) {
        Map queryParams = [
                companyId: companyId,
                userId: userId,
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> lstResult = executeSelectSql(LST_QUERY, queryParams)
        List<GroovyRowResult> countResult = executeSelectSql(COUNT_QUERY, queryParams)

        int total = (int) countResult[0].count
        return [lstDocArticle: lstResult, count: total]
    }
}
