package com.example.myapplication.anim;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.widget.ImageView;

import com.example.myapplication.anim.bean.AnimationFrameBean;

import java.lang.ref.SoftReference;
import java.util.ArrayList;

public class FasterAnimationsContainer {

    private ArrayList<AnimationFrameBean> mAnimationFrames; // 资源文件集合
    private int mIndex; // 当前帧

    private boolean mShouldRun; // 是否开始动画
    private boolean mIsRunning; // 是否在动画中
    private SoftReference<ImageView> mSoftReferenceImageView;
    private Handler mHandler; // 通过handler不停刷新imageView的bitmap

    private Bitmap mRecycleBitmap;  // option操作过后的bitmap，也是imageView最终展示的

    // 动画停止监听
    private OnAnimationStoppedListener mOnAnimationStoppedListener;
    // 某一帧动画下标监听
    private OnAnimationFrameChangedListener mOnAnimationFrameChangedListener;

    private FasterAnimationsContainer(ImageView imageView) {
        init(imageView);
    }

    private static FasterAnimationsContainer sInstance;

    public static FasterAnimationsContainer getInstance(ImageView imageView) {
        if (sInstance == null)
            sInstance = new FasterAnimationsContainer(imageView);
        sInstance.mRecycleBitmap = null;
        return sInstance;
    }

    /**
     * initialize imageview and frames
     * @param imageView
     */
    public void init(ImageView imageView){
        mAnimationFrames = new ArrayList<AnimationFrameBean>();
        mSoftReferenceImageView = new SoftReference<ImageView>(imageView);

        mHandler = new Handler();
        if(mIsRunning){
            stop();
        }

        mShouldRun = false;
        mIsRunning = false;

        mIndex = -1;
    }

    /**
     * 增加指定帧动画
     * @param index 哪一帧下标
     * @param resId 图片资源id
     * @param interval 间隔
     */
    public void addFrame(int index, int resId, int interval){
        mAnimationFrames.add(index, new AnimationFrameBean(resId, interval));
    }

    /**
     * 增加帧动画
     * @param resId 图片资源id
     * @param interval 间隔
     */
    public void addFrame(int resId, int interval){
        mAnimationFrames.add(new AnimationFrameBean(resId, interval));
    }

    /**
     * 增加帧动画数组
     * @param resIds 图片资源id数组
     * @param interval 间隔
     */
    public void addAllFrames(int[] resIds, int interval){
        for(int resId : resIds){
            mAnimationFrames.add(new AnimationFrameBean(resId, interval));
        }
    }

    /**
     * 删除指定帧动画
     * @param index 哪一帧下标
     */
    public void removeFrame(int index){
        mAnimationFrames.remove(index);
    }

    /**
     * 删除所有动画资源图片
     */
    public void removeAllFrames(){
        mAnimationFrames.clear();
    }

    /**
     * 替换某一帧动画
     * @param index 哪一帧下标
     * @param resId 图片资源id
     * @param interval 间隔
     */
    public void replaceFrame(int index, int resId, int interval){
        mAnimationFrames.set(index, new AnimationFrameBean(resId, interval));
    }

    private AnimationFrameBean getNext() {
        mIndex++;
        if (mIndex >= mAnimationFrames.size()){
            mIndex = 0;
            //只播放一次，去掉就重复播放
            mShouldRun = false;
        }
        return mAnimationFrames.get(mIndex);
    }

    /**
     * 动画停止监听
     */
    public interface OnAnimationStoppedListener{
        public void onAnimationStopped();
    }

    /**
     * 动画哪一帧下标监听
     */
    public interface OnAnimationFrameChangedListener{
        public void onAnimationFrameChanged(int index);
    }


    /**
     * 设置动画停止监听
     * @param listener OnAnimationStoppedListener
     */
    public void setOnAnimationStoppedListener(OnAnimationStoppedListener listener){
        mOnAnimationStoppedListener = listener;
    }

    /**
     * 设置动画某一帧下标监听
     * @param listener OnAnimationFrameChangedListener
     */
    public void setOnAnimationFrameChangedListener(OnAnimationFrameChangedListener listener){
        mOnAnimationFrameChangedListener = listener;
    }

    /**
     * 开始动画
     */
    public synchronized void start() {
        mShouldRun = true;
        if (mIsRunning)
            return;
        mHandler.post(new FramesSequenceAnimation());
    }

    /**
     * 停止动画
     */
    public synchronized void stop() {
        sInstance = null;
        mHandler.removeCallbacks(null);
        mHandler.removeCallbacksAndMessages(null);

        if (mRecycleBitmap!=null&&!mRecycleBitmap.isRecycled()) {
            mRecycleBitmap.recycle();
            mRecycleBitmap= null;
            System.gc();
        }
    }

    /**
     * runnable循环执行
     */
    private class FramesSequenceAnimation implements Runnable {
        @Override
        public void run() {
            ImageView imageView = mSoftReferenceImageView.get();
            if (!mShouldRun || imageView == null) {
                mIsRunning = false;
                if (mOnAnimationStoppedListener != null) {
                    mOnAnimationStoppedListener.onAnimationStopped();
                }
                return;
            }
            mIsRunning = true;
            if (imageView.isShown()) {
                AnimationFrameBean frame = getNext();
                GetImageDrawableTask task = new GetImageDrawableTask(imageView);
                task.execute(frame.getResourceId());
                mHandler.postDelayed(this, frame.getDuration());
            }
        }
    }

    /**
     * 异步处理图片
     */
    @SuppressLint("StaticFieldLeak")
    private class GetImageDrawableTask extends AsyncTask<Integer, Void, Drawable>{

        private ImageView mImageView;

        public GetImageDrawableTask(ImageView imageView) {
            mImageView = imageView;
        }

        @SuppressLint({"NewApi", "UseCompatLoadingForDrawables", "ObsoleteSdkInt"})
        @Override
        protected Drawable doInBackground(Integer... params) {
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB){
                return mImageView.getContext().getResources().getDrawable(params[0]);
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;
            if (mRecycleBitmap != null)
                options.inBitmap = mRecycleBitmap;
            mRecycleBitmap = BitmapFactory.decodeResource(mImageView
                    .getContext().getResources(), params[0], options);
            BitmapDrawable drawable = new BitmapDrawable(mImageView.getContext().getResources(),mRecycleBitmap);
            return drawable;
        }

        @Override
        protected void onPostExecute(Drawable result) {
            super.onPostExecute(result);
            if(result!=null) mImageView.setImageDrawable(result);
            if (mOnAnimationFrameChangedListener != null)
                mOnAnimationFrameChangedListener.onAnimationFrameChanged(mIndex);
        }
    }
}