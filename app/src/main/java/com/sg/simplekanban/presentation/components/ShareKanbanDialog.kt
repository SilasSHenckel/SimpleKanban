package com.sg.simplekanban.presentation.components

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.google.firebase.auth.FirebaseAuth
import com.sg.simplekanban.R
import com.sg.simplekanban.data.singleton.CurrentKanbanManager
import com.sg.simplekanban.presentation.screens.home.HomeViewModel
import com.sg.simplekanban.presentation.theme.PlaceholderGrey
import com.sg.simplekanban.presentation.theme.Purple40
import com.sg.simplekanban.presentation.theme.SelectedBlue

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ShareKanbanDialog (
    homeViewModel: HomeViewModel?,
    setShowDialog: (Boolean) -> Unit,
) {

    val context = LocalContext.current

    Dialog(
        onDismissRequest = { setShowDialog(false) }
    ) {

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = colorResource(id = R.color.background)
        ) {

            var showButton by remember { mutableStateOf(false) }
            var email by remember { mutableStateOf(TextFieldValue("")) }

            Column(
                modifier = Modifier.padding(horizontal = 30.dp, vertical = 40.dp)
            ) {

                Text(
                    modifier = Modifier.padding(start = 10.dp),
                    text = stringResource(id = R.string.share),
                    color = colorResource(id = R.color.title),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 24.sp,
                )

                Spacer(modifier = Modifier.height(20.dp))

                MyTextField(
                    text = email,
                    onValueChange = { newText ->
                        if(newText.text != email.text && !showButton) showButton = true
                        if(newText.text.length < 320) email = newText
                    },
                    placeholderText = stringResource(id = R.string.email),
                    KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                if(showButton){
                    Spacer(modifier = Modifier.height(20.dp))

                    Row (
                        modifier = Modifier.width(350.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Button(
                            onClick = {
                                if(showButton){
                                    if(email.text.isNotEmpty()){
                                        if(isEmailValid(email.text)){
                                            if(!isAlreadySharedWithEmail(email.text, homeViewModel?.getCurrentKanban()?.sharedWithUsers)){
                                                homeViewModel?.getUserByEmail(
                                                    email.text,
                                                    onSuccess = {
                                                        email = TextFieldValue("")
                                                        showButton = false
                                                    },
                                                    context
                                                )
                                            } else {
                                                Toast.makeText(context, ContextCompat.getString(context, R.string.invalid_email), Toast.LENGTH_LONG).show()
                                            }
                                        } else {
                                            Toast.makeText(context, ContextCompat.getString(context, R.string.invalid_email), Toast.LENGTH_LONG).show()
                                        }
                                    } else {
                                        Toast.makeText(context, ContextCompat.getString(context, R.string.insert_title), Toast.LENGTH_LONG).show()
                                    }
                                }
                            },
                            colors = ButtonColors(if(showButton) SelectedBlue else colorResource(id = R.color.menu_background), if(showButton) Color.White else PlaceholderGrey, Purple40, Color.White)
                        ) {
                            Text(text =  stringResource(id = R.string.search_user).uppercase())
                        }
                    }
                }

                val userFounded = homeViewModel?.userFounded

                if(userFounded != null){

                    Spacer(modifier = Modifier.height(40.dp))

                    Text(
                        modifier = Modifier.padding(start = 10.dp),
                        text = stringResource(id = R.string.user_founded),
                        color = colorResource(id = R.color.title),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row {

                        if(userFounded.photoUrl != null){

                            Spacer(modifier = Modifier.width(10.dp))

                            GlideImage(
                                model = userFounded.photoUrl,
                                contentDescription = "user",
                                modifier = Modifier.size(30.dp).clip(RoundedCornerShape(15.dp)),
                            )

                            Spacer(modifier = Modifier.width(5.dp))
                        }

                        Text(
                            modifier = Modifier.padding(start = 10.dp),
                            text = userFounded.email ?: "",
                            color = colorResource(id = R.color.text),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        modifier = Modifier
                            .height(36.dp)
                            .align(Alignment.End),
                        onClick = {
                            homeViewModel.shareKanbanWithUser(userFounded, context)
                        },
                        colors = ButtonColors( SelectedBlue, Color.White , Purple40, Color.White)
                    ) {
                        Text(text =  stringResource(id = R.string.share).uppercase())
                    }

                }

                val sharedWithUsers = homeViewModel?.getCurrentKanban()?.sharedWithUsers
                if(sharedWithUsers != null){
                    Spacer(modifier = Modifier.height(40.dp))

                    Text(
                        modifier = Modifier.padding(start = 10.dp),
                        text = stringResource(id = R.string.kanban_members),
                        color = colorResource(id = R.color.title),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    val listSharedWithUsers = sharedWithUsers.toList()

                    LazyColumn (
                        verticalArrangement = Arrangement.spacedBy(space = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        items(listSharedWithUsers.size){
                            Text(
                                modifier = Modifier.padding(start = 10.dp),
                                text = listSharedWithUsers[it].second,
                                color = colorResource(id = R.color.text),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                            )
                        }
                    }
                }
            }
        }
    }
}

fun isEmailValid(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() && email != FirebaseAuth.getInstance().currentUser?.email
}

fun isAlreadySharedWithEmail(email: String, sharedWithUsers: HashMap<String, String>?) : Boolean{
    if(sharedWithUsers != null){
        for((userId, userEmail) in sharedWithUsers){
           if(email == userEmail) return true
        }
    }
    return false
}