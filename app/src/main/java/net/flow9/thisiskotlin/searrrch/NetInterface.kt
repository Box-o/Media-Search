package net.flow9.thisiskotlin.searrrch

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.QueryMap

interface NetInterface {
    @Headers("Authorization: KakaoAK 786bf2d3b93582f7a08ba0f20299da20")
    @GET("v2/search/image") //카카오 이미지
    suspend fun getSearch(@QueryMap param: HashMap<String, String>): SearchResponse
}