<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Search Voucher
        </div>
    </div>

    <g:formRemote name="searchForm" url="[controller: 'accReport', action: 'searchVoucher']" method="POST"
                  update="updateVoucherDiv"
                  before="if (!executePreCondition()) return false"
                  onComplete="executePostCondition()">
        <div class="panel-body">
            <input type="hidden" name="hideVoucherId" id="hideVoucherId"/>
            <div class="form-group">
                <label class="col-md-2 control-label label-required" for="traceNo">Trace No:</label>

                <div class="col-md-3">
                    <input type="text" class="k-textbox"
                           id="traceNo" name="traceNo"
                           placeholder="Trace No"/>
                </div>

                <label class="col-md-2 control-label label-optional">Cancelled:</label>

                <div class="col-md-3">
                    <g:checkBox tabindex="2" id="cancelledVoucher" name="cancelledVoucher"/>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="showReport" name="showReport" type="submit" data-role="button"
                    class="k-button k-button-icontext" role="button"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>
            <app:ifAllUrl urls="/accReport/downloadVoucher">
                <span class="download_icon_set">
                    <ul>
                        <li>Save as :</li>
                        <li><a href="javascript:void(0)" id="printVoucher" class="pdf_icon"></a></li>
                    </ul>
                </span>
            </app:ifAllUrl>
        </div>
    </g:formRemote>
</div>

<div id="updateVoucherDiv">
    <g:render template='/accounting/report/voucher/tmpVoucher'/>
</div>
<g:render template="/accounting/report/voucher/script"/>