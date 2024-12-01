package com.example.crmapp.classes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import com.example.crmapp.R

class RequisicionExpandableListAdapter(
    private val context: Context,
    private val requisicionesList: List<Map<String, String>>
) : BaseExpandableListAdapter() {

    override fun getGroupCount(): Int {
        return requisicionesList.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return 1 // Cada grupo tiene un solo elemento hijo en este caso.
    }

    override fun getGroup(groupPosition: Int): Any {
        return requisicionesList[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return requisicionesList[groupPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val group = getGroup(groupPosition) as Map<String, String>
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_requisicion_group, parent, false)

        val textViewIdRequisicion: TextView = view.findViewById(R.id.textViewIdRequisicion)
        val textViewEmpresa: TextView = view.findViewById(R.id.textViewEmpresa)

        // Configura el texto de los TextViews
        textViewIdRequisicion.text = "ID REQUISICION: ${group["idRequisicion"]}"
        textViewEmpresa.text = "Empresa: ${group["empresa"]}"

        return view
    }


    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val child = getChild(groupPosition, childPosition) as Map<String, String>
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_requisicion_child, parent, false)

        // Asigna los valores de la requisición a los TextViews
        view.findViewById<TextView>(R.id.textViewIdRequisicion).text = "ID: ${child["idRequisicion"]}"
        view.findViewById<TextView>(R.id.textViewEmpresa).text = "Empresa: ${child["empresa"]}"
        view.findViewById<TextView>(R.id.textViewFechaCreacion).text = "Fecha de Creación: ${child["fechaCreacion"]}"
        view.findViewById<TextView>(R.id.textViewEstado).text = "Estado: ${child["estado"]}"
        view.findViewById<TextView>(R.id.textViewCantidadServicio).text = "Cantidad Servicio: ${child["cantidadServicio"]}"
        view.findViewById<TextView>(R.id.textViewCantidadDinero).text = "Cantidad Dinero: ${child["cantidadDinero"]}"
        view.findViewById<TextView>(R.id.textViewServicio).text = "Servicio: ${child["servicio"]}"
        view.findViewById<TextView>(R.id.textViewMotivoCancelacion).text = "Motivo de Cancelación: ${child["motivoCancelacion"] ?: "N/A"}"
        view.findViewById<TextView>(R.id.textViewFechaAlteracion).text = "Fecha de Alteración: ${child["fechaAlteracion"] ?: "N/A"}"

        return view
    }


    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }
}
