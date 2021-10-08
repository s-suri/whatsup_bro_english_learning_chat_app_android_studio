package com.some.notes.PDF.pickvideo;

import androidx.fragment.app.FragmentActivity;

import com.some.notes.Audio.pickvideo.beans.AudioFile;
import com.some.notes.Audio.pickvideo.beans.BaseFile;
import com.some.notes.Audio.pickvideo.beans.ImageFile;
import com.some.notes.Audio.pickvideo.beans.NormalFile;
import com.some.notes.Audio.pickvideo.beans.PdfFile;
import com.some.notes.Audio.pickvideo.beans.VideoFile;
import com.some.notes.Audio.pickvideo.callback.FileLoaderCallbacks;
import com.some.notes.Audio.pickvideo.callback.FilterResultCallback;

public class FileFilter {

    public static void getVideos(FragmentActivity activity, FilterResultCallback<PdfFile> callback){
        activity.getSupportLoaderManager().initLoader(1, null,
                new FileLoaderCallbacks(activity, callback, FileLoaderCallbacks.TYPE_PDF));
    }

}