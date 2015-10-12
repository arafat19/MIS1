<script type="text/javascript">
    var dropDownProvince; //use only sarb plugin is installed
    $(document).ready(function () {
        initializeForm($('#residenceInfoForm'), isValidResidenceInfo);
        dropDownProvince = initKendoDropdown($('#provinceId'), null, null, modelJsonForCustomer.lstProvince);
    });
    function populateResidenceInfo(data) {
        var sarbCustomerDetails = data.sarbCustomerDetails;
        if (sarbCustomerDetails) {
            $('#suburb').val(sarbCustomerDetails.suburb);
            $('#contactSurname').val(sarbCustomerDetails.contactSurname);
            dropDownProvince.value(sarbCustomerDetails.provinceId);
            $('#contactName').val(sarbCustomerDetails.contactName);
            $('#city').val(sarbCustomerDetails.city);
        }
    }

    function isValidResidenceInfo() {
        if (!validateForm($("#residenceInfoForm"))) {
            $('#customerTabs a[href="#fragmentResidenceInfo"]').tab('show');
            return false;
        }
        return true
    }
</script>

<form name="residenceInfoForm" id="residenceInfoForm" class="form-horizontal form-widgets" role="form" method="post">
    <div class="panel-body">
        <div class="col-md-6">
            <div class="form-group">
                <label class="col-md-4 control-label label-required" for="suburb">Suburb:</label>

                <div class="col-md-5">
                    <input type="text" class="k-textbox" id="suburb" name="suburb"
                           required validationMessage="Required" tabindex="1"/>
                </div>

                <div class="col-md-3 pull-left">
                    <span class="k-invalid-msg" data-for="suburb"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-4 control-label label-required" for="provinceId">Province:</label>

                <div class="col-md-5">
                    <select name="provinceId" id="provinceId" required validationMessage="Required" tabindex="2">
                    </select>
                </div>

                <div class="col-md-3 pull-left">
                    <span class="k-invalid-msg" data-for="provinceId"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-4 control-label label-required" for="city">City:</label>

                <div class="col-md-5">
                    <input type="text" class="k-textbox" id="city" name="city"
                           required validationMessage="Required" tabindex="3"/>
                </div>

                <div class="col-md-3 pull-left">
                    <span class="k-invalid-msg" data-for="city"></span>
                </div>
            </div>
        </div>

        <div class="col-md-6">
            <div class="form-group">
                <label class="col-md-4 control-label label-required" for="contactName">Contact Name:</label>

                <div class="col-md-5">
                    <input type="text" class="k-textbox" id="contactName" name="contactName"
                           required validationMessage="Required" tabindex="4"/>
                </div>

                <div class="col-md-3 pull-left">
                    <span class="k-invalid-msg" data-for="contactName"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-4 control-label label-required" for="contactSurname">Contact Surname:</label>

                <div class="col-md-5">
                    <input type="text" class="k-textbox" id="contactSurname" name="contactSurname"
                           required validationMessage="Required" tabindex="5"/>
                </div>

                <div class="col-md-3 pull-left">
                    <span class="k-invalid-msg" data-for="contactSurname"></span>
                </div>
            </div>
        </div>
    </div>
</form>