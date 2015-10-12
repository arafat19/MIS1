<script type="text/javascript">

    var dropDownPropertyName;
    var taskListModel = false;

    $(document).ready(function() {
        onLoadBeneficiaryDetailsPage();
    });

    function onLoadBeneficiaryDetailsPage() {
        $("#property_name").kendoDropDownList({
            dataTextField: "name",
            dataValueField: "id"
        });
        dropDownPropertyName = $("#property_name").data("kendoDropDownList");

        // update page title
        $(document).attr('title', "ARMS - Search Beneficiary");
        loadNumberedMenu(MENU_ID_ARMS, "#rmsReport/showBeneficiaryDetails");
    }

    function executePreConditionForSearchBeneficiary() {
        if (!customValidateDate($("#from_date"), 'from date', $("#to_date"), 'to date')) {
            return false;
        }
        $("#property_value").val($.trim($("#property_value").val()));
        var propertyValue = $("#property_value").val();
        if (propertyValue == '') {
            showError('Please enter Name or Account No or Phone No to search beneficiary');
            return false;
        }
        if($('#property_name').val() == "beneficiaryName") {
            if(propertyValue.length < 3) {
                showError("Name should be at least three characters");
                return false;
            }
        }
        return true;
    }

</script>