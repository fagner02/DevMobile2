package com.example.myapplication
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.R.attr.value
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.animalapp.Animal
import com.example.myapplication.zooapp.PokemonActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.main_activity)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.bt).setOnClickListener {
            val myIntent: Intent = Intent(
                this.baseContext,
                Animal::class.java
            )
            myIntent.putExtra("key", value) //Optional parameters
            this.startActivity(myIntent)
        }

        val myIntent: Intent = Intent(
            this.baseContext,
            PokemonActivity::class.java
        )
        myIntent.putExtra("key", value) //Optional parameters
        this.startActivity(myIntent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}