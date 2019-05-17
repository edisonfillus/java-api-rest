package org.project.example.rest;

import java.security.Key;
import java.util.Date;

import javax.annotation.security.PermitAll;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.project.example.security.ApiKey;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Path("/authentication")
public class AuthenticationRestService {
	
	static Logger log = LogManager.getLogger(AuthenticationRestService.class);
	
	@POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@PermitAll
    public Response authenticateUser(@FormParam("username") String username, 
                                     @FormParam("password") String password) {

        try {
        	log.info("Into the Authencation");
            // Authenticate the user using the credentials provided
            authenticate(username, password);

            // Issue a token for the user
            String token = createJWT(username, "example","project", 300000);

            System.out.println("Token: " + token);
            // Return the token on the response
            return Response.ok(token).build();

        } catch (Exception e) {
        	System.out.println(e.toString());
            return Response.status(Response.Status.FORBIDDEN).build();
        }      
    }

    private void authenticate(String username, String password) throws Exception {
         // Authenticate against a database, LDAP, file or whatever
        // Throw an Exception if the credentials are invalid
    	if(!(username.equals("edisonkf")&&password.equals("123456"))) {
    		System.out.println("Authentication Fail: "+ username + "-" + password );
    		throw new Exception("Invalid User and Password");
    	}
    	
    }

    
    private String createJWT(String id, String issuer, String subject, long ttlMillis) {
    	 
        //The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
     
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
     
        //We will sign our JWT with our ApiKey secret
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(ApiKey.SECRET_KEY);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
     
        //Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder().setId(id)
                                    .setIssuedAt(now)
                                    .setSubject(subject)
                                    .setIssuer(issuer)
                                    .signWith(signatureAlgorithm, signingKey);
     
        //if it has been specified, let's add the expiration
        if (ttlMillis >= 0) {
        long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }
     
        //Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }
    
}

