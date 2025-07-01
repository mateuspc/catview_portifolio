package br.com.mateuspoloninicardoso.catviewportifolio.domain.model.repository

import br.com.mateuspoloninicardoso.catviewportifolio.domain.model.SealedResult

interface CatRepository {
    suspend fun getCatImage(): SealedResult<String>
}