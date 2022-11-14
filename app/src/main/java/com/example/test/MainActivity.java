package com.example.test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    static Context context;

    Button btn_yes_url, btn_no_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;

        // 푸시알림 채널 생성
        createNotificationChannel("TEST", "default channel", NotificationManager.IMPORTANCE_HIGH);

        // 권한 체크 및 요청
        if (!permissionGrantred()) {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
        }

        String url = "https://gist.githubusercontent.com/mrw0119/716f47bce7dac74doubt57c1747757f03b45b/raw/073dd8a69a3cb1bacd644a4d174160f3fa1ba3e3/android_clickable_notification.java";



        btn_no_url = findViewById(R.id.btn_no_url);
        btn_no_url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 알림 전송
                createNotification("TEST", 1, "url 없는 노티", "안녕하세요");
            }
        });

        btn_yes_url = findViewById(R.id.btn_yes_url);
        btn_yes_url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 알림 전송
                createNotification("TEST", 1, "url 있는 노티", "여기 접속하세요." + url);
            }
        });
    }

    void createNotification(String channelId, int id, String title, String text)
    {
        Intent intent = new Intent(this, MainActivity.class);       // 클릭시 실행할 activity를 지정
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)                // true이면 클릭시 알림이 삭제된다
                .setContentIntent(pendingIntent)    // 클릭시 설정된 PendingIntent가 실행된다
                //.setTimeoutAfter(1000)
                //.setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(id, builder.build());
    }

    void destroyNotification(int id)
    {
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
    }

    // 노티 채널 생성
    void createNotificationChannel(String channelId, String channelName, int importance)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId, channelName, importance));
        }
    }

    // 받아온 정보를 바탕으로 팝업창 표시
    static void showDialog(String title, String text, String subText, String time) {
        Dialog dl = new Dialog(context);
        dl.setContentView(R.layout.dailog_noti_posted);
        dl.show();

        TextView tv_title = dl.findViewById(R.id.tv_title);
        tv_title.setText(title);

        TextView tv_text = dl.findViewById(R.id.tv_text);
        tv_text.setText(text);

        TextView tv_text_sub = dl.findViewById(R.id.tv_text_sub);
        tv_text_sub.setText(subText);

        TextView tv_time = dl.findViewById(R.id.tv_time);
        tv_time.setText(time);

    }

    // 권환 취득여부 확인
    private boolean permissionGrantred() {
        Set<String> sets = NotificationManagerCompat.getEnabledListenerPackages(this);
        if (sets != null && sets.contains(getPackageName())) {
            return true;
        } else {
            return false;
        }
    }

}