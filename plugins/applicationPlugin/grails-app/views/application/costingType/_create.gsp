<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">

        <div class="panel-title">
            Create Costing Type
        </div>

    </div>


    <form id="costingTypeForm" name="costingTypeForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <input type="hidden" name="id" id="id"/>
            <input type="hidden" name="version" id="version"/>

            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="name">Name:</label>

                <div class="col-md-4">
                    <input type="text" class="k-textbox" id="name" name="name" tabindex="1"
                           placeholder="Unique Costing Type Name" required validationMessage="Required"
                           autofocus/>
                </div>

                <div class="col-md-2 pull-left">
                    <span class="k-invalid-msg" data-for="name"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-1 control-label label-optional" for="description">Description:</label>

                <div class="col-md-4">
                    <textarea type="text" class="k-textbox" id="description" name="description" rows="2" tabindex="2"
                              placeholder="255 Char Max"></textarea>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="3"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="4"
                    aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>
        </div>
    </form>
</div>