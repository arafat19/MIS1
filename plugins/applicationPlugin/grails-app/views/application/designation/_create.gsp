<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">

        <div class="panel-title">
            Create Designation
        </div>

    </div>
    <g:form id="designationForm" name="designationForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <g:hiddenField name="id"/>
            <g:hiddenField name="version"/>
            <div class="form-group">
                <div class="col-md-12">
                    <div class="form-group">
                        <div class="col-md-6">
                            <label class="col-md-3 control-label label-required" for="name">Name:</label>

                            <div class="col-md-6">
                                <input type="text" class="k-textbox" id="name" name="name"
                                       placeholder="Unique Designation Name" required validationMessage="Required"
                                       tabindex="1" autofocus/>
                            </div>

                            <div class="col-md-3 pull-left">
                                <span class="k-invalid-msg" data-for="name"></span>
                            </div>
                        </div>


                        <div class="col-md-6">
                            <label class="col-md-3 control-label label-required" for="shortName">Short Name:</label>

                            <div class="col-md-6">
                                <input type="text" class="k-textbox" id="shortName" name="shortName" tabindex="2"
                                       placeholder="Unique Short Name" required validationMessage="Required"/>
                            </div>

                            <div class="col-md-3 pull-left">
                                <span class="k-invalid-msg" data-for="shortName"></span>
                            </div>
                        </div>
                    </div>
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
                    aria-disabled="false" onclick='clearDesignationForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>

        </div>
    </g:form>
</div>