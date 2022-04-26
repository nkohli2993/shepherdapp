package com.app.shepherd.ui.base.listeners

import com.app.shepherd.data.dto.recipes.RecipesItem

/**
 * Created by Sumit Kumar
 */

interface RecyclerItemListener {
    fun onItemSelected(recipe : RecipesItem)
}
