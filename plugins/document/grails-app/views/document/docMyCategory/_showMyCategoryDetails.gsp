<div id="subCategoryCount">
    <doc:countSubCategories category_id="${params.categoryId ? params.categoryId : 0L}"></doc:countSubCategories>
</div>

<div id="categoryDetailsContainer">
    <doc:categoriesDetails category_id="${params.categoryId ? params.categoryId : 0L}"></doc:categoriesDetails>
</div>

<div id="categoryManagersContainer">
    <doc:categoryManagers category_id="${params.categoryId ? params.categoryId : 0L}"></doc:categoryManagers>
</div>




