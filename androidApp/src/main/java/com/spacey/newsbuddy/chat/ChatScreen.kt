package com.spacey.newsbuddy.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spacey.newsbuddy.AppBarContent
import com.spacey.newsbuddy.FabConfig
import com.spacey.newsbuddy.ListedUiState

// Create an app design with modern and material colors to it. Choose a proper color and minimalistic look for it. The app is a chat bot, where you have different chats on different topics and has a date label to it. So every day a new chat will be created. I want two screens, one which lists all the chats, and one with the actual chat window. Be creative in making the design

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    setAppBarContent: (AppBarContent?) -> Unit,
    setFabConfig: (FabConfig?) -> Unit,
    navBackToHome: () -> Unit,
    viewModel: ChatViewModel = viewModel()
) {
    LaunchedEffect(key1 = true) {
        viewModel.startChat()
        setAppBarContent(null)
        setFabConfig(null)
    }

    val conversations by viewModel.conversation.collectAsState()
    Column {
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
                    colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.tertiary, contentColor = MaterialTheme.colorScheme.onTertiary),
                    modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp).size(40.dp)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "back")
                }
            })
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
                            val alignment = if (convo.isUser) Alignment.End else Alignment.Start
                            val cardContainerColor = if (convo.isUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.tertiary
                            val cardContentColor = if (convo.isUser) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onTertiary
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(alignment)
                            ) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = cardContainerColor, contentColor = cardContentColor),
                                    modifier = Modifier
                                        .fillMaxWidth(fraction = .9f)
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    if (convo.isLoading) {
                                        CircularProgressIndicator()
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
                            disabledContainerColor = MaterialTheme.colorScheme.tertiary,
                            focusedContainerColor = MaterialTheme.colorScheme.tertiary,
                            errorContainerColor = MaterialTheme.colorScheme.tertiary,
                            unfocusedContainerColor = MaterialTheme.colorScheme.tertiary,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                        ),
                        trailingIcon = {
                            IconButton(onClick = ::onInputDone, colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                                Icon(Icons.Default.Send, "Send chat")
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            onInputDone()
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
}