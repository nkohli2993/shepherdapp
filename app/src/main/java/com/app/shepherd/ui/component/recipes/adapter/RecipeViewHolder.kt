package com.app.shepherd.ui.component.recipes.adapter

import androidx.recyclerview.widget.RecyclerView
import com.app.shepherd.R
import com.app.shepherd.data.dto.recipes.RecipesItem
import com.app.shepherd.databinding.AdapterReceipeListBinding
import com.app.shepherd.ui.base.listeners.RecyclerItemListener
import com.app.shepherd.utils.loadImage

/**
 * Created by Sumit Kumar
 */

class RecipeViewHolder(private val itemBinding: AdapterReceipeListBinding) :
    RecyclerView.ViewHolder(itemBinding.root) {

    fun bind(recipesItem: RecipesItem, recyclerItemListener: RecyclerItemListener) {
        itemBinding.tvCaption.text = recipesItem.description
        itemBinding.tvName.text = recipesItem.name
        itemBinding.ivRecipeItemImage.loadImage(R.drawable.ic_healthy_food, recipesItem.thumb)
        itemBinding.rlRecipeItem.setOnClickListener {
            recyclerItemListener.onItemSelected(
                recipesItem
            )
        }
    }
}

