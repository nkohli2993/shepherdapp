package com.shepherdapp.app.ui.component.addLovedOne.adapter

/**
 * Created by Deepak Rattan on 06/06/22
 */

import android.content.Context
import android.graphics.Typeface
import android.text.style.CharacterStyle
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.PlaceAutoComplete
import com.shepherdapp.app.databinding.ActivitySearchPlacesItemBinding
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class SearchPlacesAdapter(
    val context: Context?
) :
    RecyclerView.Adapter<SearchPlacesAdapter.SelectedImagesViewHolder>(), Filterable {

    private var adapterListener: ClickListener? = null
    private var placesList: ArrayList<PlaceAutoComplete>? = null
    private var STYLE_BOLD: CharacterStyle? = null
    private var STYLE_NORMAL: CharacterStyle? = null
    var placesClient: PlacesClient? = null

    init {
        placesClient = context?.let { Places.createClient(it) }
        STYLE_BOLD = StyleSpan(Typeface.BOLD)
        STYLE_NORMAL = StyleSpan(Typeface.NORMAL)
        placesList = ArrayList()
    }


    fun setClickListener(clickListener: ClickListener) {
        adapterListener = clickListener
    }

    interface ClickListener {
        fun click(place: Place?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedImagesViewHolder {

        val view =
            LayoutInflater.from(context)
                .inflate(R.layout.activity_search_places_item, parent, false)
        return SelectedImagesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return placesList?.size ?: 0
    }


    fun getItem(position: Int): PlaceAutoComplete? {
        return placesList?.get(position)
    }

    override fun onBindViewHolder(holder: SelectedImagesViewHolder, position: Int) {
        adapterListener?.let { placesList?.get(position)?.let { it1 -> holder.bindView(it1) } }

        holder.itemView.setOnClickListener {
            if (!placesList.isNullOrEmpty()) {
                if (it.id == R.id.predictedRow) {
                    val placeId = placesList?.get(position)?.placeId
                    val placeFields: List<Place.Field> = listOf(
                        Place.Field.ID,
                        Place.Field.NAME,
                        Place.Field.LAT_LNG,
                        Place.Field.ADDRESS
                    )
                    val request: FetchPlaceRequest? =
                        placeId?.let { it1 -> FetchPlaceRequest.builder(it1, placeFields).build() }

                    request?.let { it1 ->
                        placesClient?.fetchPlace(it1)?.addOnSuccessListener { response ->
                            val place: Place = response.place
                            adapterListener?.click(place)
                        }?.addOnFailureListener { exception ->
                            if (exception is ApiException) {
                                if(context == null) return@addOnFailureListener
                                Toast.makeText(context, exception.message + "", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                }
            }

        }
    }

    fun updateList(homeServices: ArrayList<PlaceAutoComplete>?) {
        this.placesList = homeServices
        notifyDataSetChanged()
    }

    class SelectedImagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ActivitySearchPlacesItemBinding.bind(itemView)
        fun bindView(placeAutocomplete: PlaceAutoComplete) {
            binding.edtAddress.text = placeAutocomplete.area
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                // Skip the autocomplete query if no constraints are given.
                if (constraint != null) {
                    // Query the autocomplete API for the (constraint) search string.
                    placesList = getPredictions(constraint)
                    if (placesList != null) {
                        // The API successfully returned results.
                        results.values = placesList
                        results.count = placesList?.size ?: 0
                    }
                }
                return results
            }

            override fun publishResults(p0: CharSequence?, results: FilterResults?) {
                if (results != null && results.count > 0) {
                    // The API returned at least one result, update the data.
                    notifyDataSetChanged()
                }
            }
        }
    }

    private fun getPredictions(constraint: CharSequence): ArrayList<PlaceAutoComplete> {
        val resultList: ArrayList<PlaceAutoComplete> = ArrayList()

        // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
        // and once again when the user makes a selection (for example when calling fetchPlace()).
        // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
        // and once again when the user makes a selection (for example when calling fetchPlace()).
        val token = AutocompleteSessionToken.newInstance()

        // Use the builder to create a FindAutocompletePredictionsRequest.
        // Use the builder to create a FindAutocompletePredictionsRequest.
        val request =
            FindAutocompletePredictionsRequest.builder() // Call either setLocationBias() OR setLocationRestriction().
                //.setLocationBias(bounds)
                //.setCountry("BD")
                //.setTypeFilter(TypeFilter.ADDRESS)
                .setSessionToken(token)
                .setQuery(constraint.toString())
                .build()

        val autocompletePredictions =
            placesClient?.findAutocompletePredictions(request) as Task<FindAutocompletePredictionsResponse>

        // This method should have been called off the main UI thread. Block and wait for at most
        // 60s for a result from the API.
        try {
            Tasks.await(autocompletePredictions, 60, TimeUnit.SECONDS)
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: TimeoutException) {
            e.printStackTrace()
        }
        return if (autocompletePredictions.isSuccessful) {
            val findAutocompletePredictionsResponse = autocompletePredictions.result
            for (prediction in findAutocompletePredictionsResponse.autocompletePredictions) {
                Log.d("SearchPlacesAdapter", prediction.placeId)
                resultList.add(
                    PlaceAutoComplete(
                        prediction.placeId,
                        prediction.getPrimaryText(STYLE_NORMAL).toString(),
                        prediction.getFullText(STYLE_BOLD).toString()
                    )
                )
            }
            resultList
        } else resultList
    }

}