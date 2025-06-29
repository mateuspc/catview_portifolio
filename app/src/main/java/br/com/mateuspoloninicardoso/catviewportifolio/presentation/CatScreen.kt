package br.com.mateuspoloninicardoso.catviewportifolio.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter

@Composable
fun CatScreen(viewModel: CatViewModel) {
    val estado by viewModel.estado.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (estado) {
            is EstadoDaTela.Inicial -> {
                Text("Clique para buscar um gato 😺")
            }
            is EstadoDaTela.Carregando -> {
                CircularProgressIndicator()
                Text("Carregando...")
            }
            is EstadoDaTela.Sucesso -> {
                val url = (estado as EstadoDaTela.Sucesso).imagemUrl
                val painter = rememberAsyncImagePainter(url)
                val painterState = painter.state

                Box(
                    modifier = Modifier.size(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painter,
                        contentDescription = "Imagem de gato",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    when (painterState) {
                        is AsyncImagePainter.State.Loading -> {
                            CircularProgressIndicator()
                        }
                        is AsyncImagePainter.State.Error -> {
                            Text("Erro ao carregar imagem 😿", color = MaterialTheme.colorScheme.error)
                        }
                        else -> Unit
                    }
                }

            }
            is EstadoDaTela.Erro -> {
                Text("Erro: ${(estado as EstadoDaTela.Erro).mensagem}")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = { viewModel.buscarImagemDeGato() }) {
            Text("Buscar Imagem de Gato")
        }
    }
}