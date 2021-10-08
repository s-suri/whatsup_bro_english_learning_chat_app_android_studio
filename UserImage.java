package com.some.notes;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.TaskStackBuilder;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserImage extends AppCompatActivity {

    private CircleImageView imageView;
    private String imageUri;
    RelativeLayout imageBacking;


    DatabaseReference RootRef;
    private FirebaseUser currentUser;
    FirebaseAuth mAuth;

    private String currentUserID;

    DatabaseReference reference;
    private String saveCurrentTime, saveCurrentDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_image);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        imageBacking = findViewById(R.id.imagBack);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        imageView = findViewById(R.id.image_viewr);
        imageUri = getIntent().getStringExtra("url");
        Picasso.get().load(imageUri).into(imageView);

    }
    public boolean onSupportNavigateUp() {
        Intent upIntent = getSupportParentActivityIntent();

        if (upIntent != null) {
            if (supportShouldUpRecreateTask(upIntent)) {
                TaskStackBuilder b = TaskStackBuilder.create(this);
                onCreateSupportNavigateUpTaskStack(b);
                onPrepareSupportNavigateUpTaskStack(b);
                b.startActivities();

                try {
                    ActivityCompat.finishAffinity(this);
                } catch (IllegalStateException e) {
                    // This can only happen on 4.1+, when we don't have a parent or a result set.
                    // In that case we should just finish().
                    finish();
                }
            } else {
                // This activity is part of the application's task, so simply
                // navigate up to the hierarchical parent activity.
                supportNavigateUpTo(upIntent);
            }
            return true;
        }
        return false;
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void imageBack() {
        Intent chatIntent = new Intent(UserImage.this, MainActivity.class);

        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View, String>(imageView,"imageTransition");

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(UserImage.this,pairs);


        startActivity(chatIntent,options.toBundle());
    }

    @Override
    protected void onStart() {
        super.onStart();

        state("online");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        state("offline");
    }

    @Override
    protected void onPause() {
        super.onPause();

        state("offline");

        finish();


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

