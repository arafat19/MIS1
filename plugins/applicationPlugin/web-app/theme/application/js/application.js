$(document).ready(function () {
    // for left menu click function
    $('#dockMenuBudget').click(function () {
        renderBudgetMenu(null, true);
    });
    $('#dockMenuProc').click(function () {
        renderProcurementMenu(null, true);
    });
    $('#dockMenuInv').click(function () {
        renderInventoryMenu(null, true);
    });
    $('#dockMenuAcc').click(function () {
        renderAccountingMenu(null, true);
    });
    $('#dockMenuQs').click(function () {
        renderQsMenu(null, true);
    });
    $('#dockMenuSettings').click(function () {
        renderApplicationMenu(null, true);
    });
    $('#dockMenuFixedAsset').click(function () {
        renderFixedAssetMenu(null, true);
    });
    $('#dockMenuExchangeHouse').click(function () {
        renderExchangeHouseMenu(null, true);
    });
    $('#dockMenuProjectTrack').click(function () {
        renderProjectTrackMenu(null, true);
    });
    $('#dockMenuArms').click(function () {
        renderArmsMenu(null, true);
    });
    $('#dockMenuSarb').click(function () {
        renderSarbMenu(null, true);
    });
    $('#dockMenuDocument').click(function () {
        renderDocumentMenu(null, true);
    });
});

// assign kendo validator & bind onSubmit event
function initializeForm(form, func) {
    $(form).kendoValidator({validateOnBlur: false});

    $(form).submit(function (e) {
        func();
        return false;
    });
}

// init date picker by kendo & mask by jQuery
function initDateControl(ctlDate, val) {
    var value = new Date(); // default value
    if (val == '') {
        value = '';
    }
    $(ctlDate).kendoDatePicker({
        format: "dd/MM/yyyy",
        value: value
    });
    ctlDate.mask('99/99/9999');
}

// return empty data source for kendo dropDown
function getKendoEmptyDataSource(dataModel, unselectedText) {
    var textMember = 'name'; // default value
    if (dataModel) {
        textMember = dataModel.options.dataTextField;
    }
    var unselectedData = new Object();
    unselectedData['id'] = '';
    unselectedData[textMember] = 'Please Select...'; // default value
    if (unselectedText)unselectedData[textMember] = unselectedText;
    return  new kendo.data.DataSource({ data: [unselectedData]});
}

function initKendoDropdown(control, paramTextMember, paramValueMember, paramDataSource) {
    if (!paramTextMember) paramTextMember = 'name';
    if (!paramValueMember) paramValueMember = 'id';
    if (!paramDataSource) paramDataSource = getKendoEmptyDataSource();

    $(control).kendoDropDownList({
        dataTextField: paramTextMember,
        dataValueField: paramValueMember,
        dataSource: paramDataSource
    });
    return $(control).data("kendoDropDownList");
}


function trimFormValues(form) {
    // iterate over all of the inputs for the form
    $(':input', form).each(function () {
        var type = this.type.toLowerCase();
        var tag = this.tagName.toLowerCase(); // normalize case

        // for inputs and textareas
        if (type == 'text' || type == 'hidden' || tag == 'textarea') {
            this.value = jQuery.trim(this.value);
        }
    });
}

function validateForm(form) {
    trimFormValues(form);
    var frmValidator = $(form).kendoValidator({
        validateOnBlur: false
    }).data("kendoValidator");
    if (!frmValidator.validate()) {
        return false;
    }
    return true;
}

function clearForm(frm, focusElement) {
    clearErrors(frm);
    clearFormValues(frm);
    $(focusElement).focus();
}

function clearErrors(form) {
    var frmValidator = $(form).kendoValidator({
        validateOnBlur: false
    }).data("kendoValidator");
    frmValidator.hideMessages();
}

function clearFormValues(form) {
    // iterate over all of the inputs for the form
    $(':input', form).each(function () {
        var type = this.type;
        var tag = this.tagName.toLowerCase(); // normalize case

        // password inputs, and textareas
        if (type == 'text' || type == 'password' || type == 'hidden' || tag == 'textarea' || type == 'email' || type == 'tel') {
            this.value = "";
        }

        // checkboxes and radios need to have their checked state cleared
        else if (type == 'checkbox' || type == 'radio')
            this.checked = false;

        // select elements need to have their 'selectedIndex' property set to -1
        else if (tag == 'select') {
            var dropDownKendo = $(this).data("kendoDropDownList");
            dropDownKendo.value('');
        }
    });

}

function setButtonDisabled(button, isEnable) {
    $(button).attr('disabled', isEnable);
}


