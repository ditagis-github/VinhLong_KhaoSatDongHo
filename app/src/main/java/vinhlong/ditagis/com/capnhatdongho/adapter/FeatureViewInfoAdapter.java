package vinhlong.ditagis.com.capnhatdongho.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.esri.arcgisruntime.data.Field;

import java.util.List;

import vinhlong.ditagis.com.capnhatdongho.R;


/**
 * Created by ThanLe on 04/10/2017.
 */

public class FeatureViewInfoAdapter extends ArrayAdapter<FeatureViewInfoAdapter.Item> {
    private Context mContext;
    private List<Item> items;

    public FeatureViewInfoAdapter(Context context, List<Item> items) {
        super(context, 0, items);
        this.mContext = context;
        this.items = items;
    }

    public List<Item> getItems() {
        return items;
    }

    public void clear() {
        items.clear();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_viewinfo, null);
        }
        Item item = items.get(position);

        TextView txtAlias = (TextView) convertView.findViewById(R.id.txt_viewinfo_alias);
        //todo
        txtAlias.setText(item.getAlias());

        TextView txtValue = (TextView) convertView.findViewById(R.id.txt_viewinfo_value);
        //todo
        txtValue.setText(item.getValue());
        if (item.getValue() == null)
            txtValue.setVisibility(View.GONE);
        else
            txtValue.setVisibility(View.VISIBLE);
        return convertView;
    }


    public static class Item {
        private String alias;
        private String value;
        private String fieldName;
        private boolean isEdit;
        private Field.Type fieldType;

        public Item() {
        }


        public Field.Type getFieldType() {
            return fieldType;
        }

        public void setFieldType(Field.Type fieldType) {
            this.fieldType = fieldType;
        }

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public boolean isEdit() {
            return isEdit;
        }

        public void setEdit(boolean edit) {
            isEdit = edit;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
