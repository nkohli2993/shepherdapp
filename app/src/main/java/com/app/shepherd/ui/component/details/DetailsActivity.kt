package com.app.shepherd.ui.component.details

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.app.shepherd.R
import com.app.shepherd.RECIPE_ITEM_KEY
import com.app.shepherd.data.Resource
import com.app.shepherd.data.dto.recipes.RecipesItem
import com.app.shepherd.databinding.ActivityDetailsBinding
import com.app.shepherd.ui.base.BaseActivity
import com.app.shepherd.utils.loadImage
import com.app.shepherd.utils.observe
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Sumit Kumar
 */

@AndroidEntryPoint
class DetailsActivity : BaseActivity() {

    private val viewModel: DetailsViewModel by viewModels()

    private lateinit var binding: ActivityDetailsBinding
    private var menu: Menu? = null


    override fun initViewBinding() {
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.initIntentData(intent.getParcelableExtra(RECIPE_ITEM_KEY) ?: RecipesItem())
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.details_menu, menu)
        this.menu = menu
        viewModel.isFavourites()
        return true
    }

    fun onClickFavorite(mi: MenuItem) {
        mi.isCheckable = false
        if (viewModel.isFavourite.value?.data == true) {
            viewModel.removeFromFavourites()
        } else {
            viewModel.addToFavourites()
        }
    }

    override fun observeViewModel() {
        observe(viewModel.recipeData, ::initializeView)
        observe(viewModel.isFavourite, ::handleIsFavourite)
    }

    private fun handleIsFavourite(isFavourite: Resource<Boolean>) {
        when (isFavourite) {
            is Resource.Loading -> {
                showLoading("")
            }
            is Resource.Success -> {
                isFavourite.data?.let {
                    handleIsFavouriteUI(it)
                    menu?.findItem(R.id.add_to_favorite)?.isCheckable = true
                    hideLoading()
                }
            }
            is Resource.DataError -> {
                menu?.findItem(R.id.add_to_favorite)?.isCheckable = true
                hideLoading()
            }
        }
    }

    private fun handleIsFavouriteUI(isFavourite: Boolean) {
        menu?.let {
            it.findItem(R.id.add_to_favorite)?.icon =
                if (isFavourite) {
                    ContextCompat.getDrawable(this, R.drawable.ic_star_24)
                } else {
                    ContextCompat.getDrawable(this, R.drawable.ic_outline_star_border_24)
                }
        }
    }

    private fun initializeView(recipesItem: RecipesItem) {
        binding.tvName.text = recipesItem.name
        binding.tvHeadline.text = recipesItem.headline
        binding.tvDescription.text = recipesItem.description
        binding.ivRecipeImage.loadImage(R.drawable.ic_healthy_food_small, recipesItem.image)
    }
}
