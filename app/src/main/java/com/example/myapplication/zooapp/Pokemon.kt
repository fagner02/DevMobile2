@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.example.myapplication.zooapp

import android.graphics.Color.parseColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.stopScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.example.myapplication.R
import com.example.myapplication.zooapp.components.BottomBar
import com.example.myapplication.zooapp.components.TopBar
import com.example.myapplication.zooapp.ui.theme.MyApplicationTheme
import com.example.myapplication.zooapp.ui.theme.typeColors
import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlin.time.Duration.Companion.milliseconds
import io.ktor.client.request.url as _url

@Serializable
data class ApiResource(val url: String, val name:String)
@Serializable
data class ApiResourceList(val next: String?, val previous: String?, val results: List<ApiResource>)
@Suppress("PropertyName")
@Serializable
data class Sprites(val front_default: String?)
@Serializable
data class Type(val name: String)
@Serializable
data class TypeSlot(val slot: Int, val type: Type)
@Serializable
data class Pokemon(val name: String, val types: List<TypeSlot>, val sprites: Sprites)

class Service {
    private val client=HttpClient()
    private val api = "https://pokeapi.co/api/v2"
    private var next: String? = "${api}/pokemon?offset=0&limit=10"
    suspend fun getList(): MutableList<Pokemon> {
        try {
            val res =
                client.get { _url("$next") }
            val resourceList =
                Gson().fromJson(res.bodyAsText(), ApiResourceList::class.java)
            val pokemons:MutableList<Pokemon> = emptyList<Pokemon>().toMutableList()
            for (resource in resourceList.results){
                val pokeRes =
                    HttpClient().get { _url("https://pokeapi.co/api/v2/pokemon/${resource.name}") }
                val pokemon =
                    Gson().fromJson(pokeRes.bodyAsText(), Pokemon::class.java)
                pokemons.add(pokemon)
            }
            next = resourceList.next
            return pokemons
        }
            catch (e:Error) {
                return  mutableStateListOf()
            }
    }
}
val favList: MutableList<String> = mutableStateListOf()

class PokemonActivity : ComponentActivity() {
    private val service = Service()

    @OptIn(ExperimentalSharedTransitionApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val list: MutableList<Pokemon> = remember { emptyList<Pokemon>().toMutableStateList() }

            MyApplicationTheme {
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
                                        TextButton(onClick = {}) {
                                            Text("Configurações")
                                        }
                                        TextButton(onClick = {}) {
                                            Text("Ajuda")
                                        }
                                        TextButton(onClick = {}) {
                                            Text("FAQ")
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
                                var isLoading by remember{ mutableStateOf( false)}

                                val reachedBottom by remember {
                                    derivedStateOf {
                                        val last = listState.layoutInfo.visibleItemsInfo.lastOrNull()
                                        (last == null || listState.layoutInfo.totalItemsCount <= last.index + 5)
                                    }
                                }

                                LaunchedEffect(reachedBottom) {
                                    if (reachedBottom) {
                                        isLoading=true
                                        list.addAll(service.getList())
                                        isLoading=false
                                    }
                                }

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
                                                            this@AnimatedContent
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
                                                    PokemonList(
                                                        list.filter { favList.contains(it.name) },
                                                        modifier = Modifier.padding(innerPadding),
                                                        animatedVisibilityScope = this@AnimatedContent,
                                                        sharedTransitionScope = this@SharedTransitionLayout,
                                                        onInputQuery = { searchQuery = it },
                                                        onSelectPokemon = { selected = it },
                                                        searchQuery = searchQuery,
                                                        state = favListState,
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
                                                            this@AnimatedContent
                                                        )
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
            }
        }
    }
}

