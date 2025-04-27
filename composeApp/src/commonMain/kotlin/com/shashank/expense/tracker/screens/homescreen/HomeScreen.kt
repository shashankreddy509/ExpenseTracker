package com.shashank.expense.tracker.screens.homescreen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import expensetracker.composeapp.generated.resources.Res
import expensetracker.composeapp.generated.resources.ic_add
import expensetracker.composeapp.generated.resources.ic_arrow_down
import expensetracker.composeapp.generated.resources.ic_expense
import expensetracker.composeapp.generated.resources.ic_home
import expensetracker.composeapp.generated.resources.ic_income
import expensetracker.composeapp.generated.resources.ic_notification
import expensetracker.composeapp.generated.resources.ic_pie_chart
import expensetracker.composeapp.generated.resources.ic_transaction
import expensetracker.composeapp.generated.resources.ic_user
import kotlinx.datetime.*
import org.jetbrains.compose.resources.painterResource
import com.shashank.expense.tracker.utils.CategoryUtils
import com.shashank.expense.tracker.utils.DateTimeUtils

data class CategoryModel(
    val id: Long,
    val title: String,
    val isFavorite: Boolean = false
)

data class ExpenseModel(
    val id: Long,
    val title: String,
    val amount: Double,
    val category: String,
    val isIncome: Boolean,
    val date: Long
)

@Composable
fun MonthSelector(
    modifier: Modifier = Modifier,
    selectedMonth: Month,
    onMonthSelected: (Month) -> Unit = {}
) {
    var isExpanded by remember { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "dropdown arrow rotation"
    )

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .clickable { isExpanded = !isExpanded }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            AnimatedContent(
                targetState = selectedMonth,
                transitionSpec = {
                    slideInVertically { height -> height } + fadeIn() togetherWith
                            slideOutVertically { height -> -height } + fadeOut()
                },
                label = "month transition"
            ) { month ->
                Text(
                    text = month.name.lowercase()
                        .replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF212224)
                )
            }
            Icon(
                painter = painterResource(Res.drawable.ic_arrow_down),
                contentDescription = "Select Month",
                modifier = Modifier
                    .size(24.dp)
                    .rotate(rotationAngle),
                tint = Color(0xFF212224)
            )
        }

        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            modifier = Modifier
                .background(Color.White)
                .defaultMinSize(minWidth = 160.dp),
            offset = DpOffset(0.dp, 8.dp)
        ) {
            Month.values().forEach { month ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = month.name.lowercase()
                                .replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (month == selectedMonth) Color(0xFF7F3DFF) else Color(0xFF212224)
                        )
                    },
                    onClick = {
                        onMonthSelected(month)
                        isExpanded = false
                    },
                    colors = MenuDefaults.itemColors(
                        textColor = Color(0xFF212224),
                        leadingIconColor = Color(0xFF7F3DFF),
                        trailingIconColor = Color(0xFF7F3DFF)
                    )
                )
            }
        }
    }
}

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val expenses by viewModel.filteredExpenses.collectAsState()
    val totalBalance by viewModel.totalBalance.collectAsState()
    val totalIncome by viewModel.totalIncome.collectAsState()
    val totalExpenses by viewModel.totalExpenses.collectAsState()
    val selectedMonth by viewModel.selectedMonth.collectAsState()

    Scaffold(
        containerColor = Color(0xFFFCFCFC),
        bottomBar = { BottomNavigation() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Top Section with Profile and Month
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    modifier = Modifier.size(40.dp),
                    color = Color(0xFFF1F1F1)
                ) {
                    Image(
                        painter = painterResource(Res.drawable.ic_user),
                        contentDescription = "Profile",
                        modifier = Modifier.padding(8.dp),
                        colorFilter = ColorFilter.tint(Color(0xFF7F3DFF))
                    )
                }

                MonthSelector(
                    selectedMonth = selectedMonth,
                    onMonthSelected = { viewModel.selectMonth(it) }
                )

                IconButton(
                    onClick = { /* TODO: Show notifications */ }
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_notification),
                        contentDescription = "Notifications",
                        tint = Color(0xFF212224)
                    )
                }
            }

            // Animate the content when month changes
            AnimatedContent(
                targetState = selectedMonth,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith
                            fadeOut(animationSpec = tween(300))
                },
                label = "content transition"
            ) { _ ->
                // Rest of the content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    // Balance Section
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Account Balance",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF91919F)
                        )
                        Text(
                            text = "$${formatCurrency(totalBalance.toFloat())}",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        // Income and Expense Cards
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Income Card
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF00A86B))
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color.White.copy(alpha = 0.2f),
                                        modifier = Modifier.size(48.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(Res.drawable.ic_income),
                                            contentDescription = null,
                                            modifier = Modifier.padding(12.dp),
                                            tint = Color.White
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = "Income",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "$${formatCurrency(totalIncome.toFloat())}",
                                            style = MaterialTheme.typography.titleLarge,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            // Expense Card
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFD3C4A))
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color.White.copy(alpha = 0.2f),
                                        modifier = Modifier.size(48.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(Res.drawable.ic_expense),
                                            contentDescription = null,
                                            modifier = Modifier.padding(12.dp),
                                            tint = Color.White
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = "Expenses",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "$${formatCurrency(totalExpenses.toFloat())}",
                                            style = MaterialTheme.typography.titleLarge,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Spend Frequency Section
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Spend Frequency",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        SpendFrequencyGraph(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            viewModel = viewModel,
                            onTimeFrameSelected = { timeFrame ->
                                viewModel.updateTimeFrame(timeFrame)
                            }
                        )
                    }

                    // Recent Transactions
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Recent Transaction",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            TextButton(
                                onClick = { /* TODO: Show all transactions */ }
                            ) {
                                Text(
                                    text = "See All",
                                    color = Color(0xFF7F3DFF)
                                )
                            }
                        }

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(expenses) { expense ->
                                TransactionItem(expense = expense)
                            }
                        }
                    }
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
                text = if (expense.isIncome) "+$${formatCurrency(expense.amount.toFloat())}" else "-$${formatCurrency(expense.amount.toFloat())}",
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

