<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Create Category-Maintenance Type Mapping
        </div>
    </div>

    <form id="categoryMaintenanceTypeForm" name="categoryMaintenanceTypeForm" class="form-horizontal form-widgets"
          role="form">
        <div class="panel-body">
            <input type="hidden" name="id" id="id"/>
            <input type="hidden" name="version" id="version"/>
            <input type="hidden" id="itemId" name="itemId"/>

            <div class="form-group">
                <label class="col-md-2 control-label label-optional" for="itemName">Category:</label>

                <div class="col-md-4">
                    <span id="itemName"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-2 control-label label-required"
                       for="maintenanceTypeId">Maintenance Type:</label>

                <div class="col-md-3">
                    <fxd:dropDownFxdMaintenanceType
                            id="maintenanceTypeId"
                            name="maintenanceTypeId"
                            tabindex="1"
                            data_model_name="dropDownMaintenanceType"
                            item_id="${fxdItemId}"
                            url="${createLink(controller: 'fxdCategoryMaintenanceType', action: 'dropDownFxdMaintenanceTypeReload')}"
                            required="required"
                            validation_message="Required">
                    </fxd:dropDownFxdMaintenanceType>
                </div>

                <div class="col-md-2 pull-left">
                    <span class="k-invalid-msg" data-for="maintenanceTypeId"></span>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="create" tabindex="3" name="create" type="submit" data-role="button"
                    class="k-button k-button-icontext" role="button"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
            </button>

            <button tabindex="4" id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button"
                    aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>
        </div>
    </form>
</div>