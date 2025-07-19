package dev.gbenga.dsagithub.features.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.gbenga.dsa.collections.list.LinkedList
import dev.gbenga.dsa.collections.list.LinkedListImpl
import dev.gbenga.dsagithub.base.DefaultScaffold
import dev.gbenga.dsagithub.base.Dimens
import dev.gbenga.dsagithub.base.FontSize
import dev.gbenga.dsagithub.base.UiState
import dev.gbenga.dsagithub.base.initial
import dev.gbenga.dsagithub.features.home.data.User
import dev.gbenga.dsagithub.nav.GithubDetails
import dev.gbenga.dsagithub.nav.choir.Choir
import dev.gbenga.dsagithub.ui.theme.Orange
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: Choir, homeViewModel: HomeViewModel = koinViewModel()){
    val homeUiState by homeViewModel.homeUiState.collectAsStateWithLifecycle()
    val menuItems by homeViewModel.menus.collectAsStateWithLifecycle()
    var usersState by remember { mutableStateOf<LinkedList<User>>(LinkedListImpl<User>()) }


    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var showLoading by remember { mutableStateOf(false) }

    DefaultScaffold(topBarTitle = "Home",
        showLoading = showLoading,
        actions = menuItems, onClickMenuItem = {
            homeViewModel.setOnMenuClick(it)
        }) {
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

        LaunchedEffect( Unit) {
            homeViewModel.loadMenus()
        }
       UserListView(usersState){ user ->
         navController.navigate(GithubDetails(user))
       }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListView(users: LinkedList<User>, onUserClick: (User) -> Unit){
    val pagerState = rememberPagerState(pageCount = { 2 })
    var selectedPage by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    Column {
        PrimaryTabRow(selectedTabIndex = pagerState.currentPage,
            modifier = Modifier.padding(PaddingValues(Dimens.smallPadding.dp))) {
            arrayOf("Users", "Favourite Users").forEachIndexed {index, title ->
                Tab(
                    selected = pagerState.currentPage == selectedPage,
                    onClick = {
                        Log.d("PrimaryTab", "index $index")
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = {
                        Text(
                            text = title,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
            }
        }
        HorizontalPager(pagerState) {
            selectedPage = it
            when(it){
                0 -> {
                    HomeUserListView(users, onUserClick)
                }
                1 -> {
                    FavoriteScreen()
                }
            }
        }
    }
}

@Composable
fun FavoriteScreen(){
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Text("Favorite")
        }
        item {
            Text("Favorite")
        }
    }
}

@Composable
fun HomeUserListView(users: LinkedList<User>, onUserClick: (User) -> Unit){
    val scrollable = rememberLazyListState()
    LazyColumn(state = scrollable) {
        users.forEach { user ->
            item {
                Row(
                    modifier = Modifier.clickable {
                        // clicked
                        onUserClick(user)
                    }.fillParentMaxWidth().padding(vertical = Dimens.mediumPadding.dp,
                        horizontal = Dimens.normalPadding.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimens.mediumPadding.dp)
                ) {
                    val shape = CircleShape
                    Box(modifier = Modifier
                        .size(Dimens.avatarSize.dp)
                        .clip(shape)
                        .background(Orange)
                        .border(width = 2.dp, color = Color.LightGray,
                            shape = shape)
                        .padding(Dimens.smallPadding.dp)) {
                        Text(user.login.initial(),
                            modifier = Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography
                                .bodyLarge.copy(fontSize = FontSize.xLarge.sp))
                    }
                    Text(user.login)
                }
                HorizontalDivider()
            }
        }
    }
}