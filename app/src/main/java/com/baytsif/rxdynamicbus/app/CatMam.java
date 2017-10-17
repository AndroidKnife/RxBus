package com.baytsif.rxdynamicbus.app;

import com.baytsif.rxdynamicbus.annotation.Subscribe;
import com.baytsif.rxdynamicbus.annotation.Tag;
import com.baytsif.rxdynamicbus.entity.DeadEvent;
import com.baytsif.rxdynamicbus.thread.EventThread;

import java.util.ArrayList;

import timber.log.Timber;

/**
 * Cat mam to birth so many cat to catch mouse, it's a war!
 */
public class CatMam {
    private ArrayList<Cat> cats = new ArrayList<>();

    /**
     * Birth a cat to catch mouse.
     *
     * @return cat to catch mouse
     */
    public Cat birth() {
        Cat cat = new Tom();
        cats.add(cat);
        return cat;
    }

    /**
     * Get all birth cats.
     *
     * @return cats
     */
    public ArrayList<Cat> getCats() {
        return cats;
    }
}

/**
 * Cat Tom
 */
class Tom implements Cat {
    /**
     * Heard from mouse mam, war has begin!
     *
     * @param mouseWar
     */
    @Subscribe(
            thread = EventThread.IMMEDIATE,
            tags = {@Tag}
    )
    public void heardFromMouseMam(String mouseWar) {
        Timber.e("Just heard from mouse mam: " + mouseWar + " from " + Thread.currentThread());
    }

    /**
     * heard from mouse, war has begin! but this should never call, because no mouse make a sound.
     *
     * @param mouseWar
     */
    @Subscribe(
            thread = EventThread.IMMEDIATE,
            tags = {@Tag(Constants.EventType.TAG_STORY)}
    )
    public void heardFromMouse(String mouseWar) {
        Timber.e("Just heard from mouse: " + mouseWar + " from " + Thread.currentThread());
    }

    /**
     * Could not subscribe this method, not support {@link Mouse} interface.
     *
     * @param mouse
     */
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

    /**
     * Caught Black mouse({@link BlackMouse}).
     *
     * @param mouse
     */
    @Subscribe(
            thread = EventThread.IO,
            tags = {@Tag(Constants.EventType.TAG_STORY)}
    )
    public void caught(BlackMouse mouse) {
        Timber.e("Caught Black Mouse: " + mouse.toString() + " On " + Thread.currentThread());
    }

    /**
     * Caught Dead Event, no one subscribe the {@link DeadMouse},
     * so the dead mouse will wrap to Dead Event and post to subscribers.
     *
     * @param event
     */
    @Subscribe(
            thread = EventThread.IMMEDIATE,
            tags = {@Tag, @Tag(Constants.EventType.TAG_STORY)}
    )
    public void caught(DeadEvent event) {
        Timber.e("Caught RxBus DeadEvent, event is " + event.event +
                " and source is " + event.source + " on " + Thread.currentThread());
    }
}