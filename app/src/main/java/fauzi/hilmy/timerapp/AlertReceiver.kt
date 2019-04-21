package fauzi.hilmy.timerapp

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Vibrator
import android.support.annotation.RequiresApi


class AlertReceiver : BroadcastReceiver() {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onReceive(context: Context?, intent: Intent?) {
        val componentName = ComponentName(context, AlertService::class.java)
        val builder = JobInfo.Builder(10, componentName)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .setRequiresDeviceIdle(false)
            .setRequiresCharging(false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setMinimumLatency(1000)
        } else {
            builder.setPeriodic(1000)
        }
        val scheduler = context!!.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        scheduler.schedule(builder.build())

        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(2000)
    }
}