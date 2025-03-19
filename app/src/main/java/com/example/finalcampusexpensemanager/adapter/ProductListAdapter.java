package com.example.finalcampusexpensemanager.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.finalcampusexpensemanager.R;
import com.example.finalcampusexpensemanager.model.ProductModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductListAdapter extends BaseAdapter {
    public List<ProductModel> products;
    public Context context;
    public ProductListAdapter(Context context, List<ProductModel> product) {
        this.context = context;
        this.products = product;
    }

    @Override
    public int getCount() {
        return products.size(); // lấy số lượng trong danh sách
    }

    @Override
    public Object getItem(int position) {
        return products.get(position); // lấy ra cá item theo vị trí trong list
    }

    @Override
    public long getItemId(int position) {
        return products.get(position).getId(); // lấy ra id sản phẩm trong list
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View viewPd;
        if (convertView == null) {
            viewPd = View.inflate(parent.getContext(), R.layout.list_item_products, null);
        } else {
            viewPd = convertView;
        }
        ProductModel pd = (ProductModel) getItem(position); // đổ dữ liệu item trong model
        ImageView imgPd = viewPd.findViewById(R.id.imgProduct);
        TextView tvName = viewPd.findViewById(R.id.tvNameProducts);
        TextView tvPrice = viewPd.findViewById(R.id.tvPriceProduct);

        tvName.setText(pd.getName());
        tvPrice.setText(String.valueOf(pd.getPrice()));
        Picasso.get().load(pd.getImage()).into(imgPd);

        return viewPd;
    }
}