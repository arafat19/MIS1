<script language="javascript">
    var sprintBugListModel = false;
    var dropDownModule, sprintId, sprintName, projectId;

    $(document).ready(function () {
        onLoadSprintBackLogPage();
    });

    // method called on page load
    function onLoadSprintBackLogPage() {
        initializeForm($("#taskForm"), getModuleBug);

        var output =${output ? output : ''};
        if (output.isError) {
            showError(output.message);  // show error message in case of error
        } else {
            sprintBugListModel = output.gridObj;    // set data in a global variable to populate
        }
        dropDownModule = initKendoDropdown($('#moduleId'), null, null, output.lstModule);

        sprintName = output.sprintName;
        sprintId = output.sprintId;
        projectId = output.projectId;

        initFlex();
        populateFlex1();

        var bugHeight = $('#contentHolder').height() - $('#containerSearchByModule').height() - 80;
        $('#lstBugs').css('height', bugHeight);
        // update page title
        $(document).attr('title', "MIS - Create Sprint Task");
        loadNumberedMenu(MENU_ID_PROJECT_TRACK, "#ptSprint/show");
    }

    function getModuleBug() {
        var moduleId = dropDownModule.value();
        if (moduleId == '') {
            return;
        }
        setButtonDisabled($('#search'), true);
        $('#lstBugs').attr('module_id', moduleId);
        $('#lstBugs').reloadMe(false, '#containerBugs');
        setButtonDisabled($('#search'), false);
        return false;
    }

    // check pre condition before submitting the form
    function executePreCondition() {
        if (!confirm('Are you sure you want to add the selected bug to sprint?')) {
            return false;
        }
    }

    // method called  on submit of the form
    function addBugToSprint(id) {
        if (executePreCondition() == false) {
            return false;
        }
        var params = "?sprintId=" + sprintId + "&bugId=" + id;

        setButtonDisabled($('#create'), true);    // disable the save button
        showLoadingSpinner(true);   // show loading spinner
        var actionUrl = "${createLink(controller:'ptBug', action: 'createBugForSprint')}" + params;

        // fire ajax method
        jQuery.ajax({
            type: 'post',
            data: jQuery("#sprintBugForm").serialize(),  // serialize data from UI and send as parameter
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
            var newEntry = result;
            var previousTotal = parseInt(sprintBugListModel.total);
            var firstSerial = 1;

            if (sprintBugListModel.rows.length > 0) {
                firstSerial = sprintBugListModel.rows[0].cell[0];
                regenerateSerial($(sprintBugListModel.rows), 0);
            }
            newEntry.entity.cell[0] = firstSerial;

            sprintBugListModel.rows.splice(0, 0, newEntry.entity);

            if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                sprintBugListModel.rows.pop();
            }

            sprintBugListModel.total = ++previousTotal;
            $("#flex1").flexAddData(sprintBugListModel);
            showSuccess(result.message);    // show success message

            var lstBug = $("#lstBugs").data("kendoListView");
            var elements = lstBug.element.children();
            $(elements).each(function () {
                var eachId = $(this).attr("id");
                if (eachId == newEntry.entity.id) {
                    lstBug.remove($(this));
                    return false;
                }
            });
        }
    }

    // reset the form
    function resetBugForm() {
        clearForm($("#sprintBugForm"), dropDownModule);
        $("#create").html("<span class='k-icon k-i-plus'></span>Add");   // reset create button text
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
                        {display: "Bug ID", name: "id", width: 85, sortable: false, align: "right"},
                        {display: "Module", name: "module", width: 180, sortable: false, align: "left"},
                        {display: "Title", name: "title", width: 450, sortable: false, align: "left"},
                        {display: "Severity", name: "severity", width: 70, sortable: true, align: "left"},
                        {display: "Type", name: "type", width: 170, sortable: false, align: "left"}

                    ],
                    buttons: [
                        <app:ifAllUrl urls="/ptBug/deleteBugForSprint">
                        {name: 'Remove', bclass: 'delete', onpress: deleteBugForSprint},
                        </app:ifAllUrl>
                        {name: 'Bug Report', bclass: 'report', onpress: viewBugDetails},
                        {name: 'Sprint Report', bclass: 'report', onpress: downloadBugForSprint},
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Title", name: "title", width: 180, sortable: true, align: "left"}

                    ],
                    sortname: "id",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Bugs for '+ '"'+sprintName+'"',
                    useRp: true,
                    rp: 20,
                    showTableToggleBtn: false,
                    height: getGridHeight(),
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
            sprintBugListModel = null;
        } else {
            sprintBugListModel = data;
        }
        return data;
    }

    // delete sprintBacklog object
    function deleteBugForSprint(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var bugId = getSelectedIdFromGrid($('#flex1'));


        $.ajax({
            url: "${createLink(controller:'ptBug', action:  'deleteBugForSprint')}?id=" + bugId,
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
        if (executeCommonPreConditionForSelect($('#flex1'), 'bug') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected bug?')) {
            return false;
        }
        return true;
    }

    <%-- removing selected row  --%>
    function executePostConditionForDelete(data) {
        if (data.isError == true) {
            showError(data.message);
        } else {
            var selectedRow = null;
            $('.trSelected', $('#flex1')).each(function (e) {
                selectedRow = $(this).remove();
            });

            $('#flex1').decreaseCount(1);
            sprintBugListModel.total = parseInt(sprintBugListModel.total) - 1;
            removeEntityFromGridRows(sprintBugListModel, selectedRow);
            showSuccess(data.message);
        }
    }

    // set link to grid url to populate data
    function populateFlex1() {
        var strUrl = "${createLink(controller:'ptBug',action:  'listBugForSprint')}?sprintId=" + sprintId;
        $("#flex1").flexOptions({url: strUrl});

        if (sprintBugListModel) {
            $("#flex1").flexAddData(sprintBugListModel);
        }
    }

    function downloadBugForSprint() {
        showLoadingSpinner(true);
        var params = "?projectId=" + projectId + "&sprintId=" + sprintId + "&statusId=" + '';

        if (confirm('Do you want to download the PDF now?')) {
            var url = "${createLink(controller: 'ptReport', action: 'downloadBugDetails')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
        return false;
    }

    function viewBugDetails(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'bug') == false) {
            return;
        }
        showLoadingSpinner(true);
        var bugId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller: 'ptBug', action: 'showBugDetails')}?id=" + bugId + "&leftMenu=" + 'ptSprint/show';
        $.history.load(formatLink(loc));
        return false;
    }

</script>
