package com.example.foodorder.activity

import android.annotation.SuppressLint
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.foodorder.R
import com.example.foodorder.adapter.MoreImageAdapter
import com.example.foodorder.constant.Constant
import com.example.foodorder.database.FoodDatabase
import com.example.foodorder.databinding.ActivityFoodDetailBinding
import com.example.foodorder.event.ReloadListCartEvent
import com.example.foodorder.model.Food
import com.example.foodorder.utils.GlideUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.greenrobot.eventbus.EventBus

class FoodDetailActivity : BaseActivity() {

    private var mActivityFoodDetailBinding: ActivityFoodDetailBinding? = null
    private var mFood: Food? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityFoodDetailBinding = ActivityFoodDetailBinding.inflate(layoutInflater)
        setContentView(mActivityFoodDetailBinding?.root)
        getDataIntent()
        initToolbar()
        setDataFoodDetail()
        initListener()
    }

    private fun getDataIntent() {
        val bundle = intent.extras
        if (bundle != null) {
            mFood = bundle[Constant.KEY_INTENT_FOOD_OBJECT] as Food?
        }
    }

    private fun initToolbar() {
        mActivityFoodDetailBinding?.toolbar?.imgBack?.visibility = View.VISIBLE
        mActivityFoodDetailBinding?.toolbar?.imgCart?.visibility = View.VISIBLE
        mActivityFoodDetailBinding?.toolbar?.tvTitle?.text = getString(R.string.food_detail_title)
        mActivityFoodDetailBinding?.toolbar?.imgBack?.setOnClickListener { onBackPressed() }
    }

    private fun setDataFoodDetail() {
        if (mFood == null) {
            return
        }
        GlideUtils.loadUrlBanner(mFood!!.getBanner(), mActivityFoodDetailBinding?.imageFood)
        if (mFood!!.getSale() <= 0) {
            mActivityFoodDetailBinding?.tvSaleOff?.visibility = View.GONE
            mActivityFoodDetailBinding?.tvPrice?.visibility = View.GONE
            val strPrice = mFood!!.getPrice().toString() + Constant.CURRENCY
            mActivityFoodDetailBinding?.tvPriceSale?.text = strPrice
        } else {
            mActivityFoodDetailBinding?.tvSaleOff?.visibility = View.VISIBLE
            mActivityFoodDetailBinding?.tvPrice?.visibility = View.VISIBLE
            val strSale = "Giảm " + mFood!!.getSale() + "%"
            mActivityFoodDetailBinding?.tvSaleOff?.text = strSale
            val strPriceOld = mFood!!.getPrice().toString() + Constant.CURRENCY
            mActivityFoodDetailBinding?.tvPrice?.text = strPriceOld
            mActivityFoodDetailBinding?.tvPrice?.paintFlags = mActivityFoodDetailBinding?.tvPrice?.paintFlags?.or(Paint.STRIKE_THRU_TEXT_FLAG)!!
            val strRealPrice = mFood!!.getRealPrice().toString() + Constant.CURRENCY
            mActivityFoodDetailBinding?.tvPriceSale?.text = strRealPrice
        }
        mActivityFoodDetailBinding?.tvFoodName?.text = mFood!!.getName()
        mActivityFoodDetailBinding?.tvFoodDescription?.text = mFood!!.getDescription()

        displayListMoreImages()
        setStatusButtonAddToCart()
    }

    private fun displayListMoreImages() {
        if (mFood?.getImages() == null || mFood?.getImages()!!.isEmpty()) {
            mActivityFoodDetailBinding?.tvMoreImageLabel?.visibility = View.GONE
            return
        }
        mActivityFoodDetailBinding?.tvMoreImageLabel?.visibility = View.VISIBLE
        val gridLayoutManager = GridLayoutManager(this, 2)
        mActivityFoodDetailBinding?.rcvImages?.layoutManager = gridLayoutManager
        val moreImageAdapter = MoreImageAdapter(mFood?.getImages())
        mActivityFoodDetailBinding?.rcvImages?.adapter = moreImageAdapter
    }

    private fun setStatusButtonAddToCart() {
        if (isFoodInCart()) {
            mActivityFoodDetailBinding?.tvAddToCart?.setBackgroundResource(R.drawable.bg_gray_shape_corner_6)
            mActivityFoodDetailBinding?.tvAddToCart?.text = getString(R.string.added_to_cart)
            mActivityFoodDetailBinding?.tvAddToCart?.setTextColor(ContextCompat.getColor(this, R.color.textColorPrimary))
            mActivityFoodDetailBinding?.toolbar?.imgCart?.visibility = View.GONE
        } else {
            mActivityFoodDetailBinding?.tvAddToCart?.setBackgroundResource(R.drawable.bg_green_shape_corner_6)
            mActivityFoodDetailBinding?.tvAddToCart?.text = getString(R.string.add_to_cart)
            mActivityFoodDetailBinding?.tvAddToCart?.setTextColor(ContextCompat.getColor(this, R.color.white))
            mActivityFoodDetailBinding?.toolbar?.imgCart?.visibility = View.VISIBLE
        }
    }

    private fun isFoodInCart(): Boolean {
        val list: MutableList<Food>? = mFood?.let { FoodDatabase.getInstance(this)?.foodDAO()?.checkFoodInCart(it.getId()) }
        return list != null && list.isNotEmpty()
    }

    private fun initListener() {
        mActivityFoodDetailBinding?.tvAddToCart?.setOnClickListener { onClickAddToCart() }
        mActivityFoodDetailBinding?.toolbar?.imgCart?.setOnClickListener { onClickAddToCart() }
    }

    @SuppressLint("InflateParams")
    private fun onClickAddToCart() {
        if (isFoodInCart() || mFood == null) {
            return
        }
        val viewDialog = layoutInflater.inflate(R.layout.layout_bottom_sheet_cart, null)
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(viewDialog)

        val imgFoodCart = viewDialog.findViewById<ImageView?>(R.id.img_food_cart)
        val tvFoodNameCart = viewDialog.findViewById<TextView?>(R.id.tv_food_name_cart)
        val tvFoodPriceCart = viewDialog.findViewById<TextView?>(R.id.tv_food_price_cart)
        val tvSubtractCount = viewDialog.findViewById<TextView?>(R.id.tv_subtract)
        val tvCount = viewDialog.findViewById<TextView?>(R.id.tv_count)
        val tvAddCount = viewDialog.findViewById<TextView?>(R.id.tv_add)
        val tvCancel = viewDialog.findViewById<TextView?>(R.id.tv_cancel)
        val tvAddCart = viewDialog.findViewById<TextView?>(R.id.tv_add_cart)

        GlideUtils.loadUrl(mFood!!.getImage(), imgFoodCart)
        tvFoodNameCart?.text = mFood!!.getName()
        val totalPrice = mFood!!.getRealPrice()
        val strTotalPrice = totalPrice.toString() + Constant.CURRENCY
        tvFoodPriceCart?.text = strTotalPrice
        mFood!!.setCount(1)
        mFood!!.setTotalPrice(totalPrice)

        tvSubtractCount?.setOnClickListener {
            val count = tvCount?.text.toString().toInt()
            if (count > 1) {
                val newCount = tvCount?.text.toString().toInt() - 1
                tvCount?.text = newCount.toString()
                val totalPrice1 = mFood!!.getRealPrice() * newCount
                val strTotalPrice1 = totalPrice1.toString() + Constant.CURRENCY
                tvFoodPriceCart?.text = strTotalPrice1
                mFood!!.setCount(newCount)
                mFood!!.setTotalPrice(totalPrice1)
            }
        }
        tvAddCount?.setOnClickListener {
            val newCount = tvCount?.text.toString().toInt() + 1
            tvCount?.text = newCount.toString()
            val totalPrice2 = mFood!!.getRealPrice() * newCount
            val strTotalPrice2 = totalPrice2.toString() + Constant.CURRENCY
            tvFoodPriceCart?.text = strTotalPrice2
            mFood!!.setCount(newCount)
            mFood!!.setTotalPrice(totalPrice2)
        }

        tvCancel?.setOnClickListener { bottomSheetDialog.dismiss() }
        tvAddCart?.setOnClickListener {
            FoodDatabase.getInstance(this@FoodDetailActivity)?.foodDAO()?.insertFood(mFood)
            bottomSheetDialog.dismiss()
            setStatusButtonAddToCart()
            EventBus.getDefault().post(ReloadListCartEvent())
        }
        bottomSheetDialog.show()
    }
}