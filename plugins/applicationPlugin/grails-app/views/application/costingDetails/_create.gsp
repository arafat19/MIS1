<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">

        <div class="panel-title">
            Create Costing Details
        </div>

    </div>


    <form id="costingDetailsForm" name="costingDetailsForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <input type="hidden" name="id" id="id"/>
            <input type="hidden" name="version" id="version"/>

            <div class="form-group">
                <label class="col-md-3 control-label label-required" for="costingTypeId">Costing Type:</label>

                <div class="col-md-6">
                    <app:dropDownCostingType
                            name="costingTypeId"
                            required="true"
                            validationMessage="Required"
                            dataModelName="dropDownCostingType"
                            tabindex="1">
                    </app:dropDownCostingType>
                </div>

                <div class="col-md-3 pull-left">
                    <span class="k-invalid-msg" data-for="costingTypeId"></span>
                </div>
            </div>


            <div class="form-group">
                <label class="col-md-3 control-label label-optional" for="description">Description:</label>

                <div class="col-md-9">
                    <textarea type="text" class="k-textbox" id="description" name="description" rows="2" tabindex="2"
                              placeholder="255 Char Max"></textarea>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-3 control-label label-required"
                       for="costingAmount">Costing Amount:</label>

                <div class="col-md-6">
                    <input type="text" id="costingAmount" name="costingAmount" tabindex="3"
                           placeholder="Costing Amount" required validationMessage="Required"/>
                </div>

                <div class="col-md-3 pull-left">
                    <span class="k-invalid-msg" data-for="costingAmount"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-3 control-label label-required" for="costingDate">Costing Date:</label>

                <div class="col-md-6">
                    <app:dateControl
                            name="costingDate"
                            required="true"
                            validationMessage="Required"
                            tabindex="4">
                    </app:dateControl>
                </div>

                <div class="col-md-3 pull-left">
                    <span class="k-invalid-msg" data-for="costingDate"></span>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="5"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="6"
                    aria-disabled="false" onclick='clearCostingDetailsForm();'><span
                    class="k-icon k-i-close"></span>Cancel
            </button>
        </div>
    </form>
</div>