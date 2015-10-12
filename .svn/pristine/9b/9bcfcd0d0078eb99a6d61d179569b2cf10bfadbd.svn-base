package com.athena.mis.fixedasset.actions.fxdmaintenance

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.fixedasset.entity.FxdMaintenance
import com.athena.mis.fixedasset.entity.FxdMaintenanceType
import com.athena.mis.fixedasset.service.FxdMaintenanceService
import com.athena.mis.fixedasset.utility.FxdCategoryMaintenanceTypeCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Select specific object of selected fixed asset maintenance at grid row and show for UI
 *  For details go through Use-Case doc named 'FxdSelectMaintenanceActionService'
 */
class FxdSelectMaintenanceActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())
    FxdMaintenanceService fxdMaintenanceService
    @Autowired
    FxdCategoryMaintenanceTypeCacheUtility fxdCategoryMaintenanceTypeCacheUtility

    private static final String NOT_FOUND_MASSAGE = "Selected maintenance is not found"
    private static final String DEFAULT_ERROR_MASSAGE = "Failed to select maintenance"
    private static final String FXD_MAINTENANCE = "fxdMaintenance"
    private static final String MAINTENANCE_TYPE_LIST = "maintenanceTypeList"
    private static final String FIXED_ASSET_DETAILS_LIST = "fixedAssetDetailsList"
    private static final String ITEM_LIST = "itemList"
    private static final String MAINTENANCE_DATE = "maintenanceDate"

    /**
     * Get fixed asset maintenance id
     * 1. Check the existence of fixed asset maintenance by if (!parameterMap.id)
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return - a map containing isError(TRUE/FALSE) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
                return result
            }

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    /**
     * Get fixed asset maintenance object
     * 1. Get fixed asset maintenance object id from parameterMap
     * 2. Check the existence of fixed asset maintenance by if (!parameterMap.id)
     * 3. Get the existence fixed asset maintenance object
     * 4. Get fixed asset maintenance type list
     * 5. Get fixed asset details list
     * 6. Get item list
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return - a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
                return result
            }

            long id = Long.parseLong(parameterMap.id.toString())
            FxdMaintenance fxdMaintenance = fxdMaintenanceService.read(id)
            if (!fxdMaintenance) {
                result.put(Tools.MESSAGE, NOT_FOUND_MASSAGE)
                return result
            }

            List<FxdMaintenanceType> fxdMaintenanceTypeList = fxdCategoryMaintenanceTypeCacheUtility.getMaintenanceTypeListByItemId(fxdMaintenance.itemId)
            List<GroovyRowResult> fixedAssetDetailsList = getFixedAssetDetailsList(fxdMaintenance.itemId)
            List<GroovyRowResult> itemList = getItemListOfFixedAssetDetails()
            result.put(ITEM_LIST, Tools.listForKendoDropdown(itemList,null,null))
            result.put(FIXED_ASSET_DETAILS_LIST, Tools.listForKendoDropdown(fixedAssetDetailsList, null, null))
            result.put(MAINTENANCE_TYPE_LIST, fxdMaintenanceTypeList)
            result.put(FXD_MAINTENANCE, fxdMaintenance)
            result.put(MAINTENANCE_DATE, DateUtility.getDateForUI(fxdMaintenance.maintenanceDate))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
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
     * Show selected object on the form
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
            FxdMaintenance fxdMaintenance = (FxdMaintenance) executeResult.get(FXD_MAINTENANCE)
            result.put(MAINTENANCE_TYPE_LIST, executeResult.get(MAINTENANCE_TYPE_LIST))
            result.put(FIXED_ASSET_DETAILS_LIST, executeResult.get(FIXED_ASSET_DETAILS_LIST))
            result.put(MAINTENANCE_DATE, executeResult.get(MAINTENANCE_DATE))
            result.put(ITEM_LIST, executeResult.get(ITEM_LIST))
            result.put(Tools.ENTITY, fxdMaintenance)
            result.put(Tools.VERSION, fxdMaintenance.version)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
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
            if (obj) {
                Map previousResult = (Map) obj
                result.put(Tools.MESSAGE, previousResult.message)
            } else {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            }
            return result
        }
        catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        }
    }

    private static final String QUERY_SELECT = """
                            SELECT fad.id, fad.name FROM fxd_fixed_asset_details fad
                            WHERE fad.item_id = :itemId
                            """

    /**
     * Get fixed asset details list by item id
     * @param itemId - item id from execute method
     * @return - a list of fixed asset details
     */
    private List<GroovyRowResult> getFixedAssetDetailsList(long itemId) {
        List resultList = executeSelectSql(QUERY_SELECT, [itemId: itemId])
        return resultList
    }

    private static final String QUERY_SELECT_DISTINCT = """
                        SELECT DISTINCT item.id, item.name FROM fxd_fixed_asset_details  fad
                        LEFT JOIN item ON item.id = fad.item_id
                        ORDER BY item.name
                        """

    /**
     * Get Item list of fixed asset details
     * @return - list of items
     */
    private List<GroovyRowResult> getItemListOfFixedAssetDetails() {
        List<GroovyRowResult> itemList = executeSelectSql(QUERY_SELECT_DISTINCT)
        return itemList
    }
}
