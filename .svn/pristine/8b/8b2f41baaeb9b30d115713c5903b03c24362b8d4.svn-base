<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Create Indent
        </div>
    </div>

    <form id="indentForm" name="indentForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <input type="hidden" name="id" id="id"/>
            <input type="hidden" name="version" id="version"/>

            <div class="form-group">
                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="projectId">Project:</label>

                        <div class="col-md-6">
                            <app:dropDownProject
                                    name="projectId"
                                    validationMessage="Required"
                                    dataModelName="dropDownProject"
                                    required="true"
                                    tabindex="1">
                            </app:dropDownProject>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="projectId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="fromDate">From Date:</label>

                        <div class="col-md-6">
                            <app:dateControl
                                    name="fromDate"
                                    tabindex="2"
                                    required="true"
                                    placeholder="dd/MM/yyyy">
                            </app:dateControl>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="fromDate"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="toDate">To Date:</label>

                        <div class="col-md-6">
                            <app:dateControl
                                    name="toDate"
                                    tabindex="3"
                                    required="true"
                                    placeholder="dd/MM/yyyy">
                            </app:dateControl>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="toDate"></span>
                        </div>
                    </div>
                </div>

                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="comments">Comments:</label>

                        <div class="col-md-9">
                            <textarea type="text" class="k-textbox" id="comments" name="comments" rows="5"
                                      placeholder="255 Char Max"
                                      validationMessage="Short Comments" tabindex="4"></textarea>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <app:ifAllUrl urls="/procIndent/create,/procIndent/update,/procIndent/select">
            <div class="panel-footer">
                <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                        role="button"
                        aria-disabled="false" tabindex="5"><span class="k-icon k-i-plus"></span>Create
                </button>

                <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                        class="k-button k-button-icontext" role="button"
                        aria-disabled="false" onclick='resetIndentForm();' tabindex="6"><span
                        class="k-icon k-i-close"></span>Cancel
                </button>
            </div>
        </app:ifAllUrl>
    </form>
</div>




