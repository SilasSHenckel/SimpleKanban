package com.sg.simplekanban.presentation.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.sg.simplekanban.data.model.Card
import com.sg.simplekanban.presentation.screens.home.HomeViewModel

@Composable
fun HomeBody(
    columnId: String,
    homeViewModel: HomeViewModel,
    nav: NavHostController,
){

    val cardList = homeViewModel.cards.collectAsStateWithLifecycle().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 60.dp)
    ) {

        val kanbanMembers = homeViewModel.kanbanMembers.collectAsStateWithLifecycle()

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(space = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(all = 16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {

            item {
                AddCardButton(nav = nav, columnId, homeViewModel)
            }

            itemsIndexed(cardList) { index: Int, card: Card ->
                HomeListItem(card = card, nav, homeViewModel, kanbanMembers.value)
            }
        }
    }
}