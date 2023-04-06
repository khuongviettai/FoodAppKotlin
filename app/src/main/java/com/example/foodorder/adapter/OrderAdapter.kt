package com.example.foodorder.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodorder.adapter.OrderAdapter.OrderViewHolder
import com.example.foodorder.constant.Constant
import com.example.foodorder.databinding.ItemOrderBinding
import com.example.foodorder.model.Order
import com.example.foodorder.utils.DateTimeUtils

class OrderAdapter(private val mListOrder: MutableList<Order>?) : RecyclerView.Adapter<OrderViewHolder?>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val itemOrderBinding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context),
                parent, false)
        return OrderViewHolder(itemOrderBinding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = mListOrder?.get(position) ?: return
        holder.mItemOrderBinding.tvId.text = order.getId().toString()
        holder.mItemOrderBinding.tvName.text = order.getName()
        holder.mItemOrderBinding.tvPhone.text = order.getPhone()
        holder.mItemOrderBinding.tvAddress.text = order.getAddress()
        holder.mItemOrderBinding.tvMenu.text = order.getFoods()
        holder.mItemOrderBinding.tvDate.text = DateTimeUtils.convertTimeStampToDate(order.getId())
        val strAmount = order.getAmount().toString() + Constant.CURRENCY
        holder.mItemOrderBinding.tvTotalAmount.text = strAmount
        var paymentMethod = ""
        if (Constant.TYPE_PAYMENT_CASH == order.getPayment()) {
            paymentMethod = Constant.PAYMENT_METHOD_CASH
        }
        holder.mItemOrderBinding.tvPayment.text = paymentMethod
    }

    override fun getItemCount(): Int {
        return mListOrder?.size ?: 0
    }

    class OrderViewHolder(val mItemOrderBinding: ItemOrderBinding) : RecyclerView.ViewHolder(mItemOrderBinding.root)
}