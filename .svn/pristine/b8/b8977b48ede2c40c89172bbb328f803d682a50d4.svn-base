package com.athena.mis.procurement.controller

import com.athena.mis.procurement.utility.ProcSessionUtil
import com.athena.mis.procurement.actions.indent.*
import com.athena.mis.procurement.entity.ProcIndent
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.converters.JSON
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class ProcIndentController {

    static allowedMethods = [show: "POST", create: "POST", select: "POST",
            update: "POST", delete: "POST", list: "POST", approve: "POST",
            listOfUnApprovedIndent: "POST", approveIndentDashBoard: "POST"
    ]

    @Autowired
    ProcSessionUtil procSessionUtil
    ShowIndentActionService showIndentActionService
    CreateIndentActionService createIndentActionService
    SelectIndentActionService selectIndentActionService
    UpdateIndentActionService updateIndentActionService
    ListIndentActionService listIndentActionService
    SearchIndentActionService searchIndentActionService
    DeleteIndentActionService deleteIndentActionService
    ApproveIndentActionService approveIndentActionService
    ListUnApprovedIndentActionService listUnApprovedIndentActionService
    ApproveIndentForDashBoardActionService approveIndentForDashBoardActionService

    /**
     * Show indent
     */
    def show() {
        Map result
        Map executeResult
        Boolean isError
        executeResult = (Map) showIndentActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) showIndentActionService.buildSuccessResultForUI(executeResult);
        } else {
            result = (Map) showIndentActionService.buildFailureResultForUI(executeResult);
        }
        render(view: '/procurement/procIndent/show', model: [output: result as JSON])
    }

    /**
     * Create indent
     */
    def create() {
        Map preResult
        Map result
        Map executeResult
        Map postResult
        Boolean isError

        ProcIndent indent = buildIndent(params)

        preResult = (Map) createIndentActionService.executePreCondition(params, indent)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createIndentActionService.buildFailureResultForUI(preResult);
            render(result as JSON)
            return
        }
        executeResult = (Map) createIndentActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) createIndentActionService.buildFailureResultForUI(executeResult);
            render(result as JSON)
            return
        }

        postResult = (LinkedHashMap) createIndentActionService.executePostCondition(null, executeResult);
        isError = (Boolean) postResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createIndentActionService.buildSuccessResultForUI(executeResult);
            result.put(Tools.MAIL_SENDING_ERR_MSG, postResult.message)
            render(result as JSON)
            return
        }

        result = (LinkedHashMap) createIndentActionService.buildSuccessResultForUI(executeResult);
        render(result as JSON)
    }

    /**
     * Select indent
     */
    def select() {
        Map executeResult

        Map result
        executeResult = (Map) selectIndentActionService.execute(params, null);
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) selectIndentActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (Map) selectIndentActionService.buildSuccessResultForUI(executeResult);
        }
        render result as JSON
    }

    /**
     * Update indent
     */
    def update() {
        Map preResult
        Map result
        Map executeResult
        Boolean isError

        ProcIndent indent = buildIndent(params)
        indent.updatedOn = new Date();
        indent.updatedBy = procSessionUtil.appSessionUtil.getAppUser().id
        indent.id = Long.parseLong(params.id.toString());
        indent.version = Integer.parseInt(params.version.toString());

        preResult = (Map) updateIndentActionService.executePreCondition(null, indent)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateIndentActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }

        executeResult = (Map) updateIndentActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateIndentActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) updateIndentActionService.buildFailureResultForUI(executeResult)
        }
        render(result as JSON)
    }

    /**
     * List and search indent
     */
    def list() {
        Map preResult;
        Boolean hasAccess;
        Boolean isError
        Map result;
        Map executeResult;
        if (params.query) {
            executeResult = (Map) searchIndentActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (!isError.booleanValue()) {
                result = (Map) searchIndentActionService.buildSuccessResultForUI(executeResult);
            } else {
                result = (Map) searchIndentActionService.buildFailureResultForUI(executeResult);
            }

        } else {
            executeResult = (Map) listIndentActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (!isError.booleanValue()) {
                result = (Map) listIndentActionService.buildSuccessResultForUI(executeResult);
            } else {
                result = (Map) listIndentActionService.buildFailureResultForUI(executeResult);
            }
        }
        render(result as JSON)
    }

    /**
     * Delete indent
     */
    def delete() {
        Map preResult
        Map result
        Map executeResult
        Boolean isError
        preResult = (Map) deleteIndentActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteIndentActionService.buildFailureResultForUI(preResult);
            render(result as JSON)
            return
        }
        executeResult = (Map) deleteIndentActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) deleteIndentActionService.buildSuccessResultForUI(executeResult);
        } else {
            result = (Map) deleteIndentActionService.buildFailureResultForUI(executeResult);
        }
        render(result as JSON)
    }

    /**
     * Approve indent
     */
    def approve() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        preResult = (LinkedHashMap) approveIndentActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) approveIndentActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) approveIndentActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) approveIndentActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) approveIndentActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    /**
     * Build indent object
     * @param params - serialized params from UI
     * @return - indent object
     */
    private ProcIndent buildIndent(GrailsParameterMap params) {
        ProcIndent indent = new ProcIndent(params);

        indent.createdOn = new Date();
        indent.createdBy = procSessionUtil.appSessionUtil.getAppUser().id
        indent.fromDate = DateUtility.parseMaskedFromDate(params.fromDate.toString())
        indent.toDate = DateUtility.parseMaskedToDate(params.toDate.toString())
        indent.companyId = procSessionUtil.appSessionUtil.getCompanyId()
        indent.projectId = Long.parseLong(params.projectId.toString())
        indent.approvedBy = 0L
        indent.totalPrice = 0.0d
        return indent
    }

    /**
     * List of un approved indent
     */
    def listOfUnApprovedIndent() {
        Map result
        Map executeResult = (Map) listUnApprovedIndentActionService.execute(params, null);
        Boolean isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) listUnApprovedIndentActionService.buildSuccessResultForUI(executeResult);
        } else {
            result = (Map) listUnApprovedIndentActionService.buildFailureResultForUI(executeResult);
        }
        String output = result as JSON
        render output
    }

    /**
     * Approved indent for dash board
     */
    def approveIndentDashBoard() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        preResult = (LinkedHashMap) approveIndentForDashBoardActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) approveIndentForDashBoardActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) approveIndentForDashBoardActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) approveIndentForDashBoardActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) approveIndentForDashBoardActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }
}

