package com.some.notes.Video.pickvideo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.some.notes.Audio.pickvideo.BaseActivity;
import com.some.notes.Audio.pickvideo.BaseAdapter;
import com.some.notes.Audio.pickvideo.FolderListAdapter;
import com.some.notes.Audio.pickvideo.StaticFinalValues;
import com.some.notes.Audio.pickvideo.Util;
import com.some.notes.Audio.pickvideo.beans.Directory;
import com.some.notes.Audio.pickvideo.beans.VideoFile;
import com.some.notes.Audio.pickvideo.callback.FilterResultCallback;
import com.some.notes.Audio.pickvideo.callback.OnSelectStateListener;
import com.some.notes.Audio.pickvideo.itemDecoration.DividerGridItemDecoration;
import com.some.notes.Images.pickvideo.GroupMultipleImagePickActivity;
import com.some.notes.Images.pickvideo.groupImagePickctivity;
import com.some.notes.MassageActivityInstagram;
import com.some.notes.MessageActivity;
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

public class GroupVideoPickActivity extends BaseActivity {
    public static final String THUMBNAIL_PATH = "FilePick";
    public static final String IS_TAKEN_AUTO_SELECTED = "IsTakenAutoSelected";
    public static final int COLUMN_NUMBER = 2;
    String messageid, AES = "AES", cheker = "", myUrl = "", saveCurrentTime, saveCurrentDate, saveCurrentTime1, saveCurrentDate1, trans_msg, outputString;
    private RecyclerView mRecyclerView;
    List<DownloadModel> downloadModels = new ArrayList<>();
    private VideoPickAdapter mAdapter;
    private boolean isTakenAutoSelected;
    private ArrayList<VideoFile> mSelectedList = new ArrayList<>();
    private List<Directory<VideoFile>> mAll;
    DatabaseReference RootRef;
    private StorageTask uploadTask;
    public static FirebaseUser fuser;
    private ProgressBar mProgressBar;
    private TextView tv_folder;
    private LinearLayout ll_folder;
    private RelativeLayout tb_pick,record_image;
    Intent intent;
    boolean notify = false;
    Realm realm;
    ImageView image,rl_back;
    APIService apiService;
    Uri compressUri = null;
    public static String userid,adminId;
    public static String sqlId, id,str_name;

    @Override
    protected void permissionGranted() {
        loadData();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//状态栏半透明
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.vw_activity_video_pick);
        realm = Realm.getDefaultInstance();
        isTakenAutoSelected = getIntent().getBooleanExtra(IS_TAKEN_AUTO_SELECTED, true);
        mSelectedList.clear();
        initView();

        intent = getIntent();
        userid = intent.getStringExtra("userid");
        sqlId = intent.getStringExtra("name");
        adminId = intent.getStringExtra("adminId");


        apiService = Client.getClient("https://fcm.googleapis.com").create(APIService.class);


        fuser = FirebaseAuth.getInstance().getCurrentUser();



        DatabaseReference dbreference = FirebaseDatabase.getInstance().getReference("Users");
        dbreference.child(fuser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    str_name = dataSnapshot.child("fullname").getValue().toString();
                } else
                    Toast.makeText(GroupVideoPickActivity.this, "", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initView() {

        rl_back = findViewById(R.id.iv_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(GroupVideoPickActivity.this, MassageActivityInstagram.class);
                intent2.putExtra("userid", userid);
                intent2.putExtra("adminId", adminId);
                intent2.putExtra("name", sqlId);
                startActivityForResult(intent2, StaticFinalValues.REQUEST_CODE_PICK_VIDEO);
                finish();
            }
        });


