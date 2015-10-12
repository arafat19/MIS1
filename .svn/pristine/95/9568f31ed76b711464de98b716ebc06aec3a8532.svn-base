<script type="text/javascript">

    var gridModelDesignationInfo;

    $(document).ready(function () {
        onLoadDesignationPage();
    });

    function onLoadDesignationPage() {
        // initialize form with kendo validator & bind onSubmit event
        initializeForm($('#designationForm'), onSubmitCustomer);

        var output =${output ? output : ''};
        if (output.isError) {
            showError(output.message);
            return;
        }

        gridModelDesignationInfo = output.gridObject;
        initFlex();
        setUrlDesignationGrid();

        // update page title
        $(document).attr('title', "MIS - Create Designation Information");
        loadNumberedMenu(MENU_ID_APPLICATION, "#designation/show");
    }

    function onSubmitCustomer() {
        if (!validateForm($('#designationForm'))) {
            return false;
        }
        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'designation', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'designation', action:  'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#designationForm").serialize(),
            url: actionUrl,
            success: function (data, textStatus) {
                executePostConForSubmitDesignation(data);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
            },
            complete: function (XMLHttpRequest, textStatus) {
                setButtonDisabled($('#create'), false);
                showLoadingSpinner(false);
            },
            dataType: 'json'
        });
        return false;
    }

    function executePostConForSubmitDesignation(data) {
        if (data.isError) {
            showError(data.message);
            showLoadingSpinner(false);
        } else {
            try {
                var newEntry = data.entity;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created

                    var previousTotal = parseInt(gridModelDesignationInfo.total);
                    var firstSerial = 1;

                    if (gridModelDesignationInfo.rows.length > 0) {
                        firstSerial = gridModelDesignationInfo.rows[0].cell[0];
                        regenerateSerial($(gridModelDesignationInfo.rows), 0);
                    }
                    newEntry.cell[0] = firstSerial;

                    gridModelDesignationInfo.rows.splice(0, 0, newEntry);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        gridModelDesignationInfo.rows.pop();
                    }

                    gridModelDesignationInfo.total = ++previousTotal;
                    $("#flex1").flexAddData(gridModelDesignationInfo);

                } else if (newEntry != null) { // updated existing
                    updateListModel(gridModelDesignationInfo, newEntry, 0);
                    $("#flex1").flexAddData(gridModelDesignationInfo);
                }

                clearDesignationForm();
                showSuccess(data.message);

            } catch (e) {
                // Do Nothing
            }
        }
    }

    function customPopulateDesignationGrid(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            gridModelDesignationInfo = data;
        }
        $("#flex1").flexAddData(gridModelDesignationInfo);
    }

    function editDesignation(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'designation') == false) {
            return;
        }
        showLoadingSpinner(true);
        var designationId = getSelectedIdFromGrid($('#flex1'));
        var url = "${createLink(controller: 'designation', action: 'select')}?id=" + designationId;
        $.ajax({
            url: url,
            success: executePostConForEdit,
            complete: onCompleteAjaxCall,
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConForEdit(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            showDesignationInfo(data);
        }
    }

    function showDesignationInfo(data) {
        clearDesignationForm();

        var designationEntity = data.entity;
        $('#id').val(designationEntity.id);
        $('#version').val(data.version);
        $('#name').val(designationEntity.name);
        $('#shortName').val(designationEntity.shortName);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function clearDesignationForm() {
        clearForm($("#designationForm"), $('#name'));
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'designation') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected designation?')) {
            return false;
        }
        return true;
    }

    function deleteDesignation(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var designationId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller: 'designation', action: 'delete')}?id=" + designationId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConditionForDelete(data) {
        if (data.isError) {
            showError(data.message);
            return;
        } else {
            var selectedRow = null;
            $('.trSelected', $('#flex1')).each(function (e) {
                selectedRow = $(this).remove();
            });
            clearDesignationForm();
            $('#flex1').decreaseCount(1);
            showSuccess(data.message);
            gridModelDesignationInfo.total = parseInt(gridModelDesignationInfo.total) - 1;
            removeEntityFromGridRows(gridModelDesignationInfo, selectedRow);
        }
    }

    function reloadDesignationGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function setUrlDesignationGrid() {
        var strUrl = "${createLink(controller:'designation', action: 'list')}";
        $("#flex1").flexOptions({url: strUrl});
        if (gridModelDesignationInfo) {
            $('#flex1').flexAddData(gridModelDesignationInfo);
        }
    }

    function initFlex() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 60, sortable: false, align: "left"},
                        {display: "Name", name: "name", width: 300, sortable: false, align: "left"},
                        {display: "Short Name", name: "short_name", width: 120, sortable: false, align: "left"}
                    ],
                    buttons: [
                        {name: 'Edit', bclass: 'edit', onpress: editDesignation},
                        {name: 'Delete', bclass: 'delete', onpress: deleteDesignation},
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadDesignationGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Name", name: "name", width: 180, sortable: true, align: "left"},
                        {display: "Short Name", name: "shortName", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "name",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Designation',
                    useRp: true,
                    rp: 15,
                    rpOptions: [15, 20, 25, 30],
                    showTableToggleBtn: false,
                    height: getGridHeight() - 20,
                    customPopulate: customPopulateDesignationGrid,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);
                    }
                }
        );
    }

</script>
