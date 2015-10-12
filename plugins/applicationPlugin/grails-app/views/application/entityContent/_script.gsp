<script type="text/javascript">
    var uploading;
    var output = false;
    var entityContentListModel = false;
    var dropDownContentTypeId, dropDownContentCategoryId, validatorAttachment, entityTypeId, entityId;

    $(document).ready(function () {
        onLoadEntityContentPage();
    });

    function onLoadEntityContentPage() {
        // common initializeForm() is not used here due to customValidation/upload
        initFormWithCustomRule();
        $("#contentObj").kendoUpload({
            multiple: false
        });
        dropDownContentCategoryId = initKendoDropdown($("#contentCategoryId"), null, null, null);

        output = ${output ? output : ''};
        if (output.isError) {
            showError(output.message);
        } else {
            entityContentListModel = output.entityContentList;
        }
        entityTypeId = output.entityTypeId;
        entityId = output.entityId;
        $("#entityTypeId").val(entityTypeId);
        $("#entityId").val(entityId);
        var entityContentMap = output.entityContentMap;
        $("#lblEntityTypeName").text(entityContentMap.entityTypeName);
        $("#lblEntityName").text(entityContentMap.entityName);
        $("#lblFormTitle").text(entityContentMap.panelTitle);
        var pluginId = entityContentMap.pluginId;
        var leftMenu = entityContentMap.leftMenu;

        bindFormEvents();
        initFlexContent();
        populateFlexGrid();

        // update page title
        $(document).attr('title', 'MIS - ' + entityContentMap.panelTitle);
        loadMenu(pluginId, leftMenu);
    }

    function initFormWithCustomRule() {
        validatorAttachment = $("#entityContentForm").kendoValidator({
            validateOnBlur: false,
            rules: {
                upload: function (input) {
                    if ((input[0].type == "file") && ($(input[0]).is('[validationMessage]'))) {
                        return input.closest(".k-upload").find(".k-file").length;
                    }
                    return true;
                }
            }
        }).data("kendoValidator");
    }

    function bindFormEvents() {
        var actionUrl = "${createLink(controller: 'entityContent',action: 'create')}";
        $("#entityContentForm").attr('action', actionUrl);

        $('#entityContentForm').iframePostForm({
            post: function () {
                uploading = true;
                showLoadingSpinner(true);
                setButtonDisabled($('#create'), true);
            },
            complete: function (response) {
                if (uploading == true) {
                    showLoadingSpinner(false);
                    executePostCondition(response);
                    uploading = false;
                    setButtonDisabled($('#create'), false);
                }
            },
            beforePost: function () {
                if (executePreCondition() == false) {
                    return false;
                }
                return true;
            }
        });
    }

    function executePreCondition() {
        // trim field vales before process.
        trimFormValues($("#entityContentForm"));

        if (!validatorAttachment.validate()) {
            return false;
        }
        return true;
    }

    function executePostCondition(data) {
        var result = eval('(' + data + ')');
        if (result.isError) {
            showError(result.message);
        } else {
            try {
                var newEntry = result.entity;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created

                    var previousTotal = parseInt(entityContentListModel.total);
                    var firstSerial = 1;

                    if (entityContentListModel.rows.length > 0) {
                        firstSerial = entityContentListModel.rows[0].cell[0];
                        regenerateSerial($(entityContentListModel.rows), 0);
                    }
                    newEntry.cell[0] = firstSerial;
                    entityContentListModel.rows.splice(0, 0, newEntry);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        entityContentListModel.rows.pop();
                    }

                    entityContentListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(entityContentListModel);

                } else if (newEntry != null) { // updated existing
                    updateListModel(entityContentListModel, newEntry, 0);
                    $("#flex1").flexAddData(entityContentListModel);
                }

                resetEntityContentForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetEntityContentForm() {
        clearForm($("#entityContentForm"), $("#caption"));
        $("#entityTypeId").val(entityTypeId);
        $("#entityId").val(entityId);
        // reset kando upload
        $(".k-delete").parent().click();
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");

        $("#labelAttachment").removeClass('label-optional');
        $("#labelAttachment").addClass('label-required');
        $('#contentObj').attr('validationMessage', 'Required');

        dropDownContentTypeId.enable(true);
        dropDownContentCategoryId.setDataSource(getKendoEmptyDataSource());
        dropDownContentCategoryId.value('');

        var actionUrl = "${createLink(controller: 'entityContent',action: 'create')}";
        $("#entityContentForm").attr('action', actionUrl);
    }

    function initFlexContent() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 40, sortable: false, align: "right"},
                        {display: "ID", name: "id", width: 30, sortable: false, align: "right", hide: true},
                        {display: "Content Type", name: "contentType", width: 120, sortable: false, align: "left"},
                        {display: "Content Category", name: "contentCategory", width: 120, sortable: false, align: "left"},
                        {display: "Extension", name: "extension", width: 60, sortable: false, align: "left"},
                        {display: "Caption", name: "caption", width: 400, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/entityContent/select,/entityContent/update">
                        {name: 'Edit', bclass: 'edit', onpress: editEntityContent},
                        </app:ifAllUrl>
                        <sec:access url="/entityContent/delete">
                        {name: 'Delete', bclass: 'delete', onpress: deleteEntityContent},
                        </sec:access>
                        <sec:access url="/entityContent/downloadContent">
                        {name: 'Download', bclass: 'report', onpress: downloadContent},
                        </sec:access>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ], searchitems: [
                    {display: "Content Category", name: "cg.name", width: 180, sortable: true, align: "left"},
                    {display: "Caption", name: "ec.caption", width: 180, sortable: true, align: "left"}
                ],
                    sortname: "name",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Attachment List',
                    useRp: true,
                    rp: 20,
                    rpOptions: [15, 20, 25],
                    showTableToggleBtn: false,
                    height: getGridHeight() - 20,
                    customPopulate: customPopulateEntityContentGrid
                }
        );
    }

    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function customPopulateEntityContentGrid(data) {
        if (data.isError) {
            showError(data.message);
            entityContentListModel = getEmptyGridModel();
        } else {
            entityContentListModel = data.entityContentList;
        }
        $('#flex1').flexAddData(entityContentListModel);
        return false;
    }

    function editEntityContent(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'attachment') == false) {
            return;
        }

        resetEntityContentForm();
        showLoadingSpinner(true);
        var entityContentId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'entityContent', action: 'select')}?id=" + entityContentId,
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
            showEntityContent(data);
        }
    }

    function showEntityContent(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#caption').val(entity.caption);
        $('#expirationDate').val(data.expirationDate);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
        $("#labelAttachment").removeClass('label-required');
        $("#labelAttachment").addClass('label-optional');
        $('#contentObj').removeAttr('validationMessage');
        dropDownContentTypeId.value(entity.contentTypeId);
        dropDownContentTypeId.enable(false);
        dropDownContentCategoryId.setDataSource(data.contentCategoryList);
        dropDownContentCategoryId.value(entity.contentCategoryId);
        var actionUrl = "${createLink(controller: 'entityContent',action: 'update')}";
        $("#entityContentForm").attr('action', actionUrl);
    }

    function deleteEntityContent(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var id = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'entityContent', action:'delete')}?id=" + id,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'attachment') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected attachment?')) {
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
            resetEntityContentForm();
            $('#flex1').decreaseCount(1);
            showSuccess(data.message);
            entityContentListModel.total = parseInt(entityContentListModel.total) - 1;
            removeEntityFromGridRows(entityContentListModel, selectedRow);
        } else {
            showError(data.message);
        }
    }

    function populateFlexGrid() {
        var strUrl = "${createLink(controller:'entityContent', action: 'list')}?entityId=" + entityId + "&entityTypeId=" + entityTypeId;
        $("#flex1").flexOptions({url: strUrl});
        if (entityContentListModel) {
            $("#flex1").flexAddData(entityContentListModel);
        }
    }

    function executePreConditionForDownload(ids) {
        var downloadCount = ids.length;
        if (downloadCount == 0) {
            showError("Please select an attachment to download");
            return false;
        }
        return true;
    }

    function downloadContent(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'attachment') == false) {
            return;
        }

        var confirmMsg = 'Do you want to download the attachment now?';
        showLoadingSpinner(true);
        var entityContentId = getSelectedIdFromGrid($('#flex1'));
        if (confirm(confirmMsg)) {
            var url = "${createLink(controller:'entityContent', action: 'downloadContent')}?entityContentId=" + entityContentId;
            document.location = url;
        }
        showLoadingSpinner(false);
        return true;
    }

    function updateContentCategoryList() {
        var contentTypeId = dropDownContentTypeId.value();
        if (contentTypeId == '') {
            dropDownContentCategoryId.value('');
            return false;
        }
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller: 'contentCategory', action: 'listContentCategoryByContentTypeId')}?contentTypeId=" + contentTypeId,
            success: function (data) {
                updateContentCategoryListDropDown(data);
            }, complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
        return true;
    }

    function updateContentCategoryListDropDown(data) {
        dropDownContentCategoryId.setDataSource(data.contentCategoryList);
        dropDownContentCategoryId.value('');
    }

    function loadMenu(pluginId, leftMenu) {
        var MENU_ID;
        switch (pluginId) {
            case 1:
                MENU_ID = MENU_ID_APPLICATION
                break
            case 2:
                MENU_ID = MENU_ID_ACCOUNTING
                break
            case 3:
                MENU_ID = MENU_ID_BUDGET
                break
            case 9:
                MENU_ID = MENU_ID_EXCHANGE_HOUSE
                break
            default:
                throw new RuntimeException('Failed to Load')
        }
        loadNumberedMenu(MENU_ID, leftMenu);
    }

</script>