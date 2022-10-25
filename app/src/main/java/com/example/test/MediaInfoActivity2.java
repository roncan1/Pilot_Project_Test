package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MediaInfoActivity2 extends AppCompatActivity {

    ImageView iv;
    int imgCount = 0, appCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_info2);

        TextView tv_img = findViewById(R.id.tv_img2);
        TextView tv_app = findViewById(R.id.tv_app2);
        iv = findViewById(R.id.iv_1);
        tv_img.setText(unitConversion(getAllImageSizeByByte()) + " / " + imgCount + "개");
//        tv_app.setText(unitConversion(getPackageList()) + " / " + appCount + "개");
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

    void test1() {
        final PackageManager packageManager = this.getApplicationContext().getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, 0);

        for (ResolveInfo info : list) {
            String appActivity      = info.activityInfo.name;
            String appPackageName   = info.activityInfo.packageName;
            String appName          = info.loadLabel(packageManager).toString();


            Log.d("TAG", "appName : " + appName + ", appActivity : " + appActivity + ", appPackageName : " + appPackageName);
        }
    }
}