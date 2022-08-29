package com.example.myapplication.anim.path;

import android.graphics.Color;
import android.graphics.Path;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class PathActivity extends AppCompatActivity {
    PathAnimView pathAnimView1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path);


        //动态设置Path实例
        pathAnimView1 = findViewById(R.id.pathView);
        Path sPath = new Path();
        sPath.moveTo(370, 360);
        sPath.lineTo(820, 810);
        sPath.lineTo(220, 610);
        sPath.close();
        pathAnimView1.setSourcePath(sPath);
        //代码示例 动态对path加工，通过Helper
        pathAnimView1.setPathAnimHelper(new SysLoadAnimHelper(pathAnimView1, pathAnimView1.getSourcePath(), pathAnimView1.getAnimPath()));
        //设置颜色
        pathAnimView1.setColorBg(Color.BLUE).setColorFg(Color.RED);
        //当然你可以自己拿到Paint，然后搞事情，我这里设置线条宽度
        pathAnimView1.getPaint().setStrokeWidth(10);
        pathAnimView1.startAnim();
    }
}
