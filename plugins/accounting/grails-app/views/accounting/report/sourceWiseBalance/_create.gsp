<%@ page import="com.athena.mis.accounting.utility.AccSourceCacheUtility; com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>

<div class="form-group">
    <div class="form-group col-md-9">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading">
                <div class="panel-title">
                    Search Source wise Balance
                </div>
            </div>

            <form name='searchForm' id='searchForm' class="form-horizontal form-widgets" role="form">
                <div class="panel-body">
                    <input type="hidden" name="hidSourceTypeId" id="hidSourceTypeId"/>
                    <input type="hidden" name="hidSourceCategoryId" id="hidSourceCategoryId"/>
                    <input type="hidden" name="hidToDate" id="hidToDate"/>
                    <input type="hidden" name="hidFromDate" id="hidFromDate"/>
                    <input type="hidden" name="hidProjectId" id="hidProjectId"/>

                    <div class="form-group">
                        <label class="col-md-2 control-label label-required" for="fromDate">From Date:</label>

                        <div class="col-md-4">
                            <app:dateControl name="fromDate"
                                             tabindex="1">
                            </app:dateControl>
                        </div>

                        <label class="col-md-2 control-label label-required" for="toDate">To Date:</label>

                        <div class="col-md-4">
                            <app:dateControl name="toDate"
                                             tabindex="2">
                            </app:dateControl>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-2 control-label label-required">Source Type:</label>

                        <div class="col-md-4">
                            <app:dropDownSystemEntity
                                    dataModelName="dropDownSourceType"
                                    name="sourceTypeId"
                                    tabindex="3"
                                    onchange="onChangeSourceType();"
                                    elements="${[AccSourceCacheUtility.SOURCE_TYPE_CUSTOMER,
                                            AccSourceCacheUtility.SOURCE_TYPE_EMPLOYEE,
                                            AccSourceCacheUtility.SOURCE_TYPE_ITEM,
                                            AccSourceCacheUtility.SOURCE_TYPE_LEASE_ACCOUNT,
                                            AccSourceCacheUtility.SOURCE_TYPE_SUB_ACCOUNT,
                                            AccSourceCacheUtility.SOURCE_TYPE_SUPPLIER,
                                            AccSourceCacheUtility.SOURCE_TYPE_LC,
                                            AccSourceCacheUtility.SOURCE_TYPE_IPC]}"
                                    typeId="${SystemEntityTypeCacheUtility.TYPE_ACC_SOURCE}">
                            </app:dropDownSystemEntity>
                        </div>

                        <label class="col-md-2 control-label label-optional"
                               for="sourceCategoryId">Source Category:</label>

                        <div class="col-md-4">
                            <select id="sourceCategoryId"
                                    name="sourceCategoryId"
                                    tabindex="5">
                            </select>
                        </div>

                    </div>

                    <div class="form-group">
                        <label class="col-md-2 control-label label-optional" for="projectId">Project:</label>

                        <div class="col-md-4">
                            <app:dropDownProject name="projectId"
                                                 tabindex="4"
                                                 hintsText="ALL"
                                                 dataModelName="dropDownProject">
                            </app:dropDownProject>
                        </div>
                        <label class="col-md-2 control-label label-optional">Account Code:</label>

                        <div class="col-md-4">
                            <span id='coaCode'></span>
                            <input type="hidden" id="hidCoaId" name="hidCoaId"/>
                        </div>
                    </div>
                </div>

                <div class="panel-footer">
                    <button id="showReport" name="showReport" type="submit" data-role="button"
                            class="k-button k-button-icontext" role="button" tabindex="5"
                            aria-disabled="false"><span class="k-icon k-i-search"></span>Search
                    </button>
                    <app:ifAnyUrl urls="/accReport/downloadSourceWiseBalance,/accReport/downloadSourceWiseBalanceCsv">
                        <span class="download_icon_set">
                            <ul>
                                <li>Save as :</li>
                                <app:ifAllUrl urls="/accReport/downloadSourceWiseBalance">
                                    <li><a href="javascript:void(0)" id="printSourceWiseBalancePdf"
                                           class="pdf_icon"></a></li>
                                </app:ifAllUrl>
                                <app:ifAllUrl urls="/accReport/downloadSourceWiseBalanceCsv">
                                    <li><a href="javascript:void(0)" id="printSourceWiseBalanceCsv"
                                           class="csv_icon"></a></li>
                                </app:ifAllUrl>
                            </ul>
                        </span>
                    </app:ifAnyUrl>
                </div>
            </form>
        </div>

        <div class="form-group">
            <table id="flex1" style="display:none"></table>
        </div>
    </div>

    <div class="form-group col-md-3">
        <table id="flexSearchCOA" style="display:none"></table>
    </div>
</div>