package com.athena.mis.budget.actions.budgschema

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.ItemType
import com.athena.mis.application.entity.Project
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.application.utility.ItemTypeCacheUtility
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.application.utility.UnitCacheUtility
import com.athena.mis.budget.entity.BudgBudget
import com.athena.mis.budget.entity.BudgBudgetScope
import com.athena.mis.budget.entity.BudgSchema
import com.athena.mis.budget.service.BudgSchemaService
import com.athena.mis.budget.service.BudgetService
import com.athena.mis.budget.utility.BudgSessionUtil
import com.athena.mis.budget.utility.BudgetScopeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Show UI for budget schema CRUD and list of budget schema for grid
 *  For details go through Use-Case doc named 'ShowBudgSchemaActionService'
 */
class ShowBudgSchemaActionService extends BaseService implements ActionIntf {

    BudgetService budgetService
    BudgSchemaService budgSchemaService
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    BudgetScopeCacheUtility budgetScopeCacheUtility
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility
    @Autowired
    BudgSessionUtil budgSessionUtil
    @Autowired
    UnitCacheUtility unitCacheUtility

    private Logger log = Logger.getLogger(getClass())

    private static final String SERVER_ERROR_MESSAGE = "Fail to load budget details"
    private static final String DEFAULT_ERROR_MESSAGE = "Can't load budget details due to internal error"
    private static final String BUDGET_NOT_FOUND = "Budget not found"
    private static final String BUDGET_OBJ = "budgetObj"
    private static final String BUDGET_INFO_MAP = "budgetInfoMap"
    private static final String LST_BUDGET_SCHEMA = "lstBudgetSchema"
    private static final String ONE = "1 "

    /**
     * 1. check required parameters
     * 2. get budget object by id
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            GrailsParameterMap params = (GrailsParameterMap) parameters
            initPager(params)
            // check required parameters
            if (!params.budgetId) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long budgetId = Long.parseLong(params.budgetId.toString())
            BudgBudget budget = budgetService.read(budgetId)
            //Checking budget is exist or not by budgetId
            if (!budget) {
                result.put(Tools.MESSAGE, BUDGET_NOT_FOUND)
                return result
            }
            result.put(BUDGET_OBJ, budget)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * 1. get list & count of budget schema to show on grid
     * 2. build a map with necessary budget information
     * @param params -N/A
     * @param obj -map returned form executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned form executePreCondition method
            BudgBudget budget = (BudgBudget) preResult.get(BUDGET_OBJ)
            Map budgetInfoMap = buildBudgetInfoMap(budget) // build budget information map
            long companyId = budgSessionUtil.appSessionUtil.getCompanyId()
            List<BudgSchema> lstBudgetSchema = budgSchemaService.findAllByBudgetIdAndCompanyId(budget.id, companyId, this)
            int total = budgSchemaService.countByBudgetIdAndCompanyId(budget.id, companyId)
            result.put(BUDGET_INFO_MAP, budgetInfoMap)
            result.put(LST_BUDGET_SCHEMA, lstBudgetSchema)
            result.put(Tools.COUNT, total)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap budget schema list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show page
     * map -contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map executeResult = (LinkedHashMap) obj // cast map returned form execute method
            List<BudgSchema> lstBudgetSchema = (List<BudgSchema>) executeResult.get(LST_BUDGET_SCHEMA)
            Map budgetInfo = (Map) executeResult.get(BUDGET_INFO_MAP)
            int total = (int) executeResult.get(Tools.COUNT)
            List lstWrappedBudgetSchema = wrapBudgetSchema(lstBudgetSchema, start)
            Map gridObject = [page: pageNumber, total: total, rows: lstWrappedBudgetSchema]
            result.put(BUDGET_INFO_MAP, budgetInfo)
            result.put(LST_BUDGET_SCHEMA, gridObject)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
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
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Wrap list of budget schema in grid entity
     * @param lstBudgetSchema -list of budget schema object(s)
     * @param start -starting index of the page
     * @return -list of wrapped budget schema
     */
    private List wrapBudgetSchema(List<BudgSchema> lstBudgetSchema, int start) {
        List lstWrappedBudgetSchema = []
        int counter = start + 1
        for (int i = 0; i < lstBudgetSchema.size(); i++) {
            BudgSchema budgSchema = lstBudgetSchema[i]
            Item item = (Item) itemCacheUtility.read(budgSchema.itemId)
            ItemType itemType = (ItemType) itemTypeCacheUtility.read(item.itemTypeId)
            GridEntity obj = new GridEntity()
            obj.id = budgSchema.id
            obj.cell = [
                    counter,
                    itemType.name,
                    item.name,
                    Tools.formatAmountWithoutCurrency(budgSchema.quantity) + Tools.SINGLE_SPACE + item.unit,
                    Tools.makeAmountWithThousandSeparator(budgSchema.rate)
            ]
            lstWrappedBudgetSchema << obj
            counter++
        }
        return lstWrappedBudgetSchema
    }

    /**
     * Build a map with necessary budget information
     * @param budget -budget object from execute
     * @return -a map of budget
     */
    Map buildBudgetInfoMap(BudgBudget budget) {
        Project project = (Project) projectCacheUtility.read(budget.projectId)
        BudgBudgetScope budgetScope = (BudgBudgetScope) budgetScopeCacheUtility.read(budget.budgetScopeId)
        SystemEntity unit = (SystemEntity) unitCacheUtility.read(budget.unitId)
        Map budgetMap = [
                projectId: project.id,
                projectName: project.name,
                budgetScope: budgetScope.name,
                budgetId: budget.id,
                budgetItem: budget.budgetItem,
                details: Tools.makeDetailsShort(budget.details, 100)
        ]
        return budgetMap
    }
}
