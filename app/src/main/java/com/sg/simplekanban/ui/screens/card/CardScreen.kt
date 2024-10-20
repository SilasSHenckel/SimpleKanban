package com.sg.simplekanban.ui.screens.card

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.sg.simplekanban.R
import com.sg.simplekanban.data.model.Card
import com.sg.simplekanban.ui.components.DeleteCardDialog
import com.sg.simplekanban.ui.theme.MenuBackgroundGrey
import com.sg.simplekanban.ui.theme.PlaceholderGrey
import com.sg.simplekanban.ui.theme.Purple40
import com.sg.simplekanban.ui.theme.SelectedBlue
import com.sg.simplekanban.ui.theme.TitleGrey
import com.sg.simplekanban.data.inMemory.CardInMemory
import com.sg.simplekanban.data.inMemory.UserInMemory
import com.sg.simplekanban.ui.components.MyProgressBar

@Composable
fun CardScreen (
    nav: NavHostController = rememberNavController(),
    columnId: String?,
    cardViewModel: CardViewModel = hiltViewModel(),
){

    val userId = UserInMemory.userId
    val card = CardInMemory.card

    val isCreatingCard: Boolean = (card == null)

    val context = LocalContext.current

    val initialTitle = card?.title ?: ""
    val initialDescription = card?.description ?: ""

    var showButton by remember { mutableStateOf(false) }

    var title by remember { mutableStateOf(TextFieldValue(initialTitle)) }
    var description by remember { mutableStateOf(TextFieldValue(initialDescription)) }

    Box (
        modifier = Modifier
        .fillMaxSize()
    ){

        Column {

            Box (
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(color = colorResource(id = R.color.menu_background)),
                Alignment.Center
            ){
                Text(
                    modifier = Modifier
                        .align(alignment = Alignment.CenterStart)
                        .padding(start = 20.dp),
                    text = stringResource(id = R.string.create_card),
                    color = colorResource(id = R.color.title),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                )

                Row (
                    modifier = Modifier
                        .align(alignment = Alignment.CenterEnd),
                ){

                    if(showButton){
                        Button(
                            onClick = {
                                if(isCreatingCard){
                                    onSaveClick(title.text, description.text, columnId ?: "0", 3, userId, context, cardViewModel, nav)
                                } else {
                                    onUpdateClick(card, title.text, description.text, context, cardViewModel, nav)
                                }
                            },
                            colors = ButtonColors(SelectedBlue, Color.White, Purple40, Color.White)
                        ) {
                            Text(text = if(isCreatingCard) stringResource(id = R.string.save) else stringResource(id = R.string.update))
                        }
                    }


                    if(!isCreatingCard){
                        IconButton(
                            onClick = { cardViewModel.showDeleteCardDialog = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = colorResource(id = R.color.title)
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            TitleTextField(
                text = title,
                onValueChange = { newText ->
                    if(newText.text != title.text && !showButton) showButton = true
                    title = newText
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            DescriptionTextField(
                text = description,
                onValueChange = { newText ->
                    if(newText.text != description.text && !showButton) showButton = true
                    description = newText
                }
            )

        }

        if(!isCreatingCard){
            val showDeleteCardDialog = cardViewModel.showDeleteCardDialog
            if(showDeleteCardDialog && card != null){
                DeleteCardDialog(
                    card = card,
                    cardViewModel = cardViewModel,
                    setShowDialog = { cardViewModel.showDeleteCardDialog = it },
                    requestCloseScreen = {
                        nav.popBackStack()
                        nav.currentBackStackEntry?.savedStateHandle?.set("cardDeleted", card)
                    }
                )
            }
        }

        val isLoading = cardViewModel.isLoading
        if(isLoading) MyProgressBar()
    }

}


fun onSaveClick(
    title: String?,
    description: String,
    columnId: String,
    priority: Int,
    userId: String?,
    context: Context,
    cardViewModel: CardViewModel,
    nav: NavHostController,
){
    if(!title.isNullOrEmpty()){
        cardViewModel.saveCard(title, description, columnId, priority, userId, nav)
    } else {
        Toast.makeText(context, ContextCompat.getString(context, R.string.insert_title), Toast.LENGTH_LONG).show()
    }
}

fun onUpdateClick(
    card: Card?,
    title: String?,
    description: String,
    context: Context,
    cardViewModel: CardViewModel,
    nav: NavHostController,
){
    if(card != null){
        if(!title.isNullOrEmpty()){
            if(verifyIfHasChangesInCard(card, title, description)){
                card.title = title
                card.description = description
                cardViewModel.updateCard(card, nav)
            } else {
                nav.popBackStack()
            }
        } else {
            Toast.makeText(context, ContextCompat.getString(context, R.string.insert_title), Toast.LENGTH_LONG).show()
        }
    }
}

fun verifyIfHasChangesInCard(
    card: Card,
    title: String?,
    description: String
) : Boolean{
    return card.title != title || card.description != description
}

@Composable
fun TitleTextField(
    text: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
) {
    TextField(
        modifier = Modifier.fillMaxWidth(),
        textStyle = TextStyle(fontWeight = FontWeight.Medium, fontSize = 18.sp),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = colorResource(id = R.color.menu_background),
            unfocusedIndicatorColor = colorResource(id = R.color.menu_background),
            focusedIndicatorColor = colorResource(id = R.color.menu_background),
            focusedTextColor = colorResource(id = R.color.title)
        ),
        value = text,
        onValueChange = { onValueChange(it) },
        placeholder = {
            Text(
                text = stringResource(id = R.string.title),
                color = PlaceholderGrey,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp
            )
        },
        maxLines = 1
    )
}

@Composable
fun DescriptionTextField(
    text: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit
) {
    TextField(
        modifier = Modifier.fillMaxWidth(),
        textStyle = TextStyle(fontWeight = FontWeight.Medium, fontSize = 18.sp),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = colorResource(id = R.color.menu_background),
            unfocusedIndicatorColor = colorResource(id = R.color.menu_background),
            focusedIndicatorColor = colorResource(id = R.color.menu_background),
            focusedTextColor = colorResource(id = R.color.title)
        ),
        value = text,
        onValueChange = { onValueChange(it) },
        placeholder = { Text(
            text = stringResource(id = R.string.description),
            color = PlaceholderGrey,
            fontWeight = FontWeight.Medium,
            fontSize = 18.sp
        )}
    )
}

