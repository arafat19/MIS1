<div class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Assign Right to Role
        </div>
    </div>

    <form id="frmSaveAttribute" name="frmSaveAttribute" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <div class="form-group">
                <div class="col-md-5">
                    <label class="control-label">Available Right(s):</label>
                </div>

                <div class="col-md-5 col-md-offset-2">
                    <label class="control-label">Assigned Right(s):</label>
                </div>
            </div>

            <div class="form-group">
                <div class="col-md-5">
                    <select id="availableAttributes"
                            name="availableAttributes"
                            multiple="true"
                            style="width:100%;"
                            tabindex="1"></select>
                </div>

                <div class="col-md-2">
                    <div class="form-group">
                        <button style="width: 100%" id="addSingle" name="addSingle" data-role="button" class="k-button"  onclick='return addDataToSelectedRole();' title="Add Selected Right"
                                role="button" tabindex="7"><span class="k-icon k-i-arrow-e"></span>
                        </button>

                    </div>

                    <div class="form-group">
                        <button style="width: 100%" id="addAll" name="addAll" data-role="button" class="k-button"  onclick='return addAllDataToSelectedRole();' title="Add All Rights"
                                role="button" tabindex="7"><span class="k-icon k-i-seek-e"></span>
                        </button>
                    </div>

                    <div class="form-group">
                        <button style="width: 100%" id="removeSingle" name="removeSingle" data-role="button" class="k-button"  onclick='return removeDataFromSelectedRole();' title="Remove Selected Right"
                                role="button" tabindex="7"><span class="k-icon k-i-arrow-w"></span>
                        </button>
                    </div>

                    <div class="form-group">
                        <button style="width: 100%" id="removeAll" name="removeAll" data-role="button" class="k-button"  onclick='return removeAllDataFromSelectedRole();' title="Remove All Rights"
                                role="button" tabindex="7"><span class="k-icon k-i-seek-w"></span>
                        </button>
                    </div>
                </div>
                <div class="col-md-5">
                    <select id="selectedAttributes"
                            name="selectedAttributes"
                            optionKey="id"
                            multiple="true" class=""
                            optionValue="" from=""
                            style="width:100%;"
                            size="" tabindex="6"></select>
                </div>
            </div>
        </div>
        <div class="panel-footer">
            <button id="assign" name="assign"type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="7"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Save
            </button>
            <button id="clear" name="clear" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="8" onclick='return discardChanges();'
                    aria-disabled="false"><span class="k-icon k-i-cancel"></span>Discard Changes
            </button>
        </div>
    </form>
</div>