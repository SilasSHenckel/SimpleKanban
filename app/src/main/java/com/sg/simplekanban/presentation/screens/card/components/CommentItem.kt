package com.sg.simplekanban.presentation.screens.card.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.google.firebase.auth.FirebaseAuth
import com.sg.simplekanban.R
import com.sg.simplekanban.data.model.Comment
import com.sg.simplekanban.presentation.screens.card.CardViewModel

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
            color = colorResource(id = if (isAuthorCurrentUser) R.color.comment_author_background else R.color.comment_background),
            shape = RoundedCornerShape(
                topStart = 30.dp,
                topEnd = 30.dp,
                bottomStart = if (isAuthorCurrentUser) 30.dp else 0.dp,
                bottomEnd = if (isAuthorCurrentUser) 0.dp else 30.dp
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
                    onClick = { cardViewModel.setShowCommentOptionsDialog(comment) }
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