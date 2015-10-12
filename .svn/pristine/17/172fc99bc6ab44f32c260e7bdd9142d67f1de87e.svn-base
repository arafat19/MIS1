package com.athena.mis.inventory.actions.invproductiondetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.inventory.entity.InvProductionDetails
import com.athena.mis.inventory.entity.InvProductionLineItem
import com.athena.mis.inventory.utility.InvProductionDetailsCacheUtility
import com.athena.mis.inventory.utility.InvProductionItemTypeCacheUtility
import com.athena.mis.inventory.utility.InvProductionLineItemCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

// List of Inventory Production Details
class ListInvProductionDetailsActionService extends BaseService implements ActionIntf {

    @Autowired
    InvProductionDetailsCacheUtility invProductionDetailsCacheUtility
    @Autowired
    InvProductionItemTypeCacheUtility invProductionItemTypeCacheUtility
    @Autowired
    InvProductionLineItemCacheUtility invProductionLineItemCacheUtility
    @Autowired
    ItemCacheUtility itemCacheUtility

    protected final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to load production details list"
    private static final String PRODUCTION_DETAILS_LIST = "productionDetailsList"
    private static final String COUNT = "count"

    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            initPager(parameterMap)
            long productionLineItemId = Long.parseLong(parameterMap.productionLineItemId.toString())
            Map lstResult = invProductionDetailsCacheUtility.listByProductionItemTypeId(productionLineItemId, this)
            List<InvProductionDetails> productionDetailsList = lstResult.list
            int count = lstResult.count
            result.put(PRODUCTION_DETAILS_LIST, productionDetailsList)
            result.put(COUNT, count)
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
        // do nothing for post operation
        return null
    }

    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj
            List<InvProductionDetails> productionDetailsList = (List<InvProductionDetails>) executeResult.get(PRODUCTION_DETAILS_LIST)
            int count = (int) executeResult.get(COUNT)
            List productionDetailsListWrap = wrapListInGridEntityList(productionDetailsList, this.start)
            result = [page: pageNumber, total: count, rows: productionDetailsListWrap]
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result = [page: pageNumber, total: 0, rows: null, isError: Boolean.TRUE, message: DEFAULT_ERROR_MESSAGE]
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
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    private List wrapListInGridEntityList(List<InvProductionDetails> productionDetailsList, int start) {
        List productionDetails = []
        int counter = start + 1
        SystemEntity invProductionItemType
        Item item
        for (int i = 0; i < productionDetailsList.size(); i++) {
            InvProductionLineItem invProductionLineItem = (InvProductionLineItem) invProductionLineItemCacheUtility.read(productionDetailsList[i].productionLineItemId)
            invProductionItemType = (SystemEntity) invProductionItemTypeCacheUtility.read(productionDetailsList[i].productionItemTypeId)
            item = (Item) itemCacheUtility.read(productionDetailsList[i].materialId)
            GridEntity obj = new GridEntity()
            obj.id = productionDetailsList[i].id
            obj.cell = [
                    counter,
                    productionDetailsList[i].id,
                    invProductionItemType.key,
                    item.name,
                    Tools.makeAmountWithThousandSeparator(productionDetailsList[i].overheadCost)
            ]
            productionDetails << obj
            counter++
        }
        return productionDetails
    }
}