<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Task Status Report
        </div>
    </div>
    <form id='frmSearchTaskDetails' name='frmSearchTaskDetails' class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <div class="form-group">
                <label class="col-md-2 control-label label-required" for="createdDateFrom">From Date:</label>
                <div class="col-md-3">

                    <app:dateControl name="createdDateFrom" tabindex="1">
                    </app:dateControl>
                </div>
                <label class="col-md-2 control-label label-required" for="securityType">Search By:</label>
                <div class="col-md-3">
                    <g:select
                            name="securityType" width="50"
                            tabindex="3"
                            onchange=""
                            from="${['Ref No','Pin No']}"/>
                </div>
            </div>
            <div class="form-group">
                <label class="col-md-2 control-label label-required" for="createdDateTo">To Date:</label>
                <div class="col-md-3">
                    <app:dateControl name="createdDateTo" tabindex="2">
                    </app:dateControl>
                </div>

                <label class="col-md-2 control-label label-required" for="securityNo">Security No:</label>
                <div class="col-md-3">
                    <input type="text" class="k-textbox" id="securityNo" name="securityNo" tabindex="4"
                           placeholder="" />
                </div>
            </div>
        </div>
        <div class="panel-footer">
            <button id="create" name="searchBatch" id="searchBatch" type="submit" data-role="button"
                    class="k-button k-button-icontext"
                    role="button" tabindex="5"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>
        </div>
    </form>
</div>


<g:render template="/exchangehouse/exhTask/scriptForSearchTaskForAgent"/>


<div id="taskDetailsContainer"></div>

