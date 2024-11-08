package com.theateam.sparklinehr

import java.io.Serializable

data class Payslip(
    val empName: String = "",
    val empNum: String = "",
    val empPos: String = "",
    val company: String = "",
    val taxNum: String = "",
    val issueDate: String = "",
    val payslipPeriod: String = "",
    val basicSalary: Double = 0.0,
    val taxPercent: Double = 0.0,
    val uifPercent: Double = 0.0,
    val pensionPercent: Double = 0.0
) : Serializable