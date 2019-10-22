package dev.rupansh.kernelsu

import android.app.Dialog
import android.content.Context
import android.content.pm.ApplicationInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.apps_list_item.view.*

class AppsViewAdapter(val context: Context, val appList: MutableList<ApplicationInfo>, val dialog: Dialog): RecyclerView.Adapter<AppsViewAdapter.AppsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.apps_list_item, parent, false)
        return AppsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return appList.size
    }

    override fun onBindViewHolder(holder: AppsViewHolder, position: Int) {
        val appToAdd = appList[position]
        holder.setData(appToAdd, position)
    }

    inner class AppsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setData(appInfo: ApplicationInfo, pos: Int) {
            val appIcon = try {
                val appIconc: Drawable = context.packageManager.getApplicationIcon(appInfo)
                val appIconb = getBitmapFromDrawable(appIconc)
                BitmapDrawable(
                    context.resources,
                    Bitmap.createScaledBitmap(appIconb, 100, 100, true)
                )
            } catch (e: Exception) {
                context.resources.getDrawable(R.drawable.ic_gear, null)
            }
            itemView.app_name.text = context.packageManager.getApplicationLabel(appInfo)
            itemView.app_name.setCompoundDrawablesRelativeWithIntrinsicBounds(
                appIcon,
                null,
                null,
                null
            )

            itemView.app_name.setOnClickListener {
                SuOps.addUid(appInfo.uid)
                SuOps.uidList = SuOps.getApps()
                appList.removeAt(pos)
                notifyItemRemoved(pos)
                dialog.dismiss()
                itemView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            }
        }
    }

    private fun getBitmapFromDrawable(drawable: Drawable): Bitmap {
        val bmp = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bmp)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bmp
    }
}