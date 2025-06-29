package br.com.mateuspoloninicardoso.catviewportifolio.data.remote

import br.com.mateuspoloninicardoso.catviewportifolio.data.remote.model.ApiErrorResponse
import br.com.mateuspoloninicardoso.catviewportifolio.data.remote.model.CatResponse
import br.com.mateuspoloninicardoso.catviewportifolio.domain.model.SealedResult
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

suspend fun getCatImage(client: HttpClient): SealedResult<String> {
    return try {
        val response: HttpResponse = client.get("https://cataas.com/cat?json=true") {
            expectSuccess = false
        }

        if (response.status.isSuccess()) {
            print(response)
            val cat = response.body<CatResponse>()
            SealedResult.Success(cat.url)
        } else {
            SealedResult.Error(ApiErrorResponse(response.status.value, "Erro inesperado da API do gato."))
        }

    } catch (e: Exception) {
        SealedResult.Error(ApiErrorResponse(0, "Erro de conexão: ${e.message}"))
    }
}