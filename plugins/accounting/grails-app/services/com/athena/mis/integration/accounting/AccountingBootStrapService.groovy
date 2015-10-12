package com.athena.mis.integration.accounting

import com.athena.mis.accounting.config.AccSysConfigurationCacheUtility
import com.athena.mis.accounting.utility.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class AccountingBootStrapService {
    @Autowired
    AccSourceCacheUtility accSourceCacheUtility
    @Autowired
    AccTypeCacheUtility accTypeCacheUtility
    @Autowired
    AccGroupCacheUtility accGroupCacheUtility
    @Autowired
    AccCustomGroupCacheUtility accCustomGroupCacheUtility
    @Autowired
    AccChartOfAccountCacheUtility accChartOfAccountCacheUtility
    @Autowired
    AccTier1CacheUtility accTier1CacheUtility
    @Autowired
    AccTier3CacheUtility accTier3CacheUtility
    @Autowired
    AccTier2CacheUtility accTier2CacheUtility
    @Autowired
    AccVoucherTypeCoaCacheUtility accVoucherTypeCoaCacheUtility
    @Autowired
    AccSubAccountCacheUtility accSubAccountCacheUtility
    @Autowired
    AccVoucherTypeCacheUtility accVoucherTypeCacheUtility
    @Autowired
    AccDivisionCacheUtility accDivisionCacheUtility
    @Autowired
    AccFinancialYearCacheUtility accFinancialYearCacheUtility
    @Autowired
    AccSysConfigurationCacheUtility accSysConfigurationCacheUtility
    @Autowired
    AccInstrumentTypeCacheUtility accInstrumentTypeCacheUtility
    @Autowired
    AccLcCacheUtility accLcCacheUtility
    @Autowired
    AccIpcCacheUtility accIpcCacheUtility
    @Autowired
    AccLeaseAccountCacheUtility accLeaseAccountCacheUtility

    public void init () {
        initAllUtility()
    }

    @Transactional(readOnly = true)
    private void initAllUtility() {
        accSourceCacheUtility.init()
        accTypeCacheUtility.init()
        accGroupCacheUtility.init()
        accCustomGroupCacheUtility.init()
        accChartOfAccountCacheUtility.init()
        accTier1CacheUtility.init()
        accTier3CacheUtility.init()
        accTier2CacheUtility.init()
        accVoucherTypeCoaCacheUtility.init()
        accSubAccountCacheUtility.init()
        accVoucherTypeCacheUtility.init()
        accDivisionCacheUtility.init()
        accFinancialYearCacheUtility.init()
        accSysConfigurationCacheUtility.init()
        accInstrumentTypeCacheUtility.init()
        accLcCacheUtility.init()
        accIpcCacheUtility.init()
        accLeaseAccountCacheUtility.init()
    }
}
