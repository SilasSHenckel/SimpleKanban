package com.sg.simplekanban.ui.components

import android.view.Gravity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.sg.simplekanban.R
import com.sg.simplekanban.data.inMemory.ColumnsInMemory
import com.sg.simplekanban.data.inMemory.KanbanInMemory
import com.sg.simplekanban.data.inMemory.UserInMemory
import com.sg.simplekanban.ui.routes.AppScreen
import com.sg.simplekanban.ui.screens.home.HomeViewModel
import com.sg.simplekanban.ui.theme.TitleGrey

@Composable
fun HomeOptionsDialog (
    nav: NavHostController,
    homeViewModel: HomeViewModel?,
    setShowDialog: (Boolean) -> Unit,
    modifier: Modifier
) {

    Dialog(
        onDismissRequest = { setShowDialog(false) },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {

        val dialogWindowProvider = LocalView.current.parent as DialogWindowProvider
        dialogWindowProvider.window.setGravity(Gravity.TOP or Gravity.END)

        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {

            Column(
                modifier = Modifier.padding(30.dp)
            ) {

                Text(
                    modifier = Modifier
                        .padding(vertical = 5.dp)
                        .clickable {
                            UserInMemory.currentKanbanUserId = homeViewModel?.lastKanbanUserId
                            UserInMemory.userId = homeViewModel?.userId
                            KanbanInMemory.currentKanbanId = homeViewModel?.currentKanban?.documentId
                            ColumnsInMemory.currentKanbanColumns = homeViewModel?.columns
                            nav.navigate(AppScreen.Columns.name)
                            setShowDialog(false)
                        },
                    text = stringResource(id = R.string.create_column),
                    color = TitleGrey,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    modifier = Modifier
                        .padding(vertical = 5.dp)
                        .clickable {

                        },
                    text = stringResource(id = R.string.share),
                    color = TitleGrey,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    modifier = Modifier
                        .padding(vertical = 5.dp)
                        .clickable {

                        },
                    text = stringResource(id = R.string.alternate_kanban),
                    color = TitleGrey,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    modifier = Modifier
                        .padding(vertical = 5.dp)
                        .clickable {

                        },
                    text = stringResource(id = R.string.my_profile),
                    color = TitleGrey,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                )
            }
        }
    }
}

@Preview
@Composable
fun HomeOptionsDialogPreview(){

    HomeOptionsDialog(
        rememberNavController(),
       null,
       {},
        Modifier
   )

}