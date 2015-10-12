package com.athena.mis.budget.actions.report.budget

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.EntityContent
import com.athena.mis.application.entity.Project
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.ContentEntityTypeCacheUtility
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.application.utility.UnitCacheUtility
import com.athena.mis.budget.entity.BudgBudget
import com.athena.mis.budget.entity.BudgBudgetScope
import com.athena.mis.budget.model.BudgetDetailsItemModel
import com.athena.mis.budget.utility.BudgSessionUtil
import com.athena.mis.budget.utility.BudgetScopeCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import grails.converters.JSON
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Show Budget and list, item list and content list in UI
 *  For details go through Use-Case doc named 'ShowForBudgetActionService'
 */
class ShowForBudgetActionService extends BaseService implements ActionIntf {
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    BudgetScopeCacheUtility budgetScopeCacheUtility
    @Autowired
    UnitCacheUtility unitCacheUtility
    @Autowired
    ContentEntityTypeCacheUtility contentEntityTypeCacheUtility
    @Autowired
    BudgSessionUtil budgSessionUtil
    @Autowired
    LinkGenerator grailsLinkGenerator

    private static final String FAILURE_MSG = "Fail to generate budget"
    private static final String BUDGET_MAP = "budgetMap"
    private static final String ITEM_LIST = "itemList"
    private static final String ITEM_MAP_LIST = "itemMapList"
    private static final String CONTENT_MAP_LIST = "contentMapList"
    private static final String ENTITY_CONTENT = "entityContent"
    private static final String DOWNLOAD_CONTENT = "downloadContent"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get budget list, item list and content list
     * 1. Get budget object by BudgBudget.read(budgetId)
     * 2. Get item list from BudgetDetailsItemModel by budget id when budget object exist
     * 3. Build budget map by using buildBudgetMap(budget) method
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return - contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap params = (GrailsParameterMap) parameters

