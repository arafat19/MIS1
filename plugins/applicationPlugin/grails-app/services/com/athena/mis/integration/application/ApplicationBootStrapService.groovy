package com.athena.mis.integration.application

import com.athena.mis.application.config.AppSysConfigurationCacheUtility
import com.athena.mis.application.config.ThemeCacheUtility
import com.athena.mis.application.utility.*
import grails.plugins.springsecurity.SpringSecurityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class ApplicationBootStrapService {

    @Autowired
    SpringSecurityService springSecurityService
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    CompanyCacheUtility companyCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    RoleCacheUtility roleCacheUtility
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    CustomerCacheUtility customerCacheUtility
    @Autowired
    EmployeeCacheUtility employeeCacheUtility
    @Autowired
    VehicleCacheUtility vehicleCacheUtility
    @Autowired
    SupplierCacheUtility supplierCacheUtility
    @Autowired
    SupplierItemCacheUtility supplierItemCacheUtility
    @Autowired
    AppGroupCacheUtility appGroupCacheUtility
    @Autowired
    UserGroupCacheUtility userGroupCacheUtility
    @Autowired
    CountryCacheUtility countryCacheUtility
    @Autowired
    SystemEntityTypeCacheUtility systemEntityTypeCacheUtility
    @Autowired
    UserProjectCacheUtility userProjectCacheUtility
    @Autowired
    UserPtProjectCacheUtility userPtProjectCacheUtility
    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility
    @Autowired
    OwnerTypeCacheUtility ownerTypeCacheUtility
    @Autowired
    UnitCacheUtility unitCacheUtility
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility
    @Autowired
    DesignationCacheUtility designationCacheUtility
    @Autowired
    ValuationTypeCacheUtility valuationTypeCacheUtility
    @Autowired
    CurrencyCacheUtility currencyCacheUtility
    @Autowired
    ContentEntityTypeCacheUtility contentEntityTypeCacheUtility
    @Autowired
    NoteEntityTypeCacheUtility noteEntityTypeCacheUtility
    @Autowired
    ThemeCacheUtility themeCacheUtility
    @Autowired
    ContentTypeCacheUtility contentTypeCacheUtility
    @Autowired
    ContentCategoryCacheUtility contentCategoryCacheUtility
    @Autowired
    SupplierTypeCacheUtility supplierTypeCacheUtility
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility
    @Autowired
    ItemCategoryCacheUtility itemCategoryCacheUtility
    @Autowired
    SmsCacheUtility smsCacheUtility
    @Autowired
    AppSysConfigurationCacheUtility appSysConfigurationCacheUtility
    @Autowired
    BankCacheUtility bankCacheUtility
    @Autowired
    BankBranchCacheUtility bankBranchCacheUtility
    @Autowired
    DistrictCacheUtility districtCacheUtility
    @Autowired
    GenderCacheUtility genderCacheUtility
    @Autowired
    UserBankBranchCacheUtility userBankBranchCacheUtility
    @Autowired
    CostingTypeCacheUtility costingTypeCacheUtility
    @Autowired
    CostingDetailsCacheUtility costingDetailsCacheUtility


    public void init() {
        initAllUtility()
    }

    @Transactional(readOnly = true)
    private void initAllUtility() {
        companyCacheUtility.init()
        projectCacheUtility.init()
        appUserCacheUtility.init()
        roleCacheUtility.init()
        roleTypeCacheUtility.init()
        itemCacheUtility.init()
        itemCategoryCacheUtility.init()
        itemTypeCacheUtility.init()
        employeeCacheUtility.init()
        customerCacheUtility.init()
        vehicleCacheUtility.init()
        supplierCacheUtility.init()
        supplierItemCacheUtility.init()
        appGroupCacheUtility.init()
        appUserEntityTypeCacheUtility.init()
        userGroupCacheUtility.init()
        countryCacheUtility.init()
        systemEntityTypeCacheUtility.init()
        userProjectCacheUtility.init()
        userPtProjectCacheUtility.init()
        ownerTypeCacheUtility.init()
        unitCacheUtility.init()
        designationCacheUtility.init()
        valuationTypeCacheUtility.init()
        currencyCacheUtility.init()
        noteEntityTypeCacheUtility.init()
        themeCacheUtility.init()
        contentEntityTypeCacheUtility.init()
        contentTypeCacheUtility.init()
        contentCategoryCacheUtility.init()
        supplierTypeCacheUtility.init()
        smsCacheUtility.init()
        appSysConfigurationCacheUtility.init()
        bankCacheUtility.init()
        districtCacheUtility.init()
        bankBranchCacheUtility.init()
        genderCacheUtility.init()
        userBankBranchCacheUtility.init()
        costingTypeCacheUtility.init()
        costingDetailsCacheUtility.init()
    }
}
