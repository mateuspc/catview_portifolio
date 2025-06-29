package br.com.mateuspoloninicardoso.catviewportifolio.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class CatResponse(val id: String, val url: String)