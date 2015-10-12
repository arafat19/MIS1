<%@ page import="com.athena.mis.document.config.DocSysConfigurationCacheUtility; com.athena.mis.PluginConnector" %>
<script type="text/javascript">
    var articleList, output, dropDownContentType;
    $(document).ready(function () {
        onLoadArticle();
    });

    function onLoadArticle() {
        initializeForm($('#articleForm'), onSubmitArticle);
        initFlexGrid();

        var containerHeight = $('#contentHolder').height();
        $('#searchResultDiv').css('height', containerHeight);

        $(document).attr('title', "DOC - Add Article Query");
        loadNumberedMenu(MENU_ID_DOCUMENT, "#docArticleQuery/show");
    }

    function executePreCondition() {
        // trim field vales before process.
        if (!validateForm($("#articleForm"))) {
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
            actionUrl = "${createLink(controller: 'docArticleQuery', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'docArticleQuery', action: 'update')}";
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

                    if ($('#flexArticleQuery').countEqualsResultPerPage(previousTotal)) {
                        articleList.rows.pop();
                    }

                    articleList.total = ++previousTotal;
                    $("#flexArticleQuery").flexAddData(articleList);

                } else if (result.entity != null) { // updated existing
                    updateListModel(articleList, result.entity, 0);
                    $("#flexArticleQuery").flexAddData(articleList);
                }
                resetForm();
                showSuccess(result.message);
            } catch (e) {
            }
        }
        setButtonDisabled($('#create'), false);
    }

    function resetForm() {
        clearForm($('#articleForm'));
        setButtonDisabled($('#create'), false);
        $("#create").html("<span class='k-icon k-i-plus'></span>Save");   // reset create button text
    }

    function initFlexGrid() {
        $("#flexArticleQuery").flexigrid
        (
                {
                    url: "${createLink(controller: 'docArticleQuery', action: 'list')}",
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 50, sortable: false, align: "right"},
                        {display: "ID", name: "id", width: 30, sortable: false, align: "right", hide: true},
                        {display: "Name", name: "name", width: 150, sortable: false, align: "left"},
                        {display: "Content Type", name: "contentType", width: 100, sortable: false, align: "left"},
                        {display: "Criteria", name: "criteria", width: 350, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/docArticleQuery/show">
                        {name: 'Edit', bclass: 'edit', onpress: editArticle},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/docArticleQuery/select,/docArticleQuery/update">
                        {name: 'Delete', bclass: 'delete', onpress: deleteArticle},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/docArticleQuery/movedToTrash">
                        {name: 'Search', bclass: 'search', onpress: movedToTrashArticle},
                        </app:ifAllUrl>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Name", name: "name", width: 180, sortable: false, align: "left"}
                    ],
                    sortname: "createdOn",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'My Article Query List',
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
            articleList = getEmptyGridModel();
        } else {
            articleList = data.gridObj;
        }
        $("#flexArticleQuery").flexAddData(articleList);
    }


    <%-- reloading grid data --%>
    function reloadGrid(com, grid) {
        $('#flexArticleQuery').flexOptions({query: ''}).flexReload();
    }

    function deleteArticle(com, grid) {
        if (executePreConditionForDeleteArticle() == false) {
            return;
        }
        showLoadingSpinner(true); // Spinner Show on AJAX Call
        var id = getSelectedIdFromGrid($('#flexArticleQuery'));
        $.ajax({
            url: "${createLink(controller: 'docArticleQuery', action: 'delete')}?id=" + id,
            success: executePostConditionForDeleteArticle,
            complete: onCompleteAjaxCall,    // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDeleteArticle() {
        if (executeCommonPreConditionForSelect($('#flexArticleQuery'), 'Article') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected Article?')) {
            return false;
        }
        return true;
    }

    <%-- removing selected row and clean input form --%>
    function executePostConditionForDeleteArticle(data) {
        if (data.deleted == true) {
            var selectedRow = null;
            $('.trSelected', $('#flexArticleQuery')).each(function (e) {
                selectedRow = $(this).remove();
            });
            resetForm();
            $('#flexArticleQuery').decreaseCount(1);
            showSuccess(data.message);
            articleList.total = parseInt(articleList.total) - 1;
            removeEntityFromGridRows(articleList, selectedRow);

        } else {
            // show delete error
            showError(data.message);
        }
    }

    <%-- Start : Delete operation of Category --%>
    function movedToTrashArticle(com, grid) {
        if (executePreConditionForMovedToTrashArticle() == false) {
            return;
        }
        showLoadingSpinner(true); // Spinner Show on AJAX Call
        var id = getSelectedIdFromGrid($('#flexArticleQuery'));
        $.ajax({
            url: "${createLink(controller: 'docArticleQuery', action: 'movedToTrash')}?id=" + id,
            success: executePostConditionForMovedToTrashArticle,
            complete: onCompleteAjaxCall,    // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForMovedToTrashArticle() {
        if (executeCommonPreConditionForSelect($('#flexArticleQuery'), 'Article') == false) {
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
            $('.trSelected', $('#flexArticleQuery')).each(function (e) {
                selectedRow = $(this).remove();
            });
            resetForm();
            $('#flexArticleQuery').decreaseCount(1);
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
        clearForm($('#articleForm'));
        if (executeCommonPreConditionForSelect($('#flexArticleQuery'), 'Article') == false) {
            return;
        }
        showLoadingSpinner(true);	// Spinner Show on AJAX Call
        var id = getSelectedIdFromGrid($('#flexArticleQuery'));
        $.ajax({
            url: "${createLink(controller: 'docArticleQuery', action: 'select')}?id=" + id,
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
        $('#version').val(entity.version);
        $('#name').val(entity.name);
        dropDownContentType.value(entity.contentTypeId);
        $('#criteria').val(entity.criteria);
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");
    }
</script>

<script type="text/x-kendo-template" id="template">
        <div class="popover">
            <div class="arrow"></div>
            <h3 class="popover-title">#:name#</h3>
            <div class="popover-content">
            <p>#:criteria#</p>
            </div>
        </div>
</script>

<script>
    $(function () {
        var dataSource = new kendo.data.DataSource({
            data: [
                { name: "Test Article", criteria: "Test Criteria1" },
                { name: "Test Article1", criteria: "Test Criteria2" },
                { name: "Test Article2", criteria: "Test Criteria3" },
                { name: "Test Article3", criteria: "Test Criteria4" }
            ],
            pageSize: 20
        });

        $("#pager").kendoPager({
            dataSource: dataSource
        });

        $("#listView").kendoListView({
            dataSource: dataSource,
            template: kendo.template($("#template").html())
        });
    });
</script>