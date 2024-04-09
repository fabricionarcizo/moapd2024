/*
 * MIT License
 *
 * Copyright (c) 2024 Fabricio Batista Narcizo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package dk.itu.moapd.lazycomposable

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

/**
 * Composable function for displaying a row item.
 *
 * @param dummy The dummy model containing information to display in the row item.
 * @param modifier Optional modifier for configuring the layout and behavior of the row item.
 *
 * @see DummyModel
 * @see DummyImage
 * @see DummyText
 * @see DummyButtons
 */
@Composable
fun RowItem(
    dummy: DummyModel,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier.padding(8.dp)
    ) {
        Column {
            DummyImage(dummy.url)
            DummyText(dummy)
            DummyButtons()
        }
    }
}

/**
 * Composable function for displaying a dummy image.
 *
 * @param url The URL of the image to display.
 *
 * @see AsyncImage
 */
@Composable
private fun DummyImage(url: String) {
    AsyncImage(
        model = url,
        contentDescription = stringResource(R.string.content_description_card),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .height(194.dp)
    )
}

/**
 * Composable function for displaying dummy text information.
 *
 * @param dummy The dummy model containing text information to display.
 *
 * @see DummyModel
 */
@Composable
private fun DummyText(dummy: DummyModel) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = dummy.cityName,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = stringResource(R.string.secondary_text, dummy.country, dummy.zipCode),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = dummy.description,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

/**
 * Composable function for displaying dummy buttons.
 *
 * This function contains three icons/buttons: ThumbUp, Favorite, and Share.
 *
 * @see IconButton
 */
@Composable
private fun DummyButtons() {
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(onClick = { /* Do something */ }) {
            Icon(
                Icons.Filled.ThumbUp,
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = stringResource(R.string.content_description_thumb_up)
            )
        }
        IconButton(onClick = { /* Do something */ }) {
            Icon(
                Icons.Filled.Favorite,
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = stringResource(R.string.content_description_favorite)
            )
        }
        IconButton(onClick = { /* Do something */ }) {
            Icon(
                Icons.Filled.Share,
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = stringResource(R.string.content_description_share)
            )
        }
    }
}
