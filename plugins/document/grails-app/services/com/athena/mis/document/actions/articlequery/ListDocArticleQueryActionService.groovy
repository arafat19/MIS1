package com.athena.mis.document.actions.articlequery

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.document.utility.DocSessionUtil
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class ListDocArticleQueryActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = 'Failed to load article query list'
    private static final String LST_ARTICLE_QUERY= 'lstArticleQuery'
    private static final String GRID_OBJ = 'gridObj'

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

            Map lstResult = listArticleQuery()
            List<GroovyRowResult> lstArticleQuery = lstResult.lstArticleQuery
            //get list of article from DB
            int count = lstResult.count
            result.put(LST_ARTICLE_QUERY, lstArticleQuery)
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
            List<GroovyRowResult> lstDocArticleQuery = (List<GroovyRowResult>) preResult.get(LST_ARTICLE_QUERY)
            int count = (int) preResult.get(Tools.COUNT)
            List lstArticleQuery = wrapListArticleQuery(lstDocArticleQuery, start)
            Map gridObject = [page: pageNumber, total: count, rows: lstArticleQuery]
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

    private List wrapListArticleQuery(List<GroovyRowResult> lstDocArticleQuery, int start) {
        List lstArticleQuery = []
        int counter = start + 1
        for (int i = 0; i < lstDocArticleQuery.size(); i++) {
            GridEntity object = new GridEntity()
            GroovyRowResult articleQuery = lstDocArticleQuery[i]
            object.id = articleQuery.id
            object.cell = [
                    counter,
                    articleQuery.id,
                    articleQuery.name,
                    articleQuery.value,
                    Tools.makeDetailsShort(articleQuery.criteria, 60)
            ]
            lstArticleQuery << object
            counter++
        }
        return lstArticleQuery
    }

    private static final String LST_DOC_ARTICLE_QUERY = """
            SELECT daq.id,daq.name,se.value,daq.criteria
            FROM doc_article_query daq
            LEFT JOIN system_entity se ON daq.content_type_id=se.id
            WHERE daq.company_id=:companyId
            AND daq.created_by=:userId
            ORDER BY daq.created_on desc
            LIMIT :resultPerPage OFFSET :start
        """

    private static final String COUNT_ARTICLE_QUERY = """
            SELECT COUNT(daq.id) count
            FROM doc_article_query daq
            LEFT JOIN system_entity se ON daq.content_type_id=se.id
            WHERE daq.company_id=:companyId
            AND daq.created_by=:userId
        """

    /**
     * Get the list of DocArticle
     * @param companyId - companyId Id
     * @param userId - userId Id
     * @return - a map containing list of docArticle and count
     */
    private Map listArticleQuery() {
        long companyId = docSessionUtil.appSessionUtil.getCompanyId()
        long userId = docSessionUtil.appSessionUtil.getAppUser().id
        Map queryParams = [
                companyId: companyId,
                userId: userId,
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> lstArticleQuery = executeSelectSql(LST_DOC_ARTICLE_QUERY, queryParams)
        List<GroovyRowResult> countResult = executeSelectSql(COUNT_ARTICLE_QUERY, queryParams)

        int total = (int) countResult[0].count
        return [lstArticleQuery: lstArticleQuery, count: total]
    }
}