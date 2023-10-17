package njugjug.project.njugjug
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inisialisasi elemen UI
        usernameEditText = findViewById(R.id.uname)
        passwordEditText = findViewById(R.id.pwd)
        loginButton = findViewById(R.id.loginButton)

        // Menambahkan event click listener pada tombol login
        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            // User dan password statis
            val staticUsername = "user"
            val staticPassword = "123123"


                if (username == staticUsername && password == staticPassword) {
                    // Login berhasil, arahkan ke MainActivity
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Tutup LoginActivity agar tidak dapat dikembali
                } else {
                    // Login gagal, tampilkan pesan kesalahan
                    Toast.makeText(this@LoginActivity, "Login gagal, silakan coba lagi.", Toast.LENGTH_SHORT).show()
                }

        }

    }
}
