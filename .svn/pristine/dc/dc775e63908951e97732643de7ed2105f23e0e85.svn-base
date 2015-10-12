<div id="application_top_panel" class="panel panel-primary">
	<div class="panel-heading">
		<div class="panel-title">
			Search Transaction Summary
		</div>
	</div>
    <input id="hidLocalCurrency" type="hidden" value="<app:localCurrency property="symbol"/>">
	<form id='searchForm' name='searchForm' class="form-horizontal form-widgets" role="form">
		<div class="panel-body">
			<div class="form-group">
				<label class="col-md-2 control-label label-required" for="createdDateFrom">Start Date:</label>
				<div class="col-md-2">
                    <app:dateControl name="createdDateFrom" tabindex="1">
                    </app:dateControl>
				</div>

				<label class="col-md-2 control-label label-required" for="createdDateTo">End Date:</label>
				<div class="col-md-2">
                    <app:dateControl name="createdDateTo" tabindex="2">
                    </app:dateControl>
				</div>

				<label class="col-md-2 control-label label-required" for="amount">Amount Higher or Equal:</label>
				<div class="col-md-2">
					<input type="text" id="amount" name="amount" class="k-textbox" tabindex="3" />
				</div>
			</div>
		</div>
		<div class="panel-footer">
			<button id="create" name="searchTask" id="searchTask" type="submit" data-role="button"
			        class="k-button k-button-icontext"
			        role="button" tabindex="4"
			        aria-disabled="false"><span class="k-icon k-i-search"></span>Search
			</button>
			<span class="download_icon_set">
				<ul>
					<li>Save as :</li>
					<li><a href="javascript:void(0)" id="printBtn" class="pdf_icon"></a></li>
				</ul>
			</span>
		</div>
	</form>

</div>

<table id="flex1" style="display:none"></table>

<g:render template='/exchangehouse/report/transactionSummary/script'/>
