package io.github.teamsix.bbvaforo

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import io.github.teamsix.bbvaforo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

	private lateinit var appBarConfiguration: AppBarConfiguration
	private lateinit var binding: ActivityMainBinding
	private val requestPermissionLauncher = registerForActivityResult(
		ActivityResultContracts.RequestPermission()) { granted ->
		if (granted) {
			takePictureOrRequestPermission()
		}
		else {
			Toast.makeText(this, "Error. Permiso a la camara denegegado.", Toast.LENGTH_SHORT).show()
		}
	}
	private val takePictureLauncher = registerForActivityResult(
		ActivityResultContracts.TakePicture()) { success ->
		if (success) {
			takePictureCallback()
		}
	}
	private var tempUri: Uri? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)

		setSupportActionBar(binding.toolbar)

		val navController = findNavController(R.id.nav_host_fragment_content_main)
		appBarConfiguration = AppBarConfiguration(navController.graph)
		setupActionBarWithNavController(navController, appBarConfiguration)

		binding.fab.setOnClickListener { view ->
			askCameraPermission()
		}
	}

	private fun askCameraPermission() {
		requestPermissionLauncher.launch(Manifest.permission.CAMERA)
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

	private fun takePictureOrRequestPermission() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			takePicture()
		}
		else {
			if (ContextCompat.checkSelfPermission(
					this,
					Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
			}
			else {
				takePicture()
			}
		}
	}

	private fun takePicture() {
		val savedUri = BitmapHelper.savePicture(this) ?: return
		tempUri = savedUri
		takePictureLauncher.launch(tempUri)
	}
	
	private fun takePictureCallback() {
		android.util.Log.d("SUCCESS", "DEMO")
		tempUri?.let { onSuccessCallback(it) }
	}

	fun setOnSuccessListener(callback: (uri: Uri) -> Unit) {
		onSuccessCallback = callback
	}

	private var onSuccessCallback: (uri: Uri) -> Unit = {}
}