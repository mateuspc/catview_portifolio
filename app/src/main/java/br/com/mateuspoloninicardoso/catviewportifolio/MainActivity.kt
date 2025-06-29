package br.com.mateuspoloninicardoso.catviewportifolio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.mateuspoloninicardoso.catviewportifolio.di.provideHttpClient
import br.com.mateuspoloninicardoso.catviewportifolio.presentation.CatScreen
import br.com.mateuspoloninicardoso.catviewportifolio.presentation.CatViewModel
import br.com.mateuspoloninicardoso.catviewportifolio.ui.theme.CatViewPortifolioTheme
import io.ktor.client.HttpClient


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val client = provideHttpClient()

        val factory = CatViewModelFactory(client)

        val viewModel: CatViewModel by viewModels { factory }

        setContent {
            CatViewPortifolioTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        CatScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }

}

class CatViewModelFactory(private val client: HttpClient) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CatViewModel(client) as T
    }
}