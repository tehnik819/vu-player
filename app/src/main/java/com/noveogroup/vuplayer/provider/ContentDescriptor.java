package com.noveogroup.vuplayer.provider;

import android.content.ContentResolver;
import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

public final class ContentDescriptor {

    private static final String AUTHORITY = "com.noveogroup.vuplayer.provider";
    private static final Uri CONTENT_BASE_URI = new Uri.Builder()
            .scheme(ContentResolver.SCHEME_CONTENT).authority(AUTHORITY).build();

    private ContentDescriptor() {
        throw new UnsupportedOperationException();
    }

    public static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH) {
        {
            addURI(AUTHORITY, Notes.TABLE_NAME, Notes.ALL_URI_CODE);
            addURI(AUTHORITY, Notes.TABLE_NAME + "/#/", Notes.URI_CODE);
        }

        @Override
        public int match(Uri uri) {
            final int result = super.match(uri);
            if (result < 0) {
                throw new IllegalArgumentException("URI " + uri.toString() + " could not be matched.");
            } else {
                return result;
            }
        }

    };

    public static class Notes {
        public static final String TABLE_NAME = "notes";

        public static final Uri TABLE_URI = CONTENT_BASE_URI.buildUpon().appendPath(TABLE_NAME).build();
        public static final int ALL_URI_CODE = 0;
        public static final int URI_CODE = 1;

        public static class Cols {
            public static final String ID = BaseColumns._ID;
            public static final String WORD_COMBINATION = "combination";
            public static final String SOURCE = "source";
            public static final String COMMENT = "comment";
        }
    }

    public static String getTableName(int uriCode) {
        switch (uriCode) {
            case Notes.ALL_URI_CODE:
            case Notes.URI_CODE:
                return Notes.TABLE_NAME;
        }
        throw new IllegalArgumentException("uriCode " + uriCode);
    }
}