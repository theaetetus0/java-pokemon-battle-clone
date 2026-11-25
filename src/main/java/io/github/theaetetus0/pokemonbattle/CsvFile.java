package io.github.theaetetus0.pokemonbattle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads the simple CSV format used by the data files: a header row, then one row per record.
 * Blank lines and lines starting with # are ignored. Values can't contain commas.
 */
final class CsvFile {

  private CsvFile() {
  }

  /** One data row. Errors it reports include the file name and line number. */
  record Row(String fileName, int lineNumber, Map<String, String> values) {

    /** A required value. */
    String text(String column) {
      String value = optionalText(column);
      if (value.isEmpty()) {
        throw error("missing " + column);
      }
      return value;
    }

    /** A value that may be blank, in which case "" is returned. */
    String optionalText(String column) {
      String value = values.get(column);
      if (value == null) {
        throw new IllegalStateException(fileName + " has no column " + column);
      }
      return value;
    }

    int integer(String column) {
      String value = text(column);
      try {
        return Integer.parseInt(value);
      } catch (NumberFormatException e) {
        throw error(column + " must be a whole number, not \"" + value + "\"");
      }
    }

    /** A whole number that may be blank, in which case 0 is returned. */
    int optionalInteger(String column) {
      return optionalText(column).isEmpty() ? 0 : integer(column);
    }

    double decimal(String column) {
      String value = text(column);
      try {
        return Double.parseDouble(value);
      } catch (NumberFormatException e) {
        throw error(column + " must be a number, not \"" + value + "\"");
      }
    }

    GameDataException error(String message) {
      return new GameDataException(fileName + " line " + lineNumber + ": " + message);
    }
  }

  /** Reads every row, checking that each has the expected columns. */
  static List<Row> read(String fileName, Reader reader, List<String> expectedColumns) {
    List<Row> rows = new ArrayList<>();
    List<String> header = null;

    try (BufferedReader lines = new BufferedReader(reader)) {
      int lineNumber = 0;
      String line;
      while ((line = lines.readLine()) != null) {
        lineNumber++;
        if (line.isBlank() || line.strip().startsWith("#")) {
          continue;
        }

        List<String> cells = split(line);
        if (header == null) {
          header = cells;
          if (!header.equals(expectedColumns)) {
            throw new GameDataException(fileName + " line " + lineNumber + ": header should be "
                + String.join(",", expectedColumns) + " but was " + String.join(",", header));
          }
          continue;
        }

        if (cells.size() != header.size()) {
          throw new GameDataException(fileName + " line " + lineNumber + ": expected " + header.size()
              + " values but found " + cells.size());
        }
        Map<String, String> values = new HashMap<>();
        for (int i = 0; i < header.size(); i++) {
          values.put(header.get(i), cells.get(i));
        }
        rows.add(new Row(fileName, lineNumber, values));
      }
    } catch (IOException e) {
      throw new GameDataException("could not read " + fileName + ": " + e.getMessage(), e);
    }

    if (header == null) {
      throw new GameDataException(fileName + " is empty");
    }
    return rows;
  }

  private static List<String> split(String line) {
    List<String> cells = new ArrayList<>();
    for (String cell : line.split(",", -1)) {
      cells.add(cell.strip());
    }
    return cells;
  }
}
