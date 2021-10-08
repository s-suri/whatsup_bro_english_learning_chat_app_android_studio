package com.some.notes;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.some.notes.Uploads.MainActivityUpload;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MainMessanger extends AppCompatActivity {

    ViewPager myViewPager;
    TabLayout myTabLayout;
    Button button;
    TextView search,ic_more;
    DatabaseReference RootRef;
    private FirebaseUser currentUser;
    FirebaseAuth mAuth;
    TextView log_out,change_password,data_upload;
    RelativeLayout menu_relative;
    private TabsAccessorAdapter myTabsAccesorAdapter;
    Window window;
    DatabaseReference reference;
    private String saveCurrentTime, saveCurrentDate;
    AdView adView;
    int SPLASH_TIME = 3000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_messanger);



        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        if (currentUser == null) {
            Intent intent = new Intent(MainMessanger.this, LoginActivityInstagram.class);
            startActivity(intent);
            finish();

        }
        else
        {
            state("online");
        }





        if (Build.VERSION.SDK_INT>=21){
            window = this.getWindow();
            window.setStatusBarColor(this.getResources().getColor(R.color.white));
        }


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        RootRef = FirebaseDatabase.getInstance().getReference();

        search = findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                state("offline");
                    Intent intent = new Intent(MainMessanger.this,ContactsActivity.class);
                  startActivity(intent);
            }
        });

        myViewPager = (ViewPager) findViewById(R.id.main_tabs_pager);
        myTabsAccesorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabsAccesorAdapter);
        myTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);



        menu_relative = findViewById(R.id.menu_relative);
        log_out = findViewById(R.id.log_out);
        change_password = findViewById(R.id.change_password);
        ic_more = findViewById(R.id.ic_more);
        data_upload = findViewById(R.id.data_upload);


        menu_relative.setVisibility(View.GONE);
       ic_more.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               menu_relative.setVisibility(View.VISIBLE);

               //Code to start timer and take action after the timer ends
               new Handler().postDelayed(new Runnable() {
                   @Override
                   public void run() {

                       menu_relative.setVisibility(View.GONE);

                   }
               }, SPLASH_TIME);


           }
       });

       log_out.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               menu_relative.setVisibility(View.GONE);
               state("offline");
               SendUserToLoginActivity();
               mAuth.signOut();

           }
       });

       change_password.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               menu_relative.setVisibility(View.GONE);
               Intent intent = new Intent(MainMessanger.this,LoginChangePasspword.class);
               startActivity(intent);
               finish();
           }
       });


       data_upload.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               menu_relative.setVisibility(View.GONE);
               Intent intent = new Intent(MainMessanger.this, SettingsActivity.class);
               startActivity(intent);
           }
       });






    }

    private void SendUserToLoginActivity() {

        Intent intent = new Intent(MainMessanger.this,LoginActivityInstagram.class);
        startActivity(intent);
        finish();
    }


    @Override
        protected void onPause() {
            super.onPause();

            state("offline");

        }

        @Override
        protected void onStop() {
            super.onStop();
            state("offline");



        }

        @Override
        public void onBackPressed() {
            super.onBackPressed();
            state("offline");


        }



    private void state(String online) {

        reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid()).child("userState");


        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currendateFormat = new SimpleDateFormat("MMM dd");
        saveCurrentDate = currendateFormat.format(calForDate.getTime());


        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currenTimeFormat = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currenTimeFormat.format(calForTime.getTime());


        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("state", online);
        hashMap.put("date", saveCurrentDate);
        hashMap.put("time", saveCurrentTime);

        reference.updateChildren(hashMap);
    }


}








