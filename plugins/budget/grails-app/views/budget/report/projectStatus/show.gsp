
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Search Project Status
        </div>
    </div>

    <g:formRemote name="searchForm" url="[controller: 'budgReport', action: 'searchProjectStatus']" method="POST"
                  update="updateProjectStatusDiv"
                  before="if (!executePreCondition()) return false"
                  onComplete="executePostCondition()">
        <div class="panel-body">
            <input type="hidden" name="hideProjectId" id="hideProjectId"/>

            <div class="form-group">

                <label class="col-md-1 control-label label-required" for="projectId">Project:</label>

                <div class="col-md-5">
                    <app:dropDownProject
                            name="projectId"
                            required="true"
                            validationMessage="Select a project"
                            dataModelName="dropDownProject">
                    </app:dropDownProject>
                </div>

                <div class="col-md-2 pull-left">
                    <span class="k-invalid-msg" data-for="projectId"></span>
                </div>

            </div>
        </div>

        <div class="panel-footer">
            <button id="showProjectStatus" name="showProjectStatus" type="submit" data-role="button"
                    class="k-button k-button-icontext"
                    role="button" tabindex="2"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>

            <app:ifAllUrl urls="/budgReport/downloadProjectStatus">
                <span class="download_icon_set">
                    <ul>
                        <li>Save as :</li>
                        <li><a href="javascript:void(0)" id="printProjectStatus" class="pdf_icon"></a></li>
                    </ul>
                </span>
            </app:ifAllUrl>
        </div>
    </g:formRemote>
</div>


<div id="updateProjectStatusDiv" style="display: none">
    <g:render template='/budget/report/projectStatus/tmpProjectStatus'/>
</div>
<g:render template="/budget/report/projectStatus/script"/>

