package com.example.fetchrewardsexercisejava.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fetchrewardsexercisejava.R;
import com.example.fetchrewardsexercisejava.model.HiringObject;

import java.util.List;

public class RecyclerViewAdapter  extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

//    private Context context;
    private List<HiringObject> hiringObjectList;

    public RecyclerViewAdapter(List<HiringObject> hiringObjectList) { //TODO:  Update w/ context?

        this.hiringObjectList = hiringObjectList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        // parse the view or the hiring object view
        //
       View view = LayoutInflater.from(viewGroup.getContext())
               .inflate(R.layout.hiring_object_info_row, viewGroup, false);

       return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        // bind the view and the data
        if (hiringObjectList != null) {
            HiringObject hiringObject = hiringObjectList.get(position); // each hiring object inside list
            viewHolder.hiringObjectId.setText(String.valueOf(hiringObject.getId()));
            viewHolder.hiringObjectName.setText(String.valueOf(hiringObject.getName()));
        } else {

            viewHolder.hiringObjectId.setText("No hiring object id");
            viewHolder.hiringObjectName.setText("No hiring object name");
        }
    }

    @Override
    public int getItemCount() {
        if (hiringObjectList != null) {
            return hiringObjectList.size();
        }

        return 0;
    }

    public void setHiringObjects(List<HiringObject> newHiringObjectList) {
        if (this.hiringObjectList == null) {
            this.hiringObjectList = newHiringObjectList;
            notifyItemRangeInserted(0, newHiringObjectList.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return hiringObjectList.size();
                }

                @Override
                public int getNewListSize() {
                    return newHiringObjectList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return hiringObjectList.get(oldItemPosition).getId() ==
                            newHiringObjectList.get(newItemPosition).getId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    HiringObject oldItem = hiringObjectList.get(oldItemPosition);
                    HiringObject newItem = newHiringObjectList.get(newItemPosition);
                    return oldItem.getName().equals(newItem.getName()) &&
                            oldItem.getListId() == newItem.getListId();
                }
            });

            this.hiringObjectList = newHiringObjectList;
            result.dispatchUpdatesTo(this);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // viewHolder construct used to get the items in the xml view row
     public TextView hiringObjectId;
      public TextView hiringObjectName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            hiringObjectId = itemView.findViewById(R.id.hiringObjectIdTextView);
            hiringObjectName = itemView.findViewById(R.id.hiringObjectNameTextView);
        }
    }
}
