package com.hiskytechs.muhallinewuserapp.Ui

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.hiskytechs.muhallinewuserapp.Data.AppData
import com.hiskytechs.muhallinewuserapp.MainActivity
import com.hiskytechs.muhallinewuserapp.Models.Order
import com.hiskytechs.muhallinewuserapp.R
import com.hiskytechs.muhallinewuserapp.Utill.AddressManager
import com.hiskytechs.muhallinewuserapp.databinding.ActivityOrderDetailsBinding
import java.util.Locale

class OrderDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        val orderId = intent.getStringExtra(EXTRA_ORDER_ID).orEmpty()
        val order = AppData.findOrder(orderId)
        if (order == null) {
            finish()
            return
        }

        val statusUi = buildStatusUi(order)
        bindSummary(order, statusUi)
        bindTracking(order, statusUi)
        bindAddress()

        binding.btnContactSupplier.setOnClickListener {
            val supplier = AppData.findSupplierByName(order.supplier)
            val targetIntent = if (supplier != null) {
                Intent(this, ChatConversationActivity::class.java).apply {
                    putExtra(ChatConversationActivity.EXTRA_SUPPLIER_NAME, supplier.name)
                    putExtra(ChatConversationActivity.EXTRA_SUPPLIER_LOCATION, supplier.location)
                }
            } else {
                Intent(this, ChatsActivity::class.java)
            }
            startActivity(targetIntent)
        }

        binding.btnViewOrders.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java).apply {
                putExtra("navigate_to", "orders")
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            })
            finish()
        }
    }

    private fun bindSummary(order: Order, statusUi: OrderStatusUi) {
        val supplier = AppData.findSupplierByName(order.supplier)
        binding.tvSupplierName.text = order.supplier
        binding.tvSupplierLocation.text = supplier?.location ?: "Wholesale Supplier"
        binding.tvOrderId.text = order.orderId
        binding.tvOrderDate.text = order.date
        binding.tvTotal.text = String.format(Locale.getDefault(), "$%.2f", order.totalAmount)
        binding.tvItems.text = "${order.itemsCount} items"
        binding.tvEstimatedDate.text = statusUi.estimatedDeliveryText
        binding.tvOrderStatusBadge.text = statusUi.badgeText
        binding.tvOrderStatusBadge.setBackgroundResource(statusUi.badgeBackgroundRes)
        binding.tvOrderStatusBadge.setTextColor(
            ContextCompat.getColor(this, statusUi.badgeTextColorRes)
        )
        binding.tvTrackingSummary.text = statusUi.trackingSummary
    }

    private fun bindAddress() {
        val address = AddressManager.getAddress()
        binding.tvDeliveryAddress.text = address?.formattedAddress
            ?: "123 Commerce Street, Industrial Area, Dubai"
    }

    private fun bindTracking(order: Order, statusUi: OrderStatusUi) {
        val isCancelled = order.status.equals("cancelled", ignoreCase = true)
        updateStep(
            stepViews = StepViews(
                binding.ivStepOne,
                binding.tvStepOneTitle,
                binding.tvStepOneSubtitle,
                binding.tvStepOneMeta,
                binding.viewLineOne
            ),
            stepNumber = 1,
            currentStep = statusUi.currentStep,
            title = "Order Placed",
            subtitle = "Your order has been confirmed",
            meta = "${order.date} - 10:30 AM",
            currentColorRes = statusUi.currentColorRes,
            currentBackgroundRes = statusUi.currentBackgroundRes,
            currentIconRes = R.drawable.ic_check_circle_24
        )
        updateStep(
            stepViews = StepViews(
                binding.ivStepTwo,
                binding.tvStepTwoTitle,
                binding.tvStepTwoSubtitle,
                binding.tvStepTwoMeta,
                binding.viewLineTwo
            ),
            stepNumber = 2,
            currentStep = statusUi.currentStep,
            title = "Order Confirmed",
            subtitle = "Supplier confirmed your order",
            meta = "${order.date} - 11:15 AM",
            currentColorRes = statusUi.currentColorRes,
            currentBackgroundRes = statusUi.currentBackgroundRes,
            currentIconRes = R.drawable.ic_check_circle_24
        )
        updateStep(
            stepViews = StepViews(
                binding.ivStepThree,
                binding.tvStepThreeTitle,
                binding.tvStepThreeSubtitle,
                binding.tvStepThreeMeta,
                binding.viewLineThree
            ),
            stepNumber = 3,
            currentStep = statusUi.currentStep,
            title = if (isCancelled) "Order Cancelled" else "Preparing Order",
            subtitle = if (isCancelled) {
                "This order was cancelled before dispatch"
            } else {
                "Your items are being prepared"
            },
            meta = if (isCancelled) "${order.date} - 1:05 PM" else "${order.date} - 2:45 PM",
            currentColorRes = statusUi.currentColorRes,
            currentBackgroundRes = statusUi.currentBackgroundRes,
            currentIconRes = if (isCancelled) {
                R.drawable.ic_delete_24
            } else {
                R.drawable.ic_sync_24
            }
        )
        updateStep(
            stepViews = StepViews(
                binding.ivStepFour,
                binding.tvStepFourTitle,
                binding.tvStepFourSubtitle,
                binding.tvStepFourMeta,
                binding.viewLineFour
            ),
            stepNumber = 4,
            currentStep = statusUi.currentStep,
            title = if (isCancelled) "Dispatch Skipped" else "Out for Delivery",
            subtitle = if (isCancelled) {
                "Delivery did not start for this order"
            } else {
                "Order is on the way"
            },
            meta = if (isCancelled) {
                "Cancelled before dispatch"
            } else {
                order.deliveryDate ?: "Estimated next business day"
            },
            currentColorRes = statusUi.currentColorRes,
            currentBackgroundRes = statusUi.currentBackgroundRes,
            currentIconRes = R.drawable.ic_local_shipping_24,
            pendingIconRes = R.drawable.ic_local_shipping_24
        )
        updateStep(
            stepViews = StepViews(
                binding.ivStepFive,
                binding.tvStepFiveTitle,
                binding.tvStepFiveSubtitle,
                binding.tvStepFiveMeta,
                null
            ),
            stepNumber = 5,
            currentStep = statusUi.currentStep,
            title = if (isCancelled) "Tracking Closed" else "Delivered",
            subtitle = if (isCancelled) {
                "Order was not delivered"
            } else if (statusUi.currentStep >= 5) {
                "Order delivered successfully"
            } else {
                "Waiting for final delivery"
            },
            meta = if (isCancelled) {
                "No further delivery updates"
            } else {
                order.deliveryDate ?: "Estimated after dispatch"
            },
            currentColorRes = statusUi.currentColorRes,
            currentBackgroundRes = statusUi.currentBackgroundRes,
            currentIconRes = R.drawable.ic_check_circle_24,
            pendingIconRes = R.drawable.ic_location_on_24
        )
    }

    private fun updateStep(
        stepViews: StepViews,
        stepNumber: Int,
        currentStep: Int,
        title: String,
        subtitle: String,
        meta: String,
        currentColorRes: Int = R.color.status_transit_text,
        currentBackgroundRes: Int = R.drawable.bg_tracking_current,
        currentIconRes: Int = R.drawable.ic_sync_24,
        pendingIconRes: Int = R.drawable.ic_sync_24
    ) {
        val doneColor = ContextCompat.getColor(this, R.color.status_delivered_text)
        val currentColor = ContextCompat.getColor(this, currentColorRes)
        val pendingColor = ContextCompat.getColor(this, R.color.text_grey)
        val dividerColor = ContextCompat.getColor(this, R.color.divider)

        stepViews.title.text = title
        stepViews.subtitle.text = subtitle
        stepViews.meta.text = meta

        when {
            stepNumber < currentStep -> {
                stepViews.icon.setBackgroundResource(R.drawable.bg_tracking_done)
                stepViews.icon.setImageResource(R.drawable.ic_check_circle_24)
                stepViews.icon.imageTintList = ColorStateList.valueOf(doneColor)
                stepViews.title.setTextColor(ContextCompat.getColor(this, R.color.text_dark))
                stepViews.subtitle.setTextColor(ContextCompat.getColor(this, R.color.text_grey))
                stepViews.meta.setTextColor(doneColor)
                stepViews.connector?.setBackgroundColor(doneColor)
            }
            stepNumber == currentStep -> {
                stepViews.icon.setBackgroundResource(currentBackgroundRes)
                stepViews.icon.setImageResource(currentIconRes)
                stepViews.icon.imageTintList = ColorStateList.valueOf(currentColor)
                stepViews.title.setTextColor(ContextCompat.getColor(this, R.color.text_dark))
                stepViews.subtitle.setTextColor(ContextCompat.getColor(this, R.color.text_grey))
                stepViews.meta.setTextColor(currentColor)
                stepViews.connector?.setBackgroundColor(dividerColor)
            }
            else -> {
                stepViews.icon.setBackgroundResource(R.drawable.bg_tracking_pending)
                stepViews.icon.setImageResource(pendingIconRes)
                stepViews.icon.imageTintList = ColorStateList.valueOf(pendingColor)
                stepViews.title.setTextColor(pendingColor)
                stepViews.subtitle.setTextColor(pendingColor)
                stepViews.meta.setTextColor(pendingColor)
                stepViews.connector?.setBackgroundColor(dividerColor)
            }
        }
    }

    private fun buildStatusUi(order: Order): OrderStatusUi {
        return when (order.status.lowercase()) {
            "delivered" -> OrderStatusUi(
                badgeText = "Delivered",
                badgeBackgroundRes = R.drawable.bg_status_delivered,
                badgeTextColorRes = R.color.status_delivered_text,
                estimatedDeliveryText = order.deliveryDate ?: "Delivered",
                trackingSummary = "This order has been delivered successfully to your saved address.",
                currentStep = 5,
                currentColorRes = R.color.status_delivered_text,
                currentBackgroundRes = R.drawable.bg_tracking_done
            )
            "in transit" -> OrderStatusUi(
                badgeText = "In Transit",
                badgeBackgroundRes = R.drawable.bg_status_transit,
                badgeTextColorRes = R.color.status_transit_text,
                estimatedDeliveryText = order.deliveryDate ?: "Arriving soon",
                trackingSummary = "The supplier has dispatched this order and it is currently on the way.",
                currentStep = 4,
                currentColorRes = R.color.status_transit_text,
                currentBackgroundRes = R.drawable.bg_tracking_current
            )
            "cancelled" -> OrderStatusUi(
                badgeText = "Cancelled",
                badgeBackgroundRes = R.drawable.bg_status_cancelled,
                badgeTextColorRes = R.color.status_cancelled_text,
                estimatedDeliveryText = "Cancelled",
                trackingSummary = "This order was cancelled before dispatch. Contact the supplier if you want to place it again.",
                currentStep = 3,
                currentColorRes = R.color.status_cancelled_text,
                currentBackgroundRes = R.drawable.bg_tracking_cancelled
            )
            else -> OrderStatusUi(
                badgeText = "Processing",
                badgeBackgroundRes = R.drawable.bg_status_processing,
                badgeTextColorRes = R.color.status_processing_text,
                estimatedDeliveryText = order.deliveryDate ?: "Preparing for dispatch",
                trackingSummary = "The supplier confirmed your order and is preparing the requested items.",
                currentStep = 3,
                currentColorRes = R.color.status_processing_text,
                currentBackgroundRes = R.drawable.bg_tracking_processing
            )
        }
    }

    private data class StepViews(
        val icon: ImageView,
        val title: TextView,
        val subtitle: TextView,
        val meta: TextView,
        val connector: View?
    )

    private data class OrderStatusUi(
        val badgeText: String,
        val badgeBackgroundRes: Int,
        val badgeTextColorRes: Int,
        val estimatedDeliveryText: String,
        val trackingSummary: String,
        val currentStep: Int,
        val currentColorRes: Int,
        val currentBackgroundRes: Int
    )

    companion object {
        const val EXTRA_ORDER_ID = "order_id"
    }
}
