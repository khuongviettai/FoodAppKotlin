package com.khuongviettai.foodorder.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.khuongviettai.foodorder.R
import com.khuongviettai.foodorder.activity.MainActivity
import com.khuongviettai.foodorder.adapter.ContactAdapter
import com.khuongviettai.foodorder.constant.GlobalFuntion
import com.khuongviettai.foodorder.databinding.FragmentContactBinding
import com.khuongviettai.foodorder.model.Contact
import java.util.*

class ContactFragment : BaseFragment() {

    private var mContactAdapter: ContactAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val mFragmentContactBinding = FragmentContactBinding.inflate(inflater, container, false)

        mContactAdapter = ContactAdapter(activity, getListContact(), object : ContactAdapter.ICallPhone {
            override fun onClickCallPhone() {
                GlobalFuntion.callPhoneNumber(activity!!)
            }

        })
        val layoutManager = GridLayoutManager(activity, 3)
        mFragmentContactBinding.rcvData.isNestedScrollingEnabled = false
        mFragmentContactBinding.rcvData.isFocusable = false
        mFragmentContactBinding.rcvData.layoutManager = layoutManager
        mFragmentContactBinding.rcvData.adapter = mContactAdapter
        return mFragmentContactBinding.root
    }

    private fun getListContact(): MutableList<Contact> {
        val contactArrayList: MutableList<Contact> = ArrayList()
        contactArrayList.add(Contact(Contact.FACEBOOK, R.drawable.ic_facebook))
        contactArrayList.add(Contact(Contact.HOTLINE, R.drawable.ic_hotline))
        contactArrayList.add(Contact(Contact.GMAIL, R.drawable.ic_gmail))

        contactArrayList.add(Contact(Contact.ZALO, R.drawable.ic_zalo))
        return contactArrayList
    }

    override fun onDestroy() {
        super.onDestroy()
        mContactAdapter?.release()
    }

    override fun initToolbar() {
        if (activity != null) {
            (activity as MainActivity?)?.setToolBar(false, getString(R.string.contact))
        }
    }
}