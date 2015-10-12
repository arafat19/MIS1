<script language="javascript">
    var dropDownDesignation;
    var employeeListModel = false;

    $(document).ready(function () {
        onLoadEmployeePage();
    });

    // method called on page load
    function onLoadEmployeePage() {
        // initialize form with kendo validator & bind onSubmit method
        initializeForm($("#employeeForm"), onSubmitEmployee);
        var output = ${output ? output : ''};

        employeeListModel = false;
        if (output.isError) {
            showError(output.message);  // show error message in case of error
        } else {
            employeeListModel = output.gridObj;    // set data in a global variable to populate
        }
        initFlex();
        populateFlex1();
        $(document).attr('title', "MIS - Create Employee");
        loadNumberedMenu(MENU_ID_APPLICATION, "#employee/show");
    }

    // check pre condition before submitting the form
    function executePreCondition() {
        // validate form data
        if (!validateForm($("#employeeForm"))) {
            return false;
        }
        if (!checkCustomDate($("#dateOfJoin"), "Joining  ")) {
            return false;
        }
        // check validation of birth date
        if ($("#dateOfBirth").val()) {
            if (!checkCustomDate($("#dateOfBirth"), "Birth ")) {
                return false;
            }
            if (!customValidateDate($("#dateOfBirth"), 'Date of Birth', $("#dateOfJoin"), 'Date of Join')) {
                return false;
            }
        }
        return true;
    }

    // method called  on submit of the form
    function onSubmitEmployee() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);    // disable the save button
        showLoadingSpinner(true);   // show loading spinner
        var actionUrl = null;
        // set link for create or update if there is data in hidden field id
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'employee', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'employee', action: 'update')}";
        }

        // fire ajax method
        jQuery.ajax({
            type: 'post',
            data: jQuery("#employeeForm").serialize(),  // serialize data from UI and send as parameter
            url: actionUrl,
            success: function (data, textStatus) {
                executePostCondition(data);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
            },
            complete: function (XMLHttpRequest, textStatus) {
                setButtonDisabled($('#create'), false);   // enable the save button
                showLoadingSpinner(false);  // stop loading spinner
            },
            dataType: 'json'
        });
        return false;
    }

    // execute post condition after create or update
    function executePostCondition(result) {
        if (result.isError) {
            showError(result.message);  // show error message in case of error
            showLoadingSpinner(false);  // stop loading spinner
        } else {
            try {
                var newEntry = result;
                if ($('#id').val().isEmpty() && newEntry.entity != null) { // show newly created object in a grid row

                    var previousTotal = parseInt(employeeListModel.total);
                    var firstSerial = 1;

                    if (employeeListModel.rows.length > 0) {
                        firstSerial = employeeListModel.rows[0].cell[0];
                        regenerateSerial($(employeeListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;
                    employeeListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        employeeListModel.rows.pop();
                    }

                    employeeListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(employeeListModel);

                } else if (newEntry.entity != null) { // updated existing object data in the grid
                    updateListModel(employeeListModel, newEntry.entity, 0);
                    $("#flex1").flexAddData(employeeListModel);
                }

                resetForm();    // reset the form
                showSuccess(result.message);    // show success message
            } catch (e) {
                // Do Nothing
            }
        }
    }

    // reset the form
    function resetForm() {
        clearForm($("#employeeForm"), $('#fullName'));  // clear errors & form values & bind focus event
        $("#create").html("<span class='k-icon k-i-plus'></span>Create");   // reset create button text
    }

    // initialize the grid
    function initFlex() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 30, sortable: false, align: "right"},
                        {display: "Employee ID", name: "id", width: 80, sortable: false, align: "right", hide: true},
                        {display: "Full Name", name: "fullName", width: 180, sortable: true, align: "left"},
                        {display: "Nick Name", name: "nickName", width: 180, sortable: false, align: "left"},
                        {display: "Designation", name: "designation", width: 180, sortable: false, align: "left"},
                        {display: "Mobile No", name: "mobileNo", width: 100, sortable: true, align: "left"},
                        {display: "Email", name: "email", width: 140, sortable: true, align: "left"},
                        {display: "Joining Date", name: "dateOfJoin", width: 100, sortable: true, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/employee/select,/employee/update">
                        {name: 'Edit', bclass: 'edit', onpress: selectEmployee},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/employee/delete">
                        {name: 'Delete', bclass: 'delete', onpress: deleteEmployee},
                        </app:ifAllUrl>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Full Name", name: "fullName", width: 180, sortable: true, align: "left"},
                        {display: "Nick Name", name: "nickName", width: 180, sortable: true, align: "left"},
                        {display: "Mobile No", name: "mobileNo", width: 180, sortable: true, align: "left"},
                        {display: "Email Address", name: "email", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "name",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Employee',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 20,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);
                    },
                    preProcess: onLoadListJSON
                }
        );
    }

    // reload grid
    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    // custom populate the grid
    function onLoadListJSON(data) {
        if (data.isError) {
            showError(data.message);
            employeeListModel = null;
        } else {
            employeeListModel = data;
        }
        return data;
    }

    // delete employee object
    function deleteEmployee(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var employeeId = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller:'employee', action:  'delete')}?id=" + employeeId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    // execute pre condition for delete
    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'employee') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected employee?')) {
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
            employeeListModel.total = parseInt(employeeListModel.total) - 1;
            removeEntityFromGridRows(employeeListModel, selectedRow);
        } else {
            showError(data.message);
        }
    }

    // select employee object for update
    function selectEmployee(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'emplpoyee') == false) {
            return;
        }

        resetForm();
        showLoadingSpinner(true);
        var id = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'employee', action: 'select')}?id=" + id,
            success: executePostConditionForEdit,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    // execute post condition for edit
    function executePostConditionForEdit(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            showEmployee(data);
        }
    }

    // show property of employee object on UI
    function showEmployee(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#fullName').val(entity.fullName);
        $('#nickName').val(entity.nickName);
        dropDownDesignation.value(entity.designationId);
        $('#mobileNo').val(entity.mobileNo);
        $('#email').val(entity.email);
        $('#dateOfBirth').val(data.dateOfBirth);
        $('#dateOfJoin').val(data.dateOfJoin);
        $('#address').val(entity.address);
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");   // change create button text to update
    }

    // set link to grid url to populate data
    function populateFlex1() {
        var strUrl = "${createLink(controller:'employee',action:  'list')}";
        $("#flex1").flexOptions({url: strUrl});

        if (employeeListModel) {
            $("#flex1").flexAddData(employeeListModel);
        }
    }

</script>
