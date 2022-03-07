package com.example.currencycheckertestwork.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.currencycheckertestwork.R
import com.example.currencycheckertestwork.domain.Currency
import com.example.currencycheckertestwork.util.findTV
import com.example.currencysymbols.CurrencySymbolsManager

class CommonRecyclerViewAdapter(
    private val currencySymbolsManager: CurrencySymbolsManager,
    private val clickCurrency: (currency: Currency) -> Unit
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

        private val currencyName = item.findTV(R.id.currencyName)
        private val currencyValue = item.findTV(R.id.currencyValue)
        private val currencySymbol = item.findTV(R.id.currencySymbol)
        private val addBtn = item.findViewById(R.id.addButton) as Button

        fun bind(currency: Currency) {

            currencyName.text = currency.name
            currencyValue.text = currency.value.toString()
            val currentSymbol = currencySymbolsManager.getSymbol(currency.name)
            if (currentSymbol != currency.name) {
                currencySymbol.text = currentSymbol
            } else  currencySymbol.text = ""

            when (isDeleteAction) {
                true -> addBtn.text = "-"
                false -> addBtn.text = "+"
            }
            addBtn.setOnClickListener {
                clickCurrency(currency)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Currency>() {
        override fun areItemsTheSame(old: Currency, new: Currency): Boolean =
            old === new

        override fun areContentsTheSame(old: Currency, new: Currency): Boolean =
            old.value == new.value
    }
}