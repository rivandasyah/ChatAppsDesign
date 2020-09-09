package com.rivaphy.chatapps.fragment


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import com.rivaphy.chatapps.R
import com.rivaphy.chatapps.adapter.UserAdapter
import com.rivaphy.chatapps.model.Users
import kotlinx.android.synthetic.main.fragment_search.*

/**
 * A simple [Fragment] subclass.
 */
class SearchFragment : Fragment() {

    private var userAdapter: UserAdapter? = null
    private var user: List<Users>? = null //yg mau dipanggil dalam bentuk list
    private var editSearch: EditText? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        //nge-export return sebuah variable terpisah
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        rv_search.setHasFixedSize(true)
        rv_search.layoutManager = LinearLayoutManager(context)

        //setup arraylist
        user = ArrayList()
        getAllUsers()

        editSearch = view.findViewById(R.id.edt_search)
        editSearch!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchForUser(s.toString().toLowerCase())
            }

        })

        return view
    }

    private fun searchForUser(str: String) {
        //variable untuk menampung currentUser
        var firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid

        //requestnya dimasukin kesini
        val queryUser = FirebaseDatabase.getInstance().reference.child("Users")
            .orderByChild("search").startAt(str)
            .endAt(str+"\uf8ff") //port string

        queryUser.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshots: DataSnapshot) {
                (user as ArrayList<Users>).clear()
                for (snapshot in snapshots.children) {
                    val users: Users? = snapshot.getValue(Users::class.java)

                    if ((users!!.getUID()).equals(firebaseUserID)) {
                        (user as ArrayList<Users>).add(users)
                    }
                }
                userAdapter = UserAdapter(context!!, user!!, false)
                rv_search.adapter = userAdapter
            }

        })


    }

    private fun getAllUsers() {

        //mengenali uid variable yang berisi uid & variable untuk nge get table users
        var firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid //untuk mengetahui siapa login disana
        val refUsers = FirebaseDatabase.getInstance().reference.child("Users")

        //eventListener
        refUsers.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshots: DataSnapshot) {
                (user as ArrayList<Users>).clear()
                if (editSearch!!.text.toString() == "")
                    for (snapshot in snapshots.children) {
                        val users: Users? = snapshot.getValue(Users::class.java)
                        if (!(users!!.getUID()).equals(firebaseUserID)) {
                            (user as ArrayList<Users>).add(users)
                        }
                    }
                userAdapter = UserAdapter(context!!, user!!, false)
                rv_search.adapter = userAdapter
            }

        })

    }

}
