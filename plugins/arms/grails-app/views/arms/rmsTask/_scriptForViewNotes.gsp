<script type="text/javascript">
    var lstTaskForViewNotes = false;

    $(document).ready(function() {
        var strUrl = "${createLink(controller: 'rmsTask', action: 'listForViewNotes')}";
        initFlex(strUrl);
        $(document).attr('title', "RMS - View Notes");
        loadNumberedMenu(MENU_ID_ARMS, "#rmsTask/showForViewNotes");
    });

    // initialize the grid
    function initFlex(strUrl) {
        $("#flex").flexigrid
        (
            {
                url: strUrl,
                dataType: 'json',
                colModel: [
                    {display: "SL", name: "serial", width: 80, sortable: false, align: "right"},
                    {display: "ID", name: "id", width: 100, sortable: false, align: "right", hide:true},
                    {display: "Ref No", name: "refNo", width: 100, sortable: false, align: "left"},
                    {display: "Description", name: "description", width: 400, sortable: false, align: "left"},
                    {display: "Created By", name: "createdBy", width: 120, sortable: false, align: "left"},
                    {display: "Create On", name: "createdOn", width: 120, sortable: false, align: "left"}
                ],
                buttons:[
                    {name: 'View Details', bclass: 'details', onpress: viewDetails},
                    {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid}
                ],
                sortname: "createdOn",
                sortorder: "asc",
                usepager: true,
                singleSelect: true,
                title: 'View all notes',
                useRp: true,
                rpOptions: [15, 25, 35, 50],
                rp: 25,
                showTableToggleBtn: false,
                height: getGridHeight(),
                afterAjax: function () {
                    afterAjaxError();
                    showLoadingSpinner(false);
                },
                customPopulate: populateFlex
            }
        );
    }
    function reloadGrid(com, grid) {
        $('#flex').flexOptions({query: ''}).flexReload();
    }
    // set link to grid url to populate data
    function populateFlex(data) {
        if (data.isError) {
            showError(data.message);
            lstTaskForViewNotes = getEmptyGridModel();
        } else {
            lstTaskForViewNotes = data.gridObj;
        }
        $("#flex").flexAddData(lstTaskForViewNotes);
    }
    function viewDetails(){
        if (executeCommonPreConditionForSelect($('#flex'), 'note',true) == false) {
            return;
        }
        showLoadingSpinner(true);
        var id = getSelectedIdFromGrid($('#flex'));
        var propertyValue=id;
        var propertyName="id";
        var params="?property_value="+propertyValue+"&property_name="+propertyName;
        var loc = "${createLink(controller: 'rmsTask', action: 'showTaskDetailsWithNote')}" + params;
        $.history.load(formatLink(loc));
        return false;
    }
</script>