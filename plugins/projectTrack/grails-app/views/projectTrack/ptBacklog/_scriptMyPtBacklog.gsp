<script language="javascript">

    var myBacklogListModel = false;
    var dropDownStatus, hoursText, dropDownOwner, backlogStatus, entityTypeId;

    $(document).ready(function () {
        onLoadMyBacklogPage();
    });

    // method called on page load
    function onLoadMyBacklogPage() {
        initializeForm($("#myBacklogForm"), onSubmitMyBacklog);
        var output =${output ? output : ''};
        myBacklogListModel = false;
        backlogStatus = $('#backlogStatusAccepted').val();

        if (output.isError) {
            showError(output.message);  // show error message in case of error
        } else {
            myBacklogListModel = output.gridObj;    // set data in a global variable to populate
        }
        /**
         * kendo numeric textbox customized for hours
         */
        $("#hours").kendoNumericTextBox({
            format: "0.00 Hour",
            min: 0,
            step: 0.01,
            spin: function () {
                var value = (this.value() - parseInt(this.value())).toFixed(2);  //get decimal value 1.47 -> 0.47
                if (value == 0.60)
                    this.value((parseInt(this.value()) + 1)); //if value=0.60 increase 1; 1.60->2
                if (value == 0.99)
                    this.value(this.value() - 0.40);  //if value=0.99 reset it to 0.59 ; 2.99->2.59
            },
            change: function () {
                var value = (this.value() - parseInt(this.value())).toFixed(2);
                if (value >= 0.60)
                    this.value((parseInt(this.value()) + 1));  // if value typed is > 0.60 increase 1; 1.75->2
            }
        });

        hoursText = $("#hours").data("kendoNumericTextBox");
        entityTypeId = $("#entityTypeId").val();
        initFlex();
        populateFlex1();
        // update page title
        $(document).attr('title', "MIS - My Task");
        loadNumberedMenu(MENU_ID_PROJECT_TRACK, "#ptBacklog/showMyBacklog");
    }

    // check pre condition before submitting the form
    function executePreCondition() {
        // validate form data
        if (!validateForm(("#myBacklogForm"))) {
            return false;
        }
        return true;
    }

    // method called  on submit of the form
    function onSubmitMyBacklog() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);    // disable the save button
        showLoadingSpinner(true);   // show loading spinner
        var actionUrl = null;
        // set link for create or update if there is data in hidden field id
        if ($('#id').val().isEmpty()) {
            showLoadingSpinner(false);  // stop loading spinner
            setButtonDisabled($('#create'), false);    // disable the save button
            showError("Select a backlog to update");
            return false;
        } else {
            actionUrl = "${createLink(controller:'ptBacklog', action: 'updateMyBacklog')}";
        }

        // fire ajax method
        jQuery.ajax({
            type: 'post',
            data: jQuery("#myBacklogForm").serialize(),  // serialize data from UI and send as parameter
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

                    var previousTotal = parseInt(myBacklogListModel.total);
                    var firstSerial = 1;

                    if (myBacklogListModel.rows.length > 0) {
                        firstSerial = myBacklogListModel.rows[0].cell[0];
                        regenerateSerial($(myBacklogListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;
                    myBacklogListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        myBacklogListModel.rows.pop();
                    }

                    myBacklogListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(myBacklogListModel);

                } else if (newEntry.entity != null) { // updated existing object data in the grid
                    updateListModel(myBacklogListModel, newEntry.entity, 0);
                    $("#flex1").flexAddData(myBacklogListModel);
                    var isOwnerChange = result.isOwnerChanged;
                    var backStatus = result.backlogStatus;
                    if (isOwnerChange == 'true') {
                        reloadGrid();
                    }
                    if (backStatus == 'Completed') {
                        reloadGrid();
                    }
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
        $('#module').html("");
        $('#sprint').html("");
        $('#priority').html("");
        $('#idea').html("");
        clearForm($("#myBacklogForm"), $('#statusId'));  // clear errors & form values
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");   // reset create button text
    }

    // initialize the grid
    function initFlex() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 50, sortable: false, align: "right"},
                        {display: "As a", name: "actor", width: 180, sortable: true, align: "left"},
                        {display: "I want to", name: "purpose", width: 180, sortable: true, align: "left"},
                        {display: "So that", name: "benefit", width: 180, sortable: true, align: "left"},
                        {display: "Priority", name: "priority", width: 50, sortable: true, align: "left"},
                        {display: "Status", name: "status", width: 80, sortable: true, align: "left"},
                        {display: "Hours", name: "hours", width: 50, sortable: true, align: "right"},
                        {display: "A.C. Count", name: "pac_count", width: 70, sortable: true, align: "right"},
                        {display: "Bug Count", name: "bug_count", width: 70, sortable: true, align: "right"},
                        {display: "Unresolved", name: "unresolved", width: 70, sortable: true, align: "right"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/ptBacklog/selectMyBacklog,/ptBacklog/updateMyBacklog">
                        {name: 'Edit', bclass: 'edit', onpress: selectMyBacklog},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/ptAcceptanceCriteria/showForMyBacklog">
                        {name: 'Acceptance Criteria', bclass: 'note', onpress: loadAcceptanceCriteria},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/ptBug/showBugForMyTask">
                        {name: 'Bug', bclass: 'note', onpress: showBug},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/entityNote/show">
                        {name: 'Note', bclass: 'note', onpress: addNote},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/ptReport/showForBacklogDetails">
                        {name: 'Report', bclass: 'note', onpress: showBacklogDetails},
                        </app:ifAllUrl>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Idea", name: "backlog.idea", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All My Task',
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

    function addNote(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'backlog') == false) {
            return;
        }
        showLoadingSpinner(true);
        var backlogId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller: 'entityNote', action: 'show')}?entityId=" + backlogId + "&entityTypeId=" + entityTypeId;
        $.history.load(formatLink(loc));
        return false;
    }

    // reload grid
    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    // custom populate the grid
    function onLoadListJSON(data) {
        if (data.isError) {
            showError(data.message);
            myBacklogListModel = null;
        } else {
            myBacklogListModel = data;
        }
        return data;
    }

    // load acceptance criteria for selected backlog
    function loadAcceptanceCriteria(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'backlog') == false) {
            return;
        }
        showLoadingSpinner(true);
        var backlogId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller: 'ptAcceptanceCriteria', action: 'showForMyBacklog')}?backlogId=" + backlogId;
        $.history.load(formatLink(loc));
        return false;
    }

    // select MyBacklog object for update
    function selectMyBacklog(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'backlog') == false) {
            return;
        }
        var ids = $('.trSelected', grid);
        var statusId = $(ids[0]).find('td').eq(5).find('div').text();
        if (backlogStatus == statusId) {
            showError("Accepted backlog(s) couldn't be updated.");
            return;
        }
        resetForm();
        showLoadingSpinner(true);
        var id = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'ptBacklog', action: 'selectMyBacklog')}?id=" + id,
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
            showPtBacklog(data);
        }
    }

    // show property of ptBacklog object on UI
    function showPtBacklog(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        dropDownStatus.value(entity.statusId);
        dropDownOwner.value(entity.ownerId);
        $('#module').html(data.module);
        $('#sprint').html(data.sprint);
        $('#priority').html(data.priority);
        $('#idea').html(entity.idea);
        hoursText.value(entity.hours);
        $('#useCaseId').val(entity.useCaseId);
        $('#url').val(entity.url);
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");   // change create button text to update
    }

    function showBug(com, grid) {
        if ((executeCommonPreConditionForSelect($('#flex1'), 'backlog') == false)) {
            return;
        }
        showLoadingSpinner(true);
        var backlogId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller: 'ptBug', action: 'showBugForMyTask')}?backlogId=" + backlogId;
        $.history.load(formatLink(loc));
        return false;
    }

    // set link to grid url to populate data
    function populateFlex1() {
        var strUrl = "${createLink(controller:'ptBacklog',action:  'listMyBacklog')}";
        $("#flex1").flexOptions({url: strUrl});

        if (myBacklogListModel) {
            $("#flex1").flexAddData(myBacklogListModel);
        }
    }

    function showBacklogDetails(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'Backlog') == false) {
            return;
        }
        showLoadingSpinner(true);
        var backlogId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller: 'ptReport', action: 'showForBacklogDetails')}?backlogId=" + backlogId + "&leftMenu=" + 'ptBacklog/showMyBacklog';
        $.history.load(formatLink(loc));
        return false;
    }

</script>
