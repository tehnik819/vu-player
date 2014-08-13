package com.noveogroup.vuplayer;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;

import com.noveogroup.vuplayer.fragments.NotesFragment;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCaptchaDialog;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKAttachments;
import com.vk.sdk.api.model.VKPhotoArray;
import com.vk.sdk.api.model.VKWallPostResult;
import com.vk.sdk.api.photo.VKImageParameters;
import com.vk.sdk.api.photo.VKUploadImage;

import org.json.JSONException;

public class LoginActivity extends FragmentActivity {

    private static final String[] sMyScope = new String[] {
            VKScope.FRIENDS,
            VKScope.WALL,
            VKScope.PHOTOS,
            VKScope.NOHTTPS
    };

    private String ID;
    private boolean isErrorGetId = false;
    private static final String TAG = "LoginActivity";

    private String[] postStrings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        VKUIHelper.onCreate(this);
        VKSdk.initialize(sdkListener, "4502434");
        postStrings = getIntent().getStringArrayExtra(NotesFragment.EXTRA_POST_STRINGS);
        VKSdk.authorize(sMyScope, true, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        VKUIHelper.onResume(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VKUIHelper.onDestroy(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        VKUIHelper.onActivityResult(this, requestCode, resultCode, data);
        finish();
    }

    private final VKSdkListener sdkListener = new VKSdkListener() {
        @Override
        public void onCaptchaError(VKError captchaError) {
            new VKCaptchaDialog(captchaError).show();
        }

        @Override
        public void onTokenExpired(VKAccessToken expiredToken) {
            VKSdk.authorize(sMyScope);
        }

        @Override
        public void onAccessDenied(final VKError authorizationError) {
            new AlertDialog.Builder(VKUIHelper.getTopActivity())
                    .setMessage(authorizationError.toString())
                    .show();
        }

        @Override
        public void onReceiveNewToken(VKAccessToken newToken) {
            requestUserId();
            //postToWall();
        }

        @Override
        public void onAcceptUserToken(VKAccessToken token) {
            requestUserId();
            //postToWall();
        }
    };

    private void postToWall() {
        final String post;
        if(!TextUtils.isEmpty(postStrings[1])) {
            post = "Встретил классную фразу " + postStrings[0]
                    + " в фильме " + postStrings[1] + ". " + postStrings[2] + ".\nVuPlayer - What do you vu today? ©";
        }
        else {
            post = "Встретил классную фразу " + postStrings[0] + ". " + postStrings[2] + ".\nVuPlayer - What do you vu today? ©";
        }
        final Bitmap photo = BitmapFactory.decodeResource(getResources(), R.drawable.post_image);
        VKRequest request = VKApi.uploadWallPhotoRequest(new VKUploadImage(photo, VKImageParameters.jpgImage(0.9f)), 0, Integer.valueOf(ID));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                photo.recycle();
                VKApiPhoto photoModel = ((VKPhotoArray) response.parsedModel).get(0);
                makePost(new VKAttachments(photoModel), post);
            }
            @Override
            public void onError(VKError error) {
                showError(error);
            }
        });
    }

    private void makePost(VKAttachments attachments, String message) {
        VKRequest post;
        if(isErrorGetId) {
            post = VKApi.wall().post(VKParameters.from(VKApiConst.OWNER_ID, "-" + ID, VKApiConst.ATTACHMENTS, attachments, VKApiConst.MESSAGE, message));
        }
        else {
            post = VKApi.wall().post(VKParameters.from(VKApiConst.OWNER_ID, ID, VKApiConst.ATTACHMENTS, attachments, VKApiConst.MESSAGE, message));
        }
        post.setModelClass(VKWallPostResult.class);
        post.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                VKWallPostResult result = (VKWallPostResult)response.parsedModel;
                Intent i;
                if(isErrorGetId) {
                    i = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("https://vk.com/wall-" + ID, result.post_id)));
                }
                else {
                    i = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("https://vk.com/wall" + ID, result.post_id)));
                }
                startActivity(i);
            }

            @Override
            public void onError(VKError error) {
                showError(error.apiError != null ? error.apiError : error);
            }
        });
        VKSdk.logout();
    }

    private void showError(VKError error) {
        new AlertDialog.Builder(this)
                .setMessage(error.errorMessage)
                .setPositiveButton("OK", null)
                .show();

        if (error.httpError != null) {
            Log.w("Test", "Error in request or upload", error.httpError);
        }
    }

    private void requestUserId() {
        VKRequest requestId = VKApi.users().get(VKParameters.from(VKApiConst.FIELDS, "id"));
        requestId.secure = false;
        requestId.useSystemLanguage = false;
        requestId.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                setUserId(response);
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                isErrorGetId = true;
                ID = "60479154";
            }
        });
    }

    private void setUserId(VKResponse response) {
        String json;
        try {
            json = response.json.getJSONArray("response").getJSONObject(0).getString("id");
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            isErrorGetId = true;
            json = "60479154";
        }
        ID = json;
        postToWall();
    }
}