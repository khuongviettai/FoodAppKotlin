package com.khuongviettai.foodorder.model

import java.io.Serializable

class Order : Serializable {
    private var id: Long = 0
    private var name: String? = null
    private var phone: String? = null
    private var address: String? = null
    private var amount = 0
    private var foods: String? = null
    private var payment = 0

    constructor() {}

    constructor(id: Long, name: String?, phone: String?, address: String?, amount: Int, foods: String?, payment: Int) {
        this.id = id
        this.name = name
        this.phone = phone
        this.address = address
        this.amount = amount
        this.foods = foods
        this.payment = payment
    }

    fun getId(): Long {
        return id
    }

    fun setId(id: Long) {
        this.id = id
    }

    fun getName(): String? {
        return name
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun getPhone(): String? {
        return phone
    }

    fun setPhone(phone: String?) {
        this.phone = phone
    }

    fun getAddress(): String? {
        return address
    }

    fun setAddress(address: String?) {
        this.address = address
    }

    fun getAmount(): Int {
        return amount
    }

    fun setAmount(amount: Int) {
        this.amount = amount
    }

    fun getFoods(): String? {
        return foods
    }

    fun setFoods(foods: String?) {
        this.foods = foods
    }

    fun getPayment(): Int {
        return payment
    }

    fun setPayment(payment: Int) {
        this.payment = payment
    }
}