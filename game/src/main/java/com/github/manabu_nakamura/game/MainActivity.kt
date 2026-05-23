package com.github.manabu_nakamura.game

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.github.manabu_nakamura.game.ui.theme.GameTheme

class MainActivity : ComponentActivity() {
    private companion object {
        private const val MAX = 20
    }
    private val viewModel by viewModels<ViewModel2>()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        setContent {
            GameTheme {
                Surface(Modifier.fillMaxSize()) {
                    var count by rememberSaveable {
                        mutableIntStateOf(0)
                    }
                    var answer by rememberSaveable {
                        mutableIntStateOf((Math.random() * MAX).toInt())
                    }//answer = 0～MAX - 1
                    var message by rememberSaveable {
                        mutableStateOf(getString(R.string.message0, MAX - 1))
                    }
                    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
                        rememberTopAppBarState()
                    )
                    Scaffold(
                        Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                        {
                            TopAppBar(
                                {
                                    Text(message)
                                },
                                scrollBehavior = scrollBehavior
                            )
                        }
                    ) {
                        Column(
                            Modifier
                                .verticalScroll(rememberScrollState())
                                .padding(it)
                        ) {
                            for (i in 0 until MAX) {
                                Row(
                                    Modifier
                                        .toggleable(
                                            viewModel.list[i],
                                            role = Role.Checkbox,
                                            onValueChange = {
                                                viewModel.list[i] = true
                                                count++
                                                message = getString(
                                                    if (i < answer) {
                                                        R.string.message1
                                                    } else if (i == answer) {
                                                        R.string.message2
                                                    } else {
                                                        R.string.message3
                                                    }, count, MAX - 1
                                                )
                                                if (i == answer) {
                                                    count = 0
                                                    answer = (Math.random() * MAX).toInt()//answer = 0～MAX - 1
                                                    for (j in 0 until MAX) {
                                                        viewModel.list[j] = false
                                                    }
                                                }
                                            }
                                        )
                                        .padding(10.dp)
                                        .fillMaxWidth()
                                ) {
                                    Checkbox(
                                        viewModel.list[i],
                                        null
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    Text(i.toString())
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    class ViewModel2 : ViewModel() {
        val list = MutableList(MAX) { false }.toMutableStateList()
    }
}