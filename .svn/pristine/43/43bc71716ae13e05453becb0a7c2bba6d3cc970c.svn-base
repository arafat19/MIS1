package com.athena.mis.integration.inventory

import com.athena.mis.inventory.config.InvSysConfigurationCacheUtility
import com.athena.mis.inventory.utility.*
import org.springframework.beans.factory.annotation.Autowired

class InventoryBootStrapService {

    @Autowired
    InvInventoryTypeCacheUtility invInventoryTypeCacheUtility
    @Autowired
    InvInventoryCacheUtility invInventoryCacheUtility
    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility
    @Autowired
    InvTransactionEntityTypeCacheUtility invTransactionEntityTypeCacheUtility
    @Autowired
    InvProductionItemTypeCacheUtility invProductionItemTypeCacheUtility
    @Autowired
    InvProductionDetailsCacheUtility invProductionDetailsCacheUtility
    @Autowired
    InvProductionLineItemCacheUtility invProductionLineItemCacheUtility
    @Autowired
    InvUserInventoryCacheUtility invUserInventoryCacheUtility
    @Autowired
    InvSysConfigurationCacheUtility invSysConfigurationCacheUtility

    public void init() {
        initAllUtility()
    }

    private void initAllUtility() {
        invTransactionTypeCacheUtility.init()
        invTransactionEntityTypeCacheUtility.init()
        invProductionItemTypeCacheUtility.init()
        invProductionDetailsCacheUtility.init()
        invProductionLineItemCacheUtility.init()
        invInventoryTypeCacheUtility.init()
        invInventoryCacheUtility.init()
        invUserInventoryCacheUtility.init()
        invSysConfigurationCacheUtility.init()
    }
}
