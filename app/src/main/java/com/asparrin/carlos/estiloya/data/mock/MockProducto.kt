package com.asparrin.carlos.estiloya.data.mock

import com.asparrin.carlos.estiloya.data.model.Producto
import com.asparrin.carlos.estiloya.data.model.Descuento

object MockProducto {

    fun getListaProductos(): List<Producto> = listOf(
        Producto(
            id = 1L,
            nombre = "Polo Goku",
            descripcion = "Polo naranja con símbolo kanji",
            imagenUrl = "https://uploads.tiendada.com/public/file-storage/im/product/f87cc316-5dc3-4b37-bc99-5b8f4b937ae8.gcj03p-1714107968490",
            precio = 55.0,
            stock = 10,
            tipo = "Polo",
            estado = "Disponible",
            categoria = "Anime",
            descuentoPorcentaje = null,
            descuento = null
        ),
        Producto(
            id = 2L,
            nombre = "Polera Pikachu",
            descripcion = "Polera amarilla con orejas",
            imagenUrl = "https://www.pokeraymistore.pe/wp-content/uploads/2024/09/Pokemon-Polo-Deportivo-Exclusivo-Pikachu-1.jpg",
            precio = 75.0,
            stock = 0,
            tipo = "Polera",
            estado = "Agotado",
            categoria = "Anime",
            descuentoPorcentaje = null,
            descuento = null
        ),
        Producto(
            id = 3L,
            nombre = "Polo Marvel",
            descripcion = "Diseño Avengers Endgame",
            imagenUrl = "https://home.ripley.com.pe/Attachment/WOP_5/2016310101045/2016310101045-3.jpg",
            precio = 60.0,
            stock = 15,
            tipo = "Polo",
            estado = "Disponible",
            categoria = "Comics",
            descuentoPorcentaje = 15,
            descuento = Descuento(1L, 15)
        ),
        Producto(
            id = 4L,
            nombre = "Polera Space",
            descripcion = "Diseño espacial retro",
            imagenUrl = "https://www.fuikaomar.es/44781-medium_default/camiseta-nike-lebron-james-x-space-jam-2-tune-squad.jpg",
            precio = 50.0,
            stock = 10,
            tipo = "Polo",
            estado = "Disponible",
            categoria = "Pelicula",
            descuentoPorcentaje = null,
            descuento = null
        ),
        Producto(
            id = 5L,
            nombre = "Polo Jungle",
            descripcion = "Estampa de hojas verdes",
            imagenUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQFALhd1hA3DsRvUzDX3r7t152auAAdSbIZeA&s",
            precio = 45.0,
            stock = 15,
            tipo = "Polo",
            estado = "Disponible",
            categoria = "Serie",
            descuentoPorcentaje = null,
            descuento = null
        )
    )
}
