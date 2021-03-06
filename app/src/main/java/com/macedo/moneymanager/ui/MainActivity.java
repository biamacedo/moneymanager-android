package com.macedo.moneymanager.ui;

import android.app.FragmentManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.macedo.moneymanager.R;
import com.macedo.moneymanager.ui.fragments.AccountsFragment;
import com.macedo.moneymanager.ui.fragments.DailyOperationsFragment;
import com.macedo.moneymanager.ui.fragments.DashboardFragment;
import com.macedo.moneymanager.ui.fragments.MonthlyOperationsFragment;
import com.macedo.moneymanager.ui.fragments.NavigationDrawerFragment;
import com.macedo.moneymanager.ui.fragments.ReminderFragment;
import com.macedo.moneymanager.ui.fragments.SettingsFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        AccountsFragment.OnFragmentInteractionListener,
        ReminderFragment.OnFragmentInteractionListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private int mFragmentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the menu_navigation_drawer content by replacing fragments
        String[] drawerStrings = getResources().getStringArray(R.array.drawer_list);
        FragmentManager fragmentManager = getFragmentManager();
        mFragmentPosition = position;
        switch (mFragmentPosition) {
            case 0:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, DashboardFragment.newInstance())
                        .commit();
                mTitle = drawerStrings[mFragmentPosition];
                break;
            case 1:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, AccountsFragment.newInstance())
                        .commit();
                mTitle = drawerStrings[mFragmentPosition];
                break;
            case 2:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, DailyOperationsFragment.newInstance())
                        .commit();
                mTitle = drawerStrings[mFragmentPosition];
                break;
            case 3:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, MonthlyOperationsFragment.newInstance())
                        .commit();
                mTitle = drawerStrings[mFragmentPosition];
                break;
            case 4:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, ReminderFragment.newInstance())
                        .commit();
                mTitle = drawerStrings[mFragmentPosition];
                break;
            case 5:
                Toast.makeText(this, "Fragment Pro Features not Found", Toast.LENGTH_LONG).show();
                break;
            case 6:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, SettingsFragment.newInstance())
                        .commit();
                mTitle = drawerStrings[mFragmentPosition];
                break;
            default:
                Toast.makeText(this, "Fragment Default not Found", Toast.LENGTH_LONG).show();
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.menu_navigation_drawer, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_daily_expenses) {
            Intent intent = new Intent(MainActivity.this, DailyOperationsFragment.class);
            startActivity(intent);
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
