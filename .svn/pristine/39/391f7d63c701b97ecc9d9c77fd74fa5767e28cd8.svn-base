<table id="flex1" style="display:none"></table>

<script type="text/javascript">
    var modelJsonForSanction;
    var sanctionListModel;
    $(document).ready(function() {
        modelJsonForSanction = ${modelJson};
        sanctionListModel = modelJsonForSanction.sanctionListJSON ? modelJsonForSanction.sanctionListJSON : false;
        onLoadSanction();
    });

    function onLoadSanction() {
        var fName = modelJsonForSanction.fName ? modelJsonForSanction.fName :'';
        var mName = modelJsonForSanction.mName ? modelJsonForSanction.mName :'';
        var lName = modelJsonForSanction.lName ? modelJsonForSanction.lName :'';
        $("#fName").val(fName);
        $("#mName").val(mName);
        $("#lName").val(lName);
        $("#createdOn").mask("99/99/9999");
        $("#lastUpdate").mask("99/99/9999");

	    initFlexGrid();
	    loadFlexGrid();
	    // update page title
	    $(document).attr('title', "ARMS - Sanction Information");
	    loadNumberedMenu(MENU_ID_EXCHANGE_HOUSE, "#exhSanction/show");
    }


    function resetForm() {
        clearForm($("#sanctionForm"));
        $('#application_top_panel').hide();
    }

    function initFlexGrid() {
	    $("#flex1").flexigrid
	    (
			    {
				    url: false,
				    dataType: 'json',
				    colModel : [
					    {display: "Serial", name : "serial", width : 30, sortable : false, align: "right"},
					    {display: "Name ", name : "name", width : 600, sortable : true, align: "left"},
					    {display: "Title", name : "title", width : 180, sortable : true, align: "left"},
					    {display: "Date of Birth", name : "dob", width : 90, sortable : false, align: "left"},
					    {display: "Town Of Birth", name : "townOfBirth", width : 180, sortable : false, align: "left"},
					    {display: "Country Of Birth", name : "countryOfBirth", width : 180, sortable : true, align: "left"},
					    {display: "Nationality", name : "nationality", width : 180, sortable : true, align: "left"},
					    {display: "Passport Details", name : "passportDetails", width : 180, sortable : false, align: "left"},
					    {display: "NI Number", name : "niNumber", width : 180, sortable : true, align: "left"},
					    {display: "Position", name : "position", width : 180, sortable : false, align: "left"},
					    {display: "Address 1", name : "address1", width : 180, sortable : false, align: "left"},
					    {display: "Address 2", name : "address2", width : 180, sortable : false, align: "left"},
					    {display: "Address 3", name : "address3", width : 180, sortable : false, align: "left"},
					    {display: "Address 4", name : "address4", width : 180, sortable : false, align: "left"},
					    {display: "Address 5", name : "address5", width : 180, sortable : false, align: "left"},
					    {display: "Address 6", name : "address6", width : 180, sortable : false, align: "left"},
					    {display: "Post or Zip", name : "postOrZip", width : 90, sortable : false, align: "left"},
					    {display: "Country", name : "country", width : 180, sortable : true, align: "left"},
					    {display: "Other Information", name : "otherInformation", width : 180, sortable : false, align: "left"},
					    {display: "Group Type", name : "groupType", width : 110, sortable : false, align: "left"},
					    {display: "Alias Type", name : "aliasType", width : 180, sortable : false, align: "left"},
					    {display: "Regime", name : "regime", width : 180, sortable : false, align: "left"},
					    {display: "Listed On", name : "listedOn", width : 90, sortable : false, align: "left"},
					    {display: "Last Updated", name : "lastUpdated", width : 90, sortable : false, align: "left"},
					    {display: "Group Id", name : "groupId", width : 90, sortable : false, align: "left"}
				    ],
				    buttons : [
					    {name: 'View Details', bclass: 'edit', onpress : showDetailsSanction},
//                            {name: 'Delete', bclass: 'delete', onpress : deleteBank},
					    {name: 'Clear Results', bclass: 'clear-results', onpress : reloadGrid},
					    {separator: true}
				    ],
				    searchitems : [
					    {display: "Name", name : "name", width : 180, sortable : true, align: "left"},
					    {display: "Nationality", name : "nationality", width : 180, sortable : true, align: "left"},
					    {display: "Country", name : "country", width : 180, sortable : true, align: "left"}
				    ],
				    sortname: "id",
				    sortorder: "desc",
				    usepager: true,
				    singleSelect: true,
				    title: 'All Sanctions',
				    useRp: true,
				    rp: 25,
				    rpOptions: [20,25,30],
				    showTableToggleBtn: false,
				    height: getGridHeight()-15,
				    customPopulate:populateGridForSanctionList,
				    afterAjax: function(XMLHttpRequest, textStatus) {
					    showLoadingSpinner(false);// Spinner hide after AJAX Call
					    checkGrid();
				    }
			    }
	    );
    }


    <%-- Start: Show Details Sanction --%>

    function showDetailsSanction(com, grid) {
        var ids = $('.trSelected', grid);
        if (executePreConditionForShowDetails(ids) == false) {
            return;
        }

        var name = $(ids[ids.length - 1]).find('td').eq(1).find('div').text();
        $('#name').val(name);
        var title = $(ids[ids.length - 1]).find('td').eq(2).find('div').text();
        $('#title').val(title);
        var dob = $(ids[ids.length - 1]).find('td').eq(3).find('div').text();
        $('#dob').val(dob);
        var townOfBirth = $(ids[ids.length - 1]).find('td').eq(4).find('div').text();
        $('#townOfBirth').val(townOfBirth);
        var countryOfBirth = $(ids[ids.length - 1]).find('td').eq(5).find('div').text();
        $('#countryOfBirth').val(countryOfBirth);
        var nationality = $(ids[ids.length - 1]).find('td').eq(6).find('div').text();
        $('#nationality').val(nationality);
        var passportDetails = $(ids[ids.length - 1]).find('td').eq(7).find('div').text();
        $('#passportDetails').val(passportDetails);
        var niNumber = $(ids[ids.length - 1]).find('td').eq(8).find('div').text();
        $('#niNumber').val(niNumber);
        var position = $(ids[ids.length - 1]).find('td').eq(9).find('div').text();
        $('#position').val(position);
        var address1 = $(ids[ids.length - 1]).find('td').eq(10).find('div').text();
        $('#address1').val(address1);
        var address2 = $(ids[ids.length - 1]).find('td').eq(11).find('div').text();
        $('#address2').val(address2);
        var address3 = $(ids[ids.length - 1]).find('td').eq(12).find('div').text();
        $('#address3').val(address3);
        var address4 = $(ids[ids.length - 1]).find('td').eq(13).find('div').text();
        $('#address4').val(address4);
        var address5 = $(ids[ids.length - 1]).find('td').eq(14).find('div').text();
        $('#address5').val(address5);
        var address6 = $(ids[ids.length - 1]).find('td').eq(15).find('div').text();
        $('#address6').val(address6);
        var postOrZip = $(ids[ids.length - 1]).find('td').eq(16).find('div').text();
        $('#postOrZip').val(postOrZip);
        var country = $(ids[ids.length - 1]).find('td').eq(17).find('div').text();
        $('#country').val(country);
        var otherInformation = $(ids[ids.length - 1]).find('td').eq(18).find('div').text();
        $('#otherInformation').val(otherInformation);
        var groupType = $(ids[ids.length - 1]).find('td').eq(19).find('div').text();
        $('#groupType').val(groupType);
        var aliasType = $(ids[ids.length - 1]).find('td').eq(20).find('div').text();
        $('#aliasType').val(aliasType);
        var regime = $(ids[ids.length - 1]).find('td').eq(21).find('div').text();
        $('#regime').val(regime);
        var createdOn = $(ids[ids.length - 1]).find('td').eq(22).find('div').text();
        $('#createdOn').val(createdOn);
        var lastUpdate = $(ids[ids.length - 1]).find('td').eq(23).find('div').text();
        $('#lastUpdate').val(lastUpdate);
        var groupId = $(ids[ids.length - 1]).find('td').eq(24).find('div').text();
        $('#groupId').val(groupId);
    }


    function executePreConditionForShowDetails(ids) {
        if (ids.length == 0) {
            showError("Please select a sanction to show details");
            return false;
        }
        return true;
    }


    <%-- End: Show Details operation --%>


    function populateGridForSanctionList(data) {
        executePostConditionForLoadGrid(data);
    }

    function checkGrid() {
        var rows = $('table#flex1 > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('No sanction info found');
        }
    }


    <%-- reloading grid data --%>
    function reloadGrid(com, grid) {
        if (com == 'Clear Results') {
            $('#flex1').flexOptions({query: ''}).flexReload();
        }
    }

    function loadFlexGrid() {
        var params = "?fName=" + $("#fName").val() + '&mName=' + $("#mName").val() + '&lName=' + $("#lName").val();
        var strUrl = "${createLink(controller: 'exhSanction', action: 'list')}" + params;
        $("#flex1").flexOptions({url: strUrl});
        if (sanctionListModel) {
            $("#flex1").flexAddData(sanctionListModel);
        }
    }

    function executePostConditionForLoadGrid(data) {
        if (data.isError == true) {
            showError(data.message);
            resetGrid();
        } else {
            var params = "?fName=" + $("#fName").val() + '&mName=' + $("#mName").val() + '&lName=' + $("#lName").val();
            var strUrl = "${createLink(controller: 'exhSanction', action: 'list')}" + params;
            $("#flex1").flexOptions({url: strUrl});
            $("#flex1").flexAddData(data.gridOutput);
        }
    }

    function resetGrid() {
        var obj = getEmptyGridModel();
        $('#flex1').flexOptions({url: false}).flexAddData(obj);
    }

</script>
