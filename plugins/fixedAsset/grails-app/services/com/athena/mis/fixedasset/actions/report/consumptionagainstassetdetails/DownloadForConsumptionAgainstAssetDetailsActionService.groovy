package com.athena.mis.fixedasset.actions.report.consumptionagainstassetdetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.Project
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.application.utility.UserProjectCacheUtility
import com.athena.mis.integration.inventory.InventoryPluginConnector
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Download PDF of Consumption Against Asset Details.
 * For details go through Use-Case doc named 'DownloadForConsumptionAgainstAssetDetailsActionService'
 */
class DownloadForConsumptionAgainstAssetDetailsActionService extends BaseService implements ActionIntf {

    protected final Logger log = Logger.getLogger(getClass())

    JasperService jasperService
    @Autowired(required = false)
    InventoryPluginConnector inventoryImplService
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    UserProjectCacheUtility userProjectCacheUtility

    private static final String DEFAULT_ERROR_MESSAGE = "Fail to generate consumption details list Report."
    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String PROJECT_NOT_FOUND_MESSAGE = "User is not associated with any project"
    private static final String FIXED_ASSET_NOT_FOUND_MESSAGE = "Fixed asset not found"
    private static final String FROM_DATE = "fromDate"
    private static final String TO_DATE = "toDate"
    private static final String TRANSACTION_TYPE_CON_ID = "transactionTypeConId"
    private static final String REPORT_FILE_FORMAT = 'pdf'
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_FOLDER = 'consumptionAgainstAssetDetails'
    private static final String OUTPUT_FILE_NAME = 'consumptionAgainstAssetDetails'
    private static final String REPORT_TITLE = 'Consumption Against Asset Details Report'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String JASPER_FILE = 'consumptionAgainstAssetDetails.jasper'
    private static final String PDF_EXTENSION = ".pdf"
    private static final String REPORT = "report"
    private static final String DB_QUANTITY_FORMAT = "dbQuantityFormat"
    private static final String USER_PROJECT_IDS = "userProjectIds"
    private static final String FIXED_ASSET_DETAILS_IDS = "fixedAssetDetailsIds"
    private static final String PROJECT_NAME = "projectName"
    private static final String ITEM_NAME = "itemName"
    private static final String ITEM_ID = "itemId"
    private static final String LABEL_ALL = "ALL"

