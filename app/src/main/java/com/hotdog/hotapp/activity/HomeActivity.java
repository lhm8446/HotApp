package com.hotdog.hotapp.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hotdog.hotapp.R;
import com.hotdog.hotapp.fragment.home.BlogFragment;
import com.hotdog.hotapp.fragment.home.FetchCaptureFragment;
import com.hotdog.hotapp.fragment.home.HomeFragment;
import com.hotdog.hotapp.fragment.home.MypageMainFragment;
import com.hotdog.hotapp.fragment.home.SettingFragment;
import com.hotdog.hotapp.fragment.home.StreamSecFragment;
import com.hotdog.hotapp.fragment.home.StreamStartFragment;
import com.hotdog.hotapp.other.CircleTransform;
import com.hotdog.hotapp.other.Util;
import com.hotdog.hotapp.other.network.SafeAsyncTask;
import com.hotdog.hotapp.service.PiService;
import com.hotdog.hotapp.service.UserService;
import com.hotdog.hotapp.vo.PetVo;
import com.hotdog.hotapp.vo.PiVo;
import com.hotdog.hotapp.vo.UserVo;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FragmentManager fm;
    private UserVo userVo;
    private int users_no;
    private PetVo petVo;
    private UserService userService;
    private PiService piService;
    private Menu menu;
    private MenuItem nav_home;
    private NavigationView navigationView;
    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile, img_pet;
    private TextView txtName, txtWebsite;
    private Toolbar toolbar;
    public DrawerLayout drawer;
    // urls to load navigation header background image
    // and profile image
    private static final String NAVHEAD = "http://68.media.tumblr.com/tumblr_lxhocbDywJ1qc0kfg.jpg";
    private static final String URLIMG = "http://150.95.141.66:80/hotdog/hotdog/image/user/";

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private String callback;
    public static String CURRENT_TAG;
    private String HOME_TAG = "nav_home";
    private String STREAM_TAG = "nav_streaming";
    private String PAGE_TAG = "nav_page";
    private String SETTING_TAG = "nav_settings";
    // toolbar titles respected to selected nav menu item


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        init();
        dataInit();
        Util.checkAudioPermission(this);
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        drawer.setVisibility(View.GONE);
        // Navigation view header
        navHeader = navigationView.getHeaderView(0);

        txtName = (TextView) navHeader.findViewById(R.id.name);
        txtWebsite = (TextView) navHeader.findViewById(R.id.website);
        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);
        img_pet = (ImageView) navHeader.findViewById(R.id.img_pet);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(HomeActivity.this);
        menu = navigationView.getMenu();
        nav_home = menu.findItem(R.id.nav_home);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        fm = getSupportFragmentManager();
        userService = new UserService();
    }

    private void dataInit() {
        // 회원 로그인정보 불러오기
        Intent intent = getIntent();
        users_no = intent.getIntExtra("userNo", -1);
        callback = intent.getStringExtra("callback");
        new UserGetAsyncTask().execute();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        FrameLayout Home = (FrameLayout) findViewById(R.id.frame_home);
        FrameLayout blog = (FrameLayout) findViewById(R.id.frame_blog);
        if (blog != null) {
            if ("http://150.95.141.66/hotdog/".equals(BlogFragment.mWebView.getUrl())) {
                nav_home.setChecked(true);
                super.onBackPressed();
            } else {
                BlogFragment.mWebView.goBack();
            }
        } else {
            if (Home != null) {
                finish();
            } else if (fm.getBackStackEntryCount() == 2) {
                nav_home.setChecked(true);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    super.onBackPressed();
                }
            } else {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    super.onBackPressed();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            Util.logout(getApplicationContext());
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            popClear();
            CURRENT_TAG = HOME_TAG;
            callback = "";
            new UserGetAsyncTask().execute();
        } else if (id == R.id.nav_streaming) {
            popClear();
            CURRENT_TAG = STREAM_TAG;
            new SecPassChkAsyncTask().execute();
        } else if (id == R.id.nav_vod) {
            startActivity(new Intent(HomeActivity.this, VodActivity.class));
        } else if (id == R.id.nav_gallery) {
            popClear();
            Util.changeHomeFragment(getSupportFragmentManager(), new FetchCaptureFragment(), PAGE_TAG);
        } else if (id == R.id.nav_page) {
            popClear();
            CURRENT_TAG = PAGE_TAG;
            Util.changeHomeFragment(getSupportFragmentManager(), new MypageMainFragment(), PAGE_TAG);
        } else if (id == R.id.nav_settings) {
            popClear();
            CURRENT_TAG = SETTING_TAG;
            Util.changeHomeFragment(getSupportFragmentManager(), new SettingFragment(), SETTING_TAG);
        } else if (id == R.id.nav_web_blog) {
            popClear();
            Util.changeHomeFragment(getSupportFragmentManager(), new BlogFragment());
        } else if (id == R.id.nav_about_us) {
            popClear();
            startActivity(new Intent(HomeActivity.this, AboutUsActivity.class));
            drawer.closeDrawers();
        } else if (id == R.id.nav_streaming_camera) {
            popClear();
            startActivity(new Intent(HomeActivity.this, StreamingActivity.class));
            drawer.closeDrawers();
        } else {
            popClear();
            Util.changeHomeFragment(getSupportFragmentManager(), new HomeFragment());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        loadHomeFragment();
        return true;
    }

    private void popClear() {
        fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fm.beginTransaction().add(R.id.frame, new HomeFragment()).addToBackStack(null).commit();
    }


    public void loadNavHeader() {
        userVo = Util.getUserVo(getApplicationContext());
        petVo = Util.getPetVo(getApplicationContext());
        txtName.setText(userVo.getNickname() + "님");
        txtWebsite.setText("프로필");

        String urlProfileImg = URLIMG + userVo.getUsers_image();
        String urlPetImg = URLIMG + petVo.getPet_image();

        // loading header background image
        Glide.with(this).load(NAVHEAD)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgNavHeaderBg);

        // Loading profile image
        Glide.with(this).load(urlProfileImg)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProfile);

        // Loading profile image
        Glide.with(this).load(urlPetImg)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(img_pet);


        // showing dot next to notifications label
    }

    public void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();
        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            return;
        }
        //Closing drawer on item click
        drawer.closeDrawers();
        // refresh toolbar menu
        invalidateOptionsMenu();

    }

    public int getUsers_no() {
        return users_no;
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    // getUser 통신
    private class UserGetAsyncTask extends SafeAsyncTask<UserVo> {
        @Override
        public UserVo call() throws Exception {

            return userService.getUser(users_no);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            System.out.println("-------------------- getUser 에러 ------------------- " + e);
        }

        @Override
        protected void onSuccess(UserVo userVo) throws Exception {

            Util.setUserVo(getApplicationContext(), userVo);
            new PetGetAsyncTask().execute();
        }
    }

    //getPet
    private class PetGetAsyncTask extends SafeAsyncTask<PetVo> {
        @Override
        public PetVo call() throws Exception {

            return userService.getPet(users_no);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            System.out.println("-------------------- getPet 에러 ------------------- " + e);
        }

        @Override
        protected void onSuccess(PetVo petVo) throws Exception {
            Util.setPetVo(getApplicationContext(), petVo);
            new GetPiInfoAsyncTask().execute();
            loadNavHeader();

            if ("mypage".equals(callback)) {
                Util.changeHomeFragment(getSupportFragmentManager(), new MypageMainFragment(), PAGE_TAG);
                loadHomeFragment();
            } else {
                Util.changeHomeFragment(getSupportFragmentManager(), new HomeFragment(), HOME_TAG);
                loadHomeFragment();
            }
            drawer.setVisibility(View.VISIBLE);
            drawer.setBackgroundResource(R.drawable.mainbg);

        }
    }

    //이차 비밀번호 유무
    private class SecPassChkAsyncTask extends SafeAsyncTask<String> {
        @Override
        public String call() throws Exception {
            // 통신 완료 후 리턴값 저장
            userService = new UserService();
            return userService.chkSecPass(userVo);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
        }

        @Override
        protected void onSuccess(String flag) throws Exception {
            if ("exist".equals(flag)) {
                Util.changeHomeFragment(getSupportFragmentManager(), new StreamSecFragment(), STREAM_TAG);
            } else if ("first".equals(flag)) {
                Util.changeHomeFragment(getSupportFragmentManager(), new StreamStartFragment(), STREAM_TAG);
            }
        }
    }

    //pi 정보 받기
    private class GetPiInfoAsyncTask extends SafeAsyncTask<PiVo> {
        @Override
        public PiVo call() throws Exception {
            piService = new PiService();
            return piService.getinfo(users_no);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
        }

        @Override
        protected void onSuccess(PiVo piVo) throws Exception {
            Util.setPiVo(getApplicationContext(), piVo);
        }
    }

}