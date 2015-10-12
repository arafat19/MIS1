<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Search Inventory Production
        </div>
    </div>

    <g:formRemote name="searchForm" url="[controller: 'invReport', action: 'searchInventoryProductionRpt']" method="POST"
                  update="updateProductionDiv"
                  before="if (!executePreCondition()) return false"
                  onComplete="executePostCondition()">
        <div class="panel-body">
            <input type="hidden" id="hidTransactionId" name="hidTransactionId"/>
            <div class="form-group">
                <label class="col-md-2 control-label label-required" for="transactionId">Transaction ID:</label>

                <div class="col-md-3">
                    <input class="k-textbox"
                           type="text" pattern="^(0|[1-9][0-9]*)$"
                           id="transactionId"
                           name="transactionId"
                           required="required"
                           tabindex="1"
                           placeholder="Transaction ID  (Only Digits)"
                           validationMessage="Required" autofocus/>
                </div>

                <div class="col-md-2 pull-left">
                    <span class="k-invalid-msg" data-for="transactionId"></span>
                </div>

                <label class="col-md-1 control-label label-optional" for="IdType">Type:</label>

                <div class="col-md-3">
                    <g:select id="IdType" name="IdType" tabindex="2"
                              from="${['Production ID', 'Trace/Chalan No']}"/>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="showReport" name="showReport" type="submit" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="2"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>

            <app:ifAllUrl urls="/invReport/downloadInventoryProductionRpt">
                <span class="download_icon_set">
                    <ul>
                        <li>Save as :</li>
                        <li><a href="javascript:void(0)" id="printInventoryProductionReport" class="pdf_icon"></a>
                        </li>
                    </ul>
                </span>
            </app:ifAllUrl>

        </div>
    </g:formRemote>
</div>

<div id="updateProductionDiv" style="display: none">
    <g:render template='/inventory/report/inventoryProduction/tmpProduction'/>
</div>
<g:render template="/inventory/report/inventoryProduction/script"/>

