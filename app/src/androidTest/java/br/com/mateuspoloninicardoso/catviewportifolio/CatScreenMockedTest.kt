package br.com.mateuspoloninicardoso.catviewportifolio.test

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.mateuspoloninicardoso.catviewportifolio.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CatScreenMockedTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun quando_clicar_e_api_mockada_retornar_sucesso_deve_exibir_gato_e_mensagem_sucesso() {
        composeTestRule.onNodeWithTag("buscar_imagem_button").performClick()

        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithTag("loading_indicator").fetchSemanticsNodes().isEmpty()
        }

        composeTestRule.onNodeWithTag("cat_image").assertIsDisplayed()
        composeTestRule.onNodeWithTag("status_text_sucesso").assertIsDisplayed()
    }

}
