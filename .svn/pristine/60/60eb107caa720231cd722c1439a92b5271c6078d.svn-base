<script language="javascript" type="text/javascript">

    var dropDownCurrency;
    var output = false;
    var countryListModel = false;

    $(document).ready(function () {
        onLoadCountryPage();
    });

    function onLoadCountryPage() {
        // initialize form with kendo validator & bind onSubmit event
        initializeForm($("#countryForm"), onSubmitCountryForm);

        initGrid();
        output =${output ? output : ''};
        if (output.isError) {
            showError(data.message);
        } else {
            countryListModel = output.countryList;
        }

        populateFlex1();

        // update page title
        $(document).attr('title', "MIS - Create Country");
        loadNumberedMenu(MENU_ID_APPLICATION, "#country/show");

    }

    function executePreCondition() {
        if (!validateForm($("#countryForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitCountryForm() {
        if (executePreCondition() == false) {
            return false;
        }
        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'country', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'country', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#countryForm").serialize(),
            url: actionUrl,
            success: function (data, textStatus) {
                executePostCondition(data);
            },
            complete: function (XMLHttpRequest, textStatus) {
                setButtonDisabled($('#create'), false);
                showLoadingSpinner(false);
            },
            dataType: 'json'
        });
        return false;
    }


    function executePostCondition(result) {
        if (result.isError) {
            showError(result.message);
            showLoadingSpinner(false);
        } else {
            try {
                var newEntry = result.entity;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created

                    var previousTotal = parseInt(countryListModel.total);
                    var firstSerial = 1;

                    if (countryListModel.rows.length > 0) {
                        firstSerial = countryListModel.rows[0].cell[0];
                        regenerateSerial($(countryListModel.rows), 0);
                    }
                    newEntry.cell[0] = firstSerial;

                    countryListModel.rows.splice(0, 0, newEntry);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        countryListModel.rows.pop();
                    }

                    countryListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(countryListModel);

                } else if (newEntry != null) { // updated existing
                    updateListModel(countryListModel, newEntry, 0);
                    $("#flex1").flexAddData(countryListModel);
                }

                resetForm();
                showSuccess(result.message);

            } catch (e) {
                // Do Nothing
            }
        }
    }


    function resetForm() {
        clearForm($("#countryForm"), $("#name"));
        $("#create").html("<span class='k-icon k-i-plus'></span>Create");
    }

    function initGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 50, sortable: false, align: "right"},
                        {display: "ID", name: "id", width: 30, sortable: false, align: "right", hide: true},
                        {display: "Name", name: "name", width: 300, sortable: false, align: "left"},
                        {display: "Code", name: "code", width: 50, sortable: false, align: "center"},
                        {display: "ISD Code", name: "isdCode", width: 80, sortable: false, align: "center"},
                        {display: "Nationality", name: "nationality", width: 200, sortable: false, align: "left"},
                        {display: "Currency", name: "currency", width: 200, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/country/select,/country/update">
                        {name: 'Edit', bclass: 'edit', onpress: editCountry},
                        </app:ifAllUrl>
                        <sec:access url="/country/delete">
                        {name: 'Delete', bclass: 'delete', onpress: deleteCountry},
                        </sec:access>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],

                    searchitems: [
                        {display: "Name", name: "co.name", width: 180, sortable: true, align: "left"},
                        {display: "Currency", name: "cu.name", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "name",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'Country List',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 20,
                    customPopulate: customPopulateCountryGrid
                }
        );
    }

    function reloadGrid(com, grid) {
        if (com == 'Clear Results') {
            $('#flex1').flexOptions({query: ''}).flexReload();
        }
    }

    function customPopulateCountryGrid(data) {
        if (data.isError) {
            showError(data.message);
            countryListModel = getEmptyGridModel();
        } else {
            countryListModel = data;
        }
        $("#flex1").flexAddData(countryListModel);
        return false;
    }

    function editCountry(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'country') == false) {
            return;
        }
        resetForm();
        showLoadingSpinner(true);

        var countryId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'country', action: 'select')}?id=" + countryId,
            success: executePostConditionForEdit,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForEdit(ids) {
        if (ids.length == 0) {
            showError("Please select a country to edit the details");
            return false;
        }
        return true;
    }

    function executePostConditionForEdit(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            showCountry(data);
        }
    }

    function showCountry(data) {
        var entity = data.entity;
        $("#id").val(entity.id);
        $('#version').val(data.version);
        $("#name").val(entity.name);
        $("#code").val(entity.code);
        $("#isdCode").val(entity.isdCode);
        $("#nationality").val(entity.nationality);
        dropDownCurrency.value(entity.currencyId);
        $("#phoneNumberPattern").val(entity.phoneNumberPattern);
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");
    }

    function deleteCountry(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var countryId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'country', action:'delete')}?id=" + countryId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });

    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'country') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected country?')) {
            return false;
        }
        return true;
    }

    function executePostConditionForDelete(data) {

        if (data.deleted == true) {
            var selectedRow = null;
            $('.trSelected', $('#flex1')).each(function (e) {
                selectedRow = $(this).remove();
            });
            resetForm();
            $('#flex1').decreaseCount(1);
            showSuccess(data.message);
            countryListModel.total = parseInt(countryListModel.total) - 1;
            removeEntityFromGridRows(countryListModel, selectedRow);
        } else {
            showError(data.message);
        }
    }


    function populateFlex1() {
        <sec:access url="/country/list">
        var strUrl = "${createLink(controller:'country', action: 'list')}";
        $("#flex1").flexOptions({url: strUrl});

        if (countryListModel) {
            $("#flex1").flexAddData(countryListModel);
        }
        </sec:access>
    }


</script>