package com.athena.mis.accounting.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.service.SystemEntityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component('accVoucherTypeCacheUtility')
class AccVoucherTypeCacheUtility extends ExtendedCacheUtility {
    @Autowired
    SystemEntityService systemEntityService

    // Sys entity type
    private static final long ENTITY_TYPE = 101;

    // Entity id get from reserved system entity
    public static final long PAYMENT_VOUCHER_BANK_ID = 210;
    public static final long PAYMENT_VOUCHER_CASH_ID = 211;
    public static final long RECEIVED_VOUCHER_BANK_ID = 212;
    public static final long RECEIVED_VOUCHER_CASH_ID = 213;
    public static final long JOURNAL_ID = 214;

    public void init() {
        List list = systemEntityService.listByType(ENTITY_TYPE)
        super.setList(list)
    }
}