package com.nikealarm.nikedrawalarm.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.nikealarm.nikedrawalarm.data.repository.dataSource.ProductPagingSource
import com.nikealarm.nikedrawalarm.data.retrofit.RetrofitService
import com.nikealarm.nikedrawalarm.domain.model.ProductCategory
import com.nikealarm.nikedrawalarm.util.Constants
import retrofit2.Retrofit
import javax.inject.Inject


class ProductRepository @Inject constructor(
    private val retrofitBuilder: Retrofit.Builder
) {

    fun getPagingProduct(selectedCategory: ProductCategory) = Pager(
        config = PagingConfig(
            pageSize = 50
        )
    ) {
        val retrofitService = getRetrofitService()
        ProductPagingSource(
            retrofitService,
            selectedCategory
        )
    }.flow

    private fun getRetrofitService() =
        retrofitBuilder.baseUrl(Constants.NIKE_API_URL)
            .build()
            .create(RetrofitService::class.java)
}