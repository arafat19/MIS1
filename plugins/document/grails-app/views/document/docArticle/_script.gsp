<%@ page import="com.athena.mis.document.config.DocSysConfigurationCacheUtility; com.athena.mis.PluginConnector" %>
<script type="text/javascript">
    var articleList, output, detailsEditorModel, dropDownCategory, dropDownSubCategory;
    $(document).ready(function () {
        onLoadArticle();
    });

    function onLoadArticle() {
        initializeForm($('#articleForm'), onSubmitArticle);
        initFlexGrid();
        $('#details').css('height', $('#articleGridContainer').height() - 175);
        $("#details").kendoEditor();
        detailsEditorModel = $("#details").data("kendoEditor");
        $(document).attr('title', "DOC - Add Article");
        loadNumberedMenu(MENU_ID_DOCUMENT, "#docArticle/show");
    }

    function executePreCondition() {
        // trim field vales before process.
        if (!validateForm($("#articleForm"))) {
            return false;
        }
        if (detailsEditorModel.value().length > 15000) {
            showError('Too large article details.');
            return false;
        }
        return true;
    }

    function onSubmitArticle() {
        if (executePreCondition() == false) {
            return false;
        }
        setButtonDisabled($('#create'), true);
        // Spinner Show on AJAX Call
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'docArticle', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'docArticle', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#articleForm").serialize(),
            url: actionUrl,
            success: executePostCondition,
            complete: onCompleteAjaxCall,
            dataType: 'json'
        });
        return false;
    }


    function executePostCondition(result) {
        if (result.isError) {
            showError(result.message);
        } else {
            try {
                if ($('#id').val().isEmpty() && result.entity != null) { // newly created
                    var previousTotal = parseInt(articleList.total);

                    // re-arranging serial
                    var firstSerial = 1;
                    if (articleList.rows.length > 0) {
                        firstSerial = articleList.rows[0].cell[0];
                        regenerateSerial($(articleList.rows), 0);
                    }
                    result.entity.cell[0] = firstSerial;

                    articleList.rows.splice(0, 0, result.entity);

                    if ($('#flexArticle').countEqualsResultPerPage(previousTotal)) {
                        articleList.rows.pop();
                    }

                    articleList.total = ++previousTotal;
                    $("#flexArticle").flexAddData(articleList);

                } else if (result.entity != null) { // updated existing
                    updateListModel(articleList, result.entity, 0);
                    $("#flexArticle").flexAddData(articleList);
                }
                resetForm();
                showSuccess(result.message);
            } catch (e) {
            }
        }
        setButtonDisabled($('#create'), false);
    }

    function resetForm() {
        clearArticleForm($('#articleForm'));
        dropDownCategory.value('');
        $('#subCategoryId').attr('category_id', '0');
        $('#subCategoryId').reloadMe();
        setButtonDisabled($('#create'), false);
        $("#create").html("<span class='k-icon k-i-plus'></span>Save");   // reset create button text
    }

    function initFlexGrid() {
        $("#flexArticle").flexigrid
        (
                {
                    url: "${createLink(controller: 'docArticle', action: 'list')}",
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 30, sortable: false, align: "right"},
                        {display: "ID", name: "id", width: 30, sortable: false, align: "right", hide: true},
                        {display: "Title", name: "title", width: 300, sortable: false, align: "left"},
                        {display: "<app:showSysConfig
                                    pluginId="${PluginConnector.DOCUMENT_ID}"
                                      key="${DocSysConfigurationCacheUtility.DOC_CATEGORY_LABEL}"></app:showSysConfig>", name: "category", width: 200, sortable: false, align: "left"},
                        {display: "<app:showSysConfig
                                pluginId="${PluginConnector.DOCUMENT_ID}"
                                key="${DocSysConfigurationCacheUtility.DOC_SUB_CATEGORY_LABEL}"></app:showSysConfig>", name: "subCategory", width: 200, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/docArticle/show">
                        {name: 'Preview', bclass: 'preview', onpress: previewArticle},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/docArticle/select,/docArticle/update">
                        {name: 'Edit', bclass: 'edit', onpress: editArticle},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/docArticle/movedToTrash">
                        {name: 'Moved to Trash', bclass: 'trash', onpress: movedToTrashArticle},
                        </app:ifAllUrl>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Title", name: "da.title", width: 180, sortable: false, align: "left"},
                        {display: "<app:showSysConfig
                                    pluginId="${PluginConnector.DOCUMENT_ID}"
                                      key="${DocSysConfigurationCacheUtility.DOC_CATEGORY_LABEL}"></app:showSysConfig>", name: "dc.name", width: 180, sortable: false, align: "left"},
                        {display: "<app:showSysConfig
                                    pluginId="${PluginConnector.DOCUMENT_ID}"
                                      key="${DocSysConfigurationCacheUtility.DOC_SUB_CATEGORY_LABEL}"></app:showSysConfig>", name: "dsc.name", width: 180, sortable: false, align: "left"}
                    ],
                    sortname: "createdOn",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'My Article List',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight() + 200,
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
            articleList = getEmptyGridModel();
        } else {
            articleList = data.gridObj;
        }
        $("#flexArticle").flexAddData(articleList);
    }


    <%-- reloading grid data --%>
    function reloadGrid(com, grid) {
        $('#flexArticle').flexOptions({query: ''}).flexReload();
    }

    function previewArticle() {

    }

    <%-- Start : Delete operation of Category --%>
    function movedToTrashArticle(com, grid) {
        if (executePreConditionForMovedToTrashArticle() == false) {
            return;
        }
        showLoadingSpinner(true); // Spinner Show on AJAX Call
        var id = getSelectedIdFromGrid($('#flexArticle'));
        $.ajax({
            url: "${createLink(controller: 'docArticle', action: 'movedToTrash')}?id=" + id,
            success: executePostConditionForMovedToTrashArticle,
            complete: onCompleteAjaxCall,    // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForMovedToTrashArticle() {
        if (executeCommonPreConditionForSelect($('#flexArticle'), 'Article') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to moved the selected Article to trash?')) {
            return false;
        }
        return true;
    }

    <%-- removing selected row and clean input form --%>
    function executePostConditionForMovedToTrashArticle(data) {
        if (data.moved == true) {
            var selectedRow = null;
            $('.trSelected', $('#flexArticle')).each(function (e) {
                selectedRow = $(this).remove();
            });
            resetForm();
            $('#flexArticle').decreaseCount(1);
            showSuccess(data.message);
            articleList.total = parseInt(articleList.total) - 1;
            removeEntityFromGridRows(articleList, selectedRow);

        } else {
            // show delete error
            showError(data.message);
        }
    }

    function editArticle(com, grid) {
        //clear form before putting edited value
        clearArticleForm($('#articleForm'));
        if (executeCommonPreConditionForSelect($('#flexArticle'), 'Article') == false) {
            return;
        }
        showLoadingSpinner(true);	// Spinner Show on AJAX Call
        var id = getSelectedIdFromGrid($('#flexArticle'));
        $.ajax({
            url: "${createLink(controller: 'docArticle', action: 'select')}?id=" + id,
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
            showArticle(data);
        }
    }

    function showArticle(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        dropDownCategory.value(entity.categoryId);
        $('#subCategoryId').attr('category_id', entity.categoryId);
        $('#subCategoryId').attr('default_value', entity.subCategoryId);   // reset value when change category
        $('#subCategoryId').reloadMe(false, $('#containerSubCategory'));

        $('#title').val(entity.title);
        detailsEditorModel.value(entity.details);
        $("#create").html("<span class='k-icon k-i-plus'></span>Save Changes");
    }

    function clearArticleForm(frm) {
        clearErrors(frm);
        $('#id').val('');
        $('#version').val('');
        $('#title').val('');
        detailsEditorModel.value('');
    }

    function onChangeCategory() {
        var categoryId = dropDownCategory.value();
        if (categoryId == '') {
            dropDownSubCategory.setDataSource([]);
            dropDownSubCategory.value('');
            return false;
        }
        $('#subCategoryId').attr('category_id', categoryId);
        $('#subCategoryId').attr("default_value", '""');   // reset value when change category
        $('#subCategoryId').reloadMe(false, $('#containerSubCategory'));
    }

</script>
