<script language="javascript">
    var financialYearListModel = false;
    var entityTypeId;

    $(document).ready(function () {
        onLoadFinancialYearPage();
    });

    function onLoadFinancialYearPage() {
        initializeForm($("#financialYearForm"), onSubmitFinancialYear);

        var output =${output ? output : ''};
        if (output.isError) {
            showError(output.message);
        } else {
            financialYearListModel = output.financialYearList;
        }
        entityTypeId = $("#entityTypeId").val();
        initFlexGrid()
        populateFlexGrid()
        // update page title
        $(document).attr('title', "MIS - Create Financial Year");
        loadNumberedMenu(MENU_ID_ACCOUNTING, "#accFinancialYear/show");
    }

    function executePreCondition() {
        if (!validateForm($("#financialYearForm"))) {
            return false;
        }
        if (!customValidateDate($("#startDate"), 'Start Date', $("#endDate"), 'End Date')) {
            return false;
        }
        return true;
    }

    function onSubmitFinancialYear() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'accFinancialYear', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'accFinancialYear', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#financialYearForm").serialize(),
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
                var resultMap = result.resultMap;
                if ($('#id').val().isEmpty() && resultMap.entity != null) { // newly created

                    var previousTotal = parseInt(financialYearListModel.total);
                    var firstSerial = 1;

                    if (financialYearListModel.rows.length > 0) {
                        firstSerial = financialYearListModel.rows[0].cell[0];
                        regenerateSerial($(financialYearListModel.rows), 0);
                    }
                    resultMap.entity.cell[0] = firstSerial;
                    financialYearListModel.rows.splice(0, 0, resultMap.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        financialYearListModel.rows.pop();
                    }

                    financialYearListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(financialYearListModel);

                } else if (resultMap.entity != null) { // updated existing
                    updateListModel(financialYearListModel, resultMap.entity, 0);
                    $("#flex1").flexAddData(financialYearListModel);
                }

                resetForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetForm() {
        clearForm($("#financialYearForm"), $('#startDate'));
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function initFlexGrid() {
        $('#flex1').flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 35, sortable: false, align: "right"},
                        {display: "ID", name: "id", width: 30, sortable: false, align: "right", hide: true},
                        {display: "Start Date", name: "startDate", width: 120, sortable: true, align: "left"},
                        {display: "End Date", name: "endDate", width: 120, sortable: true, align: "left"},
                        {display: "Current", name: "isCurrent", width: 80, sortable: true, align: "left"},
                        {display: "Created By", name: "createdBy", width: 180, sortable: true, align: "left"},
                        {display: "Updated By", name: "updatedBy", width: 180, sortable: true, align: "left"},
                        {display: "Content Count", name: "contentCount", width: 110, sortable: false, align: "right"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/accFinancialYear/select,/accFinancialYear/update">
                        {name: 'Edit', bclass: 'edit', onpress: selectFinancialYear},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/accFinancialYear/delete">
                        {name: 'Delete', bclass: 'delete', onpress: deleteFinancialYear},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/accFinancialYear/setCurrentFinancialYear">
                        {name: 'Set Current', bclass: 'approve', onpress: setIsCurrent},
                        </app:ifAllUrl>
                        <sec:access url="/entityContent/show">
                        {name: 'Attachment(s)', bclass: 'attachment', onpress: addContent},
                        </sec:access>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    sortname: "id",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Financial Years',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 15,
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
            financialYearListModel = null;
        } else {
            financialYearListModel = data;
        }
        return data;
    }

    function deleteFinancialYear(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var yearId = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller:'accFinancialYear', action:  'delete')}?id=" + yearId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'financial year') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected Financial Year?')) {
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
            financialYearListModel.total = parseInt(financialYearListModel.total) - 1;
            removeEntityFromGridRows(financialYearListModel, selectedRow);
        } else {
            showError(data.message);
        }
    }

    function selectFinancialYear(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'financial year') == false) {
            return;
        }

        resetForm();
        showLoadingSpinner(true);
        var yearId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'accFinancialYear', action: 'select')}?id=" + yearId,
            success: executePostConditionForEdit,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function setIsCurrent(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'financial year') == false) {
            return;
        }

        showLoadingSpinner(true);
        var acFinancialYearId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'accFinancialYear', action:'setCurrentFinancialYear')}?id=" + acFinancialYearId,
            success: executePostConditionForIsCurrent,
            complete: function (XMLHttpOrder, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConditionForIsCurrent(result) {
        if (result.isError) {
            showError(result.message);
            showLoadingSpinner(false);
        } else {
            try {
                financialYearListModel = result.financialYearList;
                $("#flex1").flexAddData(financialYearListModel);
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function executePostConditionForEdit(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            showFinancialYear(data);
        }
    }

    function showFinancialYear(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#startDate').val(data.startDate);
        $('#endDate').val(data.endDate);

        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function populateFlexGrid() {
        var strUrl = "${createLink(controller:'accFinancialYear',action:  'list')}";
        $("#flex1").flexOptions({url: strUrl});

        if (financialYearListModel) {
            $("#flex1").flexAddData(financialYearListModel);
        }
    }
    // add attachment to financial year
    function addContent(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'financial year') == false) {
            return;
        }
        showLoadingSpinner(true);
        var financialYearId = getSelectedIdFromGrid($('#flex1'));

        var loc = "${createLink(controller:'entityContent', action: 'show')}?entityId=" + financialYearId + "&entityTypeId=" + entityTypeId;
        $.history.load(formatLink(loc));
        return false;
    }

</script>
