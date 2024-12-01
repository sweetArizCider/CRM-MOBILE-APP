package com.example.crmapp.classes

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
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

class FormularioModificarRequisicionActivity : AppCompatActivity() {

    private lateinit var editTextIdRequisicion: EditText
    private lateinit var spinnerEstado: Spinner
    private lateinit var editTextFechaAlteracion: EditText
    private lateinit var editTextCantidadServicio: EditText
    private lateinit var editTextCantidadDinero: EditText
    private lateinit var editTextServicio: EditText
    private lateinit var editTextMotivoCancelacion: EditText
    private lateinit var editTextMotivoPosposicion: EditText
    private lateinit var editTextMotivoReembolso: EditText
    private lateinit var buttonEnviar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_editar_requisicion)

        // Inicializar campos
        editTextIdRequisicion = findViewById(R.id.editTextIdRequisicion)
        spinnerEstado = findViewById(R.id.spinnerEstado)
        editTextFechaAlteracion = findViewById(R.id.editTextFechaAlteracion)
        editTextCantidadServicio = findViewById(R.id.editTextCantidadServicio)
        editTextCantidadDinero = findViewById(R.id.editTextCantidadDinero)
        editTextServicio = findViewById(R.id.editTextServicio)
        editTextMotivoCancelacion = findViewById(R.id.editTextMotivoCancelacion)
        editTextMotivoPosposicion = findViewById(R.id.editTextMotivoPosposicion)
        editTextMotivoReembolso = findViewById(R.id.editTextMotivoReembolso)
        buttonEnviar = findViewById(R.id.buttonEnviar)

        // Configurar Spinner
        val estados = listOf("Creado", "En proceso", "Enviado", "Entregado", "Cancelado", "Pospuesto", "Reembolsado")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, estados)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEstado.adapter = adapter

        // Configurar fecha de alteración por defecto
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        editTextFechaAlteracion.setText(currentDate)

        buttonEnviar.setOnClickListener {
            val idRequisicion = editTextIdRequisicion.text.toString()
            val estado = spinnerEstado.selectedItem.toString()
            val fechaAlteracion = editTextFechaAlteracion.text.toString()
            val cantidadServicio = editTextCantidadServicio.text.toString()
            val cantidadDinero = editTextCantidadDinero.text.toString()
            val servicio = editTextServicio.text.toString()
            val motivoCancelacion = editTextMotivoCancelacion.text.toString()
            val motivoPosposicion = editTextMotivoPosposicion.text.toString()
            val motivoReembolso = editTextMotivoReembolso.text.toString()

            if (idRequisicion.isEmpty() || estado.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos obligatorios.", Toast.LENGTH_SHORT).show()
            } else {
                modificarRequisicion(idRequisicion, estado, cantidadServicio, cantidadDinero, servicio, fechaAlteracion, motivoCancelacion, motivoPosposicion, motivoReembolso)
            }
        }
    }

    private fun modificarRequisicion(
        idRequisicion: String,
        estado: String,
        cantidadServicio: String,
        cantidadDinero: String,
        servicio: String,
        fechaAlteracion: String,
        motivoCancelacion: String,
        motivoPosposicion: String,
        motivoReembolso: String
    ) {
        thread {
            try {
                val url = URL("http://18.223.99.187/api/modifyRequisicion.php")
                val connection = url.openConnection() as HttpURLConnection

                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                connection.doOutput = true

                val postData = "idRequisicion=$idRequisicion&estado=$estado&cantidadServicio=$cantidadServicio&cantidadDinero=$cantidadDinero&servicio=$servicio&fechaAlteracion=$fechaAlteracion&motivoCancelacion=$motivoCancelacion&motivoPosposicion=$motivoPosposicion&motivoReembolso=$motivoReembolso"
                connection.outputStream.use { it.write(postData.toByteArray()) }

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().readText()
                    Log.d("FormularioModificarRequisicion", "Respuesta: $response")

                    runOnUiThread {
                        Toast.makeText(this, "Requisición modificada exitosamente.", Toast.LENGTH_SHORT).show()
                        finish() // Cierra la actividad después de enviar los datos
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "Error del servidor: $responseCode", Toast.LENGTH_SHORT).show()
                    }
                }

                connection.disconnect()
            } catch (e: Exception) {
                Log.e("FormularioModificarRequisicion", "Error: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
