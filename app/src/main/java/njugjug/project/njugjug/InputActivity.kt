package njugjug.project.njugjug

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.ComponentActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class InputActivity : ComponentActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input)

        this.bottomNavigationView = findViewById(R.id.bottomNavView)

        // Tambahkan pendengar acara untuk BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_home -> {
                    // Pindah ke halaman Home (contoh: HomeActivity)
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menu_control -> {
                    // Pindah ke halaman Control (contoh: ControlActivity)
                    val intent = Intent(this, ControlActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menu_input -> {
                    // Pindah ke halaman Input (contoh: InputActivity)
                    bottomNavigationView.menu.findItem(R.id.menu_input).isChecked = true
                    true
                }
                else -> false
            }
        }

        val items = arrayOf("Padi", "Jagung", "Bawang Merah","Cabai Rawit", "Anggur", "Timun Sayur", "Timun Suri")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, items)

        val autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        autoCompleteTextView.setAdapter(adapter)

        autoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
            parent.getItemAtPosition(position).toString()
            // Lakukan sesuatu dengan item yang dipilih
        }
    }
}
