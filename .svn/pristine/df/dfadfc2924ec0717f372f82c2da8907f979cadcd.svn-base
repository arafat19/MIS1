<script type="text/javascript">
    var modelJsonForDistrict = ${modelJson};
    var listDistrictModel = modelJsonForDistrict.districtListJSON ? modelJsonForDistrict.districtListJSON : false;

    $(document).ready(function () {
        onLoadDistrict();
    });

    function onLoadDistrict() {
        initializeForm($('#districtForm'), onSubmitDistrict);
        $('#name').focus();

        initFlexGrid();
        loadFlexGrid();
        $(document).attr('title', "ARMS - Create district");
        loadNumberedMenu(MENU_ID_APPLICATION, "#district/show");
    }

    function executePreCondition() {
        if (validateForm($("#districtForm")) == false)
            return false;
        return true;
    }

    function onSubmitDistrict() {

        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('.save'), true);
        // Spinner Show on AJAX Call
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'district', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'district', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#districtForm").serialize(),
            url: actionUrl,
            success: function (data, textStatus) {
                executePostCondition(data);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
            },
            complete: function (XMLHttpRequest, textStatus) {
                setButtonDisabled($('.save'), false);
                // Spinner Hide on AJAX Call
                onCompleteAjaxCall();
            },
            dataType: 'json'
        });
        return false;
    }


    function executePostCondition(result) {

        if (result.isError) {
            if (result.message) {
                showError(result.message);
            }
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
            // @todo-Azam to load grid without round trip
            try {

                if ($('#id').val().isEmpty() && result.entity != null) { // newly created
                    var previousTotal = parseInt(listDistrictModel.total);

                    // re-arranging serial
                    var firstSerial = 1;
                    if (listDistrictModel.rows.length > 0) {
                        firstSerial = listDistrictModel.rows[0].cell[0];
                        regenerateSerial($(listDistrictModel.rows), 0);
                    }
                    result.entity.cell[0] = firstSerial;

                    listDistrictModel.rows.splice(0, 0, result.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        listDistrictModel.rows.pop();
                    }

                    listDistrictModel.total = ++previousTotal;
                    $("#flex1").flexAddData(listDistrictModel);

                } else if (result.entity != null) { // updated existing
                    updateListModel(listDistrictModel, result.entity, 0);
                    $("#flex1").flexAddData(listDistrictModel);
                }

                resetForm();
                showSuccess(result.message);

            } catch (e) {
                // @todo-Azam remove this alert before production
            }
            // @todo-Azam to add entity in grid without round trip, following line commented out
            //$("#flex1").flexReload();
        }
    }

    function resetForm() {
        clearForm($("#districtForm"), $('#name'));
        $("#create").html("<span class='k-icon k-i-plus'></span>Create");
    }


    function initFlexGrid() {
        $("#flex1").flexigrid
        (
                {
                    //flexigrid url is false due to remove round trip
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 30, sortable: false, align: "right"},
                        {display: "ID", name: "id", width: 30, sortable: false, align: "right", hide: true},
                        {display: "Name", name: "name", width: 180, sortable: true, align: "left"}
                    ],
                    buttons: [
                        {name: 'Edit', bclass: 'edit', onpress: editDistrict},
                        {name: 'Delete', bclass: 'delete', onpress: deleteDistrict},
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Name", name: "name", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Districts',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 20,
                    //afterAjax: showLoadingSpinner(false),
                    preProcess: onLoadDistrictListJSON
                }
        );
    }


    function onLoadDistrictListJSON(data) {
        listDistrictModel = data;
        return data;
    }

    function reloadGrid(com, grid) {
        if (com == 'Clear Results') {
            $('#flex1').flexOptions({query: ''}).flexReload();
        }
    }

    function deleteDistrict(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }

        showLoadingSpinner(true); // Spinner Show on AJAX Call

        var districtId = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller: 'district', action: 'delete')}?id=" + districtId,
            success: executePostConditionForDelete,
            complete: onCompleteAjaxCall,    // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'district') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected district?')) {
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
            // @todo-Azam managing grid to minimize round-trip
            listDistrictModel.total = parseInt(listDistrictModel.total) - 1;
            removeEntityFromGridRows(listDistrictModel, selectedRow);

        } else {
            // show delete error
            showError(data.message);
        }
    }

    function editDistrict(com, grid) {
        clearForm($("#districtForm"));
        if (executeCommonPreConditionForSelect($('#flex1'), 'district') == false) {
            return;
        }

        showLoadingSpinner(true);	// Spinner Show on AJAX Call
        var districtId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller: 'district', action: 'edit')}?id="
                    + districtId,
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
            showDistrict(data);
        }
    }

    function showDistrict(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#name').val(entity.name);
        $('#isGlobal').attr('checked',entity.isGlobal);
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");
    }


    function loadFlexGrid() {
        var strUrl = "${createLink(controller: 'district', action: 'list')}";
        $("#flex1").flexOptions({url: strUrl});
        if (listDistrictModel) {
            $("#flex1").flexAddData(listDistrictModel);
        }
    }

</script>
