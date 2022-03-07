package com.example.currencycheckertestwork.presentation.fragments

import android.os.Handler
import android.os.Looper
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
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
import com.example.currencycheckertestwork.domain.scheduler.SchedulerProvider
import com.example.currencycheckertestwork.presentation.BaseFragment
import com.example.currencycheckertestwork.presentation.adapters.CommonRecyclerViewAdapter
import com.example.currencycheckertestwork.presentation.adapters.CommonRecyclerViewAdapter.Companion.isDeleteAction
import com.example.currencycheckertestwork.presentation.viewmodels.SharedViewModel
import com.example.currencycheckertestwork.util.onClick
import com.example.currencycheckertestwork.util.setVisible
import com.example.currencysymbols.CurrencySymbolsManager
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.sorting_view.*
import javax.inject.Inject

class CommonFragment : BaseFragment<FragmentCommonBinding>() {

    @Inject
    lateinit var linearLayoutManager: LinearLayoutManager

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var currencySymbolsManager: CurrencySymbolsManager

    @Inject
    lateinit var schedulerProvider: SchedulerProvider

    private var popularCurrencyList = mutableListOf<Currency>()
    private var favouriteCurrencyList = mutableListOf<Currency>()

    private val disposable by lazy { CompositeDisposable() }

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

        disposable += viewModel.currentCurrency.subscribe()
        disposable += viewModel.favouriteCurrency.subscribe()

        disposable += viewModel
            .sortedListToView
            .observeOn(schedulerProvider.main())
            .map { listFromRoom ->
                observeListActon(popularCurrencyList, listFromRoom)
            }
            .subscribe()

        disposable += viewModel
            .favouriteListToView
            .observeOn(schedulerProvider.main())
            .map { listFromRoom ->
                observeListActon(favouriteCurrencyList, listFromRoom)
            }
            .subscribe()

        disposable += viewModel.errorListener
            .observeOn(schedulerProvider.main())
            .map { isError ->
                if (isError) showBasePopup(requireContext().resources.getString(R.string.base_error))
            }
            .subscribe()
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

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
    }
}