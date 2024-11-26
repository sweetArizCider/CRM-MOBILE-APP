package com.example.crmapp.classes

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.crmapp.MainActivity
import com.example.crmapp.databinding.ActivityLoginBinding
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    // URL de la API PHP
    private val apiUrl = "http://18.223.99.187/api/login.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar el binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar el botón de login
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUsuario(email, password)
            } else {
                Toast.makeText(this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUsuario(email: String, password: String) {
        Thread {
            try {
                // Configurar la conexión HTTP
                val url = URL(apiUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                connection.doOutput = true

                // Crear los datos del cuerpo de la solicitud
                val postData = "email=$email&password=$password"
                Log.d("Login", "Sending request with data: $postData")

                // Enviar los datos
                val outputStreamWriter = OutputStreamWriter(connection.outputStream)
                outputStreamWriter.write(postData)
                outputStreamWriter.flush()
                outputStreamWriter.close()

                // Leer la respuesta del servidor
                val responseCode = connection.responseCode
                Log.d("Login", "Response code: $responseCode")

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().readText()
                    Log.d("Login", "Server Response: $response")

                    // Procesar la respuesta JSON
                    val jsonResponse = JSONObject(response)
                    val success = jsonResponse.getBoolean("success")
                    val message = jsonResponse.getString("message")

                    runOnUiThread {
                        if (success) {
                            val token = jsonResponse.getString("token")
                            Toast.makeText(this, "Login exitoso: $message", Toast.LENGTH_SHORT).show()

                            // Navegar al MainActivity
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "Error de servidor: $responseCode", Toast.LENGTH_SHORT).show()
                    }
                }
                connection.disconnect()
            } catch (e: Exception) {
                // Agregar más detalles de la excepción para depuración
                Log.e("Login", "Error: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
                e.printStackTrace()
            }
        }.start()
    }
}
