package com.some.notes;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.some.notes.Adapter.PersonEmailDBHelper;
import com.some.notes.Model.Person;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class RegisterActivityInstagram extends AppCompatActivity {

    EditText username, fullname, password, bio;
    public static EditText email;
    Button register;
    TextView txt_login;
    Window window;
    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressDialog pd;
    private FirebaseUser currentUser;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private String filter = "",random;
    private PersonEmailDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_instagram);


        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        if (Build.VERSION.SDK_INT >= 21) {
            window = this.getWindow();
            window.setStatusBarColor(this.getResources().getColor(R.color.white));
        }
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        fullname = findViewById(R.id.fullname);
        bio = findViewById(R.id.bio);
        password = findViewById(R.id.password);
        register = findViewById(R.id.register);
        txt_login = findViewById(R.id.txt_login);

        dbHelper = new PersonEmailDBHelper(this);



        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();




        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivityInstagram.this, LoginActivityInstagram.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd = new ProgressDialog(RegisterActivityInstagram.this);
                pd.setMessage("Please wait...");
                pd.show();

                String str_username = username.getText().toString();
                String str_fullname = fullname.getText().toString();
                String str_bio = bio.getText().toString();
                String str_email = email.getText().toString();
                String str_password = password.getText().toString();

                if (TextUtils.isEmpty(str_username) || TextUtils.isEmpty(str_fullname) || TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password) || TextUtils.isEmpty(str_bio)) {
                    Toast.makeText(RegisterActivityInstagram.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                } else if (str_password.length() < 6) {
                    Toast.makeText(RegisterActivityInstagram.this, "Password must have 6 characters!", Toast.LENGTH_SHORT).show();
                } else {
                    Person person = new Person(str_email, str_fullname, str_bio, str_bio);
                    dbHelper.saveNewPerson(person);


                    register(str_username, str_fullname, str_email, str_password, str_bio);
                }
            }
        });
    }

    public void register(final String username, final String fullname, String email, String password, final String bio) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivityInstagram.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userID = firebaseUser.getUid();

                            final DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();

                            String deviceToken = FirebaseInstanceId.getInstance().getToken();

                            final String currentUserID = auth.getCurrentUser().getUid();
                            RootRef.child("Users").child(currentUserID).setValue("");

                            random = generateString(12);


                            RootRef.child("Users").child(currentUserID).child("device_token")
                                    .setValue(deviceToken);


                            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("id", userID);
                            map.put("device_token", deviceToken);
                            map.put("username", username.toLowerCase());
                            map.put("fullname", fullname);
                            map.put("search", username.toLowerCase());
                            map.put("typingTo", "noOne");
                            map.put("randomUsersId", random);
                            map.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/instagramtest-fcbef.appspot.com/o/placeholder.png?alt=media&token=b09b809d-a5f8-499b-9563-5252262e9a49");
                            map.put("bio", bio);


                            reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

//                                        RootRef.child("Follow").child(currentUserID)
//                                                .child("following").child("e4EOZElDsAVboqBwDxDyGp3LzP23").setValue(true);
//
//                                        RootRef.child("Follow").child("e4EOZElDsAVboqBwDxDyGp3LzP23")
//                                                .child("followers").child(currentUserID).setValue(true);
//
//
//                                        RootRef.child("Follow").child(currentUserID)
//                                                .child("following").child("yewaulYKrQQmltA9bHv4GbSaGeA3").setValue(true);
//
//                                        RootRef.child("Follow").child("yewaulYKrQQmltA9bHv4GbSaGeA3")
//                                                .child("followers").child(currentUserID).setValue(true);

                                        Person person = new Person(random, "hello", "suri", "rathore");
                                        dbHelper.saveNewPerson(person);


                                        pd.dismiss();
                                        Intent intent = new Intent(RegisterActivityInstagram.this, LoginSuccessActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        } else {
                            pd.dismiss();
                            Toast.makeText(RegisterActivityInstagram.this, "You can't register with this email or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private String generateString(int lenth){
        char[] chasr= "qwertyuiopasdfghjklzxcvbnm".toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i <lenth ; i++) {
            char c = chasr[random.nextInt(chasr.length)];
            stringBuilder.append(c);


        }
        return stringBuilder.toString();
    }
}