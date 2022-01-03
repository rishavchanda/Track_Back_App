package com.example.trackback

import android.app.TaskStackBuilder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Switch
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.GravityCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.trackback.databinding.ActivityMainBinding
import com.example.trackback.databinding.FragmentDashboardBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import android.content.SharedPreferences
import android.content.Intent
import android.content.ActivityNotFoundException
import android.net.Uri
import android.widget.Toast
import androidx.core.content.ContextCompat
import kotlin.properties.Delegates
import com.example.trackback.fragments.Dashboard
import java.lang.Exception


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var binding: ActivityMainBinding
    lateinit var mNightModeSwitch: SwitchCompat
    lateinit var nightMode:SharedPreferences
    var isNight:Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_TrackBack)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        nightMode()
        binding.navigationView.setNavigationItemSelectedListener(this)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)
    }

    fun nightMode(){
        // Configure night-mode switch
        nightMode = this.getSharedPreferences("NightMode",MODE_PRIVATE)
        isNight = nightMode.getBoolean("nightMode",true)
        val actionLayout:View = binding.navigationView.getMenu().findItem(R.id.dark_mode).getActionView()
        mNightModeSwitch = actionLayout.findViewById(R.id.night_switch_compat)
        if (isNight) {
            mNightModeSwitch.setChecked(true)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            mNightModeSwitch.setChecked(false)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        mNightModeSwitch.setOnCheckedChangeListener { buttonView, isChecked -> applyNightMode(isChecked) }

    }

    private fun applyNightMode(checked: Boolean) {
        if (checked) {
            isNight = true
            saveSettingsBoolean("nightMode",true)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            isNight = false
            saveSettingsBoolean("nightMode",false)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        //restartActivityInvalidateBackstack(this)
    }
    private fun saveSettingsBoolean(mode: String, isNight: Boolean) {
        val editor: SharedPreferences.Editor = nightMode.edit()
        editor.putBoolean(mode, isNight)
        editor.apply()
    }
    protected fun restartActivityInvalidateBackstack(activity: MainActivity) {
        val intent = Intent()
        intent.setClass(activity,MainActivity::class.java)
        val stackBuilder: TaskStackBuilder = TaskStackBuilder.create(activity)
        stackBuilder.addNextIntentWithParentStack(intent)
        stackBuilder.startActivities(Bundle())
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.getItemId()
        if(id == R.id.dark_mode){
            mNightModeSwitch.performClick()
        }else if(id == R.id.nav_share){

            try {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT, "@string/app_name")
                intent.putExtra(
                    Intent.EXTRA_TEXT,
                    "https://play.google.com/store/apps/details?id=" + this@MainActivity.getPackageName()
                )
                startActivity(Intent.createChooser(intent, "Share With"))
            } catch (e: Exception) {
                Toast.makeText(
                    this@MainActivity,
                    "Unable to share at this moment.." + e.message.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }else if(id == R.id.nav_RateUs){
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$packageName")
                    )
                )
            } catch (e: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                    )
                )
            }
        }else if(id == R.id.nav_aboutUs){

        }
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true
    }
}