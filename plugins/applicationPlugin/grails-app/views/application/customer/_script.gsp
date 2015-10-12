<script language="javascript">
    var customerListModel = false;

    $(document).ready(function () {
        onLoadCustomerPage();
    });

    function onLoadCustomerPage() {
        // initialize form with kendo validator & bind onSubmit method
        initializeForm($("#customerForm"), onSubmitCustomer);
        var output =${output ? output : ''};

        if (output.isError) {
            showError(output.message);
        } else {
            customerListModel = output.customerList;
        }

        // update page title
        $(document).attr('title', "MIS - Create Customer");
        loadNumberedMenu(MENU_ID_APPLICATION, "#customer/show");
    }

    function executePreCondition() {
        if (!validateForm($("#customerForm"))) {
            return false;
        }
        if (!checkCustomDate($("#dateOfBirth"), "Birth ")) {
            return false;
        }
        return true;
    }

    function onSubmitCustomer() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'customer', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'customer', action:  'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#customerForm").serialize(),
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
                var newEntry = result.customer;
                if ($('#id').val().isEmpty() && newEntry.entity != null) { // newly created

                    var previousTotal = parseInt(customerListModel.total);
                    var firstSerial = 1;

                    if (customerListModel.rows.length > 0) {
                        firstSerial = customerListModel.rows[0].cell[0];
                        regenerateSerial($(customerListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;
                    customerListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        customerListModel.rows.pop();
                    }

                    customerListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(customerListModel);

                } else if (newEntry.entity != null) { // updated existing
                    updateListModel(customerListModel, newEntry.entity, 0);
                    $("#flex1").flexAddData(customerListModel);
                }

                resetForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetForm() {
        clearForm($("#customerForm"), $('#fullName'));
        $("#create").html("<span class='k-icon k-i-plus'></span>Create");
    }

    $("#flex1").flexigrid
    (
            {
                url: false,
                dataType: 'json',
                colModel: [
                    {display: "Serial", name: "serial", width: 30, sortable: false, align: "right", hide: true},
                    {display: "Customer ID", name: "id", width: 80, sortable: false, align: "right", hide: false},
                    {display: "Full Name", name: "fullName", width: 180, sortable: true, align: "left"},
                    {display: "Nick Name", name: "nickName", width: 180, sortable: true, align: "left"} ,
                    {display: "Email", name: "email", width: 180, sortable: true, align: "left"},
                    {display: "Date Of Birth", name: "dateOfBirth", width: 130, sortable: true, align: "left"},
                    {display: "Address", name: "address", width: 180, sortable: true, align: "left"}
                ],
                buttons: [
                    <app:ifAllUrl urls="/customer/select,/customer/update">
                    {name: 'Edit', bclass: 'edit', onpress: selectCustomer},
                    </app:ifAllUrl>
                    <sec:access url="/customer/delete">
                    {name: 'Delete', bclass: 'delete', onpress: deleteCustomer},
                    </sec:access>
                    {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                    {separator: true}
                ],
                searchitems: [
                    {display: "Full Name", name: "fullName", width: 180, sortable: true, align: "left"}
                ],
                sortname: "id",
                sortorder: "desc",
                usepager: true,
                singleSelect: true,
                title: 'All Customers',
                useRp: true,
                rp: 15,
                showTableToggleBtn: false,
                height: getGridHeight() - 20,
                afterAjax: function (XMLHttpRequest, textStatus) {
                    afterAjaxError(XMLHttpRequest, textStatus);
                    showLoadingSpinner(false);
                },
                preProcess: onLoadCustomerListJSON
            }
    );

    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function onLoadCustomerListJSON(data) {
        if (data.isError) {
            showError(data.message);
            customerListModel = null;
        } else {
            customerListModel = data;
        }
        return data;
    }

    function deleteCustomer(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var customerId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'customer', action: 'delete')}?id=" + customerId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'customer') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected Customer?')) {
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
            customerListModel.total = parseInt(customerListModel.total) - 1;
            removeEntityFromGridRows(customerListModel, selectedRow);
        } else {
            showError(data.message);
        }
    }

    function selectCustomer(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'customer') == false) {
            return;
        }

        resetForm();
        showLoadingSpinner(true);
        var customerId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'customer', action: 'select')}?id=" + customerId,

            success: executePostConditionForEdit,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForEdit(ids) {
        if (ids.length == 0) {
            showError("Please select a Customer to edit");
            return false;
        }
        return true;
    }

    function executePostConditionForEdit(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            showCustomer(data);
        }
    }

    function showCustomer(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#fullName').val(entity.fullName);
        $('#nickName').val(entity.nickName);
        $('#email').val(entity.email);
        $('#dateOfBirth').val(data.dateOfBirth);
        $('#phoneNo').val(entity.phoneNo);
        $('#address').val(entity.address);
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");
    }

    <sec:access url="/customer/list">
    var strUrl = "${createLink(controller:'customer', action: 'list')}";
    $("#flex1").flexOptions({url: strUrl});

    if (customerListModel) {
        $("#flex1").flexAddData(customerListModel);
    }
    </sec:access>

</script>
