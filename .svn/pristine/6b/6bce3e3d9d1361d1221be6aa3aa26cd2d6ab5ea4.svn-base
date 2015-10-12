<%@ page import="com.athena.mis.utility.DateUtility; com.athena.mis.projecttrack.utility.PtSprintStatusCacheUtility; com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Create Sprint
        </div>
    </div>

    <form name='sprintForm' id="sprintForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <input type="hidden" id="id" name="id"/>
            <input type="hidden" id="version" name="version"/>
            <app:systemEntityByReserved
                    name="hidSprintStatusDefined"
                    reservedId="${PtSprintStatusCacheUtility.DEFINES_RESERVED_ID}"
                    typeId="${SystemEntityTypeCacheUtility.PT_SPRINT_STATUS}">
            </app:systemEntityByReserved>

            <div class="form-group">
                <label class="col-md-1 control-label label-optional" for="name">Name:</label>

                <div class="col-md-5">
                    <span id="name"></span>
                </div>

                <label class="col-md-2 control-label label-required" for="startDate">Start Date:</label>

                <div class="col-md-3">
                    <app:dateControl
                            name="startDate"
                            tabindex="3"
                            required="true"
                            validationMessage="Required">
                    </app:dateControl>
                </div>

                <div class="col-md-1 pull-left">
                    <span class="k-invalid-msg" data-for="startDate"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="projectId">Project:</label>

                <div class="col-md-3">
                    <pt:dropDownProject
                            dataModelName="dropDownProject"
                            name="projectId"
                            required="true"
                            validationMessage="Required"
                            tabindex="1">
                    </pt:dropDownProject>
                </div>

                <div class="col-md-2 pull-left">
                    <span class="k-invalid-msg" data-for="projectId"></span>
                </div>

                <label class="col-md-2 control-label label-required" for="endDate">End Date:</label>

                <div class="col-md-3">
                    <app:dateControl
                            name="endDate"
                            tabindex="4"
                            diffWithCurrent="${DateUtility.DATE_RANGE_SEVEN}"
                            required="true"
                            validationMessage="Required">
                    </app:dateControl>
                </div>

                <div class="col-md-1 pull-left">
                    <span class="k-invalid-msg" data-for="endDate"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="statusId">Status:</label>

                <div class="col-md-3">
                    <app:dropDownSystemEntity
                            dataModelName="dropDownStatus"
                            name="statusId"
                            required="true"
                            validationMessage="Required"
                            tabindex="2"
                            typeId="${SystemEntityTypeCacheUtility.PT_SPRINT_STATUS}">
                    </app:dropDownSystemEntity>
                </div>

                <div class="col-md-2 pull-left">
                    <span class="k-invalid-msg" data-for="statusId"></span>
                </div>

                <label class="col-md-2 control-label" for="isActive">Active:</label>

                <div class="col-md-4">
                    <g:checkBox tabindex="5" name="isActive"/>
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
                    aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>
        </div>
    </form>
</div>
