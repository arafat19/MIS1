
<div id="application_top_panel" class="panel panel-primary">
	<div class="panel-heading">
		<div class="panel-title">
			Create Currency Conversion
		</div>
	</div>

    <g:form name='currencyConversionForm' class="form-horizontal form-widgets" role="form"
            mothod="POST"
            action="save">
	    <div class="panel-body">
	        <g:hiddenField name="id" value=""/>
	        <g:hiddenField name="version" value=""/>

		    <div class="col-md-12">
			    <div class="form-group">
				    <label class="col-md-2 control-label label-required" for="fromCurrency">From Currency:</label>

				    <div class="col-md-2">
					    <app:dropDownCurrency dataModelName="dropDownFromCurrency" name="fromCurrency" tabindex="1" required="true"></app:dropDownCurrency>
				    </div>

				    <div class="col-md-2 pull-left">
					    <span class="k-invalid-msg" data-for="fromCurrency"></span>
				    </div>

                    <label class="col-md-2 control-label label-required" for="buyRate">Buy Rate:</label>

                    <div class="col-md-2">
                        <input type="text" class="k-textbox" id="buyRate" name="buyRate" tabindex="3"
                               placeholder="" required validationMessage="Required"/>
                    </div>

                    <div class="col-md-2 pull-left">
                        <span class="k-invalid-msg" data-for="buyRate"></span>
                    </div>
			    </div>

			    <div class="form-group">
				    <label class="col-md-2 control-label label-required" for="toCurrency">To Currency:</label>

				    <div class="col-md-2">
					    <app:dropDownCurrency dataModelName="dropDownToCurrency" name="toCurrency" tabindex="2" required="true"></app:dropDownCurrency>
				    </div>

				    <div class="col-md-2 pull-left">
					    <span class="k-invalid-msg" data-for="toCurrency"></span>
				    </div>

                    <label class="col-md-2 control-label label-required" for="sellRate">Sell Rate:</label>

                    <div class="col-md-2">
                        <input type="text" class="k-textbox" id="sellRate" name="sellRate" tabindex="4"
                               placeholder="" required validationMessage="Required"/>
                    </div>

                    <div class="col-md-2 pull-left">
                        <span class="k-invalid-msg" data-for="sellRate"></span>
                    </div>

			    </div>
	        </div>
	    </div>
	    <div class="panel-footer">

		    <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
		            role="button" tabindex="4"
		            aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
		    </button>

		    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
		            class="k-button k-button-icontext" role="button" tabindex="5"
		            aria-disabled="false" onclick='resetCurrencyConversionForm();'><span class="k-icon k-i-close"></span>Cancel
		    </button>

	    </div>
    </g:form>
</div>