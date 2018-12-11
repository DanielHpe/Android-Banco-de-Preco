package ipead.com.br.newandroidbancodepreco.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import ipead.com.br.newandroidbancodepreco.R;

/**
 * Created by daniel
 */
public class MenuAdapter extends BaseAdapter {
    private Context context;
    private List<CustomMenuItem> customItems;

    public static class CustomMenuItem {
        public String title;
        public int icon;
        public Intent intent;

        public CustomMenuItem(String t, int ic, Intent it) {
            title = t;
            icon = ic;
            intent = it;
        }
    }

    public MenuAdapter(Context c, List<CustomMenuItem> list) {
        context = c;
        customItems = list;
    }

    @Override
    public int getCount() {
        return customItems.size();
    }

    @Override
    public Object getItem(int i) {
        return customItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        View grid;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            grid = inflater.inflate(R.layout.grid_item, null);
            TextView textView = grid.findViewById(R.id.gridText);
            ImageView imageView = grid.findViewById(R.id.gridImg);
            textView.setText(customItems.get(i).title);
            imageView.setImageResource(customItems.get(i).icon);

        } else {
            grid = convertView;
        }

        return grid;
    }
}
