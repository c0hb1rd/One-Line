package com.example.crackgg.dictnote;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    WebFragment webFragment;
    NavigationView navigationView;
    ListViewFragment fragment;
    MyDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("离线手册");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initialization();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }


    public void initialization() {
        db = new MyDatabase(this, "itemDB.db3", null, 1);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        fragment = new ListViewFragment();
        webFragment = new WebFragment();
        getFragmentManager().beginTransaction().replace(R.id.frame_main, fragment).commit();

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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webFragment.canGoback()) {
                webFragment.goBack();
                getFragmentManager().beginTransaction().replace(R.id.frame_main, webFragment).commit();
            } else {
                if(getFragmentManager().findFragmentById(R.id.frame_main).equals(fragment)) {
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "已倒退到最后一个页面", Toast.LENGTH_SHORT).show();
                }
            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_exit) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.sort_by_default) {
            fragment.displayListItems(fragment.SORT_NOTE, null);
            getFragmentManager().beginTransaction().replace(R.id.frame_main, fragment).commit();
            this.setTitle("按批注排序");
        } else if (id == R.id.sort_by_world) {
            fragment.displayListItems(fragment.SORT_INDEX, null);
            getFragmentManager().beginTransaction().replace(R.id.frame_main, fragment).commit();
            this.setTitle("按INDEX排序");
        } else if (id == R.id.sort_by_index_first) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this).setTitle("输入INDEX首位");
            View view = getLayoutInflater().inflate(R.layout.choose_list, null);
            dialog.setView(view);
            final EditText editText = (EditText) view.findViewById(R.id.editText);
            editText.setSingleLine();
            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getFragmentManager().beginTransaction().replace(R.id.frame_main, fragment).commit();
                    fragment.displayListItems(fragment.SORT_INDEX_FIRST, editText.getText().toString());
                    MainActivity.this.setTitle("按INDEX首位\"" + editText.getText().toString() + "\"过滤");
                }
            });
            dialog.setNegativeButton("取消", null);
            dialog.create().show();
        } else if (id == R.id.sort_by_note_first) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this).setTitle("输入批注首位");
            View view = getLayoutInflater().inflate(R.layout.choose_list, null);
            dialog.setView(view);
            final EditText editText = (EditText) view.findViewById(R.id.editText);
            editText.setSingleLine();
            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getFragmentManager().beginTransaction().replace(R.id.frame_main, fragment).commit();
                    fragment.displayListItems(fragment.SORT_NOTE_FIRST, editText.getText().toString());
                    MainActivity.this.setTitle("按INDEX首位\"" + editText.getText().toString() + "\"过滤");
                }
            });
            dialog.setNegativeButton("取消", null);
            dialog.create().show();
        } else if (id == R.id.search_index) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this).setTitle("请输入要搜索的INDEX");
            View view = getLayoutInflater().inflate(R.layout.choose_list2, null);
            dialog.setView(view);
            final EditText editText = (EditText) view.findViewById(R.id.editText);
            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    fragment.displayListItems(fragment.SEARCH_INDEX, editText.getText().toString());
                    getFragmentManager().beginTransaction().replace(R.id.frame_main, fragment).commit();
                    MainActivity.this.setTitle("INDEX精确搜索\"" + editText.getText().toString() + "\"");
                }
            });
            dialog.setNegativeButton("取消", null);
            dialog.create().show();
        } else if (id == R.id.search_note) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this).setTitle("请输入要搜索的批注");
            View view = getLayoutInflater().inflate(R.layout.choose_list2, null);
            dialog.setView(view);
            final EditText editText = (EditText) view.findViewById(R.id.editText);
            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    fragment.displayListItems(fragment.SEARCH_NOTE, editText.getText().toString());
                    getFragmentManager().beginTransaction().replace(R.id.frame_main, fragment).commit();
                    MainActivity.this.setTitle("批注精确搜索\"" + editText.getText().toString() + "\"");
                }
            });
            dialog.setNegativeButton("取消", null);
            dialog.create().show();
        } else if (id == R.id.online_search) {
            navigationView.getMenu().getItem(0).setEnabled(false);
            navigationView.getMenu().getItem(1).setEnabled(false);
            navigationView.getMenu().getItem(2).setEnabled(false);
            navigationView.getMenu().getItem(3).setEnabled(false);
            navigationView.getMenu().getItem(4).setEnabled(false);
            navigationView.getMenu().getItem(5).setEnabled(false);
            getFragmentManager().beginTransaction().replace(R.id.frame_main, webFragment).commit();
            this.setTitle("在线查询");
        } else if (id == R.id.offline_document) {
            getFragmentManager().beginTransaction().replace(R.id.frame_main, fragment).commit();
            navigationView.getMenu().getItem(0).setEnabled(true);
            navigationView.getMenu().getItem(1).setEnabled(true);
            navigationView.getMenu().getItem(2).setEnabled(true);
            navigationView.getMenu().getItem(3).setEnabled(true);
            navigationView.getMenu().getItem(4).setEnabled(true);
            navigationView.getMenu().getItem(5).setEnabled(true);
            this.setTitle("离线手册");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return false;
    }
}

