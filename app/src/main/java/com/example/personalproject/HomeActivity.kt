package com.example.personalproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        var bnv_main = findViewById(R.id.bnv_main) as BottomNavigationView

        bnv_main.run { setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.item_1 -> {
                    // 다른 프래그먼트 화면으로 이동하는 기능
                    val userId = intent.getStringExtra(MainActivity.EXT_ID).toString()
                    val userPwd = intent.getStringExtra(MainActivity.EXT_PWD).toString()
                    val homeFragment = HomeFragment.newInstance(userId, userPwd)
                    supportFragmentManager.beginTransaction().replace(R.id.fl_container, homeFragment).commit()
                }
                R.id.item_2 -> {
                    val calendarFragment = CalendarFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.fl_container, calendarFragment).commit()
                }
                R.id.item_3 -> {
                    val userId = intent.getStringExtra(MainActivity.EXT_ID).toString()
                    val userPwd = intent.getStringExtra(MainActivity.EXT_PWD).toString()
                    val myFragment = MyFragment.newInstance(userId, userPwd)
                    supportFragmentManager.beginTransaction().replace(R.id.fl_container, myFragment).commit()
                }

            }
            true
        }
            selectedItemId = R.id.item_1
        }
    }
}