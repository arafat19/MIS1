package com.athena.mis.budget.actions.report.budget

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.EntityContent
import com.athena.mis.application.entity.Project
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.*
import com.athena.mis.budget.entity.BudgBudget
import com.athena.mis.budget.entity.BudgBudgetScope
import com.athena.mis.budget.model.BudgetDetailsItemModel
import com.athena.mis.budget.utility.BudgSessionUtil
import com.athena.mis.budget.utility.BudgetScopeCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.converters.JSON
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * UI level Search for budget.
 * For details go through Use-Case doc named 'SearchForBudgetActionService'
 */
class SearchForBudgetActionService extends BaseService implements ActionIntf {

    @Autowired
    BudgSessionUtil budgSessionUtil
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility
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
    LinkGenerator grailsLinkGenerator

    private static final String BUDGET_NOT_FOUND = "Budget not found"
    private static final String FAILURE_MSG = "Fail to generate budget"
    private static final String USER_PROJECT_NOT_MAPPED = "User is not associated with any project"
    private static final String USER_HAS_NOT_ACCESS = "User is not associated with this project"
    private static final String BUDGET_MAP = "budgetMap"
    private static final String BUDGET_OBJ = "budget"
    private static final String ITEM_LIST = "itemList"
    private static final String CONTENT_MAP_LIST = "contentMapList"
    private static final String ITEM_MAP_LIST = "itemMapList"
    private static final String ENTITY_CONTENT = "entityContent"
    private static final String DOWNLOAD_CONTENT = "downloadContent"

    private Logger log = Logger.getLogger(getClass())

