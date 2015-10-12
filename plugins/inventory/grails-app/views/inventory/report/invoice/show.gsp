<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Search Chalan
        </div>
    </div>

    <g:formRemote name="searchForm" url="[controller: 'invReport', action: 'searchInvoice']" method="POST"
                  update="updateInvoiceDiv"
                  before="if (!executePreConditionToGetInvoice()) return false"
                  onComplete="executePostConditionForInvoice()">
        <div class="panel-body">
            <input type="hidden" id="hidInvoiceNo" name="hidInvoiceNo"/>
            <div class="form-group">
                <label class="col-md-2 control-label label-required" for="invoiceNo">Trace No:</label>

                <div class="col-md-3">
                    <input type="text" class="k-textbox" tabindex="1"
                           id="invoiceNo" name="invoiceNo" pattern="^(0|[1-9][0-9]*)$"
                           placeholder="Trace No"
                           required="required"
                           data-required-msg="Required"
                           validationMessage="Not valid"
                           autofocus/>
                </div>

                <div class="col-md-2 pull-left">
                    <span class="k-invalid-msg" data-for="invoiceNo"></span>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="showReport" name="showReport" type="submit" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="2"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>

            <app:ifAllUrl urls="/invReport/downloadInvoice">
                <span class="download_icon_set">
                    <ul>
                        <li>Save as :</li>
                        <li><a href="javascript:void(0)" id="printInvoiceReport" class="pdf_icon"></a></li>
                    </ul>
                </span>
            </app:ifAllUrl>

        </div>
    </g:formRemote>
</div>

<div id="updateInvoiceDiv" style="display: none">
    <g:render template='/inventory/report/invoice/tmpInvoice'/>
</div>
<g:render template="/inventory/report/invoice/script"/>