        mRecyclerView = (RecyclerView) findViewById(R.id.rv_video_pick);
        GridLayoutManager layoutManager = new GridLayoutManager(this, COLUMN_NUMBER);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerGridItemDecoration(this));

        mAdapter = new VideoPickAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnSelectStateListener(new OnSelectStateListener<VideoFile>() {
            @Override
            public void OnSelectStateChanged(VideoFile file) {

                mSelectedList.add(file);
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra(StaticFinalValues.RESULT_PICK_VIDEO, mSelectedList);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        mProgressBar = (ProgressBar) findViewById(R.id.pb_video_pick);
        File folder = new File(getExternalCacheDir().getAbsolutePath() + File.separator + THUMBNAIL_PATH);
        if (!folder.exists()) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }


        tb_pick = (RelativeLayout) findViewById(R.id.tb_pick);
        ll_folder = (LinearLayout) findViewById(R.id.ll_folder);
        if (isNeedFolderList) {
            ll_folder.setVisibility(View.VISIBLE);
            ll_folder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFolderHelper.toggle(tb_pick);
                }
            });
            tv_folder = (TextView) findViewById(R.id.tv_folder);
            tv_folder.setText("全部");

            mFolderHelper.setFolderListListener(new FolderListAdapter.FolderListListener() {
                @Override
                public void onFolderListClick(Directory directory) {
                    mFolderHelper.toggle(tb_pick);
                    tv_folder.setText(directory.getName());

                    if (TextUtils.isEmpty(directory.getPath())) { //All
                        refreshData(mAll);
                    } else {
                        for (Directory<VideoFile> dir : mAll) {
                            if (dir.getPath().equals(directory.getPath())) {
                                List<Directory<VideoFile>> list = new ArrayList<>();
                                list.add(dir);
                                refreshData(list);
                                break;
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case StaticFinalValues.REQUEST_CODE_TAKE_VIDEO:
                if (resultCode == RESULT_OK) {
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    File file = new File(mAdapter.mVideoPath);
                    Uri contentUri = Uri.fromFile(file);
                    mediaScanIntent.setData(contentUri);
                    sendBroadcast(mediaScanIntent);
                    loadData();
                }
                break;
        }
    }

    private void loadData() {
        FileFilter.getVideos(this, new FilterResultCallback<VideoFile>() {
            @Override
            public void onResult(List<Directory<VideoFile>> directories) {
                mProgressBar.setVisibility(View.GONE);
                // Refresh folder list
                if (isNeedFolderList) {
                    ArrayList<Directory> list = new ArrayList<>();
                    Directory all = new Directory();
                    all.setName("全部");
                    list.add(all);
                    list.addAll(directories);
                    mFolderHelper.fillData(list);
                }

                mAll = directories;
                refreshData(directories);
            }
        });
    }

    private void refreshData(List<Directory<VideoFile>> directories) {
        boolean tryToFindTaken = isTakenAutoSelected;

        // if auto-select taken file is enabled, make sure requirements are met
        if (tryToFindTaken && !TextUtils.isEmpty(mAdapter.mVideoPath)) {
            File takenFile = new File(mAdapter.mVideoPath);
            tryToFindTaken = takenFile.exists(); // try to select taken file only if max isn't reached and the file exists
        }

        List<VideoFile> list = new ArrayList<>();
        for (Directory<VideoFile> directory : directories) {
            list.addAll(directory.getFiles());

            // auto-select taken file?
            if (tryToFindTaken) {
                tryToFindTaken = findAndAddTaken(directory.getFiles());   // if taken file was found, we're done
            }
        }

        for (VideoFile file : mSelectedList) {
            int index = list.indexOf(file);
            if (index != -1) {
                list.get(index).setSelected(true);
            }
        }
        mAdapter.refresh(list);
    }

    private boolean findAndAddTaken(List<VideoFile> list) {
        for (VideoFile videoFile : list) {
            if (videoFile.getPath().equals(mAdapter.mVideoPath)) {
                mSelectedList.add(videoFile);

                return true;   // taken file was found and added
            }
        }
        return false;    // taken file wasn't found
    }

    public class VideoPickAdapter extends BaseAdapter<VideoFile, VideoPickAdapter.VideoPickViewHolder> {
        public String mVideoPath;
        private Context mContext;

        public VideoPickAdapter(Context ctx) {
            this(ctx, new ArrayList<VideoFile>());
        }

        public VideoPickAdapter(Context ctx, ArrayList<VideoFile> list) {
            super(ctx, list);
            mContext = ctx;
        }

        @Override
        public VideoPickViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(mContext).inflate(R.layout.vw_layout_item_video_pick, parent, false);
            ViewGroup.LayoutParams params = itemView.getLayoutParams();
            if (params != null) {
                WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
                int width = wm.getDefaultDisplay().getWidth();
                params.height = width / VideoPickActivity.COLUMN_NUMBER;
            }
            return new VideoPickAdapter.VideoPickViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final VideoPickViewHolder holder, int position) {

            holder.mIvCamera.setVisibility(View.INVISIBLE);
            holder.mIvThumbnail.setVisibility(View.VISIBLE);
            holder.mDurationLayout.setVisibility(View.VISIBLE);
            holder.song_relative.setVisibility(View.GONE);
            holder.thumbnails_relative.setVisibility(View.VISIBLE);
            holder.send_image.setVisibility(View.GONE);
            holder.check_box_true.setVisibility(View.GONE);


            final VideoFile file;

            file = mList.get(position);

            Glide.with(mContext)
                    .load(file.getPath())
                    .into(holder.mIvThumbnail);

            holder.mDuration.setText(Util.getDurationString(file.getDuration()));

            holder.mIvThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.send_image.setVisibility(View.VISIBLE);

                }
            });

            holder.send_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

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


                    ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetWork = manager.getActiveNetworkInfo();


                    String messageID = saveCurrentDate2 +saveCurrentTime2;

                    if (null!=activeNetWork){
                        if (activeNetWork.getType() == ConnectivityManager.TYPE_WIFI){

                            addVideo("file://"+file.getPath(),fuser.getUid(),userid,messageID,"kt5rg2/r78I081y/kXkhw==\n","video",saveCurrentTime,saveCurrentDate,"hello","kya","panding",file.getPath());
                        }
                        else if (activeNetWork.getType() == ConnectivityManager.TYPE_MOBILE){

                            addVideo("file://"+file.getPath(),fuser.getUid(),userid,messageID,"kt5rg2/r78I081y/kXkhw==\n","video",saveCurrentTime,saveCurrentDate,"hello","kya","panding",file.getPath());
                        }
                    }
                    else {
                        addItemInRealmNoInternet("file://"+file.getPath(),fuser.getUid(),userid,messageID,"kt5rg2/r78I081y/kXkhw==\n","video",saveCurrentTime,saveCurrentDate,"hello","kya","never",file.getPath());

                        Toast.makeText(GroupVideoPickActivity.this, "you are not connected with Internet Connection", Toast.LENGTH_SHORT).show();
                    }


                    holder.send_image.setVisibility(View.GONE);
                    holder.check_box_true.setVisibility(View.VISIBLE);

                }
            });


            holder.check_box_true.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(GroupVideoPickActivity.this, "Already sended", Toast.LENGTH_SHORT).show();
                }
            });


        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        class VideoPickViewHolder extends RecyclerView.ViewHolder {
            private ImageView mIvCamera;
            private ImageView mIvThumbnail,check_box_true, send_image;
            private View mShadow;
            private TextView mDuration;
            private RelativeLayout mDurationLayout,song_relative,thumbnails_relative;

            public VideoPickViewHolder(View itemView) {
                super(itemView);
                song_relative = itemView.findViewById(R.id.music_relative);
                thumbnails_relative = itemView.findViewById(R.id.thumbnail_relative);
                mIvCamera = (ImageView) itemView.findViewById(R.id.iv_camera);
                mIvThumbnail = (ImageView) itemView.findViewById(R.id.iv_thumbnail);
                mShadow = itemView.findViewById(R.id.shadow);
                mDuration = (TextView) itemView.findViewById(R.id.txt_duration);
                mDurationLayout = (RelativeLayout) itemView.findViewById(R.id.layout_duration);
                check_box_true = itemView.findViewById(R.id.check_true);
                send_image = itemView.findViewById(R.id.send_image);

            }
        }

    }

    private void addVideo(String message, String sender, String receiver, String messageId, String bio, String type, String time, String date, String lastSendMessage, String preMessage, String isseen, String imageurl){
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
        downloadModel.setType("video");
        downloadModel.setImagrUrl(imageurl);
        downloadModel.setAdminId("na value");
        downloadModel.setUsername(str_name);



        downloadModels.add(downloadModel);

        Calendar calForDate1 = Calendar.getInstance();
        SimpleDateFormat currendateFormat1 = new SimpleDateFormat("yyyy-MM");
        saveCurrentDate1 =currendateFormat1.format(calForDate1.getTime());

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currendateFormat = new SimpleDateFormat("MMM dd");
        saveCurrentDate =currendateFormat.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currenTimeFormat = new SimpleDateFormat("hh:mm a");
        saveCurrentTime =currenTimeFormat.format(calForTime.getTime());


        Calendar calForDate2 = Calendar.getInstance();
        SimpleDateFormat currendateFormat2 = new SimpleDateFormat("yyyyMMdd");
        String saveCurrentDate2 = currendateFormat1.format(calForDate1.getTime());

        Calendar calForTime1 = Calendar.getInstance();
        SimpleDateFormat currenTimeFormat1 = new SimpleDateFormat("a hhmmss");
        String saveCurrentTime2 = currenTimeFormat1.format(calForTime1.getTime());

        notify =true;

        RootRef = FirebaseDatabase.getInstance().getReference("GroupChats").child(adminId).child(userid);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(saveCurrentDate1);

        final String messageSenderRef = "Chats/" + fuser.getUid() + "/" + userid;
        final String messageReceiverRef = "Chats/" + userid + "/" + fuser.getUid();


        final StorageReference filePath = storageReference.child(saveCurrentDate2 + saveCurrentTime2 + "." + "mp4");



        uploadTask = filePath.putFile(Uri.parse(message));


        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                if (taskSnapshot.getBytesTransferred()>1){
                    long i = taskSnapshot.getTotalByteCount();
                    long j= taskSnapshot.getBytesTransferred();

                    long one = i/100;

                    long tr = j/one;

                    String st = String.valueOf(tr);

                }else {
                }
            }
        });



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
                            messageTextBody.put("type", "video");
                            messageTextBody.put("sender", fuser.getUid());
                            messageTextBody.put("receiver", userid);
                            messageTextBody.put("messageID", messageId);
                            messageTextBody.put("time", saveCurrentTime);
                            messageTextBody.put("date", saveCurrentDate);
                            messageTextBody.put("title", "sure");
                            messageTextBody.put("bio", "sure");
                            messageTextBody.put("isseen", "false");
                            messageTextBody.put(sqlId, true);
                            messageTextBody.put("last", "false");
                            messageTextBody.put("preMessage", "hello");
                            messageTextBody.put("lastSendMessage", "kt5rg2/r78I081y/kXkhw==\n");



                            RootRef.child(saveCurrentDate1 + saveCurrentTime1).updateChildren(messageTextBody).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        realm.executeTransaction(new Realm.Transaction() {
                                            @Override
                                            public void execute(Realm realm) {
                                                final DownloadModel downloads = realm.where(DownloadModel.class).equalTo("id",nextId).findFirst();
                                                downloads.setIsseen("false");

                                            }
                                        });

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

                                        Toast.makeText(GroupVideoPickActivity.this, "Error", Toast.LENGTH_SHORT).show();
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

    private void sendNotifiaction(String receiver, final String username, final String message) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(fuser.getUid(), R.mipmap.ic_launcher, username + ": " + message, "New Message",
                            userid);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success != 1) {
                                            Toast.makeText(GroupVideoPickActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
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
        downloadModel.setType("video");
        downloadModel.setImagrUrl(imageurl);
        downloadModel.setAdminId("na value");
        downloadModel.setUsername("no value");

        downloadModels.add(downloadModel);


        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(downloadModel);

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent2 = new Intent(GroupVideoPickActivity.this, MassageActivityInstagram.class);
        intent2.putExtra("userid", userid);
        startActivityForResult(intent2, StaticFinalValues.REQUEST_CODE_PICK_VIDEO);

    }

}
