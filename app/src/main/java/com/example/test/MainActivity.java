package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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


}