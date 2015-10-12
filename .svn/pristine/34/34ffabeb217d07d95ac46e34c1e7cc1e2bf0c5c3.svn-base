package com.athena.mis.exchangehouse.taglib

import com.athena.mis.exchangehouse.actions.taglib.GetDropDownExhCurrencyConversionTagLibActionService
import com.athena.mis.utility.Tools

class ExhCurrencyConversionDropDownTagLib {
	static namespace = "exh"

	GetDropDownExhCurrencyConversionTagLibActionService getDropDownExhCurrencyConversionTagLibActionService

	/**
	 * Render html select of currency conversion
	 * example: <exh:dropDownCurrencyConversion name="currencyConversionId"}"></exh:dropDownCurrencyConversion>
	 *
	 * @attr name REQUIRED - name & id of html component
	 * @attr dataModelName REQUIRED - name of dataModel of Kendo dropdownList
	 * @attr class - css or validation class
	 * @attr tabindex - component tab index
	 * @attr onchange - on change event call
	 * @attr hintsText - No selection text (Default is Please Select...)
	 * @attr showHints - Hints-text will be shown (Default is 'true')
	 * @attr defaultValue - default value to be shown as selected (Default is '')
	 * @attr required - boolean value (true/false), if true append required
	 * @attr validationMessage - validation message to be shown (Default is 'Required')
	 */
	def dropDownCurrencyConversion = { attrs, body ->
		Map preResult = (Map) getDropDownExhCurrencyConversionTagLibActionService.executePreCondition(attrs, null)
		Boolean isError = preResult.isError
		if (isError.booleanValue()) {
			out << Tools.EMPTY_SPACE
			return
		}
		String html = getDropDownExhCurrencyConversionTagLibActionService.execute(preResult, null)
		out << html
	}
}
