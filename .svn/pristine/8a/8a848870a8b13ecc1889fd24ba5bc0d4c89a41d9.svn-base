<%@ page import="com.athena.mis.application.utility.AppUserEntityTypeCacheUtility; com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Create Group
        </div>
    </div>

    <form id="userGroupForm" name="userGroupForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <input type="hidden" name="id" id="id"/>
            <input type="hidden" name="version" id="version"/>

            <app:systemEntityByReserved
                    name="entityTypeId"
                    typeId="${SystemEntityTypeCacheUtility.TYPE_USER_ENTITY_TYPE}"
                    reservedId="${AppUserEntityTypeCacheUtility.GROUP}">
            </app:systemEntityByReserved>

            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="name">Name:</label>

                <div class="col-md-3">
                    <input type="text" class="k-textbox" id="name" name="name" tabindex="1"
                           placeholder="Unique Group Name" required validationMessage="Required"/>
                </div>

                <div class="col-md-2 pull-left">
                    <span class="k-invalid-msg" data-for="name"></span>
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
    </form>
</div>