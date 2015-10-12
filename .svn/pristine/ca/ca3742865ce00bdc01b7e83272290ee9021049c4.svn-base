<%@ page import="com.athena.mis.application.utility.AppUserEntityTypeCacheUtility" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            <span id="lblFormTitle"></span>
        </div>
    </div>

    <form id="appUserEntityForm" name="appUserEntityForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <input type="hidden" id="id" name="id"/>
            <input type="hidden" id="entityId" name="entityId"/>
            <input type="hidden" id="entityTypeId" name="entityTypeId"/>

            <div class="form-group">
                <div class="col-md-1 align_right">
                    <span id="lblEntityTypeName"></span>
                </div>

                <div class="col-md-9">
                    <span id="lblEntityName"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="appUserId">User:</label>

                <div class="col-md-3">
                    <app:dropDownAppUserEntity
                            id="appUserId"
                            data_model_name="dropDownUser"
                            entity_type_id="${reservedId}"
                            name="appUserId"
                            entity_id="${entityId}"
                            tabindex="1"
                            required="required"
                            validationmessage="Required"
                            url="${createLink(controller: 'appUserEntity', action: 'dropDownAppUserEntityReload')}">
                    </app:dropDownAppUserEntity>
                </div>

                <div class="col-md-2 pull-left">
                    <span class="k-invalid-msg" data-for="appUserId"></span>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="3"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="4"
                    aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>
        </div>
    </form>
</div>
