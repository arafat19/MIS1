<%@ page import="com.athena.mis.application.utility.RoleTypeCacheUtility; com.athena.mis.application.utility.RoleUtility" %>
<script type="text/javascript">
    var modelJson =${modelJson ? modelJson : ''};
    var isUserDirector = false;
    var isUserProjectDirector = false;
    var purchaseRequestListModel = false;
    var middleLayout, outerLayout, availableQuantity;
    var dropDownProject,dropDownIndentTraceNo;

    $(document).ready(function () {
        onLoadPurchaseRequest();
    });


    function onLoadPurchaseRequest() {
        initializeForm($("#purchaseRequestForm"), onSubmitPurchaseRequest);
        dropDownIndentTraceNo = initKendoDropdown($('#indentId'), "indent_date", null, null);
        dropDownIndentTraceNo.setDataSource(getKendoEmptyDataSource(dropDownIndentTraceNo,null));

        if (modelJson.isError) {
            showError(modelJson.message);
            return;
        }
        purchaseRequestListModel = modelJson;
        isUserDirector = modelJson.isUserDirector;
        isUserProjectDirector = modelJson.isUserProjectDirector;

        initPRGrid();

        var strUrl = "${createLink(controller: 'procPurchaseRequest', action: 'list')}";
        $("#flex1").flexOptions({url: strUrl});

        if (purchaseRequestListModel) {
            $("#flex1").flexAddData(purchaseRequestListModel);
        }
        $(document).attr('title', "MIS - Create Purchase Request");
        loadNumberedMenu(MENU_ID_PROCUREMENT, "#procPurchaseRequest/show");
    }


    function executePreConditionForMail(ids) {
        var notification = $(ids[ids.length - 1]).find('td').eq(9).find('div').text();
        if (notification == "YES") {
            showError("Selected Purchase Request already sent for approval.");
            return false;
        }
        if (executeCommonPreConditionForSelect($('#flex1'), 'purchase request') == false) {
            return false;
        }
        if (!confirm('Are you sure to send the selected Purchase Request for approval?')) {
            return false;
        }

        return true;
    }

    //sent notification
    function sentMailForPurchaseRequest(com, grid) {
        var ids = $('.trSelected', grid);
        if (executePreConditionForMail(ids) == false) {
            return;
        }
        showLoadingSpinner(true);
        var purchaseRequestId = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller:'procPurchaseRequest', action:  'sentMailForPRApproval')}?id=" + purchaseRequestId,
            success: executePostConditionForApprove,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function reloadBudgetGrid(com, grid) {
        if (com == 'Clear Results') {
            $('#flexBudget').flexOptions({query: ''}).flexReload();
        }
    }


    function resetFormForCreateAgain() {
        $('#id').val('');
        $('#version').val('');
        $('#comments').val('');
        dropDownIndentTraceNo.value('');
        $("#create").html("<span class='k-icon k-i-plus'></span>Create");
    }

    function executePreCondition() {
        if (!validateForm($("#purchaseRequestForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitPurchaseRequest() {
        if (executePreCondition() == false) {
            return false;
        }
        setButtonDisabled($('#create'), true);
        // Spinner Show on AJAX Call
        showLoadingSpinner(true);
        var actionUrl = null;

        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'procPurchaseRequest', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'procPurchaseRequest', action: 'update')}";
        }
        jQuery.ajax({
            type: 'post',
            data: jQuery("#purchaseRequestForm").serialize(),
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
                if ($('#id').val().isEmpty() && result.entity != null) { // newly created
                    var previousTotal = parseInt(purchaseRequestListModel.total);

                    // re-arranging serial
                    var firstSerial = 1;
                    if (purchaseRequestListModel.rows.length > 0) {
                        firstSerial = purchaseRequestListModel.rows[0].cell[0];
                        regenerateSerial($(purchaseRequestListModel.rows), 0);
                    }
                    result.entity.cell[0] = firstSerial;
                    purchaseRequestListModel.rows.splice(0, 0, result.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        purchaseRequestListModel.rows.pop();
                    }

                    purchaseRequestListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(purchaseRequestListModel);
                } else if (result.entity != null) { // updated existing
                    updateListModel(purchaseRequestListModel, result.entity, 0);
                    $("#flex1").flexAddData(purchaseRequestListModel);
                }
                resetFormForCreateAgain();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetForm() {
        clearForm($("#purchaseRequestForm"), dropDownProject);
        dropDownIndentTraceNo.setDataSource(getKendoEmptyDataSource(dropDownIndentTraceNo, null));
        dropDownIndentTraceNo.value('');
        $("#create").html("<span class='k-icon k-i-plus'></span>Create");
    }

    function initPRGrid() {
        $("#flex1").flexigrid
        (
            {
                url: false,
                dataType: 'json',
                colModel: [
                    {display: "Serial", name: "serial", width: 30, sortable: false, align: "right"},
                    {display: "PR No", name: "id", width: 50, sortable: false, align: "right"},
                    {display: "Project", name: "project", width: 350, sortable: false, align: "left"},
                    {display: "Item(s)", name: "itemCount", width: 60, sortable: false, align: "right"},
                    {display: "Approved(Director)", name: "approvedByDirector", width: 110, sortable: false, align: "center"},
                    {display: "Approved(P.D)", name: "approvedByProjectDirector", width: 110, sortable: false, align: "center"},
                    {display: "Created By", name: "created_by", width: 150, sortable: false, align: "left"},
                    {display: "Sent for Approval", name: "sentForApproval", width: 120, sortable: false, align: "center"}
                ],
                buttons: [
                    <app:ifAllUrl urls="/procPurchaseRequest/select,/procPurchaseRequest/update, /procPurchaseRequest/create">
                    {name: 'Edit', bclass: 'edit', onpress: editPurchaseRequest},
                    </app:ifAllUrl>
                    <app:ifAllUrl urls="/procPurchaseRequest/delete">
                    {name: 'Delete', bclass: 'delete', onpress: deletePurchaseRequest},
                    </app:ifAllUrl>
                    <app:ifAllUrl urls="/procPurchaseOrder/show,/procPurchaseOrder/create">
                    {name: 'Purchase Order', bclass: 'fgrid-purchase-order', onpress: createPurchaseOrder},
                    </app:ifAllUrl>
                    <app:ifAllUrl urls="/procPurchaseRequestDetails/show">
                    {name: 'Item(s)', bclass: 'addItem', onpress: addPurchaseRequestDetails},
                    </app:ifAllUrl>
                    <sec:access url="/procReport/showPurchaseRequestRpt">
                    {name: 'Report', bclass: 'report', onpress: viewPurchaseRequestReport},
                    </sec:access>
                    <app:ifAllUrl urls="/procPurchaseRequest/sentMailForPRApproval">
                    {name: 'Send for Approval', bclass: 'sendApprove', onpress: sentMailForPurchaseRequest},
                    </app:ifAllUrl>
                    <sec:access url="/procPurchaseRequest/approve">
                    <app:hasRoleType id="${RoleTypeCacheUtility.ROLE_TYPE_DIRECTOR}">
                    {name: 'Approve as Director', bclass: 'approve', onpress: processPRApprovalForDirector},
                    </app:hasRoleType>
                    <app:hasRoleType id="${RoleTypeCacheUtility.ROLE_TYPE_PROJECT_DIRECTOR}">
                    {name: 'Approve as PD', bclass: 'approve', onpress: processPRApprovalForProjectDirector},
                    </app:hasRoleType>
                    </sec:access>
                    <app:ifAllUrl urls="/procPurchaseRequest/unApprovePR">
                    {name: 'Make Editable', bclass: 'edit', onpress: unApprovePR},
                    </app:ifAllUrl>
                    {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                    {separator: true}
                ],
                sortname: "id",
                sortorder: "desc",
                usepager: true,
                singleSelect: true,
                title: 'All Purchase Request',
                useRp: true,
                rp: 15,
                showTableToggleBtn: false,
                height: getGridHeight() - 50,
                afterAjax: function (XMLHttpRequest, textStatus) {
                    afterAjaxError(XMLHttpRequest, textStatus);
                    showLoadingSpinner(false);// Spinner hide after AJAX Call
                },
                customPopulate: onLoadPurchaseRequestListJSON
            }
        );
    }

    function onLoadPurchaseRequestListJSON(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            purchaseRequestListModel = data;
        }
        $("#flex1").flexAddData(purchaseRequestListModel);
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'purchase request') == false) {
            return false;
        }
        if (!confirm('Are you sure to delete the selected Purchase Request?')) {
            return false;
        }
        return true;
    }

    function deletePurchaseRequest(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }

        showLoadingSpinner(true); // Spinner Show on AJAX Call

        var purchaseRequestId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller: 'procPurchaseRequest', action: 'delete')}?id=" + purchaseRequestId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConditionForDelete(data) {
        if (data.deleted == true) {
            var selectedRow = null;
            $('.trSelected', $('#flex1')).each(function (e) {
                selectedRow = $(this).remove();
            });

            $('#flex1').decreaseCount(1);
            showSuccess(data.message);
            resetFormForCreateAgain();
            purchaseRequestListModel.total = parseInt(purchaseRequestListModel.total) - 1;
            removeEntityFromGridRows(purchaseRequestListModel, selectedRow);
        } else {
            showError(data.message);
        }
    }

    function createPurchaseOrder(com, grid) {
        var ids = $('.trSelected', grid);
        if (executeCommonPreConditionForSelect($('#flex1'), 'purchase request') == false) {
            return;
        }

        var approvedByDirector = $(ids[ids.length - 1]).find('td').eq(4).find('div').text();
        if (approvedByDirector == "NO") {
            showError("Purchase Request is not approved by director.");
            return;
        }

        var approvedByProjectDirector = $(ids[ids.length - 1]).find('td').eq(5).find('div').text();
        if (approvedByProjectDirector == "NO") {
            showError("Purchase Request is not approved by project director.");
            return;
        }

        showLoadingSpinner(true);

        var purchaseRequestId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller: 'procPurchaseOrder', action: 'show')}?purchaseRequestId=" + purchaseRequestId;
        $.history.load(formatLink(loc));
        return false;
    }

    function viewPurchaseRequestReport(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'purchase request') == false) {
            return;
        }
        showLoadingSpinner(true);

        var purchaseRequestId = getSelectedIdFromGrid($('#flex1'));

        var loc = "${createLink(controller: 'procReport', action: 'showPurchaseRequestRpt')}?purchaseRequestId=" + purchaseRequestId;
        $.history.load(formatLink(loc));
        return false;
    }

    function processPRApprovalForDirector(com, grid) {
        var ids = $('.trSelected', grid);
        if ((executeCommonPreConditionForSelect($('#flex1'), 'purchase request') == false) || (executePreConditionForApprove() == false)) {
            return;
        }
        var approvedByDirector = $(ids[ids.length - 1]).find('td').eq(4).find('div').text();
        if (approvedByDirector == "YES") {
            showError("Purchase Request already approved by director.");
            return;
        }
        var approveAs = '${RoleUtility.ROLE_DIRECTOR}'; // @todo- populate var on pageLoad

        approvePurchaseRequest(com, grid, approveAs);

    }

    function processPRApprovalForProjectDirector(com, grid) {
        var ids = $('.trSelected', grid);
        if (executePreConditionForApprove() == false) {
            return;
        }
        var approvedByProjectDirector = $(ids[ids.length - 1]).find('td').eq(5).find('div').text();
        if (approvedByProjectDirector == "YES") {
            showError("Purchase Request already approved by project director.");
            return;
        }

        var approveAs = '${RoleUtility.ROLE_PROJECT_DIRECTOR}';   // @todo- populate var on pageLoad
        approvePurchaseRequest(com, grid, approveAs);
    }

    function approvePurchaseRequest(com, grid, approveAs) {    // @todo- populate var on pageLoad
        var ids = $('.trSelected', grid);

        showLoadingSpinner(true);
        var purchaseRequestId = $(ids[ids.length - 1]).attr('id').replace('row', '');

        $.ajax({
            url: "${createLink(controller: 'procPurchaseRequest', action: 'approve')}?id="
                    + purchaseRequestId + '&approveAs=' + approveAs,
            success: executePostConditionForApprove,
            complete: function (XMLHttpRequest, textStatus) {
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
                updateListModel(purchaseRequestListModel, result.entity, 0);
                $("#flex1").flexAddData(purchaseRequestListModel);

                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function addPurchaseRequestDetailsWithWorks(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'purchase request') == false) {
            return;
        }

        showLoadingSpinner(true);
        var purchaseRequestId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller: 'procPurchaseRequestDetails', action: 'showWithWorks')}?purchaseRequestId=" + purchaseRequestId;
        $.post(loc, function (data) {
            $('#contentHolder').html(data);
            showLoadingSpinner(false);
        });
        return false;
    }

    function addPurchaseRequestDetails(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'purchase request') == false) {
            return;
        }
        showLoadingSpinner(true);
        var purchaseRequestId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller: 'procPurchaseRequestDetails', action: 'show')}?purchaseRequestId=" + purchaseRequestId;
        $.history.load(formatLink(loc));
        return false;
    }

    function executePostConditionForShowPO(data) {
        if ((data.isError) && (data.isError == true)) {
            showError(data.message);
            return false;
        }
        $('#contentHolder').html(data);
    }

    function executePreConditionForApprove() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'purchase request') == false) {
            return false;
        }
        if (!isUserDirector && !isUserProjectDirector) {
            showError("Logged-In User has no right to approve the purchase request.");
            return false;
        }
        return true;
    }

    function reloadGrid(com, grid) {
        if (com == 'Clear Results') {
            $('#flex1').flexOptions({query: ''}).flexReload();
        }
    }

    function executePreConditionUnApprovePR() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'purchase request') == false) {
            return false;
        }
        if (!confirm('Are you sure to open the selected purchase request for editing?')) {
            return false;
        }
        return true;
    }

    function unApprovePR(com, grid) {
        if (executePreConditionUnApprovePR() == false) {
            return;
        }
        showLoadingSpinner(true);
        var purchaseRequestId = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller: 'procPurchaseRequest', action: 'unApprovePR')}?id=" + purchaseRequestId,
            success: executePostConditionForApprove,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }


    function editPurchaseRequest(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'purchase request') == false) {
            return;
        }
        var purchaseRequestId = getSelectedIdFromGrid($('#flex1'));
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller: 'procPurchaseRequest', action: 'select')}?id=" + purchaseRequestId,
            success: executePostConditionForEdit,
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConditionForEdit(data) {
        if (data.isError) {
            showError(result.message);
        } else {
            populatePurchaseRequest(data);
        }
    }

    function populatePurchaseRequest(data) {
        resetForm();
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        dropDownProject.value(entity.projectId);
        dropDownIndentTraceNo.setDataSource(data.indentList);
        dropDownIndentTraceNo.value(entity.indentId);
        $('#comments').val(entity.comments);
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");
    }

    function onChangeProject() {
        var projectId = dropDownProject.value();
        if (projectId == '') {
            dropDownIndentTraceNo.setDataSource(getKendoEmptyDataSource());
            dropDownIndentTraceNo.value('');
            return;
        }
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller:'procPurchaseRequest', action: 'listIndentByProject')}?projectId=" + projectId,
            success: function (data) {
                if (data.isError) {
                    showError(data.message);
                } else {
                    dropDownIndentTraceNo.setDataSource(data.indentList);
                    dropDownIndentTraceNo.value('');
                }
            }, complete: onCompleteAjaxCall,
            dataType: 'json',
            type: 'post'
        });
        return false;
    }

</script>