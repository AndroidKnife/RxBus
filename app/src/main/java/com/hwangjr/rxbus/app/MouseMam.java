package com.hwangjr.rxbus.app;

import com.hwangjr.rxbus.annotation.Produce;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;

import java.util.Random;

/**
 * Mouse mam to birth so many mouse.
 */
public class MouseMam {

    /**
     * Tell Tom to begin the war!
     * @return
     */
    @Produce(
            thread = EventThread.NEW_THREAD,
            tags = {@Tag}
    )
    public String tell() {
        return "Ohh, you have so many mouses!! On " + Thread.currentThread();
    }

    /**
     * birth a mouse, maybe white/black/dead mouse.
     * @return
     */
    public Mouse birth() {
        Random random = new Random();
        int godSeed = random.nextInt() % 3;
        if (godSeed == 0) {
            return new WhiteMouse();
        } else if (godSeed == 1) {
            return new BlackMouse();
        } else {
            return new DeadMouse();
        }
    }
}

/**
 * White mouse
 */
class WhiteMouse implements Mouse {
    @Override
    public void squeak() {
        BusProvider.getInstance().post(Constants.EventType.TAG_STORY, this);
    }

    @Override
    public String toString() {
        return "White Mouse: " + super.toString();
    }
}

/**
 * Black mouse
 */
class BlackMouse implements Mouse {
    @Override
    public void squeak() {
        BusProvider.getInstance().post(Constants.EventType.TAG_STORY, this);
    }

    @Override
    public String toString() {
        return "Black Mouse: " + super.toString();
    }
}

/**
 * Dead mouse
 */
class DeadMouse implements Mouse {
    @Override
    public void squeak() {
        BusProvider.getInstance().post(this);
    }

    @Override
    public String toString() {
        return "Dead Mouse: " + super.toString();
    }
}
