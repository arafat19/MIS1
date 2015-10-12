package com.athena.mis.integration.inventory

import com.athena.mis.application.entity.AppUserEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.SupplierCacheUtility
import com.athena.mis.integration.inventory.actions.*
import com.athena.mis.inventory.config.InvSysConfigurationCacheUtility
import com.athena.mis.inventory.entity.InvInventory
import com.athena.mis.inventory.utility.*
import org.springframework.beans.factory.annotation.Autowired

class InventoryImplService extends InventoryPluginConnector {

    static transactional = false
    static lazyInit = false
    // registering plugin in servletContext
    @Override
    public boolean initialize() {
        setPlugin(INVENTORY, this);
        return true
    }

    @Override
    public String getName() {
        return INVENTORY;
    }

    @Override
    public int getId() {
        return INVENTORY_ID;
    }

    @Autowired
    SupplierCacheUtility supplierCacheUtility
    @Autowired
    InvInventoryCacheUtility invInventoryCacheUtility
    @Autowired
    InvInventoryTypeCacheUtility invInventoryTypeCacheUtility
    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility
    @Autowired
    InvTransactionEntityTypeCacheUtility invTransactionEntityTypeCacheUtility
    @Autowired
    InvProductionItemTypeCacheUtility invProductionItemTypeCacheUtility
    @Autowired
    InvSysConfigurationCacheUtility invSysConfigurationCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    InvUserInventoryCacheUtility invUserInventoryCacheUtility

    ReadInvTransactionByPurchaseOrderIdImplActionService readInvTransactionByPurchaseOrderIdImplActionService
    GetTotalApprovedConsumptionByProjectIdImplActionService getTotalApprovedConsumptionByProjectIdImplActionService
    GetConsumeItemByBudgetAndItemIdImplActionService getConsumeItemByBudgetAndItemIdImplActionService
    GetMaterialStockBalanceByProjectIdImplActionService getMaterialStockBalanceByProjectIdImplActionService
    GetFixedAssetStockBalanceByProjectIdImplActionService getFixedAssetStockBalanceByProjectIdImplActionService

    InvDefaultDataBootStrapService invDefaultDataBootStrapService
    InventoryBootStrapService inventoryBootStrapService
    InvSchemaUpdateBootStrapService invSchemaUpdateBootStrapService

    //read inv Inventory Transaction Object by purchaseOrderId
    Object readInvTransactionByPurchaseOrderId(long purchaseOrderId) {
        return readInvTransactionByPurchaseOrderIdImplActionService.execute(purchaseOrderId, null)
    }

    //read invInventory object by id
    Object readInventory(long id) {
        return invInventoryCacheUtility.read(id)
    }

    //get inventory type siteId
    Long getInventoryTypeSiteId() {
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        // pull inventoryType object
        SystemEntity inventoryTypeSiteObj = (SystemEntity) invInventoryTypeCacheUtility.readByReservedAndCompany(invInventoryTypeCacheUtility.TYPE_SITE, companyId)

        return new Long(inventoryTypeSiteObj.id)
    }

    //get total consumed-Amount of an item against a particular budget
    int getConsumeItemByBudgetAndItemId(long budgetId, long itemId) {
        return (int) getConsumeItemByBudgetAndItemIdImplActionService.execute(budgetId, itemId)
    }

    //get total approved consumption of a particular project
    String getTotalApprovedConsumptionByProjectId(long projectId) {
        return getTotalApprovedConsumptionByProjectIdImplActionService.execute(projectId, null)
    }

    //get material stock balance of a particular project
    String getMaterialStockBalanceByProjectId(long projectId) {
        return getMaterialStockBalanceByProjectIdImplActionService.execute(projectId, null)
    }

    //get item category fixed asset stock balance of a particular project
    String getFixedAssetStockBalanceByProjectId(long projectId) {
        return getFixedAssetStockBalanceByProjectIdImplActionService.execute(projectId, null)
    }

