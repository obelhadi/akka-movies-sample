package org.obelhadi.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import org.obelhadi.events.FoundMovie;
import org.obelhadi.events.PublishComedyMovie;

public class MoviesActor extends AbstractActor {

	public MoviesActor(ActorRef comedyMoviesPublisherActor) {
		this.comedyMoviesPublisherActor = comedyMoviesPublisherActor;
	}

	public static Props props(ActorRef comedyMoviesPublisherActor) {
		return Props.create(MoviesActor.class, () -> new MoviesActor(comedyMoviesPublisherActor));
	}

	private final ActorRef comedyMoviesPublisherActor;

	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(FoundMovie.class, foundMovie -> {

					if (foundMovie.getGenres().contains("Comedy")) {
						comedyMoviesPublisherActor.tell(new PublishComedyMovie(foundMovie.getOriginalTitle(), foundMovie.getReleaseYear()), getSelf());
					}
				}).build();
	}


}
