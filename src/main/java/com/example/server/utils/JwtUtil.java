package com.example.server.utils;

import io.jsonwebtoken.*;

import java.util.Date;

public class JwtUtil {
	private static final String SECRET = "xvgbretefbgfh4g3rg45byrtyt4y";

	public static String generateToken(String content) {

		return Jwts.builder()
				.setSubject(content)
				.setExpiration(new Date(System.currentTimeMillis() + 3600))
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