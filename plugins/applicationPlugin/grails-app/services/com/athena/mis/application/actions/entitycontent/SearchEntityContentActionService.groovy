package com.athena.mis.application.actions.entitycontent

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.application.utility.ContentEntityTypeCacheUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to get search result list of entityContent(attachment) objects to show on grid
 *  For details go through Use-Case doc named 'SearchEntityContentActionService'
 */
class SearchEntityContentActionService extends BaseService implements ActionIntf {

    protected final Logger log = Logger.getLogger(getClass())

    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility
    @Autowired
    ContentEntityTypeCacheUtility contentEntityTypeCacheUtility

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to search attachment list"
    private static final String ENTITY_CONTENT_LIST = "entityContentList"

    /**
     * do nothing for pre condition
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * get search result list of entityContent(attachment) objects
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map contains list of entityContent(Project attachment) objects  and count
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameters = (GrailsParameterMap) params
            if (!parameters.entityTypeId || !parameters.entityId) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            initSearch(params)
            long entityTypeId = Long.parseLong(parameters.entityTypeId.toString())
            long entityId = Long.parseLong(parameters.entityId.toString())
            LinkedHashMap entityContent = searchForEntityContent(entityTypeId, entityId)
            List<GroovyRowResult> entityContentList = entityContent.searchResult
            int count = (int) entityContent.count
            result.put(ENTITY_CONTENT_LIST, entityContentList)
            result.put(Tools.COUNT, count)
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
     * do nothing for post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * wrap entityContent(attachment) objects list to show on grid
     * @param obj -a map contains entityContent(attachment) objects list and count
     * @return -wrapped entityContent(attachment) objects list to show on grid
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            List<GroovyRowResult> entityContentList = (List) receiveResult.get(ENTITY_CONTENT_LIST)
            int total = (int) receiveResult.get(Tools.COUNT)
            List<GroovyRowResult> entityContentListWrap = wrapEntityContentList(entityContentList, start)
            Map gridObject = [page: pageNumber, total: total, rows: entityContentListWrap]
            result.put(ENTITY_CONTENT_LIST, gridObject)
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

    /**
     * wrapped entityContent(attachment) objects list
     * @param entityContentList -list of GroovyRowResult
     * @param start -start index
     * @return -wrappedEntityContent(attachment) objects list
     */
    private List wrapEntityContentList(List<GroovyRowResult> entityContentList, int start) {
        List listEntityContent = [] as List
        int counter = start + 1
        for (int i = 0; i < entityContentList.size(); i++) {
            GroovyRowResult entityContent = entityContentList[i]
            GridEntity obj = new GridEntity()
            obj.id = entityContent.id
            obj.cell = [
                    counter,
                    entityContent.id,
                    entityContent.content_type,
                    entityContent.content_category,
                    entityContent.extension,
                    entityContent.caption
            ]
            listEntityContent << obj
            counter++
        }
        return listEntityContent
    }

    /**
     * get search result list of entityContent(attachment) object(s) to show on grid
     * @return -a map contains list of entityContent(attachment) object(s) and count
     */
    private LinkedHashMap searchForEntityContent(long entityTypeId, long entityId) {
        long companyId = appSessionUtil.getCompanyId()

        String strQuery = """
            SELECT ec.id, ec.extension, ec.caption, cg.name AS content_category, type.key AS content_type
            FROM entity_content ec
            LEFT JOIN content_category cg ON cg.id = ec.content_category_id
            LEFT JOIN system_entity type ON type.id = ec.content_type_id
            WHERE ${queryType} ilike :query
            AND ec.entity_type_id = :entityTypeId
            AND ec.entity_id = :entityId
            AND ec.company_id = :companyId
            AND cg.is_reserved = FALSE
            ORDER BY ec.id desc
            LIMIT :resultPerPage OFFSET :start
        """

        String queryCount = """
            SELECT COUNT(ec.id) count
            FROM entity_content ec
            LEFT JOIN content_category cg ON cg.id = ec.content_category_id
            WHERE ${queryType} ilike :query
            AND ec.entity_type_id = :entityTypeId
            AND ec.entity_id = :entityId
            AND ec.company_id = :companyId
            AND cg.is_reserved = FALSE
        """

        Map queryParams = [
                query: Tools.PERCENTAGE + query + Tools.PERCENTAGE,
                entityTypeId: entityTypeId,
                entityId: entityId,
                companyId: companyId,
                resultPerPage: resultPerPage,
                start: start
        ]

        List<GroovyRowResult> result = executeSelectSql(strQuery, queryParams)
        List<GroovyRowResult> countResult = executeSelectSql(queryCount, queryParams)

        int total = (int) countResult[0].count
        return [searchResult: result, count: total]
    }
}
