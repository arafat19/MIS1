package com.athena.mis.application.actions.itemtype

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.ItemType
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.ItemTypeService
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.ItemCategoryCacheUtility
import com.athena.mis.application.utility.ItemTypeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Create new item type object and show in grid
 *  For details go through Use-Case doc named 'CreateItemTypeActionService'
 */
class CreateItemTypeActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    ItemTypeService itemTypeService
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility
    @Autowired
    ItemCategoryCacheUtility itemCategoryCacheUtility

    private static final String INVALID_INPUT_MSG = "Error occurred due to invalid input"
    private static final String CREATE_SUCCESS_MSG = "Item type has been successfully saved"
    private static final String CREATE_FAILURE_MSG = "Item type has not been saved"
    private static final String NAME_ALREADY_EXISTS = "Same item type name already exists"
    private static final String ITEM_TYPE = "itemType"
    /**
     * 1. check user access as Admin role
     * 2. Check existence of required parameters
     * 3. Build ItemType Object
     * 4. duplicate check for item type name
     * @param parameters - serialize parameters from UI
     * @param obj - N/A
     * @return - a map containing item type object & isError(true/false) depending on method success
     *   & relevant message.
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            if (!appSessionUtil.getAppUser().isPowerUser) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }

            GrailsParameterMap params = (GrailsParameterMap) parameters

            //Check existence of required parameters
            if (!params.name || !params.categoryId) {
                result.put(Tools.MESSAGE, INVALID_INPUT_MSG)
                return result
            }

            //Build ItemType Object
            ItemType itemType = buildItemTypeObject(params)

            //Check duplicate name
            int duplicateName = itemTypeCacheUtility.countByName(itemType.name)
            if (duplicateName > 0) {
                result.put(Tools.MESSAGE, NAME_ALREADY_EXISTS)
                return result
            }

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(ITEM_TYPE, itemType)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            return result
        }
    }
    /**
     * Save item type object in DB and update cache utility accordingly
     * This method is in transactional block and will roll back in case of any exception
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            ItemType itemType = (ItemType) preResult.get(ITEM_TYPE)
            ItemType newItemType = itemTypeService.create(itemType)
            itemTypeCacheUtility.add(newItemType, itemTypeCacheUtility.NAME, itemTypeCacheUtility.SORT_ORDER_ASCENDING)
            result.put(ITEM_TYPE, newItemType)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(CREATE_FAILURE_MSG)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CREATE_FAILURE_MSG)
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
     * Show newly created item type object in grid
     * 1. receive item type from execute method
     * 2. pull item category from cache utility
     * @param obj - map from execute method
     * @return - a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
            ItemType itemType = (ItemType) executeResult.get(ITEM_TYPE)
            SystemEntity itemCategory = (SystemEntity) itemCategoryCacheUtility.read(itemType.categoryId)
            GridEntity object = new GridEntity()
            object.id = itemType.id
            object.cell = [
                    Tools.LABEL_NEW,
                    itemType.id,
                    itemCategory.key,
                    itemType.name
            ]

            Map resultMap = [entity: object, version: itemType.version]
            result.put(ITEM_TYPE, resultMap)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, CREATE_SUCCESS_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CREATE_FAILURE_MSG)
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
                result.put(Tools.MESSAGE, CREATE_FAILURE_MSG)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CREATE_FAILURE_MSG)
            return result
        }
    }
    /**
     * Build item type object
     * @param parameterMap - serialize parameters from UI
     * @return - newly built item type object
     */
    private ItemType buildItemTypeObject(GrailsParameterMap parameterMap) {
        ItemType itemType = new ItemType(parameterMap)

        itemType.createdBy = appSessionUtil.getAppUser().id
        itemType.createdOn = new Date()
        itemType.companyId = appSessionUtil.getAppUser().companyId
        itemType.updatedBy = 0L
        itemType.updatedOn = null

        return itemType
    }
}

