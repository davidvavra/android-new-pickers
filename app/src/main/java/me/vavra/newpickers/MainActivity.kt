package me.vavra.newpickers

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
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

}