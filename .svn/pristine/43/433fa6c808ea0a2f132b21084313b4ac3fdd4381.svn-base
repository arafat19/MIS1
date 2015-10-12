package com.athena.mis.budget.actions.report.budget

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Project
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.ContentEntityTypeCacheUtility
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.application.utility.UnitCacheUtility
import com.athena.mis.budget.entity.BudgBudget
import com.athena.mis.budget.entity.BudgBudgetScope
import com.athena.mis.budget.utility.BudgSessionUtil
import com.athena.mis.budget.utility.BudgetScopeCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Download Budget in pdf format
 * For details go through Use-Case doc named 'DownloadForBudgetActionService'
 */
class DownloadForBudgetActionService extends BaseService implements ActionIntf {

    JasperService jasperService
    @Autowired
    BudgSessionUtil budgSessionUtil
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

    private static final String BUDGET_NOT_FOUND = "Budget not found."
    private static final String FAILURE_MSG = "Fail to generate Budget."
    private static final String BUDGET_MAP = "budgetMap"
    private static final String BUDGET_OBJ = "budget"
    private static final String BUDGET_ID = "budgetId"

    private static final String REPORT_FILE_FORMAT = 'pdf'
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_FOLDER = 'budgBudget'
    private static final String OUTPUT_FILE_NAME = 'budget'
    private static final String REPORT_TITLE = 'Budget'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String JASPER_FILE = 'budget.jasper'
    private static final String PDF_EXTENSION = ".pdf"
    private static final String REPORT = "report"
    private static final String DB_QUANTITY_FORMAT = "dbQuantityFormat"
    private static final String DB_CURRENCY_FORMAT = "dbCurrencyFormat"
    private static final String CONTENT_ENTITY_TYPE_BUDGET_ID = "contentEntityTypeBudgetId"

    protected final Logger log = Logger.getLogger(getClass())

    /**
     * Get budget object against budget id
     * 1. Check budget object existence
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return - a map containing budget object & isError(True/False)
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            long budgetId = Long.parseLong(params.budgetId.toString())
            long companyId = budgSessionUtil.appSessionUtil.getCompanyId()

            // Pull budget object
            BudgBudget budget = BudgBudget.read(budgetId)

            // Checking budget object existence
            if (!budget || budget.companyId != companyId) {
                result.put(Tools.MESSAGE, BUDGET_NOT_FOUND)
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
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get desired report providing all required parameters
     * 1. Build budget map by given parameters
     * @param parameters - N/A
     * @param obj - receive map from executePreCondition
     * @return -a map containing report
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            BudgBudget budget = (BudgBudget) preResult.get(BUDGET_OBJ)

            LinkedHashMap budgetMap = buildBudgetMap(budget) // Build budget map
            Map report = getBudgetReport(budgetMap)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(REPORT, report)
            return result
        } catch (Exception ex) {
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
     * Build budget map for pdf report
     * 1. Pull project object from projectCacheUtility by budget.projectId
     * 2. Pull unit from unitCacheUtility by budget.unitId
     * 3. Pull createdBy from appUserCacheUtility by budget.createdBy
     * 4. Pull budgetType from budgetScopeCacheUtility by budget.budgetScopeId
     * 5. Pull total estimation cost of Budget (quantity * estimated rate) by using getTotalEstimationCostForBudget method
     * @param budget -budget object
     * @return - a new map of budget object
     */
    private LinkedHashMap buildBudgetMap(BudgBudget budget) {
        Project project = (Project) projectCacheUtility.read(budget.projectId)
        SystemEntity unit = (SystemEntity) unitCacheUtility.read(budget.unitId)
        AppUser createdBy = (AppUser) appUserCacheUtility.read(budget.createdBy)
        BudgBudgetScope budgetScope = (BudgBudgetScope) budgetScopeCacheUtility.read(budget.budgetScopeId)
        double totalCost = getTotalEstimationCostForBudget(budget.id)

        LinkedHashMap budgetMap = [
                budgetId: budget.id,
                createdOn: DateUtility.getDateFormatAsString(budget.createdOn), // Purchase Order date
                printDate: DateUtility.getDateFormatAsString(new Date()),
                createdBy: createdBy.username,
                projectName: project.name,
                projectDescription: project.description,
                budgetItem: budget.budgetItem,
                budgetDetails: budget.details,
                budgetScope: budgetScope.name,
                budgetQuantity: Tools.formatAmountWithoutCurrency(budget.budgetQuantity) + Tools.SINGLE_SPACE + unit.key,
                totalCost: Tools.formatAmountWithoutCurrency(totalCost),
                itemCount: budget.itemCount,
                contractRate: Tools.formatAmountWithoutCurrency(budget.contractRate),
                costPerUnit: Tools.formatAmountWithoutCurrency(totalCost / budget.budgetQuantity)
        ]
        return budgetMap
    }

    /**
     * Generate Budget report with assigned parameters
     * @param budgetMap - map of budget
     * @return - generated report with required params
     */
    private Map getBudgetReport(Map budgetMap) {
        long companyId = budgSessionUtil.appSessionUtil.getCompanyId()
        // pull system entity type(Budget) object
        SystemEntity contentEntityTypeBudget = (SystemEntity) contentEntityTypeCacheUtility.readByReservedAndCompany(contentEntityTypeCacheUtility.CONTENT_ENTITY_TYPE_BUDGET, companyId)

        String reportDir = Tools.getBudgetReportDirectory() + File.separator + REPORT_FOLDER
        String outputFileName = OUTPUT_FILE_NAME + Tools.HYPHEN + budgetMap.budgetId + PDF_EXTENSION
        Map reportParams = new LinkedHashMap()
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(BUDGET_MAP, budgetMap)
        reportParams.put(BUDGET_ID, budgetMap.budgetId)
        reportParams.put(CONTENT_ENTITY_TYPE_BUDGET_ID, contentEntityTypeBudget.id)
        reportParams.put(DB_QUANTITY_FORMAT, Tools.DB_QUANTITY_FORMAT)
        reportParams.put(DB_CURRENCY_FORMAT, Tools.DB_CURRENCY_FORMAT)
        ByteArrayOutputStream report

        JasperReportDef reportDef = new JasperReportDef(name: JASPER_FILE, fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: reportParams, folder: reportDir)

        report = jasperService.generateReport(reportDef)   // generate pdf report

        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]

    }

    private static final String SELECT_QUERY = """
        SELECT SUM(quantity * rate) AS total
        FROM budg_budget_details
        WHERE budget_id=:budgetId
        """

    /**
     * Get total estimation cost in budget report(summary)
     * @param budgetId - Budget Id
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
}
