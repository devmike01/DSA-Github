package dev.gbenga.dsagithub.base

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultScaffold(topBarTitle: String,
                    snackbarHostState: SnackbarHostState? =null,
                    showLoading: Boolean =false,
                    content: @Composable () -> Unit){
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(topBarTitle)
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

