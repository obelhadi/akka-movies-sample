package org.obelhadi.model;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;


@Builder
@ToString
@EqualsAndHashCode
@Getter
public class Title {

	private final String tconst;

	private final String titleType;

	private final String primaryTitle;

	private final String originalTitle;

	private final boolean isAdult;

	private final Integer startYear;

	private final Integer endYear;

	private final String runtimeMinutes;

	private final Set<String> genres;


	public static Optional<Title> fromTsvLine(String tsvLine) {
		final String[] columns = tsvLine.split("\t");
		if(columns.length != 9 || "tconst".equals(columns[0])) {
			return Optional.empty();
		}
		final Title title = Title.builder()
				.tconst(columns[0])
				.titleType(columns[1])
				.primaryTitle(columns[2])
				.originalTitle(columns[3])
				.isAdult("1".equals(columns[4]))
				.startYear(parseYear(columns[5]))
				.endYear(parseYear(columns[6]))
				.runtimeMinutes(columns[7])
				.genres(Stream.of(columns[8].split(",")).collect(Collectors.toSet()))
				.build();
		return Optional.ofNullable(title);
	}

	private static Integer parseYear(String str) {
		try {
			return Integer.valueOf(str);
		} catch (NumberFormatException e) {
			return null;
		}
	}

}