<div id="application_top_panel" class="panel panel-primary" xmlns="http://www.w3.org/1999/html">
    <div class="panel-heading">
        <div class="panel-title">
            Create Shell Script
        </div>
    </div>

    <form id="shellScriptForm" name="shellScriptForm" class="form-horizontal form-widgets" role="form" method="post">
        <div class="panel-body">
            <input type="hidden" name="id" id="id"/>
            <input type="hidden" name="version" id="version"/>

            <div class="form-group">
                <div class="col-md-12">

                    <div class="form-group">
                        <label class="col-md-2 control-label label-required" for="name">Name:</label>

                        <div class="col-md-3">
                            <input type="text" class="k-textbox" id="name" name="name" tabindex="1"
                                   placeholder="Name" required validationMessage="Required"/>
                        </div>

                        <div class="col-md-2 pull-left">
                            <span class="k-invalid-msg" data-for="name"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-2 control-label label-required" for="script">Script:</label>

                        <div class="col-md-10">
                            <textarea class="k-textbox" id="script" name="script" tabindex="2" rows="4"
                                      placeholder="Max 1500 characters" required validationMessage="Required"
                                      maxlength="1500"></textarea>
                        </div>
                    </div>
                </div>

            </div>
        </div>

        <div class="panel-footer">
            <button name="create" id="create" type="submit" data-role="button"
                    class="k-button k-button-icontext"
                    role="button" tabindex="3"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="4"
                    aria-disabled="false" onclick='clearShellScriptForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>

        </div>
    </form>
</div>


    <div class="row">
        <div class="col-md-6" id="gridContainer">
            <table id="flex1" style="display:none"></table>
        </div>
        <div class="col-md-6">
                <textarea class="k-textbox" id="evaluatedScript" name="evaluatedScript"
                          placeholder="Evaluated Shell Script shown here"></textarea>
        </div>
    </div>
