package com.athena.mis.projecttrack.controller

import com.athena.mis.projecttrack.actions.ptbacklog.*
import grails.converters.JSON

class PtBacklogController {

    static allowedMethods = [create: "POST", show: "POST", select: "POST", update: "POST", delete: "POST", list: "POST",
            addToMyBacklog: "POST", showMyBacklog: "POST", selectMyBacklog: "POST", listMyBacklog: "POST", updateMyBacklog: "POST",
            removeMyBacklog: "POST", showBackLogForSprint: "POST", createBackLogForSprint: "POST", deleteBackLogForSprint: "POST",
            listBackLogForSprint: "POST", showForActive: "POST", listForActive: "POST", showForInActive: "POST",
            listForInActive: "POST", acceptStory: "POST"
    ]

    ShowPtBacklogActionService showPtBacklogActionService
    ListPtBacklogActionService listPtBacklogActionService
    SelectPtBacklogActionService selectPtBacklogActionService
    SearchPtBacklogActionService searchPtBacklogActionService
    CreatePtBacklogActionService createPtBacklogActionService
    UpdatePtBacklogActionService updatePtBacklogActionService
    DeletePtBacklogActionService deletePtBacklogActionService

    AddMyPtBacklogActionService addMyPtBacklogActionService
    AcceptPtBacklogActionService acceptPtBacklogActionService
    ShowMyPtBacklogActionService showMyPtBacklogActionService
    SelectMyPtBacklogActionService selectMyPtBacklogActionService
    SearchMyPtBacklogActionService searchMyPtBacklogActionService
    RemoveMyPtBacklogActionService removeMyPtBacklogActionService
    ListMyPtBacklogActionService listMyPtBacklogActionService
    UpdateMyPtBacklogActionService updateMyPtBacklogActionService

    ShowBackLogForSprintActionService showBackLogForSprintActionService
    CreateBackLogForSprintActionService createBackLogForSprintActionService
    DeleteBackLogForSprintActionService deleteBackLogForSprintActionService
    ListBackLogForSprintActionService listBackLogForSprintActionService
    SearchBackLogForSprintActionService searchBackLogForSprintActionService

    ListActiveBacklogActionService listActiveBacklogActionService
    SearchActiveBacklogActionService searchActiveBacklogActionService
    ListInActiveBacklogActionService listInActiveBacklogActionService

