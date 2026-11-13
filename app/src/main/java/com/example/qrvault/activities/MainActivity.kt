package com.example.qrvault.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.qrvault.R
import com.example.qrvault.databinding.ActivityMainBinding
import com.example.qrvault.fragments.ContactsFragment
import com.example.qrvault.fragments.HistoryFragment
import com.example.qrvault.fragments.HomeFragment
import com.example.qrvault.fragments.SettingsFragment

class MainActivity : AppCompatActivity() {

    companion object {
        val TAB_HOME = R.id.navigation_home
        val TAB_CONTACTS = R.id.navigation_contacts
        val TAB_SCAN = R.id.navigation_scan
        val TAB_HISTORY = R.id.navigation_history
        val TAB_SETTINGS = R.id.navigation_settings
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()

        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.navigation_contacts -> {
                    loadFragment(ContactsFragment())
                    true
                }
                R.id.navigation_scan -> {
                    val intent = Intent(this, ScannerActivity::class.java)
                    startActivity(intent)
                    false
                }
                R.id.navigation_history -> {
                    loadFragment(HistoryFragment())
                    true
                }
                R.id.navigation_settings -> {
                    loadFragment(SettingsFragment())
                    true
                }
                else -> false
            }
        }
    }

    fun navigateToTab(tabId: Int) {
        binding.bottomNavigation.selectedItemId = tabId
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}