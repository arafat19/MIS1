package com.athena.mis.exchangehouse.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.service.SystemEntityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
/**
 *
 * SQL: SELECT * FROM system_entity WHERE type = 2002;
 * Entity id range: 2000101 to 2000201.
 * e.g. bank-deposit = 953; cash-collection = 954; mobile-pay = 955;
 * Reserved Obj: N/A.
 **/
@Component("exhPaymentMethodCacheUtility")
class ExhPaymentMethodCacheUtility extends ExtendedCacheUtility {

    @Autowired
    SystemEntityService systemEntityService

    // Sys entity type
    private static final long ENTITY_TYPE = 2002;
    // Entity id get from reserved system entity
    public static final long PAYMENT_METHOD_BANK_DEPOSIT = 953
    public static final long PAYMENT_METHOD_CASH_COLLECTION = 954

    /**
     * initialised cache utility
     * 1. pull system entity objects by entity type.
     * 2. store all objects into in-memory list.
     */
    public void init() {
        List list = systemEntityService.listByType(ENTITY_TYPE)
        setList(list)
    }
}
