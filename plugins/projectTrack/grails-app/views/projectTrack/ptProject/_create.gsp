<%@ page import="com.athena.mis.application.utility.SystemEntityTypeCacheUtility; com.athena.mis.application.utility.AppUserEntityTypeCacheUtility" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Create Project
        </div>
    </div>

    <form id='ptProjectForm' name='ptProjectForm' class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <input type="hidden" id="id" name="id"/>
            <input type="hidden" id="version" name="version"/>

            <app:systemEntityByReserved
                    name="entityTypeId"
                    typeId="${SystemEntityTypeCacheUtility.TYPE_USER_ENTITY_TYPE}"
                    reservedId="${AppUserEntityTypeCacheUtility.PT_PROJECT}">
            </app:systemEntityByReserved>

            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="name">Name:</label>

                <div class="col-md-3">
                    <input class="k-textbox" id="name" name="name" tabindex="1"
                           placeholder="Name" required validationMessage="Required"/>
                </div>

                <div class="col-md-2 pull-left">
                    <span class="k-invalid-msg" data-for="name"></span>
                </div>

                <label class="col-md-1 control-label label-required" for="code">Code:</label>

                <div class="col-md-3">
                    <input type="text" class="k-textbox" id="code" name="code" tabindex="2"
                           placeholder="Code" required validationMessage="Required"/>
                </div>

                <div class="col-md-2 pull-left">
                    <span class="k-invalid-msg" data-for="code"></span>
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