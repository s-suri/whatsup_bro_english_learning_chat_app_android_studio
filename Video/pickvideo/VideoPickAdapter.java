package com.some.notes.Video.pickvideo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.some.notes.Audio.pickvideo.BaseAdapter;
import com.some.notes.R;
import com.some.notes.Audio.pickvideo.beans.AudioFile;
import com.some.notes.Audio.pickvideo.beans.BaseFile;
import com.some.notes.Audio.pickvideo.beans.ImageFile;
import com.some.notes.Audio.pickvideo.beans.NormalFile;
import com.some.notes.Audio.pickvideo.beans.VideoFile;

import java.util.ArrayList;
//
//public class VideoPickAdapter extends BaseAdapter<VideoFile, VideoPickAdapter.VideoPickViewHolder> {
//    public String mVideoPath;
//    private Context mContext;
//
//    public VideoPickAdapter(Context ctx) {
//        this(ctx, new ArrayList<VideoFile>());
//    }
//
//    public VideoPickAdapter(Context ctx, ArrayList<VideoFile> list) {
//        super(ctx, list);
//        mContext = ctx;
//    }
//
//    @Override
//    public VideoPickViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View itemView = LayoutInflater.from(mContext).inflate(R.layout.vw_layout_item_video_pick, parent, false);
//        ViewGroup.LayoutParams params = itemView.getLayoutParams();
//        if (params != null) {
//            WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
//            int width = wm.getDefaultDisplay().getWidth();
//            params.height = width / VideoPickActivity.COLUMN_NUMBER;
//        }
//        return new VideoPickViewHolder(itemView);
//    }
//
//    @Override
//    public void onBindViewHolder(final VideoPickViewHolder holder, int position) {
//
//        holder.mIvCamera.setVisibility(View.INVISIBLE);
//        holder.mIvThumbnail.setVisibility(View.VISIBLE);
//        holder.mDurationLayout.setVisibility(View.GONE);
//        holder.song_relative.setVisibility(View.GONE);
//        holder.thumbnails_relative.setVisibility(View.VISIBLE);
//        holder.mDuration.setVisibility(View.GONE);
//
//        final VideoFile file;
//
//                file = mList.get(position);
//
////            Glide.with(mContext)
////                    .load(file.getPath())
////                    .into(holder.mIvThumbnail);
//
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                                            Toast.makeText(mContext, file.getPath()+"", Toast.LENGTH_LONG).show();
//
//
////                    int index = holder.getAdapterPosition();
////                    mList.get(index).setSelected(true);
////
////                    if (mListener != null) {
////                        mListener.OnSelectStateChanged(mList.get(index));
////
////                    }
//                }
//            });
//
//            holder.mDuration.setText(file.getName());
//    }
//
//    @Override
//    public int getItemCount() {
//        return mList.size();
//    }
//
//    class VideoPickViewHolder extends RecyclerView.ViewHolder {
//        private ImageView mIvCamera;
//        private ImageView mIvThumbnail;
//        private View mShadow;
//        private TextView mDuration;
//        private RelativeLayout mDurationLayout,song_relative,thumbnails_relative;
//
//        public VideoPickViewHolder(View itemView) {
//            super(itemView);
//            song_relative = itemView.findViewById(R.id.music_relative);
//            thumbnails_relative = itemView.findViewById(R.id.thumbnail_relative);
//            mIvCamera = (ImageView) itemView.findViewById(R.id.iv_camera);
//            mIvThumbnail = (ImageView) itemView.findViewById(R.id.iv_thumbnail);
//            mShadow = itemView.findViewById(R.id.shadow);
//            mDuration = (TextView) itemView.findViewById(R.id.txt_duration);
//            mDurationLayout = (RelativeLayout) itemView.findViewById(R.id.layout_duration);
//        }
//    }
//
//
//}
