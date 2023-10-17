package njugjug.project.njugjug

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : ComponentActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.bottomNavigationView = findViewById(R.id.bottomNavView)

        // Tambahkan pendengar acara untuk BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_home -> {
                    // Pindah ke halaman Home (contoh: HomeActivity)
                    bottomNavigationView.menu.findItem(R.id.menu_home).isChecked = false
                    true
                }
                R.id.menu_control -> {
                    // Pindah ke halaman Control (contoh: ControlActivity)
                    bottomNavigationView.menu.findItem(R.id.menu_control).isChecked = true
                    val intent = Intent(this, ControlActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menu_input -> {
                    // Pindah ke halaman Input (contoh: InputActivity)
                    bottomNavigationView.menu.findItem(R.id.menu_input).isChecked = false
                    val intent = Intent(this, InputActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
}
