package com.RB18.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.cody.rentbud.BuildConfig;
import com.example.cody.rentbud.R;
import com.RB18.sqlite.DatabaseHandler;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Cody on 3/9/2018.
 */

public class OtherPicsAdapter extends RecyclerView.Adapter<OtherPicsAdapter.ViewHolder> {
    private ArrayList<String> images;
    Context context;
    private DatabaseHandler databaseHandler;
    private OnDataChangedListener onDataChangedLisener;
    private boolean isPhotoClickEnabled;

    public OtherPicsAdapter(ArrayList<String> images, Context context) {
        this.images = images;
        this.context = context;
        this.databaseHandler = new DatabaseHandler(context);
        this.isPhotoClickEnabled = true;
    }

    public interface OnDataChangedListener {
        //void onPicDataChanged();
        void onPicSelectedToBeRemoved(String removedPicPath);
    }

    public void setOnDataChangedListener(OnDataChangedListener listener) {
        this.onDataChangedLisener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.other_pics_gallery_item, parent, false);
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
        Glide.with(context).load(image).placeholder(R.drawable.no_picture)
                .override(100, 100).centerCrop().into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPhotoClickEnabled) {
                    if (image != null) {
                        if (new File(image).exists()) {
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                                Intent intent = new Intent();
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                intent.setAction(Intent.ACTION_VIEW);
                                intent.setDataAndType(Uri.fromFile(new File(image)), "image/*");
                                context.startActivity(intent);
                            } else {
                                Intent intent = new Intent();
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                intent.setAction(Intent.ACTION_VIEW);
                                Uri photoUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".helpers.fileprovider", new File(image));
                                intent.setData(photoUri);
                                context.startActivity(intent);
                            }
                        } else {
                            Toast.makeText(context, R.string.could_not_find_file, Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });
        holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (onDataChangedLisener != null) {
                    PopupMenu popup = new PopupMenu(context, view);
                    MenuInflater inflater = popup.getMenuInflater();
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {

                                case R.id.removePic:
                                    onDataChangedLisener.onPicSelectedToBeRemoved(image);
                                    images.remove(image);
                                    OtherPicsAdapter.this.notifyDataSetChanged();
                                    return true;

                                default:
                                    return false;
                            }
                        }
                    });
                    inflater.inflate(R.menu.picture_long_click_menu, popup.getMenu());
                    popup.show();
                }

                return true;
            }
        });
        //Glide.with(context).load(image).into(holder.imageView);
    }

    public void updateResults(ArrayList<String> results) {
        images = results;

        //Triggers the list update
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public ArrayList<String> getImagePaths() {
        return images;
    }

    public void setPhotoClick(boolean isEnabled) {
        this.isPhotoClickEnabled = isEnabled;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
        }
    }

}
