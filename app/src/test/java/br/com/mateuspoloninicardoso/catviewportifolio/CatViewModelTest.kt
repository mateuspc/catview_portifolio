package br.com.mateuspoloninicardoso.catviewportifolio

import CatViewModel
import br.com.mateuspoloninicardoso.catviewportifolio.data.remote.model.ApiErrorResponse
import br.com.mateuspoloninicardoso.catviewportifolio.domain.model.SealedResult
import br.com.mateuspoloninicardoso.catviewportifolio.domain.model.repository.CatRepository
import br.com.mateuspoloninicardoso.catviewportifolio.presentation.EstadoDaTela
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher // Importar para o teste do estado inicial

class CatViewModelTest {

    // Teste 1: Sucesso na busca da imagem
    @Test
    fun `deve emitir estados Inicial, Carregando e Sucesso`() = runTest {
        // 1. Configurar o StandardTestDispatcher para controlar o tempo
        val testDispatcher = StandardTestDispatcher(testScheduler)

        // 2. Criar um repositório falso (mock/stub) que simula a resposta de sucesso
        val fakeRepository = object : CatRepository {
            override suspend fun getCatImage(): SealedResult<String> {
                delay(1) // Simula um pequeno atraso (controlado pelo testScheduler)
                return SealedResult.Success("/cat/teste.jpg")
            }
        }

        // 3. Inicializar o ViewModel, injetando o repositório e os dispatchers de teste
        val estados = mutableListOf<EstadoDaTela>()
        val viewModel = CatViewModel(
            repository = fakeRepository,
            externalScope = this,       // 'this' é o TestScope do runTest
        )

        // 4. Lançar uma corrotina para coletar os estados do Flow
        val job = launch {
            viewModel.estado.take(3).toList(estados) // Coleta os 3 estados esperados
        }

        // 5. Chamar o método que inicia a operação
        viewModel.buscarImagemDeGato()

        // 6. Avançar o tempo virtual para que todas as corrotinas pendentes sejam executadas
        testScheduler.advanceUntilIdle()

        // 7. Esperar a corrotina de coleta terminar
        job.join()

        // 8. Fazer as asserções
        assertEquals(
            listOf(
                EstadoDaTela.Inicial,
                EstadoDaTela.Carregando,
                EstadoDaTela.Sucesso("/cat/teste.jpg")
            ),
            estados
        )
    }

    // Teste 2: Erro na busca da imagem
    @Test
    fun `deve emitir estados Inicial, Carregando e Erro`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val fakeRepository = object : CatRepository {
            override suspend fun getCatImage(): SealedResult<String> {
                delay(1) // Simula um pequeno atraso
                return SealedResult.Error(ApiErrorResponse(500, "Erro desconhecido ao buscar imagem"))
            }
        }

        val estados = mutableListOf<EstadoDaTela>()
        val viewModel = CatViewModel(
            repository = fakeRepository,
            externalScope = this,
        )

        val job = launch {
            viewModel.estado.take(3).toList(estados)
        }

        viewModel.buscarImagemDeGato()
        testScheduler.advanceUntilIdle() // Garante que a corrotina com delay seja executada
        job.join()

        assertEquals(
            listOf(
                EstadoDaTela.Inicial,
                EstadoDaTela.Carregando,
                EstadoDaTela.Erro("Erro desconhecido ao buscar imagem")
            ),
            estados
        )
    }

    @Test
    fun `estado inicial deve ser Inicial`() = runTest {

        val fakeRepository = object : CatRepository {
            override suspend fun getCatImage(): SealedResult<String> {
                return SealedResult.Success("") // Não importa o que retorna, pois não será chamado.
            }
        }

        val viewModel = CatViewModel(
            repository = fakeRepository,
            externalScope = this,
        )

        // O estado deve ser Inicial imediatamente após a criação do ViewModel
        assertEquals(EstadoDaTela.Inicial, viewModel.estado.value)
    }
}