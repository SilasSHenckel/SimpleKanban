package com.sg.simplekanban.presentation.screens.card

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
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.sg.simplekanban.R
import com.sg.simplekanban.data.model.Card
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.sg.simplekanban.presentation.components.MyCommentTextField
import com.sg.simplekanban.presentation.screens.card.components.CardScreenDialogs
import com.sg.simplekanban.presentation.screens.card.components.CommentItem
import com.sg.simplekanban.presentation.screens.card.components.DescriptionTextField
import com.sg.simplekanban.presentation.screens.card.components.SaveOrUpdateButton
import com.sg.simplekanban.presentation.screens.card.components.TitleTextField
import com.sg.simplekanban.presentation.screens.card.components.onSaveClick
import com.sg.simplekanban.presentation.screens.card.components.onUpdateClick

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CardScreen (
    nav: NavHostController = rememberNavController(),
    columnId: String?,
    cardViewModel: CardViewModel = hiltViewModel(),
){

    val userId = cardViewModel.userId
    val card : Card? = cardViewModel.card

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
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = null,
                            tint = colorResource(id = R.color.title)
                        )
                    }

                    Spacer(modifier = Modifier.width(5.dp))

                    Text(
                        text = stringResource(id = if(isCreatingCard) R.string.create_card else R.string.update_card),
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
                        SaveOrUpdateButton(
                            isCreatingCard = isCreatingCard,
                            onSave = { onSaveClick(title.text, description.text, columnId ?: "0", cardViewModel.priority.value?.id ?: 0, userId, context, cardViewModel, { nav.popBackStack() })} ,
                            onUpdate = { onUpdateClick(card, title.text, description.text, context, cardViewModel, { nav.popBackStack() })}
                        )
                    }

                    if(!isCreatingCard){
                        IconButton(
                            onClick = { cardViewModel.setShowDeleteCardDialog(true) }
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

                            val selectedPriority = cardViewModel.priority.collectAsStateWithLifecycle().value

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
                                    cardViewModel.setShowSelectPriorityDialog(true)
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
                                    cardViewModel.setShowChecklistDialog(true)
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
                                    Text(text = stringResource(id = if(card?.checklist.isNullOrEmpty() && cardViewModel.checklistTemp.isNullOrEmpty()) R.string.create_checklist else R.string.see_checklist).uppercase(), fontSize = 12.sp, color = Color.White)
                                }

                            }
                        }

                        Spacer(modifier = Modifier.height(34.dp))

                        val currentKanban = cardViewModel.currentKanban.collectAsStateWithLifecycle()

                        if (currentKanban.value?.shared == true){
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

                                val responsible = cardViewModel.responsible.collectAsStateWithLifecycle().value

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
                                            cardViewModel.setShowSelectResponsibleDialog(true)
                                        },
                                    text = responsible?.name ?: (responsible?.email ?: stringResource(id = R.string.not_assigned))  ,
                                    color = colorResource(id = R.color.title),
                                    fontSize = 16.sp
                                )
                            }
                        }


                        Spacer(modifier = Modifier.height(20.dp))

                        if(!isCreatingCard){
                            if (currentKanban.value?.shared == true){
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

                                    val author = cardViewModel.author.collectAsStateWithLifecycle().value

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

                                val startDate = cardViewModel.startDate.value ?: card?.startDate

                                Text(
                                    modifier = Modifier
                                        .width((width * 3) - 52.dp)
                                        .clickable {
                                            cardViewModel.setShowSelectStartDateDialog(true)
                                        },
                                    text = startDate ?: stringResource(id = R.string.select),
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

                                val endDate = cardViewModel.finalDate.collectAsStateWithLifecycle().value ?: card?.endDate

                                Text(
                                    modifier = Modifier
                                        .width((width * 3) - 52.dp)
                                        .clickable {
                                            cardViewModel.setShowSelectFinalDateDialog(true)
                                        },
                                    text = endDate ?: stringResource(id = R.string.select),
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

                val comments = cardViewModel.comments.value

                if(comments.isNotEmpty()){
                    items(comments.size){ index ->
                        Row (modifier = Modifier.padding(start = 5.dp, end = 5.dp , bottom = 12.dp)) {
                            CommentItem(comment = comments[index], cardViewModel = cardViewModel)
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }

        CardScreenDialogs(
            isCreatingCard = isCreatingCard,
            card = card,
            popBackStack = { nav.popBackStack() },
            cardViewModel,
            showButton = showButton,
            setShowButton = { value -> showButton = value},
        )
    }
}

