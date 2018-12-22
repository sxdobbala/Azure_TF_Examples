package com.optum.jenkins.pipeline.library.compliance

import com.optum.jenkins.pipeline.library.compliance.models.AgileReleaseScope
import com.optum.jenkins.pipeline.library.compliance.models.CM3Approver
import com.optum.jenkins.pipeline.library.compliance.models.CM3Report
import org.apache.poi.hssf.usermodel.HSSFCellStyle
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.util.CellRangeAddress

@Grab('org.apache.poi:poi:3.9')

//Responsible for getting/making all data required for a CM1/CM2/CM3 report.

class CM3Sheet implements Serializable {
  static Excel excel = new Excel()
  static HSSFSheet insertCM3Sheet(HSSFWorkbook workbook,
                                  AgileReleaseScope deploymentInfo,
                                  CM3Report cm3Report) {
    HSSFSheet sheet = workbook.createSheet('CM3')

    // Styles
    HSSFCellStyle titleStyle = excel.createDefaultStyle(workbook)
    excel.setStyleCenter(titleStyle)
    excel.setStyleBold(titleStyle, workbook)

    HSSFCellStyle labelStyle = excel.createDefaultStyle(workbook)
    excel.setStyleBold(labelStyle, workbook)

    HSSFCellStyle headerStyle = excel.createDefaultStyle(workbook)
    excel.setStyleGreyBackground(headerStyle)
    excel.setStyleBlackBorders(headerStyle)
    excel.setStyleCenter(headerStyle)
    excel.setStyleBold(headerStyle, workbook)

    HSSFCellStyle contentStyle = excel.createDefaultStyle(workbook)
    excel.setStyleBlackBorders(contentStyle)

    HSSFCellStyle noWrapContentStyle = excel.createDefaultStyle(workbook)
    excel.setStyleBlackBorders(noWrapContentStyle)
    excel.setStyleNoWrap(noWrapContentStyle)

    HSSFCellStyle noBorderContentStyle = excel.createDefaultStyle(workbook)

    // Set column widths
    sheet.with {
      delegate = sheet
      setColumnWidth(0, 6800)
      setColumnWidth(1, 5400)
      setColumnWidth(2, 8000)
      setColumnWidth(3, 4500)
      setColumnWidth(4, 4500)
    }

    int currentRow = 0

    currentRow = InsertExcelData(sheet, currentRow, titleStyle, deploymentInfo, labelStyle, noBorderContentStyle, headerStyle, cm3Report, contentStyle)

    // Errors
    excel.InsertErrors(cm3Report.errors, sheet, currentRow, labelStyle, noWrapContentStyle)
    return sheet
  }

  private static int InsertExcelData(HSSFSheet sheet, int currentRow, HSSFCellStyle titleStyle, AgileReleaseScope deploymentInfo, HSSFCellStyle labelStyle, HSSFCellStyle noBorderContentStyle, HSSFCellStyle headerStyle, CM3Report cm3Report, HSSFCellStyle contentStyle) {
    HSSFRow row
    // Milestone label
    row = sheet.createRow(currentRow)
    excel.insertCellInRow(row, 0, titleStyle, 'Milestone: ' + deploymentInfo.id + ', Generated at ' + Excel.dateToTimestampString(deploymentInfo.timestamp), null)
    sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4))
    currentRow += 1

    // Deployment Date
    row = sheet.createRow(currentRow)
    excel.insertCellInRow(row, 0, labelStyle, 'Deployment Date:', null)
    excel.insertCellInRow(row, 1, noBorderContentStyle, Excel.dateToTimestampString(deploymentInfo.deploymentDate) ?: 'N/A', null)
    currentRow += 1

    // Service Now Change Ticket
    row = sheet.createRow(currentRow)
    excel.insertCellInRow(row, 0, labelStyle, 'Service Now Change Ticket:', null)
    excel.insertCellInRow(row, 1, noBorderContentStyle, deploymentInfo.deploymentId ?: 'N/A', null)
    currentRow += 1
    currentRow += 1

    // Approver Headers
    row = sheet.createRow(currentRow)
    excel.with {
      delegate = excel
      insertCellInRow(row, 0, headerStyle, 'Approver Name', null)
      insertCellInRow(row, 1, headerStyle, 'Approver Date', null)
      insertCellInRow(row, 2, headerStyle, 'Approver Email', null)
      insertCellInRow(row, 3, headerStyle, 'Approver MS ID', null)
      insertCellInRow(row, 4, headerStyle, 'Approver Type', null)
    }
    currentRow += 1

    // Approvers
    for (CM3Approver approval : cm3Report.itApprovals) {
      row = sheet.createRow(currentRow)
      excel.with {
        delegate = excel
        insertCellInRow(row, 0, contentStyle, approval.approverName, null)
        insertCellInRow(row, 1, contentStyle, Excel.dateToTimestampString(approval.approvedDate), null)
        insertCellInRow(row, 2, contentStyle, approval.approverEmail, null)
        insertCellInRow(row, 3, contentStyle, approval.approverMSid, null)
        insertCellInRow(row, 4, contentStyle, 'IT', null)
      }
      currentRow += 1
    }
    for (CM3Approver approval : cm3Report.businessApprovals) {
      row = sheet.createRow(currentRow)
      excel.with {
        delegate = excel
        insertCellInRow(row, 0, contentStyle, approval.approverName, null)
        insertCellInRow(row, 1, contentStyle, Excel.dateToTimestampString(approval.approvedDate), null)
        insertCellInRow(row, 2, contentStyle, approval.approverEmail, null)
        insertCellInRow(row, 3, contentStyle, approval.approverMSid, null)
        insertCellInRow(row, 4, contentStyle, 'Business', null)
      }
      currentRow += 1
    }
    currentRow += 1
    return currentRow
  }
}
