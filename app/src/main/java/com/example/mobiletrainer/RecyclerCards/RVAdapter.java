package com.example.mobiletrainer.RecyclerCards;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mobiletrainer.R;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class RVAdapter extends StatefulRecyclerView.Adapter<RVAdapter.CardViewHolder> {
    /**Адаптер для RecyclerView*/

    private Context context;
    // Лист с карточками
    private List<Card> cards;
    public int position=1;
    public RVAdapter(List<Card> cards){
        this.cards = cards;
    }

    public void setPosition(int position){
        this.position=position;
    }

    public int getPosition() {
        return position;
    }

    public void setCurrentContext(Context context) {
        this.context=context;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent,false);
        CardViewHolder cardViewHolder = new CardViewHolder(view,context);
        return cardViewHolder;
    }

    @Override
    public void onBindViewHolder(final CardViewHolder holder, int position) {
        // Устаналиваем заголовок
        holder.tv_name.setText(cards.get(position).getTitle_exercise());
        // устанавливаем строку с количеством повторений, которое надо сделать
        holder.tv_hint.setText(cards.get(position).getHint());
        // Устанавлием прогресс прогрессбара с анимацией в 1300 мс
        holder.pb_progress.setProgressWithAnimation(cards.get(position).getProgress(), (long) 1300);
        // Записываем положение карточки
        cards.get(position).setPosition(position);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(holder.getAdapterPosition());
                return false;
            }
        });
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }


    @Override
    public int getItemCount() {
        return cards.size();
    }

    public Card getItem(int position){
        return cards.get(position);
        //TODO Не используется
    }


    public static class CardViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        CardView cardView;

        TextView tv_name, tv_hint;
        CircularProgressBar pb_progress;
        Context context;


        CardViewHolder(View item, Context context) {
            super(item);
            // Инициализация UI объектов каждой карточки
            cardView = (CardView) item.findViewById(R.id.cv);

            tv_name = (TextView) item.findViewById(R.id.tv_name);
            tv_hint = (TextView) item.findViewById(R.id.tv_hint);
            pb_progress = (CircularProgressBar) item.findViewById(R.id.pb_progress);

            this.context = context;
            item.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            if(context!=null){
                //TODO: Strings -->> res strings
                menu.setHeaderTitle(context.getResources().getString(R.string.choose_action));
                menu.add(context.getResources().getString(R.string.delete));
            }
        }
    }
}
