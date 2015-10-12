<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">

        <div class="panel-title">
            Pending Fixed Asset Report
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
                                    required="true"
                                    validationMessage="Required"
                                    name="projectId"
                                    tabindex="1"
                                    dataModelName="dropDownProjectId">
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
            <button id="searchPendingFixedAsset" name="searchPendingFixedAsset" type="submit" data-role="button"
                    class="k-button k-button-icontext"
                    role="button" tabindex="2"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>
            <app:ifAllUrl urls="/fixedAssetReport/downloadPendingFixedAsset">
                <span class="download_icon_set">
                    <ul>
                        <li>Save as :</li>
                        <li><a href="javascript:void(0)" id="printPendingFixedAsset" class="pdf_icon"></a></li>
                    </ul>
                </span>
            </app:ifAllUrl>
        </div>
    </form>
</div>