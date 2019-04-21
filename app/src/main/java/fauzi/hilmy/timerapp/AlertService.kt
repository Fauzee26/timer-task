package fauzi.hilmy.timerapp

import android.app.AlertDialog
import android.app.job.JobParameters
import android.app.job.JobService
import android.os.Build
import android.support.annotation.RequiresApi
import android.view.WindowManager

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
class AlertService : JobService() {

    override fun onStopJob(params: JobParameters?): Boolean {
        return true
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        getRelease(params)
        return true
    }

    private fun getRelease(params: JobParameters?) {
        val builder = AlertDialog.Builder(applicationContext)
        builder.setTitle("Caution")
        builder.setIcon(R.drawable.ic_launcher_foreground)
        builder.setMessage("Time's Up")
        builder.setPositiveButton("OK") { dialog, whichButton ->
            //Do something
            dialog.dismiss()
        }

        builder.setNegativeButton(
            "Close"
        ) { dialog, whichButton -> dialog.dismiss() }
        val alert = builder.create()
        alert.window!!.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
        alert.show()

    }
}