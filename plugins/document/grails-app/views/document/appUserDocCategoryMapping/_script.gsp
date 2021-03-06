<script language="javascript">

    var dropDownAppUser, categoryID, categoryLabel;

    var appUserCategoryMappingListModel = false;
    $(document).ready(function () {
        onLoadAppUserCategoryMappingPage();
    });

    function onLoadAppUserCategoryMappingPage() {

        // initialize form with kendo validator & bind onSubmit event
        initializeForm($("#userCategoryMappingForm"), onSubmitAppUserCategoryMapping);

        var output =${output ? output : ''};

        if (output.isError) {
            showError(output.message);
        } else {
            appUserCategoryMappingListModel = output.gridObj;
            categoryID = output.categoryId;
            categoryLabel = output.categoryLabel;
            $('#categoryId').val(categoryID);
            $('#categoryName').text(output.categoryName);
            $("#categoryLabel").html(categoryLabel);
            $("#categoryLabel1").html(categoryLabel);

        }

        initFlexGrid();
        loadFlexGrid();

        // update page title
        $(document).attr('title', "MIS - Create Member");
        loadNumberedMenu(MENU_ID_DOCUMENT, "#docCategory/show");

    }

    function executePreCondition() {
        if (!validateForm($("#userCategoryMappingForm"))) {
            return false;
        }

        return true;
    }

    function onSubmitAppUserCategoryMapping() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'docCategoryUserMapping', action: 'createForCategory')}";
        } else {
            actionUrl = "${createLink(controller:'docCategoryUserMapping', action: 'updateForCategory')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#userCategoryMappingForm").serialize(),
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


    function executePostCondition(result) {
        if (result.isError) {
            showError(result.message);
            showLoadingSpinner(false);
        } else {
            try {
                var newEntry = result;
                if ($('#id').val().isEmpty() && newEntry.entity != null) { // newly created
                    var previousTotal = parseInt(appUserCategoryMappingListModel.total);
                    var firstSerial = 1;

                    if (appUserCategoryMappingListModel.rows.length > 0) {
                        firstSerial = appUserCategoryMappingListModel.rows[0].cell[0];
                        regenerateSerial($(appUserCategoryMappingListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;

                    appUserCategoryMappingListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flexUserCategory').countEqualsResultPerPage(previousTotal)) {
                        appUserCategoryMappingListModel.rows.pop();
                    }

                    appUserCategoryMappingListModel.total = ++previousTotal;
                    $("#flexUserCategory").flexAddData(appUserCategoryMappingListModel);

                    resetFormForCreate();
                } else if (newEntry.entity != null) { // updated existing
                    updateListModel(appUserCategoryMappingListModel, newEntry.entity, 0);
                    $("#flexUserCategory").flexAddData(appUserCategoryMappingListModel);

                    resetFormForCreate();
                }
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetFormForCreate() {
        clearForm($("#userCategoryMappingForm"), dropDownAppUser);
        $('#id').val('');
        $('#categoryId').val(categoryID);
        $('#isCategoryManager').attr('checked', false);
        $('#userId').attr('default_value', '');
        $('#userId').reloadMe();
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function resetForm() {
        clearForm($("#userCategoryMappingForm"), dropDownAppUser);
        // re-assign dropDown for cancel action.
        $('#userId').attr('default_value', '');
        $('#userId').reloadMe();
        $('#categoryId').val(categoryID);
        $('#isCategoryManager').attr('checked', false);
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }
    function initFlexGrid() {
        $("#flexUserCategory").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 80, sortable: false, align: "right"},
                        {display: "Id", name: "id", width: 80, sortable: false, align: "right", hide: true},
                        {display: "Member", name: "user", width: 400, sortable: false, align: "left"},
                        {display: "Manager", name: "isManager", width: 200, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/docCategoryUserMapping/selectForCategory,/docCategoryUserMapping/updateForCategory">
                        {name: 'Edit', bclass: 'edit', onpress: selectAppUserCategoryMapping},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/docCategoryUserMapping/deleteForCategory">
                        {name: 'Delete', bclass: 'delete', onpress: deleteAppUserCategoryMapping},
                        </app:ifAllUrl>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],

                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Added Member List',
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

    function reloadGrid(com, grid) {
        if (com == 'Clear Results') {
            $('#flexUserCategory').flexOptions({query: ''}).flexReload();
        }
    }

    function customPopulateGrid(data) {
        if (data.isError) {
            showError(data.message);
            appUserCategoryMappingListModel = getEmptyGridModel();
        } else {
            appUserCategoryMappingListModel = data.gridObj;
        }
        $("#flexUserCategory").flexAddData(appUserCategoryMappingListModel);
    }

    function deleteAppUserCategoryMapping(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var appUserCategoryId = getSelectedIdFromGrid($('#flexUserCategory'));

        $.ajax({
            url: "${createLink(controller:'docCategoryUserMapping', action:  'deleteForCategory')}?id=" + appUserCategoryId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flexUserCategory'), ' member') == false) {
            return false;
        }
        if (!confirm('Are you sure to delete the selected member?')) {
            return false;
        }
        return true;
    }

    <%-- removing selected row and clean input form --%>
    function executePostConditionForDelete(data) {
        if (data.deleted == true) {
            var selectedRow = null;
            $('.trSelected', $('#flexUserCategory')).each(function (e) {
                selectedRow = $(this).remove();
            });
            resetForm();
            $('#flexUserCategory').decreaseCount(1);
            showSuccess(data.message);
            appUserCategoryMappingListModel.total = parseInt(appUserCategoryMappingListModel.total) - 1;
            removeEntityFromGridRows(appUserCategoryMappingListModel, selectedRow);

        } else {
            showError(data.message);
        }
    }

    function selectAppUserCategoryMapping(com, grid) {
        if (executeCommonPreConditionForSelect($('#flexUserCategory'), ' member') == false) {
            return;
        }

        dropDownAppUser.value('');
        showLoadingSpinner(true);
        var appUserCategoryMappingId = getSelectedIdFromGrid($('#flexUserCategory'));
        $.ajax({
            url: "${createLink(controller:'docCategoryUserMapping', action: 'selectForCategory')}?id="
                    + appUserCategoryMappingId,
            success: executePostConditionForEdit,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConditionForEdit(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            showAppUserCategory(data);
        }
    }

    function showAppUserCategory(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#userId').attr('default_value', entity.userId);
        $('#userId').reloadMe();
        $('#isCategoryManager').attr('checked', entity.isCategoryManager);

        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }
    function loadFlexGrid() {
        var strUrl = "${createLink(controller:'docCategoryUserMapping',action:  'listForCategory')}?categoryId=" + categoryID;
        $("#flexUserCategory").flexOptions({url: strUrl});
        if (appUserCategoryMappingListModel) {
            $("#flexUserCategory").flexAddData(appUserCategoryMappingListModel);
        }
    }

</script>
