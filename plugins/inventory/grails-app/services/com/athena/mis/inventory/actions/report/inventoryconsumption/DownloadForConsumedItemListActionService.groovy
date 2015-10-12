package com.athena.mis.inventory.actions.report.inventoryconsumption

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Project
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.inventory.entity.InvInventory
import com.athena.mis.inventory.utility.InvInventoryCacheUtility
import com.athena.mis.inventory.utility.InvInventoryTypeCacheUtility
import com.athena.mis.inventory.utility.InvTransactionTypeCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 * Download item consumption report in pdf format
 * For details go through Use-Case doc named 'DownloadForConsumedItemListActionService'
 */
class DownloadForConsumedItemListActionService extends BaseService implements ActionIntf {

    protected final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Fail to generate consumed item list report"
    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String INVENTORY_NOT_FOUND = "Inventory not found"
    private static final String START_DATE = "startDate"
    private static final String END_DATE = "endDate"
    private static final String INVENTORY_IDS = "inventoryIds"
    private static final String INVENTORY_NAME = "inventoryName"
    private static final String INVENTORY_OBJ = "inventoryObj"
    private static final String TRANSACTION_TYPE_CONSUMPTION = "transactionTypeConsumption"
    private static final String REPORT_FILE_FORMAT = 'pdf'
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_FOLDER = 'inventoryConsumption'
    private static final String OUTPUT_FILE_NAME = 'ConsumedItemListReport'
    private static final String REPORT_TITLE = 'Consumed Item List'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String JASPER_FILE = 'inventoryConsumedItemList.jasper'
    private static final String REPORT = "report"
    private static final String DB_QUANTITY_FORMAT = "dbQuantityFormat"
    private static final String DB_CURRENCY_FORMAT = "dbCurrencyFormat"
    private static final String PROJECT_ID = "projectId"
    private static final String PROJECT_NAME = "projectName"
    private static final String LABEL_ALL = "ALL"
    private static final String LABEL_ALL_SITE = "ALL SITE"
    private static final String LABEL_ALL_STORE = "ALL STORE"

    JasperService jasperService
    @Autowired
    InvInventoryCacheUtility invInventoryCacheUtility
    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    InvInventoryTypeCacheUtility invInventoryTypeCacheUtility

    /**
     * Check pre condition before downloading the report
     * 1. check required parameters
     * 2. check if inventory exists or not
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
            // check required parameters
            if (!params.inventoryId || !params.projectId || !params.startDate || !params.endDate) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
            }
            // check if inventory exists or not
            long inventoryId = Long.parseLong(params.inventoryId.toString())
            InvInventory invInventory = null
            if (inventoryId > 0) {
                invInventory = (InvInventory) invInventoryCacheUtility.read(inventoryId)
                if (!invInventory) {
                    result.put(Tools.MESSAGE, INVENTORY_NOT_FOUND)
                    return result
                }
            }
            result.put(INVENTORY_OBJ, invInventory)
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
     * Get desired report providing all required parameters
     * 1. get all required parameters
     * 2. generate report
     * @param parameters -serialized parameters from UI
     * @param obj -map returned from executePreCondition method
     * @return -a map containing report
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from executePreCondition method
            InvInventory inventory = (InvInventory) preResult.get(INVENTORY_OBJ)
            long inventoryTypeId = Long.parseLong(params.inventoryTypeId.toString())
            long projectId = Long.parseLong(params.projectId.toString())
            Date startDate = DateUtility.parseMaskedFromDate(params.startDate.toString())
            Date endDate = DateUtility.parseMaskedToDate(params.endDate.toString())
            // generate report
            Map report = getInventoryConsumptionReport(projectId, inventory, inventoryTypeId, startDate, endDate)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(REPORT, report)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
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
     * Generate item consumption report
     * 1. pull inventory type object & transaction object from cache utility
     * 2. pull project object from projectCacheUtility by projectId
     * 3. get specific inventory id and name if exists or get all inventory ids by type mapped with user
     * 4. put required parameters to generate report
     * @param projectId -id of project
     * @param invInventory -object of InvInventory
     * @param inventoryTypeId -id of SystemEntity(Store/Site)
     * @param startDate -starting date
     * @param endDate -end date
     * @return -generated report
     */
    private Map getInventoryConsumptionReport(long projectId, InvInventory invInventory, long inventoryTypeId, Date startDate, Date endDate) {
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        // pull inventoryType object
        SystemEntity inventoryTypeSiteObj = (SystemEntity) invInventoryTypeCacheUtility.readByReservedAndCompany(invInventoryTypeCacheUtility.TYPE_SITE, companyId)
        SystemEntity inventoryTypeStoreObj = (SystemEntity) invInventoryTypeCacheUtility.readByReservedAndCompany(invInventoryTypeCacheUtility.TYPE_STORE, companyId)
        // pull transactionType object
        SystemEntity transactionTypeCons = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_CONSUMPTION, companyId)

        Project project = (Project) projectCacheUtility.read(projectId)
        String projectName = project.name
        String inventoryName = LABEL_ALL
        // get specific inventory id and name if exists or get all inventory ids by type mapped with user
        List inventoryIds = []
        if (invInventory) {
            inventoryIds << new Long(invInventory.id)
            inventoryName = invInventory.name
        } else {
            inventoryIds = invSessionUtil.getUserInventoryIdsByType(inventoryTypeId)
            if (inventoryTypeId == inventoryTypeSiteObj.id) {
                inventoryName = LABEL_ALL_SITE
            } else if (inventoryTypeId == inventoryTypeStoreObj.id) {
                inventoryName = LABEL_ALL_STORE
            }
        }

        String reportDir = Tools.getInventoryReportDirectory() + File.separator + REPORT_FOLDER
        String outputFileName = OUTPUT_FILE_NAME + Tools.PDF_EXTENSION

        Map reportParams = new LinkedHashMap()
        // put required parameters to generate report
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(INVENTORY_IDS, inventoryIds)
        reportParams.put(INVENTORY_NAME, inventoryName)
        reportParams.put(PROJECT_ID, projectId)
        reportParams.put(PROJECT_NAME, projectName)
        reportParams.put(START_DATE, startDate)
        reportParams.put(END_DATE, endDate)

        long transactionTypeConsumption = transactionTypeCons.id
        reportParams.put(TRANSACTION_TYPE_CONSUMPTION, transactionTypeConsumption)

        reportParams.put(DB_QUANTITY_FORMAT, Tools.DB_QUANTITY_FORMAT)
        reportParams.put(DB_CURRENCY_FORMAT, Tools.DB_CURRENCY_FORMAT)

        JasperReportDef reportDef = new JasperReportDef(name: JASPER_FILE, fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: reportParams, folder: reportDir)

        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }
}

