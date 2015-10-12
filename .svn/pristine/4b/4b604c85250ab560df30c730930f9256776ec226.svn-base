import com.athena.mis.application.actions.login.LoginSuccessActionService
import grails.converters.JSON
import grails.util.Environment
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.codehaus.groovy.grails.web.servlet.FlashScope
import org.springframework.security.authentication.AccountExpiredException
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler

class LoginController {

//    static allowedMethods = []

    LoginSuccessActionService loginSuccessActionService

//    def jcaptchaService
    def authenticationTrustResolver
    def springSecurityService
    def jcaptchaService

    /**
     * Default action; redirects to 'defaultTargetUrl' if logged in, /login/auth otherwise.
     */
    def index() {
        if (springSecurityService.isLoggedIn()) {
            redirect uri: SpringSecurityUtils.securityConfig.successHandler.defaultTargetUrl
        } else {
            redirect action: auth, params: params
        }
    }

    def noAccess() {
        render(template: '/login/noAccess')
    }

    /**
     * Show the login page.
     */
    def auth = {
        def config = SpringSecurityUtils.securityConfig
        if (springSecurityService.isLoggedIn()) {
            redirect uri: config.successHandler.defaultTargetUrl
            return
        }

        String view = '/application/login/auth'
        String postUrl = "${request.contextPath}${config.apf.filterProcessesUrl}"
        render view: view, model: [postUrl: postUrl,
                rememberMeParameter: config.rememberMe.parameter]
    }

    def loginSuccess() {
        def config = SpringSecurityUtils.securityConfig
        String view = '/application/login/auth'
        Map executeResult = (LinkedHashMap) loginSuccessActionService.execute(null, request);
        Boolean isExpired = (Boolean) executeResult.isExpired
        if (isExpired.booleanValue()) {
            SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler()
            securityContextLogoutHandler.logout(request, response, null)
            view = '/application/login/showResetPassword'
            render view: view, model: [userInfoMap: executeResult.userInfoMap]
            return
        }
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler()
            securityContextLogoutHandler.logout(request, response, null)
            String postUrl = "${request.contextPath}${config.apf.filterProcessesUrl}"
            flash.message = executeResult.message
            render view: view, model: [postUrl: postUrl, rememberMeParameter: config.rememberMe.parameter]
            return
        }
        Map postResult = (LinkedHashMap) loginSuccessActionService.executePostCondition(session, executeResult);
        isError = (Boolean) postResult.isError
        if (isError.booleanValue()) {
            SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler()
            securityContextLogoutHandler.logout(request, response, null)
            String postUrl = "${request.contextPath}${config.apf.filterProcessesUrl}"
            flash.message = postResult.message
            render view: view, model: [postUrl: postUrl, rememberMeParameter: config.rememberMe.parameter]
            return
        }
        redirect(uri: "/")
    }

    /**
     * Show denied page.
     */
    def denied() {
        if (springSecurityService.isLoggedIn() &&
                authenticationTrustResolver.isRememberMe(SecurityContextHolder.context?.authentication)) {
            // have cookie but the page is guarded with IS_AUTHENTICATED_FULLY
            redirect action: full, params: params
        }
    }

    /**
     * Login page for users with a remember-me cookie but accessing a IS_AUTHENTICATED_FULLY page.
     */
    def full() {
        def config = SpringSecurityUtils.securityConfig
        render view: '/application/login/auth', params: params,
                model: [hasCookie: authenticationTrustResolver.isRememberMe(SecurityContextHolder.context?.authentication),
                        postUrl: "${request.contextPath}${config.apf.filterProcessesUrl}"]
    }

    /**
     * Callback after a failed login. Redirects to the auth page with a warning message.
     */
    def authfail() {

        def username = session[UsernamePasswordAuthenticationFilter.SPRING_SECURITY_LAST_USERNAME_KEY]
        String msg = ''
        def exception = session[AbstractAuthenticationProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY]
        if (exception) {
            if (exception instanceof AccountExpiredException) {
                msg = SpringSecurityUtils.securityConfig.errors.login.expired
            } else if (exception instanceof CredentialsExpiredException) {
                msg = SpringSecurityUtils.securityConfig.errors.login.passwordExpired
            } else if (exception instanceof DisabledException) {
                msg = SpringSecurityUtils.securityConfig.errors.login.disabled
            } else if (exception instanceof LockedException) {
                msg = SpringSecurityUtils.securityConfig.errors.login.locked
            } else {
                msg = SpringSecurityUtils.securityConfig.errors.login.fail
            }
        }

        if (springSecurityService.isAjax(request)) {
            render([error: msg] as JSON)
        } else {
            flash.message = msg
            redirect action: auth, params: params
        }
    }

    /**
     * The Ajax success redirect url.
     */
    def ajaxSuccess() {
        render([success: true, username: springSecurityService.authentication.name] as JSON)
    }

    /**
     * The Ajax denied redirect url.
     */
    def ajaxDenied() {
        render([error: 'access denied'] as JSON)
    }

    def checklogin() {
        def config = SpringSecurityUtils.securityConfig
        if (springSecurityService.isLoggedIn()) {
            redirect uri: config.successHandler.defaultTargetUrl
            return
        }
        String view = '/application/login/auth'
        String postUrl = "${request.contextPath}${config.apf.filterProcessesUrl}"

        // captcha check
        try {
            boolean matched = true      // default value for development mode
            Environment.executeForCurrentEnvironment {
                production {
                    matched = jcaptchaService.validateResponse("image", session.id, params.captcha);
                }
            }
            session.invalidate();
            if (!matched) {
                flash.message = "Security ID wasn't matched."
                render view: view, model: [postUrl: postUrl,
                        rememberMeParameter: config.rememberMe.parameter]

            } else {
                def model = [j_username: params.j_username, j_password: params.j_password]
                forward(view: "/j_spring_security_check", params: model)
            }
        } catch (Exception exp) {
            session.invalidate();
            flash.message = "Security ID wasn't matched."
            render view: view, model: [postUrl: postUrl,
                    rememberMeParameter: config.rememberMe.parameter]
        }
    }
}

