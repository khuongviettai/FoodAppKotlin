package com.khuongviettai.foodorder.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.khuongviettai.foodorder.ControllerApplication
import com.khuongviettai.foodorder.R
import com.khuongviettai.foodorder.activity.MainActivity
import com.khuongviettai.foodorder.adapter.CartAdapter
import com.khuongviettai.foodorder.adapter.CartAdapter.IClickListener
import com.khuongviettai.foodorder.constant.Constant
import com.khuongviettai.foodorder.constant.GlobalFuntion
import com.khuongviettai.foodorder.database.FoodDatabase
import com.khuongviettai.foodorder.databinding.FragmentCartBinding
import com.khuongviettai.foodorder.event.ReloadListCartEvent
import com.khuongviettai.foodorder.model.Food
import com.khuongviettai.foodorder.model.Order
import com.khuongviettai.foodorder.utils.StringUtil
import com.khuongviettai.foodorder.utils.Utils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

class CartFragment : BaseFragment() {

    private var mFragmentCartBinding: FragmentCartBinding? = null
    private var mCartAdapter: CartAdapter? = null
    private var mListFoodCart: MutableList<Food>? = null
    private var mAmount = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mFragmentCartBinding = FragmentCartBinding.inflate(inflater, container, false)
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        displayListFoodInCart()
        mFragmentCartBinding?.tvOrderCart?.setOnClickListener { onClickOrderCart() }
        return mFragmentCartBinding?.root
    }

    override fun initToolbar() {
        if (activity != null) {
            (activity as MainActivity?)?.setToolBar(false, getString(R.string.cart))
        }
    }

    private fun displayListFoodInCart() {
        if (activity == null) {
            return
        }
        val linearLayoutManager = LinearLayoutManager(activity)
        mFragmentCartBinding?.rcvFoodCart?.layoutManager = linearLayoutManager
        val itemDecoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        mFragmentCartBinding?.rcvFoodCart?.addItemDecoration(itemDecoration)
        initDataFoodCart()
    }

    private fun initDataFoodCart() {
        if (activity == null) {
            return
        }
        mListFoodCart = ArrayList()
        mListFoodCart = FoodDatabase.getInstance(activity!!)?.foodDAO()?.getListFoodCart()
        if (mListFoodCart == null || mListFoodCart!!.isEmpty()) {
            return
        }
        mCartAdapter = CartAdapter(mListFoodCart, object : IClickListener {
            override fun clickDeteteFood(food: Food?, position: Int) {
                deleteFoodFromCart(food, position)
            }

            override fun updateItemFood(food: Food?, position: Int) {
                FoodDatabase.getInstance(activity!!)?.foodDAO()?.updateFood(food)
                mCartAdapter?.notifyItemChanged(position)
                calculateTotalPrice()
            }
        })
        mFragmentCartBinding?.rcvFoodCart?.adapter = mCartAdapter
        calculateTotalPrice()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun clearCart() {
        if (mListFoodCart != null) {
            mListFoodCart!!.clear()
        }
        mCartAdapter?.notifyDataSetChanged()
        calculateTotalPrice()
    }

    private fun calculateTotalPrice() {
        if (activity == null) {
            return
        }
        val listFoodCart: MutableList<Food>? = FoodDatabase.getInstance(activity!!)?.foodDAO()?.getListFoodCart()
        if (listFoodCart == null || listFoodCart.isEmpty()) {
            val strZero = 0.toString() + Constant.CURRENCY
            mFragmentCartBinding?.tvTotalPrice?.text = strZero
            mAmount = 0
            return
        }
        var totalPrice = 0
        for (food in listFoodCart) {
            totalPrice += food.getTotalPrice()
        }
        val strTotalPrice = totalPrice.toString() + Constant.CURRENCY
        mFragmentCartBinding?.tvTotalPrice?.text = strTotalPrice
        mAmount = totalPrice
    }

    private fun deleteFoodFromCart(food: Food?, position: Int) {
        if (activity == null) {
            return
        }
        AlertDialog.Builder(activity)
                .setTitle(getString(R.string.confirm_delete_food))
                .setMessage(getString(R.string.message_delete_food))
                .setPositiveButton(getString(R.string.delete)) { _: DialogInterface?, _: Int ->
                    FoodDatabase.getInstance(activity!!)?.foodDAO()?.deleteFood(food)
                    mListFoodCart?.removeAt(position)
                    mCartAdapter?.notifyItemRemoved(position)
                    calculateTotalPrice()
                }
                .setNegativeButton(getString(R.string.dialog_cancel)) { dialog: DialogInterface?, _: Int -> dialog?.dismiss() }
                .show()
    }

    @SuppressLint("InflateParams")
    private fun onClickOrderCart() {
        if (activity == null) {
            return
        }
        if (mListFoodCart == null || mListFoodCart!!.isEmpty()) {
            return
        }
        val viewDialog = layoutInflater.inflate(R.layout.layout_bottom_sheet_order, null)
        val bottomSheetDialog = BottomSheetDialog(activity!!)
        bottomSheetDialog.setContentView(viewDialog)
        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED

        // init ui
        val tvFoodsOrder = viewDialog.findViewById<TextView?>(R.id.tv_foods_order)
        val tvPriceOrder = viewDialog.findViewById<TextView?>(R.id.tv_price_order)
        val edtNameOrder = viewDialog.findViewById<TextView?>(R.id.edt_name_order)
        val edtPhoneOrder = viewDialog.findViewById<TextView?>(R.id.edt_phone_order)
        val edtAddressOrder = viewDialog.findViewById<TextView?>(R.id.edt_address_order)
        val tvCancelOrder = viewDialog.findViewById<TextView?>(R.id.tv_cancel_order)
        val tvCreateOrder = viewDialog.findViewById<TextView?>(R.id.tv_create_order)

        // Set data
        tvFoodsOrder?.text = getStringListFoodsOrder()
        tvPriceOrder?.text = mFragmentCartBinding?.tvTotalPrice?.text.toString()

        // Set listener
        tvCancelOrder?.setOnClickListener { bottomSheetDialog.dismiss() }
        tvCreateOrder?.setOnClickListener {
            val strName = edtNameOrder?.text.toString().trim { it <= ' ' }
            val strPhone = edtPhoneOrder?.text.toString().trim { it <= ' ' }
            val strAddress = edtAddressOrder?.text.toString().trim { it <= ' ' }
            if (StringUtil.isEmpty(strName) || StringUtil.isEmpty(strPhone) || StringUtil.isEmpty(strAddress)) {
                GlobalFuntion.showToastMessage(activity, getString(R.string.message_enter_infor_order))
            } else {
                val id = System.currentTimeMillis()
                val order = Order(id, strName, strPhone, strAddress,
                        mAmount, getStringListFoodsOrder(), Constant.TYPE_PAYMENT_CASH)
                ControllerApplication[activity].getBookingDatabaseReference()
                        ?.child(Utils.getDeviceId(activity))
                        ?.child(id.toString())
                        ?.setValue(order) { _: DatabaseError?, _: DatabaseReference? ->
                            GlobalFuntion.showToastMessage(activity, getString(R.string.msg_order_success))
                            GlobalFuntion.hideSoftKeyboard(activity!!)
                            bottomSheetDialog.dismiss()
                            FoodDatabase.getInstance(activity!!)?.foodDAO()?.deleteAllFood()
                            clearCart()
                        }
            }
        }
        bottomSheetDialog.show()
    }

    private fun getStringListFoodsOrder(): String {
        if (mListFoodCart == null || mListFoodCart!!.isEmpty()) {
            return ""
        }
        var result = ""
        for (food in mListFoodCart!!) {
            result = if (StringUtil.isEmpty(result)) {
                ("- " + food.getName() + " (" + food.getRealPrice() + Constant.CURRENCY + ") "
                        + "- " + getString(R.string.quantity) + " " + food.getCount())
            } else {
                (result + "\n" + ("- " + food.getName() + " (" + food.getRealPrice() + Constant.CURRENCY + ") "
                        + "- " + getString(R.string.quantity) + " " + food.getCount()))

            }
        }
        return result
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ReloadListCartEvent?) {
        displayListFoodInCart()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }
}