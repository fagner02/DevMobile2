@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.example.myapplication.zooapp

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.stopScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomNavigationDefaults.windowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.R
import com.example.myapplication.zooapp.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

data class Pokemon (
    val name: String,
    val type: Pair<String, String>,
    val nature: String,
    val abilities: String,
    val imgId: Int,
    val description: String,
    val location: String,
    val fav: Boolean
)

val pikachu = Pokemon(
    name = "Pikachu",
    type = Pair("Electric", ""),
    nature = "Hardy",
    abilities = "Static, Lightning Rod",
    imgId = R.drawable.pika,
    description = "Um rato elétrico que armazena eletricidade em suas bochechas.",
    location = "Região de Kanto",
    fav= false
)

val charmander = Pokemon(
    name = "Charmander",
    type = Pair("Fire", ""),
    nature = "Timid",
    abilities = "Blaze",
    imgId = R.drawable.chara,
    description = "Uma pequena salamandra com uma chama na ponta da cauda.",
    location = "Região de Kanto",
    fav= false
)

val bulbasaur = Pokemon(
    name = "Bulbasaur",
    type = Pair("Grass", "Poison"),
    nature = "Quiet",
    abilities = "Overgrow",
    imgId = R.drawable.bulb,
    description = "Uma pequena criatura com uma semente nas costas.",
    location = "Região de Kanto",
    fav= false
)

val squirtle = Pokemon(
    name = "Squirtle",
    type = Pair("Water", ""),
    nature = "Bold",
    abilities = "Torrent",
    imgId = R.drawable.turt,
    description = "Uma pequena tartaruga que se esconde em sua carapaça.",
    location = "Região de Kanto",
    fav= false
)

