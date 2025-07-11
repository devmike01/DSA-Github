package dev.gbenga.dsagithub.features.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import org.koin.androidx.compose.koinViewModel
import androidx.compose.material3.Tab

@Composable
fun DetailScreen(navController: NavHostController,
                 userId: String, detailViewModel: DetailViewModel = koinViewModel()){
    Scaffold { paddingValues ->
        ConstraintLayout (modifier = Modifier.padding(paddingValues)) {
            val (imageBox, listColumn) = createRefs()
            Box(modifier = Modifier.constrainAs(imageBox) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }) {
                // add image
                AsyncImage(
                    modifier = Modifier.fillMaxWidth(),
                    model = "https://t3.ftcdn.net/jpg/01/73/37/16/360_F_173371622_02A2qGqjhsJ5SWVhUPu0t9O9ezlfvF8l.jpg",
                    contentDescription = null,
                )
            }

            Column(modifier = Modifier.constrainAs(listColumn) {
                top.linkTo(imageBox.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }) {
                // Display 10 items
                val pagerState = rememberPagerState(pageCount = {
                    10
                })
                NavigationTabRow()
                HorizontalPager(state = pagerState) { page ->
                    // Our page content
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = "Page: $page",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationTabRow(modifier: Modifier = Modifier, ) {
    val navController = rememberNavController()
    val startDestination = UserDestination.Repositories
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    PrimaryTabRow(selectedTabIndex = selectedDestination,
        modifier = Modifier.padding()) {
        UserDestination.entries.forEachIndexed { index, destination ->
            Tab(
                selected = selectedDestination == index,
                onClick = {
                    navController.navigate(route = destination.name)
                    selectedDestination = index
                },
                text = {
                    Text(
                        text = destination.name,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
    }
}

enum class UserDestination{
    Repositories,
}