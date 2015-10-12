package com.athena.mis.integration.inventory

import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.AppUserEntity

public abstract class InventoryPluginConnector extends PluginConnector {

    //read inv Inventory Transaction Object by purchaseOrderId
    public abstract Object readInvTransactionByPurchaseOrderId(long purchaseOrderId)

    //read invInventory object by id
    public abstract Object readInventory(long id)

    //get inventory type siteId
    public abstract Long getInventoryTypeSiteId()

    //get consumeItem by budget and item id
    public abstract int getConsumeItemByBudgetAndItemId(long budgetId, long itemId)

    //get total approved consumption of the project
    public abstract String getTotalApprovedConsumptionByProjectId(long projectId)

    //get material stock balance of the project
    public abstract String getMaterialStockBalanceByProjectId(long projectId)

    //get fixed asset stock balance of the project
    public abstract String getFixedAssetStockBalanceByProjectId(long projectId)

    //Get Inventory Transaction Entity Type Supplier
    public abstract Long getTransactionEntityTypeIdSupplier()

    //Get Inventory Transaction Entity Type None
    public abstract Long getTransactionEntityTypeIdNone()

    //get inventory type list
    public abstract List<Object> getInventoryTypeList()

    //get InvProductionItemTypeFinishedMaterialId
    public abstract Long getInvProductionItemTypeFinishedMaterialId()

    //init system config
    public abstract void initInvSysConfiguration()

    // Re-initialize the whole cacheUtility of InvProductionItem (used in create,update,delete)
    public abstract void initInvProductionItemTypeCacheUtility()

    // Re-initialize the whole cacheUtility of InvTransactionEntity (used in create,update,delete)
    public abstract void initInvTransactionEntityTypeCacheUtility()

    // Re-initialize the whole cacheUtility of InvInventory (used in create,update,delete)
    public abstract void initInvInventoryTypeCacheUtility()

    // Re-initialize the whole cacheUtility of InvTransactionType (used in create,update,delete)
    public abstract void initInvTransactionTypeCacheUtility()

    // get list of InvProductionItemType system entity
    public abstract List<Object> listInvProductionItemType()

    // get list of InvTransactionEntityType system entity
    public abstract List<Object> listInvTransactionEntityType()

    // get active list of InvTransactionType system entity
    public abstract List<Object> listInvTransactionType()

    // get transaction type id In
    public abstract long getInvTransactionTypeIdIn()

    // get transaction type id consumption
    public abstract long getInvTransactionTypeIdConsumption()

    // get reserved system entity of production item type
    public abstract Object readByReservedProductionItemType(long reservedId, long companyId)

    // get reserved system entity of transaction type
    public abstract Object readByReservedTransactionType(long reservedId, long companyId)

    // get reserved system entity of transaction entity type
    public abstract Object readByReservedTransactionEntityType(long reservedId, long companyId)

    // get reserved system entity of inventory type
    public abstract Object readByReservedInventoryType(long reservedId, long companyId)

    public abstract void bootStrap(boolean isSchema, boolean isData)

    // init session value
    public abstract void initSession()

    //get list of inventory ids mapped with logged in user
    public abstract List<Long> getUserInventoryIds()

    // add user inventory in cache
    public abstract boolean addUserInventoryInCache(AppUserEntity appUserEntity)

    // update user inventory in cache
    public abstract boolean updateUserInventoryInCache(AppUserEntity appUserEntity)

    // delete user inventory from cache
    public abstract boolean deleteUserInventoryFromCache(long appUserEntityId)

    //give user and inventory mapped inventory objects
    public abstract List<Object> getUserInventoriesByType(long typeId)
}