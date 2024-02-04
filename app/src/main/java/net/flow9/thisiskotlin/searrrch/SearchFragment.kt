package net.flow9.thisiskotlin.searrrch

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.renderscript.ScriptGroup.Binding
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
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.coroutines.launch
import net.flow9.thisiskotlin.searrrch.databinding.FragmentSearchBinding
import okhttp3.internal.toImmutableMap


class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchAdapter: SearchAdapter
    private var items: ArrayList<Document> = ArrayList()
    private var backPressedOnce = false
    private lateinit var cont: Context



    override fun onAttach(context: Context) {
        super.onAttach(context)
        cont = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        /** 뒤로가기 시 프레그먼트가 닫히는 문제를 해결하기 위해 오버라이드 */
        overrideBackAction()

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {

            /** 키보드 숨김 기능은 etSearchBar가 비어있을 경우 실행되지 않도록 communicateNetWork 내부로 이동 */
            ivBtnSearch.setOnClickListener {communicateNetWork(setUpSearchParameters(etSearchBar.text.toString())) }

            /** 에뮬레이터에서는 물리 키보드 Enter 클릭시 줄바꿈 오류가 있지만 실제 기기에서 물리 키보드 연결 테스트시 정상작동하는 것을 확인함*/
            etSearchBar.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    communicateNetWork(setUpSearchParameters(etSearchBar.text.toString()))
                    return@setOnEditorActionListener true
                }
                false
            }






            searchAdapter = SearchAdapter(requireContext(), items)

            val recyclerView = rvSearchMain

            recyclerView.adapter = searchAdapter
            /** 네모 빤듯한 썸네일 사용 시 그리드 사용 */
            recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
            /** 비율 지맘대로 이미지 사용 시 스트러글 그리드 사용*/
//            recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

            etSearchBar.setText(getLastSearch(requireContext()))
        }
    }


    /** 검색창의 null 체크를 단순화 (아래 기재된 것과 같이 일단 text값을 두번 받는 방식으로 처리) */
    private fun communicateNetWork(param: HashMap<String, String>) = lifecycleScope.launch() {
        //param.isNotEmpty()로 사용해서 tempData를 두번 부르지 않게 하고 싶은데 아무리 해도 안됨
        //원인은 searchNetWork 전에 empty 확인을 해야 하는데 HashMap 이여서 빈 값을 전달해도 안에는 형식이 남아있기 때문
        val tempData = binding.etSearchBar.text.toString()

        if (tempData.isNotEmpty()) {
            val responseData = NetClient.searchNetWork.getSearch(param)
            saveLastSearch(requireContext(), tempData)
            items.clear() // 기존 데이터를 초기화
            items.addAll(responseData.documents) // 받은 데이터를 추가
            items.sortByDescending { it.datetime }
            searchAdapter.notifyDataSetChanged()
            hideSoftKeyBoard()
        } else { Toast.makeText(context, "검색어를 입력해 주세요", Toast.LENGTH_SHORT).show() }
    }





    private fun setUpSearchParameters(query: String): HashMap<String, String> {
        return hashMapOf(
            "query" to query,
            "sort" to "accuracy",
            "page" to "1",
            "size" to "80"
        )
    }

    private fun hideSoftKeyBoard() {
        val inputMethodManager =
            view?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    private fun overrideBackAction() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
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
    }

    /** getSharedPreferences에 접근하기 위해서 Context 객체가 필요함, 앱 정보 + 시스템 접근권한 으로 생각하면 될 듯
     ** Activity는 기본적으로 Context를 포함하고 있기 때문에 바로 사용이 가능해 Context인자를 받을 필요가 없다! */
    private fun saveLastSearch(context: Context, query: String) {
        val prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(Constants.PREF_KEY, query).apply()
    }

    private fun getLastSearch(context: Context): String? {
        val prefs = context.getSharedPreferences(Constants.PREFS_NAME, 0)
        return prefs.getString(Constants.PREF_KEY, null)
    }


}


/*
class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchAdapter: SearchAdapter
    private var items: ArrayList<Document> = ArrayList()
    private var backPressedOnce = false
    private lateinit var cont: Context


    override fun onAttach(context: Context) {
        super.onAttach(context)
        cont = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
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

        */
/** 가독성을 위해 binding.apply 사용 및 검색어 null 시 알림 추가 *//*

        with(binding) {
            val tempEx = etSearchBar.text.toString()

            */
/** 검색 버튼 클릭 시 *//*


            */
/** 검색 버튼 클릭 시 *//*

            ivBtnSearch.setOnClickListener {
                if (etSearchBar.text.isNotEmpty()) {
                    communicateNetWork(setUpSearchParameters(tempEx))
//                    saveLastSearch(cont, tempEx)
                    //이거랑 cont: Context랑 this@SearchFragment 같은거 아닌가?
                    hideSoftKeyBoard()
                } else {
                    Toast.makeText(context, "검색어를 입력해 주세요", Toast.LENGTH_SHORT).show()
                }
            }
            */
/** 키보드 Enter 입력 시 , 로직오류: 값 없는 경우 키보드 숨겨지지 않게 해야함*//*

            */
/** 키보드 Enter 입력 시 , 로직오류: 값 없는 경우 키보드 숨겨지지 않게 해야함*//*

            etSearchBar.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (etSearchBar.text.isNotEmpty()) {
                        communicateNetWork(setUpSearchParameters(tempEx))
//                        saveLastSearch(cont, tempEx)
                        hideSoftKeyBoard()
                        return@setOnEditorActionListener true
                    } else {
                        Toast.makeText(context, "검색어를 입력해 주세요", Toast.LENGTH_SHORT).show()
                    }
                }
                return@setOnEditorActionListener false
            }


            searchAdapter = SearchAdapter(requireContext(), items)

            val recyclerView = rvSearchMain

            recyclerView.adapter = searchAdapter
            recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)


        }
    }

    private fun communicateNetWork(param: HashMap<String, String>) = lifecycleScope.launch() {
        val responseData = NetClient.searchNetWork.getSearch(param)

        items.clear() // 기존 데이터를 초기화
        items.addAll(responseData.documents) // 받은 데이터를 추가
        items.sortByDescending { it.datetime }
        searchAdapter.notifyDataSetChanged()

    }

    private fun setUpSearchParameters(query: String): HashMap<String, String> {
        return hashMapOf(
            "query" to query,
            "sort" to "accuracy",
            "page" to "1",
            "size" to "80"
        )
    }

    private fun hideSoftKeyBoard() {
        val inputMethodManager =
            view?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    */
/** getSharedPreferences에 접근하기 위해서 Context 객체가 필요함, 앱 정보 + 시스템 접근권한 으로 생각하면 될 듯
 ** Activity는 기본적으로 Context를 포함하고 있기 때문에 바로 사용이 가능해 Context인자를 받을 필요가 없다! *//*

    private fun saveLastSearch(context: Context, query: String) {
        val prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(Constants.PREF_KEY, query).apply()
    }

    private fun getLastSearch(context: Context): String? {
        val prefs = context.getSharedPreferences(Constants.PREFS_NAME, 0)
        return prefs.getString(Constants.PREF_KEY, null)
    }

}
*/
