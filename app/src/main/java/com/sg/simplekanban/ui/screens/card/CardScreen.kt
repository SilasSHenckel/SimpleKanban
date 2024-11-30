package com.sg.simplekanban.ui.screens.card

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.google.firebase.auth.FirebaseAuth
import com.sg.simplekanban.R
import com.sg.simplekanban.commom.util.DateUtil
import com.sg.simplekanban.data.model.Card
import com.sg.simplekanban.ui.components.DeleteCardDialog
import com.sg.simplekanban.ui.theme.PlaceholderGrey
import com.sg.simplekanban.ui.theme.Purple40
import com.sg.simplekanban.ui.theme.SelectedBlue
import com.sg.simplekanban.data.inMemory.CardInMemory
import com.sg.simplekanban.data.inMemory.KanbanInMemory
import com.sg.simplekanban.data.inMemory.UserInMemory
import com.sg.simplekanban.data.model.Comment
import com.sg.simplekanban.ui.components.CommentOptionsDialog
import com.sg.simplekanban.ui.components.DateAndTimePickerDialog
import com.sg.simplekanban.ui.components.EditCommentDialog
import com.sg.simplekanban.ui.components.MyCommentTextField
import com.sg.simplekanban.ui.components.MyProgressBar
import com.sg.simplekanban.ui.components.SelectPriorityDialog
import com.sg.simplekanban.ui.components.SelectUserDialog

