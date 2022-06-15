package com.example.practiceset2.activities

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
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
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    //private var sharedPreferences: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        if (!readThemeFromPreference(this@MainActivity))AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO) else AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
        super.onCreate(savedInstanceState)
//        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
//        sharedPreferences?.registerOnSharedPreferenceChangeListener { sharedPreferences, s ->
            //setTheme(if(readThemeFromPreference(this@MainActivity)) R.style.Theme_PracticeSet2 else R.style.Theme_PracticeSet2Night )
//            Log.e("reg", "reg is called ${sharedPreferences.getBoolean("colorMode", true)}")
//            //setAppTheme(this, if(readThemeFromPreference(this)) R.style.Theme_PracticeSet2 else R.style.Theme_PracticeSet2Night )
//        }
        //sharedPreferences?.registerOnSharedPreferenceChangeListener { sharedPreferences, s ->  }
        //setTheme(if(readThemeFromPreference(this)) R.style.Theme_PracticeSet2 else R.style.Theme_PracticeSet2Night)
        val binding = DataBindingUtil
            .setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)
        drawerLayout = binding.drawerLayout
        //val navController = this.findNavController(R.id.nav_host_fragment)
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
        //setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.overflow_menu, menu)
        return true
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when(item.itemId){
//            R.id.settingsFragment-> {
//                this.findNavController(R.id.nav_host_fragment).navigate(R.id.open_settings_fragment)
//                true
//            }
//
//            else -> {
//                super.onOptionsItemSelected(item)
//            }
//        }
//    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.nav_host_fragment)
        //return navController.navigateUp()
        return NavigationUI.navigateUp(navController, drawerLayout)

//        return when(navController.currentDestination?.id) {
//            R.id.settingsFragment -> {
//                // custom behavior here
//                Log.e("set", "settings")
//                //navController.popBackStack()
//                navController.po
//                true
//            }
//            else -> NavigationUI.navigateUp(navController, drawerLayout)
//        }
    }

    fun readThemeFromPreference(context: Context): Boolean{
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean("colorMode", false)
    }

    fun setAppTheme(context: AppCompatActivity, theme: Int){
        //context.setTheme(theme)
//        AppCompatDelegate.setDefaultNightMode(if (isLight) AppCompatDelegate.MODE_NIGHT_NO else AppCompatDelegate.MODE_NIGHT_YES)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        context.recreate()
    }
}