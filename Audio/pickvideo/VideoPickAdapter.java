package com.some.notes.Audio.pickvideo;

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
import com.some.notes.Adapter.ChatsAdapter;
import com.some.notes.R;
import com.some.notes.Audio.pickvideo.beans.AudioFile;
import com.some.notes.Audio.pickvideo.beans.BaseFile;
import com.some.notes.Audio.pickvideo.beans.ImageFile;
import com.some.notes.Audio.pickvideo.beans.NormalFile;
import com.some.notes.Audio.pickvideo.beans.VideoFile;

import java.util.ArrayList;

public class VideoPickAdapter extends BaseAdapter<AudioFile, VideoPickAdapter.VideoPickViewHolder> {
    public String mVideoPath;
    private Context mContext;

    public VideoPickAdapter(Context ctx) {
        this(ctx, new ArrayList<AudioFile>());
    }

    public VideoPickAdapter(Context ctx, ArrayList<AudioFile> list) {
        super(ctx, list);
        mContext = ctx;
    }

    @Override
    public VideoPickViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

//        View itemView = LayoutInflater.from(mContext).inflate(R.layout.vw_layout_item_video_pick, parent, false);
//        ViewGroup.LayoutParams params = itemView.getLayoutParams();
//        if (params != null) {
//            WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
//            int width = wm.getDefaultDisplay().getWidth();
//            params.height = width / VideoPickActivity.COLUMN_NUMBER;
//        }
//        return new VideoPickViewHolder(itemView);


        View view = LayoutInflater.from(mContext).inflate(R.layout.vw_layout_item_video_pick,parent,false);
        VideoPickViewHolder viewHolder = new VideoPickViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final VideoPickViewHolder holder, int position) {


            holder.image_relative.setVisibility(View.GONE);
            holder.music_relative.setVisibility(View.VISIBLE);

            final AudioFile file;

                file = mList.get(position);


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(mContext, file.getPath()+"", Toast.LENGTH_LONG).show();

                }
            });

            holder.song_title.setText(file.getName());
        holder.song_duration.setText(Util.getDurationString(file.getDuration()));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class VideoPickViewHolder extends RecyclerView.ViewHolder {
        private ImageView mIvCamera;
        private ImageView mIvThumbnail;
        private View mShadow;
        private TextView mDuration,song_title,song_duration;
        private RelativeLayout mDurationLayout,music_relative,image_relative;


        public VideoPickViewHolder(View itemView) {
            super(itemView);
            song_title = itemView.findViewById(R.id.song_title);
            song_duration = itemView.findViewById(R.id.song_duration);
            music_relative = itemView.findViewById(R.id.music_relative);
            image_relative = itemView.findViewById(R.id.thumbnail_relative);
            mIvCamera = (ImageView) itemView.findViewById(R.id.iv_camera);
            mIvThumbnail = (ImageView) itemView.findViewById(R.id.iv_thumbnail);
            mShadow = itemView.findViewById(R.id.shadow);
            mDuration = (TextView) itemView.findViewById(R.id.txt_duration);
            mDurationLayout = (RelativeLayout) itemView.findViewById(R.id.layout_duration);
        }
    }


}
