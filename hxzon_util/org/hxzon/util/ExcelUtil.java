package org.hxzon.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelUtil {
    private static final Logger logger = LoggerFactory.getLogger(ExcelUtil.class);

    public Workbook read(String filePath) {
        logger.debug(filePath);
        InputStream ins = null;
        try {
            ins = new FileInputStream(filePath);
            return WorkbookFactory.create(ins);
        } catch (Exception e) {// InvalidFormatException,IOException,RuntimeException
            throw new RuntimeException(e);
        } finally {
            try {
                ins.close();
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }//end finally

    }

    public Workbook read(InputStream ins) {
        try {
            return WorkbookFactory.create(ins);
        } catch (Exception e) {// InvalidFormatException,IOException,RuntimeException
            throw new RuntimeException(e);
        }
    }

    public void write(String absoluteFilePath, Workbook wb) {
        try {
            OutputStream ous = FileUtils.openOutputStream(new File(absoluteFilePath));
            wb.write(ous);
            ous.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setCellString(Sheet sheet, int rowIndex, int rowNum, int colIndex, int colNum, String value) {
        setCellString(sheet, rowIndex, colIndex, value);
        if (rowNum < 0 || colNum < 0) {
            throw new IllegalArgumentException("error rowNum or colNum");
        }
        if (rowNum > 1 || colNum > 1) {
            sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex + rowNum - 1, colIndex, colIndex + colNum - 1));
        }
    }

    public static Cell setCellString(Sheet sheet, int rowIndex, int colIndex, String value) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        return setCellString(row, colIndex, value);
    }

    public static Cell setCellString(Row row, int colIndex, String value) {
        Cell cell = row.getCell(colIndex);
        if (cell == null) {
            cell = row.createCell(colIndex);
        }
        cell.setCellType(Cell.CELL_TYPE_STRING);
        cell.setCellValue(value);
        return cell;
    }

    //get cell string
    public static String getCellString(Sheet sheet, int rowIndex, int colIndex) {
        if (sheet == null) {
            return "";
        }
        return getCellString(sheet.getRow(rowIndex), colIndex);

    }

    public static String getCellString(Row row, int colIndex) {
        if (row == null) {
            return "";
        }
        return getCellString(row.getCell(colIndex));
    }

    public static String getCellString(Cell cell) {
        String value = "";
        if (cell != null) {
            switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC:
                value = String.valueOf(cell.getNumericCellValue());
                break;
            case Cell.CELL_TYPE_STRING:// not escape
                value = cell.getStringCellValue().trim();
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                value = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_BLANK:
            }
        }
        return value;

    }

    public static boolean isEmptyRow(Row row) {
        for (int i = 0; i < 5; i++) {
            if (!getCellString(row, i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

}
