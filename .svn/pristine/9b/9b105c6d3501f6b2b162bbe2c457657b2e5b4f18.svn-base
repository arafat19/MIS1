package com.athena.mis.arms.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.service.SystemEntityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Created by user on 3/16/14.
 */
@Component("rmsInstrumentTypeCacheUtility")
class RmsInstrumentTypeCacheUtility extends ExtendedCacheUtility {

    @Autowired
    SystemEntityService systemEntityService

    // System entity type
    private static final long ENTITY_TYPE = 11714
    private static final String SORT_ON_NAME = "id"

    // Reserved System Entity id
    public static final long PO = 1153
    public static final long EFT = 1154
    public static final long ONLINE = 1155
    public static final long CASH_COLLECTION = 1156
    public static final long TT = 1157
    public static final long MT = 1158

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
