package com.some.notes;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.anstrontechnologies.corehelper.AnstronCoreHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.google.android.exoplayer2.SimpleExoPlayer;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;
import com.some.notes.Adapter.TrimmerUtils;
import com.some.notes.Audio.pickvideo.AudioPickActivity;
import com.some.notes.Audio.pickvideo.StaticFinalValues;
import com.some.notes.Audio.pickvideo.beans.VideoFile;
import com.some.notes.Documents.MainActivity;
import com.some.notes.Documents.Method;
import com.some.notes.Documents.SplashScreen;
import com.some.notes.Documents.StorageUtil;
import com.some.notes.Images.pickvideo.ImagesPickActivity;
import com.some.notes.Images.pickvideo.MultipleImagesPickAtivity;
import com.some.notes.ImoticonsText.Android8.Android8EmoticonProvider;
import com.some.notes.ImoticonsText.EmoticonGIFKeyboardFragment;
import com.some.notes.ImoticonsText.emoticons.Emoticon;
import com.some.notes.ImoticonsText.emoticons.EmoticonSelectListener;
import com.some.notes.ImoticonsText.gifs.Gif;
import com.some.notes.ImoticonsText.gifs.GifSelectListener;
import com.some.notes.ImoticonsText.gipfy.GiphyGifProvider;
import com.some.notes.ImoticonsText.widget.EmoticonEditText;
import com.some.notes.ImoticonsText.widget.EmoticonTextView;
import com.some.notes.Model.CityDataItem;
import com.some.notes.Model.Comment;
import com.some.notes.Model.DownloadModel;
import com.some.notes.Model.ItemClickListener;
import com.some.notes.Model.PathUtil;
import com.some.notes.Model.SampleDataProvider;
import com.some.notes.Model.SqlMessage;
import com.some.notes.Model.SqlUser;
import com.some.notes.Model.User;
import com.some.notes.Notifications.APIService;
import com.some.notes.Notifications.Client;
import com.some.notes.Notifications.Data;
import com.some.notes.Notifications.MyResponse;
import com.some.notes.Notifications.Sender;
import com.some.notes.Notifications.Token;
import com.some.notes.PDF.pickvideo.PdfPickActivity;
import com.some.notes.PhotoEditing.EditImageActivity;
import com.some.notes.PhotoEditing.MultipleImagesEditingactivity;
import com.some.notes.Video.pickvideo.VideoPickActivity;
import com.some.notes.sillicompresser.SiliCompressor;
import com.squareup.picasso.Picasso;


import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity implements ItemClickListener{
    private static final String TAG = "MainActivity";
    private EmoticonGIFKeyboardFragment mEmoticonGIFKeyboardFragment;
    SqlChatsActivity.PersonChatsUserDBHelper helper;
    private static final int PERMISSION_REQUEST_CODE = 101;
    MessageAdapter downloadAdapter;
    List<DownloadModel> downloadModels=new ArrayList<>();
    Realm realm;
    DownloadModel downloads;
    private static final int CREATE_FILE = 1;
    AiSuggetionsAdapter aiSuggetionsAdapter;
    Uri compressUri = null;
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
    TextView  inputPassword, txt_seen, userLastSeen, messageConvert,reply_preview_message,reply_delete,type,smiley_btn,ic_keyboard,ai_text_search,swipeRecyclerview;
    public static FirebaseUser fuser;
    DatabaseReference reference;
    ImageButton btn_send, SendFilesButton,reply_send;
    EditText reply_edit,reply_password;
    EmoticonEditText text_send;
    EmoticonTextView username;
    AppCompatImageView background_image;
 //   private List<DownloadModel> mChat = new ArrayList<>();
    RecyclerView recyclerView, findFriendLis,upload_recycler_view,ai_suggetions;
    Intent intent;
    ValueEventListener seenListener;
    public static String userid;
    String messageid,email, AES = "AES", cheker = "", myUrl = "", saveCurrentTime, saveCurrentDate,saveCurrentTime1, saveCurrentDate1,trans_msg,outputString;
    private static final int RESULT_LOAD_IMAGE = 1;
    APIService apiService;
    boolean notify = false;
    boolean seen = false;
    Dialog myDialog;
    Window window;
  //  private long receivedPersonId;
    private List<CityDataItem> mDataList;
    private MyDataAdapter mDataAdapter;
    private String filter = "";
    ImageView compress_image_view;
    AnstronCoreHelper anstronCoreHelper;
    LinearLayoutManager linearLayoutManager;
    PersonAdapter adapter;
    LinearLayoutManager chat_linear_layout;
    LinearLayoutManager upload_linear_layout;
    int SPLASH_TIME = 250;
    int delay = 300;
    private File storage;
    private String[] storagePaths;
    int count = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        realm = Realm.getDefaultInstance();

        intent = getIntent();
        userid = intent.getStringExtra("userid");

    //    sqlId = intent.getStringExtra("name");
//        try {
//
//            receivedPersonId = intent.getLongExtra("USER_ID", 1);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        initialization();

        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        RootRef = FirebaseDatabase.getInstance().getReference();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);


        myDialog = new Dialog(this);
        if (Build.VERSION.SDK_INT >= 21) {
            window = this.getWindow();
            window.setStatusBarColor(this.getResources().getColor(R.color.white));
        }

        apiService = Client.getClient("https://fcm.googleapis.com").create(APIService.class);

        fileNameList = new ArrayList<>();
        fileDoneList = new ArrayList<>();

        notify = true;

        recyclerView.setHasFixedSize(true);
        upload_recycler_view.setHasFixedSize(true);

        reply_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relative_layout_message.setVisibility(View.VISIBLE);
                reply_bottom.setVisibility(View.GONE);

            }
        });

        mDataList = SampleDataProvider.cityDataItemList;

        findFriendLis.setVisibility(View.GONE);
        LinearLayoutManager linearLayoutTranslate = new LinearLayoutManager(getApplicationContext());
        linearLayoutTranslate.setStackFromEnd(true);
        findFriendLis.setLayoutManager(linearLayoutTranslate);


        mDataAdapter = new MyDataAdapter(mDataList, this);
        findFriendLis.setAdapter(mDataAdapter);


        DatabaseReference  databaseRef = FirebaseDatabase.getInstance().getReference("Chats").child(fuser.getUid()).child(userid);
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String isseen = snapshot.child("isseen").getValue().toString();
                    String sender = snapshot.child("sender").getValue().toString();


                    if ((isseen.equals("true")) && (sender.equals(fuser.getUid()))) {
                        txt_seen.setVisibility(View.VISIBLE);
                        txt_seen.setText("Seen");
                    }
                    else if ((isseen.equals("false")) && (sender.equals(fuser.getUid()))) {
                        txt_seen.setVisibility(View.VISIBLE);
                        txt_seen.setText("deliever");
                    }
                    else if ((sender.equals(userid))) {
                        txt_seen.setVisibility(View.GONE);
                    }
                     else {
                        txt_seen.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        EmoticonGIFKeyboardFragment.EmoticonConfig emoticonConfig = new EmoticonGIFKeyboardFragment.EmoticonConfig()
                .setEmoticonProvider(Android8EmoticonProvider.create())
                .setEmoticonSelectListener(new EmoticonSelectListener() {

                    @Override
                    public void emoticonSelected(Emoticon emoticon) {
                        Log.d(TAG, "emoticonSelected: " + emoticon.getUnicode());
                        text_send.append(emoticon.getUnicode(),
                                text_send.getSelectionStart(),
                                text_send.getSelectionEnd());
                    }

                    @Override
                    public void onBackSpace() {

                    }
                });


        EmoticonGIFKeyboardFragment.GIFConfig gifConfig = new EmoticonGIFKeyboardFragment
                .GIFConfig(GiphyGifProvider.create(this, "564ce7370bf347f2b7c0e4746593c179"))
                .setGifSelectListener(new GifSelectListener() {
                    @Override
                    public void onGifSelected(@NonNull Gif gif) {
                        Log.d(TAG, "onGifSelected: " + gif.getGifUrl());

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

                        String messageId = saveCurrentDate2 +saveCurrentTime2;

                        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

                        NetworkInfo activeNetWork = manager.getActiveNetworkInfo();

                        if (null != activeNetWork) {
                            if (activeNetWork.getType() == ConnectivityManager.TYPE_WIFI) {
                                addText(gif.getPreviewGifUrl(),fuser.getUid(),userid,messageId,trans_msg,"gif",saveCurrentTime,saveCurrentDate,"hello","kya","false",gif.getPreviewGifUrl());

                                sendGifImages(fuser.getUid(), userid, gif.getPreviewGifUrl(), "knij", "jjijj", messageId,saveCurrentTime,saveCurrentDate,saveCurrentTime1,saveCurrentDate1);
                                sendGifImages(fuser.getUid(), userid, gif.getPreviewGifUrl(), "hbuhuj","ugu8u", messageId,saveCurrentTime,saveCurrentDate,saveCurrentTime1,saveCurrentDate1);

                            } else if (activeNetWork.getType() == ConnectivityManager.TYPE_MOBILE) {

                                addText(gif.getPreviewGifUrl(),fuser.getUid(),userid,messageId,trans_msg,"gif",saveCurrentTime,saveCurrentDate,"hello","kya","false",gif.getPreviewGifUrl());

                                sendGifImages(fuser.getUid(), userid, gif.getPreviewGifUrl(), trans_msg, outputString, messageId,saveCurrentTime,saveCurrentDate,saveCurrentTime1,saveCurrentDate1);
                                sendGifImages(fuser.getUid(), userid, gif.getPreviewGifUrl(), trans_msg,"uh8h", messageId,saveCurrentTime,saveCurrentDate,saveCurrentTime1,saveCurrentDate1);

                            }
                        } else {
                            addText(gif.getPreviewGifUrl(),fuser.getUid(),userid,messageId,trans_msg,"gif",saveCurrentTime,saveCurrentDate,"hello","kya","never",gif.getPreviewGifUrl());

                        }

                    }
                });


        mEmoticonGIFKeyboardFragment = EmoticonGIFKeyboardFragment
                .getNewInstance(findViewById(R.id.keyboard_container), emoticonConfig, gifConfig);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.keyboard_container, mEmoticonGIFKeyboardFragment)
                .commit();

        ic_keyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ic_keyboard.setVisibility(View.GONE);
                smiley_btn.setVisibility(View.VISIBLE);
                mEmoticonGIFKeyboardFragment.emoticonClose(MessageActivity.this);
                UIUtil.showKeyboard(MessageActivity.this,text_send);
            }
        });

        smiley_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ic_keyboard.setVisibility(View.VISIBLE);
                smiley_btn.setVisibility(View.GONE);
                UIUtil.hideKeyboard(MessageActivity.this);
                mEmoticonGIFKeyboardFragment.open(MessageActivity.this);
            }
        });

        text_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ic_keyboard.setVisibility(View.GONE);
                smiley_btn.setVisibility(View.VISIBLE);
                mEmoticonGIFKeyboardFragment.emoticonClose(MessageActivity.this);
                UIUtil.showKeyboard(MessageActivity.this,text_send);
            }
        });

        helper = new SqlChatsActivity.PersonChatsUserDBHelper(this);

     //   SqlUser queriedPerson = helper.getPerson(receivedPersonId);

        username.setEmoticonProvider(Android8EmoticonProvider.create());
        username.setEmoticonSize(65);

