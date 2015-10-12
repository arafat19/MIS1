<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">

        <div class="panel-title">
            Search Purchase Request
        </div>

    </div>
    <g:formRemote name="searchForm" url="[controller: 'procReport', action: 'searchPurchaseRequestRpt']" method="POST"
                  update="updatePRDiv"
                  before="if (!executePreConditionForSearchPR()) return false"
                  onComplete="executePostCondition()">
        <div class="panel-body">
            <g:hiddenField name="hidPurchaseRequestId" value=""/>
            <div class="form-group">
                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="purchaseRequestId">PR No:</label>

                        <div class="col-md-5">
                            <input type="text"
                                   tabindex="1"
                                   class="k-textbox"
                                   pattern="^(0|[1-9][0-9]*)$"
                                   placeholder="PR No"
                                   id="purchaseRequestId"
                                   name="purchaseRequestId"/>
                        </div>

                        <div class="col-md-4 pull-left">
                            <span class="k-invalid-msg" data-for="purchaseRequestId"></span>
                        </div>

                    </div>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="showReport" name="showReport" type="submit" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="2"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>
        %{--<input name="showReport" id="showReport" class="ui-icon-search search bu" type='button' value='Search'/>--}%
            <app:ifAllUrl urls="/procReport/downloadPurchaseRequestRpt">
                <span class="download_icon_set">
                    <ul>
                        <li>Save as :</li>
                        <li><a href="javascript:void(0)" id="printPurchaseRequestReport" class="pdf_icon"></a></li>
                    </ul>
                </span>
            </app:ifAllUrl>

        </div>
    </g:formRemote>
</div>

<div id="updatePRDiv" style="display: none">
    <g:render template='/procurement/report/purchaseRequest/tmpPurchaseRequest'/>
</div>
<g:render template="/procurement/report/purchaseRequest/script"/>

