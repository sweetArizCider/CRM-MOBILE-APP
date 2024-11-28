import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import com.example.crmapp.R

class ClienteExpandableListAdapter(
    private val context: Context,
    private val clientes: List<Cliente>
) : BaseExpandableListAdapter() {

    override fun getGroupCount(): Int = clientes.size

    override fun getChildrenCount(groupPosition: Int): Int = 1 // Cada grupo tiene un solo elemento desplegable

    override fun getGroup(groupPosition: Int): Cliente = clientes[groupPosition]

    override fun getChild(groupPosition: Int, childPosition: Int): Cliente = clientes[groupPosition]

    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()

    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()

    override fun hasStableIds(): Boolean = true

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_cliente_group, parent, false)
        val textViewEmpresa = view.findViewById<TextView>(R.id.textEmpresa)
        val cliente = getGroup(groupPosition)
        textViewEmpresa.text = "Empresa: ${cliente.empresa}" // Mostrar "Empresa: [nombre]"
        return view
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_cliente_child, parent, false)

        val textCiudad = view.findViewById<TextView>(R.id.textCiudad)
        val textEstatus = view.findViewById<TextView>(R.id.textEstatus)
        val textPresupuesto = view.findViewById<TextView>(R.id.textPresupuesto) // Nuevo TextView para el presupuesto

        val cliente = getChild(groupPosition, childPosition)

        textCiudad.text = "Ciudad: ${cliente.ciudad}"
        textEstatus.text = "Estatus: ${cliente.estatus}"
        textPresupuesto.text = "Presupuesto: ${cliente.presupuesto}" // Asignar el presupuesto

        return view
    }
}