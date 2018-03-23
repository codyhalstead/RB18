package com.rentbud.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.cody.rentbud.R;
import com.rentbud.activities.ApartmentViewActivity;
import com.rentbud.activities.MainActivity;
import com.rentbud.fragments.ApartmentListFragment;
import com.rentbud.helpers.ImageViewDialog;
import com.rentbud.sqlite.DatabaseHandler;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Cody on 3/9/2018.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private ArrayList<String> images;
    Context context;
    private DatabaseHandler databaseHandler;

    public RecyclerViewAdapter(ArrayList<String> images, Context context) {
        this.images = images;
        this.context = context;
        this.databaseHandler = new DatabaseHandler(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pager_gallery_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //holder.position = holder.getAdapterPosition();
        final String image = images.get(position);
        //File imgFile = new File(image);
        //Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        //holder.imageView.setImageBitmap(bitmap);
        // new ImageLoaderTask(holder, position, image).execute();
        Glide.with(context)
                .load(image)
                .override(400, 400)
                .into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (image != null) {
                    ImageViewDialog ivd = new ImageViewDialog(context, image);
                    ivd.show();
                }
            }
        });
        holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                PopupMenu popup = new PopupMenu(context, view);
                MenuInflater inflater = popup.getMenuInflater();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {

                            case R.id.removePic:
                                databaseHandler.removeApartmentOtherPic(image, MainActivity.user);
                                images.remove(image);
                                RecyclerViewAdapter.this.notifyDataSetChanged();
                                return true;

                            default:
                                return false;
                        }
                    }
                });
                inflater.inflate(R.menu.picture_long_click_menu, popup.getMenu());
                popup.show();

                return true;
            }
        });
        //Glide.with(context).load(image).into(holder.imageView);
    }


    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
        }
    }

}
