package org.obelhadi.actors;

import java.util.Optional;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import org.obelhadi.events.FoundMovie;
import org.obelhadi.model.Title;
import org.obelhadi.utils.GZipReader;

public class TitlesActor extends AbstractActor {


	public static Props props(String gzFileUrl, GZipReader gZipReader, ActorRef moviesActor) {
		return Props.create(TitlesActor.class, () -> new TitlesActor(gzFileUrl, moviesActor, gZipReader));
	}

	public enum Msg {
		START_READING
	}


	private final String gzFileUrl;

	private final ActorRef moviesActor;

	private final GZipReader gZipReader;


	public TitlesActor(String gzFileUrl, ActorRef moviesActor, GZipReader gZipReader) {
		this.gzFileUrl = gzFileUrl;
		this.moviesActor = moviesActor;
		this.gZipReader = gZipReader;
	}


	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.matchEquals(Msg.START_READING, msg -> {
					System.out.println("Start Reading " + gzFileUrl);
					gZipReader.readGzipFile(gzFileUrl)
							.map(Title::fromTsvLine)
							.filter(Optional::isPresent)
							.map(Optional::get)
							.filter(t -> "movie".equals(t.getTitleType()))
							.forEach(t -> moviesActor.tell(new FoundMovie(t.getOriginalTitle(), t.getStartYear(), t.getGenres()), getSelf()));
				}).build();
	}
}
