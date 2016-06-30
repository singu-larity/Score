package com.example.john.score;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ScoreListAdapter extends BaseAdapter {
    ScoreListAdapter(Context context, ArrayList<ArrayList<String>> data) {
        m_context = context;
        m_data = data;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater)m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.score_layout, null);
            set_text_for_textView(i, view);
        }
        else {
            set_text_for_textView(i, view);
        }
        return view;
    }

    @Override
    public int getCount() {
        return m_data.size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    private void set_text_for_textView(int i, View view) {
        ((TextView) view.findViewById(R.id.course_name))
                .setText(m_data.get(i).get(DisplayScoreActivity.INDEX_OF_NAME));
        ((TextView) view.findViewById(R.id.course_type))
                .setText(m_data.get(i).get(DisplayScoreActivity.INDEX_OF_TYPE));
        ((TextView) view.findViewById(R.id.course_credit))
                .setText(m_data.get(i).get(DisplayScoreActivity.INDEX_OF_CREDIT));
        ((TextView) view.findViewById(R.id.course_score))
                .setText(m_data.get(i).get(DisplayScoreActivity.INDEX_OF_SCORE));
    }

    private Context m_context;
    private ArrayList<ArrayList<String>> m_data;
}
