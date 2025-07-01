import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mateuspoloninicardoso.catviewportifolio.domain.model.SealedResult
import br.com.mateuspoloninicardoso.catviewportifolio.domain.model.repository.CatRepository
import br.com.mateuspoloninicardoso.catviewportifolio.presentation.EstadoDaTela
import br.com.mateuspoloninicardoso.catviewportifolio.presentation.EstadoDaTela.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel // Importa a função de extensão cancel

class CatViewModel(
    private val repository: CatRepository,
    // É bom injetar externalScope para testes, mas o ViewModel precisa de seu próprio escopo interno
    // se for para sobreviver a mudanças de configuração ou gerenciar seu próprio ciclo de vida.
    externalScope: CoroutineScope? = null // Torna-o anulável se nem sempre for usado
) {
    // Este é o escopo para as operações de longa duração do ViewModel.
    // Use Dispatchers.Main.immediate ou similar no Android, ou Dispatchers.Default para Kotlin geral.
    // SupervisorJob permite que falhas de filhos não cancelem o escopo inteiro.
    private val viewModelScope = externalScope ?: CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val _estado = MutableStateFlow<EstadoDaTela>(EstadoDaTela.Inicial)
    val estado: StateFlow<EstadoDaTela> = _estado.asStateFlow()

    fun buscarImagemDeGato() {
        viewModelScope.launch { // Lança no escopo interno do ViewModel
            _estado.value = Carregando
            when (val result = repository.getCatImage()) {
                is SealedResult.Success -> {
                    _estado.value = Sucesso(result.data)
                }
                is SealedResult.Error -> {
                    _estado.value = Erro( "Erro desconhecido ao buscar imagem")
                }

                SealedResult.Loading -> _estado.value = Carregando
            }
        }
    }

    // Chame este método no bloco finally do seu teste para cancelar quaisquer corrotinas em andamento
    fun clear() {
        viewModelScope.cancel()
    }
}