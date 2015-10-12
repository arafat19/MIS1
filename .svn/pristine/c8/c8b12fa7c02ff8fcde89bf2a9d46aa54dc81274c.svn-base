<script type="text/javascript">

    var dbInstanceList, output, dropDownDbVendor;

    $(document).ready(function () {
        onLoadDbInstance();
    });

    function onLoadDbInstance() {
        output = ${output ? output : ''};

        initializeForm($('#dbInstanceForm'), onSubmitDbInstance);

        if (output.isError) {
            showError(output.message);                   // show error message in case of error
        } else {
            dbInstanceList = output.gridObj;               // set data in a global variable to populate
        }

        initFlexGrid();
        loadFlexGrid();
        $(document).attr('title', "DOC - Create DB Instance");
        loadNumberedMenu(MENU_ID_DOCUMENT, "#docDbInstance/show");
    }
    function setDriverName(){
        if(dropDownDbVendor.value()==''){
            $('#driver').val('');
            return false;
        }
        $('#driver').val(dropDownDbVendor.dataItem().value);
    }

    function executePreCondition() {
        // trim field vales before process.
        if (!validateForm($("#dbInstanceForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitDbInstance() {

        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        // Spinner Show on AJAX Call
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'docDbInstance', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'docDbInstance', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#dbInstanceForm").serialize(),
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
            var errors = $(result.errors);
            errors.each(function (i) {
                var err = $(this);
                var errStr = 'Error(s) occurred in some inputs';
                try {
                    if (err.length == 2) {
                        if ($("label[for='" + err[0] + "']").html() != null) {
                            errStr = $("label[for='" + err[0] + "']").html() + ' ' + err[1];
                        }
                    } else if (err.length == 1) {
                        errStr = err[0]
                    }
                } catch (e) { /** ignored */
                }
                showError(errStr);
            });

            showLoadingSpinner(false);
        } else {
            try {
                if ($('#id').val().isEmpty() && result.entity != null) { // newly created
                    var previousTotal = parseInt(dbInstanceList.total);

                    // re-arranging serial
                    var firstSerial = 1;
                    if (dbInstanceList.rows.length > 0) {
                        firstSerial = dbInstanceList.rows[0].cell[0];
                        regenerateSerial($(dbInstanceList.rows), 0);
                    }
                    result.entity.cell[0] = firstSerial;

                    dbInstanceList.rows.splice(0, 0, result.entity);

                    if ($('#flexDbInstance').countEqualsResultPerPage(previousTotal)) {
                        dbInstanceList.rows.pop();
                    }

                    dbInstanceList.total = ++previousTotal;
                    $("#flexDbInstance").flexAddData(dbInstanceList);

                } else if (result.entity != null) { // updated existing
                    updateListModel(dbInstanceList, result.entity, 0);
                    $("#flexDbInstance").flexAddData(dbInstanceList);
                }

                resetForm();
                showSuccess(result.message);

            } catch (e) {
            }
        }
    }

    function resetForm() {
        clearForm($("#dbInstanceForm"), $('#instanceName'));
        setButtonDisabled($('.save'), false);
        $("#create").html("<span class='k-icon k-i-plus'></span> Create");   // reset create button text
    }

    function initFlexGrid() {
        $("#flexDbInstance").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 30, sortable: false, align: "right"},
                        {display: "ID", name: "id", width: 30, sortable: false, align: "right", hide: true},
                        {display: "Vendor", name: "vendor", width: 150, sortable: false, align: "left"},
                        {display: "Instance Name", name: "name", width: 150, sortable: false, align: "left"},
                        {display: "Driver", name: "driver", width: 250, sortable: false, align: "left"},
                        {display: "Connection String", name: "connectionString", width: 350, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/docDbInstance/select,/docDbInstance/update">
                        {name: 'Edit', bclass: 'edit', onpress: editDbInstance},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/docDbInstance/delete">
                        {name: 'Delete', bclass: 'delete', onpress: deleteDbInstance},
                        </app:ifAllUrl>
                        {name: 'Show Result', bclass: 'clear-add', onpress: showResult},
                        {separator: true},
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid}
                    ],
                    searchitems: [
                        {display: "Instance Name", name: "instanceName", width: 180, sortable: false, align: "left"}
                    ],
                    sortname: "id",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All DB Instance List',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 20,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);
                    },
                    customPopulate: customPopulateGrid
                }
        );
    }

    function customPopulateGrid(data) {
        if (data.isError) {
            showError(data.message);
            dbInstanceList = getEmptyGridModel();
        } else {
            dbInstanceList = data.gridObj;
        }
        $("#flexDbInstance").flexAddData(dbInstanceList);
    }

    <%-- reloading grid data --%>
    function reloadGrid(com, grid) {
        $('#flexDbInstance').flexOptions({query: ''}).flexReload();
    }

    <%-- Start : Delete operation of DB Connection--%>
    function deleteDbInstance(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }

        showLoadingSpinner(true); // Spinner Show on AJAX Call

        var id = getSelectedIdFromGrid($('#flexDbInstance'));

        $.ajax({
            url: "${createLink(controller: 'docDbInstance', action: 'delete')}?id=" + id,
            success: executePostConditionForDelete,
            complete: onCompleteAjaxCall,    // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flexDbInstance'), 'DB Instance') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected DB Instance?')) {
            return false;
        }
        return true;
    }

    <%-- removing selected row and clean input form --%>
    function executePostConditionForDelete(data) {
        if (data.deleted == true) {
            var selectedRow = null;
            $('.trSelected', $('#flexDbInstance')).each(function (e) {
                selectedRow = $(this).remove();
            });
            resetForm();
            $('#flexDbInstance').decreaseCount(1);
            showSuccess(data.message);
            dbInstanceList.total = parseInt(dbInstanceList.total) - 1;
            removeEntityFromGridRows(dbInstanceList, selectedRow);

        } else {
            // show delete error
            showError(data.message);
        }
    }

    function editDbInstance(com, grid) {
        //clear form before putting edited value
        clearForm($("#dbInstanceForm"));

        if (executeCommonPreConditionForSelect($('#flexDbInstance'), 'DB Instance') == false) {
            return;
        }
        showLoadingSpinner(true);	// Spinner Show on AJAX Call
        var id = getSelectedIdFromGrid($('#flexDbInstance'));
        $.ajax({
            url: "${createLink(controller: 'docDbInstance', action: 'select')}?id="
                    + id,
            success: executePostConditionForEdit,
            complete: onCompleteAjaxCall,    // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConditionForEdit(data) {
        if (data.entity == null) {
            showError(result.message);
        } else {
            showDbInstance(data);
        }
    }

    function showDbInstance(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#instanceName').val(entity.instanceName);
        dropDownDbVendor.value(entity.vendorId);
        $('#driver').val(entity.driver);
        $('#connectionString').val(entity.connectionString);
        $('#sqlQuery').val(entity.sqlQuery);
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");
    }


    function loadFlexGrid() {
        var strUrl = "${createLink(controller: 'docDbInstance', action: 'list')}";
        $("#flexDbInstance").flexOptions({url: strUrl});
        if (dbInstanceList) {
            $("#flexDbInstance").flexAddData(dbInstanceList);
        }
    }


    function showResult() {
        if (executeCommonPreConditionForSelect($('#flexDbInstance'), 'DB Instance') == false) {
            return;
        }
        var id = getSelectedIdFromGrid($('#flexDbInstance'));
        var loc = "${createLink(controller: 'docDbInstance', action: 'showResult')}?dbInstanceId=" + id;
        $.history.load(formatLink(loc));
    }

</script>
