package com.athena.mis.integration.accounting

import com.athena.mis.accounting.actions.accvoucher.GetTotalVoucherAmountByPOIdActionService
import com.athena.mis.accounting.config.AccSysConfigurationCacheUtility
import com.athena.mis.accounting.entity.AccFinancialYear
import com.athena.mis.accounting.service.AccGroupService
import com.athena.mis.accounting.service.AccTypeService
import com.athena.mis.accounting.utility.*
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.integration.accounting.actions.ReadFinancialYearImplActionService
import com.athena.mis.integration.accounting.actions.UpdateContentCountForFinancialYearImplActionService
import org.springframework.beans.factory.annotation.Autowired

class AccountingImplService extends AccountingPluginConnector {

    static transactional = false
    static lazyInit = false
    // registering plugin in servletContext
    @Override
    public boolean initialize() {
        setPlugin(ACCOUNTING, this);
        return true
    }

    @Override
    public String getName() {
        return ACCOUNTING;
    }

    @Override
    public int getId() {
        return ACCOUNTING_ID;
    }

    AccDefaultDataBootStrapService accDefaultDataBootStrapService
    AccSchemaUpdateBootStrapService accSchemaUpdateBootStrapService
    AccountingBootStrapService accountingBootStrapService
    AccTypeService accTypeService
    AccGroupService accGroupService
    ReadFinancialYearImplActionService readFinancialYearImplActionService
    UpdateContentCountForFinancialYearImplActionService updateContentCountForFinancialYearImplActionService
    GetTotalVoucherAmountByPOIdActionService getTotalVoucherAmountByPOIdActionService
    @Autowired
    AccSourceCacheUtility accSourceCacheUtility
    @Autowired
    AccFinancialYearCacheUtility accFinancialYearCacheUtility
    @Autowired
    AccInstrumentTypeCacheUtility accInstrumentTypeCacheUtility
    @Autowired
    AccSysConfigurationCacheUtility accSysConfigurationCacheUtility
    @Autowired
    AccVoucherTypeCacheUtility accVoucherTypeCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil


    // Return the financial year Object by id
    Object readFinancialYear(long id) {
        return readFinancialYearImplActionService.execute(id,null)
    }

    // get source type supplier id
    long getAccSourceTypeSupplier() {
        long companyId = accSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity accSourceTypeSupplier = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_SUPPLIER, companyId)
        return accSourceTypeSupplier.id
    }

    // get source type customer id
    long getAccSourceTypeCustomer() {
        long companyId = accSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity accSourceTypeCustomer = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_CUSTOMER, companyId)
        return accSourceTypeCustomer.id
    }

    // get source type customer id
    long getAccSourceTypeEmployee() {
        long companyId = accSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity accSourceTypeEmployee = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_EMPLOYEE, companyId)
        return accSourceTypeEmployee.id
    }

    // get source type customer id
    long getAccSourceTypeItem() {
        long companyId = accSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity accSourceTypeItem = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_ITEM, companyId)
        return accSourceTypeItem.id
    }

    //get voucher balance by purchase order id
    double getTotalAmountByPurchaseOrderId(long purchaseOrderId) {
        return (double) getTotalVoucherAmountByPOIdActionService.execute(purchaseOrderId, null)
    }

    //get current financial year
    Object getCurrentFinancialYear() {
        AccFinancialYear currentFinancialYear = accFinancialYearCacheUtility.getCurrentFinancialYear()
        return currentFinancialYear
    }

    // get instrument type po
    long getInstrumentTypePurchaseOrder() {
        // pull system entity object
        long companyId = accSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity instrumentIouObj = (SystemEntity) accInstrumentTypeCacheUtility.readByReservedAndCompany(accInstrumentTypeCacheUtility.INSTRUMENT_PO_ID, companyId)

        return instrumentIouObj.id
    }

    //init Account SysConfiguration
    void initAccSysConfiguration() {
        accSysConfigurationCacheUtility.init()
    }

    // Re-initialize the whole cacheUtility of AccSource (used in create,update,delete)
    public void initAccSourceCacheUtility() {
        accSourceCacheUtility.init()
    }

    // Re-initialize the whole cacheUtility of AccVoucherType (used in create,update,delete)
    public void initAccVoucherTypeCacheUtility() {
        accVoucherTypeCacheUtility.init()
    }

    // Re-initialize the whole cacheUtility of AccInstrumentType (used in create,update,delete)
    public void initAccInstrumentTypeCacheUtility() {
        accInstrumentTypeCacheUtility.init()
    }

    // get list of AccSourceType system entity
    public List<Object> listAccSourceType() {
        return accSourceCacheUtility.listByIsActive()
    }

    // get list of AccVoucherType system entity
    public List<Object> listAccVoucherType() {
        return accVoucherTypeCacheUtility.listByIsActive()
    }

    // get list of AccInstrumentType system entity
    public List<Object> listAccInstrumentType() {
        return accInstrumentTypeCacheUtility.listByIsActive()
    }

    // get reserved system entity of instrument type
    public Object readByReservedInstrumentType(long reservedId, long companyId) {
        return accInstrumentTypeCacheUtility.readByReservedAndCompany(reservedId, companyId)
    }

    // get reserved system entity of source type
    public Object readByReservedSourceType(long reservedId, long companyId) {
        return accSourceCacheUtility.readByReservedAndCompany(reservedId, companyId)
    }

    // get reserved system entity of voucher type
    public Object readByReservedVoucherType(long reservedId, long companyId) {
        return accVoucherTypeCacheUtility.readByReservedAndCompany(reservedId, companyId)
    }

    // update content count for financial year during create, update and delete content
    Object updateContentCountForFinancialYear(long financialYearId, int count){
        Integer updateCount = (Integer) updateContentCountForFinancialYearImplActionService.execute(financialYearId, count)
        return updateCount
    }

    public void bootStrap(boolean isSchema, boolean isData) {
        if (isData) accDefaultDataBootStrapService.init()
        if (isSchema) accSchemaUpdateBootStrapService.init()
        accountingBootStrapService.init()
    }

    public void createDefaultData(long companyId) {
        accTypeService.createDefaultData(companyId)
    }

    public void createDefaultDataForAccGroup(long companyId) {
        accGroupService.createDefaultData(companyId)
    }
}
