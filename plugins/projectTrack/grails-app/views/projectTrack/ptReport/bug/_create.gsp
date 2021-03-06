<%@ page import="com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Search Bug
        </div>
    </div>

    <form id='bugDetailsForm' name='bugDetailsForm' class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <input type="hidden" id="hideProjectId" name="hideProjectId"/>
            <input type="hidden" id="hideSprintId" name="hideSprintId"/>
            <input type="hidden" id="hideStatusId" name="hideStatusId"/>

            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="projectId">Project:</label>

                <div class="col-md-3">
                    <pt:dropDownProject
                            dataModelName="dropDownProject"
                            name="projectId"
                            onchange="updateSprint();">
                    </pt:dropDownProject>
                </div>
                <label class="col-md-1 control-label label-optional" for="sprintId">Sprint:</label>

                <div class="col-md-3">
                    <select id="sprintId" name="sprintId"></select>
                </div>
                <label class="col-md-2 control-label label-optional" for="statusId">Bug Status:</label>

                <div class="col-md-2">
                    <app:dropDownSystemEntity
                            dataModelName="dropDownStatus"
                            hintsText="ALL"
                            name="statusId"
                            typeId="${SystemEntityTypeCacheUtility.PT_BUG_STATUS}">
                    </app:dropDownSystemEntity>

                </div>
            </div>
        </div>

        <div class="panel-footer">

            <button id="search" name="search" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>
            <span class="download_icon_set">
                <ul>
                    <li>Save as :</li>
                    <li><a href="javascript:void(0)" id="printPdfBtn" class="pdf_icon"></a></li>
                </ul>
            </span>
        </div>
    </form>
</div>
