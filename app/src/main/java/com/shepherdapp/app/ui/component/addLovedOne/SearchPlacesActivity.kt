package com.shepherdapp.app.ui.component.addLovedOne

import CommonFunctions
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.shepherdapp.app.R
import com.shepherdapp.app.databinding.ActivitySearchPlacesBinding
import com.shepherdapp.app.ui.base.BaseActivity
import com.shepherdapp.app.ui.component.addLovedOne.adapter.SearchPlacesAdapter
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.shepherdapp.app.utils.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Deepak Rattan on 06-06-22
 */

@AndroidEntryPoint
class SearchPlacesActivity : BaseActivity(), SearchPlacesAdapter.ClickListener,
    View.OnClickListener {
    private var searchPlacesAdapter: SearchPlacesAdapter? = null
    private lateinit var binding: ActivitySearchPlacesBinding
    private var searchType: String? = null

    companion object {
        const val TAG: String = "SearchPlacesFragment"
    }

    override fun initViewBinding() {
        binding = ActivitySearchPlacesBinding.inflate(layoutInflater)
        val view = binding.root
        searchType = intent.getStringExtra("search_type")
        when (searchType) {
            "event" -> {
                binding.editTextSearch.setHint(getString(R.string.please_enter_the_event_address))
            }
            else -> {
                binding.editTextSearch.setHint(getString(R.string.please_enter_the_loved_one_address))
            }
        }
        setContentView(view)
        initPlacesAutoComplete()
        initRecyclerView()
        initListeners()
    }

    private fun initRecyclerView() {

        CommonFunctions.showKeyBoard(this)
        binding.listSearch.layoutManager = LinearLayoutManager(this)
        searchPlacesAdapter = SearchPlacesAdapter(this)
        searchPlacesAdapter?.setClickListener(this)
        binding.listSearch.adapter = searchPlacesAdapter
        searchPlacesAdapter?.notifyDataSetChanged()

    }

    private fun initPlacesAutoComplete() {
        if (!Places.isInitialized()) {
            Places.initialize(this, getString(R.string.google_maps_api_key))
        }
    }

    private fun initListeners() {
        binding.listener = this

        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != "") {
                    searchPlacesAdapter?.filter?.filter(s.toString())
                    if (binding.listSearch.visibility == View.GONE) {
                        binding.listSearch.visibility = View.VISIBLE
                    }
                } else {
                    if (binding.listSearch.visibility == View.VISIBLE) {
                        binding.listSearch.visibility = View.GONE
                    }
                }
            }
        })
    }

    override fun click(place: Place?) {
        CommonFunctions.hideKeyBoard(this, binding.parentLayout)
        val intent = Intent()
        intent.putExtra("placeId", place?.id)
        intent.putExtra("latitude", place?.latLng?.latitude)
        intent.putExtra("longitude", place?.latLng?.longitude)
        intent.putExtra("placeName", place?.address)
        setResult(10101, intent)
        finish()
    }

    override fun observeViewModel() = Unit
    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.img_clear_text -> {
                if (binding.editTextSearch.text.toString().isEmpty()) {
                    hideKeyboard()
                    onBackPressed()
                } else {
                    binding.editTextSearch.text.clear()
                }

            }

        }
    }


}