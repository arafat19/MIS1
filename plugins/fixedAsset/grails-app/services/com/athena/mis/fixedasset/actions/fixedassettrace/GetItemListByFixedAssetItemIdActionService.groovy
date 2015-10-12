package com.athena.mis.fixedasset.actions.fixedassettrace

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.application.utility.OwnerTypeCacheUtility
import com.athena.mis.fixedasset.utility.FxdSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Get Item List By Fixed Asset Item.
 * For details go through Use-Case doc named 'GetItemListByFixedAssetItemIdActionService'
 */
class GetItemListByFixedAssetItemIdActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    ItemCacheUtility itemCacheUtility
    @Autowired
    OwnerTypeCacheUtility ownerTypeCacheUtility
    @Autowired
    FxdSessionUtil fxdSessionUtil

    private static final String DEFAULT_ERROR_MESSAGE = "Fail to load item list"
    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String FIXED_ASSET_NOT_FOUND = "Selected fixed asset not found. Refresh the page"
    private static final String FIXED_ASSET_ITEM_LIST = "fixedAssetItemList"
    /**
     * 1. check all input validations
     * @param params - serialized parameters from UI
     * @param obj - N/A
     * @return - a map containing isError(true/false) and relevant message
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            if (!parameterMap.fixedAssetItemId) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            log.error(ex.getMessage())
            return result
        }
    }
    /**
     * 1. pull item object
     * 2. check fixed asset existence
     * 3. get item list by fixed asset item id
     * 4. check current date & expire date for owner type = rental
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return - fixed asset item list
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            List fixedAssetItemList = []
            long fixedAssetItemId = Long.parseLong(parameterMap.fixedAssetItemId.toString())
            Item item = (Item) itemCacheUtility.read(fixedAssetItemId)
            if (!item) {
                result.put(Tools.MESSAGE, FIXED_ASSET_NOT_FOUND)
                return result
            }
            List<GroovyRowResult> lstFixedAssetItem = getItemListByFixedAssetItemId(fixedAssetItemId)
            long companyId = fxdSessionUtil.appSessionUtil.getCompanyId()
            SystemEntity rentalObj = (SystemEntity) ownerTypeCacheUtility.readByReservedAndCompany(ownerTypeCacheUtility.RENTAL, companyId)
            for (int i = 0; i < lstFixedAssetItem.size(); i++) {
                long ownerTypeId = (long) lstFixedAssetItem[i].owner_type_id
                if (ownerTypeId == rentalObj.id) {
                    Date expireDate = (Date) lstFixedAssetItem[i].expire_date
                    Date currentDate = DateUtility.getOnlyDate(new Date())
                    if (expireDate < currentDate) {
                        continue
                    }
                }
                fixedAssetItemList << lstFixedAssetItem[i]
            }

            result.put(FIXED_ASSET_ITEM_LIST, fixedAssetItemList)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            log.error(ex.getMessage())
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
     * receive fixed asset item list from execute method
     * @param obj- N/A
     * @return - a map containing fixed asset item list
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            List<GroovyRowResult> fixedAssetItemList = (List) receiveResult.get(FIXED_ASSET_ITEM_LIST)
            result = [fixedAssetItemList: Tools.listForKendoDropdown(fixedAssetItemList,'item',null)]
            return result
        } catch (Exception ex) {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            log.error(ex.getMessage())
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
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            log.error(ex.getMessage())
            return result
        }
    }


    private static final String QUERY_SELECT = """
            SELECT fad.id, fad.name AS item, inv.id AS current_inventory_id,
                   inv.name AS inventory, fad.owner_type_id, fad.expire_date
            FROM fxd_fixed_asset_details fad
                LEFT JOIN inv_inventory inv ON inv.id = fad.current_inventory_id
            WHERE fad.item_id = :itemId
            ORDER BY fad.name
        """
    // Get List of Items When a Category (Fixed Asset) select at Drop Down In Fixed Asset Trace
    private List<GroovyRowResult> getItemListByFixedAssetItemId(long itemId) {
        List<GroovyRowResult> result = executeSelectSql(QUERY_SELECT, [itemId:itemId])
        return result
    }
}