function bindAutoLoadClass() {
    $('div.ui-layout-west a.autoload').click(function (e) {
        var href = $(this).attr('href');
        var currentUrl = document.location.href;
        var currentPage = currentUrl.substring(currentUrl.indexOf('#', 0), currentUrl.length);
        if (currentPage == href) {
            var reqUrl = href.substring(1, href.length);
            load(reqUrl);
        }
    });
}


function setTabId(tabid) {
    $("#accordion1").accordion({
        fillSpace: true, active: tabid
    });
}
function load(loc) {
    showLoadingSpinner(true);
    jQuery.ajax({type: 'post',
        url: loc,                      //+ "?ajaxid=" + new Date().getTime()
        success: function (data, textStatus) {
            $('#contentHolder').html(data);
            showLoadingSpinner(false);
        },
        complete: function (XMLHttpRequest, textStatus) {
            showLoadingSpinner(false);

        }
    });
}
function setFavicon() {
    var link = $("link[type='image/x-icon']").remove().attr("href");
    $('<link href="' + link + '" rel="shortcut icon" type="image/x-icon" />').appendTo('head');
}


function afterAjaxError(XMLHttpRequest, textStatus) {
    if ((XMLHttpRequest == undefined) || (XMLHttpRequest.status == 200)) {
        return false;
    }
    if (XMLHttpRequest.status == 0) {
        redirectToLogoutPage(); // defined in main.gsp
    } else if (XMLHttpRequest.status == 404) {
        redirectToLogoutPage(); // defined in main.gsp
        showLoadingSpinner(false);
    } else if (XMLHttpRequest.status == 500) {
        redirectToLogoutPage(); // defined in main.gsp
    } else if (textStatus == 'parsererror') {
        alert('Error.\nParsing JSON Request failed.');
    } else if (textStatus == 'timeout') {
        alert('Request Time out.');
    } else {
        alert('Unknow Error.\n' + XMLHttpRequest.responseText);
    }
    return false;
}

function showLoadingSpinner(show) {
    if (show) {
        $('#spinner').show();
    } else {
        $('#spinner').hide();
    }
}


function onCompleteAjaxCall(XMLHttpRequest, textStatus) {
    showLoadingSpinner(false);
}

// function trims the leading / for hash loading
function formatLink(link) {
    if (link.indexOf('/') == 0) {
        link = link.substring(1, link.length);
    }
    return link;
}


function showError(errDescrip) {
    if (errDescrip.length == 0) {
        return false;
    }
    $.jGrowl(errDescrip, 'error');
}

function showSuccess(successDescrip) {
    if (successDescrip.length == 0) {
        return false;
    }
    $.jGrowl(successDescrip, 'success');
}

function showInfo(errDescrip) {
    if (!errDescrip || errDescrip.length == 0) {
        return false;
    }
    $.jGrowl(errDescrip, 'info');
}

String.prototype.endsWith = function (str) {
    return (this.match(str + "$") == str)
}

function checkAmountValidity(value) {
    try {
        value = $.trim(value);
        if (value.length <= 0) {
            return false
        }
        // check amount pattern
        var regexForAmount = /^[0-9]\d{0,10}(\.\d{1,2})?$/;
        if ((isNaN(value)) || (value <= 0) || (regexForAmount.test(value) == false)) {
            return false;
        }

        return true;
    } catch (ex) {
        return false;
    }
}

String.prototype.isEmpty = function () {
    try {
        return ($.trim(this).length == 0);
    } catch (e) {
        return true;
    }
}

function isEmpty(val) {
    var val2;
    val2 = $.trim(val);
    return (val2.length == 0);
}

/*Function is responsible to refresh dropDown by url
 * 1. Extract all attributes
 * 2. build url parameter with attributes
 * 3. fire the url, receive html data & update div with data
 * */

$.fn.reloadMe = function (callbackFunc, customDivToUpdate) {
    var attributes = this.get(0).attributes;
    var params = "?" + attributes[0].name + "=" + attributes[0].value;
    for (var i = 1; i < attributes.length; i++) {
        params = params + "&" + attributes[i].name + "=" + encodeURIComponent(attributes[i].value);
    }
    var url = this.attr('url');
    var divToUpdate = (typeof customDivToUpdate != "undefined") ? $(customDivToUpdate) : $(this).parent().closest('div');
    var isDropDown = this.is('select');
    if (isDropDown) {
        $(this).prev('span.k-dropdown-wrap').find('span.k-icon').toggleClass('k-loading');
    } else {
        showLoadingSpinner(true);
    }
    jQuery.ajax({type: 'post', dataType: 'html', url: url + params,
        success: function (data, textStatus) {
            $(divToUpdate).html(data);
        },
        complete: function (XMLHttpRequest, textStatus) {
            if (isDropDown) {
                $(this).prev('span.k-dropdown-wrap').find('span.k-icon').toggleClass('k-loading');
            } else {
                showLoadingSpinner(false);
            }
            if (callbackFunc) callbackFunc();
        }
    });
};


