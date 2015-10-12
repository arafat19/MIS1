package com.athena.mis.inventory.actions.report.inventorystock

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Project
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.inventory.entity.InvInventory
import com.athena.mis.inventory.utility.InvInventoryCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 * Download inventory stock report in pdf format
 * For details go through Use-Case doc named 'DownloadForInventoryStockActionService'
 */
class DownloadForInventoryStockActionService extends BaseService implements ActionIntf {

    JasperService jasperService
    @Autowired
    InvInventoryCacheUtility invInventoryCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility
    @Autowired
    ProjectCacheUtility projectCacheUtility

    private static final String INVALID_INPUT = "Inventory not found"
    private static final String PROJECT_IDs = "projectIds"
    private static final String INVENTORY_ID = "inventoryId"
    private static final String INVENTORY_TYPE_ID = "inventoryTypeId"
    private static final String LST_INVENTORY_ID = "lstInventoryIds"
    private static final String PROJECT_NAME = "projectName"
    private static final String INVENTORY_NAME = "inventoryName"
    private static final String INVENTORY_NAME_ALL = "ALL"
    private static final String PROJECT_NAME_ALL = "ALL"
    private static final String FAILURE_MSG = "Fail to generate inventory stock report"
    private static final String REPORT_FOLDER = 'inventoryStock'
    private static final String REPORT = "report"
    private static final String OUTPUT_FILE_NAME = 'stock'
    private static final String PDF_EXTENSION = ".pdf"
    private static final String REPORT_FILE_FORMAT = 'pdf'
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_TITLE = 'Inventory stock'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String JASPER_FILE = 'stock.jasper'
    private static final String DB_QUANTITY_FORMAT = "dbQuantityFormat"

    protected final Logger log = Logger.getLogger(getClass())

    /**
     * Check and get required parameters from UI
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            // check required parameter
            if (!params.projectId) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
            }
            // get required parameters from UI
            long projectId = Long.parseLong(params.projectId.toString())
            long inventoryId = Long.parseLong(params.inventoryId.toString())
            long inventoryTypeId = Long.parseLong(params.inventoryTypeId.toString())

            result.put(PROJECT_IDs, projectId)
            result.put(INVENTORY_ID, inventoryId)
            result.put(INVENTORY_TYPE_ID, inventoryTypeId)
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
     * Get desired report providing all required parameters
     * 1. get name and ids of inventory
     * 2. generate report
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing report
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from executePreCondition method
            long projectId = Long.parseLong(preResult.get(PROJECT_IDs).toString())
            long inventoryId = Long.parseLong(preResult.get(INVENTORY_ID).toString())
            long inventoryTypeId = Long.parseLong(preResult.get(INVENTORY_TYPE_ID).toString())

            List<Long> lstInventoryIds = []
            String inventoryName = INVENTORY_NAME_ALL
            // get ids and name of inventory
            if (inventoryTypeId < 0 && inventoryId < 0) {
                lstInventoryIds = projectId > 0 ? invSessionUtil.getUserInventoryIdsByProject(projectId) : invSessionUtil.getUserInventoryIds()
            } else if (inventoryTypeId > 0 && inventoryId < 0) {
                lstInventoryIds = projectId > 0 ? invSessionUtil.getUserInventoryIdsByTypeAndProject(inventoryTypeId, projectId) : invSessionUtil.getUserInventoryIdsByType(inventoryTypeId)
            } else if (inventoryTypeId > 0 && inventoryId > 0) {
                lstInventoryIds << new Long(inventoryId)
                InvInventory invInventory = (InvInventory) invInventoryCacheUtility.read(inventoryId)
                inventoryName = invInventory.name
            }
            // generate report
            Map report = getInventoryStockReport(projectId, lstInventoryIds, inventoryName)
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
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Do nothing for build successful result
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
            LinkedHashMap previousResult = (LinkedHashMap) obj   // cast map returned from previous method
            result.put(Tools.IS_ERROR, previousResult.get(Tools.IS_ERROR))
            if (previousResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, previousResult.get(Tools.MESSAGE))
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
     * Generate inventory stock report pdf format
     * Get project name and ids
     * Put required parameters to generate report
     * @param projectId -id of project
     * @param lstInventoryIds -list of inventory ids
     * @param inventoryName -name of inventory
     * @return -generated report
     */
    private Map getInventoryStockReport(long projectId, List<Long> lstInventoryIds, String inventoryName) {
        String reportDir = Tools.getInventoryReportDirectory() + File.separator + REPORT_FOLDER
        String outputFileName = OUTPUT_FILE_NAME + PDF_EXTENSION
        // put required parameters to generate report
        Map reportParams = new LinkedHashMap()
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(LST_INVENTORY_ID, lstInventoryIds)
        // get project name and ids
        String projectName = PROJECT_NAME_ALL
        List<Long> projectIds = []
        if (projectId <= 0) {
            projectIds = invSessionUtil.appSessionUtil.getUserProjectIds()
        } else {
            projectIds << new Long(projectId)
            Project project = (Project) projectCacheUtility.read(projectId)
            projectName = project.name
        }

        reportParams.put(PROJECT_IDs, projectIds)
        reportParams.put(PROJECT_NAME, projectName)
        reportParams.put(INVENTORY_NAME, inventoryName)
        reportParams.put(DB_QUANTITY_FORMAT, Tools.DB_QUANTITY_FORMAT)

        JasperReportDef reportDef = new JasperReportDef(name: JASPER_FILE, fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: reportParams, folder: reportDir)

        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }
}