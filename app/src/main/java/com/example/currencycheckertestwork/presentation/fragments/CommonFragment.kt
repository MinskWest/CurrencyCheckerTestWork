package com.example.currencycheckertestwork.presentation.fragments

import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.currencycheckertestwork.AppClass
import com.example.currencycheckertestwork.R
import com.example.currencycheckertestwork.constants.DataMode
import com.example.currencycheckertestwork.databinding.FragmentCommonBinding
import com.example.currencycheckertestwork.di.MainComponent
import com.example.currencycheckertestwork.di.ViewModelFactory
import com.example.currencycheckertestwork.domain.Currency
import com.example.currencycheckertestwork.presentation.BaseFragment
import com.example.currencycheckertestwork.presentation.adapters.CommonRecyclerViewAdapter
import com.example.currencycheckertestwork.presentation.adapters.CommonRecyclerViewAdapter.Companion.isDeleteAction
import com.example.currencycheckertestwork.presentation.viewmodels.SharedViewModel
import kotlinx.android.synthetic.main.fragment_common.*
import kotlinx.android.synthetic.main.sorting_view.*
import javax.inject.Inject

class CommonFragment : BaseFragment<FragmentCommonBinding>() {

    @Inject
    lateinit var linearLayoutManager: LinearLayoutManager

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private var popularCurrencyList = mutableListOf<Currency>()
    private var favouriteCurrencyList = mutableListOf<Currency>()

    private var isSortedMode = false
    private var isFavouriteMode = false
    private var isSearchMode = false

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[SharedViewModel::class.java]
    }

    private val currencyAdapter by lazy { CommonRecyclerViewAdapter(::saveOrDeleteCurrency) }

    override fun getFragmentLayoutId(): Int = R.layout.fragment_common

    private val component: MainComponent by lazy {
        (requireActivity().application as AppClass).mainComponent
    }

    override fun initView() {
        super.initView()
        component.inject(this)

        viewModel.loadData()

        setupViews()
    }

    private fun setupViews() {
        with(currencyRecyclerView) {
            layoutManager = linearLayoutManager
            adapter = currencyAdapter
        }

        popularBtn.setOnClickListener { navigateClickAction(popularCurrencyList, false) }
        favouriteBtn.setOnClickListener { navigateClickAction(favouriteCurrencyList, true) }

        sortButton.setOnClickListener {
            when (isSortedMode) {
                true -> {
                    !isSortedMode
                    sortView.visibility = View.INVISIBLE
                }
                false -> {
                    !isSortedMode
                    sortView.visibility = View.VISIBLE
                }
            }
        }

        setUpSortClickListeners()

        searchTv.addTextChangedListener(onTextChanged = { text, _, _, _ ->
            isSearchMode = !text.isNullOrEmpty()
            viewModel.setSearch(text.toString(), isFavouriteMode)
        })
    }

    private fun navigateClickAction(list: MutableList<Currency>, isFavouriteStateClick: Boolean) {
        isFavouriteMode = isFavouriteStateClick
        isDeleteAction = isFavouriteStateClick
        with(currencyAdapter) {
            submitList(list)
            notifyDataSetChanged()
        }
        currencyRecyclerView.scrollToPosition(0)
    }

    private fun setUpSortClickListeners() {
        alphabet_increase.setOnClickListener { sortClickAction(DataMode.MODE_SORTED_BY_NAME) }
        alphabet_decrease.setOnClickListener { sortClickAction(DataMode.MODE_SORTED_BY_NAME_VV) }
        value_increase.setOnClickListener { sortClickAction(DataMode.MODE_SORTED_BY_VALUE) }
        value_decrease.setOnClickListener { sortClickAction(DataMode.MODE_SORTED_BY_VALUE_VV) }
    }

    private fun sortClickAction(mode: DataMode) {
        sortView.visibility = View.INVISIBLE
        viewModel.finalListToView(mode, isFavouriteMode)
    }

    private fun saveOrDeleteCurrency(name: String) {
        when (isFavouriteMode) {
            true -> viewModel.deleteFavourite(name)
            false -> viewModel.insertFavourite(name)
        }
        viewModel.loadData()
    }


    override fun initObservers() {
        super.initObservers()

        corkCall()

        viewModel.favouriteListToView.observe(viewLifecycleOwner, { state ->
            with(favouriteCurrencyList) {
                clear()
                addAll(state)
            }
            updateRecycler()
        })
        viewModel.sortedListToView.observe(viewLifecycleOwner, { state ->
            with(popularCurrencyList) {
                clear()
                addAll(state)
            }
            updateRecycler()
            if (!isFavouriteMode) currencyAdapter.submitList(state)
        })
    }

    private fun corkCall() {
        viewModel.currencyListFromRoom.observe(viewLifecycleOwner, {})
        viewModel.favouriteCurrencyList.observe(viewLifecycleOwner, {})
    }

    private fun updateRecycler() {
        when (isFavouriteMode) {
            true -> updateAction(favouriteCurrencyList)
            false -> updateAction(popularCurrencyList)
        }
    }

    private fun updateAction(newList: MutableList<Currency>) {
        with(currencyAdapter) {
            submitList(listOf<Currency>())
            submitList(newList)
            notifyDataSetChanged()
        }
        currencyRecyclerView.scrollToPosition(0)
    }
}