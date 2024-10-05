package com.spacey.newsbuddy.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spacey.newsbuddy.AppBarContent
import com.spacey.newsbuddy.FabConfig
import com.spacey.newsbuddy.ListedUiState

@Composable
fun ChatScreen(setAppBarContent: (AppBarContent) -> Unit, setFabConfig: (FabConfig) -> Unit, viewModel: ChatViewModel = viewModel()) {
    LaunchedEffect(key1 = true) {
        viewModel.startChat()
    }
    setAppBarContent(AppBarContent {
        Text(text = "News talk!")
    })
    setFabConfig(FabConfig(false) {})

    val conversations by viewModel.conversation.collectAsState()

    when (val chat = conversations) {
        is ListedUiState.Error -> {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text(text = chat.message, color = MaterialTheme.colorScheme.error)
            }
        }

        is ListedUiState.Loading -> {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator()
            }
        }

        is ListedUiState.Success -> {
            var chatInput by remember {
                mutableStateOf("")
            }
            Column(Modifier.fillMaxSize()) {
                LazyColumn(Modifier.weight(1f)) {
                    items(items = chat.conversations) { convo ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(if (convo.isUser) Alignment.End else Alignment.Start)
                        ) {
                            Card(
                                colors = CardDefaults.cardColors(if (convo.isUser) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant),
                                modifier = Modifier
                                    .fillMaxWidth(fraction = .9f)
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                if (convo.isLoading) {
                                    CircularProgressIndicator(
                                        Modifier
                                            .fillMaxWidth()
                                            .align(Alignment.CenterHorizontally))
                                } else {
                                    Text(text = convo.text, modifier = Modifier.padding(16.dp))
                                }
                            }
                        }
                    }
                }
                fun onInputDone() {
                    viewModel.chat(chatInput)
                    chatInput = ""
                }
                val (focus) = FocusRequester.createRefs()
                TextField(value = chatInput,
                    shape = RoundedCornerShape(30.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                    ),
                    trailingIcon = {
                        IconButton(onClick = ::onInputDone) {
                            Icon(Icons.Default.Send, "Send chat")
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { onInputDone()
                        focus.requestFocus()
                    }),
                    singleLine = true,
                    onValueChange = {
                        chatInput = it
                    },
                    placeholder = {
                        Text(text = "Start typing...")
                    })
            }
        }
    }
}