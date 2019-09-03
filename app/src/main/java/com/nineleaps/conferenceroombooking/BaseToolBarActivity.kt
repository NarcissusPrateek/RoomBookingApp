package com.nineleaps.conferenceroombooking

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.widget.Toolbar
import butterknife.BindView


@Suppress("DEPRECATED_IDENTITY_EQUALS")
abstract class BaseToolBarActivity : BaseActivity() {
    protected val RESOURCE_NO_MENU: Int = 0

    @BindView(R.id.toolbar)
    lateinit var mToolbar:Toolbar

    public fun getmToolBar():Toolbar{
        return mToolbar
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolBar()
    }

    private fun setToolBar() {
        setSupportActionBar(mToolbar)
    }

    protected abstract fun getMenuResource():Int

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (getMenuResource() === RESOURCE_NO_MENU)
            return super.onCreateOptionsMenu(menu)
        else {
            menuInflater.inflate(getMenuResource(), menu)
            return true
        }
    }
}