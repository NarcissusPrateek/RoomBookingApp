package com.nineleaps.conferenceroombooking

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import butterknife.ButterKnife
import com.google.android.material.textfield.TextInputLayout
import com.nineleaps.conferenceroombooking.Helper.NetworkState
import com.nineleaps.conferenceroombooking.checkConnection.NoInternetConnectionActivity
import com.orhanobut.hawk.Hawk


abstract class BaseActivity : AppCompatActivity() {

    lateinit var progressBarDialog: AlertDialog

    lateinit var dialogBuilder: AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResource())
        ButterKnife.bind(this)
        Hawk.init(applicationContext).build()
        if (!NetworkState.appIsConnectedToInternet(applicationContext)) {
            startActivity(Intent(applicationContext, NoInternetConnectionActivity::class.java))
        }
    }


    protected abstract fun getLayoutResource(): Int

    fun initActionBar(title: String) {
        val actionbar = supportActionBar
        actionbar!!.title =
            HtmlCompat.fromHtml("<font color=\"#FFFFFF\">$title</font>", HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    fun validateEditText(textOfEditext: String, textInputLayout: TextInputLayout): Boolean {
        return if (textOfEditext.isEmpty()) {
            textInputLayout.error = getString(R.string.field_cant_be_empty)
            false
        } else {
            textInputLayout.error = null
            true
        }
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    fun showProgressDialog(context: Context){
        dialogBuilder = AlertDialog.Builder(context)
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogView = inflater.inflate(R.layout.layout_progress_dialog,null)
        val progressTextView:TextView = dialogView.findViewById(R.id.progress_textView)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setCancelable(false)
        progressBarDialog = dialogBuilder.create()
        progressBarDialog.show()

    }

    fun hideProgressDialog(){
        progressBarDialog.dismiss()
    }

    fun disableClickListenerForProgressBar(){
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    fun enableClickListenerForProgressBar(){
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }
}