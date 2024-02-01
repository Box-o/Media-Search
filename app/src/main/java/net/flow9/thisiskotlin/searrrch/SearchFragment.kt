package net.flow9.thisiskotlin.searrrch

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import net.flow9.thisiskotlin.searrrch.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchAdapter: SearchAdapter
    private var items : ArrayList<Document> = ArrayList()
    private var backPressedOnce = false
    //종료 카운드 저장소

//    interface OnItemClickListener {
//        fun onItemClick(document: Document)
//    }//전달 인터페이스
//    private lateinit var itemClickListener: OnItemClickListener

    //        this.onBackPressedDispatcher.addCallback()
//        세이브에서 서치 뒤로가기, 서치에서 메인 안가고 종료 (데이터 저장 관리)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        return inflater.inflate(R.layout.fragment_search, container, false) 기본 생성
        binding = FragmentSearchBinding.inflate(inflater,container,false)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner){
            if (backPressedOnce) {
                requireActivity().finish() // 애플리케이션 종료
            } else {
                backPressedOnce = true
                Toast.makeText(requireContext(), "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()

                // 2초 후에 backPressedOnce 변수를 false로 초기화
                Handler(Looper.getMainLooper()).postDelayed({
                    backPressedOnce = false
                }, 2000)
            }
        }

        binding.ivBtnSearch.setOnClickListener {
            communicateNetWork(setUpSearchParameters(binding.etSearchBar.text.toString()))
            hideSoftKeyBoard()
        }
        binding.etSearchBar.setOnEditorActionListener { _, actionId, _ ->
            //done=enter 클릭 시 ivBtnSearch의 검색하기와 같은 동작하기 = communicateNetWork로 검색할 텍스트 전달하기
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                communicateNetWork(setUpSearchParameters(binding.etSearchBar.text.toString()))

                //done=enter 클릭 시 키보드 숨기기
                hideSoftKeyBoard()

                return@setOnEditorActionListener true
            }
            false
        }


        searchAdapter = SearchAdapter(requireContext(), items)

        val recyclerView = binding.rvSearchMain

        recyclerView.adapter = searchAdapter
        recyclerView.layoutManager = GridLayoutManager(requireContext(),2)

        return binding.root
    }




    private fun communicateNetWork(param: HashMap<String, String>) = lifecycleScope.launch() {
        val responseData = NetClient.searchNetWork.getSearch(param)

        items.clear() // 기존 데이터를 초기화
        items.addAll(responseData.documents) // 받은 데이터를 추가
        items.sortByDescending { it.datetime }
        searchAdapter.notifyDataSetChanged()

    }

    private fun setUpSearchParameters(query: String): HashMap<String, String> {
//        val apiKey = "내 키"

        return hashMapOf(
//            "Authorization" to "Authorization: KakaoAK ${apiKey}",
            "query" to query,
            "sort" to "accuracy",
            "page" to "1",
            "size" to "80"
        )
    }
    //쿼리 파라미터 헤더 나누어
    //headers 어노테이션

    private fun hideSoftKeyBoard(){
        val inputMethodManager = view?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireView().windowToken, 0)
    }


}
