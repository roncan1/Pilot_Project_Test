package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.text.DecimalFormat;

public class MediaInfoActivity extends AppCompatActivity {

    long imgStUsedMemory, exStAllMemory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_info);

        checkExternalStorageAllMemory();
        checkExternalUsedMemory();

        TextView tv_img = findViewById(R.id.tv_img);
        TextView tv_video = findViewById(R.id.tv_video);
        TextView tv_audio = findViewById(R.id.tv_audio);

        tv_img.setText(getFileSize(imgStUsedMemory));
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

    private void checkExternalUsedMemory() {
        if(isExternalMemoryAvailable()){
            File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            Log.d("파일경로", folder.getPath());
            long bytes = getFolderSize(folder);

            imgStUsedMemory = bytes;
        }
    }

    public static long getFolderSize(File folder) {
        long length = 0;
        File[] files = folder.listFiles();
        Log.d("폴더인가?", ""+folder.isDirectory());
        int count = files.length;
        Log.d("하위 폴더 개수", ""+count);
        for (int i = 0; i < count; i++) {
            if (files[i].isFile()) {
                Log.d("파일 or 폴더", "파일, 경로 : " + files[i].getPath());
                length += files[i].length();
            } else {
                Log.d("파일 or 폴더", "파일, 경로 : " + files[i].getPath());
                length += getFolderSize(files[i]);
            }
        }

        return length;
    }

    private void checkExternalStorageAllMemory() {
        if(isExternalMemoryAvailable()){
            StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
            long blockSize = stat.getBlockSizeLong();
            long totalBlocks = stat.getBlockCountLong();

            exStAllMemory = totalBlocks * blockSize;
        }
    }
}