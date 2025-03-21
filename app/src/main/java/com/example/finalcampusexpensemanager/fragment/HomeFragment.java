package com.example.finalcampusexpensemanager.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.finalcampusexpensemanager.R;
import com.example.finalcampusexpensemanager.adapter.ProductListAdapter;
import com.example.finalcampusexpensemanager.model.ProductModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ListView lvProduct = view.findViewById(R.id.lvProductList);
        List<ProductModel> productModels = new ArrayList<>();
        productModels.add(new ProductModel(1,"Iphone 16 Pro max",20000000,"https://cdn.tgdd.vn/Products/Images/1363/314738/mieng-dan-kinh-cuong-luc-iphone-15-pro-jcpal-thumb-600x600.jpg"));
        productModels.add(new ProductModel(2,"Iphone 13 Pro max",40000000,"https://cdn.tgdd.vn/Products/Images/1363/314738/mieng-dan-kinh-cuong-luc-iphone-15-pro-jcpal-thumb-600x600.jpg"));
        productModels.add(new ProductModel(3,"Iphone 18 Pro max",70000000,"https://cdn.tgdd.vn/Products/Images/1363/314738/mieng-dan-kinh-cuong-luc-iphone-15-pro-jcpal-thumb-600x600.jpg"));
        productModels.add(new ProductModel(4,"Iphone 12 Pro max",40000000,"https://cdn.tgdd.vn/Products/Images/1363/314738/mieng-dan-kinh-cuong-luc-iphone-15-pro-jcpal-thumb-600x600.jpg"));

        ProductListAdapter adapter = new ProductListAdapter(getContext(),productModels);
        lvProduct.setAdapter(adapter);

        lvProduct.setClickable(true);
        lvProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProductModel pd = (ProductModel) lvProduct.getItemAtPosition(position);
                String name = pd.getName();
                int price = pd.getPrice();
                Toast.makeText(getContext(), name + " - " + price, Toast.LENGTH_SHORT).show();

            }
        });

        return view;
    }
}