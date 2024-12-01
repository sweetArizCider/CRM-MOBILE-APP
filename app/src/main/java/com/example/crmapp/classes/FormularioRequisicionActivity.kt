package com.example.crmapp.classes

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.crmapp.R
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.util.Calendar
import kotlin.concurrent.thread

class FormularioRequisicionActivity : AppCompatActivity() {

    private lateinit var spinnerCliente: Spinner
    private lateinit var editTextFecha: EditText
    private lateinit var editTextCantidadServicio: EditText
    private lateinit var editTextCantidadDinero: EditText
    private lateinit var editTextServicio: EditText
    private lateinit var buttonEnviar: Button
    private lateinit var selectedClientId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_requisicion)

        spinnerCliente = findViewById(R.id.spinnerCliente)
        editTextFecha = findViewById(R.id.editTextFecha)
        editTextCantidadServicio = findViewById(R.id.editTextCantidadServicio)
        editTextCantidadDinero = findViewById(R.id.editTextCantidadDinero)
        editTextServicio = findViewById(R.id.editTextServicio)
        buttonEnviar = findViewById(R.id.buttonEnviar)

        // Configurar el DatePickerDialog para el EditText de la fecha
        editTextFecha.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                    // Formato de la fecha: yyyy-MM-dd
                    val formattedDate = "${selectedYear}-${selectedMonth + 1}-${selectedDayOfMonth}"
                    editTextFecha.setText(formattedDate)
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }

        // Cargar clientes en el Spinner
        cargarClientes()

        // Configurar el listener del Spinner
        spinnerCliente.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedClientId = (parentView.getItemAtPosition(position) as String).split("-")[0] // Extrae el ID del cliente
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // No hacer nada si no se selecciona ningún elemento
            }
        }

        buttonEnviar.setOnClickListener {
            val fkidCliente = selectedClientId
            val fechaCreacion = editTextFecha.text.toString()
            val cantidadServicio = editTextCantidadServicio.text.toString()
            val cantidadDinero = editTextCantidadDinero.text.toString()
            val servicio = editTextServicio.text.toString()

            if (fkidCliente.isEmpty() || fechaCreacion.isEmpty() || cantidadServicio.isEmpty() ||
                cantidadDinero.isEmpty() || servicio.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
            } else {
                enviarRequisicion(fkidCliente, fechaCreacion, cantidadServicio, cantidadDinero, servicio)
            }
        }
    }

    private fun cargarClientes() {
        thread {
            try {
                val url = URL("http://18.223.99.187/api/getClientesNombre.php")
                val connection = url.openConnection() as HttpURLConnection

                connection.requestMethod = "GET"
                connection.connect()

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().readText()
                    val jsonArray = JSONArray(response)

                    val clientList = mutableListOf<String>()
                    for (i in 0 until jsonArray.length()) {
                        val client = jsonArray.getJSONObject(i)
                        val idCliente = client.getString("idCliente")
                        val empresa = client.getString("empresa")
                        clientList.add("$idCliente - $empresa")
                    }

                    runOnUiThread {
                        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, clientList)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinnerCliente.adapter = adapter
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "Error al cargar clientes: $responseCode", Toast.LENGTH_SHORT).show()
                    }
                }

                connection.disconnect()
            } catch (e: Exception) {
                Log.e("FormularioRequisicion", "Error: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun enviarRequisicion(fkidCliente: String, fechaCreacion: String, cantidadServicio: String, cantidadDinero: String, servicio: String) {
        thread {
            try {
                val url = URL("http://18.223.99.187/api/createRequisicion.php")
                val connection = url.openConnection() as HttpURLConnection

                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                connection.doOutput = true

                val postData = "fkidCliente=$fkidCliente&fechaCreacion=$fechaCreacion&cantidadServicio=$cantidadServicio&cantidadDinero=$cantidadDinero&servicio=$servicio"
                connection.outputStream.use { it.write(postData.toByteArray()) }

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().readText()
                    Log.d("FormularioRequisicion", "Respuesta: $response")

                    runOnUiThread {
                        Toast.makeText(this, "Requisición creada exitosamente.", Toast.LENGTH_SHORT).show()
                        finish() // Cierra la actividad después de enviar los datos
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "Error del servidor: $responseCode", Toast.LENGTH_SHORT).show()
                    }
                }

                connection.disconnect()
            } catch (e: Exception) {
                Log.e("FormularioRequisicion", "Error: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
