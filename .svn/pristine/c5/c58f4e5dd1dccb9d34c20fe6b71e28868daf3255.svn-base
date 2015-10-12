<script language="javascript" type="text/javascript">
    var output = false;
    var exhRegularFeeModel = false;
    $(document).ready(function () {
        onLoadExhRegularFee();
    });

    function onLoadExhRegularFee() {
        initializeForm($("#exhRegularForm"), onSubmitExhRegularFee);
        initializeForm($("#frmEvaluateLogic"), evaluateRegularFee);

        output =${output ? output : ''};
        if (output.isError) {
            showError(data.message);
        } else {
            exhRegularFeeModel = output.exhRegularFee;

            $('#id').val(exhRegularFeeModel.id);
            $('#version').val(exhRegularFeeModel.version);
            $('#logic').val(exhRegularFeeModel.logic);
        }

        //update page title
        $(document).attr('title', "Exchange House  - Update Regular Fee");
        loadNumberedMenu(MENU_ID_EXCHANGE_HOUSE, "#exhRegularFee/show");
    }

    function evaluateRegularFee() {

        if ($('#amount').val() == '') {
            showError("Please enter amount");
            return false;
        }
        showLoadingSpinner(true);
        var amount = $('#amount').val();
        $.ajax({
            url: "${createLink(controller:'exhRegularFee', action:'calculate')}?amount=" + amount,
            success: executePostConditionForCalculate,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConditionForCalculate(data) {
        if (data.isError == true) {
            showError(data.message);
            $('#lblResult').text('');
            return false;
        }
        $('#lblResult').text(data.regularFee);
    }

    function onSubmitExhRegularFee() {

        if (!validateForm("#exhRegularForm")) {
            return false;
        }
        setButtonDisabled($('.save'), true);
        // Spinner Show on AJAX Call
        showLoadingSpinner(true);
        var actionUrl = "${createLink(controller:'exhRegularFee', action: 'update')}";

        jQuery.ajax({
            type: 'post',
            data: jQuery("#exhRegularForm").serialize(),
            url: actionUrl,
            success: function (data, textStatus) {
                if (data.isError == true) {
                    showError(data.message);
                } else {
                    showSuccess(data.message);
                }
                showLoadingSpinner(false);
                setButtonDisabled($('.save'), false);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
            },
            complete: function (XMLHttpRequest, textStatus) {
            },
            dataType: 'json'
        });
        return false;
    }

</script>