<script type="text/javascript">
    var modelJsonList, dropDownCountry, dropDownPhotoType, validatorImage, company, isdCode;
    $(document).ready(function () {

        modelJsonList = ${modelJson ? modelJson : ''};
        dropDownCountry = initKendoDropdown($('#countryId'), 'nationality', null, modelJsonList.countryList);
        dropDownPhotoType = initKendoDropdown($('#photoIdTypeId'), null, null, modelJsonList.photoIdTypeList);
        company = modelJsonList.objCompany;
        isdCode = modelJsonList.isdCode;
        initCustRegistrationForm();

        validatorImage = $("#customerForm").kendoValidator({
            validateOnBlur: false,
            rules: {
                upload: function (input) {
                    if ((input[0].type == "file") && ($(input[0]).is('[validationMessage]'))) {
                        return input.closest(".k-upload").find(".k-file").length;
                    }
                    return true;
                }
            }
        }).data("kendoValidator");
        $("#photoIdImage").kendoUpload({multiple: false});

        $('#confirmPassword').keypress(function (event) {
            $('#retypePassError').hide();
        });

        setValues();

        $('#sendByEmail').click(function (e) {
            return disablePhotoId();
        });

        $('#isCorporate').click(function (e) {
            return checkCorporatePanel();
        });

        $('#companyRegNo').attr('disabled', 'disabled');
    //    $("#dateOfIncorporation").data("kendoDatePicker").enable(false);

        $('#smsSubscription').attr('checked', 'checked');

        $('#mailSubscription').attr('checked', 'checked');

        //checkVisaExpire();

        showMessage();
    });

    function showMessage() {
        if (modelJsonList.isError) {
            showError(modelJsonList.message);
        }
        return false;
    }

    function initCustRegistrationForm() {
        $('#customerForm').kendoValidator({validateOnBlur: false});
        $('#customerForm').submit(function (e) {
            return executePreCondition();
        });
    }

    function checkPasswordMatch() {
        if ($("#password").val() != $("#confirmPassword").val()) {
            $('#retypePassError').show();
            return false;
        } else {
            $('#retypePassError').hide();
            return true;
        }
    }

    function checkVisaExpire() {
        var countryId = dropDownCountry.value();
        var companyCountryId = company.countryId;
        $('#lblVisaExpireDate').removeClass('label-required');
        $('#lblVisaExpireDate').removeClass('label-optional');
        $("#visaExpireDate").data("kendoDatePicker").enable(true);
        if (countryId == companyCountryId) {
            $('#visaExpireDate').val('');
            $("#visaExpireDate").data("kendoDatePicker").enable(false);
            $('#lblVisaExpireDate').addClass('label-optional');
            $('#visaExpireDate').attr('required', false);

        } else {
            $('#lblVisaExpireDate').addClass('label-required');
            $('#visaExpireDate').attr('required', true);
        }
    }

    function setValues() {
        $("#companyId").val(company.id);
        $("#companyName").html(company.name);
        $('#isdCode').text(isdCode);
    }

    function resetForm() {
        clearForm($("#customerForm"));
        $('#agentId').val('0');
        $('#photoIdImage').val('');

        $('#companyRegNo').attr('disabled', 'disabled');
        $("#dateOfIncorporation").data("kendoDatePicker").enable(false);
        $('#lblVisaExpireDate').addClass('label-required');
        $("#visaExpireDate").data("kendoDatePicker").enable(true);
        $('#lblCompanyRegNo').removeClass('label-holder-req');
        $('#lblDateOfIncorporation').removeClass('label-holder-req');
        $('#companyRegNo').removeClass('required');
        $('#dateOfIncorporation').removeClass('required');
        $('#smsSubscription').attr('checked', false);
        $('#mailSubscription').attr('checked', false);
        $('#lblCompanyRegNo').removeClass('label-required');
        $('#lblDateOfIncorporation').removeClass('label-required');
        $("#photoId").removeAttr("disabled");
        $("#lblPhotoId").addClass('label-required');
        $("#companyId").val(company.id);
        $('#name').focus();

    }

    function checkCorporatePanel() {
        var checked = $('#isCorporate').attr('checked');
        if (checked) {
            $('#lblCompanyRegNo').addClass('label-required');
            $('#lblDateOfIncorporation').addClass('label-required');
            $('#companyRegNo').addClass('required');
            $('#companyRegNo').attr('required', true);
            $('#companyRegNo').attr('disabled', false);
            $('#dateOfIncorporation').addClass('required');
            $('#companyRegNo').removeAttr('disabled');
            $('#dateOfIncorporation').attr('required', true);
            $("#dateOfIncorporation").data("kendoDatePicker").enable(true);
        } else {
            $('#lblCompanyRegNo').removeClass('label-required');
            $('#lblDateOfIncorporation').removeClass('label-required');
            $('#companyRegNo').removeClass('required');
            $('#companyRegNo').attr('required', false);
            $('#companyRegNo').attr('disabled', true);
            $('#dateOfIncorporation').removeClass('required');
            $('#companyRegNo').attr('disabled', 'disabled');
            $("#dateOfIncorporation").data("kendoDatePicker").enable(false);
        }
    }

    function executePreCondition() {
        if (!validateForm($("#customerForm"))) {
            return false;
        }

        var passLength = $("#password").val().length;
        var hiddenPassLength = $("#existingPass").val().length;

        if (hiddenPassLength <= 0) {
            $("#password").addClass('required');
            $("#confirmPassword").addClass('required');
        } else if (hiddenPassLength > 0 && passLength == 0) {
            $("#password").removeClass('required');
            $("#confirmPassword").removeClass('required');
        } else {
            $("#password").addClass('required');
            $("#confirmPassword").addClass('required');
            $("#password").addClass('error');
        }


        if ($("#customerForm").validate({onfocusout: false}).form() == false) {
            return false;
        }

        if (!checkCustomDate($("#customerDateOfBirth"), "Date of Birth ")) {
            return false;
        }

        if (checkPasswordMatch() == false) return false;
        //showLoadingSpinner(true);
        return true;
    }
    function disablePhotoId() {
        var checked = $('#sendByEmail').attr('checked');
        if (checked) {
            $('#lblPhotoId').removeClass('label-required');
            $('#lblPhotoId').addClass('label-optional');
            $("#photoIdImage").val('');
            $("#photoIdImage").removeAttr("validationMessage");
        } else {
            $('#lblPhotoId').addClass('label-required');
            $('#lblPhotoId').removeClass('label-optional');
            $("#photoIdImage").attr("validationMessage");
        }
    }

</script>