            if (params.budgetId) {
                long budgetId = Long.parseLong(params.budgetId.toString())
                BudgBudget budget = BudgBudget.read(budgetId)
                if (budget) {
                    List<BudgetDetailsItemModel> itemList = BudgetDetailsItemModel.findAllByBudgetId(budget.id, [readOnly: true])
                    LinkedHashMap budgetMap = buildBudgetMap(budget)  // build budget map

                    // acceptance criteria map list for html view for search task UI
                    List itemMapList = []
                    for (int i = 0; i < itemList.size(); i++) {
                        BudgetDetailsItemModel item = itemList[i]
                        Map itemMap = [
                                sl: i + 1,
                                itemType: item.itemTypeName,
                                itemName: item.itemName,
                                code: item.itemCode,
                                quantity: item.strQuantity,
                                estimateRate:item.strRate,
                                totalCost: item.strTotalCost
                        ]
                        itemMapList << itemMap
                    }

                    result.put(ITEM_MAP_LIST, itemMapList as JSON)
                    result.put(BUDGET_MAP, budgetMap)
                    result.put(ITEM_LIST, itemList)

                    if (budget.contentCount > 0) {
                        List contentList = getContentList(budget.id)
                        List lstContent = []
                        for (int i = 0; i < contentList.size(); i++) {
                            GroovyRowResult eachRow = contentList[i]
                            String link = grailsLinkGenerator.link(controller: ENTITY_CONTENT, action: DOWNLOAD_CONTENT, absolute: true, params: [entityContentId: eachRow.id])
                            Map contentMap = [
                                    sl: i + 1,
                                    caption: eachRow.caption,
                                    fileName: eachRow.file_name,
                                    link: link
                            ]
                            lstContent << contentMap
                        }
                        result.put(CONTENT_MAP_LIST, lstContent as JSON)
                    }
                }
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    /**
     * do nothing for build success operation
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    /**
     * do nothing for build failure operation
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    /**
     * Build budget map
     * 1. Pull project object from projectCacheUtility by budget.projectId
     * 2. Pull unit from unitCacheUtility by budget.unitId
     * 3. Pull createdBy from appUserCacheUtility by budget.createdBy
     * 4. Pull budgetScope from budgetScopeCacheUtility by budget.budgetScopeId
     * 5. Pull total estimation cost of Budget (quantity * estimated rate) by using getTotalEstimationCostForBudget
     * @param budget - object of BudgBudget
     * @return - a new map of budget
     */
    private LinkedHashMap buildBudgetMap(BudgBudget budget) {
        Project project = (Project) projectCacheUtility.read(budget.projectId)
        AppUser createdBy = (AppUser) appUserCacheUtility.read(budget.createdBy)
        BudgBudgetScope budgetScope = (BudgBudgetScope) budgetScopeCacheUtility.read(budget.budgetScopeId)
        double totalCost = getTotalEstimationCostForBudget(budget.id)
        SystemEntity unit = (SystemEntity) unitCacheUtility.read(budget.unitId)

        LinkedHashMap budgetMap = [
                budgetId: budget.id,
                createdOn: DateUtility.getDateFormatAsString(budget.createdOn), // purchase order date
                printDate: DateUtility.getDateFormatAsString(new Date()),
                createdBy: createdBy.username, // logged in user name
                projectName: project.name,   // project name
                projectDescription: project.description ? project.description : Tools.EMPTY_SPACE,
                budgetItem: budget.budgetItem,
                budgetDetails: budget.details,
                budgetScope: budgetScope.name,   // budget scope name
                budgetQuantity: Tools.formatAmountWithoutCurrency(budget.budgetQuantity) + Tools.SINGLE_SPACE + unit.key,
                totalCost: Tools.formatAmountWithoutCurrency(totalCost),
                itemCount: budget.itemCount,
                contentCount: budget.contentCount,
                contractRate: Tools.formatAmountWithoutCurrency(budget.contractRate),
                costPerUnit: Tools.formatAmountWithoutCurrency(totalCost / budget.budgetQuantity)
        ]
        return budgetMap
    }

    private static final String SELECT_QUERY = """
        SELECT SUM(quantity * rate) AS total
        FROM budg_budget_details
        WHERE budget_id=:budgetId
        """

    /**
     * Get total estimation cost by budget id
     * 1. Get result by executeSelectSql method method by sending raw query string(SELECT_QUERY) and queryParams
     * @param budgetId - get budget id from buildBudgetMap() method
     * @return - total cost of specific item (quantity * estimated rate)
     */
    private double getTotalEstimationCostForBudget(long budgetId) {
        //@todo:model use existing sql
        Map queryParams = [budgetId: budgetId]
        List result = executeSelectSql(SELECT_QUERY, queryParams)
        if (result[0].total) {
            double total = Double.parseDouble(result[0].total.toString())
            return total
        }
        return 0.00
    }

    private static final String ENTITY_CONTENT_SELECT_QUERY = """
            SELECT id, caption, file_name FROM entity_content
            WHERE entity_type_id =:entityTypeId AND
                  entity_id =:budgetId
            ORDER BY caption
    """

    /**
     * Get Budget Content List
     * 1. Get budget content list by executeSelectSql method by sending raw query string(ENTITY_CONTENT_SELECT_QUERY) and queryParams
     * @param budgetId - budget id from execute method
     * @return - a list of budget content
     */
    private List<GroovyRowResult> getContentList(long budgetId) {
        long companyId = budgSessionUtil.appSessionUtil.getCompanyId()
        // pull system entity type(Budget) object
        SystemEntity contentEntityTypeBudget = (SystemEntity) contentEntityTypeCacheUtility.readByReservedAndCompany(contentEntityTypeCacheUtility.CONTENT_ENTITY_TYPE_BUDGET, companyId)

        Map queryParam = [
                entityTypeId: contentEntityTypeBudget.id,
                budgetId: budgetId
        ]
        List<GroovyRowResult> budgetContentList = executeSelectSql(ENTITY_CONTENT_SELECT_QUERY, queryParam)
        return budgetContentList
    }
}
