package com.example.rigid

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.rigid.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.storage.StorageOptions
import com.google.common.collect.Lists
import java.io.ByteArrayInputStream

// Google Auth Key
// Upload key to Github, trust me
const val credentialsString = ""

val credentials = GoogleCredentials.fromStream(ByteArrayInputStream(credentialsString.toByteArray()))
val scopedCredentials = credentials.createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"))
val options = StorageOptions.newBuilder().setCredentials(scopedCredentials).build()
val storage = options.service

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityMainBinding by lazy {
            ActivityMainBinding.inflate(layoutInflater)
        }


        fun <T : ViewDataBinding> binding(@LayoutRes resId: Int): Lazy<T> =
            lazy { DataBindingUtil.setContentView<T>(this, resId) }
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener {
            val navController = findNavController(R.id.nav_host_fragment_content_main)
            val currentDestinationId = navController.currentDestination?.id

            if (currentDestinationId == R.id.FirstFragment) {
                navController.navigate(R.id.action_FirstFragment_to_SecondFragment)
                Snackbar.make(it, "Inference Screen", Snackbar.LENGTH_SHORT).show()
            } else if (currentDestinationId == R.id.SecondFragment) {
                navController.navigate(R.id.action_SecondFragment_to_FirstFragment)
                Snackbar.make(it, "Input Screen", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}