package com.lesmtech.nackeratichallenge.Activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lesmtech.nackeratichallenge.Database.DBAdapter;
import com.lesmtech.nackeratichallenge.Model.Application;
import com.lesmtech.nackeratichallenge.R;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import io.fabric.sdk.android.Fabric;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends FragmentActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "R35GXig6spybMrBEDxcf0qqo2";
    private static final String TWITTER_SECRET = "yp5IvCoECrC91OQaqa3JlwOhzzEcrjdHtthP2YGXTlO3a8ewUH";

    // ListView in ApplicationsFragment;
    List<Application> applications;
    ListView lvInApplications;
    List<String> apps_names;

    // ListView in CollectionsFragment;
    List<Application> collections;
    ListView lvInCollections;
    List<String> collections_names;
    DBAdapter dbAdapter = new DBAdapter(this);


    final static int COLLECTIONFRAGMENT = 0;
    final static int APPLICATIONSFRAGMENT = 1;
    final static int NUMS_FRAGMENTS = 2;

    // Show which Fragment is showing
    int flag = COLLECTIONFRAGMENT;

    Fragment[] mFragments = new Fragment[NUMS_FRAGMENTS];

    FragmentManager mFragmentManager;
    ApplicationsFragment mApplicationsFragment;
    CollectionsFragment mCollectionsFragment;

    ActionBar mActionBar;

    ProgressDialog mProgressDialog;

    //SlidingMenu
    SlidingMenu mSlidingMenu;


    //Get all collections and show in the CollectionsFragment;


    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            onClickToDetail(position);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);
        initView();
        new requestJSON().execute("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topgrossingapplications/sf=143441/limit=25/json");
    }

    private void initView() {

        // Initialize Fabric
        Fabric.with(this, new TweetComposer());

        // ProgressDialog
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading...");
        mProgressDialog.show();

        // Create SlidingMenu
        mSlidingMenu = new SlidingMenu(this);
        mSlidingMenu.setMode(SlidingMenu.LEFT);
        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        mSlidingMenu.setFadeDegree(0.35f);
        mSlidingMenu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
        mSlidingMenu.setMenu(R.layout.activity_slidingmenu);
        mSlidingMenu.showMenu();

        mActionBar = getActionBar();

        lvInApplications = (ListView) findViewById(R.id.listView);
        lvInApplications.setOnItemClickListener(onItemClickListener);

        lvInCollections = (ListView) findViewById(R.id.lv_collections);
        lvInCollections.setOnItemClickListener(onItemClickListener);

        mFragmentManager = getSupportFragmentManager();
        mApplicationsFragment = (ApplicationsFragment) getSupportFragmentManager().findFragmentById(R.id.applications);
        mCollectionsFragment = (CollectionsFragment) getSupportFragmentManager().findFragmentById(R.id.collections);
        mFragments[APPLICATIONSFRAGMENT] = mApplicationsFragment;
        mFragments[COLLECTIONFRAGMENT] = mCollectionsFragment;

        renderCollections();

        showThisFragment(APPLICATIONSFRAGMENT);

    }

    private void renderCollections() {
        dbAdapter.open();
        Cursor cursor = dbAdapter.getAllCollection();
        collections = new ArrayList<>();
        collections_names = new ArrayList<>();
        while (cursor.moveToNext()) {
            Application app = new Application(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10));
            collections_names.add(cursor.getString(1));
            collections.add(app);
        }
        ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.activity_main_listitem, collections_names);
        lvInCollections.setAdapter(mArrayAdapter);
    }

    private void showThisFragment(int fragment) {

        flag = fragment;

        for (int i = 0; i < NUMS_FRAGMENTS; i++) {
            mFragmentManager.beginTransaction().hide(mFragments[i]).commit();
        }
        mFragmentManager.beginTransaction().show(mFragments[fragment]).commit();
    }

    // SlidingMenu Button Onclick
    public void showApplications(View v) {
        showThisFragment(APPLICATIONSFRAGMENT);
        mActionBar.setTitle("Applications");
        mSlidingMenu.toggle();
    }

    public void showCollections(View v) {
        showThisFragment(COLLECTIONFRAGMENT);
        mActionBar.setTitle("Collections");
        renderCollections();
        mSlidingMenu.toggle();
    }

    // Request JSON
    private class requestJSON extends AsyncTask<String, Void, List<Application>> {

        @Override
        protected List<Application> doInBackground(String... params) {

            StringBuilder resultJSON = new StringBuilder();

            try {
                URL url = new URL(params[0]);
                InputStream is = url.openConnection().getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = br.readLine()) != null) {
                    resultJSON.append(line);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return analysisJSON(resultJSON.toString());
        }

        @Override
        protected void onPostExecute(List<Application> applications) {
            super.onPostExecute(applications);
            ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.activity_main_listitem, apps_names);
            lvInApplications.setAdapter(mArrayAdapter);
            mProgressDialog.dismiss();
        }
    }

    private List<Application> analysisJSON(String s) {

        applications = new ArrayList<>();
        apps_names = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(s).getJSONObject("feed");
            JSONArray jsonArray = jsonObject.getJSONArray("entry");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject app_json = jsonArray.getJSONObject(i);
                String name = app_json.getJSONObject("im:name").getString("label");
                apps_names.add(i, name);
                String id = app_json.getJSONObject("id").getJSONObject("attributes").getString("im:id");
                String image = app_json.getJSONArray("im:image").getJSONObject(2).getString("label");
                String summary = app_json.getJSONObject("summary").getString("label");
                String price = app_json.getJSONObject("im:price").getJSONObject("attributes").getString("amount");
                String contenttype = app_json.getJSONObject("im:contentType").getJSONObject("attributes").getString("label");
                String rights = app_json.getJSONObject("rights").getString("label");
                String title = app_json.getJSONObject("title").getString("label");
                String artist = app_json.getJSONObject("im:artist").getString("label");
                String category = app_json.getJSONObject("category").getJSONObject("attributes").getString("label");
                String releaseDate = app_json.getJSONObject("im:releaseDate").getString("label");
                Application app = new Application(id, name, image, summary, price, contenttype, rights, title, artist, category, releaseDate);
                applications.add(i, app);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return applications;
    }


    //  For Test
    private void displayToast(String test) {
        Toast.makeText(getApplicationContext(), test, Toast.LENGTH_SHORT).show();
    }

    // Two Fragments
    public static class CollectionsFragment extends Fragment {

        public CollectionsFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_collections, container, false);
            return rootView;
        }

    }

    public static class ApplicationsFragment extends Fragment {
        public ApplicationsFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_applications, container, false);
            return rootView;
        }
    }

    // Click into DetailActivity
    public void onClickToDetail(int position) {
        Intent detailActivity = new Intent(this, DetailActivity.class);

        if (flag == APPLICATIONSFRAGMENT)
            detailActivity.putExtra("app", applications.get(position));
        else
            detailActivity.putExtra("app", collections.get(position));

        startActivity(detailActivity);
    }
}
