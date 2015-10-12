<%@ page import="com.athena.mis.application.utility.AppUserEntityTypeCacheUtility" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Create User-Agent Mapping
        </div>
    </div>

    <form id="userAgentForm" name='userAgentForm' class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <input type="hidden" name="id" id="id"/>
            <input type="hidden" name="agentId" id="agentId"/>

            <div class="form-group">
                <label class="col-md-1 control-label label-optional">Agent:</label>

                <div class="col-md-11">
                    <span id="agentName"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="userId">User:</label>

                <div class="col-md-3">
                    <app:dropDownAppUserEntity
                            name="userId" id="userId"
                            data_model_name="dropDownAppUser"
                            entity_type_id="${AppUserEntityTypeCacheUtility.AGENT}"
                            entity_id="${agentId}"
                            tabindex="1"
                            required="true"
                            url="${createLink(controller: 'appUserEntity', action: 'dropDownAppUserEntityReload')}">
                            validationMessage="Required">
                    </app:dropDownAppUserEntity>
                </div>

                <div class="col-md-2 pull-left">
                    <span class="k-invalid-msg" data-for="userId"></span>
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
                    aria-disabled="false" onclick='resetUserAgentForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>
        </div>
    </form>
</div>