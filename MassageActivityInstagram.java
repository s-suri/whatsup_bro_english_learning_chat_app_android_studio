package com.some.notes;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
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
import android.speech.tts.TextToSpeech;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
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
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;
import com.some.notes.Adapter.TrimmerUtils;
import com.some.notes.Audio.pickvideo.StaticFinalValues;
import com.some.notes.Documents.GroupDocumentActivity;
import com.some.notes.Documents.MainActivity;
import com.some.notes.Documents.SplashScreen;
import com.some.notes.GroupNotification.APIService;
import com.some.notes.GroupNotification.Client;
import com.some.notes.GroupNotification.Data;
import com.some.notes.GroupNotification.MyResponse;
import com.some.notes.GroupNotification.Sender;
import com.some.notes.GroupNotification.Token;
import com.some.notes.Images.pickvideo.GroupMultipleImagePickActivity;
import com.some.notes.Images.pickvideo.ImagesPickActivity;
import com.some.notes.Images.pickvideo.MultipleImagesPickAtivity;
import com.some.notes.Images.pickvideo.groupImagePickctivity;
import com.some.notes.ImoticonsText.Android8.Android8EmoticonProvider;
import com.some.notes.ImoticonsText.EmoticonGIFKeyboardFragment;
import com.some.notes.ImoticonsText.emoticons.Emoticon;
import com.some.notes.ImoticonsText.emoticons.EmoticonSelectListener;
import com.some.notes.ImoticonsText.gifs.Gif;
import com.some.notes.ImoticonsText.gifs.GifSelectListener;
import com.some.notes.ImoticonsText.gipfy.GiphyGifProvider;
import com.some.notes.ImoticonsText.widget.EmoticonEditText;
import com.some.notes.ImoticonsText.widget.EmoticonTextView;
import com.some.notes.Model.Chat;
import com.some.notes.Model.CityDataItem;
import com.some.notes.Model.Comment;
import com.some.notes.Model.DownloadModel;
import com.some.notes.Model.GroupMessage;
import com.some.notes.Model.ItemClickListener;
import com.some.notes.Model.SampleDataProvider;
import com.some.notes.Model.SqlMessage;
import com.some.notes.Model.SqlUser;
import com.some.notes.Model.User;
import com.some.notes.PDF.pickvideo.GroupPDFPickActivity;
import com.some.notes.PDF.pickvideo.PdfPickActivity;
import com.some.notes.PhotoEditing.EditImageActivity;
import com.some.notes.PhotoEditing.MultiImages;
import com.some.notes.Video.pickvideo.GroupVideoPickActivity;
import com.some.notes.Video.pickvideo.VideoPickActivity;
import com.some.notes.sillicompresser.SiliCompressor;
import com.squareup.picasso.Picasso;

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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.os.Environment.getExternalStoragePublicDirectory;
import static com.some.notes.sillicompresser.videocompression.MediaController.mContext;

public class MassageActivityInstagram extends AppCompatActivity {

    String names[] = {"Hi", "Hello", "Aap kese ho? (How are you)", "Me thik hu. (I am fine)", "tum kese ho. (How are you)", "shubh prabhaat (good morning)",
    };

    private static final String TAG = "MainActivity";
    private EmoticonGIFKeyboardFragment mEmoticonGIFKeyboardFragment;
    List<DownloadModel> downloadModels=new ArrayList<>();
    Realm realm;
    private List<String> fileNameList;
    private List<String> fileDoneList;
    Uri compressUri = null;
    Uri multifileUri;
    private static final int PERMISSION_REQUEST_CODE = 101;
    DownloadAdapter downloadAdapter;
    SqlChatsActivity.PersonChatsUserDBHelper helper;
    public static String sqlId,id;
    private String filter = "";
    Button getText_btn;
    ImageView showImage_img, reply_image;
    String AES = "AES";
    boolean seenMessageseen = false;
    public static String postid, adminId;
    String msg, trans_msg;
    FirebaseAuth mAuth;
    DatabaseReference UsersRef, RootRef;
    private Uri fileUri;
    ProgressBar splashProgress;
    Dialog myDialog;
    CircleImageView profile_image;
    TextView  messageConvert;
    private String cheker = "", myUrl = "", url = "",saveCurrentTime1, saveCurrentDate1;
    FirebaseUser fuser;
    DatabaseReference reference;
    private static final int RESULT_LOAD_IMAGE = 1;
    private StorageTask uploadTask;
    ImageButton btn_send, SendFilesButton, reply_send;
    EditText  reply_edit, reply_password;
    EmoticonEditText text_send;
    EmoticonTextView username;
    AppCompatImageView background_image;
    RecyclerView recyclerView, findFriendLis, group_users_recyclerview;
    Intent intent;
    private String saveCurrentTime, saveCurrentDate;
    RelativeLayout reply_bottom, relative_layout_message;
    String messageId;
    boolean notify = false;
    APIService apiService;
    LinearLayoutManager chat_linear_layout;
    RelativeLayout toolbar;
    TextView inputPassword, fab_status, reply_preview_message, reply_delete, type,smiley_btn,ic_keyboard;
    Window window;
    DownloadModel downloads;
    String str_name;
    String imageReciever, bioReciever, fullnameReciever, imageSender, bioSender, fullnameSender, theLastMessage, outputString;
    private List<CityDataItem> mDataList;
    private RecyclerView mRecyclerView;
    private MyDataAdapter mDataAdapter;
    int SPLASH_TIME = 200;
    int delay = 250;
    int currentItems,totalItems,scrollOutItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_massage_instagram);
        realm=Realm.getDefaultInstance();
     //   checkConnection();

        inilaization();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        fileNameList = new ArrayList<>();
        fileDoneList = new ArrayList<>();

        myDialog = new Dialog(this);
        if (Build.VERSION.SDK_INT >= 21) {
            window = this.getWindow();
            window.setStatusBarColor(this.getResources().getColor(R.color.white));
        }



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
                mEmoticonGIFKeyboardFragment.emoticonClose(MassageActivityInstagram.this);
                UIUtil.showKeyboard(MassageActivityInstagram.this,text_send);
            }
        });

        smiley_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ic_keyboard.setVisibility(View.VISIBLE);
                smiley_btn.setVisibility(View.GONE);
                UIUtil.hideKeyboard(MassageActivityInstagram.this);
                mEmoticonGIFKeyboardFragment.open(MassageActivityInstagram.this);
            }
        });

        text_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ic_keyboard.setVisibility(View.GONE);
                smiley_btn.setVisibility(View.VISIBLE);
                mEmoticonGIFKeyboardFragment.emoticonClose(MassageActivityInstagram.this);
                UIUtil.showKeyboard(MassageActivityInstagram.this,text_send);
            }
        });


        intent = getIntent();
        postid = intent.getStringExtra("userid");
