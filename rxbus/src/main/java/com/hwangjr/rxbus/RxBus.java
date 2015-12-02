package com.hwangjr.rxbus;

import android.support.annotation.NonNull;

import com.hwangjr.timber.Timber;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

@Deprecated
public class RxBus {
    private static final boolean DEBUG = true;

    private static RxBus mInstance;

    private ConcurrentHashMap<Object, List<Subject>> mSubjectsMapper = new ConcurrentHashMap<>();

    public static synchronized RxBus instance() {
        if (mInstance == null) {
            mInstance = new RxBus();
        }
        return mInstance;
    }

    private RxBus() {
    }

    public <T> Observable<T> register(@NonNull Object tag, @NonNull Class<T> clazz) {
        List<Subject> subjectList = mSubjectsMapper.get(tag);
        if (subjectList == null) {
            subjectList = new ArrayList<>();
            mSubjectsMapper.put(tag, subjectList);
        }

        Subject<T, T> subject = PublishSubject.create();
        subjectList.add(subject);
        if (DEBUG) {
            Timber.d("[register] mSubjectsMapper: " + mSubjectsMapper);
        }
        return subject;
    }

    public void unregister(@NonNull Object tag, @NonNull Observable observable) {
        List<Subject> subjects = mSubjectsMapper.get(tag);
        if (subjects != null) {
            subjects.remove(observable);
            if (subjects.isEmpty()) {
                mSubjectsMapper.remove(tag);
            }
            if (DEBUG) {
                Timber.d("[unregister] mSubjectsMapper: " + mSubjectsMapper);
            }
        }
    }

    public void post(@NonNull Object tag, @NonNull Object content) {
        List<Subject> subjects = mSubjectsMapper.get(tag);
        if (subjects != null && !subjects.isEmpty()) {
            for (Subject subject : subjects) {
                subject.onNext(content);
            }
        }
        if (DEBUG) {
            Timber.d("[send] mSubjectsMapper: " + mSubjectsMapper);
        }
    }

}
