package com.example.currencycheckertestwork.presentation.adapters

import androidx.recyclerview.widget.DiffUtil
import com.example.currencycheckertestwork.domain.Currency

class BasicDiffUtil : DiffUtil.ItemCallback<Currency>() {
    override fun areItemsTheSame(old: Currency, new: Currency): Boolean =
        old === new

    override fun areContentsTheSame(old: Currency, new: Currency): Boolean =
        old.value == new.value
}