package com.example.amirassignment.data.remote



import com.example.amirassignment.domain.model.Product



fun ProductDto.imageUrl(): String =

    thumbnail.ifBlank { images.firstOrNull().orEmpty() }



fun ProductDto.toDomain(): Product =

    Product(

        id = id,

        title = title,

        price = price,

        imageUrl = imageUrl(),

        category = category,

        brand = brand,

        inventoryCount = stock.coerceAtLeast(0),

        description = description,

        discountPercentage = discountPercentage,

    )



fun ProductDto.toEntity(cachedAt: Long): com.example.amirassignment.data.local.ProductEntity {

    val domain = toDomain()

    return com.example.amirassignment.data.local.ProductEntity(

        id = domain.id,

        title = domain.title,

        price = domain.price,

        imageUrl = domain.imageUrl,

        category = domain.category,

        brand = domain.brand,

        displayCategory = domain.category,

        inventoryCount = domain.inventoryCount,

        description = domain.description,

        discountPercentage = domain.discountPercentage,

        cachedAt = cachedAt,

    )

}



fun com.example.amirassignment.data.local.ProductEntity.toDomain(): Product =

    Product(

        id = id,

        title = title,

        price = price,

        imageUrl = imageUrl,

        category = category,

        brand = brand,

        inventoryCount = inventoryCount,

        description = description,

        discountPercentage = discountPercentage,

    )

