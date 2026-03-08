package com.sg.simplekanban.presentation.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sg.simplekanban.R

@Composable
fun HomeTab(
    name: String,
    onTabClick: () -> Unit,
    selected: Boolean,
    widthDivisionNumber: Int = 3,
    listSize: Int
) {

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    var widthSpaceRequired = if(listSize > 3) (if(isTablet()) 30.dp else 20.dp) else 0.dp

    Box(
        modifier = Modifier
            .clickable { onTabClick() }
            .width((screenWidth / widthDivisionNumber) - widthSpaceRequired)
            .height(60.dp),
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = name,
            color = if (selected) colorResource(id = R.color.blue_selected) else colorResource(id = R.color.title),
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )

        if (selected) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .background(
                        color = colorResource(id = R.color.blue_selected),
                        shape = RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp)
                    )
                    .height(3.dp)
                    .width((screenWidth / widthDivisionNumber) - 40.dp)
            ) {

            }
        }
    }
}