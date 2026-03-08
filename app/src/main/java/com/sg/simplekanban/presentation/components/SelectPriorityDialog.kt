package com.sg.simplekanban.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.sg.simplekanban.R
import com.sg.simplekanban.data.model.CardPriority

@Composable
fun SelectPriorityDialog (
    priorities: List<CardPriority>,
    setShowDialog: (Boolean) -> Unit,
    title: String,
    onSelect: (CardPriority) -> Unit,
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
                    text = title,
                    color = colorResource(id = R.color.title),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 24.sp,
                )

                Spacer(modifier = Modifier.height(30.dp))

                LazyColumn (
                    verticalArrangement = Arrangement.spacedBy(space = 30.dp),
                ){
                    itemsIndexed(priorities) { index: Int, priority: CardPriority ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                onSelect(priority)
                                setShowDialog(false)
                            }
                        ){
                            Text(
                                text = priority.name?.uppercase() ?: "",
                                color = colorResource(id = R.color.text),
                                fontSize = 18.sp,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun SelectPriorityDialogPreview(){

    SelectPriorityDialog(
        priorities = listOf(
            CardPriority(0, "Select Priority", "#9E9E9E", "#3E3E3E"),
            CardPriority(1, "Low Priority", "#73FF88", "#0BA923"),
            CardPriority(2, "Medium Priority", "#FFDA73", "#E49800"),
            CardPriority(3, "High Priority", "#FFA3A3", "#E83411"),
        ),
        setShowDialog = {},
        title = "Maybe",
        onSelect = {},
    )

}