    def show() {
        Map result

        Map executeResult = (Map) showPtBacklogActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) showPtBacklogActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) showPtBacklogActionService.buildSuccessResultForUI(executeResult)
        }
        render(view: '/projectTrack/ptBacklog/show', model: [output: result as JSON])
    }

    def list() {
        Map result
        Map executeResult
        Boolean isError
        String output

        if (params.query) {
            executeResult = (Map) searchPtBacklogActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchPtBacklogActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) searchPtBacklogActionService.buildSuccessResultForUI(executeResult)
            }
        } else {
            executeResult = (Map) listPtBacklogActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listPtBacklogActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) listPtBacklogActionService.buildSuccessResultForUI(executeResult)
            }
        }
        output = result as JSON
        render output
    }

    def select() {
        Map result
        String output
        Boolean isError

        Map preResult = (Map) selectPtBacklogActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) selectPtBacklogActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) selectPtBacklogActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectPtBacklogActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) selectPtBacklogActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def create() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) createPtBacklogActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createPtBacklogActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) createPtBacklogActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) createPtBacklogActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) createPtBacklogActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def update() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) updatePtBacklogActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updatePtBacklogActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) updatePtBacklogActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updatePtBacklogActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) updatePtBacklogActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def delete() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) deletePtBacklogActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deletePtBacklogActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) deletePtBacklogActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) deletePtBacklogActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) deletePtBacklogActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def addToMyBacklog() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) addMyPtBacklogActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) addMyPtBacklogActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) addMyPtBacklogActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) addMyPtBacklogActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) addMyPtBacklogActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def acceptStory() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) acceptPtBacklogActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) acceptPtBacklogActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) acceptPtBacklogActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) acceptPtBacklogActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) acceptPtBacklogActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def showMyBacklog() {
        Map result

        Map executeResult = (Map) showMyPtBacklogActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) showMyPtBacklogActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) showMyPtBacklogActionService.buildSuccessResultForUI(executeResult)
        }
        render(view: '/projectTrack/ptBacklog/showMyPtBacklog', model: [output: result as JSON])
    }

    def listMyBacklog() {
        Map result
        Map executeResult
        Boolean isError
        String output

        if (params.query) {
            executeResult = (Map) searchMyPtBacklogActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchMyPtBacklogActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) searchMyPtBacklogActionService.buildSuccessResultForUI(executeResult)
            }
        } else {
            executeResult = (Map) listMyPtBacklogActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listMyPtBacklogActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) listMyPtBacklogActionService.buildSuccessResultForUI(executeResult)
            }
        }
        output = result as JSON
        render output
    }

    def selectMyBacklog() {
        Map result
        String output
        Boolean isError

        Map preResult = (Map) selectMyPtBacklogActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) selectMyPtBacklogActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) selectMyPtBacklogActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectMyPtBacklogActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) selectMyPtBacklogActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def updateMyBacklog() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) updateMyPtBacklogActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateMyPtBacklogActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) updateMyPtBacklogActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateMyPtBacklogActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) updateMyPtBacklogActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def removeMyBacklog() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) removeMyPtBacklogActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) removeMyPtBacklogActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) removeMyPtBacklogActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) removeMyPtBacklogActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) removeMyPtBacklogActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     * Show BackLog for Sprint
     */
    def showBackLogForSprint() {
        Map result
        Map preResult
        String output
        if (params.sprintId) {
            preResult = (Map) showBackLogForSprintActionService.executePreCondition(params, null)
            Boolean isError = (Boolean) preResult.isError
            if (isError.booleanValue()) {
                result = (Map) showBackLogForSprintActionService.buildFailureResultForUI(preResult)
            } else {
                result = (Map) showBackLogForSprintActionService.execute(params, preResult);
            }
            output = result as JSON
            render(view: '/projectTrack/ptBacklog/showBackLogForSprint', model: [output: output])

        }

    }
    /**
     * Add BackLog For Sprint
     */
    def createBackLogForSprint() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) createBackLogForSprintActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createBackLogForSprintActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) createBackLogForSprintActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) createBackLogForSprintActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) createBackLogForSprintActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     * Remove backlog for sprint
     */
    def deleteBackLogForSprint() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) deleteBackLogForSprintActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteBackLogForSprintActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) deleteBackLogForSprintActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteBackLogForSprintActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) deleteBackLogForSprintActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     * List and search backlog for sprint
     */
    def listBackLogForSprint() {
        Map result
        Map executeResult
        Boolean isError
        String output

        if (params.query) {
            executeResult = (Map) searchBackLogForSprintActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchBackLogForSprintActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) searchBackLogForSprintActionService.buildSuccessResultForUI(executeResult)
            }
        } else {
            executeResult = (Map) listBackLogForSprintActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listBackLogForSprintActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) listBackLogForSprintActionService.buildSuccessResultForUI(executeResult)
            }
        }
        output = result as JSON
        render output

    }

    /**
     * list backlog by module
     */
    def backlogListForModule() {
        render pt.backlogList(params)
    }

    /**
     * show active backlog
     */
    def showForActive() {
        render(view: '/projectTrack/ptBacklog/showForActive', model: [modelJson: null])
    }

    /**
     * list & search active backlog
     */
    def listForActive() {
        Map result
        Map preResult
        Map executeResult
        Boolean isError
        String output

        if (params.query) {
            preResult = (Map) searchActiveBacklogActionService.executePreCondition(params, null)
            isError = (Boolean) preResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchActiveBacklogActionService.buildFailureResultForUI(preResult)
                output = result as JSON
                render output
                return
            }
            executeResult = (Map) searchActiveBacklogActionService.execute(params, preResult)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchActiveBacklogActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) searchActiveBacklogActionService.buildSuccessResultForUI(executeResult)
            }
        } else {
            preResult = (Map) listActiveBacklogActionService.executePreCondition(params, null)
            isError = (Boolean) preResult.isError
            if (isError.booleanValue()) {
                result = (Map) listActiveBacklogActionService.buildFailureResultForUI(preResult)
                output = result as JSON
                render output
                return
            }
            executeResult = (Map) listActiveBacklogActionService.execute(params, preResult)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listActiveBacklogActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) listActiveBacklogActionService.buildSuccessResultForUI(executeResult)
            }
        }

        output = result as JSON
        render output
    }

    /**
     * show inactive backlog
     */
    def showForInActive() {
        render(view: '/projectTrack/ptBacklog/showForInActive', model: [modelJson: null])
    }

    /**
     * list inactive backlog
     */
    def listForInActive() {
        Map result
        String output

        Map preResult = (Map) listInActiveBacklogActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) listInActiveBacklogActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) listInActiveBacklogActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) listInActiveBacklogActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) listInActiveBacklogActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }
}
