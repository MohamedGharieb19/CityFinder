package com.gharieb.cityfinder.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeGesturesPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gharieb.cityfinder.R
import com.gharieb.cityfinder.domain.model.City
import com.gharieb.cityfinder.domain.model.normalize
import com.gharieb.cityfinder.presentation.ui.theme.lineColor

@Composable
fun CityScreen(
    viewModel: CityViewModel = hiltViewModel(),
    onCitySelected: (City) -> Unit
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val searchQuery by viewModel.querySearch.collectAsState()
    val result by viewModel.result.collectAsState()
    val totalCount = result.size

    Scaffold(
        topBar = { TopBar(totalCount = totalCount) },
        bottomBar = {
            BottomSearchBar(
                query = searchQuery,
                onQueryChange = { viewModel.onEvent(CityFinderEvents.OnQuerySearchChanged(it)) }
            )
        },
        containerColor = Color(0xFFF5F6F8)
    ) { innerPadding ->
        if (isLoading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding), contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(
                count = result.size,
                key = { result.start + it }
            ) { index ->
                val city = result.itemAt(index)
                val currentLetter = firstLetter(city)
                val prevLetter = if (index == 0) null else firstLetter(result.itemAt(index - 1))
                val isFirstOfSection = prevLetter == null || prevLetter != currentLetter
                val isFirstRow = index == 0
                val isLastRow = index == result.size - 1

                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                ) {
                    LetterRail(
                        letter = currentLetter,
                        showTopLine = !isFirstRow,
                        showBottomLine = !isLastRow,
                        showBadge = isFirstOfSection
                    )

                    CityCard(
                        modifier = Modifier
                            .weight(1f)
                            .padding(PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp)),
                        city = city,
                        onClick = { onCitySelected(city) }
                    )
                }
            }

        }
    }
}

@Composable
private fun LetterRail(
    letter: Char,
    showTopLine: Boolean,
    showBottomLine: Boolean,
    showBadge: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(48.dp)
            .fillMaxHeight()
    ) {
        if (showBadge) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .border(2.dp, lineColor, MaterialTheme.shapes.large)
                    .background(color = Color.White, shape = MaterialTheme.shapes.large)
            ) { Text(letter.toString(), style = MaterialTheme.typography.titleMedium) }
        }
        if (showTopLine) {
            Box(
                Modifier
                    .width(2.dp)
                    .weight(1f)
                    .background(lineColor)
            )
        }
        if (showBottomLine) {
            Box(
                Modifier
                    .width(2.dp)
                    .weight(1f)
                    .background(lineColor)
            )
        } else {
            Box(
                Modifier
                    .size(10.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(lineColor)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CityCard(
    modifier: Modifier = Modifier,
    city: City,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Row(
            Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(56.dp)
                    .background(color = Color(0xFFF0F2F5), shape = MaterialTheme.shapes.extraLarge)
            ) {
                Text(city.flagEmoji, style = MaterialTheme.typography.titleLarge)
            }
            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = city.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${city.coordinates.longitude}, ${city.coordinates.latitude}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF7C7F86)
                )
            }
        }
    }
}

@Composable
private fun BottomSearchBar(query: String, onQueryChange: (String) -> Unit) {
    Surface(shadowElevation = 8.dp, color = Color.White) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .safeGesturesPadding()
                .padding(PaddingValues(bottom = 12.dp, start = 12.dp, end = 12.dp)),
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text(stringResource(R.string.search)) },
            prefix = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null
                )
            },
            singleLine = true,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    totalCount: Int
) {
    val noOfCities = totalCount.toString()
        .plus(" ${if (totalCount == 1) stringResource(R.string.city) else stringResource(R.string.cities)}")

    Column(
        modifier = modifier
            .padding(bottom = 16.dp)
    ) {
        TopAppBar(
            title = { Text(stringResource(R.string.city_search), fontWeight = FontWeight.Bold) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = Color.Black
            )
        )
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = noOfCities,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF8D8D8D)
        )
    }
}

private fun firstLetter(city: City): Char =
    normalize(city.name).firstOrNull()?.uppercaseChar() ?: '#'