package com.sg.simplekanban.presentation.screens.kanban.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sg.simplekanban.R
import com.sg.simplekanban.data.model.Kanban
import com.sg.simplekanban.presentation.theme.Yellow
import com.sg.simplekanban.presentation.theme.YellowLight

@Composable
fun KanbanListItem(
    kanban: Kanban,
    isCurrentKanban: Boolean,
    isMyKanban: Boolean,
    onClick: () -> Unit,
    onMenuClick: () -> Unit
) {

    val colorStops =
        if (isCurrentKanban) arrayOf(0.0f to Yellow, 1f to YellowLight)
        else arrayOf(
            0.0f to colorResource(id = R.color.card_background),
            1f to colorResource(id = R.color.card_background)
        )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .background(
                brush = Brush.verticalGradient(colorStops = colorStops),
                shape = RoundedCornerShape(15.dp)
            )
            .padding(16.dp)
            .clickable { onClick() }
    ) {

        Text(
            modifier = Modifier.padding(end = if (isMyKanban) 28.dp else 0.dp),
            text = kanban.name ?: "",
            textAlign = TextAlign.Start,
            color = if (isCurrentKanban) Color.White else colorResource(id = R.color.title),
            fontWeight = FontWeight.Bold,
            fontSize = 19.sp
        )

        if (isMyKanban) {
            IconButton(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(25.dp),
                onClick = onMenuClick
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null,
                    tint = colorResource(
                        id = if (isCurrentKanban) R.color.white else R.color.title
                    )
                )
            }
        }
    }
}