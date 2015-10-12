package com.athena.mis.application.taglib

import com.athena.mis.application.actions.taglib.CheckRoleTypeTagLibActionService
import com.athena.mis.utility.Tools
import grails.plugins.springsecurity.SpringSecurityService
import org.codehaus.groovy.grails.plugins.springsecurity.GrailsWebInvocationPrivilegeEvaluator
import org.springframework.beans.factory.annotation.Autowired

/**
 * AccessControlTagLib methods check the user accessibility for given url(s)
 */

class AccessControlTagLib {

    static namespace = "app"

    SpringSecurityService springSecurityService
    CheckRoleTypeTagLibActionService checkRoleTypeTagLibActionService
    @Autowired
    GrailsWebInvocationPrivilegeEvaluator grailsWebInvocationPrivilegeEvaluator

    private static final String METHOD_GET = 'GET'
    private static final String STR_URLS = 'urls'
    private static final String COMMA = ','
    private static final String EMPTY_SPACE = ''
    private static final String SPACE_CHARACTER = "\\s"

    /**
     * Renders the body if any of the the specified urls are granted
     * parameter attr takes the attribute 'urls' as comma separated urls
     * example: <app:ifAnyUrl urls="/controllerName1/action1,/controllerName2/action2"></app:ifAnyUrl>
     *
     * @attr urls REQUIRED the comma separated urls
     */
    def ifAnyUrl = { attrs, body ->

        if (!springSecurityService.isLoggedIn()) {
            out << EMPTY_SPACE
            return
        }
        String strUrls = attrs.remove(STR_URLS)
        strUrls = strUrls.replaceAll(SPACE_CHARACTER, EMPTY_SPACE)

        if (strUrls.size() <= 0) {
            out << EMPTY_SPACE
            return
        }

        List<String> lstUrls = strUrls.split(COMMA);
        for (int i = 0; i < lstUrls.size(); i++) {
            String url = lstUrls[i]
            if (url.length() == 0) {
                continue
            }
            if (hasAccess(url)) {
                out << body()
                break
            }
        }
    }

    /**
     * Renders the body if all of the the specified urls are granted
     * parameter  attr takes the attribute 'urls' as comma separated urls
     * example: <app:ifAllUrl urls="/controllerName1/action1,/controllerName2/action2"></app:ifAllUrl>
     *
     * @attr urls REQUIRED the comma separated urls
     */
    def ifAllUrl = { attrs, body ->
        if (!springSecurityService.isLoggedIn()) {
            out << EMPTY_SPACE
            return
        }
        String strUrls = attrs.remove(STR_URLS)
        strUrls = strUrls.replaceAll(SPACE_CHARACTER, EMPTY_SPACE)

        if (strUrls.size() <= 0) {
            out << EMPTY_SPACE
            return
        }
        boolean allGranted = true
        List<String> lstUrls = strUrls.split(COMMA);
        for (int i = 0; i < lstUrls.size(); i++) {
            String url = lstUrls[i]
            if (url.length() == 0) {
                out << EMPTY_SPACE
                return
            }
            if (!hasAccess(url)) {
                allGranted = false
                break
            }
        }
        if (allGranted) {
            out << body()
        }
    }

    /**
     * Renders the body if logged-in user has role of given type
     * parameter  attr takes the attribute 'id' as roleType.id
     * example: <app:hasRoleType id="${RoleTypeCacheUtility.ROLE_TYPE_DEVELOPMENT_USER}"></app:hasRoleType>
     *
     * @attr id REQUIRED the id of roleType domain
     */

    def hasRoleType = { attrs, body ->
        Boolean hasRoleType = checkRoleTypeTagLibActionService.execute(attrs, null)
        if (hasRoleType.booleanValue()) {
            out << body()
        } else {
            out << Tools.EMPTY_SPACE
        }
    }

    /**
     * Check url access for a given authentication
     * @param url - the url to check access
     * @return - true(if has access), false(otherwise)
     */
    private boolean hasAccess(String url) {
        def auth = springSecurityService.authentication
        String method = METHOD_GET
        return grailsWebInvocationPrivilegeEvaluator.isAllowed(request.contextPath, url, method, auth)
    }

}
