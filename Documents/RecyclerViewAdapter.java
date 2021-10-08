package com.some.notes.Documents;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.some.notes.Images.pickvideo.ImagesPickActivity;
import com.some.notes.R;

import java.util.HashMap;
import java.util.Map;
//
//public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//
//    private Context mContext;
//
//    RecyclerViewAdapter(Context mContext){
//        this.mContext = mContext;
//    }
//
//    @NonNull
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.files_list,parent,false);
//        return new FileLayoutHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//        ((FileLayoutHolder)holder).videoTitle.setText(Constant.allMediaList.get(position).getName());
//        //we will load thumbnail using glid library
//        Uri uri = Uri.fromFile(Constant.allMediaList.get(position));
//
//        Glide.with(mContext)
//                .load(uri).thumbnail(0.1f).into(((FileLayoutHolder)holder).thumbnail);
//
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                                    uploadTask.continueWithTask(new Continuation() {
//                        @Override
//                        public Object then(@NonNull Task task) throws Exception {
//
//                            if (!task.isSuccessful()) {
//                                throw task.getException();
//
//
//                            }
//                            return filePath.getDownloadUrl();
//                        }
//                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Uri> task) {
//                            if (task.isSuccessful()) {
//                                Toast.makeText(ImagesPickActivity.this, "url load", Toast.LENGTH_SHORT).show();
//
//                                Uri downloadUrl = task.getResult();
//                                myUrl = downloadUrl.toString();
//
//
//                                Map messageTextBody = new HashMap();
//                                messageTextBody.put("message", myUrl);
//                                messageTextBody.put("url", myUrl);
//                                messageTextBody.put("name", compressUri.getLastPathSegment());
//                                messageTextBody.put("type", "image");
//                                messageTextBody.put("sender", fuser.getUid());
//                                messageTextBody.put("receiver", userid);
//                                messageTextBody.put("messageID", saveCurrentDate1 + saveCurrentTime1);
//                                messageTextBody.put("time", saveCurrentTime);
//                                messageTextBody.put("date", saveCurrentDate);
//                                messageTextBody.put("title", "sure");
//                                messageTextBody.put("bio", "sure");
//                                messageTextBody.put("isseen", "false");
//                                messageTextBody.put(sqlId, true);
//                                messageTextBody.put("last", "false");
//                                messageTextBody.put("preMessage", "hello");
//                                messageTextBody.put("lastSendMessage", "kt5rg2/r78I081y/kXkhw==\n");
//
//
//                                Map messageBodyDetails = new HashMap();
//
//                                messageBodyDetails.put(messageSenderRef + "/" + saveCurrentDate1 + saveCurrentTime1, messageTextBody);
//                                messageBodyDetails.put(messageReceiverRef + "/" + saveCurrentDate1 + saveCurrentTime1, messageTextBody);
//
//                                HashMap<String, Object> map = new HashMap<>();
//                                map.put("time", saveCurrentDate1 + "-" + saveCurrentTime1);
//
//
//                                RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
//                                    @Override
//                                    public void onComplete(@NonNull Task task) {
//                                        if (task.isSuccessful()) {
//                                            Toast.makeText(ImagesPickActivity.this, "he succes", Toast.LENGTH_LONG).show();
//
//                                            DatabaseReference reference;
//                                            reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
//                                            reference.addValueEventListener(new ValueEventListener() {
//                                                @Override
//                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                                    User user = dataSnapshot.getValue(User.class);
//
//                                                        sendNotifiaction(userid, user.getUsername(), cheker);
//
//                                                }
//
//                                                @Override
//                                                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                                }
//                                            });
//                                        } else {
//
//                                            Toast.makeText(ImagesPickActivity.this, "Error", Toast.LENGTH_SHORT).show();
//                                        }
//
//
//                                    }
//                                });
//                            }
//                        }
//                    });
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return Constant.allMediaList.size();
//    }
//
//    class FileLayoutHolder extends RecyclerView.ViewHolder{
//
//        ImageView thumbnail;
//        TextView videoTitle;
//        ImageView ic_more_btn;
//
//        public FileLayoutHolder(@NonNull View itemView) {
//            super(itemView);
//
//            thumbnail = itemView.findViewById(R.id.thumbnail);
//            videoTitle = itemView.findViewById(R.id.videotitle);
//            ic_more_btn = itemView.findViewById(R.id.ic_more_btn);
//
//        }
//    }
//
//}
