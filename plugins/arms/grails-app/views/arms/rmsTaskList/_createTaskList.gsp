<div class="panel panel-primary">
    <form id='taskListForm' name='taskListForm' class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <div class="form-group">
                <input type="hidden" id="exchangeHouseId" name="exchangeHouseId"/>

                <label class="col-md-2 control-label">Previous Task List:</label>

                <div class="col-md-1">
                    <span id="lblPreviousName" name="lblPreviousName"></span>
                </div>

                <label class="col-md-2 control-label label-required" for="name">Enter Task List Name:</label>

                <div class="col-md-2">
                    <input class="k-textbox" id="name" name="name" tabindex="1"/>
                </div>

                <label class="col-md-2 control-label" for="applyToAllTask">Apply To All Task(s):</label>

                <div class="col-md-1">
                    <g:checkBox tabindex="2" name="applyToAllTask"/>
                </div>

                <div class="col-md-2">
                    <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                            role="button" aria-disabled="false" tabindex="3" onclick='return createTaskList();'>
                        <span class="k-icon k-i-plus"></span>Make Task List
                    </button>
                </div>
            </div>
        </div>
    </form>
</div>

