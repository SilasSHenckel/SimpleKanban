package com.sg.simplekanban.presentation.components

import android.view.Gravity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
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
import com.google.firebase.auth.FirebaseAuth
import com.sg.simplekanban.R
import com.sg.simplekanban.presentation.navigation.AppScreen
import com.sg.simplekanban.presentation.screens.home.HomeViewModel

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
            modifier = modifier.width(220.dp),
            shape = RoundedCornerShape(16.dp),
            color = colorResource(id = R.color.background)
        ) {

            Column(
                modifier = Modifier.padding(30.dp)
            ) {

                if(homeViewModel?.currentKanbanUserId == FirebaseAuth.getInstance().currentUser?.uid){
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .clickable {
                                homeViewModel?.setShowEditNameDialog(true)
                                setShowDialog(false)
                            },
                        text = stringResource(id = R.string.change_kanban_name),
                        color = colorResource(id = R.color.title),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                }

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clickable {
                            homeViewModel?.updateUserIdWithFirebaseUser()
                            nav.navigate(AppScreen.Columns.name)
                            setShowDialog(false)
                        },
                    text = stringResource(id = R.string.manage_columns),
                    color = colorResource(id = R.color.title),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                )

                Spacer(modifier = Modifier.height(14.dp))

                if(homeViewModel?.currentKanbanUserId == FirebaseAuth.getInstance().currentUser?.uid){
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .clickable {
                                homeViewModel?.setShowShareDialog(true)
                                setShowDialog(false)
                            },
                        text = stringResource(id = R.string.share),
                        color = colorResource(id = R.color.title),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                }

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clickable {
                            nav.navigate(AppScreen.Kanbans.name)
                            setShowDialog(false)
                        },
                    text = stringResource(id = R.string.alternate_kanban),
                    color = colorResource(id = R.color.title),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clickable {
                            nav.navigate(AppScreen.Profile.name)
                            setShowDialog(false)
                        },
                    text = stringResource(id = R.string.my_profile),
                    color = colorResource(id = R.color.title),
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