package com.athena.mis.intergation.fixedasset

import com.athena.mis.fixedasset.utility.FxdCategoryMaintenanceTypeCacheUtility
import com.athena.mis.fixedasset.utility.FxdMaintenanceTypeCacheUtility
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class FixedAssetBootStrapService {

    @Autowired
    FxdMaintenanceTypeCacheUtility fxdMaintenanceTypeCacheUtility
    @Autowired
    FxdCategoryMaintenanceTypeCacheUtility fxdCategoryMaintenanceTypeCacheUtility

    public void init() {
        initAllUtility()
    }

    @Transactional(readOnly = true)
    private void initAllUtility() {
        fxdMaintenanceTypeCacheUtility.init()
        fxdCategoryMaintenanceTypeCacheUtility.init()
    }
}
