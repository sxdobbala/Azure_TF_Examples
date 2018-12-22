package com.optum.jenkins.pipeline.library.compliance

import com.optum.jenkins.pipeline.library.compliance.models.AgileReleaseScope
import com.optum.jenkins.pipeline.library.compliance.models.CM1Report
import com.optum.jenkins.pipeline.library.compliance.models.CM1UserStory
import org.apache.poi.hssf.usermodel.HSSFCellStyle
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.util.CellRangeAddress

@Grab('org.apache.poi:poi:3.9')

//Responsible for getting/making all data required for a CM1/CM2/CM3 report.

class CM1Sheet implements Serializable {
  static HSSFSheet insertCM1Sheet(HSSFWorkbook workbook,
                                  AgileReleaseScope deploymentInfo,
                                  CM1Report cm1Report) {
    HSSFSheet sheet = workbook.createSheet('CM1')

    // Styles
    HSSFCellStyle titleStyle = Excel.createDefaultStyle(workbook)
    Excel.setStyleCenter(titleStyle)
    Excel.setStyleBold(titleStyle, workbook)

    HSSFCellStyle labelStyle = Excel.createDefaultStyle(workbook)
    Excel.setStyleBold(labelStyle, workbook)

    HSSFCellStyle headerStyle = Excel.createDefaultStyle(workbook)
    Excel.setStyleGreyBackground(headerStyle)
    Excel.setStyleBlackBorders(headerStyle)
    Excel.setStyleCenter(headerStyle)
    Excel.setStyleBold(headerStyle, workbook)

    HSSFCellStyle contentStyle = Excel.createDefaultStyle(workbook)
    Excel.setStyleBlackBorders(contentStyle)

    HSSFCellStyle noWrapContentStyle = Excel.createDefaultStyle(workbook)
    Excel.setStyleBlackBorders(noWrapContentStyle)
    Excel.setStyleNoWrap(noWrapContentStyle)

    HSSFCellStyle hrefStyle = Excel.createDefaultStyle(workbook)
    Excel.setStyleBlackBorders(hrefStyle)
    Excel.setStyleHref(hrefStyle, workbook)

    // Set column widths
    sheet.with {
      delegate = sheet
      setColumnWidth(0, 5000)
      setColumnWidth(1, 15000)
      setColumnWidth(2, 15000)
      setColumnWidth(3, 8000)
      setColumnWidth(4, 10000)
    }

    int currentRow = 0
    HSSFRow row

    // Milestone label
    row = sheet.createRow(currentRow)
    Excel.insertCellInRow(row, 0, titleStyle, 'Milestone: ' + deploymentInfo.id + ', Generated at ' + Excel.dateToTimestampString(deploymentInfo.timestamp), null)
    sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4))
    currentRow += 1
    // User stories headers
    row = sheet.createRow(currentRow)
    Excel.insertCellInRow(row, 0, headerStyle, 'ID', null)
    Excel.insertCellInRow(row, 1, headerStyle, 'Name', null)
    Excel.insertCellInRow(row, 2, headerStyle, 'Acceptance Criteria', null)
    Excel.insertCellInRow(row, 3, headerStyle, 'Accepted By', null)
    Excel.insertCellInRow(row, 4, headerStyle, 'Accepted On', null)
    currentRow += 1
    // User Stories
    for (CM1UserStory userStory : cm1Report.userStories) {
      row = sheet.createRow(currentRow)
      Excel.insertCellInRow(row, 0, contentStyle, userStory.formattedId, null)
      Excel.insertCellInRow(row, 1, contentStyle, userStory.name, null)
      Excel.insertCellInRow(row, 2, noWrapContentStyle, userStory.acceptanceCriteria, null)
      Excel.insertCellInRow(row, 3, contentStyle, userStory.accepted ? userStory.accepted.acceptorName : '', null)
      Excel.insertCellInRow(row, 4, contentStyle, userStory.accepted ? Excel.dateToTimestampString(userStory.accepted.timestamp) : '', null)
      currentRow += 1
    }
    currentRow += 1

    Excel.InsertErrors(cm1Report.errors, sheet, currentRow, labelStyle, noWrapContentStyle)
    return sheet
  }
}
