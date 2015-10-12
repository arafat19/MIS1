<%@ page import="com.athena.mis.utility.DateUtility; com.athena.mis.accounting.utility.AccGroupCacheUtility" %>

<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Search Project Wise Expense
        </div>
    </div>

    <form id="searchForm" name="searchForm" class="form-horizontal form-widgets" role="form">
        <input type="hidden" name="hidProjectId" id="hidProjectId"/>
        <input type="hidden" name="hidFromDate" id="hidFromDate"/>
        <input type="hidden" name="hidToDate" id="hidToDate"/>
        <input type="hidden" name="hidCoaId" id="hidCoaId"/>
        <input type="hidden" name="hidAccGroupId" id="hidAccGroupId"/>

        <div class="panel-body">
            <div class="form-group">
                <label class="col-md-1 control-label label-optional">Project:</label>

                <div class="col-md-3">
                    <app:dropDownProject dataModelName="dropDownProject"
                                         addAllAttributes="true"
                                         hintsText="ALL"
                                         tabindex="1"
                                         name="projectId"
                                         onchange="updateFromDate();">
                    </app:dropDownProject>
                </div>

                <label class="col-md-1 control-label label-required" for="fromDate">From:</label>

                <div class="col-md-3">
                    <app:dateControl name="fromDate"
                                     diffWithCurrent="${DateUtility.DATE_RANGE_THIRTY * -1}"
                                     tabindex="2">
                    </app:dateControl>
                </div>

                <label class="col-md-1 control-label label-required" for="toDate">To:</label>

                <div class="col-md-3">
                    <app:dateControl name="toDate"
                                     tabindex="3">
                    </app:dateControl>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-1 control-label label-optional">Group:</label>

                <div class="col-md-3">
                    <acc:dropDownAccGroup dataModelName="dropDownAccGroup" tabindex="4"
                                          name="accGroupId"
                                          elements="${AccGroupCacheUtility.ACC_GROUP_BANK},
                                          ${AccGroupCacheUtility.ACC_GROUP_CASH}"
                                          onchange="updateCOAList()"
                                          hintsText="ALL">
                    </acc:dropDownAccGroup>
                </div>

                <label class="col-md-1 control-label label-optional">COA:</label>

                <div class="col-md-3">
                    <select id="coaId"
                            name="coaId"
                            tabindex="5">
                    </select>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="searchProjectWiseExpense" name="searchProjectWiseExpense" type="submit" data-role="button"
                    class="k-button k-button-icontext" role="button"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>

            <app:ifAnyUrl urls="/accReport/downloadProjectWiseExpense,/accReport/downloadProjectWiseExpenseCsv">
                <span class="download_icon_set">
                    <ul>
                        <li>Save as :</li>
                        <app:ifAllUrl urls="/accReport/downloadProjectWiseExpense">
                            <li><a href="javascript:void(0)" id="printProjectWiseExpensePdf" class="pdf_icon"></a>
                            </li>
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/accReport/downloadProjectWiseExpenseCsv">
                            <li><a href="javascript:void(0)" id="printProjectWiseExpenseCsv" class="csv_icon"></a>
                            </li>
                        </app:ifAllUrl>
                    </ul>
                </span>
            </app:ifAnyUrl>
        </div>
    </form>
</div>

<table class="create-form-table" style="height: auto;border: 0px">
    <tr>
        <td width="47%"><table id="flex1" style="display:none"></table></td>
        <td width="55%" style="vertical-align: top;"><table id="flexForExpenseList" style="display:none"></table></td>
    </tr>
</table>