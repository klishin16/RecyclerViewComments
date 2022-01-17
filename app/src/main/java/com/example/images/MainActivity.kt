package com.example.images

import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.images.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val commentsItemsList: MutableList<CommentsModel> = mutableListOf()

    private val pageStart: Int = 1
    private var isLoading: Boolean = false
    private var isLastPage: Boolean = false
    private var currentPage: Int = pageStart
    private val limit: Int = 5 // Page number per request

    private var requestStartTime: Long = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val serviceGenerator = ServiceGenerator.buildService(ApiService::class.java)

        binding.getCommentsBtn.setOnClickListener {
            Log.d("MY LOG", "Click")
            binding.getCommentsBtn.isGone = true

            loadComments(serviceGenerator, 0, limit) {
                binding.postsRecyclerView.apply {
                    layoutManager = LinearLayoutManager(this@MainActivity)
                    adapter = CommentItemAdapter(commentsItemsList)
                    addItemDecoration(object : RecyclerView.ItemDecoration() {
                        override fun getItemOffsets(
                            outRect: Rect,
                            view: View,
                            parent: RecyclerView,
                            state: RecyclerView.State
                        ) {
                            super.getItemOffsets(outRect, view, parent, state)
                            if (parent.getChildAdapterPosition(view) > 0) {
                                outRect.top = 14
                            }
                        }
                    })
                }

                binding.postsRecyclerView.addOnScrollListener(object :
                    PaginationScrollListener(binding.postsRecyclerView.layoutManager as LinearLayoutManager) {
                    override fun loadMoreItems() {
                        Log.d("MY LOG", "Need load more comments $currentPage")
                        loadComments(serviceGenerator, currentPage * limit, limit) {
                            currentPage++
                            binding.postsRecyclerView.adapter?.notifyItemRangeInserted(commentsItemsList.size, commentsItemsList.size + it)
                        }
                    }
                    override fun isLastPage(): Boolean {
                        return isLastPage
                    }

                    override fun isLoading(): Boolean {
                        return isLoading
                    }
                })
            }
        }
    }

    private fun loadComments(serviceGenerator: ApiService, offset: Int, limit: Int, onSuccess: (loadedItems: Int) -> Unit) {
        val call = serviceGenerator.getCommentsWithPaging(offset, limit)
        isLoading = true
        binding.loadingProgressBar.isVisible = true
        requestStartTime = System.currentTimeMillis()
        call.enqueue(object : Callback<MutableList<CommentsModel>> {
            override fun onResponse(
                call: Call<MutableList<CommentsModel>>,
                response: Response<MutableList<CommentsModel>>
            ) {
                if (response.isSuccessful) {
                    val requestEndTime = System.currentTimeMillis()
                    isLoading = false
                    binding.loadingProgressBar.isVisible = false
                    Log.d("MY LOG", "Successfully fetched")
                    val commentsBatchSize = response.body()!!.size
                    if (commentsBatchSize < limit) {
                        isLastPage = true
                    }
                    Toast.makeText(this@MainActivity, "Successfully fetched $commentsBatchSize items in ${requestEndTime - requestStartTime} milliseconds", Toast.LENGTH_SHORT).show()
                    commentsItemsList.addAll(response.body()!!)
                    onSuccess(commentsBatchSize)
                }
            }

            override fun onFailure(call: Call<MutableList<CommentsModel>>, t: Throwable) {
                isLoading = false
                Toast.makeText(this@MainActivity, "Some connection problems :(", Toast.LENGTH_SHORT).show()
            }
        })
    }
}