//        username.setText(queriedPerson.getFullname());
//
//        Picasso.get().load(queriedPerson.getImageurl()).into(profile_image);
//        Picasso.get().load(queriedPerson.getImageurl()).into(background_image);
//

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                final DownloadModel downloads = realm.where(DownloadModel.class).equalTo("receiver",userid).and().equalTo("preMessage", fuser.getUid()).findFirst();
                username.setText(downloads.getMessage());

                sqlId = downloads.getFile_path();
                email = downloads.getUsername();

                Picasso.get().load(downloads.getImagrUrl()).into(profile_image);
                Picasso.get().load(downloads.getImagrUrl()).into(background_image);

            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEmoticonGIFKeyboardFragment.toggle(MessageActivity.this);
            }
        });

        seen = true;

        checkPermission();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Realm realm=Realm.getDefaultInstance();
                RealmResults<DownloadModel> result = realm.where(DownloadModel.class).equalTo("receiver", userid).and().equalTo("sender",fuser.getUid()).or().equalTo("receiver", fuser.getUid()).and().equalTo("sender",userid).findAll();

                List<DownloadModel> downloadModelsLocal = getAllDownloads();
                if (downloadModelsLocal != null) {
                    if (downloadModelsLocal.size() > 0) {
                        downloadModels.addAll(downloadModelsLocal);

                        for (int i = 0; i < downloadModels.size(); i++) {
                            if (downloadModels.get(i).getStatus().equalsIgnoreCase("Pending") || downloadModels.get(i).getStatus().equalsIgnoreCase("Running") || downloadModels.get(i).getStatus().equalsIgnoreCase("Downloading")) {
                                DownloadStatusTask downloadStatusTask = new DownloadStatusTask(downloadModels.get(i));
                                runTask(downloadStatusTask, "" + downloadModels.get(i).getDownloadId());
                            }
                        }
                    }
                }
            }
        }, SPLASH_TIME);


        downloadAdapter = new MessageAdapter(MessageActivity.this, downloadModels);
        chat_linear_layout = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(chat_linear_layout);
        recyclerView.setAdapter(downloadAdapter);
        chat_linear_layout.setStackFromEnd(true);

        adapter = new PersonAdapter(MessageActivity.this, downloadModels);
        upload_linear_layout = new LinearLayoutManager(this);
        upload_linear_layout.setStackFromEnd(true);
        upload_recycler_view.setHasFixedSize(true);
        upload_recycler_view.setLayoutManager(upload_linear_layout);
        upload_recycler_view.setAdapter(adapter);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                downloadAdapter.notifyDataSetChanged();
                readMessagges();
            }
        }, delay);

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
                                                   pdf = findViewById(R.id.pdf1);
                                                   pdfs = (LinearLayout) findViewById(R.id.pdfs);
                                                   ms_file = (LinearLayout) findViewById(R.id.ms_word);
                                                   ms_files = (LinearLayout) findViewById(R.id.ms_words);
                                                   txtclose.setText("X");

                                                   txtclose.setOnClickListener(new View.OnClickListener() {
                                                       @Override
                                                       public void onClick(View v) {
                                                           relativeLayout.setVisibility(View.GONE);
                                                       }
                                                   });

                                                   image.setOnClickListener(new View.OnClickListener() {
                                                       @Override
                                                       public void onClick(View v) {
                                                           relativeLayout.setVisibility(View.GONE);

                                                           Intent intent2 = new Intent(MessageActivity.this, ImagesPickActivity.class);
                                                           intent2.putExtra("userid", userid);
                                                           intent2.putExtra("name", sqlId);
                                                           startActivityForResult(intent2, StaticFinalValues.REQUEST_CODE_PICK_VIDEO);

                                                       }
                                                   });

                                                   images.setOnClickListener(new View.OnClickListener() {
                                                       @Override
                                                       public void onClick(View v) {
                                                           relativeLayout.setVisibility(View.GONE);
                                                           cheker = "image";
                                                           Intent intent2 = new Intent(MessageActivity.this, MultipleImagesPickAtivity.class);
                                                           intent2.putExtra("userid", userid);
                                                           intent2.putExtra("name", sqlId);
                                                           startActivityForResult(intent2, StaticFinalValues.REQUEST_CODE_PICK_VIDEO);


//                                                           Intent intent = new Intent();
//                                                           intent.setAction(Intent.ACTION_GET_CONTENT);
//                                                           intent.setType("image/*");
//                                                           intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//                                                           startActivityForResult(intent.createChooser(intent, "Select Ms World File"), RESULT_LOAD_IMAGE);

                                                       }
                                                   });

                                                   video.setOnClickListener(new View.OnClickListener() {
                                                       @Override
                                                       public void onClick(View v) {
                                                           relativeLayout.setVisibility(View.GONE);
                                                           cheker = "video";
                                                           Intent intent2 = new Intent(MessageActivity.this, VideoPickActivity.class);
                                                           intent2.putExtra("userid", userid);
                                                           intent2.putExtra("name", sqlId);
                                                           startActivityForResult(intent2, StaticFinalValues.REQUEST_CODE_PICK_VIDEO);

//                                                           Intent intent = new Intent();
//                                                           intent.setAction(Intent.ACTION_GET_CONTENT);
//                                                           intent.setType("video/*");
//                                                           startActivityForResult(intent.createChooser(intent, "Select Ms World File"), 438);

                                                       }
                                                   });


                                                   pdf.setOnClickListener(new View.OnClickListener() {
                                                       @Override
                                                       public void onClick(View v) {
                                                           relativeLayout.setVisibility(View.GONE);
                                                           cheker = "PDF";


                                                           Intent intent2 = new Intent(MessageActivity.this, PdfPickActivity.class);
                                                           intent2.putExtra("userid", userid);
                                                           intent2.putExtra("name", sqlId);
                                                           startActivityForResult(intent2, StaticFinalValues.REQUEST_CODE_PICK_VIDEO);


                                                       }
                                                   });

                                                   pdfs.setOnClickListener(new View.OnClickListener() {
                                                       @Override
                                                       public void onClick(View v) {
                                                           relativeLayout.setVisibility(View.GONE);
                                                           cheker = "PDF";
                                                           Intent intent = new Intent();
                                                           intent.setAction(Intent.ACTION_GET_CONTENT);
                                                           intent.setType("application/pdf");
                                                           intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                                           startActivityForResult(intent.createChooser(intent, "Select Ms World File"), RESULT_LOAD_IMAGE);

                                                       }
                                                   });

                                                   ms_file.setOnClickListener(new View.OnClickListener() {
                                                       @Override
                                                       public void onClick(View v) {
                                                           relativeLayout.setVisibility(View.GONE);
                                                           cheker = "docx";
                                                           Intent intent2 = new Intent(MessageActivity.this, MainActivity.class);
                                                           intent2.putExtra("userid", userid);
                                                           intent2.putExtra("name", sqlId);
                                                           startActivity(intent2);

//                                                           Intent intent = new Intent();
//                                                           intent.setAction(Intent.ACTION_GET_CONTENT);
//                                                           intent.setType("application/*");
//                                                           startActivityForResult(intent.createChooser(intent, "Select PDF"), 438);

                                                       }
                                                   });

                                                   ms_files.setOnClickListener(new View.OnClickListener() {
                                                       @Override
                                                       public void onClick(View v) {
                                                           relativeLayout.setVisibility(View.GONE);
                                                           cheker = "docx";
                                                           Intent intent = new Intent();
                                                           intent.setAction(Intent.ACTION_GET_CONTENT);
                                                           intent.setType("docx/*");
                                                           intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                                           startActivityForResult(intent.createChooser(intent, "Select Ms World File"), RESULT_LOAD_IMAGE);

                                                       }
                                                   });

                                               }
                                           });

        DisplayLastSeen();

        swipeRecyclerview= findViewById(R.id.swipeRecyclerview);

        swipeRecyclerview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mySuperIntent = new Intent(MessageActivity.this, MessageActivity.class);
                mySuperIntent.putExtra("userid",userid);
                startActivity(mySuperIntent);
                finish();
            }
        });

        text_send.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().trim().length() == 0) {
                    typingStatus("noOne");
                    SendFilesButton.setVisibility(View.VISIBLE);
                    btn_send.setVisibility(View.GONE);
                    findFriendLis.setVisibility(View.GONE);
                    findViewById(R.id.btnSpeak).setVisibility(View.VISIBLE);
                    inputPassword.setVisibility(View.GONE);


                } else {
                    SendFilesButton.setVisibility(View.GONE);
                    btn_send.setVisibility(View.VISIBLE);
                    findViewById(R.id.btnSpeak).setVisibility(View.GONE);
                    findFriendLis.setVisibility(View.VISIBLE);
                   typingStatus(fuser.getUid());
                   btn_send.setEnabled(true);
                    mDataAdapter.getFilter().filter(s);
                    inputPassword.setVisibility(View.VISIBLE);
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

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reply_edit.setText(text_send.getText().toString());
                text_send.setText("");

                notify = true;


                String msg = reply_edit.getText().toString();

                if (!msg.equals("")) {


                    try {
                        msg = encrypt(reply_edit.getText().toString(), inputPassword.getText().toString());
                        trans_msg = encrypt(messageConvert.getText().toString(), inputPassword.getText().toString());


                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    try {
                        outputString = decrypt(text_send.getText().toString(), inputPassword.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();

                    }


                    messageConvert.setText("");

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

                    String messageId = saveCurrentDate2 +saveCurrentTime2;

                    ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

                    NetworkInfo activeNetWork = manager.getActiveNetworkInfo();

                    if (null != activeNetWork) {
                        if (activeNetWork.getType() == ConnectivityManager.TYPE_WIFI) {
                          //  addTextOnFirebase(msg,fuser.getUid(),userid,messageId,trans_msg,"text",saveCurrentTime,saveCurrentDate,"hello","kya","false",msg);

                            sendMessage(fuser.getUid(), userid, msg, trans_msg, outputString, messageId,saveCurrentTime,saveCurrentDate,saveCurrentTime1,saveCurrentDate1);
                            sendMessageReciever(fuser.getUid(), userid, msg, trans_msg, messageId,saveCurrentTime,saveCurrentDate,saveCurrentTime1,saveCurrentDate1);

                        } else if (activeNetWork.getType() == ConnectivityManager.TYPE_MOBILE) {

                       //     addTextOnFirebase(msg,fuser.getUid(),userid,messageId,trans_msg,"text",saveCurrentTime,saveCurrentDate,"hello","kya","false",msg);

                            sendMessage(fuser.getUid(), userid, msg, trans_msg, outputString, messageId,saveCurrentTime,saveCurrentDate,saveCurrentTime1,saveCurrentDate1);
                            sendMessageReciever(fuser.getUid(), userid, msg, trans_msg, messageId,saveCurrentTime,saveCurrentDate,saveCurrentTime1,saveCurrentDate1);

                        }
                    } else {
                        addTextOnFirebase(msg,fuser.getUid(),userid,messageId,trans_msg,"text",saveCurrentTime,saveCurrentDate,"hello","kya","never",msg);

                    }

                } else {
                    Toast.makeText(MessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
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
                if (!msg.equals("")){


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

                    String messageId = saveCurrentDate2 +saveCurrentTime2;



                    ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

                    NetworkInfo activeNetWork = manager.getActiveNetworkInfo();

                    if (null != activeNetWork) {
                        if (activeNetWork.getType() == ConnectivityManager.TYPE_WIFI) {
                            addText(msg,fuser.getUid(),userid,messageId,trans_msg,ret_type,saveCurrentTime,saveCurrentDate,"hello",ret_preview_message,"false",msg);

                            ReplyMessage(fuser.getUid(), userid, msg, trans_msg, outputString, ret_type, ret_preview_message, messageId);
                            ReplyMessageReciever(fuser.getUid(), userid, msg, trans_msg, ret_type, ret_preview_message, messageId);

                            Toast.makeText(MessageActivity.this, "wifi Enabled", Toast.LENGTH_SHORT).show();
                        } else if (activeNetWork.getType() == ConnectivityManager.TYPE_MOBILE) {

                            addText(msg,fuser.getUid(),userid,messageId,trans_msg,ret_type,saveCurrentTime,saveCurrentDate,"hello",ret_preview_message,"false",msg);

                            ReplyMessage(fuser.getUid(), userid, msg, trans_msg, outputString, ret_type, ret_preview_message, messageId);
                            ReplyMessageReciever(fuser.getUid(), userid, msg, trans_msg, ret_type, ret_preview_message, messageId);

                            Toast.makeText(MessageActivity.this, "data Network Enabled", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        addText(msg,fuser.getUid(),userid,messageId,trans_msg,ret_type,saveCurrentTime,saveCurrentDate,"hello",ret_preview_message,"never",msg);

                    }


                } else {
                    Toast.makeText(MessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }

            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        seenMessage(userid);

    }

    private void initialization(){
        recyclerView = findViewById(R.id.recycler_view);
        upload_recycler_view = findViewById(R.id.upload_recycler_view);
       // ai_suggetions = findViewById(R.id.ai_suggetions);
        compress_image_view = findViewById(R.id.compress_image_view);
        userLastSeen = (TextView) findViewById(R.id.custom_user_last_seen);
        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);
        text_send.setEmoticonProvider(Android8EmoticonProvider.create());
        text_send.setEmoticonSize(50);
        btn_send.setEnabled(true);
        smiley_btn = findViewById(R.id.smiley_btn);
        ic_keyboard = findViewById(R.id.keyboard_btn);
        inputPassword = findViewById(R.id.password);
        reply_bottom = findViewById(R.id.reply_bottom);
        reply_bottom.setVisibility(View.GONE);
        reply_edit = findViewById(R.id.reply_edit);
        reply_image = findViewById(R.id.reply_image);
        reply_send = findViewById(R.id.reply_Send);
        type = findViewById(R.id.type);
        txt_seen = findViewById(R.id.txt_seen);
        background_image = findViewById(R.id.background_image);
        reply_preview_message = findViewById(R.id.preview_message);
        relative_layout_message = findViewById(R.id.relative_layout_message);
        reply_delete = findViewById(R.id.reply_delete);
        reply_password = findViewById(R.id.reply_password);
        findFriendLis = findViewById(R.id.search_translate);
        getText_btn = findViewById(R.id.btn_gettext);
        showImage_img = findViewById(R.id.img_imageview);
        messageConvert = findViewById(R.id.message_convert);


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

    private  void deleteSingleUserMessage(){

        DatabaseReference rf = FirebaseDatabase.getInstance().getReference("DeleteSingleUserMessage").child(userid).child(fuser.getUid());

        rf.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot :dataSnapshot.getChildren()){
                    if (snapshot.child("messageID").exists()) {

                        String messageID = snapshot.child("messageID").getValue().toString();
                        String id = snapshot.child("id").getValue().toString();

                     //   dbHelper.deletePersonRecord(messageID, MessageActivity.this, id);

                    }
                    else {
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void opendocument(String pickerInitialUri) {
        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        File fileWithinMyDir = new File(pickerInitialUri);

        intentShareFile.setType("application/pdf");
        intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse(pickerInitialUri));

        intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                "Sharing File...");
        intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");

        startActivity(Intent.createChooser(intentShareFile, "Share File"));

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


        SqlMessage sql = new SqlMessage(message,sender,receiver,saveCurrentDate1+saveCurrentTime1, uid,rep_type,saveCurrentTime,saveCurrentDate, message, preview_message,"false","hello");


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
                                            Toast.makeText(MessageActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
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

    private void readMessagges(){
        DatabaseReference  databaseRef = FirebaseDatabase.getInstance().getReference("Chats").child(fuser.getUid()).child(userid);
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child(sqlId).exists()){
                    }
                    else {

                        String messageId = snapshot.child("messageID").getValue().toString();
                        String time = snapshot.child("time").getValue().toString();
                        String date = snapshot.child("date").getValue().toString();
                        String sender = snapshot.child("sender").getValue().toString();
                        String receiver = snapshot.child("receiver").getValue().toString();
                        String isseen = snapshot.child("isseen").getValue().toString();
                        String message = snapshot.child("message").getValue().toString();
                        String bio = snapshot.child("bio").getValue().toString();
                        String type = snapshot.child("type").getValue().toString();
                        String preMessage = snapshot.child("preMessage").getValue().toString();
                        String lastSendMessage = snapshot.child("lastSendMessage").getValue().toString();
                        String title = snapshot.child("title").getValue().toString();

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put(sqlId, true);
                        databaseRef.child(messageId).updateChildren(hashMap);


                        if ((type.equals("text")) || (type.equals("rep_type")) || (type.equals("gif")))
                        {
                            addText(message,sender,receiver,messageId,bio,type,time,date,lastSendMessage,preMessage,isseen,message);
                        }
                        else if ((type.equals("video")) || (type.equals("PDF")) || (type.equals("docx")))
                        {
                            addMediaFiles(message,sender,receiver,messageId,bio,type,time,date,lastSendMessage,preMessage,"false",message,title);

                            Intent intent = new Intent(MessageActivity.this, MessageActivity.class);
                            intent.putExtra("userid", userid);
                            startActivity(intent);
                            finish();
                        }
                        else if (type.equals("image")) {
                            downloadImage(message,sender,receiver,messageId, bio,type,time,date, lastSendMessage, preMessage,isseen,message);
                        }
                        else {

                        }

                        currentItems =   chat_linear_layout.getChildCount();
                        totalItems =     chat_linear_layout.getItemCount();
                        scrollOutItems = chat_linear_layout.findFirstVisibleItemPosition();

                        if (currentItems + scrollOutItems == totalItems){
                            recyclerView.scrollToPosition(downloadModels.size()-1);
                        }
                        if (currentItems +scrollOutItems !=totalItems)
                        {

                        }

//                        if(currentItems+scrollOutItems == totalItems){
//                            populaterecyclerView(filter);
//                            adapter.notifyDataSetChanged();
//                        }
//                        else {
//
//                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        status("online");
    }

    private void updateseenmessageMesagges(String messageId,long updateId) {

//        DatabaseReference  databaseRef = FirebaseDatabase.getInstance().getReference("Chats").child(fuser.getUid()).child(userid).child(messageId);
//        databaseRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//
//                Comment user = dataSnapshot.getValue(Comment.class);
//                if (dataSnapshot.child("isseen").exists()){
//
//                    String isseen = dataSnapshot.child("isseen").getValue().toString();
//                    if (isseen.equals("true")){
//                 //       dbHelper.updateIsseenRecord(updateId,MessageActivity.this,"true");
//
//                    }
//
//                }
//                else {
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//        status("online");
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
        unregisterReceiver(onComplete);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        status("offline");
        typingStatus("noOne");
        seen = false;

        Intent intent = new Intent(MessageActivity.this,SqlChatsActivity.class);
        intent.putExtra("name",email);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        status("offline");
        typingStatus("noOne");
        seen = false;

    }

    String videoFileName;
    @Override
    protected void onActivityResult(int requestCode, final int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case StaticFinalValues.REQUEST_CODE_PICK_VIDEO:
                if (resultCode == RESULT_OK) {
                    ArrayList<VideoFile> list = data.getParcelableArrayListExtra(StaticFinalValues.RESULT_PICK_VIDEO);
                    for (VideoFile file : list) {
                        videoFileName = file.getPath();
                    }

                    try {
                        MediaPlayer player = new MediaPlayer();
                        player.setDataSource(videoFileName);
                        player.prepare();
                        int duration = player.getDuration();
                        player.release();
                        int s = duration / 1000;
                        int hour = s / 3600;
                        int minute = s % 3600 / 60;
                        int second = s % 60;
                        Log.e(TAG, "视频文件长度,分钟: " + minute + "视频有" + s + "秒");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
                break;
        }
        Calendar calForDate1 = Calendar.getInstance();
        SimpleDateFormat currendateFormat1 = new SimpleDateFormat("yyyy-MM");
        saveCurrentDate1 = currendateFormat1.format(calForDate1.getTime());

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {

            if (data.getClipData() != null) {

                int totalItemsSelected = data.getClipData().getItemCount();

                for (int i = 0; i < totalItemsSelected; i++) {

                    Uri fileUri = data.getClipData().getItemAt(i).getUri();

                    String fileName = getFileName(fileUri);

                    if (cheker.equals("PDF")) {

                        notify = true;

                        String dataUri = data.getDataString();
                        if (dataUri!=null&&(dataUri.contains("jpg")||dataUri.contains("png")||dataUri.contains("jpeg")
                        ||dataUri.contains("gif")||dataUri.contains("image")||dataUri.contains("video")||dataUri.contains("audio")
                        ||dataUri.contains("mp3")||dataUri.contains("mp4")||dataUri.contains("docx"))){
                            Toast.makeText(this, "please select pdf file", Toast.LENGTH_SHORT).show();
                        }

                        else {

                            String messagePushID = generateString(12);

                            saveCurrentDate1 = currendateFormat1.format(calForDate1.getTime());

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

                                    addPdfs(fileUri.toString(), fuser.getUid(), userid, messageID, "kt5rg2/r78I081y/kXkhw==\n", "pdf", saveCurrentTime, saveCurrentDate, "hello", "kya", "panding", fileUri.toString(), fileUri);
                                } else if (activeNetWork.getType() == ConnectivityManager.TYPE_MOBILE) {

                                    addPdfs(fileUri.toString(), fuser.getUid(), userid, messageID, "kt5rg2/r78I081y/kXkhw==\n", "pdf", saveCurrentTime, saveCurrentDate, "hello", "kya", "panding", fileUri.toString(), fileUri);
                                }
                            } else {
                                addItemInRealmNoInternet(fileUri.toString(), fuser.getUid(), userid, messageID, "kt5rg2/r78I081y/kXkhw==\n", "pdf", saveCurrentTime, saveCurrentDate, "hello", "kya", "never", fileUri.toString());

                                Toast.makeText(MessageActivity.this, "you are not connected with Internet Connection", Toast.LENGTH_SHORT).show();
                            }


                        }

                    }

                    else if (cheker.equals("docx")){

                        notify = true;

                        String dataUri = data.getDataString();

                        if (dataUri!=null&&(dataUri.contains("jpg")||dataUri.contains("png")||dataUri.contains("jpeg")
                                ||dataUri.contains("gif")||dataUri.contains("image")||dataUri.contains("video")||dataUri.contains("audio")
                                ||dataUri.contains("mp3")||dataUri.contains("mp4")||dataUri.contains("pdf"))){
                            Toast.makeText(this, "please select docx file", Toast.LENGTH_SHORT).show();
                        }
                        else {


                            String messagePushID = generateString(12);

                            saveCurrentDate1 = currendateFormat1.format(calForDate1.getTime());

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

                                    addDocx(fileUri.toString(), fuser.getUid(), userid, messageID, "kt5rg2/r78I081y/kXkhw==\n", "docx", saveCurrentTime, saveCurrentDate, "hello", "kya", "panding", fileUri.toString(), fileUri);
                                } else if (activeNetWork.getType() == ConnectivityManager.TYPE_MOBILE) {

                                    addDocx(fileUri.toString(), fuser.getUid(), userid, messageID, "kt5rg2/r78I081y/kXkhw==\n", "docx", saveCurrentTime, saveCurrentDate, "hello", "kya", "panding", fileUri.toString(), fileUri);
                                }
                            } else {
                                addItemInRealmNoInternet(fileUri.toString(), fuser.getUid(), userid, messageID, "kt5rg2/r78I081y/kXkhw==\n", "docx", saveCurrentTime, saveCurrentDate, "hello", "kya", "never", fileUri.toString());

                                Toast.makeText(MessageActivity.this, "you are not connected with Internet Connection", Toast.LENGTH_SHORT).show();
                            }

                        }

                    }
                    else if (cheker.equals("image")) {
                        notify = true;


                        saveCurrentDate1 = currendateFormat1.format(calForDate1.getTime());

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


                        new ImageCompressionAsyncTask(MessageActivity.this).execute(fileUri.toString(),
                                Environment.getExternalStorageDirectory() + "/Study Chats/images");
                    }

                    else if (cheker.equals("video")) {
                        notify = true;


                        saveCurrentDate1 = currendateFormat1.format(calForDate1.getTime());

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

                        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "Study chats";

                        int fileNo = 0;
                        String Name = "trimmed_video_";
                        File newFile = new File(path + File.separator +
                                (Name + fileNo) + "." + TrimmerUtils.getFileExtension(this, fileUri));
                        while (newFile.exists()) {
                            fileNo++;
                            newFile = new File(path + File.separator +
                                    (Name + fileNo) + "." + TrimmerUtils.getFileExtension(this, fileUri));

                        }
                        Toast.makeText(MessageActivity.this, newFile.toString(), Toast.LENGTH_LONG).show();

                            ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo activeNetWork = manager.getActiveNetworkInfo();


                        String messageID = saveCurrentDate2 +saveCurrentTime2;

                        if (null!=activeNetWork){
                            if (activeNetWork.getType() == ConnectivityManager.TYPE_WIFI){

                                addVideo(fileUri.toString(),fuser.getUid(),userid,messageID,"kt5rg2/r78I081y/kXkhw==\n","video",saveCurrentTime,saveCurrentDate,"hello","kya","panding",fileUri.toString(),fileUri);
                            }
                            else if (activeNetWork.getType() == ConnectivityManager.TYPE_MOBILE){

                                addVideo(fileUri.toString(),fuser.getUid(),userid,messageID,"kt5rg2/r78I081y/kXkhw==\n","video",saveCurrentTime,saveCurrentDate,"hello","kya","panding",fileUri.toString(),fileUri);
                            }
                        }
                        else {
                            addItemInRealmNoInternet(fileUri.toString(),fuser.getUid(),userid,messageID,"kt5rg2/r78I081y/kXkhw==\n","video",saveCurrentTime,saveCurrentDate,"hello","kya","never",fileUri.toString());

                            Toast.makeText(MessageActivity.this, "you are not connected with Internet Connection", Toast.LENGTH_SHORT).show();
                        }

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

        if (requestCode == 438 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            fileUri = data.getData();

            if (cheker.equals("PDF")) {
                notify = true;

               File file = new File(fileUri.toString());

               String pt = file.getAbsolutePath();

                Toast.makeText(MessageActivity.this, file.toString(), Toast.LENGTH_SHORT).show();


                String dataUri = data.getDataString();
                if (dataUri!=null&&(dataUri.contains("jpg")||dataUri.contains("png")||dataUri.contains("jpeg")
                        ||dataUri.contains("gif")||dataUri.contains("image")||dataUri.contains("video")||dataUri.contains("audio")
                        ||dataUri.contains("mp3")||dataUri.contains("mp4")||dataUri.contains("docx")||dataUri.contains("ppt")))
                {
                }

                else
                    {

                    saveCurrentDate1 = currendateFormat1.format(calForDate1.getTime());

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


                    RootRef = FirebaseDatabase.getInstance().getReference();
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(saveCurrentDate1);

                    final String messageSenderRef = "Chats/" + fuser.getUid() + "/" + userid;
                    final String messageReceiverRef = "Chats/" + userid + "/" + fuser.getUid();

                    DatabaseReference userMessageKeyRef = RootRef.child("Chats")
                            .child(fuser.getUid()).child(userid).push();


                    ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetWork = manager.getActiveNetworkInfo();

                    String messageID = saveCurrentDate2 + saveCurrentTime2;

                    if (null != activeNetWork) {
                        if (activeNetWork.getType() == ConnectivityManager.TYPE_WIFI) {

                            addPdfs(fileUri.toString(), fuser.getUid(), userid, messageID, "kt5rg2/r78I081y/kXkhw==\n", "pdf", saveCurrentTime, saveCurrentDate, "hello", "kya", "panding", fileUri.toString(), fileUri);
                        } else if (activeNetWork.getType() == ConnectivityManager.TYPE_MOBILE) {

                            addPdfs(fileUri.toString(), fuser.getUid(), userid, messageID, "kt5rg2/r78I081y/kXkhw==\n", "pdf", saveCurrentTime, saveCurrentDate, "hello", "kya", "panding", fileUri.toString(), fileUri);
                        }
                    } else {
                        addItemInRealmNoInternet(fileUri.toString(), fuser.getUid(), userid, messageID, "kt5rg2/r78I081y/kXkhw==\n", "pdf", saveCurrentTime, saveCurrentDate, "hello", "kya", "never", fileUri.toString());

                        Toast.makeText(MessageActivity.this, "you are not connected with Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }

            }
            else if (cheker.equals("docx")) {

                notify = true;

                String dataUri = data.getDataString();

                if (dataUri!=null&&(dataUri.contains("jpg")||dataUri.contains("png")||dataUri.contains("jpeg")
                        ||dataUri.contains("gif")||dataUri.contains("image")||dataUri.contains("video")||dataUri.contains("audio")
                        ||dataUri.contains("mp3")||dataUri.contains("mp4")||dataUri.contains("pdf")||dataUri.contains("ppt")))
                {
                    Toast.makeText(this, "please select docx file", Toast.LENGTH_SHORT).show();
                }
                else {


                    saveCurrentDate1 = currendateFormat1.format(calForDate1.getTime());

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

                            addDocx(fileUri.toString(), fuser.getUid(), userid, messageID, "kt5rg2/r78I081y/kXkhw==\n", "docx", saveCurrentTime, saveCurrentDate, "hello", "kya", "panding", fileUri.toString(), fileUri);
                        } else if (activeNetWork.getType() == ConnectivityManager.TYPE_MOBILE) {

                            addDocx(fileUri.toString(), fuser.getUid(), userid, messageID, "kt5rg2/r78I081y/kXkhw==\n", "docx", saveCurrentTime, saveCurrentDate, "hello", "kya", "panding", fileUri.toString(), fileUri);
                        }
                    } else {
                        addItemInRealmNoInternet(fileUri.toString(), fuser.getUid(), userid, messageID, "kt5rg2/r78I081y/kXkhw==\n", "docx", saveCurrentTime, saveCurrentDate, "hello", "kya", "never", fileUri.toString());

                        Toast.makeText(MessageActivity.this, "you are not connected with Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }

            }

            else if (cheker.equals("video")) {

                notify = true;

                Calendar calFor = Calendar.getInstance();
                SimpleDateFormat currendate = new SimpleDateFormat("yyyy-MM");
                saveCurrentDate1 = currendate.format(calFor.getTime());

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

                String messageID = saveCurrentDate2 +saveCurrentTime2;

                ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetWork = manager.getActiveNetworkInfo();

                if (null!=activeNetWork){
                    if (activeNetWork.getType() == ConnectivityManager.TYPE_WIFI){

                        addVideo(fileUri.toString(),fuser.getUid(),userid,messageID,"kt5rg2/r78I081y/kXkhw==\n","video",saveCurrentTime,saveCurrentDate,"hello","kya","panding",fileUri.toString(),fileUri);
                    }
                    else if (activeNetWork.getType() == ConnectivityManager.TYPE_MOBILE){

                        addVideo(fileUri.toString(),fuser.getUid(),userid,messageID,"kt5rg2/r78I081y/kXkhw==\n","video",saveCurrentTime,saveCurrentDate,"hello","kya","panding",fileUri.toString(),fileUri);
                    }

                }
                else {
                    addItemInRealmNoInternet(fileUri.toString(),fuser.getUid(),userid,messageID,"kt5rg2/r78I081y/kXkhw==\n","video",saveCurrentTime,saveCurrentDate,"hello","kya","never",fileUri.toString());

                    Toast.makeText(MessageActivity.this, "you are not connected with Internet Connection", Toast.LENGTH_SHORT).show();
                }

            }

            else {

                Toast.makeText(this, "nothing selected, Error.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class ImageCompressionAsyncTask extends AsyncTask<String, Void, String> {
        Context mContext;
        int position;

        public ImageCompressionAsyncTask(Context context) {
            mContext = context;
        }

        @Override
        protected String doInBackground(String... params) {
            return SiliCompressor.with(mContext).compress(params[0], new File(params[1]));
        }

        @Override
        protected void onPostExecute(String s) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                compressUri = Uri.parse(s);

                Cursor c = getContentResolver().query(compressUri, null, null, null, null);
                c.moveToFirst();
               } else {
                File imageFile = new File(s);
                compressUri = Uri.fromFile(imageFile);
                }


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

            addImages(compressUri.toString(),fuser.getUid(),userid,saveCurrentDate2 +saveCurrentTime2,"kt5rg2/r78I081y/kXkhw==\n",cheker,saveCurrentTime,saveCurrentDate,"hello","kya","false",compressUri.toString());

            adapter.notifyDataSetChanged();
            downloadAdapter.notifyDataSetChanged();
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

    public class MyDataAdapter extends RecyclerView.Adapter<MyDataAdapter.MyViewHolder> implements Filterable {

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
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(mContext).inflate(R.layout.trans_search,parent,false);

            return new MyViewHolder(view);

        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

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

                        String messageId = saveCurrentDate2 +saveCurrentTime2;

                        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo activeNetWork = manager.getActiveNetworkInfo();

                        if (null != activeNetWork) {
                            if (activeNetWork.getType() == ConnectivityManager.TYPE_WIFI) {
                                addText(msg,fuser.getUid(),userid,messageId,trans_msg,"text",saveCurrentTime,saveCurrentDate,"hello","kya","false",msg);

                                sendMessage(fuser.getUid(), userid, msg, trans_msg, outputString, messageId,saveCurrentTime,saveCurrentDate,saveCurrentTime1,saveCurrentDate1);
                                sendMessageReciever(fuser.getUid(), userid, msg, trans_msg, messageId,saveCurrentTime,saveCurrentDate,saveCurrentTime1,saveCurrentDate1);

                            } else if (activeNetWork.getType() == ConnectivityManager.TYPE_MOBILE) {

                                addText(msg,fuser.getUid(),userid,messageId,trans_msg,"text",saveCurrentTime,saveCurrentDate,"hello","kya","false",msg);

                                sendMessage(fuser.getUid(), userid, msg, trans_msg, outputString, messageId,saveCurrentTime,saveCurrentDate,saveCurrentTime1,saveCurrentDate1);
                                sendMessageReciever(fuser.getUid(), userid, msg, trans_msg, messageId,saveCurrentTime,saveCurrentDate,saveCurrentTime1,saveCurrentDate1);

                                Toast.makeText(MessageActivity.this, "data Network Enabled", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            addText(msg,fuser.getUid(),userid,messageId,trans_msg,"text",saveCurrentTime,saveCurrentDate,"hello","kya","never",msg);
                        }

                    } else {
                        Toast.makeText(MessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
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

    public class AiSuggetionsAdapter extends RecyclerView.Adapter<AiSuggetionsAdapter.MyViewHolder> implements Filterable {
        public static final String ITEM_KEY = "item_key";
        private List<CityDataItem> mDataList;
        private List<CityDataItem> mDataListFull;
        private Context mContext;

        public AiSuggetionsAdapter(List<CityDataItem> mDataList, Context mContext) {
            this.mDataList = mDataList;
            this.mContext = mContext;
            mDataListFull=new ArrayList<>();
            mDataListFull.addAll(mDataList);
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(mContext).inflate(R.layout.trans_search,parent,false);
            return new MyViewHolder(view);

        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

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

                        String messageId = saveCurrentDate2 +saveCurrentTime2;

                        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo activeNetWork = manager.getActiveNetworkInfo();

                        if (null != activeNetWork) {
                            if (activeNetWork.getType() == ConnectivityManager.TYPE_WIFI) {
                                addText(msg,fuser.getUid(),userid,messageId,trans_msg,"text",saveCurrentTime,saveCurrentDate,"hello","kya","false",msg);

                                sendMessage(fuser.getUid(), userid, msg, trans_msg, outputString, messageId,saveCurrentTime,saveCurrentDate,saveCurrentTime1,saveCurrentDate1);
                                sendMessageReciever(fuser.getUid(), userid, msg, trans_msg, messageId,saveCurrentTime,saveCurrentDate,saveCurrentTime1,saveCurrentDate1);

                            } else if (activeNetWork.getType() == ConnectivityManager.TYPE_MOBILE) {

                                addText(msg,fuser.getUid(),userid,messageId,trans_msg,"text",saveCurrentTime,saveCurrentDate,"hello","kya","false",msg);

                                sendMessage(fuser.getUid(), userid, msg, trans_msg, outputString, messageId,saveCurrentTime,saveCurrentDate,saveCurrentTime1,saveCurrentDate1);
                                sendMessageReciever(fuser.getUid(), userid, msg, trans_msg, messageId,saveCurrentTime,saveCurrentDate,saveCurrentTime1,saveCurrentDate1);

                                Toast.makeText(MessageActivity.this, "data Network Enabled", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            addText(msg,fuser.getUid(),userid,messageId,trans_msg,"text",saveCurrentTime,saveCurrentDate,"hello","kya","never",msg);
                        }

                    } else {
                        Toast.makeText(MessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
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

    private void sendGifImages(String sender, final String receiver, String message, String uid, String fuserUid, String messageId,String saveCurrentTime,String saveCurrentDate,String saveCurrentTime1,String saveCurrentDate1) {

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
        hashMap.put("type", "gif");
        hashMap.put("preMessage", "hello");
        hashMap.put("last","false");
        hashMap.put("lastSendMessage", message);


        HashMap<String, Object> map = new HashMap<>();
        map.put("time", saveCurrentDate1 + "-" + saveCurrentTime1);


        reference.child("Chats").child(fuser.getUid()).child(userid).child(saveCurrentDate1+saveCurrentTime1).setValue(hashMap);
        reference.child("Chats").child(userid).child(fuser.getUid()).child(saveCurrentDate1+saveCurrentTime1).setValue(hashMap);


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

    private void sendMessage(String sender, final String receiver, String message, String uid, String fuserUid, String messageId,String saveCurrentTime,String saveCurrentDate,String saveCurrentTime1,String saveCurrentDate1) {
        Number currentnum=realm.where(DownloadModel.class).max("id");
        int nextId;

        if(currentnum==null){
            nextId=1;
        }
        else{
            nextId=currentnum.intValue()+1;
        }

        int itemId = nextId;
        final DownloadModel downloadModel=new DownloadModel();
        downloadModel.setId(itemId);
        downloadModel.setStatus("Never");
        downloadModel.setTitle("hello");
        downloadModel.setFile_size("0");
        downloadModel.setProgress("0");
        downloadModel.setIs_paused(false);
        downloadModel.setDownloadId(8878);
        downloadModel.setFile_path("");
        downloadModel.setMessage(message);
        downloadModel.setSender(sender);
        downloadModel.setReceiver(receiver);
        downloadModel.setMessageID(messageId);
        downloadModel.setIsseen("No");
        downloadModel.setLastSendMessage("hello");
        downloadModel.setBio(uid);
        downloadModel.setDate(saveCurrentDate);
        downloadModel.setPreMessage("hgyg");
        downloadModel.setTime(saveCurrentTime);
        downloadModel.setType("text");
        downloadModel.setImagrUrl(imageurl);
        downloadModel.setUsername("no value");
        downloadModel.setAdminId("no value");


        downloadModels.add(downloadModel);

        downloadAdapter.notifyDataSetChanged();
        adapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(downloadModels.size()-1);


        DownloadModel id;
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(downloadModel);

                txt_seen.setText("panding");

            }
        });


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
        hashMap.put("title", "title");


        HashMap<String, Object> map = new HashMap<>();
        map.put("time", saveCurrentDate1 + "-" + saveCurrentTime1);


        reference.child("Chats").child(fuser.getUid()).child(userid).child(saveCurrentDate1+saveCurrentTime1).setValue(hashMap)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    RealmResults<DownloadModel> results = realm.where(DownloadModel.class).equalTo("id",itemId).findAll();

                    realm.beginTransaction();


                    results.setString("isseen","false");

                    txt_seen.setText("deliver");


                    realm.commitTransaction();

                   // downloadAdapter.notifyItemChanged(downloadModels.size());

                    downloadAdapter.notifyDataSetChanged();


                }
                else {
                    Toast.makeText(MessageActivity.this, "uploading failed...!", Toast.LENGTH_SHORT).show();
                }
            }
        });


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
        downloadAdapter.notifyDataSetChanged();
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
        hashMap.put("title", "title");



        HashMap<String, Object> map = new HashMap<>();
        map.put("time", saveCurrentDate1 + "-" + saveCurrentTime1);


        reference.child(saveCurrentDate1+saveCurrentTime1).setValue(hashMap);

    }

    public class DownloadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
        private Context mContext;
        FirebaseAuth mAuth;
        String outputString, convertString;
        public static final int MSG_TYPE_LEFT = 0;
        public static final int MSG_TYPE_RIGHT = 1;
        Context context;
        List<DownloadModel> mChat = new ArrayList<>();
        ItemClickListener clickListener;

        public DownloadAdapter(Context context, List<DownloadModel> mChat, ItemClickListener itemClickListener) {
            this.context = context;
            this.clickListener = itemClickListener;
            this.mChat = mChat;
        }

        public class DownloadViewHolder extends RecyclerView.ViewHolder {

            public TextView id_new,date,  play, pause, imageTimeDate, videoTimeDate, pdfTimeDate, delete_one_message, delete_text_message, download_pdf,
                    open_video_activity, reply_image, reply_pdf, reply_video, reply_text, rep_message_text, rep_chat_text, rep_chat_image, rep_chat_video, rep_chat_pdf, rep_pdf_name, single_check_reply_text_one, single_check_reply_text_two, single_check_reply_text_three, single_check_reply_text_four, single_check_reply_text_five, single_check_reply_image_one, single_check_reply_image_two, single_check_reply_image_three, single_check_reply_image_four, single_check_reply_image_five, single_check_reply_pdf_one, single_check_reply_pdf_two, single_check_reply_pdf_three, single_check_reply_pdf_four, single_check_reply_pdf_five,
                    single_check_reply_video_one, single_check_reply_video_two, single_check_reply_video_three, single_check_reply_video_four, single_check_reply_video_five, single_check_text_one, single_check_text_two, single_check_text_three, single_check_text_four, single_check_text_five,
                    single_check_image_one, single_check_image_two, single_check_image_three, single_check_image_four, single_check_image_five,
                    single_check_video_one, single_check_video_two, single_check_video_three, single_check_video_four, single_check_video_five,
                    single_check_pdf_one, single_check_pdf_two, single_check_pdf_three, single_check_pdf_four, single_check_pdf_five;
            public ImageView profile_image, show_image,show_pdf, rep_message_image, rep_message_pdf,pdf_download;
            VideoView full_video;
            AppCompatImageView gif_image;
            EmoticonTextView text_show_password,show_message;
            EditText password, password_null, password_reply_text;
            public TextView  textTimeDate, pdfName, full_screen, iconHidden;
            LinearLayout linearText, delete_text_linear, linearName;
            RelativeLayout linearimage, linearVideo, linearpdf, fullLinesrvideo, delete_one_linear, rep_text, rep_image, rep_video, rep_pdf,relative_pdf;
            private ImageView show_video, rep_message_video;
            Button password_ok, password_reply_ok;
            ProgressBar splashProgress;
            TextView file_title;
            TextView file_size;
            ProgressBar file_progress;
            Button sharefile;
            TextView file_status,pause_resume;
            RelativeLayout main_rel;

            TextView file_title_document;
            TextView file_size_document;
            ProgressBar file_progress_document;
            Button pause_resume_document,sharefile_document;
            TextView file_status_document;
            RelativeLayout main_rel_document;

            TextView download_video_size;



            public DownloadViewHolder(@NonNull View itemView) {
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
     //           txt_seen = itemView.findViewById(R.id.txt_seen);
                gif_image =itemView.findViewById(R.id.show_gif_image);
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

                relative_pdf = itemView.findViewById(R.id.pdf);


                file_title=itemView.findViewById(R.id.file_title);
                file_size=itemView.findViewById(R.id.file_size);
                file_status=itemView.findViewById(R.id.file_status);
                file_progress=itemView.findViewById(R.id.file_progress);
                pause_resume=itemView.findViewById(R.id.pause_resume);
                main_rel=itemView.findViewById(R.id.main_media_relative);
                sharefile=itemView.findViewById(R.id.sharefile);


                file_title_document=itemView.findViewById(R.id.file_title_document);
                file_size_document=itemView.findViewById(R.id.file_size_document);
                file_status_document=itemView.findViewById(R.id.file_status_document);
                file_progress_document=itemView.findViewById(R.id.file_progress_document);
                pause_resume_document=itemView.findViewById(R.id.pause_resume_document);
                main_rel_document=itemView.findViewById(R.id.main_document_relative);
                sharefile_document=itemView.findViewById(R.id.sharefile_document);

                download_video_size = itemView.findViewById(R.id.download_video_size);

                pdf_download = itemView.findViewById(R.id.pdf_download);

                date =itemView.findViewById(R.id.date);


            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == MSG_TYPE_RIGHT) {
                RecyclerView.ViewHolder vh;
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_right, parent, false);
                vh = new DownloadViewHolder(view);
                return vh;
            } else {
                RecyclerView.ViewHolder vh;
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_left, parent, false);
                vh = new DownloadViewHolder(view);
                return vh;
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder Viewholder, final int position) {


            final DownloadModel chat = mChat.get(position);
            final DownloadViewHolder holder = (DownloadViewHolder) Viewholder;

            mAuth = FirebaseAuth.getInstance();
            final String messageSenderId = mAuth.getCurrentUser().getUid();
            String fromMessageType = chat.getType();
            String fromUserID = chat.getSender();

//            if (position==0)
//            {
//            }
//           else if(mChat.get(position-1).getIsseen().equals(mChat.get(position).getIsseen()))
//            {
//                holder.date.setVisibility(View.GONE);
//                Toast.makeText(MessageActivity.this, position+mChat.get(position-1).getIsseen()+mChat.get(position).getIsseen()+"yes", Toast.LENGTH_SHORT).show();
//            }
//           else
//               {
//                   holder.date.setVisibility(View.VISIBLE);
//                   Toast.makeText(MessageActivity.this, "no", Toast.LENGTH_SHORT).show();
//               }

            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currendateFormat = new SimpleDateFormat("MMM dd");
            saveCurrentDate = currendateFormat.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currenTimeFormat = new SimpleDateFormat("hh:mm a");
            saveCurrentTime = currenTimeFormat.format(calForTime.getTime());

//            if (position == mChat.size() - 1) {
//                if (chat.getSender().equals(fromUserID)){
//                    if (chat.getDate().equals(saveCurrentDate)){
//
//                    }
//                    else {
//                    }
//
//                }
//                else if (chat.getReceiver().equals(userid)&& (chat.getDate().equals(saveCurrentDate))){
//
//                }
//                else {
//
//                }
//
//            }
//

//            DatabaseReference  databaseRef = FirebaseDatabase.getInstance().getReference("Chats").child(fuser.getUid()).child(userid);
//            databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//
//                              String isseen = snapshot.child("isseen").getValue().toString();
//
//                            if (position == mChat.size() - 1) {
//                                if (isseen.equals("false")) {
//                                    holder.txt_seen.setVisibility(View.VISIBLE);
//                                    holder.txt_seen.setText("Seen");
//
//                                }
//                                else {
//                                    holder.txt_seen.setVisibility(View.VISIBLE);
//                                    holder.txt_seen.setText("Delivered");
//
//                                }
//                            } else {
//
//                                holder.txt_seen.setVisibility(View.GONE);
//                            }
//                        }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                }
//            });
//

            holder.text_show_password.setEmoticonProvider(Android8EmoticonProvider.create());
            holder.show_message.setEmoticonProvider(Android8EmoticonProvider.create());
            holder.text_show_password.setEmoticonSize(60);
            holder.show_message.setEmoticonSize(60);
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
                            updateseenmessageMesagges(chat.getMessageID(), chat.getId());
                        } else {
                            holder.single_check_text_five.setVisibility(View.VISIBLE);
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


                                reply_preview_message.setText(String.valueOf(position));

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
                           //     Toast.makeText(MessageActivity.this, String.valueOf(position), Toast.LENGTH_SHORT).show();
                                holder.show_message.setText(outputString);
                                holder.password.setVisibility(View.VISIBLE);
                                holder.password_ok.setVisibility(View.VISIBLE);



                            }
                        });

                        holder.password_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.show_message.setEmoticonProvider(Android8EmoticonProvider.create());
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

                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MessageActivity.this);
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
                                        realm.beginTransaction();
                                        RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", id).findAll();
                                        item.remove(0);
                                        realm.commitTransaction();
                                        downloadModels.remove(position);
                                        downloadAdapter.notifyDataSetChanged();


                                    }
                                });
                                builder.setNegativeButton("Me", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        deleteSentMessage(position, holder);
                                        realm.beginTransaction();
                                        RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
                                        item.deleteAllFromRealm();
                                        realm.commitTransaction();
                                        downloadModels.remove(position);
                                        downloadAdapter.notifyDataSetChanged();

                                    }
                                });
                                builder.create().show();
                                return true;
                            }
                        });


                    } else {

                        holder.linearText.setVisibility(View.VISIBLE);
                        holder.reply_text.setVisibility(View.VISIBLE);
                        holder.text_show_password.setVisibility(View.VISIBLE);
                        holder.textTimeDate.setVisibility(View.VISIBLE);
                        holder.textTimeDate.setText(chat.getTime());




                        if (chat.getIsseen().equals("false")) {
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
                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MessageActivity.this);
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
                                        realm.beginTransaction();
                                        RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
                                        item.deleteAllFromRealm();
                                        realm.commitTransaction();
                                        downloadModels.remove(position);
                                        downloadAdapter.notifyDataSetChanged();

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

            else if (fromMessageType.equals("gif")) {

                    if (fromUserID.equals(messageSenderId)) {
                        holder.gif_image.setVisibility(View.VISIBLE);
                        holder.show_image.setVisibility(View.GONE);
                        holder.linearimage.setVisibility(View.VISIBLE);
                        holder.imageTimeDate.setVisibility(View.VISIBLE);
                        holder.imageTimeDate.setText(chat.getDate() + "  -  " + chat.getTime());

                        long interval = 100;
                        RequestOptions options = new RequestOptions().frame(interval);

                        Glide.with(MessageActivity.this)
                                .load(chat.getMessage())
                                .apply(options)
                                .into(holder.gif_image);

                        if (chat.getIsseen().equals("true")) {
                            holder.single_check_image_one.setVisibility(View.VISIBLE);
                            holder.single_check_image_two.setVisibility(View.VISIBLE);
                        } else if (chat.getIsseen().equals("false")) {

                            holder.single_check_image_three.setVisibility(View.VISIBLE);
                            holder.single_check_image_four.setVisibility(View.VISIBLE);

                        } else {
                            holder.single_check_image_five.setVisibility(View.VISIBLE);
                        }

                        holder.reply_image.setVisibility(View.GONE);

                        holder.show_image.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MessageActivity.this);
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
                                        realm.beginTransaction();
                                        RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
                                        item.deleteAllFromRealm();
                                        realm.commitTransaction();
                                        downloadModels.remove(position);
                                        downloadAdapter.notifyDataSetChanged();

                                    }
                                });
                                builder.setNegativeButton("Me", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        deleteSentMessage(position, holder);
                                        realm.beginTransaction();
                                        RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
                                        item.deleteAllFromRealm();
                                        realm.commitTransaction();
                                        downloadModels.remove(position);
                                        downloadAdapter.notifyDataSetChanged();
                                        dialog.dismiss();
                                    }
                                });
                                builder.create().show();
                                return true;
                            }
                        });


                    } else if (fromUserID.equals(userid)) {

                        holder.gif_image.setVisibility(View.VISIBLE);
                        holder.show_image.setVisibility(View.GONE);
                        holder.linearimage.setVisibility(View.VISIBLE);
                        holder.imageTimeDate.setVisibility(View.VISIBLE);
                        holder.imageTimeDate.setText(chat.getDate() + "  -  " + chat.getTime());

                        long interval = 1000;
                        RequestOptions options = new RequestOptions().frame(interval);

                        Glide.with(MessageActivity.this)
                                .load(chat.getMessage())
                                .apply(options)
                                .into(holder.gif_image);


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

                        holder.show_image.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {

                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MessageActivity.this);
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
                                        realm.beginTransaction();
                                        RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
                                        item.deleteAllFromRealm();
                                        realm.commitTransaction();
                                        downloadModels.remove(position);
                                        downloadAdapter.notifyDataSetChanged();

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
                    } else {

                    }
                }

            else if (fromMessageType.equals("image")) {

                    if (fromUserID.equals(messageSenderId)) {
                        holder.show_image.setVisibility(View.VISIBLE);
                        holder.linearimage.setVisibility(View.VISIBLE);
                        holder.imageTimeDate.setVisibility(View.VISIBLE);
                        holder.imageTimeDate.setText(chat.getDate() + "  -  " + chat.getTime());
                        //   holder.imageTimeDate.setText(chat.getDate() + "  -  " + chat.getTime());
                        Picasso.get().load(chat.getMessage()).into(holder.show_image);


                        //   holder.show_image.setImageURI(Uri.parse(chat.getMessage()));


                        if (chat.getIsseen().equals("true")) {
                            holder.single_check_image_one.setVisibility(View.VISIBLE);
                            holder.single_check_image_two.setVisibility(View.VISIBLE);
                        } else if (chat.getIsseen().equals("false")) {

                            holder.single_check_image_three.setVisibility(View.VISIBLE);
                            holder.single_check_image_four.setVisibility(View.VISIBLE);
                            //     updateseenmessageMesagges(chat.getMessageID(),chat.getId());


                        } else {
                            holder.single_check_image_five.setVisibility(View.VISIBLE);
                        }


                        holder.reply_image.setVisibility(View.GONE);
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

//                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mChat.get(position).getMessage()));
//                                holder.itemView.getContext().startActivity(intent);
//
//
//                                Toast.makeText(holder.itemView.getContext(), chat.getMessage(), Toast.LENGTH_SHORT).show();

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
                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MessageActivity.this);
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
                                        realm.beginTransaction();
                                        RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
                                        item.deleteAllFromRealm();
                                        realm.commitTransaction();
                                        downloadModels.remove(position);
                                        downloadAdapter.notifyDataSetChanged();

                                    }
                                });
                                builder.setNegativeButton("Me", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        deleteSentMessage(position, holder);
                                        realm.beginTransaction();
                                        RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
                                        item.deleteAllFromRealm();
                                        realm.commitTransaction();
                                        downloadModels.remove(position);
                                        downloadAdapter.notifyDataSetChanged();
                                        dialog.dismiss();
                                    }
                                });
                                builder.create().show();
                                return true;
                            }
                        });


                    } else if (fromUserID.equals(userid)) {

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


                        holder.reply_image.setVisibility(View.GONE);
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

                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MessageActivity.this);
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
                                        realm.beginTransaction();
                                        RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
                                        item.deleteAllFromRealm();
                                        realm.commitTransaction();
                                        downloadModels.remove(position);
                                        downloadAdapter.notifyDataSetChanged();

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
                    } else {

                    }
                }

            else if (fromMessageType.equals("video")) {
                    if (fromUserID.equals(messageSenderId)) {

                        holder.linearVideo.setVisibility(View.VISIBLE);
                        holder.videoTimeDate.setVisibility(View.VISIBLE);
                        holder.videoTimeDate.setText(chat.getDate() + "  -  " + chat.getTime());
                        holder.show_video.setVisibility(View.VISIBLE);
                        holder.reply_video.setVisibility(View.GONE);


                        updateseenmessageMesagges(chat.getMessageID(), chat.getId());

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

                        Glide.with(MessageActivity.this).asBitmap().load(chat.getMessage()).apply(options).into(holder.show_video);

                        holder.reply_video.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                relative_layout_message.setVisibility(View.GONE);
                                reply_bottom.setVisibility(View.VISIBLE);

                                reply_preview_message.setText(chat.getMessage());

                                type.setText("reply_video");

                                Glide.with(MessageActivity.this).asBitmap().load(chat.getMessage()).apply(options).into(reply_image);

                            }
                        });

                        holder.linearVideo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(MessageActivity.this, chat.getMessage(), Toast.LENGTH_SHORT).show();

