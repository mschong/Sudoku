package edu.utep.cs.cs4330.sudoku;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class Menu extends AppCompatActivity {
    private Button playButton;
    private RadioGroup boardSize;
    private RadioGroup gameDifficulty;
    public int size;
    public int difficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        playButton = findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Menu.this, MainActivity.class);
                startActivity(i);
            }
        });

        boardSize =  findViewById(R.id.boardSize);
        boardSize.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton smallSize = findViewById(R.id.button4x4);
                if(smallSize.isChecked()){
                    size = 4;
                    Toast.makeText(getApplicationContext(), "4x4 Selected", Toast.LENGTH_SHORT).show();
                } else{
                    size = 9;
                    Toast.makeText(getApplicationContext(), "9x9 Selected", Toast.LENGTH_SHORT).show();
                }
            }
        });


        gameDifficulty = findViewById(R.id.difficulty);
        gameDifficulty.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton easyMode = findViewById(R.id.easyButton);
                RadioButton mediumMode = findViewById(R.id.mediumButton);
                if(easyMode.isChecked()){
                    Toast.makeText(getApplicationContext(), "Easy Mode", Toast.LENGTH_SHORT).show();
                    difficulty = 1;
                } else if( mediumMode.isChecked()){
                    Toast.makeText(getApplicationContext(), "Medium Mode", Toast.LENGTH_SHORT).show();
                    difficulty = 2;
                } else {
                    Toast.makeText(getApplicationContext(), "Hard Mode", Toast.LENGTH_SHORT).show();
                    difficulty = 3;
                }
            }
        });

    }

}
