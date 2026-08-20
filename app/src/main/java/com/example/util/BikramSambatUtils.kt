package com.example.util

import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

data class BsDate(
    val year: Int,
    val month: Int, // 1 to 12
    val day: Int    // 1 to 32
) {
    val monthName: String
        get() = BikramSambatUtils.BS_MONTH_NAMES.getOrElse(month - 1) { "Month $month" }

    val monthNameNepali: String
        get() = BikramSambatUtils.BS_MONTH_NAMES_NEPALI.getOrElse(month - 1) { "" }

    fun format(includeNepali: Boolean = false): String {
        val paddedDay = day.toString().padStart(2, '0')
        return if (includeNepali) {
            "$year $monthNameNepali $paddedDay ($monthName)"
        } else {
            "$year $monthName $paddedDay"
        }
    }

    fun toIsoString(): String {
        return "${year}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"
    }
}

object BikramSambatUtils {

    val BS_MONTH_NAMES = listOf(
        "Baisakh", "Jestha", "Ashadh", "Shrawan", "Bhadra", "Ashwin",
        "Kartik", "Mangshir", "Poush", "Magh", "Falgun", "Chaitra"
    )

    val BS_MONTH_NAMES_NEPALI = listOf(
        "बैशाख", "जेठ", "असार", "साउन", "भदौ", "असोज",
        "कार्तिक", "मंसिर", "पुस", "माघ", "फागुन", "चैत"
    )

