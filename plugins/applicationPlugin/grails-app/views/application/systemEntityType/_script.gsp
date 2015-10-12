<script type="text/javascript">

    var output = false;
    var systemEntityTypeListModel = false;

    $(document).ready(function () {
        onLoadSystemEntityTypePage()
    });

    function onLoadSystemEntityTypePage() {
        output =${output ? output : ''};

        if (output.isError) {
            showError(output.message);
        } else {
            systemEntityTypeListModel = output.systemEntityTypeList;
        }

        initFlex1();
        populateFlex1();

        // update page title
        $('span.headingText').html('Update System Entity type Information');
        $('#icon_box').attr('class', 'pre-icon-header system-entity-type');
        $(document).attr('title', "MIS - Update System Entity type Information");
        loadNumberedMenu(MENU_ID_APPLICATION, "#systemEntityType/show");
    }

    function resetSystemEntityTypeForm() {
        $('#name').text('');
        $('#description').text('');
    }

    function initFlex1() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 30, sortable: false, align: "right"},
                        {display: "ID", name: "id", width: 30, sortable: false, align: "right", hide: true},
                        {display: "Name", name: "name", width: 200, sortable: false, align: "left"},
                        {display: "Description", name: "description", width: 400, sortable: false, align: "left"},
                        {display: "System Entity Count", name: "sysEntityCount", width: 120, sortable: false, align: "right"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/systemEntityType/select,/systemEntityType/update">
                        {name: 'Details', bclass: 'details', onpress: aboutSystemEntityType},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/systemEntity/show">
                        {name: 'System Entity', bclass: 'addItem', onpress: addSystemEntity},
                        </app:ifAllUrl>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ], searchitems: [
                    {display: "Name", name: "name", width: 180, sortable: true, align: "left"}
                ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All System Entity Type List',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 20,
                    customPopulate: customPopulateSystemEntityTypeGrid
                }
        );
    }

    function addSystemEntity(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'system entity type') == false) {
            return;
        }
        showLoadingSpinner(true);
        var systemEntityTypeId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller:'systemEntity', action: 'show')}?systemEntityTypeId=" + systemEntityTypeId;
        $.history.load(formatLink(loc));
        return false;
    }

    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function customPopulateSystemEntityTypeGrid(data) {
        if (data.isError) {
            showError(data.message);
            systemEntityTypeListModel = getEmptyGridModel();
        } else {
            systemEntityTypeListModel = data;
        }
        $('#flex1').flexAddData(systemEntityTypeListModel);
        return false;
    }

    function aboutSystemEntityType(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'system entity type') == false) {
            return;
        }
        resetSystemEntityTypeForm();
        showLoadingSpinner(true);
        var seTypeId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'systemEntityType', action: 'select')}?id=" + seTypeId,
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
            showSystemEntityType(data);
        }
    }

    function showSystemEntityType(data) {
        var entity = data.entity;
        $('#name').text(entity.name);
        $('#description').text(entity.description);
    }

    <%-- End: Edit operation --%>

    function populateFlex1() {
        var strUrl = "${createLink(controller:'systemEntityType', action: 'list')}";
        $("#flex1").flexOptions({url: strUrl});

        if (systemEntityTypeListModel) {
            $("#flex1").flexAddData(systemEntityTypeListModel);
        }
    }

</script>
