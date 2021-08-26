package com.hwangjr.rxbus;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import timber.log.Timber;

@Deprecated
public class RxBusOlder {
    private static final boolean DEBUG = true;

    private static RxBusOlder sInstance;

    private ConcurrentHashMap<Object, List<Subject>> mSubjectsMapper = new ConcurrentHashMap<>();

    private RxBusOlder() {
    }

    public static synchronized RxBusOlder instance() {
        if (sInstance == null) {
            sInstance = new RxBusOlder();
        }
        return sInstance;
    }

    public <T> Observable<T> register(@NonNull Object tag, @NonNull Class<T> clazz) {
        List<Subject> subjectList = mSubjectsMapper.get(tag);
        if (subjectList == null) {
            subjectList = new ArrayList<>();
            mSubjectsMapper.put(tag, subjectList);
        }

        // FIXME: Won't serialized the PublishSubject to SerializedSubject, thread unsafe
        Subject<T> subject = PublishSubject.create();
        subjectList.add(subject);
        if (DEBUG) {
            Timber.d("[register] mSubjectsMapper: %s", mSubjectsMapper);
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
                Timber.d("[unregister] mSubjectsMapper: %s", mSubjectsMapper);
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
            Timber.d("[send] mSubjectsMapper: %s", mSubjectsMapper);
        }
    }
}
