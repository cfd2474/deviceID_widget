package com.taksolutions.devicenamewidget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val prefs = getSharedPreferences("WidgetPrefs", Context.MODE_PRIVATE)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WidgetConfigScreen(
                        initialTextColor = Color(prefs.getInt("textColor", android.graphics.Color.WHITE)),
                        initialBgColor = Color(prefs.getInt("bgColor", android.graphics.Color.TRANSPARENT)),
                        onSave = { textColor, bgColor ->
                            // Save to SharedPreferences
                            prefs.edit()
                                .putInt("textColor", textColor.toArgb())
                                .putInt("bgColor", bgColor.toArgb())
                                .apply()

                            // Trigger widget update
                            val intent = Intent(this, DeviceNameWidgetProvider::class.java).apply {
                                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                                val ids = AppWidgetManager.getInstance(this@MainActivity).getAppWidgetIds(
                                    ComponentName(this@MainActivity, DeviceNameWidgetProvider::class.java)
                                )
                                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                            }
                            sendBroadcast(intent)
                            
                            // Close the activity
                            finish()
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetConfigScreen(
    initialTextColor: Color,
    initialBgColor: Color,
    onSave: (Color, Color) -> Unit
) {
    var textColor by remember { mutableStateOf(initialTextColor) }
    var bgOpacity by remember { mutableStateOf(initialBgColor.alpha) }
    
    // We will just allow picking white or black for text, and a slider for black background opacity
    val isTextWhite = textColor == Color.White

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Widget Settings") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text("Text Color", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ColorOption(Color.White, isSelected = isTextWhite) { textColor = Color.White }
                ColorOption(Color.Black, isSelected = !isTextWhite) { textColor = Color.Black }
            }

            Text("Background Opacity", style = MaterialTheme.typography.titleMedium)
            Slider(
                value = bgOpacity,
                onValueChange = { bgOpacity = it },
                valueRange = 0f..1f
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = { onSave(textColor, Color.Black.copy(alpha = bgOpacity)) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save & Update Widget")
            }
        }
    }
}

@Composable
fun ColorOption(color: Color, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(color)
            .clickable(onClick = onClick)
            .then(
                if (isSelected) Modifier.padding(4.dp).background(Color.Gray, CircleShape).padding(2.dp).background(color, CircleShape)
                else Modifier
            )
    )
}
