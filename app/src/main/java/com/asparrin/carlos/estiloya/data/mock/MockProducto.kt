package com.asparrin.carlos.estiloya.data.mock

import com.asparrin.carlos.estiloya.data.model.Producto

object MockProducto {

    fun getListaProductos(): List<Producto> = listOf(
        Producto(1, "Polo Goku", "Polo naranja con símbolo kanji", "https://uploads.tiendada.com/public/file-storage/im/product/f87cc316-5dc3-4b37-bc99-5b8f4b937ae8.gcj03p-1714107968490", 55.0, 10, "Anime", "Polo"),
        Producto(2, "Polera Pikachu", "Polera amarilla con orejas", "https://www.pokeraymistore.pe/wp-content/uploads/2024/09/Pokemon-Polo-Deportivo-Exclusivo-Pikachu-1.jpg", 75.0, 0, "Anime", "Polera"),
        Producto(3, "Polo Marvel", "Diseño Avengers Endgame", "https://home.ripley.com.pe/Attachment/WOP_5/2016310101045/2016310101045-3.jpg", 60.0, 15, "Comics", "Polo"),
        Producto(4, "Polera Space", "Diseño espacial retro", "https://www.fuikaomar.es/44781-medium_default/camiseta-nike-lebron-james-x-space-jam-2-tune-squad.jpg", 50.0, 10, "Pelicula", "Polo"),
        Producto(5, "Polo Jungle", "Estampa de hojas verdes", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQFALhd1hA3DsRvUzDX3r7t152auAAdSbIZeA&s", 45.0, 15, "Serie", "Polo"),
        Producto(6, "Polera City", "Skyline nocturno", "https://images.puma.com/image/upload/f_auto,q_auto,b_rgb:fafafa,e_sharpen:95,w_2000,h_2000/global/775075/01/fnd/PER/fmt/png/Camiseta-Manchester-City-local-para-hombre", 60.5, 20, "Deporte", "Polo"),
        Producto(7, "Polo Stranger Things", "Logo retro de la serie", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQue1UeDFtdvb-6Vy0i5FIXimrusDKfTkQIhA&s", 58.0, 5, "Serie", "Polo"),
        Producto(8, "Polera Zelda", "Arcoíris Hyrule con Link", "https://http2.mlstatic.com/D_NQ_NP_961850-MPE84041819009_042025-O-polera-con-capucha-videojuego-zelda.webp", 68.0, 0, "Videojuegos", "Polera"),
        Producto(9, "Polo Star Wars", "Battlefront y personajes icónicos", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRojzuOOBSiaAESQcxLc3eRwCQN1EK0OJgoyw&s", 62.5, 10, "Película", "Polo"),
        Producto(10, "Polera Rolling Stones", "Lengua icónica en fondo negro", "https://http2.mlstatic.com/D_NQ_NP_642149-MPE84688369280_052025-O-polera-cuello-redondo-rolling-stone-d0049.webp", 70.0, 15, "Música", "Polera"),
    )
}
