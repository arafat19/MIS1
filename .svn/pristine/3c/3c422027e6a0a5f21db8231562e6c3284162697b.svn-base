<%@ page import="com.athena.mis.utility.DateUtility" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Forwarded Unpaid Tasks
        </div>
    </div>

    <form id='forwardUnpaidTask' name='forwardUnpaidTask' class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="fromDate">From:</label>

                <div class="col-md-2">
                    <app:dateControl name="fromDate" diffWithCurrent="${DateUtility.DATE_RANGE_SEVEN * -1}"
                                     tabindex="1" placeholder="dd/MM/yyyy">
                    </app:dateControl>
                </div>

                <label class="col-md-1 control-label label-required" for="toDate">To:</label>

                <div class="col-md-2">
                    <app:dateControl name="toDate" tabindex="2" placeholder="dd/MM/yyyy">
                    </app:dateControl>
                </div>

            </div>
        </div>

        <div class="panel-footer">

            <button id="search" name="search" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="4"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>
        </div>
    </form>
</div>