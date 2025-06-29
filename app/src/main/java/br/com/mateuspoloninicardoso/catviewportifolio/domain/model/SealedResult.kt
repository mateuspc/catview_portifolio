package br.com.mateuspoloninicardoso.catviewportifolio.domain.model

import br.com.mateuspoloninicardoso.catviewportifolio.data.remote.model.ApiErrorResponse

sealed class SealedResult<out T> {
    data class Success<out T>(val data: T) : SealedResult<T>()
    data class Error(val error: ApiErrorResponse) : SealedResult<Nothing>()
    object Loading : SealedResult<Nothing>()
}