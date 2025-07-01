package br.com.mateuspoloninicardoso.catviewportifolio

import CatViewModel
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
import br.com.mateuspoloninicardoso.catviewportifolio.ui.theme.CatViewPortifolioTheme
import br.com.mateuspoloninicardoso.catviewportifolio.domain.model.remote.CatRepositoryImpl
import br.com.mateuspoloninicardoso.catviewportifolio.domain.model.repository.CatRepository

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val client = provideHttpClient()
        val repository: CatRepository = CatRepositoryImpl(client)

        val viewModel = CatViewModel(repository)

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


