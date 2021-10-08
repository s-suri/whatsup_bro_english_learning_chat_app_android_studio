package com.some.notes.Audio.pickvideo.callback;

import java.util.List;

import com.some.notes.Audio.pickvideo.beans.BaseFile;
import com.some.notes.Audio.pickvideo.beans.Directory;

public interface FilterResultCallback<T extends BaseFile> {
    void onResult(List<Directory<T>> directories);
}
