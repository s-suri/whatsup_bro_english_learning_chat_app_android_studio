package com.some.notes.Documents;


import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
public class SplashScreen extends AppCompatActivity {

    public static String sqlId, id;
    Intent intent;
    public static String userid;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

        //load data here
//        storagePaths = StorageUtil.getStorageDirectories(this);
//
//        for (String path : storagePaths) {
//            storage = new File(path);
//            Method.load_Directory_Files(storage);
//        }

        intent = getIntent();
        userid = intent.getStringExtra("userid");
        sqlId = intent.getStringExtra("name");


        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
        intent.putExtra("userid", userid);
        intent.putExtra("name", sqlId);
        startActivity(intent);
    }
}
