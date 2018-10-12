package com.neptunedreams;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 10/7/18
 * <p>Time: 4:25 AM
 *
 * @author Miguel Mu\u00f1oz
 */
@SuppressWarnings({"UseOfSystemOutOrSystemErr", "HardCodedStringLiteral"})
public enum TimeTest {
	;

	public static void main(String[] args) throws IOException {
		FileSystem defSystem = FileSystems.getDefault();
		Path path = defSystem.getPath(System.getProperty("user.home"), "TimeText.txt");
		writeTime(path);
		readFile(path);
	}

	private static void readFile(final Path path) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(path)) {
			String line = reader.readLine();
			while (line != null) {
				System.out.printf("%s%n", line);
				line = reader.readLine();
			}
		}
	}

	private static void writeTime(final Path path) throws IOException {
		OffsetDateTime dateTime = OffsetDateTime.now();
		Instant instant = dateTime.toInstant();
		ZonedDateTime zonedDateTime = dateTime.toZonedDateTime();
		LocalDateTime localDateTime = dateTime.toLocalDateTime();
		if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
			Files.createFile(path);
		}
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.APPEND)) {
			writer.write(getTimeString(localDateTime, instant, dateTime, zonedDateTime));
			writer.newLine();
		}
	}

	private static String getTimeString(
			LocalDateTime  localDateTime, 
			Instant        instant, 
			OffsetDateTime offsetDateTime, 
			ZonedDateTime  zonedDateTime
	) {
		return String.format("%nLocal:   %s%nInstant: %s%nOffset:  %s  (%s)%nZoned:   %s  (%s)%n", 
				localDateTime, instant, offsetDateTime, offsetDateTime.getOffset(), zonedDateTime, zonedDateTime.getZone().getRules());
	}
}
