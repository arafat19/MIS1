package com.athena.mis.fixedasset.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.fixedasset.service.FxdMaintenanceTypeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component("fxdMaintenanceTypeCacheUtility")
class FxdMaintenanceTypeCacheUtility extends ExtendedCacheUtility {

    @Autowired
    FxdMaintenanceTypeService fxdMaintenanceTypeService

    public static final String SORT_ON_NAME = "name";
    public static final String SORT_ON_ID = "id";

    public void init() {
        List list = fxdMaintenanceTypeService.list();
        super.setList(list)
    }
}
