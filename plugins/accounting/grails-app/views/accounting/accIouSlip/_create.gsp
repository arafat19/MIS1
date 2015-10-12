<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Create IOU Slip
        </div>
    </div>

    <form id="accIouSlipForm" name="accIouSlipForm" class="form-horizontal form-widgets" role="form">
        <input type="hidden" name="id" id="id"/>
        <input type="hidden" name="version" id="version"/>

        <div class="panel-body">
            <div class="form-group">
                <label class="col-md-1 control-label label-required"
                       for="employeeId">Employee:</label>

                <div class="col-md-3">
                    <app:dropDownEmployee
                            dataModelName="dropDownEmployee"
                            name="employeeId"
                            tabindex="1"
                            required="true"
                            validationMessage="Required">
                    </app:dropDownEmployee>
                </div>

                <label class="col-md-1 control-label label-required"
                       for="projectId">Project:</label>

                <div class="col-md-3">
                    <app:dropDownProject
                            dataModelName="dropDownProject"
                            name="projectId"
                            tabindex="2"
                            required="true"
                            validationMessage="Required"
                            onchange="populateIndentList();">
                    </app:dropDownProject>
                </div>

                <label class="col-md-1 control-label label-required"
                       for="indentId">Indent:</label>

                <div class="col-md-3">
                    <select
                            name="indentId"
                            id="indentId"
                            tabindex="3"
                            required validationMessage="Required">
                    </select>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button"
                    aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>
        </div>
    </form>
</div>