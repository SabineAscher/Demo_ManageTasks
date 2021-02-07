package uebungsblatt3.taskmaster;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    protected TaskDao dao;
    protected List <Task> tasks;
    private OnTaskListener mOnTaskListener;

    public static class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView taskTitle;
        public TextView taskDueDate;
        public ImageView taskPriority;
        OnTaskListener onTaskListener;  //innerer TaskListener ist überflüssig

        public TaskViewHolder(@NonNull View itemView, OnTaskListener onTaskListener) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.task_title);
            taskDueDate = itemView.findViewById(R.id.task_date);
            taskPriority = itemView.findViewById(R.id.task_priority);
            this.onTaskListener = onTaskListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onTaskListener.onTaskClick(getAdapterPosition());
        }
    }

    //interface weil perfomant
    public interface OnTaskListener{
        void onTaskClick(int position);
    }

    public TaskAdapter (TaskDao dao, OnTaskListener onTaskListener){
        this.dao = dao;
        this.mOnTaskListener = onTaskListener;
        loadTasks();
    }

    //nachdem Neuladen muss im Adapter die Methode notifyDataSetChanged(); aufgerufen werden
    public void loadTasks(){
        tasks = dao.getAllTasks(); //alle Tasks laden
        notifyDataSetChanged(); //updating Data in RecyclerView
    }

    // Nachdem Löschen in der Datenbank müssen die Daten im Adapter neu geladen werden
    // ansonsten laden sich die Einträge in der RecyclerView nicht neu
    public void deleteTask(Task task){
            dao.deleteTask(task);
            loadTasks();
        }

    //Liefert einen Task an einer bestimmten Position in der Liste
    public Task getTaskAtPosition(int position){
        return tasks.get(position);
    }

    /*
    ViewHolder: Container für alle sichtbaren Listeneinträge (wie bei Instagram)
    OnCreate = erzeugt die Viewholder – nur so viele wie es braucht, um den Bildschirm auszufüllen
    onBind = jedes Mal, wenn ein Holder aus dem Bildschirm „verschwindet“ wird er unten wieder angefügt und gerecycelt und neu befüllt  RecyclerView  effizient
     */

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext()); //instanziert eine layout XML file in das zugehörige View Object
        View taskitem = inflater.inflate(R.layout.task_item, parent,false); //macht es sichtbar, aber noch nicht in Recy.View hinzugefügt, soll später passieren
        //constraintLayout ist parent

        return new TaskViewHolder(taskitem, mOnTaskListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, final int position) {
        Task task = tasks.get(position);
        holder.taskTitle.setText(task.title);
        holder.taskDueDate.setText(task.getFormatedDate());

        if(task.priority == 1){
            holder.taskPriority.setImageResource(R.drawable.priority_medium);
        }

        if(task.priority == 0){
            holder.taskPriority.setImageResource(R.drawable.priority_high);
        }

        if(task.priority == 2){
            holder.taskPriority.setImageBitmap(null);
        }
        //if wenn die Priority high dann rotes Icon, bei medium gelbes Icon ansonsten leer

    }

    @Override
    public int getItemCount() {
        return tasks == null ? 0 : tasks.size();
        //if-else
        //task == null ist bedingung
        //wenn ? true dann 0
        //sonst : task.size()

    }




}
