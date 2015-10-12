package com.athena.mis.accounting.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.service.SystemEntityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Instrument type used to voucher(Accounting plugin).
 * SQL: SELECT * FROM system_entity WHERE type = 601;
 * Entity id source: reserved_system_entity id of relative type.
 * e.g. instrument_iou_id = 234; instrument_po_id = 235;
 **/
@Component('accInstrumentTypeCacheUtility')
class AccInstrumentTypeCacheUtility extends ExtendedCacheUtility {

    @Autowired
    SystemEntityService systemEntityService

    // Sys entity type
    private static final long ENTITY_TYPE = 601;

    // Entity id get from reserved system entity
    public static final long INSTRUMENT_IOU_ID = 234;
    public static final long INSTRUMENT_PO_ID = 235;

    /**
     * initialised cache utility
     * 1. pull system entity objects by entity type.
     * 2. store all objects into in-memory list.
     */
    public void init() {
        List list = systemEntityService.listByType(ENTITY_TYPE)
        super.setList(list)
    }
}
