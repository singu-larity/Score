package com.example.john.score;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class ScorePointDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.score_point_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        java.text.DecimalFormat df = new java.text.DecimalFormat("#.0000");
        TextView textView = (TextView) view.findViewById(R.id.score_point_ABC);
        String str = "ABC类课的学分绩为：  " + df.format(DisplayScoreActivity.getAvg_ABC());
        textView.setText(str);
        textView = (TextView) view.findViewById(R.id.score_point_ABCD);
        str = "BCD类课的学分绩为：  " + df.format(DisplayScoreActivity.getAvg_BCD());
        textView.setText(str);
        textView = (TextView) view.findViewById(R.id.score_point_BCD);
        str = "ABCD类课的学分绩为： " + df.format(DisplayScoreActivity.getAvg_ABCD());
        textView.setText(str);
        textView = (TextView) view.findViewById(R.id.score_point_ABCDE);
        str = "ABCDE类课的学分绩为：" + df.format(DisplayScoreActivity.getAvg_ABCDE());
        textView.setText(str);
        builder.setView(view)
            .setNeutralButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            })
            .setTitle("结果");
        return builder.create();
    }
}
