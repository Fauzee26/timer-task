package fauzi.hilmy.timerapp

import android.annotation.TargetApi
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private var mCountDownTimer: CountDownTimer? = null

    private var mTimerRunning: Boolean = false
    private var isFinish: Boolean = false

    private var mStartTimeInMillis: Long = 0
    private var mTimeLeftInMillis: Long = 0
    private var mEndTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button_set.setOnClickListener(View.OnClickListener {
            val input = edit_text_input.text.toString()
            if (input.isEmpty()) {
                Toast.makeText(this@MainActivity, "Field can't be empty", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }

            val millisInput = java.lang.Long.parseLong(input) * 60000
            if (millisInput == 0L) {
                Toast.makeText(this@MainActivity, "Please enter a positive number", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }

            setTime(millisInput)
            edit_text_input.setText("")
        })

        button_start_pause.setOnClickListener {
            if (mTimerRunning) {
                pauseTimer()
            } else {
                startTimer()
            }
        }

        button_reset.setOnClickListener { resetTimer() }
//        setRelease()
    }

    private fun setRelease() {
        if (isFinish == true) {
            val intent = Intent(this@MainActivity, AlertReceiver::class.java)
            val pendingIntent =
                PendingIntent.getBroadcast(this@MainActivity, 0, intent, 0)
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent)

            isFinish = false
        }
    }

    private fun setTime(milliseconds: Long) {
        mStartTimeInMillis = milliseconds
        resetTimer()
        closeKeyboard()
    }

    private fun startTimer() {
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis

        val timeLeft = mTimeLeftInMillis.toInt()
        val intent = Intent(this@MainActivity, AlertReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(this@MainActivity, 0, intent, 0)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeLeft, pendingIntent)

        mCountDownTimer = object : CountDownTimer(mTimeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                mTimeLeftInMillis = millisUntilFinished
                updateCountDownText()
            }

            override fun onFinish() {
                mTimerRunning = false
                updateWatchInterface()

                isFinish = true
//                setRelease()

//                val intent = Intent(this@MainActivity, AlertReceiver::class.java)
//                val pendingIntent =
//                        PendingIntent.getBroadcast(this@MainActivity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
//                val calendar: Calendar = Calendar.getInstance()
//
//                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent)

//                val intent = Intent(this@MainActivity, AlertActivity::class.java)
//                startActivity(intent)

            }
        }.start()

        mTimerRunning = true
        updateWatchInterface()
    }

    private fun pauseTimer() {
        mCountDownTimer!!.cancel()
        mTimerRunning = false
        updateWatchInterface()
    }

    private fun resetTimer() {
        mTimeLeftInMillis = mStartTimeInMillis
        updateCountDownText()
        updateWatchInterface()
    }

    private fun updateCountDownText() {
        val hours = (mTimeLeftInMillis / 1000).toInt() / 3600
        val minutes = (mTimeLeftInMillis / 1000 % 3600).toInt() / 60
        val seconds = (mTimeLeftInMillis / 1000).toInt() % 60

        val timeLeftFormatted: String
        if (hours > 0) {
            timeLeftFormatted = String.format(
                Locale.getDefault(),
                "%d:%02d:%02d", hours, minutes, seconds
            )
        } else {
            timeLeftFormatted = String.format(
                Locale.getDefault(),
                "%02d:%02d", minutes, seconds
            )
        }

        text_view_countdown.text = timeLeftFormatted
    }

    private fun updateWatchInterface() {
        if (mTimerRunning) {
            edit_text_input.visibility = View.INVISIBLE
            button_reset.visibility = View.INVISIBLE
            button_reset.visibility = View.INVISIBLE
            button_start_pause.text = "Pause"
        } else {
            edit_text_input.visibility = View.VISIBLE
            button_reset.visibility = View.VISIBLE
            button_start_pause.text = "Start"

            if (mTimeLeftInMillis < 1000) {
                button_start_pause.visibility = View.INVISIBLE
            } else {
                button_start_pause.visibility = View.VISIBLE
            }

            if (mTimeLeftInMillis < mStartTimeInMillis) {
                button_reset.visibility = View.VISIBLE
            } else {
                button_reset.visibility = View.INVISIBLE
            }
        }
    }

    private fun closeKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onStop() {
        super.onStop()

        val prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()

        editor.putLong("startTimeInMillis", mStartTimeInMillis)
        editor.putLong("millisLeft", mTimeLeftInMillis)
        editor.putBoolean("timerRunning", mTimerRunning)
        editor.putLong("endTime", mEndTime)

        editor.apply()

        if (mCountDownTimer != null) {
            mCountDownTimer!!.cancel()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            openOverlaySettings()
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun openOverlaySettings() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        try {
            startActivityForResult(intent, 12)
        } catch (e: ActivityNotFoundException) {
//            Log.e(FragmentActivity.TAG, e.message)
        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            12 -> {
                val overlayEnabled = Settings.canDrawOverlays(this)

            }
        }
    }

    override fun onStart() {
        super.onStart()

        val prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)

        mStartTimeInMillis = prefs.getLong("startTimeInMillis", 600000)
        mTimeLeftInMillis = prefs.getLong("millisLeft", mStartTimeInMillis)
        mTimerRunning = prefs.getBoolean("timerRunning", false)

        updateCountDownText()
        updateWatchInterface()

        if (mTimerRunning) {
            mEndTime = prefs.getLong("endTime", 0)
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis()

            if (mTimeLeftInMillis < 0) {
                mTimeLeftInMillis = 0
                mTimerRunning = false
                updateCountDownText()
                updateWatchInterface()
            } else {
                startTimer()
            }
        }
    }
}