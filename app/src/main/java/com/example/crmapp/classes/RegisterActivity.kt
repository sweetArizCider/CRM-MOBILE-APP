package com.example.crmapp.classes

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.crmapp.databinding.ActivityRegisterBinding
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    // URL de la API PHP
    private val apiUrl = "http://18.223.99.187/api/register.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar el botón de registro
        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val firstName = binding.etFirstName.text.toString()
            val lastName = binding.etLastName.text.toString()
            val company = binding.etCompany.text.toString()

            // Validar si los campos están completos
            if (email.isNotEmpty() && password.isNotEmpty() && firstName.isNotEmpty() && lastName.isNotEmpty() && company.isNotEmpty()) {
                registerUser(email, password, firstName, lastName, company)
            } else {
                Toast.makeText(this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerUser(email: String, password: String, firstName: String, lastName: String, company: String) {
        Thread {
            try {
                // Configurar la conexión HTTP
                val url = URL(apiUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                connection.doOutput = true

                // Crear los datos del cuerpo de la solicitud
                val postData = "email=$email&password=$password&nombre=$firstName&apellido=$lastName&empresa=$company"

                // Enviar los datos
                val outputStreamWriter = OutputStreamWriter(connection.outputStream)
                outputStreamWriter.write(postData)
                outputStreamWriter.flush()
                outputStreamWriter.close()

                // Leer la respuesta del servidor
                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().readText()
                    val jsonResponse = JSONObject(response)

                    val message = jsonResponse.getString("message")

                    runOnUiThread {
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        if (message.contains("exitosamente", true)) {
                            // Si el registro es exitoso, redirigir a LoginActivity
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            finish()  // Asegura que la actividad actual se cierre
                        }
                    }
                } else {
                    runOnUiThread {
                        // Manejo de error de servidor
                        Toast.makeText(this, "Error de servidor: $responseCode", Toast.LENGTH_SHORT).show()
                    }
                }
                connection.disconnect()
            } catch (e: Exception) {
                runOnUiThread {
                    // Mostrar error de conexión
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
                e.printStackTrace()
            }
        }.start()
    }
}
