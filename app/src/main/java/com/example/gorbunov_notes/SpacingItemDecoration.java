package com.example.gorbunov_notes;

import android.graphics.Rect;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SpacingItemDecoration extends RecyclerView.ItemDecoration {
    private final int verticalSpaceHeight;

    public SpacingItemDecoration(int verticalSpaceHeight) {
        this.verticalSpaceHeight = verticalSpaceHeight;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {

        int position = parent.getChildAdapterPosition(view);

        outRect.left = verticalSpaceHeight;
        outRect.right = verticalSpaceHeight;
        outRect.bottom = verticalSpaceHeight;

        if (position == 0) {
            outRect.top = verticalSpaceHeight * 2;
        }
    }
}