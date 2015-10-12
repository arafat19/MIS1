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
 *  Update item type object and grid data
 *  For details go through Use-Case doc named 'UpdateItemTypeActionService'
 */
class UpdateItemTypeActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    ItemTypeService itemTypeService
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility
    @Autowired
    ItemCategoryCacheUtility itemCategoryCacheUtility

    private static final String INVALID_INPUT_MSG = "Error occurred due to invalid input"
    private static final String UPDATE_FAILURE_MESSAGE = "Item type could not be updated"
    private static final String UPDATE_SUCCESS_MESSAGE = "Item type has been updated successfully"
    private static final String NAME_ALREADY_EXISTS = "Same item type name already exists"
    private static final String OBJ_NOT_FOUND = "Selected item type not found"
    private static final String ITEM_TYPE = "itemType"
    /**
     * 1. check user access as Admin role
     * 2. Check existence of required parameters
     * 3. pull item type object from cache utility
     * 4. check item type existence
     * 5. Build object for update
     * 6. duplicate check for item type name
     * @param params -serialize parameters from UI
     * @param obj - N/A
     * @return - a map containing item type & isError(true/false) depending on method success &  relevant message.
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            if (!appSessionUtil.getAppUser().isPowerUser) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
                return result
            }

            GrailsParameterMap params = (GrailsParameterMap) parameters

            //Check existence of required parameters
            if ((!params.id) || (!params.name) || (!params.categoryId)) {
                result.put(Tools.MESSAGE, INVALID_INPUT_MSG)
                return result
            }

            long id = Long.parseLong(params.id.toString())
            ItemType oldItemType = (ItemType) itemTypeCacheUtility.read(id)
            if (!oldItemType) {
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND)
                return result
            }

            //Build object for update
            ItemType newItemType = buildItemTypeObject(params, oldItemType)

            //Check duplicate name
            int duplicateName = itemTypeCacheUtility.countByNameAndIdNotEqual(newItemType.name, newItemType.id)
            if (duplicateName > 0) {
                result.put(Tools.MESSAGE, NAME_ALREADY_EXISTS)
                return result
            }

            result.put(ITEM_TYPE, newItemType)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }
    /**
     * 1. receive item type object from pre execute method
     * 2. Update item type
     * 3. update item type to corresponding cache utility & sort cache utility
     * This method is in transactional block and will roll back in case of any exception
     * @param parameters - N/A
     * @param obj - receive project object from pre execute method
     * @return - a map containing item type object & isError(true/false)
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            ItemType itemType = (ItemType) preResult.get(ITEM_TYPE)
            itemTypeService.update(itemType)
            itemType.version++
            itemTypeCacheUtility.update(itemType, itemTypeCacheUtility.NAME, itemTypeCacheUtility.SORT_ORDER_ASCENDING)
            result.put(ITEM_TYPE, itemType)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(UPDATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
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
     * Wrap list of item type in grid entity
     * @param obj -item type object
     * @return -list of wrapped item type
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
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
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, UPDATE_SUCCESS_MESSAGE)
            result.put(ITEM_TYPE, resultMap)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }
    /**
     * Build failure result in case of any error
     * @param obj -N/A
     * @return -a map containing isError = true & relevant error message
     */
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
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }
    /**
     * Build item type object
     * @param parameterMap - serialize parameters from UI
     * @param oldItemType - previous state of item type object
     * @return - newly built item type object
     */
    private ItemType buildItemTypeObject(GrailsParameterMap parameterMap, ItemType oldItemType) {
        ItemType itemType = new ItemType(parameterMap)

        itemType.id = oldItemType.id
        itemType.version = oldItemType.version
        itemType.companyId = oldItemType.companyId
        itemType.createdBy = oldItemType.createdBy
        itemType.createdOn = oldItemType.createdOn

        itemType.updatedBy = appSessionUtil.getAppUser().id
        itemType.updatedOn = new Date()

        return itemType
    }
}

