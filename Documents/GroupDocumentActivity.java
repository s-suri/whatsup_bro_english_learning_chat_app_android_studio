package com.some.notes.Documents;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.some.notes.Audio.pickvideo.StaticFinalValues;
import com.some.notes.Images.pickvideo.GroupMultipleImagePickActivity;
import com.some.notes.Images.pickvideo.ImagesPickActivity;
import com.some.notes.MassageActivityInstagram;
import com.some.notes.Model.DownloadModel;
import com.some.notes.Model.User;
import com.some.notes.Notifications.APIService;
import com.some.notes.Notifications.Client;
import com.some.notes.Notifications.Data;
import com.some.notes.Notifications.MyResponse;
import com.some.notes.Notifications.Sender;
import com.some.notes.Notifications.Token;
import com.some.notes.PDF.pickvideo.GroupPDFPickActivity;
import com.some.notes.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupDocumentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private Uri fileUri;
    private StorageTask uploadTask;
    String messageid, AES = "AES", cheker = "", myUrl = "", saveCurrentTime, saveCurrentDate, saveCurrentTime1, saveCurrentDate1, trans_msg, outputString;
    private boolean permission;
    List<DownloadModel> downloadModels = new ArrayList<>();
    private File storage;
    private String[] storagePaths;
    Intent intent;
    boolean notify = false;
    Realm realm;
    ImageView image;
    APIService apiService;
    Uri compressUri = null;
    public static String userid,adminId;
    public static String sqlId, id,str_name;
    DatabaseReference RootRef;
    public static FirebaseUser fuser;
    int SPLASH_TIME = 250;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.document_items);
        realm = Realm.getDefaultInstance();

//
        //In marshmallow and above we need to ask for permission first
        checkStorageAccessPermission();
