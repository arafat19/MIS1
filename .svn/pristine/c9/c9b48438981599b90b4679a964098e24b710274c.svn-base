<form id='rmsTaskBasicInfoForm' name='rmsTaskBasicInfoForm' class="form-horizontal form-widgets" role="form">
    <div class="panel-body">
        <input type="hidden" id="id" name="id"/>
        <input type="hidden" id="version" name="version"/>
        <input type="hidden" id="countryId" name="countryId"/>

        <div class="form-group">
            <label class="col-md-2 control-label label-required" for="exchangeHouseId">Exchange House:</label>

            <div class="col-md-3">
                <rms:dropDownExchangeHouse id="exchangeHouseId" name="exchangeHouseId" data_model_name="dropDownExchangeHouse"
                                           tabindex="1" required="required" validationmessage="Required"
                                           add_all_attributes="true" onchange="onChangeExchangeHouse();">
                </rms:dropDownExchangeHouse>
            </div>

            <div class="col-md-2 pull-left">
                <span class="k-invalid-msg" data-for="exchangeHouseId"></span>
            </div>
        </div>
        <div class="form-group">
            <label class="col-md-2 control-label label-required" for="refNo">Ref No.:</label>

            <div class="col-md-3">
                <input class="k-textbox" id="refNo" name="refNo" tabindex="2"
                       placeholder="Ref No" required validationMessage="Required"/>
            </div>

            <div class="col-md-2 pull-left">
                <span class="k-invalid-msg" data-for="refNo"></span>
            </div>
        </div>
        <div class="form-group">
            <label class="col-md-2 control-label label-required" for="valueDate">Date:</label>

            <div class="col-md-3">
                <app:dateControl name="valueDate" tabindex="3" placeholder="dd/MM/yyyy"
                                 required="true" validationMessage="Required">
                </app:dateControl>
            </div>

            <div class="col-md-2 pull-left">
                <span class="k-invalid-msg" data-for="valueDate"></span>
            </div>
        </div>
        <div class="form-group">
            <label class="col-md-2 control-label label-optional">Country:</label>

            <div class="col-md-3">
                <span id="spanCountry">N/A</span>
            </div>
        </div>
    </div>

</form>
