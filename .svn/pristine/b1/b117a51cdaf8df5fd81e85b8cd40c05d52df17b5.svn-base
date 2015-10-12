<script language="javascript">

    var output =${output ? output : ''};
    var entityTypeId, appUserEntityTypeId, startDate, endDate;
    var gridProject;

    $(document).ready(function () {
        onLoadProjectPage();
        initProjectGrid();
    });

    function onLoadProjectPage() {
        // initialize form with kendo validator & bind onSubmit method
        initializeForm($("#projectForm"), onSubmitProject);

        if (output.isError) {
            showError(output.message);
        }

        // update page title
        $(document).attr('title', "MIS - Create Project");
        loadNumberedMenu(MENU_ID_APPLICATION, "#project/show");
        entityTypeId = $("#entityTypeId").val();
        appUserEntityTypeId = $("#appUserEntityTypeId").val();
        startDate = $('#startDate').attr('value');
        endDate = $('#endDate').attr('value');
    }
    function executePreCondition() {
        if (!validateForm($("#projectForm"))) {   // check kendo validation
            return false;
        }

        if (!customValidateDate($("#startDate"), 'Start date', $("#endDate"), 'end date')) {
            return false;
        }

        return true;
    }

    function onSubmitProject() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('.save'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'project', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'project', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#projectForm").serialize(),
            url: actionUrl,
            success: function (data, textStatus) {
                executePostCondition(data);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
            },
            complete: function (XMLHttpRequest, textStatus) {
                setButtonDisabled($('.save'), false);
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
                var newEntry = result.project;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created
                    var gridData = gridProject.dataSource.data();
                    gridData.unshift(newEntry);
                    //gridProject.dataSource._total=gridProject.dataSource.total() +1;
                    //gridData.pop();
                } else if (newEntry != null) { // updated existing
                    var selectedRow = gridProject.select();
                    var allItems = gridProject.items();
                    var selectedIndex = allItems.index(selectedRow);
                    gridProject.removeRow(selectedRow);
                    gridProject.dataSource.insert(selectedIndex, newEntry);
                }
                resetForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetForm() {
        // clear all errors, validation messages & form values and bind onFocus method
        clearForm($("#projectForm"), $('#name'));
        $("#startDate").val(startDate);
        $("#endDate").val(endDate);
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }


    function addContent() {
        if (executeCommonPreConditionForSelectKendo(gridProject, 'project') == false) {
            return;
        }
        showLoadingSpinner(true);
        var projectId = getSelectedIdFromGridKendo(gridProject);
        var loc = "${createLink(controller:'entityContent', action: 'show')}?entityId=" + projectId + "&entityTypeId=" + entityTypeId;
        $.history.load(formatLink(loc));
        return false;
    }

    function addUserProject() {
        if (executeCommonPreConditionForSelectKendo(gridProject, 'project') == false) {
            return;
        }
        showLoadingSpinner(true);
        var projectId = getSelectedIdFromGridKendo(gridProject);
        var loc = "${createLink(controller:'appUserEntity', action: 'show')}?entityId=" + projectId + "&entityTypeId=" + appUserEntityTypeId;
        $.history.load(formatLink(loc));
        return false;
    }

    function reloadGrid() {
        gridProject.dataSource.filter([]);
    }

    function deleteProject() {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true); // Spinner Show on AJAX Call
        var projectId = getSelectedIdFromGridKendo(gridProject);
        $.ajax({
            url: "${createLink(controller:'project', action: 'delete')}?id=" + projectId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelectKendo(gridProject, 'project') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected Project?')) {
            return false;
        }
        return true;
    }

    <%-- removing selected row and clean input form --%>
    function executePostConditionForDelete(data) {
        if (data.deleted == true) {
            var row = gridProject.select();
            row.each(function () {
                gridProject.removeRow($(this));
            });
            resetForm();
            showSuccess(data.message);
        } else {
            // show delete error
            showError(data.message);
        }
    }

    function editProject() {
        if (executeCommonPreConditionForSelectKendo(gridProject, 'project') == false) {
            return;
        }
        resetForm();
        showLoadingSpinner(true);
        var projectId = getSelectedIdFromGridKendo(gridProject);
        $.ajax({
            url: "${createLink(controller:'project', action: 'select')}?id="
                    + projectId,
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
            showProject(data);
        }
    }

    function showProject(data) {
        var entity = data.entity
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#name').val(entity.name);
        $('#code').val(entity.code);
        $('#description').val(entity.description);
        $('#isApproveInFromSupplier').attr('checked', entity.isApproveInFromSupplier);
        $('#isApproveInFromInventory').attr('checked', entity.isApproveInFromInventory);
        $('#isApproveInvOut').attr('checked', entity.isApproveInvOut);
        $('#isApproveConsumption').attr('checked', entity.isApproveConsumption);
        $('#isApproveProduction').attr('checked', entity.isApproveProduction);
        $('#startDate').val(data.startDate);
        $('#endDate').val(data.endDate);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }


    function initProjectGrid() {
        $("#gridProject").kendoGrid({
            dataSource: {
                transport: {
                    read: {
                        url: "/project/list",
                        dataType: "json",
                        type:"post"
                    }
                },
                schema: {
                    data: "data",
                    total: "total",
                    model: {
                        fields: {
                            id: {type: "number"},
                            createdOn: {type: "date"}
                        }
                    }
                },
                pageSize: 10,
                serverPaging: true,
                serverSorting: true,
                serverFiltering: true,
                sort: { field: "name", dir: "asc" }
            },
            height: getGridHeightKendo($('#gridProject')),
            sortable: true,
            selectable: true,
            resizable: true,
            pageable: {
                refresh: true,
                pageSizes: true,
                buttonCount: 4
            },
            columns: [
                {field: "id", title: "ID", width: 20, hidden: true, sortable: true, filterable: false},
                {field: "name", title: "Name", width: 200, filterable: true },
                {field: "code", title: "Code", width: 50, filterable: false },
                {field: "description", title: "Description", width: 200, sortable: false, filterable: false},
                {field: "contentCount", title: "Content Count", width: 70, sortable: true, filterable: false},
                {field: "createdOn", title: "Created On", width: 100, sortable: true, format: "{0:dd-MMM-yy [hh:mm:ss]}", filterable: false}
            ], filterable: {
                extra: false,
                operators: {
                    string: {
                        contains: "Contains"
                    }
                }
            },
            toolbar: [
                <app:ifAllUrl urls="/project/select,/project/update">
                {name: "edit", text: "Edit", imageClass: "k-icon k-i-pencil"},
                </app:ifAllUrl>
                <sec:access url="/project/delete">
                {name: "delete", text: "Delete", imageClass: "k-icon k-i-close"},
                </sec:access>
                <app:ifAllUrl urls="/appUserEntity/show">
                {name: "add-user", text: "User", imageClass: "k-icon k-i-plus"},
                </app:ifAllUrl>
                <sec:access url="/entityContent/show">
                {name: "attach", text: "Attachment", imageClass: "k-icon k-i-folder-add"},
                </sec:access>
                {name: "clear-result", text: "Clear Result", imageClass: "k-icon k-i-funnel-clear"}
            ]
        });
        gridProject = $("#gridProject").data("kendoGrid");
        $(".k-grid-edit").click(function (e) {
            e.preventDefault();
            editProject();
        });
        $(".k-grid-delete").click(function (e) {
            e.preventDefault();
            deleteProject();
        });
        $(".k-grid-add-user").click(function (e) {
            e.preventDefault();
            addUserProject();
        });
        $(".k-grid-attach").click(function (e) {
            e.preventDefault();
            addContent();
        });
        $(".k-grid-clear-result").click(function (e) {
            e.preventDefault();
            reloadGrid();
        });
    }


</script>
