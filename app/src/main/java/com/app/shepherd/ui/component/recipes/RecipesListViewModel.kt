package com.app.shepherd.ui.component.recipes

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.shepherd.data.DataRepositorySource
import com.app.shepherd.data.Resource
import com.app.shepherd.data.dto.recipes.Recipes
import com.app.shepherd.data.dto.recipes.RecipesItem
import com.app.shepherd.ui.base.BaseViewModel
import com.app.shepherd.utils.SingleEvent
import com.app.shepherd.utils.wrapEspressoIdlingResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Locale.ROOT
import javax.inject.Inject

/**
 * Created by Sumit Kumar
 */
@HiltViewModel
class RecipesListViewModel @Inject
constructor(private val dataRepositoryRepository: DataRepositorySource) : BaseViewModel() {

    /**
     * Data --> LiveData, Exposed as LiveData, Locally in viewModel as MutableLiveData
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val recipesLiveDataPrivate = MutableLiveData<Resource<Recipes>>()
    val recipesLiveData: LiveData<Resource<Recipes>> get() = recipesLiveDataPrivate


    //TODO check to make them as one Resource
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val recipeSearchFoundPrivate: MutableLiveData<RecipesItem> = MutableLiveData()
    val recipeSearchFound: LiveData<RecipesItem> get() = recipeSearchFoundPrivate

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val noSearchFoundPrivate: MutableLiveData<Unit> = MutableLiveData()
    val noSearchFound: LiveData<Unit> get() = noSearchFoundPrivate

    /**
     * UI actions as event, user action is single one time event, Shouldn't be multiple time consumption
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val openRecipeDetailsPrivate = MutableLiveData<SingleEvent<RecipesItem>>()
    val openRecipeDetails: LiveData<SingleEvent<RecipesItem>> get() = openRecipeDetailsPrivate

    /**
     * Error handling as UI
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val showSnackBarPrivate = MutableLiveData<SingleEvent<Any>>()
    val showSnackBar: LiveData<SingleEvent<Any>> get() = showSnackBarPrivate

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()
    val showToast: LiveData<SingleEvent<Any>> get() = showToastPrivate


    fun getRecipes() {
        viewModelScope.launch {
            recipesLiveDataPrivate.value = Resource.Loading()
            wrapEspressoIdlingResource {
                dataRepositoryRepository.requestRecipes().collect {
                    recipesLiveDataPrivate.value = it
                }
            }
        }
    }

    fun openRecipeDetails(vararg itemData: Any) {
        openRecipeDetailsPrivate.value = SingleEvent(itemData[0] as RecipesItem)
    }

    fun showToastMessage(errorCode: Int) {
        val error = errorManager.getError(errorCode)
        showToastPrivate.value = SingleEvent(error.description)
    }

    fun onSearchClick(recipeName: String) {
        recipesLiveDataPrivate.value?.data?.recipesList?.let {
            if (it.isNotEmpty()) {
                for (recipe in it) {
                    if (recipe.name.toLowerCase(ROOT).contains(recipeName.toLowerCase(ROOT))) {
                        recipeSearchFoundPrivate.value = recipe
                        return
                    }
                }
            }
        }
        return noSearchFoundPrivate.postValue(Unit)
    }
}
