package com.athena.mis.application.controller

import com.athena.mis.application.actions.company.*
import com.athena.mis.application.entity.Company
import com.athena.mis.application.entity.Country
import com.athena.mis.application.utility.CountryCacheUtility
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.utility.UIConstants
import grails.converters.JSON
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class CompanyController {
    static allowedMethods = [create: "POST", show: "POST", select: "POST", update: "POST", delete: "POST", list: "POST"]

    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    CountryCacheUtility countryCacheUtility

    CreateCompanyActionService createCompanyActionService
    DeleteCompanyActionService deleteCompanyActionService
    UpdateCompanyActionService updateCompanyActionService
    ShowCompanyActionService showCompanyActionService
    SelectCompanyActionService selectCompanyActionService
    SearchCompanyActionService searchCompanyActionService
    ListCompanyActionService listCompanyActionService

    def show() {
        Map preResult = (Map) showCompanyActionService.executePreCondition(null, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        Map executeResult = (Map) showCompanyActionService.execute(params, null)
        Map result
        if (executeResult) {
            result = (Map) showCompanyActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) showCompanyActionService.buildFailureResultForUI(executeResult)
        }
        render(view: '/application/company/show', model: [output: result as JSON])
    }

    def select() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        String output
        Boolean isError
        preResult = (LinkedHashMap) selectCompanyActionService.executePreCondition(null, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }

        executeResult = (LinkedHashMap) selectCompanyActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectCompanyActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) selectCompanyActionService.buildSuccessResultForUI(executeResult)
        }
        render result as JSON
    }

    def create() {
        String output
        Company companyInstance = buildCompany(params)
        Map preResult = (Map) createCompanyActionService.executePreCondition(params, companyInstance)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        Map result
        Boolean isValid = (Boolean) preResult
        if (!isValid.booleanValue()) {
            result = (Map) createCompanyActionService.formatValidationErrorsForUI(companyInstance, { g.message(error: it) }, false)
            output = result as JSON
            render output
            return
        }
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createCompanyActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Company savedCompany = (Company) createCompanyActionService.execute(null, preResult)
        if (savedCompany) {
            result = (Map) createCompanyActionService.buildSuccessResultForUI(savedCompany)
        } else {
            result = (Map) createCompanyActionService.buildFailureResultForUI(companyInstance)
        }
        output = result as JSON
        render output
    }

    def update() {
        String output
        Boolean isError

        Map preResult = (Map) updateCompanyActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        Map result
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateCompanyActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        // pre-condition met, so we'll run the execute method of the action
        Map executeResult = (Map) updateCompanyActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateCompanyActionService.buildFailureResultForUI(null)
        } else {
            result = (Map) updateCompanyActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }
    def delete() {
        String output
        Map preResult = (Map) deleteCompanyActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        Map result
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteCompanyActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Boolean deleteResult = ((Boolean) deleteCompanyActionService.execute(params, null))
        if (deleteResult.booleanValue()) {
            result = (Map) deleteCompanyActionService.buildSuccessResultForUI(null)
        } else {
            result = (Map) deleteCompanyActionService.buildFailureResultForUI(null)
        }
        output = result as JSON
        render output
    }

    def list() {
        Map preResult
        Boolean hasAccess
        Map result
        Map executeResult
        String output
        if (params.query) {
            preResult = (Map) searchCompanyActionService.executePreCondition(null, null)
            hasAccess = (Boolean) preResult.hasAccess
            if (!hasAccess.booleanValue()) {
                redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
                return
            }
            executeResult = (Map) searchCompanyActionService.execute(params, null)
            if (executeResult) {
                result = (Map) searchCompanyActionService.buildSuccessResultForUI(executeResult)
            } else {
                result = (Map) searchCompanyActionService.buildFailureResultForUI(null)
            }
        } else {
            preResult = (Map) listCompanyActionService.executePreCondition(null, null)
            hasAccess = (Boolean) preResult.hasAccess
            if (!hasAccess.booleanValue()) {
                redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
                return
            }
            executeResult = (Map) listCompanyActionService.execute(params, null)
            if (executeResult) {
                result = (Map) listCompanyActionService.buildSuccessResultForUI(executeResult)
            } else {
                result = (Map) listCompanyActionService.buildFailureResultForUI(null)
            }
        }
        output = result as JSON
        render output
    }

    private Company buildCompany(GrailsParameterMap params) {
        Company company = new Company(params)
        Country country = (Country) countryCacheUtility.read(company.countryId)
        company.currencyId = country.currencyId
        company.createdBy = appSessionUtil.getAppUser().id
        company.createdOn = new Date()
        return company
    }
}
