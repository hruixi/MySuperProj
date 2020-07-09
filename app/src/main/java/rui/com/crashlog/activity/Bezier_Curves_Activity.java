package rui.com.crashlog.activity;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import rui.com.crashlog.R;
import rui.com.crashlog.fragment.BezierTestFragment;
import rui.com.crashlog.fragment.BezierTestFragment2;
import rui.com.crashlog.fragment.PathAnimFragment;
import rui.com.crashlog.fragment.ValueAnimFragment;
import rui.com.crashlog.fragment.WaveBezierFragment;
import rui.com.crashlog.fragment.WritingFragment;
import rui.com.crashlog.widget.No_Scrollable_ViewPager;

public class Bezier_Curves_Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private No_Scrollable_ViewPager fragmentsPager = null;
    private FragmentsAdapter fragmentsAdapter = null;

    /** 小伙伴们，框架已建好，所有的贝塞尔曲线fragment在此添加 **/
    ValueAnimFragment valueAnim = null;
    WritingFragment writing = null;
    BezierTestFragment bezierTest = null;
    BezierTestFragment2 bezierTest2 = null;
    WaveBezierFragment waveBezier = null;
    PathAnimFragment pathAnim = null;

    private List<android.support.v4.app.Fragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_bezier_curves);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //设置可用的toolbar，这是Activity的函数
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initView();
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.bezier__curves_, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            if (writing != null)
                writing.clear();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        /** 小伙伴们，框架已建好，所有的贝塞尔曲线的效果在此点击切换 **/
        if (id == R.id.nav_camera) {
            fragmentsPager.setCurrentItem(0);
        } else if (id == R.id.nav_gallery) {
            fragmentsPager.setCurrentItem(1);
        } else if (id == R.id.nav_slideshow) {
            fragmentsPager.setCurrentItem(2);
        } else if (id == R.id.nav_manage) {
            fragmentsPager.setCurrentItem(3);
        } else if (id == R.id.nav_share) {
            fragmentsPager.setCurrentItem(4);
        } else if (id == R.id.nav_send) {
            fragmentsPager.setCurrentItem(5);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void initView()
    {
        /** 小伙伴们，框架已建好，所有的贝塞尔曲线的效果在此添加即可 **/
        valueAnim = ValueAnimFragment.newInstance();
        fragments.add(valueAnim);
        writing = WritingFragment.newInstance();
        fragments.add(writing);
        bezierTest = BezierTestFragment.newInstance();
        fragments.add(bezierTest);
        bezierTest2 = BezierTestFragment2.newInstance();
        fragments.add(bezierTest2);
        waveBezier = WaveBezierFragment.newInstance();
        fragments.add(waveBezier);
        pathAnim = PathAnimFragment.newInstance();
        fragments.add(pathAnim);

        fragmentsPager = findViewById(R.id.fragments_pager);
        fragmentsPager.setOffscreenPageLimit(fragments.size());
        fragmentsAdapter = new FragmentsAdapter(getSupportFragmentManager(), fragments);
        fragmentsPager.setAdapter(fragmentsAdapter);
    }

    public class FragmentsAdapter extends FragmentPagerAdapter
    {
        private List<android.support.v4.app.Fragment> fragments;

        public FragmentsAdapter(FragmentManager fm, List<android.support.v4.app.Fragment> fragments)
        {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position)
        {
            return fragments.get(position);
        }
    }

}
