package com.athena.mis.inventory.actions.invmodifyoverheadcost

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Item
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.inventory.entity.InvProductionDetails
import com.athena.mis.inventory.entity.InvProductionLineItem
import com.athena.mis.inventory.utility.InvProductionDetailsCacheUtility
import com.athena.mis.inventory.utility.InvProductionLineItemCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

// Give Finished Materials By LineItem Id
class GetInvProdFinishedMaterialByLineItemIdActionService extends BaseService implements ActionIntf {
    protected final Logger log = Logger.getLogger(getClass())

    @Autowired
    InvProductionDetailsCacheUtility invProductionDetailsCacheUtility
    @Autowired
    InvProductionLineItemCacheUtility invProductionLineItemCacheUtility
    @Autowired
    ItemCacheUtility itemCacheUtility

    private static final String FINISHED_MATERIAL_LIST = "finishedMaterialList"
    private static final String DEFAULT_ERROR_MESSAGE = "Fail to get Finished Material List"
    private static final String PROD_LINE_ITEM_NOT_FOUND = "Production Line Item not found."
    private static final String PROD_LINE_ITEM_OBJ = "prodLineItemObj"
    private static final String INVALID_INPUT = "Error occurRed due to invalid input."

    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.prodLineItemId) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
            }

            long prodLineItemId = 0
            try {
                prodLineItemId = Long.parseLong(parameterMap.prodLineItemId.toString())
            } catch (Exception e) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
            }

            InvProductionLineItem invProductionLineItem = (InvProductionLineItem) invProductionLineItemCacheUtility.read(prodLineItemId)
            if (!invProductionLineItem) {
                result.put(Tools.MESSAGE, PROD_LINE_ITEM_NOT_FOUND)
                return result
            }

            result.put(PROD_LINE_ITEM_OBJ, invProductionLineItem)
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

    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receivedResult = (LinkedHashMap) obj
            InvProductionLineItem invProductionLineItem = (InvProductionLineItem) receivedResult.get(PROD_LINE_ITEM_OBJ)
            Map serviceReturn = invProductionDetailsCacheUtility.getFinishedMaterialsByLineItem(invProductionLineItem.id)
            result.put(FINISHED_MATERIAL_LIST, getFinishedList(serviceReturn.lstFinished))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receivedResult = (LinkedHashMap) obj
            result.put(FINISHED_MATERIAL_LIST, Tools.listForKendoDropdown(receivedResult.get(FINISHED_MATERIAL_LIST),null,null))
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receivedResult = (LinkedHashMap) obj

            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receivedResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, receivedResult.get(Tools.MESSAGE))
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

    private List getFinishedList(List<InvProductionDetails> lstFinished) {
        List latMaterials = []
        Map materialMap
        Item item
        for (int i = 0; i < lstFinished.size(); i++) {
            item = (Item) itemCacheUtility.read(lstFinished[i].materialId)
            materialMap = ['id': lstFinished[i].id, 'name': item.name]
            latMaterials << materialMap
        }
        return latMaterials
    }
}