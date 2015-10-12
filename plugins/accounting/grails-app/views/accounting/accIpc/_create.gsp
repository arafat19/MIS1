<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Create I.P.C.
        </div>
    </div>

    <form id="accIpcForm" name="accIpcForm" class="form-horizontal form-widgets" role="form">
        <input type="hidden" name="id" id="id"/>
        <input type="hidden" name="version" id="version"/>

        <div class="panel-body">
            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="projectId">Project:</label>

                <div class="col-md-3">
                    <app:dropDownProject dataModelName="dropDownProject"
                                         tabindex="1"
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
                <label class="col-md-1 control-label label-required" for="ipcNo">IPC No:</label>

                <div class="col-md-3">
                    <input type="text" class="k-textbox" id="ipcNo" name="ipcNo"
                           placeholder="IPC No" required validationMessage="Required"/>
                </div>

                <div class="col-md-2 pull-left">
                    <span class="k-invalid-msg" data-for="ipcNo"></span>
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
                    aria-disabled="false" onclick='resetAccIpcForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>
        </div>
    </form>
</div>

