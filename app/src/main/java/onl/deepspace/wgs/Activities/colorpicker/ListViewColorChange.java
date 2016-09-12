package onl.deepspace.wgs.activities.colorpicker;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import onl.deepspace.wgs.Helper;
import onl.deepspace.wgs.R;
import onl.deepspace.wgs.activities.ChangeColorActivity;

/**
 * Created by Dennis on 11.09.2016.
 */

public class ListViewColorChange extends RecyclerView.Adapter<ListViewColorChange.ViewHolder> {

    List<SubjectColorItem> items;
    ChangeColorActivity activity;

    public ListViewColorChange(ChangeColorActivity activity) {
        this.activity = activity;
        this.items = new ArrayList<>();
    }

    public void add(SubjectColorItem item) {
        items.add(item);
    }

    public void reset() {
        items = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_subject_color, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        SubjectColorItem item = items.get(position);
        final String subject = item.subject;

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.openColorPickerDialog(subject);
            }
        });

        holder.subject.setText(Helper.getLongSubjectId(subject));

        holder.color.setImageResource(Helper.getColorId(activity, subject));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView subject;
        public final CircleImageView color;

        public ViewHolder(View itemView) {
            super(itemView);
            subject = (TextView) itemView.findViewById(R.id.lv_subj_name);
            color = (CircleImageView) itemView.findViewById(R.id.lv_subj_color);
        }
    }
}