//                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mChat.get(position).getImagrUrl()));
//                                holder.itemView.getContext().startActivity(intent);

                                Intent intent = new Intent(holder.itemView.getContext(), DirectVideo.class);
                                intent.putExtra("visit_user_id", chat.getReceiver());
                                intent.putExtra("messageId", chat.getMessage());
                                holder.itemView.getContext().startActivity(intent);

                            }
                        });

                        holder.linearVideo.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MessageActivity.this);
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
                                        realm.beginTransaction();
                                        RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
                                        item.deleteAllFromRealm();
                                        realm.commitTransaction();
                                        downloadModels.remove(position);
                                        downloadAdapter.notifyDataSetChanged();

                                    }
                                });
                                builder.setNegativeButton("Me", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        deleteSentMessage(position, holder);
                                        realm.beginTransaction();
                                        RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
                                        item.deleteAllFromRealm();
                                        realm.commitTransaction();
                                        downloadModels.remove(position);
                                        downloadAdapter.notifyDataSetChanged();

                                        dialog.dismiss();
                                    }
                                });
                                builder.create().show();
                                return true;
                            }
                        });


                    } else if (fromUserID.equals(userid)) {

                        holder.linearVideo.setVisibility(View.VISIBLE);
                        holder.videoTimeDate.setText(chat.getDate() + "  -  " + chat.getTime());
                        holder.show_video.setVisibility(View.VISIBLE);
                        holder.reply_video.setVisibility(View.GONE);

                        holder.file_title.setText(chat.getTitle());
                        holder.file_status.setText(chat.getStatus());
                        holder.file_progress.setProgress(Integer.parseInt(chat.getProgress()));
                        holder.file_size.setText(chat.getFile_size());



                        if (chat.getIsseen().equals("false")){
                            holder.main_rel.setVisibility(View.VISIBLE);
                            holder.videoTimeDate.setVisibility(View.GONE);

                        }else {
                            holder.main_rel.setVisibility(View.GONE);
                            holder.videoTimeDate.setVisibility(View.VISIBLE);
                        }

                        holder.download_video_size.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                downloadMediaFiles(chat.getImagrUrl(),chat.getId());
                            }
                        });



                        if (chat.getIsseen().equals("false")) {
                            holder.id_new.setVisibility(View.VISIBLE);
                        } else {
                            holder.id_new.setVisibility(View.GONE);
                        }

                        if(chat.isIs_paused()){
                            holder.pause_resume.setText("RESUME");
                        }
                        else{
                            holder.pause_resume.setText("");
                        }

                        if(chat.getStatus().equalsIgnoreCase("RESUME")){
                            holder.file_status.setText("Running");
                        }


                        holder.pause_resume.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(chat.isIs_paused()){
                                    chat.setIs_paused(false);
                                    holder.pause_resume.setText("PAUSE");
                                    chat.setStatus("RESUME");
                                    holder.file_status.setText("Running");
                                    if(!resumeDownload(chat)){
                                        Toast.makeText(context, "Failed to Resume", Toast.LENGTH_SHORT).show();
                                    }
                                    notifyItemChanged(position);
                                }
                                else {
                                    chat.setIs_paused(true);
                                    holder.pause_resume.setText("RESUME");
                                    chat.setStatus("PAUSE");
                                    holder.file_status.setText("PAUSE");
                                    if(!pauseDownload(chat)){
                                        Toast.makeText(context, "Failed to Pause", Toast.LENGTH_SHORT).show();
                                    }
                                    notifyItemChanged(position);
                                }
                            }
                        });

                        holder.main_rel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                clickListener.onCLickItem(chat.getFile_path());
                            }
                        });

                        holder.sharefile.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                clickListener.onShareClick(chat);
                            }
                        });






                        long interval = 1000;
                        RequestOptions options = new RequestOptions().frame(interval);

                        Glide.with(MessageActivity.this).asBitmap().load(chat.getImagrUrl()).apply(options).into(holder.show_video);


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

                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MessageActivity.this);
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
                                        realm.beginTransaction();
                                        RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
                                        item.deleteAllFromRealm();
                                        realm.commitTransaction();
                                        downloadModels.remove(position);
                                        downloadAdapter.notifyDataSetChanged();
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

                    } else {

                    }
                }

            else if (fromMessageType.equals("PDF")) {
                    {
                        if (fromUserID.equals(messageSenderId)) {
                            holder.linearpdf.setVisibility(View.VISIBLE);
                            holder.pdfName.setVisibility(View.VISIBLE);
                            holder.pdfName.setText(chat.getBio());
                            holder.linearName.setVisibility(View.VISIBLE);
                            holder.pdfTimeDate.setVisibility(View.VISIBLE);
                            holder.show_pdf.setVisibility(View.VISIBLE);
                            holder.pdfTimeDate.setText(chat.getDate() + "  -  " + chat.getTime());
                            Picasso.get().load("file://"+chat.getImagrUrl()).into(holder.show_pdf);

                            updateseenmessageMesagges(chat.getMessageID(), chat.getId());

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

                                    opendocument( chat.getMessage());

                                    generateImageFromPdf(Uri.parse(chat.getMessage()),chat.getId());

                                }
                            });

                            holder.download_pdf.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {


                                    Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(mChat.get(position).getMessage()));
                                    holder.itemView.getContext().startActivity(intent);

                                }
                            });

                            holder.reply_pdf.setVisibility(View.GONE);
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

                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MessageActivity.this);
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
                                            realm.beginTransaction();
                                            RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
                                            item.deleteAllFromRealm();
                                            realm.commitTransaction();
                                            downloadModels.remove(position);
                                            downloadAdapter.notifyDataSetChanged();

                                        }
                                    });
                                    builder.setNegativeButton("Me", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            deleteSentMessage(position, holder);
                                            realm.beginTransaction();
                                            RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
                                            item.deleteAllFromRealm();
                                            realm.commitTransaction();
                                            downloadModels.remove(position);
                                            downloadAdapter.notifyDataSetChanged();
                                            dialog.dismiss();
                                        }
                                    });
                                    builder.create().show();
                                    return true;
                                }
                            });


                        } else if (fromUserID.equals(userid)) {
                            holder.linearName.setVisibility(View.VISIBLE);
                            holder.pdfName.setVisibility(View.VISIBLE);
                            holder.pdfName.setText(chat.getBio());
                            holder.linearpdf.setVisibility(View.VISIBLE);
                            holder.pdfTimeDate.setVisibility(View.VISIBLE);
                            holder.pdfTimeDate.setText(chat.getDate() + "  -  " + chat.getTime());
                            holder.show_pdf.setVisibility(View.VISIBLE);
                            Picasso.get().load("file://"+chat.getImagrUrl()).into(holder.show_pdf);


                            if (chat.getIsseen().equals("false")) {
                                holder.id_new.setVisibility(View.VISIBLE);
                            } else {
                                holder.id_new.setVisibility(View.GONE);
                            }

                            holder.file_title_document.setText(chat.getTitle());
                            holder.file_status_document.setText(chat.getStatus());
                            holder.file_progress_document.setProgress(Integer.parseInt(chat.getProgress()));
                            holder.file_size_document.setText(chat.getFile_size());

                            if (chat.getIsseen().equals("false")){
                                holder.main_rel_document.setVisibility(View.VISIBLE);
                                holder.show_pdf.setVisibility(View.GONE);

                            }else {
                                holder.main_rel_document.setVisibility(View.GONE);
                                holder.show_pdf.setVisibility(View.VISIBLE);
                            }

                            holder.pdf_download.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    downloadMediaFiles(chat.getImagrUrl(),chat.getId());
                                }
                            });

                            if(chat.isIs_paused()){
                                holder.pause_resume_document.setText("RESUME");
                            }
                            else{
                                holder.pause_resume_document.setText("");
                            }

                            if(chat.getStatus().equalsIgnoreCase("RESUME")){
                                holder.file_status_document.setText("Running");
                            }

                            holder.pause_resume_document.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(chat.isIs_paused()){
                                        chat.setIs_paused(false);
                                        holder.pause_resume_document.setText("PAUSE");
                                        chat.setStatus("RESUME");
                                        holder.file_status_document.setText("Running");
                                        if(!resumeDownload(chat)){
                                            Toast.makeText(context, "Failed to Resume", Toast.LENGTH_SHORT).show();
                                        }
                                        notifyItemChanged(position);
                                    }
                                    else {
                                        chat.setIs_paused(true);
                                        holder.pause_resume_document.setText("RESUME");
                                        chat.setStatus("PAUSE");
                                        holder.file_status_document.setText("PAUSE");
                                        if(!pauseDownload(chat)){
                                            Toast.makeText(context, "Failed to Pause", Toast.LENGTH_SHORT).show();
                                        }
                                        notifyItemChanged(position);
                                    }
                                }
                            });

                            holder.main_rel_document.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    clickListener.onCLickItem(chat.getFile_path());
                                }
                            });

                            holder.sharefile.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    clickListener.onShareClick(chat);
                                }
                            });

                            holder.reply_pdf.setVisibility(View.GONE);

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
                                    
                                    if (chat.getIsseen().equals("true")){
                                        opendocument(chat.getMessage());
                                        generateImageFromPdf(Uri.parse("file://"+chat.getMessage()),chat.getId());

                                    }else {
                                        Toast.makeText(MessageActivity.this, "please download item", Toast.LENGTH_SHORT).show();
                                        
                                    }
                                   

