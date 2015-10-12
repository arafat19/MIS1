<%@ page import="com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Update Sprint Task
        </div>
    </div>

    <form id="updateTaskForm" name="updateTaskForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <input type="hidden" name="hidSprintId" id="hidSprintId"/>
            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="statusId">Status:</label>

                <div class="col-md-3">
                    <app:dropDownSystemEntity
                            typeId = "${SystemEntityTypeCacheUtility.TYPE_BUDG_TASK_STATUS}"
                            tabindex="1"
                            name="statusId"
                            required="true"
                            validationMessage="Required"
                            dataModelName="dropDownTaskStatus">
                    </app:dropDownSystemEntity>
                </div>

                <div class="col-md-2 pull-left">
                    <span class="k-invalid-msg" data-for="statusId"></span>
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
                    aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>
        </div>
    </form>
</div>