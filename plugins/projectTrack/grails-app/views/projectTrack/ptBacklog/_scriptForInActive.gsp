<script language="javascript">
    var storyListModel = false;
    var dropDownProject, dropDownSprint, dropDownStatus, projectId, sprintId, statusId;

    $(document).ready(function () {
        onLoadStoryPage();
    });

    // method called on page load
    function onLoadStoryPage() {
        initializeForm($("#storyForm"), onSearchStory);

        $("#sprintId").kendoDropDownList({
            dataTextField: "name",
            dataValueField: "id"
        });

        dropDownSprint = $("#sprintId").data("kendoDropDownList");
        dropDownSprint.setDataSource(getKendoEmptyDataSource(dropDownSprint, null));

        storyListModel = false;
        initFlex();

        // update page title
        $(document).attr('title', "MIS - Search Task");
        loadNumberedMenu(MENU_ID_PROJECT_TRACK, "#ptBacklog/showForInActive");
    }

    // update sprint on change of project
    function updateSprint() {
        var projectId = dropDownProject.value();
        if (projectId == '') {
            dropDownSprint.value('');
            dropDownSprint.setDataSource(getKendoEmptyDataSource());
            return false;
        }
        showLoadingSpinner(true);
        var params = "?projectId=" + projectId;
        $.ajax({
            url: "${createLink(controller:'ptSprint', action: 'listInActiveSprintByProjectId')}" + params,
            success: function (data, textStatus) {
                dropDownSprint.value('');
                dropDownSprint.setDataSource(data.sprintList);
            },
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });

    }

    // check pre condition before submitting the form
    function executePreConditionForSearchStory() {
        if (dropDownProject.value() == '') {
            showError('Please select a project.');
            return false;
        }
        if (dropDownSprint.value() == '') {
            showError('Please select a sprint.');
            return false;
        }
        return true;
    }

    // perform on click of search button
    function onSearchStory() {
        if (executePreConditionForSearchStory() == false) {
            return false;
        }
        projectId = dropDownProject.value();
        sprintId = dropDownSprint.value();
        statusId = dropDownStatus.value();

        setButtonDisabled($('#search'), true);  // disable the save button
        showLoadingSpinner(true);   // show loading spinner

        var params = "?sprintId=" + sprintId + "&statusId=" + statusId;
        var strUrl = "${createLink(controller:'ptBacklog', action: 'listForInActive')}" + params;

        $.ajax({
            url: strUrl,
            success: function (data, textStatus) {
                executePostConditionForStory(data);
            },
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
                setButtonDisabled($('#search'), false);
            },
            dataType: 'json',
            type: 'post'
        });
        return false;
    }

    function executePostConditionForStory(result) {
        $("#flex1").flexAddData(getEmptyGridModel());
        if (result.isError) {
            showError(result.message);
            return false;
        }
        storyListModel = result.gridObj;
        populateFlex1();
    }

    // initialize the grid
    function initFlex() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 40, sortable: false, align: "right"},
                        {display: "Type", name: "type", width: 40, sortable: false, align: "left"},
                        {display: "Title", name: "idea", width: 500, sortable: false, align: "left"},
                        {display: "Status", name: "status", width: 180, sortable: false, align: "left"},
                        {display: "Owner", name: "user", width: 180, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/ptBug/show">
                        {name: 'View Bug List', bclass: 'note', onpress: showBug},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/ptReport/showForBacklogDetails,/ptBug/showBugDetails">
                        {name: 'Report', bclass: 'note', onpress: viewReport},
                        </app:ifAllUrl>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Task',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 20,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
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
            storyListModel = getEmptyGridModel();
        } else {
            storyListModel = data.gridObj;
        }
        $("#flex1").flexAddData(storyListModel);
    }

    // load bug for selected backlog
    function showBug(com, grid) {
        if ((executeCommonPreConditionForSelect($('#flex1'), 'task') == false)) {
            return;
        }
        var ids = $('.trSelected', grid);
        var type = $.trim($(ids[0]).find('td').eq(1).find('div').text());
        if (type == "Bug") {
            showError('Select a task to view bug list');
            return;
        }
        var backlogId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller: 'ptBug', action: 'show')}?backlogId=" + backlogId + "&leftMenu=" + 'ptBacklog/showForInActive';
        showLoadingSpinner(true);
        $.history.load(formatLink(loc));
        return false;
    }

    // reload grid
    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    // set link to grid url to populate data
    function populateFlex1() {
        var params = "?sprintId=" + sprintId + "&statusId=" + statusId;
        var strUrl = "${createLink(controller:'ptBacklog', action: 'listForInActive')}" + params;
        $("#flex1").flexOptions({url: strUrl});
        if (storyListModel) {
            $("#flex1").flexAddData(storyListModel);
        }
    }

    function viewReport(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'task') == false) {
            return;
        }
        var ids = $('.trSelected', grid);
        var loc;
        var id = getSelectedIdFromGrid($('#flex1'));
        var type = $.trim($(ids[0]).find('td').eq(1).find('div').text());
        if (type == "Bug") {
            loc = "${createLink(controller: 'ptBug', action: 'showBugDetails')}?id=" + id + "&leftMenu=" + 'ptBacklog/showForInActive';
        }
        if (type == "Task") {
            loc = "${createLink(controller: 'ptReport', action: 'showForBacklogDetails')}?backlogId=" + id + "&leftMenu=" + 'ptBacklog/showForInActive';
        }
        showLoadingSpinner(true);
        $.history.load(formatLink(loc));
        return false;
    }

</script>
