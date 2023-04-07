package com.khuongviettai.foodorder.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "food")
class Food : Serializable {
    @PrimaryKey
    private var id = 0
    private var name: String? = null
    private var image: String? = null
    private var banner: String? = null
    private var description: String? = null
    private var price = 0
    private var sale = 0
    private var count = 0
    private var totalPrice = 0
    private var popular = false
    @Ignore
    private var images: MutableList<Image>? = null

    fun getId(): Int {
        return id
    }

    fun setId(id: Int) {
        this.id = id
    }

    fun getName(): String? {
        return name
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun getPrice(): Int {
        return price
    }

    fun setPrice(price: Int) {
        this.price = price
    }

    fun getRealPrice(): Int {
        return if (sale <= 0) {
            price
        } else price - price * sale / 100
    }

    fun getImage(): String? {
        return image
    }

    fun setImage(image: String?) {
        this.image = image
    }

    fun getBanner(): String? {
        return banner
    }

    fun setBanner(banner: String?) {
        this.banner = banner
    }

    fun getDescription(): String? {
        return description
    }

    fun setDescription(description: String?) {
        this.description = description
    }

    fun getSale(): Int {
        return sale
    }

    fun setSale(sale: Int) {
        this.sale = sale
    }

    fun getCount(): Int {
        return count
    }

    fun setCount(count: Int) {
        this.count = count
    }

    fun getTotalPrice(): Int {
        return totalPrice
    }

    fun setTotalPrice(totalPrice: Int) {
        this.totalPrice = totalPrice
    }

    fun isPopular(): Boolean {
        return popular
    }

    fun setPopular(popular: Boolean) {
        this.popular = popular
    }

    fun getImages(): MutableList<Image>? {
        return images
    }

    fun setImages(images: MutableList<Image>?) {
        this.images = images
    }
}