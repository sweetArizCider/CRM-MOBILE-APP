package com.example.crmapp.classes

import Contacto
import ContactoExpandableListAdapter
import com.example.crmapp.classes.FormularioContactoActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.crmapp.R
import com.example.crmapp.databinding.FragmentCuentasBinding
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread
import android.app.AlertDialog

class CuentasFragment : Fragment(R.layout.fragment_cuentas) {

    private var _binding: FragmentCuentasBinding? = null
    private val binding get() = _binding!!
    private val apiUrl = "http://18.223.99.187/api/getContactos.php" // URL de la API
    private var isAscendente = true // Variable para controlar el orden de los elementos

    private val contactosList = mutableListOf<Contacto>() // Lista de contactos
    private lateinit var adapter: ContactoExpandableListAdapter // Adaptador para el ExpandableListView
    private lateinit var btnAddContacto: ImageButton // Botón para agregar contacto

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCuentasBinding.inflate(inflater, container, false)

        // Asignar referencia al botón desde el XML
        val view = binding.root
        btnAddContacto = view.findViewById(R.id.btnAddContacto)

        // Configurar acción del botón para abrir el formulario
        btnAddContacto.setOnClickListener {
            val intent = Intent(requireContext(), FormularioContactoActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar ExpandableListView y adaptador
        val expandableListView = binding.expandableListViewCuentas // Asegúrate de que el ExpandableListView esté en tu XML
        adapter = ContactoExpandableListAdapter(requireContext(), contactosList) // Inicializa el adaptador con la lista de contactos
        expandableListView.setAdapter(adapter)

        // Cargar contactos al iniciar
        loadContactos()
    }

    private fun loadContactos() {
        thread {
            try {
                val order = if (isAscendente) "ASC" else "DESC"

                // Construir la URL con el parámetro de ordenamiento
                val url = URL("$apiUrl?order=$order")
                val connection = url.openConnection() as HttpURLConnection

                connection.requestMethod = "GET"
                connection.setRequestProperty("Content-Type", "application/json")

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().readText()
                    val jsonArray = JSONArray(response)

                    contactosList.clear() // Limpiar la lista antes de agregar nuevos datos
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val contacto = Contacto(
                            nombre = jsonObject.getString("nombre"),
                            apellido = jsonObject.getString("apellido"),
                            direccion = jsonObject.getString("direccion"),
                            correo = jsonObject.getString("correo"),
                            numero = jsonObject.getString("numero"),
                            empresa = jsonObject.getString("empresa")
                        )
                        contactosList.add(contacto)
                    }

                    // Actualizar la UI en el hilo principal
                    activity?.runOnUiThread {
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    activity?.runOnUiThread {
                        Toast.makeText(context, "Error del servidor: $responseCode", Toast.LENGTH_SHORT).show()
                    }
                }

                connection.disconnect()
            } catch (e: Exception) {
                Log.e("CuentasFragment", "Error: ${e.message}")
                activity?.runOnUiThread {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadContactos() // Recargar contactos al volver al fragmento
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Limpiar el binding para evitar fugas de memoria
    }

    // Método para mostrar el diálogo de filtrado
    private fun showFilterDialog() {
        val options = arrayOf("Ascendente", "Descendente")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Ordenar por empresa")
        builder.setItems(options) { _, which ->
            isAscendente = which == 0

            // Muestra un mensaje con la opción seleccionada
            val mensaje = if (isAscendente) "Orden Ascendente seleccionado" else "Orden Descendente seleccionado"
            Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()

            loadContactos() // Recargar los contactos con el nuevo orden
        }
        builder.show()
    }
}
