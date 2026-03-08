package com.sg.simplekanban.presentation.screens.columns.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sg.simplekanban.R
import com.sg.simplekanban.data.model.Column

@Composable
fun ColumnListItem(
    column: Column,
    onClick : () -> Unit
){

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = colorResource(id = R.color.card_background),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(20.dp)
            .clickable {
                onClick()
            }
    ) {
        Text(
            text = column.name ?: "",
            color = colorResource(id = R.color.title),
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        )
    }
}