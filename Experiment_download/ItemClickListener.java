package com.some.notes.Experiment_download;

import com.some.notes.Model.DownloadModel;

public interface ItemClickListener {
    void onCLickItem(String file_path);
    void onShareClick(DownloadModel downloadModel);
}