@Composable
private fun BottomNavigation(
    onAddClick: () -> Unit = {}
) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
        containerColor = Color.White,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    painterResource(resource = Res.drawable.ic_home),
                    contentDescription = "Home",
                    tint = Color(0xFF7F3DFF)
                )
            },
            label = {
                Text(
                    "Home",
                    color = Color(0xFF7F3DFF),
                    fontSize = 10.sp
                )
            },
            selected = true,
            onClick = {},
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF7F3DFF),
                unselectedIconColor = Color(0xFFC6C6C6),
                selectedTextColor = Color(0xFF7F3DFF),
                unselectedTextColor = Color(0xFFC6C6C6),
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            icon = {
                Icon(
                    painterResource(Res.drawable.ic_transaction),
                    contentDescription = "Transaction",
                    tint = Color(0xFFC6C6C6)
                )
            },
            label = {
                Text(
                    "Transaction",
                    color = Color(0xFFC6C6C6),
                    fontSize = 10.sp
                )
            },
            selected = false,
            onClick = {},
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF7F3DFF),
                unselectedIconColor = Color(0xFFC6C6C6),
                selectedTextColor = Color(0xFF7F3DFF),
                unselectedTextColor = Color(0xFFC6C6C6),
                indicatorColor = Color.Transparent
            )
        )
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = Color(0xFF7F3DFF),
                shape = CircleShape,
                modifier = Modifier
                    .size(56.dp)
                    .offset(y = (-8).dp)
            ) {
                Icon(
                    painterResource(Res.drawable.ic_add),
                    contentDescription = "Add",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        NavigationBarItem(
            icon = {
                Icon(
                    painterResource(Res.drawable.ic_pie_chart),
                    contentDescription = "Budget",
                    tint = Color(0xFFC6C6C6)
                )
            },
            label = {
                Text(
                    "Budget",
                    color = Color(0xFFC6C6C6),
                    fontSize = 10.sp
                )
            },
            selected = false,
            onClick = {},
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF7F3DFF),
                unselectedIconColor = Color(0xFFC6C6C6),
                selectedTextColor = Color(0xFF7F3DFF),
                unselectedTextColor = Color(0xFFC6C6C6),
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            icon = {
                Icon(
                    painterResource(Res.drawable.ic_user),
                    contentDescription = "Profile",
                    tint = Color(0xFFC6C6C6)
                )
            },
            label = {
                Text(
                    "Profile",
                    color = Color(0xFFC6C6C6),
                    fontSize = 10.sp
                )
            },
            selected = false,
            onClick = {},
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF7F3DFF),
                unselectedIconColor = Color(0xFFC6C6C6),
                selectedTextColor = Color(0xFF7F3DFF),
                unselectedTextColor = Color(0xFFC6C6C6),
                indicatorColor = Color.Transparent
            )
        )
    }
}

