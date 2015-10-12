<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Create <span id="categoryLabel"></span>
        </div>
    </div>

    <form id='categoryForm' name='categoryForm' class="form-horizontal form-widgets" role="form" method="post">
        <div class="panel-body">
            <input type="hidden" name="id" id="id"/>
            <input type="hidden" name="version" id="version"/>

            <div class="form-group">
                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="name">Name:</label>

                        <div class="col-md-6">
                            <input type="text" class="k-textbox" id="name" name="name" tabindex="1"
                                   placeholder="Category Name" required validationMessage="Required"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="name"></span>
                        </div>
                    </div>

                    <div class="form-group">

                        <label class="col-md-3 control-label" for="description">Description:</label>

                        <div class="col-md-9">
                            <textarea type="text" class="k-textbox" id="description" name="description" tabindex="2"
                                      rows="3" placeholder="Category Description 255 Char Max"></textarea>
                        </div>
                    </div>

                </div>

                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label" for="url">URL:</label>
                        <span id="url" class="col-md-9">(Auto Generated)</span>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label" for="isActive">Active:</label>

                        <div class="col-md-1">
                            <g:checkBox tabindex="3" name="isActive" checked="true"/>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button name="create" id="create" type="submit" data-role="button"
                    class="k-button k-button-icontext"
                    role="button" tabindex="3"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span> Create
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="4"
                    aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>

        </div>
    </form>
</div>

