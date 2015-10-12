<%@ page import="com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Search Sprint Details
        </div>
    </div>
    <g:form id="sprintDetailsForm" name="sprintDetailsForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="projectId">Project:</label>

                <div class="col-md-3">
                    <pt:dropDownProject dataModelName="dropDownProject"
                                        name="projectId"
                                        tabindex="1"
                                        onchange="populateSprint();">
                    </pt:dropDownProject>
                </div>

                <label class="col-md-1 control-label label-required" for="sprintId">Sprint:</label>

                <div class="col-md-3">
                    <select id="sprintId" tabindex="2" name="sprintId"></select>
                </div>

                <label class="col-md-1 control-label label-optional" for="hasOwner">Owner:</label>

                <div class="col-md-2">
                    <g:checkBox tabindex="3" id="hasOwner" name="hasOwner"/>
                </div>
            </div>
        </div>

        <div class="panel-footer">

            <button id="search" name="search" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="4"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>
            <span class="download_icon_set">
                <ul>
                    <li>Save as :</li>
                    <li><a href="javascript:void(0)" id="printPdfBtn" class="pdf_icon"></a></li>
                </ul>
            </span>

        </div>
    </g:form>
</div>

