package dev.gbenga.dsagithub.features.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import dev.gbenga.dsa.collections.list.LinkedList
import dev.gbenga.dsagithub.MainActivity
import dev.gbenga.dsagithub.R
import dev.gbenga.dsagithub.base.DefaultScaffold
import dev.gbenga.dsagithub.base.Dimens
import dev.gbenga.dsagithub.base.UiState
import dev.gbenga.dsagithub.base.titleCase
import dev.gbenga.dsagithub.nav.choir.Choir
import dev.gbenga.dsagithub.ui.theme.PurpleGrey40
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(navController: Choir,
                 userName: String?,
                 avatarUrl: String?,
                 isFavourite: Boolean?,
                 detailViewModel: DetailViewModel = koinViewModel()){

    val detailsState by detailViewModel.details.collectAsStateWithLifecycle()
    var userRepos = remember { derivedStateOf { detailsState.userRepos } }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var refresh by rememberSaveable { mutableStateOf(false) }
    val activity = LocalContext.current.let { it as MainActivity }

    LaunchedEffect(refresh) {
        if (refresh)return@LaunchedEffect
        detailViewModel.message.collect { status ->
            if (status.message.isNotEmpty()){
                scope.launch {
                    snackbarHostState.showSnackbar(status.message)
                }
            }else if(status.action == MessengerAction.CLOSE_SCREEN){
                navController.popBackStack()
            }
        }
        refresh = true
    }

    LaunchedEffect(Unit) {
        detailViewModel.populateDetailsTabContent(userName)
    }

    DefaultScaffold(
        modifier = Modifier.fillMaxSize(),
        navigationIcon = {
            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Go back")
            }
        },
        snackbarHostState = snackbarHostState,
        topBarTitle = userName?.titleCase() ?: "",
        floatingActionButton = {
            if(isFavourite == true){
                FloatingActionButton(onClick = {
                    avatarUrl?.let {
                        detailViewModel.unFavoriteUser(userName)
                    }
                }) {
                    Icon(Icons.Default.Close,
                        contentDescription = "Favorite user")
                }
            }else{
                FloatingActionButton(onClick = {
                    avatarUrl?.let {
                        detailViewModel.favoriteUser(userName, avatarUrl)
                    }
                }) {
                    Icon(Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite user")
                }
            }

        }
    ) {
        DetailContent(avatarUrl, userName, userRepos)
    }
}

@Composable
fun DetailContent(avatarUrl: String?, userName: String?,
                  userRepos: State<UiState<LinkedList<UserRepositories>>> ){
    var loadingImage by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    ConstraintLayout (modifier = Modifier.fillMaxSize()
        .verticalScroll(scrollState)) {
        val (imageBox, listColumn) = createRefs()
        Box(modifier = Modifier.constrainAs(imageBox) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }.fillMaxWidth().height(300.dp)) {

            // add image
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = avatarUrl,
                contentScale = ContentScale.FillBounds,
                contentDescription = null,
                onState = {
                    loadingImage = it is AsyncImagePainter.State.Loading
                }
            )

            ProgressIndicator(loadingImage)
        }

        Column(modifier = Modifier.constrainAs(listColumn) {
            top.linkTo(imageBox.bottom)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }) {

            Text("${userName?.titleCase()}'s Repositories",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(Dimens.mediumPadding.dp))
            when(val repos = userRepos.value){
                is UiState.Success ->{
                    if (repos.data.isEmpty()){
                        Box(modifier = Modifier.fillMaxSize()) {
                            Text("User's repository is empty")
                        }
                        return@Column
                    }
                    val userRepos = arrayOfNulls<UserRepositories?>(repos.data.size())
                    var index = 0
                    repos.data.forEach {
                        userRepos[index] = it
                        index++
                    }

                    var initialPage by rememberSaveable { mutableIntStateOf(0) }

                    val pagerState = rememberPagerState(pageCount = {
                        userRepos.size
                    }, initialPage = initialPage)

                    HorizontalPager(state = pagerState,
                        contentPadding = PaddingValues(end = Dimens.largePadding.dp),) { page ->
                        initialPage = page
                        RepositoryCard(userRepos[page],
                            modifier = Modifier)
                    }
                }
                is UiState.Loading ->{
                    CircularProgressIndicator()
                }
                else ->{
                    // Nothing
                }
            }
            // userRepos
            // NavigationTabRow()

        }
    }
}

@Composable
fun ProgressIndicator(isLoading: Boolean){
    if (isLoading) CircularProgressIndicator()
}

@Composable
fun RepositoryCard(userRepos: UserRepositories?, modifier: Modifier){
    userRepos?.let {
        ConstraintLayout(
            modifier = modifier
                .fillMaxWidth()
                .padding(Dimens.smallPadding.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(PurpleGrey40).fillMaxWidth()
            .padding(horizontal = Dimens.mediumPadding.dp)
            .height(150.dp)) {
            val (image, repoContent) = createRefs()
            Image(painter = painterResource(R.drawable.repo_img),
                contentDescription = "repos_image",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.constrainAs(image) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                }.size(Dimens.repoIconSize.dp)
            )
            Column(modifier = Modifier.constrainAs(repoContent) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(image.end)
            }.padding(horizontal = Dimens.mediumPadding.dp),
                verticalArrangement = Arrangement.spacedBy(Dimens.normalPadding.dp)) {
                Text(userRepos.full_name.titleCase(),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier)

                Text(userRepos.description ?: "N/A",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier)


                Text("${userRepos.forks} Forks " +
                        //"· ${if(userRepos.fork) "Forked" else "Original Creator"} " +
                        "· ${userRepos.language}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier)

            }
        }
    }

}

enum class UserDestination{
    Repositories,
}