<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">

        <div class="panel-title">
            Create Fixed Asset Maintenance
        </div>

    </div>

    <form id="maintenanceForm" name="maintenanceForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <input type="hidden" id="id" name="id"/>
            <input type="hidden" id="version" name="version"/>

            <div class="form-group">
                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="itemId">Category:</label>

                        <div class="col-md-6">
                            <fxd:dropDownFxdItemForFADetails
                                    dataModelName="dropDownItemId"
                                    name="itemId"
                                    onchange="updateModelAndMaintenanceList();"
                                    tabindex="1"
                                    required="true"
                                    validationMessage="Required">
                            </fxd:dropDownFxdItemForFADetails>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="itemId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required"
                               for="fixedAssetDetailsId">Model/Serial No:</label>

                        <div class="col-md-6">
                            <select id="fixedAssetDetailsId"
                                    name="fixedAssetDetailsId"
                                    tabindex="2"
                                    required="required"
                                    validationMessage="Required">
                            </select>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="fixedAssetDetailsId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required"
                               for="maintenanceTypeId">Maintenance Type:</label>

                        <div class="col-md-6">
                            <select id="maintenanceTypeId"
                                    name="maintenanceTypeId"
                                    tabindex="3"
                                    required="required"
                                    validationMessage="Required">
                            </select>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="maintenanceTypeId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required"
                               for="maintenanceDate">Maintenance Date:</label>

                        <div class="col-md-6">
                            <app:dateControl
                                    name="maintenanceDate"
                                    tabindex="4"
                                    required="true"
                                    validationMessage="Required"
                                    value=""
                                    placeholder="dd/mm/yyyy">
                            </app:dateControl>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="maintenanceDate"></span>
                        </div>
                    </div>
                </div>

                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="amount">Amount:</label>

                        <div class="col-md-6">
                            <input type="text" class="k-textbox" id="amount" name="amount" tabindex="5"
                                   placeholder="Amount" tabindex="5" required validationMessage="Required"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="amount"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required"
                               for="description">Maintenance Details:</label>

                        <div class="col-md-9">
                            <textarea class="k-textbox" id="description" name="description" rows="3"
                                      placeholder="255 Char Max" tabindex="6"
                                      required validationMessage="Maintenance details is Required"></textarea>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="panel-footer">

            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="7"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="8"
                    aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>
        </div>

    </form>
</div>

