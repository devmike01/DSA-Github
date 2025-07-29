package dev.gbenga.dsagithub.base

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import dev.gbenga.dsa.collections.list.LinkedList
import dev.gbenga.dsa.collections.list.Node
import dev.gbenga.dsa.collections.list.toArray
import dev.gbenga.dsagithub.ui.theme.Orange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultScaffold(
    modifier: Modifier = Modifier,
    topBarTitle: String,
    navigationIcon: @Composable () -> Unit = {},
                    snackbarHostState: SnackbarHostState? =null,
                    showLoading: Boolean =false,
                    floatingActionButton: @Composable () -> Unit = {},
                    actions: LinkedList<MenuItem>? = null,
                    onClickMenuItem: ((MenuId) -> Unit)? = null,
                    content: @Composable () -> Unit){
    Scaffold(
        floatingActionButton =floatingActionButton,

        topBar = {
            val textFieldState by rememberSaveable { mutableStateOf(TextFieldState()) }
            TopAppBar(title = {
                ConstraintLayout {
                    val (search, title) = createRefs()
                    Text(topBarTitle,
                        modifier= Modifier.constrainAs(search){
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        bottom.linkTo(parent.bottom)
                    })
                    SimpleSearchBar(
                        modifier= Modifier.constrainAs(search){
                            top.linkTo(parent.top)
                            start.linkTo(title.end)
                            bottom.linkTo(parent.bottom)
                        },
                        textFieldState = textFieldState
                    ){

                    }
                }
            }, //containerColor = Color.Blue
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Orange
                ),
                modifier = Modifier,
                actions = {
                    var menuNode : Node<MenuItem>? = actions?.peekHeadNode()
                    while (menuNode != null){
                        val menu = menuNode.data
                        IconButton(onClick = {
                            Log.d("TopAppBar", "${menu.id}")
                            onClickMenuItem?.invoke(menu.id)
                        }) {
                            Icon(painter = painterResource(menu.icon.useIcon()),
                                tint = Color.White ,
                                contentDescription = null,
                                modifier = Modifier.size(30.dp))
                        }
                        menuNode = menuNode.next
                    }
            },
                navigationIcon = navigationIcon
                )
        },
        snackbarHost = {
            snackbarHostState?.let {
                SnackbarHost(hostState = snackbarHostState)
            }
        }

    ) {
        Box(modifier = modifier.padding(it)) {
            content()
            if(showLoading){
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleSearchBar(
    modifier: Modifier = Modifier,
    textFieldState: TextFieldState,
    onSearch: (String) -> Unit,
) {
    // Controls expansion state of the search bar
    var expanded by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier
            .fillMaxSize()
            .semantics { isTraversalGroup = true }
    ) {
        SearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .semantics { traversalIndex = 0f },
            inputField = {
                SearchBarDefaults.InputField(
                    query = textFieldState.text.toString(),
                    onQueryChange = { textFieldState.edit { replace(0, length, it) } },
                    onSearch = {
                        onSearch(textFieldState.text.toString())
                        expanded = false
                    },
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    placeholder = { Text("Search") }
                )
            },
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {

        }
    }
}