//                                    generateImageFromPdf(Uri.parse(chat.getMessage()),chat.getId());
//                                    holder.download_pdf.setVisibility(View.VISIBLE);
                                }
                            });

                            holder.download_pdf.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Intent intent = new Intent(MessageActivity.this,UpdateRecordActivity.class);
                                    intent.putExtra("file",chat.getMessage());
                                    startActivity(intent);
//                                    Intent intent = new Intent(Intent.ACTION_VIEW,  Uri.fromFile(new File(chat.getMessage())));
//                                    holder.itemView.getContext().startActivity(intent);

                                }
                            });

                            holder.linearpdf.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {

                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MessageActivity.this);
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
                                            realm.beginTransaction();
                                            RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
                                            item.deleteAllFromRealm();
                                            realm.commitTransaction();
                                            downloadModels.remove(position);
                                            downloadAdapter.notifyDataSetChanged();
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


                        } else {

                        }
                    }

                }

            else if (fromMessageType.equals("docx")){
                {
                    if (fromUserID.equals(messageSenderId)) {
                        holder.linearpdf.setVisibility(View.VISIBLE);
                        holder.pdfName.setVisibility(View.VISIBLE);
                        holder.pdfName.setText(chat.getBio());
                        holder.linearName.setVisibility(View.VISIBLE);
                        holder.pdfTimeDate.setVisibility(View.VISIBLE);
                        holder.show_pdf.setVisibility(View.GONE);
                        holder.relative_pdf.setVisibility(View.GONE);
                        holder.pdfTimeDate.setText(chat.getDate() + "  -  " + chat.getTime());

                        updateseenmessageMesagges(chat.getMessageID(), chat.getId());




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

                               opendocument(chat.getMessage());
                            }
                        });

                        holder.download_pdf.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(mChat.get(position).getMessage()));
                                holder.itemView.getContext().startActivity(intent);

                            }
                        });

                        holder.reply_pdf.setVisibility(View.GONE);
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

                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MessageActivity.this);
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
                                        realm.beginTransaction();
                                        RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
                                        item.deleteAllFromRealm();
                                        realm.commitTransaction();
                                        downloadModels.remove(position);
                                        downloadAdapter.notifyDataSetChanged();

                                    }
                                });
                                builder.setNegativeButton("Me", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        deleteSentMessage(position, holder);
                                        realm.beginTransaction();
                                        RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
                                        item.deleteAllFromRealm();
                                        realm.commitTransaction();
                                        downloadModels.remove(position);
                                        downloadAdapter.notifyDataSetChanged();
                                        dialog.dismiss();
                                    }
                                });
                                builder.create().show();
                                return true;
                            }
                        });


                    }
                    else if (fromUserID.equals(userid)) {
                        holder.linearName.setVisibility(View.VISIBLE);
                        holder.pdfName.setVisibility(View.VISIBLE);
                        holder.pdfName.setText(chat.getBio());
                        holder.linearpdf.setVisibility(View.VISIBLE);
                        holder.pdfTimeDate.setVisibility(View.VISIBLE);
                        holder.pdfTimeDate.setText(chat.getDate() + "  -  " + chat.getTime());
                        holder.show_pdf.setVisibility(View.GONE);
                     //   holder.relative_pdf.setVisibility(View.GONE);


                        if (chat.getIsseen().equals("false")) {
                            holder.id_new.setVisibility(View.VISIBLE);
                        } else {
                            holder.id_new.setVisibility(View.GONE);
                        }


                        holder.file_title_document.setText(chat.getTitle());
                        holder.file_status_document.setText(chat.getStatus());
                        holder.file_progress_document.setProgress(Integer.parseInt(chat.getProgress()));
                        holder.file_size_document.setText(chat.getFile_size());


                        if (chat.getIsseen().equals("false")){
                            holder.main_rel_document.setVisibility(View.VISIBLE);
                            holder.show_pdf.setVisibility(View.GONE);

                        }else {
                            holder.main_rel_document.setVisibility(View.GONE);
                            holder.show_pdf.setVisibility(View.GONE);
                        }


                        holder.pdf_download.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                downloadMediaFiles(chat.getImagrUrl(),chat.getId());
                            }
                        });


                        if(chat.isIs_paused()){
                            holder.pause_resume_document.setText("RESUME");
                        }
                        else{
                            holder.pause_resume_document.setText("");
                        }

                        if(chat.getStatus().equalsIgnoreCase("RESUME")){
                            holder.file_status_document.setText("Running");
                        }


                        holder.pause_resume_document.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(chat.isIs_paused()){
                                    chat.setIs_paused(false);
                                    holder.pause_resume_document.setText("PAUSE");
                                    chat.setStatus("RESUME");
                                    holder.file_status_document.setText("Running");
                                    if(!resumeDownload(chat)){
                                        Toast.makeText(context, "Failed to Resume", Toast.LENGTH_SHORT).show();
                                    }
                                    notifyItemChanged(position);
                                }
                                else {
                                    chat.setIs_paused(true);
                                    holder.pause_resume_document.setText("RESUME");
                                    chat.setStatus("PAUSE");
                                    holder.file_status_document.setText("PAUSE");
                                    if(!pauseDownload(chat)){
                                        Toast.makeText(context, "Failed to Pause", Toast.LENGTH_SHORT).show();
                                    }
                                    notifyItemChanged(position);
                                }
                            }
                        });

                        holder.main_rel_document.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                clickListener.onCLickItem(chat.getFile_path());
                            }
                        });

                        holder.sharefile.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                clickListener.onShareClick(chat);
                            }
                        });


                        holder.reply_pdf.setVisibility(View.GONE);
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
                                if (chat.getIsseen().equals("true")){
                                    opendocument(chat.getMessage());

                                }else {
                                    Toast.makeText(MessageActivity.this, "please download item", Toast.LENGTH_SHORT).show();

                                }


//                                generateImageFromPdf(Uri.parse(chat.getMessage()),chat.getId());
//                                holder.download_pdf.setVisibility(View.VISIBLE);
                            }
                        });

                        holder.download_pdf.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(MessageActivity.this,UpdateRecordActivity.class);
                                intent.putExtra("file",chat.getMessage());
                                startActivity(intent);
//                                    Intent intent = new Intent(Intent.ACTION_VIEW,  Uri.fromFile(new File(chat.getMessage())));
//                                    holder.itemView.getContext().startActivity(intent);

                            }
                        });


                        holder.linearpdf.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {

                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MessageActivity.this);
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
                                        realm.beginTransaction();
                                        RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
                                        item.deleteAllFromRealm();
                                        realm.commitTransaction();
                                        downloadModels.remove(position);
                                        downloadAdapter.notifyDataSetChanged();
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


                    } else {

                    }
                }
            }

            else if(fromMessageType.equals("reply_text")) {
                    if (fromUserID.equals(messageSenderId)) {

                        holder.rep_message_text.setVisibility(View.VISIBLE);
                        holder.rep_chat_text.setVisibility(View.VISIBLE);
                        holder.rep_text.setVisibility(View.VISIBLE);
//
//                        holder.rep_text.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                               recyclerView.scrollToPosition(Integer.parseInt(chat.getPreMessage()));
//
//                                if (position ==Integer.parseInt(chat.getPreMessage())){
//                                    holder.textTimeDate.setText("hello suri rathore");
//                                    Toast.makeText(MessageActivity.this, chat.getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                                else {
//                                    holder.textTimeDate.setText("hello suri rathore");
//
//
//                                }
//                            }
//                        });

                        updateseenmessageMesagges(chat.getMessageID(), chat.getId());

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

                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MessageActivity.this);
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
                                        realm.beginTransaction();
                                        RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
                                        item.deleteAllFromRealm();
                                        realm.commitTransaction();
                                        downloadModels.remove(position);
                                        downloadAdapter.notifyDataSetChanged();
                                        deleteMessageForEveryone(position, holder);
                                    }
                                });
                                builder.setNegativeButton("Me", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        deleteSentMessage(position, holder);
                                        realm.beginTransaction();
                                        RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
                                        item.deleteAllFromRealm();
                                        realm.commitTransaction();
                                        downloadModels.remove(position);
                                        downloadAdapter.notifyDataSetChanged();
                                        dialog.dismiss();
                                    }
                                });
                                builder.create().show();
                                return true;
                            }
                        });


                    }
                    else if (fromUserID.equals(userid)) {
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
                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MessageActivity.this);
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
                                        realm.beginTransaction();
                                        RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
                                        item.deleteAllFromRealm();
                                        realm.commitTransaction();
                                        downloadModels.remove(position);
                                        downloadAdapter.notifyDataSetChanged();
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

                    } else {

                    }
                }

