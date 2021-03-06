package com.some.notes.Images.pickvideo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.some.notes.Audio.pickvideo.BaseActivity;
import com.some.notes.Audio.pickvideo.BaseAdapter;
import com.some.notes.Audio.pickvideo.FolderListAdapter;
import com.some.notes.Audio.pickvideo.StaticFinalValues;
import com.some.notes.Audio.pickvideo.beans.Directory;
import com.some.notes.Audio.pickvideo.beans.ImageFile;
import com.some.notes.Audio.pickvideo.callback.FilterResultCallback;
import com.some.notes.Audio.pickvideo.callback.OnSelectStateListener;
import com.some.notes.Audio.pickvideo.itemDecoration.DividerGridItemDecoration;
import com.some.notes.MessageActivity;
import com.some.notes.Model.DownloadModel;
import com.some.notes.Model.User;
import com.some.notes.Notifications.APIService;
import com.some.notes.Notifications.Client;
import com.some.notes.Notifications.Data;
import com.some.notes.Notifications.MyResponse;
import com.some.notes.Notifications.Sender;
import com.some.notes.Notifications.Token;
import com.some.notes.PhotoEditing.EditImageActivity;
import com.some.notes.R;
import com.some.notes.sillicompresser.SiliCompressor;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MultipleImagesPickAtivity extends BaseActivity {

    public static final String THUMBNAIL_PATH = "FilePick";
    public static final String IS_TAKEN_AUTO_SELECTED = "IsTakenAutoSelected";
    String messageid, AES = "AES", cheker = "", myUrl = "", saveCurrentTime, saveCurrentDate, saveCurrentTime1, saveCurrentDate1, trans_msg, outputString;
    public static final int COLUMN_NUMBER = 2;
    private RecyclerView mRecyclerView;
    List<DownloadModel> downloadModels = new ArrayList<>();
    private VideoPickAdapter mAdapter;
    private boolean isTakenAutoSelected;
    private ArrayList<ImageFile> mSelectedList = new ArrayList<>();
    private List<Directory<ImageFile>> mAll;
    private ProgressBar mProgressBar;
    APIService apiService;
    Uri compressUri = null;
    DatabaseReference RootRef;
    private StorageTask uploadTask;
    public static FirebaseUser fuser;
    private TextView tv_folder;
    private LinearLayout ll_folder;
    private RelativeLayout tb_pick, record_image,rl_back;
    ImageView image;
    Intent intent;
    boolean notify = false;
    Realm realm;
    public static String userid;
    public static String sqlId, id;

    @Override
    protected void permissionGranted() {
        loadData();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//??????????????????
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.vw_activity_video_pick);
        realm = Realm.getDefaultInstance();

        isTakenAutoSelected = getIntent().getBooleanExtra(IS_TAKEN_AUTO_SELECTED, true);
        mSelectedList.clear();
        initView();

        intent = getIntent();
        userid = intent.getStringExtra("userid");
        sqlId = intent.getStringExtra("name");

        apiService = Client.getClient("https://fcm.googleapis.com").create(APIService.class);


        fuser = FirebaseAuth.getInstance().getCurrentUser();


        RootRef = FirebaseDatabase.getInstance().getReference();

    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_video_pick);
        GridLayoutManager layoutManager = new GridLayoutManager(this, COLUMN_NUMBER);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerGridItemDecoration(this));

        mAdapter = new VideoPickAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnSelectStateListener(new OnSelectStateListener<ImageFile>() {
            @Override
            public void OnSelectStateChanged(ImageFile file) {

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
            tv_folder.setText("??????");

            mFolderHelper.setFolderListListener(new FolderListAdapter.FolderListListener() {
                @Override
                public void onFolderListClick(Directory directory) {
                    mFolderHelper.toggle(tb_pick);
                    tv_folder.setText(directory.getName());

                    if (TextUtils.isEmpty(directory.getPath())) { //All
                        refreshData(mAll);
                    } else {
                        for (Directory<ImageFile> dir : mAll) {
                            if (dir.getPath().equals(directory.getPath())) {
                                List<Directory<ImageFile>> list = new ArrayList<>();
                                list.add(dir);
                                refreshData(list);
                                break;
                            }
                        }
                    }
                }
            });
        }
        record_image = findViewById(R.id.rl_rec_aud);
        image = findViewById(R.id.iv_rec_aud);
        record_image.setVisibility(View.GONE);

        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MultipleImagesPickAtivity.this, MessageActivity.class);
                intent.putExtra("userid",userid);
                intent.putExtra("name",sqlId);
                startActivity(intent);
                finish();
            }
        });

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
                    Toast.makeText(MultipleImagesPickAtivity.this, contentUri.toString(), Toast.LENGTH_SHORT).show();
                    mediaScanIntent.setData(contentUri);
                    sendBroadcast(mediaScanIntent);
                    loadData();
                }
                break;
        }
    }

    private void loadData() {
        FileFilter.getVideos(this, new FilterResultCallback<ImageFile>() {
            @Override
            public void onResult(List<Directory<ImageFile>> directories) {
                mProgressBar.setVisibility(View.GONE);
                // Refresh folder list
                if (isNeedFolderList) {
                    ArrayList<Directory> list = new ArrayList<>();
                    Directory all = new Directory();
                    all.setName("??????");
                    list.add(all);
                    list.addAll(directories);
                    mFolderHelper.fillData(list);
                }

                mAll = directories;
                refreshData(directories);
            }
        });
    }

    private void refreshData(List<Directory<ImageFile>> directories) {
        boolean tryToFindTaken = isTakenAutoSelected;

        // if auto-select taken file is enabled, make sure requirements are met
        if (tryToFindTaken && !TextUtils.isEmpty(mAdapter.mVideoPath)) {
            File takenFile = new File(mAdapter.mVideoPath);
            tryToFindTaken = takenFile.exists(); // try to select taken file only if max isn't reached and the file exists
        }

        List<ImageFile> list = new ArrayList<>();
        for (Directory<ImageFile> directory : directories) {
            list.addAll(directory.getFiles());

            // auto-select taken file?
            if (tryToFindTaken) {
                tryToFindTaken = findAndAddTaken(directory.getFiles());   // if taken file was found, we're done
            }
        }

        for (ImageFile file : mSelectedList) {
            int index = list.indexOf(file);
            if (index != -1) {
                list.get(index).setSelected(true);
            }
        }
        mAdapter.refresh(list);
    }

    private boolean findAndAddTaken(List<ImageFile> list) {
        for (ImageFile videoFile : list) {
            if (videoFile.getPath().equals(mAdapter.mVideoPath)) {
                mSelectedList.add(videoFile);

                return true;   // taken file was found and added
            }
        }
        return false;    // taken file wasn't found
    }

    public class VideoPickAdapter extends BaseAdapter<ImageFile, VideoPickAdapter.VideoPickViewHolder> {
        public String mVideoPath;
        private Context mContext;

        public VideoPickAdapter(Context ctx) {
            this(ctx, new ArrayList<ImageFile>());
        }

        public VideoPickAdapter(Context ctx, ArrayList<ImageFile> list) {
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
                params.height = width / MultipleImagesPickAtivity.COLUMN_NUMBER;
            }
            return new VideoPickViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final VideoPickViewHolder holder, int position) {


            holder.mIvCamera.setVisibility(View.INVISIBLE);
            holder.mIvThumbnail.setVisibility(View.VISIBLE);
            holder.mDurationLayout.setVisibility(View.GONE);
            holder.song_relative.setVisibility(View.GONE);
            holder.thumbnail_relative.setVisibility(View.VISIBLE);
            holder.mDuration.setVisibility(View.GONE);

            final ImageFile file;

            file = mList.get(position);

            Glide.with(mContext)
                    .load(file.getPath())
                    .into(holder.mIvThumbnail);

            holder.mIvThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.send_image.setVisibility(View.VISIBLE);

                }
            });

            holder.send_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new ImageCompressionAsyncTask(MultipleImagesPickAtivity.this).execute("file://" + file.getPath(),
                            Environment.getExternalStorageDirectory() + "/Study Chats/images");

                    holder.send_image.setVisibility(View.GONE);
                    holder.check_box_true.setVisibility(View.VISIBLE);

                }
            });


            holder.check_box_true.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(MultipleImagesPickAtivity.this, "Already sended", Toast.LENGTH_SHORT).show();
                }
            });

        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        class VideoPickViewHolder extends RecyclerView.ViewHolder {
            private ImageView mIvCamera;
            private ImageView mIvThumbnail, check_box_true, send_image;
            private View mShadow;
            private TextView mDuration;
            private RelativeLayout mDurationLayout, thumbnail_relative, song_relative;

            public VideoPickViewHolder(View itemView) {
                super(itemView);
                thumbnail_relative = itemView.findViewById(R.id.thumbnail_relative);
                song_relative = itemView.findViewById(R.id.music_relative);
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

    class ImageCompressionAsyncTask extends AsyncTask<String, Void, String> {

        Context mContext;

        public ImageCompressionAsyncTask(Context context) {
            mContext = context;
        }

        @Override
        protected String doInBackground(String... params) {

            return SiliCompressor.with(mContext).compress(params[0], new File(params[1]));

        }

        @Override
        protected void onPostExecute(String s) {

            float length = 0;
            String name;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                compressUri = Uri.parse(s);
                Cursor c = getContentResolver().query(compressUri, null, null, null, null);
                c.moveToFirst();
                name = c.getString(c.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                length = c.getLong(c.getColumnIndex(OpenableColumns.SIZE)) / 1024;
            } else {
                File imageFile = new File(s);
                compressUri = Uri.fromFile(imageFile);
                name = imageFile.getName();
                length = imageFile.length() / 1024f; // Size in KB
            }


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


            addItems(compressUri.toString(), fuser.getUid(), userid, saveCurrentDate2 + saveCurrentTime2, "kt5rg2/r78I081y/kXkhw==\n", cheker, saveCurrentTime, saveCurrentDate, "hello", "kya", "false", compressUri.toString());



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
            downloadModel.setMessage(compressUri.toString());
            downloadModel.setSender(fuser.getUid());
            downloadModel.setReceiver(userid);
            downloadModel.setMessageID(saveCurrentDate2 + saveCurrentTime2);
            downloadModel.setIsseen("One Single");
            downloadModel.setLastSendMessage("hello");
            downloadModel.setBio("kt5rg2/r78I081y/kXkhw==\n");
            downloadModel.setDate(saveCurrentDate);
            downloadModel.setPreMessage("kya");
            downloadModel.setTime(saveCurrentTime);
            downloadModel.setType("image");
            downloadModel.setImagrUrl(compressUri.toString());


            downloadModels.add(downloadModel);

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealm(downloadModel);


                }
            });


            notify = true;
            RootRef = FirebaseDatabase.getInstance().getReference();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(saveCurrentDate1);

            final String messageSenderRef = "Chats/" + fuser.getUid() + "/" + userid;
            final String messageReceiverRef = "Chats/" + userid + "/" + fuser.getUid();


            final StorageReference filePath = storageReference.child(saveCurrentDate2 + saveCurrentTime2 + "." + "jpg");


            uploadTask = filePath.putFile(compressUri);
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


                        HashMap<String, Object> messageTextBody = new HashMap();
                        messageTextBody.put("message", myUrl);
                        messageTextBody.put("url", myUrl);
                        messageTextBody.put("name", compressUri.getLastPathSegment());
                        messageTextBody.put("type", "image");
                        messageTextBody.put("sender", fuser.getUid());
                        messageTextBody.put("receiver", userid);
                        messageTextBody.put("messageID", saveCurrentDate1 + saveCurrentTime1);
                        messageTextBody.put("time", saveCurrentTime);
                        messageTextBody.put("date", saveCurrentDate);
                        messageTextBody.put("title", compressUri.getLastPathSegment());
                        messageTextBody.put("bio", "sure");
                        messageTextBody.put("isseen", "false");
                        messageTextBody.put(sqlId, true);
                        messageTextBody.put("last", "false");
                        messageTextBody.put("preMessage", "hello");
                        messageTextBody.put("lastSendMessage", "kt5rg2/r78I081y/kXkhw==\n");


                        HashMap<String, Object> messageBodyDetails = new HashMap();

                        messageBodyDetails.put(messageSenderRef + "/" + saveCurrentDate1 + saveCurrentTime1, messageTextBody);
                        messageBodyDetails.put(messageReceiverRef + "/" + saveCurrentDate1 + saveCurrentTime1, messageTextBody);

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("time", saveCurrentDate1 + "-" + saveCurrentTime1);


                        RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(MultipleImagesPickAtivity.this, "he succes", Toast.LENGTH_SHORT).show();

                                    realm.executeTransaction(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {

                                            final DownloadModel id=realm.where(DownloadModel.class).equalTo("id",nextId).findFirst();

                                            id.setIsseen("false");

                                            realm.copyToRealmOrUpdate(id);


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

                                    Toast.makeText(MultipleImagesPickAtivity.this, "Error", Toast.LENGTH_SHORT).show();
                                }


                            }
                        });
                    }
                }
            });
        }


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
                                            Toast.makeText(MultipleImagesPickAtivity.this, "Failed!", Toast.LENGTH_SHORT).show();
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

    private void addItems(String message, String sender, String receiver, String messageId, String bio, String type, String time, String date, String lastSendMessage, String preMessage, String isseen, String imageurl){
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
        downloadModel.setIsseen("One Single");
        downloadModel.setLastSendMessage(lastSendMessage);
        downloadModel.setBio(bio);
        downloadModel.setDate(date);
        downloadModel.setPreMessage(preMessage);
        downloadModel.setTime(time);
        downloadModel.setType("image");
        downloadModel.setImagrUrl(imageurl);


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

        Intent intent = new Intent(MultipleImagesPickAtivity.this, MessageActivity.class);
        intent.putExtra("userid",userid);
        intent.putExtra("name",sqlId);
        startActivity(intent);
        finish();
    }

}