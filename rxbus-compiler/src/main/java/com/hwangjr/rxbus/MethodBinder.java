package com.hwangjr.rxbus;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.lang.model.type.TypeMirror;

/**
 * Created by trs on 17-1-5.
 */

final class MethodBinder {
    private String mMethodName;
    private Set<String> mTags;
    private ThreadType mThreadType;
    private TypeMirror mParamType;

    public MethodBinder() {
        mTags = new LinkedHashSet<>();
    }

    public void setMethodName(String name) {
        mMethodName = name;
    }

    public String getMethodName() {
        return mMethodName;
    }

    public void setThreadType(ThreadType type) {
        mThreadType = type;
    }

    public ThreadType getThreadType() {
        return mThreadType;
    }

    public void addTag(String tag) {
        mTags.add(tag);
    }

    public Set<String> getTags() {
        return mTags;
    }

    public void setParamType(TypeMirror typeMirror) {
        mParamType = typeMirror;
    }

    public TypeMirror getParamTypes() {
        return mParamType;
    }

    @Override
    public String toString() {
        return "MethodBinder{" +
                "mMethodName='" + mMethodName + '\'' +
                ", mTags=" + mTags +
                ", mThreadType=" + mThreadType +
                ", mParamType=" + mParamType +
                '}';
    }
}
