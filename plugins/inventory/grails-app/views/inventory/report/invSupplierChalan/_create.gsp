<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Search Supplier Chalan
        </div>
    </div>

    <form id="searchForm" name="searchForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <input type="hidden" id="hideChalanNo" name="hideChalanNo"/>
            <input type="hidden" id="hideSupplierId" name="hideSupplierId"/>
            <input type="hidden" id="hideStatus" name="hideStatus"/>

            <div class="form-group">
                <label class="col-md-2 control-label label-required" for="chalanNo">Chalan No:</label>

                <div class="col-md-2">
                    <input type="text" class="k-textbox"
                           id="chalanNo" name="chalanNo"
                           placeholder="Chalan No"
                           tabindex="1"
                           autofocus/>
                </div>
                <label class="col-md-1 control-label label-required" for="supplierId">Supplier:</label>

                <div class="col-md-3">
                    <app:dropDownSupplier
                            name="supplierId"
                            tabindex="2"
                            dataModelName="dropDownSupplier">
                    </app:dropDownSupplier>
                </div>
                <label class="col-md-1 control-label label-optional" for="status">Status:</label>

                <div class="col-md-3">
                    <select id="status" name="status" tabindex="3"></select>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="showReport" name="showReport" type="submit" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="4"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>
            <app:ifAnyUrl urls="/invReport/downloadSupplierChalanReport,/invReport/downloadSupplierChalanCsvReport">
                <span class="download_icon_set">
                    <ul>
                        <li>Save as :</li>
                        <app:ifAllUrl urls="/invReport/downloadSupplierChalanReport">
                            <li><a href="javascript:void(0)" id="printPdfBtn" class="pdf_icon"></a></li>
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/invReport/downloadSupplierChalanCsvReport">
                            <li><a href="javascript:void(0)" id="printCsvBtn" class="csv_icon"></a></li>
                        </app:ifAllUrl>
                    </ul>
                </span>
            </app:ifAnyUrl>
        </div>

    </form>
</div>
