package org.obelhadi.events;

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
public class PublishComedyMovie {

	private final String originalTitle;

	private final Integer releaseYear;

}
