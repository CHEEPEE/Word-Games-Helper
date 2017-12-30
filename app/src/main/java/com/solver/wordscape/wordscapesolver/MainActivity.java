package com.solver.wordscape.wordscapesolver;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.Html;
import android.text.Layout;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bezyapps.floatieslibrary.Floaty;
import com.bezyapps.floatieslibrary.FloatyOrientationListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
ListView textList;

ArrayList<String> arrayDictionary = new ArrayList<>();
ArrayList<String> filterd = new ArrayList<>();
TextInputEditText search;
ImageView share,exit,rate;
RecycleSearchResultAdapter recycleSearchResultAdapter;
RecyclerView searchResultsRecycler;
RecyclerView.LayoutManager recyclerviewLayoutManager;
ArrayList<String> filterdLast = new ArrayList<>();
TextView loadingText;
Context context;
Button btnStartFloat;
    Floaty floaty;
    Button btnSearch;
    Button button_start, button_stop;
    private static final int NOTIFICATION_ID = 1500;
    public static final int PERMISSION_REQUEST_CODE = 16;
    AdView mAdView;
    ImageView headFloat;
    private RewardedVideoAd mRewardedVideoAd;
    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.before_head);
        DatabaseHelper.getInstance(MainActivity.this,"dictionarycom.db");



        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = Floaty.createNotification(this, "Words Helper", "Service Running", R.drawable.ic_launcher_background, resultPendingIntent);
        context = MainActivity.this;
        // Inflate the Views that are to be used as HEAD and BODY of The Window
        View head = LayoutInflater.from(this).inflate(R.layout.float_head, null);
        // You should not add click listeners to head as it will be overridden, but the purpose of not making head just
        // an ImageView is so you can add multiple views in it, and show and hide the relevant views to notify user etc.
        View body = LayoutInflater.from(this).inflate(R.layout.activity_main, null);
        /*textList = (ListView) body.findViewById(R.id.listview);*/
        //button_start = (Button) findViewById(R.id.floating);

        search = (TextInputEditText) body.findViewById(R.id.search);
        btnSearch = (Button) body.findViewById(R.id.searchButton);
        exit = (ImageView) body.findViewById(R.id.closeApp);
        searchResultsRecycler = (RecyclerView) body.findViewById(R.id.searchResultList);
        recyclerviewLayoutManager = new StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.HORIZONTAL);
        searchResultsRecycler.setLayoutManager(recyclerviewLayoutManager);
        recycleSearchResultAdapter = new RecycleSearchResultAdapter(context,filterdLast);
        searchResultsRecycler.setAdapter(recycleSearchResultAdapter);
        loadingText = (TextView) body.findViewById(R.id.loadingText);
        btnStartFloat = (Button) findViewById(R.id.btnStart);
        btnStartFloat.setEnabled(false);
        btnStartFloat.setText("Loading Dictionary");
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId== EditorInfo.IME_ACTION_SEARCH){
                    doTheSearch(v.getText().toString());
                }
                return true;
            }
        });


        floaty = Floaty.createInstance(this, head, body, NOTIFICATION_ID, notification, new FloatyOrientationListener() {
            @Override
            public void beforeOrientationChange(Floaty floaty) {
                Toast.makeText(MainActivity.this, "Orientation Change Start", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void afterOrientationChange(Floaty floaty) {
                Toast.makeText(MainActivity.this, "Orientation Change End", Toast.LENGTH_SHORT).show();
            }
        });
        MobileAds.initialize(this, getString(R.string.admob_app_id));
        mAdView = (AdView) body.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.another_ad_id));

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             doTheSearch(search.getText().toString());
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floaty.stopService();
            }
        });
        Cursor cursor = DatabaseHelper.rawQuery("Select word from entries;");
        cursor.moveToFirst();
        if (cursor != null && cursor.getCount() != 0){
            if (cursor.moveToFirst()){
                do {
                    arrayDictionary.add(cursor.getString(cursor.getColumnIndex("word")).toLowerCase());
                    recycleSearchResultAdapter.notifyDataSetChanged();
                }while (cursor.moveToNext());
            }
        btnStartFloat.setEnabled(true);
            btnStartFloat.setText("Start Floater");
        }


        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {

            }

            @Override
            public void onRewardedVideoAdOpened() {

            }

            @Override
            public void onRewardedVideoStarted() {

            }

            @Override
            public void onRewardedVideoAdClosed() {

            }

            @Override
            public void onRewarded(RewardItem rewardItem) {

            }

            @Override
            public void onRewardedVideoAdLeftApplication() {

            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {

            }
        });
        btnStartFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadRewardedVideoAd();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    startFloatyForAboveAndroidL();
                    finish();
                } else {
                    floaty.startService();
                    finish();
                }

            }
        });
    }
    @TargetApi(Build.VERSION_CODES.M)
    public void startFloatyForAboveAndroidL() {
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, PERMISSION_REQUEST_CODE);
        } else {
            floaty.startService();
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                floaty.startService();
            } else {
                Spanned message = Html.fromHtml("Please allow this permission, so <b>Floaties</b> could be drawn.");
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd(getString(R.string.rewarded_video_id),
                new AdRequest.Builder().build());
    }

    private void doTheSearch(String s){
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        loadingText.setVisibility(View.VISIBLE);
        s = search.getText().toString();
        filterdLast.clear();
        ArrayList<String> textInput = new ArrayList<>(); // {"c","a","t"}
        textInput.clear();
        int wordSize =0;

        for (int i = 0;i<s.length();i++){
            textInput.add(s.subSequence(i,i+1).toString().trim());
        }
        for (int in = 0;in<textInput.size();in++){
            System.out.println(textInput.get(in));
        }
        filterd.clear();
        for (int i = 0;i<arrayDictionary.size();i++){
            if (arrayDictionary.get(i).length()<=s.length()){ // dictionary word length word length <= to input
                wordSize = 0;
                for (int index = 0;index<textInput.size();index++){ //for ever letter of the word
                    if (arrayDictionary.get(i).contains(textInput.get(index))){ //if the word contains a letter of arrayChar
                        wordSize++;
                        if (wordSize==arrayDictionary.get(i).length() && arrayDictionary.get(i).length()>2){
                            filterd.add(arrayDictionary.get(i));
                        }
                    }

                }
            }
        }

        //remove duplicate words
        Object[] tempArray = filterd.toArray();
        for (Object words: tempArray){
            if (filterd.indexOf(words)!=filterd.lastIndexOf(words)){
                filterd.remove(filterd.lastIndexOf(words));
            }
        }
        for (int fIndex = 0;fIndex<filterd.size();fIndex++){
            ArrayList<String> filterdWord = new ArrayList<>();
            filterdWord.clear();
            for (int i = 0;i<filterd.get(fIndex).length();i++){
                filterdWord.add(filterd.get(fIndex).subSequence(i,i+1).toString());
            }
            if (textInput.containsAll(filterdWord)){
                filterdLast.add(filterd.get(fIndex));
                recycleSearchResultAdapter.notifyDataSetChanged();
            }
        }
        recycleSearchResultAdapter.notifyDataSetChanged();
        loadingText.setVisibility(View.GONE);
    }


}
