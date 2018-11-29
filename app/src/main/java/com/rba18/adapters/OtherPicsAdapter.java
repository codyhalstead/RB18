package com.rba18.adapters;

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
import com.rba18.BuildConfig;
import com.rba18.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Cody on 3/9/2018.
 */

public class OtherPicsAdapter extends RecyclerView.Adapter<OtherPicsAdapter.ViewHolder> {
    private ArrayList<String> mImages;
    private Context mContext;
    private OnDataChangedListener mOnDataChangedListener;
    private boolean mIsPhotoClickEnabled;

    public OtherPicsAdapter(ArrayList<String> images, Context context) {
        mImages = images;
        mContext = context;
        mIsPhotoClickEnabled = true;
    }

    public interface OnDataChangedListener {
        void onPicSelectedToBeRemoved(String removedPicPath);
    }

    public void setOnDataChangedListener(OnDataChangedListener listener) {
        mOnDataChangedListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.other_pics_gallery_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final String image = mImages.get(position);
        Glide.with(mContext).load(image).placeholder(R.drawable.no_picture)
                .override(100, 100).centerCrop().into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsPhotoClickEnabled) {
                    if (image != null) {
                        if (new File(image).exists()) {
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                                Intent intent = new Intent();
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                intent.setAction(Intent.ACTION_VIEW);
                                intent.setDataAndType(Uri.fromFile(new File(image)), "image/*");
                                mContext.startActivity(intent);
                            } else {
                                Intent intent = new Intent();
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                intent.setAction(Intent.ACTION_VIEW);
                                Uri photoUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".helpers.fileprovider", new File(image));
                                intent.setData(photoUri);
                                mContext.startActivity(intent);
                            }
                        } else {
                            Toast.makeText(mContext, R.string.could_not_find_file, Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });
        holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mOnDataChangedListener != null) {
                    PopupMenu popup = new PopupMenu(mContext, view);
                    MenuInflater inflater = popup.getMenuInflater();
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {

                                case R.id.removePic:
                                    mOnDataChangedListener.onPicSelectedToBeRemoved(image);
                                    mImages.remove(image);
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
    }

    public void updateResults(ArrayList<String> results) {
        mImages = results;

        //Triggers the list update
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mImages.size();
    }

    public ArrayList<String> getImagePaths() {
        return mImages;
    }

    public void setPhotoClick(boolean isEnabled) {
        mIsPhotoClickEnabled = isEnabled;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
        }
    }

}