val eevee = Pokemon(
    name = "Eevee",
    type = Pair("Normal", ""),
    nature = "Calm",
    abilities = "Run Away",
    imgId = R.drawable.eve,
    description = "Um Pokémon com genes instáveis que podem evoluir de várias maneiras.",
    location = "Região de Kanto",
    fav= false
)
val pokemons = mutableStateListOf(pikachu, charmander, bulbasaur, squirtle, eevee)
class PokemonActivity() : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
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
                                    Column(modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally) {
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
                        },

                        ) {
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                            Scaffold(modifier = Modifier.fillMaxSize(),
                                bottomBar = {
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                    ) {
                                        Row(
                                            Modifier
                                                .windowInsetsPadding(windowInsets)
                                                .fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceAround,
                                            verticalAlignment = Alignment.CenterVertically,
                                        )
                                        {
                                            TextButton(
                                                enabled = route != "list",
                                                colors = ButtonDefaults.textButtonColors(),
                                                onClick = { navController.navigate("list") },
                                            ) {
                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                    Icon(
                                                        painterResource(R.drawable.home),
                                                        contentDescription = "início"
                                                    )
                                                    Text("início")
                                                }
                                            }

                                            TextButton(enabled = route != "fav",
                                                onClick = { navController.navigate("fav") }) {
                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                    Icon(
                                                        painterResource(R.drawable.favout),
                                                        contentDescription = "favoritos"
                                                    )
                                                    Text("favoritos")
                                                }
                                            }
                                        }
                                    }
                                },
                                topBar = {
                                    val title: String
                                    if (route == "list") title = "Início"
                                    else if (route == "fav") title = "Favoritos"
                                    else title =
                                        navController.currentBackStackEntry?.arguments?.getString("pokemon")
                                            ?: ""
                                    var showDropDownMenu by remember { mutableStateOf(false) }
                                    TopAppBar(title = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Image(
                                                painterResource(R.drawable.poke),
                                                contentDescription = "icone"
                                            )
                                            Spacer(modifier = Modifier.width(16.dp))
                                            Text(title)
                                        }
                                    },
                                        actions = {
                                            IconButton(onClick = {
                                                scope.launch {
                                                    drawerState.apply { if (isClosed) open() else close() }
                                                }
                                            }) {
                                                Icon(painterResource(R.drawable.menu), "menu",
                                                    Modifier.size(24.dp))
                                            }
                                            DropdownMenu(
                                                showDropDownMenu,
                                                onDismissRequest = { showDropDownMenu = false }) {
                                                DropdownMenuItem(
                                                    onClick = {},
                                                    text = { Text("ajuda") })
                                                DropdownMenuItem(
                                                    onClick = {},
                                                    text = { Text("config") })
                                            }
                                        }
                                    )
                                }
                            ) { innerPadding ->
                                var searchQuery by remember { mutableStateOf("") }
                                val filteredPokemon =
                                    pokemons.filter {
                                        it.name.contains(
                                            searchQuery,
                                            ignoreCase = true
                                        )
                                    }
                                val listState = rememberLazyListState()
                                var favListState = rememberLazyListState()
                                NavHost(navController, startDestination = "list") {
                                    composable("list") {
                                        var selected by remember { mutableStateOf("") }
                                        SharedTransitionLayout {
                                            AnimatedContent(selected, label = "hero") { state ->
                                                if (state == "") {
                                                    PokemonList(
                                                        filteredPokemon,
                                                        animatedVisibilityScope = this@AnimatedContent,
                                                        sharedTransitionScope = this@SharedTransitionLayout,
                                                        modifier = Modifier.padding(innerPadding),
                                                        searchQuery = searchQuery,
                                                        onSelectPokemon = { pokemon ->
                                                            selected = pokemon
                                                        },
                                                        onInputQuery = { input ->
                                                            searchQuery = input
                                                        },
                                                        state = listState
                                                    )

                                                } else {
                                                    val pokemon =
                                                        pokemons.first { it.name == state }
                                                    PokemonScreen(
                                                        pokemon,
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
                                    composable("fav") {
                                        var selected by remember { mutableStateOf("") }
                                        SharedTransitionLayout {
                                            AnimatedContent(selected, label = "fav") { state ->
                                                if (state == "") {
                                                    PokemonList(
                                                        filteredPokemon.filter { it.fav },
                                                        modifier = Modifier.padding(innerPadding),
                                                        animatedVisibilityScope = this@AnimatedContent,
                                                        sharedTransitionScope = this@SharedTransitionLayout,
                                                        onInputQuery = { searchQuery = it },
                                                        onSelectPokemon = { selected = it },
                                                        searchQuery = searchQuery,
                                                        state = favListState
                                                    )
                                                } else {
                                                    val pokemon =
                                                        pokemons.first { it.name == state }
                                                    PokemonScreen(
                                                        pokemon,
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

@Composable
fun PokemonList(list: List<Pokemon>, modifier: Modifier,
                onSelectPokemon: (String)-> Unit,
                onInputQuery: (String)->Unit,
                searchQuery: String,
                sharedTransitionScope: SharedTransitionScope,
                animatedVisibilityScope: AnimatedVisibilityScope,
                state: LazyListState) {
    with(sharedTransitionScope) {
        Column(modifier = modifier.fillMaxSize()) {
            TextField(
                value = searchQuery,
                onValueChange = { onInputQuery(it) },
                label = { Text("Pesquisar") },
                modifier = Modifier
                    .sharedElement(
                        rememberSharedContentState(key = "text"),
                        animatedVisibilityScope
                    )
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            val coroutine = rememberCoroutineScope()

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = 8.dp),
                state = state
            ) {
                itemsIndexed(list) { index, pokemon ->
                    var selecting by remember { mutableStateOf(false) }
                    var timedout by remember { mutableStateOf(false) }
                    if (selecting && (state.isScrollInProgress && !timedout)) {
                        DisposableEffect(Unit) {
                            onDispose {
                                coroutine.launch {
                                    state.stopScroll()
                                }
                                selecting=false
                                timedout=false
                                onSelectPokemon(pokemon.name)
                                println("scroll completed")
                            }
                        }
                    }
                    if(selecting) {
                        LaunchedEffect(Unit) {
                            while (true) {
                                delay(150.milliseconds)
                                timedout=true
                            }
                        }
                    }


                    ListItem(
                        pokemon,
                        index,
                        scrollToItem = {
                            coroutine.launch {
                                state.animateScrollToItem(index = index, -500)
                            }
                            selecting=true
                        },
                        onPokemonSelected = {

                        },
                        animatedVisibilityScope = animatedVisibilityScope,
                        sharedTransitionScope = sharedTransitionScope
                    )
                }
            }
        }
    }
}
@Composable
fun PokemonScreen(pokemon: Pokemon, modifier: Modifier,
                  onBack: ()->Unit,
                  searchQuery: String,
                  sharedTransitionScope: SharedTransitionScope,
                  animatedVisibilityScope: AnimatedVisibilityScope) {
    with(sharedTransitionScope) {
        Column (modifier = modifier
            .padding()
        ){
            TextField(
                value = searchQuery,
                onValueChange = { },
                modifier = Modifier.sharedElement(
                    rememberSharedContentState(key = "text"),
                    animatedVisibilityScope
                )
                    .fillMaxWidth()
                    .height(0.dp)
                    .absoluteOffset(y= (-20).dp)
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
                            val index = pokemons.indexOf(pokemon)
                            pokemons[index] = pokemon.copy(fav = !pokemon.fav)
                        }) {
                            Icon(
                                if (pokemon.fav) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                                "favoritar",
                                modifier = Modifier.sharedElement(
                                    rememberSharedContentState(key = "${pokemon.name}-fav"),
                                    animatedVisibilityScope
                                )
                            )
                        }
                    }
                    Image(
                        painter = painterResource(id = pokemon.imgId),
                        contentDescription = "${pokemon.name} Image",
                        modifier = Modifier
                            .sharedElement(
                                rememberSharedContentState(key = "${pokemon}-image"),
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
                            rememberSharedContentState(key = "${pokemon}-name"),
                            animatedVisibilityScope
                        )

                    )
                    Text(
                        text = pokemon.location,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = pokemon.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
@Composable
fun ListItem(pokemon: Pokemon,
             index: Int,
             scrollToItem: (Int)->Unit,
             onPokemonSelected: (Pokemon) -> Unit,
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
                    onPokemonSelected(pokemon)
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
                        Image(
                            painter = painterResource(id = pokemon.imgId),
                            contentDescription = "${pokemon.name} Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .sharedElement(
                                    rememberSharedContentState(key = "${pokemon}-image"),
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
                                    rememberSharedContentState(key = "${pokemon}-name"),
                                    animatedVisibilityScope
                                )
                            )
                            Text(
                                text = pokemon.type.first + "" + pokemon.type.second,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    TextButton(onClick = {
                        pokemons[pokemons.indexOf(pokemon)] = pokemon.copy(fav = !pokemon.fav)
                    }) {
                        Icon(
                            painterResource(if (pokemon.fav) R.drawable.favfill else R.drawable.favout),
                            contentDescription = "favoritar",
                            modifier = Modifier.sharedElement(
                                rememberSharedContentState(key = "${pokemon.name}-fav"),
                                animatedVisibilityScope
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = pokemon.nature,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Curiosidade: ${pokemon.abilities}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
