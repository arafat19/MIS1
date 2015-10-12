
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">

        <div class="panel-title">
            Create Role
        </div>

    </div>
    <g:form id="roleForm" name="roleForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <g:hiddenField name="id"/>
            <g:hiddenField name="version"/>
            <g:hiddenField name="hidPreviousRole"/>
            <div class="form-group">
                <div class="col-md-12">
                    <div class="form-group">
                        <div class="col-md-6">
                            <label class="col-md-3 control-label label-required" for="name">Name:</label>

                            <div class="col-md-6">
                                <input type="text" class="k-textbox" id="name" name="name" tabindex="1"
                                       placeholder="Unique Role Name" required validationMessage="Required"
                                       autofocus/>
                            </div>

                            <div class="col-md-3 pull-left">
                                <span class="k-invalid-msg" data-for="name"></span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

        </div>

        <div class="panel-footer">

            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="2"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="3"
                    aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>

        </div>
    </g:form>
</div>