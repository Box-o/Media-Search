package net.flow9.thisiskotlin.searrrch

import com.google.gson.annotations.SerializedName
import java.util.Date

//data class Search(val response: SearchResponse)

// 대분류
data class SearchResponse(
    @SerializedName("meta")
    val metaData: Meta,
    @SerializedName("documents")
    val documents: MutableList<Document>
)

////메타데이터?
data class Meta(
    @SerializedName("total_count")
    val totalCount: Int,
    @SerializedName("pageable_count")
    val pageableCount: Int,
    @SerializedName("is_end")
    val isEnd: Boolean
)

//검색결과
data class Document(
    val collection: String,
    @SerializedName("thumbnail_url")
    val thumbnailUrl: String?,
    @SerializedName("image_url")
    val imageUrl: String,
    val width: Int,
    val height: Int,
    @SerializedName("display_sitename")
    val displaySiteName: String?,
    @SerializedName("doc_url")
    val docUrl: String?,
    val datetime: Date?
)

data class SearchItemModel(
    var title: String,
    var dateTime: String,
    var url: String,
    var isLike: Boolean = false
)
