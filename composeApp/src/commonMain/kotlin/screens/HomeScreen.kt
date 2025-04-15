import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.DatabaseHelper
import kotlinx.coroutines.launch
import viewmodels.HomeViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val scope = rememberCoroutineScope()
    val expenses by viewModel.expenses.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val favoriteCategories by viewModel.favoriteCategories.collectAsState()
    val totalBalance by remember { mutableStateOf(viewModel.totalBalance) }
    val totalIncome by remember { mutableStateOf(viewModel.totalIncome) }
    val totalExpense by remember { mutableStateOf(viewModel.totalExpense) }

    LaunchedEffect(Unit) {
        scope.launch {
            viewModel.loadExpenses()
            viewModel.loadCategories()
            viewModel.loadFavoriteCategories()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))
    ) {
        // Top Bar with Profile
        TopBar()
        
        // Balance Card
        BalanceCard(
            totalBalance = totalBalance,
            totalIncome = totalIncome,
            totalExpense = totalExpense
        )
        
        // Favorite Categories
        FavoriteCategories(
            categories = favoriteCategories,
            onCategoryClick = { category ->
                scope.launch {
                    viewModel.toggleCategoryFavorite(category)
                }
            }
        )
        
        // Recent Spendings
        RecentSpendings(expenses = expenses)
        
        // Bottom Navigation
        BottomNavigation()
    }
}

@Composable
private fun TopBar() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(
                    text = "Hi John Doe",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Recommended actions for you",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
        Row {
            IconButton(onClick = {}) {
                Icon(Icons.Default.Notifications, contentDescription = "Notifications")
            }
            IconButton(onClick = {}) {
                Icon(Icons.Default.MoreVert, contentDescription = "More")
            }
        }
    }
}

@Composable
private fun BalanceCard(
    totalBalance: Double,
    totalIncome: Double,
    totalExpense: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .background(Color(0xFF7C4DFF))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Check,
                contentDescription = "Balance",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "₹ ${String.format("%.2f", totalBalance)}",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Available Balance",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "Income",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                    Text(
                        "₹ ${String.format("%.2f", totalIncome)}",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "Expense",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                    Text(
                        "₹ ${String.format("%.2f", totalExpense)}",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoriteCategories(
    categories: List<DatabaseHelper.CategoryModel>,
    onCategoryClick: (DatabaseHelper.CategoryModel) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            "Favourite Categories",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            categories.take(4).forEach { category ->
                CategoryItem(
                    title = category.name,
                    icon = category.icon,
                    isFavorite = category.isFavorite,
                    onClick = { onCategoryClick(category) }
                )
            }
        }
    }
}

@Composable
private fun CategoryItem(
    title: String,
    icon: String,
    isFavorite: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.ShoppingCart, // TODO: Map icon string to actual icon
                contentDescription = title,
                tint = Color(0xFF1A237E),
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(title, fontSize = 14.sp)
    }
}

@Composable
private fun RecentSpendings(expenses: List<DatabaseHelper.ExpenseModel>) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Recent Spendings",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = {}) {
                Text("See All")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        expenses.take(5).forEach { expense ->
            ExpenseItem(expense)
        }
    }
}

@Composable
private fun ExpenseItem(expense: DatabaseHelper.ExpenseModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.ShoppingCart, // TODO: Map category to icon
                    contentDescription = expense.category,
                    tint = Color(0xFF1A237E)
                )
            }
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(
                    expense.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    expense.category,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
        Text(
            "₹ ${String.format("%.2f", expense.amount)}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = if (expense.type == "expense") Color.Red else Color.Green
        )
    }
}

@Composable
private fun BottomNavigation() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        IconButton(onClick = {}) {
            Icon(Icons.Default.Home, contentDescription = "Home")
        }
        IconButton(onClick = {}) {
            Icon(Icons.Default.Add, contentDescription = "Add")
        }
        IconButton(onClick = {}) {
            Icon(Icons.Default.Person, contentDescription = "Profile")
        }
    }
} 