package com.khuongviettai.foodorder.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.khuongviettai.foodorder.adapter.FoodPopularAdapter.FoodPopularViewHolder
import com.khuongviettai.foodorder.databinding.ItemFoodPopularBinding
import com.khuongviettai.foodorder.listener.IOnClickFoodItemListener
import com.khuongviettai.foodorder.model.Food
import com.khuongviettai.foodorder.utils.GlideUtils

class FoodPopularAdapter(private val mListFoods: MutableList<Food>?,
                         private val iOnClickFoodItemListener: IOnClickFoodItemListener?) : RecyclerView.Adapter<FoodPopularViewHolder?>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodPopularViewHolder {
        val itemFoodPopularBinding = ItemFoodPopularBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodPopularViewHolder(itemFoodPopularBinding)
    }

    override fun onBindViewHolder(holder: FoodPopularViewHolder, position: Int) {
        val food = mListFoods?.get(position) ?: return
        GlideUtils.loadUrlBanner(food.getBanner(), holder.mItemFoodPopularBinding.imageFood)
        if (food.getSale() <= 0) {
            holder.mItemFoodPopularBinding.tvSaleOff.visibility = View.GONE
        } else {
            holder.mItemFoodPopularBinding.tvSaleOff.visibility = View.VISIBLE
            val strSale = "Giảm " + food.getSale() + "%"
            holder.mItemFoodPopularBinding.tvSaleOff.text = strSale
        }
        holder.mItemFoodPopularBinding.layoutItem.setOnClickListener {
        iOnClickFoodItemListener?.onClickItemFood(food) }
    }

    override fun getItemCount(): Int {
        return mListFoods?.size ?: 0
    }

    class FoodPopularViewHolder(val mItemFoodPopularBinding: ItemFoodPopularBinding) : RecyclerView.ViewHolder(mItemFoodPopularBinding.root)
}