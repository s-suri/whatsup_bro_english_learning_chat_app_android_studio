package com.some.notes.Audio.pickvideo.loader;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.loader.content.CursorLoader;

import static android.provider.MediaStore.MediaColumns.MIME_TYPE;

    public class PdfLoader extends CursorLoader {
        private static final String[] AUDIO_PROJECTION = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.DATE_ADDED,
                MediaStore.Audio.Media.DURATION

        };

        private PdfLoader(Context context, Uri uri, String[] projection, String selection,
                            String[] selectionArgs, String sortOrder) {
            super(context, uri, projection, selection, selectionArgs, sortOrder);
        }

        public PdfLoader(Context context) {
            super(context);

            setProjection(AUDIO_PROJECTION);
            setUri(MediaStore.Files.getContentUri("external"));
            setSortOrder(MediaStore.Audio.Media.DATE_ADDED + " ASC");

            setSelection(MIME_TYPE + "=? or "
                    + MIME_TYPE + "=? or "
//                + MIME_TYPE + "=? or "
                    + MIME_TYPE + "=?");
            String[] selectionArgs;
            //     selectionArgs = new String[]{"audio/mpeg", "audio/mp3", "audio/x-ms-wma"};
            selectionArgs = new String[]{"application/pdf"};

            setSelectionArgs(selectionArgs);
        }
    }


