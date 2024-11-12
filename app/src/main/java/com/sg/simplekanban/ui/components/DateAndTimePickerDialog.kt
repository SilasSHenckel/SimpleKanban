package com.sg.simplekanban.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateAndTimePickerDialog(
    onConfirm: (Calendar) -> Unit,
    onDismiss: () -> Unit,
) {

    val selectedDate = Calendar.getInstance()

    val timePickerState = rememberTimePickerState(
        initialHour = selectedDate.get(Calendar.HOUR_OF_DAY),
        initialMinute = selectedDate.get(Calendar.MINUTE),
        is24Hour = true,
    )

    val stage = remember { mutableIntStateOf(0) }

    val datePickerState = rememberDatePickerState()

    if(stage.intValue == 0){
        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = {
                    selectedDate.timeInMillis = datePickerState.selectedDateMillis ?: Calendar.getInstance().timeInMillis
                    stage.intValue = 1
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    } else {

        TimePickerDialog(
            onDismiss = { onDismiss() },
            onConfirm = {
                selectedDate.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                selectedDate.set(Calendar.MINUTE, timePickerState.minute)
                onConfirm(selectedDate)
                onDismiss()
            }
        ) {
            TimePicker(
                state = timePickerState,
            )
        }
    }

}

@Composable
fun TimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm() }) {
                Text("OK")
            }
        },
        text = { content() }
    )
}