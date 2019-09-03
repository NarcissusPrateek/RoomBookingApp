package com.nineleaps.conferenceroombooking

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import butterknife.ButterKnife

abstract class BaseFragment : Fragment() {

    lateinit var mBaseActivity: BaseActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(getLayoutResources(), container, false)
        ButterKnife.bind(this, view)
        return view
    }

    abstract fun getLayoutResources(): Int

    protected fun setTiltle(title: String) {
        mBaseActivity.initActionBar(title)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mBaseActivity = context as BaseActivity
    }

    fun showProgressDialogInFragment(context: Context) {
        mBaseActivity.showProgressDialog(context)
    }

    fun hideProgressDialogInFragment() {
        mBaseActivity.hideProgressDialog()
    }
}