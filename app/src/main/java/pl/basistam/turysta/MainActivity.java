package pl.basistam.turysta;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import pl.basistam.turysta.auth.AccountGeneral;
import pl.basistam.turysta.components.utils.KeyboardUtils;
import pl.basistam.turysta.fragments.EventFragment;
import pl.basistam.turysta.fragments.MapViewFragment;

import static pl.basistam.turysta.LoginActivity.ARG_ACCOUNT_TYPE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private MapViewFragment mapFragment = new MapViewFragment();
    private EventFragment eventFragment = new EventFragment();

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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        checkIfLoggedIn();

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().add(R.id.content, mapFragment).commit();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        mapFragment.initSearchField(this, (SearchView) searchItem.getActionView());
//        new SearchField(this, (SearchView) searchItem.getActionView(), map).initialize();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentManager fragmentManager = getFragmentManager();
        View searchPanel = findViewById(R.id.action_search);
        searchPanel.setVisibility(View.GONE);

        KeyboardUtils.hide(this, getCurrentFocus());

        int id = item.getItemId();
        if (id == R.id.nav_map) {
            fragmentManager.beginTransaction().replace(R.id.content, mapFragment).commit();
            searchPanel.setVisibility(View.VISIBLE);
        } else if (id == R.id.nav_event) {
            fragmentManager.beginTransaction().replace(R.id.content, eventFragment).commit();
        } else if (id == R.id.nav_log_in) {
            AccountManager accountManager = AccountManager.get(this);
            accountManager.addAccount(AccountGeneral.ACCOUNT_TYPE, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, null, null, this, new AccountManagerCallback<Bundle>() {
                @Override
                public void run(AccountManagerFuture<Bundle> future) {
                    try {
                        future.getResult();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, null);
        } else if (id == R.id.nav_sign_up) {
            Intent signup = new Intent(this, SignUpActivity.class);
            signup.putExtra(ARG_ACCOUNT_TYPE, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS);
            startActivity(signup);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void checkIfLoggedIn() {
        AccountManager accountManager = AccountManager.get(this);
        Account[] accounts = accountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (accounts.length > 0) {
            MenuItem item = navigationView.getMenu().findItem(R.id.account).getSubMenu().findItem(R.id.nav_unlog);
           /* navigationView.getMenu().getItem((R.id.nav_log_in)).setVisible(false);
            navigationView.getMenu().getItem((R.id.nav_sign_up)).setVisible(false);
            navigationView.getMenu().getItem((R.id.nav_unlog)).setVisible(true);*/
        } else {
          /*  navigationView.getMenu().getItem((R.id.nav_log_in)).setVisible(true);
            navigationView.getMenu().getItem((R.id.nav_sign_up)).setVisible(true);
            navigationView.getMenu().getItem((R.id.nav_unlog)).setVisible(false);*/
        }
        navigationView.invalidate();
    }

}
