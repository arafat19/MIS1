package com.athena.mis.integration.budget

import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.Company
import com.athena.mis.application.service.*
import com.athena.mis.budget.service.BudgetScopeService
import org.hibernate.SessionFactory
import org.springframework.transaction.annotation.Transactional

class BudgDefaultDataBootStrapService {
    SessionFactory sessionFactory
    BudgetScopeService budgetScopeService
    RoleService roleService
    AppUserService appUserService
    UserRoleService userRoleService
    RequestMapService requestMapService
    RoleFeatureMappingService roleFeatureMappingService
    RoleTypeService roleTypeService
    SystemEntityTypeService systemEntityTypeService
    SystemEntityService systemEntityService
    ReservedSystemEntityService reservedSystemEntityService

    @Transactional
    public void init() {

        Company defaultCompany = Company.list(readOnly: true)[0]    // read the only object
        long companyId = defaultCompany.id

        systemEntityTypeService.createDefaultDataForBudget()
        reservedSystemEntityService.createDefaultDataForBudget()
        budgetScopeService.createDefaultData(companyId)
        systemEntityService.createDefaultBudgetTaskStatus(companyId)
        roleFeatureMappingService.createRoleFeatureMapForBudgetPlugin()
        roleTypeService.createDefaultDataForBudgetAndProcurement()
        roleService.createDefaultDataForBudgetAndProcurement(companyId)
        flushSession()
        requestMapService.createRequestMapForBudget()
        flushSession()
        requestMapService.appendRoleInRequestMap(PluginConnector.BUDGET_ID)
        appUserService.createDefaultDataForBudgetAndProcurement(companyId)
        userRoleService.createDefaultDataForBudgetAndProcurement()
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
