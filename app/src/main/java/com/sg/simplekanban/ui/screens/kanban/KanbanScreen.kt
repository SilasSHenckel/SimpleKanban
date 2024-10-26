package com.sg.simplekanban.ui.screens.kanban
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.sg.simplekanban.R
import com.sg.simplekanban.data.model.Kanban
import com.sg.simplekanban.ui.components.MyToolBar
import com.sg.simplekanban.ui.theme.SelectedBlue
import com.sg.simplekanban.ui.theme.White

val kanbans = (1..4).toList()

@Composable
fun KanbanScreen(
    nav: NavHostController,
    kanbanViewModel: KanbanViewModel? = hiltViewModel()
) {
    Box (
        modifier = Modifier.fillMaxSize()
    ){
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            MyToolBar(title = stringResource(id = R.string.alternate_kanban), nav = nav)
            MyBody(kanbanViewModel, nav)
        }

    }
}

@Composable
fun MyBody(
    kanbanViewModel: KanbanViewModel?,
    nav: NavHostController
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        MyButtonAddKanban()

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp),
        ) {
            items(kanbans.size){
                //MyListItem("Kanban", nav, kanbanViewModel)
            }
        }
        

    }

}

@Composable
fun MyButtonAddKanban(

) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(40.dp)
        .background(color = SelectedBlue, shape = RoundedCornerShape(20.dp))
        .clickable {

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
            text = stringResource(id = R.string.create_kanban),
            color = White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )
    }

}

@Composable
fun MyListItem(
    kanban: Kanban,
    nav: NavHostController,
    kanbanViewModel: KanbanViewModel?
){

    Box (
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = colorResource(id = R.color.card_background),
                shape = RoundedCornerShape(15.dp)
            )
            .padding(top = 15.dp, start = 15.dp, bottom = 100.dp)
            .clickable {

            }
    ) {
        Text(
            text = kanban.name ?: "",
            textAlign = TextAlign.Start,
            color = colorResource(id = R.color.title),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
    }
}

@Preview
@Composable
fun KanbanScreenPreview(){
    Surface {
        KanbanScreen(
            rememberNavController(),
            null
        )
    }
}