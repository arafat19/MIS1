package com.athena.mis.exchangehouse.taglib

import com.athena.mis.exchangehouse.actions.taglib.GetDropDownRemittancePurposeTagLibActionService
import com.athena.mis.utility.Tools

class ExhRemittancePurposeDropDownTagLib {
	static namespace = "exh"

	 GetDropDownRemittancePurposeTagLibActionService getDropDownRemittancePurposeTagLibActionService

	/**
	 * Render html select of remittance purpose
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
	 *
	 */
	def dropDownRemittancePurpose = { attrs ->
		Map preResult = (Map) getDropDownRemittancePurposeTagLibActionService.executePreCondition(attrs, null)
		Boolean isError = preResult.isError
		if (isError.booleanValue()) {
			out << Tools.EMPTY_SPACE
			return
		}
		String html = getDropDownRemittancePurposeTagLibActionService.execute(preResult, null)
		out << html
	}
}
