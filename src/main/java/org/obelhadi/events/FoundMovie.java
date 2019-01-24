package org.obelhadi.events;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;


@Builder
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class FoundMovie {

	private final String originalTitle;

	private final Integer releaseYear;

	private final Set<String> genres;

}