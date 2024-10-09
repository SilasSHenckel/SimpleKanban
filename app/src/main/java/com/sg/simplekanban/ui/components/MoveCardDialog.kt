package com.sg.simplekanban.ui.components

import android.widget.Toast
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.sg.simplekanban.R
import com.sg.simplekanban.data.model.Card
import com.sg.simplekanban.data.model.Column
import com.sg.simplekanban.ui.screens.home.HomeViewModel
import com.sg.simplekanban.ui.theme.CancelGrey
import com.sg.simplekanban.ui.theme.TextGrey
import com.sg.simplekanban.ui.theme.TitleGrey

@Composable
fun MoveCardDialog (
    card: Card,
    columns: List<Column>,
    homeViewModel: HomeViewModel?,
    setShowDialog: (Boolean) -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = { setShowDialog(false) }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {

            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp)
            ) {

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.card_selected),
                    color = TitleGrey,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "\"" + (card.title ?: "Criar Tela Home") + "\"",
                    color = TextGrey,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(30.dp))

                Text(
                    text = stringResource(id = R.string.move_to),
                    color = TextGrey,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(30.dp))

                for ( column in columns ){
                    Text(
                        modifier = Modifier.clickable {
                            homeViewModel?.moveCardToColumn(columnId = column.documentId ?: "", card = card)
                            setShowDialog(false)
                            Toast.makeText(context, ContextCompat.getString(context, R.string.card_moved) + " ${column.name}", Toast.LENGTH_LONG).show()
                        },
                        text = column.name ?: "",
                        color = TitleGrey,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            setShowDialog(false)
                        },
                    text = stringResource(id = R.string.cancel),
                    color = CancelGrey,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    textAlign = TextAlign.End,

                )

            }

        }
    }
}

@Preview
@Composable
fun MoveCardDialogPreview (){
    MoveCardDialog(
        card = Card(columnId = "0", title = "Criar Tela Home"),
        columns = listOf(
            Column(documentId = "1", name = "DOING", priority = 1),
            Column(documentId = "2", name = "DONE", priority = 2),
        ),
        homeViewModel = null, setShowDialog = {})
}