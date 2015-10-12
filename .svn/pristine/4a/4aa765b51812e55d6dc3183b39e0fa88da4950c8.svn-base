<script language="javascript">
    var dropDownAccType;
    var accTier1ListModel = false;

    $(document).ready(function () {
        onLoadAccTier1Page();
    });

    // method called on page load
    function onLoadAccTier1Page() {
        initializeForm($("#accTier1Form"), onSubmitAccTier1);

        var output =${output ? output : ''};
        if (output.isError) {
            showError(output.message);   // show error message in case of error
        } else {
            accTier1ListModel = output.lstTier1;    // set data in a global variable to populate
        }
        initFlex();
        populateFlex1();

        // update page title
        $(document).attr('title', "MIS - Create Tier-1");
        loadNumberedMenu(MENU_ID_ACCOUNTING, "#accTier1/show");
    }

    // check pre-condition before submitting the form
    function executePreCondition() {
        // validate form data
        if (!validateForm($("#accTier1Form"))) {
            return false;
        }
        return true;
    }

    // method called  on submitting the form
    function onSubmitAccTier1() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        // set link for update if there is data in hidden field id otherwise for create
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'accTier1', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'accTier1', action: 'update')}";
        }
        // fire ajax method
        jQuery.ajax({
            type: 'post',
            data: jQuery("#accTier1Form").serialize(),     // serialize data from UI and send as parameter
            url: actionUrl,
            success: function (data, textStatus) {
                executePostCondition(data);
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

    // execute post condition after create or update
    function executePostCondition(result) {
        if (result.isError) {
            showError(result.message);
            showLoadingSpinner(false);
        } else {
            try {
                var newEntry = result.accTier1;
                if ($('#id').val().isEmpty() && newEntry.entity != null) {  // show newly created object in a grid row

                    var previousTotal = parseInt(accTier1ListModel.total);
                    var firstSerial = 1;

                    if (accTier1ListModel.rows.length > 0) {
                        firstSerial = accTier1ListModel.rows[0].cell[0];
                        regenerateSerial($(accTier1ListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;
                    accTier1ListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        accTier1ListModel.rows.pop();
                    }

                    accTier1ListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(accTier1ListModel);

                } else if (newEntry.entity != null) {     // updated existing object data in the grid
                    updateListModel(accTier1ListModel, newEntry.entity, 0);
                    $("#flex1").flexAddData(accTier1ListModel);
                }

                resetFormForCreate();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    // clear the form
    function resetForm() {
        clearForm($("#accTier1Form"), dropDownAccType);
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    // clear the form & set the button name 'Create'
    function resetFormForCreate() {
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
        $('#isActive').attr('checked', false);
        $('#id').val('');
        $('#version').val('');
        $('#name').val('');
        $('#name').focus();
    }

    // initialize the grid
    function initFlex() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 30, sortable: false, align: "right"},
                        {display: "ID", name: "id", width: 30, sortable: false, align: "right", hide: true},
                        {display: "Name", name: "name", width: 180, sortable: true, align: "left"},
                        {display: "Account Type", name: "accTypeId", width: 180, sortable: false, align: "left"},
                        {display: "Active", name: "isActive", width: 180, sortable: true, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/accTier1/select,/accTier1/update">
                        {name: 'Edit', bclass: 'edit', onpress: selectAccTier1},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/accTier1/delete">
                        {name: 'Delete', bclass: 'delete', onpress: deleteAccTier1},
                        </app:ifAllUrl>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Name", name: "name", width: 180, sortable: true, align: "left"},
                        {display: "Account Type", name: "accType", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "name",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Tier-1',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 20,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);
                    },
                    preProcess: onLoadListJSON
                }
        );
    }

    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function onLoadListJSON(data) {
        if (data.isError) {
            showError(data.message);
            accTier1ListModel = null;
        } else {
            accTier1ListModel = data;
        }
        return data;
    }

    function deleteAccTier1(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var accTier1Id = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller:'accTier1', action:  'delete')}?id=" + accTier1Id,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'tier-1') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected tier-1?')) {
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
            accTier1ListModel.total = parseInt(accTier1ListModel.total) - 1;
            removeEntityFromGridRows(accTier1ListModel, selectedRow);

        } else {
            showError(data.message);
        }
    }
    // select tier1 object for update
    function selectAccTier1(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'tier-1') == false) {
            return;
        }

        resetForm();
        showLoadingSpinner(true);
        var inventoryId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'accTier1', action: 'select')}?id="
                    + inventoryId,
            success: executePostConditionForEdit,
            complete: function (XMLHttpRequest, textStatus) {
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
            showAccTier1(data);
        }
    }

    // show property of tier1 object on UI
    function showAccTier1(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#name').val(entity.name);
        dropDownAccType.value(entity.accTypeId);
        $('#isActive').attr('checked', entity.isActive);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    // set link to grid url to populate data
    function populateFlex1() {
        var strUrl = "${createLink(controller:'accTier1',action:  'list')}";
        $("#flex1").flexOptions({url: strUrl});

        if (accTier1ListModel) {
            $("#flex1").flexAddData(accTier1ListModel);
        }
    }

</script>
