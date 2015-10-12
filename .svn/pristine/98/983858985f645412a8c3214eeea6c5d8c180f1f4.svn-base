<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">

        <div class="panel-title">
            Search Purchase Order
        </div>

    </div>
    <g:formRemote name="searchForm" url="[controller: 'procReport', action: 'searchPurchaseOrderRpt']" method="POST"
                  update="updatePODiv"
                  before="if (!executePreConditionForSearchPO()) return false"
                  onComplete="executePostCondition()">
        <div class="panel-body">
            <g:hiddenField name="hidPurchaseOrderId"/>
            <g:hiddenField name="hidIsCancelled"/>
            <div class="form-group">
                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="purchaseOrderId">PO No:</label>

                        <div class="col-md-5">
                            <input type="text"
                                   tabindex="1"
                                   class="k-textbox"
                                   pattern="^(0|[1-9][0-9]*)$"
                                   placeholder="PO No"
                                   id="purchaseOrderId"
                                   name="purchaseOrderId" />
                        </div>

                        <div class="col-md-4 pull-left">
                            <span class="k-invalid-msg" data-for="purchaseOrderId"></span>
                        </div>

                    </div>
                </div>

                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="cancelledPo">Cancelled:</label>

                        <div class="col-md-9">
                            <g:checkBox tabindex="2" id="cancelledPo" name="cancelledPo"/>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="showReport" name="showReport" type="submit" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="3"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>

            <app:ifAllUrl urls="/procReport/downloadPurchaseOrderRpt">
                <span class="download_icon_set">
                    <ul>
                        <li>Save as :</li>
                        <li><a href="javascript:void(0)" id="printPurchaseOrderReport" class="pdf_icon"></a></li>
                    </ul>
                </span>
            </app:ifAllUrl>

        </div>
    </g:formRemote>
</div>


<div id="updatePODiv" style="display: none">
    <g:render template='/procurement/report/purchaseOrder/tmpPurchaseOrder'/>
</div>
<g:render template="/procurement/report/purchaseOrder/script"/>
