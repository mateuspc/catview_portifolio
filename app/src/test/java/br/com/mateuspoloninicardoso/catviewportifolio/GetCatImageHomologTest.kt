package br.com.mateuspoloninicardoso.catviewportifolio
import CatViewModel
import br.com.mateuspoloninicardoso.catviewportifolio.domain.model.remote.CatRepositoryImpl
import br.com.mateuspoloninicardoso.catviewportifolio.presentation.EstadoDaTela
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

class CatViewModelIntegrationHomologTest {

    private val json = Json { ignoreUnknownKeys = true }

    private fun createRealClient(): HttpClient {
        return HttpClient {
            install(ContentNegotiation) { json(json) }
        }
    }

    @Tag("homolog")
    @Test
    fun `QUANDO a API real retorna sucesso, DEVE emitir os estados Inicial, Carregando e Sucesso`() = runTest {
        val client = createRealClient()
        val repository = CatRepositoryImpl(client)
        val viewModel = CatViewModel(repository, externalScope = this)

        val estados = mutableListOf<EstadoDaTela>()
        val job = launch {
            viewModel.estado.take(3).toList(estados)
        }

        viewModel.buscarImagemDeGato()
        job.join() // This will wait for the flow to emit 3 items

        assertEquals(3, estados.size)
        assertEquals(EstadoDaTela.Inicial, estados[0])
        assertEquals(EstadoDaTela.Carregando, estados[1])
        assertTrue(estados[2] is EstadoDaTela.Sucesso)

        val sucesso = estados[2] as EstadoDaTela.Sucesso
        assertTrue(sucesso.imagemUrl.startsWith("https://cataas.com"))
    }

    @Tag("homolog")
    @Test
    fun `QUANDO a API real retorna erro, DEVE emitir os estados Inicial, Carregando e Erro`() = runTest {
        val client = HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        val repository = CatRepositoryImpl(client, baseUrl = "https://cataas.com/invalid-endpoint") // força erro
        // Pass 'this' (TestScope) directly. No need for a separate internal scope to manage in test.
        val viewModel = CatViewModel(repository, externalScope = this)

        val estados = mutableListOf<EstadoDaTela>()
        val job = launch {
            viewModel.estado.take(3).toList(estados)
        }

        viewModel.buscarImagemDeGato()
        job.join() // This will wait for the flow to emit 3 items

        assertEquals(3, estados.size)
        assertEquals(EstadoDaTela.Inicial, estados[0])
        assertEquals(EstadoDaTela.Carregando, estados[1])
        assertTrue(estados[2] is EstadoDaTela.Erro)

        val erro = estados[2] as EstadoDaTela.Erro
        assertTrue(erro.mensagem.contains("Erro"))
    }
}