package dev.gbenga.dsagithub.features.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.constraintlayout.compose.ConstraintLayout
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import dev.gbenga.dsagithub.R
import dev.gbenga.dsagithub.base.Dimens
import dev.gbenga.dsagithub.base.UiState
import dev.gbenga.dsagithub.base.titleCase
import dev.gbenga.dsagithub.features.home.data.User
import dev.gbenga.dsagithub.nav.choir.Choir
import dev.gbenga.dsagithub.ui.theme.PurpleGrey40
import org.koin.androidx.compose.koinViewModel
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(navController: Choir,
                 user: User?,
                 detailViewModel: DetailViewModel = koinViewModel()){
    if (user == null){
        navController.popBackStack()
        return
    }

    val detailsState by detailViewModel.details.collectAsState()
    var userRepos = remember { derivedStateOf { detailsState.userRepos } }

    LaunchedEffect(Unit) {
        detailViewModel.populateDetailsTabContent(user.login)
    }
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(user.login.titleCase())
            },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = null)
                    }
                },
                )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                detailViewModel.favoriteUser()
            }) {
                Icon(Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite user")
            }
        }
    ) { paddingValues ->
        var loadingImage by remember { mutableStateOf(false) }
        val scrollState = rememberScrollState()

        ConstraintLayout (modifier = Modifier.padding(paddingValues)
            .scrollable(scrollState, orientation = Orientation.Vertical)) {
            val (imageBox, listColumn) = createRefs()
            Box(modifier = Modifier.constrainAs(imageBox) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }) {

                // add image
                AsyncImage(
                    modifier = Modifier.fillMaxWidth(),
                    model = user.avatarUrl,
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

                Text("${user.login.titleCase()}'s Repositories",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(Dimens.mediumPadding.dp))
                when(val repos = userRepos.value){
                    is UiState.Success ->{
                        val userRepos = arrayOfNulls<UserRepositories?>(repos.data.size())
                        var index = 0
                        repos.data.forEach {
                            userRepos[index] = it
                            index++
                        }
                        val pagerState = rememberPagerState(pageCount = {
                            userRepos.size
                        })

                        HorizontalPager(state = pagerState) { page ->
                            val pageOffSet = pagerState.currentPage - page + pagerState.currentPageOffsetFraction

                            RepositoryCard(userRepos[page],
                                modifier = Modifier.graphicsLayer{

                                    alpha = lerp(
                                        start = 0.7f,
                                        stop = 1f,
                                        fraction = 1f - pageOffSet.absoluteValue.coerceIn(0f, 1f),
                                    )

                                    cameraDistance = 8 * density
                                    rotationY = lerp(
                                        start = 0f,
                                        stop = 0f,
                                        fraction = pageOffSet.coerceIn(-1f, 1f),
                                    )

                                    lerp(
                                        start = 0.8f,
                                        stop = 1f,
                                        fraction = 1f - pageOffSet.absoluteValue.coerceIn(0f, 1f),
                                    ).also { scale ->
                                        scaleX = scale
                                        scaleY = scale
                                    }
                                })
                        }
                    }
                    is UiState.Error ->{
                        // error
                    }
                    is UiState.Loading ->{
                        CircularProgressIndicator()
                    }
                }
                // userRepos
               // NavigationTabRow()

            }
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
                .padding(Dimens.mediumPadding.dp)
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
                    .drawBehind{
                        drawRoundRect(color = PurpleGrey40,
                            cornerRadius = CornerRadius(
                                x = Dimens.smallPadding.toFloat(),
                                y = Dimens.smallPadding.toFloat()),
                            size = Size(80f, 80f))
                    })
            Column(modifier = Modifier.constrainAs(repoContent) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(image.end)
            }.padding(horizontal = Dimens.mediumPadding.dp)) {
                Text(userRepos.full_name.titleCase(),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier)
                Text(userRepos.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier)

                Text("${userRepos.forks} Forks " +
                        "· ${if(userRepos.fork) "Forked" else "Original Creator"} " +
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