//            else if (fromMessageType.equals("reply_image")) {
//                    if (fromUserID.equals(messageSenderId)) {
//
//                        holder.rep_message_image.setVisibility(View.VISIBLE);
//                        holder.rep_chat_image.setVisibility(View.VISIBLE);
//                        holder.rep_image.setVisibility(View.VISIBLE);
//
//                        updateseenmessageMesagges(chat.getMessageID(), chat.getId());
//
//
//                        if (chat.getIsseen().equals("true")) {
//                            holder.single_check_reply_image_one.setVisibility(View.VISIBLE);
//                            holder.single_check_reply_image_two.setVisibility(View.VISIBLE);
//                        } else if (chat.getIsseen().equals("false")) {
//                            holder.single_check_reply_image_three.setVisibility(View.VISIBLE);
//                            holder.single_check_reply_image_four.setVisibility(View.VISIBLE);
//                        } else {
//                            holder.single_check_reply_image_five.setVisibility(View.VISIBLE);
//                        }
//
//                        try {
//                            outputString = decrypt(chat.getMessage(), holder.password_null.getText().toString());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//
//                        }
//
//
//                        Picasso.get().load(chat.getPreMessage()).into(holder.rep_message_image);
//                        holder.rep_chat_image.setText(outputString);
//
//
//                        holder.rep_message_image.setOnClickListener(new View.OnClickListener() {
//                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//                            @Override
//                            public void onClick(View v) {
//
//
//                                Intent chatIntent = new Intent(holder.itemView.getContext(), ImageViewActivity.class);
//                                chatIntent.putExtra("url", mChat.get(position).getPreMessage());
//
//
//                                Pair[] pairs = new Pair[1];
//                                pairs[0] = new Pair<View, String>(holder.rep_message_image, "imageTransition");
//
//                                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) holder.itemView.getContext(), pairs);
//
//
//                                holder.itemView.getContext().startActivity(chatIntent, options.toBundle());
//
//
//                            }
//                        });
//
//
//                        holder.rep_image.setOnLongClickListener(new View.OnLongClickListener() {
//                            @Override
//                            public boolean onLongClick(View v) {
//
//                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MessageActivity.this);
//                                builder.setTitle("Choose option");
//                                builder.setMessage("delete Message... ?");
//                                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//
//                                        dialog.dismiss();
//                                    }
//                                });
//                                builder.setNeutralButton("Everyone", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                        deleteMessageForEveryone(position, holder);
//                                        realm.beginTransaction();
//                                        RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
//                                        item.deleteAllFromRealm();
//                                        realm.commitTransaction();
//                                        downloadModels.remove(position);
//                                        downloadAdapter.notifyDataSetChanged();
//
//                                    }
//                                });
//                                builder.setNegativeButton("Me", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        deleteSentMessage(position, holder);
//                                        realm.beginTransaction();
//                                        RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
//                                        item.deleteAllFromRealm();
//                                        realm.commitTransaction();
//                                        downloadModels.remove(position);
//                                        downloadAdapter.notifyDataSetChanged();
//                                        dialog.dismiss();
//                                    }
//                                });
//                                builder.create().show();
//                                return true;
//                            }
//                        });
//
//
//                    } else if (fromUserID.equals(userid)) {
//                        holder.rep_message_image.setVisibility(View.VISIBLE);
//                        holder.rep_chat_image.setVisibility(View.VISIBLE);
//                        holder.rep_image.setVisibility(View.VISIBLE);
//
//
//                        if (chat.getIsseen().equals("false")) {
//                            holder.id_new.setVisibility(View.VISIBLE);
//                        } else {
//                            holder.id_new.setVisibility(View.GONE);
//                        }
//
//
//                        try {
//                            outputString = decrypt(chat.getMessage(), holder.password_null.getText().toString());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//
//                        }
//
//
//                        Picasso.get().load(chat.getPreMessage()).into(holder.rep_message_image);
//                        holder.rep_chat_image.setText(outputString);
//
//
//                        holder.rep_message_image.setOnClickListener(new View.OnClickListener() {
//                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//                            @Override
//                            public void onClick(View v) {
//
//
//                                Intent chatIntent = new Intent(holder.itemView.getContext(), ImageViewActivity.class);
//                                chatIntent.putExtra("url", chat.getPreMessage());
//
//
//                                Pair[] pairs = new Pair[1];
//                                pairs[0] = new Pair<View, String>(holder.rep_message_image, "imageTransition");
//
//                                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) holder.itemView.getContext(), pairs);
//
//
//                                holder.itemView.getContext().startActivity(chatIntent, options.toBundle());
//
//
//                            }
//                        });
//
//
//                        holder.rep_image.setOnLongClickListener(new View.OnLongClickListener() {
//                            @Override
//                            public boolean onLongClick(View v) {
//                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MessageActivity.this);
//                                builder.setTitle("Choose option");
//                                builder.setMessage("delete Message... ?");
//                                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//
//                                        dialog.dismiss();
//                                    }
//                                });
//                                builder.setNeutralButton("delete", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        deleteReceiveMessage(position, holder);
//                                        realm.beginTransaction();
//                                        RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
//                                        item.deleteAllFromRealm();
//                                        realm.commitTransaction();
//                                        downloadModels.remove(position);
//                                        downloadAdapter.notifyDataSetChanged();
//                                        dialog.dismiss();
//
//                                    }
//                                });
//                                builder.setNegativeButton("", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                    }
//                                });
//                                builder.create().show();
//                                return true;
//                            }
//
//
//                        });
//                    } else {
//
//                    }
//                }
//
//            else if (fromMessageType.equals("reply_video")) {
//                    if (fromUserID.equals(messageSenderId)) {
//                        holder.rep_message_video.setVisibility(View.VISIBLE);
//                        holder.rep_chat_video.setVisibility(View.VISIBLE);
//                        holder.rep_video.setVisibility(View.VISIBLE);
//
//
//                        updateseenmessageMesagges(chat.getMessageID(), chat.getId());
//
//                        if (chat.getIsseen().equals("true")) {
//                            holder.single_check_reply_video_one.setVisibility(View.VISIBLE);
//                            holder.single_check_reply_video_two.setVisibility(View.VISIBLE);
//                        } else if (chat.getIsseen().equals("false")) {
//                            holder.single_check_reply_video_three.setVisibility(View.VISIBLE);
//                            holder.single_check_reply_video_four.setVisibility(View.VISIBLE);
//                        } else {
//                            holder.single_check_reply_video_five.setVisibility(View.VISIBLE);
//                        }
//                        try {
//                            outputString = decrypt(chat.getMessage(), holder.password_null.getText().toString());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                        long interval = 1000;
//                        RequestOptions options = new RequestOptions().frame(interval);
//
//                        Glide.with(mContext).asBitmap().load(chat.getPreMessage()).apply(options).into(holder.rep_message_video);
//
//                        holder.rep_chat_video.setText(outputString);
//
//                        holder.rep_text.setOnLongClickListener(new View.OnLongClickListener() {
//                            @Override
//                            public boolean onLongClick(View v) {
//
//                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MessageActivity.this);
//                                builder.setTitle("Choose option");
//                                builder.setMessage("delete Message... ?");
//                                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//
//                                        dialog.dismiss();
//                                    }
//                                });
//                                builder.setNeutralButton("Everyone", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                        deleteMessageForEveryone(position, holder);
//
//
//                                    }
//                                });
//                                builder.setNegativeButton("Me", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        deleteSentMessage(position, holder);
//                                        dialog.dismiss();
//                                    }
//                                });
//                                builder.create().show();
//                                return true;
//                            }
//                        });
//
//
//                    } else if (fromUserID.equals(userid)) {
//                        holder.rep_message_video.setVisibility(View.VISIBLE);
//                        holder.rep_chat_video.setVisibility(View.VISIBLE);
//                        holder.rep_video.setVisibility(View.VISIBLE);
//
//
//                        if (chat.getIsseen().equals("false")) {
//                            holder.id_new.setVisibility(View.VISIBLE);
//                        } else {
//                            holder.id_new.setVisibility(View.GONE);
//                        }
//
//                        try {
//                            outputString = decrypt(chat.getMessage(), holder.password_null.getText().toString());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                        holder.rep_chat_video.setText(outputString);
//
//                        long interval = 1000;
//                        RequestOptions options = new RequestOptions().frame(interval);
//
//                        Glide.with(mContext).asBitmap().load(chat.getPreMessage()).apply(options).into(holder.rep_message_video);
//
//
//                        holder.rep_video.setOnLongClickListener(new View.OnLongClickListener() {
//                            @Override
//                            public boolean onLongClick(View v) {
//                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MessageActivity.this);
//                                builder.setTitle("Choose option");
//                                builder.setMessage("delete Message... ?");
//                                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//
//                                        dialog.dismiss();
//                                    }
//                                });
//                                builder.setNeutralButton("delete", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        deleteReceiveMessage(position, holder);
//                                        realm.beginTransaction();
//                                        RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
//                                        item.deleteAllFromRealm();
//                                        realm.commitTransaction();
//                                        downloadModels.remove(position);
//                                        downloadAdapter.notifyDataSetChanged();
//                                        dialog.dismiss();
//
//                                    }
//                                });
//                                builder.setNegativeButton("", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                    }
//                                });
//                                builder.create().show();
//                                return true;
//                            }
//                        });
//
//                    } else {
//
//                    }
//
//                }
//
//            else if (fromMessageType.equals("reply_pdf")) {
//                    if (fromUserID.equals(messageSenderId)) {
//                        holder.rep_message_pdf.setVisibility(View.VISIBLE);
//                        holder.rep_chat_pdf.setVisibility(View.VISIBLE);
//                        holder.rep_pdf.setVisibility(View.VISIBLE);
//                        holder.rep_pdf_name.setVisibility(View.VISIBLE);
//                        holder.rep_pdf_name.setText(chat.getPreMessage());
//
//
//                        updateseenmessageMesagges(chat.getMessageID(), chat.getId());
//
//                        if (chat.getIsseen().equals("true")) {
//                            holder.single_check_reply_pdf_one.setVisibility(View.VISIBLE);
//                            holder.single_check_reply_pdf_two.setVisibility(View.VISIBLE);
//                        } else if (chat.getIsseen().equals("false")) {
//                            holder.single_check_reply_pdf_three.setVisibility(View.VISIBLE);
//                            holder.single_check_reply_pdf_four.setVisibility(View.VISIBLE);
//                        } else {
//                            holder.single_check_reply_pdf_five.setVisibility(View.VISIBLE);
//                        }
//
//                        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/watsapp-6d8e6.appspot.com/o/file.png?alt=media&token=da730f2c-5760-4d67-b45b-b8ff497039b6")
//                                .into(holder.rep_message_pdf);
//
//
//                        try {
//                            outputString = decrypt(chat.getMessage(), holder.password_null.getText().toString());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//
//                        }
//                        holder.rep_chat_pdf.setText(outputString);
//
//
//                        holder.rep_pdf.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                holder.download_pdf.setVisibility(View.VISIBLE);
//                            }
//                        });
//
//                        holder.download_pdf.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(chat.getMessage()));
//                                holder.itemView.getContext().startActivity(intent);
//
//                            }
//                        });
//
//
//                        holder.reply_pdf.setOnLongClickListener(new View.OnLongClickListener() {
//                            @Override
//                            public boolean onLongClick(View v) {
//                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MessageActivity.this);
//                                builder.setTitle("Choose option");
//                                builder.setMessage("delete Message... ?");
//                                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//
//                                        dialog.dismiss();
//                                    }
//                                });
//                                builder.setNeutralButton("Everyone", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                        deleteMessageForEveryone(position, holder);
//                                        realm.beginTransaction();
//                                        RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
//                                        item.deleteAllFromRealm();
//                                        realm.commitTransaction();
//                                        downloadModels.remove(position);
//                                        downloadAdapter.notifyDataSetChanged();
//
//                                    }
//                                });
//                                builder.setNegativeButton("Me", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        deleteSentMessage(position, holder);
//                                        realm.beginTransaction();
//                                        RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
//                                        item.deleteAllFromRealm();
//                                        realm.commitTransaction();
//                                        downloadModels.remove(position);
//                                        downloadAdapter.notifyDataSetChanged();
//                                        dialog.dismiss();
//                                    }
//                                });
//                                builder.create().show();
//                                return true;
//                            }
//                        });
//
//                    } else {
//                        holder.rep_message_pdf.setVisibility(View.VISIBLE);
//                        holder.rep_chat_pdf.setVisibility(View.VISIBLE);
//                        holder.rep_pdf.setVisibility(View.VISIBLE);
//                        holder.rep_pdf_name.setVisibility(View.VISIBLE);
//                        holder.rep_pdf_name.setText(chat.getPreMessage());
//
//
//                        if (chat.getIsseen().equals("false")) {
//                            holder.id_new.setVisibility(View.VISIBLE);
//                        } else {
//                            holder.id_new.setVisibility(View.GONE);
//                        }
//
//
//                        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/watsapp-6d8e6.appspot.com/o/file.png?alt=media&token=da730f2c-5760-4d67-b45b-b8ff497039b6")
//                                .into(holder.rep_message_pdf);
//
//
//                        try {
//                            outputString = decrypt(chat.getMessage(), holder.password_null.getText().toString());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//
//                        }
//
//
//                        holder.rep_chat_pdf.setText(outputString);
//
//
//                        holder.rep_pdf.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                holder.download_pdf.setVisibility(View.VISIBLE);
//                            }
//                        });
//
//                        holder.download_pdf.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(chat.getMessage()));
//                                holder.itemView.getContext().startActivity(intent);
//
//                            }
//                        });
//
//
//                        holder.reply_pdf.setOnLongClickListener(new View.OnLongClickListener() {
//                            @Override
//                            public boolean onLongClick(View v) {
//                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MessageActivity.this);
//                                builder.setTitle("Choose option");
//                                builder.setMessage("delete Message... ?");
//                                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                    }
//                                });
//                                builder.setNeutralButton("delete", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        deleteReceiveMessage(position, holder);
//                                        realm.beginTransaction();
//                                        RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
//                                        item.deleteAllFromRealm();
//                                        realm.commitTransaction();
//                                        downloadModels.remove(position);
//                                        downloadAdapter.notifyDataSetChanged();
//                                        dialog.dismiss();
//                                    }
//                                });
//                                builder.setNegativeButton("", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                    }
//                                });
//                                builder.create().show();
//                                return true;
//                            }
//                        });
//                    }
//                }

            else {
                    holder.download_pdf.setVisibility(View.GONE);
                }


        }

        private void deleteSentMessage(int position, final RecyclerView.ViewHolder holder) {

            String suri = FirebaseDatabase.getInstance().getReference().child(fuser.getUid()).child(mChat.get(position).getReceiver()).getKey();
            DownloadModel chat = mChat.get(position);

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

        private void deleteReceiveMessage(int position, final RecyclerView.ViewHolder holder) {
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

        private void deleteMessageForEveryone(final int position, final RecyclerView.ViewHolder holder) {

            final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            rootRef.child("Chats")
                    .child(fuser.getUid())
                    .child(userid)
                    .child(mChat.get(position).getMessageID())
                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        holder.itemView.setVisibility(View.INVISIBLE);

                        rootRef.child("Chats")
                                .child(userid)
                                .child(fuser.getUid())
                                .child(mChat.get(position).getMessageID())
                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    DatabaseReference rf = FirebaseDatabase.getInstance().getReference("DeleteSingleUserMessage");

                                    String id = generateString(12);

                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("messageID", mChat.get(position).getMessageID());
                                    hashMap.put("id", id);

                                    rf.child(fuser.getUid())
                                            .child(userid)
                                            .child(id)
                                            .updateChildren(hashMap);


                                    Toast.makeText(holder.itemView.getContext(), "Deleted Successfully...", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(holder.itemView.getContext(), "Error occurred..", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }

        private boolean pauseDownload(DownloadModel downloadModel) {
            int updatedRow=0;
            ContentValues contentValues=new ContentValues();
            contentValues.put("control",1);

            try{
                updatedRow=context.getContentResolver().update(Uri.parse("content://downloads/my_downloads"),contentValues,"title=?",new String[]{downloadModel.getTitle()});
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return 0<updatedRow;
        }

        private boolean resumeDownload(DownloadModel downloadModel) {
            int updatedRow=0;
            ContentValues contentValues=new ContentValues();
            contentValues.put("control",0);

            try{
                updatedRow=context.getContentResolver().update(Uri.parse("content://downloads/my_downloads"),contentValues,"title=?",new String[]{downloadModel.getTitle()});
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return 0<updatedRow;
        }

        @Override
        public int getItemCount() {
            return mChat.size();
        }

        @Override
        public int getItemViewType(int position) {
            fuser = FirebaseAuth.getInstance().getCurrentUser();
            if (mChat.get(position).getSender().equals(fuser.getUid())) {
                return MSG_TYPE_RIGHT;
            } else  {
                return MSG_TYPE_LEFT;
            }

        }

        public void changeItem(long downloadid) {
            int i = 0;
            for (DownloadModel downloadModel : mChat) {
                if (downloadid == downloadModel.getDownloadId()) {
                    notifyItemChanged(i);
                }
                i++;
            }
        }

        public boolean ChangeItemWithStatus(final String message, long downloadid) {
            boolean comp = false;
            int i = 0;
            for (final DownloadModel downloadModel : mChat) {
                if (downloadid == downloadModel.getDownloadId()) {
                    Realm realm = Realm.getDefaultInstance();
                    final int finalI = i;
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            mChat.get(finalI).setStatus(message);
                            notifyItemChanged(finalI);
                            realm.copyToRealmOrUpdate(mChat.get(finalI));
                        }
                    });
                    comp = true;
                }
                i++;
            }
            return comp;
        }

        public void setChangeItemFilePath(final String path, long id) {
            Realm realm = Realm.getDefaultInstance();
            int i = 0;
            for (DownloadModel downloadModel : mChat) {
                if (id == downloadModel.getDownloadId()) {
                    final int finalI = i;
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            mChat.get(finalI).setMessage(path);
                            mChat.get(finalI).setIsseen("true");
                            notifyItemChanged(finalI);
                            realm.copyToRealmOrUpdate(mChat.get(finalI));
                        }
                    });
                }
                i++;
            }
        }

        public void update(final String path, long id) {
            Realm realm = Realm.getDefaultInstance();
            int i = 0;
            for (DownloadModel downloadModel : mChat) {
                if (id == downloadModel.getDownloadId()) {
                    final int finalI = i;
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            mChat.get(finalI).setIsseen("false");
                            notifyItemChanged(finalI);
                            realm.copyToRealmOrUpdate(mChat.get(finalI));
                        }
                    });
                }
                i++;
            }
        }

        public void changeItemWithSeen(final String seen, long downloadid) {
            Realm realm = Realm.getDefaultInstance();
            int i = 0;
            for (DownloadModel downloadModel : mChat) {
                if (downloadid == downloadModel.getDownloadId()) {
                    final int finalI = i;
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            mChat.get(finalI).setIsseen("false");
                            notifyItemChanged(finalI);
                            realm.copyToRealmOrUpdate(mChat.get(finalI));
                        }
                    });
                }
                i++;
            }
        }

    }

    public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.DownloadViewHolder> {

        FirebaseAuth mAuth;
        String outputString, convertString;
        public static final int MSG_TYPE_LEFT = 0;
        public static final int MSG_TYPE_RIGHT = 1;
        Context context;
        List<DownloadModel> mChat = new ArrayList<>();
        ItemClickListener clickListener;

        public MessageAdapter(Context context, List<DownloadModel> mChat) {
            this.context = context;
            this.mChat = mChat;
        }

        @NonNull
        @Override
        public DownloadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == MSG_TYPE_RIGHT) {
                View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
                return new DownloadViewHolder(view);
            } else {
                View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
                return new DownloadViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull DownloadViewHolder holder, int position) {

            final DownloadModel chat = mChat.get(position);
//            final DownloadViewHolder holder = (DownloadAdapter.DownloadViewHolder) Viewholder;


            mAuth = FirebaseAuth.getInstance();
            final String messageSenderId = mAuth.getCurrentUser().getUid();
            String fromMessageType = chat.getType();
            String fromUserID = chat.getSender();

//            if (position==0)
//            {
//            }
//           else if(mChat.get(position-1).getIsseen().equals(mChat.get(position).getIsseen()))
//            {
//                holder.date.setVisibility(View.GONE);
//                Toast.makeText(MessageActivity.this, position+mChat.get(position-1).getIsseen()+mChat.get(position).getIsseen()+"yes", Toast.LENGTH_SHORT).show();
//            }
//           else
//               {
//                   holder.date.setVisibility(View.VISIBLE);
//                   Toast.makeText(MessageActivity.this, "no", Toast.LENGTH_SHORT).show();
//               }

            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currendateFormat = new SimpleDateFormat("MMM dd");
            saveCurrentDate = currendateFormat.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currenTimeFormat = new SimpleDateFormat("hh:mm a");
            saveCurrentTime = currenTimeFormat.format(calForTime.getTime());


//            if (position == mChat.size() - 1) {
//                if (chat.getSender().equals(fromUserID)){
//                    if (chat.getDate().equals(saveCurrentDate)){
//
//                    }
//                    else {
//                    }
//
//                }
//                else if (chat.getReceiver().equals(userid)&& (chat.getDate().equals(saveCurrentDate))){
//
//                }
//                else {
//
//                }
//
//            }
//

//            DatabaseReference  databaseRef = FirebaseDatabase.getInstance().getReference("Chats").child(fuser.getUid()).child(userid);
//            databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//
//                              String isseen = snapshot.child("isseen").getValue().toString();
//
//                            if (position == mChat.size() - 1) {
//                                if (isseen.equals("false")) {
//                                    holder.txt_seen.setVisibility(View.VISIBLE);
//                                    holder.txt_seen.setText("Seen");
//
//                                }
//                                else {
//                                    holder.txt_seen.setVisibility(View.VISIBLE);
//                                    holder.txt_seen.setText("Delivered");
//
//                                }
//                            } else {
//
//                                holder.txt_seen.setVisibility(View.GONE);
//                            }
//                        }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                }
//            });
//


            holder.text_show_password.setEmoticonProvider(Android8EmoticonProvider.create());
            holder.show_message.setEmoticonProvider(Android8EmoticonProvider.create());

            holder.text_show_password.setEmoticonSize(60);
            holder.show_message.setEmoticonSize(60);


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
                        updateseenmessageMesagges(chat.getMessageID(), chat.getId());
                    } else {
                        holder.single_check_text_five.setVisibility(View.VISIBLE);
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


                            reply_preview_message.setText(String.valueOf(position));

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
                            //     Toast.makeText(MessageActivity.this, String.valueOf(position), Toast.LENGTH_SHORT).show();
                            holder.show_message.setText(outputString);
                            holder.password.setVisibility(View.VISIBLE);
                            holder.password_ok.setVisibility(View.VISIBLE);


                        }
                    });

                    holder.password_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.show_message.setEmoticonProvider(Android8EmoticonProvider.create());
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

                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MessageActivity.this);
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
                                    realm.beginTransaction();
                                    RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", id).findAll();
                                    item.remove(0);
                                    realm.commitTransaction();
                                    downloadModels.remove(position);
                                    downloadAdapter.notifyDataSetChanged();


                                }
                            });
                            builder.setNegativeButton("Me", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    deleteSentMessage(position, holder);
                                    realm.beginTransaction();
                                    RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
                                    item.deleteAllFromRealm();
                                    realm.commitTransaction();
                                    downloadModels.remove(position);
                                    downloadAdapter.notifyDataSetChanged();

                                }
                            });
                            builder.create().show();
                            return true;
                        }
                    });


                } else {

                    holder.linearText.setVisibility(View.VISIBLE);
                    holder.reply_text.setVisibility(View.VISIBLE);
                    holder.text_show_password.setVisibility(View.VISIBLE);
                    holder.textTimeDate.setVisibility(View.VISIBLE);
                    holder.textTimeDate.setText(chat.getTime());


                    if (chat.getIsseen().equals("false")) {
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
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MessageActivity.this);
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
                                    realm.beginTransaction();
                                    RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
                                    item.deleteAllFromRealm();
                                    realm.commitTransaction();
                                    downloadModels.remove(position);
                                    downloadAdapter.notifyDataSetChanged();

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

            else if (fromMessageType.equals("gif")) {

                if (fromUserID.equals(messageSenderId)) {
                    holder.gif_image.setVisibility(View.VISIBLE);
                    holder.show_image.setVisibility(View.GONE);
                    holder.linearimage.setVisibility(View.VISIBLE);
                    holder.imageTimeDate.setVisibility(View.VISIBLE);
                    holder.imageTimeDate.setText(chat.getDate() + "  -  " + chat.getTime());

                    long interval = 100;
                    RequestOptions options = new RequestOptions().frame(interval);

                    Glide.with(MessageActivity.this)
                            .load(chat.getMessage())
                            .apply(options)
                            .into(holder.gif_image);

                    if (chat.getIsseen().equals("true")) {
                        holder.single_check_image_one.setVisibility(View.VISIBLE);
                        holder.single_check_image_two.setVisibility(View.VISIBLE);
                    } else if (chat.getIsseen().equals("false")) {

                        holder.single_check_image_three.setVisibility(View.VISIBLE);
                        holder.single_check_image_four.setVisibility(View.VISIBLE);

                    } else {
                        holder.single_check_image_five.setVisibility(View.VISIBLE);
                    }

                    holder.reply_image.setVisibility(View.GONE);

                    holder.show_image.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MessageActivity.this);
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
                                    realm.beginTransaction();
                                    RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
                                    item.deleteAllFromRealm();
                                    realm.commitTransaction();
                                    downloadModels.remove(position);
                                    downloadAdapter.notifyDataSetChanged();

                                }
                            });
                            builder.setNegativeButton("Me", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteSentMessage(position, holder);
                                    realm.beginTransaction();
                                    RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
                                    item.deleteAllFromRealm();
                                    realm.commitTransaction();
                                    downloadModels.remove(position);
                                    downloadAdapter.notifyDataSetChanged();
                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();
                            return true;
                        }
                    });


                } else if (fromUserID.equals(userid)) {

                    holder.gif_image.setVisibility(View.VISIBLE);
                    holder.show_image.setVisibility(View.GONE);
                    holder.linearimage.setVisibility(View.VISIBLE);
                    holder.imageTimeDate.setVisibility(View.VISIBLE);
                    holder.imageTimeDate.setText(chat.getDate() + "  -  " + chat.getTime());

                    long interval = 1000;
                    RequestOptions options = new RequestOptions().frame(interval);

                    Glide.with(MessageActivity.this)
                            .load(chat.getMessage())
                            .apply(options)
                            .into(holder.gif_image);


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

                    holder.show_image.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {

                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MessageActivity.this);
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
                                    realm.beginTransaction();
                                    RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
                                    item.deleteAllFromRealm();
                                    realm.commitTransaction();
                                    downloadModels.remove(position);
                                    downloadAdapter.notifyDataSetChanged();

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
                } else {

                }
            }

            else if (fromMessageType.equals("image")) {

                if (fromUserID.equals(messageSenderId)) {
                    holder.show_image.setVisibility(View.VISIBLE);
                    holder.linearimage.setVisibility(View.VISIBLE);
                    holder.imageTimeDate.setVisibility(View.VISIBLE);
                    holder.imageTimeDate.setText(chat.getDate() + "  -  " + chat.getTime());
                    //   holder.imageTimeDate.setText(chat.getDate() + "  -  " + chat.getTime());
                    Picasso.get().load(chat.getMessage()).into(holder.show_image);


                    //   holder.show_image.setImageURI(Uri.parse(chat.getMessage()));


                    if (chat.getIsseen().equals("true")) {
                        holder.single_check_image_one.setVisibility(View.VISIBLE);
                        holder.single_check_image_two.setVisibility(View.VISIBLE);
                    } else if (chat.getIsseen().equals("false")) {

                        holder.single_check_image_three.setVisibility(View.VISIBLE);
                        holder.single_check_image_four.setVisibility(View.VISIBLE);
                        //     updateseenmessageMesagges(chat.getMessageID(),chat.getId());


                    } else {
                        holder.single_check_image_five.setVisibility(View.VISIBLE);
                    }


                    holder.reply_image.setVisibility(View.GONE);
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

//                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mChat.get(position).getMessage()));
//                                holder.itemView.getContext().startActivity(intent);
//
//
//                                Toast.makeText(holder.itemView.getContext(), chat.getMessage(), Toast.LENGTH_SHORT).show();

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
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MessageActivity.this);
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
                                    realm.beginTransaction();
                                    RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
                                    item.deleteAllFromRealm();
                                    realm.commitTransaction();
                                    downloadModels.remove(position);
                                    downloadAdapter.notifyDataSetChanged();

                                }
                            });
                            builder.setNegativeButton("Me", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteSentMessage(position, holder);
                                    realm.beginTransaction();
                                    RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
                                    item.deleteAllFromRealm();
                                    realm.commitTransaction();
                                    downloadModels.remove(position);
                                    downloadAdapter.notifyDataSetChanged();
                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();
                            return true;
                        }
                    });


                } else if (fromUserID.equals(userid)) {

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


                    holder.reply_image.setVisibility(View.GONE);
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

                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MessageActivity.this);
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
                                    realm.beginTransaction();
                                    RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
                                    item.deleteAllFromRealm();
                                    realm.commitTransaction();
                                    downloadModels.remove(position);
                                    downloadAdapter.notifyDataSetChanged();

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
                } else {

                }
            }

            else if (fromMessageType.equals("video")) {
                if (fromUserID.equals(messageSenderId)) {

                    holder.linearVideo.setVisibility(View.VISIBLE);
                    holder.videoTimeDate.setVisibility(View.VISIBLE);
                    holder.videoTimeDate.setText(chat.getDate() + "  -  " + chat.getTime());
                    holder.show_video.setVisibility(View.VISIBLE);
                    holder.reply_video.setVisibility(View.GONE);


                    updateseenmessageMesagges(chat.getMessageID(), chat.getId());

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

                    Glide.with(MessageActivity.this).asBitmap().load(chat.getMessage()).apply(options).into(holder.show_video);

                    holder.reply_video.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            relative_layout_message.setVisibility(View.GONE);
                            reply_bottom.setVisibility(View.VISIBLE);

                            reply_preview_message.setText(chat.getMessage());

                            type.setText("reply_video");

                            Glide.with(MessageActivity.this).asBitmap().load(chat.getMessage()).apply(options).into(reply_image);

                        }
                    });

                    holder.linearVideo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(MessageActivity.this, chat.getMessage(), Toast.LENGTH_SHORT).show();

