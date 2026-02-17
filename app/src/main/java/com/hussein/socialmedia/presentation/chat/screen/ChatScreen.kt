import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    //viewModel: ChatViewModel = hiltViewModel()
) {
    Text(text = "Chat Screen")
}