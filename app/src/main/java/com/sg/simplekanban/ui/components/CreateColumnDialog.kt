package com.sg.simplekanban.ui.components

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.sg.simplekanban.R
import com.sg.simplekanban.data.model.Column
import com.sg.simplekanban.ui.screens.columns.ColumnsViewModel
import com.sg.simplekanban.ui.theme.CancelGrey
import com.sg.simplekanban.ui.theme.PlaceholderGrey
import com.sg.simplekanban.ui.theme.Purple40
import com.sg.simplekanban.ui.theme.SelectedBlue

@Composable
fun CreateColumnDialog (
    columnsViewModel: ColumnsViewModel?,
    setShowDialog: (Boolean) -> Unit,
    columnToEdit : Column?,
    priority: Int,
) {

    val context = LocalContext.current

    Dialog(
        onDismissRequest = { setShowDialog(false) }
    ) {

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = colorResource(id = R.color.background)
        ) {

            var showButton by remember { mutableStateOf(false) }
            var name by remember { mutableStateOf(TextFieldValue(columnToEdit?.name ?: "")) }

            Column(
                modifier = Modifier.padding(40.dp)
            ) {

                Text(
                    modifier = Modifier.padding(start = 10.dp),
                    text = stringResource(id = R.string.create_column),
                    color = colorResource(id = R.color.title),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 24.sp,
                )

                Spacer(modifier = Modifier.height(30.dp))

                ColumnNameTextField(
                    text = name,
                    onValueChange = { newText ->
                        if(newText.text != name.text && !showButton) showButton = true
                        newText.text.uppercase()
                        if(newText.text.length < 19) name = newText
                    }
                )

                Spacer(modifier = Modifier.height(30.dp))

                Row (
                    modifier = Modifier.width(350.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ){

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


                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = {
                            if(showButton){
                                if (columnToEdit == null){
                                    if(name.text.isNotEmpty()){
                                        columnsViewModel?.saveColumn(name.text.uppercase(), priority, onSaveColumn = {setShowDialog(false)})
                                    } else {
                                        Toast.makeText(context, ContextCompat.getString(context, R.string.insert_title), Toast.LENGTH_LONG).show()
                                    }
                                } else {
                                    if(name.text.isNotEmpty()){
                                        columnsViewModel?.updateColum(columnToEdit, name.text.uppercase(), onSuccess = {setShowDialog(false)})
                                    } else {
                                        Toast.makeText(context, ContextCompat.getString(context, R.string.insert_title), Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        },
                        colors = ButtonColors(if(showButton) SelectedBlue else colorResource(id = R.color.menu_background), if(showButton) Color.White else PlaceholderGrey, Purple40, Color.White)
                    ) {
                        Text(text = if(columnToEdit == null) stringResource(id = R.string.save).uppercase() else stringResource(id = R.string.update).uppercase())
                    }

                }

            }
        }
    }
}

@Composable
fun ColumnNameTextField(
    text: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    placeholderText: String = stringResource(id = R.string.name),
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
) {
    TextField(
        modifier = Modifier
            .width(350.dp)
            .padding(start = 5.dp),
        textStyle = TextStyle(fontWeight = FontWeight.Medium, fontSize = 18.sp),
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
                fontSize = 18.sp,
            )
        },
        maxLines = 1,
    )
}
