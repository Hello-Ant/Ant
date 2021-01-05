/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.lee.ant.result;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lee.ant.R;

public class ResultTargetActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_act_result);
        initView();
    }

    private void initView() {
        String name = getIntent().getStringExtra("name");
        TextView tvTip = findViewById(R.id.tv_tip);
        tvTip.setText("接收到的数据：" + name);
        Button btnNavigation = findViewById(R.id.btn_navigation);
        btnNavigation.setText("Navigation 2");
        btnNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("result", "Hello，我是回传数据！");
                setResult(RESULT_OK, intent);
                finish();

                /*Intent intent = new Intent(ResultTargetActivity.this, ResultTestActivity.class);
                intent.putExtra("name", "from ResultTargetActivity.");
                startActivityForResult(intent);
                finish();*/
            }
        });
    }
}
