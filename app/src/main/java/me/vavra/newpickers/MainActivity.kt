package me.vavra.newpickers

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.service.chooser.ChooserAction
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import me.vavra.newpickers.ui.theme.NewPickersTheme

class MainActivity : ComponentActivity() {
    private lateinit var pickImage: ActivityResultLauncher<PickVisualMediaRequest>
    private var selectedImageUri by mutableStateOf<String?>(null)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NewPickersTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Button(onClick = { pickImageOld() }) {
                            Text(text = "Pick image (old)")
                        }
                        Button(onClick = { pickImageNew() }) {
                            Text(text = "Pick image (new)")
                        }
                        Text(text = "Selected image: $selectedImageUri")
                        Spacer(modifier = Modifier.padding(20.dp))
                        Button(onClick = { shareLinkOld() }) {
                            Text(text = "Share link (old)")
                        }
                        Button(onClick = { shareLinkNew() }) {
                            Text(text = "Share link (new)")
                        }
                        Text(text = "Selected package name: ${ShareReceiver.selectedSharesheetPackageName}")
                    }
                }
            }
        }
        pickImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            selectedImageUri = uri.toString()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 42 && resultCode == RESULT_OK) {
            selectedImageUri = data?.data.toString()
        }
    }

    private fun pickImageOld() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 42)
    }

    private fun pickImageNew() {
        pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun shareLinkOld() {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                "https://gdg.community.dev/events/details/google-gdg-bishkek-presents-devfest-bishkek-2023/"
            )
            type = "text/plain"
        }
        startActivity(sendIntent)
    }

    private fun shareLinkNew() {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                "https://gdg.community.dev/events/details/google-gdg-bishkek-presents-devfest-bishkek-2023/"
            )
            putExtra(Intent.EXTRA_TITLE, "DevFest Bishkek 2023")
            type = "text/plain"
        }
        val shareReceiver = PendingIntent.getBroadcast(
            this, 42,
            Intent(this, ShareReceiver::class.java),
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val shareIntent = Intent.createChooser(sendIntent, null, shareReceiver.intentSender).apply {
            putExtra(Intent.EXTRA_INITIAL_INTENTS, listOf(intent).toTypedArray())
            val excludedComponentNames = arrayOf(
                ComponentName(
                    "com.google.android.apps.docs",
                    "com.google.android.apps.docs.common.shareitem.UploadMenuActivity"
                )
            )
            putExtra(Intent.EXTRA_EXCLUDE_COMPONENTS, excludedComponentNames)
        }
        if (Build.VERSION.SDK_INT >= 34) {
            val customActions = arrayOf(
                ChooserAction.Builder(
                    Icon.createWithResource(this, androidx.core.R.drawable.ic_call_answer),
                    "Call",
                    PendingIntent.getBroadcast(
                        this,
                        1,
                        Intent(Intent.ACTION_VIEW),
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
                    )
                ).build()
            )
            shareIntent.putExtra(Intent.EXTRA_CHOOSER_CUSTOM_ACTIONS, customActions)
        }
        startActivity(shareIntent)
    }

}