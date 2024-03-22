package helloandroid.ut3.chauvequipeut;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ViewHolder> {

    private List<Score> itemList;
    private Context context;

    public ScoreAdapter(Context context, List<Score> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.score_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Score item = itemList.get(position);
        holder.scoreTexte.setText(item.getPlayerScore());

    }
    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView scoreTexte;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            scoreTexte = itemView.findViewById(R.id.scoreView);

        }
    }
}

