package com.example.crmapp.classes

import Cliente
import ClienteExpandableListAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.crmapp.R
import com.example.crmapp.databinding.FragmentCandidatosBinding
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread
import android.app.AlertDialog

class CandidatosFragment : Fragment(R.layout.fragment_candidatos) {

    private var _binding: FragmentCandidatosBinding? = null
    private val binding get() = _binding!!
    private val apiUrl = "http://18.223.99.187/api/getClientes.php" // URL de la API
    private var isAscendente = true  // Variable para controlar el orden de los elementos

    private val clientesList = mutableListOf<Cliente>() // Lista de clientes
    private lateinit var adapter: ClienteExpandableListAdapter // Adaptador para el ExpandableListView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCandidatosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar ExpandableListView y adaptador
        val expandableListView = binding.expandableListViewClientes // Asegúrate de que el ExpandableListView esté en tu XML
        adapter = ClienteExpandableListAdapter(requireContext(), clientesList) // Inicializa el adaptador con la lista de clientes
        expandableListView.setAdapter(adapter)

        // Acceder al botón de agregar cliente
        binding.btnAddCliente.setOnClickListener {
            // Abre el formulario de cliente
            val intent = Intent(activity, FormularioClienteActivity::class.java)
            startActivity(intent)
        }

        // Acceder al botón de filtro
        binding.btnFilter.setOnClickListener {
            showFilterDialog()
        }

        // Cargar clientes al iniciar
        loadClientes()
    }

    private fun loadClientes() {
        thread {
            try {
                val order = if (isAscendente) "ASC" else "DESC"

                // Construir la URL con el parámetro de ordenamiento
                val url = URL("$apiUrl?order=$order")
                val connection = url.openConnection() as HttpURLConnection

                connection.requestMethod = "GET"
                connection.setRequestProperty("Content-Type", "application/json")  // Asegúrate de usar el tipo de contenido correcto

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().readText()
                    val jsonArray = JSONArray(response)

                    clientesList.clear() // Limpiar la lista antes de agregar nuevos datos
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val cliente = Cliente(
                            empresa = jsonObject.getString("empresa"),
                            ciudad = jsonObject.getString("ciudad"),
                            estatus = jsonObject.getString("estatus")
                        )
                        clientesList.add(cliente)
                    }

                    // Actualizar la UI en el hilo principal
                    activity?.runOnUiThread {
                        adapter.notifyDataSetChanged() // Notificar al adaptador que la lista ha cambiado
                    }
                } else {
                    activity?.runOnUiThread {
                        Toast.makeText(context, "Error del servidor: $responseCode", Toast.LENGTH_SHORT).show()
                    }
                }

                connection.disconnect()
            } catch (e: Exception) {
                Log.e("CandidatosFragment", "Error: ${e.message}")
                activity?.runOnUiThread {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadClientes() // Recargar clientes al volver al fragmento
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Limpiar el binding para evitar fugas de memoria
    }

    // Método para mostrar el diálogo de filtrado
// Método para mostrar el diálogo de filtrado
    private fun showFilterDialog() {
        val options = arrayOf("Ascendente", "Descendente")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Ordenar por empresa")
        builder.setItems(options) { dialog, which ->
            // Si se elige "Ascendente" o "Descendente", se actualiza el orden
            isAscendente = which == 0

            // Muestra un mensaje con la opción seleccionada
            val mensaje = if (isAscendente) "Orden Ascendente seleccionado" else "Orden Descendente seleccionado"
            Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()

            loadClientes() // Recargar los clientes con el nuevo orden
        }
        builder.show()
    }

}
