<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">

        <div class="panel-title">
            Edit Theme Information
        </div>

    </div>
    <g:form id="themeForm" name="themeForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <g:hiddenField name="id"/>
                <div class="form-group">
                        <label class="col-md-1 control-label label-optional" for="key">Key:</label>
                        <div class="col-md-5"> <span id="key"></span></div>
                        <label class="col-md-1 control-label label-optional" for="description">Description:</label>
                        <div class="col-md-5"> <span id="description"></span></div>
                </div>
                <div class="form-group">
                        <label class="col-md-1 control-label label-required" for="value">Value:</label>
                        <div class="col-md-11">
                            <textarea type="text" class="k-textbox" id="value" name="value" rows="5"
                                      placeholder="Custom CSS/Text Here... .. ." required="" tabindex="1"
                                      validationMessage="Value is Required"></textarea>
                        </div>
                </div>
            </div>
        <div class="panel-footer">
            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="2"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Update
            </button>
            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="3"
                    aria-disabled="false" onclick='resetThemeForm();'><span
                    class="k-icon k-i-close"></span>Cancel
            </button>
        </div>
    </g:form>
</div>

