<script type="text/javascript">
    var validatorBankStatement, dropDownBank, bankStatementFile;
    var output =${output ? output : ''};
    var uploading = false;

    $(document).ready(function () {
        // common initializeForm() is not used here due to customValidation/upload
        initFormWithCustomRule();

        $("#bankStatementFile").kendoUpload({
            multiple: false,
            select: onSelect
        });
        bankStatementFile = $("#bankStatementFile").data("kendoUpload");

        var actionUrl = "${createLink(controller: 'accBankStatement',action: 'uploadBankStatementFile')}";
        $("#bankStatementUploadFrm").attr('action', actionUrl);

        $('#bankStatementUploadFrm').iframePostForm({
            post: function () {
                uploading = true;
                setButtonDisabled($('#uploadButton'), true);
                showLoadingSpinner(true);
            },
            complete: function (response) {
                if (uploading == true) {
                    setButtonDisabled($('#uploadButton'), false);
                    showLoadingSpinner(false);
                    onCSVFileUpload(response);
                    uploading = false;
                }
            },
            beforePost: function () {
                clearMsg();
                if (uploadBankStatements() == false) return false;
                return true;
            }
        });
    });

    function initFormWithCustomRule() {
        validatorBankStatement = $("#bankStatementUploadFrm").kendoValidator({
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
    }

    function onSelect(e) {
        $.each(e.files, function (index, value) {
            if (!value.extension.endsWith('.csv')) {
                showError('Invalid file extension (.csv expected)');
                return false;
            }
        })
    }

    function onCSVFileUpload(resp) {
        clearGeneralError();
        $('#errorListShow').hide();
        try {
            var data = eval('(' + resp + ')');
        } catch (e) {
            showError('Error evaluating server response. Refresh the page and try again.');
            return false;
        }
        if (data.isError == true) {
            if (data.generalError == true) {
                showError(data.message);
            } else {
                renderError(data.errors);
            }
        } else {
            clearErrorsUploadBankStatements();
            $('#bankStatementFile').val('');
            showSuccess(data.message);
            // reset kando upload
            $(".k-delete").parent().click();
        }
    }

    function uploadBankStatements() {
        $('#errorListShow').hide();

        if (!validatorBankStatement.validate()) {
            return false;
        }
        return true;
    }

    var errorList = false;
    function renderError(errors) {
        clearGeneralError();
        $('#errorListShow').hide();
        $('#csvErrorDiv').html('');
        $('#csvErrorDiv').html("<table id='flex1' style='display:none;'><thead><tr><th width='30'>SL</th><th width='100'>Trans Date</th><th width='80'>Trans Type</th><th width='80'>Company Ref</th><th width='80'>Narrative</th><th width='80'>Bank Ref</th><th width='80'>Trans Ref</th><th width='80'>Debit</th><th width='80'>Credit</th><th width='80'>Balance</th></tr></thead><tbody></tbody></table>");

        var tbody = $('#flex1 > tbody');

        errorList = $(errors);
        errorList.each(function (idx) {
            var uploadBankStatements = errorList[idx];
            tbody.append($('<tr onclick="showCsvError(this.id)"></tr>').attr('id', uploadBankStatements.serial)
                    .append($('<td></td>').html(uploadBankStatements.serial))
                    .append($('<td></td>').html(uploadBankStatements.strTransactionDate ? uploadBankStatements.strTransactionDate : ''))
                    .append($('<td></td>').html(uploadBankStatements.transactionType ? uploadBankStatements.transactionType : ''))
                    .append($('<td></td>').html(uploadBankStatements.companyRef ? uploadBankStatements.companyRef : ''))
                    .append($('<td></td>').html(uploadBankStatements.narrative ? uploadBankStatements.narrative : ''))
                    .append($('<td></td>').html(uploadBankStatements.bankRef ? uploadBankStatements.bankRef : ''))
                    .append($('<td></td>').html(uploadBankStatements.transactionRef ? uploadBankStatements.transactionRef : ''))
                    .append($('<td></td>').html(uploadBankStatements.strDebit ? uploadBankStatements.strDebit : ''))
                    .append($('<td></td>').html(uploadBankStatements.strCredit ? uploadBankStatements.strCredit : ''))
                    .append($('<td></td>').html(uploadBankStatements.strBalance ? uploadBankStatements.strBalance : ''))
            );
        });

        $('#flex1').flexigrid({
            singleSelect: true,
            title: 'Following Bank Statement contain error',
            height: getGridHeight(3) + 40,
            afterAjax: function () {
                showLoadingSpinner(false);// Spinner hide after AJAX Call
            }
        });
        $('#csvErrorDiv_Option_Pane').show();
    }

    function showGeneralError(error) {
        $('#errorDiv').empty();
        $('#errorDiv').show();
        $('#errorDiv').text(error);
    }

    function clearGeneralError() {
        $('#errorDiv').empty();
        $('#errorDiv').hide();
    }

    function showCsvError(id) {
        $('#errorListShow').hide();
        if (errorList == false) {
            showError('No error to display');
            return;
        }
        var selectedRows = $('table#flex1 > tbody > tr.trSelected');
        try {
            if (id) {
                for (i = 0; i < errorList.length; i++) {
                    var bankStatement = errorList[i];
                    if (bankStatement.serial == id) {
                        $('#csv-error-list').html('');
                        var bankStatementErrors = $(bankStatement.errors);
                        bankStatementErrors.each(function (idx) {
                            $('#csv-error-list').append($('<li class="error"></li>').html(bankStatementErrors[idx]));
                        });
                        $('#errorListShow').show();
                        break;
                    }
                }
            }
            return;
        } catch (e) {
        }
    }

    function clearSuccessDiv() {
        $('#successDiv').empty();
        $('#successDiv').hide('');
    }

    function clearMsg() {
        clearSuccessDiv();
        clearErrorsUploadBankStatements();
    }

    function clearFormUploadBankStatements() {
        var validator = $("#bankStatementUploadFrm").validate();
        validator.resetForm();
        clearErrorsUploadBankStatements();
        clearValidationErrors();
        bankStatementFile.value('');

        %{-- IE fix --}%
        document.getElementById('bankStatementUploadFrm').reset();
    }

    function clearErrorsUploadBankStatements() {
        $('#errorListShow').hide();
        $('#csvErrorDiv').html('');
        $('#errorDivDupRefNo').empty();
        $('#errorDivDupRefNo').hide();
    }

    function resetForm() {
        validatorBankStatement.hideMessages();
        clearMsg();
        clearForm($("#bankStatementUploadFrm"), dropDownBank);
        dropDownBank.value('');
        $(".k-delete").parent().click(); // remove selected file from kendo Upload
    }

    function clearValidationErrors() {
        $('input.error').each(function () {
            $(this).removeClass('error').attr('title', '');
        });

        $('label.error').each(function () {
            $(this).remove();
        });

        $("#errorList > li").remove();
    }

    // update page title
    $(document).attr('title', "MIS - Upload Bank Statement");
    loadNumberedMenu(MENU_ID_ACCOUNTING, "#accBankStatement/show");

</script>