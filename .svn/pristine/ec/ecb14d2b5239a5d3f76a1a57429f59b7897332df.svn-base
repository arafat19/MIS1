import com.athena.mis.application.service.ApplicationSessionService
import com.athena.mis.application.utility.AppSessionUtil
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.springframework.beans.factory.annotation.Autowired

class LogoutController {

    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    ApplicationSessionService applicationSessionService

    /**
     * Index action. Redirects to the Spring security logout uri.
     */
    def index() {
        applicationSessionService.remove(appSessionUtil.getCustomSessionObj())       // remove customSessionObj from application scoped service
        redirect uri: SpringSecurityUtils.securityConfig.logout.filterProcessesUrl // '/j_spring_security_logout'
    }
}
