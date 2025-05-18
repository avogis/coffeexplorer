package com.example.coffeeexplorer.views

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.coffeeexplorer.domain.Coffee
import com.example.coffeeexplorer.views.utils.CoffeeImage
import com.example.coffeeexplorer.views.viewmodels.CoffeeViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun CoffeeScreen(modifier: Modifier = Modifier, viewModel: CoffeeViewModel) {
    val coffeeItems = viewModel.pager.collectAsLazyPagingItems()
    val lazyListState = rememberLazyListState()
    var showCoffee: Coffee? by remember {
        mutableStateOf(null)
    }
    val focusRequester = remember { FocusRequester() }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Coffee Explorer", style = MaterialTheme.typography.headlineLarge) },
                navigationIcon = {
                    showCoffee?.let {
                        IconButton(onClick = { showCoffee = null }) {
                            Icon(
                                modifier = Modifier
                                    .focusRequester(focusRequester)
                                    .focusable(true),
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Tryck två gånger för att gå tillbaka"
                            )
                        }

                    }
                },
            )
        }
    ) { innerPadding ->
        SharedTransitionLayout(modifier = Modifier.padding(innerPadding)) {
            AnimatedContent(
                showCoffee,
                label = "basic_transition",
                contentAlignment = Alignment.Center,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(220, delayMillis = 90)) +
                            scaleIn(initialScale = 0.50f, animationSpec = tween(220, delayMillis = 90)))
                        .togetherWith(fadeOut(animationSpec = tween(90)))
                },
            ) { targetState ->
                if (targetState == null) {
                    MainContent(
                        onShowDetails = { coffee ->
                            showCoffee = coffee
                        },
                        animatedVisibilityScope = this@AnimatedContent,
                        sharedTransitionScope = this@SharedTransitionLayout,
                        coffeeItems = coffeeItems,
                        lazyListState = lazyListState
                    )
                } else {
                    DetailsContent(
                        onBack = {
                            showCoffee = null
                        },
                        animatedVisibilityScope = this@AnimatedContent,
                        sharedTransitionScope = this@SharedTransitionLayout,
                        coffee = targetState
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun DetailsContent(
    onBack: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    coffee: Coffee,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        with(sharedTransitionScope) {
            Column(
                Modifier
                    .verticalScroll(rememberScrollState())
                    .sharedElement(
                        rememberSharedContentState(key = "image"),
                        animatedVisibilityScope = animatedVisibilityScope
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                CoffeeInfo(coffee)
                CoffeeImage(
                    modifier = Modifier
                        .clickable(
                            onClick = onBack,
                            role = Role.Button,
                            onClickLabel = "Tillbaka till kaffelistan"
                        ),
                    url = coffee.image,
                    description = null
                )
            }
        }
    }
}

@Composable
fun CoffeeInfo(coffee: Coffee) {
    Column(
        modifier = Modifier.semantics(mergeDescendants = true) {},
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(text = coffee.title, style = MaterialTheme.typography.titleLarge)
        Row {
            Text(
                text = "Ingredienser: " + coffee.ingredients.joinToString(", "),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
        Text(text = coffee.description, style = MaterialTheme.typography.bodySmall)
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun MainContent(
    onShowDetails: (Coffee) -> Unit,
    coffeeItems: LazyPagingItems<Coffee>,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    lazyListState: LazyListState
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = lazyListState,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(count = coffeeItems.itemCount) { index ->
            coffeeItems[index]?.let {
                with(sharedTransitionScope) {
                    CoffeeItem(
                        modifier = Modifier
                            .sharedElement(
                                rememberSharedContentState(key = "image"),
                                animatedVisibilityScope = animatedVisibilityScope
                            ),
                        coffee = it,
                        onClick = onShowDetails
                    )
                }
            }
        }

        coffeeItems.apply {
            when {
                loadState.refresh is LoadState.Loading -> {
                    item { Text("Loading...") }
                }

                loadState.append is LoadState.Loading -> {
                    item { Text("Loading more...") }
                }

                loadState.refresh is LoadState.Error -> {
                    val e = coffeeItems.loadState.refresh as LoadState.Error
                    item { Text("Error: ${e.error.localizedMessage}") }
                }
            }
        }
    }
}

@Composable
private fun CoffeeItem(modifier: Modifier = Modifier, coffee: Coffee, onClick: (Coffee) -> Unit) {
    Row(
        modifier = Modifier
            .padding(16.dp)
            .border(width = 2.dp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f))
            .semantics { this.isTraversalGroup = true },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CoffeeImage(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
                .padding(vertical = 8.dp)
                .clickable(
                    role = Role.Button,
                    onClickLabel = "Ta reda på mer om ${coffee.title}",
                    onClick = { onClick(coffee) }
                )
                .semantics {
                    this.traversalIndex = 1f
                },
            url = coffee.image,
            description = null
        )
        Column(
            modifier = Modifier
                .padding(end = 8.dp)
                .weight(2f)
                .semantics(mergeDescendants = true) {
                    this.traversalIndex = 0f
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = coffee.title,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            Text(modifier = Modifier.padding(8.dp), text = coffee.description, style = MaterialTheme.typography.bodySmall)
        }
    }
}
