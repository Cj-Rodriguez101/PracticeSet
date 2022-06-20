package com.example.practiceset2.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.practiceset2.R
import com.example.practiceset2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        if (!readThemeFromPreference(this@MainActivity)){
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
        } else {
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)}
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil
            .setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)
        drawerLayout = binding.drawerLayout
        val navController = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.findNavController()
        NavigationUI.setupActionBarWithNavController(this, navController!!, drawerLayout)
        NavigationUI.setupWithNavController(binding.navView, navController)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            if (destination.id == controller.graph.startDestinationId){
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            } else {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.overflow_menu, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.nav_host_fragment)
        return NavigationUI.navigateUp(navController, drawerLayout)
    }

    fun readThemeFromPreference(context: Context): Boolean{
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean("colorMode", false)
    }

    fun setAppTheme(context: AppCompatActivity){
        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
        context.recreate()
    }
}