package com.sg.simplekanban.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.sg.simplekanban.R
import com.sg.simplekanban.presentation.screens.profile.ProfileViewModel
import com.sg.simplekanban.presentation.theme.CancelGrey

@Composable
fun DeleteAccountDialog (
    profileViewModel: ProfileViewModel,
    setShowDialog: (Boolean) -> Unit,
    nav: NavHostController,
) {

    Dialog(onDismissRequest = { setShowDialog(false) }) {

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = colorResource(id = R.color.background)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp)
            ) {

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = stringResource(id = R.string.delete_account_confirm),
                    color = colorResource(id = R.color.title),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                )

                Spacer(modifier =Modifier.height(20.dp))

                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {

                    Text(
                        modifier = Modifier
                            .padding(horizontal = 10.dp, vertical = 10.dp)
                            .clickable { setShowDialog(false) },
                        text = stringResource(id = R.string.cancel),
                        color = CancelGrey,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        modifier = Modifier
                            .padding(horizontal = 10.dp, vertical = 10.dp)
                            .clickable {
                                profileViewModel.deleteAccount(nav, setShowDialog)
                            },
                        text = stringResource(id = R.string.delete),
                        color = colorResource(id = R.color.title),
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )

                }

            }

        }

    }

}

