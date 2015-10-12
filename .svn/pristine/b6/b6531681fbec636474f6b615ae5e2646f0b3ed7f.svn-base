package com.athena.mis.integration.accounting

import com.athena.mis.PluginConnector
import com.athena.mis.accounting.service.*
import com.athena.mis.application.entity.Company
import com.athena.mis.application.service.*
import org.hibernate.SessionFactory
import org.springframework.transaction.annotation.Transactional

class AccDefaultDataBootStrapService {
    SessionFactory sessionFactory
    RoleService roleService
    AppUserService appUserService
    UserRoleService userRoleService
    AccTypeService accTypeService
    AccGroupService accGroupService
    SystemEntityService systemEntityService
    SysConfigurationService sysConfigurationService
    SystemEntityTypeService systemEntityTypeService
    RequestMapService requestMapService
    RoleFeatureMappingService roleFeatureMappingService
    AppMailService appMailService
    ReservedSystemEntityService reservedSystemEntityService
    RoleTypeService roleTypeService
    AccFinancialYearService accFinancialYearService
    AccTier1Service accTier1Service
    AccTier2Service accTier2Service
    AccTier3Service accTier3Service

    @Transactional
    public void init() {
        Company defaultCompany = Company.list(readOnly: true)[0]    // read the only object
        long companyId = defaultCompany.id

        systemEntityTypeService.createDefaultDataForAcc()
        reservedSystemEntityService.createDefaultDataForAccounting()
        systemEntityService.createDefaultAccSource(companyId)
        systemEntityService.createDefaultAccVoucherType(companyId)
        systemEntityService.createDefaultAccInstrumentType(companyId)
        sysConfigurationService.createDefaultAccSysConfig(companyId)
        roleFeatureMappingService.createRoleFeatureMapForAccountingPlugin()
        roleTypeService.createDefaultDataForAccounting()
        roleService.createDefaultDataForAccounting(companyId)
        flushSession()
        requestMapService.createRequestMapForAccounting()
        flushSession()
        requestMapService.appendRoleInRequestMap(PluginConnector.ACCOUNTING_ID)
        appUserService.createDefaultDataForAccounting(companyId)
        userRoleService.createDefaultDataForAccounting()
        accTypeService.createDefaultData(companyId)
        accGroupService.createDefaultData(companyId)
        appMailService.createDefaultDataForAccounting(companyId)
        accFinancialYearService.createDefaultData(companyId)
        accTier1Service.createDefaultData(companyId)
        accTier2Service.createDefaultData(companyId)
        accTier3Service.createDefaultData(companyId)
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
