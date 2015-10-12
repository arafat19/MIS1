<%@ page import="com.athena.mis.utility.DateUtility" %>
<g:render template='/arms/report/beneficiary/scriptSearchBeneficiary'/>

<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Search Beneficiary
        </div>
    </div>

    <g:formRemote name='searchBeneficiary' class="form-horizontal form-widgets" role="form"
                  update="divBeneficiaryDetails" method="post"
                  before="if (!executePreConditionForSearchBeneficiary()) {return false;}" onComplete ="onCompleteAjaxCall()"
                  url="${[controller:'rmsReport', action: 'searchBeneficiaryForGrid']}">
        <div class="panel-body">
            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="from_date">From:</label>

                <div class="col-md-2">
                    <app:dateControl name="from_date" diffWithCurrent="${DateUtility.DATE_RANGE_HUNDREAD_EIGHTY * -1}"
                                     tabindex="1" placeholder="dd/MM/yyyy">
                    </app:dateControl>
                </div>

                <label class="col-md-1 control-label label-required" for="to_date">To:</label>

                <div class="col-md-2">
                    <app:dateControl name="to_date" tabindex="2" placeholder="dd/MM/yyyy">
                    </app:dateControl>
                </div>

                <div class="col-md-1"></div>

                <div class="col-md-2">
                    <select id="property_name" name="property_name" tabindex="3">
                        <option value="beneficiaryName">Name</option>
                        <option value="accountNo">Account No</option>
                        <option value="beneficiaryPhone">Phone No</option>
                    </select>
                </div>

                <div class="col-md-3">
                    <input class="k-textbox" id="property_value" placeholder="Minimum 3 Characters" name="property_value" tabindex="4"/>
                </div>
            </div>
        </div>

        <div class="panel-footer">

            <button id="search" name="search" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="5"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>
        </div>
    </g:formRemote>
</div>


<div id="divBeneficiaryDetails" class="table-responsive" style="display: block">
    <g:render template="/arms/report/beneficiary/tmplBeneficiaryDetails" />
</div>

