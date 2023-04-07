package com.khuongviettai.foodorder

import android.app.Application
import android.content.Context
import com.khuongviettai.foodorder.constant.Constant
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ControllerApplication : Application() {

    private var mFirebaseDatabase: FirebaseDatabase? = null

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        mFirebaseDatabase = FirebaseDatabase.getInstance(Constant.FIREBASE_URL)
    }

    fun getFoodDatabaseReference(): DatabaseReference? {
        return mFirebaseDatabase?.getReference("/food")
    }

    fun getFeedbackDatabaseReference(): DatabaseReference? {
        return mFirebaseDatabase?.getReference("/feedback")
    }

    fun getBookingDatabaseReference(): DatabaseReference? {
        return mFirebaseDatabase?.getReference("/booking")
    }

    companion object {
        operator fun get(context: Context?): ControllerApplication {
            return context?.applicationContext as ControllerApplication
        }
    }
}