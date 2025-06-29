package br.com.mateuspoloninicardoso.catviewportifolio.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mateuspoloninicardoso.catviewportifolio.data.remote.getCatImage
import br.com.mateuspoloninicardoso.catviewportifolio.domain.model.SealedResult
import io.ktor.client.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CatViewModel(private val client: HttpClient) : ViewModel() {

    private val _estado = MutableStateFlow<EstadoDaTela>(EstadoDaTela.Inicial)
    val estado: StateFlow<EstadoDaTela> = _estado.asStateFlow()

    fun buscarImagemDeGato() {
        viewModelScope.launch(Dispatchers.IO) {
            _estado.value = EstadoDaTela.Carregando
            when (val resultado = getCatImage(client)) {
                is SealedResult.Success -> _estado.value = EstadoDaTela.Sucesso(resultado.data)
                is SealedResult.Error -> _estado.value = EstadoDaTela.Erro(resultado.error.message)
                is SealedResult.Loading -> _estado.value = EstadoDaTela.Carregando
            }
        }
    }
}