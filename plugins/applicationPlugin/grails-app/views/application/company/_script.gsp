<script language="javascript">
    var validatorCompany, dropDownCountry, uploading;
    var companyListModel = false;

    $(document).ready(function () {
        onLoadCompanyPage();
    });

    function onLoadCompanyPage() {

        // common initializeForm() is not used here due to customValidation/upload
        validatorCompany = $("#companyForm").kendoValidator({
            validateOnBlur: false
        }).data("kendoValidator");

        $("#companyLogo").kendoUpload({
            multiple: false
        });

        var output =${output ? output : ''};
        bindCompanyFormEvents();

        if (output.isError) {
            showError(output.message);
        } else {
            companyListModel = output.companyList;
        }

        initFlexGrid();
        populateFlexGrid();
        // update page title
        $(document).attr('title', "MIS - Create Company");
        loadNumberedMenu(MENU_ID_APPLICATION, "#company/show");
    }


    function bindCompanyFormEvents() {
        var actionUrl = "${createLink(controller: 'company',action: 'update')}";
        $("#companyForm").attr('action', actionUrl);

        $('#companyForm').iframePostForm({
            post: function () {
                uploading = true;
                showLoadingSpinner(true);
                setButtonDisabled($('#create'), true);
            },
            complete: function (response) {
                if (uploading == true) {
                    showLoadingSpinner(false);
                    executePostCondition(response);
                    uploading = false;
                    setButtonDisabled($('#create'), false);
                }
            },
            beforePost: function () {
                if (executePreCondition() == false) {
                    return false;
                }
                return true;
            }
        });
    }

    function executePreCondition() {
        if ($('#id').val().isEmpty()) {
            showError('Company can be updated only. Please select from grid to update.');
            return false;
        }

        // trim field vales before process.
        trimFormValues($("#companyForm"));

        if (!validatorCompany.validate()) {
            return false;
        }
        return true;
    }

    function executePostCondition(data) {
        var result = eval('(' + data + ')');
        if (result.isError) {
            showError(result.message);
            showLoadingSpinner(false);
        } else {
            try {
                var newEntry = result.company;
                if ($('#id').val().isEmpty() && newEntry.entity != null) { // newly created

                    var previousTotal = parseInt(companyListModel.total);
                    var firstSerial = 1;

                    if (companyListModel.rows.length > 0) {
                        firstSerial = companyListModel.rows[0].cell[0];
                        regenerateSerial($(companyListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;

                    companyListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        companyListModel.rows.pop();
                    }
                    companyListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(companyListModel);

                } else if (newEntry.entity != null) { // updated existing
                    updateListModel(companyListModel, newEntry.entity, 0);
                    $("#flex1").flexAddData(companyListModel);
                }

                resetForm();

                showSuccess(result.message);

            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetForm() {
        clearForm($("#companyForm"), dropDownCountry);
        // reset kendo upload
        $(".k-delete").parent().click();
    }

    function initFlexGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 30, sortable: false, align: "right"},
                        {display: "ID", name: "id", width: 30, sortable: false, align: "right", hide: true},
                        {display: "Name", name: "name", width: 250, sortable: true, align: "left"},
                        {display: "Code", name: "code", width: 100, sortable: false, align: "left"},
                        {display: "Web URL", name: "webUrl", width: 200, sortable: false, align: "left"},
                        {display: "Email", name: "email", width: 200, sortable: false, align: "left"},
                        {display: "Country", name: "country", width: 150, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/company/select,/company/update">
                        {name: 'Edit', bclass: 'edit', onpress: selectCompany},
                        </app:ifAllUrl>
                        {name: 'Refresh', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Companies',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 20,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);
                    },
                    preProcess: onLoadCompanyListJSON
                }
        );
    }

    function reloadGrid(com, grid) {
        if (com == 'Refresh') {
            $('#flex1').flexOptions({query: ''}).flexReload();
        }
    }

    function onLoadCompanyListJSON(data) {
        if (data.isError) {
            showError(data.message);
            companyListModel = null;
        } else {
            companyListModel = data;
        }
        return data;
    }

    function deleteCompany(com, grid) {
        var ids = $('.trSelected', grid);
        if (executePreConditionForDelete(ids) == false) {
            return;
        }
        showLoadingSpinner(true);
        var companyId = $(ids[ids.length - 1]).attr('id').replace('row', '');

        $.ajax({
            url: "${createLink(controller:'company', action: 'delete')}?id=" + companyId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete(ids) {
        var delCount = ids.length;
        if (delCount == 0) {
            showError("Please select a Company to delete");
            return false;
        }

        if (!confirm('Are you sure you want to delete the selected Company?')) {
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
            companyListModel.total = parseInt(companyListModel.total) - 1;
            removeEntityFromGridRows(companyListModel, selectedRow);
        } else {
            showError(data.message);
        }
    }

    function selectCompany(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'company') == false) {
            return;
        }
        resetForm();
        showLoadingSpinner(true);
        var companyId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'company', action: 'select')}?id="
                    + companyId,

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
            showError("Please select a Company to edit");
            return false;
        }
        return true;
    }

    function executePostConditionForEdit(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            showCompany(data);
        }
    }

    function showCompany(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#name').val(entity.name);
        $('#code').val(entity.code);
        $('#address1').val(entity.address1);
        $('#address2').val(entity.address2);
        dropDownCountry.value(entity.countryId);
        $('#webUrl').val(entity.webUrl);
        $('#email').val(entity.email);

        var actionUrl = "${createLink(controller: 'company',action: 'update')}";
        $("#companyForm").attr('action', actionUrl);
    }

    function populateFlexGrid() {
        var strUrl = "${createLink(controller:'company', action: 'list')}";
        $("#flex1").flexOptions({url: strUrl});

        if (companyListModel) {
            $("#flex1").flexAddData(companyListModel);
        }
    }

</script>
