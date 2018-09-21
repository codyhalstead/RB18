package com.rentbud.helpers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.cody.rentbud.BuildConfig;
import com.example.cody.rentbud.R;
import com.rentbud.activities.MainActivity;
import com.rentbud.activities.SettingsActivity;
import com.rentbud.sqlite.DatabaseHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AppFileManagementHelper {

    public static File copyPictureFileToApp(String photoToCopy, @Nullable String oldPhotoToDelete) {
        File f = new File(Environment.getExternalStorageDirectory(), "Rentbud");
        if (!f.exists()) {
            f.mkdirs();
        }
        File photos = new File(f.getAbsolutePath() + "/", "Photos");
        if (!photos.exists()) {
            photos.mkdirs();
        }
        File usersPhotos = new File(photos.getAbsolutePath() + "/", MainActivity.user.getName());
        if (!usersPhotos.exists()) {
            usersPhotos.mkdirs();
        }
        String fileName = photoToCopy.substring(photoToCopy.lastIndexOf('/') + 1);
        File photo = new File(photoToCopy);
        File newPhotoCopy = new File(usersPhotos + "/" + fileName);
        if (newPhotoCopy.exists()) {
            newPhotoCopy = makeDuplicateFileNameUnique(usersPhotos + "/", fileName);
        }
        try {
            if (photo.exists()) {
                FileChannel src = new FileInputStream(photo).getChannel();
                FileChannel dst = new FileOutputStream(newPhotoCopy).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (newPhotoCopy.exists()) {
            if (oldPhotoToDelete != null) {
                File oldPic = new File(oldPhotoToDelete);
                if (oldPic.exists()) {
                    oldPic.delete();
                }
            }
            return newPhotoCopy;
        }
        return null;
    }

    private static File makeDuplicateFileNameUnique(String path, String filename) {
        File file1 = new File(path, "x" + filename);
        if (file1.exists()) {
            return makeDuplicateFileNameUnique(path, "x" + filename);
        } else {
            return file1;
        }
    }

    public static File createImageFileFromCamera() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File f = new File(Environment.getExternalStorageDirectory(), "Rentbud");
        if (!f.exists()) {
            f.mkdirs();
        }
        File photos = new File(f.getAbsolutePath() + "/", "Photos");
        if (!photos.exists()) {
            photos.mkdirs();
        }
        File usersPhotos = new File(photos.getAbsolutePath() + "/", MainActivity.user.getName() + "/");
        if (!usersPhotos.exists()) {
            usersPhotos.mkdirs();
        }
        Log.d("TAG", "createImageFile: " + usersPhotos.exists());
        File image = new File(usersPhotos + "/" + imageFileName + timeStamp + ".jpg");
        return image;
    }


    public static boolean isSDCARDAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static File copyDBToExternal(Context context) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            //File data = Environment.getDataDirectory();
            if (sd.canWrite()) {
                File f = new File(Environment.getExternalStorageDirectory(), "Rentbud");
                if (!f.exists()) {
                    f.mkdirs();
                }
                File backups = new File(f.getAbsolutePath() + "/", "Backups");
                if (!backups.exists()) {
                    backups.mkdirs();
                }
                Date today = Calendar.getInstance().getTime();
                StringBuilder stringBuilder = new StringBuilder("RentbudBackup_");
                SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy-hh:mmaa", Locale.US);
                stringBuilder.append(formatter.format(today));
                stringBuilder.append(".db");
                String currentDBPath = context.getDatabasePath(DatabaseHandler.DB_FILE_NAME).getAbsolutePath();
                String backupDBPath = stringBuilder.toString();
                File currentDB = new File(currentDBPath);
                File backupDB = new File(backups, backupDBPath);
                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
                return backupDB;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String SHA512Hash(String stringToHash) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
        }
        if (md != null) {
            byte[] digest = md.digest(stringToHash.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < digest.length; i++) {
                sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        }
        return null;
    }
}
