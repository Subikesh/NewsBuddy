package com.spacey.newsbuddy.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spacey.newsbuddy.genai.ChatType
import com.spacey.newsbuddy.ui.BackIconButton
import com.spacey.newsbuddy.ui.CenteredTopBarScaffold
import com.spacey.newsbuddy.ui.ContentCard
import com.spacey.newsbuddy.ui.LoadingScreen
import com.spacey.newsbuddy.ui.MessageScreen
import com.spacey.newsbuddy.ui.keyboardVisibility

@Composable
fun ChatScreen(
    date: String,
//    setAppBarContent: (AppBarContent?) -> Unit,
//    setFabConfig: (FabConfig?) -> Unit,
    navBackToHome: () -> Unit,
    viewModel: ChatViewModel = viewModel()
) {
    LaunchedEffect(key1 = true) {
        viewModel.startChat(date)
//        setAppBarContent(null)
//        setFabConfig(null)
    }

    val conversations by viewModel.conversation.collectAsState()
    CenteredTopBarScaffold(title = "News Buddy", navigationIcon = {
        BackIconButton(navBackToHome)
    }) {
        Column {
            when (val chat = conversations) {
                is ChatUiState.Error -> {
                    MessageScreen(text = chat.message, contentColor = MaterialTheme.colorScheme.error)
                }

                is ChatUiState.Loading -> {
                    LoadingScreen()
                }

                is ChatUiState.Success -> {
                    var chatInput by remember {
                        mutableStateOf("")
                    }
                    // TODO: Scroll when keyboard opens is not working fine
                    val lazyColumnState = rememberLazyListState()
                    val keyboardState by keyboardVisibility()
                    LaunchedEffect(chat.chatWindow.chats.size, key2 = keyboardState) {
                        lazyColumnState.animateScrollToItem(chat.chatWindow.chats.size)
                    }
                    Column(Modifier.fillMaxSize()) {
                        LazyColumn(Modifier.weight(1f), state = lazyColumnState) {
                            items(items = chat.chatWindow.chats) { convo ->
                                val isUserChat = convo.type == ChatType.USER
                                val alignment =
                                    if (isUserChat) Arrangement.End else Arrangement.Start
                                val cardContainerColor =
                                    if (isUserChat) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.tertiary
                                val cardContentColor =
                                    if (isUserChat) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onTertiary
                                val inclinedDir = if (isUserChat) 'r' else 'l'
                                Row(
                                    horizontalArrangement = alignment,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    ContentCard(
                                        modifier = Modifier
                                            .widthIn(min = 0.dp, max = 330.dp)
                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                        cardContainerColor,
                                        cardContentColor,
                                        inclinedTo = inclinedDir
                                    ) {
                                        Text(
                                            text = convo.chatText,
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    }
                                }
                            }
                        }

                        fun onInputDone() {
                            viewModel.chat(chatInput)
                            chatInput = ""
                        }
                        TextField(
                            value = chatInput,
                            shape = RoundedCornerShape(30.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp, bottom = 8.dp),
                            colors = TextFieldDefaults.colors(
                                disabledContainerColor = MaterialTheme.colorScheme.tertiary,
                                focusedContainerColor = MaterialTheme.colorScheme.tertiary,
                                errorContainerColor = MaterialTheme.colorScheme.tertiary,
                                unfocusedContainerColor = MaterialTheme.colorScheme.tertiary,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                            ),
                            trailingIcon = {
                                AnimatedVisibility(chat.isAiChatLoading) {
                                    IconButton(
                                        onClick = { viewModel.stopThinking() },
                                        colors = IconButtonDefaults.iconButtonColors(
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    ) {
                                        Icon(Icons.Default.Stop, "Stop loading")
                                    }
                                }
                                AnimatedVisibility(!chat.isAiChatLoading && chatInput.isNotEmpty()) {
                                    IconButton(
                                        onClick = ::onInputDone,
                                        colors = IconButtonDefaults.iconButtonColors(
                                            containerColor = MaterialTheme.colorScheme.primary,
                                            contentColor = MaterialTheme.colorScheme.onPrimary
                                        )
                                    ) {
                                        Icon(Icons.Default.Send, "Send chat")
                                    }
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(onDone = {
//                            focus.requestFocus()
                                onInputDone()
                            }),
                            singleLine = true,
                            onValueChange = {
                                chatInput = it
                            },
                            readOnly = chat.isAiChatLoading,
                            placeholder = {
                                if (chat.isAiChatLoading) {
                                    Text(text = "Typing...")
                                } else {
                                    Text(text = "Start typing...")
                                }
                            })
                    }
                }
            }
        }
    }
}