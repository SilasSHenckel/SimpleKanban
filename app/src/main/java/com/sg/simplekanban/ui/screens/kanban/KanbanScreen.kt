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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.sg.simplekanban.data.inMemory.KanbanInMemory
import com.sg.simplekanban.data.model.Kanban
import com.sg.simplekanban.ui.components.CreateColumnDialog
import com.sg.simplekanban.ui.components.CreateKanbanDialog
import com.sg.simplekanban.ui.components.MyProgressBar
import com.sg.simplekanban.ui.components.MyToolBar
import com.sg.simplekanban.ui.theme.BlueDark
import com.sg.simplekanban.ui.theme.BlueLight
import com.sg.simplekanban.ui.theme.QuaseWhite
import com.sg.simplekanban.ui.theme.SelectedBlue
import com.sg.simplekanban.ui.theme.TextGrey
import com.sg.simplekanban.ui.theme.TitleGrey
import com.sg.simplekanban.ui.theme.White
import com.sg.simplekanban.ui.theme.Yellow
import com.sg.simplekanban.ui.theme.YellowLight

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

        if(kanbanViewModel?.showNewKanbanDialog == true){
            CreateKanbanDialog(
                kanbanViewModel = kanbanViewModel,
                setShowDialog = { kanbanViewModel.showNewKanbanDialog = it }
            )
        }

        val isLoading = kanbanViewModel?.isLoading ?: false
        if(isLoading) MyProgressBar()
    }
}

@Composable
fun MyBody(
    kanbanViewModel: KanbanViewModel?,
    nav: NavHostController
){

    val kanbans = kanbanViewModel?.kanbans ?: listOf()

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
                MyListItem(kanbans[it], nav, kanbanViewModel)
            }
        }

    }

}

@Composable
fun MyButtonAddKanban(
    kanbanViewModel: KanbanViewModel? = hiltViewModel()
) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(40.dp)
        .background(color = SelectedBlue, shape = RoundedCornerShape(20.dp))
        .clickable {
            kanbanViewModel?.showNewKanbanDialog = true
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
            text = stringResource(id = R.string.create_kanban).uppercase(),
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

    val isCurrentKanban = kanban.documentId == KanbanInMemory.currentKanban?.documentId

    val colorStops = if(isCurrentKanban) arrayOf(0.0f to Yellow, 1f to YellowLight)
    else arrayOf(0.0f to colorResource(id = R.color.card_background), 1f to colorResource(id = R.color.card_background))

    Box (
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .background(
                brush = Brush.verticalGradient(colorStops = colorStops),
                shape = RoundedCornerShape(15.dp)
            )
            .padding(16.dp)
            .clickable {
                kanbanViewModel?.selectKanban(kanban, nav)
            }
    ) {
        Text(
            text = kanban.name ?: "",
            textAlign = TextAlign.Start,
            color = if(isCurrentKanban) Color.White else colorResource(id = R.color.title),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
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