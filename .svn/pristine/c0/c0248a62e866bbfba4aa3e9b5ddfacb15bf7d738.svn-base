<%@ page import="com.athena.mis.utility.DateUtility" %>
<g:render template="/arms/rmsTask/scriptForDisburseCashCollection" />

<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Disburse Cash Collection
        </div>
    </div>

    <g:formRemote name='disburseCashCollectionForm' class="form-horizontal form-widgets" role="form"
                  update="divTaskDetailsWithDisburse" method="post" before="if (!executePreForDisburseCashCollection()) {return false;}"
                  onSuccess="executePostForDisburseCashCollection()" onComplete ="onCompleteAjaxCall()"
                  url="${[controller:'rmsTask', action: 'searchDisburseCashCollection']}">
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
                        <option value="refNo">Ref No</option>
                        <option value="pinNo">Pin No</option>
                    </select>
                </div>

                <div class="col-md-2">
                    <input class="k-textbox" id="property_value" name="property_value" tabindex="4"/>
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


<div id="divTaskDetailsWithDisburse" class="table-responsive" style="display: block">
</div>