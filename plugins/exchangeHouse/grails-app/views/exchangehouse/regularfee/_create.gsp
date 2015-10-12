<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Update Regular Fee
        </div>
    </div>

    <form name="exhRegularForm" id="exhRegularForm" method="post" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <g:hiddenField name="id"/>
            <g:hiddenField name="version"/>
            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="logic">Logic:</label>

                <div class="col-md-6">
                    <textarea type="text" class="k-textbox" id="logic" name="logic" tabindex="1"
                              rows="20" cols="30" style="width: 500px"
                              maxlength="3000"></textarea>
                </div>

            </div>
        </div>

        <div class="panel-footer">
            <app:ifAllUrl urls="/exhRegularFee/update">
                <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                        role="button" tabindex="2"
                        aria-disabled="false"><span class="k-icon k-i-plus"></span>Update
                </button>
            </app:ifAllUrl>
        </div>
    </form>

    <div class="panel-heading">
        <div class="panel-title">
            Evaluate Regular Fee
        </div>
    </div>

    <form name="frmEvaluateLogic" id="frmEvaluateLogic" method="post" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <div class="form-group">
                <label class="col-md-2 control-label label-required" for="amount">Amount:</label>

                <div class="col-md-2">
                    <input type="text" class="k-textbox" id="amount" name="amount" tabindex="3"/>

                </div>

                <div class="col-md-2">
                    <button id="calculate" name="calculate" type="submit" data-role="button"
                            class="k-button k-button-icontext"
                            role="button" tabindex="4" aria-disabled="false">Evaluate</button>

                </div>
            </div>

            <div class="form-group">
                <label class="col-md-2 control-label label-optional">Regular Fee:</label>

                <div class="col-md-4">
                    <span id=lblResult></span>
                </div>
            </div>
        </div>
    </form>
</div>
