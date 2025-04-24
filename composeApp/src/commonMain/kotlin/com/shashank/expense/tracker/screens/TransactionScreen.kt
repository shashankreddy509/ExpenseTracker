package com.shashank.expense.tracker.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shashank.expense.tracker.utils.CategoryUtils
import com.shashank.expense.tracker.utils.DateTimeUtils
import com.shashank.expense.tracker.viewmodels.HomeViewModel
import expensetracker.composeapp.generated.resources.Res
import expensetracker.composeapp.generated.resources.ic_arrow_right
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource

@Composable
fun TransactionScreen(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    val expenses by viewModel.filteredExpenses.collectAsState()
    var showFinancialReport by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFCFCFC))
            .padding(horizontal = 16.dp)
    ) {
        // Month Selector at top
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MonthSelector(
                selectedMonth = viewModel.selectedMonth.collectAsState().value,
                onMonthSelected = { viewModel.selectMonth(it) }
            )
        }

        // Financial Report Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F1FE))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "See your financial report",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF7F3DFF)
                )
                Icon(
                    painter = painterResource(Res.drawable.ic_arrow_right),
                    contentDescription = "View Report",
                    tint = Color(0xFF7F3DFF)
                )
            }
        }

        // Transactions List
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val groupedExpenses = expenses.groupBy { expense ->
                when {
                    DateTimeUtils.isToday(expense.date) -> "Today"
                    DateTimeUtils.isYesterday(expense.date) -> "Yesterday"
                    else -> DateTimeUtils.formatDate(expense.date)
                }
            }

            groupedExpenses.forEach { (date, expensesList) ->
                item {
                    Text(
                        text = date,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }

                items(expensesList) { expense ->
                    TransactionItem(expense = expense)
                }
            }
        }
    }
}

@Composable
private fun TransactionItem(expense: ExpenseModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Icon with background
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = Color(0x14000000),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(CategoryUtils.getCategoryIcon(CategoryModel(0, expense.category))),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = CategoryUtils.getCategoryColor(CategoryModel(0, expense.category))
                )
            }

            Column {
                Text(
                    text = expense.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = expense.category,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF91919F)
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "${if (expense.isIncome) "+" else "-"} $${expense.amount}",
                style = MaterialTheme.typography.titleMedium,
                color = if (expense.isIncome) Color(0xFF00A86B) else Color(0xFFFD3C4A),
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = DateTimeUtils.formatTime(
                    Instant.fromEpochMilliseconds(expense.date)
                        .toLocalDateTime(TimeZone.currentSystemDefault())
                ),
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF91919F)
            )
        }
    }
} 