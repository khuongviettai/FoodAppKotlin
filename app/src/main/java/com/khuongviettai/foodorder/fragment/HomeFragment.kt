package com.khuongviettai.foodorder.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.khuongviettai.foodorder.ControllerApplication
import com.khuongviettai.foodorder.R
import com.khuongviettai.foodorder.activity.FoodDetailActivity
import com.khuongviettai.foodorder.activity.MainActivity
import com.khuongviettai.foodorder.adapter.FoodGridAdapter
import com.khuongviettai.foodorder.adapter.FoodPopularAdapter
import com.khuongviettai.foodorder.constant.Constant
import com.khuongviettai.foodorder.constant.GlobalFuntion
import com.khuongviettai.foodorder.databinding.FragmentHomeBinding
import com.khuongviettai.foodorder.listener.IOnClickFoodItemListener
import com.khuongviettai.foodorder.model.Food
import com.khuongviettai.foodorder.utils.StringUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.*

class HomeFragment : BaseFragment() {

    private var mFragmentHomeBinding: FragmentHomeBinding? = null
    private var mListFood: MutableList<Food>? = null
    private var mListFoodPopular: MutableList<Food>? = null
    private var mFoodPopularAdapter: FoodPopularAdapter? = null
    private var mFoodGridAdapter: FoodGridAdapter? = null
    private val mHandlerBanner: Handler = Handler(Looper.getMainLooper())
    private val mRunnableBanner: Runnable = Runnable {
        if (mListFoodPopular == null || mListFoodPopular!!.isEmpty()) {
            return@Runnable
        }
        if (mFragmentHomeBinding?.viewpager2?.currentItem == mListFoodPopular!!.size - 1) {
            mFragmentHomeBinding?.viewpager2?.currentItem = 0
            return@Runnable
        }
        mFragmentHomeBinding?.viewpager2?.currentItem = mFragmentHomeBinding?.viewpager2?.currentItem!! + 1
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mFragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        getListFoodFromFirebase("")
        initListener()
        return mFragmentHomeBinding?.root
    }

    override fun initToolbar() {
        if (activity != null) {
            (activity as MainActivity?)?.setToolBar(true, getString(R.string.home))
        }
    }

    private fun initListener() {
        mFragmentHomeBinding?.edtSearchName?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Do nothing
            }

            override fun afterTextChanged(s: Editable?) {
                val strKey = s.toString().trim { it <= ' ' }
                if (strKey == "" || strKey.isEmpty()) {
                    if (mListFood != null) mListFood!!.clear()
                    getListFoodFromFirebase("")
                }
            }
        })
        mFragmentHomeBinding?.imgSearch?.setOnClickListener { searchFood() }
        mFragmentHomeBinding?.edtSearchName?.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchFood()
                    return true
                }
                return false
            }
        })
    }

    private fun displayListFoodPopular() {
        mFoodPopularAdapter = FoodPopularAdapter(getListFoodPopular(), object : IOnClickFoodItemListener {
            override fun onClickItemFood(food: Food) {
                goToFoodDetail(food)
            }
        })
        mFragmentHomeBinding?.viewpager2?.adapter = mFoodPopularAdapter
        mFragmentHomeBinding?.indicator3?.setViewPager(mFragmentHomeBinding?.viewpager2)
        mFragmentHomeBinding?.viewpager2?.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                mHandlerBanner.removeCallbacks(mRunnableBanner)
                mHandlerBanner.postDelayed(mRunnableBanner, 3000)
            }
        })
    }

    private fun displayListFoodSuggest() {
        val gridLayoutManager = GridLayoutManager(activity, 2)
        mFragmentHomeBinding?.rcvFood?.layoutManager = gridLayoutManager
        mFoodGridAdapter = FoodGridAdapter(mListFood, object : IOnClickFoodItemListener {
            override fun onClickItemFood(food: Food) {
                goToFoodDetail(food)
            }
        })
        mFragmentHomeBinding?.rcvFood?.adapter = mFoodGridAdapter
    }

    private fun getListFoodPopular(): MutableList<Food>? {
        mListFoodPopular = ArrayList()
        if (mListFood == null || mListFood!!.isEmpty()) {
            return mListFoodPopular
        }
        for (food in mListFood!!) {
            if (food.isPopular()) {
                mListFoodPopular?.add(food)
            }
        }
        return mListFoodPopular
    }

    private fun getListFoodFromFirebase(key: String?) {
        if (activity == null) {
            return
        }
        ControllerApplication[activity].getFoodDatabaseReference()?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mFragmentHomeBinding?.layoutContent?.visibility = View.VISIBLE
                mListFood = ArrayList()
                for (dataSnapshot in snapshot.children) {
                    val food: Food = dataSnapshot.getValue<Food>(Food::class.java) ?: return
                    if (StringUtil.isEmpty(key)) {
                        mListFood?.add(0, food)
                    } else {
                        if (GlobalFuntion.getTextSearch(food.getName()!!).trim().toLowerCase(Locale.getDefault())
                                        .contains(GlobalFuntion.getTextSearch(key!!).trim().toLowerCase(Locale.getDefault()))) {
                            mListFood?.add(0, food)
                        }
                    }
                }
                displayListFoodPopular()
                displayListFoodSuggest()
            }

            override fun onCancelled(error: DatabaseError) {
                GlobalFuntion.showToastMessage(activity, getString(R.string.msg_get_date_error))
            }
        })
    }

    private fun searchFood() {
        if (activity == null) {
            return
        }
        val strKey = mFragmentHomeBinding?.edtSearchName?.text.toString().trim { it <= ' ' }
        if (mListFood != null) mListFood!!.clear()
        getListFoodFromFirebase(strKey)
        GlobalFuntion.hideSoftKeyboard(activity!!)
    }

    private fun goToFoodDetail(food: Food) {
        if (activity == null) {
            return
        }
        val bundle = Bundle()
        bundle.putSerializable(Constant.KEY_INTENT_FOOD_OBJECT, food)
        GlobalFuntion.startActivity(activity!!, FoodDetailActivity::class.java, bundle)
    }

    override fun onPause() {
        super.onPause()
        mHandlerBanner.removeCallbacks(mRunnableBanner)
    }

    override fun onResume() {
        super.onResume()
        mHandlerBanner.postDelayed(mRunnableBanner, 3000)
    }
}