package org.obelhadi;

import java.io.IOException;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import org.obelhadi.actors.ComedyMoviesPublisherActor;
import org.obelhadi.actors.MoviesActor;
import org.obelhadi.actors.TitlesActor;
import org.obelhadi.actors.TitlesActor.Msg;
import org.obelhadi.utils.GZipReader;

public class Main {

	private final static String TITLES_FILE_URL = "https://datasets.imdbws.com/title.basics.tsv.gz";

	public static void main(String[] args) {
		final ActorSystem system = ActorSystem.create("akka-movies");
		try {
			final ActorRef comedyMoviesPublisherActor = system.actorOf(ComedyMoviesPublisherActor.props(), "comedy-movies-publisher-actor");
			final ActorRef moviesActor = system.actorOf(MoviesActor.props(comedyMoviesPublisherActor), "movies-actor");
			final ActorRef titlesActor = system.actorOf(TitlesActor.props(TITLES_FILE_URL, new GZipReader(), moviesActor), "titles-actor");
			titlesActor.tell(Msg.START_EXTRACTING_TITLES, ActorRef.noSender());

			System.out.println(">>> Press ENTER to exit <<<");
			System.in.read();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			system.terminate();
		}

	}
}