    /**
     * 1. check input validation
     * @param parameters - serialized parameters from UI.
     * @param obj - N/A
     * @return- a map containing isError msg(True/False) and relevant msg(if any)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            if (!params.fromDate || !params.toDate || !params.itemId) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
            }

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
     * 1. check project existence & pull project object
     * 2. pull list of fixed asset details ids
     * 3. set transaction type = consumption
     * 4. get generated report
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return - generated report
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters

            Date fromDate = DateUtility.parseMaskedFromDate(params.fromDate.toString())
            Date toDate = DateUtility.parseMaskedToDate(params.toDate.toString())
            long itemId = Long.parseLong(params.itemId.toString())

            if (params.fixedAssetDetailsId.equals(Tools.EMPTY_SPACE)) {
                params.fixedAssetDetailsId = 0L
            }
            if (params.projectId.equals(Tools.EMPTY_SPACE)) {
                params.projectId = 0L
            }
            String projectName = LABEL_ALL
            List<Long> userProjectIds = []
            long projectId = Long.parseLong(params.projectId.toString())
            if (projectId <= 0) {
                userProjectIds = userProjectCacheUtility.listUserProjectIds()
                if (userProjectIds.size() <= 0) {
                    result.put(Tools.MESSAGE, PROJECT_NOT_FOUND_MESSAGE)
                    return result
                }
            } else {
                Project project = (Project) projectCacheUtility.read(projectId)
                projectName = project.name
                userProjectIds << projectId
            }
            long transactionTypeConId = inventoryImplService.getInvTransactionTypeIdConsumption()

            List<Long> fixedAssetDetailsIds = []
            long fixedAssetDetailsId = Long.parseLong(params.fixedAssetDetailsId.toString())
            if (fixedAssetDetailsId <= 0) {//For All FixedAsset
                List fixedAssetList = getFixedAssetIdList(fromDate, toDate, userProjectIds, itemId, transactionTypeConId)
                if (fixedAssetList.size() <= 0) {
                    result.put(Tools.MESSAGE, FIXED_ASSET_NOT_FOUND_MESSAGE)
                    return result
                }
                for (int i = 0; i < fixedAssetList.size(); i++) {
                    fixedAssetDetailsIds << fixedAssetList[i].id
                }
            } else {//For exact FixedAsset
                fixedAssetDetailsIds << fixedAssetDetailsId
            }

            Map report = getConsumptionReport(userProjectIds, fixedAssetDetailsIds, itemId, transactionTypeConId, fromDate, toDate, projectName)
            result.put(REPORT, report)
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
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * do nothing for success operation
     */
    public Object buildSuccessResultForUI(Object result) {
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
     * Generate report by given data
     * @param userProjectIds - project ids
     * @param fixedAssetDetailsIds - fixed asset details id
     * @param itemId - item id
     * @param transactionTypeConId - transaction Type = Consumption
     * @param fromDate - starting point of date range
     * @param toDate - ending point of date range
     * @param projectName - project name
     * @return - generated report with required params
     */
    private Map getConsumptionReport(List userProjectIds, List fixedAssetDetailsIds, long itemId, long transactionTypeConId, Date fromDate, Date toDate, String projectName) {

        Item item = (Item) itemCacheUtility.read(itemId)
        String itemName = item.name

        Map reportParams = new LinkedHashMap()
        String reportDir = Tools.getFixedAssetReportDirectory() + File.separator + REPORT_FOLDER
        String outputFileName = OUTPUT_FILE_NAME + PDF_EXTENSION

        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(USER_PROJECT_IDS, userProjectIds)
        reportParams.put(FIXED_ASSET_DETAILS_IDS, fixedAssetDetailsIds)
        reportParams.put(FROM_DATE, fromDate)
        reportParams.put(TO_DATE, toDate)
        reportParams.put(TRANSACTION_TYPE_CON_ID, transactionTypeConId)

        reportParams.put(DB_QUANTITY_FORMAT, Tools.DB_QUANTITY_FORMAT)
        reportParams.put(PROJECT_NAME, projectName)
        reportParams.put(ITEM_NAME, itemName)
        reportParams.put(ITEM_ID, itemId)

        JasperReportDef reportDef = new JasperReportDef(name: JASPER_FILE, fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: reportParams, folder: reportDir)
        /*  Generate a report based on jasper file. */
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }
    /**
     * Get fixed asset id.
     * @param fromDate - start date
     * @param toDate - end date
     * @param projectIdList - list project id(s)
     * @param itemId - item id
     * @return - fixed asset id list
     */
    private List<GroovyRowResult> getFixedAssetIdList(Date fromDate, Date toDate, List<Long> projectIdList, long itemId, long transactionTypeId) {
        String lstProjectIds = Tools.buildCommaSeparatedStringOfIds(projectIdList)
        String queryStr = """
            SELECT iitd.fixed_asset_details_id AS id
              FROM vw_inv_inventory_transaction_with_details iitd
            LEFT JOIN item ON item.id = iitd.item_id
            WHERE  iitd.transaction_type_id =:transactionTypId AND
                   iitd.project_id IN(${lstProjectIds}) AND
                   iitd.approved_by > 0 AND
                   iitd.fixed_asset_details_id > 0 AND
                   iitd.is_current = TRUE AND
                   iitd.item_id =:itemId AND
                   (iitd.transaction_date BETWEEN :fromDate AND :toDate)
            GROUP BY iitd.fixed_asset_details_id
        """

        Map queryParams = [
                transactionTypId: transactionTypeId,
                itemId: itemId,
                fromDate: DateUtility.getSqlDate(fromDate),
                toDate: DateUtility.getSqlDate(toDate)
        ]
        List<GroovyRowResult> fixedAssetList = executeSelectSql(queryStr, queryParams)
        return fixedAssetList
    }
}

