package com.fierce.buerjiates.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.LruCache;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fierce.buerjiates.widget.LongImageView;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
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
 * @Author : Lukang
 * >>Live and learn<<
 * @PackegeName : com.fierce.buerjiates.utils
 * @ProjectName : BuErJiaTES
 * @Date :  2017-08-04
 * <p>
 * 需要依赖 compile 'com.jakewharton:disklrucache:2.0.2'
 */


public class ImageCacheUtils {

    private final String TAG = "ImageCacheUtils";
    /**
     * 记录所有正在下载或等待下载的任务。
     */
    private Set<BitmapWorkerTask> taskCollection;

    /**
     * 图片内存缓存技术的核心类，用于缓存所有下载好的图片，在程序内存达到设定值时会将最少最近使用的图片移除掉。
     * 《官方推荐LruCache》
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


    /**
     * 清空内存
     */
    public void removeCache() {
        mMemoryCache.evictAll();
    }

    /**
     * 加载Bitmap对象。此方法会在LruCache中检查所有屏幕中可见的ImageView的Bitmap对象，
     * 如果发现任何一个ImageView的Bitmap对象不在缓存中，就会开启异步线程去下载图片。
     *
     * @param imageView 对应显示的控件 imageView
     * @param imageUrl  图片地址
     * @param view      可以为空，如果是ListView GridVIew等 imageView需要设置Tag  img.setTag(imageUrl);
     */
    public void loadBitmaps(@NonNull ImageView imageView, @NonNull String imageUrl, ViewGroup view) {
        Bitmap bitmap;
        String key = hashKeyForDisk(imageUrl);
        //从内存中查找
        bitmap = getBitmapFromMemoryCache(key);
        if (bitmap == null) {
            //内存中没有 开启异步任务后台下载（或者硬盘中查找）
            BitmapWorkerTask task = new BitmapWorkerTask(view);
            taskCollection.add(task);
            mlog.e("z后台加载》》》list");
            task.execute(imageUrl);
        } else {
            mlog.e("内存中加载");
            if (imageView != null)
                imageView.setImageBitmap(bitmap);
        }
    }

    public void loadBitmaps(@NonNull ImageView imageView, @NonNull String imageUrl) {
        Bitmap bitmap;
        String key = hashKeyForDisk(imageUrl);
        //从内存中查找
        bitmap = getBitmapFromMemoryCache(key);
        if (bitmap == null) {
            BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            taskCollection.add(task);
            task.execute(imageUrl);
        } else {
            if (imageView != null)
                imageView.setImageBitmap(bitmap);
        }
    }

    public Bitmap loadBitmaps(@NonNull LongImageView imageView, @NonNull String imageUrl) {
        Bitmap bitmap;
        String key = hashKeyForDisk(imageUrl);
        //从内存中查找
        bitmap = getBitmapFromMemoryCache(key);
        if (bitmap == null) {
            BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            taskCollection.add(task);
            task.execute(imageUrl);
        } else {
            if (imageView != null)
                imageView.setBitmap(bitmap);
            imageView.setNeedSlide(true);
        }
        return bitmap;
    }

    /**
     * @param imageUrl url
     *                 可以用于后台预先缓存 不需ui显示
     */
    public void loadBitmaps(String imageUrl) {
        BitmapWorkerTask task = new BitmapWorkerTask();
        taskCollection.add(task);
        task.execute(imageUrl);
    }

    /**
     * 离线时 直接到磁盘查找
     *
     * @param url 图片地址
     * @return
     */
    public Bitmap getBitmap(String url) {
        String key = hashKeyForDisk(url);
        DiskLruCache.Snapshot snapshot;
        Bitmap bitmap;
        FileDescriptor fileDescriptor = null;
        FileInputStream fileInputStream = null;
        try {
            snapshot = mDiskLruCache.get(key);
            fileInputStream = (FileInputStream) snapshot.getInputStream(0);
            fileDescriptor = fileInputStream.getFD();
            bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            return bitmap;
        } catch (IOException e) {
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

    public Bitmap getLongBitmap(String imgurl) {
        Bitmap bitmap;
        String key = hashKeyForDisk(imgurl);
        //从内存中查找
        bitmap = getBitmapFromMemoryCache(key);
        if (bitmap == null) {
            BitmapWorkerTask task = new BitmapWorkerTask();
            taskCollection.add(task);
            task.execute(imgurl);

        } else {

        }
        return bitmap;
    }

    public Bitmap netPicToBmp(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            Bitmap oBitmap = null;
            ByteArrayOutputStream outputs = new ByteArrayOutputStream();
            oBitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputs); //
//
//            while ( outputs.toByteArray().length /  1024 > 32 ) {
//                outputs.reset();
//                oBitmap.compress(Bitmap.CompressFormat.JPEG, options, outputs);
//                options -=  10 ;
//            }

            ByteArrayInputStream inputs = new ByteArrayInputStream(outputs.toByteArray());
            Bitmap bitmap = BitmapFactory.decodeStream(inputs);


            return bitmap;
        } catch (IOException e) {
            // Log exception
            mlog.e("  ");
            return null;
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
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
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

    /**
     * 异步下载图片的任务。
     *
     * @author guolin
     */
    private class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

        private ViewGroup view;
        private ImageView imageView;
        private LongImageView longImageView;

        private BitmapWorkerTask(ViewGroup view) {
            this.view = view;
        }

        private BitmapWorkerTask(ImageView imageView) {
            this.imageView = imageView;
        }

        private BitmapWorkerTask(LongImageView longImageView) {
            this.longImageView = longImageView;
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
                // 生成图片URL对应的key
                final String key = hashKeyForDisk(imageUrl);
                // 查找key对应的缓存
                snapShot = mDiskLruCache.get(key);
                if (snapShot == null) {
                    mlog.e("网络加载");
                    // 如果没有找到对应的缓存，则准备从网络上请求数据，并写入缓存
                    DiskLruCache.Editor editor = mDiskLruCache.edit(key);
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
                }
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
                if (longImageView != null) {
                    if (fileDescriptor != null) {
                        bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                    }
                }
                if (bitmap != null && imageView != null) {
                    // 将Bitmap对象添加到内存缓存当中
                    addBitmapToMemoryCache(params[0], bitmap);
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
            if (longImageView != null) {
                longImageView.setBitmap(bitmap);
                longImageView.setNeedSlide(true);
            }
            //下载完成 移除任务
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
