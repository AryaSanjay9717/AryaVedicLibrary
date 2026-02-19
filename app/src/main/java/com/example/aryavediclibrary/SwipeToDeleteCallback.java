package com.example.aryavediclibrary;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public abstract class SwipeToDeleteCallback
        extends ItemTouchHelper.SimpleCallback {

    private final Drawable deleteIcon;
    private final Paint backgroundPaint;

    public SwipeToDeleteCallback(Context context) {
        super(0, ItemTouchHelper.LEFT); // ⬅️ sirf LEFT swipe
        deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete);
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.parseColor("#E53935")); // red
    }

    // ✅ REQUIRED METHOD (ERROR FIX)
    @Override
    public boolean onMove(
            @NonNull RecyclerView recyclerView,
            @NonNull RecyclerView.ViewHolder viewHolder,
            @NonNull RecyclerView.ViewHolder target
    ) {
        return false; // drag & drop nahi chahiye
    }

    @Override
    public void onChildDraw(
            @NonNull Canvas canvas,
            @NonNull RecyclerView recyclerView,
            @NonNull RecyclerView.ViewHolder viewHolder,
            float dX,
            float dY,
            int actionState,
            boolean isCurrentlyActive
    ) {

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

            int itemTop = viewHolder.itemView.getTop();
            int itemBottom = viewHolder.itemView.getBottom();
            int itemRight = viewHolder.itemView.getRight();
            int itemLeft = viewHolder.itemView.getLeft();

            // 🔴 Red background
            canvas.drawRect(
                    itemRight + dX,
                    itemTop,
                    itemRight,
                    itemBottom,
                    backgroundPaint
            );

            // 🗑 Delete icon
            if (deleteIcon != null) {
                int iconMargin =
                        (itemBottom - itemTop - deleteIcon.getIntrinsicHeight()) / 2;

                int iconTop = itemTop + iconMargin;
                int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();

                int iconLeft = itemRight - iconMargin - deleteIcon.getIntrinsicWidth();
                int iconRight = itemRight - iconMargin;

                deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                deleteIcon.draw(canvas);
            }
        }

        super.onChildDraw(
                canvas,
                recyclerView,
                viewHolder,
                dX,
                dY,
                actionState,
                isCurrentlyActive
        );
    }
}
