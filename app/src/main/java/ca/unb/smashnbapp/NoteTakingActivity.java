package ca.unb.smashnbapp;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class NoteTakingActivity extends AppCompatActivity {
    private String FILE_NAME = "";
    private EditText noteText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_taking);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String opponentName = getIntent().getStringExtra("filename");
        getSupportActionBar().setTitle(opponentName + " Notes");

        noteText = findViewById(R.id.noteEditText);
        FILE_NAME = opponentName + ".txt";
        Open(); //will only open previous notes if filename exists

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveNotes();
            }
        });
    }

    public void SaveNotes() {
        try {
            OutputStreamWriter out =
                    new OutputStreamWriter(openFileOutput(FILE_NAME, 0));
            out.write(String.valueOf(noteText.getText()));
            out.close();
            Toast.makeText(this, "Note Saved!", Toast.LENGTH_SHORT).show();
        } catch (Throwable t) {
            Toast.makeText(this, "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void Open() {
        String content = "";
        if (FileExists()) {
            try {
                InputStream in = openFileInput(FILE_NAME);
                if ( in != null) {
                    InputStreamReader tmp = new InputStreamReader( in );
                    BufferedReader reader = new BufferedReader(tmp);
                    String str;
                    StringBuilder buf = new StringBuilder();
                    while ((str = reader.readLine()) != null) {
                        buf.append(str + "\n");
                    } in .close();
                    content = buf.toString();
                    noteText.setText(content);

                }
            } catch (java.io.FileNotFoundException e) {} catch (Throwable t) {
                Toast.makeText(this, "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public boolean FileExists(){
        File file = getBaseContext().getFileStreamPath(FILE_NAME);
        return file.exists();
    }
}
