package com.athena.mis.integration.procurement

import com.athena.mis.application.utility.PaymentMethodCacheUtility
import org.springframework.beans.factory.annotation.Autowired

class ProcurementBootStrapService {

    @Autowired
    PaymentMethodCacheUtility paymentMethodCacheUtility

    public void init() {
        initAllUtility()
    }

    private void initAllUtility() {
        paymentMethodCacheUtility.init()
    }
}
