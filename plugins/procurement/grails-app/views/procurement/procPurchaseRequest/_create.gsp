<div class="form-group">
    <div id="application_top_panel" class="panel panel-primary">
        <div class="panel-heading">
            <div class="panel-title">
                Create Purchase Request
            </div>
        </div>

        <form id="purchaseRequestForm" name="purchaseRequestForm" class="form-horizontal form-widgets"
              role="form">
            <div class="panel-body">
                <input type="hidden" name="id" id="id"/>
                <input type="hidden" name="version" id="version"/>
                <input type="hidden" name="budgetAvailableQuantity" id="budgetAvailableQuantity"/>

                <div class="form-group">
                    <div class="col-md-6">
                        <div class="form-group">
                            <label class="col-md-3 control-label label-required" for="projectId">Project:</label>

                            <div class="col-md-6">
                                <app:dropDownProject
                                        name="projectId"
                                        required="true"
                                        validationMessage="Required"
                                        dataModelName="dropDownProject"
                                        onchange="onChangeProject()"
                                        tabindex="1"
                                        addAllAttributes="true">
                                </app:dropDownProject>
                            </div>

                            <div class="col-md-3 pull-left">
                                <span class="k-invalid-msg" data-for="projectId"></span>
                            </div>
                        </div>

                        <div class="form-group">
                            <label class="col-md-3 control-label label-optional"
                                   for="indentId">Indent Trace No:</label>

                            <div class="col-md-6">
                                <select name="indentId"
                                        id="indentId"
                                        tabindex="1">
                                </select>
                            </div>
                        </div>
                    </div>

                    <div class="col-md-6">

                        <div class="form-group">
                            <label class="col-md-3 control-label label-optional"
                                   for="comments">Comments:</label>

                            <div class="col-md-9">
                                <textarea type="text" class="k-textbox" id="comments" name="comments" rows="2"
                                          placeholder="255 Char Max" tabindex="3"
                                          validationMessage="Short Description is Required"></textarea>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <app:ifAllUrl
                    urls="/procPurchaseRequest/create,/procPurchaseRequest/update, /procPurchaseRequest/select">
                <div class="panel-footer">

                    <button id="create" name="create" type="submit" data-role="button"
                            class="k-button k-button-icontext"
                            role="button" tabindex="4"
                            aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
                    </button>

                    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                            class="k-button k-button-icontext" role="button" tabindex="5"
                            aria-disabled="false" onclick='resetForm();'><span
                            class="k-icon k-i-close"></span>Cancel
                    </button>
                </div>
            </app:ifAllUrl>
        </form>

    </div>

    <div class="form-group">
        <table id="flex1" style="display:none"></table>
    </div>
</div>





