package com.example.foodorder.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.foodorder.ControllerApplication
import com.example.foodorder.R
import com.example.foodorder.activity.MainActivity
import com.example.foodorder.constant.GlobalFuntion
import com.example.foodorder.databinding.FragmentFeedbackBinding
import com.example.foodorder.model.Feedback
import com.example.foodorder.utils.StringUtil
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference

class FeedbackFragment : BaseFragment() {

    private var mFragmentFeedbackBinding: FragmentFeedbackBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mFragmentFeedbackBinding = FragmentFeedbackBinding.inflate(inflater, container, false)
        mFragmentFeedbackBinding?.tvSendFeedback?.setOnClickListener { onClickSendFeedback() }
        return mFragmentFeedbackBinding?.root
    }

    private fun onClickSendFeedback() {
        if (activity == null) {
            return
        }
        val activity = activity as MainActivity?
        val strName = mFragmentFeedbackBinding?.edtName?.text.toString()
        val strPhone = mFragmentFeedbackBinding?.edtPhone?.text.toString()
        val strEmail = mFragmentFeedbackBinding?.edtEmail?.text.toString()
        val strComment = mFragmentFeedbackBinding?.edtComment?.text.toString()
        if (StringUtil.isEmpty(strName)) {
            GlobalFuntion.showToastMessage(activity, getString(R.string.name_require))
        } else if (StringUtil.isEmpty(strComment)) {
            GlobalFuntion.showToastMessage(activity, getString(R.string.comment_require))
        } else {
            activity?.showProgressDialog(true)
            val feedback = Feedback(strName, strPhone, strEmail, strComment)
            ControllerApplication[getActivity()].getFeedbackDatabaseReference()
                    ?.child(System.currentTimeMillis().toString())
                    ?.setValue(feedback) { _: DatabaseError?, _: DatabaseReference? ->
                        activity?.showProgressDialog(false)
                        sendFeedbackSuccess()
                    }
        }
    }

    private fun sendFeedbackSuccess() {
        if (activity == null) {
            return
        }
        GlobalFuntion.hideSoftKeyboard(activity!!)
        GlobalFuntion.showToastMessage(activity, getString(R.string.send_feedback_success))
        mFragmentFeedbackBinding?.edtName?.setText("")
        mFragmentFeedbackBinding?.edtPhone?.setText("")
        mFragmentFeedbackBinding?.edtEmail?.setText("")
        mFragmentFeedbackBinding?.edtComment?.setText("")
    }

    override fun initToolbar() {
        if (activity != null) {
            (activity as MainActivity?)?.setToolBar(false, getString(R.string.feedback))
        }
    }
}