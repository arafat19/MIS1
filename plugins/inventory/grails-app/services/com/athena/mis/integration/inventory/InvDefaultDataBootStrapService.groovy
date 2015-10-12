package com.athena.mis.integration.inventory

import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.Company
import com.athena.mis.application.service.*
import com.athena.mis.inventory.service.InvInventoryService
import org.hibernate.SessionFactory
import org.springframework.transaction.annotation.Transactional

class InvDefaultDataBootStrapService {
    SessionFactory sessionFactory
    SystemEntityService systemEntityService
    SystemEntityTypeService systemEntityTypeService
    RoleService roleService
    AppUserService appUserService
    UserRoleService userRoleService
    RequestMapService requestMapService
    RoleFeatureMappingService roleFeatureMappingService
    AppMailService appMailService
    ReservedSystemEntityService reservedSystemEntityService
    RoleTypeService roleTypeService
    InvInventoryService invInventoryService
    AppUserEntityService appUserEntityService

    @Transactional
    public void init() {
        Company defaultCompany = Company.list(readOnly: true)[0]    // read the only object
        long companyId = defaultCompany.id

        systemEntityTypeService.createDefaultDataForInv()
        reservedSystemEntityService.createDefaultDataForInventory()
        systemEntityService.createDefaultInvProductionItemType(companyId)
        systemEntityService.createDefaultTransactionType(companyId)
        systemEntityService.createDefaultTransactionEntityType(companyId)
        systemEntityService.createDefaultInventoryType(companyId)
        roleTypeService.createDefaultDataForInventory()
        roleService.createDefaultDataForInventory(companyId)
        roleFeatureMappingService.createRoleFeatureMapForInventoryPlugin()
        flushSession()
        requestMapService.createRequestMapForInventory()
        flushSession()
        requestMapService.appendRoleInRequestMap(PluginConnector.INVENTORY_ID)
        appUserService.createDefaultDataForInventory(companyId)
        userRoleService.createDefaultDataForInventory()
        appMailService.createDefaultDataForInventory(companyId)
        invInventoryService.createDefaultData(companyId)
        flushSession()
        appUserEntityService.createDefaultDataForUserInventory(companyId)
    }

    /**
     * Flush the current session when necessary to apply GORM
     */
    private void flushSession() {
        def hibSession = sessionFactory.getCurrentSession()
        if (hibSession) {
            hibSession.flush()
        }
    }
}
