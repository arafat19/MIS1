package com.athena.mis.application.utility

import com.athena.mis.CacheUtility
import com.athena.mis.application.entity.SystemEntityType
import com.athena.mis.application.service.SystemEntityTypeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component('systemEntityTypeCacheUtility')
class SystemEntityTypeCacheUtility extends CacheUtility {

    @Autowired
    SystemEntityTypeService systemEntityTypeService

    // Sys entity type
    public static final long TYPE_PAYMENT_METHOD = 1;
    public static final long TYPE_ACC_SOURCE = 51;
    public static final long TYPE_ACC_VOUCHER = 101;
    public static final long TYPE_INV_PRODUCTION_LINE_ITEM_TYPE = 151;
    public static final long TYPE_UNIT = 251;
    public static final long TYPE_INV_TRANSACTION_TYPE = 301;
    public static final long TYPE_INV_TRANSACTION_ENTITY_TYPE = 351;
    public static final long TYPE_VALUATION_TYPE = 451;
    public static final long TYPE_INVENTORY_TYPE = 501;
    public static final long TYPE_OWNER_TYPE = 551;
    public static final long TYPE_ACC_INSTRUMENT_TYPE = 601;

    public static final long TYPE_BUDG_TASK_STATUS = 3721


    public static final long TYPE_USER_ENTITY_TYPE = 651;
    public static final long TYPE_EXH_PAID_BY = 2001
    public static final long TYPE_EXH_PAYMENT_METHOD = 2002
    public static final long TYPE_EXH_TASK_STATUS = 2003

    public static final long TYPE_EXH_TASK_TYPE = 2004
    public static final long TYPE_CONTENT_ENTITY = 701
    public static final long TYPE_CONTENT_TYPE = 702
    public static final long TYPE_NOTE_ENTITY = 703
    public static final long TYPE_SUPPLIER_TYPE = 704
    public static final long TYPE_ITEM_CATEGORY = 705

    public static final long PT_SPRINT_STATUS=10708
    public static final long PT_BACKLOG_PRIORITY = 10706
	public static final long PT_BACKLOG_STATUS = 10707
	public static final long PT_ACCEPTANCE_CRITERIA_STATUS = 10711
	public static final long PT_BUG_SEVERITY = 10709
	public static final long PT_BUG_STATUS = 10710
    public static final long PT_BUG_TYPE = 10712
    public static final long PT_ACCEPTANCE_CRITERIA_TYPE = 10718

    public static final long ARMS_PROCESS_TYPE = 11713
    public static final long ARMS_INSTRUMENT_TYPE = 11714
    public static final long ARMS_PAYMENT_METHOD = 11715
    public static final long ARMS_TASK_STATUS = 11716

    public static final long TYPE_GENDER = 1717

    public static final long DOC_DATABASE_VENDOR= 13722
    public static final long DOC_CONTENT_TYPE= 13724

    public static final long SARB_TASK_REVISE_STATUS = 12723

    public final String SORT_ON_NAME = "id";

    /**
     * initialised cache utility
     * 1. pull system entity objects by entity type.
     * 2. store all objects into in-memory list.
     */
    public void init() {
        List list = systemEntityTypeService.list();
        super.setList(list)
    }

    public int countByNameAndIdNotEqual(String name, long id){
        int count = 0;
        List<SystemEntityType> lstSysEntityType = (List<SystemEntityType>) list()
        for (int i = 0; i < lstSysEntityType.size(); i++) {
            if (lstSysEntityType[i].name.equalsIgnoreCase(name) && lstSysEntityType[i].id != id)
                count++
        }
        return count;
    }
}
