package com.example.foodorder.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodorder.ControllerApplication
import com.example.foodorder.R
import com.example.foodorder.activity.MainActivity
import com.example.foodorder.adapter.OrderAdapter
import com.example.foodorder.databinding.FragmentOrderBinding
import com.example.foodorder.model.Order
import com.example.foodorder.utils.Utils
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import java.util.*

class OrderFragment : BaseFragment() {

    private var mFragmentOrderBinding: FragmentOrderBinding? = null
    private var mListOrder: MutableList<Order>? = null
    private var mOrderAdapter: OrderAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mFragmentOrderBinding = FragmentOrderBinding.inflate(inflater, container, false)
        initView()
        getListOrders()
        return mFragmentOrderBinding?.root
    }

    override fun initToolbar() {
        if (activity != null) {
            (activity as MainActivity?)?.setToolBar(false, getString(R.string.order))
        }
    }

    private fun initView() {
        if (activity == null) {
            return
        }
        val linearLayoutManager = LinearLayoutManager(activity)
        mFragmentOrderBinding?.rcvOrder?.layoutManager = linearLayoutManager
        mListOrder = ArrayList()
        mOrderAdapter = OrderAdapter(mListOrder)
        mFragmentOrderBinding?.rcvOrder?.adapter = mOrderAdapter
    }

    private fun getListOrders() {
        if (activity == null) {
            return
        }
        ControllerApplication[activity].getBookingDatabaseReference()
                ?.child(Utils.getDeviceId(activity))
                ?.addChildEventListener(object : ChildEventListener {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                        val order: Order? = dataSnapshot.getValue<Order>(Order::class.java)
                        if (order == null || mListOrder == null || mOrderAdapter == null) {
                            return
                        }
                        mListOrder?.add(0, order)
                        mOrderAdapter?.notifyDataSetChanged()
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                        val order: Order? = dataSnapshot.getValue<Order>(Order::class.java)
                        if (order == null || mListOrder == null || mListOrder!!.isEmpty() || mOrderAdapter == null) {
                            return
                        }
                        for (i in mListOrder!!.indices) {
                            if (order.getId() == mListOrder!![i].getId()) {
                                mListOrder!![i] = order
                                break
                            }
                        }
                        mOrderAdapter?.notifyDataSetChanged()
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                        val order: Order? = dataSnapshot.getValue<Order>(Order::class.java)
                        if (order == null || mListOrder == null || mListOrder!!.isEmpty() || mOrderAdapter == null) {
                            return
                        }
                        for (orderObject in mListOrder!!) {
                            if (order.getId() == orderObject.getId()) {
                                mListOrder!!.remove(orderObject)
                                break
                            }
                        }
                        mOrderAdapter?.notifyDataSetChanged()
                    }

                    override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
                    override fun onCancelled(databaseError: DatabaseError) {}
                })
    }
}