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

class SearchDocArticleQueryActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = 'Failed to load article query page'
    private static final String LST_ARTICLE_QUERY = 'lstArticleQuery'
    private static final String GRID_OBJ = 'gridObj'

    @Autowired
    DocSessionUtil docSessionUtil

    public Object executePreCondition(Object parameters, Object obj) {
        //Do nothing for pre - operation
        return null
    }

    public Object executePostCondition(Object parameters, Object obj) {
        //Do nothing for post - operation
        return null
    }

    /*
     * @param parameters - serialize parameters from UI
     * @param obj - N/A
     * Get article list for grid through specific search
     * @return - A map containing all objects of article list, count of articles for buildSuccessResultForUI
     * Ths method is in Transactional boundary so will rollback in case of any exception
     * */

    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            initSearch(params)
            Map searchResult = searchListArticleQuery()
            List<GroovyRowResult> lstArticleQuery = (List<GroovyRowResult>) searchResult.lstArticleQuery
            int count = (int) searchResult.count
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
    * @return a map of containing all object necessary for show page
    * */

    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map preResult = (Map) obj
            List<GroovyRowResult> lstDocArticleQuery = (List<GroovyRowResult>) preResult.get(LST_ARTICLE_QUERY)
            int count = (int) preResult.get(Tools.COUNT)
            List lstArticleQuery = wrapArticleQuery(lstDocArticleQuery, start)
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

    private List wrapArticleQuery(List<GroovyRowResult> lstDocArticleQuery, int start) {
        List lstArticleQuery = []
        try {
            int counter = start + 1
            for (int i = 0; i < lstDocArticleQuery.size(); i++) {
                GridEntity object = new GridEntity()
                GroovyRowResult articleQuery = lstDocArticleQuery[i]
                object.cell = [
                        counter,
                        articleQuery.id,
                        articleQuery.name,
                        articleQuery.value,
                        Tools.makeDetailsShort(articleQuery.criteria, 60)
                ]
                object.id = articleQuery.id
                lstArticleQuery << object
                counter++
            }
            return lstArticleQuery
        } catch (Exception e) {
            log.error(e.getMessage())
            return lstArticleQuery
        }
    }

    public Map searchListArticleQuery() {
        long companyId = docSessionUtil.appSessionUtil.getCompanyId()
        long userId = docSessionUtil.appSessionUtil.getAppUser().id
        String queryForList = """
            SELECT daq.id,daq.name,se.value,daq.criteria
            FROM doc_article_query daq
            LEFT JOIN system_entity se ON daq.content_type_id=se.id
            WHERE daq.company_id=:companyId
            AND daq.created_by=:userId
            AND ${queryType} ilike :query
            ORDER BY daq.created_on desc
            LIMIT :resultPerPage OFFSET :start
        """
        String queryForCount = """
            SELECT COUNT(daq.id) count
            FROM doc_article_query daq
            LEFT JOIN system_entity se ON daq.content_type_id=se.id
            WHERE daq.company_id=:companyId
            AND daq.created_by=:userId
            AND ${queryType} ilike :query
        """

        Map queryParams = [
                query: Tools.PERCENTAGE + query + Tools.PERCENTAGE,
                companyId: companyId,
                userId: userId,
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> lstArticleQuery = executeSelectSql(queryForList, queryParams)
        List<GroovyRowResult> resultCount = executeSelectSql(queryForCount, queryParams)
        int count = (int) resultCount[0][0]
        return [lstArticleQuery: lstArticleQuery, count: count]
    }
}

