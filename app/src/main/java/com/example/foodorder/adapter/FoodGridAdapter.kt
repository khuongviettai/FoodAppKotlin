package com.example.foodorder.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodorder.adapter.FoodGridAdapter.FoodGridViewHolder
import com.example.foodorder.constant.Constant
import com.example.foodorder.databinding.ItemFoodGridBinding
import com.example.foodorder.listener.IOnClickFoodItemListener
import com.example.foodorder.model.Food
import com.example.foodorder.utils.GlideUtils

class FoodGridAdapter(private val mListFoods: MutableList<Food>?,
                      private val iOnClickFoodItemListener: IOnClickFoodItemListener?) : RecyclerView.Adapter<FoodGridViewHolder?>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodGridViewHolder {
        val itemFoodGridBinding = ItemFoodGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodGridViewHolder(itemFoodGridBinding)
    }

    override fun onBindViewHolder(holder: FoodGridViewHolder, position: Int) {
        val food = mListFoods?.get(position) ?: return
        GlideUtils.loadUrl(food.getImage(), holder.mItemFoodGridBinding.imgFood)
        if (food.getSale() <= 0) {
            holder.mItemFoodGridBinding.tvSaleOff.visibility = View.GONE
            holder.mItemFoodGridBinding.tvPrice.visibility = View.GONE
            val strPrice = food.getPrice().toString() + Constant.CURRENCY
            holder.mItemFoodGridBinding.tvPriceSale.text = strPrice
        } else {
            holder.mItemFoodGridBinding.tvSaleOff.visibility = View.VISIBLE
            holder.mItemFoodGridBinding.tvPrice.visibility = View.VISIBLE
            val strSale = "Giảm " + food.getSale() + "%"
            holder.mItemFoodGridBinding.tvSaleOff.text = strSale
            val strOldPrice = food.getPrice().toString() + Constant.CURRENCY
            holder.mItemFoodGridBinding.tvPrice.text = strOldPrice
            holder.mItemFoodGridBinding.tvPrice.paintFlags = holder.mItemFoodGridBinding.tvPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            val strRealPrice = food.getRealPrice().toString() + Constant.CURRENCY
            holder.mItemFoodGridBinding.tvPriceSale.text = strRealPrice
        }
        holder.mItemFoodGridBinding.tvFoodName.text = food.getName()
        holder.mItemFoodGridBinding.layoutItem.setOnClickListener { iOnClickFoodItemListener?.onClickItemFood(food) }
    }

    override fun getItemCount(): Int {
        return mListFoods?.size ?: 0
    }

    class FoodGridViewHolder(val mItemFoodGridBinding: ItemFoodGridBinding) : RecyclerView.ViewHolder(mItemFoodGridBinding.root)
}