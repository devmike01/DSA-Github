package dev.gbenga.dsagithub.features.home

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.gbenga.dsa.collections.list.LinkedList
import dev.gbenga.dsagithub.base.DefaultScaffold
import dev.gbenga.dsagithub.base.Dimens
import dev.gbenga.dsagithub.base.UiState
import dev.gbenga.dsagithub.base.initial
import dev.gbenga.dsagithub.features.home.data.User
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(homeViewModel: HomeViewModel = koinViewModel()){
    val homeUiState by homeViewModel.homeUiState.collectAsStateWithLifecycle()
    var usersState by remember { mutableStateOf(LinkedList<User>()) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var showLoading by remember { mutableStateOf(false) }

    DefaultScaffold(topBarTitle = "Home",
        showLoading = showLoading) {
        LaunchedEffect(homeUiState.users) {
            when(val users = homeUiState.users){
                is UiState.Success ->{
                    showLoading = false
                    usersState = users.data
                }
                is UiState.Error ->{
                    showLoading = false
                    scope.launch {
                        snackbarHostState.showSnackbar(users.errorMsg)
                    }
                }
                is UiState.Loading ->{
                    showLoading = true
                }
            }
        }
       UserListView(usersState)
    }
}

@Composable
fun UserListView(users: LinkedList<User>){
    val scrollable = rememberLazyListState()
    LazyColumn(state = scrollable) {
        users.forEach { user ->
            Log.d("UserListView", "$user")
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimens.mediumPadding.dp)
                ) {
                    Box(modifier = Modifier.size(Dimens.mediumPadding.dp)) {
                        Text(user.login.initial(), style = MaterialTheme.typography.bodyLarge)
                    }
                    Text(user.login)
                }
            }
        }
    }
}