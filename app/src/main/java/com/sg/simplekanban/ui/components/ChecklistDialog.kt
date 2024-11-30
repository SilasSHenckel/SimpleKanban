package com.sg.simplekanban.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.window.Dialog
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.google.firebase.auth.FirebaseAuth
import com.sg.simplekanban.R
import com.sg.simplekanban.commom.util.DateUtil
import com.sg.simplekanban.data.model.Card
import com.sg.simplekanban.data.model.Comment
import com.sg.simplekanban.ui.screens.card.CardViewModel

@Composable
fun ChecklistDialog (
    cardViewModel: CardViewModel,
    setShowDialog: (Boolean) -> Unit,
    card : Card,
) {

    val checklist = card.checklist ?: hashMapOf()
    val context = LocalContext.current

    Dialog(
        onDismissRequest = { setShowDialog(false) }
    ) {

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = colorResource(id = R.color.background)
        ) {

            val keyboardController = LocalSoftwareKeyboardController.current
            var newItem by remember { mutableStateOf(TextFieldValue("")) }
            var showSaveItemButton by remember { mutableStateOf(false) }
            val configuration = LocalConfiguration.current

            Column(
                modifier = Modifier.padding(40.dp)
            ) {

                Text(
                    modifier = Modifier.padding(start = 10.dp),
                    text = stringResource(id = R.string.checklist),
                    color = colorResource(id = R.color.title),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 24.sp,
                )

                Spacer(modifier = Modifier.height(30.dp))

                MyCommentTextField(
                    text = newItem,
                    onValueChange = { newText ->
                        if(newText.text != newItem.text && !showSaveItemButton) showSaveItemButton = true
                        if(newText.text.length < 100) newItem = newText
                        if(showSaveItemButton && newText.text.isEmpty()) showSaveItemButton = false
                    },
                    placeholderText = stringResource(id = R.string.write_comment),
                    width = configuration.screenWidthDp.dp - 23.dp,
                    paddingStart = 23.dp,
                    showSendButton = showSaveItemButton,
                    onSendClicked = {
                        if(newItem.text.isNotEmpty()) {

                            val mapItem = hashMapOf<String, Boolean>()
                            mapItem[newItem.text] = false

                            checklist[DateUtil.getCurrentDateFormated()] = mapItem

                            card.checklist = checklist

                            cardViewModel.updateChecklist(card = card,
                                onFinish =  {
                                    newItem = TextFieldValue("")
                                }
                            )
                        }

                        keyboardController?.hide()
                    }
                )

                Spacer(modifier = Modifier.height(30.dp))

                if(checklist.isNotEmpty()){
                    LazyColumn {
                        itemsIndexed(checklist.toList()){ index, item ->
                            Row (modifier = Modifier.padding(start = 5.dp, end = 5.dp , bottom = 12.dp)) {
                                ChecklistItem(card = card, item = item, cardViewModel = cardViewModel)
                            }
                        }

                        item{
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ChecklistItem(
    card: Card,
    item: Pair<String, HashMap<String, Boolean>>,
    cardViewModel: CardViewModel
){
    Column (
        modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp,)
    ) {
        Row (
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(start = 20.dp, end = 5.dp, bottom = 10.dp, top = 10.dp)
                .fillMaxWidth()
        ){

            var text : String? = null
            var checkedValue = false

            val list = item.second.toList()
            if(list.isNotEmpty()) {
                text = list[0].first
                checkedValue = list[0].second
            }

            var checked by remember { mutableStateOf(checkedValue) }

//            card.checklist[item.first] = hashMapOf()

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text ?: ""
                )
                Checkbox(
                    checked = checked,
                    onCheckedChange = { checked = it }
                )
            }

//            IconButton(
//                onClick = { cardViewModel.showCommentOptionsDialog = comment }
//            ) {
//                Icon(
//                    imageVector = Icons.Default.MoreVert,
//                    contentDescription = null,
//                    tint = colorResource(id = R.color.title)
//                )
//            }
        }
    }
}

