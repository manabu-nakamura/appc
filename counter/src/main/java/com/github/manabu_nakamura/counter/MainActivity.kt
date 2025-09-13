package com.github.manabu_nakamura.counter

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.github.manabu_nakamura.counter.ui.theme.CounterTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val darkTheme = when (theme()) {
            0 -> false
            1 -> true
            else -> resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        }
        val systemBarStyle = if (darkTheme)
            SystemBarStyle.dark(Color.TRANSPARENT)
        else
            SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        enableEdgeToEdge(systemBarStyle, systemBarStyle)
        setContent {
            CounterTheme(darkTheme) {
                Surface(Modifier.fillMaxSize()) {
                    Column(
                        Modifier
                            .safeDrawingPadding()
                            .padding(horizontal = 5.dp),
                        Arrangement.Bottom
                    ) {
                        var value by rememberSaveable {
                            mutableIntStateOf(0)
                        }
                        Text(
                            value.toString(),
                            Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        Button(
                            {
                                value++
                            },
                            Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.count))
                        }
                        Button(
                            {
                                value = 0
                            },
                            Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.reset))
                        }
                        var openDialog by rememberSaveable {
                            mutableStateOf(false)
                        }
                        if (openDialog) {
                            AlertDialog(
                                {
                                    openDialog = false
                                },
                                {
                                    TextButton(
                                        {
                                            openDialog = false
                                        }
                                    ) {
                                        Text(stringResource(R.string.ok))
                                    }
                                },
                                title = {
                                    Text(stringResource(R.string.app_name))
                                },
                                text = {
//                                    Text(stringResource(R.string.copyright))
                                    Text(
                                        buildAnnotatedString {
                                            append(stringResource(R.string.copyright1))
                                            withLink(
                                                LinkAnnotation.Url(
                                                    stringResource(R.string.copyright2),
                                                    TextLinkStyles(
                                                        SpanStyle(
                                                            MaterialTheme.colorScheme.primary,
                                                            textDecoration = TextDecoration.Underline
                                                        )
                                                    )
                                                )
                                            ) {
                                                append(stringResource(R.string.copyright3))
                                            }
                                        }
                                    )
                                }
                            )
                        }
                        Row {
                            var selectedIndex by rememberSaveable {
                                mutableIntStateOf(theme())
                            }
                            SingleChoiceSegmentedButtonRow {
                                TooltipBox(
                                    TooltipDefaults.rememberPlainTooltipPositionProvider(),
                                    {
                                        PlainTooltip {
                                            Text(stringResource(R.string.light))
                                        }
                                    },
                                    rememberTooltipState()
                                ) {
                                    SegmentedButton(
                                        selectedIndex == 0,
                                        {
                                            selectedIndex = 0
                                            theme(0)
                                        },
                                        SegmentedButtonDefaults.itemShape(0, 3)
                                    ) {
                                        Icon(
                                            painterResource(R.drawable.outline_light_mode_24),
                                            null
                                        )
                                    }
                                }
                                TooltipBox(
                                    TooltipDefaults.rememberPlainTooltipPositionProvider(),
                                    {
                                        PlainTooltip {
                                            Text(stringResource(R.string.dark))
                                        }
                                    },
                                    rememberTooltipState()
                                ) {
                                    SegmentedButton(
                                        selectedIndex == 1,
                                        {
                                            selectedIndex = 1
                                            theme(1)
                                        },
                                        SegmentedButtonDefaults.itemShape(1, 3)
                                    ) {
                                        Icon(
                                            painterResource(R.drawable.outline_dark_mode_24),
                                            null
                                        )
                                    }
                                }
                                TooltipBox(
                                    TooltipDefaults.rememberPlainTooltipPositionProvider(),
                                    {
                                        PlainTooltip {
                                            Text(stringResource(R.string.system))
                                        }
                                    },
                                    rememberTooltipState()
                                ) {
                                    SegmentedButton(
                                        selectedIndex == 2,
                                        {
                                            selectedIndex = 2
                                            theme(2)
                                        },
                                        SegmentedButtonDefaults.itemShape(2, 3)
                                    ) {
                                        Icon(
                                            painterResource(R.drawable.outline_android_24),
                                            null
                                        )
                                    }
                                }
                            }
                            TooltipBox(
                                TooltipDefaults.rememberPlainTooltipPositionProvider(),
                                {
                                    PlainTooltip {
                                        Text(stringResource(R.string.about))
                                    }
                                },
                                rememberTooltipState()
                            ) {
                                IconButton(
                                    {
                                        openDialog = true
                                    }
                                ) {
                                    Icon(
                                        Icons.Outlined.Info,
                                        null
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private companion object {
        private val Context.dataStore by preferencesDataStore("settings")
        private val THEME = intPreferencesKey("theme")
    }

    private fun theme(): Int {
        return runBlocking {
            dataStore.data.first()[THEME] ?: 2
        }
    }

    private fun theme(theme: Int) {
        runBlocking {
            dataStore.edit {
                it[THEME] = theme
            }
        }
        recreate()
    }
}