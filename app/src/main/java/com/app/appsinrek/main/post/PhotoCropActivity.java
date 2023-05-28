package com.app.appsinrek.main.post;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.app.appsinrek.R;
import com.app.appsinrek.databinding.PhotoCropActivityBinding;
import com.sinrek.cropview.CropImageView;
import com.sinrek.cropview.callback.CropCallback;
import com.sinrek.cropview.callback.LoadCallback;
import com.sinrek.cropview.callback.SaveCallback;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PhotoCropActivity extends AppCompatActivity {

    private static final int REQUEST_PICK_IMAGE = 10011;
    private static final int REQUEST_SAVE_IMAGE = 10012;
    private static final String PROGRESS_DIALOG = "ProgressDialog";
    private static final String KEY_FRAME_RECT = "FrameRect";
    private static final String KEY_SOURCE_URI = "SourceUri";
    private SimpleDateFormat dateFormatter;
    private File file;
    private File sourceFile;
    private File destFile;

    PhotoCropActivityBinding bi;
    private Bitmap.CompressFormat mCompressFormat = Bitmap.CompressFormat.PNG;
    private RectF mFrameRect = null;
    private Uri mSourceUri = null;
    Activity mActivity;

    @SuppressLint({"ResourceAsColor", "WrongThread"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = PhotoCropActivityBinding.inflate(getLayoutInflater());
        setContentView(bi.getRoot());
        mActivity = this;
        String path = getIntent().getStringExtra("path");
        bi.cropImageView.setCustomRatio(1080, 1080);
//        final Bitmap toBeCropped = BitmapFactory.decodeFile(path);
//        final BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
//        bitmapOptions.inTargetDensity = 1;
//        toBeCropped.setDensity(Bitmap.DENSITY_NONE);
//        int fromHere = (int) (toBeCropped.getHeight() * 0.2);
//        Bitmap croppedBitmap =  Bitmap.createBitmap(toBeCropped, 0, (int) (toBeCropped.getHeight() * 0.8), toBeCropped.getWidth(), fromHere);
//        bi.imageView.setImageBitmap(croppedBitmap);
        if (savedInstanceState != null) {
            mFrameRect = savedInstanceState.getParcelable(KEY_FRAME_RECT);
            mSourceUri = savedInstanceState.getParcelable(KEY_SOURCE_URI);
        }
        if (mSourceUri == null) {
            mSourceUri = Uri.fromFile(new File(path));
        }

        bi.cropImageView.setGuideShowMode(CropImageView.ShowMode.NOT_SHOW);
        bi.cropImageView.load(mSourceUri)
                .initialFrameRect(mFrameRect)
                .execute(mLoadCallback);
        bi.s1.setOnClickListener(view -> {
            bi.s1.setTextColor(ContextCompat.getColor(this, R.color.black));
            bi.s2.setTextColor(ContextCompat.getColor(this, R.color.grey));
            bi.s3.setTextColor(ContextCompat.getColor(this, R.color.grey));
            bi.s1.setTypeface(bi.s1.getTypeface(), Typeface.BOLD);
            bi.s2.setTypeface(bi.s2.getTypeface(), Typeface.NORMAL);
            bi.s3.setTypeface(bi.s3.getTypeface(), Typeface.NORMAL);
            bi.cropImageView.setCustomRatio(1080, 1080);

        });
        bi.s2.setOnClickListener(view -> {
            bi.s1.setTextColor(ContextCompat.getColor(this, R.color.grey));
            bi.s2.setTextColor(ContextCompat.getColor(this, R.color.black));
            bi.s3.setTextColor(ContextCompat.getColor(this, R.color.grey));
            bi.s1.setTypeface(bi.s1.getTypeface(), Typeface.NORMAL);
            bi.s2.setTypeface(bi.s2.getTypeface(), Typeface.BOLD);
            bi.s3.setTypeface(bi.s3.getTypeface(), Typeface.NORMAL);
            bi.cropImageView.setCustomRatio(1080, 1350);

        });
        bi.s3.setOnClickListener(view -> {
            bi.s1.setTextColor(ContextCompat.getColor(this, R.color.grey));
            bi.s2.setTextColor(ContextCompat.getColor(this, R.color.grey));
            bi.s3.setTextColor(ContextCompat.getColor(this, R.color.black));
            bi.s1.setTypeface(bi.s1.getTypeface(), Typeface.NORMAL);
            bi.s2.setTypeface(bi.s2.getTypeface(), Typeface.NORMAL);
            bi.s3.setTypeface(bi.s3.getTypeface(), Typeface.BOLD);
            bi.cropImageView.setCustomRatio(1080, 680);
        });
        bi.saveCrop.setOnClickListener(view -> {
            bi.cropImageView.crop(mSourceUri)
                    .execute(new CropCallback() {
                        @Override
                        public void onSuccess(Bitmap cropped) {
                            Log.d("PPP", "oooo");
                            bi.cropImageView.save(cropped)
                                    .execute(createSaveUri(), mSaveCallback);
                        }

                        @Override
                        public void onError(Throwable e) {
                        }
                    });
        });
    }

//    protected Bitmap resize(File f) {
//        Bitmap b = null;
//        //Decode image size
//        BitmapFactory.Options o = new BitmapFactory.Options();
//        o.inJustDecodeBounds = true;
//        FileInputStream fis = null;
//        try {
//            fis = new FileInputStream(f);
//            BitmapFactory.decodeStream(fis, null, o);
//            fis.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        int IMAGE_MAX_SIZE = 1024;
//        int scale = 1;
//        if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
//            scale = (int) Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
//        }
//
//        //Decode with inSampleSize
//        BitmapFactory.Options o2 = new BitmapFactory.Options();
//        o2.inSampleSize = scale;
//        try {
//            fis = new FileInputStream(f);
//            b = BitmapFactory.decodeStream(fis, null, o2);
//            fis.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        destFile = new File(file, "img_" + dateFormatter.format(new Date()).toString() + ".png");
//        try {
//            FileOutputStream out = new FileOutputStream(destFile);
//            b.compress(Bitmap.CompressFormat.PNG, 100, out);
//            out.flush();
//            out.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return b;
//    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save data
        outState.putParcelable(KEY_FRAME_RECT, bi.cropImageView.getActualCropRect());
        outState.putParcelable(KEY_SOURCE_URI, bi.cropImageView.getSourceUri());
    }

    public static Uri getUriFromDrawableResId(Context context, int drawableResId) {
        StringBuilder builder = new StringBuilder().append(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .append("://")
                .append(context.getResources().getResourcePackageName(drawableResId))
                .append("/")
                .append(context.getResources().getResourceTypeName(drawableResId))
                .append("/")
                .append(context.getResources().getResourceEntryName(drawableResId));
        return Uri.parse(builder.toString());
    }

    public Uri createSaveUri() {
        return Uri.fromFile(new File(getTempDirectoryPath() + "/" + System.currentTimeMillis() + "-cropped." + getMimeType(mCompressFormat)));
        //createNewUri(this, mCompressFormat);
    }

    public static Uri createNewUri(Context context, Bitmap.CompressFormat format) {
        long currentTimeMillis = System.currentTimeMillis();
        Date today = new Date(currentTimeMillis);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String title = dateFormat.format(today);
        String dirPath = getDirPath();
        String fileName = "scv" + title + "." + getMimeType(format);
        String path = dirPath + "/" + fileName;
        File file = new File(path);
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/" + getMimeType(format));
        values.put(MediaStore.Images.Media.DATA, path);
        long time = currentTimeMillis / 1000;
        values.put(MediaStore.MediaColumns.DATE_ADDED, time);
        values.put(MediaStore.MediaColumns.DATE_MODIFIED, time);
        if (file.exists()) {
            values.put(MediaStore.Images.Media.SIZE, file.length());
        }
        ContentResolver resolver = context.getContentResolver();
        Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        return uri;
    }

    public static String getDirPath() {
        String dirPath = "";
        File imageDir = null;
        File extStorageDir = Environment.getExternalStorageDirectory();
        if (extStorageDir.canWrite()) {
            imageDir = new File(extStorageDir.getPath() + "/crop");
        }
        if (imageDir != null) {
            if (!imageDir.exists()) {
                imageDir.mkdirs();
            }
            if (imageDir.canWrite()) {
                dirPath = imageDir.getPath();
            }
        }
        return dirPath;
    }

    public static String getMimeType(Bitmap.CompressFormat format) {
        switch (format) {
            case JPEG:
                return "jpeg";
            case PNG:
                return "png";
        }
        return "png";
    }

    private String getTempDirectoryPath() {
        File cache = null;
        cache = getCacheDir();
        cache.mkdirs();
        return cache.getAbsolutePath();
    }

    private final LoadCallback mLoadCallback = new LoadCallback() {
        @Override
        public void onSuccess() {
        }

        @Override
        public void onError(Throwable e) {
        }
    };

    private final CropCallback mCropCallback = new CropCallback() {
        @Override
        public void onSuccess(Bitmap cropped) {
            bi.cropImageView.save(cropped)
                    .compressFormat(mCompressFormat)
                    .execute(createSaveUri(), mSaveCallback);
        }

        @Override
        public void onError(Throwable e) {
        }
    };

    private final SaveCallback mSaveCallback = new SaveCallback() {
        @Override
        public void onSuccess(Uri outputUri) {
            Intent intent = new Intent(mActivity, FinalStepActivity2.class);
            intent.putExtra("path", outputUri.toString());
            intent.putExtra("type", getIntent().getStringExtra("type"));
            intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
            startActivity(intent);
            finish();
        }

        @Override
        public void onError(Throwable e) {
        }
    };

}
