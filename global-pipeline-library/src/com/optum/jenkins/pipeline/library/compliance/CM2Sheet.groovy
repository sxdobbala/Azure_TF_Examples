package com.optum.jenkins.pipeline.library.compliance

import com.optum.jenkins.pipeline.library.compliance.models.AgileReleaseScope
import com.optum.jenkins.pipeline.library.compliance.models.CM2AutomatedTestSummary
import com.optum.jenkins.pipeline.library.compliance.models.CM2ManualTestCase
import com.optum.jenkins.pipeline.library.compliance.models.CM2ManualTestResults
import com.optum.jenkins.pipeline.library.compliance.models.CM2Report
import com.optum.jenkins.pipeline.library.compliance.models.CM2SecuritySummary
import org.apache.poi.hssf.usermodel.HSSFCellStyle
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.util.CellRangeAddress

@Grab('org.apache.poi:poi:3.9')

//Responsible for getting/making all data required for a CM1/CM2/CM3 report.

class CM2Sheet implements Serializable {
  static Excel excel = new Excel()

  static HSSFSheet insertCM2Sheet(HSSFWorkbook workbook,
                                  AgileReleaseScope deploymentInfo,
                                  CM2Report cm2Report) {
    HSSFSheet sheet = workbook.createSheet('CM2')

    // Styles
    HSSFCellStyle titleStyle = excel.createDefaultStyle(workbook)
    excel.setStyleCenter(titleStyle)
    excel.setStyleBold(titleStyle, workbook)

    HSSFCellStyle labelStyle = excel.createDefaultStyle(workbook)
    excel.setStyleBold(labelStyle, workbook)

    HSSFCellStyle subLabelStyle = excel.createDefaultStyle(workbook)
    excel.setStyleGreyBackground(subLabelStyle)
    excel.setStyleBold(subLabelStyle, workbook)

    HSSFCellStyle headerStyle = excel.createDefaultStyle(workbook)
    excel.setStyleBlackBorders(headerStyle)
    excel.setStyleBold(headerStyle, workbook)

    HSSFCellStyle contentStyle = excel.createDefaultStyle(workbook)
    excel.setStyleBlackBorders(contentStyle)

    HSSFCellStyle noWrapContentStyle = excel.createDefaultStyle(workbook)
    excel.setStyleBlackBorders(noWrapContentStyle)
    excel.setStyleNoWrap(noWrapContentStyle)

    HSSFCellStyle hrefStyle = excel.createDefaultStyle(workbook)
    excel.setStyleBlackBorders(hrefStyle)
    excel.setStyleHref(hrefStyle, workbook)

    // Set column widths
    sheet.with {
      delegate = sheet
      setColumnWidth(0, 8000)
      setColumnWidth(1, 5000)
      setColumnWidth(2, 5000)
      setColumnWidth(3, 5000)
      setColumnWidth(4, 5000)
      setColumnWidth(5, 5000)
      setColumnWidth(6, 5000)
      setColumnWidth(7, 5000)
      setColumnWidth(8, 5000)
      setColumnWidth(9, 5000)
    }

    int currentRow = 0
    HSSFRow row
    currentRow = InsertTitles(sheet, currentRow, titleStyle, deploymentInfo, labelStyle)
    currentRow = InsertManualTests(sheet, currentRow, subLabelStyle, cm2Report.manualTestResults, contentStyle, headerStyle)
    currentRow = InsertAutomatedTests(cm2Report.automatedTestSummaries, sheet, currentRow, subLabelStyle, headerStyle, contentStyle)
    currentRow = InsertSecuritySummaries(cm2Report.securitySummaries, sheet, currentRow, subLabelStyle, headerStyle, contentStyle)
    excel.InsertErrors(cm2Report.errors, sheet, currentRow, labelStyle, noWrapContentStyle)
    return sheet
  }

