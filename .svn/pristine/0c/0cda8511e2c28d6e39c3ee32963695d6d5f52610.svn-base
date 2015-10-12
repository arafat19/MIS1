package com.athena.mis.inventory.actions.invproductiondetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.ItemCategoryCacheUtility
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.inventory.entity.InvProductionDetails
import com.athena.mis.inventory.utility.InvProductionDetailsCacheUtility
import com.athena.mis.inventory.utility.InvProductionItemTypeCacheUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

// Select the object of Inventory Production Details
class SelectInvProductionDetailsActionService extends BaseService implements ActionIntf {

    private static String PRODUCTION_DETAILS_NOT_FOUND_MASSAGE = "Selected production details is not found"
    private static String DEFAULT_ERROR_MASSAGE = "Fail to get production details "
    private static String MATERIAL_LIST = "materialList"

    @Autowired
    InvProductionDetailsCacheUtility invProductionDetailsCacheUtility
    @Autowired
    InvProductionItemTypeCacheUtility invProductionItemTypeCacheUtility
    @Autowired
    ItemCategoryCacheUtility itemCategoryCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil

    private final Logger log = Logger.getLogger(getClass())

    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            Long productionDetailsId = Long.parseLong(parameterMap.id.toString())
            InvProductionDetails invProductionDetails = (InvProductionDetails) invProductionDetailsCacheUtility.read(productionDetailsId)
            if (invProductionDetails) {
                result.put(Tools.ENTITY, invProductionDetails)
            } else {
                result.put(Tools.MESSAGE, PRODUCTION_DETAILS_NOT_FOUND_MASSAGE)
            }
            List materialList = []
            long companyId = invSessionUtil.appSessionUtil.getCompanyId()
            SystemEntity rawMaterial = (SystemEntity) invProductionItemTypeCacheUtility.readByReservedAndCompany(invProductionItemTypeCacheUtility.TYPE_RAW_MATERIAL_ID, companyId)
            SystemEntity finishedProduct = (SystemEntity) invProductionItemTypeCacheUtility.readByReservedAndCompany(invProductionItemTypeCacheUtility.TYPE_FINISHED_MATERIAL_ID, companyId)
            if (invProductionDetails.productionItemTypeId == rawMaterial.id) {
                materialList = getRawMaterialList(itemCategoryCacheUtility.INVENTORY, companyId)
            } else if (invProductionDetails.productionItemTypeId == finishedProduct.id) {
                materialList = getFinishedMaterialList(itemCategoryCacheUtility.INVENTORY, companyId)
            }

            result.put(MATERIAL_LIST, materialList)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PRODUCTION_DETAILS_NOT_FOUND_MASSAGE)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
            InvProductionDetails invProductionDetails = (InvProductionDetails) executeResult.get(Tools.ENTITY)
            List materialList = (List) executeResult.get(MATERIAL_LIST)

            result.put(MATERIAL_LIST, Tools.listForKendoDropdown(materialList,null,null))
            result.put(Tools.ENTITY, invProductionDetails)
            result.put(Tools.VERSION, invProductionDetails.version)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        }
    }

    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.message) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        }
    }

    private static final String LST_RAW_MAT_QUERY = """
            SELECT id, name FROM item
            WHERE category_id=:itemCategoryInventoryId
            AND company_id=:companyId
            ORDER BY name
        """

    private List<GroovyRowResult> getRawMaterialList(long itemCategoryInventoryId, long companyId) {
        SystemEntity itemSysEntityObject = (SystemEntity) itemCategoryCacheUtility.readByReservedAndCompany(itemCategoryInventoryId, companyId)
        Map queryParams = [
                itemCategoryInventoryId: itemSysEntityObject.id,
                companyId: companyId
        ]
        List<GroovyRowResult> rawMaterialList = executeSelectSql(LST_RAW_MAT_QUERY, queryParams)
        return rawMaterialList
    }

    private static final String LST_FINISHED_MAT_QUERY = """
            SELECT id, name FROM item
            WHERE category_id=:itemCategoryInventoryId AND
                  is_finished_product = TRUE AND
                  company_id=:companyId
            ORDER BY name
        """

    private List<GroovyRowResult> getFinishedMaterialList(long itemCategoryInventoryId, long companyId) {
        SystemEntity itemSysEntityObject = (SystemEntity) itemCategoryCacheUtility.readByReservedAndCompany(itemCategoryInventoryId, companyId)
        Map queryParams = [
                itemCategoryInventoryId: itemSysEntityObject.id,
                companyId: companyId
        ]
        List<GroovyRowResult> finishedMaterialList = executeSelectSql(LST_FINISHED_MAT_QUERY, queryParams)
        return finishedMaterialList
    }
}
