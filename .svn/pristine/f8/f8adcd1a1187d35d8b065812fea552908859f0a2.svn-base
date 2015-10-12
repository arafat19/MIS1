package com.athena.mis.arms.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.service.SystemEntityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Created by user on 3/19/14.
 */
@Component("rmsPaymentMethodCacheUtility")
class RmsPaymentMethodCacheUtility extends ExtendedCacheUtility {

    @Autowired
    SystemEntityService systemEntityService

    // System entity type
    public static final long ENTITY_TYPE = 11715
    private static final String SORT_ON_NAME = "id"

    // Reserved System Entity id
    public static final long BANK_DEPOSIT_ID = 1160
    public static final long CASH_COLLECTION_ID = 1161

    /**
     * initialised cache utility
     * 1. pull system entity objects by entity type.
     * 2. store all objects into in-memory list.
     */
    public void init() {
        List list = systemEntityService.listByType(ENTITY_TYPE,SORT_ON_NAME)
        super.setList(list)
    }
}
