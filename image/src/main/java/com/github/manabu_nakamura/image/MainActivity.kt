package com.github.manabu_nakamura.image

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FlexibleBottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import coil3.compose.AsyncImage
//import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
//import com.bumptech.glide.integration.compose.GlideImage
import com.github.manabu_nakamura.image.ui.theme.ImageTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    @OptIn(
//        ExperimentalGlideComposeApi::class,
        ExperimentalMaterial3Api::class
    )
    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(
            savedInstanceState
        )
        val darkTheme = when (theme()) {
            0 -> false
            1 -> true
            else -> resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        }
        val systemBarStyle = if (darkTheme) {
            SystemBarStyle.dark(
                Color.TRANSPARENT
            )
        } else {
            SystemBarStyle.light(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            )
        }
        enableEdgeToEdge(
            systemBarStyle,
            systemBarStyle
        )
        setContent {
            ImageTheme(
                darkTheme
            ) {
                var uri by rememberSaveable {
                    mutableStateOf(
                        Uri.EMPTY
                    )
                }
                val picker = rememberLauncherForActivityResult(
                    ActivityResultContracts.PickVisualMedia()
                ) {
                    uri = it
                }
                var bottomSheet by rememberSaveable {
                    mutableStateOf(
                        false
                    )
                }
                var dialog by rememberSaveable {
                    mutableStateOf(
                        false
                    )
                }
                Scaffold(
                    Modifier.fillMaxSize(),
                    bottomBar = {
                        FlexibleBottomAppBar(
                            content = {
                                TooltipBox(
                                    TooltipDefaults.rememberTooltipPositionProvider(
                                        TooltipAnchorPosition.Above
                                    ),
                                    {
                                        PlainTooltip {
                                            Text(
                                                stringResource(
                                                    R.string.open
                                                )
                                            )
                                        }
                                    },
                                    rememberTooltipState()
                                ) {
                                    TextButton(
                                        {
                                            picker.launch(
                                                PickVisualMediaRequest(
                                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                                )
                                            )
                                        },
                                        ButtonDefaults.shapes()
                                    ) {
                                        Icon(
                                            painterResource(
                                                R.drawable.outline_image_24
                                            ),
                                            stringResource(
                                                R.string.open
                                            )
                                        )
                                    }
                                }
                                Box {
                                    var expanded by rememberSaveable {
                                        mutableStateOf(
                                            false
                                        )
                                    }
                                    TooltipBox(
                                        TooltipDefaults.rememberTooltipPositionProvider(
                                            TooltipAnchorPosition.Above
                                        ),
                                        {
                                            PlainTooltip {
                                                Text(
                                                    stringResource(
                                                        R.string.theme
                                                    )
                                                )
                                            }
                                        },
                                        rememberTooltipState()
                                    ) {
                                        TextButton(
                                            {
                                                expanded = !expanded
                                            },
                                            ButtonDefaults.shapes()
                                        ) {
                                            Icon(
                                                painterResource(
                                                    R.drawable.outline_palette_24
                                                ),
                                                stringResource(
                                                    R.string.theme
                                                )
                                            )
                                        }
                                    }
                                    val themes = stringArrayResource(
                                        R.array.theme
                                    )
                                    val icons = listOf(
                                        R.drawable.outline_light_mode_24,
                                        R.drawable.outline_dark_mode_24,
                                        R.drawable.outline_android_24
                                    )
                                    DropdownMenu(
                                        expanded,
                                        {
                                            expanded = false
                                        }
                                    ) {
                                        themes.forEachIndexed { index, theme ->
                                            DropdownMenuItem(
                                                {
                                                    Text(
                                                        theme
                                                    )
                                                },
                                                {
                                                    theme(
                                                        index
                                                    )
                                                },
                                                leadingIcon = {
                                                    Icon(
                                                        painterResource(
                                                            if (theme() == index) {
                                                                icons[index]
                                                            } else {
                                                                R.drawable.empty
                                                            }
                                                        ),
                                                        theme
                                                    )
                                                }
                                            )
                                        }
                                    }
                                }
                                TooltipBox(
                                    TooltipDefaults.rememberTooltipPositionProvider(
                                        TooltipAnchorPosition.Above
                                    ),
                                    {
                                        PlainTooltip {
                                            Text(
                                                stringResource(
                                                    R.string.theme
                                                )
                                            )
                                        }
                                    },
                                    rememberTooltipState()
                                ) {
                                    TextButton(
                                        {
                                            bottomSheet = true
                                        },
                                        ButtonDefaults.shapes()
                                    ) {
                                        Icon(
                                            painterResource(
                                                R.drawable.outline_palette_24
                                            ),
                                            stringResource(
                                                R.string.theme
                                            )
                                        )
                                    }
                                }
                                TooltipBox(
                                    TooltipDefaults.rememberTooltipPositionProvider(
                                        TooltipAnchorPosition.Above
                                    ),
                                    {
                                        PlainTooltip {
                                            Text(
                                                stringResource(
                                                    R.string.about
                                                )
                                            )
                                        }
                                    },
                                    rememberTooltipState()
                                ) {
                                    TextButton(
                                        {
                                            dialog = true
                                        },
                                        ButtonDefaults.shapes()
                                    ) {
                                        Icon(
                                            painterResource(
                                                R.drawable.outline_info_24
                                            ),
                                            stringResource(
                                                R.string.about
                                            )
                                        )
                                    }
                                }
                            }
                        )
                    }
                ) {
                    Column(
                        Modifier.padding(
                            it
                        )
                    ) {
                        var scale by remember {
                            mutableFloatStateOf(
                                1f
                            )
                        }
                        var offset by remember {
                            mutableStateOf(
                                Offset.Zero
                            )
                        }
                        val state = rememberTransformableState { _, zoomChange, panChange, _ ->
                            scale = (scale * zoomChange).coerceIn(
                                .5f..5f
                            )
                            offset += panChange
                        }
                        AsyncImage(
//                        GlideImage(
                            uri,
                            null,
                            Modifier
                                .graphicsLayer(
                                    scale,
                                    scale,
                                    translationX = offset.x,
                                    translationY = offset.y
                                )
                                .transformable(
                                    state
                                )
                        )
                        if (bottomSheet) {
                            ModalBottomSheet(
                                {
                                    bottomSheet = false
                                }
                            ) {
                                Column(
                                    Modifier.padding(
                                        horizontal = 10.dp
                                    )
                                ) {
                                    val themes = stringArrayResource(
                                        R.array.theme
                                    )
                                    val icons = listOf(
                                        R.drawable.outline_light_mode_24,
                                        R.drawable.outline_dark_mode_24,
                                        R.drawable.outline_android_24
                                    )
                                    themes.forEachIndexed { index, theme ->
                                        ToggleButton(
                                            theme() == index,
                                            {
                                                theme(
                                                    index
                                                )
                                            },
                                            Modifier.fillMaxWidth(),
                                            colors = ToggleButtonDefaults.elevatedToggleButtonColors()
                                        ) {
                                            Row(
                                                Modifier.fillMaxWidth(),
                                                Arrangement.Start,
                                                Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    painterResource(
                                                        icons[index]
                                                    ),
                                                    theme
                                                )
                                                Spacer(
                                                    Modifier.size(
                                                        ToggleButtonDefaults.IconSpacing
                                                    )
                                                )
                                                Text(
                                                    theme
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (dialog) {
                            AlertDialog(
                                {
                                    dialog = false
                                },
                                {
                                    TextButton(
                                        {
                                            dialog = false
                                        },
                                        ButtonDefaults.shapes()
                                    ) {
                                        Text(
                                            stringResource(
                                                R.string.ok
                                            )
                                        )
                                    }
                                },
                                title = {
                                    Text(
                                        stringResource(
                                            R.string.app_name
                                        )
                                    )
                                },
                                text = {
//                                    Text(stringResource(R.string.copyright))
                                    Text(
                                        buildAnnotatedString {
                                            append(
                                                stringResource(
                                                    R.string.copyright1
                                                )
                                            )
                                            withLink(
                                                LinkAnnotation.Url(
                                                    stringResource(
                                                        R.string.copyright2
                                                    ),
                                                    TextLinkStyles(
                                                        SpanStyle(
                                                            MaterialTheme.colorScheme.primary,
                                                            textDecoration = TextDecoration.Underline
                                                        )
                                                    )
                                                )
                                            ) {
                                                append(
                                                    stringResource(
                                                        R.string.copyright3
                                                    )
                                                )
                                            }
                                        }
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private companion object {
        private val Context.dataStore by preferencesDataStore(
            "settings"
        )
        private val THEME = intPreferencesKey(
            "theme"
        )
    }

    private fun theme(): Int {
        return runBlocking {
            dataStore.data.first()[THEME] ?: 2
        }
    }

    private fun theme(
        theme: Int
    ) {
        runBlocking {
            dataStore.edit {
                it[THEME] = theme
            }
        }
        recreate()
    }
}