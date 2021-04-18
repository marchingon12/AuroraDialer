package com.aurora.phone.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.phone.R;
import com.aurora.phone.entity.Favourite;
import com.aurora.phone.util.ColorUtil;
import com.aurora.phone.util.ImageUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.ViewHolder> {

    private Context context;
    private List<Favourite> favouriteList = new ArrayList<>();
    private ClickListener clickListener;

    public FavouriteAdapter(Context context, ClickListener clickListener) {
        this.context = context;
        this.clickListener = clickListener;
    }

    public boolean isEmpty() {
        return favouriteList.isEmpty();
    }

    public void updateData(List<Favourite> favouriteList) {
        this.favouriteList.clear();
        this.favouriteList = favouriteList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_favourite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Favourite favourite = favouriteList.get(position);
        final int color = ImageUtil.getGradientAccentColor(position);
        holder.itemView.setBackground(ImageUtil.getGradientDrawable(position, GradientDrawable.RECTANGLE));
        holder.line1.setText(favourite.getContact().getCompositeName());
        holder.line1.setTextColor(ColorUtil.manipulateColor(color, 0.5f));
        holder.item_menu.setColorFilter(ColorUtil.manipulateColor(color, 0.5f));
        holder.item_menu.setOnClickListener(v -> clickListener.onClickItem(favourite, position));
        holder.item_menu.setOnLongClickListener(v -> {
            clickListener.onLongClickItem(favourite, position);
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return favouriteList.size();
    }

    public interface ClickListener {
        void onClickItem(@NonNull Favourite favourite, int position);

        void onLongClickItem(@NonNull Favourite favourite, int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.line1)
        TextView line1;
        @BindView(R.id.img)
        ImageView item_image;
        @BindView(R.id.item_menu)
        ImageView item_menu;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