    // Standard Nepali BS calendar days per month lookup table: 1980 BS to 2085 BS
    // Base reference: 2000 BS Baisakh 1 = 1943 AD April 13 (Epoch)
    private val BS_CALENDAR_DATA = mapOf(
        1980 to intArrayOf(30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31),
        1981 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        1982 to intArrayOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30),
        1983 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31),
        1984 to intArrayOf(30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31),
        1985 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        1986 to intArrayOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30),
        1987 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31),
        1988 to intArrayOf(31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 29, 31),
        1989 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        1990 to intArrayOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30),
        1991 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31),
        1992 to intArrayOf(30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31),
        1993 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        1994 to intArrayOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30),
        1995 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31),
        1996 to intArrayOf(30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31),
        1997 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        1998 to intArrayOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30),
        1999 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31),
        2000 to intArrayOf(30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31),
        2001 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        2002 to intArrayOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30),
        2003 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31),
        2004 to intArrayOf(30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31),
        2005 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        2006 to intArrayOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30),
        2007 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31),
        2008 to intArrayOf(31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 29, 31),
        2009 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        2010 to intArrayOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30),
        2011 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31),
        2012 to intArrayOf(30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31),
        2013 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        2014 to intArrayOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30),
        2015 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31),
        2016 to intArrayOf(30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31),
        2017 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        2018 to intArrayOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30),
        2019 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31),
        2020 to intArrayOf(30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31),
        2021 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        2022 to intArrayOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30),
        2023 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31),
        2024 to intArrayOf(30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31),
        2025 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        2026 to intArrayOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30),
        2027 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31),
        2028 to intArrayOf(31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 29, 31),
        2029 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        2030 to intArrayOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30),
        2031 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31),
        2032 to intArrayOf(30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31),
        2033 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        2034 to intArrayOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30),
        2035 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31),
        2036 to intArrayOf(30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31),
        2037 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        2038 to intArrayOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30),
        2039 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31),
        2040 to intArrayOf(30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31),
        2041 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        2042 to intArrayOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30),
        2043 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31),
        2044 to intArrayOf(30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31),
        2045 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        2046 to intArrayOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30),
        2047 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31),
        2048 to intArrayOf(31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 29, 31),
        2049 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        2050 to intArrayOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30),
        2051 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31),
        2052 to intArrayOf(30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31),
        2053 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        2054 to intArrayOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30),
        2055 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31),
        2056 to intArrayOf(30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31),
        2057 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        2058 to intArrayOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30),
        2059 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31),
        2060 to intArrayOf(30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31),
        2061 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        2062 to intArrayOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30),
        2063 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31),
        2064 to intArrayOf(31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 29, 31),
        2065 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        2066 to intArrayOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30),
        2067 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31),
        2068 to intArrayOf(31, 31, 32, 31, 31, 30, 30, 30, 29, 30, 29, 31),
        2069 to intArrayOf(31, 31, 32, 31, 31, 30, 30, 30, 29, 30, 29, 31),
        2070 to intArrayOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30),
        2071 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31),
        2072 to intArrayOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30),
        2073 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31),
        2074 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        2075 to intArrayOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30),
        2076 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31),
        2077 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        2078 to intArrayOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30),
        2079 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31),
        2080 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        2081 to intArrayOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30),
        2082 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31),
        2083 to intArrayOf(31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30),
        2084 to intArrayOf(31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30),
        2085 to intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31)
    )

    // Base Anchor: 2000 BS Baisakh 1 = 1943 AD April 13
    private val BASE_BS_YEAR = 2000
    private val BASE_AD_DATE = LocalDate.of(1943, 4, 13)

    fun getDaysInBsMonth(year: Int, month: Int): Int {
        val yearData = BS_CALENDAR_DATA[year] ?: intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31)
        return yearData.getOrElse(month - 1) { 30 }
    }

    fun getAvailableBsYears(): List<Int> {
        return (1980..2085).toList().reversed()
    }

    /**
     * Converts a Bikram Sambat (BS) date to a Gregorian (AD) LocalDate
     */
    fun bsToAd(bsYear: Int, bsMonth: Int, bsDay: Int): LocalDate {
        val clampedYear = bsYear.coerceIn(1980, 2085)
        val clampedMonth = bsMonth.coerceIn(1, 12)
        val maxDays = getDaysInBsMonth(clampedYear, clampedMonth)
        val clampedDay = bsDay.coerceIn(1, maxDays)

        var totalDays = 0L

        if (clampedYear >= BASE_BS_YEAR) {
            for (y in BASE_BS_YEAR until clampedYear) {
                val months = BS_CALENDAR_DATA[y] ?: intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31)
                totalDays += months.sum()
            }
            val currentYearMonths = BS_CALENDAR_DATA[clampedYear] ?: intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31)
            for (m in 0 until (clampedMonth - 1)) {
                totalDays += currentYearMonths[m]
            }
            totalDays += (clampedDay - 1)
            return BASE_AD_DATE.plusDays(totalDays)
        } else {
            for (y in clampedYear until BASE_BS_YEAR) {
                val months = BS_CALENDAR_DATA[y] ?: intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31)
                totalDays += months.sum()
            }
            val currentYearMonths = BS_CALENDAR_DATA[clampedYear] ?: intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31)
            var currentYearDaysPassed = 0L
            for (m in 0 until (clampedMonth - 1)) {
                currentYearDaysPassed += currentYearMonths[m]
            }
            currentYearDaysPassed += (clampedDay - 1)
            val netDays = totalDays - currentYearDaysPassed
            return BASE_AD_DATE.minusDays(netDays)
        }
    }

    /**
     * Converts a Gregorian (AD) LocalDate to a Bikram Sambat (BS) date
     */
    fun adToBs(adDate: LocalDate): BsDate {
        var daysDiff = java.time.temporal.ChronoUnit.DAYS.between(BASE_AD_DATE, adDate)

        if (daysDiff >= 0) {
            var currentYear = BASE_BS_YEAR
            while (currentYear <= 2085) {
                val months = BS_CALENDAR_DATA[currentYear] ?: intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31)
                val daysInYear = months.sum()
                if (daysDiff < daysInYear) {
                    var currentMonth = 1
                    for (daysInMonth in months) {
                        if (daysDiff < daysInMonth) {
                            val currentDay = (daysDiff + 1).toInt()
                            return BsDate(currentYear, currentMonth, currentDay)
                        }
                        daysDiff -= daysInMonth
                        currentMonth++
                    }
                }
                daysDiff -= daysInYear
                currentYear++
            }
            return BsDate(2085, 12, 30)
        } else {
            var daysToSubtract = -daysDiff
            var currentYear = BASE_BS_YEAR - 1
            while (currentYear >= 1980) {
                val months = BS_CALENDAR_DATA[currentYear] ?: intArrayOf(31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31)
                val daysInYear = months.sum()
                if (daysToSubtract <= daysInYear) {
                    var remainingDays = daysInYear - daysToSubtract
                    var currentMonth = 1
                    for (daysInMonth in months) {
                        if (remainingDays < daysInMonth) {
                            val currentDay = (remainingDays + 1).toInt()
                            return BsDate(currentYear, currentMonth, currentDay)
                        }
                        remainingDays -= daysInMonth
                        currentMonth++
                    }
                }
                daysToSubtract -= daysInYear
                currentYear--
            }
            return BsDate(1980, 1, 1)
        }
    }

    /**
     * Calculates the real age in years from given DOB (AD or BS)
     */
    fun calculateAge(year: Int, month: Int, day: Int, isBS: Boolean): Int {
        return try {
            val adDate = if (isBS) {
                bsToAd(year, month, day)
            } else {
                LocalDate.of(year, month, day)
            }
            val today = LocalDate.now()
            if (adDate.isAfter(today)) return 0
            Period.between(adDate, today).years
        } catch (e: Exception) {
            0
        }
    }

    fun isAgeEligible(age: Int): Boolean {
        return age >= 16
    }
}
