<%@ page import="com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>


<div class="form-group">
    <div class="form-group col-md-9">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading">
                <div class="panel-title">
                    Create Inventory Consumption
                </div>
            </div>

            <form name='frmInventoryConsumption' id='frmInventoryConsumption' class="form-horizontal form-widgets"
                  role="form">
                <div class="panel-body">
                    <input type="hidden" id="id" name="id"/>
                    <input type="hidden" id="version" name="version"/>
                    <input type="hidden" id="budgetItem" name="budgetItem"/>
                    <input type="hidden" id="itemCount" name="itemCount"/>


                    <div class="col-md-6">

                        <div class="form-group">
                            <label class="col-md-4 control-label label-optional">Project:</label>

                            <div class="col-md-8">
                                <span id="lblProjectName"></span>
                            </div>
                        </div>

                        <div class="form-group">
                            <label class="col-md-4 control-label label-optional">Budget Item:</label>

                            <div class="col-md-8">
                                <span id="lblBudgetItem"></span>
                            </div>
                        </div>

                        <div class="form-group">
                            <label class="col-md-4 control-label label-optional">Budg. Details:</label>

                            <div class="col-md-8">
                                <span id="lblBudgetDetails"></span>
                            </div>
                        </div>

                        <div class="form-group">
                            <label class="col-md-4 control-label label-required"
                                   for="inventoryTypeId">Inventory Type:</label>

                            <div class="col-md-6">
                                <app:dropDownSystemEntity
                                        dataModelName="dropDownInventoryType"
                                        name="inventoryTypeId"
                                        tabindex="1"
                                        onchange="javascript:updateInventoryList();"
                                        typeId="${SystemEntityTypeCacheUtility.TYPE_INVENTORY_TYPE}">
                                </app:dropDownSystemEntity>

                            </div>

                        </div>

                    </div>

                    <div class="col-md-6">

                        <div class="form-group">
                            <label class="col-md-3 control-label label-optional" for="comments">Comments:</label>

                            <div class="col-md-9">
                                <textarea type="text" class="k-textbox" id="comments" name="comments" rows="2"
                                          placeholder="255 Char Max"></textarea>
                            </div>

                        </div>

                        <div class="form-group">
                            <label class="col-md-3 control-label label-required"
                                   for="inventoryId">Inventory:</label>

                            <div class="col-md-7">
                                <select id="inventoryId"
                                        name="inventoryId"
                                        tabindex="2">
                                </select>

                            </div>

                        </div>

                    </div>

                </div>

                <div class="panel-footer">
                    <button id="create" name="create" type="submit" data-role="button"
                            class="k-button k-button-icontext" role="button" tabindex="3"
                            aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
                    </button>

                    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                            class="k-button k-button-icontext" role="button" tabindex="4"
                            aria-disabled="false" onclick='resetCreateForm();'><span
                            class="k-icon k-i-close"></span>Cancel
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




