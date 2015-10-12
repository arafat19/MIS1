package com.athena.mis.integration.document

import com.athena.mis.document.config.DocSysConfigurationCacheUtility
import com.athena.mis.document.utility.DocCategoryCacheUtility
import com.athena.mis.document.utility.DocDBVendorCacheUtility
import com.athena.mis.document.utility.DocDbInstanceCacheUtility
import com.athena.mis.document.utility.DocSubCategoryCacheUtility
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class DocumentBootStrapService {

    @Autowired
    DocSysConfigurationCacheUtility docSysConfigurationCacheUtility
    @Autowired
    DocCategoryCacheUtility docCategoryCacheUtility
    @Autowired
    DocSubCategoryCacheUtility docSubCategoryCacheUtility
    @Autowired
    DocDbInstanceCacheUtility docDbInstanceCacheUtility
    @Autowired
    DocDBVendorCacheUtility docDBVendorCacheUtility


    public void init() {
        initAllUtility()
    }

    @Transactional(readOnly = true)
    private void initAllUtility() {
        docSysConfigurationCacheUtility.init()
        docCategoryCacheUtility.init()
        docSubCategoryCacheUtility.init()
        docDbInstanceCacheUtility.init()
        docDBVendorCacheUtility.init()
    }
}
