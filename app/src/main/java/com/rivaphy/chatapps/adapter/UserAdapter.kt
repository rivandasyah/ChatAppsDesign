package com.rivaphy.chatapps.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rivaphy.chatapps.ChatActivity
import com.rivaphy.chatapps.R
import com.rivaphy.chatapps.VisitProfileActivity
import com.rivaphy.chatapps.model.Chat
import com.rivaphy.chatapps.model.Users
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_search.view.*

class UserAdapter(mContext: Context, mUsers: List<Users>, isChatcheck: Boolean) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    private val mContext: Context
    private val mUser: List<Users>
    private var isChatcheck: Boolean
    var lastMessage: String = ""

    init {
        this.mUser = mUsers
        this.mContext = mContext
        this.isChatcheck = isChatcheck

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.ViewHolder {
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.item_search, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUser.size
    }

    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int) {
        val users: Users = mUser[position]
        holder.username.text = users.getUsername()
        Picasso.get().load(users.getProfile()).placeholder(R.drawable.ic_profile)
            .into(holder.profile)

        //untuk check online sama offline
        //
        if (isChatcheck) {
            retrieveLastMessage(users.getUID(), holder.lastMessage)
        } else {

        }

        //buat kalo statusnya online
        if (isChatcheck) {
            if (users.getStatus() == "online") {
                holder.online.visibility = View.VISIBLE
                holder.offline.visibility = View.GONE
            }

        } else {
            holder.online.visibility = View.GONE
            holder.offline.visibility = View.VISIBLE
        }

        //buat pop up = alert dialog
        holder.itemView.setOnClickListener {
            val options = arrayOf<CharSequence>("Sent Message", "Visit Profile")
            val builder: AlertDialog.Builder = AlertDialog.Builder(mContext)

            //apa yang kita lakukan ketika itemView nya di klik
            builder.setItems(options, DialogInterface.OnClickListener { dialog, position ->
                if (position == 0) {
                    //nanti bakalan nempel kemana
                    val intent = Intent(mContext, ChatActivity::class.java)
                    intent.putExtra("visit_id", users.getUID())
                    mContext.startActivity(intent)
                }

                if (position == 1) {
                    val intent = Intent(mContext, VisitProfileActivity::class.java)
                    intent.putExtra("visit_id", users.getUID())
                    mContext.startActivity(intent)
                }
            })

            builder.show()
        }
    }

    private fun retrieveLastMessage(uid: String?, lastMessages: TextView) {
        lastMessage = "defaultMessage"
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    val chat : Chat? = dataSnapshot.getValue(Chat::class.java)

                    //?
                    if (firebaseUser != null && chat != null) {
                        if (chat.getReceiver() == firebaseUser!!.uid && 
                            chat.getSender() == uid || 
                            chat.getReceiver() == uid && 
                            chat.getSender() == firebaseUser!!.uid 
                        ) {
                            lastMessage = chat.getMessage()!!
                        }
                    }
                }
                when(lastMessage) {
                    "defaultMessage" -> lastMessages.text = mContext.getString(R.string.no_message)
                    "sent you an image" -> lastMessages.text = mContext.getString(R.string.send_message)
                }

                lastMessage = "defaultMessage"
            }

        })
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var username: TextView
        var profile: CircleImageView
        var online: CircleImageView
        var offline: CircleImageView
        var lastMessage: TextView

        init {
            username = itemView.findViewById(R.id.tv_user_search)
            profile = itemView.findViewById(R.id.iv_profile_search)
            online = itemView.findViewById(R.id.iv_online_search)
            offline = itemView.findViewById(R.id.iv_offline_search)
            lastMessage = itemView.findViewById(R.id.tv_last_message_search)
        }
    }

}