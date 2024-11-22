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
    val grossSalary: String = "",
    val uifPercent: String = "",
    val pensionPercent: String = ""
) : Serializable