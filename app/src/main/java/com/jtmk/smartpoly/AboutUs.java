package com.jtmk.smartpoly;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Fade;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.smartpoly.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class AboutUs extends FragmentActivity {
    FirebaseAuth auth;
    TextView usernameTxt;
    ImageButton settingBtn;
    FloatingActionButton chatbotBtn;
    private TabLayout tabLayout;
    private SecondFragmentAdapter adapter;
    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 2;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager2 viewPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private FragmentStateAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);



        BottomNavigationView bottomNav = (BottomNavigationView) findViewById(R.id.bottom_nav);
        // Set Selected Id
        bottomNav.setSelectedItemId(R.id.aboutus);
        Menu menu=bottomNav.getMenu();
        auth=FirebaseAuth.getInstance();
        settingBtn=(ImageButton) findViewById(R.id.setting);
        usernameTxt=(TextView)findViewById(R.id.username);
        chatbotBtn=(FloatingActionButton)findViewById(R.id.chatbotB);

        chatbotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chatbot = new Intent(AboutUs.this, MainActivity.class);
                startActivity(chatbot);
                overridePendingTransition(R.anim.from_right_in, R.anim.from_left_out);
                finish();
            }
        });

        Fade fade=new Fade();
        View decor=getWindow().getDecorView();
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);

        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);

        tabLayout=(TabLayout)findViewById(R.id.tabLayout2);
        tabLayout.addTab(tabLayout.newTab().setText("Course"));
        tabLayout.addTab(tabLayout.newTab().setText("Track"));
        tabLayout.addTab(tabLayout.newTab().setText("Staff"));

        // Instantiate a ViewPager2 and a PagerAdapter.
        viewPager = findViewById(R.id.pager);

        FragmentManager fragmentManager=getSupportFragmentManager();
        adapter=new SecondFragmentAdapter(fragmentManager, getLifecycle());
        viewPager.setAdapter(adapter);

        viewPager.setPageTransformer(new ZoomOutPageTransformer());

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

        setting();


        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.introduction:
                        Intent intro = new Intent(AboutUs.this, Intro.class);
                        startActivity(intro);
                        overridePendingTransition(R.anim.from_right_in, R.anim.from_left_out);
                        finish();
                        break;

                    case R.id.activity:
                        Intent activity = new Intent(AboutUs.this, Activities.class);
                        startActivity(activity);
                        overridePendingTransition(R.anim.from_right_in, R.anim.from_left_out);
                        finish();
                        break;

                    case R.id.staff:
                        Intent staff = new Intent(AboutUs.this, Staff.class);
                        startActivity(staff);
                        overridePendingTransition(R.anim.from_right_in, R.anim.from_left_out);
                        finish();
                        break;

                    case R.id.aboutus:
                        break;
                }
                return true;
            }
        });
    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.from_left_in, R.anim.from_right_out);
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }


    private void setting()
    {
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(auth!=null) {
                    Intent intent=new Intent(AboutUs.this, SettingActivity.class);
                    ActivityOptionsCompat options=ActivityOptionsCompat.makeSceneTransitionAnimation(AboutUs.this, settingBtn, ViewCompat.getTransitionName(settingBtn));
                    startActivity(intent, options.toBundle());
                }else {
                    Toast.makeText(AboutUs.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

