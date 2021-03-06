package com.library.utils.bitmap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import com.library.utils.file.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by linqs on 2016/7/31.
 */
public class BitmapUtils {

    /**
     * 计算缩放大小
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calcInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger
            // inSampleSize).
            final float totalPixels = width * height;
            // Anything more than 2x the requested pixels we'll sample down
            // further.
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;
            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    /**
     * 获取指定路径Bitmap
     *
     * @param imagePath
     * @param maxWidth
     * @param maxHeight
     * @return
     */
    public static Bitmap decodeBitmap(String imagePath, int maxWidth, int maxHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 设置为true可以在不分配空间状态下计算出图片的大小
        options.inJustDecodeBounds = true;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        BitmapFactory.decodeFile(imagePath, options);
        options.inSampleSize = calcInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imagePath, options);
    }

    /**
     * 保存Bitmap到指定文件
     *
     * @param bitmap
     * @param path
     * @return
     */
    public static boolean saveBitmap(Context context, Bitmap bitmap, String path) {
        return saveBitmap(context, bitmap, path, false);
    }

    /**
     * 保存Bitmap到指定文件
     *
     * @param bitmap
     * @param path
     * @param recycle 是否回收Bitmap
     * @return
     */
    public static boolean saveBitmap(Context context, Bitmap bitmap, String path, boolean recycle) {
        if (null == bitmap) {
            return true;
        }
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            return false;
        } finally {
            StreamUtils.close(fos);
        }
        if (recycle) {
            recycleBitmap(bitmap);
        }
        notifyAlbum(context, path);
        return true;
    }

    /***
     * 回收Bitmap
     *
     * @param bitmap
     */
    public static void recycleBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    /***
     * 保存照片后 通知相册
     *
     * @param context
     * @param path
     */
    public static void notifyAlbum(Context context, String path) {
        if (null == context) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        if (!path.startsWith("file://")) {
            path = "file://" + path;
        }
        intent.setData(Uri.parse(path));
        context.sendBroadcast(intent);
    }

    /**
     * 图片获取
     *
     * @param context
     * @param uri
     * @return
     */
    public static Bitmap decodeBitmapByUri(Context context, Uri uri) {
        Bitmap bmp = null;
        try {
            bmp = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }

    /***
     * 压缩图片
     *
     * @param bitmap
     * @param size   图片大小kb
     * @return
     */
    public static Bitmap compressImage(Bitmap bitmap, int size) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int options = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, options, byteArrayOutputStream);
        while (byteArrayOutputStream.toByteArray().length > 1024 * size && options > 0) {
            // 重置byteArrayOutputStream即清空byteArrayOutputStream
            byteArrayOutputStream.reset();
            // 每次都减少5
            options -= 5;
            // 这里压缩options%，把压缩后的数据存放到byteArrayOutputStream中
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, byteArrayOutputStream);
        }
        // 把压缩后的数据byteArrayOutputStream存放到ByteArrayInputStream中
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        // 把ByteArrayInputStream数据生成图片
        Bitmap b = BitmapFactory.decodeStream(byteArrayInputStream);
        if (bitmap != b && bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
        return b;
    }

    /***
     * bitmap转为byte[]
     *
     * @param bitmap
     * @return
     */
    public static byte[] bitmap2Bytes(Bitmap bitmap) {
        if (bitmap == null || (bitmap != null && bitmap.isRecycled())) {
            return null;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
