
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Search Indent
        </div>
    </div>

    <g:formRemote name="searchForm" url="[controller: 'procReport', action: 'searchIndentRpt']" method="POST"
                  update="updateIndentDiv"
                  before="if (!executePreConditionToGetIndent()) return false"
                  onComplete="executePostConditionForIndent()">
        <div class="panel-body">
            <g:hiddenField name="hideIndentId"/>
            <div class="form-group">
                <div class="form-group">
                    <label class="col-md-2 control-label label-required" for="id">Indent Trace No:</label>

                    <div class="col-md-3">
                        <input type="text"
                               class="k-textbox"
                               required="Required"
                               tabindex="1"
                               validationMessage="Invalid Trace No"
                               pattern="^(0|[1-9][0-9]*)$"
                               id="id"
                               name="id"/>
                    </div>

                    <div class="col-md-3 pull-left">
                        <span class="k-invalid-msg" data-for="id"></span>
                    </div>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="showReport" name="showReport" type="submit" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="2"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>

            <app:ifAllUrl urls="/procReport/downloadIndentRpt">
                <span class="download_icon_set">
                    <ul>
                        <li>Save as :</li>
                        <li><a href="javascript:void(0)" id="printIndentReport" class="pdf_icon"></a></li>
                    </ul>
                </span>
            </app:ifAllUrl>

        </div>
    </g:formRemote>
</div>


<div id="updateIndentDiv" style="display: none">
    <g:render template='/procurement/report/indent/tmpIndent'/>
</div>
<g:render template="/procurement/report/indent/script"/>
