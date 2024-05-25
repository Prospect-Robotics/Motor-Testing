package com.team2813.Subsystems;

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
import java.util.stream.StreamSupport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.team2813.lib2813.control.Encoder;
import com.team2813.lib2813.control.InvertType;
import com.team2813.lib2813.control.PIDMotor;
import com.team2813.lib2813.control.encoders.CancoderWrapper;
import com.team2813.lib2813.control.motors.TalonFXWrapper;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class MotorTester extends SubsystemBase {
	private static final HttpClient httpClient = HttpClient.newBuilder()
			.executor(Executors.newFixedThreadPool(2))
			.connectTimeout(Duration.ofSeconds(2)).build();

	private PIDMotor motor;
	private Encoder encoder = new CancoderWrapper(1);
	private int id;
	private static HttpRequest request;

	static {
		try {
			URI uri = new URI(
				"http",
				null,
				"localhost",
				1250,
				null,
				"action=getdevices",
				null
			);
			request = HttpRequest.newBuilder(uri).GET().build();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private static class JSONHandler implements BodyHandler<JSONObject> {
		@Override
		public BodySubscriber<JSONObject> apply(ResponseInfo responseInfo) {
			return BodySubscribers.mapping(BodyHandlers.ofString(Charset.defaultCharset()).apply(responseInfo),
					JSONObject::new);
		}
	}

	private static final JSONHandler handler = new JSONHandler();

	private static boolean isMotor(JSONObject obj) {
		if (!obj.has("Model")) {
			return false;
		}
		try {
			String str = obj.getString("Model");
			return str.equals("Talon FX vers. C") || str.equals("Talon FX");
		} catch (JSONException e) {
			return false;
		}
	}

	public void findMotor() {
		try {
			JSONObject obj = httpClient.send(request, handler).body();
			JSONArray array = obj.getJSONArray("DeviceArray");
			id = StreamSupport.stream(array.spliterator(), false)
				.filter(JSONObject.class::isInstance)
				.map(JSONObject.class::cast)
				.filter(MotorTester::isMotor)
				.findFirst()
				.map((j) -> j.getInt("ID"))
				.orElse(0);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			DriverStation.reportError(e.getMessage(), false);
			id = 0;
		} catch (Exception e) {
			DriverStation.reportError(e.getMessage(), false);
			id = 0;
		}
		motor = new TalonFXWrapper(id, InvertType.CLOCKWISE);
	}

	public PIDMotor getMotor() {
		return motor;
	}

	public Encoder getEncoder() {
		return encoder;
	}
}
