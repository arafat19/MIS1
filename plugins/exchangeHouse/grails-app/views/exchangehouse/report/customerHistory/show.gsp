<div id="application_top_panel" class="panel panel-primary">
	<div class="panel-heading">
		<div class="panel-title">
			Search Customer History
		</div>
	</div>
    <input id="hidLocalCurrency" type="hidden" value="<app:localCurrency property="symbol"/>">
	<form name='searchForm' id='searchForm' class="form-horizontal form-widgets" role="form" method="post">
		<div class="panel-body">
			<g:hiddenField name="hidCustomerId"/>
			<div class="form-group">
				<label class="col-md-2 control-label label-required" for="customerId">Customer A/C No:</label>

				<div class="col-md-2">
					<input type="text" class="required digits k-textbox" id="customerId" name="customerId" tabindex="1"/>
				</div>

				<label class="col-md-2 control-label label-required" for="createdDateFrom">Start Date:</label>
            <div class="col-md-2">
                <app:dateControl id="createdDateFrom" name="createdDateFrom" value="${startDate}" tabindex="2">
                </app:dateControl>
            </div>

				<label class="col-md-2 control-label label-required" for="createdDateTo">End Date:</label>
            <div class="col-md-2">
                <app:dateControl id="createdDateTo" name="createdDateTo" tabindex="3">
                </app:dateControl>
            </div>
        </div>

		</div>

		<div class="panel-footer">

			<button id="searchReport" name="searchReport" type="submit" data-role="button"
			        class="k-button k-button-icontext"
			        role="button" tabindex="4"
			        aria-disabled="false"><span class="k-icon k-i-search"></span>Search
			</button>
			<span class="download_icon_set" style="display: none" id="downloadBtnSpan">
				<ul>
					<li>Save as :</li>
					<li><a href="javascript:void(0)" id="downloadBtn" class="pdf_icon"></a></li>
				</ul>
			</span>

		</div>
	</form>

	<div class="panel-heading">
		<div class="panel-title">
			Customer Details:
		</div>
	</div>

	<div class="panel-body" id="containerCustomerDetails">
        <exh:customerDetails id="customerDetails"
                                 url="${createLink(controller: 'exhCustomer', action: 'reloadCustomerDetails')}">
        </exh:customerDetails>
	</div>
</div>


<table id="flex1" style="display:none"></table>

<g:render template='/exchangehouse/report/customerHistory/script'/>

