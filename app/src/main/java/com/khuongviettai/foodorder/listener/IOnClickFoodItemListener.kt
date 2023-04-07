package com.khuongviettai.foodorder.listener

import com.khuongviettai.foodorder.model.Food

interface IOnClickFoodItemListener {
    fun onClickItemFood(food: Food)
}