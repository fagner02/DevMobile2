@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.example.myapplication.zooapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.gestures.stopScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.zooapp.api.Pokemon
import com.example.myapplication.zooapp.api.PokemonService
import com.example.myapplication.zooapp.components.BottomBar
import com.example.myapplication.zooapp.components.DetailsScreen
import com.example.myapplication.zooapp.components.HelpAndSupportScreen
import com.example.myapplication.zooapp.components.PokemonCard
import com.example.myapplication.zooapp.components.PokemonList
import com.example.myapplication.zooapp.components.SettingsScreen
import com.example.myapplication.zooapp.components.TopBar
import com.example.myapplication.zooapp.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

val favList: MutableList<String> = mutableStateListOf()

class PokemonActivity : ComponentActivity() {
    private val service = PokemonService()

    @OptIn(ExperimentalSharedTransitionApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
        )

        setContent {
            val list: MutableList<Pokemon> = remember { emptyList<Pokemon>().toMutableStateList() }
            var isDarkModeEnabled by remember { mutableStateOf(false) }
            val color = MaterialTheme.colorScheme.background.toArgb()
            var isLoading by remember{ mutableStateOf( false)}

            LaunchedEffect(isDarkModeEnabled) {
                enableEdgeToEdge(
                    statusBarStyle = if (isDarkModeEnabled) SystemBarStyle.dark(Color.Black.toArgb()) else SystemBarStyle.light(color,color)
                )
            }
            LaunchedEffect(true) {
                isLoading=true
                var res = service.getList()
                while (res.isNotEmpty()) {
                    list.addAll(res)
                    delay(100)
                    res = service.getList()
                }
                isLoading=false
            }

            MyApplicationTheme(
                darkTheme = isDarkModeEnabled
            ) {
                var route by remember { mutableStateOf("list") }
                val navController = rememberNavController()
                navController.addOnDestinationChangedListener { _, dest, _ ->
                    run {
                        route = dest.route ?: "list"
                    }
                }

                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {

                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            ModalDrawerSheet(Modifier.fillMaxWidth(0.6f)) {
                                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        TextButton(onClick = {
                                            navController.navigate("settings")
                                        }) {
                                            Text("Configurações")
                                        }
                                        TextButton(onClick = {
                                            navController.navigate("help")
                                        }) {
                                            Text("Ajuda")
                                        }

                                    }
                                }
                            }
                        }
                    ) {
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                            Scaffold(modifier = Modifier.fillMaxSize(),
                                bottomBar = { BottomBar(route, navController) },
                                topBar = { TopBar(route, navController, scope, drawerState) }
                            ) { innerPadding ->
                                var searchQuery by remember { mutableStateOf("") }
                                val listState = rememberLazyListState()
                                val favListState = rememberLazyListState()

//                                val reachedBottom by remember {
//                                    derivedStateOf {
//                                        val last = listState.layoutInfo.visibleItemsInfo.lastOrNull()
//                                        (last == null || listState.layoutInfo.totalItemsCount <= last.index + 5)
//                                    }
//                                }

//                                LaunchedEffect(reachedBottom) {
//                                    if (reachedBottom) {
//                                        isLoading=true
//                                        list.addAll(service.getList())
//                                        isLoading=false
//                                    }
//                                }

                                NavHost(navController, startDestination = "list") {
                                    composable("list") {
                                        var selected by remember { mutableStateOf("") }
                                        SharedTransitionLayout {
                                            AnimatedContent(selected, label = "hero") { state ->
                                                if (state == "") {
                                                    PokemonList(
                                                        list,
                                                        animatedVisibilityScope = this@AnimatedContent,
                                                        sharedTransitionScope = this@SharedTransitionLayout,
                                                        modifier = Modifier.padding(innerPadding),
                                                        searchQuery = searchQuery,
                                                        onSelectPokemon = { name ->
                                                            selected = name
                                                        },
                                                        onInputQuery = { input ->
                                                            searchQuery = input
                                                        },
                                                        state = listState,
                                                        isLoading = isLoading
                                                    )
                                                } else {
                                                    val pokemon: Pokemon? by remember {
                                                        mutableStateOf(list.find { it.name == state })
                                                    }
                                                    if (pokemon != null) {
                                                        DetailsScreen(
                                                            pokemon!!,
                                                            modifier = Modifier.padding(innerPadding),
                                                            { selected = "" },
                                                            searchQuery,
                                                            this@SharedTransitionLayout,
                                                            this@AnimatedContent,
                                                            service
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    composable("fav") {
                                        var selected by remember { mutableStateOf("") }
                                        SharedTransitionLayout {
                                            AnimatedContent(selected, label = "fav") { state ->
                                                if (state == "") {
                                                    val favourites:MutableList<Pokemon> =
                                                        list.filter { favList.contains(it.name) }.toMutableStateList()
                                                    if (favourites.isEmpty()) {
                                                        Box(modifier = Modifier
                                                            .padding(innerPadding)
                                                            .fillMaxSize()
                                                            , contentAlignment = Alignment.Center) {
                                                            Text("Você ainda não tem favoritos")
                                                        }
                                                    } else {
                                                        PokemonList(
                                                            favourites,
                                                            modifier = Modifier.padding(innerPadding),
                                                            animatedVisibilityScope = this@AnimatedContent,
                                                            sharedTransitionScope = this@SharedTransitionLayout,
                                                            onInputQuery = { searchQuery = it },
                                                            onSelectPokemon = { selected = it },
                                                            searchQuery = searchQuery,
                                                            state = favListState,
                                                            isLoading = isLoading
                                                        )
                                                    }
                                                } else {
                                                    val pokemon: Pokemon? by remember {
                                                        mutableStateOf(list.find { it.name == state })
                                                    }
                                                    if (pokemon != null) {
                                                        DetailsScreen(
                                                            pokemon!!,
                                                            modifier = Modifier.padding(innerPadding),
                                                            { selected = "" },
                                                            searchQuery,
                                                            this@SharedTransitionLayout,
                                                            this@AnimatedContent,
                                                            service
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    composable("settings") {
                                        var isNotificationsEnabled by remember { mutableStateOf(true) }
                                        val context = LocalContext.current

                                        SettingsScreen(
                                            isDarkModeEnabled = isDarkModeEnabled,
                                            isNotificationsEnabled = isNotificationsEnabled,
                                            onToggleDarkMode = { isDarkModeEnabled = it },
                                            onToggleNotifications = { isNotificationsEnabled = it },
                                            onClearFavorites = {
                                                favList.clear()
                                                Toast.makeText(context, "Favoritos limpos com sucesso!", Toast.LENGTH_SHORT).show()
                                            },
                                            onResetPreferences = {
                                                isDarkModeEnabled = false
                                                isNotificationsEnabled = true
                                                favList.clear()
                                                Toast.makeText(context,"Preferências redefinidas com sucesso!", Toast.LENGTH_SHORT).show()
                                            }
                                        )
                                    }
                                    composable("help") {
                                        val context = LocalContext.current
                                        HelpAndSupportScreen (onSendSupportMessage = { message ->
                                            Toast.makeText(context, "Mensagem enviada: $message", Toast.LENGTH_SHORT).show()
                                        },modifier = Modifier.padding(innerPadding))
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
