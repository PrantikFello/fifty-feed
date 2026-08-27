package com.bitgranules.androidproject.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bitgranules.androidproject.compositions.BgImageGridLayout
import com.bitgranules.androidproject.compositions.ImportButton
import com.bitgranules.androidproject.viewmodel.QuoteModelView

@Composable
fun ManagerScreen(viewModel: QuoteModelView) {
    val options = listOf("BG Gallery", "Option B", "Option C")

    var selectedOption by remember { mutableStateOf(options[0]) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 15.dp),
//            .border(width = 1.dp, color = MaterialTheme.colorScheme.secondary),
        Arrangement.SpaceEvenly, Alignment.CenterHorizontally
    ) {
        RadioButtonGroup(
            options = options,
            selectedOption = selectedOption,
            onOptionSelected = { selectedOption = it })

        when (selectedOption) {
            "BG Gallery" -> {
                BgManager(viewModel)
            }

            "Option B" -> {
                BgManager(viewModel)
            }

            "Option C" -> {
                Text("HELLO WORLD", modifier = Modifier.fillMaxSize())
            }

            else -> null
        }
    }
}

@Composable
fun RadioButtonGroup(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
) {


    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Top
    ) {
        options.forEach { text ->
            val selected = text == selectedOption
            Row(
                modifier = Modifier
                    .selectable(
                        selected = (text == selectedOption),
                        onClick = { onOptionSelected(text) })
                    .then(
                        if (selected) {
                            Modifier.border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.tertiary,
                                shape = MaterialTheme.shapes.medium
                            )
                        } else {
                            Modifier
                        }

                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {

                RadioButton(
                    selected = (selected),
                    onClick = { onOptionSelected(text) }
                )
                Text(
                    text = text,
                    modifier = Modifier.padding(end = 5.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
            }
        }
    }
}

@Composable
fun BgManager(
    viewModel: QuoteModelView,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = "Background Images ( )",
                fontSize = 15.sp,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.width(10.dp))
            ImportButton(viewModel)
        }

        BgImageGridLayout(viewModel)
    }

}