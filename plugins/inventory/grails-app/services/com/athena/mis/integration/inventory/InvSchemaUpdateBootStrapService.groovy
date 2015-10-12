package com.athena.mis.integration.inventory

import com.athena.mis.inventory.service.InventoryModelService
import org.springframework.transaction.annotation.Transactional

class InvSchemaUpdateBootStrapService {

    InventoryModelService inventoryModelService

    @Transactional
    public void init() {
        inventoryModelService.createDefaultSchema()
    }
}
