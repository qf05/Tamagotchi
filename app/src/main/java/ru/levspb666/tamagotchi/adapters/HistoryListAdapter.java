package ru.levspb666.tamagotchi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import ru.levspb666.tamagotchi.HistoryActivity;
import ru.levspb666.tamagotchi.R;
import ru.levspb666.tamagotchi.enums.ActionType;

public class HistoryListAdapter extends BaseExpandableListAdapter {

    //    private static HistoryListAdapter.ItemClickListener mClickListener;
    private Context context;
    private Map<Long, String> petNames;
//    private HistoryViewHolderParent viewHolderParent;

    public void setList(List<HistoryActivity.HistoryEntry> list) {
        this.list = list;
    }

    private List<HistoryActivity.HistoryEntry> list;


    public HistoryListAdapter(Context context, List<HistoryActivity.HistoryEntry> list, Map<Long, String> petNames) {
        this.context = context;
        this.list = list;
        this.petNames = petNames;
//        initCheckStates(false);
    }


//    public static class HistoryViewHolder {
//
//        //        TextView parentText;
////        View parentConvertView;
//        View childrenConvertView;
//        TextView textChildData;
//        TextView textChildName;
//        TextView textChildAction;
//
//        HistoryViewHolder(Context context) {
//            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            this.childrenConvertView = inflater.inflate(R.layout.history_child_adapter, null);
////            this.parentConvertView = inflater.inflate(R.layout.history_parent_adapter, null);
//
////            this.parentText = parentConvertView.findViewById(R.id.historyParent);
//
//            this.textChildData = childrenConvertView.findViewById(R.id.historyChildData);
//            this.textChildName = childrenConvertView.findViewById(R.id.historyChildPetName);
//            this.textChildAction = childrenConvertView.findViewById(R.id.historyAction);
//        }
//    }


    @Override
    public int getGroupCount() {
        return list.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return list.get(groupPosition).getList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return list.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return list.get(groupPosition).getList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                             ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.history_parent_adapter, null);
        }

        if (isExpanded) {
            //Изменяем что-нибудь, если текущая Group раскрыта
        } else {
            //Изменяем что-нибудь, если текущая Group скрыта
        }

        TextView textGroup = convertView.findViewById(R.id.historyParent);
        textGroup.setText(list.get(groupPosition).getParentItem());

        return convertView;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {


        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.history_child_adapter, null);
        }

        String action = "";
        switch (ActionType.valueOf(list.get(groupPosition).getList().get(childPosition).getAction())){
            case CREATE:
                action = context.getString(R.string.born);
                break;
            case EAT:
                action = context.getString(R.string.eat);
                break;
            case WALK:
                action = context.getString(R.string.walk);
                break;
            case ILL:
                action = context.getString(R.string.ill);
                break;
            case CURE:
                action = context.getString(R.string.cure);
                break;
            case SLEEP:
                action = context.getString(R.string.sleep);
                break;
            case WAKEUP:
                action = context.getString(R.string.wake_up);
                break;
            case LVLUP:
                action = context.getString(R.string.lvl_up);
                break;
            case SHIT:
                action = context.getString(R.string.shit);
                break;
            case DIE:
                action = context.getString(R.string.die);
                break;
        }

        TextView textChildData = convertView.findViewById(R.id.historyChildData);
        TextView textChildName = convertView.findViewById(R.id.historyChildPetName);
        TextView textChildAction = convertView.findViewById(R.id.historyAction);

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTimeInMillis(list.get(groupPosition).getList().get(childPosition).getDate());
        SimpleDateFormat format = new SimpleDateFormat("HH:mm dd-MM", Locale.getDefault());

        textChildData.setText(format.format(calendar.getTime()));
        textChildName.setText(petNames.get(list.get(groupPosition).getList().get(childPosition).getPetId()));
        textChildAction.setText(action);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}

