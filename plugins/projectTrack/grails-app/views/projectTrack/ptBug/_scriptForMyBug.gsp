<script type="text/javascript">

    var dropDownStatus;
    var ptBugListModel = false;

    $(document).ready(function () {
        onLoadPtBugPage();
    });

    // method called on page load
    function onLoadPtBugPage() {
        initializeForm($("#formMyBug"), onSubmitBug)

        var output =${output ? output : ''};
        if (output.isError) {
            showError(output.message);              // show error message in case of error
        } else {
            ptBugListModel = output.gridObj;        // set data in a global variable to populate
        }
        initFlex();
        populateFlex();

        $(document).attr('title', "MIS - Update Bug");
        loadNumberedMenu(MENU_ID_PROJECT_TRACK, "#ptBug/showMyBug");
    }

    function onSubmitBug() {
        if (executePreCondition() == false) {
            return false;
        }
        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            showError('Bug is updateable only. Please select a row from grid to update');
        } else {
            actionUrl = "${createLink(controller: 'ptBug', action: 'updateBugForMyTask')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#formMyBug").serialize(),
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

    // check pre condition before submitting the form
    function executePreCondition() {
        // validate form data
        if (!validateForm($("#formMyBug"))) {
            return false;
        }
        return true;
    }

    // execute post condition after create or update
    function executePostCondition(result) {
        if (result.isError) {
            showError(result.message);
            showLoadingSpinner(false);
        } else {
            try {
                var newEntry = result;
                if ($('#id').val().isEmpty() && newEntry.entity != null) { // newly created
                    var previousTotal = parseInt(ptBugListModel.total);

                    // re-arranging serial
                    var firstSerial = 1;
                    if (ptBugListModel.rows.length > 0) {
                        firstSerial = ptBugListModel.rows[0].cell[0];
                        regenerateSerial($(ptBugListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;
                    ptBugListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex').countEqualsResultPerPage(previousTotal)) {
                        ptBugListModel.rows.pop();
                    }

                    ptBugListModel.total = ++previousTotal;
                    $("#flex").flexAddData(ptBugListModel);

                } else if (newEntry.entity != null) { // updated existing
                    updateListModel(ptBugListModel, newEntry.entity, 0);
                    $("#flex").flexAddData(ptBugListModel);
                }

                resetMyBugForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    // reset the form
    function resetMyBugForm() {
        clearForm($("#formMyBug"), dropDownStatus);
        $("#title").text('');
        $("#stepToReproduce").text('');
        $("#severity").text('');
        $("#type").text('');
    }

    // initialize the grid
    function initFlex() {
        $("#flex").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 40, sortable: false, align: "right"},
                        {display: "Bug ID", name: "id", width: 50, sortable: false, align: "right"},
                        {display: "Title", name: "title", width: 180, sortable: false, align: "left"},
                        {display: "Steps To Reproduce", name: "stepToReproduce", width: 200, sortable: false, align: "left"},
                        {display: "Status", name: "status", width: 80, sortable: false, align: "left"},
                        {display: "Severity", name: "severity", width: 70, sortable: false, align: "left"},
                        {display: "Type", name: "type", width: 100, sortable: false, align: "left"},
                        {display: "Created On", name: "createdOn", width: 100, sortable: false, align: "left"},
                        {display: "Created By", name: "createdBy", width: 150, sortable: false, align: "left"},
                        {display: "Attachment", name: "hasAttachment", width: 80, sortable: false, align: "left", hide: true}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/ptBug/selectBugForMyTask,/ptBug/updateBugForMyTask">
                        {name: 'Edit', bclass: 'edit', onpress: selectPtBug},
                        </app:ifAllUrl>
                        {name: 'Report', bclass: 'report', onpress: viewBugDetails},
                        <app:ifAllUrl urls="/ptBug/downloadBugContent">
                        {name: 'Download', bclass: 'report', onpress: downloadContent},
                        </app:ifAllUrl>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Title", name: "title", width: 180, sortable: true, align: "left"},
                        {display: "Steps To Reproduce", name: "step_to_reproduce", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "title",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'Bug Details',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 30,
                    afterAjax: function () {
                        afterAjaxError();
                        showLoadingSpinner(false);
                    },
                    customPopulate: onLoadListJSON
                }
        );
    }

    // custom populate the grid
    function onLoadListJSON(data) {
        if (data.isError) {
            showError(data.message);
            ptBugListModel = getEmptyGridModel();
        } else {
            ptBugListModel = data;
        }
        $("#flex").flexAddData(ptBugListModel);
    }

    function downloadContent(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex'), 'bug') == false) {
            return;
        }
        var hasAttachment = $.trim($('.trSelected').find('td').eq(9).find('div').text());
        if (hasAttachment == 'false') {
            showError('Selected bug has no attachment');
            return;
        }

        var confirmMsg = 'Do you want to download the attachment now?';
        showLoadingSpinner(true);
        var entityId = getSelectedIdFromGrid($('#flex'));
        if (confirm(confirmMsg)) {
            var url = "${createLink(controller:'ptBug', action: 'downloadBugContent')}?entityId=" + entityId;
            document.location = url;
        }
        showLoadingSpinner(false);
        return true;
    }

    // select ptBug object for update
    function selectPtBug(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex'), 'bug') == false) {
            return;
        }
        resetMyBugForm();                    // reset the form
        showLoadingSpinner(true);       // start loading spinner
        var id = getSelectedIdFromGrid($('#flex'));
        // fire ajax method for select
        $.ajax({
            url: "${createLink(controller:'ptBug', action: 'selectBugForMyTask')}?id=" + id,
            success: executePostConditionForEdit,
            complete: function () {
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
            showPtBug(data);
        }
    }

    // show property of selected ptBug object on UI
    function showPtBug(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#title').text(entity.title);
        $('#note').val(entity.note);
        $('#stepToReproduce').text(entity.stepToReproduce);
        $('#severity').text(data.severity);
        $('#type').text(data.type);
        dropDownStatus.value(entity.status);
    }

    // reload grid
    function reloadGrid(com, grid) {
        $('#flex').flexOptions({query: ''}).flexReload();
    }

    // set link to grid url to populate data
    function populateFlex() {
        var strUrl = "${createLink(controller:'ptBug',action:  'listMyBug')}";
        $("#flex").flexOptions({url: strUrl});

        if (ptBugListModel) {
            $("#flex").flexAddData(ptBugListModel);
        }
    }

    function viewBugDetails(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex'), 'bug') == false) {
            return;
        }
        showLoadingSpinner(true);
        var bugId = getSelectedIdFromGrid($('#flex'));
        var loc = "${createLink(controller: 'ptBug', action: 'showBugDetails')}?id=" + bugId + "&leftMenu=" + '#ptBug/showMyBug';
        $.history.load(formatLink(loc));
        return false;
    }

</script>