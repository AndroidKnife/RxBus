package com.hwangjr.rxbus.app;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private CatMam catMam = new CatMam();
    private MouseMam mouseMam = new MouseMam();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Mouse mouse = mouseMam.birth();
                mouse.squeak();
                Snackbar.make(view, "Birth a mouse and squeak ! " + mouse, Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                Timber.e("Haha, i am " + mouse);
            }
        });

        BusProvider.getInstance().register(mouseMam);
        BusProvider.getInstance().register(catMam.birth());
    }

    @Override
    protected void onDestroy() {
        BusProvider.getInstance().unregister(mouseMam);
        ArrayList<Cat> cats = catMam.getCats();
        for (Cat cat : cats) {
            BusProvider.getInstance().unregister(cat);
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
