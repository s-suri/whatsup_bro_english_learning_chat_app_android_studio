package com.some.notes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Pair;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.anstrontechnologies.corehelper.AnstronCoreHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.some.notes.Model.Chat;
import com.some.notes.Model.CityDataItem;
import com.some.notes.Model.Comment;
import com.some.notes.Model.SampleDataProvider;
import com.some.notes.Model.SqlMessage;
import com.some.notes.Model.User;
import com.some.notes.Notifications.APIService;
import com.some.notes.Notifications.Client;
import com.some.notes.Notifications.Data;
import com.some.notes.Notifications.MyResponse;
import com.some.notes.Notifications.Sender;
import com.some.notes.Notifications.Token;
import com.squareup.picasso.Picasso;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OnlineMessageActivity extends AppCompatActivity {
    public static String sqlId,id;
    private List<String> fileNameList;
    private List<String> fileDoneList;
    DatabaseReference RootRef;
    private Uri fileUri;
    private String imageurl;
    private StorageTask uploadTask;
    Button getText_btn;
    ImageView showImage_img,reply_image;
    Boolean isscrolling = false;
    int currentItems,totalItems,scrollOutItems;
    RelativeLayout reply_bottom,relative_layout_message;
    CircleImageView profile_image;
    TextView username, inputPassword, showText_txt, userLastSeen, messageConvert,reply_preview_message,reply_delete,type;
    public static FirebaseUser fuser;
    DatabaseReference reference;
    ImageButton btn_send, SendFilesButton,reply_send;
    EditText text_send,reply_edit,reply_password;
    ImageView background_image;
    List<Comment> mchat = new ArrayList<>();


    RecyclerView recyclerView, findFriendLis;
    Intent intent;
    ValueEventListener seenListener;
    public static String userid;
    String messageid, AES = "AES", cheker = "", myUrl = "", saveCurrentTime, saveCurrentDate,saveCurrentTime1, saveCurrentDate1,trans_msg,outputString;
    private static final int RESULT_LOAD_IMAGE = 1;
    APIService apiService;
    boolean notify = false;
    boolean seen = false;
    Dialog myDialog;
    Window window;
    private long receivedPersonId;
    private List<CityDataItem> mDataList;
    private MessageActivity.MyDataAdapter mDataAdapter;
    private String filter = "";
    ImageView compress_image_view;
    AnstronCoreHelper anstronCoreHelper;
    LinearLayoutManager linearLayoutManager;
    int SPLASH_TIME = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");


        fileNameList = new ArrayList<>();
        fileDoneList = new ArrayList<>();


        myDialog = new Dialog(this);
        if (Build.VERSION.SDK_INT >= 21) {
            window = this.getWindow();
            window.setStatusBarColor(this.getResources().getColor(R.color.white));
        }


        apiService = Client.getClient("https://fcm.googleapis.com").create(APIService.class);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        userLastSeen = (TextView) findViewById(R.id.custom_user_last_seen);
        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);
        btn_send.setEnabled(true);
        inputPassword = findViewById(R.id.password);
        reply_bottom = findViewById(R.id.reply_bottom);
        reply_bottom.setVisibility(View.GONE);
        reply_edit = findViewById(R.id.reply_edit);
        reply_image = findViewById(R.id.reply_image);
        reply_send = findViewById(R.id.reply_Send);
        type = findViewById(R.id.type);

        reply_preview_message = findViewById(R.id.preview_message);
        relative_layout_message = findViewById(R.id.relative_layout_message);
        reply_delete = findViewById(R.id.reply_delete);
        reply_password = findViewById(R.id.reply_password);
        reply_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relative_layout_message.setVisibility(View.VISIBLE);
                reply_bottom.setVisibility(View.GONE);

            }
        });


        intent = getIntent();
        userid = intent.getStringExtra("userid");
        messageid = intent.getStringExtra("messageid");
        fuser = FirebaseAuth.getInstance().getCurrentUser();


        mDataList = SampleDataProvider.cityDataItemList;
        findFriendLis = findViewById(R.id.search_translate);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        findFriendLis.setLayoutManager(manager);


        RootRef = FirebaseDatabase.getInstance().getReference();


        seen = true;

        LinearLayoutManager linearLayoutTranslate = new LinearLayoutManager(getApplicationContext());
        linearLayoutTranslate.setStackFromEnd(true);
        findFriendLis.setLayoutManager(linearLayoutTranslate);


        getText_btn = findViewById(R.id.btn_gettext);

        showImage_img = findViewById(R.id.img_imageview);
        messageConvert = findViewById(R.id.message_convert);
        //Set OnClick event for getImage_btn Button to take image from camera

        //Set OnClick event for getText_btn Button to get Text from image

        getText_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //         GetTextFromImageFunction();
            }
        });


        SendFilesButton = (ImageButton) findViewById(R.id.send_files_btn);
        SendFilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView txtclose;
                LinearLayout image, images, video, pdf, videos, pdfs, ms_file, ms_files;

                RelativeLayout relativeLayout = findViewById(R.id.costom_popup);
                relativeLayout.setVisibility(View.VISIBLE);
                Button btnFollow;

                txtclose = (TextView) findViewById(R.id.txtclose);
                image = (LinearLayout) findViewById(R.id.image);
                images = (LinearLayout) findViewById(R.id.images);
                video = (LinearLayout) findViewById(R.id.video);
                videos = (LinearLayout) findViewById(R.id.videos);
                pdf = findViewById(R.id.pdf1);
                pdfs = (LinearLayout) findViewById(R.id.pdfs);
                ms_file = (LinearLayout) findViewById(R.id.ms_word);
                ms_files = (LinearLayout) findViewById(R.id.ms_words);
                txtclose.setText("M");
                txtclose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        relativeLayout.setVisibility(View.GONE);
                    }
                });


                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        cheker = "image";
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(intent.createChooser(intent, "Select Image"), 438);
                    }
                });

                images.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        cheker = "image";
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        startActivityForResult(Intent.createChooser(intent, "Select Image"), RESULT_LOAD_IMAGE);

                    }
                });

                video.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        cheker = "video";
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.setType("video/*");
                        startActivityForResult(intent.createChooser(intent, "Select Ms World File"), 438);

                    }
                });

                videos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        cheker = "video";
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.setType("video/*");
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        startActivityForResult(intent.createChooser(intent, "Select Ms World File"), RESULT_LOAD_IMAGE);

                    }
                });

                pdf.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        cheker = "PDF";
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.setType("application/pdf");
                        startActivityForResult(intent.createChooser(intent, "Select PDF"), 438);


                    }
                });

                pdfs.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        cheker = "PDF";
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.setType("application/pdf");
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        startActivityForResult(intent.createChooser(intent, "Select PDF"), RESULT_LOAD_IMAGE);

                    }
                });


                ms_file.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        cheker = "docx";
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.setType("application/msword");
                        startActivityForResult(intent.createChooser(intent, "Select Ms World File"), 438);

                    }
                });


                ms_files.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        cheker = "docx";
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.setType("application/msword");
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        startActivityForResult(intent.createChooser(intent, "Select Ms World File"), RESULT_LOAD_IMAGE);

                    }
                });


            }
        });


        DisplayLastSeen();


        text_send.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                if (s.toString().trim().length() == 0) {
                    typingStatus("noOne");
                    btn_send.setEnabled(false);
                    findFriendLis.setVisibility(View.GONE);

                } else {
                    btn_send.setEnabled(true);
                    findFriendLis.setVisibility(View.VISIBLE);
                    typingStatus(fuser.getUid());
                    mDataAdapter.getFilter().filter(s);
                    //          firebaseSearch(s.toString().toLowerCase());
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        reply_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().trim().length() == 0) {
                    typingStatus("noOne");
                    findFriendLis.setVisibility(View.GONE);

                } else {
                    findFriendLis.setVisibility(View.GONE);
                    typingStatus(fuser.getUid());
                    mDataAdapter.getFilter().filter(s);


                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        findFriendLis.setVisibility(View.GONE);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify = true;


                String msg = text_send.getText().toString();

                if (!msg.equals("")) {


                    try {
                        msg = encrypt(text_send.getText().toString(), inputPassword.getText().toString());
                        trans_msg = encrypt(messageConvert.getText().toString(), inputPassword.getText().toString());


                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    try {
                        outputString = decrypt(text_send.getText().toString(), inputPassword.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();

                    }

                    text_send.setText("");
                    messageConvert.setText("");


                } else {
                    Toast.makeText(OnlineMessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }

            }
        });


        reply_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify = true;
                String msg = reply_edit.getText().toString();
                String ret_type = type.getText().toString();
                String ret_preview_message = reply_preview_message.getText().toString();
                if (!msg.equals("")) {


                    try {

                        msg = encrypt(reply_edit.getText().toString(), reply_password.getText().toString());
                        trans_msg = encrypt(messageConvert.getText().toString(), reply_password.getText().toString());


                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    try {
                        outputString = decrypt(text_send.getText().toString(), inputPassword.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();

                    }

                    relative_layout_message.setVisibility(View.VISIBLE);
                    reply_bottom.setVisibility(View.GONE);


                } else {
                    Toast.makeText(OnlineMessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }

            }
        });


        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());

                Picasso.get().load(user.getImageurl()).into(profile_image);

                readMesagges();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        seenMessage(userid);
    }


    private void readMesagges() {


        reference = FirebaseDatabase.getInstance().getReference("Chats").child(fuser.getUid()).child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        status("online");
    }



    private void seenMessage(final String userid){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chats").child(userid).child(fuser.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if (snapshot.child("isseen").exists()){
                        if (seen) {


                            String Id = snapshot.child("messageID").getValue().toString();
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("isseen", "true");
                            ref.child(Id).updateChildren(hashMap);
                        }
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    private void ReplyMessage(String sender, final String receiver, String message, String uid, String fuserUid, String rep_type, String preview_message, String messageId) {
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currendateFormat = new SimpleDateFormat("MMM dd");
        saveCurrentDate = currendateFormat.format(calForDate.getTime());


        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currenTimeFormat = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currenTimeFormat.format(calForTime.getTime());


        Calendar calForDate1 = Calendar.getInstance();
        SimpleDateFormat currendateFormat1 = new SimpleDateFormat("yyyyMMdd");
        saveCurrentDate1 = currendateFormat1.format(calForDate1.getTime());


        Calendar calForTime1 = Calendar.getInstance();
        SimpleDateFormat currenTimeFormat1 = new SimpleDateFormat("a hhmmss");
        saveCurrentTime1 = currenTimeFormat1.format(calForTime1.getTime());


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();




        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("time", saveCurrentTime);
        hashMap.put("date", saveCurrentDate);
        hashMap.put("messageID", saveCurrentDate1+saveCurrentTime1);
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("isseen", "false");
        hashMap.put("message", message);
        hashMap.put("bio", uid);
        hashMap.put(sqlId, true);
        hashMap.put("type", rep_type);
        hashMap.put("lastSendMessage", message);
        hashMap.put("preMessage", preview_message);
        hashMap.put("last","false");


        HashMap<String, Object> map = new HashMap<>();
        map.put("time", saveCurrentDate1 + "-" + saveCurrentTime1);




        reference.child("Chats").child(sender).child(receiver).child(saveCurrentDate1+saveCurrentTime1).setValue(hashMap);


        final String msg = message;

        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (notify) {
                    sendNotifiaction(receiver, user.getUsername(), fuserUid);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void ReplyMessageReciever(String sender, final String receiver, String message, String uid, String rep_type, String preview_message, String messageId) {


        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currendateFormat = new SimpleDateFormat("MMM dd");
        saveCurrentDate = currendateFormat.format(calForDate.getTime());


        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currenTimeFormat = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currenTimeFormat.format(calForTime.getTime());


        Calendar calForDate1 = Calendar.getInstance();
        SimpleDateFormat currendateFormat1 = new SimpleDateFormat("yyyyMMdd");
        saveCurrentDate1 = currendateFormat1.format(calForDate1.getTime());


        Calendar calForTime1 = Calendar.getInstance();
        SimpleDateFormat currenTimeFormat1 = new SimpleDateFormat("a hhmmss");
        saveCurrentTime1 = currenTimeFormat1.format(calForTime1.getTime());



        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats").child(receiver).child(sender);


        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("time", saveCurrentTime);
        hashMap.put("date", saveCurrentDate);
        hashMap.put("messageID", saveCurrentDate1+saveCurrentTime1);
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("bio", uid);
        hashMap.put(sqlId, true);
        hashMap.put("type", rep_type);
        hashMap.put("isseen", "false");
        hashMap.put("lastSendMessage", message);
        hashMap.put("preMessage", preview_message);
        hashMap.put("last","false");


        HashMap<String, Object> map = new HashMap<>();
        map.put("time", saveCurrentDate1 + "-" + saveCurrentTime1);


        reference.child(saveCurrentDate1+saveCurrentTime1).setValue(hashMap);


        reply_edit.setText("");
        type.setText("");

    }

    private void sendNotifiaction(String receiver, final String username, final String message){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(fuser.getUid(), R.mipmap.ic_launcher, username+": "+message, "New Message",
                            userid);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200){
                                        if (response.body().success != 1){
                                            Toast.makeText(OnlineMessageActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void typingStatus(String typing) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("typingTo", typing);

        reference.updateChildren(hashMap);
    }

    private void DisplayLastSeen() {
        DatabaseReference kumar = FirebaseDatabase.getInstance().getReference("Users");
        RootRef.child("Users").child(userid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.child("userState").hasChild("state")) {
                            String state = dataSnapshot.child("userState").child("state").getValue().toString();
                            String date = dataSnapshot.child("userState").child("date").getValue().toString();

                            String time = dataSnapshot.child("userState").child("time").getValue().toString();
                            String type = dataSnapshot.child("typingTo").getValue().toString();

                            if (type.equals(userid)) {
                                userLastSeen.setText("Typing.....");
                            } else if (state.equals("online")) {
                                userLastSeen.setText("online");
                            } else if (state.equals("offline")) {
                                userLastSeen.setText("Last Seen: " + date + " at " + time);
                            }
                        } else {
                            userLastSeen.setText("offline");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void currentUser(String userid){
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentuser", userid);
        editor.apply();
    }

    private void state(String online) {

        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid()).child("userState");


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

    private void status(String status){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid()).child("userState");

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currendateFormat = new SimpleDateFormat("MMM dd");
        saveCurrentDate = currendateFormat.format(calForDate.getTime());


        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currenTimeFormat = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currenTimeFormat.format(calForTime.getTime());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("state", status);
        hashMap.put("date", saveCurrentDate);
        hashMap.put("time", saveCurrentTime);


        reference.updateChildren(hashMap);
    }

    @Override
    protected void onStart() {
        super.onStart();
        status("online");
        seen = true;

    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
        currentUser(userid);
        typingStatus("noOne");
        seen = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        //     reference.removeEventListener(seenListener);
        status("offline");
        currentUser("none");
        typingStatus("noOne");
        seen = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        status("offline");
        typingStatus("noOne");
        seen = false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        status("offline");
        typingStatus("noOne");
        seen = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        status("offline");
        typingStatus("noOne");
        seen = false;

    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Calendar calForDate1 = Calendar.getInstance();
        SimpleDateFormat currendateFormat1 = new SimpleDateFormat("yyyy-MM");
        saveCurrentDate1 = currendateFormat1.format(calForDate1.getTime());




        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {

            if (data.getClipData() != null) {

                int totalItemsSelected = data.getClipData().getItemCount();

                for (int i = 0; i < totalItemsSelected; i++) {

                    Uri fileUri = data.getClipData().getItemAt(i).getUri();

                    String fileName = getFileName(fileUri);

                    if (!cheker.equals("image")) {

                        notify = true;

                        RootRef = FirebaseDatabase.getInstance().getReference();
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(saveCurrentDate1);

                        final String messageSenderRef = "Chats/" + fuser.getUid() + "/" + userid;
                        final String messageReceiverRef = "Chats/" + userid + "/" + fuser.getUid();

                        DatabaseReference userMessageKeyRef = RootRef.child("Chats")
                                .child(fuser.getUid()).child(userid).push();

                        final String messagePushID = generateString(12);

                        final StorageReference filePath = storageReference.child(messagePushID + "." + "jpg");
                        uploadTask = filePath.putFile(fileUri);
                        uploadTask.continueWithTask(new Continuation() {
                            @Override
                            public Object then(@NonNull Task task) throws Exception {

                                if (!task.isSuccessful()) {
                                    throw task.getException();


                                }
                                return filePath.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {

                                    Toast.makeText(OnlineMessageActivity.this, "upload successfully", Toast.LENGTH_SHORT).show();
                                    Uri downloadUrl = task.getResult();
                                    myUrl = downloadUrl.toString();

                                    Calendar calForDate = Calendar.getInstance();
                                    SimpleDateFormat currendateFormat = new SimpleDateFormat("MMM dd");
                                    saveCurrentDate = currendateFormat.format(calForDate.getTime());


                                    Calendar calForTime = Calendar.getInstance();
                                    SimpleDateFormat currenTimeFormat = new SimpleDateFormat("hh:mm a");
                                    saveCurrentTime = currenTimeFormat.format(calForTime.getTime());


                                    Calendar calForDate1 = Calendar.getInstance();
                                    SimpleDateFormat currendateFormat1 = new SimpleDateFormat("yyyyMMdd");
                                    saveCurrentDate1 = currendateFormat1.format(calForDate1.getTime());


                                    Calendar calForTime1 = Calendar.getInstance();
                                    SimpleDateFormat currenTimeFormat1 = new SimpleDateFormat("a hhmmss");
                                    saveCurrentTime1 = currenTimeFormat1.format(calForTime1.getTime());



                                    HashMap<String, Object> groupMessageKey = new HashMap<>();
                                    RootRef.updateChildren(groupMessageKey);


                                    Map messageTextBody = new HashMap();



                                    messageTextBody.put("message", myUrl);
                                    messageTextBody.put("url", myUrl);
                                    messageTextBody.put("name", fileUri.getLastPathSegment());
                                    messageTextBody.put("type", cheker);
                                    messageTextBody.put("sender", fuser.getUid());
                                    messageTextBody.put("receiver", userid);
                                    messageTextBody.put("messageID", saveCurrentDate1+saveCurrentTime1);
                                    messageTextBody.put("time", saveCurrentTime);
                                    messageTextBody.put("date", saveCurrentDate);
                                    messageTextBody.put("isseen", "false");
                                    messageTextBody.put("title", "sure");
                                    messageTextBody.put("bio", "sure");
                                    messageTextBody.put(sqlId, true);
                                    messageTextBody.put("preMessage", "hello");
                                    messageTextBody.put("last","false");
                                    messageTextBody.put("lastSendMessage", "ms0enhBo1KPFs/nSUHkvpg==\n");


                                    Map messageBodyDetails = new HashMap();
                                    messageBodyDetails.put(messageSenderRef + "/" + saveCurrentDate1+saveCurrentTime1, messageTextBody);
                                    messageBodyDetails.put(messageReceiverRef + "/" + saveCurrentDate1+saveCurrentTime1, messageTextBody);



                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("time", saveCurrentDate1 + "-" + saveCurrentTime1);


                                    RootRef.updateChildren(messageBodyDetails);


                                    reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                                    reference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            User user = dataSnapshot.getValue(User.class);
                                            if (notify) {
                                                sendNotifiaction(userid, user.getUsername(), cheker);
                                            }
                                            notify = false;
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(OnlineMessageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else if (cheker.equals("image")) {
                        notify = true;
                        RootRef = FirebaseDatabase.getInstance().getReference();
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(saveCurrentDate1);

                        final String messageSenderRef = "Chats/" + fuser.getUid() + "/" + userid;
                        final String messageReceiverRef = "Chats/" + userid + "/" + fuser.getUid();


                        DatabaseReference userMessageKeyRef = RootRef.child("Chats")
                                .child(fuser.getUid()).child(userid).push();

                        final String messagePushID = generateString(12);

                        final StorageReference filePath = storageReference.child(messagePushID + "." + "jpg");

                        uploadTask = filePath.putFile(fileUri);
                        uploadTask.continueWithTask(new Continuation() {
                            @Override
                            public Object then(@NonNull Task task) throws Exception {

                                if (!task.isSuccessful()) {
                                    throw task.getException();


                                }
                                return filePath.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {

                                    Uri downloadUrl = task.getResult();
                                    myUrl = downloadUrl.toString();

                                    Calendar calForDate = Calendar.getInstance();
                                    SimpleDateFormat currendateFormat = new SimpleDateFormat("MMM dd");
                                    saveCurrentDate = currendateFormat.format(calForDate.getTime());

                                    Calendar calForTime = Calendar.getInstance();
                                    SimpleDateFormat currenTimeFormat = new SimpleDateFormat("hh:mm a");
                                    saveCurrentTime = currenTimeFormat.format(calForTime.getTime());

                                    Calendar calForDate1 = Calendar.getInstance();
                                    SimpleDateFormat currendateFormat1 = new SimpleDateFormat("yyyyMMdd");
                                    saveCurrentDate1 = currendateFormat1.format(calForDate1.getTime());


                                    Calendar calForTime1 = Calendar.getInstance();
                                    SimpleDateFormat currenTimeFormat1 = new SimpleDateFormat("a hhmmss");
                                    saveCurrentTime1 = currenTimeFormat1.format(calForTime1.getTime());


                                    Map messageTextBody = new HashMap();
                                    messageTextBody.put("message", myUrl);
                                    messageTextBody.put("url", myUrl);
                                    messageTextBody.put("name", fileUri.getLastPathSegment());
                                    messageTextBody.put("type", cheker);
                                    messageTextBody.put("sender", fuser.getUid());
                                    messageTextBody.put("receiver", userid);
                                    messageTextBody.put("messageID", saveCurrentDate1+saveCurrentTime1);
                                    messageTextBody.put("time", saveCurrentTime);
                                    messageTextBody.put("date", saveCurrentDate);
                                    messageTextBody.put("title", "sure");
                                    messageTextBody.put("bio", "sure");
                                    messageTextBody.put("isseen", "false");
                                    messageTextBody.put(sqlId, true);
                                    messageTextBody.put("preMessage", "hello");
                                    messageTextBody.put("last","false");
                                    messageTextBody.put("lastSendMessage", "kt5rg2/r78I081y/kXkhw==\n");


                                    Map messageBodyDetails = new HashMap();

                                    messageBodyDetails.put(messageSenderRef + "/" + saveCurrentDate1+saveCurrentTime1, messageTextBody);
                                    messageBodyDetails.put(messageReceiverRef + "/" + saveCurrentDate1+saveCurrentTime1, messageTextBody);



                                    Toast.makeText(OnlineMessageActivity.this, "multiple image", Toast.LENGTH_SHORT).show();

                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("time", saveCurrentDate1 + "-" + saveCurrentTime1);


                                    RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            if (task.isSuccessful()) {

                                                reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                                                reference.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        User user = dataSnapshot.getValue(User.class);
                                                        if (notify) {
                                                            sendNotifiaction(userid, user.getUsername(), cheker);
                                                        }
                                                        notify = false;
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });

                                            } else {

                                                Toast.makeText(OnlineMessageActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                            }


                                        }
                                    });

                                }
                            }
                        });
                    }

                }

                //Toast.makeText(MainActivity.this, "Selected Multiple Files", Toast.LENGTH_SHORT).show();

            } else

                Toast.makeText(this, "Please Select Multiple Items", Toast.LENGTH_SHORT).show();


        }


        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    text_send.setText(result.get(0));
                }
                break;
        }
        ;
        if (requestCode == 438 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            fileUri = data.getData();


            if (!cheker.equals("image")) {
                notify = true;


                RootRef = FirebaseDatabase.getInstance().getReference();
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(saveCurrentDate1);

                final String messageSenderRef = "Chats/" + fuser.getUid() + "/" + userid;
                final String messageReceiverRef = "Chats/" + userid + "/" + fuser.getUid();

                DatabaseReference userMessageKeyRef = RootRef.child("Chats")
                        .child(fuser.getUid()).child(userid).push();

                final String messagePushID = generateString(12);

                final StorageReference filePath = storageReference.child(messagePushID + "." + "jpg");
                uploadTask = filePath.putFile(fileUri);
                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {

                        if (!task.isSuccessful()) {
                            throw task.getException();


                        }
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(OnlineMessageActivity.this, "upload successfully", Toast.LENGTH_SHORT).show();
                            Uri downloadUrl = task.getResult();
                            myUrl = downloadUrl.toString();

                            Calendar calForDate = Calendar.getInstance();
                            SimpleDateFormat currendateFormat = new SimpleDateFormat("MMM dd");
                            saveCurrentDate = currendateFormat.format(calForDate.getTime());


                            Calendar calForTime = Calendar.getInstance();
                            SimpleDateFormat currenTimeFormat = new SimpleDateFormat("hh:mm a");
                            saveCurrentTime = currenTimeFormat.format(calForTime.getTime());


                            Calendar calForDate1 = Calendar.getInstance();
                            SimpleDateFormat currendateFormat1 = new SimpleDateFormat("yyyyMMdd");
                            saveCurrentDate1 = currendateFormat1.format(calForDate1.getTime());


                            Calendar calForTime1 = Calendar.getInstance();
                            SimpleDateFormat currenTimeFormat1 = new SimpleDateFormat("a hhmmss");
                            saveCurrentTime1 = currenTimeFormat1.format(calForTime1.getTime());



                            HashMap<String, Object> groupMessageKey = new HashMap<>();
                            RootRef.updateChildren(groupMessageKey);


                            Map messageTextBody = new HashMap();



                            messageTextBody.put("message", myUrl);
                            messageTextBody.put("url", myUrl);
                            messageTextBody.put("name", fileUri.getLastPathSegment());
                            messageTextBody.put("type", cheker);
                            messageTextBody.put("sender", fuser.getUid());
                            messageTextBody.put("receiver", userid);
                            messageTextBody.put("messageID", saveCurrentDate1+saveCurrentTime1);
                            messageTextBody.put("time", saveCurrentTime);
                            messageTextBody.put("date", saveCurrentDate);
                            messageTextBody.put("isseen", "false");
                            messageTextBody.put("title", "sure");
                            messageTextBody.put("bio", "sure");
                            messageTextBody.put(sqlId, true);
                            messageTextBody.put("last","false");
                            messageTextBody.put("preMessage", "hello");
                            messageTextBody.put("lastSendMessage", "ms0enhBo1KPFs/nSUHkvpg==\n");


                            Map messageBodyDetails = new HashMap();
                            messageBodyDetails.put(messageSenderRef + "/" + saveCurrentDate1+saveCurrentTime1, messageTextBody);
                            messageBodyDetails.put(messageReceiverRef + "/" + saveCurrentDate1+saveCurrentTime1, messageTextBody);


                            HashMap<String, Object> map = new HashMap<>();
                            map.put("time", saveCurrentDate1 + "-" + saveCurrentTime1);


                            RootRef.updateChildren(messageBodyDetails);


                            reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                            reference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    User user = dataSnapshot.getValue(User.class);
                                    if (notify) {
                                        sendNotifiaction(userid, user.getUsername(), cheker);
                                    }
                                    notify = false;
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(OnlineMessageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            } else if (cheker.equals("image")) {

                notify = true;
                RootRef = FirebaseDatabase.getInstance().getReference();
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(saveCurrentDate1);

                final String messageSenderRef = "Chats/" + fuser.getUid() + "/" + userid;
                final String messageReceiverRef = "Chats/" + userid + "/" + fuser.getUid();


                DatabaseReference userMessageKeyRef = RootRef.child("Chats")
                        .child(fuser.getUid()).child(userid).push();

                final String messagePushID = generateString(12);

                final StorageReference filePath = storageReference.child(messagePushID + "." + "jpg");


                uploadTask = filePath.putFile(fileUri);
                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {

                        if (!task.isSuccessful()) {
                            throw task.getException();


                        }
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {

                            Uri downloadUrl = task.getResult();
                            myUrl = downloadUrl.toString();

                            Calendar calForDate = Calendar.getInstance();
                            SimpleDateFormat currendateFormat = new SimpleDateFormat("MMM dd");
                            saveCurrentDate = currendateFormat.format(calForDate.getTime());

                            Calendar calForTime = Calendar.getInstance();
                            SimpleDateFormat currenTimeFormat = new SimpleDateFormat("hh:mm a");
                            saveCurrentTime = currenTimeFormat.format(calForTime.getTime());


                            Calendar calForDate1 = Calendar.getInstance();
                            SimpleDateFormat currendateFormat1 = new SimpleDateFormat("yyyyMMdd");
                            saveCurrentDate1 = currendateFormat1.format(calForDate1.getTime());


                            Calendar calForTime1 = Calendar.getInstance();
                            SimpleDateFormat currenTimeFormat1 = new SimpleDateFormat("a hhmmss");
                            saveCurrentTime1 = currenTimeFormat1.format(calForTime1.getTime());


                            Map messageTextBody = new HashMap();
                            messageTextBody.put("message", myUrl);
                            messageTextBody.put("url", myUrl);
                            messageTextBody.put("name", fileUri.getLastPathSegment());
                            messageTextBody.put("type", cheker);
                            messageTextBody.put("sender", fuser.getUid());
                            messageTextBody.put("receiver", userid);
                            messageTextBody.put("messageID", saveCurrentDate1+saveCurrentTime1);
                            messageTextBody.put("time", saveCurrentTime);
                            messageTextBody.put("date", saveCurrentDate);
                            messageTextBody.put("title", "sure");
                            messageTextBody.put("bio", "sure");
                            messageTextBody.put("isseen", "false");
                            messageTextBody.put(sqlId, true);
                            messageTextBody.put("last","false");
                            messageTextBody.put("preMessage", "hello");
                            messageTextBody.put("lastSendMessage", "kt5rg2/r78I081y/kXkhw==\n");


                            Map messageBodyDetails = new HashMap();

                            messageBodyDetails.put(messageSenderRef + "/" + saveCurrentDate1+saveCurrentTime1, messageTextBody);
                            messageBodyDetails.put(messageReceiverRef + "/" + saveCurrentDate1+saveCurrentTime1, messageTextBody);

                            HashMap<String, Object> map = new HashMap<>();
                            map.put("time", saveCurrentDate1 + "-" + saveCurrentTime1);


                            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {

                                        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                                        reference.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                User user = dataSnapshot.getValue(User.class);
                                                if (notify) {
                                                    sendNotifiaction(userid, user.getUsername(), cheker);
                                                }
                                                notify = false;
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    } else {

                                        Toast.makeText(OnlineMessageActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                    }


                                }
                            });

                        }
                    }
                });
            } else {

                Toast.makeText(this, "nothing selected, Error.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public void getSpeechInput(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }
    }

    private String encrypt(String Data, String password) throws Exception {
        SecretKeySpec key = generateKey(password);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(Data.getBytes());
        String encryptValue = Base64.encodeToString(encVal, Base64.DEFAULT);
        return encryptValue;

    }

    private SecretKeySpec generateKey(String password) throws Exception {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes("UTF-8");
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AEs");
        return secretKeySpec;

    }

    private String decrypt(String outputString, String password) throws Exception{
        SecretKeySpec key = generateKey(password);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decodeValue = Base64.decode(outputString,Base64.DEFAULT);
        byte[] decvalue = c.doFinal(decodeValue);
        String decryptvalue = new String(decvalue);
        return decryptvalue;

    }

    public class MyDataAdapter extends RecyclerView.Adapter<MessageActivity.MyDataAdapter.MyViewHolder> implements Filterable {

        public static final String ITEM_KEY = "item_key";
        private List<CityDataItem> mDataList;
        private List<CityDataItem> mDataListFull;
        private Context mContext;

        public MyDataAdapter(List<CityDataItem> mDataList, Context mContext) {
            this.mDataList = mDataList;
            this.mContext = mContext;
            mDataListFull=new ArrayList<>();
            mDataListFull.addAll(mDataList);
        }

        @NonNull
        @Override
        public MessageActivity.MyDataAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(mContext).inflate(R.layout.trans_search,parent,false);

            return null;

        }

        @Override
        public void onBindViewHolder(@NonNull MessageActivity.MyDataAdapter.MyViewHolder holder, int position) {

            final CityDataItem cityDataItem=mDataList.get(position);



            holder.ret_hindi.setVisibility(View.VISIBLE);
            holder.ret_hindi.setText(cityDataItem.getHindi());
            holder.ret_english.setText(cityDataItem.getEnglish());




            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notify = true;


                    String msg = text_send.getText().toString();
                    findFriendLis.setVisibility(View.GONE);

                    if (!msg.equals("")){


                        try {
                            msg = encrypt(cityDataItem.getHindi(), inputPassword.getText().toString());
                            trans_msg = encrypt(cityDataItem.getEnglish(), inputPassword.getText().toString());


                        } catch (Exception e) {
                            e.printStackTrace();
                        }



                        try {
                            outputString = decrypt(text_send.getText().toString(), inputPassword.getText().toString());
                        } catch (Exception e) {
                            e.printStackTrace();

                        }

                        text_send.setText("");
                        messageConvert.setText("");

                        String messageId = generateString(12);


                        sendMessage(fuser.getUid(), userid, msg, trans_msg, outputString, messageId,saveCurrentTime,saveCurrentDate,saveCurrentTime1,saveCurrentDate1);
                        sendMessageReciever(fuser.getUid(), userid, msg, trans_msg, messageId,saveCurrentTime,saveCurrentDate,saveCurrentTime1,saveCurrentDate1);



                    } else {
                        Toast.makeText(OnlineMessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                    }







                }
            });


        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }


        public  class MyViewHolder extends RecyclerView.ViewHolder{

            TextView ret_hindi, userStatus,ret_english;

            CircleImageView profileImage;
            View mView;
            public MyViewHolder(View itemView) {
                super(itemView);

                ret_hindi = itemView.findViewById(R.id.hindi);
                ret_english = itemView.findViewById(R.id.english);
                mView=itemView;
            }

        }

        @Override
        public Filter getFilter() {
            return cityDataFilter;
        }

        private Filter cityDataFilter=new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<CityDataItem> filteredList = new ArrayList<>();

                if(constraint == null || constraint.length() == 0){
                    filteredList.addAll(mDataListFull);
                }else {

                    String filter=constraint.toString().toLowerCase().trim();

                    for(CityDataItem dataItem:mDataListFull){
                        if(dataItem.getHindi().toLowerCase().contains(filter)){
                            filteredList.add(dataItem);
                        }
                    }
                }
                FilterResults results=new FilterResults();
                results.values = filteredList;

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mDataList.clear();
                mDataList.addAll((Collection<? extends CityDataItem>) results.values);
                notifyDataSetChanged();
            }
        };

    }

    private String generateString(int lenth) {
        char[] chasr = "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm".toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < lenth; i++) {
            char c = chasr[random.nextInt(chasr.length)];
            stringBuilder.append(c);


        }
        return stringBuilder.toString();
    }

    private void sendMessage(String sender, final String receiver, String message, String uid, String fuserUid, String messageId,String saveCurrentTime,String saveCurrentDate,String saveCurrentTime1,String saveCurrentDate1) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();


        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("time", saveCurrentTime);
        hashMap.put("date", saveCurrentDate);
        hashMap.put("messageID", saveCurrentDate1+saveCurrentTime1);
        hashMap.put("sender", fuser.getUid());
        hashMap.put("receiver", userid);
        hashMap.put("isseen", "false");
        hashMap.put("message", message);
        hashMap.put("bio", uid);
        hashMap.put(sqlId, true);
        hashMap.put("type", "text");
        hashMap.put("preMessage", "hello");
        hashMap.put("last","false");
        hashMap.put("lastSendMessage", message);


        HashMap<String, Object> map = new HashMap<>();
        map.put("time", saveCurrentDate1 + "-" + saveCurrentTime1);


        reference.child("Chats").child(fuser.getUid()).child(userid).child(saveCurrentDate1+saveCurrentTime1).setValue(hashMap);


        final String msg = message;

        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (notify) {
                    sendNotifiaction(receiver, user.getUsername(), fuserUid);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessageReciever(String sender, final String receiver, String message, String uid, String messageId,String saveCurrentTime,String saveCurrentDate,String saveCurrentTime1,String saveCurrentDate1) {


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats").child(receiver).child(sender);


        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("time", saveCurrentTime);
        hashMap.put("date", saveCurrentDate);
        hashMap.put("messageID", saveCurrentDate1+saveCurrentTime1);
        hashMap.put("sender", fuser.getUid());
        hashMap.put("receiver", userid);
        hashMap.put("isseen", "false");
        hashMap.put("message", message);
        hashMap.put("bio", uid);
        hashMap.put(sqlId, true);
        hashMap.put("type", "text");
        hashMap.put("last","false");
        hashMap.put("preMessage", "hello");
        hashMap.put("lastSendMessage", message);


        HashMap<String, Object> map = new HashMap<>();
        map.put("time", saveCurrentDate1 + "-" + saveCurrentTime1);


        reference.child(saveCurrentDate1+saveCurrentTime1).setValue(hashMap);

    }



    public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

        private List<SqlMessage> mChat;
        private Context mContext;
        private RecyclerView mRecyclerV;
        SimpleExoPlayer exoPlayer;
        boolean vidioplay = false;
        String AES = "AES";
        String outputString, convertString;
        ValueEventListener seenListener;
        private int current = 0;
        private int duration = 0;
        private SparseBooleanArray selected_items;
        private int current_selected_idx = -1;
        private FirebaseAuth mAuth;
        public static final int MSG_TYPE_LEFT = 0;
        public static final int MSG_TYPE_RIGHT = 1;
        private String imageurl;
        FirebaseUser fuser;


        public MessageAdapter(Context mContext, List<SqlMessage> mChat) {
            this.mChat = mChat;
            this.mContext = mContext;

        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == MSG_TYPE_RIGHT) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
                return new ViewHolder(view);
            } else {
                View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
                return new ViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            mAuth = FirebaseAuth.getInstance();
            final String messageSenderId = mAuth.getCurrentUser().getUid();
            final SqlMessage chat = mChat.get(position);

            String fromMessageType = chat.getType();
            String fromUserID = chat.getSender();
            String messageId = chat.getMessageID();
            holder.show_message.setText(chat.getMessage());


            holder.pdfName.setVisibility(View.GONE);
            holder.textTimeDate.setVisibility(View.GONE);
            holder.imageTimeDate.setVisibility(View.GONE);
            holder.pdfTimeDate.setVisibility(View.GONE);
            holder.videoTimeDate.setVisibility(View.GONE);
            holder.linearpdf.setVisibility(View.GONE);
            holder.linearName.setVisibility(View.GONE);
            holder.show_message.setVisibility(View.GONE);
            holder.show_image.setVisibility(View.GONE);
            holder.linearimage.setVisibility(View.GONE);
            holder.linearpdf.setVisibility(View.GONE);
            holder.show_pdf.setVisibility(View.GONE);
            holder.linearText.setVisibility(View.GONE);
            holder.linearVideo.setVisibility(View.GONE);
            holder.text_show_password.setVisibility(View.GONE);
            holder.password.setVisibility(View.GONE);
            holder.password_ok.setVisibility(View.GONE);
            holder.password_null.setVisibility(View.GONE);
            holder.pause.setVisibility(View.GONE);
            holder.play.setVisibility(View.GONE);
            holder.download_pdf.setVisibility(View.GONE);
            holder.reply_image.setVisibility(View.GONE);
            holder.reply_pdf.setVisibility(View.GONE);
            holder.reply_video.setVisibility(View.GONE);
            holder.reply_text.setVisibility(View.GONE);

            holder.rep_text.setVisibility(View.GONE);
            holder.rep_image.setVisibility(View.GONE);
            holder.rep_video.setVisibility(View.GONE);
            holder.rep_pdf.setVisibility(View.GONE);

            holder.rep_chat_text.setVisibility(View.GONE);
            holder.rep_chat_image.setVisibility(View.GONE);
            holder.rep_chat_video.setVisibility(View.GONE);
            holder.rep_chat_pdf.setVisibility(View.GONE);

            holder.rep_message_text.setVisibility(View.GONE);
            holder.rep_message_image.setVisibility(View.GONE);
            holder.rep_message_video.setVisibility(View.GONE);
            holder.rep_message_pdf.setVisibility(View.GONE);
            holder.rep_pdf_name.setVisibility(View.GONE);
            holder.password_reply_ok.setVisibility(View.GONE);
            holder.id_new.setVisibility(View.GONE);


            holder.single_check_text_one.setVisibility(View.GONE);
            holder.single_check_text_two.setVisibility(View.GONE);
            holder.single_check_text_three.setVisibility(View.GONE);
            holder.single_check_text_four.setVisibility(View.GONE);
            holder.single_check_text_five.setVisibility(View.GONE);
            holder.single_check_image_one.setVisibility(View.GONE);
            holder.single_check_image_two.setVisibility(View.GONE);
            holder.single_check_image_three.setVisibility(View.GONE);
            holder.single_check_image_four.setVisibility(View.GONE);
            holder.single_check_image_five.setVisibility(View.GONE);
            holder.single_check_video_one.setVisibility(View.GONE);
            holder.single_check_video_two.setVisibility(View.GONE);
            holder.single_check_video_three.setVisibility(View.GONE);
            holder.single_check_video_four.setVisibility(View.GONE);
            holder.single_check_video_five.setVisibility(View.GONE);
            holder.single_check_pdf_one.setVisibility(View.GONE);
            holder.single_check_pdf_two.setVisibility(View.GONE);
            holder.single_check_pdf_three.setVisibility(View.GONE);
            holder.single_check_pdf_four.setVisibility(View.GONE);
            holder.single_check_pdf_five.setVisibility(View.GONE);
            holder.single_check_reply_text_one.setVisibility(View.GONE);
            holder.single_check_reply_text_two.setVisibility(View.GONE);
            holder.single_check_reply_text_three.setVisibility(View.GONE);
            holder.single_check_reply_text_four.setVisibility(View.GONE);
            holder.single_check_reply_text_five.setVisibility(View.GONE);
            holder.single_check_reply_image_one.setVisibility(View.GONE);
            holder.single_check_reply_image_two.setVisibility(View.GONE);
            holder.single_check_reply_image_three.setVisibility(View.GONE);
            holder.single_check_reply_image_four.setVisibility(View.GONE);
            holder.single_check_reply_image_five.setVisibility(View.GONE);
            holder.single_check_reply_pdf_one.setVisibility(View.GONE);
            holder.single_check_reply_pdf_two.setVisibility(View.GONE);
            holder.single_check_reply_pdf_three.setVisibility(View.GONE);
            holder.single_check_reply_pdf_four.setVisibility(View.GONE);
            holder.single_check_reply_pdf_five.setVisibility(View.GONE);
            holder.single_check_reply_video_one.setVisibility(View.GONE);
            holder.single_check_reply_video_two.setVisibility(View.GONE);
            holder.single_check_reply_video_three.setVisibility(View.GONE);
            holder.single_check_reply_video_four.setVisibility(View.GONE);
            holder.single_check_reply_video_five.setVisibility(View.GONE);


            if (fromMessageType.equals("text")) {
                if (fromUserID.equals(messageSenderId)) {
                    holder.linearText.setVisibility(View.VISIBLE);
                    holder.text_show_password.setVisibility(View.VISIBLE);
                    holder.reply_text.setVisibility(View.VISIBLE);
                    holder.textTimeDate.setVisibility(View.VISIBLE);
                    holder.textTimeDate.setText(chat.getTime());


                    if (chat.getIsseen().equals("true")) {
                        holder.single_check_text_one.setVisibility(View.VISIBLE);
                        holder.single_check_text_two.setVisibility(View.VISIBLE);
                    } else if (chat.getIsseen().equals("false")) {
                        holder.single_check_text_three.setVisibility(View.VISIBLE);
                        holder.single_check_text_four.setVisibility(View.VISIBLE);

                    } else {
                        holder.single_check_text_five.setVisibility(View.VISIBLE);
                    }

                    //   lastMessage(holder.txt_seen);


                    if (chat.getBio().equals("ol6lgGovgJomq4QGS6hThA==\n")) {

                        try {
                            outputString = decrypt(chat.getMessage(), holder.password_null.getText().toString());
                        } catch (Exception e) {
                            e.printStackTrace();

                        }

                        holder.text_show_password.setText(outputString);

                    } else {
                        try {
                            convertString = decrypt(chat.getBio(), holder.password_null.getText().toString());
                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                        holder.text_show_password.setText(convertString);
                    }


                    holder.reply_text.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            try {
                                outputString = decrypt(chat.getMessage(), holder.password_null.getText().toString());
                            } catch (Exception e) {
                                e.printStackTrace();

                            }

                            relative_layout_message.setVisibility(View.GONE);
                            reply_bottom.setVisibility(View.VISIBLE);

                            reply_preview_message.setText(outputString);

                            type.setText("reply_text");

                        }
                    });

                    holder.linearText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            try {
                                outputString = decrypt(chat.getMessage(), holder.password_null.getText().toString());
                            } catch (Exception e) {
                                e.printStackTrace();

                            }


                            holder.linearText.setVisibility(View.VISIBLE);
                            holder.textTimeDate.setVisibility(View.VISIBLE);
                            holder.textTimeDate.setText(chat.getTime());
                            holder.show_message.setVisibility(View.VISIBLE);
                            holder.show_message.setText(outputString);
                            holder.password.setVisibility(View.VISIBLE);
                            holder.password_ok.setVisibility(View.VISIBLE);

                        }
                    });

                    holder.password_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                outputString = decrypt(chat.getMessage(), holder.password.getText().toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            holder.text_show_password.setText(outputString);


                            try {
                                convertString = decrypt(chat.getBio(), holder.password.getText().toString());
                            } catch (Exception e) {
                                e.printStackTrace();

                            }
                            holder.show_message.setVisibility(View.VISIBLE);
                            holder.show_message.setText(convertString);

                        }
                    });

                    holder.linearText.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {

                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
                            builder.setTitle("Choose option");
                            builder.setMessage("delete Message... ?");
                            builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.setNeutralButton("Everyone", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    deleteMessageForEveryone(position, holder);

                                }
                            });
                            builder.setNegativeButton("Me", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteSentMessage(position, holder);
                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();
                            return true;
                        }
                    });


                } else {

                    holder.linearText.setVisibility(View.VISIBLE);
                    holder.text_show_password.setVisibility(View.VISIBLE);
                    holder.reply_text.setVisibility(View.VISIBLE);


                    if (chat.getIsseen().equals("truesdd")) {
                        holder.id_new.setVisibility(View.VISIBLE);
                    } else {
                        holder.id_new.setVisibility(View.GONE);
                    }


                    if (chat.getBio().equals("ol6lgGovgJomq4QGS6hThA==\n")) {
                        try {
                            outputString = decrypt(chat.getMessage(), holder.password_null.getText().toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        holder.text_show_password.setText(outputString);
                    } else {
                        try {
                            convertString = decrypt(chat.getBio(), holder.password_null.getText().toString());
                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                        holder.text_show_password.setText(convertString);
                    }

                    holder.reply_text.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            try {
                                outputString = decrypt(chat.getMessage(), holder.password_null.getText().toString());
                            } catch (Exception e) {
                                e.printStackTrace();

                            }

                            relative_layout_message.setVisibility(View.GONE);
                            reply_bottom.setVisibility(View.VISIBLE);

                            reply_preview_message.setText(outputString);

                            type.setText("reply_text");

                        }
                    });

                    holder.linearText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.linearText.setVisibility(View.VISIBLE);
                            holder.textTimeDate.setVisibility(View.VISIBLE);
                            holder.textTimeDate.setText(chat.getDate() + "  -  " + chat.getTime());


                            try {
                                outputString = decrypt(chat.getMessage(), holder.password_null.getText().toString());
                            } catch (Exception e) {
                                e.printStackTrace();

                            }


                            holder.show_message.setVisibility(View.VISIBLE);
                            holder.show_message.setText(outputString);
                            holder.password.setVisibility(View.VISIBLE);
                            holder.password_ok.setVisibility(View.VISIBLE);


                        }
                    });
                    holder.password_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                outputString = decrypt(chat.getMessage(), holder.password.getText().toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                            holder.show_message.setVisibility(View.VISIBLE);
                            holder.show_message.setText(outputString);
                        }
                    });

                    holder.linearText.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {

                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
                            builder.setTitle("Choose option");
                            builder.setMessage("delete Message... ?");
                            builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();
                                }
                            });
                            builder.setNeutralButton("delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteReceiveMessage(position, holder);
                                     dialog.dismiss();

                                }
                            });
                            builder.setNegativeButton("", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            builder.create().show();
                            return true;
                        }
                    });

                }

            } else if (fromMessageType.equals("image")) {

                if (fromUserID.equals(messageSenderId)) {
                    holder.show_image.setVisibility(View.VISIBLE);
                    holder.linearimage.setVisibility(View.VISIBLE);
                    holder.imageTimeDate.setVisibility(View.VISIBLE);
                    holder.imageTimeDate.setText(chat.getDate() + "  -  " + chat.getTime());
                    Picasso.get().load(chat.getImagrUrl()).into(holder.show_image);


                    if (chat.getIsseen().equals("true")) {
                        holder.single_check_image_one.setVisibility(View.VISIBLE);
                        holder.single_check_image_two.setVisibility(View.VISIBLE);
                    } else if (chat.getIsseen().equals("false")) {

                        holder.single_check_image_three.setVisibility(View.VISIBLE);
                        holder.single_check_image_four.setVisibility(View.VISIBLE);


                    } else {
                        holder.single_check_image_five.setVisibility(View.VISIBLE);
                    }


                    holder.reply_image.setVisibility(View.VISIBLE);
                    holder.reply_image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            relative_layout_message.setVisibility(View.GONE);
                            reply_bottom.setVisibility(View.VISIBLE);

                            reply_preview_message.setText(chat.getMessage());

                            type.setText("reply_image");

                            Picasso.get().load(chat.getMessage()).into(reply_image);

                        }
                    });

                    holder.show_image.setOnClickListener(new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onClick(View v) {


                            Intent chatIntent = new Intent(holder.itemView.getContext(), ImageViewActivity.class);
                            chatIntent.putExtra("url", chat.getMessage());


                            Pair[] pairs = new Pair[1];
                            pairs[0] = new Pair<View, String>(holder.show_image, "imageTransition");

                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) holder.itemView.getContext(), pairs);


                            holder.itemView.getContext().startActivity(chatIntent, options.toBundle());


                        }
                    });

                    holder.show_image.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
                            builder.setTitle("Choose option");
                            builder.setMessage("delete Message... ?");
                            builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();
                                }
                            });
                            builder.setNeutralButton("Everyone", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    deleteMessageForEveryone(position, holder);
                                }
                            });
                            builder.setNegativeButton("Me", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteSentMessage(position, holder);
                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();
                            return true;
                        }
                    });


                } else {

                    holder.show_image.setVisibility(View.VISIBLE);
                    holder.linearimage.setVisibility(View.VISIBLE);
                    holder.imageTimeDate.setVisibility(View.VISIBLE);
                    holder.imageTimeDate.setText(chat.getDate() + "  -  " + chat.getTime());
                    Picasso.get().load(chat.getMessage()).into(holder.show_image);

                    if (chat.getIsseen().equals("false")) {
                        holder.id_new.setVisibility(View.VISIBLE);
                    } else {
                        holder.id_new.setVisibility(View.GONE);
                    }


                    holder.reply_image.setVisibility(View.VISIBLE);
                    holder.reply_image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            relative_layout_message.setVisibility(View.GONE);
                            reply_bottom.setVisibility(View.VISIBLE);

                            reply_preview_message.setText(chat.getMessage());

                            type.setText("reply_image");

                            Picasso.get().load(chat.getMessage()).into(reply_image);

                        }
                    });


                    holder.show_image.setOnClickListener(new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onClick(View v) {


                            Intent chatIntent = new Intent(holder.itemView.getContext(), ImageViewActivity.class);
                            chatIntent.putExtra("url", mChat.get(position).getMessage());


                            Pair[] pairs = new Pair[1];
                            pairs[0] = new Pair<View, String>(holder.show_image, "imageTransition");

                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) holder.itemView.getContext(), pairs);


                            holder.itemView.getContext().startActivity(chatIntent, options.toBundle());


                        }
                    });


                    holder.show_image.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {

                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
                            builder.setTitle("Choose option");
                            builder.setMessage("delete Message... ?");
                            builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.setNeutralButton("delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteReceiveMessage(position, holder);

                                    dialog.dismiss();

                                }
                            });
                            builder.setNegativeButton("", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            builder.create().show();
                            return true;
                        }
                    });

                }
            } else if (fromMessageType.equals("video")) {
                if (fromUserID.equals(messageSenderId)) {

                    holder.linearVideo.setVisibility(View.VISIBLE);
                    holder.videoTimeDate.setVisibility(View.VISIBLE);
                    holder.videoTimeDate.setText(chat.getDate() + "  -  " + chat.getTime());
                    holder.show_video.setVisibility(View.VISIBLE);
                    holder.reply_video.setVisibility(View.VISIBLE);


                    if (chat.getIsseen().equals("true")) {
                        holder.single_check_video_one.setVisibility(View.VISIBLE);
                        holder.single_check_video_two.setVisibility(View.VISIBLE);
                    } else if (chat.getIsseen().equals("false")) {
                        holder.single_check_video_three.setVisibility(View.VISIBLE);
                        holder.single_check_video_four.setVisibility(View.VISIBLE);
                    } else {
                        holder.single_check_video_five.setVisibility(View.VISIBLE);
                    }

                    long interval = 1000;
                    RequestOptions options = new RequestOptions().frame(interval);

                    Glide.with(mContext).asBitmap().load(chat.getImagrUrl()).apply(options).into(holder.show_video);


                    holder.reply_video.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            relative_layout_message.setVisibility(View.GONE);
                            reply_bottom.setVisibility(View.VISIBLE);

                            reply_preview_message.setText(mChat.get(position).getMessage());

                            type.setText("reply_video");

                            Picasso.get().load(chat.getMessage()).into(reply_image);

                        }
                    });


                    holder.linearVideo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(holder.itemView.getContext(), DirectVideo.class);
                            intent.putExtra("visit_user_id", chat.getReceiver());
                            intent.putExtra("messageId", chat.getMessage());
                            holder.itemView.getContext().startActivity(intent);
                        }
                    });


                    holder.linearVideo.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
                            builder.setTitle("Choose option");
                            builder.setMessage("delete Message... ?");
                            builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();
                                }
                            });
                            builder.setNeutralButton("Everyone", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    deleteMessageForEveryone(position, holder);


                                }
                            });
                            builder.setNegativeButton("Me", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteSentMessage(position, holder);
                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();
                            return true;
                        }
                    });


                } else {

                    holder.linearVideo.setVisibility(View.VISIBLE);
                    holder.videoTimeDate.setVisibility(View.VISIBLE);
                    holder.videoTimeDate.setText(chat.getDate() + "  -  " + chat.getTime());
                    holder.show_video.setVisibility(View.VISIBLE);
                    holder.reply_video.setVisibility(View.VISIBLE);


                    if (chat.getIsseen().equals("false")) {
                        holder.id_new.setVisibility(View.VISIBLE);
                    } else {
                        holder.id_new.setVisibility(View.GONE);
                    }


                    long interval = 1000;
                    RequestOptions options = new RequestOptions().frame(interval);

                    Glide.with(mContext).asBitmap().load(chat.getImagrUrl()).apply(options).into(holder.show_video);


                    holder.reply_video.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            relative_layout_message.setVisibility(View.GONE);
                            reply_bottom.setVisibility(View.VISIBLE);

                            reply_preview_message.setText(chat.getMessage());

                            type.setText("reply_video");

                            Picasso.get().load(chat.getMessage()).into(reply_image);

                        }
                    });


                    holder.linearVideo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(holder.itemView.getContext(), DirectVideo.class);
                            intent.putExtra("visit_user_id", chat.getReceiver());
                            intent.putExtra("messageId", chat.getMessage());
                            holder.itemView.getContext().startActivity(intent);

                        }
                    });

                    holder.linearVideo.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {

                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
                            builder.setTitle("Choose option");
                            builder.setMessage("delete Message... ?");
                            builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();
                                }
                            });
                            builder.setNeutralButton("delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteReceiveMessage(position, holder);
                                    dialog.dismiss();

                                }
                            });
                            builder.setNegativeButton("", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            builder.create().show();
                            return true;
                        }
                    });

                }
            } else if (fromMessageType.equals("PDF") || (fromMessageType.equals("docx"))) {
                {
                    if (fromUserID.equals(messageSenderId)) {
                        holder.linearpdf.setVisibility(View.VISIBLE);
                        holder.pdfName.setVisibility(View.VISIBLE);
                        holder.pdfName.setText(chat.getPreMessage());
                        holder.linearName.setVisibility(View.VISIBLE);
                        holder.show_pdf.setVisibility(View.VISIBLE);
                        holder.pdfTimeDate.setVisibility(View.VISIBLE);
                        holder.pdfTimeDate.setText(chat.getDate() + "  -  " + chat.getTime());
                        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/watsapp-6d8e6.appspot.com/o/file.png?alt=media&token=da730f2c-5760-4d67-b45b-b8ff497039b6")
                                .into(holder.show_pdf);



                        if (chat.getIsseen().equals("true")) {
                            holder.single_check_pdf_one.setVisibility(View.VISIBLE);
                            holder.single_check_pdf_two.setVisibility(View.VISIBLE);
                        } else if (chat.getIsseen().equals("false")) {
                            holder.single_check_pdf_three.setVisibility(View.VISIBLE);
                            holder.single_check_pdf_four.setVisibility(View.VISIBLE);
                        } else {
                            holder.single_check_pdf_five.setVisibility(View.VISIBLE);
                        }

                        holder.download_pdf.setVisibility(View.GONE);


                        holder.linearpdf.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.download_pdf.setVisibility(View.VISIBLE);
                            }
                        });

                        holder.download_pdf.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mChat.get(position).getMessage()));
                                holder.itemView.getContext().startActivity(intent);

                            }
                        });

                        holder.reply_pdf.setVisibility(View.VISIBLE);
                        holder.reply_pdf.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                relative_layout_message.setVisibility(View.GONE);
                                reply_bottom.setVisibility(View.VISIBLE);

                                reply_preview_message.setText(chat.getPreMessage() + ".pdf");

                                type.setText("reply_pdf");


                                Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/watsapp-6d8e6.appspot.com/o/file.png?alt=media&token=da730f2c-5760-4d67-b45b-b8ff497039b6")
                                        .into(reply_image);


                            }
                        });


                        holder.linearpdf.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {

                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
                                builder.setTitle("Choose option");
                                builder.setMessage("delete Message... ?");
                                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        dialog.dismiss();
                                    }
                                });
                                builder.setNeutralButton("Everyone", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        deleteMessageForEveryone(position, holder);


                                    }
                                });
                                builder.setNegativeButton("Me", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        deleteSentMessage(position, holder);
                                        dialog.dismiss();
                                    }
                                });
                                builder.create().show();
                                return true;
                            }
                        });


                    } else {
                        holder.linearName.setVisibility(View.VISIBLE);
                        holder.pdfName.setVisibility(View.VISIBLE);
                        holder.pdfName.setText(chat.getPreMessage());
                        holder.linearpdf.setVisibility(View.VISIBLE);
                        holder.pdfTimeDate.setVisibility(View.VISIBLE);
                        holder.pdfTimeDate.setText(chat.getDate() + "  -  " + chat.getTime());
                        holder.show_pdf.setVisibility(View.VISIBLE);
                        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/watsapp-6d8e6.appspot.com/o/file.png?alt=media&token=da730f2c-5760-4d67-b45b-b8ff497039b6")
                                .into(holder.show_pdf);

                        if (chat.getIsseen().equals("false")) {
                            holder.id_new.setVisibility(View.VISIBLE);
                        } else {
                            holder.id_new.setVisibility(View.GONE);
                        }


                        holder.reply_pdf.setVisibility(View.VISIBLE);
                        holder.reply_pdf.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                relative_layout_message.setVisibility(View.GONE);
                                reply_bottom.setVisibility(View.VISIBLE);

                                reply_preview_message.setText(chat.getPreMessage() + ".Pdf");

                                type.setText("reply_pdf");

                                Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/watsapp-6d8e6.appspot.com/o/file.png?alt=media&token=da730f2c-5760-4d67-b45b-b8ff497039b6")
                                        .into(reply_image);


                            }
                        });


                        holder.linearpdf.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.download_pdf.setVisibility(View.VISIBLE);
                            }
                        });

                        holder.download_pdf.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mChat.get(position).getMessage()));
                                holder.itemView.getContext().startActivity(intent);

                            }
                        });


                        holder.linearpdf.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {

                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
                                builder.setTitle("Choose option");
                                builder.setMessage("delete Message... ?");
                                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        dialog.dismiss();
                                    }
                                });
                                builder.setNeutralButton("delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        deleteReceiveMessage(position, holder);
                                        dialog.dismiss();

                                    }
                                });
                                builder.setNegativeButton("", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                                builder.create().show();
                                return true;
                            }
                        });


                    }
                }
            } else if (fromMessageType.equals("reply_text")) {
                if (fromUserID.equals(messageSenderId)) {

                    holder.rep_message_text.setVisibility(View.VISIBLE);
                    holder.rep_chat_text.setVisibility(View.VISIBLE);
                    holder.rep_text.setVisibility(View.VISIBLE);


                    if (chat.getIsseen().equals("true")) {
                        holder.single_check_reply_text_one.setVisibility(View.VISIBLE);
                        holder.single_check_reply_text_two.setVisibility(View.VISIBLE);
                    } else if (chat.getIsseen().equals("false")) {
                        holder.single_check_reply_text_three.setVisibility(View.VISIBLE);
                        holder.single_check_reply_text_four.setVisibility(View.VISIBLE);
                    } else {
                        holder.single_check_reply_text_five.setVisibility(View.VISIBLE);
                    }

                    try {
                        outputString = decrypt(chat.getMessage(), holder.password_null.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();

                    }

                    holder.rep_message_text.setText(chat.getPreMessage());
                    holder.rep_chat_text.setText(outputString);


                    holder.rep_text.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.password_reply_text.setVisibility(View.VISIBLE);
                            holder.password_reply_ok.setVisibility(View.VISIBLE);

                        }
                    });

                    holder.password_reply_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            holder.password_reply_text.setVisibility(View.VISIBLE);

                            try {
                                outputString = decrypt(chat.getMessage(), holder.password_reply_text.getText().toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            holder.rep_chat_text.setText(outputString);

                        }
                    });

                    holder.rep_text.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {

                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
                            builder.setTitle("Choose option");
                            builder.setMessage("delete Message... ?");
                            builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();
                                }
                            });
                            builder.setNeutralButton("Everyone", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    deleteMessageForEveryone(position, holder);


                                }
                            });
                            builder.setNegativeButton("Me", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteSentMessage(position, holder);
                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();
                            return true;
                        }
                    });


                } else {
                    holder.rep_message_text.setVisibility(View.VISIBLE);
                    holder.rep_chat_text.setVisibility(View.VISIBLE);
                    holder.rep_text.setVisibility(View.VISIBLE);

                    if (chat.getIsseen().equals("false")) {
                        holder.id_new.setVisibility(View.VISIBLE);
                    } else {
                        holder.id_new.setVisibility(View.GONE);
                    }

                    try {
                        outputString = decrypt(chat.getMessage(), holder.password_null.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();

                    }


                    holder.rep_message_text.setText(chat.getPreMessage());
                    holder.rep_chat_text.setText(outputString);


                    holder.rep_text.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.password_reply_text.setVisibility(View.VISIBLE);
                            holder.password_reply_ok.setVisibility(View.VISIBLE);


                        }
                    });

                    holder.password_reply_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            holder.password_reply_text.setVisibility(View.VISIBLE);

                            try {
                                outputString = decrypt(chat.getMessage(), holder.password_reply_text.getText().toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            holder.rep_chat_text.setText(outputString);

                        }
                    });

                    holder.rep_text.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
                            builder.setTitle("Choose option");
                            builder.setMessage("delete Message... ?");
                            builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();
                                }
                            });
                            builder.setNeutralButton("delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteReceiveMessage(position, holder);
                                    dialog.dismiss();

                                }
                            });
                            builder.setNegativeButton("", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            builder.create().show();
                            return true;
                        }
                    });

                }
            } else if (fromMessageType.equals("reply_image")) {
                if (fromUserID.equals(messageSenderId)) {

                    holder.rep_message_image.setVisibility(View.VISIBLE);
                    holder.rep_chat_image.setVisibility(View.VISIBLE);
                    holder.rep_image.setVisibility(View.VISIBLE);


                    if (chat.getIsseen().equals("true")) {
                        holder.single_check_reply_image_one.setVisibility(View.VISIBLE);
                        holder.single_check_reply_image_two.setVisibility(View.VISIBLE);
                    } else if (chat.getIsseen().equals("false")) {
                        holder.single_check_reply_image_three.setVisibility(View.VISIBLE);
                        holder.single_check_reply_image_four.setVisibility(View.VISIBLE);
                    } else {
                        holder.single_check_reply_image_five.setVisibility(View.VISIBLE);
                    }

                    try {
                        outputString = decrypt(chat.getMessage(), holder.password_null.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();

                    }


                    Picasso.get().load(chat.getPreMessage()).into(holder.rep_message_image);
                    holder.rep_chat_image.setText(outputString);


                    holder.rep_message_image.setOnClickListener(new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onClick(View v) {


                            Intent chatIntent = new Intent(holder.itemView.getContext(), ImageViewActivity.class);
                            chatIntent.putExtra("url", mChat.get(position).getPreMessage());


                            Pair[] pairs = new Pair[1];
                            pairs[0] = new Pair<View, String>(holder.rep_message_image, "imageTransition");

                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) holder.itemView.getContext(), pairs);


                            holder.itemView.getContext().startActivity(chatIntent, options.toBundle());


                        }
                    });


                    holder.rep_image.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {

                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
                            builder.setTitle("Choose option");
                            builder.setMessage("delete Message... ?");
                            builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();
                                }
                            });
                            builder.setNeutralButton("Everyone", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    deleteMessageForEveryone(position, holder);


                                }
                            });
                            builder.setNegativeButton("Me", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteSentMessage(position, holder);
                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();
                            return true;
                        }
                    });


                } else {
                    holder.rep_message_image.setVisibility(View.VISIBLE);
                    holder.rep_chat_image.setVisibility(View.VISIBLE);
                    holder.rep_image.setVisibility(View.VISIBLE);


                    if (chat.getIsseen().equals("false")) {
                        holder.id_new.setVisibility(View.VISIBLE);
                    } else {
                        holder.id_new.setVisibility(View.GONE);
                    }


                    try {
                        outputString = decrypt(chat.getMessage(), holder.password_null.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();

                    }


                    Picasso.get().load(chat.getPreMessage()).into(holder.rep_message_image);
                    holder.rep_chat_image.setText(outputString);


                    holder.rep_message_image.setOnClickListener(new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onClick(View v) {


                            Intent chatIntent = new Intent(holder.itemView.getContext(), ImageViewActivity.class);
                            chatIntent.putExtra("url", chat.getPreMessage());


                            Pair[] pairs = new Pair[1];
                            pairs[0] = new Pair<View, String>(holder.rep_message_image, "imageTransition");

                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) holder.itemView.getContext(), pairs);


                            holder.itemView.getContext().startActivity(chatIntent, options.toBundle());


                        }
                    });


                    holder.rep_image.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
                            builder.setTitle("Choose option");
                            builder.setMessage("delete Message... ?");
                            builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();
                                }
                            });
                            builder.setNeutralButton("delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteReceiveMessage(position, holder);
                                    dialog.dismiss();

                                }
                            });
                            builder.setNegativeButton("", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            builder.create().show();
                            return true;
                        }


                    });
                }
            } else if (fromMessageType.equals("reply_video")) {
                if (fromUserID.equals(messageSenderId)) {
                    holder.rep_message_video.setVisibility(View.VISIBLE);
                    holder.rep_chat_video.setVisibility(View.VISIBLE);
                    holder.rep_video.setVisibility(View.VISIBLE);



                    if (chat.getIsseen().equals("true")) {
                        holder.single_check_reply_video_one.setVisibility(View.VISIBLE);
                        holder.single_check_reply_video_two.setVisibility(View.VISIBLE);
                    } else if (chat.getIsseen().equals("false")) {
                        holder.single_check_reply_video_three.setVisibility(View.VISIBLE);
                        holder.single_check_reply_video_four.setVisibility(View.VISIBLE);
                    } else {
                        holder.single_check_reply_video_five.setVisibility(View.VISIBLE);
                    }
                    try {
                        outputString = decrypt(chat.getMessage(), holder.password_null.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    long interval = 1000;
                    RequestOptions options = new RequestOptions().frame(interval);

                    Glide.with(mContext).asBitmap().load(chat.getPreMessage()).apply(options).into(holder.rep_message_video);

                    holder.rep_chat_video.setText(outputString);

                    holder.rep_text.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {

                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
                            builder.setTitle("Choose option");
                            builder.setMessage("delete Message... ?");
                            builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();
                                }
                            });
                            builder.setNeutralButton("Everyone", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    deleteMessageForEveryone(position, holder);


                                }
                            });
                            builder.setNegativeButton("Me", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteSentMessage(position, holder);
                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();
                            return true;
                        }
                    });


                } else {
                    holder.rep_message_video.setVisibility(View.VISIBLE);
                    holder.rep_chat_video.setVisibility(View.VISIBLE);
                    holder.rep_video.setVisibility(View.VISIBLE);


                    if (chat.getIsseen().equals("false")) {
                        holder.id_new.setVisibility(View.VISIBLE);
                    } else {
                        holder.id_new.setVisibility(View.GONE);
                    }

                    try {
                        outputString = decrypt(chat.getMessage(), holder.password_null.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    holder.rep_chat_video.setText(outputString);

                    long interval = 1000;
                    RequestOptions options = new RequestOptions().frame(interval);

                    Glide.with(mContext).asBitmap().load(chat.getPreMessage()).apply(options).into(holder.rep_message_video);


                    holder.rep_video.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
                            builder.setTitle("Choose option");
                            builder.setMessage("delete Message... ?");
                            builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();
                                }
                            });
                            builder.setNeutralButton("delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteReceiveMessage(position, holder);

                                    dialog.dismiss();

                                }
                            });
                            builder.setNegativeButton("", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            builder.create().show();
                            return true;
                        }
                    });

                }

            } else if (fromMessageType.equals("reply_pdf")) {
                if (fromUserID.equals(messageSenderId)) {
                    holder.rep_message_pdf.setVisibility(View.VISIBLE);
                    holder.rep_chat_pdf.setVisibility(View.VISIBLE);
                    holder.rep_pdf.setVisibility(View.VISIBLE);
                    holder.rep_pdf_name.setVisibility(View.VISIBLE);
                    holder.rep_pdf_name.setText(chat.getPreMessage());



                    if (chat.getIsseen().equals("true")) {
                        holder.single_check_reply_pdf_one.setVisibility(View.VISIBLE);
                        holder.single_check_reply_pdf_two.setVisibility(View.VISIBLE);
                    } else if (chat.getIsseen().equals("false")) {
                        holder.single_check_reply_pdf_three.setVisibility(View.VISIBLE);
                        holder.single_check_reply_pdf_four.setVisibility(View.VISIBLE);
                    } else {
                        holder.single_check_reply_pdf_five.setVisibility(View.VISIBLE);
                    }

                    Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/watsapp-6d8e6.appspot.com/o/file.png?alt=media&token=da730f2c-5760-4d67-b45b-b8ff497039b6")
                            .into(holder.rep_message_pdf);


                    try {
                        outputString = decrypt(chat.getMessage(), holder.password_null.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                    holder.rep_chat_pdf.setText(outputString);


                    holder.rep_pdf.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.download_pdf.setVisibility(View.VISIBLE);
                        }
                    });

                    holder.download_pdf.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(chat.getMessage()));
                            holder.itemView.getContext().startActivity(intent);

                        }
                    });


                    holder.reply_pdf.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
                            builder.setTitle("Choose option");
                            builder.setMessage("delete Message... ?");
                            builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();
                                }
                            });
                            builder.setNeutralButton("Everyone", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    deleteMessageForEveryone(position, holder);


                                }
                            });
                            builder.setNegativeButton("Me", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteSentMessage(position, holder);
                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();
                            return true;
                        }
                    });

                } else {
                    holder.rep_message_pdf.setVisibility(View.VISIBLE);
                    holder.rep_chat_pdf.setVisibility(View.VISIBLE);
                    holder.rep_pdf.setVisibility(View.VISIBLE);
                    holder.rep_pdf_name.setVisibility(View.VISIBLE);
                    holder.rep_pdf_name.setText(chat.getPreMessage());


                    if (chat.getIsseen().equals("false")) {
                        holder.id_new.setVisibility(View.VISIBLE);
                    } else {
                        holder.id_new.setVisibility(View.GONE);
                    }


                    Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/watsapp-6d8e6.appspot.com/o/file.png?alt=media&token=da730f2c-5760-4d67-b45b-b8ff497039b6")
                            .into(holder.rep_message_pdf);


                    try {
                        outputString = decrypt(chat.getMessage(), holder.password_null.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();

                    }


                    holder.rep_chat_pdf.setText(outputString);


                    holder.rep_pdf.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.download_pdf.setVisibility(View.VISIBLE);
                        }
                    });

                    holder.download_pdf.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(chat.getMessage()));
                            holder.itemView.getContext().startActivity(intent);

                        }
                    });


                    holder.reply_pdf.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
                            builder.setTitle("Choose option");
                            builder.setMessage("delete Message... ?");
                            builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();
                                }
                            });
                            builder.setNeutralButton("delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteReceiveMessage(position, holder);
                                    dialog.dismiss();

                                }
                            });
                            builder.setNegativeButton("", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            builder.create().show();
                            return true;
                        }
                    });


                }


            } else {
                holder.download_pdf.setVisibility(View.GONE);

            }
        }

        private void deleteSentMessage(int position, final ViewHolder holder) {

            String suri = FirebaseDatabase.getInstance().getReference().child(fuser.getUid()).child(mChat.get(position).getReceiver()).getKey();
            SqlMessage chat = mChat.get(position);

            String messageId = mChat.get(position).getMessageID();

            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            rootRef.child("Chats")
                    .child(mChat.get(position).getSender())
                    .child(mChat.get(position).getReceiver())
                    .child(mChat.get(position).getMessageID())
                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                        Toast.makeText(holder.itemView.getContext(), "Deleted Successfully...", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(holder.itemView.getContext(), "Error occurred..", Toast.LENGTH_SHORT).show();

                    }
                }
            });


        }
        private void deleteReceiveMessage(int position, final ViewHolder holder) {



            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            rootRef.child("Chats")
                    .child(mChat.get(position).getReceiver())
                    .child(mChat.get(position).getSender())
                    .child(mChat.get(position).getMessageID())
                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                        Toast.makeText(holder.itemView.getContext(), "Deleted Successfully...", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(holder.itemView.getContext(), "Error occurred..", Toast.LENGTH_SHORT).show();

                    }
                }
            });

        }
        private void deleteMessageForEveryone(final int position, final ViewHolder holder) {

            final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            rootRef.child("Chats")
                    .child(fuser.getUid())
                    .child(userid)
                    .child(mChat.get(position).getMessageID())
                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {
                        holder.itemView.setVisibility(View.INVISIBLE);

                        rootRef.child("Chats")
                                .child(userid)
                                .child(fuser.getUid())
                                .child(mChat.get(position).getMessageID())
                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){

                                    DatabaseReference rf = FirebaseDatabase.getInstance().getReference("DeleteSingleUserMessage");

                                    String id = generateString(12);

                                    HashMap<String,Object> hashMap = new HashMap<>();
                                    hashMap.put("messageID",mChat.get(position).getMessageID());
                                    hashMap.put("id",id);

                                    rf.child(fuser.getUid())
                                            .child(userid)
                                            .child(id)
                                            .updateChildren(hashMap);


                                    Toast.makeText(holder.itemView.getContext(), "Deleted Successfully...", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });                }
                    else
                    {
                        Toast.makeText(holder.itemView.getContext(), "Error occurred..", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }



        @Override
        public int getItemCount() {
            return mChat.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView id_new, text_show_password, play, pause, show_message, imageTimeDate, videoTimeDate, pdfTimeDate, delete_one_message, delete_text_message, download_pdf,
                    open_video_activity, reply_image, reply_pdf, reply_video, reply_text, rep_message_text, rep_chat_text, rep_chat_image, rep_chat_video, rep_chat_pdf, rep_pdf_name, single_check_reply_text_one, single_check_reply_text_two, single_check_reply_text_three, single_check_reply_text_four, single_check_reply_text_five, single_check_reply_image_one, single_check_reply_image_two, single_check_reply_image_three, single_check_reply_image_four, single_check_reply_image_five, single_check_reply_pdf_one, single_check_reply_pdf_two, single_check_reply_pdf_three, single_check_reply_pdf_four, single_check_reply_pdf_five,
                    single_check_reply_video_one, single_check_reply_video_two, single_check_reply_video_three, single_check_reply_video_four, single_check_reply_video_five, single_check_text_one, single_check_text_two, single_check_text_three, single_check_text_four, single_check_text_five,
                    single_check_image_one, single_check_image_two, single_check_image_three, single_check_image_four, single_check_image_five,
                    single_check_video_one, single_check_video_two, single_check_video_three, single_check_video_four, single_check_video_five,
                    single_check_pdf_one, single_check_pdf_two, single_check_pdf_three, single_check_pdf_four, single_check_pdf_five;
            public ImageView profile_image, show_image, show_pdf, rep_message_image, rep_message_pdf;
            VideoView full_video;
            EditText password, password_null, password_reply_text;
            public TextView txt_seen, textTimeDate, pdfName, full_screen, iconHidden;
            LinearLayout linearText, delete_text_linear, linearName;
            RelativeLayout linearimage, linearVideo, linearpdf, fullLinesrvideo, delete_one_linear, rep_text, rep_image, rep_video, rep_pdf;
            private ImageView show_video, rep_message_video;
            Button password_ok, password_reply_ok;
            ProgressBar splashProgress;


            public ViewHolder(View itemView) {
                super(itemView);
                password = itemView.findViewById(R.id.password);
                password_null = itemView.findViewById(R.id.password_null);
                password_ok = itemView.findViewById(R.id.password_ok);
                text_show_password = itemView.findViewById(R.id.show_message_password);
                splashProgress = itemView.findViewById(R.id.splashProgress);
                imageTimeDate = itemView.findViewById(R.id.imageTimeDate);
                show_message = itemView.findViewById(R.id.show_message);
                linearText = itemView.findViewById(R.id.lineartext);
                profile_image = itemView.findViewById(R.id.profile_image);
                txt_seen = itemView.findViewById(R.id.txt_seen);
                show_image = itemView.findViewById(R.id.show_image_image);
                textTimeDate = itemView.findViewById(R.id.texttimeDate);
                linearimage = itemView.findViewById(R.id.linearsuri);
                linearpdf = itemView.findViewById(R.id.linearsuripdf);
                show_pdf = itemView.findViewById(R.id.show_pdf);
                show_video = itemView.findViewById(R.id.show_Video);
                linearVideo = itemView.findViewById(R.id.linearsuriVideo);
                videoTimeDate = itemView.findViewById(R.id.videoTimeDate);
                pdfTimeDate = itemView.findViewById(R.id.pdfTimeDate);
                download_pdf = itemView.findViewById(R.id.download);
                pdfName = itemView.findViewById(R.id.pdfName);
                linearName = itemView.findViewById(R.id.linearName);
                play = itemView.findViewById(R.id.play);
                pause = itemView.findViewById(R.id.pause);
                open_video_activity = itemView.findViewById(R.id.open_video_activity);
                reply_image = itemView.findViewById(R.id.replyImage);
                reply_pdf = itemView.findViewById(R.id.replyPdf);
                reply_video = itemView.findViewById(R.id.replyVideo);
                reply_text = itemView.findViewById(R.id.replyText);
                id_new = itemView.findViewById(R.id.id_new);


                rep_text = itemView.findViewById(R.id.rep_text);
                rep_image = itemView.findViewById(R.id.rep_image);
                rep_video = itemView.findViewById(R.id.rep_video);
                rep_pdf = itemView.findViewById(R.id.rep_pdf);


                rep_message_text = itemView.findViewById(R.id.rep_message_text);
                rep_message_image = itemView.findViewById(R.id.rep_message_image);
                rep_message_video = itemView.findViewById(R.id.rep_message_video);
                rep_message_pdf = itemView.findViewById(R.id.rep_message_pdf);

                rep_chat_text = itemView.findViewById(R.id.rep_chat_text);
                rep_chat_image = itemView.findViewById(R.id.rep_chat_image);
                rep_chat_video = itemView.findViewById(R.id.rep_chat_video);
                rep_chat_pdf = itemView.findViewById(R.id.rep_chat_pdf);


                rep_pdf_name = itemView.findViewById(R.id.rep_pdf_name);


                password_reply_text = itemView.findViewById(R.id.password_reply_text);
                password_reply_ok = itemView.findViewById(R.id.password_reply_ok);


                single_check_text_one = itemView.findViewById(R.id.single_check_text_one);
                single_check_text_two = itemView.findViewById(R.id.single_check_text_two);
                single_check_text_three = itemView.findViewById(R.id.single_check_text_three);
                single_check_text_four = itemView.findViewById(R.id.single_check_text_four);
                single_check_text_five = itemView.findViewById(R.id.single_check_text_five);
                single_check_image_one = itemView.findViewById(R.id.single_check_image_one);
                single_check_image_two = itemView.findViewById(R.id.single_check_image_two);
                single_check_image_three = itemView.findViewById(R.id.single_check_image_three);
                single_check_image_four = itemView.findViewById(R.id.single_check_image_four);
                single_check_image_five = itemView.findViewById(R.id.single_check_image_five);
                single_check_video_one = itemView.findViewById(R.id.single_check_video_one);
                single_check_video_two = itemView.findViewById(R.id.single_check_video_two);
                single_check_video_three = itemView.findViewById(R.id.single_check_video_three);
                single_check_video_four = itemView.findViewById(R.id.single_check_video_four);
                single_check_video_five = itemView.findViewById(R.id.single_check_video_five);
                single_check_pdf_one = itemView.findViewById(R.id.single_check_pdf_one);
                single_check_pdf_two = itemView.findViewById(R.id.single_check_pdf_two);
                single_check_pdf_three = itemView.findViewById(R.id.single_check_pdf_three);
                single_check_pdf_four = itemView.findViewById(R.id.single_check_pdf_four);
                single_check_pdf_five = itemView.findViewById(R.id.single_check_pdf_five);
                single_check_reply_text_one = itemView.findViewById(R.id.single_check_reply_text_one);
                single_check_reply_text_two = itemView.findViewById(R.id.single_check_reply_text_two);
                single_check_reply_text_three = itemView.findViewById(R.id.single_check_reply_text_three);
                single_check_reply_text_four = itemView.findViewById(R.id.single_check_reply_text_four);
                single_check_reply_text_five = itemView.findViewById(R.id.single_check_reply_text_five);
                single_check_reply_image_one = itemView.findViewById(R.id.single_check_reply_image_one);
                single_check_reply_image_two = itemView.findViewById(R.id.single_check_reply_image_two);
                single_check_reply_image_three = itemView.findViewById(R.id.single_check_reply_image_three);
                single_check_reply_image_four = itemView.findViewById(R.id.single_check_reply_image_four);
                single_check_reply_image_five = itemView.findViewById(R.id.single_check_reply_image_five);
                single_check_reply_video_one = itemView.findViewById(R.id.single_check_reply_video_one);
                single_check_reply_video_two = itemView.findViewById(R.id.single_check_reply_video_two);
                single_check_reply_video_three = itemView.findViewById(R.id.single_check_reply_video_three);
                single_check_reply_video_four = itemView.findViewById(R.id.single_check_reply_video_four);
                single_check_reply_video_five = itemView.findViewById(R.id.single_check_reply_video_five);
                single_check_reply_pdf_one = itemView.findViewById(R.id.single_check_reply_pdf_one);
                single_check_reply_pdf_two = itemView.findViewById(R.id.single_check_reply_pdf_two);
                single_check_reply_pdf_three = itemView.findViewById(R.id.single_check_reply_pdf_three);
                single_check_reply_pdf_four = itemView.findViewById(R.id.single_check_reply_pdf_four);
                single_check_reply_pdf_five = itemView.findViewById(R.id.single_check_reply_pdf_five);


            }
        }

        @Override
        public int getItemViewType(int position) {
            fuser = FirebaseAuth.getInstance().getCurrentUser();
            if (mChat.get(position).getSender().equals(fuser.getUid())) {
                return MSG_TYPE_RIGHT;
            } else {
                return MSG_TYPE_LEFT;
            }
        }

    }
}