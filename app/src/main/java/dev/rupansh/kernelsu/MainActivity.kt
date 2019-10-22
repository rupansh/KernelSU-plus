package dev.rupansh.kernelsu

import android.app.Dialog
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.Shell.Config.*
import com.topjohnwu.superuser.Shell.FLAG_REDIRECT_STDERR
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    init {
        setFlags(FLAG_REDIRECT_STDERR)
        verboseLogging(BuildConfig.DEBUG)
        setTimeout(10)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

       if (!Shell.rootAccess()){
           MaterialDialog(this@MainActivity).show {
               icon(R.drawable.ic_error)
               title(text = "No Root Access")
               message(text = "Are you sure that your kernel supports KernelSU+?")
               negativeButton(text = "Quit") { finish(); moveTaskToBack(true) }
               onDismiss { finish(); moveTaskToBack(true) }
           }
       } else {
           val oldsize = SuOps.uidList.size
           val layoutManager = LinearLayoutManager(this)
           layoutManager.orientation = LinearLayoutManager.VERTICAL

           su_recycler.layoutManager = layoutManager
           val suViewAdapter = SuViewAdapter(this, SuOps.uidList)
           su_recycler.adapter = suViewAdapter

           val appsDialog = showApps()

           fab.setOnClickListener { appsDialog.show() }

           appsDialog.setOnDismissListener {
               if (oldsize < SuOps.uidList.size){
                   suViewAdapter.uidList = SuOps.uidList
                   suViewAdapter.notifyItemInserted(SuOps.uidList.size - 1)
               }
           }
       }
    }

    private fun showApps(): Dialog {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.apps_recycle)
        val layoutManager2 = LinearLayoutManager(this)
        layoutManager2.orientation = LinearLayoutManager.VERTICAL

        val appRecycler = dialog.findViewById<RecyclerView>(R.id.app_recycler)
        appRecycler.layoutManager = layoutManager2

        val cancel = dialog.findViewById<TextView>(R.id.cancelBtn)
        cancel.setOnClickListener {
            dialog.dismiss()
        }

        appRecycler.adapter = AppsViewAdapter(this, getApps(), dialog)

        return dialog
    }

    private fun getApps(): MutableList<ApplicationInfo>{
        val applicationList = mutableListOf<ApplicationInfo>()
        val pm = packageManager
        val apps = pm.getInstalledApplications(0)
        for (app in apps) {
            if (app.uid in SuOps.uidList)
                continue
            if (app.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP != 0) {
                applicationList += app
            } else if (app.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
                applicationList += app
            }
        }

        return applicationList
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when(item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
