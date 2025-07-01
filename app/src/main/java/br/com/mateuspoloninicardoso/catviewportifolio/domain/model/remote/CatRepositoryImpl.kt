package br.com.mateuspoloninicardoso.catviewportifolio.domain.model.remote

import br.com.mateuspoloninicardoso.catviewportifolio.data.remote.model.ApiErrorResponse
import br.com.mateuspoloninicardoso.catviewportifolio.data.remote.model.CatResponse
import br.com.mateuspoloninicardoso.catviewportifolio.domain.model.SealedResult
import br.com.mateuspoloninicardoso.catviewportifolio.domain.model.repository.CatRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess

class CatRepositoryImpl(
    private val client: HttpClient,
    private val baseUrl: String = "https://cataas.com"
) : CatRepository {
    override suspend fun getCatImage(): SealedResult<String> {
        return try {
            val response: HttpResponse = client.get("$baseUrl/cat?json=true") {
                expectSuccess = false
            }

            if (response.status.isSuccess()) {
                val cat = response.body<CatResponse>()
                SealedResult.Success(cat.url)
            } else {
                SealedResult.Error(ApiErrorResponse(response.status.value, "Erro desconhecido ao buscar imagem."))
            }

        } catch (e: Exception) {
            SealedResult.Error(ApiErrorResponse(0, "Erro de conexão: ${e.message}"))
        }
    }
}
