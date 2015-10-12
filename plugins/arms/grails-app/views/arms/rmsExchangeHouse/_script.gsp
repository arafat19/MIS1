<script type="text/javascript">
    var dropDownCountry, entityTypeId;
    var exchangeHouseListModel = false;

    $(document).ready(function () {
        onLoadRmsExchangeHousePage();
    });

    // method called on page load
    function onLoadRmsExchangeHousePage() {
        var output =${output ? output : ''};

        initializeForm($("#rmsExchangeHouseForm"), onSubmitRmsExchangeHouse);
        entityTypeId = $('#hidEntityType').val();
        if (output.isError) {
            showError(output.message);              // show error message in case of error
        } else {
            exchangeHouseListModel = output.gridObj;        // set data in a global variable to populate
        }
        initFlex();
        populateFlex();
        $(document).attr('title', "ARMS - Create Exchange House");
        loadNumberedMenu(MENU_ID_ARMS, "#rmsExchangeHouse/show");
    }
    // method called  on submit of the form
    function onSubmitRmsExchangeHouse() {
        if (executePreCondition() == false) {
            return false;
        }
        setButtonDisabled($('#create'), true);            // disable the save button
        showLoadingSpinner(true);                       // show loading spinner
        var actionUrl = null;
        // set link for create or update if there is data in hidden field id
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'rmsExchangeHouse', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'rmsExchangeHouse', action: 'update')}";
        }
        // fire ajax method for create or update
        jQuery.ajax({
            type: 'post',
            data: jQuery("#rmsExchangeHouseForm").serialize(),             // serialize data from UI and send as parameter
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
        if (!validateForm($("#rmsExchangeHouseForm"))) {
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

                    var previousTotal = parseInt(exchangeHouseListModel.total);
                    var firstSerial = 1;

                    if (exchangeHouseListModel.rows.length > 0) {
                        firstSerial = exchangeHouseListModel.rows[0].cell[0];
                        regenerateSerial($(exchangeHouseListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;

                    exchangeHouseListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex').countEqualsResultPerPage(previousTotal)) {
                        exchangeHouseListModel.rows.pop();
                    }

                    exchangeHouseListModel.total = ++previousTotal;
                    $("#flex").flexAddData(exchangeHouseListModel);

                } else if (newEntry.entity != null) {                           // updated existing object data in the grid
                    updateListModel(exchangeHouseListModel, newEntry.entity, 0);
                    $("#flex").flexAddData(exchangeHouseListModel);
                }

                resetForm();                        // reset the form
                showSuccess(result.message);        // show success message

            } catch (e) {
                // Do Nothing
            }
        }
    }
    // reset the form
    function resetForm() {
        clearForm($("#rmsExchangeHouseForm"), $('#name'));  // clear errors & form values
        $("#create").html("<span class='k-icon k-i-plus'></span>Create");       // reset create button text
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
                        {display: "Name", name: "name", width: 300, sortable: true, align: "left"},
                        {display: "Code", name: "code", width: 100, sortable: false, align: "left"},
                        {display: "Country", name: "countryId", width: 120, sortable: false, align: "left"},
                        {display: "Balance", name: "balance", width: 120, sortable: false, align: "right"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/rmsExchangeHouse/select,/rmsExchangeHouse/update">
                        {name: 'Edit', bclass: 'edit', onpress: selectRmsExchangeHouse},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/rmsExchangeHouse/delete">
                        {name: 'Delete', bclass: 'delete', onpress: deleteRmsExchangeHouse},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/appUserEntity/show">
                        {name: 'User', bclass: 'creatCustomeruser', onpress: viewUser},
                        </app:ifAllUrl>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Name", name: "name", width: 180, sortable: true, align: "left"},
                        {display: "Code", name: "code", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "name",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Exchange House',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 10,
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
            exchangeHouseListModel = getEmptyGridModel();
        } else {
            exchangeHouseListModel = data;
        }
        $("#flex").flexAddData(exchangeHouseListModel);
    }
    // select rmsExchangeHouse object for update
    function selectRmsExchangeHouse(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex'), 'exchange house') == false) {
            return;
        }
        resetForm();                    // reset the form
        showLoadingSpinner(true);       // start loading spinner
        var id = getSelectedIdFromGrid($('#flex'));
        // fire ajax method for select
        $.ajax({
            url: "${createLink(controller:'rmsExchangeHouse', action: 'select')}?id=" + id,
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
            showRmsExchangeHouse(data);
        }
    }
    // show property of selected rmsExchangeHouse object on UI
    function showRmsExchangeHouse(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#name').val(entity.name);
        $('#code').val(entity.code);
        dropDownCountry.value(entity.countryId);
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");
    }
    // delete rmsExchangeHouse object
    function deleteRmsExchangeHouse(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var rmsExchangeHouseId = getSelectedIdFromGrid($('#flex'));
        // fire ajax method for delete
        $.ajax({
            url: "${createLink(controller:'rmsExchangeHouse', action:  'delete')}?id=" + rmsExchangeHouseId,
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
        if (executeCommonPreConditionForSelect($('#flex'), 'exchange house') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected exchange house?')) {
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
            exchangeHouseListModel.total = parseInt(exchangeHouseListModel.total) - 1;
            removeEntityFromGridRows(exchangeHouseListModel, selectedRow);
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
        var strUrl = "${createLink(controller:'rmsExchangeHouse',action:  'list')}";
        $("#flex").flexOptions({url: strUrl});

        if (exchangeHouseListModel) {
            $("#flex").flexAddData(exchangeHouseListModel);
        }
    }

    function viewUser(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex'), 'exchange house') == false) {
            return;
        }
        showLoadingSpinner(true);
        var entityId = getSelectedIdFromGrid($('#flex'));
        var loc = "${createLink(controller: 'appUserEntity', action: 'show')}?entityTypeId=" + entityTypeId + "&entityId=" + entityId;
        $.history.load(formatLink(loc));
        return false;
    }

</script>