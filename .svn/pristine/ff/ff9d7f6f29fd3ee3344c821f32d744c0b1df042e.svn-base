package com.athena.mis.procurement.actions.report.supplierwisepo

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.ItemType
import com.athena.mis.application.entity.Project
import com.athena.mis.application.entity.Supplier
import com.athena.mis.application.service.SupplierService
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.application.utility.ItemTypeCacheUtility
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.procurement.utility.ProcSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 * Download Supplier Wise Purchase Order CSV report
 * For details go through Use-Case doc named 'DownloadForSupplierWisePOCsvActionService'
 */
class DownloadForSupplierWisePOCsvActionService extends BaseService implements ActionIntf {

    JasperService jasperService
    SupplierService supplierService
    @Autowired
    ProcSessionUtil procSessionUtil
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to download supplier wise po csv report"
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_FOLDER = 'supplierWisePO'
    private static final String OUTPUT_FILE_NAME = 'supplierWisePO'
    private static final String REPORT_TITLE = 'Supplier Wise Purchase Order Report'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String REPORT = "report"
    private static final String JASPER_FILE_CSV = 'supplierWisePOCSV.jasper'
    private static final String PROJECT_IDS = "lstProjectIds"
    private static final String ITEM_TYPE_IDS = "lstItemTypeIds"
    private static final String ITEM_TYPE_NAME = "itemTypeName"
    private static final String PROJECT_NAME = "projectName"
    private static final String FROM_DATE = "fromDate"
    private static final String TO_DATE = "toDate"
    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String FAILURE_MSG = "Failed to generate supplier wise po report."
    private static final String USER_HAS_NO_PROJECT = "User is not associated with any project"
    private static final String ALL_PROJECT = "All Project"
    private static final String ALL_ITEM_TYPE = "All"
    private static final String SUPPLIER_ID = "supplierId"
    private static final String SUPPLIER_NAME = "supplierName"
    private static final String DB_QUANTITY_FORMAT = "dbQuantityFormat"
    private static final String DB_CURRENCY_FORMAT = "dbCurrencyFormat"
    private static final String CSV_EXTENSION = ".csv"
    private static final String REPORT_FILE_FORMAT = "csv"

    /**
     *  Check input validations
     * @param parameters - serialized parameters from UI
     * @param obj -N/A
     * @return - map containing isError(True/False)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (!params.supplierId || !params.fromDate || !params.toDate) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
            }
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
     * 1. receive project id, supplier id, item type, fromDate, toDate
     * 2. set project name = ALL if no project is selected
     * 3. pull project object
     * 4. pull all item if item type is not selected & itemName = ALL
     * 5. generate report by assigned data
     * @param parameters -N/A
     * @param obj - object receive from previous method
     * @return- a map containing report
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {

            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (parameterMap.projectId.equals(Tools.EMPTY_SPACE)) {
                parameterMap.projectId = 0L
            }
            if (parameterMap.itemTypeId.equals(Tools.EMPTY_SPACE)) {
                parameterMap.itemTypeId = 0L
            }
            long supplierId = Long.parseLong(parameterMap.supplierId.toString())
            long projectId = Long.parseLong(parameterMap.projectId.toString())
            long itemTypeId = Long.parseLong(parameterMap.itemTypeId.toString())
            Date fromDate = DateUtility.parseMaskedFromDate(parameterMap.fromDate.toString())
            Date toDate = DateUtility.parseMaskedToDate(parameterMap.toDate.toString())

            List<Long> lstProjectIds = []
            List<Long> lstItemTypeIds = []
            String projectName
            String itemTypeName
            if (projectId <= 0) {
                lstProjectIds = (List<Long>) procSessionUtil.appSessionUtil.getUserProjectIds()
                projectName = ALL_PROJECT
                if (lstProjectIds.size() == 0) {
                    result.put(Tools.MESSAGE, USER_HAS_NO_PROJECT)
                    return result
                }
            } else {
                lstProjectIds << new Long(projectId)
                Project project = (Project) projectCacheUtility.read(projectId)
                projectName = project.name
            }

            if (itemTypeId <= 0) {
                lstItemTypeIds = itemTypeCacheUtility.getAllItemTypeIds()
                itemTypeName = ALL_ITEM_TYPE
            } else {
                lstItemTypeIds << new Long(itemTypeId)
                ItemType itemType = (ItemType) itemTypeCacheUtility.read(itemTypeId)
                itemTypeName = itemType.name
            }
            Map report = getSupplierWisePOReport(lstProjectIds, lstItemTypeIds, supplierId, fromDate, toDate, projectName, itemTypeName)
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
            if (executeResult.get(Tools.MESSAGE)) {
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
     * 1. set report directory
     * 2. assign all required params
     * @param lstProjectIds - list of project ids
     * @param lstItemTypeIds - list of item type ids
     * @param supplierId - supplier id
     * @param fromDate - starting point of date range
     * @param toDate - ending point of date range
     * @param projectName - project name
     * @param itemTypeName - item type name
     * @return - generated report with required params
     */
    private Map getSupplierWisePOReport(List<Long> lstProjectIds, List<Long> lstItemTypeIds, long supplierId, Date fromDate, Date toDate, String projectName, String itemTypeName) {
        Map reportParams = new LinkedHashMap()
        String reportDir = Tools.getProcurementReportDirectory() + File.separator + REPORT_FOLDER
        String outputFileName = OUTPUT_FILE_NAME + CSV_EXTENSION
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(PROJECT_IDS, lstProjectIds)
        reportParams.put(PROJECT_NAME, projectName)
        reportParams.put(ITEM_TYPE_IDS, lstItemTypeIds)
        reportParams.put(ITEM_TYPE_NAME, itemTypeName)
        reportParams.put(FROM_DATE, fromDate.toTimestamp())
        reportParams.put(TO_DATE, toDate.toTimestamp())
        reportParams.put(SUPPLIER_ID, supplierId)
        Supplier supplier = supplierService.read(supplierId)
        reportParams.put(SUPPLIER_NAME, supplier.name)
        reportParams.put(DB_QUANTITY_FORMAT, Tools.DB_QUANTITY_FORMAT_CSV)
        reportParams.put(DB_CURRENCY_FORMAT, Tools.DB_CURRENCY_FORMAT_CSV)

        JasperReportDef reportDef = new JasperReportDef(name: JASPER_FILE_CSV, fileFormat: JasperExportFormat.CSV_FORMAT,
                parameters: reportParams, folder: reportDir)
        /*  Generate a report based on jasper file. */
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }
}
