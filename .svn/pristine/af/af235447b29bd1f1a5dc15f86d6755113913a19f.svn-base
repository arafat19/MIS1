<script type="text/javascript">

    var categoryObj, subCategoryObj, categoryLabel, subCategoryLabel, output;

    $(document).ready(function () {
        onLoadSubCategory();
    });

    function onLoadSubCategory() {
        output = ${modelJson ? modelJson : ''};
        initializeForm($('#applyForm'));

        categoryObj = output.category;               // set data in a global variable to populate
        subCategoryObj = output.subCategory;               // set data in a global variable to populate
        categoryLabel = output.categoryLabel;
        subCategoryLabel = output.subCategoryLabel;
        $("#subCategoryLabel").html(subCategoryLabel);
        $("#categoryName").html(categoryObj.name);
        $("#categoryDescription").html(categoryObj.description);
        $("#subCategoryName").html(subCategoryObj.name);
        $("#subCategoryDescription").html(subCategoryObj.description?subCategoryObj.description:'');
        $("#url").html(subCategoryObj.url);
        $("#categoryId").val(categoryObj.id);
        $("#subCategoryId").val(subCategoryObj.id);

        $(document).attr('title', "DOC -" + subCategoryLabel + " Details");
    }

    function showDivForApplyMember() {
        $('#applyForSubCategoryMembershipDiv').show();
    }

    function hideDivForApplyMember() {
        $('#applyForSubCategoryMembershipDiv').hide();
        clearForm($('#applyForm'));
    }
</script>
