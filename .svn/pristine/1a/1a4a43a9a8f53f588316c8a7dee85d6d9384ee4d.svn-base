package com.athena.mis.sarb.utility

import com.athena.mis.application.utility.AppSessionUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Component

@Component("sarbSessionUtil")
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "session")
class SarbSessionUtil implements Serializable {

    @Autowired
    AppSessionUtil appSessionUtil
}