//
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //if you face lack in scrolling then add following lines
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setNestedScrollingEnabled(false);

        recyclerViewAdapter = new RecyclerViewAdapter(this);

        recyclerView.setAdapter(recyclerViewAdapter);



        storagePaths = StorageUtil.getStorageDirectories(GroupDocumentActivity.this);

        for (String path : storagePaths) {
            storage = new File(path);
            Method.load_Directory_Files(storage);
        }



        intent = getIntent();
        userid = intent.getStringExtra("userid");
        sqlId = intent.getStringExtra("name");
        adminId = intent.getStringExtra("adminId");





        apiService = Client.getClient("https://fcm.googleapis.com").create(APIService.class);


        fuser = FirebaseAuth.getInstance().getCurrentUser();


        RootRef = FirebaseDatabase.getInstance().getReference();


        DatabaseReference dbreference = FirebaseDatabase.getInstance().getReference("Users");
        dbreference.child(fuser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    str_name = dataSnapshot.child("fullname").getValue().toString();
                } else
                    Toast.makeText(GroupDocumentActivity.this, "", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //load data here
                //for first time data will be loaded here
                //then it will be loaded in splash screen
                //because if we could not have permission then we could not load data in splash screen window
                storagePaths = StorageUtil.getStorageDirectories(this);

                for (String path : storagePaths) {
                    storage = new File(path);
                    Method.load_Directory_Files(storage);
                }

                recyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    private void checkStorageAccessPermission() {
        //ContextCompat use to retrieve resources. It provide uniform interface to access resources.
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                new AlertDialog.Builder(this)
                        .setTitle("Permission Needed")
                        .setMessage("This permission is needed to access media file in your phone")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(GroupDocumentActivity.this,
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                        1);
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            // Do nothing. Because if permission is already granted then files will be accessed/loaded in splash_screen_activity
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 1:
                if (resultCode == Activity.RESULT_OK){
                    fileUri  = data.getData();
                    getContentResolver().takePersistableUriPermission(fileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                            | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
        }
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Context mContext;

        RecyclerViewAdapter(Context mContext) {
            this.mContext = mContext;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.files_list, parent, false);
            return new RecyclerViewAdapter.FileLayoutHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ((RecyclerViewAdapter.FileLayoutHolder) holder).videoTitle.setText(Constant.allMediaList.get(position).getName());
            //we will load thumbnail using glid library
            Uri uri = Uri.fromFile(Constant.allMediaList.get(position));


            Glide.with(mContext).load(uri).thumbnail(0.1f).into(((RecyclerViewAdapter.FileLayoutHolder) holder).thumbnail);

            ((RecyclerViewAdapter.FileLayoutHolder) holder).docsize_size.setText(Constant.allMediaList.get(position).getName());


            ((RecyclerViewAdapter.FileLayoutHolder) holder).thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((FileLayoutHolder) holder).filled_send.setVisibility(View.VISIBLE);
                }
            });

            ((FileLayoutHolder) holder).filled_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((FileLayoutHolder) holder).filled_send.setVisibility(View.GONE);
                    ((RecyclerViewAdapter.FileLayoutHolder) holder).check_box.setVisibility(View.VISIBLE);


                    Calendar calForDate1 = Calendar.getInstance();
                    SimpleDateFormat currendateFormat1 = new SimpleDateFormat("yyyy-MM");
                    saveCurrentDate1 =currendateFormat1.format(calForDate1.getTime());

                    Calendar calForDate = Calendar.getInstance();
                    SimpleDateFormat currendateFormat = new SimpleDateFormat("MMM dd");
                    saveCurrentDate = currendateFormat.format(calForDate.getTime());

                    Calendar calForTime = Calendar.getInstance();
                    SimpleDateFormat currenTimeFormat = new SimpleDateFormat("hh:mm a");
                    saveCurrentTime = currenTimeFormat.format(calForTime.getTime());


                    Calendar calForDate2 = Calendar.getInstance();
                    SimpleDateFormat currendateFormat2 = new SimpleDateFormat("yyyyMMdd");
                    String saveCurrentDate2 = currendateFormat1.format(calForDate1.getTime());


                    Calendar calForTime1 = Calendar.getInstance();
                    SimpleDateFormat currenTimeFormat1 = new SimpleDateFormat("a hhmmss");
                    String saveCurrentTime2 = currenTimeFormat1.format(calForTime1.getTime());

                    ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

                    NetworkInfo activeNetWork = manager.getActiveNetworkInfo();

                    String messageID = saveCurrentDate2 + saveCurrentTime2;

                    if (null != activeNetWork) {
                        if (activeNetWork.getType() == ConnectivityManager.TYPE_WIFI) {

                            addDocx(uri.toString(), fuser.getUid(), userid, messageID, Constant.allMediaList.get(position).getName(), "docx", saveCurrentTime, saveCurrentDate, "hello", "kya", "panding", uri.toString(), uri);
                        } else if (activeNetWork.getType() == ConnectivityManager.TYPE_MOBILE) {

                            addDocx(uri.toString(), fuser.getUid(), userid, messageID, Constant.allMediaList.get(position).getName(), "docx", saveCurrentTime, saveCurrentDate, "hello", "kya", "panding", uri.toString(), uri);
                        }
                    }
                    else {
                        addItemInRealmNoInternet(uri.toString(), fuser.getUid(), userid, messageID, Constant.allMediaList.get(position).getName(), "docx", saveCurrentTime, saveCurrentDate, "hello", "kya", "never", uri.toString());

                        Toast.makeText(GroupDocumentActivity.this, "you are not connected with Internet Connection", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            ((FileLayoutHolder) holder).check_box.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Toast.makeText(GroupDocumentActivity.this, "Already sended....!", Toast.LENGTH_SHORT).show();



                }

            });

        }

        @Override
        public int getItemCount() {
            return Constant.allMediaList.size();
        }

        class FileLayoutHolder extends RecyclerView.ViewHolder {

            ImageView thumbnail,check_box,filled_send;
            TextView videoTitle,docsize_size;
            ImageView ic_more_btn;


            public FileLayoutHolder(@NonNull View itemView) {
                super(itemView);

                thumbnail = itemView.findViewById(R.id.thumbnail);
                videoTitle = itemView.findViewById(R.id.videotitle);
                ic_more_btn = itemView.findViewById(R.id.ic_more_btn);
                docsize_size = itemView.findViewById(R.id.docsize_size);
                check_box = itemView.findViewById(R.id.check_box);
                filled_send = itemView.findViewById(R.id.filled_send);

            }
        }

    }

    private void addDocx(String message, String sender, String receiver, String messageId, String bio, String type, String time, String date, String lastSendMessage, String preMessage, String isseen, String imageurl,Uri camuri){
        Number currentnum=realm.where(DownloadModel.class).max("id");
        int nextId;

        if(currentnum==null){
            nextId=1;
        }
        else{
            nextId=currentnum.intValue()+1;
        }
        final DownloadModel downloadModel=new DownloadModel();
        downloadModel.setId(nextId);
        downloadModel.setStatus("Completed");
        downloadModel.setTitle("hello");
        downloadModel.setFile_size("0");
        downloadModel.setProgress("0");
        downloadModel.setIs_paused(false);
        downloadModel.setDownloadId(8878);
        downloadModel.setFile_path("");
        downloadModel.setMessage(message);
        downloadModel.setSender(fuser.getUid());
        downloadModel.setReceiver(userid);
        downloadModel.setMessageID(messageId);
        downloadModel.setIsseen("one single");
        downloadModel.setLastSendMessage(lastSendMessage);
        downloadModel.setBio(bio);
        downloadModel.setDate(date);
        downloadModel.setPreMessage(preMessage);
        downloadModel.setTime(time);
        downloadModel.setType("docx");
        downloadModel.setImagrUrl(imageurl);
        downloadModel.setAdminId("na value");
        downloadModel.setUsername("no value");


        downloadModels.add(downloadModel);


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


        Calendar calForDate2 = Calendar.getInstance();
        SimpleDateFormat currendateFormat2 = new SimpleDateFormat("yyyyMMdd");
        String saveCurrentDate2 = currendateFormat2.format(calForDate2.getTime());


        Calendar calForTime2 = Calendar.getInstance();
        SimpleDateFormat currenTimeFormat2 = new SimpleDateFormat("a hhmmss");
        String saveCurrentTime2 = currenTimeFormat2.format(calForTime2.getTime());

        notify =true;
        RootRef = FirebaseDatabase.getInstance().getReference("GroupChats").child(adminId).child(userid);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(saveCurrentDate1);

        final String messageSenderRef = "Chats/" + fuser.getUid() + "/" + userid;
        final String messageReceiverRef = "Chats/" + userid + "/" + fuser.getUid();


        final StorageReference filePath = storageReference.child(saveCurrentDate2 + saveCurrentTime2 + "." + type);


        uploadTask = filePath.putFile(camuri);
        uploadTask.continueWithTask(new Continuation() {
            @Override
            public Object then (@NonNull Task task) throws Exception {

                if (!task.isSuccessful()) {
                    throw task.getException();


                }
                return filePath.getDownloadUrl();
            }
        }).

                addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete (@NonNull Task < Uri > task) {
                        if (task.isSuccessful()) {

                            Uri downloadUrl = task.getResult();
                            myUrl = downloadUrl.toString();


                            Map messageTextBody = new HashMap();
                            messageTextBody.put("message", myUrl);
                            messageTextBody.put("url", myUrl);
                            messageTextBody.put("name", str_name);
                            messageTextBody.put("type", "docx");
                            messageTextBody.put("sender", fuser.getUid());
                            messageTextBody.put("receiver", userid);
                            messageTextBody.put("messageID", messageId);
                            messageTextBody.put("time", saveCurrentTime);
                            messageTextBody.put("date", saveCurrentDate);
                            messageTextBody.put("title", camuri.getLastPathSegment());
                            messageTextBody.put("bio", "sure");
                            messageTextBody.put("isseen", "false");
                            messageTextBody.put(sqlId, true);
                            messageTextBody.put("last", "false");
                            messageTextBody.put("preMessage", "hello");
                            messageTextBody.put("lastSendMessage", "kt5rg2/r78I081y/kXkhw==\n");


                            Map messageBodyDetails = new HashMap();

                            messageBodyDetails.put(messageSenderRef + "/" + messageId, messageTextBody);
                            messageBodyDetails.put(messageReceiverRef + "/" + messageId, messageTextBody);

                            HashMap<String, Object> map = new HashMap<>();
                            map.put("time", saveCurrentDate1 + "-" + saveCurrentTime1);


                            RootRef.child(saveCurrentDate1 + saveCurrentTime1).updateChildren(messageTextBody).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {


                                        DatabaseReference reference;
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

                                        Toast.makeText(GroupDocumentActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                    }


                                }
                            });
                        }
                    }
                });

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(downloadModel);



            }
        });
    }

    private  void addItemInRealmNoInternet(String message, String sender, String receiver, String messageId, String bio, String type, String time, String date, String lastSendMessage, String preMessage, String isseen, String imageurl){

        Number currentnum=realm.where(DownloadModel.class).max("id");
        int nextId;

        if(currentnum==null){
            nextId=1;
        }
        else{
            nextId=currentnum.intValue()+1;
        }
        final DownloadModel downloadModel=new DownloadModel();
        downloadModel.setId(nextId);
        downloadModel.setStatus("Completed");
        downloadModel.setTitle("hello");
        downloadModel.setFile_size("0");
        downloadModel.setProgress("0");
        downloadModel.setIs_paused(false);
        downloadModel.setDownloadId(8878);
        downloadModel.setFile_path("");
        downloadModel.setMessage(message);
        downloadModel.setSender(fuser.getUid());
        downloadModel.setReceiver(userid);
        downloadModel.setMessageID(messageId);
        downloadModel.setIsseen(isseen);
        downloadModel.setLastSendMessage(lastSendMessage);
        downloadModel.setBio(bio);
        downloadModel.setDate(date);
        downloadModel.setPreMessage(preMessage);
        downloadModel.setTime(time);
        downloadModel.setType("docx");
        downloadModel.setImagrUrl(imageurl);
        downloadModel.setAdminId("na value");
        downloadModel.setUsername("no value");

        downloadModels.add(downloadModel);



        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(downloadModel);

                recyclerView.smoothScrollToPosition(downloadModels.size()-1);

            }
        });

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
                                            Toast.makeText(GroupDocumentActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent2 = new Intent(GroupDocumentActivity.this, MassageActivityInstagram.class);
        intent2.putExtra("userid", userid);
        intent2.putExtra("adminId", adminId);
        intent2.putExtra("name", sqlId);
        startActivityForResult(intent2, StaticFinalValues.REQUEST_CODE_PICK_VIDEO);

    }

}
