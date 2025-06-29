package br.com.mateuspoloninicardoso.catviewportifolio.presentation

sealed interface EstadoDaTela {
    object Inicial : EstadoDaTela
    object Carregando : EstadoDaTela
    data class Sucesso(val imagemUrl: String) : EstadoDaTela
    data class Erro(val mensagem: String) : EstadoDaTela
}