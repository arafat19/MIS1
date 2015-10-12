package com.athena.mis.budget.controller

import com.athena.mis.budget.actions.budget.*
import com.athena.mis.utility.UIConstants
import grails.converters.JSON

class BudgBudgetController {

    static allowedMethods = [show: "POST", create: "POST", list: "POST", select: 'POST', update: 'POST',
            getBudgetGridByProject: 'POST', delete: "POST", getBudgetGridByInventory: "POST",getBudgetGridForSprint:"POST",
            getBudgetListForQs: "POST", getBudgetStatusForDashBoard: "POST", renderBudgetMenu: "POST"]

    ShowBudgetActionService showBudgetActionService
    CreateBudgetActionService createBudgetActionService
    ListBudgetActionService listBudgetActionService
    SearchBudgetActionService searchBudgetActionService
    SelectBudgetActionService selectBudgetActionService
    UpdateBudgetActionService updateBudgetActionService
    DeleteBudgetActionService deleteBudgetActionService
    GetBudgetGridListByProjectActionService getBudgetGridListByProjectActionService
    SearchBudgetGridByProjectActionService searchBudgetGridByProjectActionService
    SearchBudgetGridForQsActionService searchBudgetGridForQsActionService
    ListBudgetGridForQsActionService listBudgetGridForQsActionService
    GetBudgetGridListByInventoryActionService getBudgetGridListByInventoryActionService
    SearchBudgetGridByInventoryActionService searchBudgetGridByInventoryActionService
    GetBudgetStatusForDashBoardActionService getBudgetStatusForDashBoardActionService
    GetBudgetGridListForSprintActionService getBudgetGridListForSprintActionService
    SearchBudgetGridListForSprintActionService searchBudgetGridListForSprintActionService

