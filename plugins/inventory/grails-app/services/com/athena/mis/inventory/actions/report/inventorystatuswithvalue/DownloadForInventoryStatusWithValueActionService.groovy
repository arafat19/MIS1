package com.athena.mis.inventory.actions.report.inventorystatuswithvalue

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.ItemType
import com.athena.mis.application.entity.Project
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.application.utility.ItemCategoryCacheUtility
import com.athena.mis.application.utility.ItemTypeCacheUtility
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.inventory.entity.InvInventory
import com.athena.mis.inventory.utility.*
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Download Inventory Status with value report in PDF format
 *  For details go through Use-Case doc named 'DownloadForInventoryStatusWithValueActionService'
 */
class DownloadForInventoryStatusWithValueActionService extends BaseService implements ActionIntf {

    JasperService jasperService
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility
    @Autowired
    InvTransactionEntityTypeCacheUtility invTransactionEntityTypeCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility
    @Autowired
    ItemCategoryCacheUtility itemCategoryCacheUtility
    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility
    @Autowired
    InvInventoryTypeCacheUtility invInventoryTypeCacheUtility
    @Autowired
    InvInventoryCacheUtility invInventoryCacheUtility

    private static final String INVALID_INPUT = "Invalid Input"
    private static final String PROJECT_IDS = "projectIds"
    private static final String INVENTORY_IDS = "inventoryIds"
    private static final String ITEM_TYPE_IDS = "itemTypeIds"
    private static final String PROJECT_NAME = "projectName"
    private static final String INVENTORY_TYPE_NAME = "inventoryTypeName"
    private static final String INVENTORY_NAME = "inventoryName"
    private static final String ITEM_TYPE_NAME = "itemTypeName"
    private static final String DB_CURRENCY_FORMAT = "dbCurrencyFormat"
    private static final String FAILURE_MSG = "Fail to generate inventory status with value report"
    private static final String REPORT_FOLDER = 'inventoryStatusWithValue'
    private static final String REPORT = "report"
    private static final String OUTPUT_FILE_NAME = 'inventoryStatusWithValue'
    private static final String PDF_EXTENSION = ".pdf"
    private static final String TRANSACTION_TYPE_CONSUMPTION = "transactionTypeConsumption"
    private static final String TRANSACTION_TYPE_PRODUCTION = "transactionTypeProduction"
    private static final String TRANSACTION_TYPE_IN = "transactionTypeIn"
    private static final String ENTITY_TYPE_SUPPLIER = "entityTypeSupplier"
    private static final String PROJECT_NAME_ALL = 'ALL'
    private static final String REPORT_FILE_FORMAT = 'pdf'
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_TITLE = 'Inventory Status With Value'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String JASPER_FILE = 'inventoryStatusWithValue.jasper'
    private static final String FROM_DATE = "fromDate"
    private static final String TO_DATE = "toDate"
    private static final String ITEM_CATEGORY_FIXED_ASSET_ID = "itemCategoryFixedAssetId"
    private static final String TRANSACTION_ENTITY_TYPE_INVENTORY = "transactionEntityTypeInventory"

    protected final Logger log = Logger.getLogger(getClass())

