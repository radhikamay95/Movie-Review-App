package com.example.work.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.work.R;
import com.example.work.adapter.FragmentPageAdapter;
import com.example.work.database.UserPojo;
import com.example.work.database.UserPojoDao;
import com.example.work.fragments.MoviePaginationFragment1;
import com.example.work.fragments.MoviePaginationFragment2;
import com.example.work.fragments.MoviePaginationFragment3;
import com.example.work.utils.SessionInitializer;
import com.example.work.utils.SharePreference;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import static android.content.DialogInterface.OnClickListener;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ViewPager view;
    private DrawerLayout dl;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private String profileImg;
    private static final int GALLERY_REQUEST_CODE = 101;
    private ImageView profileImage;


    @SuppressLint("CutPasteId")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TabLayout tab = findViewById(R.id.tabs);
        view = findViewById(R.id.viewPager);
        dl = findViewById(R.id.drawer);
        NavigationView nv = findViewById(R.id.nav_view);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, dl, R.string.open, R.string.close);

        dl.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //navigation menu
        nv.setNavigationItemSelectedListener(this);


        //fragment part
        FragmentPageAdapter fragmentPageAdapter = new FragmentPageAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        if (view != null) {
            view.setAdapter(fragmentPageAdapter);
            tab.setupWithViewPager(view);
            tab.setTabTextColors(Color.parseColor("#fafafa"), Color.parseColor("#ffffff"));
        }
        //no of fragments
        fragmentPageAdapter.addFragment(new MoviePaginationFragment1(), "1");
        fragmentPageAdapter.addFragment(new MoviePaginationFragment2(), "2");
        fragmentPageAdapter.addFragment(new MoviePaginationFragment3(), "3");

         UserPojo userPojo;
         UserPojoDao userPojoDao;

        String userId = SharePreference.getSharedPref(getApplicationContext(), "userId");
        userPojoDao=((SessionInitializer)getApplication()).getDaoSession().getUserPojoDao();
        userPojo=userPojoDao.queryBuilder().where(UserPojoDao.Properties.Userid.eq(userId)).build().unique();


        if(userPojo!=null)
            profileImg = userPojo.getConfirmPassword();

        NavigationView navigationView = findViewById(R.id.nav_view);

        View hView = navigationView.getHeaderView(0);
        profileImage = hView.findViewById(R.id.profile);


        if(profileImg!=null) {
            Glide.with(MainActivity.this)
                    .load(Uri.parse(profileImg))
                    .into(profileImage);
        }
        else {
            Log.d("tag ","===========not=========");
        }
    }

    private void selectImageFromGallery() {

        Intent i = new Intent();
        i.setAction(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "Choose a image"), GALLERY_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == GALLERY_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImageFromGallery();
            } else {
                Toast.makeText(getApplicationContext(), "permission denied", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED && requestCode == GALLERY_REQUEST_CODE) {
            return;
        }
        if (data != null) {
            Uri selectedImage = data.getData();
            profileImage.setImageURI(selectedImage);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_menu_option, menu);

        return true;
    }

    //option menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        if (item.getItemId() == R.id.tab1) {
            logOut();

        }

        dl.closeDrawers();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (dl.isDrawerOpen(GravityCompat.START)) {
            dl.closeDrawer(GravityCompat.START);
        } else

            super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        switch (id) {
            case R.id.tab1:
                view.setCurrentItem(0);
                Toast.makeText(MainActivity.this, "Tab 1 !!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tab2:
                view.setCurrentItem(1);
                Toast.makeText(MainActivity.this, "Tab 2 !!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tab3:
                view.setCurrentItem(2);
                Toast.makeText(MainActivity.this, "Tab 3 !!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tab4:
                Intent intent = new Intent(this, EditProfile.class);
                startActivity(intent);
                break;


            default:
        }
        dl.closeDrawers();
        return true;
    }

    private void logOut() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Logout from App?");
        builder.setPositiveButton("OK",
                new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SharePreference.setSharedPref(getApplicationContext(), "logged", "logout");

                        Intent intent1 = new Intent(MainActivity.this, Login.class);
                        Toast.makeText(getApplicationContext(),
                                "Logged Out",
                                Toast.LENGTH_SHORT).show();
                        startActivity(intent1);
                        finish();
                    }
                });
        builder.setNegativeButton(android.R.string.no, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(),
                        android.R.string.no, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }


}
