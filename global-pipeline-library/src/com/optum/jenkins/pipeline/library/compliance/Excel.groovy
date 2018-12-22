package com.optum.jenkins.pipeline.library.compliance

import com.optum.jenkins.pipeline.library.compliance.models.AgileReleaseScope
import com.optum.jenkins.pipeline.library.compliance.models.CM1Report
import com.optum.jenkins.pipeline.library.compliance.models.CM2Report
import com.optum.jenkins.pipeline.library.compliance.models.CM3Report
import groovy.json.JsonSlurperClassic
import org.apache.poi.common.usermodel.Hyperlink
import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFCellStyle
import org.apache.poi.hssf.usermodel.HSSFFont
import org.apache.poi.hssf.usermodel.HSSFHyperlink
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.hssf.util.HSSFColor
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.Font

import java.time.Instant
import java.time.format.DateTimeFormatter

@Grab('org.apache.poi:poi:3.9')

//Responsible for getting/making all data required for a CM1/CM2/CM3 report.

class Excel implements Serializable {

  /**
   * Creates the CM1/CM2 xsl sheet.
   * REQUIRED: jenkins.env.COMPLIANCE_DEPLOYMENT_INFO
   * REQUIRED: jenkins.env.COMPLIANCE_CM1_REPORT
   * REQUIRED: jenkins.env.COMPLIANCE_CM2_REPORT
   */
  byte[] createCM1CM2report(AgileReleaseScope deploymentInfo, CM1Report cm1Report, CM2Report cm2Report) {
    JsonSlurperClassic jsonSlurper = new JsonSlurperClassic()
    HSSFWorkbook workbook = new HSSFWorkbook()
    // Cell styles
    CM1Sheet.insertCM1Sheet(workbook, deploymentInfo, cm1Report)
    CM2Sheet.insertCM2Sheet(workbook, deploymentInfo, cm2Report)

    ByteArrayOutputStream outStream = new ByteArrayOutputStream()
    workbook.write(outStream)

    return outStream.toByteArray()
  }

  /**
   * Creates the final xsl sheet.
   * REQUIRED: jenkins.env.COMPLIANCE_DEPLOYMENT_INFO
   * REQUIRED: jenkins.env.COMPLIANCE_CM1_REPORT
   * REQUIRED: jenkins.env.COMPLIANCE_CM2_REPORT
   * REQUIRED: jenkins.env.COMPLIANCE_CM3_REPORT
   */
  byte[] createComplianceReport(AgileReleaseScope deploymentInfo, CM1Report cm1Report, CM2Report cm2Report, CM3Report cm3Report) {
    JsonSlurperClassic jsonSlurper = new JsonSlurperClassic()
    HSSFWorkbook workbook = new HSSFWorkbook()
    // Cell styles
    CM1Sheet.insertCM1Sheet(workbook, deploymentInfo, cm1Report)
    CM2Sheet.insertCM2Sheet(workbook, deploymentInfo, cm2Report)
    CM3Sheet.insertCM3Sheet(workbook, deploymentInfo, cm3Report)

    ByteArrayOutputStream outStream = new ByteArrayOutputStream()
    workbook.write(outStream)

    return outStream.toByteArray()
  }

  static HSSFCell insertCellInRow(HSSFRow row, int column, HSSFCellStyle style, String content, String href) {
    def cell = row.createCell(column)
    cell.setCellStyle(style)
    cell.setCellValue(content)
    if (href) {
      HSSFHyperlink link = new HSSFHyperlink(Hyperlink.LINK_URL)
      link.setAddress(href)
      cell.setHyperlink(link)
    }
    return cell
  }

  static HSSFCell insertCellInRowNumeric(HSSFRow row, int column, HSSFCellStyle style, Double number, boolean percentage) {
    def cell = row.createCell(column)
    cell.setCellStyle(style)
    cell.setCellValue(number)
    if (percentage) {
      CellStyle percentageStyle = row.getSheet().getWorkbook().createCellStyle()
      percentageStyle.cloneStyleFrom(style)
      percentageStyle.setDataFormat(row.getSheet().getWorkbook().createDataFormat().getFormat('0%'))
      cell.setCellStyle(percentageStyle)
      cell.setCellValue(number)
    }
    return cell
  }

  static HSSFCellStyle createDefaultStyle(HSSFWorkbook workbook) {
    HSSFCellStyle style = workbook.createCellStyle()
    style.setWrapText(true)

    Font font = workbook.createFont()
    font.setColor(HSSFColor.BLACK.index)
    font.setFontHeightInPoints((short) 9)
    style.setFont(font)

    return style
  }

  static void setStyleBold(HSSFCellStyle style, HSSFWorkbook workbook) {
    if (!style.getFont(workbook)) {
      style.setFont(workbook.createFont())
    }
    style.getFont(workbook).setBoldweight(HSSFFont.COLOR_NORMAL)
  }

  static void setStyleCenter(HSSFCellStyle style) {
    style.setAlignment(CellStyle.ALIGN_CENTER)
  }

  static void setStyleNoWrap(HSSFCellStyle style) {
    style.setWrapText(false)
  }

  static void setStyleHref(HSSFCellStyle style, HSSFWorkbook workbook) {
    if (!style.getFont(workbook)) {
      style.setFont(workbook.createFont())
    }
    style.getFont(workbook).setColor(HSSFColor.BLUE.index)
    style.getFont(workbook).setUnderline(HSSFFont.U_SINGLE)
  }

  static void setStyleGreyBackground(HSSFCellStyle style) {
    style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index)
    style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND)
  }

  static void setStyleBlackBorders(HSSFCellStyle style) {
    style.with {
      delegate = style
      setBorderTop(BORDER_THIN)
      setTopBorderColor(HSSFColor.BLACK.index)
      setBorderBottom(BORDER_THIN)
      setBottomBorderColor(HSSFColor.BLACK.index)
      setBorderRight(BORDER_THIN)
      setRightBorderColor(HSSFColor.BLACK.index)
      setBorderLeft(BORDER_THIN)
      setLeftBorderColor(HSSFColor.BLACK.index)
    }
  }

  // Inserts errors and returns the new row index
  static int InsertErrors(List<String> errors, HSSFSheet sheet, int currentRow, HSSFCellStyle labelStyle, HSSFCellStyle noWrapContentStyle) {
    HSSFRow row
    if (errors) {
      // Error label
      row = sheet.createRow(currentRow)
      insertCellInRow(row, 0, labelStyle, 'Errors', null)
      currentRow += 1
      // Errors
      for (String error : errors) {
        row = sheet.createRow(currentRow)
        insertCellInRow(row, 0, noWrapContentStyle, error, null)
        currentRow += 1
      }
    }
    return currentRow
  }

  // Turns a date string to one formatted from date.toTimestamp
  static String dateToTimestampString(String date) {
    String dateCopy = date
    String noSecondPattern = /\d\d\d\d-\d\d-\d\dT\d\d:\d\dZ/
    if (dateCopy.matches(noSecondPattern)) {
      dateCopy = dateCopy[0..-2] + ':00' + 'Z'
    }
    DateTimeFormatter formatter =
      DateTimeFormatter.ofPattern('yyyy/MM/dd HH:mm:ss').withZone(TimeZone.getTimeZone('CST6CDT').toZoneId())
    return formatter.format(Instant.parse(dateCopy))
  }
}
