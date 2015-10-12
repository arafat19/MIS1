package com.athena.mis.application.actions.entitycontent

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.Project
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.ContentEntityTypeCacheUtility
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.integration.accounting.AccountingPluginConnector
import com.athena.mis.integration.budget.BudgetPluginConnector
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Show UI for entityContent(attachment) CRUD & list of entityContent(attachment) object(s) for grid
 *  For details go through Use-Case doc named 'ShowEntityContentActionService'
 */
class ShowEntityContentActionService extends BaseService implements ActionIntf {

    protected final Logger log = Logger.getLogger(getClass())

    @Autowired(required = false)
    BudgetPluginConnector budgetImplService
    @Autowired(required = false)
    AccountingPluginConnector accountingImplService
    @Autowired(required = false)
    ExchangeHousePluginConnector exchangeHouseImplService
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    ContentEntityTypeCacheUtility contentEntityTypeCacheUtility
    @Autowired
    ProjectCacheUtility projectCacheUtility

    private static final String DEFAULT_ERROR_MESSAGE = "Can't load attachment list"
    private static final String ENTITY_CONTENT_LIST = "entityContentList"
    private static final String ENTITY_CONTENT_MAP = "entityContentMap"
    private static final String ENTITY_TYPE_ID = "entityTypeId"
    private static final String ENTITY_ID = "entityId"

    /**
     * do nothing for pre condition
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * get list of entityContent(attachment) object(s) for grid
     * @param parameters -parameters send from UI
     * @param obj -N/A
     * @return -a map containing list of item object(s) for buildSuccessResultForUI
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
            long entityTypeId = Long.parseLong(parameters.entityTypeId.toString())
            long entityId = Long.parseLong(parameters.entityId.toString())

            //get entityContent(attachment) list for grid
            LinkedHashMap entityContent = listOfEntityContent(entityTypeId, entityId)
            List<GroovyRowResult> entityContentList = entityContent.entityContentList
            int total = (int) entityContent.count

            SystemEntity systemEntity = (SystemEntity) contentEntityTypeCacheUtility.read(entityTypeId)
            Map entityContentMap = buildEntityContentMap(parameters, systemEntity, entityId)
            result.put(ENTITY_CONTENT_MAP, entityContentMap)
            result.put(ENTITY_CONTENT_LIST, entityContentList)
            result.put(Tools.COUNT, total)
            result.put(ENTITY_TYPE_ID, entityTypeId)
            result.put(ENTITY_ID, entityId)
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
     * wrap entityContent(attachment) object List and wrap budgetList to show on grid
     * @param obj -a map contains all necessary objects receives from execute method
     * @return -all necessary objects to show on UI
     * map contains isError(true/false) depending on method success
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
            result.put(ENTITY_CONTENT_MAP, receiveResult.get(ENTITY_CONTENT_MAP))
            result.put(ENTITY_TYPE_ID, receiveResult.get(ENTITY_TYPE_ID))
            result.put(ENTITY_ID, receiveResult.get(ENTITY_ID))
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
     * get list of entityContent(attachment) object(s) to show on grid
     * @return -a map contains list of entityContent(attachment) object(s) and count
     */
    private LinkedHashMap listOfEntityContent(long entityTypeId, long entityId) {
        long companyId = appSessionUtil.getCompanyId()

        String strQuery = """
            SELECT ec.id, ec.extension, ec.caption, cg.name AS content_category, type.key AS content_type
            FROM entity_content ec
            LEFT JOIN content_category cg ON cg.id = ec.content_category_id
            LEFT JOIN system_entity type ON type.id = ec.content_type_id
            WHERE ec.entity_type_id = :entityTypeId
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
            WHERE ec.entity_type_id = :entityTypeId
            AND ec.entity_id = :entityId
            AND ec.company_id = :companyId
            AND cg.is_reserved = FALSE
        """

        Map queryParams = [
                entityTypeId: entityTypeId,
                entityId: entityId,
                companyId: companyId,
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> result = executeSelectSql(strQuery, queryParams)
        List<GroovyRowResult> countResult = executeSelectSql(queryCount, queryParams)

        int total = (int) countResult[0].count
        return [entityContentList: result, count: total]
    }

    /**
     * Build entity content with dynamic left menu
     * @param systemEntity - object of SystemEntity
     * @param entityId - entity id
     * @return -  a map containing entity type name, entity name, plugin id and corresponding left menu link
     */
    private Map buildEntityContentMap(GrailsParameterMap parameters, SystemEntity systemEntity, long entityId) {
        String entityTypeName = systemEntity.key + Tools.COLON
        String entityName = Tools.EMPTY_SPACE
        String leftMenu = Tools.EMPTY_SPACE
        String panelTitle = Tools.EMPTY_SPACE
        int pluginId = 0
        switch (systemEntity.reservedId) {
            case contentEntityTypeCacheUtility.CONTENT_ENTITY_TYPE_BUDG_SPRINT:
                Object budgSprint = budgetImplService.readBudgSprint(entityId)
                entityName = budgSprint.name
                pluginId = PluginConnector.BUDGET_ID
                leftMenu = '#budgSprint/show'
                panelTitle = 'Create Attachment for Sprint'
                break
            case contentEntityTypeCacheUtility.CONTENT_ENTITY_TYPE_BUDGET:
                Object budget = budgetImplService.readBudget(entityId)
                entityName = budget.budgetItem
                pluginId = PluginConnector.BUDGET_ID
                leftMenu = '#budgBudget/show'
                panelTitle = 'Create Attachment for Budget'
                break
            case contentEntityTypeCacheUtility.CONTENT_ENTITY_TYPE_PROJECT:
                Project project = (Project) projectCacheUtility.read(entityId)
                entityName = project.name
                pluginId = PluginConnector.APPLICATION_ID
                leftMenu = '#project/show'
                panelTitle = 'Create Attachment for Project'
                break
            case contentEntityTypeCacheUtility.CONTENT_ENTITY_TYPE_FINANCIAL_YEAR:
                Object financialYear = accountingImplService.readFinancialYear(entityId)
                entityName = DateUtility.getLongDateForUI(financialYear.startDate) + Tools.TO + DateUtility.getLongDateForUI(financialYear.endDate)
                pluginId = PluginConnector.ACCOUNTING_ID
                leftMenu = '#accFinancialYear/show'
                panelTitle = 'Create Attachment for Financial Year'
                break
            case contentEntityTypeCacheUtility.CONTENT_ENTITY_TYPE_EXH_CUSTOMER:
                Object exhCustomer = exchangeHouseImplService.readCustomer(entityId)
                entityName = exhCustomer.name + (exhCustomer.surname ? (Tools.SINGLE_SPACE + exhCustomer.surname) : Tools.EMPTY_SPACE) + Tools.PARENTHESIS_START + exhCustomer.code + Tools.PARENTHESIS_END
                pluginId = PluginConnector.EXCHANGE_HOUSE_ID
                leftMenu = '#exhCustomer/' + parameters.leftMenu
                panelTitle = 'Create Attachment for Exchange House Customer'
                break
            default:
                break
        }
        Map entityContentMap = [
                entityTypeName: entityTypeName,
                entityName: entityName,
                pluginId: pluginId,
                leftMenu: leftMenu,
                panelTitle: panelTitle
        ]
        return entityContentMap
    }
}
