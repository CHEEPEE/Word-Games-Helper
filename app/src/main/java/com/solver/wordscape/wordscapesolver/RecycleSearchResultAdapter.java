package com.solver.wordscape.wordscapesolver;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Keji's Lab on 30/12/2017.
 */

public class RecycleSearchResultAdapter extends RecyclerView.Adapter<RecycleSearchResultAdapter.MyViewHolder> {
    ArrayList<String> searchResultItem = new ArrayList<>();


    private Context context;
    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView itemText;
        public MyViewHolder(View view){
            super(view);
            itemText = (TextView) view.findViewById(R.id.text);


        }
    }

    public RecycleSearchResultAdapter(Context c, ArrayList<String> categoryItem){
        this.searchResultItem = categoryItem;
        this.context = c;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_cointainer_row,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecycleSearchResultAdapter.MyViewHolder holder, final int position) {
       /* mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mDatabase.child(Utils.storeItemCategory).child(mAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                            CategoryMapModel categoryMapModel=dataSnapshot1.getValue(CategoryMapModel.class);
                            try {
                                if (categoryMapModel.key.equals(categoryItem.get(position).getItemCategory())) {
                                    holder.itemCategory.setText(categoryMapModel.category);
                                }

                            }catch (Exception error){
                                Log.d("Error",error.toString());
                            }


                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        ProductItemGridModel productItemGridModel = categoryItem.get(position);
        holder.itemName.setText(productItemGridModel.getiName());
        //holder.itemCategory.setText(productItemGridModel.getItemCategory());
        holder.itemText.setText(productItemGridModel.getItemPrice());
        Glide.with(context).load(productItemGridModel.getItemBannerUrl()).into(holder.banner);
        holder.ic_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickLitener.onItemClick(holder.itemView,position,"edit");

            }
        });
        holder.ic_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickLitener.onItemClick(holder.itemView,position,"delete");
            }
        });*/
       holder.itemText.setText(searchResultItem.get(position));
    }



    public interface OnItemClickLitener {
        void onItemClick(View view, int position,String text);

    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickListener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    @Override
    public int getItemCount() {
        return searchResultItem.size();
    }



}
