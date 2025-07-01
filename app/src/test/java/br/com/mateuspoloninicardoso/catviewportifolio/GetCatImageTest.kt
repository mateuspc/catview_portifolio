package br.com.mateuspoloninicardoso.catviewportifolio

import br.com.mateuspoloninicardoso.catviewportifolio.data.remote.model.CatResponse
import br.com.mateuspoloninicardoso.catviewportifolio.domain.model.SealedResult
import br.com.mateuspoloninicardoso.catviewportifolio.domain.model.remote.CatRepositoryImpl
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class CatRepositoryImplTest {

    private val json = Json { ignoreUnknownKeys = true }

    private fun createMockClient(handler: MockRequestHandler): HttpClient {
        return HttpClient(MockEngine) {
            engine { addHandler(handler) }
            install(ContentNegotiation) { json(json) }
        }
    }

    @Test
    fun `deve retornar sucesso com url de imagem do gato`() = runTest {
        val response = CatResponse("abc123", "/cat/abc.jpg")
        val client = createMockClient {
            respond(
                content = json.encodeToString(response),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        val repository = CatRepositoryImpl(client)
        val result = repository.getCatImage()

        assertTrue(result is SealedResult.Success)
        assertEquals("/cat/abc.jpg", (result as SealedResult.Success).data)
    }

    @Test
    fun `deve retornar erro ao receber status inesperado`() = runTest {
        val client = createMockClient {
            respond("Internal Error", HttpStatusCode.InternalServerError)
        }

        val repository = CatRepositoryImpl(client)
        val result = repository.getCatImage()

        assertTrue(result is SealedResult.Error)
        val error = (result as SealedResult.Error).error
        assertEquals(500, error.errorCode)
        assertEquals("Erro desconhecido ao buscar imagem.", error.message)
    }

    @Test
    fun `deve retornar erro de conexao`() = runTest {
        val client = createMockClient {
            throw RuntimeException("Timeout na rede")
        }

        val repository = CatRepositoryImpl(client)
        val result = repository.getCatImage()

        assertTrue(result is SealedResult.Error)
        val error = (result as SealedResult.Error).error
        assertEquals(0, error.errorCode)
        assertTrue(error.message.contains("Timeout"))
    }
}
