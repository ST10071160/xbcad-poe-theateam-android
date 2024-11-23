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


        binding.viewSelectedPayslipBasicSalaryValueTextView.text = "R ${payslip.grossSalary}"
        binding.viewSelectedPayslipTotalEarningsValueTextView.text = "R ${payslip.grossSalary}"

        var taxAmount = 0.0

        var yearlysalary:Double = (payslip.grossSalary.toInt() * 12).toDouble()

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
        else if(yearlysalary > 1817001)
        {
            taxAmount = 644489 + 0.46*(yearlysalary - 1817000)
        }
        else{
            taxAmount = 0.0
        }

        val monthlyTax = taxAmount / 12
        val roundedMonthlyTax = String.format("%.2f", monthlyTax)
        binding.viewSelectedPayslipPAYEValueTextView.text = "R $roundedMonthlyTax"

        val intUif:Int = payslip.uifPercent.toInt()
        val doubleUif:Double = intUif.toDouble()/100
        val uifAmount:Double = (Math.round(payslip.grossSalary.toInt() * doubleUif)).toDouble()
        val roundedUifAmount = String.format("%.2f", uifAmount)
        binding.viewSelectedPayslipUIFValueTextView.text = "R ${roundedUifAmount}"

        val intPension:Int = payslip.pensionPercent.toInt()
        val doublePension:Double = intPension.toDouble()/100
        val pensionAmount = (Math.round(payslip.grossSalary.toInt() * doublePension)).toDouble()
        val roundedPensionAmount = String.format("%.2f", pensionAmount)
        binding.viewSelectedPayslipPensionFundValueTextView.text = "R ${roundedPensionAmount}"

        val totalDeductions = monthlyTax + uifAmount + pensionAmount
        val roundedTotalDeductions = String.format("%.2f", totalDeductions)
        binding.viewSelectedPayslipTotalDeductionsValueTextView.text = "R ${roundedTotalDeductions}"

        val netPay = payslip.grossSalary.toInt() - totalDeductions
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

        val randomAddress = "38 De la Haye Avenue, Cape Town, 7580"
        val randomPhoneNumber = "+27 21 123 4567"
        val email = "sparkline.xbcad@gmail.com"

        canvas.drawText(randomAddress, padding, 50f, paint)
        canvas.drawText(randomPhoneNumber, padding, 75f, paint)
        canvas.drawText(email, padding, 100f, paint)

        val iconBitMap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.spark_line_icon_only)
        val appIcon = Bitmap.createScaledBitmap(iconBitMap, 100, 50, true)
        canvas.drawBitmap(appIcon, 100f, 120f, paint)

        canvas.drawText("Payslip for ${payslip.payslipPeriod}", padding, 180f, paint)

        drawLabelAndValue("Employee Name:", payslip.empName, 200f)
        drawLabelAndValue("Employee Number:", payslip.empNum, 220f)
        drawLabelAndValue("Position:", payslip.empPos, 240f)
        drawLabelAndValue("Company:", payslip.company, 260f)
        drawLabelAndValue("Tax Number:", payslip.taxNum, 280f)
        drawLabelAndValue("Date of Issue:", payslip.issueDate, 300f)
        drawLabelAndValue("Payslip Period:", payslip.payslipPeriod, 320f)

        drawLabelAndValue("Gross Salary:", "R " + payslip.grossSalary.toString(), 370f)
        drawLabelAndValue("Total Earnings:", "R " + payslip.grossSalary.toString(), 395f)

        var taxAmount = 0.0

        var yearlysalary = payslip.grossSalary.toInt() * 12

        if (yearlysalary > 95750 && yearlysalary < 237100) {
            taxAmount = 0.18 * (yearlysalary)
        } else if (yearlysalary > 237101 && yearlysalary < 370500) {
            taxAmount = 42678 + 0.26 * (yearlysalary - 237100)
        } else if (yearlysalary > 370501 && yearlysalary < 512800) {
            taxAmount = 77362 + 0.31 * (yearlysalary - 370500)
        } else if (yearlysalary > 512801 && yearlysalary < 673000) {
            taxAmount = 121475 + 0.36 * (yearlysalary - 512800)
        } else if (yearlysalary > 673001 && yearlysalary < 857900) {
            taxAmount = 179147 + 0.39 * (yearlysalary - 673000)
        } else if (yearlysalary > 857901 && yearlysalary < 1817000) {
            taxAmount = 251258 + 0.41 * (yearlysalary - 857900)
        } else if (yearlysalary > 1817001) {
            taxAmount = 251258 + 0.41 * (yearlysalary - 857900)
        } else {
            taxAmount = 0.0
        }

        val monthlyTax = taxAmount / 12
        val roundedMonthlyTax = String.format("%.2f", monthlyTax)
        drawLabelAndValue("PAYE (Tax):", "R " + roundedMonthlyTax.toString(), 445f)

        val intUif: Int = payslip.uifPercent.toInt()
        val doubleUif: Double = intUif.toDouble() / 100
        val uifAmount = payslip.grossSalary.toInt() * doubleUif
        val roundedUifAmount = String.format("%.2f", uifAmount)
        drawLabelAndValue("UIF:", "R " + roundedUifAmount, 465f)

        val intPension: Int = payslip.pensionPercent.toInt()
        val doublePension: Double = intPension.toDouble() / 100
        val pensionAmount = payslip.grossSalary.toInt() * doublePension
        val roundedPensionAmount = String.format("%.2f", uifAmount)
        drawLabelAndValue("Pension Fund:", "R " + roundedPensionAmount, 485f)

        val totalDeductions = monthlyTax + uifAmount + pensionAmount
        val roundedTotalDeductions = String.format("%.2f", totalDeductions)
        drawLabelAndValue("Total Deductions:", "R " + roundedTotalDeductions, 535f)

        val netPay = payslip.grossSalary.toInt() - totalDeductions
        val roundedNetPay = String.format("%.2f", netPay)
        drawLabelAndValue("Net Pay:", "R " + roundedNetPay, 585f)

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