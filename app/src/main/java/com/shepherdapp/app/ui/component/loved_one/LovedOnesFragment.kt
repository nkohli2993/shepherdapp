package com.shepherdapp.app.ui.component.loved_one

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.shepherdapp.app.R
import com.shepherdapp.app.ShepherdApp
import com.shepherdapp.app.data.dto.care_team.CareTeamModel
import com.shepherdapp.app.data.dto.login.UserLovedOne
import com.shepherdapp.app.databinding.FragmentLovedOnesBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseFragment
import com.shepherdapp.app.ui.component.addLovedOneCondition.AddLovedOneConditionActivity
import com.shepherdapp.app.ui.component.loved_one.adapter.LovedOneAdapter
import com.shepherdapp.app.ui.component.subscription.SubscriptionActivity
import com.shepherdapp.app.utils.CareRole
import com.shepherdapp.app.utils.Const
import com.shepherdapp.app.utils.Prefs
import com.shepherdapp.app.utils.Status
import com.shepherdapp.app.utils.extensions.showError
import com.shepherdapp.app.view_model.LovedOneViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_join_care_team.*


@AndroidEntryPoint
class LovedOnesFragment : BaseFragment<FragmentLovedOnesBinding>(), View.OnClickListener,
    LovedOneAdapter.OnItemClickListener {

    private lateinit var fragmentLovedOnesBinding: FragmentLovedOnesBinding
    private var lovedOneAdapter: LovedOneAdapter? = null
    private val lovedOneViewModel: LovedOneViewModel by viewModels()
    private var page = 1
    private var limit = 10
    private var status = Status.One.status
    private var careTeams: ArrayList<CareTeamModel> = arrayListOf()
    private var selectedCare: CareTeamModel? = null
    private var currentPage: Int = 0
    private var totalPage: Int = 0
    private var total: Int = 0

    private var TAG = "LovedOnesFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentLovedOnesBinding =
            FragmentLovedOnesBinding.inflate(inflater, container, false)
        return fragmentLovedOnesBinding.root
    }

    override fun onResume() {
        super.onResume()
        careTeams.clear()
        page = 1
        if (lovedOneViewModel.isLoggedInUserLovedOne() == true) {
            Log.d(TAG, "onResume: LovedOne loggedIn")
            lovedOneViewModel.getCareTeamsByLovedOneId(page, limit, status)
        } else {
            lovedOneViewModel.getCareTeamsForLoggedInUser(page, limit, status)
        }

        // Check if loggedIn User is CareTeam Leader for selected lovedOne
        val lovedOneDetail = lovedOneViewModel.getLovedOneDetail()
        val isCareTeamLeader = lovedOneViewModel.isLoggedInUserCareTeamLeader()
        val isNewVisible =
            (lovedOneDetail?.careRoles?.slug == CareRole.CareTeamLead.slug) || (isCareTeamLeader == true)
        if (isNewVisible) {
            fragmentLovedOnesBinding.tvNew.visibility = View.VISIBLE
        } else {
            fragmentLovedOnesBinding.tvNew.visibility = View.INVISIBLE
        }


        /* val lovedOneDetail = lovedOneViewModel.getLovedOneDetail()
         val isNewVisible = when (lovedOneDetail?.careRoles?.slug) {
             CareRole.CareTeamLead.slug -> {
                 fragmentLovedOnesBinding.tvNew.visibility = View.VISIBLE
                 true
             }
             else -> {
                 fragmentLovedOnesBinding.tvNew.visibility = View.INVISIBLE
                 false
             }
         }*/
    }

    private fun handleAddedLovedOnePagination() {
        var isScrolling: Boolean
        var visibleItemCount: Int
        var totalItemCount: Int
        var pastVisiblesItems: Int

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fragmentLovedOnesBinding.scrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                if (scrollY > oldScrollY) {
                    Log.i("TAG", "Scroll DOWN")
                    isScrolling = true
                    visibleItemCount =
                        fragmentLovedOnesBinding.recyclerViewMembers.layoutManager!!.childCount
                    totalItemCount =
                        fragmentLovedOnesBinding.recyclerViewMembers.layoutManager!!.itemCount
                    pastVisiblesItems =
                        (fragmentLovedOnesBinding.recyclerViewMembers.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    if (isScrolling && visibleItemCount + pastVisiblesItems >= totalItemCount && (currentPage < totalPage)) {
                        isScrolling = false
                        currentPage++
                        page++
                        lovedOneViewModel.getCareTeamsForLoggedInUser(page, limit, status)
                    }
                }
                if (scrollY == v.measuredHeight - v.getChildAt(0).measuredHeight) {
                    Log.i("TAG", "BOTTOM SCROLL")
                }
            })

        }
    }

    override fun observeViewModel() {
        lovedOneViewModel.careTeamsResponseLiveData.observeEvent(this) { result ->
            when (result) {
                is DataResult.Failure -> {
                    hideLoading()
                    result.message?.let { showError(requireContext(), it) }
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    careTeams.clear()
                    if (page == 1) {
                        lovedOneAdapter = null
                        setLovedOnesAdapter()
                    }
                    //To fix : Duplicate LovedOne names in LovedOne Listing
                    // If LoggedIn user is loved one, get first object and add to careTeams

                    if (lovedOneViewModel.isLoggedInUserLovedOne() == true) {
                        careTeams.add(result.data.payload.data.first())
                        lovedOneAdapter?.addData(careTeams)
                    } else {
                        val data = result.data.payload.data
                        // To fix : Duplicate loved One
                        // If love_user_id at index 0 matches with the love_user_id at index, pick first object only
                        if (data.size == 1) {
                            careTeams = data
                            lovedOneAdapter?.addData(careTeams)
                        } else {
                            if (data[0].love_user_id == data[1].love_user_id) {
                                careTeams.add(result.data.payload.data.first())
                                lovedOneAdapter?.addData(careTeams)
                            } else {
                                result.data.payload.let { payload ->
                                    careTeams = payload.data
                                    total = payload.total!!
                                    currentPage = payload.currentPage!!
                                    totalPage = payload.totalPages!!
                                }
                                lovedOneAdapter?.addData(careTeams)
                            }
                        }


                        /* if (data[0].love_user_id == data[1].love_user_id) {
                             careTeams.add(result.data.payload.data.first())
                             lovedOneAdapter?.addData(careTeams)
                         } else {
                             result.data.payload.let { payload ->
                                 careTeams = payload.data
                                 total = payload.total!!
                                 currentPage = payload.currentPage!!
                                 totalPage = payload.totalPages!!
                             }
                             lovedOneAdapter?.addData(careTeams)
                         }*/
                    }
                    val lovedOneIDInPrefs =
                        Prefs.with(ShepherdApp.appContext)!!.getString(Const.LOVED_ONE_UUID, "")
                    for (i in careTeams) {
                        if (lovedOneIDInPrefs.equals(i.love_user_id_details?.uid)) {
                            selectedCare = i
                            break
                        }
                    }
                }
            }
        }

        /*       lovedOneViewModel.checkSubscriptionStatusResponseLiveData.observeEvent(this) {
                   when (it) {
                       is DataResult.Failure -> {
                           hideLoading()
                       }
                       is DataResult.Loading -> {
                           showLoading("")
                       }
                       is DataResult.Success -> {
                           hideLoading()
                           val type = it.data.payload?.type
                           val status = it.data.payload?.status
                           if (status == true) {
                               findNavController().navigate(
                                   LovedOnesFragmentDirections.actionNavLovedOneToNavAddLovedOne(
                                       source = Const.ADD_LOVE_ONE
                                   )
                               )
                           } else {
                               var message: String? = null
                               if (type == Const.Type.ENTERPRISE) {
                                   message = getString(R.string.user_limit_exceeded)
                               } else if (type == Const.Type.SUBSCRIPTION) {
                                   message = getString(R.string.your_subscription_has_been_expired)
                               }
                               val builder = AlertDialog.Builder(requireContext())
                               val dialog = builder.apply {
                                   setTitle(getString(R.string.app_name))
                                   setMessage(message)
                                   setPositiveButton(getString(R.string.ok)) { _, _ ->
                                   }
                               }.create()
                               dialog.show()
                               dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                           }

                       }
                   }
               }*/
    }

    override fun initViewBinding() {
        fragmentLovedOnesBinding.listener = this
        setLovedOnesAdapter()
    }

    private fun setLovedOnesAdapter() {
        lovedOneAdapter = LovedOneAdapter(lovedOneViewModel)
        recyclerViewMembers.adapter = lovedOneAdapter
        lovedOneAdapter?.setClickListener(this)
        handleAddedLovedOnePagination()
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_loved_ones
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tvNew -> {
//                lovedOneViewModel.checkSubscriptionStatus()
                // Check if the loggedIn User has active subscription or attached to enterprise
                if ((!lovedOneViewModel.isUserAttachedToEnterprise()!!) && (!lovedOneViewModel.isSubscriptionPurchased()!!)) {
                    Log.d(
                        TAG,
                        "onClick: User has neither purchased subscription nor attached to enterprise"
                    )
                    // Redirect to Subscription Screen
                    val intent = Intent(requireContext(), SubscriptionActivity::class.java)
                    intent.putExtra("source", "LovedOne Screen")
                    startActivity(intent)
                    requireActivity().overridePendingTransition(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left
                    )
//                    requireContext().startActivity<SubscriptionActivity>()
                } else {
                    Log.d(TAG, "onClick:${careTeams.size} ")

                    // Filter the CareTeam(LovedOne List) according to "isInvited as false" as we need
                    // to get the careTeam whose members(lovedOne) are added by the loggedIn User i.e not invited

                    // isInvited = false means that the lovedOne is added by the loggedIn user
                    // isInvited = true means that the loggedIn user is being invited for the lovedOne

                    val addedCareTeamList = careTeams.filter {
                        it.isInvited == false
                    }

                    Log.d(TAG, "onClick: addedCareTeamList Size is  ${addedCareTeamList.size}")
                    if (addedCareTeamList.size < 3) {
                        findNavController().navigate(
                            LovedOnesFragmentDirections.actionNavLovedOneToNavAddLovedOne(
                                source = Const.ADD_LOVE_ONE
                            )
                        )
                    } else {
                        val builder = AlertDialog.Builder(requireContext())
                        val dialog = builder.apply {
                            setTitle(getString(R.string.app_name))
                            setMessage(getString(R.string.you_can_add_up_to_3_loved_one))
                            setPositiveButton(getString(R.string.ok)) { _, _ ->
                            }
                        }.create()
                        dialog.show()
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                    }
                }


                /*  Log.d(TAG, "onClick:${careTeams.size} ")
                  if (careTeams.size < 3) {
                      findNavController().navigate(
                          LovedOnesFragmentDirections.actionNavLovedOneToNavAddLovedOne(
                              source = Const.ADD_LOVE_ONE
                          )
                      )
                  } else {
                      val builder = AlertDialog.Builder(requireContext())
                      val dialog = builder.apply {
                          setTitle(getString(R.string.app_name))
                          setMessage(getString(R.string.you_can_add_up_to_3_loved_one))
                          setPositiveButton(getString(R.string.ok)) { _, _ ->
                          }
                      }.create()
                      dialog.show()
                      dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                  }*/
            }
            R.id.btnDone -> {
                selectedCare?.let { careTeams ->
                    careTeams.love_user_id_details.let {
                        lovedOneViewModel.saveLovedOneUUID(it?.uid!!)
                        val lovedOneDetail = UserLovedOne(
                            id = it.id,
                            userId = it.uid,
                            loveUserId = careTeams.love_user_id,
                            roleId = careTeams.role_id,
                            permission = careTeams.permission,
                            firstName = it.firstname,
                            lastName = it.lastname,
                            profilePic = it.profilePhoto
                        )
                        lovedOneViewModel.saveLovedOneUserDetail(lovedOneDetail)

                        // Get the care role name and saved into shared preferences
                        val roleName = careTeams.careRoles?.name
                        roleName?.let { it1 -> lovedOneViewModel.saveUserRole(it1) }
                    }
                }

//                backPress()
                // Redirect to dashboard fragment
                findNavController().navigate(R.id.action_nav_loved_one_to_nav_dashboard)
            }
            R.id.ivBack -> {
                backPress()
            }
        }
    }

    override fun onItemClick(careTeam: CareTeamModel, type: String) {
        when (type) {
            "Medical" -> {
                val intent = Intent(requireContext(), AddLovedOneConditionActivity::class.java)
                intent.putExtra("source", Const.MEDICAL_CONDITION)
                intent.putExtra("love_one_id", careTeam.love_user_id)
                startActivity(intent)
                requireActivity().overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
            }
            "Detail" -> {
//                findNavController().navigate(R.id.action_nav_loved_one_to_lovedOneProfileFragment)
                findNavController().navigate(
                    LovedOnesFragmentDirections.actionNavLovedOneToLovedOneProfileFragment(
                        careTeam
                    )
                )
            }
            "Selected" -> {
                selectedCare = careTeam
            }
            else -> {
//                selectedCare = careTeam
            }
        }
    }
}