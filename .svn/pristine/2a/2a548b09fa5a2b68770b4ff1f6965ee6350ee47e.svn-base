<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">

        <div class="panel-title">
            Current Fixed Asset Report
        </div>

    </div>
    <form id="searchForm" name="searchForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <g:hiddenField name="hidItemId" value=""/>
            <div class="form-group">
                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="itemId">Category:</label>

                        <div class="col-md-6">
                            <fxd:dropDownFxdItemForFADetails
                                    tabindex="1"
                                    name="itemId"
                                    dataModelName="dropDownItemId"
                                    hintsText="ALL">
                            </fxd:dropDownFxdItemForFADetails>
                        </div>
                    </div>
                </div>

            </div>
        </div>

        <div class="panel-footer">
            <button id="searchCurrentFixedAsset" name="searchCurrentFixedAsset" type="submit" data-role="button"
                    class="k-button k-button-icontext"
                    role="button" tabindex="2"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>
            <app:ifAnyUrl
                    urls="/fixedAssetReport/downloadCurrentFixedAsset,/fixedAssetReport/downloadCurrentFixedAssetCsv">
                <span class="download_icon_set">
                    <ul>
                        <li>Save as :</li>
                        <app:ifAllUrl urls="/fixedAssetReport/downloadCurrentFixedAsset">
                            <li><a href="javascript:void(0)" id="printCurrentFixedAsset" class="pdf_icon"></a></li>
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/fixedAssetReport/downloadCurrentFixedAssetCsv">
                            <li><a href="javascript:void(0)" id="printCurrentFixedAssetCsv" class="csv_icon"></a></li>
                        </app:ifAllUrl>
                    </ul>
                </span>
            </app:ifAnyUrl>
        </div>
    </form>
</div>