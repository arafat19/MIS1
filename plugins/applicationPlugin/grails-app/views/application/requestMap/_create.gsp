<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Search By Role
        </div>
    </div>

    <form id="frmSearchRole" name="frmSearchRole" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <g:hiddenField name="hidRole"/>
            <g:hiddenField name="hidPluginId"/>

            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="roleId">Role:</label>

                <div class="col-md-3">
                    <app:dropDownRole
                            dataModelName="dropDownRole"
                            validationMessage="Required"
                            required="true"
                            name="roleId"
                            tabindex="1">
                    </app:dropDownRole>
                </div>

                <div class="col-md-2 pull-left">
                    <span class="k-invalid-msg" data-for="roleId"></span>
                </div>

                <label class="col-md-1 control-label label-required" for="pluginId">Modules:</label>

                <div class="col-md-3">
                    <app:dropDownModules
                            dataModelName="dropDownModule"
                            name="pluginId"
                            required="true"
                            validationMessage="Required"
                            tabindex="2">
                    </app:dropDownModules>
                </div>

                <div class="col-md-2 pull-left">
                    <span class="k-invalid-msg" data-for="pluginId"></span>
                </div>

            </div>
        </div>

        <div class="panel-footer">
            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="3"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>
            <sec:access url="/requestMap/resetRequestMap">
                <button id="reSet" name="reSet" type="submit" data-role="button" class="k-button k-button-icontext"
                        role="button" tabindex="4" onclick='authenticateRequestMapReset();'
                        aria-disabled="false"><span class="k-icon k-i-restore"></span>Reset Module For All Roles
                </button>
            </sec:access>
        </div>
    </form>
</div>

