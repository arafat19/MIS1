<%@ page import="com.athena.mis.application.entity.Role" %>

<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Create User Role
        </div>
    </div>

    <form id="userRoleForm" name="userRoleForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <input type="hidden" name="existingUserId" id="existingUserId"/>
            <input type="hidden" name="existingRoleId" id="existingRoleId"/>
            <input type="hidden" name="role.id" id="role.id"/>

            <div class="form-group">
                <label class="col-md-1 control-label label-optional">Role:</label>

                <div class="col-md-11">
                    <span id="roleName"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="user.id">User:</label>

                <div class="col-md-3">
                    <select
                            id="user.id"
                            required="required"
                            validationMessage="Required"
                            name="user.id"
                            tabindex="1">
                    </select>
                </div>

                <div class="col-md-2 pull-left">
                    <span class="k-invalid-msg" data-for="user.id"></span>
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
