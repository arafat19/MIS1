<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Search Bug Details
        </div>
    </div>
    <g:formRemote name="searchForm" url="[controller: 'ptBug', action: 'searchBugDetails']" method="POST"
                  update="updateBugDiv"
                  before="if (!executePreCondition()) return false"
                  onComplete="executePostCondition()">
        <div class="panel-body">
            <input type="hidden" id="hidBugId" name="hidBugId"/>

            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="id">Bug ID:</label>

                <div class="col-md-3">
                    <input type="text" class="k-textbox" id="id" name="id" tabindex="1"
                           placeholder="" required validationMessage="Required"/>
                </div>

                <div class="col-md-2 pull-left">
                    <span class="k-invalid-msg" data-for="id"></span>
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
    </g:formRemote>
</div>

<div id="updateBugDiv" style="display: none">
    <g:render template='/projectTrack/ptBug/tmpBugDetails'/>
</div>
<g:render template='/projectTrack/ptBug/scriptBugDetails'/>

