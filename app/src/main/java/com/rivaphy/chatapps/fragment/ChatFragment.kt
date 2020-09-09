package com.rivaphy.chatapps.fragment


import android.media.session.MediaSession
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId

import com.rivaphy.chatapps.R
import com.rivaphy.chatapps.adapter.UserAdapter
import com.rivaphy.chatapps.model.Chat
import com.rivaphy.chatapps.model.ChatList
import com.rivaphy.chatapps.model.Token
import com.rivaphy.chatapps.model.Users
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class ChatFragment : Fragment() {

    private var userAdapter : UserAdapter? = null
    private var mUser : List<Users>? = null
    private var usersChatList : List<ChatList>? = null
    lateinit var recyclerView: RecyclerView
    private var firebaseUser : FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        recyclerView = view.findViewById(R.id.rv_fragment)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        usersChatList = ArrayList()

        val ref = FirebaseDatabase.getInstance().reference.child("ChatList")
        ref!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                (usersChatList as ArrayList).clear()
                for (dataSnapshot in snapshot.children) {
                    val chatList = dataSnapshot.getValue(ChatList::class.java)
                    (usersChatList as ArrayList).add(chatList!!)
                }
                retrieveDataChatList()
            }
        })
        
        updateToken (FirebaseInstanceId.getInstance().token)
        return view
    }

    //setiap user punya token yang berbeda beda
    private fun updateToken(token: String?) {
        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val newToken = Token(token!!)
        ref.child(firebaseUser!!.uid).setValue(newToken)

    }

    private fun retrieveDataChatList() {
        mUser = ArrayList()

        val ref = FirebaseDatabase.getInstance().reference.child("Users")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                (mUser as ArrayList).clear()

                for (dataSnapshot in snapshot.children) {
                    val user = dataSnapshot.getValue(Users::class.java)
                    for (eachChatList in usersChatList!!) {
                        if (user!!.getUID().equals(eachChatList.getId())) {
                            (mUser as ArrayList).add(user!!)
                        }
                    }

                    userAdapter = UserAdapter(context!!,
                        (mUser as ArrayList<Users>), true)
                    recyclerView.adapter = userAdapter
                }
            }

        })
    }
}
