package com.sg.simplekanban.presentation.screens.kanban.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sg.simplekanban.R
import com.sg.simplekanban.data.model.Kanban

@Composable
fun KanbanBody(
    kanbans: List<Kanban>,
    sharedWithMeKanbans: List<Kanban>,
    currentKanban: Kanban?,
    onAddKanbanClick: () -> Unit,
    onKanbanClick: (Kanban, Boolean) -> Unit,
    onMenuClick: (Kanban) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {

        BoxWithConstraints {
            val parentHeight = maxHeight

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
            ) {

                item {
                    AddKanbanButton(onClick = onAddKanbanClick)
                }

                item {
                    Column {
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(id = R.string.my_kanbans),
                            color = colorResource(id = R.color.title),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp
                        )
                    }
                }

                item {
                    LazyVerticalGrid(
                        modifier = Modifier.heightIn(max = parentHeight),
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {

                        items(kanbans.size) {
                            val myKanban = kanbans[it]

                            KanbanListItem(
                                kanban = myKanban,
                                isCurrentKanban = myKanban.documentId == currentKanban?.documentId,
                                isMyKanban = true,
                                onClick = {
                                    onKanbanClick(myKanban, true)
                                },
                                onMenuClick = {
                                    onMenuClick(myKanban)
                                }
                            )

                        }
                    }

                }

                if (sharedWithMeKanbans.isNotEmpty()) {

                    item {
                        Column {

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = stringResource(id = R.string.shared_with_me),
                                color = colorResource(id = R.color.title),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp
                            )
                        }
                    }

                    item {
                        LazyVerticalGrid(
                            modifier = Modifier.heightIn(max = parentHeight),
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {

                            items(sharedWithMeKanbans.size) {
                                val sharedKanban = sharedWithMeKanbans[it]
                                KanbanListItem(
                                    kanban = sharedKanban,
                                    isCurrentKanban = sharedKanban.documentId == currentKanban?.documentId,
                                    isMyKanban = false,
                                    onClick = { onKanbanClick(sharedKanban, false) },
                                    onMenuClick = {
                                        onMenuClick(sharedKanban)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}