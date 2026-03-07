package com.sg.simplekanban.presentation.screens.columns

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.sg.simplekanban.R
import com.sg.simplekanban.data.model.Column
import com.sg.simplekanban.presentation.components.CreateColumnDialog
import com.sg.simplekanban.presentation.components.MyToolBar
import com.sg.simplekanban.presentation.theme.SelectedBlue
import com.sg.simplekanban.presentation.theme.White

@Composable
fun ColumnsScreen(
    nav: NavHostController,
    columnsViewModel: ColumnsViewModel? = hiltViewModel()
) {

    val columnsList = columnsViewModel?.currentKanbanColumns?.collectAsStateWithLifecycle()?.value

    Box (
        modifier = Modifier.fillMaxSize()
    ){
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            MyToolBar(title = stringResource(id = R.string.columns), nav = nav)
            MyBody(columnsViewModel, nav)
        }

        if(columnsViewModel?.showNewColumnDialog == true){
            CreateColumnDialog(
                columnsViewModel = columnsViewModel,
                setShowDialog = { columnsViewModel.showNewColumnDialog = it },
                columnsViewModel.columnToEdit,
                columnsList?.size ?: 0
            )
        }
    }
}

@Composable
fun MyBody(
    columnsViewModel: ColumnsViewModel?,
    nav: NavHostController,
){

    val columnsList = columnsViewModel?.currentKanbanColumns?.collectAsStateWithLifecycle()?.value ?: listOf()

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
                MyButtonAddColumn(columnsViewModel = columnsViewModel)
            }

            itemsIndexed(columnsList) { index: Int, column: Column ->
                MyListItem(column, nav = nav, columnsViewModel)
            }
        }
    }
}

@Composable
fun MyButtonAddColumn(
    columnsViewModel: ColumnsViewModel?
){
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(40.dp)
        .background(color = SelectedBlue, shape = RoundedCornerShape(20.dp))
        .clickable {
            columnsViewModel?.columnToEdit = null
            columnsViewModel?.showNewColumnDialog = true
        },
        Alignment.Center,
    ) {

        Icon(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp),
            imageVector = Icons.Rounded.Add,
            tint = White,
            contentDescription = "add"
        )

        Text(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 20.dp),
            text = stringResource(id = R.string.add_column),
            color = White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )
    }
}

@Composable
fun MyListItem(
    column: Column,
    nav: NavHostController,
    columnsViewModel: ColumnsViewModel?
){

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = colorResource(id = R.color.card_background),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(20.dp)
            .clickable {
                columnsViewModel?.columnToEdit = column
                columnsViewModel?.showNewColumnDialog = true
            }
    ) {
        Text(
            text = column.name ?: "",
            color = colorResource(id = R.color.title),
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        )
    }
}

@Preview
@Composable
fun ColumnsScreenPreview() {
    Surface {
        ColumnsScreen(rememberNavController(), null)
    }
}