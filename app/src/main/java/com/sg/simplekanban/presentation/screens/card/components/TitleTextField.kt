package com.sg.simplekanban.presentation.screens.card.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.sp
import com.sg.simplekanban.R
import com.sg.simplekanban.presentation.theme.PlaceholderGrey

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