    //Get Inventory Transaction Entity Type Supplier
    Long getTransactionEntityTypeIdSupplier() {
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionEntitySupplier = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_SUPPLIER, companyId)
        return new Long(transactionEntitySupplier.id)
    }

    //Get Inventory Transaction Entity Type None
    Long getTransactionEntityTypeIdNone() {
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionEntityNone = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_NONE, companyId)
        return new Long(transactionEntityNone.id)
    }

    //get active inventory type list
    List<Object> getInventoryTypeList() {
        return invInventoryTypeCacheUtility.listByIsActive()
    }

    //get InvProductionItemTypeFinishedMaterialId
    Long getInvProductionItemTypeFinishedMaterialId() {
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity finishedProduct = (SystemEntity) invProductionItemTypeCacheUtility.readByReservedAndCompany(invProductionItemTypeCacheUtility.TYPE_FINISHED_MATERIAL_ID, companyId)
        return new Long(finishedProduct.id)
    }

    //init system config
    void initInvSysConfiguration() {
        invSysConfigurationCacheUtility.init()
    }

    // Re-initialize the whole cacheUtility of InvProductionItem (used in create,update,delete)
    public void initInvProductionItemTypeCacheUtility() {
        invProductionItemTypeCacheUtility.init()
    }

    // Re-initialize the whole cacheUtility of InvTransactionEntity (used in create,update,delete)
    public void initInvTransactionEntityTypeCacheUtility() {
        invTransactionEntityTypeCacheUtility.init()
    }

    // Re-initialize the whole cacheUtility of InvInventory (used in create,update,delete)
    public void initInvInventoryTypeCacheUtility() {
        invInventoryTypeCacheUtility.init()
    }

    // Re-initialize the whole cacheUtility of InvTransactionType (used in create,update,delete)
    public void initInvTransactionTypeCacheUtility() {
        invTransactionTypeCacheUtility.init()
    }

    // get active list of InvProductionItemType system entity
    public List<Object> listInvProductionItemType() {
        return invProductionItemTypeCacheUtility.listByIsActive()
    }

    // get active list of InvTransactionEntityType system entity
    public List<Object> listInvTransactionEntityType() {
        return invTransactionEntityTypeCacheUtility.listByIsActive()
    }

    // get active list of InvTransactionType system entity
    public List<Object> listInvTransactionType() {
        return invTransactionTypeCacheUtility.listByIsActive()
    }

    // get transaction type id IN
    long getInvTransactionTypeIdIn() {
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypeIn = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_IN, companyId)
        return transactionTypeIn.id
    }

    // get transaction type id consumption
    long getInvTransactionTypeIdConsumption() {
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypeConsumption = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_CONSUMPTION, companyId)
        return transactionTypeConsumption.id
    }

    // get reserved system entity of production item type
    public Object readByReservedProductionItemType(long reservedId, long companyId) {
        return invProductionItemTypeCacheUtility.readByReservedAndCompany(reservedId, companyId)
    }

    // get reserved system entity of transaction type
    public Object readByReservedTransactionType(long reservedId, long companyId) {
        return invTransactionTypeCacheUtility.readByReservedAndCompany(reservedId, companyId)
    }

    // get reserved system entity of transaction entity type
    public Object readByReservedTransactionEntityType(long reservedId, long companyId) {
        return invTransactionEntityTypeCacheUtility.readByReservedAndCompany(reservedId, companyId)
    }

    // get reserved system entity of inventory type
    public Object readByReservedInventoryType(long reservedId, long companyId) {
        return invInventoryTypeCacheUtility.readByReservedAndCompany(reservedId, companyId)
    }

    public void bootStrap(boolean isSchema, boolean isData) {
        if (isData) invDefaultDataBootStrapService.init()
        if (isSchema) invSchemaUpdateBootStrapService.init()
        inventoryBootStrapService.init()
    }

    // init session value
    public void initSession() {
        invSessionUtil.init()
    }

    //get list of inventory ids mapped with logged in user
    public List<Long> getUserInventoryIds() {
        return invSessionUtil.getUserInventoryIds()
    }

    // add user inventory in cache
    public boolean addUserInventoryInCache(AppUserEntity appUserEntity) {
        invUserInventoryCacheUtility.add(appUserEntity, invUserInventoryCacheUtility.SORT_BY_ID, invUserInventoryCacheUtility.SORT_ORDER_ASCENDING)
        return true
    }

    // update user inventory in cache
    public boolean updateUserInventoryInCache(AppUserEntity appUserEntity) {
        invUserInventoryCacheUtility.update(appUserEntity, invUserInventoryCacheUtility.SORT_BY_ID, invUserInventoryCacheUtility.SORT_ORDER_ASCENDING)
        return true
    }

    // delete user inventory from cache
    public boolean deleteUserInventoryFromCache(long appUserEntityId) {
        invUserInventoryCacheUtility.delete(appUserEntityId)
        return true
    }

    public List<InvInventory> getUserInventoriesByType(long typeId) {
        return invSessionUtil.getUserInventoriesByType(typeId)
    }

}
