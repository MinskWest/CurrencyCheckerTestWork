package com.example.currencycheckertestwork.presentation.fragments

import android.os.Handler
import android.os.Looper
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.currencycheckertestwork.AppClass
import com.example.currencycheckertestwork.R
import com.example.currencycheckertestwork.constants.DataMode
import com.example.currencycheckertestwork.constants.DataMode.*
import com.example.currencycheckertestwork.constants.HANDLER_DELAY
import com.example.currencycheckertestwork.databinding.FragmentCommonBinding
import com.example.currencycheckertestwork.di.MainComponent
import com.example.currencycheckertestwork.di.ViewModelFactory
import com.example.currencycheckertestwork.domain.Currency
import com.example.currencycheckertestwork.presentation.BaseFragment
import com.example.currencycheckertestwork.presentation.adapters.CommonRecyclerViewAdapter
import com.example.currencycheckertestwork.presentation.adapters.CommonRecyclerViewAdapter.Companion.isDeleteAction
import com.example.currencycheckertestwork.presentation.viewmodels.SharedViewModel
import com.example.currencycheckertestwork.util.onClick
import com.example.currencycheckertestwork.util.setVisible
import com.example.currencysymbols.CurrencySymbolsManager
import kotlinx.android.synthetic.main.sorting_view.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class CommonFragment : BaseFragment<FragmentCommonBinding>() {

    @Inject
    lateinit var linearLayoutManager: LinearLayoutManager

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var currencySymbolsManager: CurrencySymbolsManager

    private var popularCurrencyList = mutableListOf<Currency>()
    private var favouriteCurrencyList = mutableListOf<Currency>()

    private var isSortedMode = false
    private var isFavouriteMode = false
    private var isSearchMode = false
    private var isDeleteInsertFavAction = false

    private var mainHandler = Handler(Looper.getMainLooper())
    private var reverseDelInsActionRunnable = Runnable { isDeleteInsertFavAction = false }

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[SharedViewModel::class.java]
    }

    private val currencyAdapter by lazy {
        CommonRecyclerViewAdapter(currencySymbolsManager, ::saveOrDeleteCurrency)
    }

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
        with(binding) {
            with(currencyRecyclerView) {
                layoutManager = linearLayoutManager
                adapter = currencyAdapter
            }

            popularBtn.onClick { navigateClickAction(popularCurrencyList, false) }
            favouriteBtn.onClick { navigateClickAction(favouriteCurrencyList, true) }

            sortButton.setOnClickListener {
                hideKeyboard()
                when (isSortedMode) {
                    true -> {
                        !isSortedMode
                        sortView.setVisible(false)
                    }
                    false -> {
                        !isSortedMode
                        sortView.setVisible(true)
                    }
                }
            }

            setUpSortClickListeners()

            searchTv.addTextChangedListener(onTextChanged = { text, _, _, _ ->
                isSearchMode = !text.isNullOrEmpty()
                viewModel.setSearch(text.toString(), isFavouriteMode)

            })
        }
    }

    private fun navigateClickAction(list: MutableList<Currency>, isFavouriteStateClick: Boolean) {
        isFavouriteMode = isFavouriteStateClick
        isDeleteAction = isFavouriteStateClick
        with(currencyAdapter) {
            submitList(list)
        }
        binding.currencyRecyclerView.scrollToPosition(0)
    }

    private fun setUpSortClickListeners() {
        alphabet_increase.onClick { sortClickAction(MODE_SORTED_BY_NAME) }
        alphabet_decrease.onClick { sortClickAction(MODE_SORTED_BY_NAME_VV) }
        value_increase.onClick { sortClickAction(MODE_SORTED_BY_VALUE) }
        value_decrease.onClick { sortClickAction(MODE_SORTED_BY_VALUE_VV) }
        clickBackSortingLay.onClick { binding.sortView.setVisible(false) }
    }

    private fun sortClickAction(mode: DataMode) {
        binding.sortView.setVisible(false)
        viewModel.finalListToView(mode, isFavouriteMode)
    }

    private fun saveOrDeleteCurrency(currency: Currency) {

        isDeleteInsertFavAction = true
        reverseDelInsAction()

        with(viewModel) {
            when (isFavouriteMode) {
                true -> deleteFavourite(currency.name)
                false -> insertFavourite(currency)
            }
            loadData()
        }
    }

    private fun reverseDelInsAction() =
        //reverse to false after 1s by messageQueue
        with(mainHandler) {
            removeCallbacks { reverseDelInsActionRunnable }
            postDelayed(reverseDelInsActionRunnable, HANDLER_DELAY)
        }

    override fun initObservers() {
        super.initObservers()

        with(viewModel) {
            favouriteListToView
                .onEach { state ->
                    observeListActon(favouriteCurrencyList, state)
                }.launchIn(lifecycleScope)

            sortedListToView
                .onEach { state ->
                    observeListActon(popularCurrencyList, state)
                }.launchIn(lifecycleScope)

            errorListener
                .onEach { state ->
                    if (state) showBasePopup(requireContext().resources.getString(R.string.base_error))
                }.launchIn(lifecycleScope)
        }
    }

    private fun observeListActon(list: MutableList<Currency>, stateList: List<Currency>) {
        with(list) {
            clear()
            addAll(stateList)
        }
        updateRecycler()
    }

    private fun updateRecycler() {
        when (isFavouriteMode) {
            true -> updateAction(favouriteCurrencyList)
            false -> updateAction(popularCurrencyList)
        }
    }

    private fun updateAction(newList: MutableList<Currency>) {
        with(currencyAdapter) {
            submitList(newList)
            notifyDataSetChanged()
        }
        if (!isDeleteInsertFavAction) binding.currencyRecyclerView.scrollToPosition(0)
    }
}