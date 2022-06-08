package com.app.shepherd.ui.component.addLovedOne
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.shepherd.R
import com.app.shepherd.databinding.ActivitySearchPlacesBinding
import com.app.shepherd.ui.base.BaseActivity
import com.app.shepherd.ui.component.addLovedOne.adapter.SearchPlacesAdapter
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Deepak Rattan on 06-06-22
 */

@AndroidEntryPoint
class SearchPlacesActivity : BaseActivity(), SearchPlacesAdapter.ClickListener {
    private var searchPlacesAdapter: SearchPlacesAdapter? = null
    private lateinit var binding: ActivitySearchPlacesBinding

    companion object {
        const val TAG: String = "SearchPlacesFragment"
    }

    override fun initViewBinding() {
        binding = ActivitySearchPlacesBinding.inflate(layoutInflater)
        val view = binding.root
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

}