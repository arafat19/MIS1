<script type="text/javascript">
    var output = false
    var themeListModel = false;

    $(document).ready(function () {
        onLoadThemePage()
    });

    function onLoadThemePage() {
        // initialize form with kendo validator & bind onSubmit event
        initializeForm($("#themeForm"), onSubmitTheme);


        output =${output ? output : ''};
        if (output.isError) {
            showError(output.message);
        } else {
            themeListModel = output.themeList;
        }

        initFlexGrid();
        populateFlexGrid();

        // update page title
        $(document).attr('title', "Update Theme Information");
        loadNumberedMenu(MENU_ID_APPLICATION, "#theme/showTheme");

    }
    function executePreCondition() {
        if (!validateForm($("#themeForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitTheme() {
        if (executePreCondition() == false) {
            return false;
        }

        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            showError('Theme is updateable only. Please select from grid to update.');
            return false;
        } else {
            actionUrl = "${createLink(controller: 'theme', action: 'updateTheme')}";
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        jQuery.ajax({
            type: 'post',
            data: jQuery("#themeForm").serialize(),
            url: actionUrl,
            success: function (data, textStatus) {
                executePostCondition(data);
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
                    var previousTotal = parseInt(themeListModel.total);

                    // re-arranging serial
                    var firstSerial = 1;
                    if (themeListModel.rows.length > 0) {
                        firstSerial = themeListModel.rows[0].cell[0];
                        regenerateSerial($(themeListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;
                    themeListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        themeListModel.rows.pop();
                    }

                    themeListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(themeListModel);

                } else if (newEntry.entity != null) { // updated existing
                    updateListModel(themeListModel, newEntry.entity, 0);
                    $("#flex1").flexAddData(themeListModel);
                }

                resetThemeForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetThemeForm() {
        clearForm($("#themeForm"), $('#value'));
        $('#key').text('');
        $('#description').text('');
    }

    function initFlexGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 30, sortable: false, align: "right"},
                        {display: "Key", name: "key", width: 200, sortable: false, align: "left"},
                        {display: "Value", name: "value", width: 250, sortable: false, align: "left"},
                        {display: "Description", name: "description", width: 480, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/theme/selectTheme,/theme/updateTheme">
                        {name: 'Edit', bclass: 'edit', onpress: editTheme},
                        </app:ifAllUrl>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ], searchitems: [
                    {display: "Key", name: "key", width: 180, sortable: true, align: "left"}
                ],
                    sortname: "key",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'Theme Information List',
                    useRp: true,
                    rp: 15,
                    rpOptions: [15, 20, 25],
                    showTableToggleBtn: false,
                    height: getGridHeight() - 20,
                    customPopulate: customPopulateThemeGrid
                }
        );
    }

    function reloadGrid(com, grid) {
        if (com == 'Clear Results') {
            $('#flex1').flexOptions({query: ''}).flexReload();
        }
    }

    function customPopulateThemeGrid(data) {
        if (data.isError) {
            showError(data.message);
            themeListModel = getEmptyGridModel();
        } else {
            themeListModel = data;
        }
        $('#flex1').flexAddData(themeListModel);
        return false;
    }

    function editTheme(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'theme') == false) {
            return;
        }

        resetThemeForm();
        showLoadingSpinner(true);
        var themeId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'theme', action: 'selectTheme')}?id=" + themeId,
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
            showTheme(data);
        }
    }

    function showTheme(data) {
        var entity = data.entity;
        $('#id').val(entity.id);

        $('#key').text(entity.key);
        $('#value').val(entity.value);
        $('#description').text(entity.description);
    }

    function populateFlexGrid() {
        <sec:access url="/theme/listTheme">
        var strUrl = "${createLink(controller:'theme', action: 'listTheme')}";
        $("#flex1").flexOptions({url: strUrl});
        if (themeListModel) {
            $("#flex1").flexAddData(themeListModel);
        }
        </sec:access>
    }

</script>
