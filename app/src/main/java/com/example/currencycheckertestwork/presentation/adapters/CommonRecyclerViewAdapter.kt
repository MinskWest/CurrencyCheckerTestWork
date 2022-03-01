package com.example.currencycheckertestwork.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.currencycheckertestwork.R
import com.example.currencycheckertestwork.domain.Currency

class CommonRecyclerViewAdapter(
    private val clickCurrency: (name: String) -> Unit
) : ListAdapter<Currency, CommonRecyclerViewAdapter.ItemViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val layout = R.layout.recycler_item
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        var isDeleteAction = false
    }

    inner class ItemViewHolder(item: View) : RecyclerView.ViewHolder(item) {

        private val currencyName = item.findViewById(R.id.currencyName) as TextView
        private val currencyValue = item.findViewById(R.id.currencyValue) as TextView
        private val addBtn = item.findViewById(R.id.addButton) as Button

        fun bind(currency: Currency) {

            currencyName.text = currency.name
            currencyValue.text = currency.value.toString()
            when (isDeleteAction) {
                true -> addBtn.text = "-"
                false -> addBtn.text = "+"
            }
            addBtn.setOnClickListener {
                clickCurrency(currency.name)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Currency>() {
        override fun areItemsTheSame(old: Currency, new: Currency): Boolean =
            old.value == new.value

        override fun areContentsTheSame(old: Currency, new: Currency): Boolean = true
    }
}