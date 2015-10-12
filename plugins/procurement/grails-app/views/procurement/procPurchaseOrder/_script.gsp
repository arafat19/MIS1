<%@ page import="com.athena.mis.application.utility.RoleTypeCacheUtility; com.athena.mis.application.utility.RoleUtility" %>
<script type="text/javascript">
    var output =${output ? output : ''};
    var purchaseOrderListModel = false;
    var isUserDirector = false;
    var isUserProjectDirector = false;
    var purchaseRequestMap, dropDownSupplier, dropDownPaymentMethod, discount;

    function onLoadPurchaseOrder() {
        if (output.isError) {
            showError(output.message);
            return;
        }
        // initialize form with kendo validator & bind onSubmit event
        initializeForm($("#purchaseOrderForm"), onSubmitPurchaseOrder);

        $('#discount').kendoNumericTextBox({
            min: 0,
            max: 999999999999.9999,
            format: "৳ #.####"

        });
        discount = $("#discount").data("kendoNumericTextBox");

        isUserDirector = output.isUserDirector;
        isUserProjectDirector = output.isUserProjectDirector;
        purchaseOrderListModel = output.purchaseOrderList;
        purchaseRequestMap = output.purchaseRequestMap;
        populatePurchaseRequest(purchaseRequestMap);
        <sec:access url="/procPurchaseOrder/list">
        var strUrl = "${createLink(controller: 'procPurchaseOrder', action: 'list')}";
        if (purchaseRequestMap) {
            strUrl = "${createLink(controller: 'procPurchaseOrder', action: 'list')}?purchaseRequestId=" + purchaseRequestMap.purchaseRequestId;
        }
        $("#flex1").flexOptions({url: strUrl});
        if (purchaseOrderListModel) {
            $("#flex1").flexAddData(purchaseOrderListModel);
        }
        </sec:access>

        // update page title
        $(document).attr('title', "MIS - Create Purchase Order");
        loadNumberedMenu(MENU_ID_PROCUREMENT, "#procPurchaseOrder/show");
    }

    function clearPurchaseRequest() {
        $('#purchaseRequestId').val('');
        $('#lblPurchaseRequestId').html('');
        $('#projectId').val('');
        $('#projectName').html('');
    }

    function populatePurchaseRequest(purchaseRequestMap) {
        clearPurchaseRequest();
        if (!purchaseRequestMap) {
            return false;
        }
        $('#purchaseRequestId').val(purchaseRequestMap.purchaseRequestId);
        $('#lblPurchaseRequestId').html(purchaseRequestMap.purchaseRequestId);
        $('#projectId').val(purchaseRequestMap.projectId);
        $('#projectName').html(purchaseRequestMap.projectName);
        $('#materialId').val(purchaseRequestMap.materialId);
        $('#material').html(purchaseRequestMap.material);
        $('#materialUnit').text(purchaseRequestMap.materialUnit);
        $('#purchaseRequestDetailsAvailableQuantity').val(purchaseRequestMap.availableQuantity);

        return true;
    }

    function executePreCondition() {
        if (!validateForm($("#purchaseOrderForm"))) {
            return false;
        }

        var availableBDQuantity = parseFloat($('#purchaseRequestDetailsAvailableQuantity').val());
        var quantity = parseFloat($('#quantity').val());
        if (quantity > availableBDQuantity) {
            showError("Purchase Order Quantity Exceeds Purchase Request Quantity.");
            return false;
        }
        return true;
    }

    function onSubmitPurchaseOrder() {

        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        // Spinner Show on AJAX Call
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'procPurchaseOrder', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'procPurchaseOrder', action: 'update')}";
        }
        var formDate = jQuery("#purchaseOrderForm").serialize();
        jQuery.ajax({
            type: 'post',
            data: formDate,
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
                    var previousTotal = parseInt(purchaseOrderListModel.total);

                    // re-arranging serial
                    var firstSerial = 1;
                    if (purchaseOrderListModel.rows.length > 0) {
                        firstSerial = purchaseOrderListModel.rows[0].cell[0];
                        regenerateSerial($(purchaseOrderListModel.rows), 0);
                    }
                    result.entity.cell[0] = firstSerial;
                    purchaseOrderListModel.rows.splice(0, 0, result.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        purchaseOrderListModel.rows.pop();
                    }

                    purchaseOrderListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(purchaseOrderListModel);
                    clearOnlyPO();
                } else if (result.entity != null) { // updated existing
                    updateListModel(purchaseOrderListModel, result.entity, 0);
                    $("#flex1").flexAddData(purchaseOrderListModel);
                    resetPOForm();
                }

                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function clearOnlyPO() {
        dropDownSupplier.value('');
        dropDownPaymentMethod.value('');
        $('#modeOfPayment').val('');
        $('#comments').val('');
        discount.value('');

        $('#id').val('');
        $('#version').val('');
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function resetPOForm() {
        clearPurchaseRequest();
        clearForm($("#purchaseOrderForm"), dropDownSupplier);
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    //-------------

    $("#flex1").flexigrid
    (
            {
                url: false,
                dataType: 'json',
                colModel: [
                    {display: "Serial", name: "serial", width: 30, sortable: false, align: "right", hide: true},
                    {display: "PO No", name: "id", width: 40, sortable: true, align: "right"},
                    {display: "Date", name: "createdOn", width: 70, sortable: true, align: "left"},
                    {display: "PR No", name: "purchaseRequestId", width: 40, sortable: true, align: "left"},
                    {display: "Supplier", name: "supplierId", width: 100, sortable: false, align: "left"},
                    {display: "Item(s)", name: "materialCount", width: 45, sortable: false, align: "right"},
                    {display: "Discount", name: "discount", width: 80, sortable: false, align: "right"},
                    {display: "Net Price", name: "netPrice", width: 120, sortable: false, align: "right"},
                    {display: "Tr. Cost", name: "total_transport_cost", width: 80, sortable: false, align: "right"},
                    {display: "VAT/Tax", name: "total_vat_tax", width: 70, sortable: false, align: "right"},
                    {display: "Sent for Approval", name: "sentForApproval", width: 105, sortable: false, align: "center"},
                    {display: "Approved(Dir.)", name: "approvedByDirector", width: 85, sortable: false, align: "left"},
                    {display: "Approved(PD)", name: "approvedByProjectDirector", width: 85, sortable: false, align: "left"}
                ],
                buttons: [
                    <app:ifAllUrl urls="/procPurchaseOrder/select,/procPurchaseOrder/update">
                    {name: 'Edit', bclass: 'edit', onpress: editPurchaseOrder},
                    </app:ifAllUrl>
                    <app:ifAllUrl urls="/procPurchaseOrderDetails/show">
                    {name: 'Item(s)', bclass: 'addItem', onpress: addPurchaseOrderDetailsItem},
                    </app:ifAllUrl>
                    <app:ifAllUrl urls="/procReport/showPurchaseOrderRpt">
                    {name: 'Report', bclass: 'report', onpress: viewPurchaseOrderReport},
                    </app:ifAllUrl>
                    <app:ifAllUrl urls="/procTransportCost/create">
                    {name: 'Trns. Cost', bclass: 'addCost', onpress: addTransportCost},
                    </app:ifAllUrl>
                    <sec:access url="/procTermsAndCondition/show">
                    {name: 'Terms & Cond.', bclass: 'viewCost', onpress: viewProcTermsAndCondition},
                    </sec:access>
                    <app:ifAllUrl urls="/procPurchaseOrder/sendForPOApproval">
                    {name: 'Send for Approval', bclass: 'sendApprove', onpress: sentNotificationOfPurchaseOrder},
                    </app:ifAllUrl>
                    <sec:access url="/procPurchaseOrder/approve">
                    <app:hasRoleType id="${RoleTypeCacheUtility.ROLE_TYPE_DIRECTOR}">
                    {name: 'Approve as Director', bclass: 'approve', onpress: processPRApprovalForDirector},
                    </app:hasRoleType>
                    <app:hasRoleType id="${RoleTypeCacheUtility.ROLE_TYPE_PROJECT_DIRECTOR}">
                    {name: 'Approve as PD', bclass: 'approve', onpress: processPRApprovalForProjectDirector},
                    </app:hasRoleType>
                    </sec:access>
                    <app:ifAllUrl urls="/procPurchaseOrder/unApprovePO">
                    {name: 'Make Editable', bclass: 'edit', onpress: unApprovePO},
                    </app:ifAllUrl>
                    <app:ifAllUrl urls="/procPurchaseOrder/cancelPO">
                    {name: 'Cancel', bclass: 'delete', onpress: cancelPO},
                    </app:ifAllUrl>
                    {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                    {separator: true}
                ], searchitems: [
                {display: "PO No", name: "poId", width: 180, sortable: true, align: "left"},
                {display: "Supplier", name: "supplierName", width: 180, sortable: true, align: "left"}
            ],
                sortname: "id",
                sortorder: "desc",
                usepager: true,
                singleSelect: true,
                title: 'All Purchase Order',
                useRp: true,
                rp: 15,
                showTableToggleBtn: false,
                height: getGridHeight() - 40,
                afterAjax: function (XMLHttpRequest, textStatus) {
                    afterAjaxError(XMLHttpRequest, textStatus);
                    showLoadingSpinner(false);// Spinner hide after AJAX Call
                },
                preProcess: onLoadPurchaseOrderListJSON
            }
    );

    function addPurchaseOrderDetailsItem(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'purchase order') == false) {
            return;
        }
        showLoadingSpinner(true);
        var purchaseOrderId = getSelectedIdFromGrid($('#flex1'));


        var loc = "${createLink(controller: 'procPurchaseOrderDetails', action: 'show')}?purchaseOrderId=" + purchaseOrderId;
        $.history.load(formatLink(loc));
        return false;
    }

    function viewPurchaseOrderReport(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'purchase order') == false) {
            return;
        }

        showLoadingSpinner(true);

        var purchaseOrderId = getSelectedIdFromGrid($('#flex1'));

        var loc = "${createLink(controller: 'procReport', action: 'showPurchaseOrderRpt')}?purchaseOrderId=" + purchaseOrderId;
        $.history.load(formatLink(loc));
        return false;
    }

    function processPRApprovalForDirector(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'purchase order') == false) {
            return;
        }
        var ids = $('.trSelected', grid);
        var approvedByDirector = $(ids[ids.length - 1]).find('td').eq(12).find('div').text();

        if (approvedByDirector == "YES") {
            showError("Purchase Order already approved by director.");
            return;
        }
        var approveAs = '${RoleUtility.ROLE_DIRECTOR}'; // @todo- populate var on pageLoad
        approvePurchaseOrder(com, grid, approveAs);
    }

    function processPRApprovalForProjectDirector(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'purchase order') == false) {
            return;
        }
        var ids = $('.trSelected', grid);
        var approvedByProjectDirector = $(ids[ids.length - 1]).find('td').eq(13).find('div').text();

        if (approvedByProjectDirector == "YES") {
            showError("Purchase Order already approved by project director.");
            return;
        }
        var approveAs = '${RoleUtility.ROLE_PROJECT_DIRECTOR}'; // @todo- populate var on pageLoad
        approvePurchaseOrder(com, grid, approveAs);
    }

    function approvePurchaseOrder(com, grid, approveAs) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'purchase order') == false) {
            return;
        }
        showLoadingSpinner(true);
        var purchaseOrderId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller: 'procPurchaseOrder', action: 'approve')}?id="
                    + purchaseOrderId + '&approveAs=' + approveAs,
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
                updateListModel(purchaseOrderListModel, result.entity, 0);
                $("#flex1").flexAddData(purchaseOrderListModel);

                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function reloadGrid(com, grid) {
        if (com == 'Clear Results') {
            $('#flex1').flexOptions({query: ''}).flexReload();
        }
    }

    function onLoadPurchaseOrderListJSON(data) {
        if (data.isError) {
            showError(data.message);
            purchaseOrderListModel = null;
        } else {
            purchaseOrderListModel = data;
        }
        return data;
    }

    function addTransportCost(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'purchase order') == false) {
            return;
        }

        var purchaseOrderId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller: 'procTransportCost', action: 'show')}?purchaseOrderId=" + purchaseOrderId;
        $.history.load(formatLink(loc));
        showLoadingSpinner(true);
    }

    function editPurchaseOrder(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'purchase order') == false) {
            return;
        }
        resetPOForm();
        showLoadingSpinner(true);
        var purchaseOrderId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller: 'procPurchaseOrder', action: 'select')}?id=" + purchaseOrderId,
            success: executePostConditionForEdit,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }


    function viewProcTermsAndCondition(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'purchase order') == false) {
            return;
        }
        var purchaseOrderId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller: 'procTermsAndCondition', action: 'show')}?purchaseOrderId=" + purchaseOrderId;
        $.history.load(formatLink(loc));
        showLoadingSpinner(true);
    }

    function executePostConditionForEdit(data) {
        if (data.entity == null) {
            showError(result.message);
        } else {
            showPurchaseOrder(data);
        }
    }

    function showPurchaseOrder(data) {
        var entity = data.entity;

        $('#id').val(entity.id);
        $('#version').val(data.version);

        purchaseRequestMap = data.purchaseRequestMap;
        populatePurchaseRequest(purchaseRequestMap);

        dropDownSupplier.value(entity.supplierId);
        dropDownPaymentMethod.value(entity.paymentMethodId);

        $('#modeOfPayment').val(entity.modeOfPayment);
        discount.value(entity.discount);
        $('#comments').val(entity.comments);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    window.onload = onLoadPurchaseOrder();

    function executePreConditionForNotification() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'purchase order') == false) {
            return false;
        }
        if (!confirm('Are you sure to send the selected purchase order for approval?')) {
            return false;
        }
        return true;
    }

    function sentNotificationOfPurchaseOrder(com, grid) {
        if (executePreConditionForNotification() == false) {
            return;
        }
        showLoadingSpinner(true);

        var purchaseOrderId = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller:'procPurchaseOrder', action:  'sendForPOApproval')}?id=" + purchaseOrderId,
            success: executePostConditionForApprove,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionUnApprovePO() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'purchase order') == false) {
            return false;
        }
        if (!confirm('Are you sure to open the selected purchase order for editing?')) {
            return false;
        }
        return true;
    }

    function unApprovePO(com, grid) {
        if (executePreConditionUnApprovePO() == false) {
            return;
        }
        showLoadingSpinner(true);
        var purchaseOrderId = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller: 'procPurchaseOrder', action: 'unApprovePO')}?id=" + purchaseOrderId,
            success: executePostConditionForApprove,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function cancelPO(com, grid) {
        var ids = $('.trSelected', grid);
        if (executeCommonPreConditionForSelect($('#flex1'), 'purchase order') == false) {
            return;
        }
        var purchaseOrderId = $(ids[0]).attr('id').replace('row', '');
        $('#lblProcCancelPoNo').text(purchaseOrderId);    // Set PO No. in Modal form
        $('#cancelConfirmationModalPO').modal('show');    // show Modal
        return false;
    }

    // this func. will be called from Modal form
    function procProcessCancelPO() {
        var ids = $('.trSelected', $('#flex1'));
        var purchaseOrderId = $(ids[0]).attr('id').replace('row', '');
        var cancelReason = $('#txtProcPoCancelReason').val();
        if (cancelReason == '') {
            showError("Please write down proper cancellation reason");
            return false;
        }
        $.ajax({
            url: "${createLink(controller:'procPurchaseOrder', action:  'cancelPO')}?id=" + purchaseOrderId + "&reason=" + cancelReason,
            success: executePostConditionForCancelPO,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    // post condition for cancel PO
    function executePostConditionForCancelPO(data) {
        if (data.cancelled == true) {
            $('#txtProcPoCancelReason').val('');
            $('#lblProcCancelPoNo').text('');    // Clean PO No. in Modal form
            $('#cancelConfirmationModalPO').modal('hide');

            var selectedRow = null;
            $('.trSelected', $('#flex1')).each(function (e) {
                selectedRow = $(this).remove();
            });
            $('#flex1').decreaseCount(1);
            purchaseOrderListModel.total = parseInt(purchaseOrderListModel.total) - 1;
            removeEntityFromGridRows(purchaseOrderListModel, selectedRow);

            showSuccess(data.message);
        } else {
            showError(data.message);
        }
    }

    function procCleanPOCancelForm() {
        $('#lblProcCancelPoNo').text('');    // Clean PO No. in Modal form
        $('#txtProcPoCancelReason').val('');       // clean textArea
    }

</script>
