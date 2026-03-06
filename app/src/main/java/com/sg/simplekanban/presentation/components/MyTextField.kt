package com.sg.simplekanban.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sg.simplekanban.R
import com.sg.simplekanban.presentation.theme.PlaceholderGrey
import com.sg.simplekanban.presentation.theme.SelectedBlue

@Composable
fun MyTextField(
    text: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    placeholderText: String = stringResource(id = R.string.name),
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    width: Dp = 370.dp,
    paddingStart: Dp = 5.dp
) {
    TextField(
        modifier = Modifier
            .width(width)
            .padding(start = paddingStart),
        textStyle = TextStyle(fontWeight = FontWeight.Medium, fontSize = 16.sp),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = colorResource(id = R.color.menu_background),
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            focusedTextColor = colorResource(id = R.color.title)
        ),
        shape = RoundedCornerShape(50),
        value = text,
        onValueChange = { onValueChange(it) },
        keyboardOptions = keyboardOptions,
        placeholder = {
            Text(
                modifier = Modifier.padding(start = 5.dp),
                text = placeholderText,
                color = PlaceholderGrey,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
            )
        },
        maxLines = 1,
    )
}

@Composable
fun MyCommentTextField(
    text: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    placeholderText: String = stringResource(id = R.string.name),
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    width: Dp = 370.dp,
    paddingStart: Dp = 5.dp,
    showSendButton: Boolean = false,
    onSendClicked: () -> Unit
) {
    Box(){
        TextField(
            modifier = Modifier
                .width(width)
                .padding(start = paddingStart, end = if (showSendButton) 60.dp else 0.dp),
            textStyle = TextStyle(fontWeight = FontWeight.Medium, fontSize = 16.sp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = colorResource(id = R.color.menu_background),
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                focusedTextColor = colorResource(id = R.color.title)
            ),
            shape = RoundedCornerShape(50),
            value = text,
            onValueChange = { onValueChange(it) },
            keyboardOptions = keyboardOptions,
            placeholder = {
                Text(
                    modifier = Modifier.padding(start = 5.dp),
                    text = placeholderText,
                    color = PlaceholderGrey,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                )
            },
            maxLines = 1,
        )
        if(showSendButton){
            IconButton(
                modifier = Modifier.align(Alignment.CenterEnd).background(color = SelectedBlue, shape = RoundedCornerShape(50)),
                onClick = {
                    onSendClicked()
                }
            ) {
                Icon(Icons.Rounded.Send, contentDescription = "send", tint = Color.White)
            }
        }
    }

}