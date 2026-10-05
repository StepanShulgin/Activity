package otus.gpb.homework.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import otus.gpb.homework.activities.databinding.ActivityCBinding

class ActivityC : AppCompatActivity() {
    private lateinit var binding: ActivityCBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.openActivityA.setOnClickListener {
            startActivity(
                Intent(this, ActivityA::class.java).addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
                )
            )
        }
        binding.openActivityD.setOnClickListener {
            startActivity(
                Intent(this, ActivityD::class.java).addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                )
            )
        }
        binding.closeActivityC.setOnClickListener {
            finish()
        }
        binding.closeStack.setOnClickListener {
            finishAffinity()
        }
    }
}
