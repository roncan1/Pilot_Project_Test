package com.example.test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.usage.StorageStats;
import android.app.usage.StorageStatsManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.os.UserHandle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class MediaInfoActivity2 extends AppCompatActivity {

    ImageView iv;
    int imgCount = 0, appCount = 0;

    long all, image, audio, video, apps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_info2);

        all = getIntent().getLongExtra("all", 0);

        // 앱 용량 확인에 필요한 권한 취득
        int a = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.PACKAGE_USAGE_STATS);
        if (a == PackageManager.PERMISSION_DENIED) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        }

        TextView tv_img = findViewById(R.id.tv_img2);
        TextView tv_app = findViewById(R.id.tv_app2);
        iv = findViewById(R.id.iv_1);
        tv_img.setText(unitConversion(getAllImageSizeByByte()) + " / " + imgCount + "개");
        tv_app.setText(unitConversion(test2()) + " / " + appCount + "개");


    }

    private long getAllImageSizeByByte() {
        // 어플 용량
        long bytes = 0;

        // 외부저장소 Uri
        Uri externalUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        // MediaStore를 이용해서 무슨 정보를 받아올 것인가
        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.SIZE,
        };

        // 파일목록 쿼리 (3,4,5번 인자값을 null로 설정해 모든 이미지파일을 받아옴)
        Cursor cursor = getApplicationContext().getContentResolver().query(
                externalUri,// 어느저장소?
                projection, // 어떤 정보?
                null, // 어떤 조건으로? (where 절)
                null, //``
                null// 무엇을 기준으로 정렬?
        );

        // 원하는 데이터가 저장된 column값을 받아옴
        int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);

        // 모든 이미지 파일을 하나씩 탐색하며 원하는 동작을 처리
        while (cursor.moveToNext()) {
            int size = cursor.getInt(sizeColumn);
            bytes += size;
            imgCount++;
        }

        // 모든 이미지의 크기가 더해진 byte 값을 리턴
        return bytes;
    }

    // 단위 변환
    public String unitConversion(long size) {
        if (size <= 0)
            return "0";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

//
//
//
// 여기서부턴 앱 사용량 확인
//
//
//

    public long test2() {
        // 패키지 매니저 받아오기
        final PackageManager packageManager = getPackageManager();

        // 각각 설치된 앱, 시스템 앱, 중복 앱으로 설정한 인텐트 생성
        Intent intentLaunched = new Intent(Intent.ACTION_MAIN);
        intentLaunched.addCategory(Intent.CATEGORY_LAUNCHER);

        Intent intentDefault = new Intent(Intent.ACTION_MAIN);
        intentDefault.addCategory(Intent.CATEGORY_DEFAULT);

        Intent intentDuplicated = new Intent(Intent.ACTION_MAIN);
        intentDuplicated.addCategory(Intent.CATEGORY_LAUNCHER);
        intentDuplicated.addCategory(Intent.CATEGORY_DEFAULT);

        // 인텐트에 설정한 카테고리에 해당하는 앱 리스트 생성
        List<ResolveInfo> launchedList = packageManager.queryIntentActivities(intentLaunched, 0);
        List<ResolveInfo> defaultList = packageManager.queryIntentActivities(intentDefault, 0);
        List<ResolveInfo> duplicatedList = packageManager.queryIntentActivities(intentDuplicated, 0);

        // (설치된 앱의 용량 + 시스템 앱의 용량) - 중복앱의 용량을 long 타입으로 리턴
        return (getAppsSize(launchedList, true) + getAppsSize(defaultList, true)) - getAppsSize(duplicatedList, false);
    }

    private long getAppsSize(List<ResolveInfo> list, boolean plusCount) {
        long bytes = 0;

        // StorageStatsManager 생성
        StorageStatsManager statsManager = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                statsManager = getSystemService(StorageStatsManager.class);
            }
        }

        // 받아온 리스트의 길이만큼 반복
        for (ResolveInfo info : list) {
            UUID uuid = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                uuid = info.activityInfo.applicationInfo.storageUuid;
            }
            String packageName = info.activityInfo.packageName;
            UserHandle user = Process.myUserHandle();
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // 각각의 info에 들어있는 uuid, 패키지이름, userHandle을 이용해 데이터를 뽑아옴
                    StorageStats stats = statsManager.queryStatsForPackage(uuid, packageName, user);

                    // Apk, 최적화된 dex 파일, native library, cache 를 포함한 앱의 데이터의 용량을 더해줌
                    bytes += stats.getAppBytes() + stats.getDataBytes();


                    // plusCount 가 true라면 경우 앱 개수에 1을 더하고 아니라면 뺴준다
                    appCount += (plusCount) ?  1 :  -1;
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bytes;
    }
}