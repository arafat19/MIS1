<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Create Exchange House Currency Posting
        </div>
    </div>

    <form id='rmsExhHouseCurPostingForm' name='rmsExhHouseCurPostingForm' class="form-horizontal form-widgets"
          role="form">
        <div class="panel-body">
            <input type="hidden" id="id" name="id"/>
            <input type="hidden" id="version" name="version"/>

            <div class="form-group">
                <label class="col-md-2 control-label label-required" for="exchangeHouseId">Exchange House:</label>

                <div class="col-md-3">
                    <rms:dropDownExchangeHouse id="exchangeHouseId" name="exchangeHouseId"
                                               data_model_name="dropDownExchangeHouse"
                                               tabindex="1" required="required" validationmessage="Required"
                                               add_balance="true"
                                               url="${createLink(controller: 'rmsExchangeHouse', action: 'reloadExchangeHouseDropDown')}"
                                               add_all_attributes="true" onchange="onChangeExchangeHouse();">
                    </rms:dropDownExchangeHouse>
                </div>

                <div class="col-md-2 pull-left">
                    <span class="k-invalid-msg" data-for="exchangeHouseId"></span>
                </div>

                <label class="col-md-1 control-label"
                       style="font-weight: bold;font-size: 13px;color: #0000CC">Balance:</label>

                <div class="col-md-2">
                    <span id="lblBalance" style="font-weight: bold;font-size: 13px;color: #0000CC"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-2 control-label label-required" for="amount">Amount:</label>

                <div class="col-md-3">
                    <input type="text" id="amount" name="amount" tabindex="2"
                           placeholder="Amount" required validationMessage="Required"/>
                </div>

                <div class="col-md-2 pull-left">
                    <span class="k-invalid-msg" data-for="amount"></span>
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