//                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mChat.get(position).getImagrUrl()));
//                                holder.itemView.getContext().startActivity(intent);

                            Intent intent = new Intent(holder.itemView.getContext(), DirectVideo.class);
                            intent.putExtra("visit_user_id", chat.getReceiver());
                            intent.putExtra("messageId", chat.getMessage());
                            holder.itemView.getContext().startActivity(intent);

                        }
                    });

                    holder.linearVideo.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MessageActivity.this);
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
                                    realm.beginTransaction();
                                    RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
                                    item.deleteAllFromRealm();
                                    realm.commitTransaction();
                                    downloadModels.remove(position);
                                    downloadAdapter.notifyDataSetChanged();

                                }
                            });
                            builder.setNegativeButton("Me", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteSentMessage(position, holder);
                                    realm.beginTransaction();
                                    RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
                                    item.deleteAllFromRealm();
                                    realm.commitTransaction();
                                    downloadModels.remove(position);
                                    downloadAdapter.notifyDataSetChanged();

                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();
                            return true;
                        }
                    });


                } else if (fromUserID.equals(userid)) {

                    holder.linearVideo.setVisibility(View.VISIBLE);
                    holder.videoTimeDate.setText(chat.getDate() + "  -  " + chat.getTime());
                    holder.show_video.setVisibility(View.VISIBLE);
                    holder.reply_video.setVisibility(View.GONE);

                    holder.file_title.setText(chat.getTitle());
                    holder.file_status.setText(chat.getStatus());
                    holder.file_progress.setProgress(Integer.parseInt(chat.getProgress()));
                    holder.file_size.setText(chat.getFile_size());


                    if (chat.getIsseen().equals("false")) {
                        holder.main_rel.setVisibility(View.VISIBLE);
                        holder.videoTimeDate.setVisibility(View.GONE);

                    } else {
                        holder.main_rel.setVisibility(View.GONE);
                        holder.videoTimeDate.setVisibility(View.VISIBLE);
                    }

                    holder.download_video_size.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            downloadMediaFiles(chat.getImagrUrl(), chat.getId());
                        }
                    });


                    if (chat.getIsseen().equals("false")) {
                        holder.id_new.setVisibility(View.VISIBLE);
                    } else {
                        holder.id_new.setVisibility(View.GONE);
                    }

                    if (chat.isIs_paused()) {
                        holder.pause_resume.setText("RESUME");
                    } else {
                        holder.pause_resume.setText("");
                    }

                    if (chat.getStatus().equalsIgnoreCase("RESUME")) {
                        holder.file_status.setText("Running");
                    }


                    holder.pause_resume.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (chat.isIs_paused()) {
                                chat.setIs_paused(false);
                                holder.pause_resume.setText("PAUSE");
                                chat.setStatus("RESUME");
                                holder.file_status.setText("Running");
                                if (!resumeDownload(chat)) {
                                    Toast.makeText(context, "Failed to Resume", Toast.LENGTH_SHORT).show();
                                }
                                notifyItemChanged(position);
                            } else {
                                chat.setIs_paused(true);
                                holder.pause_resume.setText("RESUME");
                                chat.setStatus("PAUSE");
                                holder.file_status.setText("PAUSE");
                                if (!pauseDownload(chat)) {
                                    Toast.makeText(context, "Failed to Pause", Toast.LENGTH_SHORT).show();
                                }
                                notifyItemChanged(position);
                            }
                        }
                    });

                    holder.main_rel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            clickListener.onCLickItem(chat.getFile_path());
                        }
                    });

                    holder.sharefile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            clickListener.onShareClick(chat);
                        }
                    });


                    long interval = 1000;
                    RequestOptions options = new RequestOptions().frame(interval);

                    Glide.with(MessageActivity.this).asBitmap().load(chat.getImagrUrl()).apply(options).into(holder.show_video);


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

                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MessageActivity.this);
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
                                    realm.beginTransaction();
                                    RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
                                    item.deleteAllFromRealm();
                                    realm.commitTransaction();
                                    downloadModels.remove(position);
                                    downloadAdapter.notifyDataSetChanged();
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

                } else {

                }
            }

            else if (fromMessageType.equals("PDF")) {
                {
                    if (fromUserID.equals(messageSenderId)) {
                        holder.linearpdf.setVisibility(View.VISIBLE);
                        holder.pdfName.setVisibility(View.VISIBLE);
                        holder.pdfName.setText(chat.getBio());
                        holder.linearName.setVisibility(View.VISIBLE);
                        holder.pdfTimeDate.setVisibility(View.VISIBLE);
                        holder.show_pdf.setVisibility(View.VISIBLE);
                        holder.pdfTimeDate.setText(chat.getDate() + "  -  " + chat.getTime());
                        Picasso.get().load("file://" + chat.getImagrUrl()).into(holder.show_pdf);

                        updateseenmessageMesagges(chat.getMessageID(), chat.getId());

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

                                opendocument(chat.getMessage());

                                generateImageFromPdf(Uri.parse(chat.getMessage()), chat.getId());

                            }
                        });

                        holder.download_pdf.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mChat.get(position).getMessage()));
                                holder.itemView.getContext().startActivity(intent);

                            }
                        });

                        holder.reply_pdf.setVisibility(View.GONE);
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

                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MessageActivity.this);
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
                                        realm.beginTransaction();
                                        RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
                                        item.deleteAllFromRealm();
                                        realm.commitTransaction();
                                        downloadModels.remove(position);
                                        downloadAdapter.notifyDataSetChanged();

                                    }
                                });
                                builder.setNegativeButton("Me", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        deleteSentMessage(position, holder);
                                        realm.beginTransaction();
                                        RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
                                        item.deleteAllFromRealm();
                                        realm.commitTransaction();
                                        downloadModels.remove(position);
                                        downloadAdapter.notifyDataSetChanged();
                                        dialog.dismiss();
                                    }
                                });
                                builder.create().show();
                                return true;
                            }
                        });


                    } else if (fromUserID.equals(userid)) {
                        holder.linearName.setVisibility(View.VISIBLE);
                        holder.pdfName.setVisibility(View.VISIBLE);
                        holder.pdfName.setText(chat.getBio());
                        holder.linearpdf.setVisibility(View.VISIBLE);
                        holder.pdfTimeDate.setVisibility(View.VISIBLE);
                        holder.pdfTimeDate.setText(chat.getDate() + "  -  " + chat.getTime());
                        holder.show_pdf.setVisibility(View.VISIBLE);
                        Picasso.get().load("file://" + chat.getImagrUrl()).into(holder.show_pdf);


                        if (chat.getIsseen().equals("false")) {
                            holder.id_new.setVisibility(View.VISIBLE);
                        } else {
                            holder.id_new.setVisibility(View.GONE);
                        }

                        holder.file_title_document.setText(chat.getTitle());
                        holder.file_status_document.setText(chat.getStatus());
                        holder.file_progress_document.setProgress(Integer.parseInt(chat.getProgress()));
                        holder.file_size_document.setText(chat.getFile_size());

                        if (chat.getIsseen().equals("false")) {
                            holder.main_rel_document.setVisibility(View.VISIBLE);
                            holder.show_pdf.setVisibility(View.GONE);

                        } else {
                            holder.main_rel_document.setVisibility(View.GONE);
                            holder.show_pdf.setVisibility(View.VISIBLE);
                        }

                        holder.pdf_download.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                downloadMediaFiles(chat.getImagrUrl(), chat.getId());
                            }
                        });

                        if (chat.isIs_paused()) {
                            holder.pause_resume_document.setText("RESUME");
                        } else {
                            holder.pause_resume_document.setText("");
                        }

                        if (chat.getStatus().equalsIgnoreCase("RESUME")) {
                            holder.file_status_document.setText("Running");
                        }

                        holder.pause_resume_document.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (chat.isIs_paused()) {
                                    chat.setIs_paused(false);
                                    holder.pause_resume_document.setText("PAUSE");
                                    chat.setStatus("RESUME");
                                    holder.file_status_document.setText("Running");
                                    if (!resumeDownload(chat)) {
                                        Toast.makeText(context, "Failed to Resume", Toast.LENGTH_SHORT).show();
                                    }
                                    notifyItemChanged(position);
                                } else {
                                    chat.setIs_paused(true);
                                    holder.pause_resume_document.setText("RESUME");
                                    chat.setStatus("PAUSE");
                                    holder.file_status_document.setText("PAUSE");
                                    if (!pauseDownload(chat)) {
                                        Toast.makeText(context, "Failed to Pause", Toast.LENGTH_SHORT).show();
                                    }
                                    notifyItemChanged(position);
                                }
                            }
                        });

                        holder.main_rel_document.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                clickListener.onCLickItem(chat.getFile_path());
                            }
                        });

                        holder.sharefile.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                clickListener.onShareClick(chat);
                            }
                        });

                        holder.reply_pdf.setVisibility(View.GONE);

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

                                if (chat.getIsseen().equals("true")) {
                                    opendocument(chat.getMessage());
                                    generateImageFromPdf(Uri.parse("file://" + chat.getMessage()), chat.getId());

                                } else {
                                    Toast.makeText(MessageActivity.this, "please download item", Toast.LENGTH_SHORT).show();

                                }


//                                    generateImageFromPdf(Uri.parse(chat.getMessage()),chat.getId());
//                                    holder.download_pdf.setVisibility(View.VISIBLE);
                            }
                        });

                        holder.download_pdf.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(MessageActivity.this, UpdateRecordActivity.class);
                                intent.putExtra("file", chat.getMessage());
                                startActivity(intent);
//                                    Intent intent = new Intent(Intent.ACTION_VIEW,  Uri.fromFile(new File(chat.getMessage())));
//                                    holder.itemView.getContext().startActivity(intent);

                            }
                        });

                        holder.linearpdf.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {

                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MessageActivity.this);
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
                                        realm.beginTransaction();
                                        RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
                                        item.deleteAllFromRealm();
                                        realm.commitTransaction();
                                        downloadModels.remove(position);
                                        downloadAdapter.notifyDataSetChanged();
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


                    } else {

                    }
                }

            }

            else if (fromMessageType.equals("docx")) {
                {
                    if (fromUserID.equals(messageSenderId)) {
                        holder.linearpdf.setVisibility(View.VISIBLE);
                        holder.pdfName.setVisibility(View.VISIBLE);
                        holder.pdfName.setText(chat.getBio());
                        holder.linearName.setVisibility(View.VISIBLE);
                        holder.pdfTimeDate.setVisibility(View.VISIBLE);
                        holder.show_pdf.setVisibility(View.GONE);
                        holder.relative_pdf.setVisibility(View.GONE);
                        holder.pdfTimeDate.setText(chat.getDate() + "  -  " + chat.getTime());

                        updateseenmessageMesagges(chat.getMessageID(), chat.getId());


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

                                opendocument(chat.getMessage());
                            }
                        });

                        holder.download_pdf.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mChat.get(position).getMessage()));
                                holder.itemView.getContext().startActivity(intent);

                            }
                        });

                        holder.reply_pdf.setVisibility(View.GONE);
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

                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MessageActivity.this);
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
                                        realm.beginTransaction();
                                        RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
                                        item.deleteAllFromRealm();
                                        realm.commitTransaction();
                                        downloadModels.remove(position);
                                        downloadAdapter.notifyDataSetChanged();

                                    }
                                });
                                builder.setNegativeButton("Me", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        deleteSentMessage(position, holder);
                                        realm.beginTransaction();
                                        RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
                                        item.deleteAllFromRealm();
                                        realm.commitTransaction();
                                        downloadModels.remove(position);
                                        downloadAdapter.notifyDataSetChanged();
                                        dialog.dismiss();
                                    }
                                });
                                builder.create().show();
                                return true;
                            }
                        });


                    } else if (fromUserID.equals(userid)) {
                        holder.linearName.setVisibility(View.VISIBLE);
                        holder.pdfName.setVisibility(View.VISIBLE);
                        holder.pdfName.setText(chat.getBio());
                        holder.linearpdf.setVisibility(View.VISIBLE);
                        holder.pdfTimeDate.setVisibility(View.VISIBLE);
                        holder.pdfTimeDate.setText(chat.getDate() + "  -  " + chat.getTime());
                        holder.show_pdf.setVisibility(View.GONE);
                        //   holder.relative_pdf.setVisibility(View.GONE);


                        if (chat.getIsseen().equals("false")) {
                            holder.id_new.setVisibility(View.VISIBLE);
                        } else {
                            holder.id_new.setVisibility(View.GONE);
                        }


                        holder.file_title_document.setText(chat.getTitle());
                        holder.file_status_document.setText(chat.getStatus());
                        holder.file_progress_document.setProgress(Integer.parseInt(chat.getProgress()));
                        holder.file_size_document.setText(chat.getFile_size());


                        if (chat.getIsseen().equals("false")) {
                            holder.main_rel_document.setVisibility(View.VISIBLE);
                            holder.show_pdf.setVisibility(View.GONE);

                        } else {
                            holder.main_rel_document.setVisibility(View.GONE);
                            holder.show_pdf.setVisibility(View.GONE);
                        }


                        holder.pdf_download.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                downloadMediaFiles(chat.getImagrUrl(), chat.getId());
                            }
                        });


                        if (chat.isIs_paused()) {
                            holder.pause_resume_document.setText("RESUME");
                        } else {
                            holder.pause_resume_document.setText("");
                        }

                        if (chat.getStatus().equalsIgnoreCase("RESUME")) {
                            holder.file_status_document.setText("Running");
                        }


                        holder.pause_resume_document.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (chat.isIs_paused()) {
                                    chat.setIs_paused(false);
                                    holder.pause_resume_document.setText("PAUSE");
                                    chat.setStatus("RESUME");
                                    holder.file_status_document.setText("Running");
                                    if (!resumeDownload(chat)) {
                                        Toast.makeText(context, "Failed to Resume", Toast.LENGTH_SHORT).show();
                                    }
                                    notifyItemChanged(position);
                                } else {
                                    chat.setIs_paused(true);
                                    holder.pause_resume_document.setText("RESUME");
                                    chat.setStatus("PAUSE");
                                    holder.file_status_document.setText("PAUSE");
                                    if (!pauseDownload(chat)) {
                                        Toast.makeText(context, "Failed to Pause", Toast.LENGTH_SHORT).show();
                                    }
                                    notifyItemChanged(position);
                                }
                            }
                        });

                        holder.main_rel_document.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                clickListener.onCLickItem(chat.getFile_path());
                            }
                        });

                        holder.sharefile.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                clickListener.onShareClick(chat);
                            }
                        });


                        holder.reply_pdf.setVisibility(View.GONE);
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
                                if (chat.getIsseen().equals("true")) {
                                    opendocument(chat.getMessage());

                                } else {
                                    Toast.makeText(MessageActivity.this, "please download item", Toast.LENGTH_SHORT).show();

                                }


//                                generateImageFromPdf(Uri.parse(chat.getMessage()),chat.getId());
//                                holder.download_pdf.setVisibility(View.VISIBLE);
                            }
                        });

                        holder.download_pdf.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(MessageActivity.this, UpdateRecordActivity.class);
                                intent.putExtra("file", chat.getMessage());
                                startActivity(intent);
//                                    Intent intent = new Intent(Intent.ACTION_VIEW,  Uri.fromFile(new File(chat.getMessage())));
//                                    holder.itemView.getContext().startActivity(intent);

                            }
                        });


                        holder.linearpdf.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {

                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MessageActivity.this);
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
                                        realm.beginTransaction();
                                        RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
                                        item.deleteAllFromRealm();
                                        realm.commitTransaction();
                                        downloadModels.remove(position);
                                        downloadAdapter.notifyDataSetChanged();
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


                    } else {

                    }
                }
            }

            else if (fromMessageType.equals("reply_text")) {
                if (fromUserID.equals(messageSenderId)) {

                    holder.rep_message_text.setVisibility(View.VISIBLE);
                    holder.rep_chat_text.setVisibility(View.VISIBLE);
                    holder.rep_text.setVisibility(View.VISIBLE);
//
//                        holder.rep_text.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                               recyclerView.scrollToPosition(Integer.parseInt(chat.getPreMessage()));
//
//                                if (position ==Integer.parseInt(chat.getPreMessage())){
//                                    holder.textTimeDate.setText("hello suri rathore");
//                                    Toast.makeText(MessageActivity.this, chat.getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                                else {
//                                    holder.textTimeDate.setText("hello suri rathore");
//
//
//                                }
//                            }
//                        });

                    updateseenmessageMesagges(chat.getMessageID(), chat.getId());

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

                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MessageActivity.this);
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
                                    realm.beginTransaction();
                                    RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
                                    item.deleteAllFromRealm();
                                    realm.commitTransaction();
                                    downloadModels.remove(position);
                                    downloadAdapter.notifyDataSetChanged();
                                    deleteMessageForEveryone(position, holder);
                                }
                            });
                            builder.setNegativeButton("Me", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteSentMessage(position, holder);
                                    realm.beginTransaction();
                                    RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
                                    item.deleteAllFromRealm();
                                    realm.commitTransaction();
                                    downloadModels.remove(position);
                                    downloadAdapter.notifyDataSetChanged();
                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();
                            return true;
                        }
                    });


                } else if (fromUserID.equals(userid)) {
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
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MessageActivity.this);
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
                                    realm.beginTransaction();
                                    RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
                                    item.deleteAllFromRealm();
                                    realm.commitTransaction();
                                    downloadModels.remove(position);
                                    downloadAdapter.notifyDataSetChanged();
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

                } else {

                }
            }

