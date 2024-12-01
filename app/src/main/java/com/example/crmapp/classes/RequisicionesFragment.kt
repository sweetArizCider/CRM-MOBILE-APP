package com.example.crmapp.classes

import com.example.crmapp.classes.FormularioRequisicionActivity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.crmapp.R
import com.example.crmapp.databinding.FragmentRequisicionesBinding
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread


class RequisicionesFragment : Fragment(R.layout.fragment_requisiciones) {

    private var _binding: FragmentRequisicionesBinding? = null
    private val binding get() = _binding!!
    private val apiUrl = "http://18.223.99.187/api/getRequisiciones.php" // URL de la API
    private var isAscendente = true // Variable para controlar el orden de los elementos

    private val requisicionesList = mutableListOf<Map<String, String>>() // Lista de requisiciones
    private lateinit var adapter: RequisicionExpandableListAdapter // Adaptador para el ExpandableListView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRequisicionesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar ExpandableListView y adaptador
        val expandableListView = binding.expandableListViewRequisiciones
        adapter = RequisicionExpandableListAdapter(requireContext(), requisicionesList)
        expandableListView.setAdapter(adapter)

        // Configurar los botones de acción
        binding.btnReload.setOnClickListener {
            loadRequisiciones()
        }

        binding.btnFilter.setOnClickListener {
            showFilterDialog()
        }

        binding.btnAdd.setOnClickListener {
            val intent = Intent(context, FormularioRequisicionActivity::class.java)
            startActivity(intent)
        }

        binding.btnEdit.setOnClickListener {
            val intent = Intent(context, FormularioModificarRequisicionActivity::class.java)
            startActivity(intent)
        }

        // Cargar requisiciones al iniciar
        loadRequisiciones()
    }


    private fun loadRequisiciones() {
        thread {
            try {
                val order = if (isAscendente) "ASC" else "DESC"
                val url = URL("$apiUrl?order=$order")
                val connection = url.openConnection() as HttpURLConnection

                connection.requestMethod = "GET"
                connection.setRequestProperty("Content-Type", "application/json")

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().readText()
                    val jsonArray = JSONArray(response)

                    requisicionesList.clear()
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val requisicion = mapOf(
                            "idRequisicion" to jsonObject.getString("idRequisicion"),
                            "empresa" to jsonObject.getString("empresa"),
                            "estado" to jsonObject.getString("estado"),
                            "cantidadServicio" to jsonObject.getString("cantidadServicio"),
                            "cantidadDinero" to jsonObject.getString("cantidadDinero"),
                            "fechaCreacion" to jsonObject.getString("fechaCreacion")
                        )
                        requisicionesList.add(requisicion)
                    }

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
                Log.e("RequisicionesFragment", "Error: ${e.message}")
                activity?.runOnUiThread {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showFilterDialog() {
        val options = arrayOf("Ascendente", "Descendente")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Ordenar por fecha de creación")
        builder.setItems(options) { _, which ->
            isAscendente = which == 0
            loadRequisiciones()
        }
        builder.show()
    }
}
