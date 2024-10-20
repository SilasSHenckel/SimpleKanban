package com.sg.simplekanban.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.sg.simplekanban.R
import com.sg.simplekanban.data.model.Card
import com.sg.simplekanban.ui.screens.card.CardViewModel
import com.sg.simplekanban.ui.theme.CancelGrey
import com.sg.simplekanban.ui.theme.TitleGrey

@Composable
fun DeleteCardDialog (
    card: Card,
    cardViewModel: CardViewModel?,
    setShowDialog: (Boolean) -> Unit,
    requestCloseScreen: () -> Unit,
) {

    Dialog(onDismissRequest = { setShowDialog(false) }) {

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp)
            ) {

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = stringResource(id = R.string.delete_card),
                    color = TitleGrey,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                )

                Spacer(modifier =Modifier.height(20.dp))

                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 10.dp, vertical = 10.dp)
                            .clickable {
                                cardViewModel?.deleteCard(card, setShowDialog, requestCloseScreen)
                            },
                        text = stringResource(id = R.string.delete),
                        color = TitleGrey,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        modifier = Modifier
                            .padding(horizontal = 10.dp, vertical = 10.dp)
                            .clickable { setShowDialog(false) },
                        text = stringResource(id = R.string.cancel),
                        color = CancelGrey,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )

                }

            }

        }

    }

}

@Preview
@Composable
fun DeleteCardDialogPreview(){

   DeleteCardDialog(
       Card(title = "Quarquer coisa"),
       null,
       {}, {}
   )

}