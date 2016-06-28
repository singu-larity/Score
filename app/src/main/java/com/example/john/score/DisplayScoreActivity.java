package com.example.john.score;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class DisplayScoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_score);
        Intent intent = getIntent();
        String raw = intent.getStringExtra("Raw Resource");
        ArrayList<ArrayList<String>> parsed_data = parse_raw_resource(raw);

        ScoreListAdapter adapter = new ScoreListAdapter(getApplicationContext(), parsed_data);
        ListView listView = (ListView) findViewById(R.id.score_list);
        listView.setAdapter(adapter);
    }

    protected ArrayList<ArrayList<String>> parse_raw_resource(String raw_resource) {
        ArrayList<ArrayList<String>> result = new ArrayList<>();

        ArrayList<String> initial = new ArrayList<>();
        initial.add("课程名称");
        initial.add("课程类型");
        initial.add("学分");
        initial.add("成绩");
        result.add(initial);

        for(int begin_position = 0, end_position = raw_resource.indexOf("</tr>");begin_position != -1;
                begin_position = raw_resource.indexOf("<td align=\"center\" class=\"NavText\">", end_position),
                end_position = raw_resource.indexOf("</tr>", begin_position))
            result.add(parse_single_data(raw_resource.substring(begin_position, end_position)));

        return result;
    }
    private ArrayList<String> parse_single_data(String single_raw_data) {
        System.out.println("Parse Single Data : " + single_raw_data);
        ArrayList<String> result = new ArrayList<>(4);
        int begin_index = 0, end_index = 0;
        int count = 0;
        while(begin_index != -1) {
            begin_index = single_raw_data.indexOf("<td align=\"center\" class=\"NavText\">", end_index) +
                    "<td align=\"center\" class=\"NavText\">".length();
            end_index = single_raw_data.indexOf("</td>", begin_index) - ("\n" +
                    "          \t\t").length();
            System.out.println(single_raw_data.substring(begin_index, end_index));
            if(count++ < SKIP_ITEM_COUNT)
                continue;
            else if ((count - SKIP_ITEM_COUNT) < DISPLAY_ITEM_COUNT + 1)
                result.add(single_raw_data.substring(begin_index, end_index));
            else
                break;
        }
        System.out.println("Size : " + result.size());
        return result;
    }

    private static final int SKIP_ITEM_COUNT = 2;
    private static final int DISPLAY_ITEM_COUNT = 4;
}
