package com.example.myapplication.anim.bean;

public class AnimationFrameBean {

    private int mResourceId;
    private int mDuration;

    public AnimationFrameBean(int resourceId, int duration){
        mResourceId = resourceId;
        mDuration = duration;
    }

    public int getResourceId() {
        return mResourceId;
    }

    public int getDuration() {
        return mDuration;
    }
}
