<%@ page import="com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Search Task
        </div>
    </div>

    <form id='storyForm' name='storyForm' class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="projectId">Project:</label>

                <div class="col-md-3">
                    <pt:dropDownProject
                            dataModelName="dropDownProject"
                            name="projectId"
                            tabindex="1"
                            onchange="updateSprint();">
                    </pt:dropDownProject>
                </div>

                <label class="col-md-1 control-label label-required" for="sprintId">Sprint:</label>

                <div class="col-md-3">
                    <select id="sprintId" name="sprintId" tabindex="2"></select>
                </div>

                <label class="col-md-2 control-label label-optional" for="statusId">Task Status:</label>

                <div class="col-md-2">
                    <app:dropDownSystemEntity
                            dataModelName="dropDownStatus"
                            name="statusId"
                            tabindex="3"
                            hintsText="All"
                            typeId="${SystemEntityTypeCacheUtility.PT_BACKLOG_STATUS}">
                    </app:dropDownSystemEntity>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="search" name="search" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="4"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>
        </div>
    </form>
</div>
