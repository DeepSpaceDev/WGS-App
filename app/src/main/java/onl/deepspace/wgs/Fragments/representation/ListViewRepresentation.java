package onl.deepspace.wgs.fragments.representation;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import onl.deepspace.wgs.Helper;
import onl.deepspace.wgs.R;

public class ListViewRepresentation extends RecyclerView.Adapter<ListViewRepresentation.ViewHolder>{

    List<RepresentationItem> items;
    Context context;

    public ListViewRepresentation(Context context){
        this.context = context;
        this.items = new ArrayList<>();
    }

    public void add(RepresentationItem item){
        items.add(item);
    }

    public void reset(){
        items = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_representation_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        RepresentationItem item = items.get(position);

        //String or id
        int subject = Helper.getLongSubjectId(item.subject);
        if(subject != 0){
            holder.subject.setText(subject);
        } else {
            holder.subject.setText(item.subject);
        }

        holder.action.setText(item.action);
        holder.room.setText(item.room);
        holder.time.setText(Helper.getLongLessonId(item.time));

        holder.card.setCardBackgroundColor(ContextCompat.getColor(context, Helper.getColorId(context, item.subject)));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public final TextView subject, action, room, time;
        public final CardView card;

        public ViewHolder(View itemView) {
            super(itemView);
            subject = (TextView) itemView.findViewById(R.id.lv_rep_subject);
            action = (TextView) itemView.findViewById(R.id.lv_rep_action);
            room = (TextView) itemView.findViewById(R.id.lv_rep_room);
            time = (TextView) itemView.findViewById(R.id.lv_rep_time);

            card = (CardView) itemView.findViewById(R.id.lv_rep_card);
        }
    }
}
