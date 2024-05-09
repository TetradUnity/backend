package com.tetradunity.server.utils;

import io.jsonwebtoken.*;

import java.util.Date;

public class JwtUtil {
	private static final String SECRET = "qZoRZL2wIMZvXvpN2MLxEZm8pXjqZ8du7zVRLjdi4eaGfc5l5Tv45Wolo6vNnuubqZoRZL2wIMZvXvpN2MLxEZm8pXjqZ8du7zVRLjdi4eaGfc5l5Tv45Wolo6vNnuub";

	public static String generateToken(String content) {
		return Jwts.builder()
				.setSubject(content)
				.setExpiration(new Date(System.currentTimeMillis() + 3600 * 60))
				.signWith(SignatureAlgorithm.HS512, SECRET)
				.compact();
	}

	public static String extract(String token) {
		return Jwts.parser()
				.setSigningKey(SECRET)
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
	}
}