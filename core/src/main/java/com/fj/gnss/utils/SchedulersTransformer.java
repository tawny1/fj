package com.fj.gnss.utils;


import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

/**
 * @author tony.tang
 * date 2021/10/27 9:57
 * @version 1.0
 * @className SchedulersTransformer
 * @description 观察转换，主线程触发调用
 */
public class SchedulersTransformer<T> implements ObservableTransformer<T, T> {
    /**
    * @description:线程转换 io线程转主线程
    * @param upstream
    * @return: io.reactivex.ObservableSource<T>
    * @author: tony.tang
    * @date: 2022/1/6 11:41
    */
    @NonNull
    @Override
    public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
        return upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
