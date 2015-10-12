<%@ page import="com.athena.mis.projecttrack.utility.PtBacklogStatusCacheUtility; com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Update Acceptance Criteria
        </div>
    </div>

    <form id='acceptanceCriteriaFormForMyBacklog' name='acceptanceCriteriaFormForMyBacklog' class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <input type="hidden" id="id" name="id"/>
            <input type="hidden" id="version" name="version"/>
            <app:systemEntityByReserved
                    name="backlogStatusAccepted"
                    reservedId="${PtBacklogStatusCacheUtility.ACCEPTED_RESERVED_ID}"
                    typeId="${SystemEntityTypeCacheUtility.PT_BACKLOG_STATUS}">
            </app:systemEntityByReserved>
            <app:systemEntityByReserved
                    name="backlogStatusCompleted"
                    reservedId="${PtBacklogStatusCacheUtility.COMPLETED_RESERVED_ID}"
                    typeId="${SystemEntityTypeCacheUtility.PT_BACKLOG_STATUS}">
            </app:systemEntityByReserved>

            <div class="form-group">
                <label class="col-md-1 control-label label-optional">Idea:</label>

                <div class="col-md-11">
                    <span id="idea"></span>
                </div>
            </div>
            <div class="form-group">
                <label class="col-md-1 control-label label-optional" for="criteria">Criteria:</label>

                <div class="col-md-10"><span id="criteria"></span></div>
            </div>
            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="type">Type:</label>

                <div class="col-md-3">
                    <app:dropDownSystemEntity
                            dataModelName="dropDownType"
                            name="type"
                            typeId="${SystemEntityTypeCacheUtility.PT_ACCEPTANCE_CRITERIA_TYPE}"
                            required="true"
                            tabindex="1"
                            validationMessage="Required">
                    </app:dropDownSystemEntity>
                </div>

                <div class="col-md-2 pull-left">
                    <span class="k-invalid-msg" data-for="type"></span>
                </div>

                <label class="col-md-1 control-label label-required" for="statusId">Status:</label>

                <div class="col-md-3">
                    <app:dropDownSystemEntity
                            dataModelName="dropDownStatus"
                            name="statusId"
                            typeId="${SystemEntityTypeCacheUtility.PT_ACCEPTANCE_CRITERIA_STATUS}"
                            showHints="false"
                            required="true"
                            tabindex="2"
                            validationMessage="Required">
                    </app:dropDownSystemEntity>
                </div>

                <div class="col-md-2 pull-left">
                    <span class="k-invalid-msg" data-for="statusId"></span>
                </div>
            </div>
        </div>

        <div class="panel-footer">

            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="3"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Update
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="4"
                    aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>

        </div>
    </form>
</div>
