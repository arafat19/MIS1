<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Create Agent Currency Posting
        </div>
    </div>
    <g:form name='agentCurrencyPostingForm' class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
        <g:hiddenField name="id"/>
        <g:hiddenField name="version"/>
        <div class="form-group">

            <div class="col-md-6">
                <div class="form-group">
                    <label class="col-md-3 control-label label-required" for="agentId">Agent:</label>
                    <div class="col-md-6">
                        <exh:dropDownAgent
                                name="agentId"
                                required="true"
                                validationMessage="Required"
                                dataModelName="dropDownAgent"
                                tabindex="1">
                        </exh:dropDownAgent>
                    </div>
                    <div class="col-md-3 pull-left">
                        <span class="k-invalid-msg" data-for="agentId"></span>
                    </div>
                </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="amount">Amount:</label>

                        <div class="col-md-6">
                            <input type="text" class="k-textbox" id="amount" name="amount" tabindex="2"
                                   placeholder="amount" required validationMessage="Required"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="amount"></span>
                        </div>
                    </div>



            </div>
        </div>
        </div>


        <div class="panel-footer">
          <app:ifAllUrl urls="/exhAgentCurrencyPosting/create,/exhAgentCurrencyPosting/update">
            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="3"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
            </button>  </app:ifAllUrl>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="4"
                    aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>

        </div>

    </g:form>
</div>
