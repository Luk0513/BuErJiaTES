package com.fierce.buerjiates.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.util.LruCache;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by win7 on 2017/3/20.
 */

public class ImageCacheUtils {
    private final String TAG = "ImageCacheUtils";
    /**
     * 记录所有正在下载或等待下载的任务。
     */
    private Set<BitmapWorkerTask> taskCollection;

    /**
     * 图片缓存技术的核心类，用于缓存所有下载好的图片，在程序内存达到设定值时会将最少最近使用的图片移除掉。
     */
    private LruCache<String, Bitmap> mMemoryCache;

    /**
     * 图片硬盘缓存核心类。
     */
    private DiskLruCache mDiskLruCache;


    public ImageCacheUtils(Context context) {
        taskCollection = new HashSet<>();
        // 获取应用程序最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        // 设置图片缓存大小为程序最大可用内存的1/8
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
        try {
            // 获取图片缓存路径
            File cacheDir = getDiskCacheDir(context, "thumb");
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            // 创建DiskLruCache实例，初始化缓存数据
            mDiskLruCache = DiskLruCache
                    .open(cacheDir, getAppVersion(context), 1, 100 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将一张图片存储到LruCache中。
     *
     * @param key    LruCache的键，这里传入图片的URL地址。
     * @param bitmap LruCache的键，这里传入从网络上下载的Bitmap对象。
     */
    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     * 从LruCache中获取一张图片，如果不存在就返回null。
     *
     * @param key LruCache的键，这里传入图片的URL地址。
     * @return 对应传入键的Bitmap对象，或者null。
     */
    private Bitmap getBitmapFromMemoryCache(String key) {
        return mMemoryCache.get(key);
    }

    public void removeCache() {
        mMemoryCache.evictionCount();
        mMemoryCache.evictAll();
    }

    /**
     * 加载Bitmap对象。此方法会在LruCache中检查所有屏幕中可见的ImageView的Bitmap对象，
     * 如果发现任何一个ImageView的Bitmap对象不在缓存中，就会开启异步线程去下载图片。
     */
    int i = 0;

    public void loadBitmaps(ImageView imageView, String imageUrl, ViewGroup view) {
        Log.e(TAG, "loadBitmaps: >>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + i++);
        Bitmap bitmap;
        String key = hashKeyForDisk(imageUrl);
        bitmap = getBitmapFromMemoryCache(key);
        if (bitmap == null) {
            DiskLruCache.Snapshot snapshot;
            FileDescriptor fileDescriptor = null;
            FileInputStream fileInputStream = null;
            try {
                snapshot = mDiskLruCache.get(key);
                if (snapshot == null) {
                    BitmapWorkerTask task;
                    if (view != null)
                        task = new BitmapWorkerTask(view);
                    else
                        task = new BitmapWorkerTask(imageView);
                    taskCollection.add(task);
                    task.execute(imageUrl);
                } else {
                    fileInputStream = (FileInputStream) snapshot.getInputStream(0);
                    fileDescriptor = fileInputStream.getFD();
                    try {
                        bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                    } catch (OutOfMemoryError e) {
                        removeCache();
                    }
                    imageView.setImageBitmap(bitmap);
                    addBitmapToMemoryCache(key, bitmap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fileDescriptor == null && fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            if (imageView != null)
                imageView.setImageBitmap(bitmap);
        }
    }

    /**
     * bitmap转byteArr
     *
     * @param bitmap bitmap对象
     * @return 字节数组
     */
    private static byte[] bitmap2Bytes(Bitmap bitmap) {
        if (bitmap == null) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public byte[] getBitmapByte(String imgUrl) {
        Bitmap bitmap;
        byte[] bimapbyt;
        String key = hashKeyForDisk(imgUrl);
        DiskLruCache.Snapshot snapshot;
        FileDescriptor fileDescriptor = null;
        FileInputStream fileInputStream = null;
        try {
            snapshot = mDiskLruCache.get(key);
            fileInputStream = (FileInputStream) snapshot.getInputStream(0);
            fileDescriptor = fileInputStream.getFD();
            bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            bimapbyt = bitmap2Bytes(bitmap);
            return bimapbyt;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileDescriptor == null && fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    /**
     * @param imageUrl url
     */
    public void loadBitmaps(String imageUrl) {
        String key = hashKeyForDisk(imageUrl);
        DiskLruCache.Snapshot snapshot;
        try {
            snapshot = mDiskLruCache.get(key);
            if (snapshot == null) {
                BitmapWorkerTask task;
                task = new BitmapWorkerTask();
                taskCollection.add(task);
                task.execute(imageUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 取消所有正在下载或等待下载的任务。
     */
    public void cancelAllTasks() {
        if (taskCollection != null) {
            for (BitmapWorkerTask task : taskCollection) {
                task.cancel(false);
            }
        }
    }

    /**
     * 根据传入的uniqueName获取硬盘缓存的路径地址。
     */
    private File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    /**
     * 获取当前应用程序的版本号。
     */
    private int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * 使用MD5算法对传入的key进行加密并返回。
     */
    private String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
            // Log.e(TAG, "hashKeyForDisk: ***********************MD5  " + key + " ?? " + cacheKey);
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    /**
     * 将缓存记录同步到journal文件中。
     */
    public void fluchCache() {
        if (mDiskLruCache != null) {
            try {
                mDiskLruCache.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }


//    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
//        // Raw height and width of image
//        // 源图片的高度和宽度
//        final int height = options.outHeight;
//        final int width = options.outWidth;
//        int inSampleSize = 1;
//        if (height > reqHeight || width > reqWidth) {
//            // 计算出实际宽高和目标宽高的比率
//            final int heightRatio = Math.round((float) height / (float) reqHeight);
//            final int widthRatio = Math.round((float) width / (float) reqWidth);
//            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
//            // 一定都会大于等于目标的宽和高。
//            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
//        }
//        return inSampleSize;
//    }

    /**
     * 异步下载图片的任务。
     *
     * @author guolin
     */
    private class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

        private ViewGroup view;
        private ImageView imageView;

        private BitmapWorkerTask(ViewGroup view) {
            this.view = view;
        }

        private BitmapWorkerTask(ImageView imageView) {
            this.imageView = imageView;
        }

        private BitmapWorkerTask() {
        }

        /**
         * 图片的URL地址
         */
        private String imageUrl;

        /**
         * @param params 图片地址
         * @return bitmap
         */
        @Override
        protected Bitmap doInBackground(String... params) {
            imageUrl = params[0];
            FileDescriptor fileDescriptor = null;
            FileInputStream fileInputStream = null;
            DiskLruCache.Snapshot snapShot;
            try {
                final String key = hashKeyForDisk(imageUrl);
                DiskLruCache.Editor editor = mDiskLruCache.edit(key);
                // 存入磁盘
                if (editor != null) {
                    OutputStream outputStream = editor.newOutputStream(0);
                    if (downloadUrlToStream(imageUrl, outputStream)) {
                        editor.commit();
                    } else {
                        editor.abort();
                    }
                }
                // 缓存被写入后，再次查找key对应的缓存
                snapShot = mDiskLruCache.get(key);
                if (snapShot != null) {
                    fileInputStream = (FileInputStream) snapShot.getInputStream(0);
                    fileDescriptor = fileInputStream.getFD();
                }
                //将缓存数据解析成Bitmap对象
                Bitmap bitmap = null;
                if (imageView != null || view != null) {
                    if (fileDescriptor != null) {
                        bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                    }
                }
                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fileDescriptor == null && fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            // 根据Tag找到相应的ImageView控件，将下载好的图片显示出来。
            if (view != null) {
                ImageView v = (ImageView) view.findViewWithTag(imageUrl);
                if (v != null && bitmap != null) {
                    v.setImageBitmap(bitmap);
                    addBitmapToMemoryCache(hashKeyForDisk(imageUrl), bitmap);
                }
            } else {
                if (imageView != null && bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                    addBitmapToMemoryCache(hashKeyForDisk(imageUrl), bitmap);
                }
            }
            taskCollection.remove(this);
        }

        /**
         * 建立HTTP请求，并获取Bitmap对象。
         *
         * @return 解析后的Bitmap对象
         */
        private boolean downloadUrlToStream(String urlString, OutputStream outputStream) {
            HttpURLConnection urlConnection = null;
            BufferedOutputStream out = null;
            BufferedInputStream in = null;
            try {
                final URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                // 设置连接主机超时时间
                urlConnection.setConnectTimeout(5 * 1000);
                //设置从主机读取数据超时
                urlConnection.setReadTimeout(5 * 1000);
                urlConnection.connect();
                in = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
                out = new BufferedOutputStream(outputStream, 8 * 1024);
                int b;
                while ((b = in.read()) != -1) {
                    out.write(b);
                }
                return true;
            } catch (final IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                try {
                    if (out != null) {
                        out.close();
                    }
                    if (in != null) {
                        in.close();
                    }
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }

    }
}
