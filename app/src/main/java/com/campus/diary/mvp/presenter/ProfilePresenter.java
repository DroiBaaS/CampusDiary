package com.campus.diary.mvp.presenter;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import com.campus.diary.R;
import com.campus.diary.model.User;
import com.campus.diary.mvp.contract.ProfileContract;
import com.droi.sdk.DroiError;
import com.droi.sdk.core.DroiFile;
import com.droi.sdk.core.DroiPermission;
import com.droi.sdk.core.DroiUser;

import java.io.ByteArrayOutputStream;
import java.io.File;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;

import static android.app.Activity.RESULT_OK;

public class ProfilePresenter implements ProfileContract.Presenter {

    private final static String TAG = "ProfilePresenter";
    private ProfileContract.View view;

    public ProfilePresenter(ProfileContract.View view) {
        this.view = view;
    }

    @Override
    public void uploadHeadIcon(Context context, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            view.showToast(context.getString(R.string.get_photo_failed));
            return;
        }
        view.showLoading(context.getString(R.string.changing_avatar));
        if (data != null) {
            upload(context, data);
        } else {
            view.showToast(context.getString(R.string.upload_failed));
        }
    }

    @Override
    public void logout() {
        DroiError droiError;
        User user = DroiUser.getCurrentUser(User.class);
        if (user != null && user.isAuthorized() && !user.isAnonymous()) {
            droiError = user.logout();
            if (droiError.isOk()) {
                view.showToastByResID(R.string.logout_success);
                view.finishActivity();
            } else {
                Log.i(TAG, "error:" + droiError);
                view.showToastByResID(R.string.logout_failed);
            }
        }
    }

    @Override
    public void getHeadIcon() {
        view.refreshProfile();
    }

    @Override
    public void updateNickname(final String name) {
        view.showLoading("修改昵称中...");
        Observable.create(new Observable.OnSubscribe<DroiError>() {
            @Override
            public void call(Subscriber<? super DroiError> subscriber) {
                User user = DroiUser.getCurrentUser(User.class);
                user.setNickName(name);
                DroiError droiError = user.save();
                subscriber.onNext(droiError);
                subscriber.onCompleted();
            }
        }).subscribe(new Observer<DroiError>() {
            @Override
            public void onNext(DroiError droiError) {
                if (view == null) {
                    return;
                }
                if (droiError.isOk()) {
                    view.refreshNickname(name);
                    view.showToast("修改成功!");
                } else {
                    view.showToast("修改失败");
                }
            }

            @Override
            public void onCompleted() {
                if (view == null) {
                    return;
                }
                view.hideLoading();
            }

            @Override
            public void onError(Throwable e) {
                if (view == null) {
                    return;
                }
                Log.i(TAG, e.toString());
                view.showToast(view.getResString(R.string.error));
            }
        });
    }

    private void upload(Context context, Intent data) {
        Uri mImageCaptureUri = data.getData();
        if (mImageCaptureUri != null) {
            Bitmap image;
            try {
                image = MediaStore.Images.Media.getBitmap(context.getContentResolver(), mImageCaptureUri);
                if (image != null) {
                    String path = getPath(context, mImageCaptureUri);
                    DroiFile headIcon = new DroiFile(new File(path));
                    updateUserHeadIcon(headIcon);
                } else {
                    view.showToast(context.getString(R.string.upload_failed));
                }
            } catch (Exception e) {
                e.printStackTrace();
                view.showToast(context.getString(R.string.upload_failed));
            }
        } else {
            Bundle extras = data.getExtras();
            if (extras != null) {
                //这里是有些拍照后的图片是直接存放到Bundle中的所以我们可以从这里面获取Bitmap图片
                Bitmap image = extras.getParcelable("data");
                if (image != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] imageBytes = baos.toByteArray();
                    DroiFile headIcon = new DroiFile(imageBytes);
                    updateUserHeadIcon(headIcon);
                } else {
                    view.showToast(context.getString(R.string.upload_failed));
                }
            } else {
                view.showToast(context.getString(R.string.upload_failed));
            }
        }
    }

    public void updateUserHeadIcon(final DroiFile file) {
        Observable.create(new Observable.OnSubscribe<DroiError>() {
            @Override
            public void call(Subscriber<? super DroiError> subscriber) {
                DroiPermission permission = new DroiPermission();
                permission.setPublicReadPermission(true);
                permission.setPublicWritePermission(false);
                file.setPermission(permission);
                User user = DroiUser.getCurrentUser(User.class);
                user.setHeadIcon(file);
                DroiError droiError = user.save();
                subscriber.onNext(droiError);
                subscriber.onCompleted();
            }
        }).subscribe(new Observer<DroiError>() {
            @Override
            public void onNext(DroiError droiError) {
                if (view == null) {
                    return;
                }
                if (droiError.isOk()) {
                    view.showToast(view.getResString(R.string.upload_success));
                    getHeadIcon();
                } else {
                    view.showToast(view.getResString(R.string.upload_failed));
                }
            }

            @Override
            public void onCompleted() {
                if (view == null) {
                    return;
                }
                view.hideLoading();
            }

            @Override
            public void onError(Throwable e) {
                if (view == null) {
                    return;
                }
                Log.i(TAG, e.toString());
                view.showToast(view.getResString(R.string.error));
            }
        });
    }

    private String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private String getDataColumn(Context context, Uri uri, String selection,
                                 String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
