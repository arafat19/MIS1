package com.athena.mis.application.controller

import com.athena.mis.application.actions.sysconfiguration.*
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.utility.UIConstants
import grails.converters.JSON
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class SystemConfigurationController {

    static allowedMethods = [
            show: "POST", select: "POST",
            list: "POST", update: "POST"
    ]

    @Autowired
    AppSessionUtil appSessionUtil

    ShowSysConfigurationActionService showSysConfigurationActionService
    SelectSysConfigurationActionService selectSysConfigurationActionService
    UpdateSysConfigurationActionService updateSysConfigurationActionService
    SearchSysConfigurationActionService searchSysConfigurationActionService
    ListSysConfigurationActionService listSysConfigurationActionService

    def show() {
        Map result
        Map executeResult = (Map) showSysConfigurationActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) showSysConfigurationActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) showSysConfigurationActionService.buildSuccessResultForUI(executeResult)
        }
        render(view: '/application/sysConfiguration/show', model: [output: result as JSON])
    }

    def select() {
        Map result
        Map executeResult = (Map) selectSysConfigurationActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectSysConfigurationActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) selectSysConfigurationActionService.buildSuccessResultForUI(executeResult)
        }
        render(result as JSON)
    }

    def update() {
        Map result
        SysConfiguration sysEntity = buildSysConfigurationObject(params)
        Map preResult = (Map) updateSysConfigurationActionService.executePreCondition(params, sysEntity)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }

        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateSysConfigurationActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        Map executeResult = (Map) updateSysConfigurationActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateSysConfigurationActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) updateSysConfigurationActionService.buildSuccessResultForUI(executeResult)
        }
        String output = result as JSON
        render output
    }

    def list() {
        Map result
        Map executeResult
        Boolean isError
        if (params.query) {
            executeResult = (Map) searchSysConfigurationActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchSysConfigurationActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) searchSysConfigurationActionService.buildSuccessResultForUI(executeResult)
            }
        } else {
            executeResult = (Map) listSysConfigurationActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listSysConfigurationActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) listSysConfigurationActionService.buildSuccessResultForUI(executeResult)
            }
        }
        render(result as JSON)
    }

    private SysConfiguration buildSysConfigurationObject(GrailsParameterMap params) {
        SysConfiguration sysCon = new SysConfiguration(params)
        sysCon.id = Integer.parseInt(params.id.toString())
        sysCon.companyId = appSessionUtil.getCompanyId()
        return sysCon
    }
}
