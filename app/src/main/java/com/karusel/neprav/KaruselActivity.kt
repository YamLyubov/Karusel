package com.karusel.neprav

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DiffUtil
import com.google.gson.GsonBuilder
import com.yuyakaido.android.cardstackview.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch



class KaruselActivity : AppCompatActivity(), CardStackListener {

    private val drawerLayout by lazy { findViewById<DrawerLayout>(R.id.drawer_layout) }
    private val cardStackView by lazy { findViewById<CardStackView>(R.id.card_stack_view) }
    private val manager by lazy { NepravCardStackLayoutManager(this, this) }
    var connection = ServerConnection(this)
    var new_topics = createTopics()
    var adapter = CardStackAdapter(createTopics())



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connection.getTopics()
        setContentView(R.layout.activity_karusel)
        //установка вида и анимации карточек
        setupCardStackView()
        //функционал кнопок
        setupButton()
        //скачиваем топики
        var topics = adapter.getTopics()
//        GlobalScope.launch(Dispatchers.Main) {
//            var topics: Topics
//        }

    }


    //перетаскивание карты
    override fun onCardDragging(direction: Direction, ratio: Float) {
        Log.d("CardStackView", "onCardDragging: d = ${direction.name}, r = $ratio")
    }

    //свайп карты
    override fun onCardSwiped(direction: Direction) {
        Log.d("CardStackView", "onCardSwiped: p = ${manager.topPosition}, d = $direction")
        //вместо проверки намерения - обращение к серверу (асинх пост запрос и асинх прием ответа)
        if (direction.toString()=="Right"&& adapter.getTopics()[manager.topPosition].intent == "DISCUSS"){
            Toast.makeText(this, "New conversation", Toast.LENGTH_SHORT).show()
        }
        if (direction.toString()=="Left"&& adapter.getTopics()[manager.topPosition].intent == "ARGUE"){
            Toast.makeText(this, "New conversation", Toast.LENGTH_SHORT).show()
        }
        //подгружаем топики (в будущем адекватно)
        if (manager.topPosition == adapter.itemCount - 5) {
            paginate()

        }
    }

    //возвращение карты
    override fun onCardRewound() {
        Log.d("CardStackView", "onCardRewound: ${manager.topPosition}")
    }

    override fun onCardCanceled() {
        Log.d("CardStackView", "onCardCanceled: ${manager.topPosition}")
    }

    //появление карты
    override fun onCardAppeared(view: View, position: Int) {
        val textView = view.findViewById<TextView>(R.id.topicTextView)
        Log.d("CardStackView", "onCardAppeared: ($position) ${textView.text}")
    }

    //исчезновение карты
    override fun onCardDisappeared(view: View, position: Int) {
        val textView = view.findViewById<TextView>(R.id.topicTextView)
        Log.d("CardStackView", "onCardDisappeared: ($position) ${textView.text}")
    }



    //устанавливаем вид и  анимацию карт
    private fun setupCardStackView() {
        initialize()
    }

    //функционал кнопок
    private fun setupButton() {
        val skip = findViewById<View>(R.id.skip_button)
        skip.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Left)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
            manager.setSwipeAnimationSetting(setting)
            cardStackView.swipe()
        }

        val rewind = findViewById<View>(R.id.rewind_button)
        rewind.setOnClickListener {
            val setting = RewindAnimationSetting.Builder()
                .setDirection(Direction.Bottom)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(DecelerateInterpolator())
                .build()
            manager.setRewindAnimationSetting(setting)
            cardStackView.rewind()
        }

        val like = findViewById<View>(R.id.like_button)
        like.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Right)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
            manager.setSwipeAnimationSetting(setting)
            cardStackView.swipe()
        }
    }

    private fun initialize() {
        manager.setStackFrom(StackFrom.None)
        manager.setVisibleCount(3)
        manager.setTranslationInterval(8.0f)
        manager.setScaleInterval(0.95f)
        manager.setSwipeThreshold(0.3f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.FREEDOM)
        manager.setCanScrollHorizontal(true)
        manager.setCanScrollVertical(true)
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
        manager.setOverlayInterpolator(LinearInterpolator())
        cardStackView.layoutManager = manager
        cardStackView.adapter = adapter
        cardStackView.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }
    }


    //подгрузка топиков
    private fun paginate() {
        connection.getTopics()
        val old = adapter.getTopics()
        val new = old.plus(new_topics)
        val callback = TopicDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setTopics(new)
        result.dispatchUpdatesTo(adapter)
    }

    private fun reload() {
        val old = adapter.getTopics()
        val new = createTopics()
        val callback = TopicDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setTopics(new)
        result.dispatchUpdatesTo(adapter)
    }

    private fun addFirst(size: Int) {
        val old = adapter.getTopics()
        val new = mutableListOf<Topic>().apply {
            addAll(old)
            for (i in 0 until size) {
                add(manager.topPosition, createTopic())
            }
        }
        val callback = TopicDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setTopics(new)
        result.dispatchUpdatesTo(adapter)
    }

    private fun addLast(size: Int) {
        val old = adapter.getTopics()
        val new = mutableListOf<Topic>().apply {
            addAll(old)
            addAll(List(size) { createTopic() })
        }
        val callback = TopicDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setTopics(new)
        result.dispatchUpdatesTo(adapter)
    }

    private fun removeFirst(size: Int) {
        if (adapter.getTopics().isEmpty()) {
            return
        }

        val old = adapter.getTopics()
        val new = mutableListOf<Topic>().apply {
            addAll(old)
            for (i in 0 until size) {
                removeAt(manager.topPosition)
            }
        }
        val callback = TopicDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setTopics(new)
        result.dispatchUpdatesTo(adapter)
    }

    private fun removeLast(size: Int) {
        if (adapter.getTopics().isEmpty()) {
            return
        }

        val old = adapter.getTopics()
        val new = mutableListOf<Topic>().apply {
            addAll(old)
            for (i in 0 until size) {
                removeAt(this.size - 1)
            }
        }
        val callback = TopicDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setTopics(new)
        result.dispatchUpdatesTo(adapter)
    }

    private fun replace() {
        val old = adapter.getTopics()
        val new = mutableListOf<Topic>().apply {
            addAll(old)
            removeAt(manager.topPosition)
            add(manager.topPosition, createTopic())
        }
        adapter.setTopics(new)
        adapter.notifyItemChanged(manager.topPosition)
    }

    private fun swap() {
        val old = adapter.getTopics()
        val new = mutableListOf<Topic>().apply {
            addAll(old)
            val first = removeAt(manager.topPosition)
            val last = removeAt(this.size - 1)
            add(manager.topPosition, last)
            add(first)
        }
        val callback = TopicDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setTopics(new)
        result.dispatchUpdatesTo(adapter)
    }

    private fun createTopic(): Topic {
        return Topic(
            topic_id = 0,
            date_create = "06.05.12",
            intent = "DISCUSS",
            text = "Topics are loading",
            user_id = 111
        )
    }

    private fun createTopics(): List<Topic> {
        val body = "{'total': 100,'next_page': 101,'data':[{'id': 1,'date_create':'05.06.20', intent':'ARGUE','text':'PUTIN 407','user_id':123},{'id': 2,'date_create':'05.06.20','intent':'ARGUE','text':'PUTIN 408','user_id':124},{'id': 3,'date_create':'05.06.20','intent':'ARGUE','text':'PUTIN 409','user_id':125},{'id': 4,'date_create':'05.06.20','intent':'ARGUE','text':'PUTIN 410','user_id':126},{'id': 5,'date_create':'05.06.20','intent':'ARGUE','text':'PUTIN 411','user_id':127},{'id': 6,'date_create':'05.06.20','intent':'ARGUE','text':'PUTIN 412','user_id':128}]}"
       // connection.getTopics()
        val gson = GsonBuilder().create()
        var topics = gson.fromJson(body, Topics::class.java)
        return topics.data
    }

}

//    fun startConversation(){
//        Log.d("Karusel", "Open conversation")
//        Toast.makeText(this, "New conversation", Toast.LENGTH_SHORT).show()
//    }
//}
class Topics(val total: Int, val next_page: Int, val data: List<Topic>)

class Topic(val topic_id: Int,val date_create: String, val intent: String, val text: String, val user_id: Int)



