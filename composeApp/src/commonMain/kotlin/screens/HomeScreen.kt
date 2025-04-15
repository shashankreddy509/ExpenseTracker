import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))
    ) {
        // Top Bar with Profile
        TopBar()
        
        // Balance Card
        BalanceCard()
        
        // Favorite Categories
        FavoriteCategories()
        
        // Recent Spendings
        RecentSpendings()
        
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
private fun BalanceCard() {
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
                Icons.Default.AccountBalance,
                contentDescription = "Balance",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "₹ 86783.30",
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
                        "Salary",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                    Text(
                        "₹ 52000.00",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "Spend",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                    Text(
                        "₹ 32000.00",
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
private fun FavoriteCategories() {
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
            CategoryItem("Gift", Icons.Default.CardGiftcard)
            CategoryItem("Shopping", Icons.Default.ShoppingBag)
            CategoryItem("Travel", Icons.Default.DirectionsCar)
            CategoryItem("See All", Icons.Default.ArrowForward)
        }
    }
}

@Composable
private fun CategoryItem(title: String, icon: ImageVector) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
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
private fun RecentSpendings() {
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
                Text("Weekly")
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Select period")
            }
        }
        
        SpendingItem(
            "Nike Store",
            "Clothing",
            "- ₹1782.00",
            "Tax : ₹320.00",
            Icons.Default.ShoppingBag
        )
        
        SpendingItem(
            "Refound From Amazon",
            "Shopping",
            "+ ₹15582.00",
            "Tax : ₹520.00",
            Icons.Default.ShoppingCart,
            isPositive = true
        )
        
        SpendingItem(
            "Uber",
            "Travel",
            "- ₹120.00",
            "Tax : ₹120.00",
            Icons.Default.DirectionsCar
        )
    }
}

@Composable
private fun SpendingItem(
    title: String,
    category: String,
    amount: String,
    tax: String,
    icon: ImageVector,
    isPositive: Boolean = false
) {
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
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, fontWeight = FontWeight.Medium)
                Text(category, color = Color.Gray, fontSize = 14.sp)
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                amount,
                color = if (isPositive) Color.Green else Color.Red,
                fontWeight = FontWeight.Bold
            )
            Text(tax, color = Color.Gray, fontSize = 12.sp)
        }
    }
}

@Composable
private fun BottomNavigation() {
    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = Color.White
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            selected = true,
            onClick = {}
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.CreditCard, contentDescription = "Cards") },
            selected = false,
            onClick = {}
        )
        NavigationBarItem(
            icon = { 
                FloatingActionButton(
                    onClick = {},
                    containerColor = Color(0xFF00BCD4)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            },
            selected = false,
            onClick = {}
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.PieChart, contentDescription = "Analytics") },
            selected = false,
            onClick = {}
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            selected = false,
            onClick = {}
        )
    }
} 