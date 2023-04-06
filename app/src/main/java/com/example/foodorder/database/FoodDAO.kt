package com.example.foodorder.database

import androidx.room.*
import com.example.foodorder.model.Food

@Dao
interface FoodDAO {
    @Insert
    fun insertFood(food: Food?)

    @Query("SELECT * FROM food")
    fun getListFoodCart(): MutableList<Food>?

    @Query("SELECT * FROM food WHERE id=:id")
    fun checkFoodInCart(id: Int): MutableList<Food>?

    @Delete
    fun deleteFood(food: Food?)

    @Update
    fun updateFood(food: Food?)

    @Query("DELETE from food")
    fun deleteAllFood()
}