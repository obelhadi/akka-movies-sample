package org.obelhadi.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

// This class is mostly based on the tutorial & code of the author Erik Wramner
// https://erikwramner.wordpress.com/2014/05/02/lazily-read-lines-from-gzip-file-with-java-8-streams/
public class GZipReader {

	public Stream<String> readGzipFile(String url) {
		URL gzFileUrl = null;
		try {
			gzFileUrl = new URL(url);
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		InputStream fileIs = null;
		BufferedInputStream bufferedIs = null;
		GZIPInputStream gzipIs = null;

		try {
			File zipFile = new File("temp_file.gz");
			ReadableByteChannel rbc = Channels.newChannel(gzFileUrl.openStream());
			FileOutputStream fos = new FileOutputStream(zipFile);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
			rbc.close();

			fileIs = new DataInputStream(new FileInputStream(zipFile));
			bufferedIs = new BufferedInputStream(fileIs, 65535);
			gzipIs = new GZIPInputStream(bufferedIs);
		}
		catch (IOException e) {
			closeSafely(gzipIs);
			closeSafely(bufferedIs);
			closeSafely(fileIs);
			throw new UncheckedIOException(e);
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(gzipIs));
		return reader.lines().onClose(() -> closeSafely(reader));
	}

	private static void closeSafely(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			}
			catch (IOException e) {
				// Ignore
			}
		}
	}

}
