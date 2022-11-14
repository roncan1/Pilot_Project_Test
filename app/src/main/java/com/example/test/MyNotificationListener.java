package com.example.test;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyNotificationListener extends NotificationListenerService {
    private final String DEFAULT = "DEFAULT";
    Notification notification;
    Bundle extras;

    // 노티가 수신돠었을 때
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        notification = sbn.getNotification();
        extras = sbn.getNotification().extras;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Icon smallIcon = notification.getSmallIcon();
            Icon largeIcon = notification.getLargeIcon();
        }

        Log.d("푸시 확인", "onNotificationPosted: " + getTitle());
        // 다이얼로그 팝업
        MainActivity.showDialog(getTitle(), getText(), getSubText(), getTime());

        switch (checkContent(getText())) {
            case "doubt":
                Log.d("푸시 보내ㅐㅐㅐㅐㅐㅐㅐㅐㅐㅐㅐㅐㅐㅐㅐ", "onNotificationPosted: " + checkContent(getText()));
                // 푸시알림 채널 생성
                createNotificationChannel(DEFAULT, "default channel", NotificationManager.IMPORTANCE_HIGH);
                // 알림 전송
                createNotification(DEFAULT, 1, "의심 감지", "내용이 의심됨");
                break;
            case "danger":
                Log.d("푸시 보내ㅐㅐㅐㅐㅐㅐㅐㅐㅐㅐㅐㅐㅐㅐㅐ", "onNotificationPosted: " + checkContent(getText()));
                // 푸시알림 채널 생성
                createNotificationChannel(DEFAULT, "default channel", NotificationManager.IMPORTANCE_HIGH);
                // 알림 전송
                createNotification(DEFAULT, 1, "위험 감지", "내용이 위험함");
                break;
            default: Log.d("푸시 안보내ㅐㅐㅐㅐㅐㅐㅐㅐㅐㅐㅐㅐㅐㅐㅐ", "onNotificationPosted: " + getTitle());
                break;
        }
    }

    public String checkContent(String content) {
        try {
            String REGEX = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
            Pattern p = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(content);
            if (m.find()) {
                return checkDanger(m.group());
            }
            return "safe";
        } catch (Exception e) {
            return "safe";
        }
    }

    public String checkDanger(String content) {
        if (content.contains("danger")) {
            return "danger";
        } else if (content.contains("doubt")) {
            return "doubt";
        } else {
            return "safe";
        }
    }


    public String getTitle() {
        return extras.getString(Notification.EXTRA_TITLE);
    }

    public String getText() {
        return String.valueOf(extras.getCharSequence(Notification.EXTRA_TEXT));
    }

    public String getSubText() {
        return String.valueOf(extras.getCharSequence(Notification.EXTRA_SUB_TEXT));
    }

    public String getTime() {
        long now = System.currentTimeMillis();

        Date date = new Date(now);

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");

        return sdf.format(date);
    }

    void createNotification(String channelId, int id, String title, String text) {
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)                // true이면 클릭시 알림이 삭제된다
                //.setTimeoutAfter(1000)
                //.setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(id, builder.build());
    }

    void destroyNotification(int id) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
    }

    // 노티 채널 생성
    void createNotificationChannel(String channelId, String channelName, int importance) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId, channelName, importance));
        }
    }
}