//            else if (fromMessageType.equals("reply_image")) {
//                    if (fromUserID.equals(messageSenderId)) {
//
//                        holder.rep_message_image.setVisibility(View.VISIBLE);
//                        holder.rep_chat_image.setVisibility(View.VISIBLE);
//                        holder.rep_image.setVisibility(View.VISIBLE);
//
//                        updateseenmessageMesagges(chat.getMessageID(), chat.getId());
//
//
//                        if (chat.getIsseen().equals("true")) {
//                            holder.single_check_reply_image_one.setVisibility(View.VISIBLE);
//                            holder.single_check_reply_image_two.setVisibility(View.VISIBLE);
//                        } else if (chat.getIsseen().equals("false")) {
//                            holder.single_check_reply_image_three.setVisibility(View.VISIBLE);
//                            holder.single_check_reply_image_four.setVisibility(View.VISIBLE);
//                        } else {
//                            holder.single_check_reply_image_five.setVisibility(View.VISIBLE);
//                        }
//
//                        try {
//                            outputString = decrypt(chat.getMessage(), holder.password_null.getText().toString());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//
//                        }
//
//
//                        Picasso.get().load(chat.getPreMessage()).into(holder.rep_message_image);
//                        holder.rep_chat_image.setText(outputString);
//
//
//                        holder.rep_message_image.setOnClickListener(new View.OnClickListener() {
//                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//                            @Override
//                            public void onClick(View v) {
//
//
//                                Intent chatIntent = new Intent(holder.itemView.getContext(), ImageViewActivity.class);
//                                chatIntent.putExtra("url", mChat.get(position).getPreMessage());
//
//
//                                Pair[] pairs = new Pair[1];
//                                pairs[0] = new Pair<View, String>(holder.rep_message_image, "imageTransition");
//
//                                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) holder.itemView.getContext(), pairs);
//
//
//                                holder.itemView.getContext().startActivity(chatIntent, options.toBundle());
//
//
//                            }
//                        });
//
//
//                        holder.rep_image.setOnLongClickListener(new View.OnLongClickListener() {
//                            @Override
//                            public boolean onLongClick(View v) {
//
//                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MessageActivity.this);
//                                builder.setTitle("Choose option");
//                                builder.setMessage("delete Message... ?");
//                                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//
//                                        dialog.dismiss();
//                                    }
//                                });
//                                builder.setNeutralButton("Everyone", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                        deleteMessageForEveryone(position, holder);
//                                        realm.beginTransaction();
//                                        RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
//                                        item.deleteAllFromRealm();
//                                        realm.commitTransaction();
//                                        downloadModels.remove(position);
//                                        downloadAdapter.notifyDataSetChanged();
//
//                                    }
//                                });
//                                builder.setNegativeButton("Me", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        deleteSentMessage(position, holder);
//                                        realm.beginTransaction();
//                                        RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
//                                        item.deleteAllFromRealm();
//                                        realm.commitTransaction();
//                                        downloadModels.remove(position);
//                                        downloadAdapter.notifyDataSetChanged();
//                                        dialog.dismiss();
//                                    }
//                                });
//                                builder.create().show();
//                                return true;
//                            }
//                        });
//
//
//                    } else if (fromUserID.equals(userid)) {
//                        holder.rep_message_image.setVisibility(View.VISIBLE);
//                        holder.rep_chat_image.setVisibility(View.VISIBLE);
//                        holder.rep_image.setVisibility(View.VISIBLE);
//
//
//                        if (chat.getIsseen().equals("false")) {
//                            holder.id_new.setVisibility(View.VISIBLE);
//                        } else {
//                            holder.id_new.setVisibility(View.GONE);
//                        }
//
//
//                        try {
//                            outputString = decrypt(chat.getMessage(), holder.password_null.getText().toString());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//
//                        }
//
//
//                        Picasso.get().load(chat.getPreMessage()).into(holder.rep_message_image);
//                        holder.rep_chat_image.setText(outputString);
//
//
//                        holder.rep_message_image.setOnClickListener(new View.OnClickListener() {
//                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//                            @Override
//                            public void onClick(View v) {
//
//
//                                Intent chatIntent = new Intent(holder.itemView.getContext(), ImageViewActivity.class);
//                                chatIntent.putExtra("url", chat.getPreMessage());
//
//
//                                Pair[] pairs = new Pair[1];
//                                pairs[0] = new Pair<View, String>(holder.rep_message_image, "imageTransition");
//
//                                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) holder.itemView.getContext(), pairs);
//
//
//                                holder.itemView.getContext().startActivity(chatIntent, options.toBundle());
//
//
//                            }
//                        });
//
//
//                        holder.rep_image.setOnLongClickListener(new View.OnLongClickListener() {
//                            @Override
//                            public boolean onLongClick(View v) {
//                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MessageActivity.this);
//                                builder.setTitle("Choose option");
//                                builder.setMessage("delete Message... ?");
//                                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//
//                                        dialog.dismiss();
//                                    }
//                                });
//                                builder.setNeutralButton("delete", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        deleteReceiveMessage(position, holder);
//                                        realm.beginTransaction();
//                                        RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
//                                        item.deleteAllFromRealm();
//                                        realm.commitTransaction();
//                                        downloadModels.remove(position);
//                                        downloadAdapter.notifyDataSetChanged();
//                                        dialog.dismiss();
//
//                                    }
//                                });
//                                builder.setNegativeButton("", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                    }
//                                });
//                                builder.create().show();
//                                return true;
//                            }
//
//
//                        });
//                    } else {
//
//                    }
//                }
//
//            else if (fromMessageType.equals("reply_video")) {
//                    if (fromUserID.equals(messageSenderId)) {
//                        holder.rep_message_video.setVisibility(View.VISIBLE);
//                        holder.rep_chat_video.setVisibility(View.VISIBLE);
//                        holder.rep_video.setVisibility(View.VISIBLE);
//
//
//                        updateseenmessageMesagges(chat.getMessageID(), chat.getId());
//
//                        if (chat.getIsseen().equals("true")) {
//                            holder.single_check_reply_video_one.setVisibility(View.VISIBLE);
//                            holder.single_check_reply_video_two.setVisibility(View.VISIBLE);
//                        } else if (chat.getIsseen().equals("false")) {
//                            holder.single_check_reply_video_three.setVisibility(View.VISIBLE);
//                            holder.single_check_reply_video_four.setVisibility(View.VISIBLE);
//                        } else {
//                            holder.single_check_reply_video_five.setVisibility(View.VISIBLE);
//                        }
//                        try {
//                            outputString = decrypt(chat.getMessage(), holder.password_null.getText().toString());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                        long interval = 1000;
//                        RequestOptions options = new RequestOptions().frame(interval);
//
//                        Glide.with(mContext).asBitmap().load(chat.getPreMessage()).apply(options).into(holder.rep_message_video);
//
//                        holder.rep_chat_video.setText(outputString);
//
//                        holder.rep_text.setOnLongClickListener(new View.OnLongClickListener() {
//                            @Override
//                            public boolean onLongClick(View v) {
//
//                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MessageActivity.this);
//                                builder.setTitle("Choose option");
//                                builder.setMessage("delete Message... ?");
//                                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//
//                                        dialog.dismiss();
//                                    }
//                                });
//                                builder.setNeutralButton("Everyone", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                        deleteMessageForEveryone(position, holder);
//
//
//                                    }
//                                });
//                                builder.setNegativeButton("Me", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        deleteSentMessage(position, holder);
//                                        dialog.dismiss();
//                                    }
//                                });
//                                builder.create().show();
//                                return true;
//                            }
//                        });
//
//
//                    } else if (fromUserID.equals(userid)) {
//                        holder.rep_message_video.setVisibility(View.VISIBLE);
//                        holder.rep_chat_video.setVisibility(View.VISIBLE);
//                        holder.rep_video.setVisibility(View.VISIBLE);
//
//
//                        if (chat.getIsseen().equals("false")) {
//                            holder.id_new.setVisibility(View.VISIBLE);
//                        } else {
//                            holder.id_new.setVisibility(View.GONE);
//                        }
//
//                        try {
//                            outputString = decrypt(chat.getMessage(), holder.password_null.getText().toString());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                        holder.rep_chat_video.setText(outputString);
//
//                        long interval = 1000;
//                        RequestOptions options = new RequestOptions().frame(interval);
//
//                        Glide.with(mContext).asBitmap().load(chat.getPreMessage()).apply(options).into(holder.rep_message_video);
//
//
//                        holder.rep_video.setOnLongClickListener(new View.OnLongClickListener() {
//                            @Override
//                            public boolean onLongClick(View v) {
//                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MessageActivity.this);
//                                builder.setTitle("Choose option");
//                                builder.setMessage("delete Message... ?");
//                                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//
//                                        dialog.dismiss();
//                                    }
//                                });
//                                builder.setNeutralButton("delete", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        deleteReceiveMessage(position, holder);
//                                        realm.beginTransaction();
//                                        RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
//                                        item.deleteAllFromRealm();
//                                        realm.commitTransaction();
//                                        downloadModels.remove(position);
//                                        downloadAdapter.notifyDataSetChanged();
//                                        dialog.dismiss();
//
//                                    }
//                                });
//                                builder.setNegativeButton("", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                    }
//                                });
//                                builder.create().show();
//                                return true;
//                            }
//                        });
//
//                    } else {
//
//                    }
//
//                }
//
//            else if (fromMessageType.equals("reply_pdf")) {
//                    if (fromUserID.equals(messageSenderId)) {
//                        holder.rep_message_pdf.setVisibility(View.VISIBLE);
//                        holder.rep_chat_pdf.setVisibility(View.VISIBLE);
//                        holder.rep_pdf.setVisibility(View.VISIBLE);
//                        holder.rep_pdf_name.setVisibility(View.VISIBLE);
//                        holder.rep_pdf_name.setText(chat.getPreMessage());
//
//
//                        updateseenmessageMesagges(chat.getMessageID(), chat.getId());
//
//                        if (chat.getIsseen().equals("true")) {
//                            holder.single_check_reply_pdf_one.setVisibility(View.VISIBLE);
//                            holder.single_check_reply_pdf_two.setVisibility(View.VISIBLE);
//                        } else if (chat.getIsseen().equals("false")) {
//                            holder.single_check_reply_pdf_three.setVisibility(View.VISIBLE);
//                            holder.single_check_reply_pdf_four.setVisibility(View.VISIBLE);
//                        } else {
//                            holder.single_check_reply_pdf_five.setVisibility(View.VISIBLE);
//                        }
//
//                        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/watsapp-6d8e6.appspot.com/o/file.png?alt=media&token=da730f2c-5760-4d67-b45b-b8ff497039b6")
//                                .into(holder.rep_message_pdf);
//
//
//                        try {
//                            outputString = decrypt(chat.getMessage(), holder.password_null.getText().toString());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//
//                        }
//                        holder.rep_chat_pdf.setText(outputString);
//
//
//                        holder.rep_pdf.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                holder.download_pdf.setVisibility(View.VISIBLE);
//                            }
//                        });
//
//                        holder.download_pdf.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(chat.getMessage()));
//                                holder.itemView.getContext().startActivity(intent);
//
//                            }
//                        });
//
//
//                        holder.reply_pdf.setOnLongClickListener(new View.OnLongClickListener() {
//                            @Override
//                            public boolean onLongClick(View v) {
//                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MessageActivity.this);
//                                builder.setTitle("Choose option");
//                                builder.setMessage("delete Message... ?");
//                                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//
//                                        dialog.dismiss();
//                                    }
//                                });
//                                builder.setNeutralButton("Everyone", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                        deleteMessageForEveryone(position, holder);
//                                        realm.beginTransaction();
//                                        RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
//                                        item.deleteAllFromRealm();
//                                        realm.commitTransaction();
//                                        downloadModels.remove(position);
//                                        downloadAdapter.notifyDataSetChanged();
//
//                                    }
//                                });
//                                builder.setNegativeButton("Me", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        deleteSentMessage(position, holder);
//                                        realm.beginTransaction();
//                                        RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
//                                        item.deleteAllFromRealm();
//                                        realm.commitTransaction();
//                                        downloadModels.remove(position);
//                                        downloadAdapter.notifyDataSetChanged();
//                                        dialog.dismiss();
//                                    }
//                                });
//                                builder.create().show();
//                                return true;
//                            }
//                        });
//
//                    } else {
//                        holder.rep_message_pdf.setVisibility(View.VISIBLE);
//                        holder.rep_chat_pdf.setVisibility(View.VISIBLE);
//                        holder.rep_pdf.setVisibility(View.VISIBLE);
//                        holder.rep_pdf_name.setVisibility(View.VISIBLE);
//                        holder.rep_pdf_name.setText(chat.getPreMessage());
//
//
//                        if (chat.getIsseen().equals("false")) {
//                            holder.id_new.setVisibility(View.VISIBLE);
//                        } else {
//                            holder.id_new.setVisibility(View.GONE);
//                        }
//
//
//                        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/watsapp-6d8e6.appspot.com/o/file.png?alt=media&token=da730f2c-5760-4d67-b45b-b8ff497039b6")
//                                .into(holder.rep_message_pdf);
//
//
//                        try {
//                            outputString = decrypt(chat.getMessage(), holder.password_null.getText().toString());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//
//                        }
//
//
//                        holder.rep_chat_pdf.setText(outputString);
//
//
//                        holder.rep_pdf.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                holder.download_pdf.setVisibility(View.VISIBLE);
//                            }
//                        });
//
//                        holder.download_pdf.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(chat.getMessage()));
//                                holder.itemView.getContext().startActivity(intent);
//
//                            }
//                        });
//
//
//                        holder.reply_pdf.setOnLongClickListener(new View.OnLongClickListener() {
//                            @Override
//                            public boolean onLongClick(View v) {
//                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MessageActivity.this);
//                                builder.setTitle("Choose option");
//                                builder.setMessage("delete Message... ?");
//                                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                    }
//                                });
//                                builder.setNeutralButton("delete", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        deleteReceiveMessage(position, holder);
//                                        realm.beginTransaction();
//                                        RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", chat.getId()).findAll();
//                                        item.deleteAllFromRealm();
//                                        realm.commitTransaction();
//                                        downloadModels.remove(position);
//                                        downloadAdapter.notifyDataSetChanged();
//                                        dialog.dismiss();
//                                    }
//                                });
//                                builder.setNegativeButton("", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                    }
//                                });
//                                builder.create().show();
//                                return true;
//                            }
//                        });
//                    }
//                }

            else {
                holder.download_pdf.setVisibility(View.GONE);
            }


        }

        private void deleteSentMessage(int position, final DownloadViewHolder holder) {

            String suri = FirebaseDatabase.getInstance().getReference().child(fuser.getUid()).child(mChat.get(position).getReceiver()).getKey();
            DownloadModel chat = mChat.get(position);

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

        private void deleteReceiveMessage(int position, final DownloadViewHolder holder) {
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

        private void deleteMessageForEveryone(final int position, final DownloadViewHolder holder) {

            final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            rootRef.child("Chats")
                    .child(fuser.getUid())
                    .child(userid)
                    .child(mChat.get(position).getMessageID())
                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        holder.itemView.setVisibility(View.INVISIBLE);

                        rootRef.child("Chats")
                                .child(userid)
                                .child(fuser.getUid())
                                .child(mChat.get(position).getMessageID())
                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    DatabaseReference rf = FirebaseDatabase.getInstance().getReference("DeleteSingleUserMessage");

                                    String id = generateString(12);

                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("messageID", mChat.get(position).getMessageID());
                                    hashMap.put("id", id);

                                    rf.child(fuser.getUid())
                                            .child(userid)
                                            .child(id)
                                            .updateChildren(hashMap);


                                    Toast.makeText(holder.itemView.getContext(), "Deleted Successfully...", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(holder.itemView.getContext(), "Error occurred..", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }

        private boolean pauseDownload(DownloadModel downloadModel) {
            int updatedRow = 0;
            ContentValues contentValues = new ContentValues();
            contentValues.put("control", 1);

            try {
                updatedRow = context.getContentResolver().update(Uri.parse("content://downloads/my_downloads"), contentValues, "title=?", new String[]{downloadModel.getTitle()});
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0 < updatedRow;
        }

        private boolean resumeDownload(DownloadModel downloadModel) {
            int updatedRow = 0;
            ContentValues contentValues = new ContentValues();
            contentValues.put("control", 0);

            try {
                updatedRow = context.getContentResolver().update(Uri.parse("content://downloads/my_downloads"), contentValues, "title=?", new String[]{downloadModel.getTitle()});
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0 < updatedRow;
        }

        @Override
        public int getItemCount() {
            return mChat.size();
        }


        public class DownloadViewHolder extends RecyclerView.ViewHolder {

            public TextView id_new, date, play, pause, imageTimeDate, videoTimeDate, pdfTimeDate, delete_one_message, delete_text_message, download_pdf,
                    open_video_activity, reply_image, reply_pdf, reply_video, reply_text, rep_message_text, rep_chat_text, rep_chat_image, rep_chat_video, rep_chat_pdf, rep_pdf_name, single_check_reply_text_one, single_check_reply_text_two, single_check_reply_text_three, single_check_reply_text_four, single_check_reply_text_five, single_check_reply_image_one, single_check_reply_image_two, single_check_reply_image_three, single_check_reply_image_four, single_check_reply_image_five, single_check_reply_pdf_one, single_check_reply_pdf_two, single_check_reply_pdf_three, single_check_reply_pdf_four, single_check_reply_pdf_five,
                    single_check_reply_video_one, single_check_reply_video_two, single_check_reply_video_three, single_check_reply_video_four, single_check_reply_video_five, single_check_text_one, single_check_text_two, single_check_text_three, single_check_text_four, single_check_text_five,
                    single_check_image_one, single_check_image_two, single_check_image_three, single_check_image_four, single_check_image_five,
                    single_check_video_one, single_check_video_two, single_check_video_three, single_check_video_four, single_check_video_five,
                    single_check_pdf_one, single_check_pdf_two, single_check_pdf_three, single_check_pdf_four, single_check_pdf_five;
            public ImageView profile_image, show_image, show_pdf, rep_message_image, rep_message_pdf, pdf_download;
            VideoView full_video;
            AppCompatImageView gif_image;
            EmoticonTextView text_show_password, show_message;
            EditText password, password_null, password_reply_text;
            public TextView textTimeDate, pdfName, full_screen, iconHidden;
            LinearLayout linearText, delete_text_linear, linearName;
            RelativeLayout linearimage, linearVideo, linearpdf, fullLinesrvideo, delete_one_linear, rep_text, rep_image, rep_video, rep_pdf, relative_pdf;
            private ImageView show_video, rep_message_video;
            Button password_ok, password_reply_ok;
            ProgressBar splashProgress;
            TextView file_title;
            TextView file_size;
            ProgressBar file_progress;
            Button sharefile;
            TextView file_status, pause_resume;
            RelativeLayout main_rel;

            TextView file_title_document;
            TextView file_size_document;
            ProgressBar file_progress_document;
            Button pause_resume_document, sharefile_document;
            TextView file_status_document;
            RelativeLayout main_rel_document;

            TextView download_video_size;


            public DownloadViewHolder(@NonNull View itemView) {
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
                //           txt_seen = itemView.findViewById(R.id.txt_seen);
                gif_image = itemView.findViewById(R.id.show_gif_image);
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

                relative_pdf = itemView.findViewById(R.id.pdf);


                file_title = itemView.findViewById(R.id.file_title);
                file_size = itemView.findViewById(R.id.file_size);
                file_status = itemView.findViewById(R.id.file_status);
                file_progress = itemView.findViewById(R.id.file_progress);
                pause_resume = itemView.findViewById(R.id.pause_resume);
                main_rel = itemView.findViewById(R.id.main_media_relative);
                sharefile = itemView.findViewById(R.id.sharefile);


                file_title_document = itemView.findViewById(R.id.file_title_document);
                file_size_document = itemView.findViewById(R.id.file_size_document);
                file_status_document = itemView.findViewById(R.id.file_status_document);
                file_progress_document = itemView.findViewById(R.id.file_progress_document);
                pause_resume_document = itemView.findViewById(R.id.pause_resume_document);
                main_rel_document = itemView.findViewById(R.id.main_document_relative);
                sharefile_document = itemView.findViewById(R.id.sharefile_document);

                download_video_size = itemView.findViewById(R.id.download_video_size);

                pdf_download = itemView.findViewById(R.id.pdf_download);

                date = itemView.findViewById(R.id.date);


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

        public void changeItem(long downloadid) {
            int i = 0;
            for (DownloadModel downloadModel : mChat) {
                if (downloadid == downloadModel.getDownloadId()) {
                    notifyItemChanged(i);
                }
                i++;
            }
        }

        public boolean ChangeItemWithStatus(final String message, long downloadid) {
            boolean comp = false;
            int i = 0;
            for (final DownloadModel downloadModel : mChat) {
                if (downloadid == downloadModel.getDownloadId()) {
                    Realm realm = Realm.getDefaultInstance();
                    final int finalI = i;
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            mChat.get(finalI).setStatus(message);
                            notifyItemChanged(finalI);
                            realm.copyToRealmOrUpdate(mChat.get(finalI));
                        }
                    });
                    comp = true;
                }
                i++;
            }
            return comp;
        }

        public void setChangeItemFilePath(final String path, long id) {
            Realm realm = Realm.getDefaultInstance();
            int i = 0;
            for (DownloadModel downloadModel : mChat) {
                if (id == downloadModel.getDownloadId()) {
                    final int finalI = i;
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            mChat.get(finalI).setMessage(path);
                            mChat.get(finalI).setIsseen("true");
                            notifyItemChanged(finalI);
                            realm.copyToRealmOrUpdate(mChat.get(finalI));
                        }
                    });
                }
                i++;
            }
        }

        public void update(final String path, long id) {
            Realm realm = Realm.getDefaultInstance();
            int i = 0;
            for (DownloadModel downloadModel : mChat) {
                if (id == downloadModel.getDownloadId()) {
                    final int finalI = i;
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            mChat.get(finalI).setIsseen("false");
                            notifyItemChanged(finalI);
                            realm.copyToRealmOrUpdate(mChat.get(finalI));
                        }
                    });
                }
                i++;
            }
        }

        public void changeItemWithSeen(final String seen, long downloadid) {
            Realm realm = Realm.getDefaultInstance();
            int i = 0;
            for (DownloadModel downloadModel : mChat) {
                if (downloadid == downloadModel.getDownloadId()) {
                    final int finalI = i;
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            mChat.get(finalI).setIsseen("false");
                            notifyItemChanged(finalI);
                            realm.copyToRealmOrUpdate(mChat.get(finalI));
                        }
                    });
                }
                i++;
            }
        }

    }

    public void onPageError(int page, Throwable t) {
        Log.e(TAG, "Cannot load page " + page);
    }

    private void handlePdfFile(Intent intent) {
        Toast.makeText(this, "Not exist", Toast.LENGTH_SHORT).show();
        Uri pdffile=intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if(pdffile!=null) {
            Toast.makeText(this, "exist", Toast.LENGTH_SHORT).show();
            Log.d("Pdf File Path : ", "" + pdffile.getPath());
            //     downloadFile(pdffile);
        }
    }

    private void handleImage(Intent intent) {
        Uri image=intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if(image!=null) {
            Log.d("Image File Path : ", "" + image.getPath());
            //    downloadFile(intent);
        }
    }

    private void handleTextData(Intent intent) {
        String  textdata=intent.getStringExtra(Intent.EXTRA_TEXT);
        Toast.makeText(this, "not exist", Toast.LENGTH_SHORT).show();
        if(textdata!=null) {
            Toast.makeText(this, "exists", Toast.LENGTH_SHORT).show();
            Log.d("Text Data : ", "" + textdata);
        }
    }

    private void handleMultipleImage(Intent intent) {
        ArrayList<Uri> imageList=intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if(imageList!=null) {
            for (Uri uri : imageList) {
                Log.d("Path ",""+uri.getPath());
            }
        }
    }

    private void addPdfs(String message, String sender, String receiver, String messageId, String bio, String type, String time, String date, String lastSendMessage, String preMessage, String isseen, String imageurl,Uri camuri){
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
        downloadModel.setType("PDF");
        downloadModel.setImagrUrl(imageurl);
        downloadModel.setAdminId("na value");
        downloadModel.setUsername("no value");



        downloadModels.add(downloadModel);

        downloadAdapter.notifyDataSetChanged();
        adapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(downloadModels.size()-1);

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
        RootRef =FirebaseDatabase.getInstance().getReference();

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
                            messageTextBody.put("name", camuri.getLastPathSegment());
                            messageTextBody.put("type", cheker);
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


                            Map messageBodyDetails = new HashMap();

                            messageBodyDetails.put(messageSenderRef + "/" + messageId, messageTextBody);
                            messageBodyDetails.put(messageReceiverRef + "/" + messageId, messageTextBody);

                            HashMap<String, Object> map = new HashMap<>();
                            map.put("time", saveCurrentDate1 + "-" + saveCurrentTime1);


                            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {

                                        realm.executeTransaction(new Realm.Transaction() {
                                            @Override
                                            public void execute(Realm realm) {
                                                final DownloadModel downloads = realm.where(DownloadModel.class).equalTo("id",nextId).findFirst();
                                                downloads.setIsseen("false");
                                                downloadAdapter.notifyDataSetChanged();
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

                                        Toast.makeText(MessageActivity.this, "Error", Toast.LENGTH_SHORT).show();
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
        downloadModel.setType(type);
        downloadModel.setImagrUrl(imageurl);
        downloadModel.setAdminId("na value");
        downloadModel.setUsername("no value");


        downloadModels.add(downloadModel);

        downloadAdapter.notifyDataSetChanged();
        adapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(downloadModels.size()-1);

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
        RootRef =FirebaseDatabase.getInstance().getReference();

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
                            messageTextBody.put("name", camuri.getLastPathSegment());
                            messageTextBody.put("type", cheker);
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


                            Map messageBodyDetails = new HashMap();

                            messageBodyDetails.put(messageSenderRef + "/" + messageId, messageTextBody);
                            messageBodyDetails.put(messageReceiverRef + "/" + messageId, messageTextBody);

                            HashMap<String, Object> map = new HashMap<>();
                            map.put("time", saveCurrentDate1 + "-" + saveCurrentTime1);


                            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        realm.executeTransaction(new Realm.Transaction() {
                                            @Override
                                            public void execute(Realm realm) {
                                                final DownloadModel downloads = realm.where(DownloadModel.class).equalTo("id",nextId).findFirst();
                                                downloads.setIsseen("false");
                                                downloadAdapter.notifyDataSetChanged();
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

                                        Toast.makeText(MessageActivity.this, "Error", Toast.LENGTH_SHORT).show();
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

    private void addImages(String message, String sender, String receiver, String messageId, String bio, String type, String time, String date, String lastSendMessage, String preMessage, String isseen, String imageurl){
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
        downloadModel.setType("image");
        downloadModel.setImagrUrl(imageurl);
        downloadModel.setAdminId("na value");
        downloadModel.setUsername("no value");



        downloadModels.add(downloadModel);


        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(downloadModel);


                downloadAdapter.notifyDataSetChanged();
                adapter.notifyDataSetChanged();

                recyclerView.scrollToPosition(downloadModels.size()-1);

            }
        });
    }

    private void addVideo(String message, String sender, String receiver, String messageId, String bio, String type, String time, String date, String lastSendMessage, String preMessage, String isseen, String imageurl,Uri camuri){
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
        downloadModel.setType(type);
        downloadModel.setImagrUrl(imageurl);
        downloadModel.setAdminId("na value");
        downloadModel.setUsername("no value");



        downloadModels.add(downloadModel);
        downloadAdapter.notifyDataSetChanged();
        adapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(downloadModels.size()-1);

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
        RootRef =FirebaseDatabase.getInstance().getReference();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(saveCurrentDate1);

        final String messageSenderRef = "Chats/" + fuser.getUid() + "/" + userid;
        final String messageReceiverRef = "Chats/" + userid + "/" + fuser.getUid();


        final StorageReference filePath = storageReference.child(saveCurrentDate2 + saveCurrentTime2 + "." + "mp4");



        uploadTask = filePath.putFile(camuri);


        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                if (taskSnapshot.getBytesTransferred()>1){
                    long i = taskSnapshot.getTotalByteCount();
                    long j= taskSnapshot.getBytesTransferred();

                    long one = i/100;

                    long tr = j/one;

                    String st = String.valueOf(tr);
                    username.setText(st+"   "+count);

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
                            messageTextBody.put("name", camuri.getLastPathSegment());
                            messageTextBody.put("type", cheker);
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


                            Map messageBodyDetails = new HashMap();

                            messageBodyDetails.put(messageSenderRef + "/" + messageId, messageTextBody);
                            messageBodyDetails.put(messageReceiverRef + "/" + messageId, messageTextBody);

                            HashMap<String, Object> map = new HashMap<>();
                            map.put("time", saveCurrentDate1 + "-" + saveCurrentTime1);


                            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        realm.executeTransaction(new Realm.Transaction() {
                                            @Override
                                            public void execute(Realm realm) {
                                                final DownloadModel downloads = realm.where(DownloadModel.class).equalTo("id",nextId).findFirst();
                                                downloads.setIsseen("false");

                                                downloadAdapter.update("false",nextId);
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

                                        Toast.makeText(MessageActivity.this, "Error", Toast.LENGTH_SHORT).show();
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

    private void addTextOnFirebase(String message, String sender, String receiver, String messageId, String bio, String type, String time, String date, String lastSendMessage, String preMessage, String isseen, String imageurl){
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
        downloadModel.setStatus("Never");
        downloadModel.setTitle("hello");
        downloadModel.setFile_size("0");
        downloadModel.setProgress("0");
        downloadModel.setIs_paused(false);
        downloadModel.setDownloadId(8878);
        downloadModel.setFile_path("");
        downloadModel.setMessage(message);
        downloadModel.setSender(sender);
        downloadModel.setReceiver(receiver);
        downloadModel.setMessageID(messageId);
        downloadModel.setIsseen("No");
        downloadModel.setLastSendMessage(lastSendMessage);
        downloadModel.setBio(bio);
        downloadModel.setDate(date);
        downloadModel.setPreMessage(preMessage);
        downloadModel.setTime(time);
        downloadModel.setType(type);
        downloadModel.setImagrUrl(imageurl);
        downloadModel.setUsername("no value");
        downloadModel.setAdminId("no value");


        downloadModels.add(downloadModel);

        downloadAdapter.notifyDataSetChanged();
        adapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(downloadModels.size()-1);


        DownloadModel id;
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(downloadModel);



            }
        });


    }

    private void addText(String message, String sender, String receiver, String messageId, String bio, String type, String time, String date, String lastSendMessage, String preMessage, String isseen, String imageurl){
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
        downloadModel.setStatus("Never");
        downloadModel.setTitle("hello");
        downloadModel.setFile_size("0");
        downloadModel.setProgress("0");
        downloadModel.setIs_paused(false);
        downloadModel.setDownloadId(8878);
        downloadModel.setFile_path("");
        downloadModel.setMessage(message);
        downloadModel.setSender(sender);
        downloadModel.setReceiver(receiver);
        downloadModel.setMessageID(messageId);
        downloadModel.setIsseen(isseen);
        downloadModel.setLastSendMessage(lastSendMessage);
        downloadModel.setBio(bio);
        downloadModel.setDate(date);
        downloadModel.setPreMessage(preMessage);
        downloadModel.setTime(time);
        downloadModel.setType(type);
        downloadModel.setImagrUrl(imageurl);
        downloadModel.setUsername("no value");
        downloadModel.setAdminId("no value");


        downloadModels.add(downloadModel);

        downloadAdapter.notifyDataSetChanged();
        adapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(downloadModels.size()-1);


        DownloadModel id;
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                 realm.copyToRealm(downloadModel);


            }
        });


    }

    private void addMediaFiles(String message, String sender, String receiver, String messageId, String bio, String type, String time, String date, String lastSendMessage, String preMessage, String isseen, String imageurl,String title){
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
        downloadModel.setStatus("Never");
        downloadModel.setTitle(title);
        downloadModel.setFile_size("0");
        downloadModel.setProgress("0");
        downloadModel.setIs_paused(false);
        downloadModel.setDownloadId(8878);
        downloadModel.setFile_path("");
        downloadModel.setMessage(message);
        downloadModel.setSender(sender);
        downloadModel.setReceiver(receiver);
        downloadModel.setMessageID(messageId);
        downloadModel.setIsseen(isseen);
        downloadModel.setLastSendMessage(lastSendMessage);
        downloadModel.setBio(title);
        downloadModel.setDate(date);
        downloadModel.setPreMessage(preMessage);
        downloadModel.setTime(time);
        downloadModel.setType(type);
        downloadModel.setImagrUrl(imageurl);
        downloadModel.setUsername("no value");
        downloadModel.setAdminId("no value");


        downloadModels.add(downloadModel);

        downloadAdapter.notifyDataSetChanged();
        adapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(downloadModels.size()-1);


        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(downloadModel);


            }
        });
    }

    private void downloadMediaFiles(String message,long id) {
        String filename= URLUtil.guessFileName(message,null,null);
        File path= new File(Environment.getExternalStorageDirectory() + "/Study Chats/videos");

        String downloadPath = path.getAbsolutePath();

        File file=new File(downloadPath,filename);

        Uri uri = Uri.fromFile(file);

        DownloadManager.Request request=null;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            request=new DownloadManager.Request(Uri.parse(message))
                    .setTitle(filename)
                    .setDescription("Downloading")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                    .setDestinationUri(Uri.fromFile(file))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true);
        }
        else{
            request=new DownloadManager.Request(Uri.parse(message))
                    .setTitle(filename)
                    .setDescription("Downloading")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                    .setDestinationUri(Uri.fromFile(file))
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true);
        }

        DownloadManager downloadManager= (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        long downloadId=downloadManager.enqueue(request);

        Toast.makeText(this, downloadPath+"suriRathore", Toast.LENGTH_SHORT).show();




        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
               downloads = realm.where(DownloadModel.class).equalTo("id",id).findFirst();
                downloads.setStatus("Downloading");
                downloads.setTitle(filename);
                downloads.setFile_size("0");
                downloads.setProgress("0");
                downloads.setIs_paused(false);
                downloads.setDownloadId(downloadId);
                downloads.setFile_path("");

                downloadAdapter.notifyDataSetChanged();
            }
        });




        DownloadStatusTask downloadStatusTask=new DownloadStatusTask(downloads);
        runTask(downloadStatusTask,""+downloadId);


    }

    private void downloadImage(String message, String sender, String receiver, String messageId, String bio, String type, String time, String date, String lastSendMessage, String preMessage, String isseen, String imageurl) {
        String filename= URLUtil.guessFileName(message,null,null);
        File path= new File(Environment.getExternalStorageDirectory() + "/Study Chats/videos");

        String downloadPath = path.getAbsolutePath();



        Toast.makeText(MessageActivity.this, "kya hua", Toast.LENGTH_SHORT).show();
        File file=new File(downloadPath,filename);

        Uri uri = Uri.fromFile(file);

        DownloadManager.Request request=null;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            request=new DownloadManager.Request(Uri.parse(message))
                    .setTitle(filename)
                    .setDescription("Downloading")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                    .setDestinationUri(Uri.fromFile(file))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true);
        }
        else{
            request=new DownloadManager.Request(Uri.parse(message))
                    .setTitle(filename)
                    .setDescription("Downloading")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                    .setDestinationUri(Uri.fromFile(file))
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true);
        }

        DownloadManager downloadManager= (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        long downloadId=downloadManager.enqueue(request);

        Toast.makeText(this, downloadPath, Toast.LENGTH_SHORT).show();

        Number currentnum=realm.where(DownloadModel.class).max("id");
        int nextId;

        if(currentnum==null){
            nextId=1;
        }
        else{
            nextId=currentnum.intValue()+1;
        }


        Toast.makeText(this, uri.toString(), Toast.LENGTH_SHORT).show();

        final DownloadModel downloadModel=new DownloadModel();
        downloadModel.setId(nextId);
        downloadModel.setStatus("Downloading");
        downloadModel.setTitle(filename);
        downloadModel.setFile_size("0");
        downloadModel.setProgress("0");
        downloadModel.setIs_paused(false);
        downloadModel.setDownloadId(downloadId);
        downloadModel.setFile_path("");
        downloadModel.setMessage(downloadPath);
        downloadModel.setSender(sender);
        downloadModel.setReceiver(receiver);
        downloadModel.setMessageID(downloadPath);
        downloadModel.setIsseen("false");
        downloadModel.setLastSendMessage(lastSendMessage);
        downloadModel.setBio(bio);
        downloadModel.setDate(date);
        downloadModel.setPreMessage(preMessage);
        downloadModel.setTime(time);
        downloadModel.setType(type);
        downloadModel.setImagrUrl(imageurl);
        downloadModel.setUsername("no value");
        downloadModel.setAdminId("no value");


        downloadModels.add(downloadModel);

        downloadAdapter.notifyDataSetChanged();
        adapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(downloadModels.size()-1);


        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(downloadModel);
            }
        });


        DownloadStatusTask downloadStatusTask=new DownloadStatusTask(downloadModel);
        runTask(downloadStatusTask,""+downloadId);


    }

    @Override
    public void onCLickItem(String file_path) {
        Log.d("File Path : ",""+file_path);
        openFile(file_path);
    }

    @Override
    public void onShareClick(DownloadModel downloadModel) {
        File file=new File(downloadModel.getFile_path().replaceAll("file:///",""));
        Log.d("File Path",""+file.getAbsolutePath());
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        String ext=MimeTypeMap.getFileExtensionFromUrl(file.getName());
        String type=mimeTypeMap.getExtensionFromMimeType(ext);

        if(type==null){
            type="*/*";
        }

        try{
            Intent intent=new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT,"Sharing File from File Downloader");

            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri path=FileProvider.getUriForFile(MessageActivity.this,"com.example.sqlsearch",file);
                intent.putExtra(Intent.EXTRA_STREAM,path);
            }
            else{
                intent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(file));
            }
            intent.setType("*/*");
            startActivity(intent);
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "No Activity Availabe to Handle File", Toast.LENGTH_SHORT).show();
        }

    }

    public class DownloadStatusTask extends AsyncTask<String,String,String>{

        DownloadModel downloadModel;
        public DownloadStatusTask(DownloadModel downloadModel){
            this.downloadModel=downloadModel;
        }

        @Override
        protected String doInBackground(String... strings) {
            downloadFileProcess(strings[0]);
            return null;
        }

        private void downloadFileProcess(String downloadId) {
            DownloadManager downloadManager= (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            boolean downloading=true;
            while (downloading){
                DownloadManager.Query query=new DownloadManager.Query();
                query.setFilterById(Long.parseLong(downloadId));
                Cursor cursor=downloadManager.query(query);
                cursor.moveToFirst();

                int bytes_downloaded=cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                int total_size=cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                if(cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))==DownloadManager.STATUS_SUCCESSFUL){
                    downloading=false;
                }


                int progress= (int) ((bytes_downloaded*100L)/total_size);
                String status=getStatusMessage(cursor);
                publishProgress(new String[]{String.valueOf(progress), String.valueOf(bytes_downloaded),status});
                cursor.close();
            }

        }

        @Override
        protected void onProgressUpdate(final String... values) {
            super.onProgressUpdate(values);
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    downloadModel.setFile_size(bytesIntoHumanReadable(Long.parseLong(values[1])));
                    downloadModel.setProgress(values[0]);
                    if (!downloadModel.getStatus().equalsIgnoreCase("PAUSE") && !downloadModel.getStatus().equalsIgnoreCase("RESUME")) {
                        downloadModel.setStatus(values[2]);
                    }
                    downloadAdapter.changeItem(downloadModel.getDownloadId());

                }
            });

        }
    }

    private String getStatusMessage(Cursor cursor) {
        String msg="-";
        switch (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))){
            case DownloadManager.STATUS_FAILED:
                msg="Failed";
                break;
            case DownloadManager.STATUS_PAUSED:
                msg= "Paused";
                break;
            case DownloadManager.STATUS_RUNNING:
                msg= "Running";
                break;
            case DownloadManager.STATUS_SUCCESSFUL:
                msg= "Completed";
                break;
            case DownloadManager.STATUS_PENDING:
                msg= "Pending";
                break;
            default:
                msg="Unknown";
                break;
        }
        return msg;
    }

    BroadcastReceiver onComplete=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id=intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1);
            boolean comp=downloadAdapter.ChangeItemWithStatus("Completed",id);

            if(comp){
                DownloadManager.Query query=new DownloadManager.Query();
                query.setFilterById(id);
                DownloadManager downloadManager= (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                Cursor cursor=downloadManager.query(new DownloadManager.Query().setFilterById(id));
                cursor.moveToFirst();

                String downloaded_path=cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                downloadAdapter.setChangeItemFilePath(downloaded_path,id);
                Toast.makeText(MessageActivity.this, downloaded_path, Toast.LENGTH_LONG).show();
            }
        }
    };

    public void runTask(DownloadStatusTask downloadStatusTask,String id){
        try{
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
                downloadStatusTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,new String[]{id});
            }
            else{
                downloadStatusTask.execute(new String[]{id});
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private String bytesIntoHumanReadable(long bytes) {
        long kilobyte = 1024;
        long megabyte = kilobyte * 1024;
        long gigabyte = megabyte * 1024;
        long terabyte = gigabyte * 1024;

        if ((bytes >= 0) && (bytes < kilobyte)) {
            return bytes + " B";

        } else if ((bytes >= kilobyte) && (bytes < megabyte)) {
            return (bytes / kilobyte) + " KB";

        } else if ((bytes >= megabyte) && (bytes < gigabyte)) {
            return (bytes / megabyte) + " MB";

        } else if ((bytes >= gigabyte) && (bytes < terabyte)) {
            return (bytes / gigabyte) + " GB";

        } else if (bytes >= terabyte) {
            return (bytes / terabyte) + " TB";

        } else {
            return bytes + " Bytes";
        }
    }

    private RealmResults<DownloadModel> getAllDownloads(){
        Realm realm=Realm.getDefaultInstance();
        return realm.where(DownloadModel.class).equalTo("receiver", userid).and().equalTo("sender",fuser.getUid()).or().equalTo("receiver", fuser.getUid()).and().equalTo("sender",userid).findAll();
    }

    private void deleteRealmItems(long id){
        Realm realm=Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<DownloadModel> item = realm.where(DownloadModel.class).equalTo("id", id).findAll();
                item.deleteAllFromRealm();

            }
        });
    }

    private void requestPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(MessageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            Toast.makeText(MessageActivity.this, "Please Give Permission to Upload File", Toast.LENGTH_SHORT).show();
        }
        else{
            ActivityCompat.requestPermissions(MessageActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
        }
    }

    private boolean checkPermission(){
        int result= ContextCompat.checkSelfPermission(MessageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(result== PackageManager.PERMISSION_GRANTED){
            return true;
        }
        else{
            requestPermission();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_REQUEST_CODE:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(MessageActivity.this, "Permission Successfull", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(MessageActivity.this, "Permission Failed", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void openFile(String fileurl){
        if(Build.VERSION.SDK_INT>=23){
            if(!checkPermission()){
                requestPermission();
                Toast.makeText(this, "Please Allow Permission to Open File", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        try{
            fileurl=PathUtil.getPath(MessageActivity.this,Uri.parse(fileurl));

            File file=new File(fileurl);
            MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
            String ext=MimeTypeMap.getFileExtensionFromUrl(file.getName());
            String type=mimeTypeMap.getMimeTypeFromExtension(ext);

            if(type==null){
                type="*/*";
            }

            Intent intent=new Intent(Intent.ACTION_VIEW);
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contne= FileProvider.getUriForFile(MessageActivity.this,"com.some.notes",file);
                intent.setDataAndType(contne,type);
            }
            else{
                intent.setDataAndType(Uri.fromFile(file),type);
            }
            startActivity(intent);
        }
        catch (Exception e){
            Toast.makeText(this, "Unable to Open File", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.ViewHolder> {
        private List<DownloadModel> mChat;
        private Context mContext;
        private RecyclerView mRecyclerV;
        private FirebaseAuth mAuth;
        public static final int MSG_TYPE_LEFT = 0;
        public static final int MSG_TYPE_RIGHT = 1;

        FirebaseUser fuser;



   public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView show_text;

            public ViewHolder(View itemView) {
                super(itemView);
                show_text = itemView.findViewById(R.id.show_text);

            }
        }

        public void add(int position, DownloadModel person) {
            mChat.add(position, person);
            notifyItemInserted(position);
        }

        public void remove(int position) {
            mChat.remove(position);
            notifyItemRemoved(position);
        }


        // Provide a suitable constructor (depends on the kind of dataset)
        public PersonAdapter(List<DownloadModel> myDataset, Context context, RecyclerView recyclerView) {
            mChat = myDataset;
            mContext = context;
            mRecyclerV = recyclerView;
        }

        public PersonAdapter( Context context,List<DownloadModel> myDataset) {
            mChat = myDataset;
            mContext = context;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public PersonAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == MSG_TYPE_RIGHT) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.upload_single_user_message, parent, false);
                return new ViewHolder(view);
            } else {
                View view = LayoutInflater.from(mContext).inflate(R.layout.upload_single_user_message, parent, false);
                return new ViewHolder(view);
            }
        }

        // Replace the contents of a view (invoked by the layout manager)
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {

            mAuth = FirebaseAuth.getInstance();
            final String messageSenderId = mAuth.getCurrentUser().getUid();
            final DownloadModel chat = mChat.get(position);

            holder.show_text.setText(chat.getIsseen());

                if (chat.getIsseen().equals("never")) {
                    Toast.makeText(mContext, "hello", Toast.LENGTH_SHORT).show();
                    long id= chat.getId();
                    final DownloadModel dataModel=realm.where(DownloadModel.class).equalTo("id",id).findFirst();
                  //  ShowUpdateDialog(dataModel);
                    holder.show_text.setVisibility(View.VISIBLE);
                    holder.show_text.setText(chat.getMessage());
                    uploadDataOnFirebase(chat.getType(),chat.getMessage(),chat.getMessageID(),dataModel,chat.getId());

                } else {
                    holder.show_text.setVisibility(View.GONE);
                }

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

    private  void uploadDataOnFirebase(String cheker,String uri,String chatId,DownloadModel id,long idrealm){
        Uri uploadUri = Uri.parse(uri);


        Calendar calForDate1 = Calendar.getInstance();
        SimpleDateFormat currendateFormat1 = new SimpleDateFormat("yyyy-MM");
        saveCurrentDate1 = currendateFormat1.format(calForDate1.getTime());

        if (!cheker.equals("image")) {

            notify = true;

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


        //    addItems(uploadUri.toString(),fuser.getUid(),userid,saveCurrentDate2 +saveCurrentTime2,"kt5rg2/r78I081y/kXkhw==\n",cheker,saveCurrentTime,saveCurrentDate,"hello","kya","false",uploadUri.toString(),uploadUri);


            RootRef = FirebaseDatabase.getInstance().getReference();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(saveCurrentDate1);

            final String messageSenderRef = "Chats/" + fuser.getUid() + "/" + userid;
            final String messageReceiverRef = "Chats/" + userid + "/" + fuser.getUid();

            DatabaseReference userMessageKeyRef = RootRef.child("Chats")
                    .child(fuser.getUid()).child(userid).push();


            final StorageReference filePath = storageReference.child(chatId + "." + "jpg");
            uploadTask = filePath.putFile(uploadUri);
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

                        Toast.makeText(MessageActivity.this, "upload successfully", Toast.LENGTH_SHORT).show();
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
                        messageTextBody.put("name", "image");
                        messageTextBody.put("type", cheker);
                        messageTextBody.put("sender", fuser.getUid());
                        messageTextBody.put("receiver", userid);
                        messageTextBody.put("messageID", chatId);
                        messageTextBody.put("time", saveCurrentTime);
                        messageTextBody.put("date", saveCurrentDate);
                        messageTextBody.put("isseen", "false");
                        messageTextBody.put("title", "sure");
                        messageTextBody.put("bio", "sure");
                        messageTextBody.put(sqlId, true);
                        messageTextBody.put("preMessage", "hello");
                        messageTextBody.put("last", "false");
                        messageTextBody.put("lastSendMessage", "ms0enhBo1KPFs/nSUHkvpg==\n");


                        Map messageBodyDetails = new HashMap();
                        messageBodyDetails.put(messageSenderRef + "/" + chatId, messageTextBody);
                        messageBodyDetails.put(messageReceiverRef + "/" + chatId, messageTextBody);


                        HashMap<String, Object> map = new HashMap<>();
                        map.put("time", saveCurrentDate1 + "-" + saveCurrentTime1);


                        RootRef.updateChildren(messageBodyDetails);

                        downloadAdapter.notifyDataSetChanged();
                        adapter.notifyDataSetChanged();


                        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);


                                if (notify) {

                                    sendNotifiaction(userid, user.getUsername(), cheker);
                                }

                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        id.setStatus("Completed");
                                        id.setTitle("hello");
                                        id.setFile_size("0");
                                        id.setProgress("0");
                                        id.setIs_paused(false);
                                        id.setDownloadId(8878);
                                        id.setFile_path("");
                                        id.setMessage(uri);
                                        id.setSender(fuser.getUid());
                                        id.setReceiver(userid);
                                        id.setMessageID(messageid);
                                        id.setIsseen("false");
                                        id.setLastSendMessage("ms0enhBo1KPFs/nSUHkvpg==\n");
                                        id.setBio("sure");
                                        id.setDate(saveCurrentDate);
                                        id.setPreMessage("hello");
                                        id.setTime(saveCurrentTime);
                                        id.setType(cheker);
                                        id.setImagrUrl(uri);
                                        id.setAdminId("no value");
                                        id.setUsername("na value");

                                        realm.copyToRealmOrUpdate(id);
                                        downloadAdapter.notifyDataSetChanged();
                                        adapter.notifyDataSetChanged();




//                        if(currentItems+scrollOutItems == totalItems){
//                            populaterecyclerView(filter);
//                            adapter.notifyDataSetChanged();
//                        }
//                        else {
//
//                        }

                                    }
                                });
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

                    Toast.makeText(MessageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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


            final StorageReference filePath = storageReference.child(chatId + "." + "jpg");

            uploadTask = filePath.putFile(uploadUri);
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
                        messageTextBody.put("name", "pdf");
                        messageTextBody.put("type", cheker);
                        messageTextBody.put("sender", fuser.getUid());
                        messageTextBody.put("receiver", userid);
                        messageTextBody.put("messageID", chatId);
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

                        messageBodyDetails.put(messageSenderRef + "/" + chatId, messageTextBody);
                        messageBodyDetails.put(messageReceiverRef + "/" + chatId, messageTextBody);


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
                                       //     downloadAdapter.ChangeItemWithSeen("false",idrealm);
                                            realm.executeTransaction(new Realm.Transaction() {
                                                @Override
                                                public void execute(Realm realm) {
                                                    id.setStatus("Completed");
                                                    id.setTitle("hello");
                                                    id.setFile_size("0");
                                                    id.setProgress("0");
                                                    id.setIs_paused(false);
                                                    id.setDownloadId(8878);
                                                    id.setFile_path("");
                                                    id.setMessage(uri);
                                                    id.setSender(fuser.getUid());
                                                    id.setReceiver(userid);
                                                    id.setMessageID(messageid);
                                                    id.setIsseen("false");
                                                    id.setLastSendMessage("ms0enhBo1KPFs/nSUHkvpg==\n");
                                                    id.setBio("sure");
                                                    id.setDate(saveCurrentDate);
                                                    id.setPreMessage("hello");
                                                    id.setTime(saveCurrentTime);
                                                    id.setType(cheker);
                                                    id.setImagrUrl(uri);
                                                    id.setUsername("no Value");
                                                    id.setAdminId("na value");

                                                    realm.copyToRealmOrUpdate(id);
                                                    downloadAdapter.changeItemWithSeen("false",id.getId());


                                                    downloadAdapter.notifyDataSetChanged();
                                                    adapter.notifyDataSetChanged();


                                               //     updateDataInRecyclerView();
                                                }
                                            });

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

                                    Toast.makeText(MessageActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                }


                            }
                        });

                    }
                }
            });
        }


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
        downloadModel.setType(type);
        downloadModel.setImagrUrl(imageurl);
        downloadModel.setAdminId("na value");
        downloadModel.setUsername("no value");

        downloadModels.add(downloadModel);

        downloadAdapter.notifyDataSetChanged();
        adapter.notifyDataSetChanged();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(downloadModel);

                recyclerView.smoothScrollToPosition(downloadModels.size()-1);

            }
        });

    }

    private void updateDataInRecyclerView(){

//
//        List<DownloadModel> downloadModelsLocal=getAllDownloads();
//        if(downloadModelsLocal!=null){
//            if(downloadModelsLocal.size()>0){
//                downloadModels.addAll(downloadModelsLocal);
//                for(int i=0;i<downloadModels.size();i++){
//                    if(downloadModels.get(i).getStatus().equalsIgnoreCase("Pending") || downloadModels.get(i).getStatus().equalsIgnoreCase("Running") || downloadModels.get(i).getStatus().equalsIgnoreCase("Downloading")){
//                        DownloadStatusTask downloadStatusTask=new DownloadStatusTask(downloadModels.get(i));
//                        runTask(downloadStatusTask,""+downloadModels.get(i).getDownloadId());
//                    }
//                }
//            }
//        }
//        downloadAdapter=new DownloadAdapter(MessageActivity.this,downloadModels,MessageActivity.this);
//        chat_linear_layout = new LinearLayoutManager(this);
//        chat_linear_layout.setStackFromEnd(true);
//        recyclerView.setLayoutManager(chat_linear_layout);
//        recyclerView.setAdapter(downloadAdapter);
//
//        adapter= new PersonAdapter(MessageActivity.this,downloadModels);
//        upload_linear_layout = new LinearLayoutManager(this);
//        upload_linear_layout.setStackFromEnd(true);
//        upload_recycler_view.setLayoutManager(upload_linear_layout);
//        upload_recycler_view.setAdapter(adapter);
//
//



    }

    void generateImageFromPdf(Uri pdfUri,long idItem) {
        int pageNumber = 0;
        PdfiumCore pdfiumCore = new PdfiumCore(this);

        ParcelFileDescriptor fd = null;
        try {
            fd = getContentResolver().openFileDescriptor(pdfUri, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        PdfDocument pdfDocument = null;
        try {
            pdfDocument = pdfiumCore.newDocument(fd);
        } catch (IOException e) {
            e.printStackTrace();
        }
        pdfiumCore.openPage(pdfDocument, pageNumber);
        int width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNumber);
        int height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNumber);
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        pdfiumCore.renderPageBitmap(pdfDocument, bmp, pageNumber, 0, 0, width, height);
        saveImage(bmp,idItem);
        pdfiumCore.closeDocument(pdfDocument);


    }

    public final static String FOLDER = Environment.getExternalStorageDirectory() + "/Study Chats/images";

    private void saveImage(Bitmap bmp,long personId) {
        FileOutputStream out = null;
        try {
            File folder = new File(FOLDER);
            if(!folder.exists())
                folder.mkdirs();
            File file = new File(folder, generateString(12));
            out = new FileOutputStream(file);

            String filePath = file.getAbsolutePath();

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Toast.makeText(MessageActivity.this, "id", Toast.LENGTH_SHORT).show();
                    final DownloadModel downloads = realm.where(DownloadModel.class).equalTo("id",personId).findFirst();
                    downloads.setImagrUrl(filePath.toString());
                    downloadAdapter.notifyDataSetChanged();
                }
            });

            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
        } catch (Exception e) {
            //todo with exception
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (Exception e) {
                //todo with exception
            }
        }
    }

    private  void checkConnection(){

        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetWork = manager.getActiveNetworkInfo();

        if (null!=activeNetWork){
            if (activeNetWork.getType() == ConnectivityManager.TYPE_WIFI){

                Toast.makeText(this, "wifi Enabled", Toast.LENGTH_SHORT).show();
            }
            else if (activeNetWork.getType() == ConnectivityManager.TYPE_MOBILE){

                Toast.makeText(this, "data Network Enabled", Toast.LENGTH_SHORT).show();
            }

        }
        else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

}
