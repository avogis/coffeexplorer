package com.example.coffeeexplorer.views

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.coffeeexplorer.domain.Coffee
import com.example.coffeeexplorer.views.utils.CoffeeImage
import com.example.coffeeexplorer.views.viewmodels.CoffeeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoffeeScreen(modifier: Modifier = Modifier, viewModel: CoffeeViewModel) {
    val coffeeItems = viewModel.pager.collectAsLazyPagingItems()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(text = "Coffee Explorer", style = MaterialTheme.typography.headlineLarge) },
                scrollBehavior = scrollBehavior
            )

        }
    ) { innerPadding ->

        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier.fillMaxSize(),
            state = rememberLazyListState(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(count = coffeeItems.itemCount) { index ->
                coffeeItems[index]?.let {
                    CoffeeItem(it)
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
}

@Composable
fun CoffeeItem(coffee: Coffee) {
    Row(
        modifier = Modifier
            .border(width = 2.dp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CoffeeImage(modifier = Modifier.weight(1f), url = coffee.image, description = coffee.title)
        Column(
            modifier = Modifier
                .padding(end = 16.dp)
                .weight(2f)
                .semantics(mergeDescendants = true) {
                    contentDescription = coffee.description
                },
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Text(modifier = Modifier.padding(bottom = 16.dp), text = coffee.title, style = MaterialTheme.typography.titleLarge)
            Text(text = coffee.description, style = MaterialTheme.typography.bodySmall)
        }
    }
}

