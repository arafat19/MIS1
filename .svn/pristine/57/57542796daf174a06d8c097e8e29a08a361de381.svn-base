package com.athena.mis.arms.controller

import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.arms.actions.rmstask.*
import com.athena.mis.arms.utility.RmsPaymentMethodCacheUtility
import com.athena.mis.arms.utility.RmsSessionUtil
import com.athena.mis.utility.Tools
import grails.converters.JSON
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class RmsTaskController {
    @Autowired
    RmsPaymentMethodCacheUtility rmsPaymentMethodCacheUtility
    @Autowired
    RmsSessionUtil rmsSessionUtil

    static allowedMethods = [
            show: "POST",
            showForExh: "POST",
            create: "POST",
            update: "POST",
            delete: "POST",
            select: "POST",
            list: "POST",
            createForUploadTask: "POST",
            listTaskForTaskList: "POST",
            showForApproveTask: "POST",
            listTaskForApprove: "POST",
            approve: "POST",
            showForMapTask: "POST",
            listTaskForMap: "POST",
            mapTask: "POST",
            reviseTask: "POST",
            showTaskDetailsWithNote: "POST",
            searchTaskDetailsWithNote: "POST",
            createRmsTaskNote: "POST",
            showTaskDetailsForForward: "POST",
            searchTaskDetailsForForward: "POST",
            forwardRmsTask: "POST",
            renderTaskDetails: "POST",
            disburseRmsTask: "POST",
            showDisburseCashCollection: "POST",
            searchDisburseCashCollection: "POST",
            disburseCashCollectionRmsTask: "POST",
            showForUploadTaskForExh: "POST",
            showForManageTask: "POST",
            cancelRmsTask: "POST",
            listForViewNotes: "POST",
            showForViewNotes: "POST",
            reloadTaskDetailsTagLib: "POST"
    ]

    ShowRmsTaskActionService showRmsTaskActionService
    CreateRmsTaskActionService createRmsTaskActionService
    UpdateRmsTaskActionService updateRmsTaskActionService
    DeleteRmsTaskActionService deleteRmsTaskActionService
    SelectRmsTaskActionService selectRmsTaskActionService
    SearchRmsTaskActionService searchRmsTaskActionService
    ListRmsTaskActionService listRmsTaskActionService

    UploadBankDepositTaskActionService uploadBankDepositTaskActionService
    UploadCashCollectionTaskActionService uploadCashCollectionTaskActionService
    ListTaskForCreateTaskListActionService listTaskForCreateTaskListActionService
    ListTaskForMapTaskActionService listTaskForMapTaskActionService
    SearchTaskForMapTaskActionService searchTaskForMapTaskActionService
    MapTaskActionService mapTaskActionService
    ListTaskForApproveTaskActionService listTaskForApproveTaskActionService
    SearchTaskForApproveTaskActionService searchTaskForApproveTaskActionService
    ApproveRmsTaskActionService approveRmsTaskActionService
    ReviseTaskActionService reviseTaskActionService
    CreateRmsTaskNoteActionService createRmsTaskNoteActionService
    ForwardRmsTaskActionService forwardRmsTaskActionService
    DisburseRmsTaskActionService disburseRmsTaskActionService
    SearchDisburseCashCollectionTaskActionService searchDisburseCashCollectionTaskActionService
    DisburseCashCollectionRmsTaskActionService disburseCashCollectionRmsTaskActionService
    SendRmsTaskToBankActionService sendRmsTaskToBankActionService
    CancelRmsTaskActionService cancelRmsTaskActionService
    ListViewNotesOfRmsTaskActionService listViewNotesOfRmsTaskActionService
    SearchTaskDetailsForForwardActionService searchTaskDetailsForForwardActionService
    SearchTaskDetailsWithNoteActionService searchTaskDetailsWithNoteActionService
    ShowForMapTaskActionService showForMapTaskActionService

    /**
     * Show Task list
     */
    def show() {
        Map result
        Map executeResult
        Boolean isError

        executeResult = (Map) showRmsTaskActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) showRmsTaskActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) showRmsTaskActionService.buildSuccessResultForUI(executeResult)
        }
        render(view: '/arms/rmsTask/show', model: [output: result as JSON])
    }

    def showForExh() {
        Map result
        Map executeResult
        Boolean isError
        params.put('isExhUser', true)
        executeResult = (Map) showRmsTaskActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) showRmsTaskActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) showRmsTaskActionService.buildSuccessResultForUI(executeResult)
        }
        render(view: '/arms/rmsTask/showForExh', model: [output: result as JSON])
    }

    /**
     * Create Task object
     */
    def create() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output

        preResult = (Map) createRmsTaskActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createRmsTaskActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) createRmsTaskActionService.execute(null, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) createRmsTaskActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) createRmsTaskActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     * Update Task object
     */
    def update() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output

        preResult = (Map) updateRmsTaskActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateRmsTaskActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) updateRmsTaskActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateRmsTaskActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) updateRmsTaskActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     * Delete Task object
     */
    def delete() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output

        preResult = (Map) deleteRmsTaskActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteRmsTaskActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) deleteRmsTaskActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteRmsTaskActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) deleteRmsTaskActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     * Select Task
     */
    def select() {
        Map executeResult
        Map result
        Boolean isError
        String output

        executeResult = (Map) selectRmsTaskActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectRmsTaskActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) selectRmsTaskActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     * Get list and search Task
     */
    def list() {
        Map result
        Map executeResult
        Boolean isError
        String output

        if (params.query) {
            executeResult = (Map) searchRmsTaskActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchRmsTaskActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) searchRmsTaskActionService.buildSuccessResultForUI(executeResult)
            }
        } else {
            executeResult = (Map) listRmsTaskActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listRmsTaskActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) listRmsTaskActionService.buildSuccessResultForUI(executeResult)
            }
        }
        output = result as JSON
        render output
    }

    def sendRmsTaskToBank() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output

        preResult = (Map) sendRmsTaskToBankActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) sendRmsTaskToBankActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) sendRmsTaskToBankActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) sendRmsTaskToBankActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) sendRmsTaskToBankActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def showForUploadTask() {

        render(view: '/arms/rmsTask/showForUploadTask')
    }

    def showForUploadTaskForExh() {

        render(view: '/arms/rmsTask/showForUploadTaskForExh')
    }

    def createForUploadTask() {
        Map result
        Boolean isError

        long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity bankDepositObj = (SystemEntity) rmsPaymentMethodCacheUtility.readByReservedAndCompany(rmsPaymentMethodCacheUtility.BANK_DEPOSIT_ID, companyId)
        SystemEntity cashCollectionObj = (SystemEntity) rmsPaymentMethodCacheUtility.readByReservedAndCompany(rmsPaymentMethodCacheUtility.CASH_COLLECTION_ID, companyId)
        long bankDepositId = bankDepositObj.id
        long cashCollectionId = cashCollectionObj.id

        long paymentMethod = Tools.parseLongInput(params.paymentMethod)

        if (paymentMethod == bankDepositId) {
            Map executePreResult = (Map) uploadBankDepositTaskActionService.executePreCondition(params, request)
            isError = executePreResult.isError
            if (isError.booleanValue()) {
                result = (Map) uploadBankDepositTaskActionService.buildFailureResultForUI(executePreResult)
            } else {
                Map executeResult = (Map) uploadBankDepositTaskActionService.execute(null, executePreResult)
                isError = executeResult.isError
                if (isError.booleanValue()) {
                    result = (Map) uploadBankDepositTaskActionService.buildFailureResultForUI(executeResult)
                } else {
                    result = executeResult
                }
            }
            String output = result as JSON
            render output
        } else if (paymentMethod == cashCollectionId) {
            Map executePreResult = (Map) uploadCashCollectionTaskActionService.executePreCondition(params, request)
            isError = executePreResult.isError
            if (isError.booleanValue()) {
                result = (Map) uploadCashCollectionTaskActionService.buildFailureResultForUI(executePreResult)
            } else {
                Map executeResult = (Map) uploadCashCollectionTaskActionService.execute(null, executePreResult)
                isError = executeResult.isError
                if (isError.booleanValue()) {
                    result = (Map) uploadCashCollectionTaskActionService.buildFailureResultForUI(executeResult)
                } else {
                    result = executeResult
                }
            }
            String output = result as JSON
            render output
        } else {
            result = new LinkedHashMap()
            isError = Boolean.FALSE;
            result.put(Tools.IS_ERROR, isError)
            result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
            String output = result as JSON
            render output
        }
    }

    /**
     * Get list of task to create TaskList
     */
    def listTaskForTaskList() {
        Boolean isError
        Map result
        Map executeResult

        executeResult = (Map) listTaskForCreateTaskListActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) listTaskForCreateTaskListActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) listTaskForCreateTaskListActionService.buildSuccessResultForUI(executeResult)
        }
        String output = result as JSON
        render output
    }

    /**
     * Show map task page
     */
    def showForMapTask() {
        Map result = (Map) showForMapTaskActionService.execute(null, null)
        render(view: '/arms/rmsTask/showForMapTask', model: [modelJson: result as JSON])
    }

    /**
     * Get list of task for map task
     */
    def listTaskForMap() {
        Boolean isError
        Map result
        Map executeResult

        if (params.query) {
            executeResult = (Map) searchTaskForMapTaskActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchTaskForMapTaskActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) searchTaskForMapTaskActionService.buildSuccessResultForUI(executeResult)
            }
        } else {
            executeResult = (Map) listTaskForMapTaskActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listTaskForMapTaskActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) listTaskForMapTaskActionService.buildSuccessResultForUI(executeResult)
            }
        }
        String output = result as JSON
        render output
    }

    /**
     * Map task
     */
    def mapTask() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output

        preResult = (Map) mapTaskActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) mapTaskActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) mapTaskActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) mapTaskActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) mapTaskActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     * Show approve task page
     */
    def showForApproveTask() {
        render(view: '/arms/rmsTask/showForApproveTask', model: [modelJson: null])
    }

    /**
     * Get list of task for approve task
     */
    def listTaskForApprove() {
        Boolean isError
        Map result
        Map executeResult

        if (params.query) {
            executeResult = (Map) searchTaskForApproveTaskActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchTaskForApproveTaskActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) searchTaskForApproveTaskActionService.buildSuccessResultForUI(executeResult)
            }
        } else {
            executeResult = (Map) listTaskForApproveTaskActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listTaskForApproveTaskActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) listTaskForApproveTaskActionService.buildSuccessResultForUI(executeResult)
            }
        }
        String output = result as JSON
        render output
    }

    /**
     * Approve task
     */
    def approve() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output

        preResult = (Map) approveRmsTaskActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) approveRmsTaskActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) approveRmsTaskActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) approveRmsTaskActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) approveRmsTaskActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     * Revise task
     */
    def reviseTask() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output

        preResult = (Map) reviseTaskActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) reviseTaskActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) reviseTaskActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) reviseTaskActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) reviseTaskActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     * Show task details page
     */
    def showTaskDetailsWithNote() {
        GrailsParameterMap parameterMap = (GrailsParameterMap) params
        if (parameterMap.property_name && parameterMap.property_value) {
            Map result = (Map) searchTaskDetailsWithNoteActionService.execute(params, null)
            render(template: '/arms/rmsTask/taskDetailsWithNote', model: [model: result])
        } else {
            render(view: '/arms/report/taskDetails/showTaskDetailsWithNote')
        }
    }
    /**
     * Search task details
     */
    def searchTaskDetailsWithNote() {
        Map result = (Map) searchTaskDetailsWithNoteActionService.execute(params, null)
        render(template: '/arms/rmsTask/taskDetailsWithNote', model: [model: result])
    }

    def createRmsTaskNote() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) createRmsTaskNoteActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createRmsTaskNoteActionService.buildFailureResultForUI(preResult)
        } else {
            Map executeResult = (Map) createRmsTaskNoteActionService.execute(null, preResult)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) createRmsTaskNoteActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (LinkedHashMap) createRmsTaskNoteActionService.buildSuccessResultForUI(null)
            }
        }
        output = result as JSON
        render output
    }

    def showTaskDetailsForForward() {
        render(view: '/arms/rmsTask/showForForwardTask', model: [model: null])
    }

    def searchTaskDetailsForForward() {
        Map result
        Boolean isError
        Map preResult = (Map) searchTaskDetailsForForwardActionService.executePreCondition(params, null)
        isError = preResult.isError
        if (isError.booleanValue()) {
            result = (Map) searchTaskDetailsForForwardActionService.buildFailureResultForUI(preResult)
        } else {
            Map executeResult = (Map) searchTaskDetailsForForwardActionService.execute(null, preResult)
            isError = executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchTaskDetailsForForwardActionService.buildFailureResultForUI(executeResult)
            } else {
                result = executeResult
            }
        }
        render(template: '/arms/rmsTask/taskDetailsWithForwardPanel', model: [model: result])
    }

    def forwardRmsTask() {
        Map result
        Boolean isError

        Map preResult = (Map) forwardRmsTaskActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) forwardRmsTaskActionService.buildFailureResultForUI(preResult)
        } else {
            Map executeResult = (Map) forwardRmsTaskActionService.execute(preResult, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) forwardRmsTaskActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (LinkedHashMap) forwardRmsTaskActionService.buildSuccessResultForUI(null)
            }
        }
        render result as JSON
    }

    def renderTaskDetails() {
        GrailsParameterMap parameterMap = (GrailsParameterMap) params
        render rms.taskDetails(parameterMap, null)
    }

    def disburseRmsTask() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output
        Boolean hasAccess
        preResult = (Map) disburseRmsTaskActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) disburseRmsTaskActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) disburseRmsTaskActionService.execute(preResult, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) disburseRmsTaskActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) disburseRmsTaskActionService.executePostCondition(null, executeResult)
        }
        output = result as JSON
        render output
    }

    def showDisburseCashCollection() {
        render(view: '/arms/rmsTask/showForDisburseCashCollection')
    }

    def searchDisburseCashCollection() {
        Map result
        Boolean isError
        Map preResult = (Map) searchDisburseCashCollectionTaskActionService.executePreCondition(params, null)
        isError = preResult.isError
        if (isError.booleanValue()) {
            result = (Map) searchDisburseCashCollectionTaskActionService.buildFailureResultForUI(preResult)
        } else {
            Map executeResult = (Map) searchDisburseCashCollectionTaskActionService.execute(null, preResult)
            isError = executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchDisburseCashCollectionTaskActionService.buildFailureResultForUI(executeResult)
            } else {
                result = executeResult
            }
        }
        render(template: '/arms/rmsTask/taskDetailsWithDisbursePanel', model: [model: result])
    }

    def disburseCashCollectionRmsTask() {
        Map result
        Boolean isError
        Map preResult = (Map) disburseCashCollectionRmsTaskActionService.executePreCondition(params, null)
        isError = preResult.isError
        if (isError.booleanValue()) {
            result = (Map) disburseCashCollectionRmsTaskActionService.buildFailureResultForUI(preResult)
        } else {
            Map executeResult = (Map) disburseCashCollectionRmsTaskActionService.execute(null, preResult)
            isError = executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) disburseCashCollectionRmsTaskActionService.buildFailureResultForUI(executeResult)
            } else {
                result = disburseCashCollectionRmsTaskActionService.executePostCondition(null, executeResult)
            }
        }
        String output = result as JSON
        render output
    }

    def showForManageTask() {
        render(view: '/arms/rmsTask/showForManageTask', model: [model: null])
    }

    def cancelRmsTask() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output
        preResult = (Map) cancelRmsTaskActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) cancelRmsTaskActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) cancelRmsTaskActionService.execute(preResult, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) cancelRmsTaskActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) cancelRmsTaskActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def showForViewNotes() {
        render(view: '/arms/rmsTask/showForViewNotes')
    }

    def listForViewNotes() {
        Map executeResult
        Boolean isError
        Map result
        String output
        executeResult = (Map) listViewNotesOfRmsTaskActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) listViewNotesOfRmsTaskActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) listViewNotesOfRmsTaskActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def reloadTaskDetailsTagLib() {
        render rms.taskDetails(params, null)
    }
}
