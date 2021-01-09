RxBus - An event bus by [ReactiveX/RxJava](https://github.com/ReactiveX/RxJava)/[ReactiveX/RxAndroid](https://github.com/ReactiveX/RxAndroid)
=============================
This is an event bus designed to allowing your application to communicate efficiently.

I have use it in many projects, and now i think maybe someone would like it, so i publish it.

RxBus support annotations(@produce/@subscribe), and it can provide you to produce/subscribe on other thread 
like MAIN_THREAD, NEW_THREAD, IO, COMPUTATION, TRAMPOLINE, IMMEDIATE, even the EXECUTOR and HANDLER thread,
more in [EventThread](rxbus/src/main/java/com/hwangjr/rxbus/thread/EventThread.java).

Also RxBus provide the event tag to define the event. The method's first (and only) parameter and tag defines the event type.

**Thanks to:**

[square/otto](https://github.com/square/otto)

[greenrobot/EventBus](https://github.com/greenrobot/EventBus)

Usage
--------

Just 2 Steps:

**STEP 1**

Add dependency to your gradle file:
```groovy
compile 'com.hwangjr.rxbus:rxbus:3.0.0'
```
Or maven:
``` xml
<dependency>
  <groupId>com.hwangjr.rxbus</groupId>
  <artifactId>rxbus</artifactId>
  <version>3.0.0</version>
  <type>aar</type>
</dependency>
```

**TIP:** Maybe you also use the [JakeWharton/timber](https://github.com/JakeWharton/timber) to log your message, you may need to exclude the timber (from version 1.0.4, timber dependency update from [AndroidKnife/Utils/timber](https://github.com/AndroidKnife/Utils/tree/master/timber) to JakeWharton):
``` groovy
compile ('com.hwangjr.rxbus:rxbus:3.0.0') {
    exclude group: 'com.jakewharton.timber', module: 'timber'
}
```
en
Snapshots of the development version are available in [Sonatype's `snapshots` repository](https://oss.sonatype.org/content/repositories/snapshots/).

**STEP 2**

Just use the provided(Any Thread Enforce):
``` java
com.hwangjr.rxbus.RxBus
```
Or make RxBus instance is a better choice:
``` java
public static final class RxBus {
    private static Bus sBus;
    
    public static synchronized Bus get() {
        if (sBus == null) {
            sBus = new Bus();
        }
        return sBus;
    }
}
```

Add the code where you want to produce/subscribe events, and register and unregister the class.
``` java
public class MainActivity extends AppCompatActivity {
    ...
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ...
        RxBus.get().register(this);
        ...
    }
    
    @Override
    protected void onDestroy() {
        ...
        RxBus.get().unregister(this);
        ...
    }
        
    @Subscribe
    public void eat(String food) {
        // purpose
    }
        
    @Subscribe(
        thread = EventThread.IO,
        tags = {
            @Tag(BusAction.EAT_MORE)
        }
    )
    public void eatMore(List<String> foods) {
        // purpose
    }
    
    @Produce
    public String produceFood() {
        return "This is bread!";
    }
    
    @Produce(
        thread = EventThread.IO,
        tags = {
            @Tag(BusAction.EAT_MORE)
        }
    )
    public List<String> produceMoreFood() {
        return Arrays.asList("This is breads!");
    }
    
    public void post() {
        RxBus.get().post(this);
    }
    
    public void postByTag() {
        RxBus.get().post(Constants.EventType.TAG_STORY, this);
    }
    ...
}
```

**That is all done!**

Lint
--------

Features
--------
* JUnit test
* Docs

History
--------
Here is the [CHANGELOG](CHANGELOG.md).

FAQ
--------
**Q:** How to do pull requests?<br/>
**A:** Ensure good code quality and consistent formatting.

License
--------

    Copyright 2015 HwangJR, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
