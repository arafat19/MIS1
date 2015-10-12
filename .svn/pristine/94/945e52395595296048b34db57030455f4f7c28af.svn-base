<%@ page import="com.athena.mis.budget.utility.BudgetTaskStatusCacheUtility; com.athena.mis.utility.DateUtility; com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Create Budget Task
        </div>
    </div>

    <form id="budgTaskForm" name="budgTaskForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <input type="hidden" name="id" id="id"/>
            <input type="hidden" name="version" id="version"/>
            <input type="hidden" name="budgetId" id="budgetId"/>
            <app:systemEntityByReserved
                    name="hidBudgTaskStatusDefined"
                    reservedId="${BudgetTaskStatusCacheUtility.DEFINED_RESERVED_ID}"
                    typeId="${SystemEntityTypeCacheUtility.TYPE_BUDG_TASK_STATUS}">
            </app:systemEntityByReserved>

            <div class="form-group">
                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="budgetItemSpan">Budget Item:</label>

                        <div class="col-md-9">
                            <span id="budgetItemSpan"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optinal">Status:</label>
                        <div class="col-md-9"><span id="status"></span></div>

                       %{-- <div class="col-md-6">
                            <app:dropDownSystemEntity
                                    typeId = "${SystemEntityTypeCacheUtility.TYPE_BUDG_TASK_STATUS}"
                                    tabindex="1"
                                    name="statusId"
                                    required="true"
                                    validationMessage="Required"
                                    dataModelName="dropDownTaskStatus">
                            </app:dropDownSystemEntity>
                        </div>--}%

                       %{-- <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="statusId"></span>
                        </div>--}%
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="name">Name:</label>

                        <div class="col-md-6">
                            <input type="text" class="k-textbox" id="name" name="name" tabindex="3"
                                   placeholder="Unique Name" required validationMessage="Required"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="name"></span>
                        </div>
                    </div>
                </div>

                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="startDate">Start Date:</label>

                        <div class="col-md-6">
                            <app:dateControl
                                    name="startDate"
                                    tabindex="3"
                                    required="true"
                                    validationMessage="Required">
                            </app:dateControl>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="startDate"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="endDate">End Date:</label>

                        <div class="col-md-6">
                            <app:dateControl
                                    name="endDate"
                                    tabindex="4"
                                    diffWithCurrent="${DateUtility.DATE_RANGE_SEVEN}"
                                    required="true"
                                    validationMessage="Required">
                            </app:dateControl>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="endDate"></span>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="6"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="7"
                    aria-disabled="false" onclick='resetBudgTaskForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>
        </div>
    </form>
</div>