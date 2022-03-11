package com.example.currencycheckertestwork.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.currencycheckertestwork.R
import com.example.currencycheckertestwork.databinding.RecyclerItemBinding
import com.example.currencycheckertestwork.domain.Currency
import com.example.currencycheckertestwork.util.string
import com.example.currencysymbols.CurrencySymbolsManager

class CommonRecyclerViewAdapter(
    private val currencySymbolsManager: CurrencySymbolsManager,
    private val clickCurrency: (currency: Currency) -> Unit
) : ListAdapter<Currency, CommonRecyclerViewAdapter.ItemViewHolder>(BasicDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder =
        ItemViewHolder(
            RecyclerItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        var isDeleteAction = false
    }

    inner class ItemViewHolder(private val itemBinding: RecyclerItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(currency: Currency) {
            with(itemBinding) {
                val context = itemView.context
                currencyName.text = currency.name
                currencyValue.text = currency.value.toString()
                val currentSymbol = currencySymbolsManager.getSymbol(currency.name)
                if (currentSymbol != currency.name) {
                    currencySymbol.text = currentSymbol
                } else currencySymbol.text = ""

                when (isDeleteAction) {
                    true -> addButton.text = context.string(R.string.minus)
                    false -> addButton.text = context.string(R.string.plus)
                }
                addButton.setOnClickListener {
                    clickCurrency(currency)
                }
            }
        }

    }
}