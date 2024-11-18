package ph.edu.auf.realmdiscussion.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ph.edu.auf.realmdiscussion.viewmodels.OwnerViewModel

@Composable
fun OwnerScreen(ownerViewModel: OwnerViewModel = viewModel()){

    val owners by ownerViewModel.owners.collectAsState()

    Column (modifier = Modifier.fillMaxSize()){
        Scaffold { paddingValues ->
            LazyColumn(modifier = Modifier.padding(paddingValues)) {
                itemsIndexed(
                    items = owners,
                    key = {_,item -> item.id}
                ){_,ownerContent ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 8.dp),
                        elevation =  CardDefaults.cardElevation(
                            defaultElevation = 5.dp
                        ),
                        shape = RoundedCornerShape(5.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = ownerContent.name,
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Owns ${ownerContent.pets.size} pets",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                }
            }
        }
    }

}