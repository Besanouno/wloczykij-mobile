package pl.basistam.turysta;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

import pl.basistam.turysta.auth.AccountGeneral;
import pl.basistam.turysta.auth.LoggedUser;
import pl.basistam.turysta.components.utils.KeyboardUtils;
import pl.basistam.turysta.fragments.EventFragment;
import pl.basistam.turysta.fragments.EventsFragment;
import pl.basistam.turysta.fragments.MapViewFragment;
import pl.basistam.turysta.fragments.UserFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private MapViewFragment mapFragment = new MapViewFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

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
        if (id == R.id.nav_account ) {
            fragmentManager.beginTransaction().replace(R.id.content, new UserFragment()).commit();
        }
        else if (id == R.id.nav_map) {
            fragmentManager.beginTransaction().replace(R.id.content, mapFragment).commit();
            searchPanel.setVisibility(View.VISIBLE);
        } else if (id == R.id.nav_event) {
            fragmentManager.beginTransaction().replace(R.id.content, new EventsFragment()).commit();
        } else if (id == R.id.nav_manage_account) {
            showAccountPicker();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void checkIfLoggedIn() {
        AccountManager accountManager = AccountManager.get(this);
        Account[] accounts = accountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
        if (accounts.length > 0) {
            prepareHeader(accounts[0].name);
        }
    }

    private void showAccountPicker() {
        final Account availableAccounts[] = AccountManager.get(this).getAccountsByType(AccountGeneral.ACCOUNT_TYPE);

        final int itemsNumber = availableAccounts.length + 1;
        final String items[] = new String[itemsNumber];
        for (int i = 0; i < availableAccounts.length; i++) {
            items[i] = availableAccounts[i].name;
        }
        items[itemsNumber - 1] = "Dodaj konto";
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Wybierz konto")
                .setAdapter(new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, items), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == itemsNumber - 1) {
                            signIn();
                        } else {
                            prepareHeader(availableAccounts[which].name);
                        }
                    }
                }).create();
        alertDialog.show();
    }

    private void prepareHeader(String name) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        final View header = navigationView.getHeaderView(0);
        ((TextView) header.findViewById(R.id.tv_name)).setText(name);
        LoggedUser.getInstance().setAccount(getAccountByLogin(name));
    }

    private void signIn() {
        final AccountManager accountManager = AccountManager.get(this);
        accountManager.addAccount(AccountGeneral.ACCOUNT_TYPE, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, null, null, this, new AccountManagerCallback<Bundle>() {
            @Override
            public void run(AccountManagerFuture<Bundle> future) {
                try {
                    String name = future.getResult().getString(AccountManager.KEY_ACCOUNT_NAME);
                    prepareHeader(name);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, null);
    }

    private Account getAccountByLogin(String login) {
        final AccountManager accountManager = AccountManager.get(this);
        Account[] accounts = accountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
        for (Account account: accounts) {
            if (account.name.equals(login)) {
                return account;
            }
        }
        return null;
    }
}
