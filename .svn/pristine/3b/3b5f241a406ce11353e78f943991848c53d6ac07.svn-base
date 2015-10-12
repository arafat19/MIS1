package com.athena.mis.exchangehouse.actions.taglib

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.exchangehouse.config.ExhSysConfigurationCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

class CheckExhSysConfigTagLibActionService extends BaseService implements ActionIntf {

    private static final String KEY = 'key'
    private static final String VALUE = 'value'

    private Logger log = Logger.getLogger(getClass())

    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    ExhSysConfigurationCacheUtility exhSysConfigurationCacheUtility

    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    public Object execute(Object parameters, Object obj) {
        try {
            Map attrs = (Map) parameters
            String key = attrs.get(KEY)
            String value = attrs.get(VALUE)
            if ((!key) || (key.length() == 0) || (!value) || (value.length() == 0)) {
                return Boolean.FALSE
            }
            long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
            SysConfiguration sysConfiguration = (SysConfiguration) exhSysConfigurationCacheUtility.readByKeyAndCompanyId(key, companyId)
            if (!sysConfiguration) {
                return Boolean.FALSE
            }
            if (!sysConfiguration.value.equals(value)) {
                return Boolean.FALSE
            }
            return Boolean.TRUE
        } catch (Exception e) {
            log.error(e.message)
            return Boolean.FALSE
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    public Object buildFailureResultForUI(Object obj) {
        return null
    }
}
