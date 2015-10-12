
<div id="application_top_panel" class="panel panel-primary" xmlns="http://www.w3.org/1999/html">
    <div class="panel-heading">
        <div class="panel-title">
            Create System Entity Information
        </div>
    </div>

    <g:form name='systemEntityForm' class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <g:hiddenField name="id"/>
            <g:hiddenField name="systemEntityTypeId"/>
            <div class="form-group">
                <div class="col-md-6">

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="systemEntityTypeName">Entity Type:</label>

                        <div class="col-md-6">
                            <span id="systemEntityTypeName"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="key">Key:</label>

                        <div class="col-md-6">
                            <input type="text" class="k-textbox" id="key" name="key" tabindex="1"
                                      placeholder="Should be Unique" required validationMessage="Required"/>
                        </div>
                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="key"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="value">Value:</label>

                        <div class="col-md-6">
                            <input type="text" class="k-textbox" id="value" name="value"
                                   placeholder="System Entity Value" tabindex="2"/>
                        </div>
                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="value"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="isActive">Active:</label>

                        <div class="col-md-6">
                            <g:checkBox tabindex="3" id="isActive" name="isActive"/>
                        </div>
                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="isActive"></span>
                        </div>
                    </div>

                </div>
            </div>
        </div>

        <div class="panel-footer">

            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext" role="button"
                    aria-disabled="false" tabindex="4"><span class="k-icon k-i-plus"></span>Create
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button" class="k-button k-button-icontext" role="button"
                    aria-disabled="false" onclick='resetSystemEntityForm();' tabindex="5"><span class="k-icon k-i-close"></span>Cancel
            </button>

        </div>
    </g:form>
</div>
