<script type="text/javascript">
    var dropDownExchangeHouse, numericAmountModel;
    var exhHouseCurPostingListModel = false;

    $(document).ready(function () {
        onLoadRmsExhHouseCurPostingPage();
    });

    // method called on page load
    function onLoadRmsExhHouseCurPostingPage() {
        var output =${output ? output : ''};

        initializeForm($("#rmsExhHouseCurPostingForm"), onSubmitRmsExhHouseCurPosting);

        if (output.isError) {
            showError(output.message);              // show error message in case of error
        } else {
            exhHouseCurPostingListModel = output.gridObj;        // set data in a global variable to populate
        }
        $("#lblBalance").text('');
        initFlex();
        populateFlex();
        $("#amount").kendoNumericTextBox({
            min: 1,
            decimals: 2,
            format: "0.00"
        });
        numericAmountModel = $("#amount").data("kendoNumericTextBox");
        $(document).attr('title', "ARMS - Create Exchange House Currency Posting");
        loadNumberedMenu(MENU_ID_ARMS, "#rmsExchangeHouseCurrencyPosting/show");
    }
    // method called  on submit of the form
    function onSubmitRmsExhHouseCurPosting() {
        if (executePreCondition() == false) {
            return false;
        }
        setButtonDisabled($('#create'), true);            // disable the save button
        showLoadingSpinner(true);                       // show loading spinner
        var actionUrl = null;
        // set link for create or update if there is data in hidden field id
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'rmsExchangeHouseCurrencyPosting', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'rmsExchangeHouseCurrencyPosting', action: 'update')}";
        }
        // fire ajax method for create or update
        jQuery.ajax({
            type: 'post',
            data: jQuery("#rmsExhHouseCurPostingForm").serialize(),             // serialize data from UI and send as parameter
            url: actionUrl,
            success: function (data) {
                executePostCondition(data);
            },
            complete: function () {
                setButtonDisabled($('#create'), false);           // enable the save button
                showLoadingSpinner(false);                      // stop loading spinner
            },
            dataType: 'json'
        });
        return false;
    }
    // check pre condition before submitting the form
    function executePreCondition() {
        // validate form data
        if (!validateForm($("#rmsExhHouseCurPostingForm"))) {
            return false;
        }
        return true;
    }
    // execute post condition after create or update
    function executePostCondition(result) {
        if (result.isError) {
            showError(result.message);              // show error message in case of error
            showLoadingSpinner(false);              // stop loading spinner
        }
        else {
            try {
                var newEntry = result;
                if ($('#id').val().isEmpty() && newEntry.entity != null) {      // show newly created object in a grid row

                    var previousTotal = parseInt(exhHouseCurPostingListModel.total);
                    var firstSerial = 1;

                    if (exhHouseCurPostingListModel.rows.length > 0) {
                        firstSerial = exhHouseCurPostingListModel.rows[0].cell[0];
                        regenerateSerial($(exhHouseCurPostingListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;

                    exhHouseCurPostingListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex').countEqualsResultPerPage(previousTotal)) {
                        exhHouseCurPostingListModel.rows.pop();
                    }

                    exhHouseCurPostingListModel.total = ++previousTotal;
                    $("#flex").flexAddData(exhHouseCurPostingListModel);

                } else if (newEntry.entity != null) {                           // updated existing object data in the grid
                    updateListModel(exhHouseCurPostingListModel, newEntry.entity, 0);
                    $("#flex").flexAddData(exhHouseCurPostingListModel);
                }
                resetForm();                        // reset the form
                showSuccess(result.message);        // show success message
                $('#exchangeHouseId').reloadMe();
            } catch (e) {
                // Do Nothing
            }
        }
    }
    // reset the form
    function resetForm() {
        clearForm($("#rmsExhHouseCurPostingForm"), dropDownExchangeHouse);  // clear errors & form values
        $("#create").html("<span class='k-icon k-i-plus'></span>Create");       // reset create button text
        $("#lblBalance").text('');
    }

    // initialize the grid
    function initFlex() {
        $("#flex").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 40, sortable: false, align: "right"},
                        {display: "Id", name: "id", width: 40, sortable: false, align: "right", hide: true},
                        {display: "Exchange House", name: "exchange_house_id", width: 300, sortable: true, align: "left"},
                        {display: "Amount", name: "amount", width: 120, sortable: false, align: "right"},
                        {display: "Created By", name: "created_by", width: 100, sortable: false, align: "left"},
                        {display: "Created On", name: "created_on", width: 100, sortable: false, align: "left"},
                        {display: "Updated By", name: "updated_by", width: 100, sortable: false, align: "left"},
                        {display: "Updated On", name: "updated_on", width: 100, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/rmsExchangeHouseCurrencyPosting/select,/rmsExchangeHouseCurrencyPosting/update">
                        {name: 'Edit', bclass: 'edit', onpress: selectRmsExhHouseCurPosting},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/rmsExchangeHouseCurrencyPosting/delete">
                        {name: 'Delete', bclass: 'delete', onpress: deleteRmsExhHouseCurPosting},
                        </app:ifAllUrl>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Exchange House", name: "eh.name", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "exchange_house_id",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Exchange House Currency Posting',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 25,
                    afterAjax: function () {
                        afterAjaxError();
                        showLoadingSpinner(false);
                    },
                    customPopulate: onLoadListJSON
                }
        );
    }
    // custom populate the grid
    function onLoadListJSON(data) {
        if (data.isError) {
            showError(data.message);
            exhHouseCurPostingListModel = getEmptyGridModel();
        } else {
            exhHouseCurPostingListModel = data;
        }
        $("#flex").flexAddData(exhHouseCurPostingListModel);
    }
    // select rmsExchangeHouseCurrencyPosting object for update
    function selectRmsExhHouseCurPosting(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex'), 'exchange house currency posting') == false) {
            return;
        }
        resetForm();                    // reset the form
        showLoadingSpinner(true);       // start loading spinner
        var id = getSelectedIdFromGrid($('#flex'));
        // fire ajax method for select
        $.ajax({
            url: "${createLink(controller:'rmsExchangeHouseCurrencyPosting', action: 'select')}?id=" + id,
            success: executePostConditionForEdit,
            complete: function () {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    // execute post condition for edit
    function executePostConditionForEdit(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            showRmsExhHouseCurPosting(data);
        }
    }
    // show property of selected rmsExchangeHouseCurrencyPosting object on UI
    function showRmsExhHouseCurPosting(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        dropDownExchangeHouse.value(entity.exchangeHouseId);
        $("#lblBalance").text("BDT " + dropDownExchangeHouse.dataItem().balance);
        numericAmountModel.value(entity.amount);
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");
    }
    // delete rmsExchangeHouseCurrencyPosting object
    function deleteRmsExhHouseCurPosting(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var rmsExhHouseCurPostingId = getSelectedIdFromGrid($('#flex'));
        // fire ajax method for delete
        $.ajax({
            url: "${createLink(controller:'rmsExchangeHouseCurrencyPosting', action:  'delete')}?id=" + rmsExhHouseCurPostingId,
            success: executePostConditionForDelete,
            complete: function () {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }
    // execute pre condition for delete
    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex'), 'exchange house currency posting') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected exchange house currency posting?')) {
            return false;
        }
        return true;
    }
    // removing selected row and clean input form
    function executePostConditionForDelete(data) {
        if (data.isError == false) {
            var selectedRow = null;
            $('.trSelected', $('#flex')).each(function (e) {
                selectedRow = $(this).remove();
            });
            resetForm();
            $('#flex').decreaseCount(1);
            showSuccess(data.message);
            exhHouseCurPostingListModel.total = parseInt(exhHouseCurPostingListModel.total) - 1;
            removeEntityFromGridRows(exhHouseCurPostingListModel, selectedRow);
            $('#exchangeHouseId').reloadMe();
        } else {
            showError(data.message);
        }
    }
    // reload grid
    function reloadGrid(com, grid) {
        $('#flex').flexOptions({query: ''}).flexReload();
    }
    // set link to grid url to populate data
    function populateFlex() {
        var strUrl = "${createLink(controller:'rmsExchangeHouseCurrencyPosting',action:  'list')}";
        $("#flex").flexOptions({url: strUrl});

        if (exhHouseCurPostingListModel) {
            $("#flex").flexAddData(exhHouseCurPostingListModel);
        }
    }
    function showBalance() {

        if (dropDownExchangeHouse.dataItem().balance == null) {
            $("#lblBalance").text('');
            return;
        }
        $("#lblBalance").text("BDT " + dropDownExchangeHouse.dataItem().balance);
        return false;
    }

    function onChangeExchangeHouse() {
        $("#lblBalance").text("BDT " + dropDownExchangeHouse.dataItem().balance);
    }

</script>