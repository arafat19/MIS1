<script type="text/javascript">
    var output = false;
    var contentCategoryListModel = false, contentTypeDocument;
    var dropDownContentType, numericBoxWidth, numericBoxHeight, numericBoxMaxSize;

    $(document).ready(function () {
        onLoadContentCategoryPage();
    });

    function onLoadContentCategoryPage() {
        // initialize form with kendo validator & bind onSubmit event
        initializeForm($("#contentCategoryForm"), onSubmitContentCategory);

        $("#width,#height").kendoNumericTextBox({
            decimals: 0,
            min: 0,
            format: "# pixel"
        });
        $("#maxSize").kendoNumericTextBox({
            decimals: 0,
            min: 0,
            format: "# bytes"
        });

        numericBoxWidth = $("#width").data("kendoNumericTextBox");
        numericBoxHeight = $("#height").data("kendoNumericTextBox");
        numericBoxMaxSize = $("#maxSize").data("kendoNumericTextBox");
        contentTypeDocument = $('#hidContentTypeDocumentId').val();

        output = ${output ? output : ''};
        if (output.isError) {
            showError(output.message);
        } else {
            contentCategoryListModel = output.lstContentCategory;

        }

        initFlexGrid();
        populateFlexGrid();
        // update page title
        $(document).attr('title', "Create Content Category");
        loadNumberedMenu(MENU_ID_APPLICATION, "#contentCategory/show");
    }

    function executePreCondition() {
        if (!validateForm($("#contentCategoryForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitContentCategory() {
        if (executePreCondition() == false) {
            return false;
        }

        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'contentCategory', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'contentCategory', action: 'update')}";
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        jQuery.ajax({
            type: 'post',
            data: jQuery("#contentCategoryForm").serialize(),
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
                var newEntry = result;
                if ($('#id').val().isEmpty() && newEntry.entity != null) { // newly created
                    var previousTotal = parseInt(contentCategoryListModel.total);

                    // re-arranging serial
                    var firstSerial = 1;
                    if (contentCategoryListModel.rows.length > 0) {
                        firstSerial = contentCategoryListModel.rows[0].cell[0];
                        regenerateSerial($(contentCategoryListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;
                    contentCategoryListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        contentCategoryListModel.rows.pop();
                    }

                    contentCategoryListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(contentCategoryListModel);
                } else if (newEntry.entity != null) { // updated existing
                    updateListModel(contentCategoryListModel, newEntry.entity, 0);
                    $("#flex1").flexAddData(contentCategoryListModel);
                }

                resetContentCategoryForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetContentCategoryForm() {
        clearForm($("#contentCategoryForm"), $("#contentTypeId"))

        numericBoxWidth.enable(true);
        numericBoxHeight.enable(true);

        $('#labelWidth').removeClass('label-optional');
        $('#labelHeight').removeClass('label-optional');
        $('#labelWidth').addClass('label-required');
        $('#labelHeight').addClass('label-required');
        $('#width').attr('required', true);
        $('#height').attr('required', true);
        $('#width').attr('validationMessage', 'Required');
        $('#height').attr('validationMessage', 'Required');

        $("#create").html("<span class='k-icon k-i-plus'></span>Create");
        $('#hidContentTypeDocumentId').val(contentTypeDocument);
    }

    function initFlexGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 30, sortable: false, align: "right"},
                        {display: "ID", name: "id", width: 30, sortable: false, align: "right", hide: true},
                        {display: "Name", name: "name", width: 200, sortable: false, align: "left"},
                        {display: "Content Type", name: "contentType", width: 200, sortable: false, align: "left"},
                        {display: "Width (pixel)", name: "width", width: 100, sortable: false, align: "right"},
                        {display: "Height (pixel)", name: "height", width: 100, sortable: false, align: "right"},
                        {display: "Max Size (byte)", name: "maxSize", width: 100, sortable: false, align: "right"},
                        {display: "Extension", name: "extension", width: 200, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/contentCategory/select,/contentCategory/update">
                        {name: 'Edit', bclass: 'edit', onpress: editContentCategory},
                        </app:ifAllUrl>
                        <sec:access url="/contentCategory/delete">
                        {name: 'Delete', bclass: 'delete', onpress: deleteContentCategory},
                        </sec:access>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ], searchitems: [
                    {display: "Name", name: "name", width: 180, sortable: true, align: "left"}
                ],
                    sortname: "name",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Content Category List',
                    useRp: true,
                    rp: 15,
                    rpOptions: [15, 20, 25],
                    showTableToggleBtn: false,
                    height: getGridHeight() - 20,
                    customPopulate: customPopulateContentCategoryGrid
                }
        );
    }

    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function customPopulateContentCategoryGrid(data) {
        if (data.isError) {
            showError(data.message);
            contentCategoryListModel = getEmptyGridModel();
        } else {
            contentCategoryListModel = data.lstContentCategory;
        }
        $('#flex1').flexAddData(contentCategoryListModel);
        return false;
    }

    function editContentCategory(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'content category') == false) {
            return;
        }

        resetContentCategoryForm();

        showLoadingSpinner(true);
        var contentCategroyId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'contentCategory', action: 'select')}?id=" + contentCategroyId,
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
            showContentCategory(data);
        }
    }

    function showContentCategory(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);

        $('#name').val(entity.name);
        dropDownContentType.value(entity.contentTypeId);

        numericBoxWidth.value(entity.width);
        numericBoxHeight.value(entity.height);
        numericBoxMaxSize.value(entity.maxSize);

        $('#extension').val(entity.extension);

        toggleWidthHeight();

        $("#create").html("<span class='k-icon k-i-plus'></span>Update");
    }

    function deleteContentCategory(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var id = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'contentCategory', action:'delete')}?id=" + id,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'content category') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected content category?')) {
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
            resetContentCategoryForm();
            $('#flex1').decreaseCount(1);
            showSuccess(data.message);
            contentCategoryListModel.total = parseInt(contentCategoryListModel.total) - 1;
            removeEntityFromGridRows(contentCategoryListModel, selectedRow);
        } else {
            showError(data.message);
        }
    }

    function populateFlexGrid() {
        var strUrl = "${createLink(controller:'contentCategory', action: 'list')}";
        $("#flex1").flexOptions({url: strUrl});
        if (contentCategoryListModel) {
            $("#flex1").flexAddData(contentCategoryListModel);
        }
    }

    function toggleWidthHeight() {
        if (dropDownContentType.value() == contentTypeDocument) {
            numericBoxWidth.value('0');
            numericBoxHeight.value('0');
            numericBoxWidth.enable(false);
            numericBoxHeight.enable(false);
            $('#labelWidth').removeClass('label-required');
            $('#labelHeight').removeClass('label-required');
            $('#labelWidth').addClass('label-optional');
            $('#labelHeight').addClass('label-optional');
            $('#width').removeAttr('required');
            $('#width').removeAttr('validationMessage');
            $('#height').removeAttr('required');
            $('#height').removeAttr('validationMessage');
        } else {
            numericBoxWidth.enable(true);
            numericBoxHeight.enable(true);
            $('#labelWidth').removeClass('label-optional');
            $('#labelHeight').removeClass('label-optional');
            $('#labelWidth').addClass('label-required');
            $('#labelHeight').addClass('label-required');
            $('#width').attr('required', 'required');
            $('#width').attr('validationMessage', 'Required');
            $('#height').attr('required', 'required');
            $('#height').attr('validationMessage', 'Required');
        }
    }

</script>
