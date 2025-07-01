package br.com.mateuspoloninicardoso.catviewportifolio.presentation

import CatViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.platform.testTag // Importar testTag

@Composable
fun CatScreen(viewModel: CatViewModel) {
    val estado by viewModel.estado.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .testTag("cat_screen_root"), // Adicionei um testTag para a tela inteira
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (estado) {
            is EstadoDaTela.Inicial -> {
                Text(
                    text = "Clique para buscar um gato 😺",
                    modifier = Modifier.testTag("status_text_inicial") // Test tag para estado Inicial
                )
            }
            is EstadoDaTela.Carregando -> {
                CircularProgressIndicator(
                    modifier = Modifier.testTag("loading_indicator") // Test tag para o indicador de carregamento
                )
                Text(
                    text = "Carregando...",
                    modifier = Modifier.testTag("status_text_carregando") // Test tag para estado Carregando
                )
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
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("cat_image"), // Test tag para a imagem
                        contentScale = ContentScale.Crop
                    )

                    when (painterState) {
                        is AsyncImagePainter.State.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.testTag("image_loading_indicator")
                            )
                        }
                        is AsyncImagePainter.State.Error -> {
                            Text(
                                text = "Erro ao carregar imagem 😿",
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.testTag("image_load_error_text")
                            )
                        }
                        else -> Unit
                    }
                }
                Text(
                    text = "Gato encontrado!", // Mensagem de sucesso
                    modifier = Modifier.testTag("status_text_sucesso") // Test tag para estado Sucesso
                )
            }
            is EstadoDaTela.Erro -> {
                Text(
                    text = "Erro: ${(estado as EstadoDaTela.Erro).mensagem}",
                    modifier = Modifier.testTag("status_text_erro") // Test tag para estado Erro
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { viewModel.buscarImagemDeGato() },
            modifier = Modifier.testTag("buscar_imagem_button") // Test tag para o botão
        ) {
            Text("Buscar Imagem de Gato")
        }
    }
}