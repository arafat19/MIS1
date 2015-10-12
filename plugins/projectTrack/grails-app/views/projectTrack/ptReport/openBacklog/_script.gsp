<script language="javascript">
    var backlogListModel = false;
    var moduleId = 0, projectId;
    var dropDownModule, dropDownProject;

    $(document).ready(function () {
        onLoadBacklogPage();
    });

    // method called on page load
    function onLoadBacklogPage() {

        initializeForm($("#openBacklogForm"), onSubmitBacklog);
        $('.download_icon_set').hide();

        $('#moduleId').kendoDropDownList({
            dataTextField: "name",
            dataValueField: "id"
        });
        dropDownModule = $('#moduleId').data("kendoDropDownList");
        dropDownModule.setDataSource(getKendoEmptyDataSource(dropDownModule, 'ALL'));

        $('#printPdfBtn').click(function () {
            downloadOpenBacklog();
        });

        var output =
        ${output ? output : ''}
        if (output.isError) {
            showError(output.message);  // show error message in case of error
            return false;
        } else {
            backlogListModel = getEmptyGridModel(); // first time empty grid
        }


        initFlex();

        // update page title
        $(document).attr('title', "MIS - Open Backlog");
        loadNumberedMenu(MENU_ID_PROJECT_TRACK, "#ptReport/showReportOpenBacklog");
    }

    function executePreCondition() {
        // validate form data
        if (dropDownProject.value() == '') {
            showError("Please select a project");
            return false;
        }
        return true;
    }
    // method called  on submit of the form
    function onSubmitBacklog() {
        if (executePreCondition() == false) {
            return false;
        }
        setButtonDisabled($('#search'), true);  // disable the search button
        showLoadingSpinner(true);   // show loading spinner
        moduleId = dropDownModule.value();
        projectId = dropDownProject.value();
        doGridEmpty();
        var strUrl = "${createLink(controller:'ptReport',action:  'listReportOpenBacklog')}?moduleId=" + moduleId+"&projectId="+projectId;

        $.ajax({
            url: strUrl,
            success: function (data, textStatus) {
                executePostCondition(data);
            },
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
        setButtonDisabled($('#search'), false);  // enable the search button
        return false;
    }

    function executePostCondition(result) {
        $("#flex1").flexAddData(getEmptyGridModel());
        if (result.isError) {
            showError(result.message);
            return false;
        }
        backlogListModel = result.gridObj;
        if(result.gridObj.rows.length<1){
            showError("No report available");
            return false;
        }
        populateFlex1();
    }

    function downloadOpenBacklog() {
        showLoadingSpinner(true);
        var projectId = dropDownProject.value();
        if (confirm('Do you want to download the PDF now?')) {
            var url = "${createLink(controller: 'ptReport', action: 'downloadOpenBacklogReport')}?moduleId=" + moduleId + "&projectId=" + projectId;
            document.location = url;
        }
        showLoadingSpinner(false);
        return false;
    }

    function resetForm() {
        clearForm($("#openBacklogForm"), dropDownProject);  // clear errors & form values
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
                        {display: "Idea", name: "idea", width: 700, sortable: true, align: "left"},
                        {display: "Priority", name: "priority", width: 120, sortable: true, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/ptReport/showForBacklogDetails">
                        {name: 'Report', bclass: 'note', onpress: showBacklogDetails},
                        </app:ifAllUrl>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],


                    title: 'All Open Backlog',
                    useRp: true,
                    usepager: true,
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

    // reload grid
    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    // custom populate the grid
    function onLoadListJSON(data) {
        if (data.isError) {
            showError(data.message);
            backlogListModel = getEmptyGridModel();
        } else {
            backlogListModel = data.gridObj;
        }
        $("#flex1").flexAddData(backlogListModel);
    }

    // set link to grid url to populate data
    function populateFlex1() {
        var strUrl = "${createLink(controller:'ptReport',action:  'listReportOpenBacklog')}?moduleId=" + moduleId+"&projectId="+projectId;
        $("#flex1").flexOptions({url: strUrl});
        if (backlogListModel) {
            $("#flex1").flexAddData(backlogListModel);
            if (backlogListModel.total > 0) {
                $('.download_icon_set').show();
            }
        }
    }

    function onChangeProject() {
        var projectId = dropDownProject.value();
        if (projectId == '') {
            return;
        }

        dropDownModule.setDataSource(getKendoEmptyDataSource(dropDownModule, 'ALL'));

        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller:'ptModule', action: 'getModuleList')}?projectId=" + projectId,
            success: function (data) {
                if (data.isError) {
                    showError(data.message);
                    dropDownModule.setDataSource([]);
                } else {
                    dropDownModule.setDataSource(data.moduleList);
                }
            }, complete: onCompleteAjaxCall,
            dataType: 'json',
            type: 'post'
        });
    }

    function doGridEmpty() {
        var emptyGridModel = getEmptyGridModel();
        $("#flex1").flexAddData(emptyGridModel);
        $("#flex1").flexOptions({url: false, newp: 1, rp: 20}).flexReload();
        $('select[name=rp]').val(20);
    }

    function showBacklogDetails(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'Backlog') == false) {
            return;
        }
        showLoadingSpinner(true);
        var backlogId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller: 'ptReport', action: 'showForBacklogDetails')}?backlogId=" + backlogId + "&leftMenu=" + 'ptReport/showReportOpenBacklog';
        $.history.load(formatLink(loc));
        return false;
    }

</script>
