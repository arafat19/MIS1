<script type="text/javascript">
    var gridModelShellScript;

    $(document).ready(function () {
        onLoadShellScript();
    });

    function onLoadShellScript() {
        // initialize form with kendo validator & bind onSubmit event
        initializeForm($('#shellScriptForm'), onSubmitShellScript);

        var output =${modelJson ? modelJson : ''};
        if (output.isError) {
            showError(output.message);
            return;
        }

        gridModelShellScript = output.gridObj;
        initFlex();
        $('#evaluatedScript').css('height',$('#gridContainer').height());
        setUrlShellScriptGrid();

        // update page title
        $(document).attr('title', "MIS - Create Shell Script");
        loadNumberedMenu(MENU_ID_APPLICATION, "#appShellScript/show");
    }

    function onSubmitShellScript() {
        if (!validateForm($('#shellScriptForm'))) {
            return false;
        }
        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'appShellScript', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'appShellScript', action:  'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#shellScriptForm").serialize(),
            url: actionUrl,
            success: function (data, textStatus) {
                executePostConForSubmitShellScript(data);
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

    function executePostConForSubmitShellScript(data) {
        if (data.isError) {
            showError(data.message);
            showLoadingSpinner(false);
        } else {
            try {
                var newEntry = data.entity;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created

                    var previousTotal = parseInt(gridModelShellScript.total);
                    var firstSerial = 1;

                    if (gridModelShellScript.rows.length > 0) {
                        firstSerial = gridModelShellScript.rows[0].cell[0];
                        regenerateSerial($(gridModelShellScript.rows), 0);
                    }
                    newEntry.cell[0] = firstSerial;

                    gridModelShellScript.rows.splice(0, 0, newEntry);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        gridModelShellScript.rows.pop();
                    }

                    gridModelShellScript.total = ++previousTotal;
                    $("#flex1").flexAddData(gridModelShellScript);

                } else if (newEntry != null) { // updated existing
                    updateListModel(gridModelShellScript, newEntry, 0);
                    $("#flex1").flexAddData(gridModelShellScript);
                }

                clearShellScriptForm();
                showSuccess(data.message);

            } catch (e) {
                // Do Nothing
            }
        }
    }

    function customPopulateGrid(data) {
        if (data.isError) {
            showError(data.message);
            gridModelShellScript = getEmptyGridModel();
        } else {
            gridModelShellScript = data.gridObj;
        }
        $("#flex1").flexAddData(gridModelShellScript);
    }

    function editShellScript(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'shell script') == false) {
            return;
        }
        showLoadingSpinner(true);
        var shellScriptId = getSelectedIdFromGrid($('#flex1'));
        var url = "${createLink(controller: 'appShellScript', action: 'select')}?id=" + shellScriptId;
        $.ajax({
            url: url,
            success: executePostConForEdit,
            complete: onCompleteAjaxCall,
            dataType: 'json',
            type: 'post'
        });
    }

    function evaluateShellScript(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'shell script') == false) {
            return;
        }
        showLoadingSpinner(true);
        var shellScriptId = getSelectedIdFromGrid($('#flex1'));
        var url = "${createLink(controller: 'appShellScript', action: 'evaluate')}?id=" + shellScriptId;
        $.ajax({
            url: url,
            success: executePostConForEvaluate,
            complete: onCompleteAjaxCall,
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConForEdit(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            showShellScriptInfo(data);
        }
    }
    function executePostConForEvaluate(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            $('#evaluatedScript').val(data.output);
        }
    }

    function showShellScriptInfo(data) {
        clearShellScriptForm();
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#name').val(entity.name);
        $('#script').val(entity.script);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function clearShellScriptForm() {
        clearForm($("#shellScriptForm"), $('#name'));
        $('#evaluatedScript').val('');
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'shell script') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected shell script?')) {
            return false;
        }
        return true;
    }

    function deleteShellScript(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var shellScriptId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller: 'appShellScript', action: 'delete')}?id=" + shellScriptId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConditionForDelete(data) {
        if (data.isError) {
            showError(data.message);
            return;
        } else {
            var selectedRow = null;
            $('.trSelected', $('#flex1')).each(function (e) {
                selectedRow = $(this).remove();
            });
            clearShellScriptForm();
            $('#flex1').decreaseCount(1);
            showSuccess(data.message);
            gridModelShellScript.total = parseInt(gridModelShellScript.total) - 1;
            removeEntityFromGridRows(gridModelShellScript, selectedRow);
        }
    }

    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function setUrlShellScriptGrid() {
        var strUrl = "${createLink(controller:'appShellScript', action: 'list')}";
        $("#flex1").flexOptions({url: strUrl});
        if (gridModelShellScript) {
            $('#flex1').flexAddData(gridModelShellScript);
        }
    }

    function initFlex() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 50, sortable: false, align: "left"},
                        {display: "Name", name: "name", width: 160, sortable: false, align: "left"},
                        {display: "Script", name: "script", width: 284, sortable: false, align: "left"}
                    ],
                    buttons: [
                        {name: 'Edit', bclass: 'edit', onpress: editShellScript},
                        {name: 'Delete', bclass: 'delete', onpress: deleteShellScript},
                        {name: 'Evaluate', bclass: 'evalueate', onpress: evaluateShellScript},
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    sortname: "name",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Shell Script',
                    useRp: true,
                    rp: 15,
                    rpOptions: [15, 20, 25],
                    showTableToggleBtn: false,
                    height: getGridHeight() - 25,
                    customPopulate: customPopulateGrid,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);
                    }
                }
        );
    }

</script>
