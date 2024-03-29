package com.example.roman.targets;

import android.content.Context;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    public static DBHelper db;
    public ActionMode actionMode;
    enum Section
    {
        Main, Personal, Work, Notifications, More
    }

    public static Section section;
    private static Context applicationContext;
    private static Context activityContext;
    public static MainActivity activity;

    public static ArrayList<Page> allPagesList = new ArrayList<>();
    public static ArrayList<Point> allPointsList = new ArrayList<>();
    public static boolean currentSection = true; // true - personal, false - work

    public static Fragment mainFragment = new CardsFragment();
    public static Fragment personalFragment = new PersonalFragment();
    public static Fragment workFragment = new WorkFragment();
    public static Fragment notificationsFragment = new NotificationsFragment();
    public static Fragment moreFragment = new PreferencesFragment();
    public static Fragment currentFragment;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_main:
                    section = Section.Main;
                    navigate(mainFragment);
                    return true;
                case R.id.navigation_personal:
                    section = Section.Personal;
                    navigate(personalFragment);
                    currentSection = true;
                    return true;
                case R.id.navigation_work:
                    section = Section.Work;
                    navigate(workFragment);
                    currentSection = false;
                    return true;
                case R.id.navigation_more:
                    section = Section.More;
                    navigate(moreFragment);
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
        allPagesList=db.getAllPages();
        allPointsList=db.getAllPoints();

        if (allPagesList.isEmpty()) {
            allPagesList.add(new Page(0, "", true));
            db.addPage(MainActivity.allPagesList.get(0));
        }
        mainFragment = CardsFragment.newInstance(0);

        section = Section.Main;
        navigate(mainFragment);
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
            if (currentFragment instanceof CardEditFragment)
            {
                int id = ((CardEditFragment) currentFragment).pageID;
                navigate(CardsFragment.newInstance(id));
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void back() {
        if (currentFragment instanceof CardsFragment)
        {
            if (section == Section.Personal) {
                personalFragment = new PersonalFragment();
                navigate(personalFragment);
            }
            if (section == Section.Work) {
                workFragment = new WorkFragment();
                navigate(workFragment);
            }
            if (section == Section.Main)
                finish();
        }
        if (currentFragment instanceof CardEditFragment)
        {
            int id = ((CardEditFragment) currentFragment).pageID;
            navigate(CardsFragment.newInstance(id));
        }
    }

    public void navigate(Fragment fragment)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        currentFragment = fragment;
        switch (section){
            case Main:
                mainFragment = fragment;
                break;
            case Personal:
                personalFragment = fragment;
                break;
            case Work:
                workFragment = fragment;
                break;
            case Notifications:
                notificationsFragment = fragment;
                break;
            case More:
                moreFragment = fragment;
                break;
        }
        transaction.replace(R.id.navigation_content, fragment).commit();
    }
    public void showActionMode (ActionMode.Callback c)
    {
        actionMode = startActionMode(c);
    }

    public static Context applicationContext()
    {
        return applicationContext;
    }

    public static Context activityContext() {
        return activityContext;
    }
}