@Composable
fun PokemonList(
    list: List<Pokemon>,
    modifier: Modifier,
    onSelectPokemon: (String)-> Unit,
    onInputQuery: (String)->Unit,
    searchQuery: String,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    state: LazyListState,
    isLoading: Boolean) {
    with(sharedTransitionScope) {
        Column(modifier = modifier.fillMaxSize()) {

            TextField(
                value = searchQuery,
                onValueChange = onInputQuery,
                singleLine = true,
                shape = RoundedCornerShape(25),
                colors = TextFieldDefaults.colors().copy(
                    disabledIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                placeholder = { Text("Pesquisar") },
                trailingIcon = { Icon(Icons.Outlined.Search, contentDescription = "Pesquisar") },
                enabled = true,
                modifier = Modifier
                    .sharedElement(
                        rememberSharedContentState(key = "text"),
                        animatedVisibilityScope
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
            )


            val coroutine = rememberCoroutineScope()

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 8.dp),
                state = state
            ) {
                itemsIndexed(list.filter { it.name.contains(searchQuery) }) { index, pokemonName ->
                    var selecting by remember { mutableStateOf(false) }
                    var timedout by remember { mutableStateOf(false) }
                    if (selecting && (state.isScrollInProgress && !timedout)) {
                        DisposableEffect(Unit) {
                            onDispose {
                                coroutine.launch {
                                    state.stopScroll()
                                }
                                selecting = false
                                timedout = false

                                onSelectPokemon(pokemonName.name)
                            }
                        }
                    }
                    if (selecting) {
                        LaunchedEffect(Unit) {
                            while (true) {
                                delay(100.milliseconds)
                                timedout = true
                            }
                        }
                    }

                    ListItem(
                        pokemonName,
                        index,
                        scrollToItem = {
                            coroutine.launch {
                                state.animateScrollToItem(index = index, -500)
                            }
                            selecting = true
                        },
                        animatedVisibilityScope = animatedVisibilityScope,
                        sharedTransitionScope = sharedTransitionScope
                    )
                }
                if (isLoading) {
                    item {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeCap = StrokeCap.Round,
                                strokeWidth = 3.dp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailsScreen(
    pokemon: Pokemon,
    modifier: Modifier,
    onBack: ()->Unit,
    searchQuery: String,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope) {
    with(sharedTransitionScope) {
        Column(
            modifier = modifier
        ) {
            var animate by remember { mutableStateOf(false) }
            val offset by animateIntAsState(if (animate) (-80) else 0,label = "offset")
            val height by animateDpAsState(targetValue = if (animate) 0.dp else 30.dp, label = "height")
            LaunchedEffect(true) {
                animate=true
            }
            TextField(
                value = searchQuery,
                onValueChange = { },
                shape = RoundedCornerShape(25),
                colors = TextFieldDefaults.colors().copy(
                    disabledIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent),
                trailingIcon = {Icon(Icons.Outlined.Search, contentDescription = "pesquisar")},
                modifier = Modifier
                    .sharedElement(
                        rememberSharedContentState(key = "text"),
                        animatedVisibilityScope
                    )
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(height)
                    .absoluteOffset { IntOffset(0,offset) }
            )
            Card(
                modifier = Modifier
                    .sharedElement(
                        rememberSharedContentState(key = pokemon.name),
                        animatedVisibilityScope
                    )
                    .padding(16.dp)
                    .fillMaxSize(),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = {
                            onBack()
                        }) {
                            Icon(Icons.AutoMirrored.Outlined.ArrowBack, "voltar")
                        }
                        IconButton(onClick = {
                            if (favList.contains(pokemon.name)) {
                                favList.remove(pokemon.name)
                            } else {
                                favList.add(pokemon.name)
                            }
                        }) {
                            Icon(
                                if (favList.contains(pokemon.name)) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                                "favoritar",
                                modifier = Modifier.sharedElement(
                                    rememberSharedContentState(key = "${pokemon.name}-fav"),
                                    animatedVisibilityScope
                                )
                            )
                        }
                    }
                    AsyncImage(
                        pokemon.sprites.front_default,
                        filterQuality = FilterQuality.None,
                        contentDescription = "${pokemon.name} Image",
                        modifier = Modifier
                            .sharedElement(
                                rememberSharedContentState(key = "${pokemon.name}-image"),
                                animatedVisibilityScope
                            )
                            .size(200.dp)
                            .clip(CircleShape)
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = pokemon.name,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.sharedElement(
                            rememberSharedContentState(key = "${pokemon.name}-name"),
                            animatedVisibilityScope
                        )

                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        modifier = Modifier.sharedElement(
                            rememberSharedContentState(key = "${pokemon.name}-type"),
                            animatedVisibilityScope
                        )
                    ) {
                        pokemon.types.forEach { type ->
                            Box(
                                modifier = Modifier
                                    .wrapContentSize()
                                    .border(
                                        1.dp,
                                        Color.White,
                                        shape = RoundedCornerShape(25)
                                    )
                                    .clip(RoundedCornerShape(25))
                                    .background(
                                        color = Color(
                                            parseColor(
                                                typeColors[type.type.name] ?: "#000000"
                                            )
                                        )
                                    )
                            ) {
                                Text(
                                    text = type.type.name,
                                    modifier = Modifier.padding(horizontal = 5.dp),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun ListItem(
    pokemon: Pokemon,
    index: Int,
    scrollToItem: (Int)->Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope) {
    with(sharedTransitionScope) {
        Card(
            modifier = Modifier
                .sharedElement(
                    rememberSharedContentState(key = pokemon.name),
                    animatedVisibilityScope
                )
                .fillMaxWidth()
                .padding(8.dp)
                .clickable {
                    scrollToItem(index)
                },
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            pokemon.sprites.front_default,
                            filterQuality = FilterQuality.None,
                            contentDescription = "${pokemon.name} Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .sharedElement(
                                    rememberSharedContentState(key = "${pokemon.name}-image"),
                                    animatedVisibilityScope
                                )
                                .size(80.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = pokemon.name,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.sharedElement(
                                    rememberSharedContentState(key = "${pokemon.name}-name"),
                                    animatedVisibilityScope
                                )
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(5.dp),
                                modifier = Modifier.sharedElement(
                                    rememberSharedContentState(key = "${pokemon.name}-type"),
                                    animatedVisibilityScope
                                )) {
                                pokemon.types.forEach { type ->
                                    Box(
                                        modifier = Modifier
                                            .wrapContentSize()
                                            .border(
                                                1.dp,
                                                Color.White,
                                                shape = RoundedCornerShape(25)
                                            )
                                            .clip(RoundedCornerShape(25))
                                            .background(
                                                color = Color(
                                                    parseColor(
                                                        typeColors[type.type.name] ?: "#000000"
                                                    )
                                                )
                                            )
                                    ) {
                                        Text(
                                            text = type.type.name,
                                            modifier = Modifier.padding(horizontal = 5.dp),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                    TextButton(onClick = {
                        if (favList.contains(pokemon.name)) {
                            favList.remove(pokemon.name)
                        } else {
                            favList.add(pokemon.name)
                        }
                    }) {
                        Icon(
                            painterResource(if (favList.contains(pokemon.name)) R.drawable.favfill else R.drawable.favout),
                            contentDescription = "favoritar",
                            modifier = Modifier.sharedElement(
                                rememberSharedContentState(key = "${pokemon.name}-fav"),
                                animatedVisibilityScope
                            )
                        )
                    }
                }
            }
        }
    }
}
