package com.team2813;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpResponse.BodySubscriber;
import java.net.http.HttpResponse.BodySubscribers;
import java.net.http.HttpResponse.ResponseInfo;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

public class IdGetter {
	private static final HttpClient httpClient = HttpClient.newBuilder()
			.executor(Executors.newFixedThreadPool(2))
			.connectTimeout(Duration.ofSeconds(2)).build();
	private URI requestUri;
	private static final int defaultPort = 1250;
	private static final String defaultHost = "localhost";
	private static final Gson gson = new GsonBuilder().create();
	private static final BodyHandler<Result> handler = new BodyHandler<Result>() {
		public BodySubscriber<Result> apply(ResponseInfo responseInfo) {
			return BodySubscribers.mapping(BodyHandlers.ofString(Charset.defaultCharset()).apply(responseInfo),
					(str) -> {
						return gson.fromJson(str, Result.class);
					});
		};
	};

	/**
	 * Creates an IdGetter with a port of {@value #defaultPort}
	 * 
	 * @param host The hostname of the device API
	 * @see #IdGetter(String, int)
	 */
	public IdGetter(String host) {
		this(host, defaultPort);
	}

	/**
	 * Creates an IdGetter with a host of {@value #defaultHost}
	 * 
	 * @param port The port that the device API is on
	 * @see #IdGetter(String, int)
	 */
	public IdGetter(int port) {
		this(defaultHost, port);
	}

	/**
	 * Creates an IdGetter with a host of {@value #defaultHost},
	 * and a port of {@value #defaultPort}
	 * 
	 * @see #IdGetter(String, int)
	 */
	public IdGetter() {
		this(defaultHost, defaultPort);
	}

	/**
	 * Creates an IdGetter. The
	 * 
	 * @param host The hostname of the device API
	 * @param port The port of the device API
	 */
	public IdGetter(String host, int port) {
		try {
			requestUri = new URI(
					"http",
					null,
					host,
					port,
					null,
					"action=getdevices",
					null);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private HttpRequest createRequest() {
		return HttpRequest.newBuilder(requestUri).GET().build();
	}

	public int getId() {
		Result result;
		try {
			result = httpClient.send(createRequest(), handler).body();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new DeviceNotFoundException(e);
		} catch (IOException e) {
			throw new DeviceNotFoundException(e);
		}
		return Stream.of(result.deviceArray).filter(this::isDevice).map(Device::getId).findFirst().orElseThrow(DeviceNotFoundException::new);
	}

	boolean isDevice(Device device) {
		boolean kraken = device.model.equals("Talon FX vers. C");
		boolean falcon = device.model.equals("Talon FX");
		return  kraken || falcon;
	}

	private static class Result {
		@SerializedName("DeviceArray")
		Device[] deviceArray;
	}

	private static class Device {
		@SerializedName("Model")
		String model;
		@SerializedName("ID")
		int id;

		int getId() {
			return id;
		}
	}

	public static class DeviceNotFoundException extends RuntimeException {
		public DeviceNotFoundException() {
			this(null);
		}

		public DeviceNotFoundException(Exception cause) {
			super("No Device was found on the robot", cause);
		}
	}
}
