package com.apps.ifaldyprayanda.todoapp;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {

    private ArrayList<Todo> listTodo = new ArrayList<>();
    private Activity activity;

    public TodoAdapter(Activity activity) {
        this.activity = activity;
    }

    public ArrayList<Todo> getListTodo() {
        return listTodo;
    }

    public void setListTodo(ArrayList<Todo> listTodo)
    {
        if(listTodo.size() > 0)
        {
            this.listTodo.clear();
        }
        this.listTodo.addAll(listTodo);
        notifyDataSetChanged();
    }

    public void addItem(Todo todo) {
        this.listTodo.add(todo);
        notifyItemInserted(listTodo.size() - 1);
    }

    public void updateItem(int position, Todo todo) {
        this.listTodo.set(position, todo);
        notifyItemChanged(position, todo);
    }

    public void removeItem(int position) {
        this.listTodo.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,listTodo.size());
    }

    @NonNull
    @Override
    public TodoAdapter.TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todo, parent, false);
        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoAdapter.TodoViewHolder holder, int position) {
        holder.tvTitle.setText(listTodo.get(position).getTitle());
        holder.tvDate.setText(listTodo.get(position).getDate());
        holder.tvDescription.setText(listTodo.get(position).getDescription());
        holder.cvTodo.setOnClickListener(new CustomOnItemClickListener(position, new CustomOnItemClickListener.OnItemClickCallback() {
            @Override
            public void onItemClicked(View view, int position) {
                Intent intent = new Intent(activity, TodoAddUpdateActivity.class);
                intent.putExtra(TodoAddUpdateActivity.EXTRA_POSITION, position);
                intent.putExtra(TodoAddUpdateActivity.EXTRA_TODO, listTodo.get(position));
                activity.startActivityForResult(intent, TodoAddUpdateActivity.REQUEST_UPDATE);
            }
        }));
    }

    @Override
    public int getItemCount() {
        return listTodo.size();
    }

    public class TodoViewHolder extends RecyclerView.ViewHolder {
        final TextView tvTitle, tvDescription, tvDate;
        final CardView cvTodo;
        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_item_title);
            tvDescription = itemView.findViewById(R.id.tv_item_description);
            tvDate = itemView.findViewById(R.id.tv_item_date);
            cvTodo = itemView.findViewById(R.id.cv_item_note);
        }
    }
}
