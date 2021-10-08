package com.some.notes.Audio.pickvideo;

import androidx.fragment.app.FragmentActivity;

import com.some.notes.Audio.pickvideo.beans.AudioFile;
import com.some.notes.Audio.pickvideo.beans.BaseFile;
import com.some.notes.Audio.pickvideo.beans.ImageFile;
import com.some.notes.Audio.pickvideo.beans.NormalFile;
import com.some.notes.Audio.pickvideo.beans.VideoFile;
import com.some.notes.Audio.pickvideo.callback.FileLoaderCallbacks;
import com.some.notes.Audio.pickvideo.callback.FilterResultCallback;

public class FileFilter {

    public static void getVideos(FragmentActivity activity, FilterResultCallback<AudioFile> callback){
        activity.getSupportLoaderManager().initLoader(1, null,
                new FileLoaderCallbacks(activity, callback, FileLoaderCallbacks.TYPE_AUDIO));
    }

}