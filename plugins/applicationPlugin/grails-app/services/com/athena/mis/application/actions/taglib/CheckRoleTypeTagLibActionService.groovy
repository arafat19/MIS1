package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.application.utility.AppSessionUtil
import grails.plugins.springsecurity.SpringSecurityService
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

class CheckRoleTypeTagLibActionService extends BaseService implements ActionIntf {

    SpringSecurityService springSecurityService
    @Autowired
    AppSessionUtil appSessionUtil

    private static final String STR_ID = 'id'

    private Logger log = Logger.getLogger(getClass())


    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }


    public Object execute(Object parameters, Object obj) {
        try {
            if (!springSecurityService.isLoggedIn()) {
                return Boolean.FALSE
            }
            Map attrs = (Map) parameters
            String strId = attrs.get(STR_ID)
            if ((!strId) || (strId.length() == 0)) {
                return Boolean.FALSE
            }
            long roleTypeId = Long.parseLong(strId)
            boolean hasRole = appSessionUtil.hasRole(roleTypeId)
            return new Boolean(hasRole)
        } catch (Exception e) {
            log.error(e.message)
            return Boolean.FALSE
        }

    }

    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    public Object buildFailureResultForUI(Object obj) {
        return null
    }

}