@OptIn(ExperimentalGlideComposeApi::class)
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

                Row (
                    modifier = Modifier.align(alignment = Alignment.CenterStart),
                    verticalAlignment = Alignment.CenterVertically
                ){

                    Spacer(modifier = Modifier.width(14.dp))

                    IconButton(
                        onClick = {
                            nav.popBackStack()
                        }
                    ) {
                        androidx.compose.material.Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = null,
                            tint = colorResource(id = R.color.title)
                        )
                    }

                    Spacer(modifier = Modifier.width(5.dp))

                    Text(
                        text = stringResource(id = R.string.create_card),
                        color = colorResource(id = R.color.title),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp
                    )
                }



                Row (
                    modifier = Modifier
                        .align(alignment = Alignment.CenterEnd),
                ){

                    if(showButton){
                        Button(
                            onClick = {
                                if(isCreatingCard){
                                    onSaveClick(title.text, description.text, columnId ?: "0", cardViewModel.priority?.id ?: 0, userId, context, cardViewModel, nav)
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

            Row (modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(color = colorResource(id = R.color.background))) {

            }

            LazyColumn {
                item {
                    Column {

                        Spacer(modifier = Modifier.height(14.dp))

                        TitleTextField(
                            text = title,
                            onValueChange = { newText ->
                                if(newText.text != title.text && !showButton) showButton = true
                                if(newText.text.length < 62) title = newText
                            }
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        DescriptionTextField(
                            text = description,
                            onValueChange = { newText ->
                                if(newText.text != description.text && !showButton) showButton = true
                                if(newText.text.length < 500) description = newText
                            }
                        )

                        Spacer(modifier = Modifier.height(34.dp))

                        val configuration = LocalConfiguration.current
                        val width = configuration.screenWidthDp.dp / 5

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            val selectedPriority = cardViewModel.priority

                            Button(
                                modifier = Modifier
                                    .width((configuration.screenWidthDp.dp / 2) - 30.dp)
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(
                                                Color(
                                                    (selectedPriority?.color1
                                                        ?: "#9E9E9E").toColorInt()
                                                ),
                                                Color(
                                                    (selectedPriority?.color2
                                                        ?: "#3E3E3E").toColorInt()
                                                )
                                            )
                                        ), shape = RoundedCornerShape(8.dp)
                                    )
                                    .height(30.dp),
                                contentPadding = PaddingValues(5.dp),

                                onClick = {
                                    cardViewModel.showSelectPriorityDialog = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                            ) {
                                Row (
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 7.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ){
                                    Image(painter = painterResource(id = R.drawable.priority), contentDescription = "finuto minal", Modifier.size(18.dp))
                                    Text(selectedPriority?.name?.uppercase() ?: stringResource(id = R.string.select_priority).uppercase(), fontSize = 12.sp, color = Color.White)
                                }

                            }

                            Button(
                                modifier = Modifier
                                    .width((configuration.screenWidthDp.dp / 2) - 30.dp)
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(
                                                Color("#57D8E6".toColorInt()),
                                                Color("#096DE4".toColorInt())
                                            )
                                        ), shape = RoundedCornerShape(8.dp)
                                    )
                                    .height(30.dp),
                                contentPadding = PaddingValues(5.dp),

                                onClick = {
                                    cardViewModel.showSelectPriorityDialog = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                            ) {
                                Row (
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 7.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ){
                                    Image(painter = painterResource(id = R.drawable.check), contentDescription = "finuto minal", Modifier.size(15.dp))
                                    Text(text = stringResource(id = R.string.create_checklist).uppercase(), fontSize = 12.sp, color = Color.White)
                                }

                            }
                        }

                        Spacer(modifier = Modifier.height(34.dp))

                        if (KanbanInMemory.currentKanban?.shared == true){
                            Row (
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ){

                                Text(
                                    modifier = Modifier
                                        .width(width * 2)
                                        .padding(start = 20.dp),
                                    text = stringResource(id = R.string.responsible),
                                    color = colorResource(id = R.color.title),
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 18.sp
                                )

                                val responsible = cardViewModel.responsible

                                if(responsible?.photoUrl == null){
                                    Image(
                                        painter = painterResource(id = R.drawable.profile),
                                        contentDescription = "user",
                                        modifier = Modifier
                                            .size(30.dp)
                                            .clip(RoundedCornerShape(15.dp))
                                    )
                                } else {
                                    GlideImage(
                                        model = responsible.photoUrl,
                                        contentDescription = "user",
                                        modifier = Modifier
                                            .size(30.dp)
                                            .clip(RoundedCornerShape(15.dp)),
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Text(
                                    modifier = Modifier
                                        .width((width * 3) - 52.dp)
                                        .clickable {
                                            cardViewModel.showSelectResponsibleDialog = true
                                        },
                                    text = responsible?.name ?: (responsible?.email ?: stringResource(id = R.string.not_assigned))  ,
                                    color = colorResource(id = R.color.title),
                                    fontSize = 16.sp
                                )
                            }
                        }


                        Spacer(modifier = Modifier.height(20.dp))

                        if(!isCreatingCard){
                            if (KanbanInMemory.currentKanban?.shared == true){
                                Row (
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ){

                                    Text(
                                        modifier = Modifier
                                            .width(width * 2)
                                            .padding(start = 20.dp),
                                        text = stringResource(id = R.string.creator),
                                        color = colorResource(id = R.color.title),
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 18.sp
                                    )

                                    val author = cardViewModel.author

                                    if(author?.photoUrl == null ){
                                        Image(
                                            painter = painterResource(id = R.drawable.profile),
                                            contentDescription = "user",
                                            modifier = Modifier
                                                .size(30.dp)
                                                .clip(RoundedCornerShape(15.dp))
                                        )
                                    } else {
                                        GlideImage(
                                            model = author.photoUrl,
                                            contentDescription = "user",
                                            modifier = Modifier
                                                .size(30.dp)
                                                .clip(RoundedCornerShape(15.dp)),
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Text(
                                        modifier = Modifier
                                            .width((width * 3) - 52.dp),
                                        text = author?.name ?: (author?.email ?: ""),
                                        color = colorResource(id = R.color.title),
                                        fontSize = 16.sp
                                    )
                                }
                            }


                            Spacer(modifier = Modifier.height(20.dp))

                            Row (
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ){
                                //start date
                                Text(
                                    modifier = Modifier
                                        .width(width * 2)
                                        .padding(start = 20.dp),
                                    text = stringResource(id = R.string.start_date),
                                    color = colorResource(id = R.color.title),
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 18.sp
                                )

                                val startDate = cardViewModel.startDate ?: card?.startDate

                                Text(
                                    modifier = Modifier
                                        .width((width * 3) - 52.dp)
                                        .clickable {
                                            cardViewModel.showSelectStartDateDialog = true
                                        },
                                    text = if(startDate != null) startDate else stringResource(id = R.string.select),
                                    color = if(startDate != null) colorResource(id = R.color.title) else colorResource(id = R.color.hint),
                                    fontSize = 16.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Row (
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ){
                                //end date
                                Text(
                                    modifier = Modifier
                                        .width(width * 2)
                                        .padding(start = 20.dp),
                                    text = stringResource(id = R.string.end_date),
                                    color = colorResource(id = R.color.title),
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 18.sp
                                )

                                val endDate = cardViewModel.finalDate ?: card?.endDate

                                Text(
                                    modifier = Modifier
                                        .width((width * 3) - 52.dp)
                                        .clickable {
                                            cardViewModel.showSelectFinalDateDialog = true
                                        },
                                    text =  if(endDate != null) endDate else stringResource(id = R.string.select),
                                    color = if(endDate != null) colorResource(id = R.color.title) else colorResource(id = R.color.hint),
                                    fontSize = 16.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(34.dp))

                            //COMENTS
                            Text(
                                modifier = Modifier
                                    .padding(start = 20.dp),
                                text = stringResource(id = R.string.comments),
                                color = colorResource(id = R.color.title),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 20.sp
                            )

                            val keyboardController = LocalSoftwareKeyboardController.current
                            var newComment by remember { mutableStateOf(TextFieldValue("")) }
                            var showCommentButton by remember { mutableStateOf(false) }

                            Spacer(modifier = Modifier.height(24.dp))


                            MyCommentTextField(
                                text = newComment,
                                onValueChange = { newText ->
                                    if(newText.text != newComment.text && !showCommentButton) showCommentButton = true
                                    if(newText.text.length < 100) newComment = newText
                                    if(showCommentButton && newText.text.isEmpty()) showCommentButton = false
                                },
                                placeholderText = stringResource(id = R.string.write_comment),
                                width = configuration.screenWidthDp.dp - 23.dp,
                                paddingStart = 23.dp,
                                showSendButton = showCommentButton,
                                onSendClicked = {
                                    cardViewModel.saveComment(commentText = newComment.text,
                                        onCommentSaved =  {
                                            newComment = TextFieldValue("")
                                        }
                                    )
                                    keyboardController?.hide()
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }

                val comments = cardViewModel.comments

                if(comments.isNotEmpty()){
                    items(comments.size){ index ->
                        Row (modifier = Modifier.padding(start = 5.dp, end = 5.dp , bottom = 12.dp)) {
                            CommentItem(comment = comments[index], cardViewModel = cardViewModel)
                        }
                    }

                    item{
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }





        if(!isCreatingCard){
            val showDeleteCardDialog = cardViewModel.showDeleteCardDialog
            if(showDeleteCardDialog && card != null){
                DeleteCardDialog(
                    card = card,
                    cardViewModel = cardViewModel,
                    setShowDialog = { cardViewModel.showDeleteCardDialog = it },
                    requestCloseScreen = {
                        cardViewModel.removeCardFromList(card)
                        nav.popBackStack()
                    }
                )
            }
        }


        if(cardViewModel.showSelectResponsibleDialog){
            SelectUserDialog(
                users = KanbanInMemory.kanbanMembers,
                setShowDialog = {cardViewModel.showSelectResponsibleDialog = false},
                title = stringResource(id = R.string.responsible),
                onSelectUser = { user ->
                    cardViewModel.responsible = user
                    if(!showButton) showButton = true
                }
            )
        }

        if(cardViewModel.showSelectPriorityDialog){
            SelectPriorityDialog(
                priorities = cardViewModel.priorities.filter { it.id != 0 },
                setShowDialog = {cardViewModel.showSelectPriorityDialog = false},
                title = stringResource(id = R.string.select_priority),
                onSelect = { priority ->
                    cardViewModel.priority = priority
                    if(!showButton) showButton = true
                }
            )
        }

        if(cardViewModel.showSelectStartDateDialog){
            DateAndTimePickerDialog(
                onConfirm = { date ->
                    cardViewModel.startDate = DateUtil.getDateFormated(date)
                    if(!showButton) showButton = true
                },
                onDismiss = { cardViewModel.showSelectStartDateDialog = false },
            )
        }

        if(cardViewModel.showSelectFinalDateDialog){
            DateAndTimePickerDialog(
                onConfirm = { date ->
                    cardViewModel.finalDate = DateUtil.getDateFormated(date)
                    if(!showButton) showButton = true
                },
                onDismiss = {cardViewModel.showSelectFinalDateDialog = false},
            )
        }

        if(cardViewModel.showCommentOptionsDialog != null){
            CommentOptionsDialog(
                cardViewModel,
                setShowDialog = {cardViewModel.showCommentOptionsDialog = null},
                comment = cardViewModel.showCommentOptionsDialog!!
            )
        }

        if(cardViewModel.showEditCommentDialog != null){
            EditCommentDialog(
                cardViewModel,
                setShowDialog = {cardViewModel.showEditCommentDialog = null},
                commentToEdit = cardViewModel.showEditCommentDialog!!
            )
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
            if(verifyIfHasChangesInCard(card, title, description, cardViewModel.responsible?.documentId, cardViewModel.priority?.id ?: card.priority, cardViewModel)){
                card.title = title
                card.description = description
                card.responsibleId = cardViewModel.responsible?.documentId
                card.priority = cardViewModel.priority?.id ?: card.priority
                card.startDate = cardViewModel.startDate ?: card.startDate
                card.endDate = cardViewModel.finalDate ?: card.endDate
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
    description: String,
    responsibleId: String?,
    selectedPriority: Int,
    cardViewModel: CardViewModel
) : Boolean{
    return card.title != title ||
            card.description != description ||
            card.responsibleId != responsibleId ||
            card.priority != selectedPriority ||
            card.startDate != cardViewModel.startDate ||
            card.endDate != cardViewModel.finalDate
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

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CommentItem(
    comment: Comment,
    cardViewModel: CardViewModel
){

    val isAuthorCurrentUser = comment.authorId == FirebaseAuth.getInstance().currentUser?.uid

    Column (modifier = Modifier
        .fillMaxWidth()
        .padding(
            start = if (isAuthorCurrentUser) 50.dp else 20.dp,
            end = if (isAuthorCurrentUser) 20.dp else 50.dp
        )
        .background(
            color = colorResource(id = if(isAuthorCurrentUser) R.color.comment_author_background else R.color.comment_background) ,
            shape = RoundedCornerShape(
                topStart = 30.dp,
                topEnd = 30.dp,
                bottomStart = if (isAuthorCurrentUser) 30.dp else 0.dp,
                bottomEnd = if (isAuthorCurrentUser) 0.dp else 30 .dp
            )
        )) {
        Row (
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(start = 20.dp, end = 5.dp, bottom = 10.dp, top = 10.dp)
                .fillMaxWidth()
        ){
            Row {
                val author = cardViewModel.getKanbanMember(comment.authorId)

                if(author?.photoUrl == null ){
                    Image(
                        painter = painterResource(id = R.drawable.profile),
                        contentDescription = "author",
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(15.dp))
                    )
                } else {
                    GlideImage(
                        model = author.photoUrl,
                        contentDescription = "author",
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(15.dp)),
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    modifier = Modifier,
                    text = author?.name ?: (author?.email ?: ""),
                    color = colorResource(id = R.color.title),
                    fontSize = 16.sp
                )
            }
            if(isAuthorCurrentUser) {
                IconButton(
                    onClick = { cardViewModel.showCommentOptionsDialog = comment }
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = null,
                        tint = colorResource(id = R.color.title)
                    )
                }
            }
        }

        Text(
            modifier = Modifier.padding(start = 20.dp, end = 20.dp),
            text = comment.text ?: "",
            color = colorResource(id = R.color.title),
            fontSize = 16.sp
        )

        Text(
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
            text = comment.creationDate ?: "",
            color = colorResource(id = R.color.text),
            fontSize = 12.sp
        )

    }

}