    /**
     * Check existence of required parameters send from UI
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if ((!params.toDate) || (!params.fromDate)) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
            }
            Date fromDate = DateUtility.parseMaskedFromDate(params.fromDate.toString())
            Date toDate = DateUtility.parseMaskedToDate(params.toDate.toString())
            result.put(FROM_DATE, fromDate)
            result.put(TO_DATE, toDate)
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
     * Method to get map that contains required parameters to generate report in PDF format
     * @param parameters -parameters from UI
     * @param obj -parameters send from executePreCondition method
     * @return -map contains parameters to generate report & isError(true/false) depending on method success
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            LinkedHashMap preResult = (LinkedHashMap) obj
            Date fromDate = (Date) preResult.get(FROM_DATE)
            Date toDate = (Date) preResult.get(TO_DATE)
            Map report = getInventoryStatusReport(params, fromDate, toDate)
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
     * do nothing for executePostCondition operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * do nothing for buildSuccessResultForUI operation
     */
    public Object buildSuccessResultForUI(Object result) {
        return null
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous method, can be null
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
     * Method to get required parameters for jasper report
     * @param projectId -project.id
     * @param fromDate -start date
     * @param toDate -end date
     * @return -a map contains parameters for jasper report
     */
    private Map getInventoryStatusReport(GrailsParameterMap params, Date fromDate, Date toDate) {
        Map reportParams = new LinkedHashMap()
        String reportDir = Tools.getInventoryReportDirectory() + File.separator + REPORT_FOLDER
        String outputFileName = OUTPUT_FILE_NAME + PDF_EXTENSION
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)

        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypeIn = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_IN, companyId)
        SystemEntity transactionTypeProduction = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_PRODUCTION, companyId)
        SystemEntity transactionTypeConsumption = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_CONSUMPTION, companyId)
        SystemEntity itemCategoryFixedAsset = (SystemEntity) itemCategoryCacheUtility.readByReservedAndCompany(itemCategoryCacheUtility.FIXED_ASSET, companyId)
        SystemEntity transactionEntityInventory = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_INVENTORY, companyId)
        SystemEntity transactionEntitySupplier = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_SUPPLIER, companyId)

        /*if specific project is given then put given projectId in list
            else select all projects then get user-mapped projectIds*/
        List<Long> projectIds = []
        String projectName = PROJECT_NAME_ALL //default value
        if (params.projectId.equals(Tools.EMPTY_SPACE)) {  //if no specific project is selected
            projectIds = invSessionUtil.appSessionUtil.getUserProjectIds()
        } else {//if specific project is given
            long projectId = Long.parseLong(params.projectId.toString())
            projectIds << new Long(projectId)
            Project project = (Project) projectCacheUtility.read(projectId)
            projectName = project.name
        }

        // assign default value -1 for 'ALL'-(projects/inventories/inventory types)
        long projectId = params.projectId.equals(Tools.EMPTY_SPACE) ? -1L : Long.parseLong(params.projectId.toString())
        long inventoryId = params.inventoryId.equals(Tools.EMPTY_SPACE) ? -1L : Long.parseLong(params.inventoryId.toString())
        long inventoryTypeId = params.inventoryTypeId.equals(Tools.EMPTY_SPACE) ? -1L : Long.parseLong(params.inventoryTypeId.toString())

        String inventoryTypeName = Tools.ALL
        String inventoryName = Tools.ALL
        String itemTypeName = Tools.ALL
        // get inventory ids
        List<Long> lstInventoryIds = []
        if (inventoryTypeId < 0 && inventoryId < 0) {
            lstInventoryIds = projectId > 0 ? invSessionUtil.getUserInventoryIdsByProject(projectId) : invSessionUtil.getUserInventoryIds()
        } else if (inventoryTypeId > 0 && inventoryId < 0) {
            lstInventoryIds = projectId > 0 ? invSessionUtil.getUserInventoryIdsByTypeAndProject(inventoryTypeId, projectId) : invSessionUtil.getUserInventoryIdsByType(inventoryTypeId)
            SystemEntity invType = (SystemEntity) invInventoryTypeCacheUtility.read(inventoryTypeId)
            inventoryTypeName = invType.key
        } else if (inventoryTypeId > 0 && inventoryId > 0) {
            lstInventoryIds << new Long(inventoryId)
            SystemEntity invType = (SystemEntity) invInventoryTypeCacheUtility.read(inventoryTypeId)
            inventoryTypeName = invType.key
            InvInventory inventory = (InvInventory) invInventoryCacheUtility.read(inventoryId)
            inventoryName = inventory.name
        }

        List<Long> itemTypeIds = []
        if (params.itemTypeId.equals(Tools.EMPTY_SPACE)) {
            //if no specific item type is selected then get all item types
            SystemEntity itemCategoryInventory = (SystemEntity) itemCategoryCacheUtility.readByReservedAndCompany(itemCategoryCacheUtility.INVENTORY, companyId)
            itemTypeIds = itemTypeCacheUtility.listIdsByCategoryId(itemCategoryInventory.id)
        } else { //if specific item type is given then put given item type id in list
            long itemTypeId = Long.parseLong(params.itemTypeId.toString())
            itemTypeIds << new Long(itemTypeId)
            ItemType itemType = (ItemType) itemTypeCacheUtility.read(itemTypeId)
            itemTypeName = itemType.name
        }

        reportParams.put(ITEM_CATEGORY_FIXED_ASSET_ID, itemCategoryFixedAsset.id)
        reportParams.put(TRANSACTION_ENTITY_TYPE_INVENTORY, transactionEntityInventory.id)
        reportParams.put(PROJECT_IDS, projectIds)
        reportParams.put(INVENTORY_IDS, lstInventoryIds)
        reportParams.put(ITEM_TYPE_IDS, itemTypeIds)
        reportParams.put(PROJECT_NAME, projectName)
        reportParams.put(ITEM_TYPE_NAME, itemTypeName)
        reportParams.put(INVENTORY_TYPE_NAME, inventoryTypeName)
        reportParams.put(INVENTORY_NAME, inventoryName)
        reportParams.put(FROM_DATE, fromDate)
        reportParams.put(TO_DATE, toDate)
        reportParams.put(DB_CURRENCY_FORMAT, Tools.DB_CURRENCY_FORMAT)
        reportParams.put(TRANSACTION_TYPE_CONSUMPTION, transactionTypeConsumption.id)
        reportParams.put(TRANSACTION_TYPE_PRODUCTION, transactionTypeProduction.id)
        reportParams.put(TRANSACTION_TYPE_IN, transactionTypeIn.id)
        reportParams.put(ENTITY_TYPE_SUPPLIER, transactionEntitySupplier.id)

        JasperReportDef reportDef = new JasperReportDef(name: JASPER_FILE, fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: reportParams, folder: reportDir)

        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }
}
