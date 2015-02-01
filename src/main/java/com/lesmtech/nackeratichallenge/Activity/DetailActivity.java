package com.lesmtech.nackeratichallenge.Activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.lesmtech.nackeratichallenge.Database.DBAdapter;
import com.lesmtech.nackeratichallenge.Model.Application;
import com.lesmtech.nackeratichallenge.R;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Te on 1/30/15.
 */

public class DetailActivity extends Activity {


    private TextView tv_name;
    private ImageView iv_image;
    private TextView tv_price;
    private TextView tv_artist;
    private TextView tv_category;
    private TextView tv_contenttype;
    private TextView tv_summary;
    private TextView tv_title;
    private TextView tv_releaseDate;

    // MenuItems
    private MenuItem operateCollection;

    private ActionBar mActionBar;


    private Application app;

    private URL image_url;
    /*
        Two Solutions:
          1. SQLite
           2. SharedPreference
     */
    DBAdapter adapter = new DBAdapter(this);

    SharedPreferences mSharedPreferences;


    MenuItem.OnMenuItemClickListener addAppToCollection = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {

            adapter.open();
            adapter.insertCollection(app.getId(), app.getName(), app.getImage(), app.getSummary(), app.getPrice(), app.getContenttype(), app.getRights(), app.getTitle(), app.getArtist(), app.getCategory(), app.getReleaseDate());
            adapter.close();

            operateCollection.setIcon(R.drawable.add_selected);
            operateCollection.setOnMenuItemClickListener(deleteAppToCollection);
            return true;
        }
    };

    MenuItem.OnMenuItemClickListener deleteAppToCollection = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {

            adapter.open();
            adapter.deleteCollection(app.getId());
            adapter.close();

            operateCollection.setIcon(R.drawable.add_normal);
            operateCollection.setOnMenuItemClickListener(addAppToCollection);
            return true;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        init();
    }

    private void init() {
        app = (Application) getIntent().getSerializableExtra("app");

        // Initial ActionBar
        mActionBar = getActionBar();
        mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);

        tv_name = (TextView) findViewById(R.id.name);
        iv_image = (ImageView) findViewById(R.id.image);
        tv_price = (TextView) findViewById(R.id.price);
        tv_artist = (TextView) findViewById(R.id.artist);
        tv_contenttype = (TextView) findViewById(R.id.contenttype);
        tv_category = (TextView) findViewById(R.id.category);
        tv_summary = (TextView) findViewById(R.id.summary);
        tv_title = (TextView) findViewById(R.id.title);
        tv_releaseDate = (TextView) findViewById(R.id.releaseDate);

        tv_name.setText(app.getName());
        tv_price.setText(app.getPrice());
        tv_releaseDate.setText(app.getReleaseDate());
        tv_artist.setText(app.getArtist());
        tv_category.setText(app.getCategory());
        tv_title.setText(app.getTitle());
        tv_summary.setText(app.getSummary());
        tv_contenttype.setText(app.getContenttype());

        new LoadImageDrawable().execute(app.getImage());

    }

    private class LoadImageDrawable extends AsyncTask<String, Void, Drawable> {
        @Override
        protected Drawable doInBackground(String... params) {

            Drawable drawable = null;

            try {
                image_url = new URL(params[0]);
                InputStream is = image_url.openConnection().getInputStream();
                drawable = Drawable.createFromStream(is, "app_icon");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return drawable;
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            super.onPostExecute(drawable);
            iv_image.setImageDrawable(drawable);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuinflater = new MenuInflater(this);
        menuinflater.inflate(R.menu.menu_main, menu);
        operateCollection = menu.findItem(R.id.addToCollection);

        adapter.open();

        Cursor exist = adapter.queryCollection(app.getId());

        if (!exist.moveToNext()) {
            operateCollection.setOnMenuItemClickListener(addAppToCollection);
            operateCollection.setIcon(R.drawable.add_normal);
        } else {
            operateCollection.setOnMenuItemClickListener(deleteAppToCollection);
            operateCollection.setIcon(R.drawable.add_selected);
        }

        adapter.close();

        // Check SQLite


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                break;
            case R.id.email:
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Let's play " + app.getName() + " together! Dude");
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                break;
            case R.id.twitter:
                shareToTwitter();
                break;
        }
        return true;
    }

    private void shareToTwitter() {
        TweetComposer.Builder builder = new TweetComposer.Builder(this)
                .text("Let's play " + app.getName() + " together! Dude");
        builder.show();
    }
}
