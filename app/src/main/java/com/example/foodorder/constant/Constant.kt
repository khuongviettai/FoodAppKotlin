package com.example.foodorder.constant

interface Constant {
    companion object {
        const val GENERIC_ERROR: String = "General error, please try again later"

        const val LINK_FACEBOOK: String = "https://www.facebook.com/khuongviettai"

        const val PHONE_NUMBER: String = "+84945034118"
        const val GMAIL: String = "khuongtai879@gmail.com"

        const val ZALO_LINK: String = "https://zalo.me/0945034118"
        const val FIREBASE_URL: String = "https://foododer-7e2c6-default-rtdb.firebaseio.com"
        const val CURRENCY: String = " 000 VNĐ"
        const val TYPE_PAYMENT_CASH = 1
        const val PAYMENT_METHOD_CASH: String = "Tiền mặt"

        // Key Intent
        const val KEY_INTENT_FOOD_OBJECT: String = "food_object"
    }
}