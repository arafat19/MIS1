<script type="text/javascript">

    var provinceListModel;
    $(document).ready(function(){
        onLoadSarbProvince();
    });

    function onLoadSarbProvince(){
        initializeForm($('#sarbProvince'),onSubmitSarbProvince);


        var output =${output ? output : ''};
        provinceListModel = false;
        if (output.isError) {
            showError(output.message);  // show error message in case of error
        } else {
            provinceListModel = output.gridObj;    // set data in a global variable to populate
        }
        initFlex();
        populateFlex();
        $(document).attr('title','ARMS-Create Sarb Province');
        loadNumberedMenu(MENU_ID_SARB, "#sarbProvince/show");
    }

    function onSubmitSarbProvince(){
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('.save'), true);
        // Spinner Show on AJAX Call
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'sarbProvince', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'sarbProvince', action: 'update')}";
        }
        jQuery.ajax({
            type:'post',
            data:jQuery("#sarbProvince").serialize(),
            url: actionUrl,
            success:function(data, textStatus) {
                executePostCondition(data);
            },
            error:function(XMLHttpRequest, textStatus, errorThrown) {
            },
            complete:function(XMLHttpRequest, textStatus) {
                setButtonDisabled($('.save'), false);
                // Spinner Hide on AJAX Call
                onCompleteAjaxCall();
            },
            dataType:'json'
        });
        return false;
    }
    function executePreCondition(){
        if(!validateForm($("#sarbProvince"))){
            return false;
        }
        return true;
    }
    function executePostCondition(result){
        if (result.isError) {
            showError(result.message);  // show error message in case of error
            showLoadingSpinner(false);  // stop loading spinner
        } else {
            try {
                var newEntry = result;
                if ($('#id').val().isEmpty() && newEntry.entity != null) { // show newly created object in a grid row

                    var previousTotal = parseInt(provinceListModel.total);
                    var firstSerial = 1;

                    if (provinceListModel.rows.length > 0) {
                        firstSerial = provinceListModel.rows[0].cell[0];
                        regenerateSerial($(provinceListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;

                    provinceListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex').countEqualsResultPerPage(previousTotal)) {
                        provinceListModel.rows.pop();
                    }

                    provinceListModel.total = ++previousTotal;
                    $("#flex").flexAddData(provinceListModel);

                } else if (newEntry.entity != null) { // updated existing object data in the grid
                    updateListModel(provinceListModel, newEntry.entity, 0);
                    $("#flex").flexAddData(provinceListModel);
                }

                resetForm();    // reset the form
                showSuccess(result.message);    // show success message

            } catch (e) {
                // Do Nothing
            }
        }

    }
    function initFlex() {
        $("#flex").flexigrid
        (
                {
                    //flexigrid url is false due to remove round trip
                    url: false,
                    dataType: 'json',
                    colModel : [
                        {display: "Serial", name : "serial", width : 70, sortable : false, align: "right"},
                        {display: "Name", name : "name", width : 180, sortable : true, align: "left"}
                    ],
                    buttons : [
                        {name: 'Edit', bclass: 'edit', onpress : selectProvince},
                        {name: 'Delete', bclass: 'delete', onpress : deleteProvince},
                        {name: 'Clear Results', bclass: 'clear-results', onpress : reloadGrid},
                        {separator: true}
                    ],
                    searchitems : [
                        {display: "Name", name : "name", width : 180, sortable : true, align: "left"}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Province',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight()-10,
                    preProcess: onLoadProvinceJSON
                }
        );
    }
    function onLoadProvinceJSON(data){

        if (data.isError) {
            showError(data.message);
            provinceListModel = null;
        } else {
            provinceListModel = data;
        }
        return data;
    }

    function selectProvince(){
        if (executeCommonPreConditionForSelect($('#flex'), 'province') == false) {
            return;
        }

        showLoadingSpinner(true);
        var id = getSelectedIdFromGrid($('#flex'));
        $.ajax({
            url: "${createLink(controller:'sarbProvince', action: 'select')}?id=" + id,
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
            showProvince(data);
        }
    }
    function showProvince(data){
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#name').val(entity.name);
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");   // change create button text to update
    }
    function resetForm(){
        clearForm($('#sarbProvince'),$("#name"));
        $("#create").html("<span class='k-icon k-i-plus'></span>Create");
    }

    function deleteProvince(data){
        if (executeCommonPreConditionForSelect($('#flex'), 'province') == false){
            return;
        }
        if(!confirm("Are you sure you want to delete the selected province?")){
            return false;
        }
        var provinceId=getSelectedIdFromGrid($('#flex'));
        $.ajax({
            url: "${createLink(controller:'sarbProvince', action:  'delete')}?id=" + provinceId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }
    function executePostConditionForDelete(data){
        if (data.isError == false) {
            var selectedRow = null;
            $('.trSelected', $('#flex')).each(function (e) {
                selectedRow = $(this).remove();
            });
            resetForm();
            $('#flex').decreaseCount(1);
            showSuccess(data.message);
            provinceListModel.total = parseInt(provinceListModel.total) - 1;
            removeEntityFromGridRows(provinceListModel, selectedRow);
        }
    }

    function reloadGrid() {
        $('#flex').flexOptions({query: ''}).flexReload();
    }
    function populateFlex(){
        var strUrl="${createLink(controller: 'sarbProvince', action: 'list')}";
        $('#flex').flexOptions({url: strUrl});

        if (provinceListModel) {
            $("#flex").flexAddData(provinceListModel);
        }
    }

</script>