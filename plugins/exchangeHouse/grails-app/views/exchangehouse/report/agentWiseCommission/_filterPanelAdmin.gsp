<div id="application_top_panel" class="panel panel-primary">
	<div class="panel-heading">
		<div class="panel-title">
			Agent Wise Commission
		</div>
	</div>
    <input id="hidLocalCurrency" type="hidden" value="<app:localCurrency property="symbol"/>">
	<form id='filterAgentForm' name='filterAgentForm' class="form-horizontal form-widgets" role="form">
		<div class="panel-body">
			<div class="form-group">
				<label class="col-md-2 control-label label-required" for="createdDateFrom">Start Date:</label>
				<div class="col-md-2">
				<app:dateControl name="createdDateFrom" value="${startDate}" tabindex="1">
				</app:dateControl>
				</div>

				<label class="col-md-2 control-label label-required" for="createdDateTo">End Date:</label>
				<div class="col-md-2">
                    <app:dateControl name="createdDateTo" tabindex="2">
                    </app:dateControl>
				</div>

				<label class="col-md-2 control-label label-required" for="agentId">Agent:</label>
				<div class="col-md-2">
					<exh:dropDownAgent dataModelName="dropDownAgent" name="agentId" tabindex="3">
					</exh:dropDownAgent>
				</div>
			</div>
		</div>
		<div class="panel-footer">
			<button id="create" name="search" id="search" type="submit" data-role="button"
			        class="k-button k-button-icontext"
			        role="button" tabindex="4"
			        aria-disabled="false"><span class="k-icon k-i-search"></span>Search
			</button>
			<span class="download_icon_set">
				<ul>
					<li>Save as :</li>
					<li><a href="javascript:void(0)" id="downloadBtn" class="pdf_icon"></a></li>
				</ul>
			</span>
		</div>
	</form>
</div>

<table id="flex1" style="display:none"></table>