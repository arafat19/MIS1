package com.athena.mis.exchangehouse.integration.exchangehouse

import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.Company
import com.athena.mis.application.service.*
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.exchangehouse.service.*
import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

import javax.sql.DataSource

class ExhDefaultDataBootStrapService {

    DataSource dataSource
    SessionFactory sessionFactory
    SystemEntityService systemEntityService
    SystemEntityTypeService systemEntityTypeService
    ReservedSystemEntityService reservedSystemEntityService
    RequestMapService requestMapService
    ThemeService themeService
    CurrencyService currencyService
    RoleService roleService
    AppUserService appUserService
    UserRoleService userRoleService
    AppMailService appMailService
    ExhRegularFeeService exhRegularFeeService
    SysConfigurationService sysConfigurationService
    RoleFeatureMappingService roleFeatureMappingService
    CompanyService companyService
    ExhPhotoIdTypeService exhPhotoIdTypeService
    ExhRemittancePurposeService exhRemittancePurposeService
    ExhCurrencyConversionService exhCurrencyConversionService
    ExhSanctionService exhSanctionService
    ExhPostalCodeService exhPostalCodeService
    RoleTypeService roleTypeService

    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    @Transactional
    def init() {
        Company defaultCompany = Company.list(readOnly: true)[0]    // read the only object
        long companyId = defaultCompany.id

        appMailService.createDefaultDataForExchangeHouse(companyId)
        exhSanctionService.createDefaultData()
        exhCurrencyConversionService.createDefaultData(companyId)
        exhRemittancePurposeService.createDefaultData(companyId)
        exhPhotoIdTypeService.createDefaultData(companyId)
        exhRegularFeeService.createDefaultData(companyId)
        reservedSystemEntityService.createDefaultDataForExh()
        systemEntityTypeService.createDefaultDataForExh()
        systemEntityService.createDefaultDataPaidByForExh(companyId)
        systemEntityService.createDefaultDataPaymentMethodForExh(companyId)
        systemEntityService.createDefaultDataTaskStatusForExh(companyId)
        systemEntityService.createDefaultDataTaskTypeForExh(companyId)
        roleService.createDefaultDataForExh(companyId)
        requestMapService.createRequestMapForExchangeHouse()
        roleFeatureMappingService.createRoleFeatureMapForExchangeHousePlugin()
        roleTypeService.createDefaultDataForExh(companyId)

        //@todo: remove conflict with AppDefaultDataBootStrapService - themeService.createDefaultData()
        //themeService.createDefaultDataExchangeHouse(companyId)
        appUserService.createDefaultDataForExchangeHouse(companyId)
        userRoleService.createDefaultDataExchangeHouse()
        sysConfigurationService.createDefaultDataForExh(companyId)
        exhPostalCodeService.createDefaultDataForExhPostalCode(companyId)

        flushSession()
        requestMapService.appendRoleInRequestMap(PluginConnector.EXCHANGE_HOUSE_ID)
    }


    private void flushSession() {
        def hibSession = sessionFactory.getCurrentSession()
        if (hibSession) {
            hibSession.flush()
        }
    }

}
