package net.flow9.thisiskotlin.searrrch

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import net.flow9.thisiskotlin.searrrch.databinding.DialogPreviewBinding
import net.flow9.thisiskotlin.searrrch.databinding.SearchItemBinding
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlin.math.max

class SearchAdapter(
    private val context: Context,
    private val searchedList: List<Document>
) : RecyclerView.Adapter<SearchAdapter.Holder>() {


    inner class Holder(private val binding: SearchItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val image = binding.ivThumbnail
        val title = binding.tvTitle
        val date = binding.tvDate

        val card = binding.cvThumbnail

        val book = binding.ivBookmark
        val layoutItem = binding.cvItem
        var savedItems = ArrayList<SearchItemModel>()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = SearchItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }



    override fun getItemCount(): Int {
        return searchedList.size
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data = searchedList[position]

        holder.apply {

            Glide.with(context)
                .load(data.thumbnailUrl)
                .into(image)


            val originalFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
            val targetFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

            try {
                val dateChanger = originalFormat.parse(data.datetime.toString())
                val formattedDate = targetFormat.format(dateChanger)
//                val formattedDate = dateChanger?.let { targetFormat.format(it) } 검색해 볼 것!
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

//            card.setOnLongClickListener { view ->
//                val location = IntArray(2)
//                view.getLocationOnScreen(location)
//                val x = location[0] + view.width / 2 // 클릭된 위치의 x 좌표
//                val y = location[1] + view.height / 2 // 클릭된 위치의 y 좌표
//                // 클릭된 위치를 사용하여 원하는 작업 수행
//                true // 이벤트가 소비되었음을 나타내기 위해 true를 반환합니다.
//            }

            card.setOnLongClickListener { view ->
                val imageUrl = data.imageUrl
                if (!imageUrl.isNullOrEmpty()) {
                    val dialog = Dialog(context).apply { setContentView(R.layout.dialog_preview) }
                    val imageView = dialog.findViewById<ImageView>(R.id.iv_dialog)

                    Glide.with(context)
                        .load(imageUrl)
                        .into(imageView)

                    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                    val display = windowManager.defaultDisplay
                    val size = Point()
                    display.getSize(size)

                    // 다이얼로그의 크기 설정
                    val layoutParams = WindowManager.LayoutParams()
                    dialog.window?.setBackgroundDrawableResource(R.drawable.apply_edit_text_focus_style)
                    layoutParams.copyFrom(dialog.window?.attributes)
                    layoutParams.width = (size.x * 0.9).toInt() //배율
//                    layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
//                    val maxHeight = (size.y * 0.9).toInt()
//                    layoutParams.height = if (layoutParams.height > maxHeight) maxHeight else layoutParams.height

                    dialog.window?.attributes = layoutParams

                    dialog.show()
                    if (dialog.isShowing){view.parent.requestDisallowInterceptTouchEvent(true)}

                    view.setOnTouchListener { _, event ->
                        if (event.action == MotionEvent.ACTION_UP) {
                            dialog.dismiss() // 다이얼로그를 닫음
                            true // 터치 이벤트 소비
                        }
                        if (event.action == MotionEvent.ACTION_DOWN) {
                            false
                        } else {
                            false // 다른 이벤트는 처리하지 않음
                        }
                    }
                } else {
                    // 이미지 URL이 없는 경우 처리
                }
                true // 롱클릭 이벤트를 소비함
            }
            /** 롱 클릭 이벤트 때문에 따로 처리해야함 layoutItem 트리거로 사용 */
            card.setOnClickListener {
                layoutItem.performClick()
            }

            layoutItem.setOnClickListener {
//                val position = adapterPosition.takeIf { it != RecyclerView.NO_POSITION } ?: return@setOnClickListener
                val item = savedItems[position]
                item.title = data.displaySiteName.toString()

                item.isLike = !item.isLike

                if (item.isLike){
                    (context as MainActivity).addLikedItem(item)
                }else{
                    (context as MainActivity).removeLikedItem(item)
                }
                notifyItemChanged(position)
            }



        }
        //클릭 이벤트
    }

}


