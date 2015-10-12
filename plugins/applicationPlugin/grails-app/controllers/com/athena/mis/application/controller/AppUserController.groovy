package com.athena.mis.application.controller

import com.athena.mis.application.actions.appcompanyuser.*
import com.athena.mis.application.actions.appuser.*
import com.athena.mis.application.config.AppSysConfigurationCacheUtility
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.utility.Tools
import com.athena.mis.utility.UIConstants
import grails.converters.JSON
import grails.plugins.springsecurity.SpringSecurityService
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class AppUserController {

    static allowedMethods = [show: "POST", create: "POST", update: "POST", select: "POST",
            delete: "POST", list: "POST", managePassword: "POST", changePassword: "POST", checkPassword: "POST",
            showForCompanyUser: "POST", createForCompanyUser: "POST", updateForCompanyUser: "POST",
            selectForCompanyUser: "POST", deleteForCompanyUser: "POST", listForCompanyUser: "POST",
            showOnlineUser: "POST", listOnlineUser: "POST", forceLogoutOnlineUser: "POST",
            sendPasswordResetLink: "POST", showResetPassword: "GET", resetPassword: "POST", resetExpiredPassword: "POST"
    ];

    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    AppSysConfigurationCacheUtility appSysConfigurationCacheUtility

    CreateAppUserActionService createAppUserActionService
    UpdateAppUserActionService updateAppUserActionService
    SelectAppUserActionService selectAppUserActionService
    ListAppUserActionService listAppUserActionService
    SearchAppUserActionService searchAppUserActionService
    DeleteAppUserActionService deleteAppUserActionService
    ShowAppUserActionService showAppUserActionService
    ManageUserPasswordActionService manageUserPasswordActionService
    ChangeUserPasswordActionService changeUserPasswordActionService
    CheckUserPasswordActionService checkUserPasswordActionService
    SpringSecurityService springSecurityService

    // action services for company user
    CreateAppCompanyUserActionService createAppCompanyUserActionService
    DeleteAppCompanyUserActionService deleteAppCompanyUserActionService
    ListAppCompanyUserActionService listAppCompanyUserActionService
    SearchAppCompanyUserActionService searchAppCompanyUserActionService
    SelectAppCompanyUserActionService selectAppCompanyUserActionService
    ShowAppCompanyUserActionService showAppCompanyUserActionService
    UpdateAppCompanyUserActionService updateAppCompanyUserActionService

    ForceLogoutOnlineUserActionService forceLogoutOnlineUserActionService
    ListOnlineUserActionService listOnlineUserActionService

    // actions for password reset
    SendMailForPasswordResetActionService sendMailForPasswordResetActionService
    ShowForResetPasswordActionService showForResetPasswordActionService
    ResetPasswordActionService resetPasswordActionService
    ResetExpiredPasswordActionService resetExpiredPasswordActionService

    /**
     * show AppUser list
     */
    def show() {
        Map preResult = (Map) showAppUserActionService.executePreCondition(null, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        Map executeResult = (Map) showAppUserActionService.execute(params, null)
        render(view: '/application/appUser/show', model: [output: executeResult as JSON])
    }

    /**
     * Build AppUser object with parameters
     * @param params -serialized parameters from UI
     * @return -object of AppUser
     */
    private AppUser buildAppUser(GrailsParameterMap params) {
        if (params.employeeId.equals(Tools.EMPTY_SPACE)) {
            params.employeeId = 0L
        }
        AppUser appUser = new AppUser(params)
        if ((params.password.length() < 1) && params.existingPass.length() > 1) {  // While updating Password is Blank
            appUser.password = params.existingPass
        } else {
            appUser.password = springSecurityService.encodePassword(params.password)
        }

        if (params.id) {
            appUser.id = Long.parseLong(params.id)
            appUser.version = Integer.parseInt(params.version.toString())
        }

        //  although nextExpireDate is set here, it is only effective while create, this value will not reset in Update
        if (params.companyId) {
            appUser.companyId = Long.parseLong(params.companyId)
            appUser.nextExpireDate = new Date() + 180   // default value for new company user
        } else {
            appUser.companyId = appSessionUtil.getCompanyId()
            SysConfiguration sysConfig = (SysConfiguration) appSysConfigurationCacheUtility.readByKeyAndCompanyId(appSysConfigurationCacheUtility.DEFAULT_PASSWORD_EXPIRE_DURATION)
            if (sysConfig) {
                appUser.nextExpireDate = new Date() + Integer.parseInt(sysConfig.value)
            } else {
                appUser.nextExpireDate = new Date()
            }
        }
        return appUser
    }

    /**
     * create AppUser
     */
    def create() {
        def result
        String output

        AppUser appUser = buildAppUser(params)
        Map preResult = (Map) createAppUserActionService.executePreCondition(params, appUser)
        Boolean hasAccess = ((Boolean) preResult.hasAccess)
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
        }
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            if (preResult.message) {
                result = createAppUserActionService.buildFailureResultForUI(preResult)
                output = result as JSON
                render output
                return
            }
            result = createAppUserActionService.formatValidationErrorsForUI(appUser, { g.message(error: it) }, false)
            output = result as JSON
            render output
            return
        }
        AppUser savedAppUser = (AppUser) createAppUserActionService.execute(null, preResult)
        if (savedAppUser) {
            result = createAppUserActionService.buildSuccessResultForUI(savedAppUser)
        } else {
            result = createAppUserActionService.buildFailureResultForUI(null)
        }
        output = result as JSON
        render output
    }

    /**
     * update AppUser
     */
    def update() {
        def result
        String output

        AppUser appUser = buildAppUser(params)
        Map preResult = (Map) updateAppUserActionService.executePreCondition(params, appUser)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
        }
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            if (preResult.message) {
                result = updateAppUserActionService.buildFailureResultForUI(preResult)
                output = result as JSON
                render output
                return
            }
            result = updateAppUserActionService.formatValidationErrorsForUI(appUser, { g.message(error: it) }, false)
            output = result as JSON
            render output
            return
        }
        AppUser updatedUser = (AppUser) updateAppUserActionService.execute(null, preResult)
        if (updatedUser) {
            result = updateAppUserActionService.buildSuccessResultForUI(updatedUser)
        } else {
            result = updateAppUserActionService.buildFailureResultForUI(null)
        }
        output = result as JSON
        render output
    }

    /**
     * select AppUser
     */
    def select() {
        def result

        Map preResult = (Map) selectAppUserActionService.executePreCondition(null, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
        }
        Map executeResult = (Map) selectAppUserActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = selectAppUserActionService.buildFailureResultForUI(executeResult)
        } else {
            result = selectAppUserActionService.buildSuccessResultForUI(executeResult)
        }
        String output = result as JSON
        render output
    }

    /**
     * delete AppUser
     */
    def delete() {
        Map result
        String output

        Map preResult = (Map) deleteAppUserActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
        }
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteAppUserActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        boolean deleteResult = ((Boolean) deleteAppUserActionService.execute(params, null));
        if (deleteResult.booleanValue()) {
            result = (Map) deleteAppUserActionService.buildSuccessResultForUI(null);
        } else {
            result = (Map) deleteAppUserActionService.buildFailureResultForUI(null);
        }
        output = result as JSON
        render output
    }

    /**
     * list and search AppUser
     */
    def list() {
        Map executeResult
        Map result

        Map preResult = (Map) searchAppUserActionService.executePreCondition(null, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
        }
        // if it's a search request
        if (params.query) {
            executeResult = (Map) searchAppUserActionService.execute(params, null);
            result = (Map) searchAppUserActionService.buildSuccessResultForUI(executeResult);
        } else { // normal listing
            executeResult = (Map) listAppUserActionService.execute(params, null);
            result = (Map) listAppUserActionService.buildSuccessResultForUI(executeResult);
        }
        String output = result as JSON
        render output
    }

    /**
     * show UI for change password
     */
    def managePassword() {

        Map preResult = (Map) manageUserPasswordActionService.execute(null, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        render(view: '/application/appUser/managePassword')
    }

    /**
     * change user password
     */
    def changePassword() {
        Map result
        String output

        Map preResult = (Map) changeUserPasswordActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) changeUserPasswordActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) changeUserPasswordActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) changeUserPasswordActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) changeUserPasswordActionService.buildSuccessResultForUI(null)
        }
        output = result as JSON
        render output
    }

    /**
     * check user password to reset request map
     */
    def checkPassword() {
        Map result
        String output

        Map preResult = (Map) checkUserPasswordActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) checkUserPasswordActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) checkUserPasswordActionService.execute(params, preResult)
        output = executeResult as JSON
        render output
    }

    /**
     * show company user(AppUser) list
     */
    def showForCompanyUser() {
        Map preResult = (Map) showAppCompanyUserActionService.executePreCondition(null, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        Map executeResult = (Map) showAppCompanyUserActionService.execute(params, null)
        render(view: '/application/appCompanyUser/show', model: [output: executeResult as JSON])
    }

    /**
     * create company user(AppUser)
     */
    def createForCompanyUser() {
        def result
        String output

        AppUser appUser = buildAppUser(params)
        Map preResult = (Map) createAppCompanyUserActionService.executePreCondition(params, appUser);
        Boolean hasAccess = ((Boolean) preResult.hasAccess)
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
        }
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            if (preResult.message) {
                result = createAppCompanyUserActionService.buildFailureResultForUI(preResult)
                output = result as JSON
                render output
                return
            }
            result = createAppCompanyUserActionService.formatValidationErrorsForUI(appUser, { g.message(error: it) }, false)
            output = result as JSON
            render output
            return
        }
        AppUser savedAppUser = (AppUser) createAppCompanyUserActionService.execute(null, preResult)
        if (savedAppUser) {
            result = createAppCompanyUserActionService.buildSuccessResultForUI(savedAppUser)
        } else {
            result = createAppCompanyUserActionService.buildFailureResultForUI(null)
        }
        output = result as JSON
        render output
    }

    /**
     * update company user(AppUser)
     */
    def updateForCompanyUser() {
        def result
        String output

        AppUser appUser = buildAppUser(params)
        Map preResult = (Map) updateAppCompanyUserActionService.executePreCondition(params, appUser)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
        }
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            if (preResult.message) {
                result = updateAppCompanyUserActionService.buildFailureResultForUI(preResult)
                output = result as JSON
                render output
                return
            }
            result = updateAppCompanyUserActionService.formatValidationErrorsForUI(appUser, { g.message(error: it) }, false)
            output = result as JSON
            render output
            return
        }
        AppUser updatedUser = (AppUser) updateAppCompanyUserActionService.execute(null, preResult)
        if (updatedUser) {
            result = updateAppCompanyUserActionService.buildSuccessResultForUI(updatedUser)
        } else {
            result = updateAppCompanyUserActionService.buildFailureResultForUI(null)
        }
        output = result as JSON
        render output
    }

    /**
     * select company user(AppUser)
     */
    def selectForCompanyUser() {
        def result

        Map preResult = (Map) selectAppCompanyUserActionService.executePreCondition(null, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
        }
        Map executeResult = (Map) selectAppCompanyUserActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError

        if (isError.booleanValue()) {
            result = selectAppCompanyUserActionService.buildFailureResultForUI(executeResult)
        } else {
            result = selectAppCompanyUserActionService.buildSuccessResultForUI(executeResult)
        }
        String output = result as JSON
        render output
    }

    /**
     * delete company user(AppUser)
     */
    def deleteForCompanyUser() {
        Map result
        String output

        Map preResult = (Map) deleteAppCompanyUserActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
        }
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteAppCompanyUserActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        boolean deleteResult = ((Boolean) deleteAppCompanyUserActionService.execute(params, null))
        if (deleteResult.booleanValue()) {
            result = (Map) deleteAppCompanyUserActionService.buildSuccessResultForUI(null)
        } else {
            result = (Map) deleteAppCompanyUserActionService.buildFailureResultForUI(null)
        }
        output = result as JSON
        render output
    }

    /**
     * list and search company user(AppUser)
     */
    def listForCompanyUser() {
        Map executeResult
        Map result

        Map preResult = (Map) searchAppCompanyUserActionService.executePreCondition(null, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
        }
        // if it's a search request
        if (params.query) {
            executeResult = (Map) searchAppCompanyUserActionService.execute(params, null)
            result = (Map) searchAppCompanyUserActionService.buildSuccessResultForUI(executeResult)
        } else { // normal listing
            executeResult = (Map) listAppCompanyUserActionService.execute(params, null)
            result = (Map) listAppCompanyUserActionService.buildSuccessResultForUI(executeResult)
        }
        String output = result as JSON
        render output
    }

    /**
     * show UI to view list of online user
     */
    def showOnlineUser() {
        render(view: '/application/appUser/showOnlineUser')
    }

    /**
     * show list of online user
     */
    def listOnlineUser() {
        Map result
        String output

        Map executeResult = (Map) listOnlineUserActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) listOnlineUserActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) listOnlineUserActionService.buildFailureResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     * log out online user
     * @return
     */
    def forceLogoutOnlineUser() {
        Map preResult = (Map) forceLogoutOnlineUserActionService.executePreCondition(null, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        Map executeResult = (Map) forceLogoutOnlineUserActionService.execute(params, null)
        String output = executeResult as JSON
        render output
    }

    /**
     * update appUser(generate link etc.) and send mail for password reset
     */
    def sendPasswordResetLink() {
        String view = '/application/login/auth'
        Map preResult = (Map) sendMailForPasswordResetActionService.executePreCondition(params, request)
        Boolean isError = preResult.isError
        if (isError.booleanValue()) {
            flash.success = false
            flash.message = preResult.message
            render view: view
            return
        }
        Map executeResult = (Map) sendMailForPasswordResetActionService.execute(null, preResult)
        isError = executeResult.isError
        flash.success = !isError.booleanValue()
        flash.message = executeResult.message
        render view: view
    }

    /**
     * render the page to reset forgotten password
     */
    def showResetPassword() {
        Map result
        String view = '/application/login/showForgotPassword'
        Map preResult = (Map) showForResetPasswordActionService.executePreCondition(params, request)
        Boolean isError = preResult.isError
        if (isError.booleanValue()) {
            result = (Map) showForResetPasswordActionService.buildFailureResultForUI(preResult)
            flash.success = false
            flash.message = result.message
            render view: view, model: [userInfoMap: result.userInfoMap]
            return
        }
        Map executeResult = (Map) showForResetPasswordActionService.execute(null, preResult)
        isError = executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) showForResetPasswordActionService.buildFailureResultForUI(executeResult)
            flash.success = false
            flash.message = result.message
            render view: view, model: [userInfoMap: result.userInfoMap]
            return
        }
        flash.message = null
        render view: view, model: [userInfoMap: executeResult.userInfoMap]
    }

    /**
     * reset user password
     */
    def resetPassword() {
        Map result
        String view = '/application/login/showForgotPassword'
        Map userInfoMap = [passwordResetLink: params.link, username: params.username]   // store param values to display again
        Map preResult = (Map) resetPasswordActionService.executePreCondition(params, request)
        Boolean isError = preResult.isError
        if (isError.booleanValue()) {
            result = (Map) resetPasswordActionService.buildFailureResultForUI(preResult)
            flash.success = false
            flash.message = result.message
            render view: view, model: [userInfoMap: userInfoMap]
            return
        }
        Map executeResult = (Map) resetPasswordActionService.execute(params, preResult)
        isError = executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) resetPasswordActionService.buildFailureResultForUI(executeResult)
            flash.success = false
            flash.message = result.message
            render view: view, model: [userInfoMap: userInfoMap]
            return
        }
        flash.success = (!isError.booleanValue())
        flash.message = executeResult.message
        render view: view, model: [userInfoMap: userInfoMap]
    }

    /**
     * reset expired user password
     */
    def resetExpiredPassword() {
        Map result
        String view = '/application/login/showResetPassword'
        Map userInfoMap = [userName: params.userName, userId: params.userId, expireDate: params.expireDate]   // store param values to display again
        Map preResult = (Map) resetExpiredPasswordActionService.executePreCondition(params, request)
        Boolean isError = preResult.isError
        if (isError.booleanValue()) {
            result = (Map) resetExpiredPasswordActionService.buildFailureResultForUI(preResult)
            flash.success = false
            flash.message = result.message
            render view: view, model: [userInfoMap: userInfoMap]
            return
        }
        Map executeResult = (Map) resetExpiredPasswordActionService.execute(params, preResult)
        isError = executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) resetExpiredPasswordActionService.buildFailureResultForUI(executeResult)
            flash.success = false
            flash.message = result.message
            render view: view, model: [userInfoMap: userInfoMap]
            return
        }
        flash.success = (!isError.booleanValue())
        flash.message = executeResult.message
        render view:  '/application/login/auth', model: [userInfoMap: userInfoMap]
    }
}
