package com.athena.mis.integration.sarb

import com.athena.mis.sarb.config.SarbSysConfigurationCacheUtility
import com.athena.mis.sarb.utility.SarbProvinceCacheUtility
import com.athena.mis.sarb.utility.SarbTaskReviseStatusCacheUtility
import org.springframework.beans.factory.annotation.Autowired

class SarbBootStrapService {

    @Autowired
    SarbProvinceCacheUtility sarbProvinceCacheUtility
    @Autowired
    SarbSysConfigurationCacheUtility sarbSysConfigurationCacheUtility
    @Autowired
    SarbTaskReviseStatusCacheUtility sarbTaskReviseStatusCacheUtility

    def init () {
        initAllUtility()
    }

    def initAllUtility() {
        sarbProvinceCacheUtility.init()
        sarbSysConfigurationCacheUtility.init()
        sarbTaskReviseStatusCacheUtility.init()
    }
}
