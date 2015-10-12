<g:render template='/sarb/taskModel/scriptForTaskStatusDetails'/>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Show Status
        </div>
    </div>
    <input id="hidLocalCurrency" type="hidden" value="<app:localCurrency property="symbol"/>">
    <form id='showTaskForm' name='showTaskForm' class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <g:hiddenField name="id"/>
            <g:hiddenField name="sarbRefNo"/>
            <g:hiddenField name="responseOfRef"/>
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
            <label class="col-md-2 control-label" for="taskRefNo">Ref No:</label>
            <div class="col-md-2">
                    <input id="taskRefNo" name="taskRefNo" class="k-textbox" tabindex="3" placeholder="Enter keyword"/>
                </div>
            </div>
        </div>
        <div class="panel-footer">
            <button id="create" name="searchTask" id="searchTask" type="submit" data-role="button"
                    class="k-button k-button-icontext"
                    role="button" tabindex="3"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>
        </div>

    </form>

</div>


<table id="flex" style="display:none"></table>


<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Task Status Details
        </div>
    </div>
    <div class="panel-body">
        <div class="row">
            <div class="col-md-12">
                <span id="lblSarbRefNo"></span>
            </div>
        </div>
        <div class="row">
            <div class="col-md-12">&nbsp;</div>
        </div>
        <div class="row">
            <div class="col-md-12">
                <span id="lblResponseOfRef"></span>
            </div>
        </div>
    </div>

</div>
