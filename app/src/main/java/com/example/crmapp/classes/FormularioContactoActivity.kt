package com.example.crmapp.classes

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.crmapp.databinding.ActivityFormularioContactoBinding
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject

class FormularioContactoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormularioContactoBinding

    private val apiUrl = "http://18.223.99.187/api/registerContacto.php" // URL de la API para contactos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormularioContactoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar el botón de guardar contacto
        binding.btnGuardarContacto.setOnClickListener {
            val nombre = binding.etNombre.text.toString() // Cambiar a `etNombre`
            val apellido = binding.etApellido.text.toString() // Cambiar a `etApellido`
            val direccion = binding.etDireccion.text.toString() // Cambiar a `etDireccion`
            val correo = binding.etCorreo.text.toString() // Cambiar a `etCorreo`
            val numero = binding.etNumero.text.toString() // Cambiar a `etNumero`
            val empresa = binding.etEmpresa.text.toString() // Cambiar a `etEmpresa`

            if (nombre.isNotEmpty() && apellido.isNotEmpty() && direccion.isNotEmpty() &&
                correo.isNotEmpty() && numero.isNotEmpty()
            ) {
                insertContact(nombre, apellido, direccion, correo, numero, empresa)
            } else {
                Toast.makeText(this, "Por favor, llena todos los campos obligatorios", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun insertContact(
        nombre: String,
        apellido: String,
        direccion: String,
        correo: String,
        numero: String,
        empresa: String
    ) {
        Thread {
            try {
                val url = URL(apiUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                connection.doOutput = true

                val postData =
                    "nombre=$nombre&apellido=$apellido&direccion=$direccion&correo=$correo&numero=$numero&empresa=$empresa"

                val outputStreamWriter = OutputStreamWriter(connection.outputStream)
                outputStreamWriter.write(postData)
                outputStreamWriter.flush()
                outputStreamWriter.close()

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().readText()
                    val jsonResponse = JSONObject(response)
                    val message = jsonResponse.getString("message")

                    runOnUiThread {
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        if (message.contains("exitosamente")) {
                            finish() // Cierra la actividad actual y vuelve a la pantalla principal
                        }
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "Error de servidor: $responseCode", Toast.LENGTH_SHORT).show()
                    }
                }
                connection.disconnect()
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }
}
