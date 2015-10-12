<script type="text/javascript">
    // @todo-Azam to load grid without round trip
    var modelJsonForCurrency = ${modelJson};
    var listCurrencyModel = modelJsonForCurrency.currencyListJSON ? modelJsonForCurrency.currencyListJSON : false;

    $(document).ready(function () {
        onLoadCurrency();
    });

    function onLoadCurrency() {
        // initialize form with kendo validator & bind onSubmit event
        initializeForm($("#currencyForm"), onSubmitCurrency);

        // update page title
        $(document).attr('title', "MIS - Create currency");
        loadNumberedMenu(MENU_ID_APPLICATION, "#currency/show");
    }

    function executePreCondition() {
        if (!validateForm($("#currencyForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitCurrency() {

        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        // Spinner Show on AJAX Call
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'currency', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'currency', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#currencyForm").serialize(),
            url: actionUrl,
            success: function (data, textStatus) {
                executePostCondition(data);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
            },
            complete: function (XMLHttpRequest, textStatus) {
                setButtonDisabled($('#create'), false);
                // Spinner Hide on AJAX Call
                onCompleteAjaxCall();
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

                    var previousTotal = parseInt(listCurrencyModel.total);
                    var firstSerial = 1;

                    if (listCurrencyModel.rows.length > 0) {
                        firstSerial = listCurrencyModel.rows[0].cell[0];
                        regenerateSerial($(listCurrencyModel.rows), 0);
                    }
                    newEntry.cell[0] = firstSerial;

                    listCurrencyModel.rows.splice(0, 0, newEntry);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        listCurrencyModel.rows.pop();
                    }

                    listCurrencyModel.total = ++previousTotal;
                    $("#flex1").flexAddData(listCurrencyModel);

                } else if (newEntry != null) { // updated existing
                    updateListModel(listCurrencyModel, newEntry, 0);
                    $("#flex1").flexAddData(listCurrencyModel);
                }

                resetForm();
                showSuccess(result.message);

            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetForm() {
        setButtonDisabled($('#create'), false);
        clearForm($("#currencyForm"), $('#name'));
        $("#create").html("<span class='k-icon k-i-plus'></span>Create");
    }

    //-------------
    //-------------

    $("#flex1").flexigrid
    (
            {
                //flexigrid url is false due to remove round trip
                url: false,
                dataType: 'json',
                colModel: [
                    {display: "Serial", name: "serial", width: 30, sortable: false, align: "right"},
                    {display: "ID", name: "id", width: 30, sortable: false, align: "right", hide: true},
                    {display: "Name", name: "name", width: 180, sortable: true, align: "left"},
                    {display: "Symbol", name: "symbol", width: 100, sortable: true, align: "left"}
                ],
                buttons: [
                    {name: 'Edit', bclass: 'edit', onpress: editCurrency},
                    {name: 'Delete', bclass: 'delete', onpress: deleteCurrency},
                    {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                    {separator: true}
                ],
                searchitems: [
                    {display: "Name", name: "name", width: 180, sortable: true, align: "left"},
                    {display: "Symbol", name: "symbol", width: 180, sortable: true, align: "left"}
                ],
                sortname: "id",
                sortorder: "desc",
                usepager: true,
                singleSelect: true,
                title: 'All Currencies',
                useRp: true,
                rp: 15,
                showTableToggleBtn: false,
                height: getGridHeight() - 20,
                //afterAjax: showLoadingSpinner(false),
                preProcess: onLoadCurrencyListJSON // @todo-Azam to load grid without round trip
            }
    );

    // @todo-Azam to load grid without round trip
    // storing currency list grid JSON for future reference
    function onLoadCurrencyListJSON(data) {
        listCurrencyModel = data;
        return data;
    }


    <%-- reloading grid data --%>
    function reloadGrid(com, grid) {
        if (com == 'Clear Results') {
            $('#flex1').flexOptions({query: ''}).flexReload();
        }
    }

    <%-- Start : Delete operation of Currency --%>
    function deleteCurrency(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }

        showLoadingSpinner(true); // Spinner Show on AJAX Call

        var currencyId = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller: 'currency', action: 'delete')}?id=" + currencyId,
            success: executePostConditionForDelete,
            complete: onCompleteAjaxCall,	// Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'currency') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected Currency?')) {
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
            resetForm();
            $('#flex1').decreaseCount(1);
            showSuccess(data.message);
            // @todo-Azam managing grid to minimize round-trip
            listCurrencyModel.total = parseInt(listCurrencyModel.total) - 1;
            removeEntityFromGridRows(listCurrencyModel, selectedRow);

        } else {
            // show delete error
            showError(data.message);
        }
    }

    <%-- End: Delete operation of Currency--%>



    <%-- Start: Edit Currency --%>

    function editCurrency(com, grid) {
        clearForm($("#currencyForm"));
        if (executeCommonPreConditionForSelect($('#flex1'), 'currency') == false) {
            return;
        }

        showLoadingSpinner(true);	// Spinner Show on AJAX Call
        var currencyId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller: 'currency', action: 'edit')}?id=" + currencyId,
            success: executePostConditionForEdit,
            complete: onCompleteAjaxCall,	// Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConditionForEdit(data) {
        if (data.entity == null) {
            showError(result.message);
        } else {
            showCurrency(data);
        }
    }

    function showCurrency(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#name').val(entity.name);
        $('#symbol').val(entity.symbol);
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");
    }

    <%-- End: Edit operation --%>




    // @todo-Azam to load grid without round trip
    var strUrl = "${createLink(controller: 'currency', action: 'list')}";
    $("#flex1").flexOptions({url: strUrl});

    if (listCurrencyModel) {
        $("#flex1").flexAddData(listCurrencyModel);
    }

</script>
