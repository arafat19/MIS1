package com.athena.mis.integration.accounting

import com.athena.mis.PluginConnector

public abstract class AccountingPluginConnector extends PluginConnector {

    // Return the financial year Object by id
    public abstract Object readFinancialYear(long id)

    // get source type supplier id
    public abstract long getAccSourceTypeSupplier()

    // get source type customer id
    public abstract long getAccSourceTypeCustomer()

    // get source type customer id
    public abstract long getAccSourceTypeItem()

    // get source type customer id
    public abstract long getAccSourceTypeEmployee()

    //get voucher balance by purchase order id
    public abstract double getTotalAmountByPurchaseOrderId(long purchaseOrderId)

    //get current financial year
    public abstract Object getCurrentFinancialYear()

    // get instrument type po
    public abstract long getInstrumentTypePurchaseOrder()

    //init Account SysConfiguration
    public abstract void initAccSysConfiguration()

    // Re-initialize the whole cacheUtility of AccSource (used in create,update,delete)
    public abstract void initAccSourceCacheUtility()

    // Re-initialize the whole cacheUtility of AccVoucherType (used in create,update,delete)
    public abstract void initAccVoucherTypeCacheUtility()

    // Re-initialize the whole cacheUtility of AccInstrumentType (used in create,update,delete)
    public abstract void initAccInstrumentTypeCacheUtility()

    // get list of AccSourceType system entity
    public abstract List<Object> listAccSourceType()

    // get list of AccVoucherType system entity
    public abstract List<Object> listAccVoucherType()

    // get list of AccInstrumentType system entity
    public abstract List<Object> listAccInstrumentType()

    // get reserved system entity of instrument type
    public abstract Object readByReservedInstrumentType(long reservedId, long companyId)

    // get reserved system entity of source type
    public abstract Object readByReservedSourceType(long reservedId, long companyId)

    // get reserved system entity of voucher type
    public abstract Object readByReservedVoucherType(long reservedId, long companyId)

    // update content count for financial year during create, update and delete content
    public abstract Object updateContentCountForFinancialYear(long financialYearId, int count)

    public abstract void bootStrap(boolean isSchema, boolean isData)

    public abstract void createDefaultData(long companyId)

    public abstract void createDefaultDataForAccGroup(long companyId)
}