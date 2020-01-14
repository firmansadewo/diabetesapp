/**************************************************************************
 *
 * Copyright (C) 2012-2015 Alex Taradov <alex@taradov.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *************************************************************************/

package com.example.diabetes.Alarm.Olahraga;

import android.app.Activity;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.diabetes.Alarm.AlarmFragment;
import com.example.diabetes.R;

import java.util.Timer;
import java.util.TimerTask;

public class AlarmNotificationOl extends Activity
{
  private final String TAG = "AlarmFragment";

  private Ringtone mRingtone;
  private Vibrator mVibrator;
  private final long[] mVibratePattern = { 0, 500, 500 };
  private boolean mVibrate;
  private Uri mAlarmSound;
  private long mPlayTime;
  private Timer mTimer = null;
  private AlarmOl mAlarm;
  private DateTimeol mDateTime;
  private TextView mTextView;
  private PlayTimerTask mTimerTask;

  @Override
  protected void onCreate(Bundle bundle)
  {
    super.onCreate(bundle);

    getWindow().addFlags(
      WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
      WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
      WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

    setContentView(R.layout.notificationolahraga);

    mDateTime = new DateTimeol(this);
    mTextView = (TextView)findViewById(R.id.alarm_title_text);

    readPreferences();

    mRingtone = RingtoneManager.getRingtone(getApplicationContext(), mAlarmSound);
    if (mVibrate)
      mVibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

    start(getIntent());
  }

  @Override
  protected void onDestroy()
  {
    super.onDestroy();
    Log.i(TAG, "AlarmNotificationOl.onDestroy()");

    stop();
  }

  @RequiresApi(api = Build.VERSION_CODES.O)
  @Override
  protected void onNewIntent(Intent intent)
  {
    super.onNewIntent(intent);
    Log.i(TAG, "AlarmNotificationOl.onNewIntent()");

    addNotification(mAlarm);

    stop();
    start(intent);
  }

  private void start(Intent intent)
  {
    mAlarm = new AlarmOl(this);
    mAlarm.fromIntent(intent);

    Log.i(TAG, "AlarmNotificationOl.start('" + mAlarm.getTitle() + "')");

    mTextView.setText(mAlarm.getTitle());

    mTimerTask = new PlayTimerTask();
    mTimer = new Timer();
    mTimer.schedule(mTimerTask, mPlayTime);
    mRingtone.play();
    if (mVibrate)
      mVibrator.vibrate(mVibratePattern, 0);
  }

  private void stop()
  {
    Log.i(TAG, "AlarmNotificationOl.stop()");

    mTimer.cancel();
    mRingtone.stop();
    if (mVibrate)
      mVibrator.cancel();
  }

  public void onDismissClick(View view)
  {
    finish();
  }

  private void readPreferences()
  {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

    mAlarmSound = Uri.parse(prefs.getString("alarm_sound_pref", "DEFAULT_RINGTONE_URI"));
    mVibrate = prefs.getBoolean("vibrate_pref", true);
    mPlayTime = (long)Integer.parseInt(prefs.getString("alarm_play_time_pref", "30")) * 1000;
  }

  @RequiresApi(api = Build.VERSION_CODES.O)
  private void addNotification(AlarmOl alarm)
  {
    NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
    Notification notification;
    PendingIntent activity;
    Intent intent;

    Log.i(TAG, "AlarmNotificationOl.addNotification(" + alarm.getId() + ", '" + alarm.getTitle() + "', '" + mDateTime.formatDetails(alarm) + "')");

    intent = new Intent(this.getApplicationContext(), AlarmFragment.class);
    intent.setAction(Intent.ACTION_MAIN);
    intent.addCategory(Intent.CATEGORY_LAUNCHER);

    activity = PendingIntent.getActivity(this, (int)alarm.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

    NotificationChannel channel = new NotificationChannel("alarmfragment_01", "AlarmFragment Notifications",
        NotificationManager.IMPORTANCE_DEFAULT);

    notification = new Builder(this)
        .setContentIntent(activity)
        .setSmallIcon(R.drawable.ic_notification)
        .setAutoCancel(true)
        .setContentTitle("Missed alarm: " + alarm.getTitle())
        .setContentText(mDateTime.formatDetails(alarm))
        .setChannelId("alarmfragment_01")
        .build();

    notificationManager.createNotificationChannel(channel);

    notificationManager.notify((int)alarm.getId(), notification);
  }

  @Override
  public void onBackPressed()
  {
    finish();
  }

  private class PlayTimerTask extends TimerTask
  {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void run()
    {
      Log.i(TAG, "AlarmNotificationOl.PalyTimerTask.run()");
      addNotification(mAlarm);
      finish();
    }
  }
}

