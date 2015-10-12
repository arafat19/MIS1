<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">

        <div class="panel-title">
            Consumption Against Fixed Asset Report
        </div>

    </div>
    <form id="searchForm" name="searchForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <g:hiddenField name="hidProjectId" value=""/>
            <div class="form-group">
                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="projectId">Project:</label>

                        <div class="col-md-6">
                            <app:dropDownProject
                                    dataModelName="dropDownProject"
                                    name="projectId"
                                    tabindex="1"
                                    required="true"
                                    validationMessage="Required">
                            </app:dropDownProject>
                        </div>
                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="projectId"></span>
                        </div>
                    </div>
                </div>

            </div>
        </div>

        <div class="panel-footer">
            <button id="searchConsumpAgainstAsset" name="searchConsumpAgainstAsset" type="submit" data-role="button"
                    class="k-button k-button-icontext"
                    role="button" tabindex="2"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>
            <app:ifAllUrl urls="/fixedAssetReport/downloadConsumptionAgainstAsset">
                <span class="download_icon_set">
                    <ul>
                        <li>Save as :</li>
                        <li><a href="javascript:void(0)" id="printReportPDF" type="submit" class="pdf_icon"></a></li>
                    </ul>
                </span>
            </app:ifAllUrl>
        </div>
    </form>
</div>