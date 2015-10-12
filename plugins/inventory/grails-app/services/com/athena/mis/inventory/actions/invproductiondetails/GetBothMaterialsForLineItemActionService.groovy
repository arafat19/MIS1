package com.athena.mis.inventory.actions.invproductiondetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.inventory.entity.InvProductionLineItem
import com.athena.mis.inventory.utility.InvProductionDetailsCacheUtility
import com.athena.mis.inventory.utility.InvProductionItemTypeCacheUtility
import com.athena.mis.inventory.utility.InvProductionLineItemCacheUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

// Give Materials For Line Item
class GetBothMaterialsForLineItemActionService extends BaseService implements ActionIntf {
    private final Logger log = Logger.getLogger(getClass())

    private static final String LINE_ITEM_NOT_FOUND = "Production line item not found"
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to load raw and finished products"
    private static final String LST_RAW_MATERIALS = "lstRawMaterials"
    private static final String LST_FINISHED_PRODUCTS = "lstFinishedProducts"

    @Autowired
    InvProductionLineItemCacheUtility invProductionLineItemCacheUtility
    @Autowired
    InvProductionDetailsCacheUtility invProductionDetailsCacheUtility
    @Autowired
    InvProductionItemTypeCacheUtility invProductionItemTypeCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil

    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameters = (GrailsParameterMap) params
            long productionLineItemId = Long.parseLong(parameters.lineItemId.toString())
            InvProductionLineItem invProductionLineItem = (InvProductionLineItem) invProductionLineItemCacheUtility.read(new Long(productionLineItemId))
            if (!invProductionLineItem) {
                result.put(Tools.MESSAGE, LINE_ITEM_NOT_FOUND)
                return result
            }
            long inventoryId = Long.parseLong(parameters.inventoryId.toString())
            Map bothMaterials = invProductionDetailsCacheUtility.getBothMaterialsByLineItem(productionLineItemId)
            List rawMaterialList = (List) bothMaterials.lstRaw
            List lstRawMaterials = []

            if (rawMaterialList.size() > 0) {
                lstRawMaterials = listRawMaterialForInventoryProduction(inventoryId, rawMaterialList)
            }

            List lstFinishedMaterials = []
            lstFinishedMaterials = listFinishedMaterialForInventoryProduction(invProductionLineItem.id)

            result.put(LST_RAW_MATERIALS, Tools.listForKendoDropdown(lstRawMaterials,"name_quantity",null))
            result.put(LST_FINISHED_PRODUCTS, Tools.listForKendoDropdown(lstFinishedMaterials,null,null))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    private List listRawMaterialForInventoryProduction(long inventoryId, List rawMaterialList) {
        String queryStr = """
            SELECT
                material.id AS id,
                material.name AS name,
                material.name || '(' || summary.consumeable_stock || ')' AS name_quantity,
                material.unit AS unit,
                coalesce(summary.consumeable_stock,0) AS quantity
            FROM item  material
            LEFT JOIN vw_inv_inventory_consumable_stock summary ON summary.item_id = material.id
            WHERE material.id IN  (${Tools.buildCommaSeparatedStringOfIds(rawMaterialList)})
            AND summary.inventory_id = :inventoryId
            AND material.is_individual_entity = false
            AND material.company_id =:companyId
            ORDER BY material.name ASC
        """
        Map queryParams = [
                inventoryId: inventoryId,
                companyId: invSessionUtil.appSessionUtil.getCompanyId()
        ]
        List<GroovyRowResult> materialListWithQnty = executeSelectSql(queryStr, queryParams)
        return materialListWithQnty
    }

    private static final String FINISHED_MAT_INV_PROD_QUERY = """
            SELECT item.id, item.name AS name, item.unit, details.overhead_cost AS overheadCost
              FROM inv_production_details details
            LEFT JOIN item ON item.id = details.material_id
                WHERE details.production_line_item_id=:productionLineItemId
                AND details.production_item_type_id=:productionItemTypeId
                AND details.company_id =:companyId
            ORDER BY item.name ASC
    """

    private List listFinishedMaterialForInventoryProduction(long productionLineItemId) {
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity finishedProduct = (SystemEntity) invProductionItemTypeCacheUtility.readByReservedAndCompany(invProductionItemTypeCacheUtility.TYPE_FINISHED_MATERIAL_ID, companyId)
        Map queryParams = [
                productionLineItemId: productionLineItemId,
                productionItemTypeId: finishedProduct.id,
                companyId: companyId
        ]
        List<GroovyRowResult> materialListWithOverheadCost = executeSelectSql(FINISHED_MAT_INV_PROD_QUERY, queryParams)
        return materialListWithOverheadCost
    }
}
