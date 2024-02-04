package net.flow9.thisiskotlin.searrrch

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import net.flow9.thisiskotlin.searrrch.Constants.PREFS_NAME
import net.flow9.thisiskotlin.searrrch.Constants.PREF_KEY
import net.flow9.thisiskotlin.searrrch.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    var likedItems: ArrayList<SearchItemModel> = ArrayList()

    @SuppressLint("InternalInsetResource", "DiscouragedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setFragment(SearchFragment())

        //스테이터스바 무시하고 뷰를 화면 최상단으로 배치 + 아이템 색상 검정
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        //상단 배치로 가려진 내용 보기 위해 + 시스템 색상 스테이터스바 투명화
        window.statusBarColor = Color.TRANSPARENT

        //시스템의 스테이터스바 크기를 가져오고 그만큼 padding을 준다
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            val statusBarHeight = resources.getDimensionPixelSize(resourceId)
            binding.flMain.setPadding(0, statusBarHeight + 15, 0, 0)
            //binding.statusBar.layoutParams.height = statusBarHeight+15 //메인에서 바로쓰면 좋으나 프레그먼트쓰면 두번 벌어져서 레이아웃 안 맞음
        }


        binding.cvBtnFrag1.setOnClickListener { moveFrag1() }
        //binding apply 사용 context this@ 위치를 지정해 주어야 함
        binding.cvBtnFrag2.setOnClickListener {moveFrag2()}
        //여기까지 프레그먼트 전환!!!!


    } // End of onCreate!!!!!

    private fun setFragment(frag: Fragment) {
        supportFragmentManager.commit {
            replace(binding.flMain.id, frag)
            setReorderingAllowed(true)
            addToBackStack(null)
        }
    }

    fun moveFrag1() {
        if (binding.cvBtnFrag1.alpha == 0.5f) {
//                Toast.makeText(this, "좌 버튼 테스트", Toast.LENGTH_SHORT).show()
//                binding.cvBtnFrag1.foreground = ContextCompat.getDrawable(this, R.drawable.apply_ripple_effect)
            val fadeIn = ObjectAnimator.ofFloat(binding.cvBtnFrag1, "alpha", 0.5f, 1f).apply {
                duration = 200
                start()
            }
            val fadeout = ObjectAnimator.ofFloat(binding.cvBtnFrag2, "alpha", 1f, 0.5f).apply {
                duration = 200
                start()
            }
            setFragment(SearchFragment())
        }
    }

    private fun moveFrag2() {
//                Toast.makeText(this@MainActivity, "우 버튼 테스트", Toast.LENGTH_SHORT).show()
//                binding.cvBtnFrag1.foreground = ContextCompat.getDrawable(this, R.drawable.apply_ripple_effect)
        if (binding.cvBtnFrag2.alpha == 0.5f) {
            val fadeIn = ObjectAnimator.ofFloat(binding.cvBtnFrag1, "alpha", 1f, 0.5f).apply {
                duration = 200
                start()
            }
            val fadeout = ObjectAnimator.ofFloat(binding.cvBtnFrag2, "alpha", 0.5f, 1f).apply {
                duration = 200
                start()
            }
        }
        setFragment(SavedFragment())
    }

    fun addLikedItem(item: SearchItemModel) {
        if(!likedItems.contains(item)) {
            likedItems.add(item)
        }
    }

    fun removeLikedItem(item: SearchItemModel) {
        likedItems.remove(item)
    }


} // End of MainActivity class!!!!!