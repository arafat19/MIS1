package com.athena.mis.fixedasset.actions.fxdmaintenance

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.fixedasset.entity.FxdFixedAssetDetails
import com.athena.mis.fixedasset.entity.FxdMaintenanceType
import com.athena.mis.fixedasset.model.FxdMaintenanceModel
import com.athena.mis.fixedasset.service.FixedAssetDetailsService
import com.athena.mis.fixedasset.utility.FxdCategoryMaintenanceTypeCacheUtility
import com.athena.mis.fixedasset.utility.FxdSessionUtil
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Show Maintenance for fixed asset.
 * For details go through Use-Case doc named 'FxdShowMaintenanceActionService'
 */
class FxdShowMaintenanceActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    FixedAssetDetailsService fixedAssetDetailsService
    @Autowired
    FxdSessionUtil fxdSessionUtil
    @Autowired
    FxdCategoryMaintenanceTypeCacheUtility fxdCategoryMaintenanceTypeCacheUtility

    private static final String PAGE_LOAD_ERROR_MESSAGE = "Failed to load fixed asset maintenance page"
    private static final String FXD_MAINTENANCE_LIST = "fxdMaintenanceList"
    private static final String ITEM_LIST = "itemList"
    private static final String FIXED_ASSET_DETAILS_LIST = "fixedAssetDetailsList"
    private static final String FIXED_ASSET_DETAILS_OBJ = "fixedAssetDetails"
    private static final String FIXED_ASSET_DETAILS_ID = "fixedAssetDetailsId"
    private static final String ITEM_ID = "itemId"
    private static final String MAINTENANCE_TYPE_LIST = "maintenanceTypeList"
    /**
     * do nothing for pre condition
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * 1. initialize pagination
     * 2. pull fixed asset details object
     * 3. pull maintenance list
     * @param params - serialize parameters from UI
     * @param obj - N/A
     * @return - a map containing fixed asset maintenance list , fixed asset details object
     *  and isError(true/false)
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            initPager(parameterMap)
            long companyId = fxdSessionUtil.appSessionUtil.getCompanyId()
            List<FxdMaintenanceModel> fxdMaintenanceList
            int count
            FxdFixedAssetDetails fixedAssetDetails = null
            if (parameterMap.fixedAssetDetailsId) {
                long fixedAssetDetailsId = Long.parseLong(parameterMap.fixedAssetDetailsId.toString())
                fixedAssetDetails = fixedAssetDetailsService.read(fixedAssetDetailsId)
                fxdMaintenanceList = FxdMaintenanceModel.findAllByCompanyIdAndFixedAssetDetailsId(companyId, fixedAssetDetailsId, [offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder, readOnly: true])
                count = FxdMaintenanceModel.countByCompanyIdAndFixedAssetDetailsId(companyId, fixedAssetDetailsId)
            } else {
                fxdMaintenanceList = FxdMaintenanceModel.findAllByCompanyId(companyId, [offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder, readOnly: true])
                count = FxdMaintenanceModel.countByCompanyId(companyId)
            }
            result.put(FXD_MAINTENANCE_LIST, fxdMaintenanceList)
            result.put(Tools.COUNT, count)
            result.put(FIXED_ASSET_DETAILS_OBJ, fixedAssetDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }
    /**
     * do nothing for post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * 1. fixed asset maintenance receive from execute method
     * 2. wrap object for grid entity
     * 3. get fixed asset items
     * 4. get fixed asset details
     * @param obj - object receive from execute method
     * @return - fixed asset maintenance for grid entity, item list, maintenance list & maintenance type list
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj
            FxdFixedAssetDetails fixedAssetDetails = (FxdFixedAssetDetails) executeResult.get(FIXED_ASSET_DETAILS_OBJ)
            List<FxdMaintenanceModel> fxdMaintenanceList = (List<FxdMaintenanceModel>) executeResult.get(FXD_MAINTENANCE_LIST)
            int count = (int) executeResult.get(Tools.COUNT)
            List wrapFxdMaintenanceList = wrapListInGridEntityList(fxdMaintenanceList, start)
            Map output = [page: pageNumber, total: count, rows: wrapFxdMaintenanceList]
            List<GroovyRowResult> itemList = getItemListOfFixedAssetDetails()
            List<GroovyRowResult> fixedAssetDetailsList = null
            List<FxdMaintenanceType> fxdMaintenanceTypeList = null
            if (fixedAssetDetails) {
                fixedAssetDetailsList = getFixedAssetDetailsList(fixedAssetDetails.itemId)
                fxdMaintenanceTypeList = fxdCategoryMaintenanceTypeCacheUtility.getMaintenanceTypeListByItemId(fixedAssetDetails.itemId)
                result.put(FIXED_ASSET_DETAILS_ID, fixedAssetDetails.id)
                result.put(ITEM_ID, fixedAssetDetails.itemId)
            }
            result.put(MAINTENANCE_TYPE_LIST, fxdMaintenanceTypeList)
            result.put(FIXED_ASSET_DETAILS_LIST, fixedAssetDetailsList)
            result.put(FXD_MAINTENANCE_LIST, output)
            result.put(ITEM_LIST, itemList)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
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
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map receiveResult = (Map) obj
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return result
        }
    }
    /**
     * wrap maintenance for grid entity
     * @param fxdMaintenanceList - list of maintenance
     * @param start - starting point of index
     * @return - wrapped fixed asset maintenance for grid entity
     */
    private static List wrapListInGridEntityList(List<FxdMaintenanceModel> fxdMaintenanceList, int start) {
        List lstFxdMaintenance = []
        int counter = start + 1
        int len = 0
        if (fxdMaintenanceList != null) {
            len = fxdMaintenanceList.size()
        }
        FxdMaintenanceModel fxdMaintenanceModel
        for (int i = 0; i < len; i++) {
            fxdMaintenanceModel = fxdMaintenanceList[i]
            String description = Tools.makeDetailsShort(fxdMaintenanceModel.description, Tools.DEFAULT_LENGTH_DETAILS_OF_BUDGET)

            GridEntity obj = new GridEntity()
            obj.id = fxdMaintenanceModel.maintenanceId
            obj.cell = [
                    counter,
                    fxdMaintenanceModel.itemName + Tools.SINGLE_SPACE + Tools.PARENTHESIS_START + fxdMaintenanceModel.modelName + Tools.PARENTHESIS_END,
                    fxdMaintenanceModel.maintenanceTypeName,
                    fxdMaintenanceModel.strAmount,
                    fxdMaintenanceModel.strMaintenanceDate,
                    fxdMaintenanceModel.createdByUserName,
                    description
            ]
            lstFxdMaintenance << obj
            counter++
        }
        return lstFxdMaintenance
    }

    private static final String QUERY_SELECT_DISTINCT = """
                        SELECT DISTINCT item.id, item.name FROM fxd_fixed_asset_details  fad
                        LEFT JOIN item ON item.id = fad.item_id
                        ORDER BY item.name
                        """
    /**
     * @return - a list containing fixed asset items
     */
    private List<GroovyRowResult> getItemListOfFixedAssetDetails() {
        List<GroovyRowResult> itemList = executeSelectSql(QUERY_SELECT_DISTINCT)
        return itemList
    }

    private static final String QUERY_SELECT = """
                            SELECT fad.id, fad.name FROM fxd_fixed_asset_details fad
                            WHERE fad.item_id = :itemId
                            """
    /**
     * @param itemId - item id
     * @return - a list containing fixed asset details
     */
    private List<GroovyRowResult> getFixedAssetDetailsList(long itemId) {
        List resultList = executeSelectSql(QUERY_SELECT, [itemId: itemId])
        return resultList
    }
}