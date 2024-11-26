package com.example.crmapp.classes

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.crmapp.databinding.ActivityFormularioClienteBinding
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject

class FormularioClienteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormularioClienteBinding

    private val apiUrl = "http://18.223.99.187/api/registerCliente.php" // URL actualizada

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormularioClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar botón de envío
        binding.btnSubmit.setOnClickListener {
            val empresa = binding.etEmpresa.text.toString()
            val cdMatriz = binding.etCdMatriz.text.toString()
            val presupuesto = binding.etPresupuesto.text.toString()
            val estatus = binding.etEstatus.text.toString()
            val calle = binding.etCalle.text.toString()
            val cp = binding.etCP.text.toString()
            val numExterior = binding.etNumExterior.text.toString()

            if (empresa.isNotEmpty() && cdMatriz.isNotEmpty() && presupuesto.isNotEmpty() &&
                estatus.isNotEmpty() && calle.isNotEmpty() && cp.isNotEmpty() && numExterior.isNotEmpty()
            ) {
                insertClient(empresa, cdMatriz, presupuesto, estatus, calle, cp, numExterior)
            } else {
                Toast.makeText(this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun insertClient(
        empresa: String,
        cdMatriz: String,
        presupuesto: String,
        estatus: String,
        calle: String,
        cp: String,
        numExterior: String
    ) {
        Thread {
            try {
                val url = URL(apiUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                connection.doOutput = true

                val postData =
                    "empresa=$empresa&cdMatriz=$cdMatriz&presupuesto=$presupuesto&estatus=$estatus&calle=$calle&cp=$cp&numExterior=$numExterior"

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
                            // Volver al fragmento `CandidatosFragment`
                            finish() // Cierra la actividad actual
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
