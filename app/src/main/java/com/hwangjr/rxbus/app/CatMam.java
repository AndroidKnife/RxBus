package com.hwangjr.rxbus.app;

import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.entity.DeadEvent;
import com.hwangjr.rxbus.thread.EventThread;

import java.util.ArrayList;

import timber.log.Timber;

/**
 * Created by hwangjr on 2/23/16.
 */
public class CatMam {
    private ArrayList<Cat> cats = new ArrayList<>();

    public Cat birth() {
        Cat cat = new WhiteCat();
        cats.add(cat);
        return cat;
    }

    public ArrayList<Cat> getCats() {
        return cats;
    }
}

class WhiteCat implements Cat {

    @Subscribe(
            thread = EventThread.IMMEDIATE,
            tags = {@Tag}
    )
    public void heardFromMouseMam(String mouseWar) {
        Timber.e("Just heard from mouse mam: " + mouseWar + " from " + Thread.currentThread());
    }

    @Subscribe(
            thread = EventThread.IMMEDIATE,
            tags = {@Tag(Constants.EventType.TAG_STORY)}
    )
    public void heardFromMouse(String mouseWar) {
        Timber.e("Just heard from mouse: " + mouseWar + " from " + Thread.currentThread());
    }

    @Override
    public void caught(Mouse mouse) {
        Timber.e("Caught Mouse: " + mouse.toString() + " On " + Thread.currentThread());
    }

    /**
     * Caught white mouse({@link WhiteMouse}).
     *
     * @param mouse
     */
    @Subscribe(
            thread = EventThread.IO,
            tags = {@Tag(Constants.EventType.TAG_STORY)}
    )
    public void caught(WhiteMouse mouse) {
        Timber.e("Caught White Mouse: " + mouse.toString() + " On " + Thread.currentThread());
    }

    @Subscribe(
            thread = EventThread.IO,
            tags = {@Tag(Constants.EventType.TAG_STORY)}
    )
    public void caught(BlackMouse mouse) {
        Timber.e("Caught Black Mouse: " + mouse.toString() + " On " + Thread.currentThread());
    }

    @Subscribe(
            thread = EventThread.IMMEDIATE,
            tags = {@Tag, @Tag(Constants.EventType.TAG_STORY)}
    )
    public void caught(DeadEvent event) {
        Timber.e("Caught RxBus DeadEvent, event is " + event.event +
                " and source is " + event.source + " on " + Thread.currentThread());
    }
}