@Composable
private fun SpendFrequencyGraph(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel,
    onTimeFrameSelected: (String) -> Unit = {}
) {
    val spendingData by viewModel.spendingData.collectAsState()
    val selectedTimeFrame by viewModel.selectedTimeFrame.collectAsState()
    var selectedPoint by remember { mutableStateOf<SpendingPoint?>(null) }

    Column(modifier = modifier) {
        // Value indicator
        selectedPoint?.let { point ->
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "$${formatCurrency(point.value)}",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF7F3DFF),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = point.label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF91919F)
                )
            }
        } ?: run {
            // Show total when no point is selected
            val total = spendingData.sumOf { it.value.toDouble() }.toFloat()
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF91919F)
                )
                Text(
                    text = "$${formatCurrency(total)}",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF7F3DFF),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Graph
        if (spendingData.isNotEmpty()) {
            val points = spendingData.map { it.value }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                val width = size.width
                                val xStep = width / (points.size - 1)
                                val index = (offset.x / xStep).toInt().coerceIn(0, points.size - 1)
                                selectedPoint = spendingData.getOrNull(index)
                            }
                        }
                ) {
                    val width = size.width
                    val height = size.height
                    val maxPoint = points.maxOrNull() ?: 0f

                    val xStep = width / (points.size - 1)
                    val yStep = height / maxPoint

                    // Draw grid lines
                    drawLine(
                        color = Color.LightGray.copy(alpha = 0.3f),
                        start = Offset(0f, height / 2),
                        end = Offset(width, height / 2),
                        strokeWidth = 1.dp.toPx()
                    )

                    val path = Path()
                    val fillPath = Path()

                    points.forEachIndexed { index, point ->
                        val x = index * xStep
                        val y = height - (point * yStep)

                        if (index == 0) {
                            path.moveTo(x, y)
                            fillPath.moveTo(x, height)
                            fillPath.lineTo(x, y)
                        } else {
                            val prevX = (index - 1) * xStep
                            val prevY = height - (points[index - 1] * yStep)

                            val controlX1 = prevX + (x - prevX) / 2f
                            val controlX2 = prevX + (x - prevX) / 2f

                            path.cubicTo(
                                controlX1, prevY,
                                controlX2, y,
                                x, y
                            )

                            fillPath.cubicTo(
                                controlX1, prevY,
                                controlX2, y,
                                x, y
                            )
                        }
                    }

                    fillPath.lineTo(width, height)
                    fillPath.close()

                    drawPath(
                        path = fillPath,
                        color = Color(0xFF7F3DFF).copy(alpha = 0.2f)
                    )

                    drawPath(
                        path = path,
                        color = Color(0xFF7F3DFF),
                        style = Stroke(
                            width = 4.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    )

                    // Draw points and highlight selected point
                    points.forEachIndexed { index, point ->
                        val x = index * xStep
                        val y = height - (point * yStep)
                        val isSelected = selectedPoint == spendingData[index]

                        if (isSelected) {
                            drawCircle(
                                color = Color(0xFF7F3DFF).copy(alpha = 0.2f),
                                radius = 12.dp.toPx(),
                                center = Offset(x, y)
                            )
                        }

                        drawCircle(
                            color = Color.White,
                            radius = 6.dp.toPx(),
                            center = Offset(x, y)
                        )

                        drawCircle(
                            color = Color(0xFF7F3DFF),
                            radius = 4.dp.toPx(),
                            center = Offset(x, y)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Time Period Selection
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("Today", "Week", "Month", "Year").forEach { period ->
                Surface(
                    shape = RoundedCornerShape(32.dp),
                    color = if (period == selectedTimeFrame) Color(0xFF7F3DFF) else Color.Transparent,
                    modifier = Modifier
                        .clickable { onTimeFrameSelected(period) }
                        .padding(end = 8.dp)
                ) {
                    Text(
                        text = period,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = if (period == selectedTimeFrame) Color.White else Color(0xFF91919F)
                    )
                }
            }
        }
    }
}

private fun formatCurrency(value: Float): String {
    return when {
        value >= 1_000_000 -> "${(value / 1_000_000).toInt()}M"
        value >= 1_000 -> "${(value / 1_000).toInt()}K"
        else -> value.toInt().toString()
    }
} 