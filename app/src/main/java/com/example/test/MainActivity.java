package com.example.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    // 전체 저장소 용량 확인 테스트

    long exStAllMemory, exStAvailableMemory, exStUsedMemory;

    private PermissionSupport permission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionCheck();

        checkExternalStorageAllMemory();
        checkExternalAvailableMemory();
        checkExternalUsedMemory(exStAllMemory);

        TextView tv_all = findViewById(R.id.tv_all);
        TextView tv_used = findViewById(R.id.tv_used);
        TextView tv_available = findViewById(R.id.tv_available);

        tv_all.setText("전체 " + getFileSize(exStAllMemory));
        tv_used.setText("사용중 " + getFileSize(exStUsedMemory));
        tv_available.setText("사용 가능 " + getFileSize(exStAvailableMemory));

        Button btn = findViewById(R.id.btn_go_to_2);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MediaInfoActivity.class);
                startActivity(intent);
            }
        });

    }

    public String getFileSize(long size) {
        if (size <= 0)
            return "0";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    private boolean isExternalMemoryAvailable(){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    private void checkExternalStorageAllMemory() {
        if(isExternalMemoryAvailable() == true){
            StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
            long blockSize = stat.getBlockSizeLong();
            long totalBlocks = stat.getBlockCountLong();

            exStAllMemory = totalBlocks * blockSize;
        }
    }

    private void checkExternalAvailableMemory() {
        if(isExternalMemoryAvailable() == true){
            File file = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(file.getPath());
            long blockSize = stat.getBlockSizeLong();
            long availableBlocks = stat.getAvailableBlocksLong();

            exStAvailableMemory = availableBlocks * blockSize;
        }
    }

    private void checkExternalUsedMemory(long exStAllMemory) {
        if(isExternalMemoryAvailable() == true){
            File file = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(file.getPath());
            long blockSize = stat.getBlockSizeLong();
            long availableBlocks = stat.getAvailableBlocksLong();

            exStUsedMemory = exStAllMemory - (availableBlocks * blockSize);
        }
    }

    // 권한 체크
    private void permissionCheck(){
        // sdk 23버전 이하 버전에서는 permission이 필요하지 않음
        if(Build.VERSION.SDK_INT >= 23){

            // 클래스 객체 생성
            permission =  new PermissionSupport(this, this);

            // 권한 체크한 후에 리턴이 false일 경우 권한 요청을 해준다.
            if(!permission.checkPermission()){
                permission.requestPermission();
            }
        }
    }

    // Request Permission에 대한 결과 값을 받는다.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // 리턴이 false일 경우 다시 권한 요청
        if (!permission.permissionResult(requestCode, permissions, grantResults)){
            permission.requestPermission();
        }
    }


}