package com.example.amirassignment.data.remote



import kotlinx.serialization.Serializable



@Serializable

data class ProductsResponseDto(

    val products: List<ProductDto>,

    val total: Int,

    val skip: Int,

    val limit: Int,

)



@Serializable

data class ProductDto(

    val id: Int,

    val title: String,

    val price: Double,

    val description: String = "",

    val category: String,

    val brand: String = "",

    val thumbnail: String = "",

    val images: List<String> = emptyList(),

    val stock: Int = 0,

    val discountPercentage: Double = 0.0,

)



@Serializable

data class AnalyticsEventDto(

    val type: String,

    val productId: Int?,

    val timestamp: Long,

)



@Serializable

data class AnalyticsUploadPayload(

    val events: List<AnalyticsEventDto>,

)

