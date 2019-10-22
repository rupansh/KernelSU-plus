package dev.rupansh.kernelsu

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import dev.rupansh.kernelsu.SuOps.delUid
import kotlinx.android.synthetic.main.su_apps_card.view.*


class SuViewAdapter(val context: Context, var uidList: MutableList<Int>): RecyclerView.Adapter<SuViewAdapter.SuViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.su_apps_card, parent, false)
        return SuViewHolder(view)
    }

    override fun getItemCount(): Int {
        return uidList.size
    }

    override fun onBindViewHolder(holder: SuViewHolder, position: Int) {
        val suApp = uidList[position]
        holder.setData(suApp, position)
    }

    inner class SuViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun setData(UID: Int, pos: Int){
            lateinit var appIcon: Drawable
            var appName = context.packageManager.getNameForUid(UID)
            try {
                appName = appName!!.split(":")[0]
                val appIconc: Drawable = context.packageManager.getApplicationIcon(appName)
                val appIconb = getBitmapFromDrawable(appIconc)
                appIcon = BitmapDrawable(context.resources, Bitmap.createScaledBitmap(appIconb, 128, 128, true))
            } catch(e: Exception) {
                appName = "custom uid"
                appIcon = context.resources.getDrawable(R.drawable.ic_gear, null)
            }
            itemView.maintext.text = appName
            itemView.maintext.setCompoundDrawablesRelativeWithIntrinsicBounds(appIcon,null, null, null)
            val subText = "UID: $UID"
            itemView.subtext.text = subText

            itemView.setOnLongClickListener {
                itemView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                itemView.rmbut.visibility = if (itemView.rmbut.visibility == GONE){
                    VISIBLE
                } else {
                    GONE
                }
                true
            }

            itemView.rmbut.setOnClickListener {
                MaterialDialog(context).show {
                    icon(R.drawable.ic_error)
                    title(text = "Confirm")
                    message(text = "Are you sure that you want remove this from allowed apps?")
                    negativeButton(text = "Cancel") {  }
                    positiveButton(text = "Yes") {
                        itemView.rmbut.visibility = GONE
                        delUid(UID)
                        uidList.removeAt(pos)
                        notifyItemRemoved(pos)
                    }
                    onDismiss { }
                }
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