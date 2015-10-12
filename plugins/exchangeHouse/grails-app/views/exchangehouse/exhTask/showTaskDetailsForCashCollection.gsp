<script type="text/javascript">
    //global variables
    var taskGridModel;
    var modelJsonForTaskDetails;
    $(function() {
        onLoadCCDisbursement();
    });

    function onLoadCCDisbursement() {
        modelJsonForTaskDetails = ${modelJson};
    }

    function showTaskDetailsFromGrid() {
        var data = modelJsonForTaskDetails;
        executePostConditionForSearchPin(data);
    }
    function executePostConditionForSearchPin(data) {
        try {
            showLoadingSpinner(true);
            $('#containerTable').fadeOut();
            $('#disbursementContainer').fadeOut();
            if (data.taskInfoMap) {
                showTaskDetails(data);
            }

        } catch(e) {
            taskGridModel = false;
            showLoadingSpinner(false);
        }
    }


    function clearErrorsCCDisburse() {
        $('input.error').each(function() {
            $(this).removeClass('error').attr('title', '');
        });

        $('label.error').each(function() {
            $(this).remove();
        });
    }

    window.onload = showTaskDetailsFromGrid();   // check if redirected from Grid

    function showTaskDetails(data) {
        var taskMap = data.taskInfoMap;
        try {
            $('#containerTable').fadeIn();
            $('#btnPrint').show();

            $('#exchangeHouseNameWithCountry').html(taskMap.exchangeHouse + ', ' + taskMap.country);
            $('#refNo').text(taskMap.transactionRefNum);
            $('#pinNo').text(taskMap.pinNo);
            $('#valueDate').text(taskMap.valueDate);
            $('#amount').text(taskMap.amount + ' BDT');
            $('#listName').text(taskMap.listName);

            // order info
            $('#isPaid').text(taskMap.isPaid);
            $('#taskStatus').text(' ( Status: '+ data.taskStatus + ' )');
            //$('#sentFromHO').text(data.sentFromHo);
            $('#paidOn').text(taskMap.paidOn);
            var isPaid = taskMap.isPaid;
            if (isPaid == 'Cancelled') {
                $('#isPaid').css("color", "red");
                $('#lblPaidOn').text('Cancelled On');
            } else {
                $('#isPaid').css("color", "blue");
                $('#lblPaidOn').text('Paid On:');
            }

            $('#paymentMethod').text(taskMap.paymentMethodName);
            $('#mappingBankInfo').text('');    // @todo- refactor in a proper way
            if (taskMap.mappingBankInfo) {
                $('#mappingBankInfo').text(taskMap.mappingBankInfo);
            }
            // sender info
            $('#senderName').text(taskMap.senderName);
            $('#senderPhone').text(taskMap.senderMobile);

            // beneficiary info
            $('#beneficiaryName').text(taskMap.beneficiaryName);
            $('#beneficiaryPhone').text(taskMap.beneficiaryPhone ? taskMap.beneficiaryPhone :'');
            $('#identityType').text(taskMap.identityType);
            $('#identityNo').text(taskMap.identityNo ? taskMap.identityNo :'');
            showLoadingSpinner(false);
        } catch(e) {
            showLoadingSpinner(false);
        }
    }

</script>

<div id="containerTable" class="table-responsive" style="display: none;">
    <table width="900px" class="table table-bordered">
        <tr>
            <td width="50%" style="padding: 0">
                <table class="table" style="margin-bottom: 0">
                    <tr>
                        <td colspan='2' class='active'>TASK DETAILS</td>
                    </tr>
                    <tr>
                        <td width="30%" class='active' nowrap="nowrap" valign="top">Exchange House:</td>
                        <td width="70%" id="exchangeHouseNameWithCountry"></td>
                    </tr>

                    <tr>
                        <td class='active' nowrap="nowrap" >Ref No:</td>
                        <td id="refNo"></td>
                    </tr>

                    <tr>
                        <td class='active' width='30%' nowrap="nowrap" >Pin No:</td>
                        <td width='70%' id="pinNo"></td>
                    </tr>
                    <tr>
                        <td class='active' nowrap="nowrap">Transaction Date:</td>
                        <td id="valueDate"></td>
                    </tr>

                    <tr>
                        <td class='active' nowrap="nowrap">Amount:</td>
                        <td id="amount"></td>
                    </tr>

                    <tr>
                        <td width="30%" class='active' nowrap="nowrap">Task List Name:</td>
                        <td width="70%"  id="listName"></td>
                    </tr>

                    <tr>
                        <td width="30%" class='active' nowrap="nowrap">Current Status:</td>
                        <td width="70%">
                            <span id="isPaid" style="font-weight: bold;"></span>
                            <span id="taskStatus"></span>
                        </td>
                    </tr>

                    <tr>
                        <td width="30%" class='active' nowrap="nowrap" id="lblPaidOn">Paid On:</td>
                        <td width="70%" id="paidOn"></td>
                    </tr>

                    <tr>
                        <td width="30%" class='active' nowrap="nowrap">Delivery Instruction:</td>
                        <td width="70%" id="paymentMethod"></td>
                    </tr>

                    <tr>
                        <td class='active' nowrap="nowrap">Destination Branch:</td>
                        <td  id="mappingBankInfo" style="font-weight: bold;color: blue"></td>
                    </tr>
                </table>
            </td>
            <td width="50%" style="padding: 0">
                <table style="margin-bottom: 0" class="table">
                    <tr>
                        <td colspan='2' class='active'>SENDER INFORMATION</td>
                    </tr>
                    <tr>
                        <td width="30%" class='active' nowrap="nowrap">Sender Name:</td>
                        <td width="70%" id="senderName"></td>
                    </tr>
                    <tr>
                        <td class='active' nowrap="nowrap">Telephone:</td>
                        <td id="senderPhone"></td>
                    </tr>
                </table>

                <table style="border-bottom: 1px solid #DDDDDD" class="table">
                    <tr>
                        <td colspan='2' class='active'>BENEFICIARY INFORMATION</td>
                    </tr>
                    <tr>
                        <td width="30%" class='active' nowrap="nowrap">Receiver Name:</td>
                        <td width="70%" id="beneficiaryName"></td>
                    </tr>
                    <tr>
                        <td class='active' nowrap="nowrap">Telephone:</td>
                        <td id="beneficiaryPhone"></td>
                    </tr>
                    <tr>
                        <td class='active' nowrap="nowrap">Identity Type:</td>
                        <td id="identityType"></td>
                    </tr>
                    <tr>
                        <td class='active' nowrap="nowrap">Identity No:</td>
                        <td id="identityNo"></td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</div>
