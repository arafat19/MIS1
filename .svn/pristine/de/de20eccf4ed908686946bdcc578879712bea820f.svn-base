package com.athena.mis.integration.arms

import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.Company
import com.athena.mis.application.service.*
import com.athena.mis.arms.service.RmsExchangeHouseCurrencyPostingService
import com.athena.mis.arms.service.RmsExchangeHouseService
import com.athena.mis.arms.service.RmsProcessInstrumentMappingService
import org.hibernate.SessionFactory

class ArmsDefaultDataBootStrapService {

    SessionFactory sessionFactory
    SystemEntityTypeService systemEntityTypeService
    ReservedSystemEntityService reservedSystemEntityService
    SystemEntityService systemEntityService
    RequestMapService requestMapService
    RoleFeatureMappingService roleFeatureMappingService

    RmsExchangeHouseService rmsExchangeHouseService
    RmsExchangeHouseCurrencyPostingService rmsExchangeHouseCurrencyPostingService
    RmsProcessInstrumentMappingService rmsProcessInstrumentMappingService
    RoleTypeService roleTypeService
    RoleService roleService
    AppUserService appUserService
    UserRoleService userRoleService
    AppUserEntityService appUserEntityService

    public void init() {
        Company defaultCompany = Company.list(readOnly: true)[0]    // read the only object
        long companyId = defaultCompany.id

        systemEntityTypeService.createDefaultDataForArms()
        reservedSystemEntityService.createDefaultDataForArms()
        systemEntityService.createDefaultDataProcessTypeForArms(companyId)
        systemEntityService.createDefaultDataInstrumentTypeForArms(companyId)
        systemEntityService.createDefaultDataPaymentMethodForArms(companyId)
        systemEntityService.createDefaultDataTaskStatusForArms(companyId)
        systemEntityService.createDefaultDataForRmsTaskNote(companyId)
        systemEntityService.createDefaultDataForArmsAppUserEntity(companyId)
        requestMapService.createRequestMapForARMS()
        roleFeatureMappingService.createRoleFeatureMapForArmsPlugin()
        roleTypeService.createDefaultDataForArms()
        roleService.createDefaultDataForArms(companyId)
        appUserService.createDefaultDataForArms(companyId)
        userRoleService.createDefaultDataForArms()

        flushSession()
        requestMapService.appendRoleInRequestMap(PluginConnector.ARMS_ID)

        rmsExchangeHouseService.createDefaultDataForRmsExchangeHouse(companyId)
        rmsExchangeHouseCurrencyPostingService.createDefaultDataForRmsExchangeHouseCurrencyPosting(companyId)
        rmsProcessInstrumentMappingService.createDefaultDataForRmsProcessInstrumentMapping(companyId)
        appUserEntityService.createDefaultDataForUserArms(companyId)
    }

    private void flushSession() {
        def hibSession = sessionFactory.getCurrentSession()
        if (hibSession) {
            hibSession.flush()
        }
    }
}
