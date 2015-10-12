<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Create Project Module
        </div>
    </div>

    <form name='projectModuleForm' id="projectModuleForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <input type="hidden" id="projectId" name="projectId"/>
            <input type="hidden" id="id" name="id"/>

            <div class="col-md-6">
                <div class="form-group">
                    <label class="col-md-2 control-label label-optional" for="projectName">Project:</label>

                    <div class="col-md-6">
                        <span id="projectName"></span>
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-md-2 control-label label-required" for="moduleId">Module:</label>

                    <div class="col-md-6">
                        <select id="moduleId" name="moduleId" required="true"
                                validationMessage="Required"></select>
                    </div>

                    <div class="col-md-3 pull-left">
                        <span class="k-invalid-msg" data-for="moduleId"></span>
                    </div>
                </div>
            </div>
        </div>

        <div class="panel-footer">

            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button"
                    aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>
        </div>
    </form>
</div>