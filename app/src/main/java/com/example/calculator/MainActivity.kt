package com.example.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import java.lang.Exception

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Calculator()
        }
    }
}

@Composable
fun Calculator() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CalculatorLayout()
    }
}

@Composable
fun CalculatorLayout() {
    var display by remember { mutableStateOf("0") }
    var operation by remember { mutableStateOf("") }
    var operator by remember { mutableStateOf("") }
    var firstOperand by remember { mutableStateOf("") }
    var shouldResetDisplay by remember { mutableStateOf(false) }
    var isResultDisplayed by remember { mutableStateOf(false) } // Para controlar si se mostró un resultado

    // Mostrar la operación completa
    Text(
        text = operation.ifEmpty { display },
        fontSize = 48.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        textAlign = TextAlign.End
    )

    Spacer(modifier = Modifier.height(16.dp))

    val buttonRows = listOf(
        listOf("7", "8", "9", "/"),
        listOf("4", "5", "6", "*"),
        listOf("1", "2", "3", "-"),
        listOf("0", ".", "C", "+"),
        listOf("=")
    )

    buttonRows.forEach { row ->
        ButtonRow(row) { symbol ->
            when (symbol) {
                "C" -> {
                    display = "0"
                    firstOperand = ""
                    operator = ""
                    operation = ""
                    isResultDisplayed = false // Reseteamos el estado de resultado
                }
                "=" -> {
                    if (firstOperand.isNotEmpty() && operator.isNotEmpty()) {
                        try {
                            val result = calculate(firstOperand, display, operator)
                            display = result
                            firstOperand = result  // Aquí se guarda el resultado como nuevo primer operando
                            operator = ""  // Limpiar el operador
                            operation = ""  // Limpiar operación
                            isResultDisplayed = true // Indicamos que se mostró un resultado
                        } catch (e: Exception) {
                            display = "Error"
                        }
                    }
                }
                "/", "*", "-", "+" -> {
                    if (firstOperand.isEmpty() || isResultDisplayed) {
                        firstOperand = display
                        operator = symbol
                        operation = "$firstOperand $operator "
                        shouldResetDisplay = true
                        isResultDisplayed = false // El resultado ya no está en pantalla
                    } else if (!shouldResetDisplay) {
                        try {
                            val result = calculate(firstOperand, display, operator)
                            firstOperand = result
                            operator = symbol
                            operation = "$firstOperand $operator "
                            display = result
                        } catch (e: Exception) {
                            display = "Error"
                        }
                        shouldResetDisplay = true
                    }
                }
                else -> {  // Números y decimal
                    if (shouldResetDisplay || isResultDisplayed) {
                        display = symbol
                        shouldResetDisplay = false
                        isResultDisplayed = false // El resultado ya no está en pantalla
                    } else {
                        display = if (display == "0") symbol else display + symbol
                    }
                    operation += symbol  // Mostrar el número en la operación
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

fun calculate(firstOperand: String, secondOperand: String, operator: String): String {
    val first = firstOperand.toDoubleOrNull() ?: return "Error"
    val second = secondOperand.toDoubleOrNull() ?: return "Error"

    return when (operator) {
        "+" -> (first + second).toString()
        "-" -> (first - second).toString()
        "*" -> (first * second).toString()
        "/" -> if (second != 0.0) (first / second).toString() else "Error"
        else -> "Error"
    }
}

@Composable
fun ButtonRow(row: List<String>, onClick: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        row.forEach { symbol ->
            CalculatorButton(symbol, onClick)
        }
    }
}

@Composable
fun CalculatorButton(symbol: String, onClick: (String) -> Unit) {
    val backgroundColor = when (symbol) {
        "C" -> Color.Red
        "=" -> Color.Green
        "/", "*", "-", "+" -> Color(0xFFFF9800)
        else -> Color(0xFF0DD1E9)
    }

    Button(
        onClick = { onClick(symbol) },
        modifier = Modifier
            .size(80.dp)
            .padding(4.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor)
    ) {
        Text(
            text = symbol,
            fontSize = 24.sp,
            color = Color.White
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CalculatorPreview() {
    Calculator()
}
