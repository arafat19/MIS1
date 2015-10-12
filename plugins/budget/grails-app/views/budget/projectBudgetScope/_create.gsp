<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Search By Project
        </div>
    </div>

    <form id="frmSearchProject" name="frmSearchProject" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <input type="hidden" name="hidProject" id="hidProject"/>

            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="projectId">Project:</label>

                <div class="col-md-5">
                    <app:dropDownProject
                            name="projectId"
                            required="true"
                            validationMessage="Select a project"
                            dataModelName="dropDownProject">
                    </app:dropDownProject>
                </div>

                <div class="col-md-2 pull-left">
                    <span class="k-invalid-msg" data-for="projectId"></span>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="2"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>
        </div>
    </form>
</div>

<div class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Assign Budget Scope to Project
        </div>
    </div>

    <form id="frmSaveBudgetScope" name="frmSaveBudgetScope" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <div class="form-group">
                <div class="col-md-5">
                    <label class="control-label">Available Budget Scope(s):</label>
                </div>

                <div class="col-md-5 col-md-offset-2">
                    <label class="control-label">Assigned Budget Scope(s):</label>
                </div>
            </div>

            <div class="form-group">
                <div class="col-md-5">
                    <select id="availableBudgetScope"
                            name="availableBudgetScope"
                            multiple="true"
                            style="width:100%;"
                            tabindex="1"></select>
                </div>

                <div class="col-md-2">
                    <div class="form-group">
                        <button style="width: 100%" id="addSingle" name="addSingle" data-role="button" class="k-button"
                                onclick='return addDataToSelectedProject();' title="Add Selected Right"
                                role="button" tabindex="7"><span class="k-icon k-i-arrow-e"></span>
                        </button>
                    </div>

                    <div class="form-group">
                        <button style="width: 100%" id="addAll" name="addAll" data-role="button" class="k-button"
                                onclick='return addAllDataToSelectedProject();' title="Add All Rights"
                                role="button" tabindex="7"><span class="k-icon k-i-seek-e"></span>
                        </button>
                    </div>

                    <div class="form-group">
                        <button style="width: 100%" id="removeSingle" name="removeSingle" data-role="button"
                                class="k-button" onclick='return removeDataFromSelectedProject();'
                                title="Remove Selected Right"
                                role="button" tabindex="7"><span class="k-icon k-i-arrow-w"></span>
                        </button>
                    </div>

                    <div class="form-group">
                        <button style="width: 100%" id="removeAll" name="removeAll" data-role="button" class="k-button"
                                onclick='return removeAllDataFromSelectedProject();' title="Remove All Rights"
                                role="button" tabindex="7"><span class="k-icon k-i-seek-w"></span>
                        </button>
                    </div>
                </div>

                <div class="col-md-5">
                    <select id="selectedBudgetScope"
                            name="selectedBudgetScope"
                            multiple="true"
                            style="width:100%;"
                            size="" tabindex="6">
                    </select>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="assign" name="assign" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="7"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Save
            </button>
            <button id="clear" name="clear" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="8" onclick='return discardChanges();'
                    aria-disabled="false"><span class="k-icon k-i-cancel"></span>Discard Changes
            </button>
        </div>
    </form>
</div>