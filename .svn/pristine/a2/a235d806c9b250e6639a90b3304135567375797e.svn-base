<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Create Division
        </div>
    </div>

    <form id="accDivisionForm" name="accDivisionForm" class="form-horizontal form-widgets" role="form">
        <input type="hidden" name="id" id="id"/>
        <input type="hidden" name="version" id="version"/>

        <div class="panel-body">
            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="projectId">Project:</label>

                <div class="col-md-3">
                    <app:dropDownProject dataModelName="dropDownProject"
                                         name="projectId"
                                         required="true"
                                         validationMessage="Required">
                    </app:dropDownProject>
                </div>

                <div class="col-md-2 pull-left">
                    <span class="k-invalid-msg" data-for="projectId"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="name">Name:</label>

                <div class="col-md-3">
                    <input type="text" class="k-textbox" id="name" name="name"
                           placeholder="Division Name" required validationMessage="Required"/>
                </div>

                <div class="col-md-2 pull-left">
                    <span class="k-invalid-msg" data-for="name"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-1 control-label label-optional"
                       for="isActive">Is Active:</label>

                <div class="col-md-3">
                    <g:checkBox class="form-control-static" name="isActive"/>
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

