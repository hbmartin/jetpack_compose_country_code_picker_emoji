package com.togitech.ccp.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.togitech.ccp.R
import com.togitech.ccp.data.CountryData
import com.togitech.ccp.data.utils.countryNames
import com.togitech.ccp.data.utils.emojiFlag
import com.togitech.ccp.data.utils.searchCountry
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

internal val DEFAULT_ROUNDING = 10.dp
private val DEFAULT_ROW_PADDING = 16.dp
private const val ROW_PADDING_VERTICAL_SCALING = 1.1f
private val DEFAULT_ROW_FONT_SIZE = 16.sp
private val SEARCH_ICON_PADDING = 5.dp

/**
 * @param onDismissRequest Executes when the user tries to dismiss the dialog.
 * @param onSelect Executes when the user selects a country from the list.
 * @param countryList The list of countries to display in the dialog.
 * @param modifier The modifier to be applied to the dialog surface.
 * @param rowPadding The padding to be applied to each row.
 * @param rowFontSize The font size to be applied to each row.
 */
@Composable
fun CountryDialog(
    onDismissRequest: () -> Unit,
    onSelect: (item: CountryData) -> Unit,
    countryList: ImmutableList<CountryData>,
    modifier: Modifier = Modifier,
    rowPadding: Dp = DEFAULT_ROW_PADDING,
    rowFontSize: TextUnit = DEFAULT_ROW_FONT_SIZE,
) {
    val context = LocalContext.current
    var searchValue by rememberSaveable { mutableStateOf("") }
    val filteredCountries by remember(context, searchValue) {
        derivedStateOf {
            if (searchValue.isEmpty()) {
                countryList
            } else {
                countryList.searchCountry(
                    searchValue,
                    context,
                )
            }
        }
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        content = {
            @Suppress("ReusedModifierInstance")
            Surface(
                color = MaterialTheme.colors.surface,
                modifier = modifier,
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    HeaderRow(onDismissRequest)
                    SearchTextField(
                        value = searchValue,
                        onValueChange = { searchValue = it },
                        textColor = MaterialTheme.colors.onSurface,
                        fontSize = DEFAULT_ROW_FONT_SIZE,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = stringResource(id = R.string.search),
                                tint = MaterialTheme.colors.onSurface,
                                modifier = Modifier.padding(horizontal = SEARCH_ICON_PADDING),
                            )
                        },
                        modifier = Modifier.padding(DEFAULT_ROW_PADDING),
                    )
                    Spacer(modifier = Modifier.height(DEFAULT_ROW_PADDING))
                    Divider()
                    LazyColumn {
                        items(filteredCountries, key = { it.countryIso }) { countryItem ->
                            CountryRowItem(
                                rowPadding = rowPadding,
                                onSelect = { onSelect(countryItem) },
                                countryItem = countryItem,
                                rowFontSize = rowFontSize,
                            )
                            Divider()
                        }
                    }
                }
            }
        },
    )
}

@Composable
private fun HeaderRow(onDismissRequest: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Spacer(modifier = Modifier.width(DEFAULT_ROW_PADDING))
        Text(
            text = stringResource(id = R.string.select_country),
            style = MaterialTheme.typography.h6,
            color = MaterialTheme.colors.onSurface,
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = { onDismissRequest() },
        ) {
            Icon(
                imageVector = Icons.Filled.Clear,
                contentDescription = "Close",
                tint = MaterialTheme.colors.onSurface,
            )
        }
    }
}

@Composable
private fun CountryRowItem(
    rowPadding: Dp,
    onSelect: () -> Unit,
    countryItem: CountryData,
    rowFontSize: TextUnit,
) {
    Row(
        Modifier
            .padding(
                horizontal = rowPadding,
                vertical = rowPadding * ROW_PADDING_VERTICAL_SCALING,
            )
            .fillMaxWidth()
            .clickable(onClick = { onSelect() }),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = countryItem.emojiFlag + "  " +
                stringResource(
                    id = countryNames.getOrDefault(
                        countryItem.countryIso,
                        R.string.unknown,
                    ),
                ),
            color = MaterialTheme.colors.onSurface,
            fontSize = rowFontSize,
            fontFamily = FontFamily.SansSerif,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun SearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    textColor: Color = Color.Black,
    leadingIcon: (@Composable () -> Unit)? = null,
    hint: String = stringResource(id = R.string.search),
    fontSize: TextUnit = MaterialTheme.typography.body2.fontSize,
) {
    BasicTextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = DEFAULT_ROW_PADDING),
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        cursorBrush = SolidColor(MaterialTheme.colors.primary),
        textStyle = LocalTextStyle.current.copy(
            color = textColor,
            fontSize = fontSize,
        ),
        decorationBox = { innerTextField ->
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (leadingIcon != null) leadingIcon()
                if (value.isEmpty()) {
                    Text(
                        text = hint,
                        maxLines = 1,
                        style = LocalTextStyle.current.copy(
                            color = textColor.copy(alpha = 0.5f),
                            fontSize = fontSize,
                        ),
                    )
                }
                innerTextField()
            }
        },
    )
}

@Preview
@Composable
private fun CountryDialogPreview() {
    CountryDialog(
        onSelect = {},
        countryList = CountryData.entries.toImmutableList(),
        onDismissRequest = {},
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(DEFAULT_ROUNDING)),
    )
}
