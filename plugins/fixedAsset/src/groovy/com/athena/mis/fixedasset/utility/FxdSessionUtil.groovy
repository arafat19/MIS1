package com.athena.mis.fixedasset.utility

import com.athena.mis.application.utility.AppSessionUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Component

@Component("fxdSessionUtil")
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "session")
class FxdSessionUtil implements Serializable {

    @Autowired
    AppSessionUtil appSessionUtil
}
