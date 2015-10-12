<script type="text/javascript">
    var modelJsonForRemittancePurpose ;
    var listRemittancePurposeModel=null;
    // @todo-Azam to load grid without round trip

    $(document).ready(function() {

        onLoadRemittancePurpose();

    });

    function onLoadRemittancePurpose() {
        initializeForm($("#remittancePurposeForm"),onSubmitRemittancePurpose);
        modelJsonForRemittancePurpose = ${modelJson};
        listRemittancePurposeModel = modelJsonForRemittancePurpose.remittancePurposeListJSON ? modelJsonForRemittancePurpose.remittancePurposeListJSON : false;

        initFlex();
        populateOnLoadFlexGrid();

        $(document).attr('title', "ARMS - Create Remittance Purpose");
        loadNumberedMenu(MENU_ID_EXCHANGE_HOUSE, "#exhRemittancePurpose/show");

        $('#name').focus();
    }

    function executePreCondition() {
        if (!validateForm($("#remittancePurposeForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitRemittancePurpose() {

        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('.save'), true);
        // Spinner Show on AJAX Call
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'exhRemittancePurpose', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'exhRemittancePurpose', action: 'update')}";
        }

        jQuery.ajax({
                    type:'post',
                    data:jQuery("#remittancePurposeForm").serialize(),
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


    function executePostCondition(result) {

        if (result.isError) {
            var errors = $(result.errors);
            errors.each(function (i) {
                var err = $(this);
                var errStr = 'Error(s) occurred in some inputs';
                try {
                    if (err.length == 2) {
                        if ($("label[for='" + err[0] + "']").html() != null) {
                            errStr = $("label[for='" + err[0] + "']").html() + ' ' + err[1];
                        }
                    } else if (err.length == 1) {
                        errStr = err[0]
                    }
                } catch (e) { /** ignored */
                }
                showError(errStr);
            });

            showLoadingSpinner(false);
        } else {
            // @todo-Azam to load grid without round trip
            try {

                if ($('#id').val().isEmpty() && result.entity != null) { // newly created
                    var previousTotal = parseInt(listRemittancePurposeModel.total);

                    // re-arranging serial
                    var firstSerial = 1;
                    if (listRemittancePurposeModel.rows.length > 0) {
                        firstSerial = listRemittancePurposeModel.rows[0].cell[0];
                        regenerateSerial($(listRemittancePurposeModel.rows), 0);
                    }
                    result.entity.cell[0] = firstSerial;

                    listRemittancePurposeModel.rows.splice(0, 0, result.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        listRemittancePurposeModel.rows.pop();
                    }

                    listRemittancePurposeModel.total = ++previousTotal;
                    $("#flex1").flexAddData(listRemittancePurposeModel);

                } else if (result.entity != null) { // updated existing
                    updateListModel(listRemittancePurposeModel, result.entity, 0);
                    $("#flex1").flexAddData(listRemittancePurposeModel);
                }

                resetForm();
                showSuccess(result.message);

            } catch (e) {
                // @todo-Azam remove this alert before production
            }
            // @todo-Azam to add entity in grid without round trip, following line commented out
            //$("#flex1").flexReload();
        }
    }

    function resetForm() {
        setButtonDisabled($('.save'),false);
        clearForm($("#remittancePurposeForm"),$('#name'));
        $("#create").html("<span class='k-icon k-i-plus'></span>Create");
    }

    function initFlex(){
    $("#flex1").flexigrid
    (
        {
            //flexigrid url is false due to remove round trip
            url: false,
            dataType: 'json',
            colModel : [
                {display: "Serial", name : "serial", width : 30, sortable : false, align: "right"},
                {display: "ID", name : "id", width : 30, sortable : false, align: "right", hide: true},
                {display: "Name", name : "name", width : 180, sortable : true, align: "left"},
                {display: "Code", name : "code", width : 180, sortable : true, align: "left"}
                ],
            buttons : [
                {name: 'Edit', bclass: 'edit', onpress : editRemittancePurpose},
                {name: 'Delete', bclass: 'delete', onpress : deleteRemittancePurpose},
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
            title: 'All Remittance Purposes',
            useRp: true,
            rp: 15,
            showTableToggleBtn: false,
            height: getGridHeight(),
            //afterAjax: showLoadingSpinner(false),
            preProcess: onLoadRemittancePurposeListJSON // @todo-Azam to load grid without round trip
        }
    );
    }

    // @todo-Azam to load grid without round trip
    // storing remittancePurpose list grid JSON for future reference
    function onLoadRemittancePurposeListJSON(data) {
      listRemittancePurposeModel = data;
      return data;
    }


    <%-- reloading grid data --%>
    function reloadGrid(com, grid)
    {
        if (com == 'Clear Results') {
            $('#flex1').flexOptions({query: ''}).flexReload();
        }
    }

    <%-- Start : Delete operation of RemittancePurpose --%>
    function deleteRemittancePurpose(com, grid) {

        if(executeCommonPreConditionForSelect($('#flex1'),'remittance')==false){
            return;
        }

        showLoadingSpinner(true); // Spinner Show on AJAX Call

        if (!confirm('Are you sure you want to delete the selected Remittance Purpose?')) {
            return false;
        }
        var remittancePurposeId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller: 'exhRemittancePurpose', action: 'delete')}?id=" + remittancePurposeId,
            success: executePostConditionForDelete,
            complete:onCompleteAjaxCall,	// Spinner Hide on AJAX Call
            dataType:'json',
            type:'post'
        });
    }

    function executePreConditionForDelete(ids) {
      var delCount = ids.length;
      if (delCount == 0) {
          showError("Please select a remittance purpose to delete");
          return false;
      }

      return true;
    }

    <%-- removing selected row and clean input form --%>
    function executePostConditionForDelete(data) {
        if (data.deleted == true) {
            var selectedRow = null;
            $('.trSelected', $('#flex1')).each(function (e) {
                selectedRow = $(this).remove();
            });
            resetForm();
            $('#flex1').decreaseCount(1);
            showSuccess(data.message);
           // @todo-Azam managing grid to minimize round-trip
           listRemittancePurposeModel.total = parseInt(listRemittancePurposeModel.total) - 1;
           removeEntityFromGridRows(listRemittancePurposeModel, selectedRow);

        } else {
          // show delete error
          showError(data.message) ;
        }
    }

    function editRemittancePurpose(com, grid) {
      if(executeCommonPreConditionForSelect($('#flex1'),'remittance')==false){
          return;
      }

      showLoadingSpinner(true);	// Spinner Show on AJAX Call
      var remittancePurposeId = getSelectedIdFromGrid($('#flex1'));
      $.ajax({
          url: "${createLink(controller: 'exhRemittancePurpose', action: 'edit')}?id="+ remittancePurposeId,
          success: executePostConditionForEdit,
          complete:onCompleteAjaxCall,	// Spinner Hide on AJAX Call
          dataType:'json',
          type:'post'
      });
    }

    function executePostConditionForEdit(data) {
        if (data.entity == null) {
          showError(result.message);
        } else {
          showRemittancePurpose(data);
        }
    }

    function showRemittancePurpose(data) {
      var entity = data.entity;
      $('#id').val(entity.id);
      $('#version').val(data.version);
      $('#name').val(entity.name);
      $('#code').val(entity.code);
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");
    }

    <%-- End: Edit operation --%>



  // @todo-Azam to load grid without round trip
    function populateOnLoadFlexGrid(){
  var strUrl = "${createLink(controller: 'exhRemittancePurpose', action: 'list')}";
  $("#flex1").flexOptions({url: strUrl});

  if (listRemittancePurposeModel) {
    $("#flex1").flexAddData(listRemittancePurposeModel);
  }
    }
</script>
