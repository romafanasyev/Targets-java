package com.example.roman.targets;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.MenuItem;

import org.json.JSONException;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static DBHelper db;
    public ActionMode actionMode;
    enum Section
    {
        Main, Personal, Work, Notifications, More
    }

    private static Section section;
    private static Context applicationContext;
    private static Context activityContext;
    public static MainActivity activity;

    public static ArrayList<Page> allPagesList = new ArrayList<>();
    public static boolean currentSection = true; // true - personal, false - work

    public static Fragment mainFragment = new CardsFragment();
    public static Fragment personalFragment = new PersonalFragment();
    public static Fragment workFragment = new WorkFragment();
    public static Fragment notificationsFragment = new NotificationsFragment();
    public static Fragment moreFragment = new MoreFragment();
    public static Fragment currentFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_main:
                    navigate(mainFragment);
                    section = Section.Main;
                    return true;
                case R.id.navigation_personal:
                    navigate(personalFragment);
                    section = Section.Personal;
                    currentSection = true;
                    return true;
                case R.id.navigation_work:
                    navigate(workFragment);
                    section = Section.Work;
                    currentSection = false;
                    return true;
                case R.id.navigation_notifications:
                    navigate(notificationsFragment);
                    section = Section.Notifications;
                    return true;
                case R.id.navigation_more:
                    navigate(moreFragment);
                    section = Section.More;
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        applicationContext = getApplicationContext();
        activityContext = this;
        activity = this;
        db = new DBHelper(this);
        try {
            allPagesList=db.getAllPages();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (allPagesList.isEmpty()) {
            allPagesList.add(new Page(0, "", true));
            try {
                db.addPage(MainActivity.allPagesList.get(0));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mainFragment = CardsFragment.newInstance(0);

        navigate(mainFragment);
        section = Section.Main;

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    //navigation
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            if (currentFragment instanceof CardsFragment)
            {
                if (section == Section.Personal) {
                    personalFragment = new PersonalFragment();
                    navigate(personalFragment);
                    return false;
                }
                if (section == Section.Work) {
                    workFragment = new WorkFragment();
                    navigate(workFragment);
                    return false;
                }
            }
            return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    public void navigate(Fragment fragment)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        currentFragment = fragment;
        transaction.replace(R.id.navigation_content, fragment).commit();
    }
    public void showActionMode (ActionMode.Callback c)
    {
        actionMode = startActionMode(c);
    }

    public static Context mainContext()
    {
        return applicationContext;
    }
    public static Context getActivityContext() { return activityContext; }
}

