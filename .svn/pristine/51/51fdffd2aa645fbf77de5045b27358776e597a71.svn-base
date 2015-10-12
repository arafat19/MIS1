<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">

        <div class="panel-title">
            Create Indent Details
        </div>

    </div>

    <form id="indentDetailsForm" name="indentDetailsForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <input type="hidden" name="id" id="id"/>
            <input type="hidden" name="version" id="version"/>

            <div class="form-group">
                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="lblIndentId">Indent Trace No:</label>

                        <div class="col-md-9">
                            <span id="lblIndentId"></span>
                            <g:hiddenField name="indentId" class="required"/>
                        </div>

                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="lblProjectName">Project Name:</label>

                        <div class="col-md-9">
                            <span id="lblProjectName"></span>
                            <g:hiddenField name="projectId" class="required"/>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="comments">Comments:</label>

                        <div class="col-md-9">
                            <textarea type="text" class="k-textbox" id="comments" name="comments" rows="5"
                                      placeholder="255 Char Max"
                                      validationMessage="Short Comments" tabindex="1"></textarea>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="total">Total Amount:</label>

                        <div class="col-md-9">
                            <span id="total"></span>
                        </div>
                    </div>
                </div>

                <div class="col-md-6">

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="itemDescription">Item:</label>

                        <div class="col-md-9">
                            <textarea type="text" class="k-textbox" id="itemDescription" name="itemDescription" rows="3"
                                      placeholder="255 Char Max" required
                                      validationMessage="Short item description is required" tabindex="2"></textarea>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="total">Unit:</label>

                        <div class="col-md-6">
                            <input type="text" class="k-textbox" tabindex="3"
                                   placeholder="Unit"
                                   id="unit" name="unit" required validationMessage="Required"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="unit"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="quantity">Quantity:</label>

                        <div class="col-md-6">
                            <input type="text"
                                   tabindex="4"
                                   id="quantity"
                                   name="quantity"
                                   required validationMessage="Required"
                                   onchange="calculateTotal();"
                                   placeholder="Quantity"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="quantity"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="rate">Rate:</label>

                        <div class="col-md-6">
                            <input type="text"
                                   tabindex="5"
                                   id="rate"
                                   name="rate"
                                   required validationMessage="Required"
                                   onchange="calculateTotal();"
                                   placeholder="Rate"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="rate"></span>
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
                    aria-disabled="false" onclick='resetFormIndentDetails();'><span
                    class="k-icon k-i-close"></span>Cancel
            </button>

        </div>
    </form>
</div>


