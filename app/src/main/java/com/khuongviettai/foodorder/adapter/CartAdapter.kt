package com.khuongviettai.foodorder.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.khuongviettai.foodorder.adapter.CartAdapter.CartViewHolder
import com.khuongviettai.foodorder.constant.Constant
import com.khuongviettai.foodorder.databinding.ItemCartBinding
import com.khuongviettai.foodorder.model.Food
import com.khuongviettai.foodorder.utils.GlideUtils

class CartAdapter(private val mListFoods: MutableList<Food>?,
                  private val iClickListener: IClickListener?) : RecyclerView.Adapter<CartViewHolder?>() {

    interface IClickListener {
        fun clickDeteteFood(food: Food?, position: Int)
        fun updateItemFood(food: Food?, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val itemCartBinding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(itemCartBinding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val food = mListFoods?.get(position) ?: return
        GlideUtils.loadUrl(food.getImage(), holder.mItemCartBinding.imgFoodCart)
        holder.mItemCartBinding.tvFoodNameCart.text = food.getName()
        var strFoodPriceCart = food.getPrice().toString() + Constant.CURRENCY
        if (food.getSale() > 0) {
            strFoodPriceCart = food.getRealPrice().toString() + Constant.CURRENCY
        }
        holder.mItemCartBinding.tvFoodPriceCart.text = strFoodPriceCart
        holder.mItemCartBinding.tvCount.text = food.getCount().toString()

        holder.mItemCartBinding.tvSubtract.setOnClickListener {
            val strCount = holder.mItemCartBinding.tvCount.text.toString()
            val count = strCount.toInt()
            if (count <= 1) {
                return@setOnClickListener
            }
            val newCount = count - 1
            holder.mItemCartBinding.tvCount.text = newCount.toString()
            val totalPrice = food.getRealPrice() * newCount
            food.setCount(newCount)
            food.setTotalPrice(totalPrice)
            iClickListener?.updateItemFood(food, holder.adapterPosition)
        }

        holder.mItemCartBinding.tvAdd.setOnClickListener {
            val newCount = holder.mItemCartBinding.tvCount.text.toString().toInt() + 1
            holder.mItemCartBinding.tvCount.text = newCount.toString()
            val totalPrice = food.getRealPrice() * newCount
            food.setCount(newCount)
            food.setTotalPrice(totalPrice)
            iClickListener?.updateItemFood(food, holder.adapterPosition)
        }

        holder.mItemCartBinding.tvDelete.setOnClickListener {
        iClickListener?.clickDeteteFood(food, holder.adapterPosition) }
    }

    override fun getItemCount(): Int {
        return mListFoods?.size ?: 0
    }

    class CartViewHolder(val mItemCartBinding: ItemCartBinding) : RecyclerView.ViewHolder(mItemCartBinding.root)
}