$.fn.refreshDropDown = function (data, options) {
    try {
        var ctrlDropDown = this;
        ctrlDropDown.children().remove();

        options = $.extend({
            valueMember: 'id',
            textMember: 'name',
            addAllAttributes: false,
            unselectedValue: '-1',
            selectHints: 'Please Select...',
            defaultValue: -1
        }, options);
        var hasUnselected = options.unselectedValue.length;
        if (hasUnselected != undefined) { // if undefined then skip
            $($('<option></option>')).val(options.unselectedValue).html(options.selectHints).appendTo(ctrlDropDown);
        }

        $(data).each(function (e) {
            var opt = $($('<option></option>'));
            $.each(this, function (k, v) {
                if (k == options.textMember) opt.html(v);
                else if (k == options.valueMember) opt.val(v);
                else if (options.addAllAttributes) opt.attr(k, v);
            });
            opt.appendTo(ctrlDropDown);
        });

        // initial selection with default
        if (options.defaultValue) {
            ctrlDropDown.attr('value', options.defaultValue);
        }
    } catch (e) {

    }
};


function getGridHeight(numberOfGridBar) {
    var extraHieght = 0;
    if ($('#message_div').length) {
        extraHieght = $('#message_div').height() + 4;
    }
    var totalHeight = (27 * 2 + 31 + 8 + 2) + 5;
    if (numberOfGridBar != 3) {
        totalHeight += 27;
    }

    var gridHeight = $("#contentHolder").height() - ($("#application_top_panel").height() + 2 + totalHeight + extraHieght);
    return gridHeight;
}
function getFullGridHeight() {
    var totalHeight = (27 * 2 + 31 + 8 + 2) + 5;
    if ($('.tDiv').length) {
        totalHeight += 27;
    }
    var gridHeight = $("#contentHolder").height() - totalHeight;
    return gridHeight;
}

function getGridHeightKendo(ctl) {
    var height = $(ctl).parent('div').parent('div').height() - $(ctl).parent('div').prev('div').height();
    return height;
}

function clearGridKendo(gridModel) {
    gridModel.dataSource.data([]);
}

// function to check if one or more grid row is selected or not
function executeCommonPreConditionForSelect(flexGrid, objName, singleOnly) {
    var objectName = 'row';
    if (objName) {
        objectName = objName;
    }
    var ids = $('.trSelected', flexGrid);
    if (ids.length == 0) {
        showError("Please select " + objectName + " to perform this operation");
        return false;
    }
    if (singleOnly && (ids.length > 1)) {
        showError("Please select only one " + objectName + " to perform this operation");
        return false;
    }

    return true;
}

// function to check if one or more grid row is selected or not
function executeCommonPreConditionForSelectKendo(gridModel, objName, singleOnly) {
    var objectName = 'row';
    if (objName) {
        objectName = objName;
    }
    var row = gridModel.select();

    if (row.size() == 0) {
        showError("Please select " + objectName + " to perform this operation");
        return false;
    }
    if (singleOnly && (row.size() > 1)) {
        showError("Please select only one " + objectName + " to perform this operation");
        return false;
    }
    return true;
}

/* function returns objectId from grid row.
 In case of multi select return '_' separated ids
 */
function getSelectedIdFromGrid(flexGrid) {
    var objectId = '';
    var selectedIds = $('.trSelected', flexGrid);
    if (selectedIds.length == 1) {
        objectId = $(selectedIds[0]).attr('id').replace('row', '');
    } else if (selectedIds.length > 1) {
        selectedIds.each(function (e) {
            var singleId = $(this).attr('id').replace('row', '');
            objectId += singleId + '_';
        });
    }
    return objectId;
}

/* function returns objectId from grid row.
 In case of multi select return '_' separated ids
 */
function getSelectedIdFromGridKendo(gridModel) {
    var objectId = '';
    var row = gridModel.select();
    var data;
    if (row.size() == 1) {
        data = gridModel.dataItem(row);
        objectId = data.id;
    } else if (row.size() > 1) {
        row.each(function (e) {
            data = gridModel.dataItem($(this));
            objectId += data.id + '_';
        });
    }
    return objectId;
}
