<script type="text/javascript">
    var indentListModel = false;
    var fromDate, toDate;
    var dropDownProject;
    $(document).ready(function () {
        onLoadIndentPage();
    });

    function onLoadIndentPage() {
        // initialize form with kendo validator & bind onSubmit event
        initializeForm($("#indentForm"), onSubmitIndent);
        var output = ${output ? output : ''};
        initFlexIndent();

        if (output.isError) {
            showError(output.message);
        } else {
            indentListModel = output.indentList;
        }
        populateIndentGrid();
        fromDate = $("#fromDate").val();
        toDate = $("#toDate").val();

        // update page title
        $(document).attr('title', "MIS - Create Indent");
        loadNumberedMenu(MENU_ID_PROCUREMENT, "#procIndent/show");
    }

    function executePreCondition() {
        if (!validateForm($("#indentForm"))) {
            return false;
        }

        if ($("#indentForm").validate({onfocusout: false}).form() == false) {
            return false;
        }
        if (!customValidateDate($('#fromDate'), 'From Date', $('#toDate'), 'To Date')) {
            return false;
        }
        return true;
    }

    function onSubmitIndent() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'procIndent', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'procIndent', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#indentForm").serialize(),
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
                var newEntry = result.indent;
                if ($('#id').val().isEmpty() && newEntry.entity != null) { // newly created
                    var previousTotal = parseInt(indentListModel.total);

                    // re-arranging serial
                    var firstSerial = 1;
                    if (indentListModel.rows.length > 0) {
                        firstSerial = indentListModel.rows[0].cell[0];
                        regenerateSerial($(indentListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;
                    indentListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        indentListModel.rows.pop();
                    }

                    indentListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(indentListModel);

                } else if (newEntry.entity != null) { // updated existing
                    updateListModel(indentListModel, newEntry.entity, 0);
                    $("#flex1").flexAddData(indentListModel);
                }

                resetIndentForm();
                if (result.mailSendingErrMsg) {
                    showError(result.mailSendingErrMsg);
                } else {
                    showSuccess(result.message);
                }
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetIndentForm() {
        clearForm($("#indentForm"), dropDownProject);
        $("#fromDate").val(fromDate);
        $("#toDate").val(toDate);
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function initFlexIndent() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 30, sortable: false, align: "right", hide: true},
                        {display: "Trace No", name: "id", width: 60, sortable: false, align: "right"},
                        {display: "Project Name", name: "projectId", width: 280, sortable: false, align: "left"},
                        {display: "From Date", name: "fromDate", width: 80, sortable: false, align: "left"},
                        {display: "To Date", name: "toDate", width: 80, sortable: false, align: "left"},
                        {display: "No. of Item(s)", name: "itemCount", width: 85, sortable: false, align: "right"},
                        {display: "Approved", name: "approved", width: 80, sortable: false, align: "left"},
                        {display: "Total Price", name: "totalPrice", width: 120, sortable: false, align: "right"},
                        {display: "Created By", name: "createdBy", width: 120, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/procIndent/select,/procIndent/update">
                        {name: 'Edit', bclass: 'edit', onpress: selectIndent},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/procIndent/delete">
                        {name: 'Delete', bclass: 'delete', onpress: deleteIndent},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/procIndentDetails/show">
                        {name: 'Item(s)', bclass: 'addItem', onpress: addIndentDetails},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/procIndent/approve">
                        {name: 'Approve', bclass: 'fgrid-approve', onpress: processApproval},
                        </app:ifAllUrl>
                        <sec:access url="/procReport/showIndentRpt">
                        {name: 'Report', bclass: 'report', onpress: viewIndentReport},
                        </sec:access>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Trace No", name: "id", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Indent',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 35,
                    customPopulate: customPopulateIndentGrid,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);
                    }
                }
        );
    }

    function customPopulateIndentGrid(data) {
        if (data.isError) {
            showError(data.message);
            indentListModel = getEmptyGridModel();
        } else {
            indentListModel = data;
        }
        $("#flex1").flexAddData(indentListModel);
    }

    function viewIndentReport(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'indent') == false) {
            return;
        }
        showLoadingSpinner(true);

        var indentId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller: 'procReport', action: 'showIndentRpt')}?indentId=" + indentId;
        $.history.load(formatLink(loc));
        return false;
    }

    function addIndentDetails(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'indent') == false) {
            return;
        }

        showLoadingSpinner(true);
        var indentId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller: 'procIndentDetails', action: 'show')}?indentId=" + indentId;
        $.history.load(formatLink(loc));
        return false;
    }

    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function deleteIndent(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var indentId = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller: 'procIndent', action: 'delete')}?id=" + indentId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'indent') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected indent?')) {
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
            resetIndentForm();
            $('#flex1').decreaseCount(1);
            showSuccess(data.message);
            indentListModel.total = parseInt(indentListModel.total) - 1;
            removeEntityFromGridRows(indentListModel, selectedRow);
        } else {
            showError(data.message);
        }
    }

    function selectIndent(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'indent') == false) {
            return;
        }

        resetIndentForm();
        showLoadingSpinner(true);
        var inventoryId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller: 'procIndent', action: 'select')}?id=" + inventoryId,
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
            showIndent(data);
        }
    }

    function showIndent(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#comments').val(entity.comments);
        $('#fromDate').val(data.fromDate);
        $('#toDate').val(data.toDate);
        dropDownProject.value(entity.projectId);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function populateIndentGrid() {
        var strUrl = "${createLink(controller: 'procIndent', action: 'list')}";
        $("#flex1").flexOptions({url: strUrl});
        if (indentListModel) {
            $("#flex1").flexAddData(indentListModel);
        }
    }

    function processApproval(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'indent') == false) {
            return;
        }

        var ids = $('.trSelected', grid);
        var approvedBy = $(ids[ids.length - 1]).find('td').eq(6).find('div').text();
        if (approvedBy == "YES") {
            showError("Indent is already approved.");
            return;
        }
        if (!confirm('Are you sure you want to approve this indent?')) {
            return;
        }
        approveIndent(com, grid);
    }

    function approveIndent(com, grid, approveAs) {
        var ids = $('.trSelected', grid);

        showLoadingSpinner(true);
        var indentId = $(ids[ids.length - 1]).attr('id').replace('row', '');
        $.ajax({
            url: "${createLink(controller: 'procIndent', action: 'approve')}?id=" + indentId,
            success: executePostConditionForApprove,
            complete: function (XMLHttpOrder, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConditionForApprove(result) {
        if (result.isError) {
            showError(result.message);
            showLoadingSpinner(false);
        } else {
            try {
                updateListModel(indentListModel, result.entity, 0);
                $("#flex1").flexAddData(indentListModel);

                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

</script>
