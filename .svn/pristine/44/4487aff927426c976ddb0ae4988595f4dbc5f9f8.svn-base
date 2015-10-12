<script type="text/javascript">

    var categoryObj, categoryLabel, output;

    $(document).ready(function () {
        onLoadCategory();
    });

    function onLoadCategory() {
        output = ${modelJson ? modelJson : ''};
        initializeForm($('#applyForm'));

        categoryObj = output.entity;               // set data in a global variable to populate
        categoryLabel = output.categoryLabel;
        $("#categoryLabel").html(categoryLabel);
        $("#name").html(categoryObj.name);
        $("#description").html(categoryObj.description);
        $("#url").html(categoryObj.url);
        $("#categoryId").val(categoryObj.id);

        $(document).attr('title', "DOC -" + categoryLabel + " Details");
    }

    function showDivForApplyMember() {
        $('#applyForMembershipDiv').show();
    }

    function hideDivForApplyMember() {
        $('#applyForMembershipDiv').hide();
        clearForm($('#applyForm'));
    }
</script>
