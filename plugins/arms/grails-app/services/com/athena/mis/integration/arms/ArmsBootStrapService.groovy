package com.athena.mis.integration.arms

import com.athena.mis.arms.utility.*
import org.springframework.beans.factory.annotation.Autowired

class ArmsBootStrapService {

    @Autowired
    RmsProcessTypeCacheUtility rmsProcessTypeCacheUtility
    @Autowired
    RmsInstrumentTypeCacheUtility rmsInstrumentTypeCacheUtility
    @Autowired
    RmsPaymentMethodCacheUtility rmsPaymentMethodCacheUtility
    @Autowired
    RmsTaskStatusCacheUtility rmsTaskStatusCacheUtility
    @Autowired
    RmsExchangeHouseCacheUtility rmsExchangeHouseCacheUtility
    @Autowired
    RmsProcessInstrumentMappingCacheUtility rmsProcessInstrumentMappingCacheUtility
    @Autowired
    UserRmsExchangeHouseCacheUtility userRmsExchangeHouseCacheUtility

    public void init() {
        initAllUtility()
    }

    private void initAllUtility() {
        rmsProcessTypeCacheUtility.init()
        rmsInstrumentTypeCacheUtility.init()
        rmsPaymentMethodCacheUtility.init()
        rmsTaskStatusCacheUtility.init()
        rmsExchangeHouseCacheUtility.init()
        rmsProcessInstrumentMappingCacheUtility.init()
        userRmsExchangeHouseCacheUtility.init()
    }
}
