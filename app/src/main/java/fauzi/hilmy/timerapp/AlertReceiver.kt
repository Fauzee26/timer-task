package fauzi.hilmy.timerapp

import android.app.Dialog
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Vibrator
import android.support.annotation.RequiresApi
import android.R.attr.bitmap
import android.view.Window
import android.view.WindowManager
import android.view.Window.FEATURE_NO_TITLE
import android.R.attr.bitmap
import android.widget.Button
import android.widget.Toast


class AlertReceiver : BroadcastReceiver() {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent!!.action == "fauzi.hilmy.timerapp.TRIGGER_DIALOG"){
//            val componentName = ComponentName(context, AlertService::class.java)
//            val builder = JobInfo.Builder(10, componentName)
//                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
//                .setRequiresDeviceIdle(false)
//                .setRequiresCharging(false)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                builder.setMinimumLatency(1000)
//            } else {
//                builder.setPeriodic(1000)
//            }
//            val scheduler = context!!.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
//            scheduler.schedule(builder.build())

            val dialog = Dialog(context!!, android.R.style.Theme_Black_NoTitleBar_Fullscreen)

            dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
            dialog.setContentView(R.layout.dialog_view)

            val button = dialog.findViewById(R.id.btnn) as Button
            button.setOnClickListener {
                val intentt = Intent(context, MainActivity::class.java)
                intentt.flags = Intent.FLAG_ACTIVITY_NEW_TASK

                context.startActivity(intentt)

                dialog.dismiss()
            }

            dialog.show()

            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(3000)
        }
    }
}