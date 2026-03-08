package com.sg.simplekanban.presentation.screens.columns

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.sg.simplekanban.R
import com.sg.simplekanban.data.model.Column
import com.sg.simplekanban.presentation.components.CreateColumnDialog
import com.sg.simplekanban.presentation.components.MyToolBar
import com.sg.simplekanban.presentation.screens.columns.components.AddColumnButton
import com.sg.simplekanban.presentation.screens.columns.components.ColumnListItem

@Composable
fun ColumnsScreen(
    nav: NavHostController,
    columnsViewModel: ColumnsViewModel? = hiltViewModel()
) {

    val showNewColumnDialog = columnsViewModel?.showNewColumnDialog?.collectAsStateWithLifecycle()?.value
    val columnsList = columnsViewModel?.currentKanbanColumns?.collectAsStateWithLifecycle()?.value ?: listOf()

    Box (
        modifier = Modifier.fillMaxSize()
    ){
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            MyToolBar(title = stringResource(id = R.string.columns), popBackStack = {nav.popBackStack()})
            ColumnsScreenBody(
                columnsList = columnsList,
                onAddColumnButtonClick = {
                    columnsViewModel?.columnToEdit = null
                    columnsViewModel?.setShowNewColumnDialog(true)
                },
                onColumnListItemClick = { columnToEdit ->
                    columnsViewModel?.columnToEdit = columnToEdit
                    columnsViewModel?.setShowNewColumnDialog(true)
                }
           )
        }

        if(showNewColumnDialog == true){
            CreateColumnDialog(
                columnsViewModel = columnsViewModel,
                setShowDialog = { columnsViewModel.setShowNewColumnDialog(it) },
                columnsViewModel.columnToEdit,
                columnsList.size
            )
        }
    }
}

@Composable
fun ColumnsScreenBody(
    columnsList : List<Column>,
    onAddColumnButtonClick: () -> Unit,
    onColumnListItemClick: (column: Column) -> Unit
){

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(space = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(all = 16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {

            item {
                AddColumnButton (onClick = onAddColumnButtonClick)
            }

            itemsIndexed(columnsList) { index: Int, column: Column ->
                ColumnListItem (column, onClick = { onColumnListItemClick(column) })
            }
        }
    }
}


@Preview
@Composable
fun ColumnsScreenPreview() {
    Surface {

        val nav = rememberNavController()

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            MyToolBar(title = stringResource(id = R.string.columns), popBackStack = { nav.popBackStack()})
            ColumnsScreenBody(
                columnsList = listOf(Column(name = "DONE")),
                onAddColumnButtonClick = {},
                onColumnListItemClick = {}
            )
        }
    }
}