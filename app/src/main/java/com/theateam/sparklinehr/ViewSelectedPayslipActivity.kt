package com.theateam.sparklinehr

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.theateam.sparklinehr.databinding.ActivityViewSelectedPayslipBinding
import java.io.File
import java.io.FileOutputStream

class ViewSelectedPayslipActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewSelectedPayslipBinding

    val outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityViewSelectedPayslipBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setContentView(R.layout.activity_view_selected_payslip)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val payslip = intent.getSerializableExtra("payslip") as Payslip
        if (payslip != null) {
            Log.d("ViewSelectedPayslip", "Payslip received: $payslip")
        } else {
            Log.e("ViewSelectedPayslip", "Payslip is null. Intent extras: ${intent?.extras}")
            finish() // Close the activity to avoid further issues
        }


        binding.viewSelectedPayslipSpecificMonthTextView.text = "Payslip: ${payslip.payslipPeriod}"

        binding.viewSelectedPayslipEmployeeNameTextView.text = payslip.empName
        binding.viewSelectedPayslipEmployeeNumberTextView.text = payslip.empNum
        binding.viewSelectedPayslipPositionTextView.text = payslip.empPos
        binding.viewSelectedPayslipTaxNumberTextView.text = payslip.taxNum
        binding.viewSelectedPayslipDateOfIssueTextView.text = payslip.issueDate
        binding.viewSelectedPayslipPeriodTextView.text = payslip.payslipPeriod

        val roundedPayslip = String.format("%.2f", payslip.basicSalary.toString())
        binding.viewSelectedPayslipBasicSalaryValueTextView.text = "R ${roundedPayslip}"
        binding.viewSelectedPayslipTotalEarningsValueTextView.text = "R ${roundedPayslip}"

        var taxAmount = 0.0

        var yearlysalary = payslip.basicSalary * 12

        if(yearlysalary > 95750 && yearlysalary < 237100)
        {
            taxAmount = 0.18*(yearlysalary)
        }
        else if(yearlysalary > 237101 && yearlysalary < 370500)
        {
            taxAmount = 42678 + 0.26*(yearlysalary - 237100)
        }
        else if(yearlysalary > 370501 && yearlysalary < 512800)
        {
            taxAmount = 77362 + 0.31*(yearlysalary - 370500)
        }
        else if(yearlysalary > 512801 && yearlysalary < 673000)
        {
            taxAmount = 121475 + 0.36*(yearlysalary - 512800)
        }
        else if(yearlysalary > 673001 && yearlysalary < 857900)
        {
            taxAmount = 179147 + 0.39*(yearlysalary - 673000)
        }
        else if(yearlysalary > 857901 && yearlysalary < 1817000)
        {
            taxAmount = 251258 + 0.41*(yearlysalary - 857900)
        }
        else if(payslip.basicSalary > 1817001)
        {
            taxAmount = 251258 + 0.41*(yearlysalary - 857900)
        }
        else{
            taxAmount = 0.0
        }

        val monthlyTax = taxAmount / 12
        val roundedMonthlyTax = String.format("%.2f", monthlyTax)
        binding.viewSelectedPayslipPAYEValueTextView.text = "R $roundedMonthlyTax"

        val uifAmount = Math.round(payslip.basicSalary * payslip.uifPercent)
        val roundedUifAmount = String.format("%.2f", uifAmount)
        binding.viewSelectedPayslipUIFValueTextView.text = "R ${roundedUifAmount}"

        val pensionAmount = Math.round(payslip.basicSalary * payslip.pensionPercent)
        val roundedPensionAmount = String.format("%.2f", pensionAmount)
        binding.viewSelectedPayslipPensionFundValueTextView.text = "R ${roundedPensionAmount}"

        val totalDeductions = monthlyTax + uifAmount + pensionAmount
        val roundedTotalDeductions = String.format("%.2f", totalDeductions)
        binding.viewSelectedPayslipTotalDeductionsValueTextView.text = "R ${roundedTotalDeductions}"

        val netPay = payslip.basicSalary - totalDeductions
        val roundedNetPay = String.format("%.2f", netPay)
        binding.viewSelectedPayslipNetPayValueTextView.text = "R ${roundedNetPay}"


        binding.backBtn.setOnClickListener{
            finish()
        }

        binding.viewSelectedPayslipDownloadPayslipBtn.setOnClickListener()
        {
            val pdf = createPDF(payslip)
            promptUserToDownloadOrOpen(this@ViewSelectedPayslipActivity, pdf)
        }
    }


    private fun createPDF(payslip: Payslip): File {
        val pdfDocument = PdfDocument()
        val paint = Paint()
        paint.textSize = 14f

        val pageInfo = PdfDocument.PageInfo.Builder(300, 600, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val padding = 10f
        val pageWidth = pageInfo.pageWidth.toFloat()

        fun drawLabelAndValue(label: String, value: String, yPos: Float) {
            canvas.drawText(label, padding, yPos, paint)
            val valueWidth = paint.measureText(value)
            canvas.drawText(value, pageWidth - valueWidth - padding, yPos, paint)
        }

        canvas.drawText("SparkLine", padding, 25f, paint)

        val iconBitMap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.spark_line_icon_only)
        val appIcon = Bitmap.createScaledBitmap(iconBitMap, 100, 50, true)
        canvas.drawBitmap(appIcon, 100f, 40f, paint)

        canvas.drawText("Payslip for ${payslip.payslipPeriod}", padding, 120f, paint)

        drawLabelAndValue("Employee Name:", payslip.empName, 140f)
        drawLabelAndValue("Employee Number:", payslip.empNum, 160f)
        drawLabelAndValue("Position:", payslip.empPos, 180f)
        drawLabelAndValue("Company:", payslip.company, 200f)
        drawLabelAndValue("Tax Number:", payslip.taxNum, 220f)
        drawLabelAndValue("Date of Issue:", payslip.issueDate, 240f)
        drawLabelAndValue("Payslip Period:", payslip.payslipPeriod, 260f)

        drawLabelAndValue("Gross Salary:", "R " + payslip.basicSalary.toString(), 310f)
        drawLabelAndValue("Total Earnings:", "R " + payslip.basicSalary.toString(), 335f)

        val taxAmount = payslip.basicSalary * payslip.taxPercent
        drawLabelAndValue("PAYE (Tax):", "R " + taxAmount.toString(), 385f)

        val uifAmount = payslip.basicSalary * payslip.uifPercent
        drawLabelAndValue("UIF:", "R " + uifAmount.toString(), 405f)

        val pensionAmount = payslip.basicSalary * payslip.pensionPercent
        drawLabelAndValue("Pension Fund:", "R " + pensionAmount.toString(), 425f)

        val totalDeductions = taxAmount + uifAmount + pensionAmount
        drawLabelAndValue("Total Deductions:", "R " + totalDeductions.toString(), 475f)

        val netPay = payslip.basicSalary - totalDeductions
        drawLabelAndValue("Net Pay:", "R " + netPay.toString(), 525f)

        pdfDocument.finishPage(page)
        val pdfFile = File(outputDir, "employee_payslip_${payslip.payslipPeriod}.pdf")
        pdfDocument.writeTo(FileOutputStream(pdfFile))
        pdfDocument.close()

        return pdfFile
    }





    fun promptUserToDownloadOrOpen(context: Context, pdfFile: File) {
        val pdfUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", pdfFile)

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(pdfUri, "application/pdf")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        context.startActivity(Intent.createChooser(intent, "Open PDF with"))
    }

}