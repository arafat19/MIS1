package com.athena.mis.accounting.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.service.SystemEntityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component('accSourceCacheUtility')
class AccSourceCacheUtility extends ExtendedCacheUtility {
    @Autowired
    SystemEntityService systemEntityService

    // SystemEntity type (Accounting Source Type)
    private static final long ENTITY_TYPE = 51;

    // Entity id get from reserved system entity
    public static final long ACC_SOURCE_NAME_NONE = 21
    public static final long SOURCE_TYPE_CUSTOMER = 22
    public static final long SOURCE_TYPE_EMPLOYEE = 23
    public static final long SOURCE_TYPE_SUB_ACCOUNT = 24
    public static final long SOURCE_TYPE_SUPPLIER = 25

    public static final long SOURCE_TYPE_ITEM = 26
    public static final long SOURCE_TYPE_LC = 27
    public static final long SOURCE_TYPE_IPC = 28
    public static final long SOURCE_TYPE_LEASE_ACCOUNT = 29

    public void init() {
        List list = systemEntityService.listByType(ENTITY_TYPE)
        super.setList(list)
    }
}
