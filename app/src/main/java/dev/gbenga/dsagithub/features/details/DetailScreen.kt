package dev.gbenga.dsagithub.features.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import org.koin.androidx.compose.koinViewModel

@Composable
fun DetailScreen(navController: NavHostController,
                 userId: String, detailViewModel: DetailViewModel = koinViewModel()){
    Scaffold { paddingValues ->
        ConstraintLayout (modifier = Modifier.padding(paddingValues)) {
            val (imageBox, listColumn) = createRefs()
            Box(modifier = Modifier.fillMaxWidth().constrainAs(imageBox) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }) {
                // add image
                AsyncImage(
                    model = "https://t3.ftcdn.net/jpg/01/73/37/16/360_F_173371622_02A2qGqjhsJ5SWVhUPu0t9O9ezlfvF8l.jpg",
                    contentDescription = null,
                )
            }

            Column {
                // Display 10 items
                val pagerState = rememberPagerState(pageCount = {
                    10
                })
                HorizontalPager(state = pagerState) { page ->
                    // Our page content
                    Text(
                        text = "Page: $page",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}