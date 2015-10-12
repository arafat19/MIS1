package com.athena.mis.fixedasset.actions.report.currentfixedasset

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Item
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.fixedasset.utility.FxdSessionUtil
import com.athena.mis.integration.inventory.InventoryPluginConnector
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
 * Download PDF of Current Fixed Asset.
 * For details go through Use-Case doc named 'DownloadForCurrentFixedAssetActionService'
 */
class DownloadForCurrentFixedAssetActionService extends BaseService implements ActionIntf {

    protected final Logger log = Logger.getLogger(getClass())

    JasperService jasperService
    @Autowired
    FxdSessionUtil fxdSessionUtil
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired(required = false)
    InventoryPluginConnector inventoryImplService

    private static final String DEFAULT_ERROR_MESSAGE = "Fail to Download Current fixed asset report"
    private static final String REPORT_FILE_FORMAT = 'pdf'
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_FOLDER = 'currentFixedAsset'
    private static final String OUTPUT_FILE_NAME = 'currentFixedAsset'
    private static final String REPORT_TITLE = 'Current fixed asset Report'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String JASPER_FILE = 'currentFixedAsset.jasper'
    private static final String PDF_EXTENSION = ".pdf"
    private static final String REPORT = "report"
    private static final String ITEM_IDS = "itemIds"
    private static final String USER_INVENTORY_ID_LIST = "userInventoryIdList"
    private static final String ITEM_NAME = "itemName"
    private static final String DB_QUANTITY_FORMAT = "dbQuantityFormat"
    private static final String ALL_ITEM = "ALL"

    /**
     * 1. get list of all fixed asset
     * 2. set item name= ALL & item = all fixed asset item if no item is selected
     * 3. if select a category , pull only that specific item
     * 4. pull logged user assigned inventories
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return - a map containing inventory id list, item ids, item name
     *  and isError(true/false) & relevant msg(if amy)
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters

            List<Long> itemIds = []
            String itemName
            long itemId = Long.parseLong(params.itemId.toString())
            Map receiveReturn = listAllFixedAsset()
            if (itemId < 0) {
                itemIds = (List<Long>) receiveReturn.fixedAssetList.id
                itemName = ALL_ITEM
            } else {
                itemIds << new Long(itemId)
                Item item = (Item) itemCacheUtility.read(itemId)
                itemName = item.name
            }

            List<Long> userInventoryIdList = inventoryImplService.getUserInventoryIds()
            if (userInventoryIdList.size() <= 0) {
                userInventoryIdList << 0L
            }

            result.put(USER_INVENTORY_ID_LIST, userInventoryIdList)
            result.put(ITEM_IDS, itemIds)
            result.put(ITEM_NAME, itemName)
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
     * 1. receive inventory id list, item ids, item name from pre execute method
     * 2. get generated report
     * @param parameters - N/A
     * @param obj - object receive from pre execute method
     * @return - a map containing generated report
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            List<Long> userInventoryIdList = (List<Long>) preResult.get(USER_INVENTORY_ID_LIST)
            List<Long> itemIds = (List<Long>) preResult.get(ITEM_IDS)
            String itemName = (String) preResult.get(ITEM_NAME)
            Map report = getCurrentFixedAssetReport(userInventoryIdList, itemIds, itemName)
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
     * @param userInventoryIdList - list of inventory id
     * @param itemIds- list of item ids
     * @param itemName - item name
     * @return - generated report with required params
     */
    private Map getCurrentFixedAssetReport(List<Long> userInventoryIdList, List<Long> itemIds, String itemName) {
        Map reportParams = new LinkedHashMap()
        String reportDir = Tools.getFixedAssetReportDirectory() + File.separator + REPORT_FOLDER
        String outputFileName = OUTPUT_FILE_NAME + PDF_EXTENSION
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(ITEM_IDS, itemIds)
        reportParams.put(USER_INVENTORY_ID_LIST, userInventoryIdList)
        reportParams.put(ITEM_NAME, itemName)
        reportParams.put(DB_QUANTITY_FORMAT, Tools.DB_QUANTITY_FORMAT)
        JasperReportDef reportDef = new JasperReportDef(name: JASPER_FILE, fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: reportParams, folder: reportDir)
        /*  Generate a report based on jasper file. */
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }

    private static final String SELECT_QUERY = """
        SELECT DISTINCT ON (item.name) item.name,fad.item_id as id
        FROM fxd_fixed_asset_details fad
        LEFT JOIN item on item.id=fad.item_id
        WHERE fad.company_id=:companyId
        """

    //get fixedAsset list for current fixed asset as category
    private Map listAllFixedAsset() {
        long companyId = fxdSessionUtil.appSessionUtil.getCompanyId()
        Map queryParams = [
                companyId: companyId
        ]
        List<GroovyRowResult> fixedAssetList = executeSelectSql(SELECT_QUERY, queryParams)
        return [fixedAssetList: fixedAssetList]
    }
}
