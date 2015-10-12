<%@ page import="com.athena.mis.application.utility.ContentEntityTypeCacheUtility; com.athena.mis.application.utility.SystemEntityTypeCacheUtility; com.athena.mis.utility.DateUtility;" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Create Budget Sprint
        </div>
    </div>

    <form name='budgSprintForm' id="budgSprintForm" class="form-horizontal form-widgets" role="form">

        <div class="panel-body">
            <input type="hidden" id="id" name="id"/>
            <input type="hidden" id="version" name="version"/>

            <app:systemEntityByReserved
                    name="entityTypeId"
                    typeId="${SystemEntityTypeCacheUtility.TYPE_CONTENT_ENTITY}"
                    reservedId="${ContentEntityTypeCacheUtility.CONTENT_ENTITY_TYPE_BUDG_SPRINT}">
            </app:systemEntityByReserved>

            <div class="form-group">
                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-2 control-label label-optional" for="name">Name:</label>

                        <div class="col-md-9">
                            <span id="name"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-2 control-label label-required" for="projectId">Project:</label>

                        <div class="col-md-6">
                            <app:dropDownProject
                                    tabindex="1"
                                    name="projectId"
                                    required="true"
                                    validationMessage="Required"
                                    dataModelName="dropDownProject">
                            </app:dropDownProject>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="projectId"></span>
                        </div>
                    </div>
                </div>

                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="startDate">Start Date:</label>

                        <div class="col-md-6">
                            <app:dateControl
                                    name="startDate"
                                    tabindex="3"
                                    required="true"
                                    validationMessage="Required">
                            </app:dateControl>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="startDate"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="endDate">End Date:</label>

                        <div class="col-md-6">
                            <app:dateControl
                                    name="endDate"
                                    tabindex="4"
                                    diffWithCurrent="${DateUtility.DATE_RANGE_THIRTY}"
                                    required="true"
                                    validationMessage="Required">
                            </app:dateControl>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="endDate"></span>
                        </div>
                    </div>
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
                    aria-disabled="false" onclick='resetBudgSprintForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>
        </div>
    </form>
</div>