    /**
     * Show Budget
     */
    def show() {
        if (request.method == UIConstants.REQUEST_METHOD_GET) {
            render(view: UIConstants.NON_AJAX_TEMPLATE_URI, model: [page: g.createLink(action: 'show')])
            return;
        }

        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        Boolean isProduction
        String output

        executeResult = (LinkedHashMap) showBudgetActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) showBudgetActionService.buildFailureResultForUI(executeResult);
            isProduction = (Boolean) executeResult.isProduction
            output = result as JSON
            if (isProduction.booleanValue()) {
                render(view: '/budget/budgBudget/showProduction', model: [output: output])
            } else {
                render(view: '/budget/budgBudget/show', model: [output: output])
            }
            return;
        }
        result = (LinkedHashMap) showBudgetActionService.buildSuccessResultForUI(executeResult);
        isProduction = (Boolean) result.isProduction
        output = result as JSON
        if (isProduction.booleanValue()) {
            render(view: '/budget/budgBudget/showProduction', model: [output: output])
        } else {
            render(view: '/budget/budgBudget/show', model: [output: output])
        }
    }

    /**
     * Create Budget
     */
    def create() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) createBudgetActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createBudgetActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) createBudgetActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createBudgetActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) createBudgetActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    /**
     * List and search budget
     */
    def list() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        // if it's a search request
        if (params.query) {
            executeResult = (LinkedHashMap) searchBudgetActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) searchBudgetActionService.buildFailureResultForUI(executeResult);
                output = result as JSON
                render output
                return;
            }

            result = (LinkedHashMap) searchBudgetActionService.buildSuccessResultForUI(executeResult);

        } else { // normal listing

            executeResult = (LinkedHashMap) listBudgetActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) listBudgetActionService.buildFailureResultForUI(executeResult);
                output = result as JSON
                render output
                return;
            }
            result = (LinkedHashMap) listBudgetActionService.buildSuccessResultForUI(executeResult);
        }

        output = result as JSON
        render output
    }

    /**
     * Select a budget
     */
    def select() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) selectBudgetActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectBudgetActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) selectBudgetActionService.buildSuccessResultForUI(preResult);
        output = result as JSON
        render output
    }

    /**
     * Update Budget
     */
    def update() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) updateBudgetActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updateBudgetActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) updateBudgetActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updateBudgetActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) updateBudgetActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    /**
     * Delete Budget
     */
    def delete() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        LinkedHashMap preResult = (LinkedHashMap) deleteBudgetActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) deleteBudgetActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) deleteBudgetActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) deleteBudgetActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) deleteBudgetActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    /**
     * Get Budget by project
     */
    def getBudgetGridByProject() {
        Map executeResult
        Map result
        Boolean isError
        String output
        if (params.query) {
            executeResult = (Map) searchBudgetGridByProjectActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchBudgetGridByProjectActionService.buildFailureResultForUI(executeResult);
            } else {
                result = (Map) searchBudgetGridByProjectActionService.buildSuccessResultForUI(executeResult);
            }
        } else {
            executeResult = (LinkedHashMap) getBudgetGridListByProjectActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) getBudgetGridListByProjectActionService.buildFailureResultForUI(executeResult);
            } else {
                result = (Map) getBudgetGridListByProjectActionService.buildSuccessResultForUI(executeResult);
            }
        }

        output = result as JSON
        render output
    }

    /**
     * Get budget grid to search lineItem (Sprint Budget CRUD)
     */
    def getBudgetGridForSprint() {
        Map executeResult
        Map result
        Boolean isError
        String output
        if (params.query) {
            executeResult = (Map) searchBudgetGridListForSprintActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchBudgetGridListForSprintActionService.buildFailureResultForUI(executeResult);
            } else {
                result = (Map) searchBudgetGridListForSprintActionService.buildSuccessResultForUI(executeResult);
            }
        } else {
            executeResult = (LinkedHashMap) getBudgetGridListForSprintActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) getBudgetGridListForSprintActionService.buildFailureResultForUI(executeResult);
            } else {
                result = (Map) getBudgetGridListForSprintActionService.buildSuccessResultForUI(executeResult);
            }
        }

        output = result as JSON
        render output
    }
    /**
     * Get budget grid to search lineItem (only projects that belongs to user's Inventory)
     */
    def getBudgetGridByInventory() {
        Map executeResult
        Map result
        Boolean isError
        String output
        if (params.query) {
            executeResult = (Map) searchBudgetGridByInventoryActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchBudgetGridByInventoryActionService.buildFailureResultForUI(executeResult);
            } else {
                result = (Map) searchBudgetGridByInventoryActionService.buildSuccessResultForUI(executeResult);
            }
        } else {
            executeResult = (LinkedHashMap) getBudgetGridListByInventoryActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) getBudgetGridListByInventoryActionService.buildFailureResultForUI(executeResult);
            } else {
                result = (Map) getBudgetGridListByInventoryActionService.buildSuccessResultForUI(executeResult);
            }
        }

        output = result as JSON
        render output
    }

    /**
     * Get budget for QS
     */
    def getBudgetListForQs() {
        Map executeResult
        Map result
        Boolean isError
        String output
        if (params.query) {
            executeResult = (Map) searchBudgetGridForQsActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchBudgetGridForQsActionService.buildFailureResultForUI(executeResult);
            } else {
                result = (Map) searchBudgetGridForQsActionService.buildSuccessResultForUI(executeResult);
            }
        } else {
            executeResult = (LinkedHashMap) listBudgetGridForQsActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listBudgetGridForQsActionService.buildFailureResultForUI(executeResult);
            } else {
                result = (Map) listBudgetGridForQsActionService.buildSuccessResultForUI(executeResult);
            }
        }
        output = result as JSON
        render output
    }

    /**
     * Get budget status for dash board
     */
    def getBudgetStatusForDashBoard() {
        Map executeResult
        Map result
        Boolean isError
        String output

        executeResult = (LinkedHashMap) getBudgetStatusForDashBoardActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) getBudgetStatusForDashBoardActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (Map) getBudgetStatusForDashBoardActionService.buildSuccessResultForUI(executeResult);
        }

        output = result as JSON
        render output
    }

    def springSecurityService

    /**
     * Used to render left menu and dash board of budget
     */
    def renderBudgetMenu() {
        if (!springSecurityService.isLoggedIn()) {
            render('false')
            return
        }
        // Now pull data for first tab of Dash board
        Map resultBudgetStatus
        Map executeResult = (Map) getBudgetStatusForDashBoardActionService.execute(params, null);
        Boolean isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            resultBudgetStatus = (Map) getBudgetStatusForDashBoardActionService.buildSuccessResultForUI(executeResult);
        } else {
            resultBudgetStatus = (Map) getBudgetStatusForDashBoardActionService.buildFailureResultForUI(executeResult);
        }
        String outputBudgetStatus = resultBudgetStatus as JSON

        render(contentType: "text/json") {
            lstTemplates = array {
                element([name: 'menu', content: g.render(plugin: 'budgeting',template: '/budget/leftmenuBudget')])
                element([name: 'dashBoard', content: g.render(plugin: 'budgeting',template: '/budget/dashBoardBudget', model: [outputBudgetStatus: outputBudgetStatus])])
            }
        }
    }
}
