<script type="text/javascript">
    var modelJsonForPhotoIdType;
    var listPhotoIdTypeModel;

    $(document).ready(function() {
        onLoadPhotoIdType();
    });

    function onLoadPhotoIdType() {
	    initializeForm($('#photoIdTypeForm'), onSubmitPhotoIdType);

	    modelJsonForPhotoIdType = ${modelJson};
	    listPhotoIdTypeModel = modelJsonForPhotoIdType.photoIdTypeListJSON ? modelJsonForPhotoIdType.photoIdTypeListJSON : false;

		initFlex();
	    populateFlex();

	    $(document).attr('title', "ARMS - Create photo id type");
	    loadNumberedMenu(MENU_ID_EXCHANGE_HOUSE, "#exhPhotoIdType/show");
	    $('#name').focus();
    }

    function executePreCondition() {
	    if (!validateForm($("#photoIdTypeForm"))) {
		    return false;
	    }
        return true;
    }

    function onSubmitPhotoIdType() {

        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('.save'), true);
        // Spinner Show on AJAX Call
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'exhPhotoIdType', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'exhPhotoIdType', action: 'update')}";
        }

        jQuery.ajax({
                    type:'post',
                    data:jQuery("#photoIdTypeForm").serialize(),
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
                    var previousTotal = parseInt(listPhotoIdTypeModel.total);

                    // re-arranging serial
                    var firstSerial = 1;
                    if (listPhotoIdTypeModel.rows.length > 0) {
                        firstSerial = listPhotoIdTypeModel.rows[0].cell[0];
                        regenerateSerial($(listPhotoIdTypeModel.rows), 0);
                    }
                    result.entity.cell[0] = firstSerial;

                    listPhotoIdTypeModel.rows.splice(0, 0, result.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        listPhotoIdTypeModel.rows.pop();
                    }

                    listPhotoIdTypeModel.total = ++previousTotal;
                    $("#flex1").flexAddData(listPhotoIdTypeModel);

                } else if (result.entity != null) { // updated existing
                    updateListModel(listPhotoIdTypeModel, result.entity, 0);
                    $("#flex1").flexAddData(listPhotoIdTypeModel);
                }

                resetForm();
                showSuccess(result.message);

            } catch (e) {
            }
        }
    }

    function resetForm() {
        clearForm($("#photoIdTypeForm"),$('#name'));
	    $("#create").html("<span class='k-icon k-i-plus'></span>Create");
    }

    function initFlex() {
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
                        {display: "Code", name : "code", width : 180, sortable : true, align: "left"},
                        {display: "isSecondary", name : "isSecondary", width : 180, sortable : false, align : "left"}
				    ],
				    buttons : [
					    {name: 'Edit', bclass: 'edit', onpress : editPhotoIdType},
					    {name: 'Delete', bclass: 'delete', onpress : deletePhotoIdType},
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
				    title: 'All Photo Id Types',
				    useRp: true,
				    rp: 15,
				    showTableToggleBtn: false,
				    height: getGridHeight()-10,
				    //afterAjax: showLoadingSpinner(false),
				    preProcess: onLoadPhotoIdTypeListJSON
			    }
	    );
    }


    function onLoadPhotoIdTypeListJSON(data) {
      listPhotoIdTypeModel = data;
      return data;
    }


    <%-- reloading grid data --%>
    function reloadGrid(com, grid)
    {
        if (com == 'Clear Results') {
            $('#flex1').flexOptions({query: ''}).flexReload();
        }
    }

    <%-- Start : Delete operation of PhotoIdType --%>
    function deletePhotoIdType(com, grid) {

        if(executeCommonPreConditionForSelect($('#flex1'),'photo ID')==false){
            return;
        }
        showLoadingSpinner(true); // Spinner Show on AJAX Call
        var photoIdTypeId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller: 'exhPhotoIdType', action: 'delete')}?id=" + photoIdTypeId,
            success: executePostConditionForDelete,
            complete:onCompleteAjaxCall,	// Spinner Hide on AJAX Call
            dataType:'json',
            type:'post'
        });
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
           listPhotoIdTypeModel.total = parseInt(listPhotoIdTypeModel.total) - 1;
           removeEntityFromGridRows(listPhotoIdTypeModel, selectedRow);

        } else {
          // show delete error
          showError(data.message) ;
        }
    }

    function editPhotoIdType(com, grid) {
      if(executeCommonPreConditionForSelect($('#flex1'),'photo ID')==false){
          return;
      }

      showLoadingSpinner(true);	// Spinner Show on AJAX Call
      var photoIdTypeId = getSelectedIdFromGrid($('#flex1'));
      $.ajax({
          url: "${createLink(controller: 'exhPhotoIdType', action: 'edit')}?id="+ photoIdTypeId,
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
          showPhotoIdType(data);
        }
    }

    function showPhotoIdType(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#name').val(entity.name);
        $('#code').val(entity.code);
        $('#isSecondary').attr('checked',entity.isSecondary);
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");
    }

	function populateFlex() {
		var strUrl = "${createLink(controller: 'exhPhotoIdType', action: 'list')}";
		$("#flex1").flexOptions({url: strUrl});

		if (listPhotoIdTypeModel) {
			$("#flex1").flexAddData(listPhotoIdTypeModel);
		}
	}

</script>
