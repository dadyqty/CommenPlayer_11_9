package com.d.commenplayer.activity;

import android.app.Instrumentation;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import com.d.commenplayer.R;
import com.d.commenplayer.fragment.Fragment1;
import com.d.commenplayer.fragment.Fragment2;
import com.d.commenplayer.fragment.Fragment3;
import com.d.commenplayer.fragment.MyFragmentPagerAdapter;
import java.util.ArrayList;


public class MsgBox extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ArrayList<String> tab_title_list = new ArrayList<>();//存放标签页标题
    private ArrayList<Fragment> fragment_list = new ArrayList<>();//存放ViewPager下的Fragment
    private Fragment fragment1, fragment2, fragment3;
    private MyFragmentPagerAdapter adapter;//适配器
    public boolean posplusflag = true;

    private int count_changlanguage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_box);
        Intent intent = getIntent();

        posplusflag = false;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("信箱");
        setSupportActionBar(toolbar);
        tabLayout = (TabLayout) findViewById(R.id.my_tablayout);

        viewPager = (ViewPager) findViewById(R.id.my_viewpager);
        tab_title_list.add("收件箱");
        tab_title_list.add("发件箱");
        tab_title_list.add("草稿箱");

        tabLayout.addTab(tabLayout.newTab().setText(tab_title_list.get(0)));
        tabLayout.addTab(tabLayout.newTab().setText(tab_title_list.get(1)));
        tabLayout.addTab(tabLayout.newTab().setText(tab_title_list.get(2)));


        fragment1 = new Fragment1();
        fragment2 = new Fragment2();
        fragment3 = new Fragment3();

        fragment_list.add(fragment1);
        fragment_list.add(fragment2);
        fragment_list.add(fragment3);

        count_changlanguage= Integer.valueOf(intent.getStringExtra("count_changlanguage"));
        int select_tablelayout = intent.getIntExtra("select_tablelayout",0);

        tabLayout.setSelectedTabIndicatorHeight(1);

        adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), tab_title_list, fragment_list);

        viewPager.setAdapter(adapter);//给ViewPager设置适配器
        tabLayout.setupWithViewPager(viewPager);//将TabLayout与Viewpager联动起来
        tabLayout.setTabsFromPagerAdapter(adapter);//给TabLayout设置适配器
        viewPager.setCurrentItem(select_tablelayout);

//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                Log.e("TAG","tab.getPosition()="+tab.getPosition());
//
//
//                switch (tab.getPosition()){
//                    case 0:
//                        // 步骤1：获取FragmentManager
//                        FragmentManager fragmentManager = getFragmentManager();
//                        // 步骤2：获取FragmentTransaction
//                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                        // 步骤4:创建Bundle对象
//                        // 作用:存储数据，并传递到Fragment中
//                        Bundle bundle = new Bundle();
//                        // 步骤5:往bundle中添加数据
//                        bundle.putString("message", String.valueOf(count_changlanguage) );
//                        // 步骤6:把数据设置到Fragment中
//                        fragment1.setArguments(bundle);
//                        fragmentTransaction.commit();
//                        break;
//                    case 1:
//                        break;
//                    case 2:
//                        break;
//                    default:
//                        break;
//                }
//
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event){
        if ((event.getKeyCode() == 21 ||event.getKeyCode() == 22) && event.getAction() == KeyEvent.ACTION_UP){
            sendKeyCode(66);
        }
        if ((event.getKeyCode() == 20 ) && event.getAction() == KeyEvent.ACTION_UP) {
            sendKeyCode(66);
        }
        if ((event.getKeyCode() == 19 ) && event.getAction() == KeyEvent.ACTION_UP) {
            sendKeyCode(66);
        }
        if ((event.getKeyCode() == 216 || event.getKeyCode() == 48 ) && event.getAction() == KeyEvent.ACTION_UP){
            //保存一个结果码
            int resultCode=3;
            //准备一个带额外数据的intent对象
            Intent data=new Intent();
            String result=String.valueOf(count_changlanguage);
            data.putExtra("RESULT",result);
            //设置结果
            setResult(resultCode,data);
            finish();
        }
        if (event.getKeyCode()== 62 && event.getAction() == KeyEvent.ACTION_UP){  //归中键
            getCurrentFocus().performLongClick();
        }
        Log.i("TAG", "dispatchKeyEvent:" + event.getKeyCode());
        return super.dispatchKeyEvent(event);
    }



    private void sendKeyCode(final int keyCode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 创建一个Instrumentation对象
                    Instrumentation inst = new Instrumentation();
                    // 调用inst对象的按键模拟方法
                    inst.sendKeyDownUpSync(keyCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    } //按键转义为 键盘的按键

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}