import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import com.example.crmapp.R

class ContactoExpandableListAdapter(
    private val context: Context,
    private val contactos: List<Contacto>
) : BaseExpandableListAdapter() {

    override fun getGroupCount(): Int = contactos.size

    override fun getChildrenCount(groupPosition: Int): Int = 1 // Cada grupo tiene un solo elemento desplegable

    override fun getGroup(groupPosition: Int): Contacto = contactos[groupPosition]

    override fun getChild(groupPosition: Int, childPosition: Int): Contacto = contactos[groupPosition]

    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()

    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()

    override fun hasStableIds(): Boolean = true

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_contacto_group, parent, false)
        val textEmpresa = view.findViewById<TextView>(R.id.textEmpresa)
        val textNombre = view.findViewById<TextView>(R.id.textNombre)

        val contacto = getGroup(groupPosition)
        textEmpresa.text = "Contacto: "
        textNombre.text = contacto.nombre ?: "Nombre no disponible" // Evitar que se muestre "null"

        return view
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_contacto_child, parent, false)
        val textNombre = view.findViewById<TextView>(R.id.textNombre)
        val textApellido = view.findViewById<TextView>(R.id.textApellido)
        val textDireccion = view.findViewById<TextView>(R.id.textDireccion)
        val textCorreo = view.findViewById<TextView>(R.id.textCorreo)
        val textNumero = view.findViewById<TextView>(R.id.textNumero)

        val contacto = getChild(groupPosition, childPosition)
        textNombre.text = "Nombre: ${contacto.nombre}"
        textApellido.text = "Apellido: ${contacto.apellido}"
        textDireccion.text = "Dirección: ${contacto.direccion}"
        textCorreo.text = "Correo: ${contacto.correo}"
        textNumero.text = "Número: ${contacto.numero}"

        return view
    }
}
