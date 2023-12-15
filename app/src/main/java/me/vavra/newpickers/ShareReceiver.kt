package me.vavra.newpickers

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.Intent.EXTRA_CHOSEN_COMPONENT
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class ShareReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val clickedComponent : ComponentName? = intent.getParcelableExtra(EXTRA_CHOSEN_COMPONENT)
        selectedSharesheetPackageName = clickedComponent?.packageName
    }

    companion object {
        var selectedSharesheetPackageName by mutableStateOf<String?>(null)
    }

}