<script type="text/javascript">

    var postalListModel;


    $(document).ready(function(){
        onLoadPostalCode();
    });

    function onLoadPostalCode(){
        initializeForm($('#postalCodeForm'),onSubmitPostalCode);


        var output =${output ? output : ''};
        postalListModel = false;
        if (output.isError) {
            showError(output.message);  // show error message in case of error
        } else {
            postalListModel = output.gridObj;    // set data in a global variable to populate
        }
        initFlex();
        populateFlex();
        $(document).attr('title','ARMS-Create Postal Code');
        loadNumberedMenu(MENU_ID_EXCHANGE_HOUSE, "#exhPostalCode/show");
    }

    function onSubmitPostalCode(){
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('.save'), true);
        // Spinner Show on AJAX Call
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'exhPostalCode', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'exhPostalCode', action: 'update')}";
        }

        jQuery.ajax({
            type:'post',
            data:jQuery("#postalCodeForm").serialize(),
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
        if(!validateForm($("#postalCodeForm"))){
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

                    var previousTotal = parseInt(postalListModel.total);
                    var firstSerial = 1;

                    if (postalListModel.rows.length > 0) {
                        firstSerial = postalListModel.rows[0].cell[0];
                        regenerateSerial($(postalListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;

                    postalListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex').countEqualsResultPerPage(previousTotal)) {
                        postalListModel.rows.pop();
                    }

                    postalListModel.total = ++previousTotal;
                    $("#flex").flexAddData(postalListModel);

                } else if (newEntry.entity != null) { // updated existing object data in the grid
                    updateListModel(postalListModel, newEntry.entity, 0);
                    $("#flex").flexAddData(postalListModel);
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
                    {display: "Code", name : "code", width : 180, sortable : true, align: "left"}
                ],
                buttons : [
                    {name: 'Edit', bclass: 'edit', onpress : selectPostalCode},
                    {name: 'Delete', bclass: 'delete', onpress : deletePostalCode},
                    {name: 'Clear Results', bclass: 'clear-results', onpress : reloadGrid},
                    {separator: true}
                ],
                searchitems : [
                    {display: "Code", name : "code", width : 180, sortable : true, align: "left"}
                ],
                sortname: "id",
                sortorder: "desc",
                usepager: true,
                singleSelect: true,
                title: 'All Postal Codes',
                useRp: true,
                rp: 15,
                showTableToggleBtn: false,
                height: getGridHeight()-10,
                preProcess: onLoadPostalCodeJSON
            }
        );
    }
    function onLoadPostalCodeJSON(data){

        if (data.isError) {
            showError(data.message);
            postalListModel = null;
        } else {
            postalListModel = data;
        }
        return data;
    }

    function selectPostalCode(){
        if (executeCommonPreConditionForSelect($('#flex'), 'postal code') == false) {
            return;
        }

        showLoadingSpinner(true);
        var id = getSelectedIdFromGrid($('#flex'));
        $.ajax({
            url: "${createLink(controller:'exhPostalCode', action: 'select')}?id=" + id,
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
            showPostalCode(data);
        }
    }
    function showPostalCode(data){
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#code').val(entity.code);
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");   // change create button text to update
    }
    function resetForm(){
        clearForm($('#postalCodeForm'),$("#code"));
        $("#create").html("<span class='k-icon k-i-plus'></span>Create");
    }

    function deletePostalCode(data){
        if (executeCommonPreConditionForSelect($('#flex'), 'postal code') == false){
            return;
        }
        if(!confirm("Are you sure you want to delete the selected postal code?")){
            return false;
        }
        var postalCodeId=getSelectedIdFromGrid($('#flex'));
        $.ajax({
            url: "${createLink(controller:'exhPostalCode', action:  'delete')}?id=" + postalCodeId,
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
            postalListModel.total = parseInt(postalListModel.total) - 1;
            removeEntityFromGridRows(postalListModel, selectedRow);
         }
    }

    function reloadGrid() {
        $('#flex').flexOptions({query: ''}).flexReload();
    }
    function populateFlex(){
        var strUrl="${createLink(controller: 'exhPostalCode', action: 'list')}";
        $('#flex').flexOptions({url: strUrl});

        if (postalListModel) {
            $("#flex").flexAddData(postalListModel);
        }
    }

</script>