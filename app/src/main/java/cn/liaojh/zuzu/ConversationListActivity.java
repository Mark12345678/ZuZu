package cn.liaojh.zuzu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ConversationListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_list);
        setTitle("聊天ing");
    }
}
