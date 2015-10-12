<script type="text/javascript">
    var modelJsonForSprintList;

    jQuery(function ($) {
        modelJsonForSprintList = ${output};

        onLoadSprintList();
        initCurrentSprintListGrid();
        populateCurrentSprintListGrid(modelJsonForSprintList);
    });

    function onLoadSprintList() {
        // update page title
        $(document).attr('title', "MIS - Current Sprint List");
        loadNumberedMenu(MENU_ID_QS, "#budgSprint/showForCurrentSprint");
    }

    function populateCurrentSprintListGrid(data) {
        var url = "${createLink(controller:'budgSprint', action: 'listForCurrentSprint')}";
        $("#flex1").flexOptions({url: url});
        onLoadCurrentSprintListJSON(data);
    }

    function initCurrentSprintListGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 40, sortable: false, align: "right"},
                        {display: "ID", name: "id", width: 40, sortable: false, align: "right", hide: true},
                        {display: "Project", name: "project_name", width: 300, sortable: false, align: "left"},
                        {display: "Sprint", name: "sprint", width: 300, sortable: true, align: "left"},
                        {display: "Task(s)", name: "task_count", width: 100, sortable: true, align: "right"}
                    ],
                    buttons: [
                        {name: 'Details', bclass: 'details', onpress: getDetails},
                        <app:ifAllUrl urls="/budgReport/showBudgetSprint">
                        {name: 'Report', bclass: 'note', onpress: sprintReport},
                        </app:ifAllUrl>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadCurrentSprintGrid},
                        {separator: true}
                    ],
                    sortname: "project_name",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Current Sprint List',
                    useRp: true,
                    rp: 25,
                    rpOptions: [15, 20, 25, 30],
                    showTableToggleBtn: false,
                    height: getGridHeight(5),
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                        checkGrid();
                    },
                    customPopulate: onLoadCurrentSprintListJSON
                }
        );

    }

    function sprintReport() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'sprint') == false) {
            return;
        }
        showLoadingSpinner(true);
        var sprintId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller:'budgReport', action: 'showBudgetSprint')}?sprintId=" + sprintId;
        $.history.load(formatLink(loc));
        return false;
    }

    function reloadCurrentSprintGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function onLoadCurrentSprintListJSON(data) {
        if (data.isError) {
            showError(data.message);
            return null;
        }
        $("#flex1").flexAddData(data.lstCurrentSprint);
    }

    function getDetails() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'sprint') == false) {
            return;
        }
        showLoadingSpinner(true);
        var sprintId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller:'budgTask', action: 'showTaskForSprint')}?sprintId=" + sprintId;
        $.history.load(formatLink(loc));
        return false;
    }

    function checkGrid() {
        var rows = $('table#flex1 > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('No current sprint found');
        }
    }

    function doGridEmpty() {
        var emptyGridModel = getEmptyGridModel();
        $("#flex1").flexAddData(emptyGridModel);
        $("#flex1").flexOptions({url: false, newp: 1, rp: 20}).flexReload();
        $('select[name=rp]').val(20);
    }

</script>