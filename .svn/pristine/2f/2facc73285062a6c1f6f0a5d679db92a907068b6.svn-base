<script language="javascript">
    var accIouSlipListModel = false;
    var dropDownEmployee, dropDownProject, dropDownIndent;

    $(document).ready(function () {
        onLoadAccIouSlipPage();
    });

    function onLoadAccIouSlipPage() {
        initializeForm($("#accIouSlipForm"), onSubmitAccIouSlip);
        dropDownIndent = initKendoDropdown($('#indentId'), "indent_date", null, null);
        dropDownIndent.setDataSource(getKendoEmptyDataSource(dropDownIndent, null));

        var output =${output ? output : ''};
        if (output.isError) {
            showError(output.message);
        } else {
            accIouSlipListModel = output.accIouSlipList;
        }
        gridInit();

        // update page title
        $(document).attr('title', "MIS - Create IOU Slip");
        loadNumberedMenu(MENU_ID_ACCOUNTING, "#accIouSlip/show");
    }

    function executePreCondition() {
        if (!validateForm($("#accIouSlipForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitAccIouSlip() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'accIouSlip', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'accIouSlip', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#accIouSlipForm").serialize(),
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

                    var previousTotal = parseInt(accIouSlipListModel.total);
                    var firstSerial = 1;

                    if (accIouSlipListModel.rows.length > 0) {
                        firstSerial = accIouSlipListModel.rows[0].cell[0];
                        regenerateSerial($(accIouSlipListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;
                    accIouSlipListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        accIouSlipListModel.rows.pop();
                    }

                    accIouSlipListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(accIouSlipListModel);

                } else if (newEntry.entity != null) { // updated existing
                    updateListModel(accIouSlipListModel, newEntry.entity, 0);
                    $("#flex1").flexAddData(accIouSlipListModel);
                }

                resetForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function populateIndentList() {
        var projectId = dropDownProject.value();
        if (projectId == '') {
            dropDownIndent.setDataSource(getKendoEmptyDataSource(dropDownIndent, null));
            dropDownIndent.value('');
            return;
        }

        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller:'accIouSlip', action: 'getIndentList')}?projectId=" + projectId,
            success: function (data) {
                if (data.isError) {
                    showError(data.message);
                } else {
                    dropDownIndent.setDataSource(data.indentList);
                    dropDownIndent.value('');
                }
            }, complete: onCompleteAjaxCall,
            dataType: 'json',
            type: 'post'
        });
    }

    function resetForm() {
        clearForm($("#accIouSlipForm"), dropDownEmployee);
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");

        dropDownIndent.setDataSource(getKendoEmptyDataSource(dropDownIndent, null));
        dropDownIndent.value('');
    }

    function gridInit() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 30, sortable: false, align: "right", hide: true},
                        {display: "Trace No", name: "id", width: 50, sortable: false, align: "right"},
                        {display: "Created On", name: "createdOn", width: 70, sortable: false, align: "left"},
                        {display: "Employee", name: "employee", width: 180, sortable: false, align: "left"},
                        {display: "Indent Details", name: "indentDate", width: 180, sortable: false, align: "left"},
                        {display: "Total Amount", name: "totalAmount", width: 120, sortable: false, align: "right"},
                        {display: "Sent for Approval", name: "sentForApproval", width: 100, sortable: false, align: "center"},
                        {display: "Approved By", name: "approvedBy", width: 145, sortable: false, align: "left"},
                        {display: "Purpose Count", name: "approvedBy", width: 95, sortable: false, align: "center"},
                        {display: "Project", name: "project", width: 60, sortable: false, align: "left"},
                        {display: "Created By", name: "createdBy", width: 145, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/accIouSlip/select,/accIouSlip/update">
                        {name: 'Edit', bclass: 'edit', onpress: selectAccIouSlip},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/accIouSlip/delete">
                        {name: 'Delete', bclass: 'delete', onpress: deleteAccIouSlip},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/accIouPurpose/show">
                        {name: 'Purpose', bclass: 'addItem', onpress: addIOUPurpose},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/accReport/showAccIouSlipRpt">
                        {name: 'Report', bclass: 'report', onpress: showIOUSlipReport},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/accIouSlip/sentNotification">
                        {name: 'Send for Approval', bclass: 'sendApprove', onpress: sentNotificationOfAccIouSlip},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/accIouSlip/approve">
                        {name: 'Approve', bclass: 'approve', onpress: approveAccIouSlip},
                        </app:ifAllUrl>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Trace No", name: "ais.id", width: 180, sortable: true, align: "left"},
                        {display: "Total Amount", name: "ais.total_purpose_amount", width: 180, sortable: true, align: "left"},
                        {display: "Employee", name: "employee.full_name", width: 180, sortable: true, align: "left"},
                        {display: "Project Name", name: "project.name", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All IOU Slip',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 10,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);
                    },
                    preProcess: onLoadListJSON
                }
        );
    }

    function showIOUSlipReport(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'IOU Slip') == false) {
            return;
        }
        showLoadingSpinner(true);
        var accIouSlipId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller: 'accReport', action: 'showAccIouSlipRpt')}?accIouSlipId=" + accIouSlipId;
        $.history.load(formatLink(loc));
        return false;
    }

    function addIOUPurpose(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), ' IOU slip') == false) {
            return;
        }
        showLoadingSpinner(true);
        var accIouSlipId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller: 'accIouPurpose', action: 'show')}?accIouSlipId=" + accIouSlipId;
        $.history.load(formatLink(loc));
        return false;
    }

    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function onLoadListJSON(data) {
        if (data.isError) {
            showError(data.message);
            accIouSlipListModel = getEmptyGridModel();
        } else {
            accIouSlipListModel = data;
        }
        return accIouSlipListModel;
    }

    function deleteAccIouSlip(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var accIouSlipId = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller:'accIouSlip', action:  'delete')}?id=" + accIouSlipId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'IOU Slip') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected IOU Slip?')) {
            return false;
        }
        return true;
    }

    //sent notification 
    function sentNotificationOfAccIouSlip(com, grid) {
        var ids = $('.trSelected', grid);
        if (executePreConditionForNotification(ids) == false) {
            return;
        }
        showLoadingSpinner(true);
        var accIouSlipId = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller:'accIouSlip', action:  'sentNotification')}?id=" + accIouSlipId,
            success: executePostConditionForApprove,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForNotification(ids) {
        var notification = $(ids[ids.length - 1]).find('td').eq(5).find('div').text();
        if (notification == "YES") {
            showError("IOU Slip already sent for approval.");
            return false;
        }
        if (executeCommonPreConditionForSelect($('#flex1'), 'IOU slip') == false) {
            return false;
        }
        if (!confirm('Are you sure to send the selected IOU Slip for approval?')) {
            return false;
        }
        return true;
    }

    function approveAccIouSlip(com, grid) {
        var ids = $('.trSelected', grid);
        if (executePreConditionForApprove(ids) == false) {
            return;
        }
        showLoadingSpinner(true);
        var accIouSlipId = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller:'accIouSlip', action:  'approve')}?id=" + accIouSlipId,
            success: executePostConditionForApprove,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForApprove(ids) {
        var notification = $(ids[ids.length - 1]).find('td').eq(5).find('div').text();
        if (notification == "NO") {
            showError("IOU Slip is not prepared to approve.");
            return false;
        }


        var approvedBy = $(ids[ids.length - 1]).find('td').eq(6).find('div').text();
        if (approvedBy == "") {
            showError("IOU Slip already approved.");
            return false;
        }

        if (executeCommonPreConditionForSelect($('#flex1'), 'IOU slip') == false) {
            return false;
        }

        if (!confirm('Are you sure to approve the selected IOU Slip?')) {
            return false;
        }
        return true;
    }

    function executePostConditionForApprove(result) {
        if (result.isError) {
            showError(result.message);
            showLoadingSpinner(false);
        } else {
            try {
                updateListModel(accIouSlipListModel, result.entity, 0);
                $("#flex1").flexAddData(accIouSlipListModel);
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
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
            accIouSlipListModel.total = parseInt(accIouSlipListModel.total) - 1;
            removeEntityFromGridRows(accIouSlipListModel, selectedRow);
        } else {
            showError(data.message);
        }
    }

    function selectAccIouSlip(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), ' IOU slip') == false) {
            return;
        }

        resetForm();
        showLoadingSpinner(true);
        var accIouSlipId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'accIouSlip', action: 'select')}?id=" + accIouSlipId,
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
            showAccIouSlip(data);
        }
    }

    function showAccIouSlip(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        dropDownEmployee.value(entity.employeeId);
        dropDownProject.value(entity.projectId);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");

        dropDownIndent.setDataSource(data.indentList);
        dropDownIndent.value(entity.indentId);
    }

    var strUrl = "${createLink(controller:'accIouSlip',action:  'list')}";
    $("#flex1").flexOptions({url: strUrl});

    if (accIouSlipListModel) {
        $("#flex1").flexAddData(accIouSlipListModel);
    }

</script>
