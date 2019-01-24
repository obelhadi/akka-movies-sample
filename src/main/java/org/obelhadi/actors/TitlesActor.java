package org.obelhadi.actors;

import java.util.Optional;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.obelhadi.events.FoundMovie;
import org.obelhadi.model.Title;
import org.obelhadi.utils.GZipReader;

public class TitlesActor extends AbstractActor {

	private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

	private final String gzFileUrl;

	private final ActorRef moviesActor;

	private final GZipReader gZipReader;

	public static Props props(String gzFileUrl, GZipReader gZipReader, ActorRef moviesActor) {
		return Props.create(TitlesActor.class, () -> new TitlesActor(gzFileUrl, moviesActor, gZipReader));
	}

	public enum Msg {
		START_EXTRACTING_TITLES
	}


	private TitlesActor(String gzFileUrl, ActorRef moviesActor, GZipReader gZipReader) {
		this.gzFileUrl = gzFileUrl;
		this.moviesActor = moviesActor;
		this.gZipReader = gZipReader;
	}


	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.matchEquals(Msg.START_EXTRACTING_TITLES, msg -> {
					log.info("Start Reading & Extracting titles from file : {} ", gzFileUrl);
					gZipReader.readGzipFile(gzFileUrl)
							.map(Title::fromTsvLine)
							.filter(Optional::isPresent)
							.map(Optional::get)
							.filter(t -> "movie".equals(t.getTitleType()))
							.forEach(t -> moviesActor.tell(new FoundMovie(t.getOriginalTitle(), t.getStartYear(), t.getGenres()), getSelf()));
				}).build();
	}
}