    /**
     * Check several pre conditions and get budget object
     * 1. Check budget existence
     * 2. Get the project ids those are associated with the user
     * 3. Check budget object existence
     * 4. Checking user access for the particular project
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return -a map containing voucher object & isError msg(True/False)
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            // Check the existence of budget by budgetLineItem from params
            if (!params.budgetLineItem) {
                result.put(Tools.MESSAGE, BUDGET_NOT_FOUND)
                return result
            }

            String budgetLineItem = params.budgetLineItem.toString()
            List<Long> userProjectIds = (List<Long>) budgSessionUtil.appSessionUtil.getUserProjectIds()
            // Checking user with project mapping
            if (userProjectIds.size() <= 0) {
                result.put(Tools.MESSAGE, USER_PROJECT_NOT_MAPPED)
                return result
            }

            long companyId = budgSessionUtil.appSessionUtil.getCompanyId()
            BudgBudget budget = checkByCompanyIdAndBudgetItem(budgetLineItem, companyId)
            //Check budget object existence
            if (!budget) {
                result.put(Tools.MESSAGE, BUDGET_NOT_FOUND)
                return result
            }
            // Checking user access for the particular project
            boolean isAccessible = projectCacheUtility.isAccessible(budget.projectId)
            if (!isAccessible) {
                result.put(Tools.MESSAGE, USER_HAS_NOT_ACCESS)
                return result
            }

            result.put(BUDGET_OBJ, budget)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    /**
     * nothing to do in post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get all parameters to generate budget report
     * 1. Get budget object from executePreCondition
     * 2. Get item list by BudgetDetailsItemModel using budget id
     * 3. Get content list if (budget.contentCount > 0)
     * 4. Build budget object
     * @param parameters -N/A
     * @param obj - map received from pre execute method
     * @return - a map containing budget object
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            BudgBudget budget = (BudgBudget) preResult.get(BUDGET_OBJ)
            List<BudgetDetailsItemModel> itemList = BudgetDetailsItemModel.findAllByBudgetId(budget.id, [readOnly: true])

            // ITEM map list for html view for search task UI
            List itemMapList = []
            for (int i = 0; i < itemList.size(); i++) {
                BudgetDetailsItemModel item = itemList[i]
                Map itemMap = [
                        sl: i + 1,
                        itemType: item.itemTypeName,
                        itemName: item.itemName,
                        code: item.itemCode,
                        quantity: item.strQuantity,
                        estimateRate: item.strRate,
                        totalCost: item.strTotalCost
                ]
                itemMapList << itemMap
            }

            if (budget.contentCount > 0) {
                List<GroovyRowResult> contentList = getContentList(budget.id) // get content list
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

            LinkedHashMap budgetMap = buildBudgetMap(budget)  // build budget map
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(ITEM_MAP_LIST, itemMapList as JSON)
            result.put(BUDGET_MAP, budgetMap)
            result.put(ITEM_LIST, itemList)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    /**
     * Get budget object, item list, content list from execute method
     * @param obj - object received from execute method
     * @return - a map containing budget map, item list map, content list map & error msg(true/false)
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj

            result.put(BUDGET_MAP, executeResult.get(BUDGET_MAP))
            result.put(ITEM_MAP_LIST, executeResult.get(ITEM_MAP_LIST))
            result.put(ITEM_LIST, executeResult.get(ITEM_LIST))
            result.put(CONTENT_MAP_LIST, executeResult.get(CONTENT_MAP_LIST))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
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
            LinkedHashMap executeResult = (LinkedHashMap) obj

            result.put(Tools.IS_ERROR, executeResult.get(Tools.IS_ERROR))
            if (executeResult.message) {
                result.put(Tools.MESSAGE, executeResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, FAILURE_MSG)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    /**
     * Build budget map
     * 1. Pull project object from projectCacheUtility by budget.projectId
     * 2. Pull unit from unitCacheUtility by budget.unitId
     * 3. Pull createdBy from appUserCacheUtility by budget.createdBy
     * 4. Pull budgetScope from budgetScopeCacheUtility by budget.budgetScopeId
     * 5. Pull total estimation cost of Budget (quantity * estimated rate)
     * @param budget - object of BudgBudget
     * @return - a new map of budget
     */
    private LinkedHashMap buildBudgetMap(BudgBudget budget) {
        Project project = (Project) projectCacheUtility.read(budget.projectId)
        AppUser createdBy = (AppUser) appUserCacheUtility.read(budget.createdBy)
        BudgBudgetScope budgetScope = (BudgBudgetScope) budgetScopeCacheUtility.read(budget.budgetScopeId)
        double totalCost = getTotalEstimationCostForBudget(budget.id)
        // get total estimation cost of specific item (quantity * estimated rate)
        SystemEntity unit = (SystemEntity) unitCacheUtility.read(budget.unitId)

        LinkedHashMap budgetMap = [
                budgetId: budget.id,
                createdOn: DateUtility.getDateFormatAsString(budget.createdOn), // Purchase order date
                printDate: DateUtility.getDateFormatAsString(new Date()),
                createdBy: createdBy.username,
                projectName: project.name,
                projectDescription: project.description ? project.description : Tools.EMPTY_SPACE,
                budgetItem: budget.budgetItem,
                budgetDetails: budget.details,
                budgetScope: budgetScope.name,
                budgetQuantity: Tools.formatAmountWithoutCurrency(budget.budgetQuantity) + Tools.SINGLE_SPACE + unit.key,
                totalCost: Tools.formatAmountWithoutCurrency(totalCost),
                itemCount: budget.itemCount,
                contentCount: budget.contentCount,
                contractRate: Tools.formatAmountWithoutCurrency(budget.contractRate),
                costPerUnit: Tools.formatAmountWithoutCurrency(totalCost / budget.budgetQuantity)
        ]
        return budgetMap
    }

    /**
     * Get budget by dynamic finder using budget item
     * 1. check the existence of by condition if ((budget) && (budget.companyId == companyId))
     * @param budgetItem - string of budget item
     * @param companyId - get company id from executePreCondition
     * @return - a budget object or null by conditional check
     */
    private BudgBudget checkByCompanyIdAndBudgetItem(String budgetItem, long companyId) {
        BudgBudget budget = BudgBudget.findByBudgetItem(budgetItem, [readOnly: true])
        if ((budget) && (budget.companyId == companyId)) {
            return budget
        }
        return null
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
