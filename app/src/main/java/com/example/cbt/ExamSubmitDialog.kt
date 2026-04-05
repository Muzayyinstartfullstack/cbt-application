import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.example.cbt.R   // sesuaikan dengan package name Anda

class ExamSubmitDialog(private val context: Context) {

    private lateinit var dialog: Dialog

    fun show(
        isAllQuestionsAnswered: Boolean,
        onCekSoal: () -> Unit,
        onKumpulkan: () -> Unit
    ) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_submit_exam, null)

        val layoutNotFinished = dialogView.findViewById<ViewGroup>(R.id.layoutNotFinished)
        val layoutReady = dialogView.findViewById<ViewGroup>(R.id.layoutReady)

        if (isAllQuestionsAnswered) {
            layoutNotFinished.visibility = View.GONE
            layoutReady.visibility = View.VISIBLE

            dialogView.findViewById<Button>(R.id.btnKumpulkanJawaban).setOnClickListener {
                dialog.dismiss()
                onKumpulkan()
            }
            dialogView.findViewById<Button>(R.id.btnCekSoalReady).setOnClickListener {
                dialog.dismiss()
                onCekSoal()
            }
        } else {
            layoutNotFinished.visibility = View.VISIBLE
            layoutReady.visibility = View.GONE

            dialogView.findViewById<Button>(R.id.btnCekSoal).setOnClickListener {
                dialog.dismiss()
                onCekSoal()
            }
            dialogView.findViewById<Button>(R.id.btnKumpulkanSaja).setOnClickListener {
                dialog.dismiss()
                onKumpulkan()
            }
        }

        dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.show()
    }
}