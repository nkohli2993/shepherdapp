package com.shepherdapp.app.ui.component.resources

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.resource.AllResourceData
import com.shepherdapp.app.data.dto.resource.CategoryData
import com.shepherdapp.app.databinding.FragmentResourcesBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseFragment
import com.shepherdapp.app.ui.base.listeners.ChildFragmentToActivityListener
import com.shepherdapp.app.ui.component.home.HomeActivity
import com.shepherdapp.app.ui.component.resources.adapter.MedicalCategoryTagsAdapter
import com.shepherdapp.app.ui.component.resources.adapter.MedicalHistoryTopicsAdapter
import com.shepherdapp.app.ui.component.resources.adapter.TopicsAdapter
import com.shepherdapp.app.utils.SingleEvent
import com.shepherdapp.app.utils.extensions.hideKeyboard
import com.shepherdapp.app.utils.extensions.showError
import com.shepherdapp.app.utils.extensions.showInfo
import com.shepherdapp.app.utils.observeEvent
import com.shepherdapp.app.view_model.ResourceViewModel
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Nikita 31-08-2022
 */
@AndroidEntryPoint
class ResourcesFragment : BaseFragment<FragmentResourcesBinding>(), View.OnClickListener {

    private lateinit var fragmentResourcesBinding: FragmentResourcesBinding
    private val resourcesViewModel: ResourceViewModel by viewModels()
    private var addedConditionPayload: ArrayList<CategoryData> = arrayListOf()
    private var pageNumber = 1
    private var pageNumberCategoryTag = 1
    private var currentPageCategoryTag: Int = 0
    private var totalPageCategoryTag: Int = 0
    private val limit = 20
    private var currentPage: Int = 0
    private var totalPage: Int = 0
    private var total: Int = 0
    private var topicsAdapter: TopicsAdapter? = null
    private var medicalHistoryTopicsAdapter: MedicalHistoryTopicsAdapter? = null
    private var medicalCategoryTagsAdapter: MedicalCategoryTagsAdapter? = null
    private var resourceList: ArrayList<AllResourceData> = arrayListOf()
    private var medicalCategoryTagList: ArrayList<CategoryData> = arrayListOf()
    private var trendingResourceList: ArrayList<AllResourceData> = arrayListOf()
    private var conditionIDs: ArrayList<Int> = arrayListOf()
    private var isSearch: Boolean = false
    private var isLoading = false
    private var TAG = "ResourcesFragment"

    private var parentActivityListener: ChildFragmentToActivityListener? = null

