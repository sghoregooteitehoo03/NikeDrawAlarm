package com.nikealarm.nikedrawalarm.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.nikealarm.nikedrawalarm.data.model.entity.FavoriteEntity
import com.nikealarm.nikedrawalarm.data.model.entity.NotificationEntity
import com.nikealarm.nikedrawalarm.data.repository.dataSource.ProductPagingSource
import com.nikealarm.nikedrawalarm.data.repository.database.ProductDao
import com.nikealarm.nikedrawalarm.data.retrofit.RetrofitService
import com.nikealarm.nikedrawalarm.domain.model.ProductCategory
import com.nikealarm.nikedrawalarm.domain.model.ProductInfo
import com.nikealarm.nikedrawalarm.domain.model.getDateToLong
import com.nikealarm.nikedrawalarm.domain.model.getShoesCategory
import com.nikealarm.nikedrawalarm.util.AlarmBuilder
import com.nikealarm.nikedrawalarm.util.Constants
import kotlinx.coroutines.flow.Flow
import retrofit2.Retrofit
import javax.inject.Inject

// TODO: 데이터베이스만 관리하는 Repository 만들어서 분리하기
class ProductRepository @Inject constructor(
    private val alarmBuilder: AlarmBuilder,
    private val retrofitBuilder: Retrofit.Builder,
    private val dao: ProductDao
) {

    fun getPagingProducts(selectedCategory: ProductCategory) = Pager(
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

    suspend fun getProductInfo(productId: String): ProductInfo {
        val retrofitService = getRetrofitService()
        val productEntity = dao.getProductData(productId) ?: throw NullPointerException()
        val slug = productEntity.url.substringAfter("t/")
        val productData =
            retrofitService.getProductInfo("seoSlugs%28{slug}%29".replace("{slug}", slug))

        val productObject = productData.objects[0]
        val explains: String =
            productObject.publishedContent.nodes[0].properties.jsonBody?.content?.get(0)?.content?.filter {
                !it.text.contains("SNKRS")
            }?.get(0)?.text ?: ""

        return ProductInfo(
            productId = productId,
            title = productEntity.title,
            subTitle = productEntity.subTitle,
            price = productEntity.price,
            images = productObject.publishedContent.nodes[0].nodes!!.map { it.properties.squarishURL },
            eventDate = productEntity.eventDate,
            explains = explains,
            sizes = productObject.productInfo[0].skus?.map {
                it.countrySpecifications[0].localizedSize
            } ?: listOf(),
            url = productEntity.url,
            category = when (productEntity.category) {
                "Coming" -> ProductCategory.Coming
                "Draw" -> ProductCategory.Draw
                else -> ProductCategory.All
            }
        )
    }

    suspend fun getProductData(productId: String) =
        dao.getProductData(productId)

    fun getFavoriteData(productId: String) =
        dao.getFavoriteData(productId)

    fun getNotificationData(productId: String): Flow<NotificationEntity?> {
        return dao.getNotificationData(productId = productId)
    }

    suspend fun insertFavoriteData(productInfo: ProductInfo) {
        val productEntity = productInfo.getProductEntity()
        val favoriteEntity = FavoriteEntity(
            productId = productInfo.productId,
            favoriteDate = System.currentTimeMillis()
        )

        dao.insertProductData(productEntity)
        dao.insertFavoriteData(favoriteEntity)
    }

    // 알림 설정
    suspend fun setNotificationProduct(
        productInfo: ProductInfo,
        notificationTime: Long
    ) {
        val triggerTime = productInfo.eventDate.minus(notificationTime)

        alarmBuilder.setProductAlarm(
            triggerTime = triggerTime,
            productId = productInfo.productId
        )
        insertNotificationData(
            productInfo = productInfo,
            triggerTime = triggerTime,
            notificationTime = notificationTime
        )
    }

    suspend fun cancelNotificationProduct(
        productInfo: ProductInfo
    ) {
        alarmBuilder.cancelProductAlarm(productInfo.productId)
        deleteNotificationData(productInfo.productId)
    }

    suspend fun insertNotificationData(
        productInfo: ProductInfo,
        triggerTime: Long,
        notificationTime: Long
    ) {
        val productEntity = productInfo.getProductEntity()
        val notificationEntity = NotificationEntity(
            productId = productInfo.productId,
            triggerTime = triggerTime,
            notificationDate = notificationTime,
            addedDate = System.currentTimeMillis()
        )

        dao.insertProductData(productEntity)
        dao.insertNotificationData(notificationEntity)
    }

    suspend fun deleteFavoriteData(productId: String) {
        dao.deleteFavoriteData(productId)
    }

    suspend fun deleteNotificationData(productId: String) {
        dao.deleteNotificationData(productId)
    }

    private fun getRetrofitService() =
        retrofitBuilder.baseUrl(Constants.NIKE_API_URL)
            .build()
            .create(RetrofitService::class.java)
}