package likelion.project.dongnation.ui.donate

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import likelion.project.dongnation.databinding.ItemSpinnerBinding

class SpinnerAdapter(context: Context, resource: Int, private val items: Array<String>) :
    ArrayAdapter<String>(context, resource, items) {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    private fun getCustomView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: ItemSpinnerBinding
        val view: View

        if (convertView == null) {
            // 뷰 바인딩 초기화
            binding = ItemSpinnerBinding.inflate(inflater, parent, false)
            view = binding.root
            view.tag = binding
        } else {
            view = convertView
            binding = view.tag as ItemSpinnerBinding
        }

        val text = binding.textViewSpinnerItemCategory

        val item = items[position]
        text.text = item

        return view
    }
}