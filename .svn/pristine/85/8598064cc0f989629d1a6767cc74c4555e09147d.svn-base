<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            <span id="lblFormTitle"></span>
        </div>
    </div>

    <form id="entityNoteForm" name='entityNoteForm' class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <input type="hidden" name="id" id="id"/>
            <input type="hidden" name="version" id="version"/>
            <input type="hidden" name="entityTypeId" id="entityTypeId"/>
            <input type="hidden" name="entityId" id="entityId"/>
            <input type="hidden" name="pluginId" id="pluginId"/>

            <div class="form-group">
                <div class="col-md-1 align_right">
                    <span id="lblEntityTypeName"></span>
                </div>

                <div class="col-md-11">
                    <span id="lblEntityName"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-1 control-label label-required"
                       for="note">Note:</label>

                <div class="col-md-11">
                    <textarea type="text" class="k-textbox" id="note" name="note" rows="2" maxlength="1000"
                              placeholder="1000 Char Max" tabindex="1"
                              required validationMessage="Required"></textarea>
                </div>

                <div class="col-md-3">
                    <span class="k-invalid-msg" data-for="caption"></span>
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
                    aria-disabled="false" onclick='resetEntityNoteForm();'><span
                    class="k-icon k-i-close"></span>Cancel
            </button>
        </div>
    </form>
</div>

<div class="form-group">
    <table id="flex1" style="display:none"></table>
</div>
