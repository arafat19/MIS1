package com.athena.mis.integration.application

import com.athena.mis.PluginConnector
import com.athena.mis.application.service.*
import org.hibernate.SessionFactory
import org.springframework.transaction.annotation.Transactional

class AppDefaultDataBootStrapService {
    SessionFactory sessionFactory
    RequestMapService requestMapService
    CompanyService companyService
    ProjectService projectService
    RoleService roleService
    AppUserService appUserService
    UserRoleService userRoleService
    ItemService itemService
    AppMailService appMailService
    AppUserEntityService appUserEntityService
    SupplierService supplierService
    CountryService countryService
    SystemEntityTypeService systemEntityTypeService
    SystemEntityService systemEntityService
    CurrencyService currencyService
    ContentCategoryService contentCategoryService
    RoleFeatureMappingService roleFeatureMappingService
    RoleTypeService roleTypeService
    ItemTypeService itemTypeService
    ReservedSystemEntityService reservedSystemEntityService
    SysConfigurationService sysConfigurationService
    ThemeService themeService
    VehicleService vehicleService
    DesignationService designationService
    EmployeeService employeeService
    BankService bankService
    BankBranchService bankBranchService
    DistrictService districtService
    AppGroupService appGroupService
    SmsService smsService

    public static final Long companyId = 1L

    @Transactional
    public void init() {
        companyService.createDefaultData(companyId)

        systemEntityTypeService.createDefaultDataForApp()
        sysConfigurationService.createDefaultAppSysConfig(companyId)
        reservedSystemEntityService.createDefaultDataForApplication()
        systemEntityService.createDefaultSupplierEntityTypeForApp(companyId)
        systemEntityService.createDefaultContentEntityTypeForApp(companyId)
        systemEntityService.createDefaultNoteEntityTypeForApp(companyId)
        systemEntityService.createDefaultUnitForApp(companyId)
        systemEntityService.createDefaultItemCategoryForApp(companyId)
        systemEntityService.createDefaultContentTypeForApp(companyId)
        systemEntityService.createDefaultOwnerTypeForApp(companyId)
        systemEntityService.createDefaultValuationTypeForApp(companyId)
        systemEntityService.createDefaultDataUserEntityTypeForApp(companyId)
        systemEntityService.createDefaultDataGenderForApp(companyId)
        flushSession()
        appUserService.createDefaultDataForApplication(companyId)
        projectService.createDefaultData(companyId)

        roleTypeService.createDefaultDataForApplication()
        roleTypeService.createDefaultDataForDevelopment()
        roleService.createDefaultDataForApplication(companyId)
        roleFeatureMappingService.createRoleFeatureMapForApplication()
        // for development (@todo: temp solution)
        roleService.createDefaultDataForDevelopment(companyId)

        appMailService.createDefaultDataForApplication(companyId)
//        supplierService.createDefaultData()
        countryService.createDefaultData(companyId)
        requestMapService.createRequestMapForApplicationPlugin()
        flushSession()
        requestMapService.appendRoleInRequestMap(PluginConnector.APPLICATION_ID)
        currencyService.createDefaultData(companyId)
        contentCategoryService.createDefaultData(companyId)

        flushSession()  // required to create Item (pull item type from SystemEntity)
        itemTypeService.createDefaultData(companyId)
        itemService.createDefaultData(companyId)
        userRoleService.createDefaultDataForApplication()

        //@todo: should insert default data at budget or procurement default data bootstrap
        appUserEntityService.createDefaultDataForUserProject(companyId)

        themeService.createDefaultData(companyId)
        vehicleService.createDefaultData(companyId)
        designationService.createDefaultData(companyId)
        employeeService.createDefaultData(companyId)

		//bank,bankBranch,district (moved from exchangeHouse)
		bankService.createDefaultData(companyId)
        bankBranchService.createDefaultData(companyId)
        districtService.createDefaultData(companyId)

        appGroupService.createDefaultDataForAppGroup()
        appUserEntityService.createDefaultDataForUserGroup(companyId)
        smsService.createDefaultData(companyId)
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
