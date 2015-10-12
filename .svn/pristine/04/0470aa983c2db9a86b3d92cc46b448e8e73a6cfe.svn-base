<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Search Task
        </div>
    </div>

    <g:formRemote name="searchTaskForm" url="[controller: 'ptReport', action: 'searchForBacklogDetails',params: [ leftMenu: 'ptReport/showForBacklogDetails' ]]" method="POST"
                  update="updateBacklogDetailsDiv"
                  before="if (!executePreConditionForSearchBacklogDetails()) return false"
                  onComplete="executePostCondition()">
        <div class="panel-body">
            <div class="form-group">
                <div class="col-md-2">
                    <select id='idType'
                            name="idType"
                            tabindex="1">
                    </select>
                </div>

                <div class="col-md-8">
                    <input class="k-textbox" id="backlogId" name="backlogId" tabindex="2" placeholder=""
                           required validationMessage="Required"/>
                </div>

                <div class="col-md-2 pull-left">
                    <span class="k-invalid-msg" data-for="backlogId"></span>
                </div>

            </div>
        </div>

        <div class="panel-footer">
            <button id="search" name="search" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="3"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>

            <span class="download_icon_set" style="display: none">
                <ul>
                    <li>Save as :</li>
                    <li><a href="javascript:void(0)" id="printPdfBtn" class="pdf_icon"></a></li>
                </ul>
            </span>
        </div>
    </g:formRemote>
</div>