  // Inserts security result rows and returns the new row index
  private static int InsertSecuritySummaries(List<CM2SecuritySummary> securitySummaries, HSSFSheet sheet, int currentRow, HSSFCellStyle subLabelStyle, HSSFCellStyle headerStyle, HSSFCellStyle contentStyle) {
    HSSFRow row
    for (CM2SecuritySummary summary : securitySummaries) {
      // Security Summaries Label
      row = sheet.createRow(currentRow)
      excel.insertCellInRow(row, 0, subLabelStyle, summary.sourceType as String, null)
      sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 0, 9))
      currentRow += 1
      // Security Summary Headers
      row = sheet.createRow(currentRow)
      excel.with {
        delegate = excel
        insertCellInRow(row, 0, headerStyle, 'Type of Test', null)
        insertCellInRow(row, 1, headerStyle, 'Open Critical Issues', null)
        insertCellInRow(row, 2, headerStyle, 'Open High Issues', null)
        insertCellInRow(row, 3, headerStyle, 'Open Medium Issues', null)
        insertCellInRow(row, 4, headerStyle, 'Open Low Issues', null)
        insertCellInRow(row, 5, headerStyle, 'Total Critical Issues', null)
        insertCellInRow(row, 6, headerStyle, 'Total High Issues', null)
        insertCellInRow(row, 7, headerStyle, 'Total Medium Issues', null)
        insertCellInRow(row, 8, headerStyle, 'Total Low Issues', null)
        insertCellInRow(row, 9, headerStyle, 'Recent Scan Date', null)
      }
      currentRow += 1
      // Security Summary Content
      row = sheet.createRow(currentRow)
      excel.with {
        delegate = excel
        insertCellInRow(row, 0, contentStyle, summary.testType as String, null)
        insertCellInRowNumeric(row, 1, contentStyle, summary.openCriticalIssues as int, false)
        insertCellInRowNumeric(row, 2, contentStyle, summary.openHighIssues as int, false)
        insertCellInRowNumeric(row, 3, contentStyle, summary.openMediumIssues as int, false)
        insertCellInRowNumeric(row, 4, contentStyle, summary.openLowIssues as int, false)
        insertCellInRowNumeric(row, 5, contentStyle, summary.totalCriticalIssues as int, false)
        insertCellInRowNumeric(row, 6, contentStyle, summary.totalHighIssues as int, false)
        insertCellInRowNumeric(row, 7, contentStyle, summary.totalMediumIssues as int, false)
        insertCellInRowNumeric(row, 8, contentStyle, summary.totalLowIssues as int, false)
        insertCellInRow(row, 9,  contentStyle, summary.recentScanDate as String, null)
      }
      currentRow += 1
    }
    currentRow += 1
    return currentRow
  }

  // Inserts automated test rows and returns the new row index
  private static int InsertAutomatedTests(List<CM2AutomatedTestSummary> automatedTestSummaries, HSSFSheet sheet, int currentRow, HSSFCellStyle subLabelStyle, HSSFCellStyle headerStyle, HSSFCellStyle contentStyle) {
    HSSFRow row
    // Automated Test Summaries Label
    for (CM2AutomatedTestSummary summary : automatedTestSummaries) {
      // Automated Test Summary Type
      row = sheet.createRow(currentRow)
      excel.insertCellInRow(row, 0, subLabelStyle, summary.type, null)
      sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 0, 5))
      currentRow += 1
      // Automated Test Summary Headers
      row = sheet.createRow(currentRow)
      excel.with {
        delegate = excel
        insertCellInRow(row, 0, headerStyle, 'Total Scenarios', null)
        insertCellInRow(row, 1, headerStyle, '# Executed', null)
        insertCellInRow(row, 2, headerStyle, '# Passed', null)
        insertCellInRow(row, 3, headerStyle, '# Failed', null)
        insertCellInRow(row, 4, headerStyle, 'Pass Percentage', null)
        insertCellInRow(row, 5, headerStyle, 'Pass Threshold', null)
      }
      currentRow += 1
      // Automated Test Summary Content
      row = sheet.createRow(currentRow)
      excel.with {
        delegate = excel
        insertCellInRowNumeric(row, 0, contentStyle, summary.totalScenarios, false)
        insertCellInRowNumeric(row, 1, contentStyle, summary.executedCases, false)
        insertCellInRowNumeric(row, 2, contentStyle, summary.passedCases, false)
        insertCellInRowNumeric(row, 3, contentStyle, summary.failedCases, false)
        insertCellInRowNumeric(row, 4, contentStyle, summary.passedCases / summary.executedCases, true)
        insertCellInRowNumeric(row, 5, contentStyle, summary.failThreshold, true)
      }
      currentRow += 1
    }
    currentRow += 1
    return currentRow
  }

  // Inserts manual test rows and returns the new row index
  private static int InsertManualTests(HSSFSheet sheet, int currentRow, HSSFCellStyle subLabelStyle, CM2ManualTestResults manualTestResults, HSSFCellStyle contentStyle, HSSFCellStyle headerStyle) {
    HSSFRow row
    // Manual Test Label
    row = sheet.createRow(currentRow)
    excel.insertCellInRow(row, 0, subLabelStyle, 'Manual Test Cases', null)
    BigDecimal percentagePassed = 0
    if (manualTestResults.testCases) {
      def passed = 0
      for (CM2ManualTestCase testCase : manualTestResults.testCases) {
        if (testCase.executed && testCase.executed.passed) {
          passed += 1
        }
      }
      percentagePassed = passed / manualTestResults.testCases.size()
    }
    sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 0, 4))
    currentRow += 1
    row = sheet.createRow(currentRow)
    excel.insertCellInRow(row, 0, contentStyle, 'Passed Percentage', null)
    excel.insertCellInRowNumeric(row, 1, contentStyle, percentagePassed, true)
    currentRow += 1
    row = sheet.createRow(currentRow)
    excel.insertCellInRow(row, 0, contentStyle, 'Passed Threshold', null)
    excel.insertCellInRowNumeric(row, 1, contentStyle, manualTestResults.failThreshold, true)
    currentRow += 1
    // Manual Test Headers
    row = sheet.createRow(currentRow)
    excel.with {
      delegate = excel
      insertCellInRow(row, 0, headerStyle, 'User Story ID', null)
      insertCellInRow(row, 1, headerStyle, 'Test Case ID', null)
      insertCellInRow(row, 2, headerStyle, 'Passed?', null)
      insertCellInRow(row, 3, headerStyle, 'Executed By', null)
      insertCellInRow(row, 4, headerStyle, 'Executed On', null)
    }
    currentRow += 1
    // Manual Tests
    for (CM2ManualTestCase testCase : manualTestResults.getTestCases()) {
      row = sheet.createRow(currentRow)
      excel.insertCellInRow(row, 0, contentStyle, testCase.userStoryFormattedId, null)
      excel.insertCellInRow(row, 1, contentStyle, testCase.formattedId, null)
      if (testCase.executed) {
        excel.insertCellInRow(row, 2, contentStyle, testCase.executed.passed ? 'Passed' : 'Failed', null)
        excel.insertCellInRow(row, 3, contentStyle, testCase.executed.testerName ?: '', null)
        excel.insertCellInRow(row, 4, contentStyle, Excel.dateToTimestampString(testCase.executed.timestamp), null)
      }
      currentRow += 1
    }
    currentRow += 1
    return currentRow
  }

  // Inserts title labels and returns the new row index
  private static int InsertTitles(HSSFSheet sheet, int currentRow, HSSFCellStyle titleStyle, AgileReleaseScope deploymentInfo, HSSFCellStyle labelStyle) {
    HSSFRow row
    // Milestone label
    row = sheet.createRow(currentRow)
    excel.insertCellInRow(row, 0, titleStyle, 'Milestone: ' + deploymentInfo.id + ', Generated at ' + Excel.dateToTimestampString(deploymentInfo.timestamp), null)
    sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8))
    currentRow += 1
    // System Tests Label
    row = sheet.createRow(currentRow)
    excel.insertCellInRow(row, 0, labelStyle, 'System Tests', null)
    currentRow += 1
    currentRow += 1
    return currentRow
  }
}
