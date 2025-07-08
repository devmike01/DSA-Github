package dev.gbenga.dsagithub.base

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.gbenga.dsa.collections.list.LinkedList
import dev.gbenga.dsa.collections.list.toArray
import dev.gbenga.dsagithub.ui.theme.Orange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultScaffold(topBarTitle: String,
                    snackbarHostState: SnackbarHostState? =null,
                    showLoading: Boolean =false,
                    actions: LinkedList<MenuItem>? = null,
                    onClickMenuItem: ((MenuId) -> Unit)? = null,
                    content: @Composable () -> Unit){
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(topBarTitle)
            }, //containerColor = Color.Blue
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Orange
                ),
                modifier = Modifier,
                actions = {
                actions?.toArray()?.let { menus ->
                    for ( menu in menus){
                        menu?.let {
                            IconButton(onClick = {
                                onClickMenuItem?.invoke(menu.id)
                            }) {
                                Icon(painter = painterResource(menu.icon.useIcon()),
                                    tint = Color.White ,
                                    contentDescription = null,
                                    modifier = Modifier.size(50.dp))
                            }
                        } ?: break

                    }
                }

            })
        },
        snackbarHost = {
            snackbarHostState?.let {
                SnackbarHost(hostState = snackbarHostState)
            }
        }

    ) {
        Box(modifier = Modifier.padding(it)) {
            content()
            if(showLoading){
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

