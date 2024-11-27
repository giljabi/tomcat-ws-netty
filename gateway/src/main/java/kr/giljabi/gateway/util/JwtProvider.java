package kr.giljabi.gateway.util;

import kr.giljabi.gateway.model.UserInfo;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@Slf4j
public class JwtProvider {
	@Value("${gateway.jwt.secret}")
	private String accessTokenSecret;

	@Value("${gateway.jwt.expirationInMs}")
	private long accessTokenExpiration;

	public String generateJwt(UserInfo userInfo) {
		SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessTokenSecret));
		Date expiration = new Date((new Date()).getTime() + accessTokenExpiration);

		return Jwts.builder()
				.setSubject(userInfo.getUserId())
				.claim("terminalId", userInfo.getTerminalId() + "")//문자열
				.setIssuedAt(new Date())
				.setExpiration(expiration)
				.signWith(secretKey)
				.compact();
	}

	public String parse(String token) {
		SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessTokenSecret));
		Claims claims = Jwts.parserBuilder()
				.setSigningKey(secretKey)
				.build()
				.parseClaimsJws(token)
				.getBody();
		return claims.getSubject();
	}

	public String getTerminalId(String token) {
		SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessTokenSecret));
		Jws<Claims> claims = Jwts.parserBuilder()
				.setSigningKey(secretKey)
				.build()
				.parseClaimsJws(token);

		String terminalId = claims.getBody().get("terminalId", String.class);
		log.info("terminalId={}", terminalId);
		return terminalId;
	}

	public long getAccessTokenExpiration() {
		return accessTokenExpiration;
	}

	public boolean validateToken(String token) {
		SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessTokenSecret));
		try {
			Jws<Claims> claimsJwts = Jwts.parserBuilder()
					.setSigningKey(secretKey)
					.build()
					.parseClaimsJws(token);

			Date expiration = claimsJwts.getBody().getExpiration();

			if (null != expiration) {
				return !expiration.before(new Date());
			}

			return true;
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}

		return false;
	}
}
