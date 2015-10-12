<div class="form-group">
    <div class="form-group col-md-9">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading">
                <div class="panel-title">
                    Create Sprint's Budget
                </div>
            </div>

            <form id="sprintBudgetForm" name="sprintBudgetForm" class="form-horizontal form-widgets" role="form">
                <div class="panel-body">
                    <input type="hidden" name="id" id="id"/>
                    <input type="hidden" name="version" id="version"/>
                    <input type="hidden" name="sprintId" id="sprintId"/>
                    <input type="hidden" name="budgetId" id="budgetId"/>

                    <div class="form-group">
                        <label class="col-md-1 control-label label-optional">Sprint:</label>

                        <div class="col-md-4">
                            <span id="lblSprintName"></span>
                        </div>

                        <label class="col-md-2 control-label label-optional">Available:</label>

                        <div class="col-md-5">
                            <span id="lblQuantity"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-1 control-label label-optional">Budget:</label>

                        <div class="col-md-4">
                            <span id="lblBudgetItem"></span>
                        </div>
                        <label class="col-md-2 control-label label-required" for="quantity">Quantity:</label>

                        <div class="col-md-3">
                            <input type="text" class="" id="quantity" name="quantity" tabindex="1"
                                   placeholder="Quantity" required validationMessage="Required"/>
                        </div>

                        <div class="col-md-2 pull-left">
                            <span class="k-invalid-msg" data-for="quantity"></span>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-md-1 control-label label-optional">Details:</label>

                        <div class="col-md-10">
                            <span id='lblBudgetDetails'></span>
                        </div>
                    </div>
                </div>

                <div class="panel-footer">
                    <button id="create" name="create" type="submit" data-role="button"
                            class="k-button k-button-icontext"
                            role="button" tabindex="2"
                            aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
                    </button>

                    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                            class="k-button k-button-icontext" role="button" tabindex="3"
                            aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Cancel
                    </button>
                </div>
            </form>
        </div>

        <div class="form-group">
            <table id="flex1" style="display:none"></table>
        </div>
    </div>

    <div class="form-group col-md-3">
        <table id="flexBudget" style="display:none"></table>
    </div>
</div>