//        adminId = intent.getStringExtra("adminId");
//        sqlId = intent.getStringExtra("name");


        mAuth = FirebaseAuth.getInstance();
        fuser = FirebaseAuth.getInstance().getCurrentUser();


        notify = true;
        seenMessageseen = true;



        apiService = Client.getClient("https://fcm.googleapis.com").create(APIService.class);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                List<DownloadModel> downloadModelsLocal=getAllDownloads();
                if(downloadModelsLocal!=null){
                    if(downloadModelsLocal.size()>0){
                        downloadModels.addAll(downloadModelsLocal);
                        for(int i=0;i<downloadModels.size();i++){
                            if(downloadModels.get(i).getStatus().equalsIgnoreCase("Pending") || downloadModels.get(i).getStatus().equalsIgnoreCase("Running") || downloadModels.get(i).getStatus().equalsIgnoreCase("Downloading")){
                                DownloadStatusTask downloadStatusTask=new DownloadStatusTask(downloadModels.get(i));
                                runTask(downloadStatusTask,""+downloadModels.get(i).getDownloadId());
                            }
                        }
                    }
                }

            }
        }, SPLASH_TIME);

        downloadAdapter=new DownloadAdapter(MassageActivityInstagram.this,downloadModels);
        chat_linear_layout = new LinearLayoutManager(this);
        chat_linear_layout.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(chat_linear_layout);
        recyclerView.setAdapter(downloadAdapter);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

               downloadAdapter.notifyDataSetChanged();
                readMesagges();
            }
        }, delay);

        group_users_recyclerview.setLayoutManager(new LinearLayoutManager(this));


        reply_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relative_layout_message.setVisibility(View.VISIBLE);
                reply_bottom.setVisibility(View.GONE);

            }
        });


        mDataList = SampleDataProvider.cityDataItemList;


        LinearLayoutManager linearLayoutTranslate = new LinearLayoutManager(getApplicationContext());
        linearLayoutTranslate.setStackFromEnd(true);
        findFriendLis.setLayoutManager(linearLayoutTranslate);


        mDataAdapter = new MyDataAdapter(mDataList, this);
        findFriendLis.setAdapter(mDataAdapter);




        getText_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                GetTextFromImageFunction();
            }
        });

        fab_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   recyclerView.smoothScrollToPosition(mchat.size());

                fab_status.setVisibility(View.GONE);
            }
        });


        RootRef = FirebaseDatabase.getInstance().getReference();


        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MassageActivityInstagram.this, GroupInfo.class);
                intent.putExtra("groupName", postid);
                intent.putExtra("adminId", adminId);
              //  intent.putExtra("USER_ID", receivedPersonId);
                intent.putExtra("email",sqlId);
                startActivity(intent);
            }
        });

        helper = new SqlChatsActivity.PersonChatsUserDBHelper(this);

        username.setEmoticonProvider(Android8EmoticonProvider.create());
        username.setEmoticonSize(65);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                final DownloadModel downloads = realm.where(DownloadModel.class).equalTo("message",postid).and().equalTo("preMessage", fuser.getUid()).findFirst();
                username.setText(downloads.getMessage());

                sqlId = downloads.getFile_path();
                adminId = downloads.getAdminId();

                Picasso.get().load(downloads.getImagrUrl()).into(profile_image);
                Picasso.get().load(downloads.getImagrUrl()).into(background_image);

            }
        });

        UsersRef = FirebaseDatabase.getInstance().getReference().child(fuser.getUid());

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
                        Intent intent2 = new Intent(MassageActivityInstagram.this, groupImagePickctivity.class);
                        intent2.putExtra("userid", postid);
                        intent2.putExtra("adminId", adminId);
                        intent2.putExtra("name", sqlId);
                        startActivityForResult(intent2, StaticFinalValues.REQUEST_CODE_PICK_VIDEO);

                    }
                });

                images.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        relativeLayout.setVisibility(View.GONE);
                        cheker = "image";
                        Intent intent2 = new Intent(MassageActivityInstagram.this, GroupMultipleImagePickActivity.class);
                        intent2.putExtra("userid", postid);
                        intent2.putExtra("adminId",adminId);
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
                        Intent intent2 = new Intent(MassageActivityInstagram.this, GroupVideoPickActivity.class);
                        intent2.putExtra("userid", postid);
                        intent2.putExtra("adminId",adminId);
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
                        Intent intent2 = new Intent(MassageActivityInstagram.this, GroupPDFPickActivity.class);
                        intent2.putExtra("userid", postid);
                        intent2.putExtra("adminId",adminId);
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
                        Intent intent2 = new Intent(MassageActivityInstagram.this, GroupDocumentActivity.class);
                        intent2.putExtra("userid", postid);
                        intent2.putExtra("adminId",adminId);
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

        text_send.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().trim().length() == 0) {


                    SendFilesButton.setVisibility(View.VISIBLE);
                    btn_send.setVisibility(View.GONE);
                    findFriendLis.setVisibility(View.GONE);
                    findViewById(R.id.btnSpeak).setVisibility(View.VISIBLE);
                    inputPassword.setVisibility(View.GONE);

                } else {
                    mDataAdapter.getFilter().filter(s);
                    btn_send.setVisibility(View.VISIBLE);
                    findViewById(R.id.btnSpeak).setVisibility(View.GONE);
                    findFriendLis.setVisibility(View.VISIBLE);
                    typingStatus(fuser.getUid());
                    btn_send.setEnabled(true);
                    SendFilesButton.setVisibility(View.GONE);

                    inputPassword.setVisibility(View.VISIBLE);
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
                    findFriendLis.setVisibility(View.VISIBLE);
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
                notify = true;

                try {
                    msg = encrypt(text_send.getText().toString(), inputPassword.getText().toString());
                    trans_msg = encrypt(messageConvert.getText().toString(), inputPassword.getText().toString());

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (!msg.equals("")) {

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
                            addText(msg,fuser.getUid(),postid,messageId,trans_msg,"text",saveCurrentTime,saveCurrentDate,"hello","kya","false",msg);

                            addCommentSender(msg, trans_msg);

                        } else if (activeNetWork.getType() == ConnectivityManager.TYPE_MOBILE) {

                            addText(msg,fuser.getUid(),postid,messageId,trans_msg,"text",saveCurrentTime,saveCurrentDate,"hello","kya","false",msg);

                            addCommentSender(msg, trans_msg);

                        }
                    } else {
                        addText(msg,fuser.getUid(),postid,messageId,trans_msg,"text",saveCurrentTime,saveCurrentDate,"hello","kya","never",msg);

                    }

                } else {
                    Toast.makeText(MassageActivityInstagram.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
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
                    reply_edit.setText("");
                    relative_layout_message.setVisibility(View.VISIBLE);
                    reply_bottom.setVisibility(View.GONE);


                    replyCommentSender(msg, trans_msg, ret_type, ret_preview_message);


                } else {
                    Toast.makeText(MassageActivityInstagram.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }

            }
        });

        findFriendLis.setVisibility(View.GONE);

        reference = FirebaseDatabase.getInstance().getReference("Users").child(adminId);


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("imageurl")) {
                        imageReciever = dataSnapshot.child("imageurl").getValue().toString();
                        bioReciever = dataSnapshot.child("bio").getValue().toString();
                        fullnameReciever = dataSnapshot.child("fullname").getValue().toString();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        RerieveUserInfo();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("imageurl")) {
                        imageSender = dataSnapshot.child("imageurl").getValue().toString();
                        bioSender = dataSnapshot.child("bio").getValue().toString();
                        fullnameSender = dataSnapshot.child("fullname").getValue().toString();

                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference dbreference = FirebaseDatabase.getInstance().getReference("Users");
        dbreference.child(fuser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    str_name = dataSnapshot.child("fullname").getValue().toString();
                } else
                    Toast.makeText(MassageActivityInstagram.this, "", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void inilaization(){
        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        btn_send.setEnabled(false);
        inputPassword = findViewById(R.id.password);
        text_send = findViewById(R.id.text_send);
        text_send.setEmoticonProvider(Android8EmoticonProvider.create());
        text_send.setEmoticonSize(50);
        btn_send.setEnabled(true);
        smiley_btn = findViewById(R.id.smiley_btn);
        ic_keyboard = findViewById(R.id.keyboard_btn);
        reply_bottom = findViewById(R.id.reply_bottom);
        reply_bottom.setVisibility(View.GONE);
        reply_edit = findViewById(R.id.reply_edit);
        reply_image = findViewById(R.id.reply_image);
        reply_send = findViewById(R.id.reply_Send);
        type = findViewById(R.id.type);
        background_image = findViewById(R.id.background_image);
        reply_preview_message = findViewById(R.id.preview_message);
        relative_layout_message = findViewById(R.id.relative_layout_message);
        reply_delete = findViewById(R.id.reply_delete);
        reply_password = findViewById(R.id.reply_password);
        splashProgress = findViewById(R.id.splashProgress);
        recyclerView = findViewById(R.id.recycler_view_group);
        group_users_recyclerview = (RecyclerView) findViewById(R.id.group_users_recyclerview);
        SendFilesButton = (ImageButton) findViewById(R.id.send_files_btn);
        toolbar = findViewById(R.id.bar_layout);
        getText_btn = findViewById(R.id.btn_gettext);
        showImage_img = findViewById(R.id.img_imageview);
        messageConvert = findViewById(R.id.message_convert);
        fab_status = findViewById(R.id.fab_status);
        findFriendLis = findViewById(R.id.search_translate);


    }

    private RealmResults<DownloadModel> getAllDownloads(){
        Realm realm=Realm.getDefaultInstance();
        return realm.where(DownloadModel.class).equalTo("receiver", postid).findAll();
    }

    private void seenMessage(){
        DatabaseReference Root = FirebaseDatabase.getInstance().getReference("GroupChats").child(adminId).child(postid);
        Root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {


                        if (snapshot.child("sk" + fuser.getUid()).exists()) {
                        } else {


                            String Id = snapshot.child("messageID").getValue().toString();
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("sk" + fuser.getUid(), true);
                            Root.child(Id).updateChildren(hashMap);



                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    private  void deleteSingleUserMessage(){

        DatabaseReference rf = FirebaseDatabase.getInstance().getReference("DeleteGroupUserMessage").child(adminId).child(postid);

        rf.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot :dataSnapshot.getChildren()){
                    if (snapshot.child("messageID").exists()) {

                        String messageID = snapshot.child("messageID").getValue().toString();
                        String id = snapshot.child("id").getValue().toString();


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

    private void replyCommentSender(String message, String trans_msg, String rep_type, String preview_message) {


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


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("GroupChats").child(adminId).child(postid);


        String commentid = generateString(12);


        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("time", saveCurrentTime);
        hashMap.put("date", saveCurrentDate);
        hashMap.put("username", str_name);
        hashMap.put("messageID", saveCurrentDate1+saveCurrentTime1);
        hashMap.put("sender", fuser.getUid());
        hashMap.put("receiver", postid);
        hashMap.put("isseen", "false");
        hashMap.put(sqlId, true);
        hashMap.put("message", message);
        hashMap.put("bio", trans_msg);
        hashMap.put("type", rep_type);
        hashMap.put("lastSendMessage", message);
        hashMap.put("preMessage", preview_message);


        reference.child(saveCurrentDate1+saveCurrentTime1).setValue(hashMap);


        findFriendLis.setVisibility(View.GONE);


    }

    private void readMesagges() {

        DatabaseReference  databaseRef = FirebaseDatabase.getInstance().getReference("GroupChats").child(adminId).child(postid);
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child(sqlId).exists())
                    {
                    }else if (snapshot.child("delete"+fuser.getUid()).exists())
                    {
                    }
                    else {
                        String userName = snapshot.child("name").getValue().toString();
                        String pdfTitle = snapshot.child("title").getValue().toString();
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

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put(sqlId, true);
                        databaseRef.child(messageId).updateChildren(hashMap);

                        if ((type.equals("text")) || (type.equals("rep_type"))){
                            addText(message,sender,receiver,messageId,bio,type,time,date,lastSendMessage,preMessage,isseen,message);

                        }

                        else if ((type.equals("video")) || (type.equals("PDF")) || (type.equals("docx")))
                        {
                            addMediaFiles(message,sender,receiver,messageId,bio,type,time,date,lastSendMessage,preMessage,"false",message);

                            Intent intent = new Intent(MassageActivityInstagram.this, MessageActivity.class);
                            intent.putExtra("userid", postid);
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


                      }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        state("online");

    }

    private void state(String online) {

        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid()).child("userState");


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


        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("state", online);
        hashMap.put("date", saveCurrentDate);
        hashMap.put("time", saveCurrentTime);

        reference.updateChildren(hashMap);
    }

    private void typingStatus(String typing) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("typingTo", typing);

        reference.updateChildren(hashMap);
    }

    private void RerieveUserInfo() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(adminId);


        reference
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild(postid + "image") && (dataSnapshot.hasChild(postid + "status")))) {
                            String retriveimage = dataSnapshot.child(postid + "image").getValue().toString();
                            String retrieveStatus = dataSnapshot.child(postid + "status").getValue().toString();


//                            userLastSeen.setVisibility(View.VISIBLE);
//
//                            userLastSeen.setText(retrieveStatus);
//
//
//                            Picasso.get().load(retriveimage).into(profile_image);


                        } else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild(postid + "status"))) {

                            String retrieveStatus = dataSnapshot.child(postid + "status").getValue().toString();

//
//                            userLastSeen.setVisibility(View.VISIBLE);
//                            userLastSeen.setText(retrieveStatus);


                        } else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild(postid + "image"))) {

                            String retriveimage = dataSnapshot.child(postid + "image").getValue().toString();


                            Picasso.get().load(retriveimage).into(profile_image);


                        } else {

//
//                            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/instagramtest-fcbef.appspot.com/o/placeholder.png?alt=media&token=b09b809d-a5f8-499b-9563-5252262e9a49").into(profile_image);
//
                       }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void readUser() {
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

                    multifileUri = data.getClipData().getItemAt(i).getUri();


                    if (cheker.equals("PDF")) {

                        notify = true;

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

                        String messageID = saveCurrentDate2 +saveCurrentTime2;


                        if (null!=activeNetWork){
                            if (activeNetWork.getType() == ConnectivityManager.TYPE_WIFI){

                                addPdfs(multifileUri.toString(),fuser.getUid(),postid,messageID,"kt5rg2/r78I081y/kXkhw==\n","PDF",saveCurrentTime,saveCurrentDate,"hello","kya","panding",multifileUri.toString(),multifileUri);
                            }
                            else if (activeNetWork.getType() == ConnectivityManager.TYPE_MOBILE){

                                addPdfs(multifileUri.toString(),fuser.getUid(),postid,messageID,"kt5rg2/r78I081y/kXkhw==\n","PDF",saveCurrentTime,saveCurrentDate,"hello","kya","panding",multifileUri.toString(),multifileUri);
                            }
                        }
                        else {
                            addItemInRealmNoInternet(multifileUri.toString(),fuser.getUid(),postid,messageID,"kt5rg2/r78I081y/kXkhw==\n","PDF",saveCurrentTime,saveCurrentDate,"hello","kya","never",multifileUri.toString());

                            Toast.makeText(MassageActivityInstagram.this, "you are not connected with Internet Connection", Toast.LENGTH_SHORT).show();
                        }

//                        RootRef = FirebaseDatabase.getInstance().getReference();
//                        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(saveCurrentDate1);
//
//                        final String messageSenderRef = "Chats/" + fuser.getUid() + "/" + userid;
//                        final String messageReceiverRef = "Chats/" + userid + "/" + fuser.getUid();
//
//                        DatabaseReference userMessageKeyRef = RootRef.child("Chats")
//                                .child(fuser.getUid()).child(userid).push();


//                        final StorageReference filePath = storageReference.child(messagePushID + "." + "jpg");
//                        uploadTask = filePath.putFile(fileUri);
//                        uploadTask.continueWithTask(new Continuation() {
//                            @Override
//                            public Object then(@NonNull Task task) throws Exception {
//
//                                if (!task.isSuccessful()) {
//                                    throw task.getException();
//
//
//                                }
//                                return filePath.getDownloadUrl();
//                            }
//                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Uri> task) {
//                                if (task.isSuccessful()) {
//
//                                    Toast.makeText(MessageActivity.this, "upload successfully", Toast.LENGTH_SHORT).show();
//                                    Uri downloadUrl = task.getResult();
//                                    myUrl = downloadUrl.toString();
//
//                                    Calendar calForDate = Calendar.getInstance();
//                                    SimpleDateFormat currendateFormat = new SimpleDateFormat("MMM dd");
//                                    saveCurrentDate = currendateFormat.format(calForDate.getTime());
//
//
//                                    Calendar calForTime = Calendar.getInstance();
//                                    SimpleDateFormat currenTimeFormat = new SimpleDateFormat("hh:mm a");
//                                    saveCurrentTime = currenTimeFormat.format(calForTime.getTime());
//
//
//                                    Calendar calForDate1 = Calendar.getInstance();
//                                    SimpleDateFormat currendateFormat1 = new SimpleDateFormat("yyyyMMdd");
//                                    saveCurrentDate1 = currendateFormat1.format(calForDate1.getTime());
//
//
//                                    Calendar calForTime1 = Calendar.getInstance();
//                                    SimpleDateFormat currenTimeFormat1 = new SimpleDateFormat("a hhmmss");
//                                    saveCurrentTime1 = currenTimeFormat1.format(calForTime1.getTime());
//
//
//                                    HashMap<String, Object> groupMessageKey = new HashMap<>();
//                                    RootRef.updateChildren(groupMessageKey);
//
//
//                                    Map messageTextBody = new HashMap();
//
//                                    SqlMessage sql = new SqlMessage(myUrl, fuser.getUid(), userid, saveCurrentDate1 + saveCurrentTime1, "sure", cheker, saveCurrentTime, saveCurrentDate, "ms0enhBo1KPFs/nSUHkvpg==\n", fileUri.getLastPathSegment(), "false", myUrl);
//
//
//                                    messageTextBody.put("message", myUrl);
//                                    messageTextBody.put("url", myUrl);
//                                    messageTextBody.put("name", fileUri.getLastPathSegment());
//                                    messageTextBody.put("type", cheker);
//                                    messageTextBody.put("sender", fuser.getUid());
//                                    messageTextBody.put("receiver", userid);
//                                    messageTextBody.put("messageID", saveCurrentDate1 + saveCurrentTime1);
//                                    messageTextBody.put("time", saveCurrentTime);
//                                    messageTextBody.put("date", saveCurrentDate);
//                                    messageTextBody.put("isseen", "false");
//                                    messageTextBody.put("title", "sure");
//                                    messageTextBody.put("bio", "sure");
//                                    messageTextBody.put(sqlId, true);
//                                    messageTextBody.put("preMessage", "hello");
//                                    messageTextBody.put("last", "false");
//                                    messageTextBody.put("lastSendMessage", "ms0enhBo1KPFs/nSUHkvpg==\n");
//
//
//                                    Map messageBodyDetails = new HashMap();
//                                    messageBodyDetails.put(messageSenderRef + "/" + saveCurrentDate1 + saveCurrentTime1, messageTextBody);
//                                    messageBodyDetails.put(messageReceiverRef + "/" + saveCurrentDate1 + saveCurrentTime1, messageTextBody);
//
//
//                                    HashMap<String, Object> map = new HashMap<>();
//                                    map.put("time", saveCurrentDate1 + "-" + saveCurrentTime1);
//
//
//                                    RootRef.updateChildren(messageBodyDetails);
//
//
//                                    reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
//                                    reference.addValueEventListener(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                            User user = dataSnapshot.getValue(User.class);
//                                            if (notify) {
//                                                sendNotifiaction(userid, user.getUsername(), cheker);
//                                            }
//                                            notify = false;
//                                        }
//
//                                        @Override
//                                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                        }
//                                    });
//
//                                }
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//
//                                Toast.makeText(MessageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        });

                    }

                    else if (cheker.equals("docx")){

                        notify = true;

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

                        String messageID = saveCurrentDate2 +saveCurrentTime2;

                        if (null!=activeNetWork){
                            if (activeNetWork.getType() == ConnectivityManager.TYPE_WIFI){

                                addPdfs(multifileUri.toString(),fuser.getUid(),postid,messageID,"kt5rg2/r78I081y/kXkhw==\n","PDF",saveCurrentTime,saveCurrentDate,"hello","kya","panding",multifileUri.toString(),multifileUri);
                            }
                            else if (activeNetWork.getType() == ConnectivityManager.TYPE_MOBILE){

                                addPdfs(multifileUri.toString(),fuser.getUid(),postid,messageID,"kt5rg2/r78I081y/kXkhw==\n","PDF",saveCurrentTime,saveCurrentDate,"hello","kya","panding",multifileUri.toString(),multifileUri);
                            }
                        }
                        else {
                            addItemInRealmNoInternet(multifileUri.toString(),fuser.getUid(),postid,messageID,"kt5rg2/r78I081y/kXkhw==\n","PDF",saveCurrentTime,saveCurrentDate,"hello","kya","never",multifileUri.toString());

                            Toast.makeText(MassageActivityInstagram.this, "you are not connected with Internet Connection", Toast.LENGTH_SHORT).show();
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

                        new ImageCompressionAsyncTask(this).execute(multifileUri.toString(),
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
                                (Name + fileNo) + "." + TrimmerUtils.getFileExtension(this, multifileUri));
                        while (newFile.exists()) {
                            fileNo++;
                            newFile = new File(path + File.separator +
                                    (Name + fileNo) + "." + TrimmerUtils.getFileExtension(this, multifileUri));

                        }
                        Toast.makeText(MassageActivityInstagram.this, newFile.toString(), Toast.LENGTH_LONG).show();

                        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo activeNetWork = manager.getActiveNetworkInfo();


                        String messageID = saveCurrentDate2 +saveCurrentTime2;

                        if (null!=activeNetWork){
                            if (activeNetWork.getType() == ConnectivityManager.TYPE_WIFI){

                                addVideo(multifileUri.toString(),fuser.getUid(),postid,messageID,"kt5rg2/r78I081y/kXkhw==\n","video",saveCurrentTime,saveCurrentDate,"hello","kya","panding",multifileUri.toString(),multifileUri);
                            }
                            else if (activeNetWork.getType() == ConnectivityManager.TYPE_MOBILE){

                                addVideo(multifileUri.toString(),fuser.getUid(),postid,messageID,"kt5rg2/r78I081y/kXkhw==\n","video",saveCurrentTime,saveCurrentDate,"hello","kya","panding",multifileUri.toString(),multifileUri);
                            }
                        }
                        else {
                            addItemInRealmNoInternet(multifileUri.toString(),fuser.getUid(),postid,messageID,"kt5rg2/r78I081y/kXkhw==\n","video",saveCurrentTime,saveCurrentDate,"hello","kya","never",multifileUri.toString());

                            Toast.makeText(MassageActivityInstagram.this, "you are not connected with Internet Connection", Toast.LENGTH_SHORT).show();
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

                final String messageSenderRef = "Chats/" + fuser.getUid() + "/" + postid;
                final String messageReceiverRef = "Chats/" + postid + "/" + fuser.getUid();

                DatabaseReference userMessageKeyRef = RootRef.child("Chats")
                        .child(fuser.getUid()).child(postid).push();


                ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetWork = manager.getActiveNetworkInfo();

                String messageID = saveCurrentDate2 +saveCurrentTime2;

                if (null!=activeNetWork){
                    if (activeNetWork.getType() == ConnectivityManager.TYPE_WIFI){

                        addPdfs(fileUri.toString(),fuser.getUid(),postid,messageID,"kt5rg2/r78I081y/kXkhw==\n","PDF",saveCurrentTime,saveCurrentDate,"hello","kya","panding",fileUri.toString(),fileUri);
                    }
                    else if (activeNetWork.getType() == ConnectivityManager.TYPE_MOBILE){

                        addPdfs(fileUri.toString(),fuser.getUid(),postid,messageID,"kt5rg2/r78I081y/kXkhw==\n","PDF",saveCurrentTime,saveCurrentDate,"hello","kya","panding",fileUri.toString(),fileUri);
                    }
                }
                else {
                    addItemInRealmNoInternet(fileUri.toString(),fuser.getUid(),postid,messageID,"kt5rg2/r78I081y/kXkhw==\n","PDF",saveCurrentTime,saveCurrentDate,"hello","kya","never",fileUri.toString());

                    Toast.makeText(MassageActivityInstagram.this, "you are not connected with Internet Connection", Toast.LENGTH_SHORT).show();
                }


            } else if (cheker.equals("docx")) {

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


                ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetWork = manager.getActiveNetworkInfo();

                String messageID = saveCurrentDate2 +saveCurrentTime2;
                if (null!=activeNetWork){
                    if (activeNetWork.getType() == ConnectivityManager.TYPE_WIFI){

                        addPdfs(fileUri.toString(),fuser.getUid(),postid,messageID,"kt5rg2/r78I081y/kXkhw==\n","docx",saveCurrentTime,saveCurrentDate,"hello","kya","panding",fileUri.toString(),fileUri);
                    }
                    else if (activeNetWork.getType() == ConnectivityManager.TYPE_MOBILE){

                        addPdfs(fileUri.toString(),fuser.getUid(),postid,messageID,"kt5rg2/r78I081y/kXkhw==\n","docx",saveCurrentTime,saveCurrentDate,"hello","kya","panding",fileUri.toString(),fileUri);
                    }
                }
                else {
                    addItemInRealmNoInternet(fileUri.toString(),fuser.getUid(),postid,messageID,"kt5rg2/r78I081y/kXkhw==\n","docx",saveCurrentTime,saveCurrentDate,"hello","kya","never",fileUri.toString());

                    Toast.makeText(MassageActivityInstagram.this, "you are not connected with Internet Connection", Toast.LENGTH_SHORT).show();
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

                        addVideo(fileUri.toString(),fuser.getUid(),postid,messageID,"kt5rg2/r78I081y/kXkhw==\n","video",saveCurrentTime,saveCurrentDate,"hello","kya","panding",fileUri.toString(),fileUri);
                    }
                    else if (activeNetWork.getType() == ConnectivityManager.TYPE_MOBILE){

                        addVideo(fileUri.toString(),fuser.getUid(),postid,messageID,"kt5rg2/r78I081y/kXkhw==\n","video",saveCurrentTime,saveCurrentDate,"hello","kya","panding",fileUri.toString(),fileUri);
                    }

                }
                else {
                    addItemInRealmNoInternet(fileUri.toString(),fuser.getUid(),postid,messageID,"kt5rg2/r78I081y/kXkhw==\n","video",saveCurrentTime,saveCurrentDate,"hello","kya","never",fileUri.toString());

                    Toast.makeText(MassageActivityInstagram.this, "you are not connected with Internet Connection", Toast.LENGTH_SHORT).show();
                }

            }

            else {

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

    class ImageCompressionAsyncTask extends AsyncTask<String, Void, String> {
        Context mContext;
        int position;
        int totalItemSelected;

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


            ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetWork = manager.getActiveNetworkInfo();

            if (null!=activeNetWork){
                if (activeNetWork.getType() == ConnectivityManager.TYPE_WIFI){

                    addImages(compressUri.toString(),fuser.getUid(),postid,saveCurrentDate2 +saveCurrentTime2,"kt5rg2/r78I081y/kXkhw==\n",cheker,saveCurrentTime,saveCurrentDate,"hello","kya","false",compressUri.toString());
                }
                else if (activeNetWork.getType() == ConnectivityManager.TYPE_MOBILE){

                    addImages(compressUri.toString(),fuser.getUid(),postid,saveCurrentDate2 +saveCurrentTime2,"kt5rg2/r78I081y/kXkhw==\n",cheker,saveCurrentTime,saveCurrentDate,"hello","kya","false",compressUri.toString());
                }

            }
            else {
                addImages(compressUri.toString(),fuser.getUid(),postid,saveCurrentDate2 +saveCurrentTime2,"kt5rg2/r78I081y/kXkhw==\n",cheker,saveCurrentTime,saveCurrentDate,"hello","kya","false",compressUri.toString());

                Toast.makeText(MassageActivityInstagram.this, "you are not connected with Internet Connection", Toast.LENGTH_SHORT).show();
            }

            downloadAdapter.notifyDataSetChanged();
        }

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

    @Override
    protected void onStart() {
        super.onStart();
        seenMessageseen = false;
        state("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        seenMessageseen = false;
        typingStatus("noOne");
        state("offline");
        findFriendLis.setVisibility(View.GONE);

    }

    @Override
    protected void onStop() {
        super.onStop();
        seenMessageseen = false;
        typingStatus("noOne");
        state("offline");
        findFriendLis.setVisibility(View.GONE);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        seenMessageseen = false;
        typingStatus("noOne");
        findFriendLis.setVisibility(View.GONE);

    }

    public class MyDataAdapter extends RecyclerView.Adapter<MyDataAdapter.MyViewHolder> implements Filterable {

        public static final String ITEM_KEY = "item_key";
        private List<CityDataItem> mDataList;
        private List<CityDataItem> mDataListFull;
        private Context mContext;

        public MyDataAdapter(List<CityDataItem> mDataList, Context mContext) {
            this.mDataList = mDataList;
            this.mContext = mContext;
            mDataListFull = new ArrayList<>();
            mDataListFull.addAll(mDataList);
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(mContext).inflate(R.layout.trans_search, parent, false);

            return new MyViewHolder(view);

        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

            final CityDataItem cityDataItem = mDataList.get(position);


            holder.ret_hindi.setVisibility(View.VISIBLE);
            holder.ret_hindi.setText(cityDataItem.getHindi());
            holder.ret_english.setText(cityDataItem.getEnglish());


            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notify = true;

                    findFriendLis.setVisibility(View.GONE);

                    try {
                        msg = encrypt(cityDataItem.getHindi(), inputPassword.getText().toString());
                        trans_msg = encrypt(cityDataItem.getEnglish().toString(), inputPassword.getText().toString());


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (!msg.equals("")) {


                        addCommentSender(msg, trans_msg);

                    } else {
                        Toast.makeText(MassageActivityInstagram.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                    }


                }
            });

        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView ret_hindi, userStatus, ret_english;

            CircleImageView profileImage;
            View mView;

            public MyViewHolder(View itemView) {
                super(itemView);

                ret_hindi = itemView.findViewById(R.id.hindi);
                ret_english = itemView.findViewById(R.id.english);
                mView = itemView;
            }

        }

        @Override
        public Filter getFilter() {
            return cityDataFilter;
        }

        private Filter cityDataFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<CityDataItem> filteredList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(mDataListFull);
                } else {

                    String filter = constraint.toString().toLowerCase().trim();

                    for (CityDataItem dataItem : mDataListFull) {
                        if (dataItem.getHindi().toLowerCase().contains(filter)) {
                            filteredList.add(dataItem);
                        }
                    }
                }
                FilterResults results = new FilterResults();
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

    private void lastMessage(final String userid) {
        theLastMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats").child(fuser.getUid()).child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (firebaseUser != null && chat != null) {
                        if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                                chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())) {
                            theLastMessage = chat.getMessageID();
                        }
                    }
                }

                switch (theLastMessage) {
                    case "default":
                        HashMap<String, Object> hashMap1 = new HashMap<>();

                        hashMap1.put("date", null);
                        break;

                    default:
                        username.setText(theLastMessage);

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(fuser.getUid());
                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference(userid);


                        HashMap<String, Object> hashMap = new HashMap<>();

                        hashMap.put("date", null);
                        reference.child(theLastMessage).setValue(hashMap);
                        reference1.child(theLastMessage).setValue(hashMap);


                        break;
                }

                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String decrypt(String outputString, String password) throws Exception {
        SecretKeySpec key = generateKey(password);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decodeValue = Base64.decode(outputString, Base64.DEFAULT);
        byte[] decvalue = c.doFinal(decodeValue);
        String decryptvalue = new String(decvalue);
        return decryptvalue;

    }

    private void sendNotifiaction(String receiver, final String username, final String message) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(postid, R.mipmap.ic_launcher, username + ": " + message, adminId,
                            receiver);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success != 1) {
                                            Toast.makeText(MassageActivityInstagram.this, "Failed!", Toast.LENGTH_SHORT).show();
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

    private void addCommentSender(String msg, String trans_msg) {


        RootRef = FirebaseDatabase.getInstance().getReference("GroupChats").child(adminId).child(postid);

        final String messagePushID = generateString(12);


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



        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("time", saveCurrentTime);
        hashMap.put("date", saveCurrentDate);
        hashMap.put("messageID", saveCurrentDate1+saveCurrentTime1);
        hashMap.put("sender", fuser.getUid());
        hashMap.put("receiver", postid);
        hashMap.put("message", msg);
        hashMap.put("name",str_name);
        hashMap.put("title","rj 31");
        hashMap.put("bio", trans_msg);
        hashMap.put("type", "text");
        hashMap.put("isseen", "false");
        hashMap.put(sqlId, "false");
        hashMap.put("username", str_name);
        hashMap.put("preMessage", "jscns");
        hashMap.put("lastSendMessage", msg);


        RootRef.child(saveCurrentDate1+saveCurrentTime1).setValue(hashMap);

        findFriendLis.setVisibility(View.GONE);


        readUser();


        // add user to chat fragment
        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(fuser.getUid())
                .child(postid);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef.child("id").setValue(postid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(postid)
                .child(fuser.getUid());
        chatRefReceiver.child("id").setValue(fuser.getUid());


        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (notify) {
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private String generateString(int lenth){
        char[] chasr= "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM".toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i <lenth ; i++) {
            char c = chasr[random.nextInt(chasr.length)];
            stringBuilder.append(c);


        }
        return stringBuilder.toString();
    }

    public static class PersonDBHelper extends SQLiteOpenHelper {

        public static final String DATABASE_NAME = sqlId + ".db";
        private static final int DATABASE_VERSION = 3;
        public static final String TABLE_NAME = "People";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_PERSON_NAME = "name";
        public static final String COLUMN_PERSON_SENDER = "sender";
        public static final String COLUMN_PERSON_RECIEVER = "recicver";
        public static final String COLUMN_PERSON_IMAGEURL = "image";
        public static final String COLUMN_PERSON_MESSAGEID = "messageID";
        public static final String COLUMN_PERSON_RANDOMID = "randomId";
        public static final String COLUMN_PERSON_DATE = "date";
        public static final String COLUMN_PERSON_TIME = "time";
        public static final String COLUMN_PERSON_TYPE = "type";
        public static final String COLUMN_PERSON_MESSAGE = "message";
        public static final String COLUMN_PERSON_PREVIEW = "preview";
        public static final String COLUMN_PERSON_BIO = "bio";
        public static final String COLUMN_PERSON_ISSEEN = "isseen";

        public static final String COLUMN_PERSON_LASTSENDMESSAGE = "lastsendmessage";


        public PersonDBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public PersonDBHelper(Context context, String database) {
            super(context, database, null, DATABASE_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(" CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_PERSON_MESSAGE + " TEXT NOT NULL, " +
                    COLUMN_PERSON_NAME + " TEXT NOT NULL, " +
                    COLUMN_PERSON_SENDER + " TEXT NOT NULL, " +
                    COLUMN_PERSON_RECIEVER + " TEXT NOT NULL, " +
                    COLUMN_PERSON_MESSAGEID + " TEXT NOT NULL, " +
                    COLUMN_PERSON_BIO + " TEXT NOT NULL, " +
                    COLUMN_PERSON_TYPE + " TEXT NOT NULL, " +
                    COLUMN_PERSON_TIME + " TEXT NOT NULL, " +
                    COLUMN_PERSON_DATE + " TEXT NOT NULL, " +
                    COLUMN_PERSON_LASTSENDMESSAGE + " TEXT NOT NULL, " +
                    COLUMN_PERSON_PREVIEW + " TEXT NOT NULL, " +
                    COLUMN_PERSON_ISSEEN + " TEXT NOT NULL, " +
                    COLUMN_PERSON_IMAGEURL + " BLOB NOT NULL);"
            );

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // you can implement here migration process
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            this.onCreate(db);
        }

        /**
         * create record
         **/
        public void saveNewPerson(GroupMessage sqlUser) {

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_PERSON_MESSAGE, sqlUser.getMessage());
            values.put(COLUMN_PERSON_NAME, sqlUser.getFullname());
            values.put(COLUMN_PERSON_SENDER, sqlUser.getSender());
            values.put(COLUMN_PERSON_RECIEVER, sqlUser.getReceiver());
            values.put(COLUMN_PERSON_MESSAGEID, sqlUser.getMessageID());
            values.put(COLUMN_PERSON_BIO, sqlUser.getBio());
            values.put(COLUMN_PERSON_TYPE, sqlUser.getType());
            values.put(COLUMN_PERSON_TIME, sqlUser.getTime());
            values.put(COLUMN_PERSON_DATE, sqlUser.getDate());
            values.put(COLUMN_PERSON_LASTSENDMESSAGE, sqlUser.getLastSendMessage());
            values.put(COLUMN_PERSON_PREVIEW, sqlUser.getPreMessage());
            values.put(COLUMN_PERSON_ISSEEN, sqlUser.getIsseen());
            values.put(COLUMN_PERSON_IMAGEURL, sqlUser.getImagrUrl());

            // insert
            db.insert(TABLE_NAME, null, values);
            db.close();
        }

        /**
         * Query records, give options to filter results
         **/
        public List<GroupMessage> peopleList(String filter) {
            String query;
            if (filter.equals("")) {
                //regular query
                query = "SELECT  * FROM " + TABLE_NAME;
            } else {
                //filter results by filter option provided
                query = "SELECT  * FROM " + TABLE_NAME + " ORDER BY " + filter;
            }

            List<GroupMessage> personLinkedList = new LinkedList<>();
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(query, null);
            GroupMessage person;


            if (cursor.moveToFirst()) {
                do {
                    person = new GroupMessage();

                    person.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
                    person.setMessage(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_MESSAGE)));
                    person.setFullname(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_NAME)));
                    person.setSender(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_SENDER)));
                    person.setReceiver(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_RECIEVER)));
                    person.setMessageID(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_MESSAGEID)));
                    person.setBio(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_BIO)));
                    person.setType(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_TYPE)));
                    person.setTime(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_TIME)));
                    person.setDate(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_DATE)));
                    person.setLastSendMessage(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_LASTSENDMESSAGE)));
                    person.setPreMessage(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_PREVIEW)));
                    person.setIsseen(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_ISSEEN)));
                    person.setImagrUrl(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_IMAGEURL)));
                    personLinkedList.add(person);
                } while (cursor.moveToNext());
            }

            return personLinkedList;
        }


        public List<GroupMessage> lastRow(String filter, TextView last_msg) {
            String query;
            if (filter.equals("")) {
                //regular query
                query = "SELECT  * FROM " + TABLE_NAME;
            } else {
                //filter results by filter option provided
                query = "SELECT  * FROM " + TABLE_NAME + " ORDER BY " + filter;
            }

            List<GroupMessage> personLinkedList = new LinkedList<>();
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(query, null);
            GroupMessage person;

            if (cursor.moveToLast()) {
                do {
                    person = new GroupMessage();

                    person.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
                    person.setMessage(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_MESSAGE)));
                    person.setFullname(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_NAME)));
                    person.setSender(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_SENDER)));
                    person.setReceiver(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_RECIEVER)));
                    person.setMessageID(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_MESSAGEID)));
                    person.setBio(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_BIO)));
                    person.setType(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_TYPE)));
                    person.setTime(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_TIME)));
                    person.setDate(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_DATE)));
                    person.setLastSendMessage(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_LASTSENDMESSAGE)));
                    person.setPreMessage(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_PREVIEW)));
                    person.setIsseen(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_ISSEEN)));
                    person.setImagrUrl(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_IMAGEURL)));
                    personLinkedList.add(person);


                    last_msg.setText(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_ISSEEN)));

                } while (cursor.moveToNext());
            }


            return personLinkedList;
        }


        /**
         * Query only 1 record
         **/
        public GroupMessage getPerson(long id) {
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "SELECT  * FROM " + TABLE_NAME + " WHERE _id=" + id;
            Cursor cursor = db.rawQuery(query, null);

            GroupMessage receivedPerson = new GroupMessage();
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                receivedPerson.setMessage(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_MESSAGE)));
                receivedPerson.setFullname(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_NAME)));
                receivedPerson.setSender(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_SENDER)));
                receivedPerson.setReceiver(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_RECIEVER)));
                receivedPerson.setMessageID(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_MESSAGEID)));
                receivedPerson.setBio(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_BIO)));
                receivedPerson.setType(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_TYPE)));
                receivedPerson.setTime(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_TIME)));
                receivedPerson.setDate(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_DATE)));
                receivedPerson.setLastSendMessage(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_LASTSENDMESSAGE)));
                receivedPerson.setPreMessage(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_PREVIEW)));
                receivedPerson.setIsseen(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_ISSEEN)));
                receivedPerson.setImagrUrl(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_IMAGEURL)));
            }


            return receivedPerson;


        }


        /**
         * delete record
         **/
        public void deletePersonRecord(long id, Context context) {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE _id='" + id + "'");
            Toast.makeText(context, "Deleted successfully.", Toast.LENGTH_SHORT).show();
        }

        public void deletePersonRecord(String id, Context context,String firebaseId) {
            SQLiteDatabase db = this.getWritableDatabase();

            db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE messageID='" + id + "'");
            Toast.makeText(context, "Deleted successfully.", Toast.LENGTH_SHORT).show();

//            DatabaseReference rf = FirebaseDatabase.getInstance().getReference("DeleteSingleUserMessage").child(adminId).child(postid);
//            rf.child(firebaseId).removeValue();

        }

        /**
         * update record
         **/
        public void updateIsseenRecord(long personId, Context context, String sqlMessage) {
            SQLiteDatabase db = this.getWritableDatabase();
            //you can use the constants above instead of typing the column names
            db.execSQL("UPDATE  " + TABLE_NAME + " SET isseen ='" + "true" + "'  WHERE _id='" + personId + "'");
        }
    }

    public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.ViewHolder> {
        private List<GroupMessage> mChat;
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


        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {


            public TextView text_show_password, play, pause, show_message, imageTimeDate, videoTimeDate, pdfTimeDate, delete_one_message, download_pdf, open_video_activity, reply_image, reply_pdf, reply_video, reply_text, rep_message_text, rep_chat_text, rep_chat_image, rep_chat_video, rep_chat_pdf, rep_pdf_name;
            public ImageView profile_image, show_image, show_pdf, rep_message_image, rep_message_pdf;
            VideoView full_video;
            EditText password, password_null;
            public TextView txt_seen, textTimeDate, pdfName, full_screen, iconHidden, textUserName, imageUserName, videoUserName, pdfUserName;
            LinearLayout linearText, delete_text_linear, linearName;
            RelativeLayout linearimage, linearVideo, linearpdf, rep_text, rep_image, rep_video, rep_pdf, delete_one_linear;

            private ImageView show_video, rep_message_video;

            Button password_ok;
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
                delete_one_linear = itemView.findViewById(R.id.delete_one_linear);
                delete_one_message = itemView.findViewById(R.id.delete_one_message);
                download_pdf = itemView.findViewById(R.id.download_pdf);
                delete_text_linear = itemView.findViewById(R.id.delete_text_linear);
                pdfName = itemView.findViewById(R.id.pdfName);
                linearName = itemView.findViewById(R.id.linearName);
                pdfTimeDate = itemView.findViewById(R.id.pdfTimeDate);
                play = itemView.findViewById(R.id.play);
                pause = itemView.findViewById(R.id.pause);
                textUserName = itemView.findViewById(R.id.userNameText);
                pdfUserName = itemView.findViewById(R.id.userNamePdf);
                imageUserName = itemView.findViewById(R.id.userNameImage);
                videoUserName = itemView.findViewById(R.id.userNameVideo);

                open_video_activity = itemView.findViewById(R.id.open_video_activity);

                reply_image = itemView.findViewById(R.id.replyImage);
                reply_pdf = itemView.findViewById(R.id.replyPdf);
                reply_video = itemView.findViewById(R.id.replyVideo);
                reply_text = itemView.findViewById(R.id.replyText);


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


            }
        }

        public void add(int position, GroupMessage person) {
            mChat.add(position, person);
            notifyItemInserted(position);
        }

        public void remove(int position) {
            mChat.remove(position);
            notifyItemRemoved(position);
        }


        // Provide a suitable constructor (depends on the kind of dataset)
        public PersonAdapter(List<GroupMessage> myDataset, Context context, RecyclerView recyclerView) {
            mChat = myDataset;
            mContext = context;
            mRecyclerV = recyclerView;
        }

        public PersonAdapter(List<GroupMessage> myDataset, Context context) {
            mChat = myDataset;
            mContext = context;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public PersonAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == MSG_TYPE_RIGHT) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.group_chat_item_right, parent, false);
                return new ViewHolder(view);
            } else {
                View view = LayoutInflater.from(mContext).inflate(R.layout.group_chat_item_left, parent, false);
                return new ViewHolder(view);
            }
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {

            mAuth = FirebaseAuth.getInstance();
            final String messageSenderId = mAuth.getCurrentUser().getUid();
            final GroupMessage chat = mChat.get(position);

            String fromMessageType = chat.getType();
            String fromUserID = chat.getSender();
            String messageId = chat.getMessageID();
            holder.show_message.setText(chat.getMessage());


            holder.pdfName.setVisibility(View.GONE);
            holder.textTimeDate.setVisibility(View.GONE);
            holder.imageTimeDate.setVisibility(View.GONE);
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
            holder.delete_one_linear.setVisibility(View.GONE);
            holder.text_show_password.setVisibility(View.GONE);
            holder.password.setVisibility(View.GONE);
            holder.password_ok.setVisibility(View.GONE);
            holder.password_null.setVisibility(View.GONE);
            holder.pause.setVisibility(View.GONE);
            holder.play.setVisibility(View.GONE);
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


            /*
            holder.play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playvideo(position, holder);

                }
            });
            holder.pause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pausevideo(position, holder);

                }
            });

            holder.linearVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    holder.play.setVisibility(View.GONE);
                    holder.pause.setVisibility(View.VISIBLE);

                }
            });

             */


            if (fromMessageType.equals("text")) {
                if (fromUserID.equals(messageSenderId)) {
                    holder.linearText.setVisibility(View.VISIBLE);
                    holder.text_show_password.setVisibility(View.VISIBLE);
                    holder.reply_text.setVisibility(View.VISIBLE);

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
                    holder.linearText.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            deleteSentMessage(position,holder,chat.getId(),chat.getMessageID());

                            return true;
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
                                    holder.linearText.setVisibility(View.GONE);

                                }
                            });
                            builder.setNegativeButton("Me", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    holder.linearText.setVisibility(View.GONE);
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
                    holder.textUserName.setText(chat.getFullname());
                    holder.reply_text.setVisibility(View.VISIBLE);


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
                                convertString = decrypt(chat.getBio(), holder.password_null.getText().toString());
                            } catch (Exception e) {
                                e.printStackTrace();

                            }

                            holder.show_message.setVisibility(View.VISIBLE);
                            holder.show_message.setText(convertString);
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
                            builder.setNeutralButton("delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    holder.linearText.setVisibility(View.GONE);

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


                    holder.delete_one_message.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        //    deleteSentMessage(position, holder);
                        }
                    });
                }

            } else if (fromMessageType.equals("image")) {

                if (fromUserID.equals(messageSenderId)) {
                    holder.show_image.setVisibility(View.VISIBLE);
                    holder.linearimage.setVisibility(View.VISIBLE);
                    holder.imageTimeDate.setVisibility(View.VISIBLE);
                    holder.imageTimeDate.setText(chat.getDate() + "  -  " + chat.getTime());
                    Picasso.get().load(chat.getMessage()).into(holder.show_image);


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
                            builder.setNeutralButton("Everyone", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    deleteMessageForEveryone(position,holder);
                                    holder.linearimage.setVisibility(View.GONE);

                                }
                            });
                            builder.setNegativeButton("Me", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    holder.linearimage.setVisibility(View.GONE);
                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();

                                return true;
                        }
                    });


                    holder.download_pdf.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mChat.get(position).getMessage()));
                            holder.itemView.getContext().startActivity(intent);
                        }
                    });


                    holder.delete_one_message.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                       //     deleteSentMessage(position, holder);
                        }
                    });


                } else {

                    holder.show_image.setVisibility(View.VISIBLE);
                    holder.linearimage.setVisibility(View.VISIBLE);
                    holder.imageTimeDate.setVisibility(View.VISIBLE);
                    holder.imageUserName.setText(chat.getFullname());
                    holder.imageTimeDate.setText(chat.getDate() + "  -  " + chat.getTime());
                    Picasso.get().load(chat.getMessage()).into(holder.show_image);


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
                                    dialog.dismiss();
                                    holder.linearimage.setVisibility(View.GONE);


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


                    holder.download_pdf.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mChat.get(position).getMessage()));
                            holder.itemView.getContext().startActivity(intent);
                        }
                    });

                    holder.delete_one_message.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                       //     deleteSentMessage(position, holder);
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


                    holder.show_video.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(holder.itemView.getContext(), DirectVideo.class);
                            intent.putExtra("visit_user_id", chat.getReceiver());
                            intent.putExtra("messageId", mChat.get(position).getMessage());
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
                                    deleteMessageForEveryone(position,holder);
                                    holder.linearVideo.setVisibility(View.GONE);
                                }
                            });
                            builder.setNegativeButton("Me", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    holder.linearVideo.setVisibility(View.GONE);
                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();

                              return true;
                        }
                    });


                    holder.delete_one_message.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                      //      deleteSentMessage(position, holder);
                        }
                    });


                } else {

                    holder.linearVideo.setVisibility(View.VISIBLE);
                    holder.videoTimeDate.setVisibility(View.VISIBLE);
                    holder.videoTimeDate.setText(chat.getDate() + "  -  " + chat.getTime());
                    holder.show_video.setVisibility(View.VISIBLE);
                    holder.videoUserName.setText(chat.getFullname());


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


                    holder.show_video.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(holder.itemView.getContext(), DirectVideo.class);
                            intent.putExtra("visit_user_id", chat.getReceiver());
                            intent.putExtra("messageId", mChat.get(position).getMessage());
                            holder.itemView.getContext().startActivity(intent);

                        }
                    });

                    holder.show_video.setOnLongClickListener(new View.OnLongClickListener() {
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
                                    dialog.dismiss();
                                    holder.linearVideo.setVisibility(View.GONE);

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

                    holder.delete_one_message.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
        //                    deleteSentMessage(position, holder);
                        }
                    });


                }
            } else if (fromMessageType.equals("PDF") || (fromMessageType.equals("docx"))) {
                {
                    if (fromUserID.equals(messageSenderId)) {
                        holder.linearpdf.setVisibility(View.VISIBLE);
                        holder.pdfName.setVisibility(View.VISIBLE);
                        holder.pdfName.setText(chat.getFullname());
                        holder.linearName.setVisibility(View.VISIBLE);
                        holder.show_pdf.setVisibility(View.VISIBLE);
                        holder.pdfTimeDate.setVisibility(View.VISIBLE);
                        holder.pdfTimeDate.setText(chat.getDate() + "  -  " + chat.getTime());
                        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/watsapp-6d8e6.appspot.com/o/file.png?alt=media&token=da730f2c-5760-4d67-b45b-b8ff497039b6")
                                .into(holder.show_pdf);


                        holder.reply_pdf.setVisibility(View.VISIBLE);
                        holder.reply_pdf.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                relative_layout_message.setVisibility(View.GONE);
                                reply_bottom.setVisibility(View.VISIBLE);
                                reply_preview_message.setText(chat.getFullname() + ".pdf");
                                type.setText("reply_pdf");


                                Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/watsapp-6d8e6.appspot.com/o/file.png?alt=media&token=da730f2c-5760-4d67-b45b-b8ff497039b6")
                                        .into(reply_image);


                            }
                        });


                        holder.show_pdf.setOnLongClickListener(new View.OnLongClickListener() {
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
                                        dialog.dismiss();
                                        holder.linearpdf.setVisibility(View.GONE);
                                    }
                                });
                                builder.setNegativeButton("Download", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mChat.get(position).getMessage()));
                                        holder.itemView.getContext().startActivity(intent);
                                        dialog.dismiss();
                                    }
                                });
                                builder.create().show();

                                 return true;
                            }
                        });


                        holder.download_pdf.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mChat.get(position).getMessage()));
                                holder.itemView.getContext().startActivity(intent);
                            }
                        });



                    } else {
                        holder.linearName.setVisibility(View.VISIBLE);
                        holder.pdfName.setVisibility(View.VISIBLE);
                        holder.pdfName.setText(chat.getFullname());
                        holder.linearpdf.setVisibility(View.VISIBLE);
                        holder.pdfTimeDate.setVisibility(View.VISIBLE);
                        holder.pdfUserName.setText(chat.getFullname());
                        holder.pdfTimeDate.setText(chat.getDate() + "  -  " + chat.getTime());
                        holder.show_pdf.setVisibility(View.VISIBLE);
                        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/watsapp-6d8e6.appspot.com/o/file.png?alt=media&token=da730f2c-5760-4d67-b45b-b8ff497039b6")
                                .into(holder.show_pdf);


                        holder.reply_pdf.setVisibility(View.VISIBLE);
                        holder.reply_pdf.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                relative_layout_message.setVisibility(View.GONE);
                                reply_bottom.setVisibility(View.VISIBLE);

                                reply_preview_message.setText(chat.getFullname() + ".Pdf");

                                type.setText("reply_pdf");

                                Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/watsapp-6d8e6.appspot.com/o/file.png?alt=media&token=da730f2c-5760-4d67-b45b-b8ff497039b6")
                                        .into(reply_image);


                            }
                        });


                        holder.show_pdf.setOnLongClickListener(new View.OnLongClickListener() {
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
                                        dialog.dismiss();
                                        holder.linearpdf.setVisibility(View.GONE);
                                    }
                                });
                                builder.setNegativeButton("Download", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mChat.get(position).getMessage()));
                                        holder.itemView.getContext().startActivity(intent);
                                    }
                                });
                                builder.create().show();
                                return true;
                            }
                        });


                        holder.download_pdf.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mChat.get(position).getMessage()));
                                holder.itemView.getContext().startActivity(intent);
                            }
                        });


                        holder.delete_one_message.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                     //           deleteSentMessage(position, holder);
                            }
                        });


                    }
                }
            } else if (fromMessageType.equals("reply_text")) {
                if (fromUserID.equals(messageSenderId)) {

                    holder.rep_message_text.setVisibility(View.VISIBLE);
                    holder.rep_chat_text.setVisibility(View.VISIBLE);
                    holder.rep_text.setVisibility(View.VISIBLE);


                    try {
                        outputString = decrypt(chat.getMessage(), holder.password_null.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();

                    }


                    holder.rep_message_text.setText(chat.getPreMessage());
                    holder.rep_chat_text.setText(outputString);


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
                                    holder.rep_text.setVisibility(View.GONE);


                                }
                            });
                            builder.setNegativeButton("Me", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    holder.rep_text.setVisibility(View.GONE);
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

                    try {
                        outputString = decrypt(chat.getMessage(), holder.password_null.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();

                    }


                    holder.rep_message_text.setText(chat.getPreMessage());
                    holder.rep_chat_text.setText(outputString);


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
                                    dialog.dismiss();
                                    holder.rep_text.setVisibility(View.GONE);


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


                    try {
                        outputString = decrypt(chat.getMessage(), holder.password_null.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();

                    }


                    Picasso.get().load(chat.getPreMessage()).into(holder.rep_message_image);
                    holder.rep_chat_image.setText(outputString);


                    holder.rep_message_image.setOnLongClickListener(new View.OnLongClickListener() {
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
                                    holder.rep_image.setVisibility(View.GONE);

                                }
                            });
                            builder.setNegativeButton("Me", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    holder.rep_image.setVisibility(View.GONE);
                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();

                               return true;
                        }
                    });

                    holder.delete_one_message.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                    //        deleteSentMessage(position, holder);
                        }
                    });


                } else {
                    holder.rep_message_image.setVisibility(View.VISIBLE);
                    holder.rep_chat_image.setVisibility(View.VISIBLE);
                    holder.rep_image.setVisibility(View.VISIBLE);


                    try {
                        outputString = decrypt(chat.getMessage(), holder.password_null.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();

                    }


                    Picasso.get().load(chat.getPreMessage()).into(holder.rep_message_image);
                    holder.rep_chat_image.setText(outputString);


                    holder.rep_message_image.setOnLongClickListener(new View.OnLongClickListener() {
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
                                    dialog.dismiss();
                                    holder.rep_image.setVisibility(View.GONE);

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

                    holder.delete_one_message.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                      //      deleteSentMessage(position, holder);
                        }
                    });


                }

            } else if (fromMessageType.equals("reply_video")) {
                if (fromUserID.equals(messageSenderId)) {
                    holder.rep_message_video.setVisibility(View.VISIBLE);
                    holder.rep_chat_video.setVisibility(View.VISIBLE);
                    holder.rep_video.setVisibility(View.VISIBLE);

                    try {
                        outputString = decrypt(chat.getMessage(), holder.password_null.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    holder.rep_chat_video.setText(outputString);


                    long interval = 1000;
                    RequestOptions options = new RequestOptions().frame(interval);

                    Glide.with(mContext).asBitmap().load(chat.getImagrUrl()).apply(options).into(holder.rep_message_video);

                    holder.rep_message_video.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(holder.itemView.getContext(), DirectVideo.class);
                            intent.putExtra("visit_user_id", chat.getReceiver());
                            intent.putExtra("messageId", mChat.get(position).getImagrUrl());
                            holder.itemView.getContext().startActivity(intent);

                        }
                    });

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
                            builder.setNeutralButton("Everyone", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    deleteMessageForEveryone(position, holder);
                                    holder.rep_video.setVisibility(View.GONE);

                                }
                            });
                            builder.setNegativeButton("Me", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    holder.rep_video.setVisibility(View.GONE);
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

                    try {
                        outputString = decrypt(chat.getMessage(), holder.password_null.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    holder.rep_chat_video.setText(outputString);

                    long interval = 1000;
                    RequestOptions options = new RequestOptions().frame(interval);

                    Glide.with(mContext).asBitmap().load(chat.getImagrUrl()).apply(options).into(holder.rep_message_video);


                    holder.rep_message_video.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(holder.itemView.getContext(), DirectVideo.class);
                            intent.putExtra("visit_user_id", chat.getReceiver());
                            intent.putExtra("messageId", mChat.get(position).getImagrUrl());
                            holder.itemView.getContext().startActivity(intent);

                        }
                    });


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
                                    dialog.dismiss();
                                    holder.rep_video.setVisibility(View.GONE);

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

                    holder.delete_one_message.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                      //      deleteSentMessage(position, holder);
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

                    Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/watsapp-6d8e6.appspot.com/o/file.png?alt=media&token=da730f2c-5760-4d67-b45b-b8ff497039b6")
                            .into(holder.rep_message_pdf);


                    try {
                        outputString = decrypt(chat.getMessage(), holder.password_null.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();

                    }


                    holder.rep_chat_pdf.setText(outputString);


                    holder.rep_pdf.setOnLongClickListener(new View.OnLongClickListener() {
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
                                    dialog.dismiss();
                                    holder.rep_pdf.setVisibility(View.GONE);


                                }
                            });
                            builder.setNegativeButton("Download", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mChat.get(position).getPreMessage()));
                                    holder.itemView.getContext().startActivity(intent);
                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();

                             return true;
                        }
                    });

                    holder.delete_one_message.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                       //     deleteSentMessage(position, holder);
                        }
                    });


                    holder.download_pdf.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mChat.get(position).getPreMessage()));
                            holder.itemView.getContext().startActivity(intent);
                        }
                    });


                } else {
                    holder.rep_message_pdf.setVisibility(View.VISIBLE);
                    holder.rep_chat_pdf.setVisibility(View.VISIBLE);
                    holder.rep_pdf.setVisibility(View.VISIBLE);
                    holder.rep_pdf_name.setVisibility(View.VISIBLE);
                    holder.rep_pdf_name.setText(chat.getPreMessage());


                    Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/watsapp-6d8e6.appspot.com/o/file.png?alt=media&token=da730f2c-5760-4d67-b45b-b8ff497039b6")
                            .into(holder.rep_message_pdf);


                    try {
                        outputString = decrypt(chat.getMessage(), holder.password_null.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();

                    }


                    holder.rep_chat_pdf.setText(outputString);


                    holder.rep_pdf.setOnLongClickListener(new View.OnLongClickListener() {
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
                                    dialog.dismiss();
                                    holder.rep_pdf.setVisibility(View.GONE);

                                }
                            });
                            builder.setNegativeButton("Download", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mChat.get(position).getPreMessage()));
                                    holder.itemView.getContext().startActivity(intent);
                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();
                            return true;
                        }
                    });

                    holder.delete_one_message.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                      //      deleteSentMessage(position, holder);
                        }
                    });

                }


            } else {
                //      holder.fullLinesrvideo.setVisibility(View.GONE);

            }

        }

        private void goToUpdateActivity(long personId) {
            Intent goToUpdate = new Intent(mContext, UpdateRecordActivity.class);
            goToUpdate.putExtra("USER_ID", personId);
            mContext.startActivity(goToUpdate);
        }

        private void deleteSentMessage(int position, final ViewHolder holder,long id,String messageId) {

            HashMap<String,Object> hashMap = new HashMap<>();
            hashMap.put("delete"+fuser.getUid(),true);

            DatabaseReference  databaseRef = FirebaseDatabase.getInstance().getReference("GroupChats").child(adminId).child(postid);
            databaseRef.child(messageId).updateChildren(hashMap);

            databaseRef.child(messageId).updateChildren(hashMap).
                    addOnCompleteListener(new OnCompleteListener<Void>() {
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
            DatabaseReference  databaseRef = FirebaseDatabase.getInstance().getReference("GroupChats").child(adminId).child(postid);
        databaseRef.child(mChat.get(position).getMessageID())
                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                        DatabaseReference  databaseRef = FirebaseDatabase.getInstance().getReference("GroupChats").child(adminId).child(postid);

                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put(fuser.getUid(),true);

                        databaseRef.child(mChat.get(position).getMessageID()).updateChildren(hashMap);
                        Toast.makeText(holder.itemView.getContext(), "Deleted Successfully...", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(holder.itemView.getContext(), "Error occurred..", Toast.LENGTH_SHORT).show();

                    }
                }
            });

        }

        private void deleteMessageForEveryone(final int position, final ViewHolder holder) {

            String messageId = mChat.get(position).getMessageID();

            final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            rootRef.child("GroupChats")
                    .child(adminId)
                    .child(postid)
                    .child(mChat.get(position).getMessageID())
                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        holder.itemView.setVisibility(View.INVISIBLE);

                        DatabaseReference rf = FirebaseDatabase.getInstance().getReference("DeleteGroupUserMessage");

                        String id = generateString(12);

                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("messageID",mChat.get(position).getMessageID());
                        hashMap.put("id",id);

                        rf.child(adminId)
                                .child(postid)
                                .child(id)
                                .updateChildren(hashMap);


                    }   else
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

    public class DownloadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private RecyclerView mRecyclerV;
        SimpleExoPlayer exoPlayer;
        boolean vidioplay = false;
        String AES = "AES";
        FirebaseAuth mAuth;
        String outputString, convertString;
        public static final int MSG_TYPE_LEFT = 0;
        public static final int MSG_TYPE_RIGHT = 1;
        Context context;
        List<DownloadModel> mChat = new ArrayList<>();

        public DownloadAdapter(Context context, List<DownloadModel> mChat) {
            this.context = context;
            this.mChat = mChat;
        }

        public class DownloadViewHolder extends RecyclerView.ViewHolder {
            public TextView text_show_password, play, pause, show_message, imageTimeDate, videoTimeDate, pdfTimeDate, delete_one_message, download_pdf, open_video_activity, reply_image, reply_pdf, reply_video, reply_text, rep_message_text, rep_chat_text, rep_chat_image, rep_chat_video, rep_chat_pdf, rep_pdf_name;
            public ImageView profile_image, show_image, show_pdf, rep_message_image, rep_message_pdf;
            VideoView full_video;
            EditText password, password_null;
            public TextView txt_seen, textTimeDate, pdfName, full_screen, iconHidden, textUserName, imageUserName, videoUserName, pdfUserName;
            LinearLayout linearText, delete_text_linear, linearName;
            RelativeLayout linearimage, linearVideo, linearpdf, rep_text, rep_image, rep_video, rep_pdf, delete_one_linear;

            private ImageView show_video, rep_message_video,pdf_download;

            Button password_ok;
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
                txt_seen = itemView.findViewById(R.id.txt_seen);
                show_image = itemView.findViewById(R.id.show_image_image);
                textTimeDate = itemView.findViewById(R.id.texttimeDate);
                linearimage = itemView.findViewById(R.id.linearsuri);
                linearpdf = itemView.findViewById(R.id.linearsuripdf);
                show_pdf = itemView.findViewById(R.id.show_pdf);
                show_video = itemView.findViewById(R.id.show_Video);
                linearVideo = itemView.findViewById(R.id.linearsuriVideo);
                videoTimeDate = itemView.findViewById(R.id.videoTimeDate);
                delete_one_linear = itemView.findViewById(R.id.delete_one_linear);
                delete_one_message = itemView.findViewById(R.id.delete_one_message);
                download_pdf = itemView.findViewById(R.id.download_pdf);
                delete_text_linear = itemView.findViewById(R.id.delete_text_linear);
                pdfName = itemView.findViewById(R.id.pdfName);
                linearName = itemView.findViewById(R.id.linearName);
                pdfTimeDate = itemView.findViewById(R.id.pdfTimeDate);
                play = itemView.findViewById(R.id.play);
                pause = itemView.findViewById(R.id.pause);
                textUserName = itemView.findViewById(R.id.userNameText);
                pdfUserName = itemView.findViewById(R.id.userNamePdf);
                imageUserName = itemView.findViewById(R.id.userNameImage);
                videoUserName = itemView.findViewById(R.id.userNameVideo);
                open_video_activity = itemView.findViewById(R.id.open_video_activity);
                reply_image = itemView.findViewById(R.id.replyImage);
                reply_pdf = itemView.findViewById(R.id.replyPdf);
                reply_video = itemView.findViewById(R.id.replyVideo);
                reply_text = itemView.findViewById(R.id.replyText);


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

            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            if (viewType == MSG_TYPE_RIGHT) {
                RecyclerView.ViewHolder vh;
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_chat_item_right, parent, false);
                vh = new DownloadViewHolder(view);
                return vh;
            } else {
                RecyclerView.ViewHolder vh;
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_chat_item_left, parent, false);
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
            String messageId = chat.getMessageID();
            holder.show_message.setText(chat.getMessage());


            holder.pdfName.setVisibility(View.GONE);
            holder.textTimeDate.setVisibility(View.GONE);
            holder.imageTimeDate.setVisibility(View.GONE);
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
            holder.delete_one_linear.setVisibility(View.GONE);
            holder.text_show_password.setVisibility(View.GONE);
            holder.password.setVisibility(View.GONE);
            holder.password_ok.setVisibility(View.GONE);
            holder.password_null.setVisibility(View.GONE);
            holder.pause.setVisibility(View.GONE);
            holder.play.setVisibility(View.GONE);
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


            if (fromMessageType.equals("text")) {
                if (fromUserID.equals(messageSenderId)) {
                    holder.linearText.setVisibility(View.VISIBLE);
                    holder.text_show_password.setVisibility(View.VISIBLE);
                    holder.reply_text.setVisibility(View.VISIBLE);

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
                    holder.linearText.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            //       deleteSentMessage(position,holder,chat.getId(),chat.getMessageID());

                            return true;
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
                                    holder.linearText.setVisibility(View.GONE);

                                }
                            });
                            builder.setNegativeButton("Me", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    holder.linearText.setVisibility(View.GONE);
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
                    holder.textUserName.setText(chat.getFile_path());
                    holder.reply_text.setVisibility(View.VISIBLE);


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
                                convertString = decrypt(chat.getBio(), holder.password_null.getText().toString());
                            } catch (Exception e) {
                                e.printStackTrace();

                            }

                            holder.show_message.setVisibility(View.VISIBLE);
                            holder.show_message.setText(convertString);
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
                            builder.setNeutralButton("delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    holder.linearText.setVisibility(View.GONE);

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

                    holder.delete_one_message.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //    deleteSentMessage(position, holder);
                        }
                    });
                }

            } else if (fromMessageType.equals("image")) {

                if (fromUserID.equals(messageSenderId)) {
                    holder.show_image.setVisibility(View.VISIBLE);
                    holder.linearimage.setVisibility(View.VISIBLE);
                    holder.imageTimeDate.setVisibility(View.VISIBLE);
                    holder.imageTimeDate.setText(chat.getDate() + "  -  " + chat.getTime());
                    Picasso.get().load(chat.getMessage()).into(holder.show_image);


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
                            builder.setNeutralButton("Everyone", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    holder.linearimage.setVisibility(View.GONE);

                                }
                            });
                            builder.setNegativeButton("Me", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    holder.linearimage.setVisibility(View.GONE);
                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();

                            return true;
                        }
                    });


                    holder.download_pdf.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mChat.get(position).getMessage()));
                            holder.itemView.getContext().startActivity(intent);
                        }
                    });


                    holder.delete_one_message.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //     deleteSentMessage(position, holder);
                        }
                    });


                } else {

                    holder.show_image.setVisibility(View.VISIBLE);
                    holder.linearimage.setVisibility(View.VISIBLE);
                    holder.imageTimeDate.setVisibility(View.VISIBLE);
                    holder.imageUserName.setText(chat.getFile_path());
                    holder.imageTimeDate.setText(chat.getDate() + "  -  " + chat.getTime());
                    Picasso.get().load(chat.getMessage()).into(holder.show_image);


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
                                    dialog.dismiss();
                                    holder.linearimage.setVisibility(View.GONE);


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


                    holder.download_pdf.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mChat.get(position).getMessage()));
                            holder.itemView.getContext().startActivity(intent);
                        }
                    });

                    holder.delete_one_message.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //     deleteSentMessage(position, holder);
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
                    holder.main_rel.setVisibility(View.GONE);


                    long interval = 1000;
                    RequestOptions options = new RequestOptions().frame(interval);

                    Glide.with(MassageActivityInstagram.this).asBitmap().load(chat.getImagrUrl()).apply(options).into(holder.show_video);

                    holder.reply_video.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            relative_layout_message.setVisibility(View.GONE);
                            reply_bottom.setVisibility(View.VISIBLE);

                            reply_preview_message.setText(mChat.get(position).getMessage());

                            type.setText("reply_video");

                            Glide.with(MassageActivityInstagram.this).asBitmap().load(chat.getImagrUrl()).apply(options).into(reply_image);


                        }
                    });


                    holder.show_video.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(holder.itemView.getContext(), DirectVideo.class);
                            intent.putExtra("visit_user_id", chat.getReceiver());
                            intent.putExtra("messageId", mChat.get(position).getMessage());
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
                                    holder.linearVideo.setVisibility(View.GONE);
                                }
                            });
                            builder.setNegativeButton("Me", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    holder.linearVideo.setVisibility(View.GONE);
                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();

                            return true;
                        }
                    });


                    holder.delete_one_message.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //      deleteSentMessage(position, holder);
                        }
                    });


                } else {

                    holder.linearVideo.setVisibility(View.VISIBLE);
                    holder.videoTimeDate.setVisibility(View.VISIBLE);
                    holder.videoTimeDate.setText(chat.getDate() + "  -  " + chat.getTime());
                    holder.show_video.setVisibility(View.VISIBLE);
                    holder.videoUserName.setText(chat.getFile_path());


                    long interval = 1000;
                    RequestOptions options = new RequestOptions().frame(interval);

                    Glide.with(MassageActivityInstagram.this).asBitmap().load(chat.getImagrUrl()).apply(options).into(holder.show_video);


                    holder.file_title.setText(chat.getTitle());
                    holder.file_status.setText(chat.getStatus());
                    holder.file_progress.setProgress(Integer.parseInt(chat.getProgress()));
                    holder.file_size.setText(chat.getFile_size());



                    if (chat.getIsseen().equals("false")){
                        holder.main_rel.setVisibility(View.VISIBLE);
                        holder.videoTimeDate.setVisibility(View.GONE);
                        holder.reply_video.setVisibility(View.GONE);

                    }else {
                        holder.main_rel.setVisibility(View.GONE);
                        holder.videoTimeDate.setVisibility(View.VISIBLE);
                        holder.reply_video.setVisibility(View.VISIBLE);
                    }

                    holder.download_video_size.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            downloadMediaFiles(chat.getImagrUrl(),chat.getId());
                        }
                    });

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

                    holder.show_video.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(holder.itemView.getContext(), DirectVideo.class);
                            intent.putExtra("visit_user_id", chat.getReceiver());
                            intent.putExtra("messageId", mChat.get(position).getMessage());
                            holder.itemView.getContext().startActivity(intent);

                        }
                    });

                    holder.show_video.setOnLongClickListener(new View.OnLongClickListener() {
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
                                    dialog.dismiss();
                                    holder.linearVideo.setVisibility(View.GONE);

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

                    holder.delete_one_message.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //                    deleteSentMessage(position, holder);
                        }
                    });


                }
            } else if (fromMessageType.equals("PDF")) {
                {
                    if (fromUserID.equals(messageSenderId)) {
                        holder.linearpdf.setVisibility(View.VISIBLE);
                        holder.pdfName.setVisibility(View.VISIBLE);
                        holder.pdfName.setText(chat.getFile_path());
                        holder.linearName.setVisibility(View.VISIBLE);
                        holder.show_pdf.setVisibility(View.VISIBLE);
                        holder.pdfTimeDate.setVisibility(View.VISIBLE);
                        holder.pdfTimeDate.setText(chat.getDate() + "  -  " + chat.getTime());
                        Picasso.get().load("file://"+chat.getImagrUrl()).into(holder.show_pdf);



                        holder.reply_pdf.setVisibility(View.VISIBLE);

                        holder.reply_pdf.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                relative_layout_message.setVisibility(View.GONE);
                                reply_bottom.setVisibility(View.VISIBLE);
                                reply_preview_message.setText(chat.getFile_path() + ".pdf");
                                type.setText("reply_pdf");


                                Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/watsapp-6d8e6.appspot.com/o/file.png?alt=media&token=da730f2c-5760-4d67-b45b-b8ff497039b6")
                                        .into(reply_image);


                            }
                        });

                        holder.show_pdf.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                opendocument( chat.getMessage());

                                generateImageFromPdf(Uri.parse(chat.getMessage()),chat.getId());

                            }
                        });

                        holder.show_pdf.setOnLongClickListener(new View.OnLongClickListener() {
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
                                        dialog.dismiss();
                                        holder.linearpdf.setVisibility(View.GONE);
                                    }
                                });
                                builder.setNegativeButton("Download", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mChat.get(position).getMessage()));
                                        holder.itemView.getContext().startActivity(intent);
                                        dialog.dismiss();
                                    }
                                });
                                builder.create().show();

                                return true;
                            }
                        });

                        holder.download_pdf.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mChat.get(position).getMessage()));
                                holder.itemView.getContext().startActivity(intent);
                            }
                        });


                    }
                    else {
                        holder.linearName.setVisibility(View.VISIBLE);
                        holder.pdfName.setVisibility(View.VISIBLE);
                        holder.pdfName.setText(chat.getFile_path());
                        holder.linearpdf.setVisibility(View.VISIBLE);
                        holder.pdfTimeDate.setVisibility(View.VISIBLE);
                        holder.pdfUserName.setText(chat.getFile_path());
                        holder.pdfTimeDate.setText(chat.getDate() + "  -  " + chat.getTime());
                        holder.show_pdf.setVisibility(View.VISIBLE);
                        Picasso.get().load("file://"+chat.getImagrUrl()).into(holder.show_pdf);


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


                        holder.reply_pdf.setVisibility(View.VISIBLE);
                        holder.reply_pdf.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                relative_layout_message.setVisibility(View.GONE);
                                reply_bottom.setVisibility(View.VISIBLE);

                                reply_preview_message.setText(chat.getFile_path() + ".Pdf");

                                type.setText("reply_pdf");

                                Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/watsapp-6d8e6.appspot.com/o/file.png?alt=media&token=da730f2c-5760-4d67-b45b-b8ff497039b6")
                                        .into(reply_image);


                            }
                        });

                        holder.linearpdf.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                opendocument( chat.getMessage());

                                generateImageFromPdf(Uri.parse(chat.getMessage()),chat.getId());

                            }
                        });


                        holder.show_pdf.setOnLongClickListener(new View.OnLongClickListener() {
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
                                        dialog.dismiss();
                                        holder.linearpdf.setVisibility(View.GONE);
                                    }
                                });
                                builder.setNegativeButton("Download", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mChat.get(position).getMessage()));
                                        holder.itemView.getContext().startActivity(intent);
                                    }
                                });
                                builder.create().show();
                                return true;
                            }
                        });


                        holder.download_pdf.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mChat.get(position).getMessage()));
                                holder.itemView.getContext().startActivity(intent);
                            }
                        });


                        holder.delete_one_message.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //           deleteSentMessage(position, holder);
                            }
                        });


                    }
                }

            }

            else if ((fromMessageType.equals("docx")))
            {
                if (fromUserID.equals(messageSenderId)) {
                    holder.linearpdf.setVisibility(View.VISIBLE);
                    holder.pdfName.setVisibility(View.VISIBLE);
                    holder.pdfName.setText(chat.getFile_path());
                    holder.linearName.setVisibility(View.VISIBLE);
                    holder.show_pdf.setVisibility(View.VISIBLE);
                    holder.pdfTimeDate.setVisibility(View.VISIBLE);
                    holder.pdfTimeDate.setText(chat.getDate() + "  -  " + chat.getTime());
                    holder.show_pdf.setVisibility(View.GONE);

                    holder.reply_pdf.setVisibility(View.VISIBLE);
                    holder.reply_pdf.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            relative_layout_message.setVisibility(View.GONE);
                            reply_bottom.setVisibility(View.VISIBLE);
                            reply_preview_message.setText(chat.getFile_path() + ".pdf");
                            type.setText("reply_pdf");


                            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/watsapp-6d8e6.appspot.com/o/file.png?alt=media&token=da730f2c-5760-4d67-b45b-b8ff497039b6")
                                    .into(reply_image);


                        }
                    });
                    holder.linearpdf.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            opendocument(chat.getMessage());
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
                                    dialog.dismiss();
                                    holder.linearpdf.setVisibility(View.GONE);
                                }
                            });
                            builder.setNegativeButton("Download", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mChat.get(position).getMessage()));
                                    holder.itemView.getContext().startActivity(intent);
                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();

                            return true;
                        }
                    });

                    holder.download_pdf.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mChat.get(position).getMessage()));
                            holder.itemView.getContext().startActivity(intent);
                        }
                    });


                } else {
                    holder.linearName.setVisibility(View.VISIBLE);
                    holder.pdfName.setVisibility(View.VISIBLE);
                    holder.pdfName.setText(chat.getFile_path());
                    holder.linearpdf.setVisibility(View.VISIBLE);
                    holder.pdfTimeDate.setVisibility(View.VISIBLE);
                    holder.pdfUserName.setText(chat.getFile_path());
                    holder.pdfTimeDate.setText(chat.getDate() + "  -  " + chat.getTime());
                    holder.show_pdf.setVisibility(View.VISIBLE);


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


                    holder.reply_pdf.setVisibility(View.VISIBLE);
                    holder.reply_pdf.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            relative_layout_message.setVisibility(View.GONE);
                            reply_bottom.setVisibility(View.VISIBLE);

                            reply_preview_message.setText(chat.getFile_path() + ".Pdf");

                            type.setText("reply_pdf");

                            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/watsapp-6d8e6.appspot.com/o/file.png?alt=media&token=da730f2c-5760-4d67-b45b-b8ff497039b6")
                                    .into(reply_image);


                        }
                    });


                    holder.show_pdf.setOnLongClickListener(new View.OnLongClickListener() {
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
                                    dialog.dismiss();
                                    holder.linearpdf.setVisibility(View.GONE);
                                }
                            });
                            builder.setNegativeButton("Download", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mChat.get(position).getMessage()));
                                    holder.itemView.getContext().startActivity(intent);
                                }
                            });
                            builder.create().show();
                            return true;
                        }
                    });


                    holder.download_pdf.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mChat.get(position).getMessage()));
                            holder.itemView.getContext().startActivity(intent);
                        }
                    });


                    holder.delete_one_message.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //           deleteSentMessage(position, holder);
                        }
                    });


                }
            }

            else if (fromMessageType.equals("reply_text")) {
                if (fromUserID.equals(messageSenderId)) {

                    holder.rep_message_text.setVisibility(View.VISIBLE);
                    holder.rep_chat_text.setVisibility(View.VISIBLE);
                    holder.rep_text.setVisibility(View.VISIBLE);


                    try {
                        outputString = decrypt(chat.getMessage(), holder.password_null.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();

                    }


                    holder.rep_message_text.setText(chat.getPreMessage());
                    holder.rep_chat_text.setText(outputString);


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
                                    holder.rep_text.setVisibility(View.GONE);


                                }
                            });
                            builder.setNegativeButton("Me", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    holder.rep_text.setVisibility(View.GONE);
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

                    try {
                        outputString = decrypt(chat.getMessage(), holder.password_null.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();

                    }


                    holder.rep_message_text.setText(chat.getPreMessage());
                    holder.rep_chat_text.setText(outputString);


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
                                    dialog.dismiss();
                                    holder.rep_text.setVisibility(View.GONE);


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

            else if (fromMessageType.equals("reply_image")) {
                if (fromUserID.equals(messageSenderId)) {

                    holder.rep_message_image.setVisibility(View.VISIBLE);
                    holder.rep_chat_image.setVisibility(View.VISIBLE);
                    holder.rep_image.setVisibility(View.VISIBLE);


                    try {
                        outputString = decrypt(chat.getMessage(), holder.password_null.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();

                    }


                    Picasso.get().load(chat.getPreMessage()).into(holder.rep_message_image);
                    holder.rep_chat_image.setText(outputString);


                    holder.rep_message_image.setOnLongClickListener(new View.OnLongClickListener() {
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
                                    holder.rep_image.setVisibility(View.GONE);

                                }
                            });
                            builder.setNegativeButton("Me", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    holder.rep_image.setVisibility(View.GONE);
                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();

                            return true;
                        }
                    });

                    holder.delete_one_message.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //        deleteSentMessage(position, holder);
                        }
                    });


                }

                else {
                    holder.rep_message_image.setVisibility(View.VISIBLE);
                    holder.rep_chat_image.setVisibility(View.VISIBLE);
                    holder.rep_image.setVisibility(View.VISIBLE);


                    try {
                        outputString = decrypt(chat.getMessage(), holder.password_null.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();

                    }


                    Picasso.get().load(chat.getPreMessage()).into(holder.rep_message_image);
                    holder.rep_chat_image.setText(outputString);


                    holder.rep_message_image.setOnLongClickListener(new View.OnLongClickListener() {
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
                                    dialog.dismiss();
                                    holder.rep_image.setVisibility(View.GONE);

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

                    holder.delete_one_message.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //      deleteSentMessage(position, holder);
                        }
                    });


                }

            }

            else if (fromMessageType.equals("reply_video")) {
                if (fromUserID.equals(messageSenderId)) {
                    holder.rep_message_video.setVisibility(View.VISIBLE);
                    holder.rep_chat_video.setVisibility(View.VISIBLE);
                    holder.rep_video.setVisibility(View.VISIBLE);

                    try {
                        outputString = decrypt(chat.getMessage(), holder.password_null.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    holder.rep_chat_video.setText(outputString);


                    long interval = 1000;
                    RequestOptions options = new RequestOptions().frame(interval);

                    Glide.with(MassageActivityInstagram.this).asBitmap().load(chat.getImagrUrl()).apply(options).into(holder.rep_message_video);

                    holder.rep_message_video.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(holder.itemView.getContext(), DirectVideo.class);
                            intent.putExtra("visit_user_id", chat.getReceiver());
                            intent.putExtra("messageId", mChat.get(position).getImagrUrl());
                            holder.itemView.getContext().startActivity(intent);

                        }
                    });

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
                            builder.setNeutralButton("Everyone", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    holder.rep_video.setVisibility(View.GONE);

                                }
                            });
                            builder.setNegativeButton("Me", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    holder.rep_video.setVisibility(View.GONE);
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

                    try {
                        outputString = decrypt(chat.getMessage(), holder.password_null.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    holder.rep_chat_video.setText(outputString);

                    long interval = 1000;
                    RequestOptions options = new RequestOptions().frame(interval);

                    Glide.with(MassageActivityInstagram.this).asBitmap().load(chat.getImagrUrl()).apply(options).into(holder.rep_message_video);


                    holder.rep_message_video.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(holder.itemView.getContext(), DirectVideo.class);
                            intent.putExtra("visit_user_id", chat.getReceiver());
                            intent.putExtra("messageId", mChat.get(position).getImagrUrl());
                            holder.itemView.getContext().startActivity(intent);

                        }
                    });


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
                                    dialog.dismiss();
                                    holder.rep_video.setVisibility(View.GONE);

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

                    holder.delete_one_message.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //      deleteSentMessage(position, holder);
                        }
                    });
                }

            }

            else if (fromMessageType.equals("reply_pdf")) {
                if (fromUserID.equals(messageSenderId)) {
                    holder.rep_message_pdf.setVisibility(View.VISIBLE);
                    holder.rep_chat_pdf.setVisibility(View.VISIBLE);
                    holder.rep_pdf.setVisibility(View.VISIBLE);
                    holder.rep_pdf_name.setVisibility(View.VISIBLE);
                    holder.rep_pdf_name.setText(chat.getPreMessage());

                    Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/watsapp-6d8e6.appspot.com/o/file.png?alt=media&token=da730f2c-5760-4d67-b45b-b8ff497039b6")
                            .into(holder.rep_message_pdf);


                    try {
                        outputString = decrypt(chat.getMessage(), holder.password_null.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();

                    }


                    holder.rep_chat_pdf.setText(outputString);

                    holder.rep_pdf.setOnLongClickListener(new View.OnLongClickListener() {
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
                                    dialog.dismiss();
                                    holder.rep_pdf.setVisibility(View.GONE);


                                }
                            });
                            builder.setNegativeButton("Download", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mChat.get(position).getPreMessage()));
                                    holder.itemView.getContext().startActivity(intent);
                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();

                            return true;
                        }
                    });

                    holder.delete_one_message.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //     deleteSentMessage(position, holder);
                        }
                    });

                    holder.download_pdf.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mChat.get(position).getPreMessage()));
                            holder.itemView.getContext().startActivity(intent);
                        }
                    });


                }

                else {
                    holder.rep_message_pdf.setVisibility(View.VISIBLE);
                    holder.rep_chat_pdf.setVisibility(View.VISIBLE);
                    holder.rep_pdf.setVisibility(View.VISIBLE);
                    holder.rep_pdf_name.setVisibility(View.VISIBLE);
                    holder.rep_pdf_name.setText(chat.getPreMessage());


                    Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/watsapp-6d8e6.appspot.com/o/file.png?alt=media&token=da730f2c-5760-4d67-b45b-b8ff497039b6")
                            .into(holder.rep_message_pdf);


                    try {
                        outputString = decrypt(chat.getMessage(), holder.password_null.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();

                    }


                    holder.rep_chat_pdf.setText(outputString);


                    holder.rep_pdf.setOnLongClickListener(new View.OnLongClickListener() {
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
                                    dialog.dismiss();
                                    holder.rep_pdf.setVisibility(View.GONE);


                                }
                            });
                            builder.setNegativeButton("Download", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mChat.get(position).getPreMessage()));
                                    holder.itemView.getContext().startActivity(intent);
                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();
                            return true;
                        }
                    });

                    holder.delete_one_message.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //      deleteSentMessage(position, holder);
                        }
                    });

                }

            } else {

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

        public void changeItem(long downloadid) {
            int i = 0;
            for (DownloadModel downloadModel : mChat) {
                if (downloadid == downloadModel.getDownloadId()) {
                    notifyItemChanged(i);
                }
                i++;
            }
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
    }

    private void requestPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(MassageActivityInstagram.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            Toast.makeText(MassageActivityInstagram.this, "Please Give Permission to Upload File", Toast.LENGTH_SHORT).show();
        }
        else{
            ActivityCompat.requestPermissions(MassageActivityInstagram.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
        }
    }

    private boolean checkPermission(){
        int result= ContextCompat.checkSelfPermission(MassageActivityInstagram.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
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
                    Toast.makeText(MassageActivityInstagram.this, "Permission Successfull", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(MassageActivityInstagram.this, "Permission Failed", Toast.LENGTH_SHORT).show();
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
        downloadModel.setSender(sender);
        downloadModel.setReceiver(postid);
        downloadModel.setMessageID(messageId);
        downloadModel.setIsseen("false");
        downloadModel.setLastSendMessage(lastSendMessage);
        downloadModel.setBio(bio);
        downloadModel.setDate(date);
        downloadModel.setPreMessage(preMessage);
        downloadModel.setTime(time);
        downloadModel.setType(type);
        downloadModel.setImagrUrl(imageurl);
        downloadModel.setAdminId(adminId);
        downloadModel.setUsername("no value");



        downloadModels.add(downloadModel);

        downloadAdapter.notifyDataSetChanged();
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

        final String messageSenderRef = "Chats/" + fuser.getUid() + "/" + postid;
        final String messageReceiverRef = "Chats/" + postid + "/" + fuser.getUid();


        final StorageReference filePath = storageReference.child(saveCurrentDate2 + saveCurrentTime2 + "." + "pdf");


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
                            messageTextBody.put("receiver", postid);
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

                                        DatabaseReference reference;
                                        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                                        reference.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                User user = dataSnapshot.getValue(User.class);
                                                if (notify) {
                                                    Toast.makeText(MassageActivityInstagram.this, "succes", Toast.LENGTH_SHORT).show();
                                                    sendNotifiaction(postid, user.getUsername(), cheker);


                                                }
                                                notify = false;
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    } else {

                                        Toast.makeText(MassageActivityInstagram.this, "Error", Toast.LENGTH_SHORT).show();
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
        downloadModel.setSender(sender);
        downloadModel.setReceiver(postid);
        downloadModel.setMessageID(messageId);
        downloadModel.setIsseen("false");
        downloadModel.setLastSendMessage(lastSendMessage);
        downloadModel.setBio(bio);
        downloadModel.setDate(date);
        downloadModel.setPreMessage(preMessage);
        downloadModel.setTime(time);
        downloadModel.setType("image");
        downloadModel.setImagrUrl(imageurl);
        downloadModel.setAdminId(adminId);
        downloadModel.setUsername("no value");



        downloadModels.add(downloadModel);


        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(downloadModel);


                downloadAdapter.notifyDataSetChanged();

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
        downloadModel.setSender(sender);
        downloadModel.setReceiver(postid);
        downloadModel.setMessageID(messageId);
        downloadModel.setIsseen("false");
        downloadModel.setLastSendMessage(lastSendMessage);
        downloadModel.setBio(bio);
        downloadModel.setDate(date);
        downloadModel.setPreMessage(preMessage);
        downloadModel.setTime(time);
        downloadModel.setType(type);
        downloadModel.setImagrUrl(imageurl);
        downloadModel.setAdminId(adminId);
        downloadModel.setUsername("no value");



        downloadModels.add(downloadModel);
        downloadAdapter.notifyDataSetChanged();
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

        final String messageSenderRef = "Chats/" + fuser.getUid() + "/" + postid;
        final String messageReceiverRef = "Chats/" + postid + "/" + fuser.getUid();


        final StorageReference filePath = storageReference.child(saveCurrentDate2 + saveCurrentTime2 + "." + "mp4");


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
                            messageTextBody.put("receiver", postid);
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

                                        DatabaseReference reference;
                                        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                                        reference.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                User user = dataSnapshot.getValue(User.class);
                                                if (notify) {
                                                    Toast.makeText(MassageActivityInstagram.this, "succes", Toast.LENGTH_SHORT).show();
                                                    sendNotifiaction(postid, user.getUsername(), cheker);

                                                }
                                                notify = false;
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    } else {

                                        Toast.makeText(MassageActivityInstagram.this, "Error", Toast.LENGTH_SHORT).show();
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
        downloadModel.setStatus("Completed");
        downloadModel.setTitle("hello");
        downloadModel.setFile_size("0");
        downloadModel.setProgress("0");
        downloadModel.setIs_paused(false);
        downloadModel.setDownloadId(8878);
        downloadModel.setFile_path("");
        downloadModel.setMessage(message);
        downloadModel.setSender(sender);
        downloadModel.setReceiver(postid);
        downloadModel.setMessageID(messageId);
        downloadModel.setIsseen(isseen);
        downloadModel.setLastSendMessage(lastSendMessage);
        downloadModel.setBio(bio);
        downloadModel.setDate(date);
        downloadModel.setPreMessage(preMessage);
        downloadModel.setTime(time);
        downloadModel.setType(type);
        downloadModel.setImagrUrl(imageurl);
        downloadModel.setAdminId(adminId);
        downloadModel.setUsername("no value");



        downloadModels.add(downloadModel);

        downloadAdapter.notifyDataSetChanged();
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



        Toast.makeText(MassageActivityInstagram.this, "kya hua", Toast.LENGTH_SHORT).show();
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

    private void addMediaFiles(String message, String sender, String receiver, String messageId, String bio, String type, String time, String date, String lastSendMessage, String preMessage, String isseen, String imageurl){
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
        recyclerView.smoothScrollToPosition(downloadModels.size()-1);


        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(downloadModel);


            }
        });
    }

    private void downloadFile(String message, String sender, String receiver, String messageId, String bio, String type, String time, String date, String lastSendMessage, String preMessage, String isseen, String imageurl) {
        String filename= URLUtil.guessFileName(message,null,null);
        File path= new File(Environment.getExternalStorageDirectory() + "/Study Chats/videos");

        String downloadPath = path.getAbsolutePath();

        Toast.makeText(MassageActivityInstagram.this, "kya hua", Toast.LENGTH_SHORT).show();
        File file=new File(downloadPath,filename);

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
        downloadModel.setMessageID(messageId);
        downloadModel.setIsseen("false");
        downloadModel.setLastSendMessage(lastSendMessage);
        downloadModel.setBio(bio);
        downloadModel.setDate(date);
        downloadModel.setPreMessage(preMessage);
        downloadModel.setTime(time);
        downloadModel.setType(type);
        downloadModel.setImagrUrl(imageurl);
        downloadModel.setAdminId(adminId);
        downloadModel.setUsername("no value");



        downloadModels.add(downloadModel);

        downloadAdapter.notifyDataSetChanged();
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
        downloadModel.setSender(sender);
        downloadModel.setReceiver(postid);
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

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(downloadModel);

                recyclerView.smoothScrollToPosition(downloadModels.size()-1);

            }
        });

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
            }
        }
    };

    public void runTask(DownloadStatusTask downloadStatusTask, String id){
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

    public final static String FOLDER = Environment.getExternalStorageDirectory() + "/PDF";

    private void saveImage(Bitmap bmp,long personId) {
        FileOutputStream out = null;
        try {
            File folder = new File(FOLDER);
            if(!folder.exists())
                folder.mkdirs();
            File file = new File(folder, "PDF.png");
            out = new FileOutputStream(file);

            String filePath = file.getAbsolutePath();

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Toast.makeText(MassageActivityInstagram.this, "id", Toast.LENGTH_SHORT).show();
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

}


