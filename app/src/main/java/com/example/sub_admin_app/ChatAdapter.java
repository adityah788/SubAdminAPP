package com.example.sub_admin_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_SENDER = 1;
    private static final int TYPE_RECEIVER = 2;

    private List<ChatMessage> messages;

    public ChatAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        // Check if message is from subadmin (sender) or admin (receiver)
        // Add null safety check
        if (position < messages.size() && messages.get(position) != null) {
            return messages.get(position).isSender() ? TYPE_SENDER : TYPE_RECEIVER;
        }
        return TYPE_RECEIVER; // Default to receiver if message is null
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_SENDER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.m_sent, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.m_receive, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position >= messages.size()) return;
        
        ChatMessage msg = messages.get(position);
        if (msg == null) return;

        if (holder instanceof SenderViewHolder) {
            // Message from subadmin
            SenderViewHolder senderHolder = (SenderViewHolder) holder;
            if (msg.getMessage() != null) senderHolder.tvMessage.setText(msg.getMessage());
            if (msg.getTime() != null) senderHolder.tvTime.setText(msg.getTime());
            if (msg.getUsername() != null) senderHolder.tvUser.setText(msg.getUsername());
        } else if (holder instanceof ReceiverViewHolder) {
            // Message from admin
            ReceiverViewHolder receiverHolder = (ReceiverViewHolder) holder;
            if (msg.getMessage() != null) receiverHolder.tvMessage.setText(msg.getMessage());
            if (msg.getTime() != null) receiverHolder.tvTime.setText(msg.getTime());
            
            // Show admin name if available, otherwise fallback to username
            String displayName = "Admin"; // Default name
            if (msg.getAdminName() != null && !msg.getAdminName().isEmpty()) {
                displayName = msg.getAdminName();
            } else if (msg.getUsername() != null && !msg.getUsername().isEmpty()) {
                displayName = msg.getUsername();
            }
            receiverHolder.tvUser.setText(displayName);
            
            // TODO: Load admin profile picture using Glide if adminProfilePic URL is available
            // For now, using default user icon
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTime, tvUser;

        SenderViewHolder(View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tv_message);
            tvTime = itemView.findViewById(R.id.tvtime);
            tvUser = itemView.findViewById(R.id.tvuser);
        }
    }

    static class ReceiverViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTime, tvUser;

        ReceiverViewHolder(View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tv_message);
            tvTime = itemView.findViewById(R.id.tvtime);
            tvUser = itemView.findViewById(R.id.tvuser);
        }
    }
}

