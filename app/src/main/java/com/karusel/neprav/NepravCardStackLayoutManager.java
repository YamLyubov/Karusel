package com.karusel.neprav;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.internal.CardStackSetting;
import com.yuyakaido.android.cardstackview.internal.CardStackState;

public class NepravCardStackLayoutManager extends CardStackLayoutManager {
    private CardStackListener listener = CardStackListener.DEFAULT;
    private CardStackSetting setting = new CardStackSetting();
    private CardStackState state = new CardStackState();

    public NepravCardStackLayoutManager(Context context) {
        super(context);
    }
    public NepravCardStackLayoutManager(Context context,CardStackListener listener) {
        super(context, listener);
    }
    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State s) {
        if (dy>0){
            return 0;
        }else{
            return super.scrollVerticallyBy(dy, recycler,s);
        }
    }
}
