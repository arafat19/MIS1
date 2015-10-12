<script type="text/javascript">
    var dropDownProcessType, dropDownInstrumentType;
    var proInsMappingListModel = false;

    $(document).ready(function () {
        onLoadRmsProInsMappingPage();
    });

    // method called on page load
    function onLoadRmsProInsMappingPage() {
        var output =${output ? output : ''};

        initializeForm($("#rmsProInsMappingForm"), onSubmitRmsProInsMapping);

        if (output.isError) {
            showError(output.message);              // show error message in case of error
        } else {
            proInsMappingListModel = output.gridObj;        // set data in a global variable to populate
        }
        initFlex();
        populateFlex();
        $(document).attr('title', "ARMS - Create Process Instrument Mapping");
        loadNumberedMenu(MENU_ID_ARMS, "#rmsProcessInstrumentMapping/show");
    }

    // method called  on submit of the form
    function onSubmitRmsProInsMapping() {
        if (executePreCondition() == false) {
            return false;
        }
        setButtonDisabled($('#create'), true);            // disable the save button
        showLoadingSpinner(true);                       // show loading spinner
        var actionUrl = null;
        // set link for create or update if there is data in hidden field id
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'rmsProcessInstrumentMapping', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'rmsProcessInstrumentMapping', action: 'update')}";
        }
        // fire ajax method for create or update
        jQuery.ajax({
            type: 'post',
            data: jQuery("#rmsProInsMappingForm").serialize(),      // serialize data from UI and send as parameter
            url: actionUrl,
            success: function (data) {
                executePostCondition(data);
            },
            complete: function () {
                setButtonDisabled($('#create'), false);         // enable the save button
                showLoadingSpinner(false);                      // stop loading spinner
            },
            dataType: 'json'
        });
        return false;
    }
    // check pre condition before submitting the form
    function executePreCondition() {
        // validate form data
        if (!validateForm($("#rmsProInsMappingForm"))) {
            return false;
        }
        return true;
    }
    // execute post condition after create or update
    function executePostCondition(result) {
        if (result.isError) {
            showError(result.message);              // show error message in case of error
            showLoadingSpinner(false);              // stop loading spinner
        }
        else {
            try {
                var newEntry = result;
                if ($('#id').val().isEmpty() && newEntry.entity != null) {      // show newly created object in a grid row

                    var previousTotal = parseInt(proInsMappingListModel.total);
                    var firstSerial = 1;

                    if (proInsMappingListModel.rows.length > 0) {
                        firstSerial = proInsMappingListModel.rows[0].cell[0];
                        regenerateSerial($(proInsMappingListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;

                    proInsMappingListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex').countEqualsResultPerPage(previousTotal)) {
                        proInsMappingListModel.rows.pop();
                    }

                    proInsMappingListModel.total = ++previousTotal;
                    $("#flex").flexAddData(proInsMappingListModel);

                } else if (newEntry.entity != null) {                           // updated existing object data in the grid
                    updateListModel(proInsMappingListModel, newEntry.entity, 0);
                    $("#flex").flexAddData(proInsMappingListModel);
                }

                resetForm();                        // reset the form
                showSuccess(result.message);        // show success message

            } catch (e) {
                // Do Nothing
            }
        }
    }
    // reset the form
    function resetForm() {
        clearForm($("#rmsProInsMappingForm"), dropDownProcessType);              // clear errors & form values
        $("#create").html("<span class='k-icon k-i-plus'></span>Create");       // reset create button text
    }

    // initialize the grid
    function initFlex() {
        $("#flex").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 40, sortable: false, align: "right"},
                        {display: "Id", name: "id", width: 40, sortable: false, align: "right", hide: true},
                        {display: "Process Type", name: "process_type", width: 120, sortable: true, align: "left"},
                        {display: "Instrument Type", name: "instrument_type", width: 120, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/rmsProcessInstrumentMapping/select,/rmsProcessInstrumentMapping/update">
                        {name: 'Edit', bclass: 'edit', onpress: selectRmsProInsMapping},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/rmsProcessInstrumentMapping/delete">
                        {name: 'Delete', bclass: 'delete', onpress: deleteRmsProInsMapping},
                        </app:ifAllUrl>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Process Type", name: " process.key", width: 180, sortable: true, align: "left"},
                        {display: "Instrument Type", name: " instrument.key", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "id",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Process Instrument Mapping',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 60,
                    afterAjax: function () {
                        afterAjaxError();
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
            proInsMappingListModel = getEmptyGridModel();
        } else {
            proInsMappingListModel = data;
        }
        $("#flex").flexAddData(proInsMappingListModel);
    }
    // select rmsProcessInstrumentMapping object for update
    function selectRmsProInsMapping(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex'), 'process instrument mapping') == false) {
            return;
        }
        resetForm();                    // reset the form
        showLoadingSpinner(true);       // start loading spinner
        var id = getSelectedIdFromGrid($('#flex'));
        // fire ajax method for select
        $.ajax({
            url: "${createLink(controller:'rmsProcessInstrumentMapping', action: 'select')}?id=" + id,
            success: executePostConditionForEdit,
            complete: function () {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }
    
    // execute post condition for edit
    function executePostConditionForEdit(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            showRmsProInsMapping(data);
        }
    }
    // show property of selected rmsProcessInstrumentMapping object on UI
    function showRmsProInsMapping(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        dropDownProcessType.value(entity.processType);
        dropDownInstrumentType.value(entity.instrumentType);
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");
    }

    // delete rmsProcessInstrumentMapping object
    function deleteRmsProInsMapping(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var rmsProInsMappingId = getSelectedIdFromGrid($('#flex'));
        // fire ajax method for delete
        $.ajax({
            url: "${createLink(controller:'rmsProcessInstrumentMapping', action:  'delete')}?id=" + rmsProInsMappingId,
            success: executePostConditionForDelete,
            complete: function () {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }
    // execute pre condition for delete
    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex'), 'process instrument mapping') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected process instrument mapping?')) {
            return false;
        }
        return true;
    }
    // removing selected row and clean input form
    function executePostConditionForDelete(data) {
        if (data.isError == false) {
            var selectedRow = null;
            $('.trSelected', $('#flex')).each(function (e) {
                selectedRow = $(this).remove();
            });
            resetForm();
            $('#flex').decreaseCount(1);
            showSuccess(data.message);
            proInsMappingListModel.total = parseInt(proInsMappingListModel.total) - 1;
            removeEntityFromGridRows(proInsMappingListModel, selectedRow);
        } else {
            showError(data.message);
        }
    }
    // reload grid
    function reloadGrid(com, grid) {
        $('#flex').flexOptions({query: ''}).flexReload();
    }
    // set link to grid url to populate data
    function populateFlex() {
        var strUrl = "${createLink(controller:'rmsProcessInstrumentMapping',action:  'list')}";
        $("#flex").flexOptions({url: strUrl});

        if (proInsMappingListModel) {
            $("#flex").flexAddData(proInsMappingListModel);
        }
    }

</script>