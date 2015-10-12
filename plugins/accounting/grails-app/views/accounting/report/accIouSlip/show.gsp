
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Search IOU Slip
        </div>
    </div>

    <g:formRemote name="searchForm" url="[controller: 'accReport', action: 'listAccIouSlipRpt']" method="POST"
                  update="updateIouSlipDiv"
                  before="if (!executePreConditionToGetIouSlip()) return false"
                  onComplete="executePostConditionForIouSlip()">
        <div class="panel-body">
            <input type="hidden" name="hideAccIouSlipId" id="hideAccIouSlipId"/>
            <div class="form-group">
                <label class="col-md-2 control-label label-required" for="accIouSlipId">Trace No:</label>

                <div class="col-md-3">
                    <input type="text" class="k-textbox" id="accIouSlipId" name="accIouSlipId"
                           placeholder="Trace No..." required="" validationMessage="Required"/>
                </div>

                <div class="col-md-3 pull-left">
                    <span class="k-invalid-msg" data-for="accIouSlipId"></span>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="showIouSlip" name="showIouSlip" type="submit" data-role="button"
                    class="k-button k-button-icontext" role="button"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>
            <app:ifAllUrl urls="/accReport/downloadAccIouSlipRpt">
                <span class="download_icon_set">
                    <ul>
                        <li>Save as :</li>
                        <li><a href="javascript:void(0)" id="printIouSlip" class="pdf_icon"></a></li>
                    </ul>
                </span>
            </app:ifAllUrl>
        </div>
    </g:formRemote>
</div>


<div id="updateIouSlipDiv" style="display: none">
    <g:render template='/accounting/report/accIouSlip/tmpIouSlip'/>
</div>
<g:render template="/accounting/report/accIouSlip/script"/>





