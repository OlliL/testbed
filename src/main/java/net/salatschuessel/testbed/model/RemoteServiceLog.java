package net.salatschuessel.testbed.model;

import java.time.Instant;

public record RemoteServiceLog(String direction, String body, Instant insertTime) {
}