<script language="javascript" type="text/javascript">
    var output = false;
    var accTypeListModel = false;
    var orderId;

    $(document).ready(function () {
        onLoadAccTypePage();
    });

    // method called on page load
    function onLoadAccTypePage() {
        initializeForm($("#accTypeForm"), onSubmitAccType);

        $("#orderId").kendoNumericTextBox({
            min: 0,
            max: 999999999999.9,
            format: "#.#"
        });
        orderId = $("#orderId").data("kendoNumericTextBox");

        output = ${output ? output : ''};
        if (output.isError) {
            showError(data.message);   // show error message in case of error
        } else {
            accTypeListModel = output.accTypeList; // set data in a global variable to populate
        }
        initGrid();
        populateFlex1();

        //update page title
        $(document).attr('title', "MIS - Edit Account Type");
        loadNumberedMenu(MENU_ID_ACCOUNTING, "#accType/show");
    }

    // initialize the grid
    function initGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 30, sortable: false, align: "right"},
                        {display: "ID", name: "id", width: 30, sortable: false, hide: true, align: "right"},
                        {display: "System Account Type", name: "systemAccountType", width: 150, sortable: false, align: "left"},
                        {display: "Name", name: "name", width: 100, sortable: false, align: "left"},
                        {display: "Order Id", name: "orderId", width: 60, sortable: false, align: "right"},
                        {display: "Prefix", name: "prefix", width: 50, sortable: false, align: "left"},
                        {display: "Description", name: "description", width: 500, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/accType/select,/accType/update">
                        {name: 'Edit', bclass: 'edit', onpress: selectAccType},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/accType/delete">
                        {name: 'Delete', bclass: 'delete', onpress: deleteAccType},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/accType/list">
                        {name: 'Refresh', bclass: 'clear-results', onpress: reloadGrid},
                        </app:ifAllUrl>
                        {separator: true}
                    ],
                    sortname: "name",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'Account Type List',
                    useRp: true,
                    rp: 15,
                    rpOptions: [15, 20, 25, 30],
                    showTableToggleBtn: false,
                    height: getGridHeight() - 10,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);
                    },
                    customPopulate: populateGridForAccType
                }
        );
    }

    // execute pre condition for delete
    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'account type') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected account type?')) {
            return false;
        }
        return true;
    }

    <%-- removing selected row and clean input form --%>
    function executePostConditionForDelete(data) {
        if (data.deleted == true) {
            var selectedRow = null;
            $('.trSelected', $('#flex1')).each(function (e) {
                selectedRow = $(this).remove();
            });
            resetAccTypeForm();
            $('#flex1').decreaseCount(1);
            showSuccess(data.message);
            accTypeListModel.total = parseInt(accTypeListModel.total) - 1;
            removeEntityFromGridRows(accTypeListModel, selectedRow);
        } else {
            showError(data.message);
        }
    }

    // delete accType object
    function deleteAccType(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var accTypeId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'accType', action:'delete')}?id=" + accTypeId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    // select accType object for update
    function selectAccType(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), ' account type') == false) {
            return;
        }

        resetAccTypeForm();
        showLoadingSpinner(true);
        var accTypeId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'accType', action: 'select')}?id=" + accTypeId,
            success: executePostConditionForEdit,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    // reset the form
    function resetAccTypeForm() {
        clearForm($("#accTypeForm"), $('#name'));   // clear errors & form values
        $('#systemAccountType').text('');
    }

    // execute post condition for edit
    function executePostConditionForEdit(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            showAccType(data);
        }
    }

    // show property of accType object on UI
    function showAccType(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#name').val(entity.name);
        orderId.value(entity.orderId);
        $('#prefix').val(entity.prefix);
        $('#systemAccountType').text(entity.systemAccountType);
        $('#description').val(entity.description);
    }

    // reload grid
    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    // custom populate the grid
    function populateGridForAccType(data) {
        if (data.isError) {
            showError(data.message);
            accTypeListModel = getEmptyGridModel();
        } else {
            accTypeListModel = data.accTypeList;
        }
        $("#flex1").flexAddData(accTypeListModel);
    }

    // method called  on submit of the form
    function onSubmitAccType() {
        if (executePreCondition() == false) {
            return false;
        }
        setButtonDisabled($('#create'), true); // disable the save button
        // Spinner Show on AJAX Call
        showLoadingSpinner(true);
        var actionUrl = null;
        // set link for create or update if there is data in hidden field id
        if ($('#id').val().isEmpty()) {
            showError('Account type can only be updated, please select an account type to update from the grid');
        } else {
            actionUrl = "${createLink(controller:'accType', action: 'update')}";
        }

        // fire ajax method
        jQuery.ajax({
            type: 'post',
            data: jQuery("#accTypeForm").serialize(), // serialize data from UI and send as parameter
            url: actionUrl,
            success: function (data, textStatus) {
                executePostCondition(data);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
            },
            complete: function (XMLHttpRequest, textStatus) {
                setButtonDisabled($('#create'), false);   // enable the save button
                showLoadingSpinner(false);   // stop loading spinner
            },
            dataType: 'json'
        });
        return false;
    }

    // check pre condition before submitting the form
    function executePreCondition() {
        // validate form data
        if (!validateForm($("#accTypeForm"))) {
            return false;
        }
        return true;
    }

    // execute post condition after create or update
    function executePostCondition(result) {
        if (result.isError) {
            showError(result.message);   // show error message in case of error
            showLoadingSpinner(false);  // stop loading spinner
        } else {
            try {
                var newEntry = result.entity;
                if ($('#id').val().isEmpty() && newEntry != null) { // show newly created object in a grid row

                    var previousTotal = parseInt(accTypeListModel.total);
                    var firstSerial = 1;

                    if (accTypeListModel.rows.length > 0) {
                        firstSerial = accTypeListModel.rows[0].cell[0];
                        regenerateSerial($(accTypeListModel.rows), 0);
                    }
                    newEntry.cell[0] = firstSerial;
                    accTypeListModel.rows.splice(0, 0, newEntry);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        accTypeListModel.rows.pop();
                    }

                    accTypeListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(accTypeListModel);

                } else if (newEntry != null) { // updated existing object data in the grid
                    updateListModel(accTypeListModel, newEntry, 0);
                    $("#flex1").flexAddData(accTypeListModel);
                }

                resetAccTypeForm();    // reset the form
                showSuccess(result.message);  // show success message
            } catch (e) {
                // Do Nothing
            }
        }
    }

    // set link to grid url to populate data
    function populateFlex1() {
        var strUrl = "${createLink(controller:'accType', action: 'list')}";
        $("#flex1").flexOptions({url: strUrl});

        if (accTypeListModel) {
            $("#flex1").flexAddData(accTypeListModel);
        }
    }

</script>