    private lateinit var homeActivity: HomeActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HomeActivity) {
            homeActivity = context
        }
        if (context is ChildFragmentToActivityListener) parentActivityListener = context
        else throw RuntimeException("$context must implement ChildFragmentToActivityListener")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentResourcesBinding =
            FragmentResourcesBinding.inflate(inflater, container, false)

        return fragmentResourcesBinding.root
    }

    override fun initViewBinding() {
        fragmentResourcesBinding.listener = this
        resourcesViewModel.getLovedOneUUId()
            ?.let { resourcesViewModel.getLovedOneMedicalConditions(it) }


        /*resourcesViewModel.getResourceCategories(
            pageNumberCategoryTag,
            limit
        )*/

//        callAllResourceBasedOnMedicalConditions()

//        resourcesViewModel.getTrendingResourceApi(pageNumber, limit)
        setTopicsAdapter()
        setMedicalHistoryTopicsAdapter()
        setMedicalConditionsTagAdapter()
        /*  resourcesViewModel.getResourceCategories(
              pageNumber,
              limit
          )*/
//        resourcesViewModel.getUserDetailByUUID()

        fragmentResourcesBinding.imgCancel.setOnClickListener {
            fragmentResourcesBinding.editTextSearch.setText("")
            showTrendingPost(View.VISIBLE)
            resourceList.clear()
            pageNumber = 1
            isSearch = false
            hideKeyboard()
            callAllResourceBasedOnMedicalConditions()
        }


        fragmentResourcesBinding.editTextSearch.doOnTextChanged { s, _, lengthBefore, lengthAfter ->
            if (s.toString().isEmpty() && (lengthBefore == 0 && lengthAfter == 0)) {
                fragmentResourcesBinding.imgCancel.visibility = View.GONE
                showTrendingPost(View.VISIBLE)
                hideKeyboard()
            } else if (s.toString().isEmpty() && (lengthBefore > 0 && lengthAfter >= 0)) {
                fragmentResourcesBinding.imgCancel.visibility = View.GONE
                showTrendingPost(View.VISIBLE)
                pageNumber = 1
                isSearch = false
                hideKeyboard()
                callAllResourceBasedOnMedicalConditions()
            } else {
                fragmentResourcesBinding.imgCancel.visibility = View.VISIBLE
                showTrendingPost(View.GONE)
                //Hit search api
                isSearch = true
                resourceList.clear()
                pageNumber = 1
                resourcesViewModel.getSearchResourceResultApi(
                    pageNumber,
                    limit,
//                    resourcesViewModel.getLovedOneUUId()!!,
                    /*  conditionIDs.toString()
                          .replace(" ", ""),*/
                    fragmentResourcesBinding.editTextSearch.text.toString().trim()
                )

            }
        }
    }

    override fun onResume() {
        super.onResume()
        parentActivityListener?.msgFromChildFragmentToActivity()
        fragmentResourcesBinding.editTextSearch.setText("")
        resourceList.clear()
        pageNumber = 1
        pageNumberCategoryTag = 1
        isSearch = false


        if (!resourcesViewModel.getCategoryIds().isNullOrEmpty()) {
            val conditionIDs = resourcesViewModel.getCategoryIds()
            val medicalCategoryTagList = resourcesViewModel.getCategoryDataList()
            Log.d(TAG, "onResume: categoryIds : $conditionIDs")
            if (conditionIDs?.size == 0) {
                pageNumber = 1
                callAllResourceBasedOnMedicalConditions()
                resourcesViewModel.getResourceCategories(
                    pageNumberCategoryTag,
                    limit
                )
            } else if (!resourcesViewModel.getCategoryDataList().isNullOrEmpty()) {
                Log.d(TAG, "onResume: medicalCategoryTagList : $medicalCategoryTagList")
                if (medicalCategoryTagList != null) {
                    medicalCategoryTagsAdapter?.addData(medicalCategoryTagList)
                }
                pageNumberCategoryTag = 1
                conditionIDs?.joinToString()?.replace(" ", "")?.let {
                    resourcesViewModel.getAllResourceAsPerCategoryApi(
                        pageNumberCategoryTag,
                        limit,
                        it
                    )
                }
            } else {
                Log.d(TAG, "onResume: ")
                callAllResourceBasedOnMedicalConditions()
                resourcesViewModel.getResourceCategories(
                    pageNumberCategoryTag,
                    limit
                )
            }

        } else {
            callAllResourceBasedOnMedicalConditions()
            resourcesViewModel.getResourceCategories(
                pageNumberCategoryTag,
                limit
            )
        }
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_resources
    }


    private fun showTrendingPost(value: Int) {
//        fragmentResourcesBinding.textViewTrendingTopics.visibility = value
//        fragmentResourcesBinding.recyclerViewTopics.visibility = value
//        fragmentResourcesBinding.textViewBasedOnMedicalHistory.visibility = value
//        fragmentResourcesBinding.medicalHistory.visibility = value
    }

    private fun handleResourcesPagination() {
        var isScrolling: Boolean
        var visibleItemCount: Int
        var totalItemCount: Int
        var pastVisiblesItems: Int

        fragmentResourcesBinding.recyclerViewMedicalHistoryTopics.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    isScrolling = true
                    visibleItemCount = recyclerView.layoutManager!!.childCount
                    totalItemCount = recyclerView.layoutManager!!.itemCount
                    pastVisiblesItems =
                        (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    if (isScrolling && visibleItemCount + pastVisiblesItems >= totalItemCount && (currentPage < totalPage)) {
                        isScrolling = false
//                        currentPage++
//                        pageNumber++
                        callAllResourceBasedOnMedicalConditions()
                    }
                }
            }
        })
    }

    private fun handleMedicalCategoryPagination() {
        var isScrolling: Boolean
        var visibleItemCount: Int
        var totalItemCount: Int
        var pastVisibleItems: Int

        fragmentResourcesBinding.rvMedicalConditionsTag.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    isScrolling = true
                    visibleItemCount = recyclerView.layoutManager!!.childCount
                    totalItemCount = recyclerView.layoutManager!!.itemCount
                    pastVisibleItems =
                        (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    if (isScrolling && visibleItemCount + pastVisibleItems >= totalItemCount && (currentPageCategoryTag < totalPageCategoryTag)) {
                        isScrolling = false
//                        currentPage++
//                        pageNumber++
//                        getAllResourcesBasedOnMedicalConditionsSelected()
                        resourcesViewModel.getResourceCategories(
                            pageNumberCategoryTag,
                            limit
                        )

                    }
                }
            }
        })
    }

    private fun getAllResourcesBasedOnMedicalConditionsSelected() {
        resourcesViewModel.getAllResourceApi(
            pageNumber,
            limit,
//            resourcesViewModel.getLovedOneUUId()!!,
//            null
            //conditionIDs.joinToString().replace(" ", "")
        )

    }

    private fun callAllResourceBasedOnMedicalConditions() {
        resourcesViewModel.getAllResourceApi(
            pageNumber,
            limit,
//            resourcesViewModel.getLovedOneUUId()!!,
//            null
            //conditionIDs.joinToString().replace(" ", "")
        )

    }

    override fun observeViewModel() {

        observeEvent(resourcesViewModel.selectedResourceDetail, ::navigateToResourceItems)
        observeEvent(
            resourcesViewModel.selectedMedicalCategoryTAG,
            ::openSelectedMedicalCategoryTag
        )

        /*resourcesViewModel.userDetailByUUIDLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    val userProfile = it.data.payload?.userProfiles
                    val lovedOneFirstName = userProfile?.firstname
                    val lovedOneLastName = userProfile?.lastname
                    var lovedOneFullName: String? = null
                    lovedOneFullName = if (lovedOneLastName.isNullOrEmpty()) {
                        lovedOneFirstName
                    } else {
                        "$lovedOneFirstName $lovedOneLastName"
                    }

                    val lovedOneProfilePic = userProfile?.profilePhoto

                    Picasso.get().load(lovedOneProfilePic)
                        .placeholder(R.drawable.ic_defalut_profile_pic)
                        .into(fragmentResourcesBinding.imgLovedOne)

                    fragmentResourcesBinding.txtLoved.text = lovedOneFullName

                }
            }
        }*/

        //Observe the response of get loved one's medical conditions api
        resourcesViewModel.categoryResourceResponseLiveData.observeEvent(this) { result ->
            when (result) {
                is DataResult.Failure -> {
                    hideLoading()
                    result.message?.let { showError(requireContext(), it) }
                    if (conditionIDs.isEmpty()) {
                        pageNumber = 1
                        callAllResourceBasedOnMedicalConditions()
                    }
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
//                    addedConditionPayload.clear()
//                    fragmentResourcesBinding.medicalHistory.removeAllViews()
                    /*  for (i in result.data.payload.categories) {
                          i.isSelected = true
                          addedConditionPayload.add(i)
                          conditionIDs.add(i.id!!)
                      }*/
                    // set tags
//                    setMedicalTags()


//                    callAllResourceBasedOnMedicalConditions()
                    // Get categories
                    medicalCategoryTagList = result.data.payload.categories
                    medicalCategoryTagsAdapter?.addData(medicalCategoryTagList)
                    currentPageCategoryTag = result.data.payload.currentPage
                    totalPageCategoryTag = result.data.payload.total
                    pageNumberCategoryTag = currentPage + 1
                }
            }
        }

        // observe resource list based on condition based
        resourcesViewModel.resourceResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    fragmentResourcesBinding.recyclerViewMedicalHistoryTopics.visibility = View.GONE
                    fragmentResourcesBinding.btnJoin.visibility = View.GONE
                    fragmentResourcesBinding.noResourceTxt.visibility = View.VISIBLE

//                    showError(requireContext(), it.message.toString())
                }
                is DataResult.Loading -> {
                    if (isLoading) {
                        showLoading("")
                    }
                }
                is DataResult.Success -> {
                    hideLoading()
                    isLoading = false
                    resourceList.clear()
                    if (pageNumber == 1) {
                        medicalHistoryTopicsAdapter = null
                        setMedicalHistoryTopicsAdapter()
                    }
                    if (it.data.message == "Successfully found") {
                        resourceList = it.data.payload!!.data
                        it.data.payload.let { payload ->
                            resourceList = payload!!.data
                            total = payload.total
                            currentPage = payload.currentPage
                            totalPage = payload.totalPages
                            pageNumber = currentPage + 1
                        }

                        if (resourceList.isEmpty()) return@observeEvent

                        if (resourceList.isEmpty()) {
                            fragmentResourcesBinding.recyclerViewMedicalHistoryTopics.visibility =
                                View.GONE
                            fragmentResourcesBinding.noResourceTxt.visibility = View.VISIBLE

                        } else {
                            fragmentResourcesBinding.noResourceTxt.visibility = View.GONE
                            fragmentResourcesBinding.recyclerViewMedicalHistoryTopics.visibility =
                                View.VISIBLE
                            resourceList.let { it1 ->
                                medicalHistoryTopicsAdapter?.addData(
                                    it1, isSearch
                                )
                            }
                        }
                    } else {
                        fragmentResourcesBinding.recyclerViewMedicalHistoryTopics.visibility =
                            View.GONE
                        fragmentResourcesBinding.noResourceTxt.visibility = View.VISIBLE

                    }

                }
            }
        }
        // observe resource list based on treding list
        resourcesViewModel.trendingResourceResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    showError(requireContext(), it.message.toString())
                }
                is DataResult.Loading -> {
                    //showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    trendingResourceList = it.data.payload!!.data

                    if (trendingResourceList.isEmpty()) return@observeEvent

                    if (trendingResourceList.isEmpty()) {
//                        fragmentResourcesBinding.recyclerViewTopics.visibility = View.GONE

                    } else {
                        /*fragmentResourcesBinding.recyclerViewTopics.visibility =
                            View.VISIBLE*/
                        trendingResourceList.let { it1 ->
                            topicsAdapter?.addData(
                                it1
                            )
                        }
                    }
                }
            }
        }

    }

    private fun navigateToResourceItems(navigateEvent: SingleEvent<AllResourceData>) {
        // Save selected categoryIDs to sharedPref
        /* Log.d(TAG, "CategoryIDs : $conditionIDs")
         if (conditionIDs.size != 0) {
             resourcesViewModel.saveCategoryIds(categoryIds = conditionIDs)
             resourcesViewModel.saveCategoryDataList(categoryDataList = medicalCategoryTagList)
         }*/
        navigateEvent.getContentIfNotHandled()?.let {
            findNavController().navigate(
                ResourcesFragmentDirections.actionNavResourceToResourceToResourceDetail(
                    source = it.id.toString(),
                    detail = it
                )
            )
        }
    }


    private fun openSelectedMedicalCategoryTag(navigateEvent: SingleEvent<CategoryData>) {
        navigateEvent.getContentIfNotHandled()?.let { it ->
            medicalCategoryTagList.map { categoryData ->
                if (categoryData.id == it.id) {
                    categoryData.isSelected = !categoryData.isSelected
                }
            }
            Log.d(TAG, "medicalCategoryTagList : $medicalCategoryTagList")
            medicalCategoryTagsAdapter?.addData(medicalCategoryTagList)

            conditionIDs.clear()
            conditionIDs = medicalCategoryTagList.filter {
                it.isSelected
            }.map {
                it.id
            } as ArrayList<Int>

            Log.d(TAG, "selected IDs : $conditionIDs")

            if (conditionIDs.size == 0) {

                conditionIDs.clear()
                medicalCategoryTagList.clear()

                // Save the empty categoryIDs and categoryDataList in SharedPref
                resourcesViewModel.saveCategoryIds(categoryIds = conditionIDs)
                resourcesViewModel.saveCategoryDataList(categoryDataList = medicalCategoryTagList)

                pageNumber = 1
                callAllResourceBasedOnMedicalConditions()
                resourcesViewModel.getResourceCategories(
                    pageNumberCategoryTag,
                    limit
                )
            } else {
                pageNumberCategoryTag = 1
                resourcesViewModel.getAllResourceAsPerCategoryApi(
                    pageNumberCategoryTag,
                    limit,
                    conditionIDs.joinToString().replace(" ", "")
                )

                // Save the categoryIDs and categoryDataList in SharedPref
                resourcesViewModel.saveCategoryIds(categoryIds = conditionIDs)
                resourcesViewModel.saveCategoryDataList(categoryDataList = medicalCategoryTagList)
            }


        }
    }

    private fun setTopicsAdapter() {
        topicsAdapter = TopicsAdapter(resourcesViewModel)
//        fragmentResourcesBinding.recyclerViewTopics.adapter = topicsAdapter
    }

    private fun createTagView(text: CategoryData, position: Int): View {
        val parent = LinearLayout(context)

        parent.layoutParams =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        parent.orientation = LinearLayout.HORIZONTAL
        val closeIV = ImageView(context)

        val layout2 = LinearLayout(context)
        layout2.layoutParams =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        layout2.orientation = LinearLayout.HORIZONTAL
        parent.addView(layout2)
        val textConditionName = TextView(context)
        layout2.addView(textConditionName)
        layout2.addView(closeIV)
        textConditionName.text = text.name
        textConditionName.typeface = ResourcesCompat.getFont(requireContext(), R.font.gotham_book)
        textConditionName.setTextColor(ContextCompat.getColor(requireContext(), R.color._192032))
        textConditionName.setPadding(20, 15, 10, 15)
        parent.setPadding(5, 5, 10, 5)
        closeIV.setPadding(0, 10, 0, 0)
        textConditionName.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorWhite))
        parent.setBackgroundResource(R.drawable.shape_black_border_filled)
        closeIV.setImageResource(R.drawable.ic_round_cancel)
        if (addedConditionPayload[position].isSelected) {
            textConditionName.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorBlack
                )
            )
            closeIV.setImageResource(R.drawable.ic_add_filled)
            parent.setBackgroundResource(R.drawable.shape_black_border)
        }

        layout2.gravity = Gravity.CENTER_HORIZONTAL

        textConditionName.setOnClickListener {
            if (addedConditionPayload[position].isSelected) {
                callSelectedConditionResources(position)
            }
        }
        closeIV.setOnClickListener {
            callSelectedConditionResources(position)
        }

        return parent
    }

    private fun callSelectedConditionResources(position: Int) {
        addedConditionPayload[position].isSelected =
            !addedConditionPayload[position].isSelected
//        setMedicalTags()
        conditionIDs.clear()
        isLoading = true
        for (i in addedConditionPayload) {
            if (!i.isSelected) {
                conditionIDs.add(i.id!!)
            }
        }
        callAllResourceBasedOnMedicalConditions()
    }


    /*private fun setMedicalTags() {
        //set medical history names
        fragmentResourcesBinding.medicalHistory.removeAllViews()
        for (medicalCondition in addedConditionPayload.indices) {
            fragmentResourcesBinding.medicalHistory.addView(
                createTagView(addedConditionPayload[medicalCondition], medicalCondition),
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )
        }
    }*/

    private fun setMedicalHistoryTopicsAdapter() {
        medicalHistoryTopicsAdapter = MedicalHistoryTopicsAdapter(resourcesViewModel)
        fragmentResourcesBinding.recyclerViewMedicalHistoryTopics.adapter =
            medicalHistoryTopicsAdapter
        handleResourcesPagination()
    }

    private fun setMedicalConditionsTagAdapter() {
        medicalCategoryTagsAdapter = MedicalCategoryTagsAdapter(resourcesViewModel)
        fragmentResourcesBinding.rvMedicalConditionsTag.adapter = medicalCategoryTagsAdapter
        handleMedicalCategoryPagination()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnJoin -> {
                showInfo(requireContext(), getString(R.string.coming_soon))
            }
        }
    }


}

