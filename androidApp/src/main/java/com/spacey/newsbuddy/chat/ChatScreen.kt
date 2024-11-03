package com.spacey.newsbuddy.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spacey.newsbuddy.AppBarContent
import com.spacey.newsbuddy.FabConfig
import com.spacey.newsbuddy.genai.ChatType

@OptIn(ExperimentalMaterial3Api::class)
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
    Scaffold(containerColor = Color.Transparent, topBar = {
        CenterAlignedTopAppBar(colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            title = {
                Text(
                    "News Buddy",
                    Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )
            }, navigationIcon = {
                IconButton(
                    onClick = navBackToHome,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary
                    ),
                    modifier = Modifier
                        .padding(vertical = 16.dp, horizontal = 8.dp)
                        .size(40.dp)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "back")
                }
            }
        )
    }, content = { padding ->
        Column(Modifier.padding(padding)) {
            when (val chat = conversations) {
                is ChatUiState.Error -> {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text(text = chat.message, color = MaterialTheme.colorScheme.error)
                    }
                }

                is ChatUiState.Loading -> {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator()
                    }
                }

                is ChatUiState.Success -> {
                    var chatInput by remember {
                        mutableStateOf("")
                    }
                    Column(Modifier.fillMaxSize()) {
                        LazyColumn(Modifier.weight(1f)) {
                            items(items = chat.chatWindow.chats) { convo ->
                                val isUserChat = convo.type == ChatType.USER
                                val alignment =
                                    if (isUserChat) Arrangement.End else Arrangement.Start
                                val cardContainerColor =
                                    if (isUserChat) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.tertiary
                                val cardContentColor =
                                    if (isUserChat) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onTertiary
                                Row(
                                    horizontalArrangement = alignment,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = cardContainerColor,
                                            contentColor = cardContentColor
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth(fraction = .9f)
                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                        shape = RoundedCornerShape(20.dp)
                                    ) {
                                        // TODO: Loading chat bubble
//                                    if (convo.isLoading) {
//                                        CircularProgressIndicator()
//                                    } else {
                                        Text(
                                            text = convo.chatText,
                                            modifier = Modifier.padding(16.dp)
                                        )
//                                    }
                                    }
                                }
                            }
                        }
                        fun onInputDone() {
                            viewModel.chat(chatInput)
                            chatInput = ""
                        }
//                    val (focus) = remember {
//                        FocusRequester.createRefs()
//                    }
                        TextField(value = chatInput,
                            shape = RoundedCornerShape(30.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
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
                                AnimatedVisibility(chatInput.isNotEmpty()) {
                                    IconButton(
                                        onClick = ::onInputDone,
                                        colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                                    ) {
                                        Icon(Icons.Default.Send, "Send chat")
                                    }
                                }
                            },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
//                            focus.requestFocus()
                                onInputDone()
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
    })
}