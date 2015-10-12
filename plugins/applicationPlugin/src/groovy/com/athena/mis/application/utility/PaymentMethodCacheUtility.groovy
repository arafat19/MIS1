package com.athena.mis.application.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.service.SystemEntityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
/**
 * PaymentMethod used in Purchase Order create
 * SQL: SELECT * FROM system_entity WHERE type = 1;
 * Entity id range: 1 to 50.
 * e.g. Cash = 1; Cheque = 2; LC = 3
 * Reserved Obj: N/A
 **/

@Component('paymentMethodCacheUtility')
class PaymentMethodCacheUtility extends ExtendedCacheUtility {
    @Autowired
    SystemEntityService systemEntityService

    // Sys entity type
    private static final long ENTITY_TYPE = 1;     // entity type payment method

    /**
     * initialised cache utility
     * 1. pull system entity objects by entity type.
     * 2. store all objects into in-memory list.
     */
    public void init() {
        List list = systemEntityService.listByType(ENTITY_TYPE);
        super.setList(list)
    }
}

