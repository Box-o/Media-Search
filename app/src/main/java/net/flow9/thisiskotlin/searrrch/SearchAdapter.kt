package net.flow9.thisiskotlin.searrrch

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import net.flow9.thisiskotlin.searrrch.databinding.SearchItemBinding
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class SearchAdapter(
    private val context: Context,
    private val searchedList: List<Document>
) : RecyclerView.Adapter<SearchAdapter.Holder>() {

    inner class Holder(private val binding: SearchItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val image = binding.ivThumbnail
        val title = binding.tvTitle
        val date = binding.tvDate
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = SearchItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun getItemCount(): Int {
        return searchedList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data = searchedList[position]

        holder.apply {

            Glide.with(context)
                .load(data.thumbnailUrl)
                .into(image)
//            image.setImageURI(Uri.parse(data.thumbnailUrl))
//            title.text = "출처 : ${data.displaySiteName}"
//            date.text = data.datetime.toString()

            val originalFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
            val targetFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

            try {
                val dateChanger = originalFormat.parse(data.datetime.toString())
                val formattedDate = targetFormat.format(dateChanger)
                date.text = formattedDate
            } catch (e: Exception) {
                e.printStackTrace()
            }

            //출처 사이트명 없을 때
            if (data.displaySiteName.isNullOrEmpty()) {
                val url = data.docUrl.toString()
                val pattern = if (url.contains("://www.")) {
                    Regex("(?<=://www\\.)(.*?)(?=\\.)")
                } else {
                    Regex("(?<=://)(.*?)(?=\\.)")
                }
                val matchResult = pattern.find(url)
                val extractedString = matchResult?.value
                title.text = "출처명 X : ${extractedString ?: "정보 없음"}"
            } else {
                title.text = "출처 : ${data.displaySiteName}"
            }



        }
        //클릭 이벤트
    }


}


