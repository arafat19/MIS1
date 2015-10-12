package com.athena.mis.exchangehouse.integration.exchangehouse

import com.athena.mis.application.utility.BankBranchCacheUtility
import com.athena.mis.application.utility.BankCacheUtility
import com.athena.mis.application.utility.DistrictCacheUtility
import com.athena.mis.exchangehouse.config.ExhSysConfigurationCacheUtility
import com.athena.mis.exchangehouse.utility.*
import org.springframework.beans.factory.annotation.Autowired

class ExchangeHouseBootStrapService {

    @Autowired
    ExhCurrencyConversionCacheUtility exhCurrencyConversionCacheUtility

    @Autowired
    BankCacheUtility bankCacheUtility
    @Autowired
    DistrictCacheUtility districtCacheUtility
    @Autowired
    BankBranchCacheUtility bankBranchCacheUtility
    @Autowired
    ExhPaidByCacheUtility exhPaidByCacheUtility
    @Autowired
    ExhRemittancePurposeCacheUtility exhRemittancePurposeCacheUtility
    @Autowired
    ExhPhotoIdTypeCacheUtility exhPhotoIdTypeCacheUtility

    @Autowired
    ExhTaskStatusCacheUtility exhTaskStatusCacheUtility
    @Autowired
    ExhTaskTypeCacheUtility exhTaskTypeCacheUtility
    @Autowired
    ExhPaymentMethodCacheUtility exhPaymentMethodCacheUtility
    @Autowired
    ExhUserAgentCacheUtility exhUserAgentCacheUtility
    @Autowired
    ExhRegularFeeCacheUtility exhRegularFeeCacheUtility
    @Autowired
    ExhAgentCacheUtility exhAgentCacheUtility
    @Autowired
    ExhSysConfigurationCacheUtility exhSysConfigurationCacheUtility
    @Autowired
    ExhPostalCodeCacheUtility exhPostalCodeCacheUtility
    @Autowired
    ExhUserCustomerCacheUtility exhUserCustomerCacheUtility

    def init () {
        initAllUtility()
    }


    def initAllUtility() {
        exhCurrencyConversionCacheUtility.init()
        exhPaidByCacheUtility.init()
        exhPaymentMethodCacheUtility.init()
        exhTaskStatusCacheUtility.init()
        exhTaskTypeCacheUtility.init()
        exhRemittancePurposeCacheUtility.init()
        exhPhotoIdTypeCacheUtility.init()

        exhUserAgentCacheUtility.init()
        exhAgentCacheUtility.init()
        exhRegularFeeCacheUtility.init()
        exhSysConfigurationCacheUtility.init()

        exhPostalCodeCacheUtility.init()
        exhUserCustomerCacheUtility.init()
    }
}
