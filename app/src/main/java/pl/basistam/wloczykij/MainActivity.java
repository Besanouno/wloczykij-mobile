package pl.basistam.wloczykij;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
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
import android.widget.TextView;

import pl.basistam.wloczykij.auth.AccountGeneral;
import pl.basistam.wloczykij.auth.LoggedUser;
import pl.basistam.wloczykij.components.utils.KeyboardUtils;
import pl.basistam.wloczykij.fragments.WeatherFragment;
import pl.basistam.wloczykij.fragments.events.EventsFragment;
import pl.basistam.wloczykij.fragments.MapViewFragment;
import pl.basistam.wloczykij.fragments.UserFragment;
import pl.basistam.wloczykij.fragments.events.PublicEventsFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private MapViewFragment mapFragment = new MapViewFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        final View header = navigationView.getHeaderView(0);
        TextView textView = (TextView) header.findViewById(R.id.tv_name);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
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

        mapFragment.initSearchField((SearchView) searchItem.getActionView());
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentManager fragmentManager = getFragmentManager();
        View searchPanel = findViewById(R.id.action_search);
        searchPanel.setVisibility(View.GONE);

        KeyboardUtils.hide(this, getCurrentFocus());

        int id = item.getItemId();
        if (id == R.id.nav_account ) {
            openTab(new UserFragment());
        }
        else if (id == R.id.nav_map) {
            fragmentManager.beginTransaction().replace(R.id.content, mapFragment).commit();
            searchPanel.setVisibility(View.VISIBLE);
        } else if (id == R.id.nav_events) {
            openTab(new EventsFragment());
        } else if (id == R.id.nav_manage_account) {
            fragmentManager.beginTransaction().replace(R.id.content, mapFragment).commit();
            searchPanel.setVisibility(View.VISIBLE);
            showAccountPicker();
        } else if (id == R.id.nav_public_events) {
            openTab(new PublicEventsFragment());
        } else if (id == R.id.nav_weather) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.content, new WeatherFragment())
                    .addToBackStack(null)
                    .commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openTab(Fragment fragment) {
        if (LoggedUser.getInstance().getAccount() == null) {
            signIn();
        } else {
            getFragmentManager().beginTransaction()
                    .replace(R.id.content, fragment)
                    .addToBackStack(null)
                    .commit();
        }
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

    public void signIn() {
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
