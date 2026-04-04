package com.hiskytechs.muhallinewuserapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.hiskytechs.muhallinewuserapp.adapters.OnboardingAdapter
import com.hiskytechs.muhallinewuserapp.MainActivity
import com.hiskytechs.muhallinewuserapp.models.OnboardingItem
import com.hiskytechs.muhallinewuserapp.R
import com.hiskytechs.muhallinewuserapp.databinding.ActivityOnboardingBinding

class ActivityOnboarding : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var onboardingAdapter: OnboardingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupOnboardingItems()
        setupIndicators()
        setCurrentIndicator(0)

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
                updateButtonText(position)
            }
        })

        binding.btnNext.setOnClickListener {
            if (binding.viewPager.currentItem + 1 < onboardingAdapter.itemCount) {
                binding.viewPager.currentItem += 1
            } else {
                navigateToMain()
            }
        }

        binding.tvSkip.setOnClickListener {
            navigateToMain()
        }
    }

    private fun setupOnboardingItems() {
        val items = listOf(
            OnboardingItem(
                R.drawable.onboard1,
                "Discover Trusted Suppliers",
                "Browse through multiple verified wholesalers and find the best products for your business needs"
            ),
            OnboardingItem(
                R.drawable.onboard2,
                "Order in Bulk Easily",
                "Shop snacks, drinks, groceries and more in bulk quantities with competitive wholesale prices"
            ),
            OnboardingItem(
                R.drawable.onboard3,
                "Fast Delivery and Easy Tracking",
                "Get your orders delivered quickly with real-time tracking for complete peace of mind"
            )
        )
        onboardingAdapter = OnboardingAdapter(items)
        binding.viewPager.adapter = onboardingAdapter
    }

    private fun setupIndicators() {
        val indicators = arrayOfNulls<ImageView>(onboardingAdapter.itemCount)
        val layoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(applicationContext)
            indicators[i]?.apply {
                setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )
                this.layoutParams = layoutParams
            }
            binding.layoutIndicators.addView(indicators[i])
        }
    }

    private fun setCurrentIndicator(position: Int) {
        val childCount = binding.layoutIndicators.childCount
        for (i in 0 until childCount) {
            val imageView = binding.layoutIndicators.getChildAt(i) as ImageView
            if (i == position) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )
            }
        }
    }

    private fun updateButtonText(position: Int) {
        if (position == onboardingAdapter.itemCount - 1) {
            binding.btnNext.text = "Get Started"
            binding.tvSkip.visibility = android.view.View.GONE
            binding.layoutBottomText.visibility = android.view.View.VISIBLE
        } else {
            binding.btnNext.text = "Next"
            binding.tvSkip.visibility = android.view.View.VISIBLE
            binding.layoutBottomText.visibility = android.view.View.GONE
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
