package com.athena.mis.fixedasset.actions.fxdmaintenance

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.fixedasset.entity.FxdMaintenanceType
import com.athena.mis.fixedasset.utility.FxdCategoryMaintenanceTypeCacheUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Get Maintenance Type and Model List By Item Id.
 * For details go through Use-Case doc named 'FxdGetMaintenanceTypeAndModelListByItemIdActionService'
 */
class FxdGetMaintenanceTypeAndModelListByItemIdActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())
    @Autowired
    FxdCategoryMaintenanceTypeCacheUtility fxdCategoryMaintenanceTypeCacheUtility

    private static final String DEFAULT_ERROR_MASSAGE = "Failed to get maintenance type and model"
    private static final String MAINTENANCE_TYPE_LIST = "maintenanceTypeList"
    private static final String FIXED_ASSET_DETAILS_LIST = "fixedAssetDetailsList"
    /**
     * 1. check input validation
     * @param params - serialized parameters from UI
     * @param obj - N/A
     * @return - a map containing isError(true/false) and relevant message
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.itemId) {
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
     * 1. check input validation
     * 2. get maintenance type list
     * 3. get fixed asset details list
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return - fixed asset details list & maintenance list
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.itemId) {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
                return result
            }

            long itemId = Long.parseLong(parameterMap.itemId.toString())

            List<FxdMaintenanceType> fxdMaintenanceTypeList = fxdCategoryMaintenanceTypeCacheUtility.getMaintenanceTypeListByItemId(itemId)
            List<GroovyRowResult> fixedAssetDetailsList = getFixedAssetDetailsList(itemId)
            result.put(FIXED_ASSET_DETAILS_LIST, Tools.listForKendoDropdown(fixedAssetDetailsList, null, null))
            result.put(MAINTENANCE_TYPE_LIST, Tools.listForKendoDropdown(fxdMaintenanceTypeList, null, null))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
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
     * receive fixed asset details & maintenance type list from execute method
     * @param obj- N/A
     * @return - a map containing fixed asset details & maintenance type list
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
            result.put(MAINTENANCE_TYPE_LIST, executeResult.get(MAINTENANCE_TYPE_LIST))
            result.put(FIXED_ASSET_DETAILS_LIST, executeResult.get(FIXED_ASSET_DETAILS_LIST))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
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
                    WHERE fad.item_id = :itemId"""
    /**
     * @param itemId - item id
     * @return - a list containing fixed asset details list
     */
    private List<GroovyRowResult> getFixedAssetDetailsList(long itemId) {
        List resultList = executeSelectSql(QUERY_SELECT, [itemId: itemId])
        return resultList
    }
}
