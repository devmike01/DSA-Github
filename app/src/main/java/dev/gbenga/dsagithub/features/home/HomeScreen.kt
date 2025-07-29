package dev.gbenga.dsagithub.features.home

import android.util.Log
import androidx.activity.compose.LocalActivity
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
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.gbenga.dsa.collections.list.LinkedList
import dev.gbenga.dsa.collections.list.LinkedListImpl
import dev.gbenga.dsagithub.MainActivity
import dev.gbenga.dsagithub.base.DefaultScaffold
import dev.gbenga.dsagithub.base.Dimens
import dev.gbenga.dsagithub.base.FontSize
import dev.gbenga.dsagithub.base.UiState
import dev.gbenga.dsagithub.base.initial
import dev.gbenga.dsagithub.base.titleCase
import dev.gbenga.dsagithub.data.database.Favourite
import dev.gbenga.dsagithub.features.home.data.User
import dev.gbenga.dsagithub.nav.GithubDetails
import dev.gbenga.dsagithub.nav.choir.Choir
import dev.gbenga.dsagithub.ui.theme.Orange
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: Choir, homeViewModel: HomeViewModel = koinViewModel()){

    val menuItems by homeViewModel.menus.collectAsStateWithLifecycle()
    var showLoading by remember { mutableStateOf(false) }
    val activity = LocalActivity.current as MainActivity
    val snackbarHostState = remember { SnackbarHostState() }


    DefaultScaffold(topBarTitle = "Home",
        snackbarHostState = snackbarHostState,
        showLoading = showLoading,
        actions = menuItems, onClickMenuItem = {
            homeViewModel.setOnMenuClick(it)
        }) {

        val homeUiState by homeViewModel.homeUiState.collectAsStateWithLifecycle()
        val favUsersState by homeViewModel.favUserUiState.collectAsStateWithLifecycle()
        var usersState by remember { mutableStateOf<LinkedList<User>>(LinkedListImpl<User>()) }

        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }

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
                else -> {
                    // Nothing
                }
            }
        }

        LaunchedEffect(Unit) {
            homeViewModel.loadMenus()
            homeViewModel.loadGithubUsers()
            homeViewModel.loadFavourite()
        }
       HomeContent(usersState, favUsersState.favUsers, onLoadMore = {
           homeViewModel.loadMoreGithubUsers()
       }){ isFavourite,  userName, avatarUrl ->
         navController.navigate(GithubDetails(userName, avatarUrl,isFavourite))
       }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(users: LinkedList<User>, favoriteUsers: LinkedList<Favourite>,
                onLoadMore: () -> Unit,
                onUserClick: (Boolean, String, String) -> Unit){

    val pagerState = rememberPagerState(pageCount = { 2 })
    var selectedPage by rememberSaveable { mutableIntStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    Column {
        PrimaryTabRow(selectedTabIndex = pagerState.currentPage,
            modifier = Modifier.padding(PaddingValues(Dimens.smallPadding.dp))) {
            arrayOf("Users", "Favourite Users").forEachIndexed {index, title ->
                Tab(
                    selected = pagerState.currentPage == selectedPage,
                    onClick = {
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
                    HomeUserListView(users, onUserClick, onLoadMore)
                }
                1 -> {
                    FavoriteScreen(favoriteUsers, onUserClick)
                }
            }
        }
    }
}

@Composable
fun FavoriteScreen(favoriteUsers: LinkedList<Favourite>, onUserClick: (Boolean, String, String) -> Unit){
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        favoriteUsers.forEach { favUser ->
            item {
                HomeItem(favUser.userName, onUserClick = {
                    onUserClick(true, favUser.userName, favUser.avatarUrl)
                })
            }
        }
    }
}


private const val buffer = 1

@Composable
fun HomeUserListView(users: LinkedList<User>,
                     onUserClick: (Boolean, String, String) -> Unit,
                     onLoadMore: () -> Unit){
    val listState = rememberLazyListState()

    // observe list scrolling
    val reachedBottom: Boolean by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem?.index != 0 && lastVisibleItem?.index == listState.layoutInfo.totalItemsCount - buffer
        }
    }

    LaunchedEffect(reachedBottom) {
        if(reachedBottom){ // https://api.github.com/users?per_page=2&since=2
            // Load more
            onLoadMore()
        }
    }

    LazyColumn(state = listState) {
        users.forEach { user ->
            item {
                HomeItem(user.login, onUserClick ={
                    onUserClick(false, user.login, user.avatarUrl)
                })
            }
        }
    }
}

@Composable
private fun LazyItemScope.HomeItem( login: String, onUserClick: ( String) -> Unit){
    Row(
        modifier = Modifier.clickable {
            // clicked
            onUserClick(login)
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
            Text(login.initial(),
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography
                    .bodyLarge.copy(fontSize = FontSize.xLarge.sp))
        }
        Text(login.titleCase())
    }
    HorizontalDivider()
}