<%@ page import="com.athena.mis.application.utility.ContentEntityTypeCacheUtility; com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Customer Details
        </div>
    </div>

    <div class="panel-body" id="containerCustomerDetails">
        <exh:customerDetails id="customerDetails"
                url="${createLink(controller: 'exhCustomer', action: 'reloadCustomerDetails')}">
        </exh:customerDetails>
    </div>
    <sec:access url="/exhReport/downloadCustomerCSV">
    <span class="download_icon_set">
        <ul>
            <li>Save as :</li>
            <li><a href="javascript:void(0)" id="printCsvBtn" class="csv_icon"></a></li>
        </ul>
    </span>
    </sec:access>
</div>
<app:systemEntityByReserved
        name="entityTypeId"
        typeId="${SystemEntityTypeCacheUtility.TYPE_CONTENT_ENTITY}"
        reservedId="${ContentEntityTypeCacheUtility.CONTENT_ENTITY_TYPE_EXH_CUSTOMER}">
</app:systemEntityByReserved>

<table id="flex1" style="display:none"></table>
<g:render template='/exchangehouse/exhCustomer/scriptForAdmin'/>
