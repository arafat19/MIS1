<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            IOU Purpose for IOU Slip
        </div>
    </div>

    <form id="accIouPurposeForm" name="accIouPurposeForm" class="form-horizontal form-widgets" role="form">
        <input type="hidden" name="id" id="id"/>
        <input type="hidden" name="version" id="version"/>
        <input type="hidden" name="slipId" id="slipId"/>

        <div class="panel-body">
            <div class="form-group">
                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional">Project Name:</label>

                        <div class="col-md-9">
                            <span id="lblProjectName"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional">IOU Trace No:</label>

                        <div class="col-md-9">
                            <span id="accIouSlipId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="indentDetailsId">Purpose:</label>

                        <div class="col-md-6">
                            <select id='indentDetailsId'
                                    name="indentDetailsId"
                                    tabindex="1"
                                    required=""
                                    validationMessage="Required"
                                    onchange="populateItemDescription();">
                            </select>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="indentDetailsId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional">Purpose Details:</label>

                        <div class="col-md-9">
                            <span id="itemDescription"></span>
                        </div>
                    </div>
                </div>

                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional">Indent Trace No:</label>

                        <div class="col-md-9">
                            <span id="lblIndentTrace"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="amount">Amount:</label>

                        <div class="col-md-6">
                            <input type="text" class="" id="amount" name="amount"
                                   placeholder="Amount" required validationMessage="Required"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="amount"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="comments">Head Name:</label>

                        <div class="col-md-9">
                            <textarea class="k-textbox" id="comments" name="comments" tabindex="3" rows="3"></textarea>
                        </div>
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
                    aria-disabled="false" onclick='return resetCreateForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>
        </div>
    </form>
</div>