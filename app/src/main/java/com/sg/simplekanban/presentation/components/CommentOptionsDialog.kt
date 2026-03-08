package com.sg.simplekanban.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.sg.simplekanban.R
import com.sg.simplekanban.data.model.Comment
import com.sg.simplekanban.presentation.screens.card.CardViewModel

@Composable
fun CommentOptionsDialog (
    setShowDialog: (Boolean) -> Unit,
    setShowEditCommentDialog: (Comment) -> Unit,
    deleteComment: (Comment) -> Unit,
    comment : Comment,
) {

    Dialog(
        onDismissRequest = { setShowDialog(false) }
    ) {

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = colorResource(id = R.color.background)
        ) {

            Column(
                modifier = Modifier.padding(40.dp)
            ) {

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clickable {
                            setShowEditCommentDialog(comment)
                            setShowDialog(false)
                        },
                    text = stringResource(id = R.string.edit_comment).uppercase(),
                    color = colorResource(id = R.color.title),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clickable {
                            deleteComment(comment)
                        },
                    text = (stringResource(id = R.string.delete) + " "+ stringResource(id = R.string.comment)).uppercase(),
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
fun CommentOptionsDialogPreview(){
    CommentOptionsDialog(
        {},
        {},
        {} ,
        Comment(text = "hey", authorId = "1", creationDate = "Today")
    )
}
